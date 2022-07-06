## About
This project is testing the Alfresco Share interface using Selenium WebDriver

## How to run maven tests ?

### clone the repository

```shell
$ git clone https://git.alfresco.com/tas/alfresco-tas-share-test
```

### run single test

```shell
$ mvn -Dtest=ToolbarTests#verifyAlfrescoToolbarItemsWithNormalUser test
```

### run test suite in parallel:
  
#### Execute an already created test suite.
*  1.Open desired test suite.xml file and make sure you have specified parallel and thread-count attributes.
>  suite name="Admin Tools Test Suite" parallel="classes" thread-count="3"
> 
>  For a higher tests passing rate we recommend above configuration
*  2.In terminal/command type below:

```shell
$ mvn clean install -DsuiteXmlFile=src/test/resources/test-suites/admin-tools-tests.xml
```
  More info: https://testng.org/doc/documentation-main.html#parallel-suites

#### Create and execute a custom test suite.
*  1.Make sure you have Create TestNG XML plugin installed.
*  2.Right click in project root where you would like to create your test suite.
*  3.Select Create TestNG XML.
*  4.Click Reload All from disk from the IDE. 
*  5.Customize your test suite.
*  6.Follow steps from #### Execute an already created test suite.
 
  More info: https://testng.org/doc/documentation-main.html#testng-xml

## Lombok plugin
   * IntelliJ editor is compatible with **lombok without** a **plugin** as of version 2020.3
   * For versions prior to 2020.3, **you can** add the **Lombok IntelliJ plugin**
   to add **lombok** support for **IntelliJ**: Go to File > Settings > **Plugins**



