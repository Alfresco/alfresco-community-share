#!/usr/bin/env bash

echo "=========================== Starting Project Alfresco Tas Share ==========================="
PS4="\[\e[35m\]+ \[\e[m\]"
set -vex
pushd "$(dirname "${BASH_SOURCE[0]}")/../../"

cd alfresco-tas-share-test

mvn --version
java -version

export XML_SUITE=$1

mvn install \
               -DsuiteXmlFile="src/test/resources/test-suites/${XML_SUITE}" \
               -Dalfresco.scheme=http \
               -Dalfresco.server=localhost \
               -Dalfresco.port=8080 \
               -Dalfresco.url="http://localhost:8080/alfresco" \
               -Dshare.port=8082 \
               -Dshare.url="http://localhost:8082/share" \
               -Dadmin.user=admin '-Dadmin.password=admin' -Dbrowser.name=chrome \
               -Daims.enabled=false \
               -Dsuite-name=${XML_SUITE}& # send the long living command to background!

# wait for the exit code of the background process
wait $!
SUCCESS=$?

popd
set +vex
echo "=========================== Finishing Project Alfresco Tas Share =========================="

exit ${SUCCESS}
