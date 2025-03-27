package org.alfresco.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:default.properties")
@PropertySource(value = "classpath:${environment}.properties", ignoreResourceNotFound = true)
public class DefaultProperties
{
    @Autowired
    Environment env;

    @Value("${admin.user:admin}")
    private String adminUserName;

    @Value("${admin.password:admin}")
    private String adminPassword;

    @Value ("${admin.name}")
    private String adminName;

    @Value("${alfresco.scheme:http}")
    private String scheme;

    @Value("${alfresco.server:localhost}")
    private String server;

    @Value("${alfresco.port:8070}")
    private int port;

    @Value("${jmx.user:controlRole}")
    private String jmxUser;

    @Value("${jmx.password:change_asap}")
    private String jmxPassword;

    @Value("${jmx.port:50500}")
    private String jmxPort;

    @Value("${jmx.useJolokiaAgent:true}")
    private Boolean useJolokiaJmxAgent;

    @Value("${db.url:localhost}")
    private String dbUrl;

    @Value("${db.username:alfresco}")
    private String dbUsername;

    @Value("${db.password:alfresco}")
    private String dbPassword;

    @Value("${serverHealth.showTenants:true}")
    private Boolean showTenantsOnServerHealth;

    @Value("${browser.explicitWait}")
    private long explicitWait;

    @Value("${browser.pollingTimeInMillis}")
    private long pollingTimeInMillis;

    @Value("${grid.url:not-set}")
    private String gridUrl;

    @Value("${grid.enabled:false}")
    private boolean gridEnabled;

    @Value("${screenshots.dir:screenshots}")
    private File screenshotsDir;

    @Value("${env.platform:WINDOWS}")
    private String envPlatformName;

    @Value("${share.url:http://localhost}")
    private URL shareUrl;

    @Value("${auth.context.factory:com.sun.jndi.ldap.LdapCtxFactory}")
    private String authContextFactory;

    @Value("${auth.security.authentication:simple}")
    private String securityAuth;

    @Value("${oracle.url:ldap://172.29.100.111:2389}")
    private String oracleURL;

    @Value("${oracle.security.principal:cn=Directory Manager}")
    private String oracleSecurityPrincipal;

    @Value("${oracle.security.credentials:directory}")
    private String oracleSecurityCredentials;

    @Value("${ldap.url:ldap://172.29.100.119:389}")
    private String ldapURL;

    @Value("${ldap.search.base:CN=Users,DC=alfresconess,DC=com}")
    private String ldapSearchBase;

    @Value("${ldap2.search.base:CN=Users,DC=alfresconess2,DC=com}")
    private String ldapSearchBase2;

    @Value("${ldap.security.principal:CN=Administrator,CN=Users,DC=alfness,DC=com}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.security.credentials:Alf1234}")
    private String ldapSecurityCredentials;

    @Value("${ldap2.url:ldap://1/52.213.122.208:389}")
    private String ldap2URL;

    @Value("${ldap2.security.principal:CN=Administrator,CN=Users,DC=alfness,DC=com}")
    private String ldap2SecurityPrincipal;

    @Value("${ldap2.security.credentials:Alf1234}")
    private String ldap2SecurityCredentials;

    @Value("${oldap.url:ldap://172.29.100.226:389}")
    private String oldapURL;

    @Value("${oldap.security.principal:CN=admin,DC=alfness,DC=com}")
    private String oldapSecurityPrincipal;

    @Value("${oldap.security.credentials:Alf1234}")
    private String oldapSecurityCredentials;

    @Value("${ntlm.host:172.29.100.126}")
    private String ntlmHost;

    @Value("${ntlm.security.principal:alfntlm\\Administrator}")
    private String ntlmSecurityPrincipal;

    @Value("${ntlm.security.credentials:Alf1234}")
    private String ntlmSecurityCredentials;

    @Value("${sync.scheme:http}")
    private String syncScheme;

    @Value("${sync.server:localhost}")
    private String syncServer;

    @Value("${sync.port:9090}")
    private int syncPort;

    @Value("${browser.language:en}")
    private String browserLanguage;

    @Value("${browser.language.country:gb}")
    private String browserLanguageCountry;

