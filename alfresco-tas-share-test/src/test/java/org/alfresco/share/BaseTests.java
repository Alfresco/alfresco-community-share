package org.alfresco.share;

import java.util.Arrays;
import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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

    protected ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    DriverProvider driverProvider = new DriverProvider();

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        webDriver.set(driverProvider.startWebDriver());
        navigateTo();
    }

    private void navigateTo() {
        try {
            webDriver.get().navigate().to("http://localhost:8080/share/page/");
//            new WebDriverWait(webDriver.get(), 30, 1).until(ExpectedConditions.urlToBe("http://localhost:8080/share/page/"));
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
        webDriver.get().manage().deleteAllCookies();
        webDriver.get().quit();
    }
}
