package org.alfresco.common;

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;
import static io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver;

import java.io.File;
import java.util.HashMap;
import org.alfresco.utility.exception.UnrecognizedBrowser;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class creates selenium webdriver specific for each browser and OS.
 */
@Component
public class WebDriverFactory
{
    private static final String CHROME = "chrome";
    private static final String FIREFOX = "firefox";

    @Autowired
    private DefaultProperties properties;

    private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    public WebDriver createWebDriver()
    {
        webDriver.set(createWebDriverBasedOnOS(properties));

        if (properties.getBrowserName().equalsIgnoreCase(FIREFOX) && !properties.isBrowserHeadless())
        {
            webDriver.get().manage().window().maximize();
        }
        return webDriver.get();
    }

    private WebDriver createWebDriverBasedOnOS(DefaultProperties properties)
    {
        switch (properties.getBrowserName().toLowerCase())
        {
            case FIREFOX:
                firefoxdriver().driverVersion(properties.getCurrentWebDriverVersion()).setup();
                return new FirefoxDriver(setFirefoxBrowserOptions(properties));

            case CHROME:
                chromedriver().driverVersion(properties.getCurrentWebDriverVersion()).setup();
                return new ChromeDriver(setChromeBrowserOptions(properties));
            default:
                throw new UnrecognizedBrowser(properties.getBrowserName());
        }
    }

    private ChromeOptions setChromeBrowserOptions(DefaultProperties properties)
    {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--single-process");
        chromeOptions.setHeadless(properties.isBrowserHeadless());
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        //disable chrome browser info bar
        chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        chromeOptions.addArguments(String.format("--lang=%s", getBrowserLanguage(properties)));

        //disable profile password manager
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);

        chromePrefs.put("download.default_directory", getDownloadLocation());
        chromeOptions.setExperimentalOption("prefs", chromePrefs);
        return chromeOptions;
    }

    private FirefoxOptions setFirefoxBrowserOptions(DefaultProperties properties)
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
        firefoxOptions.setHeadless(properties.isBrowserHeadless());

        return firefoxOptions;
    }

    private String getDownloadLocation()
    {
        String srcRoot = System.getProperty("user.dir") + File.separator;
        return srcRoot + "testdata" + File.separator;
    }

    private String getBrowserLanguage(DefaultProperties properties)
    {
        if(!StringUtils.isEmpty(properties.getBrowserLanguageCountry()))
        {
            return properties.getBrowserLanguage() + "-" +  properties.getBrowserLanguageCountry();
        }
        return properties.getBrowserLanguage();
    }
}