    @Value("${solrWaitTimeInSeconds:60}")
    private int solrWaitTimeInSeconds;

    @Value("${solr.scheme:http}")
    private String solrScheme;

    @Value("${solr.server:localhost}")
    private String solrServer;

    @Value("${solr.port:8983}")
    private int solrPort;

    @Value("${display.xport:1}")
    private String displayXport;

    @Value("${browser.headless}")
    private boolean browserHeadless;

    @Value ("${browser.name}")
    private String browserName;

    @Value("${aims.enabled}")
    private boolean aimsEnabled;

    @Value ("${locale.language}")
    private String language;

    @Value ("${locale.country}")
    private String country;

    @Value("${chrome.options.disable.dev.shm.usage}")
    private String disableDevShmUsage;

    @Value("${chrome.options.disable.extensions}")
    private String disableExtensionsChrome;

    @Value("${chrome.options.single.process}")
    private String singleProcess;

    @Value("${chrome.options.start.maximized}")
    private String startMaximizedChrome;

    @Value("${chrome.options.window.size}")
    private String windowSizeChrome;

    @Value("${chrome.options.credentials.enable.service}")
    private String credentialsEnableServiceChrome;

    @Value("${chrome.options.profile.password.manager.enabled}")
    private String profilePasswordManagerEnabledChrome;

    @Value("${chrome.options.no.sandbox}")
    private String noSandboxChrome;

    @Value("${chrome.options.disable.gpu}")
    private String disableGpuChrome;



    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public Boolean showTenantsOnServerHealth()
    {
        return showTenantsOnServerHealth;
    }

