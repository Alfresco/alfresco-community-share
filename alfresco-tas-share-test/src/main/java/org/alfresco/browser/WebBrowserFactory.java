package org.alfresco.browser;

import java.net.MalformedURLException;

import org.alfresco.common.EnvProperties;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory of {@link WebBrowser} object
 * This will initialize automatically local/remote WebDriver based on settings defined *.properties/spring XML
 * file/system environment
 * Take a look on {@link ContextAwareParallelSampleTest} for a simple example on how to use it
 * 
 * @author Paul.Brodner
 */
@Component
public class WebBrowserFactory implements FactoryBean<WebBrowser>
{
    private static final Logger LOG = LoggerFactory.getLogger(WebBrowserFactory.class);

    @Autowired
    EnvProperties properties;

    public WebBrowser getWebBrowser() throws Exception
    {
        WebBrowser webbrowser = null;

        if (properties.isGridEnabled())
        {
            webbrowser = new WebBrowser(getRemoteWebDriver(properties));
        }
        else
        {
            webbrowser = new WebBrowser(Browser.fromProperties(properties));
        }

        //EventWebBrowserListener listener = new EventWebBrowserListener();
        //webbrowser.register(listener);

        /*long default_wait = Long.valueOf(properties.getImplicitWait());
        webbrowser.manage().timeouts().implicitlyWait(default_wait, TimeUnit.SECONDS);*/
        webbrowser.maximize();

        return webbrowser;
    }

    @Override
    public Class<?> getObjectType()
    {
        return WebBrowser.class;
    }

    @Override
    public boolean isSingleton()
    {
        return false;
    }

    /**
     * Get a new {@link }
     * 
     * @param properties
     * @return
     * @throws MalformedURLException
     */
    private static RemoteWebDriver getRemoteWebDriver(EnvProperties properties) throws MalformedURLException
    {
        LOG.info("Using RemoveWebdriver on Hub URL {}", properties.getGridUrl().toString());

        DesiredCapabilities caps = new DesiredCapabilities(Browser.getBrowserFromProperties(properties).getCapabilities());
        caps.setCapability("version", properties.getBrowserVersion());
        caps.setCapability("platform", properties.getEnvPlatformName());

        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(properties.getGridUrl(), caps);
        return remoteWebDriver;
    }

    @Override
    public WebBrowser getObject() throws Exception
    {
        return getWebBrowser();
    }

    public void quit() throws Exception
    {
        getWebBrowser().quit();
    }

}
