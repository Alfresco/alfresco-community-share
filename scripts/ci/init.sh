#!/usr/bin/env bash
echo "=========================== Starting Init Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

# Maven Setup
find "${HOME}/.m2/repository/" -type d -name "*-SNAPSHOT*" | xargs -r -l rm -rf

# Docker Logins
echo "${DOCKERHUB_PASSWORD}" | docker login -u="${DOCKERHUB_USERNAME}" --password-stdin
echo "${QUAY_PASSWORD}" | docker login -u="${QUAY_USERNAME}" --password-stdin quay.io

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

echo "::set-env name=TAG_NAME::$TAG_NAME"
echo "::set-env name=NAMESPACE::$NAMESPACE"

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
