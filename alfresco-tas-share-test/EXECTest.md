# SHARE-TEST IN ALFRESCO-PIPELINE
Share UI Tests are integrated using the infrastructure of: https://github.com/Alfresco/terraform-alfresco-pipeline

## Modules of alfresco-pipeline infrastructure
1. **CI**: The main source of configuration for the creation of the environment is in the .github/workflows/build.yml file. In this file is imported `share-test.yml`
2. **Deploy**: The root folder for any architectural component of the alfresco infrastructure is `module` folder. In this modules are described Alfresco services and Amazon services/infrastructure needed to run the Alfresco platform in an `AWS EKS Cluster`. If you need to add alfresco global properties you have `modules/acs/main.tf` where is defined the recipe for deploy of acs and share modules.
3. **Tests**: The run command for test are defined in scripts folder. For share we have `execute-share-web.sh`.
The main run command is : 

```java
mvn clean install \
        -DexcludeGroups='google-docs,unit,SmartFolders,ExternalUsers,tobefixed,office,TransformationServer,xsstests' \
        -DrunBugs=false \
        -Dalfresco.scheme=https \
        -Dalfresco.server=${ACS_HOST} \
        -Dalfresco.port=443 \
        -Dalfresco.url=${REPO_URL} \
        -Dshare.port=443 \
        -Dshare.url=${SHARE_URL} \
        -Dalfresco.scheme=https \
        -D[secure].user=${ADMIN_USER} \
        -D[secure].[secure]=${ADMIN_PASS} \
        -Dwebdriver.grid.url='http://127.0.0.1:4444/wd/hub' \
        -Dwebdriver.element.wait.time=30000 \
        -Dwebdriver.page.render.wait.time=60000 \
        -Dbrowser.name=Firefox \
        -Dbrowser.version=44.0 \
        -Dbrowser.headless=true \
        -Ddisplay.xport=99.0 \
        -Daims.enabled=true \
        -Denvironment=aims-environment \
        -Didentity-service.auth-server-url=${IDENTITY_HOST} \
        -Denv.platform=linux \
        $MVN_ARGS_SUITE
```

## Branches and Commit Messages

* Create a branch (from develop) which must contain env in the name **(branch =~ /.*env.*/ )** and commit message **[create env]**. 
  As a result the stages Create EKS Cluster and Deploy Applications are running.
 Example branch name : apps-env-share 
=> alfresvo-url : http://apps-share.envalfresco.com/alfresco/
=> share-url : http://apps-share.envalfresco.com/share/
=> aims-url: http://apps-share.envalfresco.com/auth/

* Execute share test commit with message: **[execute SHARE test]**.
* Cleanup work (destroy cluster): the cluster is destroyed using commit message: **[delete env]**.
* Recreating the cluster: the cluster is recreated using commit message: **[recreate env]**.







