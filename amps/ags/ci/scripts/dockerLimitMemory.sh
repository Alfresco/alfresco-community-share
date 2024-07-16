#!/usr/bin/env bash
set -x

#stop not needed containers
docker stop $(docker ps -a | grep '\-zeppelin-' | awk '{print $1}')
docker stop $(docker ps -a | grep '\-sync-service-' | awk '{print $1}')

shareContainerId=$(docker ps -a | grep '\-share-' | awk '{print $1}')
if [ -n "$shareContainerId" ]; then
      docker stop $(docker ps -a | grep '\-transform-router-' | awk '{print $1}')
      docker stop $(docker ps -a | grep '\-shared-file-store-' | awk '{print $1}')
fi

# Display containers resources usage
docker stats --no-stream
