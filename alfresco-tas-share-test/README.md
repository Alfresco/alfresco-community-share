## About
This project is testing the Alfresco Share interface using Selenium WebDriver

## 1) How to run maven tests ?

### a) clone the repository

```shell
$ git clone https://git.alfresco.com/tas/alfresco-tas-share-test
```

### b) run maven tests

```shell
$ mvn clean install -Dtest=SeleniumValidationTest
```
> notice that you need to have a Firefox version installed and configured on your machine (or you can use the second option bellow: **containers**)

## 1) How to run test suite in parallel?
  
### Execute an already created test suite.
>  1.Open desired test suite.xml file and make sure you have specified parallel and thread-count attributes.
>    e.g. <suite name="Admin Tools Test Suite" parallel="methods" thread-count="5">
>    More info: https://testng.org/doc/documentation-main.html#parallel-suites
>
>  2.In terminal/command line type:
>  $ mvn clean install -DsuiteXmlFile=src/test/resources/test-suites/admin-tools-tests.xml

### Create and execute a custom test suite.
>  1.Make sure you have Create TestNG XML plugin installed.
>  2.Right click in project root where you would like to create your test suite.
>  3.Select Create TestNG XML.
>  4.Click Reload All from disk from the IDE. 
>  4.Customize your test suite.
>  More info: https://testng.org/doc/documentation-main.html#testng-xml
>  5.Follow steps from ### Execute an already created test suite.


## 2) How to run single test?
> mvn -Dtest=ToolbarTests#verifyAlfrescoToolbarItemsWithNormalUser test

## 3) How to run maven tests inside a docker container? (i.e Selenium RemoteWebDriver)

> inside `acs` folder you will find a [docker-compose.yml](./acs/docker-compose.yml) file that will spin up:
* alfresco
* share
* search
* and selenium-firefox

> the version of these services is specified by [.env](./acs/.env) file

### a) start docker compose

```shell
$ cd acs && docker-compose up && ./wait-service-to-start.sh
```
> in `acs` folder you will also find out a script that will wait for alfresco to start

### b) start maven tests using RemoteWebDriver
> Our Maven Tests will run inside the "selenium-firefox" container using the RemoteWebDriver so we need to pass the appropriate container ports for "alfresco" and "share" services.
> I've created a [docker.properties](./src/main/resources/docker.properties) file that will enable us to run Maven Tests over ACS started with docker-compose above (analyze the hosts/ports used)

```shell
mvn test -Denvironment=docker -Dtest=SeleniumValidationTest
```
> when we are passing -Denvironment=docker we are actually using the docker.properties file
> tests will run remotely inside 'selenium-firefox' container
> test results will be found on your HOST machine as usual.

### `(!)` Hint (Unix systems):
> there is a Makefile in the root of this folder with a couple of tasks
> that will do all of the steps above for you:

```shell
# will start docker with all services, wait for them and run the SeleniumValidationTest passed in 'mvnArgs'
$ make acs-up mvnArgs=-Dtest=SeleniumValidationTest test 
```

```shell
# will cleanup all containers
$ make acs-down 
```

> you can also watch how the test are executed inside selenium-firefox container using [VNCViewer](https://www.realvnc.com/en/connect/download/viewer/)
```
vnc://localhost:5900  
```

