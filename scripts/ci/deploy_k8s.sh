#!/usr/bin/env bash

export HOST="${NAMESPACE}.${HOSTED_ZONE}"
export RELEASE_NAME="${NAMESPACE}"
export ACS_HELM_CHART_REPO="https://kubernetes-charts.alfresco.com/incubator"
export ACS_HELM_CHART_VERSION="5.2.0"
export INGRESS_RELEASE_NAME="${NAMESPACE}-ingress"
export ALFRESCO_REPO_IMAGE="alfresco-enterprise-repo-for-share"
export ALFRESCO_SHARE_IMAGE="alfresco-share-base"
export STORAGE_CLASS="nfs-client"

#
# Determine if the current branch is master or not
#
function isBranchMaster() {
  test "${BRANCH_NAME}" = "master"
}

#
# Determine if the develop environment is up and running
#
function isMasterUp() {
  test "$(curl --write-out '%{http_code}' --silent --output /dev/null "${MASTER_URL}")" -eq 200
}

function installAcs() {
  # NOTE: repository.replicaCount=1 - this is a temporary fix until issues on cluster environments are fixed.
  # NOTE: repository.environment.JAVA_OPTS is duplicated from chart to add before "-Dalfresco.restApi.basicAuthScheme=true"
  helm upgrade --install "${RELEASE_NAME}" --repo "${ACS_HELM_CHART_REPO}" alfresco-content-services --version "${ACS_HELM_CHART_VERSION}" \
    --set repository.replicaCount=1 \
    --set externalPort="443" \
    --set externalProtocol="https" \
    --set externalHost="${HOST}" \
    --set persistence.enabled=true \
    --set persistence.storageClass.enabled=true \
    --set persistence.storageClass.name="${STORAGE_CLASS}" \
    --set postgresql.persistence.existingClaim="" \
    --set postgresql-syncservice.persistence.existingClaim="" \
    --set global.alfrescoRegistryPullSecrets=quay-registry-secret \
    --set repository.adminPassword="${ADMIN_PWD}" \
    --set repository.image.repository="quay.io/alfresco/${ALFRESCO_REPO_IMAGE}" \
    --set repository.image.tag="${REPO_TAG_NAME}" \
    --set share.image.repository="quay.io/alfresco/${ALFRESCO_SHARE_IMAGE}" \
    --set share.image.tag="${SHARE_TAG_NAME}" \
    --set alfresco-sync-service.syncservice.image.tag="${SYNC_TAG_NAME}" \
    --set repository.environment.JAVA_OPTS="-Dalfresco.restApi.basicAuthScheme=true -Dsolr.base.url=/solr -Dindex.subsystem.name=solr6 -Dalfresco.cluster.enabled=true -Ddeployment.method=HELM_CHART -Dtransform.service.enabled=true -XX:MinRAMPercentage=50 -XX:MaxRAMPercentage=80 -Dencryption.keystore.type=JCEKS -Dencryption.cipherAlgorithm=DESede/CBC/PKCS5Padding -Dencryption.keyAlgorithm=DESede -Dencryption.keystore.location=/usr/local/tomcat/shared/classes/alfresco/extension/keystore/keystore -Dmetadata-keystore.aliases=metadata -Dmetadata-keystore.metadata.algorithm=DESede" \
    --set global.tracking.sharedsecret=secret \
    --set repository.image.pullPolicy="Always"        \
    --set share.image.pullPolicy="Always"        \
    --wait \
    --timeout 20m0s \
    --namespace "${NAMESPACE}"
}



function updateEnv()  {
  echo "=========================== Updating the environment ==========================="

  installAcs
}

#
# Creates the environment
#
function createEnv() {
  echo "=========================== Creating the environment ==========================="

  # create k8s namespace
  kubectl create namespace "${NAMESPACE}"

  # install ingress
  helm upgrade --install "${INGRESS_RELEASE_NAME}" --repo https://kubernetes.github.io/ingress-nginx ingress-nginx --version 3.7.1 \
    --set controller.scope.enabled=true \
    --set controller.scope.namespace="${NAMESPACE}" \
    --set rbac.create=true \
    --set controller.config."proxy-body-size"="100m" \
    --set controller.service.targetPorts.https=80 \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-backend-protocol"="http" \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-ports"="https" \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-cert"="${SSL_CERT}" \
    --set controller.service.annotations."external-dns\.alpha\.kubernetes\.io/hostname"="${HOST}" \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/aws-load-balancer-ssl-negotiation-policy"="ELBSecurityPolicy-TLS-1-2-2017-01" \
    --set controller.publishService.enabled=true \
    --set controller.admissionWebhooks.enabled=false \
    --wait \
    --namespace "${NAMESPACE}"

  # create secret on k8s namespace
  kubectl create secret docker-registry quay-registry-secret --docker-server=quay.io --docker-username="${QUAY_USERNAME}" --docker-password="${QUAY_PASSWORD}" --namespace "${NAMESPACE}"

  installAcs
}

#
# Before running the tests make sure that all pods are green
#
function wait_for_pods() {
  # counters
  PODS_COUNTER=0
  # counters limit
  PODS_COUNTER_MAX=120
  # sleep seconds
  PODS_SLEEP_SECONDS=10

  namespace="${1}"

  echo "Validate that all the pods in the deployment are ready"
  while [ "${PODS_COUNTER}" -lt "${PODS_COUNTER_MAX}" ]; do
    pendingpodcount=$(kubectl get pods --namespace "${namespace}" | awk '{print $2}' | grep -c '0/1' || true)
    if [ "${pendingpodcount}" -eq 0 ]; then
      runningPods=$(kubectl get pods --namespace "${namespace}")
      echo "All pods are Running and Ready!"
      echo "${runningPods}"
      break
    fi
    PODS_COUNTER=$((PODS_COUNTER + 1))
    echo "${pendingpodcount} pods are not yet ready - sleeping ${PODS_SLEEP_SECONDS} seconds - counter ${PODS_COUNTER}"
    sleep "${PODS_SLEEP_SECONDS}"
    continue
  done
  if [ "${PODS_COUNTER}" -ge "${PODS_COUNTER_MAX}" ]; then
    failedPods=$(kubectl get pods --namespace "${namespace}" | grep '0/1' | awk '{print $1}')
    printf "\nThe following pods were not ready:\n"
    for failedpod in ${failedPods}; do
      echo "${failedpod}"
    done
    for failedpod in ${failedPods}; do
      printf "Description for %s:\n" "${failedpod}"
      kubectl describe pod "${failedpod}" --namespace "${namespace}"
    done
    echo "Pods did not start - exit"
    kubectl get pods --namespace "${namespace}"
    exit 1
  fi
}

# Main
if isBranchMaster ; then
  echo "On branch master"
  REPO_TAG_NAME="${TAG_NAME}"
  SHARE_TAG_NAME="${TAG_NAME}"
  if [ "${PULL_REQUEST}" != "false" ]; then
    if isMasterUp ; then
      echo "Update master environment"
      updateEnv
      wait_for_pods "${NAMESPACE}"
    else
      echo "Create master environment"
      createEnv
      wait_for_pods "${NAMESPACE}"
    fi
  else
    echo "Create PR env environment"
    createEnv
    wait_for_pods "${NAMESPACE}"
  fi
else
  echo "On development branch"
  REPO_TAG_NAME="${TAG_NAME}"
  SHARE_TAG_NAME="${TAG_NAME}"
  createEnv
  wait_for_pods "${NAMESPACE}"
fi
