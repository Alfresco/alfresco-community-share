package org.alfresco.share;

import static org.alfresco.common.Utils.saveScreenshot;
import static org.alfresco.common.Utils.screenshotFolder;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.common.WebBrowserConfig;
import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.joda.time.DateTime;
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

@ContextConfiguration(classes = ShareTestContext.class)
public abstract class BaseShareWebTests extends AbstractTestNGSpringContextTests
{
    protected final Logger LOG = LoggerFactory.getLogger(BaseShareWebTests.class);

    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;
    public static final GroupModel ALFRESCO_ADMIN_GROUP = new GroupModel("ALFRESCO_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SITE_ADMINISTRATORS = new GroupModel("SITE_ADMINISTRATORS");
    public static final GroupModel ALFRESCO_SEARCH_ADMINISTRATORS = new GroupModel("ALFRESCO_SEARCH_ADMINISTRATORS");
    public static String FILE_CONTENT = "Share file content";
    public final String password = "password";

    @Autowired
    public TasProperties tasProperties;

    @Autowired
    public EnvProperties properties;

    @Autowired
    private WebBrowserConfig browserConfig;

    @Autowired
    public DataSite dataSite;

    @Autowired
    protected DataUserAIS dataUser;

    @Autowired
    protected DataAIS dataAIS;

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
    public void beforeEachTest()
    {
        browser.set(browserConfig.getWebBrowser());

        loginPage = new LoginPage(browser);
        userDashboardPage = new UserDashboardPage(browser);
        toolbar = new Toolbar(browser);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest(final Method method, final ITestResult result)
    {
        LOG.info("***************************************************************************************************");
        DateTime now = new DateTime();
        LOG.info(String.format("*** %s ***  Ending test %s:%s %s (%s s.)", now.toString("HH:mm:ss"), method.getDeclaringClass().getSimpleName(), method.getName(),
                result.isSuccess() ? "SUCCESS" : "!!! FAILURE !!!", (result.getEndMillis() - result.getStartMillis()) / 1000));
        LOG.info("***************************************************************************************************");
        if(!result.isSuccess())
        {
            saveScreenshot(getBrowser(),method);
        }
        closeBrowser();
    }

    private void closeBrowser()
    {
        LOG.info("Close browser..");
        try {
            getBrowser().manage().deleteAllCookies();
            getBrowser().quit();
        } catch (NoSuchSessionException noSuchSessionException) {
            LOG.info("Browser is not closed: {}", noSuchSessionException.getMessage());
        } finally
        {
            LOG.info("Finally close browser..");
            getBrowser().quit();
        }
    }

    protected WebBrowser getBrowser()
    {
        return browser.get();
    }

    public void setupAuthenticatedSession(String userName, String password)
    {
        loginViaBrowser(userName, password);
    }

    public void setupAuthenticatedSession(UserModel userModel)
    {
        loginViaBrowser(userModel.getUsername(), userModel.getPassword());
    }

    private void loginViaBrowser(String userName, String password)
    {
        getBrowser().manage().deleteAllCookies();
        UserModel validUser = new UserModel(userName, password);
        getLoginPage().navigate().login(validUser);
        userDashboardPage.waitForSharePageToLoad();
    }

    public CommonLoginPage getLoginPage()
    {
        if (dataAIS.isEnabled())
        {
            return new AIMSPage(browser);
        }

        return loginPage;
    }

    protected void navigate(String pageUrl)
    {
        LOG.info(String.format("Navigate to: '%s'", pageUrl));
        try
        {
            getBrowser().navigate().to(properties.getShareUrl().toURI().resolve(pageUrl).toURL());
        }
        catch (URISyntaxException | MalformedURLException me)
        {
            throw new RuntimeException("Page url: " + pageUrl + " is invalid");
        }
    }

    public UserModel getAdminUser()
    {
        return dataUser.getAdminUser();
    }

    public void cleanupAuthenticatedSession()
    {
        getBrowser().cleanUpAuthenticatedSession();
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
