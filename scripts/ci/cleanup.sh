#!/usr/bin/env bash

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vx
set -e
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

if [[ -z "$S3_BUCKET_NAME" || -z "$S3_BUCKET2_NAME" ]]; then
    echo "Error: S3_BUCKET_NAME or S3_BUCKET2_NAME is not set."
    exit 1
fi

if aws s3 ls "s3://${S3_BUCKET_NAME}" &>/dev/null; then
    echo "Bucket '${S3_BUCKET_NAME}' exists. Deleting..."
    aws s3 rb "s3://${S3_BUCKET_NAME}" --force || true
    echo "Bucket '${S3_BUCKET_NAME}' deleted."
else
    echo "Bucket '${S3_BUCKET_NAME}' does not exist."
fi

if aws s3 ls "s3://${S3_BUCKET2_NAME}" &>/dev/null; then
    echo "Bucket '${S3_BUCKET2_NAME}' exists. Deleting..."
    aws s3 rb "s3://${S3_BUCKET2_NAME}" --force || true
    echo "Bucket '${S3_BUCKET2_NAME}' deleted."
else
    echo "Bucket '${S3_BUCKET2_NAME}' does not exist."
fi


popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
