#!/usr/bin/env bash

# uploads all the artifacts generated by maven surefire to amazon s3 storage based on github build number
printf "${ARTIFACTS_KEY}\n${ARTIFACTS_SECRET}\n\n\n" | aws configure
aws s3 sync s3://$ARTIFACTS_BUCKET/share/$GITHUB_RUN_NUMBER reports/$GITHUB_RUN_NUMBER

# change directory and list all artifacts in reports folder
cd reports/$GITHUB_RUN_NUMBER
export ARTIFACTS_LIST=$(ls)

# print out list of artifacts
echo $ARTIFACTS_LIST

# allure generates report after clear list of artifacts and set output directory in S3 based on github build number
allure generate -c $ARTIFACTS_LIST -o allure/$GITHUB_RUN_NUMBER

# upload and aggregate one single allure report to amazon s3 storage, in the final-report folder,
# which will contain all test suites results
aws s3 sync  allure/$GITHUB_RUN_NUMBER s3://$ARTIFACTS_BUCKET/share/final-report/$GITHUB_RUN_NUMBER