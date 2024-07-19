#!/usr/bin/env bash

echo "=========================== Starting Cleanup Script ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vx
pushd "$(dirname "${BASH_SOURCE[0]}")/../"


# Stop and remove the containers
docker ps -a -q | xargs -l -r docker stop
docker ps -a -q | xargs -l -r docker rm

pip install awscli
printf "${AGS_AWS_ACCESS_KEY_ID}\n${AGS_AWS_SECRET_ACCESS_KEY}\n\n\n" | aws configure

echo ${S3_BUCKET_NAME}

aws s3 ls | awk '{print $3}' | grep "^${S3_BUCKET_NAME}" | xargs -l -r -I{} aws s3 rb "s3://{}" --force
aws s3 ls | awk '{print $3}' | grep "^${S3_BUCKET2_NAME}" | xargs -l -r -I{} aws s3 rb "s3://{}" --force

popd
set +vx
echo "=========================== Finishing Cleanup Script =========================="
