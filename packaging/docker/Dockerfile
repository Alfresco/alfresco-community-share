# More infos about this image: https://github.com/Alfresco/alfresco-docker-base-tomcat
FROM alfresco/alfresco-base-tomcat:tomcat10-jre17-rockylinux9@sha256:00d89fb84bda7bb37c17b0117adb2cfe4f7cbddcd6c1e42b0a67ea8dbb41a734

# Needed for installation but make sure another USER directive is added after
# this with a non-root user
USER root

RUN mkdir -p /usr/local/tomcat/shared/classes/alfresco/web-extension
RUN mkdir -p /usr/local/tomcat/amps_share
RUN mkdir /licenses

COPY target/war /usr/local/tomcat/webapps
COPY target/alfresco-mmt/* /usr/local/tomcat/alfresco-mmt/
COPY target/amps_share /usr/local/tomcat/amps_share

COPY target/classes/web-extension-samples/share-config-custom.xml /usr/local/tomcat/shared/classes/alfresco/web-extension
COPY target/classes/web-extension-samples/custom-slingshot-application-context.xml.sample /usr/local/tomcat/shared/classes/alfresco/web-extension
COPY target/classes/web-extension-samples/smartfolders-amp-actions-config.xml /usr/local/tomcat/shared/classes/alfresco/web-extension

COPY target/licenses/* /licenses/

# not sure if this is needed for share:
RUN sed -i "s/shared.loader=/shared.loader=\${catalina.base}\/shared\/classes/" /usr/local/tomcat/conf/catalina.properties

COPY substituter.sh /usr/local/tomcat/shared/classes/alfresco
RUN chmod +x /usr/local/tomcat/shared/classes/alfresco/substituter.sh

# apply amps
RUN java -jar /usr/local/tomcat/alfresco-mmt/alfresco-mmt*.jar install \
              /usr/local/tomcat/amps_share /usr/local/tomcat/webapps/share -directory -nobackup -force

ENTRYPOINT ["/usr/local/tomcat/shared/classes/alfresco/substituter.sh", "catalina.sh run"]

EXPOSE 8000
LABEL quay.expires-after=${docker.quay-expires.value}
