#!/usr/bin/env bash
echo "=========================== Starting Build Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

source "$(dirname "${BASH_SOURCE[0]}")/build_functions.sh"

ENT_DEPENDENCY_VERSION="$(retrievePomProperty "dependency.alfresco-enterprise-repo.version")"
REPO_IMAGE=$([[ "${ENT_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] && echo "-Drepo.image.tag=latest" || echo)

# Prevent merging of any SNAPSHOT dependencies into the master or the release/* branches
if [[ $(isPullRequestBuild) && "${ENT_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ && "${BRANCH_NAME}" =~ ^master$|^release/.+$ ]] ; then
  printf "PRs with SNAPSHOT dependencies are not allowed into master or release branches\n"
  exit 1
fi

# Prevent release jobs from starting when there are SNAPSHOT upstream dependencies
if [[ "${ENT_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] && [ "${JOB_NAME,,}" = "release" ] ; then
  printf "Cannot release project with SNAPSHOT dependencies!\n"
  exit 1
fi

ENT_UPSTREAM_REPO="github.com/Alfresco/alfresco-enterprise-repo.git"

# Checkout the upstream alfresco-enterprise-repo project (tag or branch)
if [[ "${ENT_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] ; then
  pullSameBranch "${ENT_UPSTREAM_REPO}"
else
  pullUpstreamTag "${ENT_UPSTREAM_REPO}" "${ENT_DEPENDENCY_VERSION}"
fi

COM_DEPENDENCY_VERSION="$(retrievePomProperty "dependency.alfresco-community-repo.version")"

# Either both the parent and the upstream dependency are the same, or else fail the build
if [ "${COM_DEPENDENCY_VERSION}" != "$(retrievePomParentVersion "${ENT_UPSTREAM_REPO}")" ]; then
  printf "Upstream dependency version (%s) is different then the project parent version!\n" "${COM_DEPENDENCY_VERSION}"
  exit 1
fi

# Prevent merging of any SNAPSHOT dependencies into the master or the release/* branches
if [[ $(isPullRequestBuild) && "${COM_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ && "${BRANCH_NAME}" =~ ^master$|^release/.+$ ]] ; then
  printf "PRs with SNAPSHOT dependencies are not allowed into master or release branches\n"
  exit 1
fi

# Prevent release jobs from starting when there are SNAPSHOT upstream dependencies
if [[ "${COM_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] && [ "${JOB_NAME,,}" = "release" ] ; then
  printf "Cannot release project with SNAPSHOT dependencies!\n"
  exit 1
fi

COM_UPSTREAM_REPO="github.com/Alfresco/alfresco-community-repo.git"

# Checkout the upstream alfresco-community-repo project (tag or branch; + build if the latter)
if [[ "${COM_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] ; then
  pullAndBuildSameBranchOnUpstream "${COM_UPSTREAM_REPO}" "-Pags -Pall-tas-tests -Dlicense.failOnNotUptodateHeader=false"
else
  pullUpstreamTag "${COM_UPSTREAM_REPO}" "${COM_DEPENDENCY_VERSION}"
fi

# Build the upstream alfresco-enterprise-repo project with its docker image
if [[ "${ENT_DEPENDENCY_VERSION}" =~ ^.+-SNAPSHOT$ ]] ; then
  buildSameBranchOnUpstream "${ENT_UPSTREAM_REPO}" "-Pbuild-docker-images -Pags -Pall-tas-tests -Dlicense.failOnNotUptodateHeader=false"
else
  buildUpstreamTag "${ENT_UPSTREAM_REPO}" "${ENT_DEPENDENCY_VERSION}" "-Pbuild-docker-images -Pags -Pall-tas-tests -Dlicense.failOnNotUptodateHeader=false"
fi


# Build the current project
mvn -B -ntp -V install -DskipTests -Dmaven.javadoc.skip=true -Pags "-Dimage.tag=${TAG_NAME}" ${REPO_IMAGE} \
  $([ "${JOB_NAME,,}" = "build" ] && echo "-Ppush-docker-images" || echo "-Pbuild-docker-images") \

popd
set +vex
echo "=========================== Finishing Build Script =========================="