    /**
     * # in containers we cannot access directly JMX, so we will use {@link <a href="http://jolokia.org">...</a>} agent
     * # disabling this we will use direct JMX calls to server
     */
    public boolean useJolokiaJmxAgent()
    {
        return useJolokiaJmxAgent;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Environment getEnv()
    {
        return env;
    }

    public String getAdminUser()
    {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName)
    {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword)
    {
        this.adminPassword = adminPassword;
    }

    public String getAdminName()
    {
        return adminName;
    }

    public void setAdminName(String adminName)
    {
        this.adminName = adminName;
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme(String scheme)
    {
        this.scheme = scheme;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getJmxUser()
    {
        return jmxUser;
    }

    public void setJmxUser(String jmxUser)
    {
        this.jmxUser = jmxUser;
    }

    public String getJmxPassword()
    {
        return jmxPassword;
    }

    public void setJmxPassword(String jmxPassword)
    {
        this.jmxPassword = jmxPassword;
    }

    public String getJmxPort()
    {
        return jmxPort;
    }

    public void setJmxPort(String jmxPort)
    {
        this.jmxPort = jmxPort;
    }

    public String getJmxUrl()
    {
        return String.format("service:jmx:rmi:///jndi/rmi://%s:%s/alfresco/jmxrmi", getServer(), getJmxPort());
    }

    /**
     * @return host: <schema>://<server>:<port>
     */
    public String getFullServerUrl()
    {
        return new UrlBuilder(getScheme(), getServer(), getPort(), "").toString();
    }

    /**
     * @return host: <schema>://<server>
     */
    public String getTestServerUrl()
    {
        return String.format("%s://%s", getScheme(), getServer());
    }

    public String getDbUrl()
    {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl)
    {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername()
    {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername)
    {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword()
    {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword)
    {
        this.dbPassword = dbPassword;
    }

    public long getExplicitWait()
    {
        return explicitWait;
    }

    public long getPollingTimeInMillis()
    {
        return pollingTimeInMillis;
    }

    public void setExplicitWait(long explicitWait)
    {
        this.explicitWait = explicitWait;
    }

    public URL getGridUrl() throws MalformedURLException
    {
        return new URL(gridUrl);
    }

    public void setGridUrl(String gridUrl)
    {
        this.gridUrl = gridUrl;
    }

    public boolean isGridEnabled()
    {
        return gridEnabled;
    }

    public void setGridEnabled(boolean gridEnabled)
    {
        this.gridEnabled = gridEnabled;
    }

    public File getScreenshotsDir()
    {
        return screenshotsDir;
    }

    public void setScreenshotsDir(String screenshotsDir)
    {
        File f = Paths.get(screenshotsDir).toFile();
        if (f.isFile() && !f.exists())
        {
            f.getParentFile().mkdirs();
        }
        else if (!f.exists())
        {
            f.mkdirs();
        }
        this.screenshotsDir = f;
    }

    public String getLdapSearchBase()
    {
        return ldapSearchBase;
    }

    public void setLdapSearchBase(String ldapSearchBase)
    {
        this.ldapSearchBase = ldapSearchBase;
    }

    public String getLdapSearchBase2()
    {
        return ldapSearchBase2;
    }

    public void setLdapSearchBase2(String ldapSearchBase2)
    {
        this.ldapSearchBase2 = ldapSearchBase2;
    }

    public String getEnvPlatformName()
    {
        return envPlatformName;
    }

    public void setEnvPlatformName(String envPlatformName)
    {
        this.envPlatformName = envPlatformName;
    }

    public URL getShareUrl()
    {
        return shareUrl;
    }

    public void setShareUrl(URL shareUrl)
    {
        this.shareUrl = shareUrl;
    }

    public String getAuthContextFactory()
    {
        return authContextFactory;
    }

    public void setAuthContextFactory(String authContextFactory)
    {
        this.authContextFactory = authContextFactory;
    }

    public String getSecurityAuth()
    {
        return securityAuth;
    }

    /**
     * @return host: <schema>://<server>
     */
    public String getSyncServerUrl()
    {
        return String.format("%s://%s", getSyncScheme(), getSyncServer());
    }

    public void setSecurityAuth(String securityAuth)
    {
        this.securityAuth = securityAuth;
    }

    public String getOracleURL()
    {
        return oracleURL;
    }

    public void setOracleURL(String oracleURL)
    {
        this.oracleURL = oracleURL;
    }

    public String getOracleSecurityPrincipal()
    {
        return oracleSecurityPrincipal;
    }

    public void setOracleSecurityPrincipal(String oracleSecurityPrincipal)
    {
        this.oracleSecurityPrincipal = oracleSecurityPrincipal;
    }

    public String getOracleSecurityCredentials()
    {
        return oracleSecurityCredentials;
    }

    public void setOracleSecurityCredentials(String oracleSecurityCredentials)
    {
        this.oracleSecurityCredentials = oracleSecurityCredentials;
    }

    public String getLdapURL()
    {
        return ldapURL;
    }

    public String getLdap2URL()
    {
        return ldap2URL;
    }

    public void setLdapURL(String ldapURL)
    {
        this.ldapURL = ldapURL;
    }

    public void setLdap2URL(String ldap2URL)
    {
        this.ldap2URL = ldap2URL;
    }

    public String getLdapSecurityPrincipal()
    {
        return ldapSecurityPrincipal;
    }

    public String getLdap2SecurityPrincipal()
    {
        return ldap2SecurityPrincipal;
    }

    public void setLdapSecurityPrincipal(String ldapSecurityPrincipal)
    {
        this.ldapSecurityPrincipal = ldapSecurityPrincipal;
    }

    public void setLdap2SecurityPrincipal(String ldap2SecurityPrincipal)
    {
        this.ldap2SecurityPrincipal = ldap2SecurityPrincipal;
    }

    public String getLdapSecurityCredentials()
    {
        return ldapSecurityCredentials;
    }

    public String getLdap2SecurityCredentials()
    {
        return ldap2SecurityCredentials;
    }

    public void setLdapSecurityCredentials(String ldapSecurityCredentials)
    {
        this.ldapSecurityCredentials = ldapSecurityCredentials;
    }

    public void setLdap2SecurityCredentials(String ldap2SecurityCredentials)
    {
        this.ldap2SecurityCredentials = ldap2SecurityCredentials;
    }

    public String getOLdapURL()
    {
        return oldapURL;
    }

    public void setOLdapURL(String oldapURL)
    {
        this.oldapURL = oldapURL;
    }

    public String getOLdapSecurityPrincipal()
    {
        return oldapSecurityPrincipal;
    }

    public void setOLdapSecurityPrincipal(String oldapSecurityPrincipal)
    {
        this.oldapSecurityPrincipal = oldapSecurityPrincipal;
    }

    public String getOLdapSecurityCredentials()
    {
        return oldapSecurityCredentials;
    }

    public void setOLdapSecurityCredentials(String oldapSecurityCredentials)
    {
        this.oldapSecurityCredentials = oldapSecurityCredentials;
    }

    public String getNtlmHost()
    {
        return ntlmHost;
    }

    public void setNtlmHost(String ntlmHost)
    {
        this.ntlmHost = ntlmHost;
    }

    public String getNtlmSecurityPrincipal()
    {
        return ntlmSecurityPrincipal;
    }

    public void setNtlmSecurityPrincipal(String ntlmSecurityPrincipal)
    {
        this.ntlmSecurityPrincipal = ntlmSecurityPrincipal;
    }

    public String getNtlmSecurityCredentials()
    {
        return ntlmSecurityCredentials;
    }

    public void setNtlmSecurityCredentials(String ntlmSecurityCredentials)
    {
        this.ntlmSecurityCredentials = ntlmSecurityCredentials;
    }

    public String getSyncScheme()
    {
        return syncScheme;
    }

    public void setSyncScheme(String syncScheme)
    {
        this.syncScheme = syncScheme;
    }

    public String getSyncServer()
    {
        return syncServer;
    }

    public void setSyncServer(String syncServer)
    {
        this.syncServer = syncServer;
    }

    public int getSyncPort()
    {
        return syncPort;
    }

    public void setSyncPort(int syncPort)
    {
        this.syncPort = syncPort;
    }

    public String getBrowserLanguage()
    {
        return browserLanguage;
    }

    public void setBrowserLanguage(String browserLanguage)
    {
        this.browserLanguage = browserLanguage;
    }

    public String getBrowserLanguageCountry()
    {
        return browserLanguageCountry;
    }

    public void setBrowserLanguageCountry(String browserLanguageCountry)
    {
        this.browserLanguageCountry = browserLanguageCountry;
    }

    public int getSolrWaitTimeInSeconds()
    {
        return solrWaitTimeInSeconds;
    }

    public void setSolrWaitTimeInSeconds(int solrWaitTimeInSeconds)
    {
        this.solrWaitTimeInSeconds = solrWaitTimeInSeconds;
    }

    public String getSolrScheme()
    {
        return solrScheme;
    }

    public void setSolrScheme(String solrScheme)
    {
        this.solrScheme = solrScheme;
    }

    public String getSolrServer()
    {
        return solrServer;
    }

    public void setSolrServer(String solrServer)
    {
        this.solrServer = solrServer;
    }

    public int getSolrPort()
    {
        return solrPort;
    }

    public void setSolrPort(int solrPort)
    {
        this.solrPort = solrPort;
    }

    /**
     * @return host: <schema>://<server>
     */
    public String getSolrServerUrl()
    {
        return String.format("%s://%s", getSolrScheme(), getSolrServer());
    }

    /**
     * @return displayXport
     */
    public String getDisplayXport()
    {
        return displayXport;
    }

    public boolean isBrowserHeadless()
    {
        return browserHeadless;
    }

    public String getBrowserName()
    {
        return browserName;
    }

    public boolean isAimsEnabled()
    {
        return aimsEnabled;
    }
    public String getNoSandboxChrome()
    {
        return noSandboxChrome;
    }

    public String getDisableGpuChrome()
    {
        return disableGpuChrome;
    }

    public String getDisableDevShmUsage()
    {
        return disableDevShmUsage;
    }

    public String getDisableExtensionsChrome()
    {
        return disableExtensionsChrome;
    }

    public String getSingleProcess()
    {
        return singleProcess;
    }

    public String getStartMaximizedChrome()
    {
        return startMaximizedChrome;
    }

    public String getWindowSizeChrome()
    {
        return windowSizeChrome;
    }

    public boolean getCredentialsEnableServiceChrome()
    {
        return Boolean.parseBoolean(credentialsEnableServiceChrome);
    }

    public boolean getProfilePasswordManagerEnabledChrome()
    {
        return Boolean.parseBoolean(profilePasswordManagerEnabledChrome);
    }

}

