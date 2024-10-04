#!/usr/bin/env bash
# fail script immediately on any errors in external commands and print the lines
set -ev

export TRANSFORMERS_TAG=$(mvn help:evaluate -Dexpression=dependency.alfresco-transform-core.version -q -DforceStdout)
export TRANSFORM_ROUTER_TAG=$(mvn help:evaluate -Dexpression=dependency.alfresco-transform-service.version -q -DforceStdout)

cd "${1}"

docker compose up -d
