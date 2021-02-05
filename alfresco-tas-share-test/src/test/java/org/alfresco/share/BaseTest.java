package org.alfresco.share;

import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.DefaultProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ScreenshotHelper;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.WebDriverFactory;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginAimsPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.apache.commons.httpclient.HttpState;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * This class represents a test template which should be inherit by each test class.
 * e.g. ToolbarTests extends BaseTest
 * Only test class which will not use this template should be LoginTests.
 *
 * This class should only contain common members/methods which are used in each test class
 */
@ContextConfiguration(classes = ShareTestContext.class)
@Slf4j
public abstract class BaseTest extends AbstractTestNGSpringContextTests
{
    //private ScreenshotHelper screenshotHelper;

    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    private DefaultProperties defaultProperties;

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

    protected final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();
    private final ThreadLocal<CmisWrapper> cmisApi = new ThreadLocal<>();
    private final ThreadLocal<RestWrapper> restApi = new ThreadLocal<>();
    private final ThreadLocal<UserService> userService = new ThreadLocal<>();
    private final ThreadLocal<DataSite> dataSiteThread = new ThreadLocal<>();
    private final ThreadLocal<DataUserAIS> dataUserThread = new ThreadLocal<>();
    private final ThreadLocal<DataGroup> dataGroupThread = new ThreadLocal<>();

    protected LoginPage loginPage;
    protected UserDashboardPage userDashboardPage;
    protected Toolbar toolbar;

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest()
    {
        webDriver.set(webDriverFactory.createWebDriver());

        cmisApi.set(applicationContext.getBean(CmisWrapper.class));
        restApi.set(applicationContext.getBean(RestWrapper.class));
        userService.set(applicationContext.getBean(UserService.class));
        dataSiteThread.set(applicationContext.getBean(DataSite.class));
        dataUserThread.set(applicationContext.getBean(DataUserAIS.class));
        dataGroupThread.set(applicationContext.getBean(DataGroup.class));
        //screenshotHelper = new ScreenshotHelper(webDriver);

        loginPage = new LoginPage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        toolbar = new Toolbar(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(Method method, ITestResult result)
    {
        log.info("FINISHED TEST: {}, {}, {}", method.getDeclaringClass().getSimpleName(), method.getName(),
            result.isSuccess() ? "PASSED" : "FAILED");
        if(!result.isSuccess())
        {
            log.warn("TEST FAILED {}", method);
            //screenshotHelper.captureAndSaveScreenshot(webDriver, method);
        }
        closeWebDriver();
    }

    private void closeWebDriver()
    {
        try
        {
            if (webDriver.get() != null)
            {
                log.info("Close webdriver..");
                webDriver.get().quit();
            }
        }
        catch (NoSuchSessionException noSuchSessionException)
        {
            log.error("Webdriver is not closed: {}", noSuchSessionException.getMessage());
        }
        finally
        {
            log.info("Finally close webdriver..");
            webDriver.get().quit();
        }
    }

    public synchronized void setupAuthenticatedSession(UserModel user)
    {
        log.info("Setup authenticated session for user {}", user.getUsername());
        if(dataAIS.isEnabled()) // if identity-service is enabled do the login using the UI
        {
            if(userDashboardPage.isAlfrescoLogoDisplayed())
            {
                toolbar.clickUserMenu().clickLogout();
            }
            setupAuthenticatedSessionViaLoginPage(user);
        }
        else
        {
            authenticateViaCookies(user);
        }
    }

    public synchronized void setupAuthenticatedSessionViaLoginPage(UserModel userModel)
    {
        deleteAllCookiesIfNotNull();
        getLoginPage().navigate().login(userModel);
        userDashboardPage.waitForSharePageToLoad();
    }

    private void authenticateViaCookies(UserModel user)
    {
        webDriver.get().get(defaultProperties.getShareUrl().toString());
        HttpState state = getUserService().login(user.getUsername(), user.getPassword());
        deleteAllCookiesIfNotNull();
        Arrays.stream(state.getCookies()).forEach(cookie -> {
            cookie.setPath("/share");
            webDriver.get().manage().addCookie(new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath()));
        });
    }

    protected void deleteAllCookiesIfNotNull()
    {
        if (webDriver.get().manage().getCookies() != null)
        {
            log.info("Delete all cookies");
            webDriver.get().manage().deleteAllCookies();
        }
    }

    private CommonLoginPage getLoginPage()
    {
        if (dataAIS.isEnabled())
        {
            return new LoginAimsPage(webDriver);
        }
        return loginPage;
    }

    public UserModel getAdminUser()
    {
        return dataUser.getAdminUser();
    }

    public CmisWrapper getCmisApi()
    {
        return cmisApi.get();
    }

    public RestWrapper getRestApi()
    {
        return restApi.get();
    }

    public UserService getUserService()
    {
        return userService.get();
    }

    public DataSite getDataSite()
    {
        return dataSiteThread.get();
    }

    public DataUserAIS getDataUser()
    {
        return dataUserThread.get();
    }

    public DataGroup getDataGroup()
    {
        return dataGroupThread.get();
    }

    public void deleteUsersIfNotNull(UserModel... users)
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

    public void deleteSitesIfNotNull(SiteModel... sites)
    {
        for (SiteModel siteModel : sites)
        {
            if (siteModel != null)
            {
                dataSite.usingAdmin().deleteSite(siteModel);
            }
        }
    }
}
