#!/usr/bin/env bash

echo "=========================== Create Worm Bucket ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../"

if aws s3 ls | awk '{print $3}' | grep -q "^${S3_BUCKET2_NAME}$" ; then
  echo "Bucket ${S3_BUCKET2_NAME} already exists"
  exit 0
fi

if [[ "$S3_BUCKET2_NAME" == ${S3_BUCKET_PREFIX}* && ! -z "S3_BUCKET_PREFIX" ]]; then echo "creating bucket ${S3_BUCKET2_NAME}"
  aws s3api create-bucket --bucket "${S3_BUCKET2_NAME}" --region ${S3_BUCKET_REGION} --object-lock-enabled-for-bucket
  aws s3api put-object-lock-configuration \
      --bucket "${S3_BUCKET2_NAME}" \
      --object-lock-configuration 'ObjectLockEnabled=Enabled,Rule={DefaultRetention={Mode=COMPLIANCE,Days=1}}'

  aws s3api put-bucket-tagging --bucket "${S3_BUCKET2_NAME}" \
     --tagging="TagSet=[{Key=toDeleteAfterTests,Value=true}]"
fi

popd
set +vex
echo "=========================== Finishing Create Worm Bucket Script =========================="
