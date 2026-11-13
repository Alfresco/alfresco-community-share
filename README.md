# Share

Alfresco Share (share.war) is a web application that runs on the Java Platform. In a development environment it is usually deployed and run on top of Apache Tomcat. 
Share is an option for the front end of the Alfresco Content Service offering. 

Share is built up of a main menu that leads to pages, which is similar to most other web applications that you might come across. However, there is one special page type called Dashboard that contains Dashlets. A Dashboard page can be configured by the end-user, who can add, remove, and organize the Dashlets on the page.

More about Share: https://docs.hyland.com/p/alfresco

## Maven publishing

Release artifacts are published to the Maven repository using GitHub OIDC
credentials from `cloudsmith-auth`, shared by dependency resolution and
deployment. No long-lived Maven repository secrets are required.

| Artifacts | GA and M releases | A releases, such as `26.3.0-A.1` |
|-----------|-------------------|--------------------------------|
| Share and community modules | `alfresco-community-releases` | `alfresco-internal-releases` |
| AGS enterprise and automation modules | `alfresco-enterprise-releases` | `alfresco-internal-releases` |
| Share TAS tests | `alfresco-internal-releases` | `alfresco-internal-releases` |

Deployment uses `https://maven.cloudsmith.io/hyland/<repository>/` until the DNS
record for `maven.artifacts.hyland.dev` exists. The existing pull mirror is unchanged.

The release workflow uses `maven-release-slim` to activate the `prerelease` profile
for `-A.x` versions alongside `ags,release`. Regular CI builds install snapshots
locally without publishing them.
