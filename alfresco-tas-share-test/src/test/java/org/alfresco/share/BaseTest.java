package org.alfresco.share;

import static org.alfresco.common.Utils.saveScreenshot;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.common.BrowserFactory;
import org.alfresco.common.Language;
import org.alfresco.common.ShareTestContext;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.rest.core.RestWrapper;
import org.alfresco.utility.data.DataContent;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUserAIS;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.exception.DataPreparationException;
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

/**
 * This class represents a test template which should be inherit by each test class.
 * e.g. ToolbarTests extends BaseTest
 * Only test class which will not use this template should be LoginTests.
 *
 * This class should only contain common members/methods which are used in each test class
 */
@ContextConfiguration(classes = ShareTestContext.class)
public abstract class BaseTest extends AbstractTestNGSpringContextTests
{
    private final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    @Autowired
    private BrowserFactory browserFactory;

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
    protected UserService userService;

    protected ThreadLocal<WebBrowser> browser = new ThreadLocal<>();
    private ThreadLocal<CmisWrapper> cmisApi = new ThreadLocal<>();
    private ThreadLocal<RestWrapper> restApi = new ThreadLocal<>();

    protected LoginPage loginPage;
    protected UserDashboardPage userDashboardPage;
    protected Toolbar toolbar;

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest(Method method)
    {
        LOG.info("STARTED TEST: {}", method.getName());
        browser.set(browserFactory.createBrowser());

        cmisApi.set(applicationContext.getBean(CmisWrapper.class));
        restApi.set(applicationContext.getBean(RestWrapper.class));

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
            saveScreenshot(browser.get(), method);
        }
        closeBrowser();
    }

    private void closeBrowser()
    {
        try
        {
            LOG.info("Close browser..");
            browser.get().quit();
        }
        catch (NoSuchSessionException noSuchSessionException)
        {
            LOG.info("Browser is not closed: {}", noSuchSessionException.getMessage());
        }
        finally
        {
            if (browser.get() != null)
            {
                LOG.info("Finally close browser..");
                browser.get().quit();
            }
        }
    }

    public void setupAuthenticatedSession(UserModel user)
    {
        LOG.info("Setup authenticated session for user {}", user.getUsername());
        browser.get().manage().deleteAllCookies();
        loginPage.navigate();
        HttpState state = userService.login(user.getUsername(), user.getPassword());
        browser.get().manage().deleteAllCookies();
        Arrays.stream(state.getCookies()).forEach(cookie
            -> browser.get().manage().addCookie(new Cookie(cookie.getName(), cookie.getValue())));
    }

    public void setupAuthenticatedSessionViaLoginPage(UserModel userModel)
    {
        browser.get().manage().deleteAllCookies();
        getLoginPage().navigate().login(userModel);
        userDashboardPage.waitForSharePageToLoad();
    }

    private CommonLoginPage getLoginPage()
    {
        if (dataAIS.isEnabled())
        {
            return new AIMSPage(browser);
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

    public void removeUserFromAlfresco(UserModel... users)
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
                    LOG.error("Failed to delete user {}.", userModel.getUsername());
                }
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
