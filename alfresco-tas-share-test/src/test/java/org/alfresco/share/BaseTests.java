package org.alfresco.share;

import java.io.File;
import java.util.Arrays;
import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.ExecuteMethod;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.html5.RemoteLocalStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(classes = ShareTestContext.class)
public class BaseTests extends AbstractTestNGSpringContextTests {

    @Autowired
    protected SiteDashboardPage siteDashboardPage;

    @Autowired
    DataSite dataSite;

    @Autowired
    DataUser dataUser;

    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;
    public static String FILE_CONTENT = "Share file content";

    protected String adminUser;
    protected String adminPassword;
    protected String adminName;
    protected String domain;
    protected String password = "password";

    protected WebDriver webDriver;
    private DriverProvider driverProvider = new DriverProvider();

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        webDriver = driverProvider.startWebDriver();
        navigateTo();

        new WebDriverWait(webDriver, 30, 1)
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[id$='_default-submit-button']")));
    }

    private void navigateTo() {
        try {
            webDriver.navigate().to("http://localhost:8080/share/page/");
        }catch (Exception e) {
        }
    }

    public void removeUserFromAlfresco(UserModel... users)
    {
        Arrays.stream(users).forEach(user -> {
            dataUser.usingAdmin().deleteUser(user);
        });
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest() {
        webDriver.manage().deleteAllCookies();
        webDriver.quit();
    }
}
