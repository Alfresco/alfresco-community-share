#!/usr/bin/env bash

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vx
set -e
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

if [[ -z "$S3_BUCKET_PREFIX" ]]; then
    echo "Error: S3_BUCKET_PREFIX is not set."
    exit 1
fi

aws s3api list-buckets --query "Buckets[?starts_with(Name, '${S3_BUCKET_PREFIX}')].Name" --output text | tr '\t' '\n' | while read bucket; do
    if [[ -n "$bucket" ]]; then
        echo "Deleting bucket: $bucket"
        aws s3 rb "s3://$bucket" --force || true
    fi
done


popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
