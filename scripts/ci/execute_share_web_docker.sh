#!/usr/bin/env bash

echo "=========================== Starting Project Alfresco Tas Share ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

mvn --version
java -version

export XML_SUITE=$1

if [ -z "${XML_SUITE}" ]; then
    echo "XML_SUITE parameter is missing. Usage: ./scripts/ci/execute_share_web_docker.sh <xml-suite-file-name> [additional-maven-parameters]"
    exit 1
fi

mvn -B -ntp clean install \
    -f alfresco-tas-share-test/pom.xml \
    -DsuiteXmlFile="src/test/resources/test-suites/${XML_SUITE}" \
    -Dalfresco.scheme=http \
    -Dalfresco.server=localhost \
    -Dalfresco.port=8080 \
    -Dalfresco.url="http://localhost:8080/alfresco" \
    -Dshare.port=8080 \
    -Dshare.url="http://localhost:8080/share" \
    -Dadmin.user=admin '-Dadmin.password=admin' -Dbrowser.name=chrome \
    -Daims.enabled=false \
    -Dsuite-name=${XML_SUITE} \
    "${@:2}" &           # send the long living command to background!

# wait for the exit code of the background process
wait $!
SUCCESS=$?

popd
set +vex
echo "=========================== Finishing Project Alfresco Tas Share =========================="

exit ${SUCCESS}
