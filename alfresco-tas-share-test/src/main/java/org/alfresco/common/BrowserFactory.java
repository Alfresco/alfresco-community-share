package org.alfresco.common;

import static org.alfresco.common.Utils.getBrowserLanguage;
import static org.alfresco.common.Utils.getDownloadLocation;
import static org.alfresco.common.Utils.getGeckodriverResourceFile;
import static org.alfresco.utility.Utility.getTestResourceFile;

import java.util.HashMap;
import java.util.Map;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.exception.UnrecognizedBrowser;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BrowserFactory implements FactoryBean<WebBrowser>
{
    private static final String CHROME = "chrome";
    private static final String FIREFOX = "firefox";

    @Autowired
    private TasProperties properties;

    @Autowired
    private EnvProperties shareProperties;

    public WebBrowser createBrowser()
    {
        WebBrowser webBrowser = new WebBrowser(createBrowserBasedOnOS(properties), properties);
        webBrowser.maximize();

        return webBrowser;
    }

    public WebDriver createBrowserBasedOnOS(TasProperties properties)
    {
        switch (properties.getBrowserName().toLowerCase())
        {
            case FIREFOX:
                setFirefoxDriverPathBasedOnOS();
                if (SystemUtils.IS_OS_LINUX)
                {
                    Map<String, String> environment = new HashMap<>();
                    environment.put("DISPLAY", ":" + properties.getDisplayXport());
                    FirefoxOptions firefoxOptions = setFirefoxOptions(properties);

                    FirefoxBinary firefoxBinary = new FirefoxBinary();
                    firefoxOptions.setBinary(firefoxBinary);

                    return new FirefoxDriver(new GeckoDriverService.Builder().withEnvironment(environment).build(), firefoxOptions);
                }
                else
                {
                    return new FirefoxDriver(setFirefoxOptions(properties));
                }
            case CHROME:
                setChromeDriverPathBasedOnOS();
                HashMap<String, Object> chromePreferences = new HashMap<>();
                chromePreferences.put("profile.default_content_settings.popups", 0);
                chromePreferences.put("download.default_directory", getDownloadLocation());

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments(String.format("--lang=%s", getBrowserLanguage(properties)));
                chromeOptions.setExperimentalOption("prefs", chromePreferences);

                return new ChromeDriver(chromeOptions);
            default:
                throw new UnrecognizedBrowser(properties.getBrowserName());
        }
    }

    private FirefoxOptions setFirefoxOptions(TasProperties properties)
    {
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        firefoxOptions.addPreference("browser.download.dir", getDownloadLocation());
        firefoxOptions.addPreference("browser.download.folderList", 2);
        firefoxOptions.addPreference("browser.download.manager.alertOnEXEOpen", false);
        firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk",
            "application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, "
                + "application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream, "
                + "application/vnd.openxmlformats-officedocument.wordprocessingml.document,"
                + "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                + "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        firefoxOptions.addPreference("browser.download.manager.showWhenStarting", false);
        firefoxOptions.addPreference("browser.download.manager.focusWhenStarting", false);
        firefoxOptions.addPreference("browser.download.useDownloadDir", true);
        firefoxOptions.addPreference("browser.helperApps.alwaysAsk.force", false);
        firefoxOptions.addPreference("browser.download.manager.alertOnEXEOpen", false);
        firefoxOptions.addPreference("browser.download.manager.closeWhenDone", true);
        firefoxOptions.addPreference("browser.download.manager.showAlertOnComplete", false);
        firefoxOptions.addPreference("intl.accept_languages", getBrowserLanguage(properties));
        firefoxOptions.setAcceptInsecureCerts(true);
        firefoxOptions.setHeadless(shareProperties.isBrowserHeadless());

        return firefoxOptions;
    }

    private void setFirefoxDriverPathBasedOnOS()
    {
        String geckodriverPath;
        if (SystemUtils.IS_OS_WINDOWS)
        {
            geckodriverPath = "shared-resources/geckodriver/geckodriver.exe";
        }
        else if (SystemUtils.IS_OS_MAC)
        {
            geckodriverPath = "shared-resources/geckodriver/geckodriver_mac";
            getGeckodriverResourceFile(geckodriverPath).setExecutable(true);
        }
        else
        {
            geckodriverPath = "shared-resources/geckodriver/geckodriver_linux";
            getGeckodriverResourceFile(geckodriverPath).setExecutable(true);
        }
        System.setProperty("webdriver.gecko.driver", this.getClass().getClassLoader().getResource(geckodriverPath).getPath());
    }

    private void setChromeDriverPathBasedOnOS()
    {
        String chromedriverPath;
        if (SystemUtils.IS_OS_WINDOWS)
        {
            chromedriverPath = "shared-resources/chromedriver/chromedriver.exe";
        }
        else if (SystemUtils.IS_OS_MAC)
        {
            chromedriverPath = "shared-resources/chromedriver/chromedriver_mac";
            getTestResourceFile(chromedriverPath).setExecutable(true);
        }
        else
        {
            chromedriverPath = "shared-resources/chromedriver/chromedriver_linux";
            getTestResourceFile(chromedriverPath).setExecutable(true);
        }
        System.setProperty("webdriver.chrome.driver", getTestResourceFile(chromedriverPath).toString());
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

    @Override
    public WebBrowser getObject()
    {
        return createBrowser();
    }
}
