package org.alfresco.share;

import static org.alfresco.common.Utils.saveScreenshot;
import static org.alfresco.common.Utils.screenshotFolder;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.BrowserFactory;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.httpclient.HttpState;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * This class represents a test template which should be inherit by each test class.
 * e.g. ToolbarTests extends BaseTest
 * Only test class which will not use this template should be LoginTests.
 */
@ContextConfiguration(classes = ShareTestContext.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests
{
    private final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    public static final GroupModel ALFRESCO_ADMIN_GROUP = new GroupModel("ALFRESCO_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SITE_ADMINISTRATORS = new GroupModel("SITE_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SEARCH_ADMINISTRATORS = new GroupModel("ALFRESCO_SEARCH_ADMINISTRATORS");
    public static String FILE_CONTENT = "Share file content";
    public final String password = "password";

    @Autowired
    private BrowserFactory browserFactory;

    @Autowired
    public DataSite dataSite;

    @Autowired
    protected DataUserAIS dataUser;

    @Autowired
    protected DataGroup dataGroup;

    @Autowired
    protected Language language;

    @Autowired
    protected CmisWrapper cmisApi;

    @Autowired
    protected RestWrapper restApi;

    @Autowired
    protected DataContent dataContent;

    @Autowired
    private UserService userService;

    protected ThreadLocal<WebBrowser> browser = new ThreadLocal<>();
    protected LoginPage loginPage;
    protected UserDashboardPage userDashboardPage;
    protected Toolbar toolbar;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite()
    {
        if(!screenshotFolder.exists())
        {
            LOG.info("Creating screenshot folder");
            screenshotFolder.mkdir();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest(Method method)
    {
        LOG.info("STARTED TEST: {}", method.getName());
        browser.set(browserFactory.createBrowser());

        loginPage = new LoginPage(browser);
        userDashboardPage = new UserDashboardPage(browser);
        toolbar = new Toolbar(browser);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(Method method, ITestResult result)
    {
        LOG.info("FINISHED TEST: {}, {}, {}", method.getDeclaringClass().getSimpleName(), method.getName(),
            result.isSuccess() ? "PASSED" : "FAILED");
        if(!result.isSuccess())
        {
            saveScreenshot(getBrowser(),method);
        }
        closeBrowser();
    }

    private void closeBrowser()
    {
        try
        {
            LOG.info("Close browser..");
            getBrowser().quit();
        }
        catch (NoSuchSessionException noSuchSessionException)
        {
            LOG.info("Browser is not closed: {}", noSuchSessionException.getMessage());
        }
        finally
        {
            if (getBrowser() != null)
            {
                LOG.info("Finally close browser..");
                getBrowser().quit();
            }
        }
    }

    protected WebBrowser getBrowser()
    {
        return browser.get();
    }

    public void setupAuthenticatedSession(UserModel user)
    {
        LOG.info("Setup authenticated session for user {}", user.getUsername());
        getBrowser().manage().deleteAllCookies();
        loginPage.navigate();
        HttpState state = userService.login(user.getUsername(), user.getPassword());
        getBrowser().manage().deleteAllCookies();
        Arrays.stream(state.getCookies()).forEach(cookie
            -> getBrowser().manage().addCookie(new Cookie(cookie.getName(), cookie.getValue())));
    }

    public UserModel getAdminUser()
    {
        return dataUser.getAdminUser();
    }

    public void removeUserFromAlfresco(UserModel... users)
    {
        for (UserModel userModel : users)
        {
            if (userModel != null)
            {
                dataUser.usingAdmin().deleteUser(userModel);
            }
        }
    }

    public void deleteSites(SiteModel... sites)
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
