package org.alfresco.share;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.DefaultProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.WebDriverFactory;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestAisAuthentication;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.util.Base64;

/**
 * This class represents a test template which should be inherit by each test class.
 * e.g. ToolbarTests extends BaseTest
 *
 * This class should only contain common members/methods which are used in each test class
 */
@Slf4j
@Listeners(TestListener.class)
@ContextConfiguration(classes = ShareTestContext.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests
{
    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    protected DefaultProperties defaultProperties;

    @Autowired
    protected DataUserAIS dataUser;

    @Autowired
    protected DataAIS dataAIS;

    @Autowired
    protected DataSite dataSite;

    @Autowired
    protected DataGroup dataGroup;

    @Autowired
    protected Language language;

    @Autowired
    private RestAisAuthentication aisAuthentication;
    @Autowired
    private SiteService siteService;
    @Autowired
    protected ContentService contentService;

    private static final String authorization_header ="Authorization";

    private final ThreadLocal<CmisWrapper> cmisApi = new ThreadLocal<>();
    private final ThreadLocal<RestWrapper> restApi = new ThreadLocal<>();
    private final ThreadLocal<UserService> userService = new ThreadLocal<>();
    private final ThreadLocal<DataSite> dataSiteThread = new ThreadLocal<>();
    private final ThreadLocal<DataUserAIS> dataUserThread = new ThreadLocal<>();
    private final ThreadLocal<DataGroup> dataGroupThread = new ThreadLocal<>();

    protected final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    protected UserDashboardPage userDashboardPage;

    @BeforeMethod(alwaysRun = true)
    protected void beforeEachTest(ITestContext iTestContext)
    {
        setApplicationsContext();
        webDriver.set(webDriverFactory.createWebDriver());
        setTestContext(iTestContext, webDriver.get());

        userDashboardPage = new UserDashboardPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    protected void afterEachTest()
    {
        quitWebDriver();
    }

    private void setApplicationsContext()
    {
        cmisApi.set(applicationContext.getBean(CmisWrapper.class));
        restApi.set(applicationContext.getBean(RestWrapper.class));
        userService.set(applicationContext.getBean(UserService.class));
        dataSiteThread.set(applicationContext.getBean(DataSite.class));
        dataUserThread.set(applicationContext.getBean(DataUserAIS.class));
        dataGroupThread.set(applicationContext.getBean(DataGroup.class));
    }

    private void quitWebDriver()
    {
        try
        {
            if (webDriver.get() != null)
            {
                log.info("Quit webdriver..");
                webDriver.get().quit();
            }
        }
        catch (NoSuchSessionException noSuchSessionException)
        {
            log.warn("Webdriver is not quit: {}", noSuchSessionException.getMessage());
        }
        finally
        {
            log.info("Finally quit webdriver..");
            webDriver.get().quit();
        }
    }

    protected synchronized void authenticateUsingCookies(UserModel userModel)
    {
        Authenticator authenticator = new Authenticator(dataAIS, userService, webDriver);
        authenticator
            .authenticateUsingCookies(userModel, userDashboardPage, defaultProperties.getShareUrl().toString());
    }

    protected synchronized void authenticateUsingLoginPage(UserModel userModel)
    {
        Authenticator authenticator = new Authenticator(webDriver, dataAIS);
        authenticator.authenticateUsingLoginPage(userModel, userDashboardPage);
    }

    protected void deleteAllCookiesIfNotNull()
    {
        if (webDriver.get().manage().getCookies() != null)
        {
            log.info("Delete all cookies");
            webDriver.get().manage().deleteAllCookies();
        }
    }

    protected UserModel getAdminUser()
    {
        UserModel adminUser = dataUser.getAdminUser();
        adminUser.setFirstName(defaultProperties.getAdminName());
        return adminUser;
    }

    protected CmisWrapper getCmisApi()
    {
        return cmisApi.get();
    }

    protected RestWrapper getRestApi()
    {
        return restApi.get();
    }

    protected UserService getUserService()
    {
        return userService.get();
    }

    protected DataSite getDataSite()
    {
        return dataSiteThread.get();
    }

    protected DataUserAIS getDataUser()
    {
        return dataUserThread.get();
    }

    protected DataGroup getDataGroup()
    {
        return dataGroupThread.get();
    }

    protected void deleteUsersIfNotNull(UserModel... users)
    {
        for (UserModel userModel : users)
        {
            if (userModel != null)
            {
                try
                {
                    dataUser.usingAdmin().deleteUser(userModel);
                }
                catch (DataPreparationException e)
                {
                    log.error("Failed to delete user {}", userModel.getUsername());
                }
                catch (RuntimeException e)
                {
                    log.error("User {} does not exist", userModel.getUsername());
                }
            }
        }
    }

    protected void deleteSitesIfNotNull(SiteModel... sites)
    {
        for (SiteModel siteModel : sites)
        {
            if (siteModel != null)
            {
                dataSite.usingAdmin().deleteSite(siteModel);
            }
        }
    }

    private ITestContext setTestContext(ITestContext iTestContext, WebDriver driver)
    {
        iTestContext.setAttribute("driver", driver);
        return iTestContext;
    }

    protected RestWrapper setAuthorizationRequestHeader(RestWrapper restWrapper) {
        UserModel user = restWrapper.getTestUser();
        if (null != user) {
            if (!this.aisAuthentication.isAisAuthenticationEnabled()) {
                String usernameColonPassword = user.getUsername() + ":" + user.getPassword();
                String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
                restWrapper.configureRequestSpec().addHeader(authorization_header,authorizationHeader);
            }
        }
    return restWrapper;
    }
    protected void addDashlet(UserModel user, SiteModel siteModel, DashboardCustomization.SiteDashlet dashlet, int columnNumber, int position)
    {
        siteService.addDashlet(
            user.getUsername(),
            user.getPassword(),
            siteModel.getId(),
            dashlet,
            DashboardCustomization.DashletLayout.TWO_COLUMNS_WIDE_RIGHT,
            columnNumber,
            position
        );
    }
}
