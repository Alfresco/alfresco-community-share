.DEFAULT_GOAL := help

bamboo_JAVA_HOME 	?= /opt/jdk-8
mvnArgs        		?=-DsuiteXmlFile=src/test/resources/share-smoke-with-docker.xml 

ifeq ($(SERVER),)
	SERVER := cd acs && docker-compose
endif

ifeq ($(MVN),)
	ifeq ($(bamboo_working_directory),) 
		MVN	:= mvn
	else 
		# on bamboo environment define the environment
		export M2_HOME=
		MVN:=/opt/maven-3.3/bin/mvn
		export JAVA_HOME=$(bamboo_JAVA_HOME)
	endif	
endif

export GIT_AUTHOR_NAME=bamboo_auth
export GIT_AUTHOR_EMAIL=bamboo_auth@internal.alfresco.com
export GIT_COMMITTER_NAME=bamboo_auth
export GIT_COMMITTER_EMAIL=bamboo_auth@internal.alfresco.com

compile:
	$(MVN) --version && $(MVN) --batch-mode compile -U -DskipTests

help: ## main: output this help	
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)



test: ## run maven tests, you can override 'mvnArgs':  make test mvnArgs=-Dtest=SeleniumValidationTest
	$(MVN) --batch-mode clean test -Denvironment=docker $(mvnArgs)

release: ## perform the release, automatically increase the version
	$(MVN) --batch-mode release:prepare release:perform \
	-Dmaven.javadoc.skip=true \
	-Dresume=false \
	-Dusername=$(GIT_COMMITTER_NAME) \
	-Dpassword=$(bamboo_auth_ldap_password) \
	"-Darguments=-Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dadditionalparam=-Xdoclint:none"

acs-up: ## will install ACS with docker-compose according to profile
	$(SERVER) config
	$(SERVER) up -d
	./acs/wait-service-to-start.sh

acs-down:	## will stop ACS docker-compose according to profile
	$(SERVER) kill
	$(SERVER) rm -fv

acs-logs: ## show logs
	$(SERVER) logs -f	
