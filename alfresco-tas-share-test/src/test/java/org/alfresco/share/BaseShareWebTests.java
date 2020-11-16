package org.alfresco.share;

import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.TasProperties;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.browser.WebBrowserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;

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
    private WebBrowserFactory browserFactory;

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

    protected ThreadLocal<WebBrowser> browser = new ThreadLocal<>();
    protected LoginPage loginPage;
    protected UserDashboardPage userDashboardPage;
    protected Toolbar toolbar;

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() throws Exception
    {
        browser.set(browserFactory.getWebBrowser());

        loginPage = new LoginPage(browser);
        userDashboardPage = new UserDashboardPage(browser);
        toolbar = new Toolbar(browser);
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest()
    {
        getBrowser().manage().deleteAllCookies();
        getBrowser().quit();
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
        cleanupAuthenticatedSession();
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
        dataUser.logout();
        getBrowser().cleanUpAuthenticatedSession();
    }

    public void removeUserFromAlfresco(UserModel... users)
    {
        Arrays.stream(users).forEach(user ->
        {
            dataUser.usingAdmin().deleteUser(user);
        });
    }

    public void deleteSites(SiteModel... sites)
    {
        Arrays.stream(sites).forEach(site -> dataSite.usingAdmin().deleteSite(site));
    }

    public void assertCurrentUrlContains(String value)
    {
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(value), String.format("%s is displayed in current url", value));
    }
}
