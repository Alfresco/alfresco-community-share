#!/usr/bin/env bash
echo "=========================== Starting Update Downstream Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

source "$(dirname "${BASH_SOURCE[0]}")/build_functions.sh"

#Fetch the latest changes, as CI will only checkout the PR commit
git fetch origin "${BRANCH_NAME}"
git checkout "${BRANCH_NAME}"
git pull

# Retrieve the Enterprise Repo version
ENT_VERSION="$(evaluatePomProperty "dependency.alfresco-enterprise-repo.version")"

# Retrieve the current Enterprise Share version - latest tag on the current branch
SHARE_VERSION="$(git describe --abbrev=0 --tags)"

DOWNSTREAM_REPO="github.com/Alfresco/acs-packaging.git"

cloneRepo "${DOWNSTREAM_REPO}" "${BRANCH_NAME}"

cd "$(dirname "${BASH_SOURCE[0]}")/../../../$(basename "${DOWNSTREAM_REPO%.git}")"

# Update parent (Enterprise Repo)
mvn -B versions:update-parent versions:commit "-DparentVersion=[${ENT_VERSION}]"

# This can fail silently due to https://github.com/mojohaus/versions-maven-plugin/issues/268
grep "<version>${ENT_VERSION}</version>" pom.xml
if [[ $? != 0 ]]; then
  # Check out and build alfresco-enterprise-repo.
  echo "Updating parent failed - installing alfresco-enterprise-repo ${ENT_VERSION} and retrying."
  UPSTREAM_REPO="github.com/Alfresco/alfresco-enterprise-repo.git"
  pullUpstreamTag "${UPSTREAM_REPO}" "${ENT_VERSION}"
  pushd "$(dirname "${BASH_SOURCE[0]}")/../../../$(basename "${UPSTREAM_REPO%.git}")"
  mvn -B -V -ntp clean install -DskipTests -Dmaven.javadoc.skip=true -Pags
  popd

  echo "Retrying update of parent in acs-packaging."
  mvn -B versions:update-parent versions:commit "-DparentVersion=[${ENT_VERSION}]"
fi

# Update Enterprise Repo dependency version
mvn -B versions:set-property versions:commit \
  -Dproperty=dependency.alfresco-enterprise-repo.version \
  "-DnewVersion=${ENT_VERSION}"

# Update Enterprise Share dependency version
mvn -B versions:set-property versions:commit \
  -Dproperty=dependency.alfresco-enterprise-share.version \
  "-DnewVersion=${SHARE_VERSION}"

# Commit changes
git status
git --no-pager diff pom.xml
git add pom.xml

DOWNSTREAM_MESSAGE="Update upstream versions

    - alfresco-enterprise-repo:  ${ENT_VERSION}
    - alfresco-enterprise-share: ${SHARE_VERSION}"
if [[ "${GITHUB_COMMIT_MESSAGE}" =~ \[force[^\]]*\] ]]; then
  # Check if the force directive contains a version number or not.
  FORCE_TOKEN=$(echo "${GITHUB_COMMIT_MESSAGE}" | tr "\n" "\r" | sed "s|^.*\(\[force[^]]*\]\).*$|\1|g")
  NEW_ACS_VERSION=$(echo "${FORCE_TOKEN}" | sed "s|^.*\[force\s*\([^]]*\)\].*$|\1|g")
  if [[ "${NEW_ACS_VERSION}" == "" ]]; then
    # The force directive does not contain a version so we need to find the next A release to use.
    ACS_SNAPSHOT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec 2>/dev/null)
    NEW_ACS_VERSION=$(nextARelease ${ACS_SNAPSHOT_VERSION})
  fi
  echo "Updating acs-packaging to release ${NEW_ACS_VERSION}"
  sed -i "s|RELEASE_VERSION:.*|RELEASE_VERSION: ${NEW_ACS_VERSION}|g" .github/workflows/master_release.yml
  # Determine the next development version by incrementing the last number in the release version.
  FIRST_PART=$(echo ${NEW_ACS_VERSION} | sed "s|^\(.*[^0-9]\)[0-9]*$|\1|g")
  LAST_PART=$(echo ${NEW_ACS_VERSION} | sed "s|^.*[^0-9]\([0-9]*\)$|\1|g")
  NEXT_VERSION=${FIRST_PART}$((LAST_PART+1))
  if ! [[ ${NEXT_VERSION} =~ "A" ]]; then
    # If this was not an A release then we should find the next A release.
    NEXT_VERSION=$(nextARelease ${NEXT_VERSION})
  fi
  NEXT_DEV_VERSION=${NEXT_VERSION}"-SNAPSHOT"
  echo "Updating acs-packaging with next development version ${NEXT_DEV_VERSION}"
  sed -i "s|DEVELOPMENT_VERSION:.*|DEVELOPMENT_VERSION: ${NEXT_DEV_VERSION}|g" .github/workflows/master_release.yml
  git add .github/workflows/master_release.yml
  git commit --allow-empty -m "${FORCE_TOKEN}[release][skip tests] ${DOWNSTREAM_MESSAGE}"
  git push
elif git status --untracked-files=no --porcelain | grep -q '^' ; then
  git commit -m "${DOWNSTREAM_MESSAGE}"
  git push
else
  echo "Dependencies are already up to date."
  git status
fi


popd
set +vex
echo "=========================== Finishing Update Downstream Script =========================="

