#!/usr/bin/env bash

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vx
pushd "$(dirname "${BASH_SOURCE[0]}")/../"


# Stop and remove the containers
docker ps -a -q | xargs -l -r docker stop
docker ps -a -q | xargs -l -r docker rm

pip3 install awscli
printf "${AGS_AWS_ACCESS_KEY_ID}\n${AGS_AWS_SECRET_ACCESS_KEY}\n\n\n" | aws configure

if aws s3 ls "s3://${S3_BUCKET_NAME}" &>/dev/null; then
    echo "Bucket '${S3_BUCKET_NAME}' exists. Deleting..."
    aws s3 rb "s3://${S3_BUCKET_NAME}" --force
    aws s3 rb "s3://${S3_BUCKET2_NAME}" --force
    echo "Bucket deleted."
else
    echo "Bucket '${S3_BUCKET_NAME}' does not exist."
fi


popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
