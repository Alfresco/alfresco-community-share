package org.alfresco.browser;

import java.io.File;

import org.alfresco.common.EnvProperties;
import org.alfresco.po.exception.UnrecognizedBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Return differed DesiredCapabilities for {@link WebDriver}
 * 
 * @author Paul.Brodner
 */
public enum Browser 
{
    FIREFOX(DesiredCapabilities.firefox()),
    CHROME(DesiredCapabilities.chrome()),
    HTMLUNIT(DesiredCapabilities.htmlUnit()),
    INTERNETEXPLORER(DesiredCapabilities.internetExplorer()),
    OPERA(DesiredCapabilities.operaBlink()),
   
    /*
     * Add Safari-Driver extension prior to tests
     * http://selenium-release.storage.googleapis.com/2.48/SafariDriver.safariextz
     */
    SAFARI(DesiredCapabilities.safari());

    public static Browser getBrowserFromProperties(EnvProperties properties)
    {
        return valueOf(properties.getBrowserName().toUpperCase());
    }

    private final DesiredCapabilities capabilities;

    Browser(DesiredCapabilities caps)
    {
        this.capabilities = caps;
    }

    public DesiredCapabilities getCapabilities()
    {
        return capabilities;
    }
    
    
    /*
     * Change Firefox browser's default download location to testdata folder
     * return type : FirefoxProfile
     */
    public static FirefoxProfile changeFirefoxDownloadLocationToTestDataFolder()
    {
        String srcRoot = System.getProperty("user.dir") + File.separator; 
        String testDataFolder = srcRoot + "testdata" + File.separator;
        
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.dir",testDataFolder ); 
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        
        return profile;
    }

    public static WebDriver fromProperties(EnvProperties properties)
    {
                    
        switch (properties.getBrowserName().toLowerCase())
        {
            case "firefox":
                return new FirefoxDriver(changeFirefoxDownloadLocationToTestDataFolder());
            case "chrome":
                System.setProperty("webdriver.chrome.driver", properties.getEnv().getProperty("browser.chrome.driver"));
                return new ChromeDriver();
            case "ie":
                return new InternetExplorerDriver();
            case "htmlunit":
                return new HtmlUnitDriver();

            case "safari":
                return new SafariDriver();
            default:
                throw new UnrecognizedBrowser(properties.getBrowserName());
        }
    }
    
}
