#!/usr/bin/env zsh
#Script aimed to delete environments on K8s from local machines. In order to use it, you need to have jq installed 'brew install jq'.
#You can replace the namespace name at line 4 and run the script.
#1 namespace . 2 hosted zone
export NAMESPACE="travis-share-228"
export HOSTED_ZONE="dev.envalfresco.com"

export HOST="${NAMESPACE}.${HOSTED_ZONE}"
export RELEASE_NAME="${NAMESPACE}"
export INGRESS_RELEASE_NAME="${NAMESPACE}-ingress"

#
# Deletes the environment
#
function deleteEnv {
  echo "=========================== Deleting the environment ==========================="

  # remove environments
  helm uninstall "${RELEASE_NAME}" --namespace "${NAMESPACE}"
  helm uninstall "${INGRESS_RELEASE_NAME}" --namespace "${NAMESPACE}"

  # remove namespace
  kubectl delete namespace "${NAMESPACE}"
}

deleteEnv
