#!/usr/bin/env bash
echo "=========================== Starting Init Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

# Maven Setup
find "${HOME}/.m2/repository/" -type d -name "*-SNAPSHOT*" | xargs -r -l rm -rf

# Docker Logins
# Only attempt a registry login when credentials are actually provided. Logging in with
# empty credentials writes a malformed/empty entry to ~/.docker/config.json, which then makes
# every subsequent pull (even of public base images) fail with:
#   "malformed HTTP Authorization header" (Internal Server Error: 500)
# This mirrors the guard used in the Alfresco/alfresco-build-tools "maven-build" action
# (docker/login-action steps gated with `if: inputs.docker-username != ''`).
if [ -n "${DOCKERHUB_USERNAME}" ] && [ -n "${DOCKERHUB_PASSWORD}" ]; then
  echo "${DOCKERHUB_PASSWORD}" | docker login -u="${DOCKERHUB_USERNAME}" --password-stdin
else
  echo "DockerHub credentials not provided - skipping DockerHub login (anonymous pulls)."
fi
if [ -n "${QUAY_USERNAME}" ] && [ -n "${QUAY_PASSWORD}" ]; then
  echo "${QUAY_PASSWORD}" | docker login -u="${QUAY_USERNAME}" --password-stdin quay.io
else
  echo "Quay.io credentials not provided - skipping quay.io login."
fi

# Define docker image tag
if [ "${PULL_REQUEST}" != "false" ]; then
  export TAG_NAME="pr-${PULL_REQUEST}"
  export NAMESPACE="travis-share-pr-${GITHUB_RUN_NUMBER}"
elif [ "${BRANCH_NAME}" = "master" ]; then
  export TAG_NAME="latest"
  export NAMESPACE="master-share"
else
  # substitude all '/' to '-' as Docker doesn't allow it
  export TAG_NAME="$(echo "${BRANCH_NAME}" | tr / - )"
  export NAMESPACE="travis-share-$GITHUB_RUN_NUMBER"
fi

# Export values for subsequent steps (GitHub Actions)
if [ -n "${GITHUB_ENV:-}" ]; then
  {
    echo "TAG_NAME=${TAG_NAME}"
    echo "NAMESPACE=${NAMESPACE}"
  } >> "$GITHUB_ENV"
else
  # Fallback for non-GitHub-Actions environments
  export TAG_NAME
  export NAMESPACE
fi

if [[ "${JOB_NAME,,}" =~ ^deploy$|^report$|^teardown$ ]]; then
  # AWS Tools
  (umask 066 && aws eks update-kubeconfig --name acs-cluster --region=eu-west-1)

  # Allure
  # TODO - review/redo the Allure installation - this seems to fail sometimes
  #curl -o allure-2.7.0.tgz -Ls https://dl.bintray.com/qameta/generic/io/qameta/allure/allure/2.7.0/allure-2.7.0.tgz
  #tar xvfz allure-2.7.0.tgz -C /opt/
  #sudo ln -s /opt/allure-2.7.0/bin/allure /usr/bin/allure
  #allure --version
fi

popd
set +vex
echo "=========================== Finishing Init Script =========================="
