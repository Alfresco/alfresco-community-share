package org.alfresco.share;

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
import org.alfresco.utility.Utility;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.assertTrue;

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
    private File screenshotFolder = new File("./target/reports/screenshots");

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
            LOG.info("Creating screenshots folder");
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
            saveScreenshot(method);
        }

        getBrowser().manage().deleteAllCookies();
        getBrowser().quit();
    }

    private void saveScreenshot(Method testMethod)
    {
        File screen = ((TakesScreenshot)getBrowser()).getScreenshotAs(OutputType.FILE);
        try
        {
            if(testMethod != null)
            {
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd.hhmmss");
                LOG.info(String.format("Generating screenshot for test: %s",
                    testMethod.getDeclaringClass().getSimpleName() + "#" + testMethod.getName()));
                File destination = new File(String.format("%s%s%s_%s.png", screenshotFolder.getAbsolutePath(),
                    File.separator, testMethod.getDeclaringClass().getSimpleName() + "#" + testMethod.getName(), ft.format(dNow)));
                FileUtils.copyFile(screen, destination);
            }
        }
        catch (IOException e)
        {
            LOG.error(String.format("Failed to copy screenshot %s", screen.getAbsolutePath()));
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
        Arrays.stream(users).forEach(user -> dataUser.usingAdmin().deleteUser(user));
    }

    public void deleteSites(SiteModel... sites)
    {
        Arrays.stream(sites).forEach(site -> dataSite.usingAdmin().deleteSite(site));
    }

    public void assertCurrentUrlContains(String value)
    {
        assertTrue(getBrowser().getCurrentUrl().contains(value), String.format("%s is displayed in current url", value));
    }

    public boolean isFileInDirectory(String fileName, String extension)
    {
        int retry = 0;
        int seconds = 10;
        if (extension != null)
        {
            fileName = fileName + extension;
        }
        File filePath = new File(testDataFolder + File.separator + fileName);
        filePath.deleteOnExit();
        while (retry <= seconds && !filePath.exists())
        {
            retry++;
            Utility.waitToLoopTime(1, String.format("Wait for '%s' to get downloaded", fileName));
        }
        return filePath.exists();
    }

    public String getDocumentLibraryPath(SiteModel site)
    {
        return Utility.buildPath(String.format("/Sites/%s/documentLibrary", site.getId()));
    }
}
