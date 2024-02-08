#!/usr/bin/env bash
# fail script immediately on any errors in external commands and print the lines
set -ev
export TAG_NAME="latest"

cd "${1}"

docker-compose up -d
