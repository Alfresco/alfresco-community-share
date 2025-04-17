#!/usr/bin/env bash

export DOCKER_COMPOSE_PATH="${1}"

if [ -z "$DOCKER_COMPOSE_PATH" ] ; then
  echo "Please provide path to docker-compose.yml: \"${0##*/} /path/to/docker-compose.yml\""
  exit 1
fi

echo "Starting AGS stack in ${DOCKER_COMPOSE_PATH}"

export TRANSFORMERS_TAG=$(mvn help:evaluate -Dexpression=dependency.alfresco-transform-core.version -q -DforceStdout)
export TRANSFORM_ROUTER_TAG=$(mvn help:evaluate -Dexpression=dependency.alfresco-transform-service.version -q -DforceStdout)

# .env files are picked up from project directory correctly on docker-compose 1.23.0+
docker compose --file "${DOCKER_COMPOSE_PATH}" --project-directory $(dirname "${DOCKER_COMPOSE_PATH}") up -d

if [ $? -eq 0 ] ; then
  echo "Docker Compose started ok"
else
  echo "Docker Compose failed to start" >&2
  exit 1
fi