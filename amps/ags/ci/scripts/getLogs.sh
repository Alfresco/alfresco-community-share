#!/usr/bin/env bash
set -x

# Display running containers
docker ps

collect_logs() {
  local containerNamePattern="$1"
  local outputFile="$2"
  local containerIds

  containerIds=$(docker ps -aq --filter "name=${containerNamePattern}")
  if [ -z "$containerIds" ]; then
    return
  fi

  : > "$outputFile"
  while IFS= read -r containerId; do
    [ -n "$containerId" ] || continue
    {
      echo "===== ${containerId} ====="
      docker logs "$containerId" 2>&1 || true
      echo
    } >> "$outputFile"
  done <<< "$containerIds"
}

collect_logs "-alfresco-" "alfresco.log"
collect_logs "-share-" "share.log"
collect_logs "-search-" "search.log"
