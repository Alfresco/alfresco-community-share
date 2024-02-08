#!/usr/bin/env bash

export HOST="${NAMESPACE}.${HOSTED_ZONE}"
export RELEASE_NAME="${NAMESPACE}"
export RELEASE_INGRESS_NAME="${NAMESPACE}-ingress"

#
# Deletes the environment
#
function deleteEnv {
  echo "=========================== Deleting the environment ==========================="

  # remove environments
  helm uninstall "${RELEASE_NAME}" --namespace "${NAMESPACE}"
  helm uninstall "${RELEASE_INGRESS_NAME}" --namespace "${NAMESPACE}"

  # remove namespace
  kubectl delete namespace "${NAMESPACE}"
}

if [[ "${PULL_REQUEST}" != "false" || "${BRANCH_NAME}" != "master" || "${KEEP_ENV}" != "true" ]]; then
  deleteEnv
else
  # Keep the environment on *master* branch when *KEEP_ENV* is true
  echo "Keeping environment ${HOST}"
  echo "Please delete it manually when you will no longer need it!"
fi
