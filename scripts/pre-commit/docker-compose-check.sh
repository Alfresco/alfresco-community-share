#!/usr/bin/env bash

# Based on https://github.com/IamTheFij/docker-pre-commit/blob/master/compose-check.sh
# This adds support for .env files in the same directory as the docker-compose.yml.

# Verifies that files passed in are valid for docker-compose
set -e

if command -v docker &> /dev/null && docker help compose &> /dev/null; then
    COMPOSE='docker compose'
else
    echo "ERROR: 'docker compose' was not found"
    exit 1
fi

check_file() {
    local file=$1
    local dir=$(dirname $file)
    local env_instruction=""
    if [[ -f "$dir/.env" ]]; then
        env_instruction="--env-file $dir/.env"
    fi
    env $COMPOSE -f "$file" $env_instruction config --quiet 2>&1 \
        | sed "/variable is not set. Defaulting/d"
    return "${PIPESTATUS[0]}"
}

check_files() {
    local all_files=( "$@" )
    has_error=0
    for file in "${all_files[@]}" ; do
        if [[ -f "$file" ]]; then
            if ! check_file "$file" ; then
                echo "ERROR: $file"
                has_error=1
            fi
        fi
    done
    return $has_error
}

if ! check_files "$@" ; then
    echo "Some compose files failed"
fi

exit $has_error
