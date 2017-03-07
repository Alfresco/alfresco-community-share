package org.alfresco.share;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.alfresco.browser.WebBrowser;
import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.LogFactory;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.GroupService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * 
 * @author bogdan.bocancea
 *
 */
@ContextConfiguration("classpath:alfresco-share-po-context.xml")
public abstract class ContextAwareWebTest extends AbstractTestNGSpringContextTests
{
    protected Logger LOG = LogFactory.getLogger();
    
    @Autowired
    protected EnvProperties properties;

    @Autowired
    @Qualifier("webBrowserInstance")
    protected WebBrowser browser;

    @Autowired
    protected UserService userService;
    @Autowired
    protected SiteService siteService;
    @Autowired
    protected ContentService contentService;
    @Autowired
    protected DataListsService datalistService;
    @Autowired
    protected SitePagesService sitePagesService;
    @Autowired 
    protected ContentService content;
    @Autowired 
    protected ContentActions contentAction;
    @Autowired
    protected GroupService groupService;

    @Autowired
    protected Language language;
    
    protected String srcRoot = System.getProperty("user.dir") + File.separator;
    protected String testDataFolder = srcRoot + "testdata" + File.separator;

    protected String adminUser;
    protected String adminPassword;
    protected String adminName;
    protected String domain;
    protected String password;
    protected String mainWindow;

    @BeforeClass(alwaysRun = true)
    public void setup()
    {
        adminUser = properties.getAdminUser();
        adminPassword = properties.getAdminPassword();
        adminName = properties.getAdminName();
        domain = "@test.com";
        password = "password";
        cleanupAuthenticatedSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void startTest(final Method method) throws Exception {
        LOG.info("***************************************************************************************************");
        DateTime now = new DateTime();
        LOG.info("*** {} ***  Starting test {}:{} ", now.toString("HH:mm:ss"), method.getDeclaringClass().getSimpleName(), method.getName());
        LOG.info("***************************************************************************************************");
    }

    @AfterMethod(alwaysRun = true)
    public void endTest(final Method method, final ITestResult result) throws Exception {
        LOG.info("***************************************************************************************************");
        DateTime now = new DateTime();
        LOG.info("*** {} ***   Ending test {}:{} {} ({} s.)", now.toString("HH:mm:ss"), method.getDeclaringClass().getSimpleName(), method.getName(),
                result.isSuccess() ? "SUCCESS" : "!!! FAILURE !!!",
                (result.getEndMillis() - result.getStartMillis()) / 1000);
        LOG.info("***************************************************************************************************");
    }

    /**
     * Just authenticate using <username> and <password> provided as parameters
     * And inject the cookies in current browser
     * use this method in a @BeforeClass to pass the login screen
     * 
     * @param userName
     * @param password
     */
    protected void setupAuthenticatedSession(String userName, String password)
    {
        browser.authenticatedSession(userService.login(userName, password));
    }

    /**
     * Cleanup authenticated session with all cookies and logout the user
     */
    protected void cleanupAuthenticatedSession()
    {
        userService.logout();
        browser.cleanUpAuthenticatedSession();
    }
    
    /**
     * Navigate to specific page 
     * @param pageUrl e.g. 'share/page/user/admin/profile'
     */
    protected void navigate(String pageUrl)
    {
        try
        {
            browser.navigate().to(properties.getShareUrl().toURI().resolve(pageUrl).toURL());
        }
        catch (URISyntaxException | MalformedURLException me)
        {
            throw new RuntimeException("Page url: " + pageUrl + " is invalid");
        }
    }
    
    /**
     * Switches to the newly created window.
     */
    protected void switchWindow()
    {
        browser.switchWindow();
    }
    
    /**
     * Closes the newly created win and swithes back to main
     */
    protected void closeWindowAndSwitchBack()
    {
        browser.closeWindowAndSwitchBack();
    }
    
    public WebBrowser getBrowser() {
        return browser;
    }
}