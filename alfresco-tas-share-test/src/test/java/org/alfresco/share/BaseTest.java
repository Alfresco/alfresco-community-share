package org.alfresco.share;

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

    private static final String AUTHORIZATION_HEADER ="Authorization";
    private static final int CREATE_USER_MAX_ATTEMPTS = 3;
    private static final long CREATE_USER_RETRY_WAIT_MILLIS = 2000L;

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

    // Retries user creation on transient "User ... not created" failures seen intermittently in CI under load.
    protected UserModel createRandomTestUserWithRetry()
    {
        return createRandomTestUserWithRetry(CREATE_USER_MAX_ATTEMPTS);
    }

    private UserModel createRandomTestUserWithRetry(int attemptsLeft)
    {
        try
        {
            return dataUser.usingAdmin().createRandomTestUser();
        }
        catch (DataPreparationException e)
        {
            if (attemptsLeft <= 1)
            {
                DataPreparationException wrapped = new DataPreparationException(
                    "Failed to create random test user after " + CREATE_USER_MAX_ATTEMPTS + " attempts: " + e.getMessage());
                wrapped.initCause(e);
                throw wrapped;
            }
            log.warn("Failed to create random test user, retrying ({} attempts left): {}", attemptsLeft - 1, e.getMessage());
            try
            {
                Thread.sleep(CREATE_USER_RETRY_WAIT_MILLIS);
            }
            catch (InterruptedException interruptedException)
            {
                Thread.currentThread().interrupt();
            }
            return createRandomTestUserWithRetry(attemptsLeft - 1);
        }
    }

    // Retries user creation on transient "User ... not created" failures seen intermittently in CI under load.
    protected UserModel createTestUserWithRetry(String username, String password)
    {
        return createTestUserWithRetry(username, password, CREATE_USER_MAX_ATTEMPTS);
    }

    private UserModel createTestUserWithRetry(String username, String password, int attemptsLeft)
    {
        try
        {
            return dataUser.usingAdmin().createUser(username, password);
        }
        catch (DataPreparationException e)
        {
            if (attemptsLeft <= 1)
            {
                DataPreparationException wrapped = new DataPreparationException(
                    "Failed to create test user " + username + " after " + CREATE_USER_MAX_ATTEMPTS + " attempts: " + e.getMessage());
                wrapped.initCause(e);
                throw wrapped;
            }
            log.warn("Failed to create test user {}, retrying ({} attempts left): {}", username, attemptsLeft - 1, e.getMessage());
            try
            {
                Thread.sleep(CREATE_USER_RETRY_WAIT_MILLIS);
            }
            catch (InterruptedException interruptedException)
            {
                Thread.currentThread().interrupt();
            }
            return createTestUserWithRetry(username, password, attemptsLeft - 1);
        }
    }

    // Retries user creation on transient "User ... not created" failures seen intermittently in CI under load.
    protected UserModel createTestUserWithRetry(String username)
    {
        return createTestUserWithRetry(username, CREATE_USER_MAX_ATTEMPTS);
    }

    private UserModel createTestUserWithRetry(String username, int attemptsLeft)
    {
        try
        {
            return dataUser.usingAdmin().createUser(username);
        }
        catch (DataPreparationException e)
        {
            if (attemptsLeft <= 1)
            {
                DataPreparationException wrapped = new DataPreparationException(
                    "Failed to create test user " + username + " after " + CREATE_USER_MAX_ATTEMPTS + " attempts: " + e.getMessage());
                wrapped.initCause(e);
                throw wrapped;
            }
            log.warn("Failed to create test user {}, retrying ({} attempts left): {}", username, attemptsLeft - 1, e.getMessage());
            try
            {
                Thread.sleep(CREATE_USER_RETRY_WAIT_MILLIS);
            }
            catch (InterruptedException interruptedException)
            {
                Thread.currentThread().interrupt();
            }
            return createTestUserWithRetry(username, attemptsLeft - 1);
        }
    }

    // Retries user creation on transient "User ... not created" failures seen intermittently in CI under load.
    protected UserModel createTestUserWithRetry(UserModel userModel)
    {
        return createTestUserWithRetry(userModel, CREATE_USER_MAX_ATTEMPTS);
    }

    private UserModel createTestUserWithRetry(UserModel userModel, int attemptsLeft)
    {
        try
        {
            return dataUser.usingAdmin().createUser(userModel);
        }
        catch (DataPreparationException e)
        {
            if (attemptsLeft <= 1)
            {
                DataPreparationException wrapped = new DataPreparationException(
                    "Failed to create test user " + userModel.getUsername() + " after " + CREATE_USER_MAX_ATTEMPTS + " attempts: " + e.getMessage());
                wrapped.initCause(e);
                throw wrapped;
            }
            log.warn("Failed to create test user {}, retrying ({} attempts left): {}", userModel.getUsername(), attemptsLeft - 1, e.getMessage());
            try
            {
                Thread.sleep(CREATE_USER_RETRY_WAIT_MILLIS);
            }
            catch (InterruptedException interruptedException)
            {
                Thread.currentThread().interrupt();
            }
            return createTestUserWithRetry(userModel, attemptsLeft - 1);
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
         // Re-derive a fresh baseURI/port from properties to avoid "Base URI cannot be null" if RestWrapper's serverURI went stale.
        restWrapper.configureAlfrescoEndpoint();

        UserModel user = restWrapper.getTestUser();
        if (null != user) {
            if (!this.aisAuthentication.isAisAuthenticationEnabled()) {
                String usernameColonPassword = user.getUsername() + ":" + user.getPassword();
                String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());
                restWrapper.configureRequestSpec().addHeader(AUTHORIZATION_HEADER,authorizationHeader);
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
