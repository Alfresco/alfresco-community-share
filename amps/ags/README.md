# Records Management: README

## Contributing
Please refer to our [How to contribute](/CONTRIBUTING.md) guide and our [Contributor Covenant Code of Conduct](/CODE_OF_CONDUCT.md).

## Configuring Maven for local development
The project poms already declare the public Alfresco Maven repository
(`https://artifacts.hyland.dev/public/alfresco-public/maven/`, anonymous access) and Maven Central,
so no extra `~/.m2/settings.xml` configuration is needed to build the Community modules.

Browse the public repository at <https://artifacts.hyland.dev/public/alfresco-public/maven/>.

For additional instructions you can check the official Maven documentation:
* [setting up repositories](https://maven.apache.org/guides/mini/guide-multiple-repositories.html)
* [setting up servers](https://maven.apache.org/settings.html#servers)
* [password encryption](https://maven.apache.org/guides/mini/guide-encryption.html)

## Configuring and starting Alfresco/Share
* Clone the project (e.g. `git clone git@github.com:Alfresco/governance-services.git`)
* Import the project as a maven project
* Start the Alfresco/Share instances with the following commands:

   ```
   mvn clean install -Pstart-repo
   mvn clean install -Pstart-share
   ```

   (these commands work best if run from the specific directories, e.g. start Share from
   rm-community/rm-community-share/ or rm-enterprise/rm-enterprise-share/ )

## Configuring a different DB other than H2 (e.g. MySQL or PostgreSQL)
* Create a file called _local.properties_ under src/main/resources in alfresco-rm-enterprise-repo
* Add the following properties in this new file

   ```
   my.db.name -> The name of the database schema
   my.db.port -> The port number for your database (default port number for postgres is 5432 and for mysql it is 3306)
   ```
* Run the following commands to start your Alfresco instance:

   to start Alfresco (using Postgres):
   ```
   mvn clean install -Pstart-repo,use-postgres
   ```

   to start Alfresco (using MySQL):

   ```
   mvn clean install -Pstart-repo,use-mysql
   ```

## Technical documentation
Technical documentation is available at [rm-community/documentation/README.md](/rm-community/documentation/README.md) and [rm-enterprise/documentation/README.md](/rm-enterprise/documentation/README.md). This should be particularly useful for anyone wanting to integrate with or extend RM.

## Running integration test
In order to execute the integration tests run the following command (unit tests will be executed every time before you start Alfresco/Share):

```
mvn clean install -Pstart-db
```

## Running UI Automation tests
To run the automated UI tests, change to the rm-automation directory and run:

```
mvn clean install -Dskip.automationtests=false
```

```
Selenium Upgrade and Browser Configuration
```
With the upgrade from Selenium version 2.52 to 3.141.59, UI tests can now be executed in Chrome, Firefox, and Edge.

Users can specify the desired browser by setting the browser.name property in the browser.properties file.

Additionally, browser-related settings, such as enabling or disabling headless mode (headless=true/false) and password management options, can also be configured in the same file.

Ensure to update the browser.properties file as per your testing requirements to customize the test execution environment.

After that run UI test cases as usual

## Updating License Headers
In order to refesh out of date license source headers run the following command:

```
mvn clean install -Dlicense.update.dryrun=false
```

## Running tests against latest Aikau snapshot
The latest Aikau snapshot can be pulled by running the following command in rm-community:

```
mvn clean install -DskipTests -Dalfresco.aikau.version=LATEST -U
```

Thereafter start the Share instance and run automation tests as described above.

## SNAPSHOT dependencies
If you're building Enterprise RM, the base project (Community) is pulled in via a snapshot dependency configured in maven.
This dependency will either be loaded from your local .m2 cache, or from the Maven repository if the version in your .m2 doesn't exist or is old
('old' in maven terms is anything over 24 hours old). If maven fetches community dependencies from the Maven repository, then it's unlikely to contain your changes.
You want to always use the version in your local cache - this means either doing a daily build at the root project level
that pushes a new copy of the correct version into your cache, or alternatively you could run mvn with the
`--no-snapshot-dependency` (or `-nsu`) option, which won't try to download a newer version.

## Code Formatting
This project follows the usual Alfresco Coding Standards. If you use Eclipse or IntelliJ, there are settings inside the ide-config directory for you to import.

## Surf build errors
If you get:
```
[ERROR] Failed to execute goal on project alfresco-rm-community-share: Could not resolve dependencies for project org.alfresco:alfresco-rm-community-share:amp:2.6-SNAPSHOT: Failed to collect dependencies at org.alfresco.surf:spring-surf-api:jar:6.3 -> org.alfresco.surf:spring-surf:jar:${dependency.surf.version}: Failed to read artifact descriptor for org.alfresco.surf:spring-surf:jar:${dependency.surf.version}: Could not transfer artifact org.alfresco.surf:spring-surf:pom:${dependency.surf.version} from/to alfresco-internal (repository URL): Not authorized , ReasonPhrase:Unauthorized. -> [Help 1]
```

then please re-run with `-Ddependency.surf.version=6.3`

## Install lombok plugin for IDEs
To allow automation and benchmark projects to be built within an IDE the lombok 'plugin' needs to be installed.
Execute lombok.jar (doubleclick it, or run `java -jar lombok.jar`). Follow the instructions.

## Use Solr 6 with Alfresco 5.2.x
In alfresco-global.properties (depending on the RM edition `/records-management/rm-community/rm-community-repo/src/test/properties/local` or `/records-management/rm-enterprise/rm-enterprise-repo/src/test/properties/local`)
change the value for "index.subsystem.name" from "solr4" to "solr6".
Add also the following property "solr.port=8983".

Download the latest Alfresco Search Services from
[https://nexus.alfresco.com/nexus/#nexus-search;gav\~\~alfresco-search-services\~\~\~](https://nexus.alfresco.com/nexus/#nexus-search;gav~~alfresco-search-services~~~)
Currently it's 1.0.0 (alfresco-search-services-1.0.0.zip)

Unzip it and change to the "solr" folder within it. Start the Solr server using the following command:
```
solr start -a "-Dcreate.alfresco.defaults=alfresco,archive"
```
Start your repository

## Build Docker images for RM Repo and Share
A first step is checking that you have installed a working version of Docker that can be downloaded from here:
[https://docs.docker.com/install/]

The second step, in case you already have Docker installed, the current running images must be checked in order to be sure that they are not occupying any of the ports that
ACS and Share use.

To kill and clean all the images and containers the following command can be used:

```
docker system prune --volumes
```
 > Note that this will also remove all the stopped containers, containers, networks, volumes and build cache.

Depending on which version of AGS you want to start, Community or Enterprise, **you must first build the docker images**.
From the root folder of the project you can create both the Repo and the Share images for Community and Enterprise.
To build all the images use the following command:
```
mvn install -PbuildDockerImage
```
If only the Community or Enterprise images need to be built than the same command as above must be run either in the rm-community or rm-enterprise modules.

## Start the Docker images

The Docker images of the Repo can be started independently from Share running the following command in the rm-repo-enterprise or rm-repo-community folder which contains the Docker-compose.yml file:
```
docker compose up
```
> Be aware of the fact that the Share images can not be started independently from Repo

e.g. In order to start an instance of rm-enterprise-repo and rm-enterprise-share, the above command must be run in rm-enterprise-share after the images have been built.

## Start the Docker images with jRebel in remote server mode

If you have a license for jRebel then this can be used from the rm-community-share or rm-enterprise-share directories with:
```
docker compose -f docker-compose.yml -f jrebel-docker-compose.yml --project-name agsdev up --build --force-recreate
```
