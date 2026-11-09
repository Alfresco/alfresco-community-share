#!/usr/bin/env bash
# Installs a pinned Google Chrome build on a CI runner.
#
# The browser is downloaded from Google's own "Chrome for Testing" distribution
# (https://developer.chrome.com/blog/chrome-for-testing), which is the officially
# supported source for pinned automation builds and keeps every released version
# available forever. Third-party mirrors and GitHub release assets are deliberately
# not used: they are mutable and would let a compromised account inject arbitrary
# code that this script installs with sudo.
#
# As defence in depth the archive is additionally verified against a SHA-256 checksum
# pinned in the workflow, so a change of the remote artifact fails the build.
#
# Required environment variables:
#   CHROME_VERSION  Pinned Chrome version, e.g. "133.0.6943.141-1"
#                   (a trailing debian revision such as "-1" is ignored)
#   CHROME_SHA256   Expected SHA-256 of chrome-linux64.zip for that version

set -euo pipefail

echo "=========================== Installing Google Chrome =========================="

: "${CHROME_VERSION:?CHROME_VERSION must be set, e.g. 133.0.6943.141-1}"
: "${CHROME_SHA256:?CHROME_SHA256 must be set to the expected checksum of chrome-linux64.zip}"

# Version as reported by "google-chrome --version", i.e. without the debian revision.
CHROME_PLAIN_VERSION="${CHROME_VERSION%-*}"
CHROME_URL="https://storage.googleapis.com/chrome-for-testing-public/${CHROME_PLAIN_VERSION}/linux64/chrome-linux64.zip"
INSTALL_DIR="/opt/chrome-for-testing"

WORK_DIR="$(mktemp -d)"
trap 'rm -rf "${WORK_DIR}"' EXIT
ARCHIVE="${WORK_DIR}/chrome-linux64.zip"

echo "Downloading ${CHROME_URL}"
# No -q: download problems must be visible in the build log.
wget --tries=3 --timeout=30 --waitretry=10 -O "${ARCHIVE}" "${CHROME_URL}"

echo "Verifying SHA-256 checksum"
echo "${CHROME_SHA256}  ${ARCHIVE}" | sha256sum --check --strict

sudo apt-get update

# Chrome for Testing ships without package metadata, so its shared library
# dependencies have to be installed explicitly. Some names carry the "t64" suffix
# since Ubuntu 24.04, hence those are resolved on a best effort basis.
sudo apt-get install -y unzip fonts-liberation libcairo2 libdbus-1-3 libdrm2 \
    libgbm1 libnspr4 libnss3 libpango-1.0-0 libx11-6 libxcb1 libxcomposite1 \
    libxdamage1 libxext6 libxfixes3 libxkbcommon0 libxrandr2 xdg-utils
for pkg in libasound2 libatk-bridge2.0-0 libatk1.0-0 libatspi2.0-0 libcups2 \
           libglib2.0-0 libgtk-3-0; do
    sudo apt-get install -y "${pkg}t64" || sudo apt-get install -y "${pkg}"
done

# Drop any Chrome preinstalled on the runner so that only the pinned build is used.
sudo apt-mark unhold google-chrome-stable || true
sudo apt-get purge -y google-chrome-stable || true
rm -rf ~/.config/google-chrome ~/.cache/google-chrome
sudo rm -f /usr/bin/google-chrome /usr/bin/google-chrome-stable
sudo rm -rf "${INSTALL_DIR}"

sudo mkdir -p "${INSTALL_DIR}"
sudo unzip -q -o "${ARCHIVE}" -d "${INSTALL_DIR}"
sudo ln -sf "${INSTALL_DIR}/chrome-linux64/chrome" /usr/bin/google-chrome
sudo ln -sf "${INSTALL_DIR}/chrome-linux64/chrome" /usr/bin/google-chrome-stable

INSTALLED_VERSION="$(google-chrome --version)"
echo "Installed: ${INSTALLED_VERSION}"
if [[ "${INSTALLED_VERSION}" != *"${CHROME_PLAIN_VERSION}"* ]]; then
    echo "ERROR: expected Chrome ${CHROME_PLAIN_VERSION} but got '${INSTALLED_VERSION}'"
    exit 1
fi

echo "=========================== Google Chrome installed ==========================="
