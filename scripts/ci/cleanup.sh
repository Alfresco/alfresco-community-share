#!/usr/bin/env bash

safe_del_bucket() {
    local bucket_name=$1
    if [[ "$S3_BUCKET2_NAME" != ${S3_BUCKET_PREFIX}* && ! -z "$S3_BUCKET_PREFIX" ]]; then
        echo "Error: Bucket name '${bucket_name}' does not start with prefix '${S3_BUCKET_PREFIX}'. Exiting to avoid accidental deletion of buckets."
        exit 1
    fi
    if aws s3 ls "s3://${bucket_name}" &>/dev/null; then
        echo "Bucket '${bucket_name}' exists. Deleting..."
        aws s3 rb "s3://${bucket_name}" --force
        echo "Bucket '${bucket_name}' deleted."
    else
        echo "Bucket '${bucket_name}' does not exist."
    fi
}

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -evx
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

if [[ -z "$S3_BUCKET_PREFIX" ]]; then
    echo "Error: S3_BUCKET_PREFIX is not set. Exiting to avoid accidental deletion of buckets."
    exit 1
fi

for BUCKET in "$S3_BUCKET_NAME"; do
    safe_del_bucket "$BUCKET"
done

popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
