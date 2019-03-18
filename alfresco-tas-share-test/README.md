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



## 2) How to run maven tests inside a docker container? (i.e Selenium RemoteWebDriver)

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

