package org.alfresco.share;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverProvider {

    private WebDriver webDriver;

    public WebDriver startWebDriver() {
        webDriver = createChromeDriver();

        return webDriver;
    }

    private WebDriver createChromeDriver() {
        System.setProperty("webdriver.chrome.driver", "D:\\share-tests-automation\\src\\test\\java\\org\\alfresco\\share\\chromedriver.exe");
//        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--headless");
//        chromeOptions.addArguments("--no-sandbox");
//        chromeOptions.addArguments("--disable-dev-shm-usage");

        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);

        return new ChromeDriver(chromeOptions);
    }

    private WebDriver createFirefoxDriver() {
        System.setProperty("webdriver.gecko.driver", "D:\\share-tests-automation\\src\\test\\java\\org\\alfresco\\share\\geckodriver.exe");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("--disable-dev-shm-usage");

//        firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NONE);

        return new FirefoxDriver(firefoxOptions);
    }
}
