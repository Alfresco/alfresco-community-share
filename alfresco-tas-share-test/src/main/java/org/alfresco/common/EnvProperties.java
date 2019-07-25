package org.alfresco.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * Generic Properties class that will load and override properties based on environment defined
 * For "test" environment just define
 * {code}
 * System.setProperty("environment", "test");
 * {code}
 * or run your test passing -Denvironment=test
 *
 * @author Paul.Brodner
 */
@Configuration
@PropertySource ("classpath:default.properties")
@PropertySource (value = "classpath:alfresco-share-po-${environment}.properties", ignoreResourceNotFound = true)
public class EnvProperties
{
    @Autowired
    Environment env;

    @Value ("${browser.name}")
    private String browserName;

    @Value ("${browser.version}")
    private String browserVersion;

    @Value ("${browser.implicitWait}")
    private long implicitWait;

    @Value ("${grid.url}")
    private String gridUrl;

    @Value ("${grid.enabled}")
    private boolean gridEnabled;

    @Value ("${screenshots.dir}")
    private File screenshotsPath;

    @Value ("${env.platform}")
    private String envPlatformName;

    @Value ("${share.url}")
    private URL shareUrl;

    @Value ("${alfresco.url}")
    private URL alfrescoUrl;

    @Value ("${server2.url}")
    private String server2Url;

    @Value ("${server2.port}")
    private String server2Port;

    @Value ("${share2.url}")
    private URL share2Url;

    @Value ("${admin.user}")
    private String adminUserName;

    @Value ("${admin.password}")
    private String adminPassword;

    @Value ("${admin.name}")
    private String adminName;

    @Value ("${locale.language}")
    private String language;

    @Value ("${locale.country}")
    private String country;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Environment getEnv()
    {
        return env;
    }

    public String getBrowserName()
    {
        return browserName;
    }

    public void setBrowserName(String browserName)
    {
        this.browserName = browserName;
    }

    public long getImplicitWait()
    {
        return implicitWait;
    }

    public void setImplicitWait(long implicitWait)
    {
        this.implicitWait = implicitWait;
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

    public File getScreenshotsPath()
    {
        return screenshotsPath;
    }

    public void setScreenshotsPath(String screenshotsPath)
    {
        File f = Paths.get(screenshotsPath).toFile();
        if (f.isFile() && !f.exists())
        {
            f.getParentFile().mkdirs();
        } else if (!f.exists())
        {
            f.mkdirs();
        }
        this.screenshotsPath = f;
    }

    public String getBrowserVersion()
    {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion)
    {
        this.browserVersion = browserVersion;
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

    public URL getAlfrescoUrl()
    {
        return alfrescoUrl;
    }

    public void setAlfrescoUrl(URL alfrescoUrl)
    {
        this.alfrescoUrl = alfrescoUrl;
    }

    public URL getShare2Url()
    {
        return share2Url;
    }

    public String getServer2Port()
    {
        return server2Port;
    }

    public String getServer2Url()
    {
        return server2Url;
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
}
