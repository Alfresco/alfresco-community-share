package org.alfresco.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Andreea Nechifor
 *
 */
public class AIMSLoginTest extends ContextAwareWebTest
{

    @Autowired
    private AIMSPage aimsPage;
    @Autowired
    private UserDashboardPage userDashboard;
    @Autowired
    private ToolbarUserMenu toolbarUserMenu;

    private String authError;
    private static String DASHBOARD_URL = "share/page/user/%s/dashboard";
    public static String AIMS_LOGIN_TITLE = "Sign in to alfresco";
    public static String DASHBOARD_TITLE = "Alfresco » User Dashboard";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {

        authError = language.translate("login.authError.AIMS");
    }

    @Test(groups = { "AIMS" })
    public void loginValidCredentials()
    {

        LOG.info("STEP1: Navigate to Login page");
        aimsPage.navigate();
        assertEquals(aimsPage.getPageTitle(), AIMS_LOGIN_TITLE, "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with valid credentials and click 'Login' button");
        aimsPage.loginSucced(adminUser, adminPassword);
        assertEquals(userDashboard.getPageTitle(), DASHBOARD_TITLE, "Displayed page=");

        LOG.info("STEP 3 - Click on the \"User menu\" -> \"Logout\" option");
        toolbarUserMenu.clickLogout();
        Assert.assertTrue(aimsPage.isCopyrightDisplayed(), "\"Copyright\" is displayed");

    }

    @Test(groups = { "AIMS" })
    public void loginInvalidCredentials()
    {
        LOG.info("STEP1: Navigate to Login page");
        aimsPage.navigate();
        assertEquals(aimsPage.getPageTitle(), AIMS_LOGIN_TITLE, "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with invalid username & password. Click 'Login' button");
        aimsPage.loginFailed("fakeUser", "fakePassword");
        assertEquals(aimsPage.getAuthenticationError(), authError, "Authentication error message=");

    }

    @Test(groups = { "AIMS" })
    public void loginInvalidPassword()
    {
        LOG.info("STEP1: Navigate to Login page");
        aimsPage.navigate();
        assertEquals(aimsPage.getPageTitle(), AIMS_LOGIN_TITLE, "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with valid username and invalid password. Click 'Login' button");
        aimsPage.loginFailed(adminUser, "fakePassword");
        assertEquals(aimsPage.getAuthenticationError(), authError, "Authentication error message=");

    }

    @Test(groups = { "AIMS" })
    public void dashboardUriRedirectedToLoginPage()
    {
        LOG.info("STEP1: In any browser, enter the URL for any page from Share in the address bar");
        navigate(String.format(DASHBOARD_URL, adminUser));
        assertEquals(aimsPage.getPageTitle(), AIMS_LOGIN_TITLE, "Displayed page= ");

        LOG.info("STEP2: Fill in username and password fields with valid credentials and click 'Login' button");
        aimsPage.loginSucced(adminUser, adminPassword);
        assertEquals(userDashboard.getPageTitle(), DASHBOARD_TITLE, "Displayed page=");

        LOG.info("STEP 3 - Click on the \"User menu\" -> \"Logout\" option");
        toolbarUserMenu.clickLogout();
        Assert.assertTrue(aimsPage.isCopyrightDisplayed(), "\"Copyright\" is displayed");

    }

    @Test(groups = { "AIMS" })
    public void loginAutoComplete()
    {
        aimsPage.navigate();
        assertEquals(aimsPage.getPageTitle(), AIMS_LOGIN_TITLE, "Displayed page=");

        aimsPage.autoCompleteUsername(adminUser);
        aimsPage.typePassword(adminPassword);
        aimsPage.clickLogin();
        assertEquals(aimsPage.getPageTitle(), DASHBOARD_TITLE, "Displayed page=");
        assertTrue(userDashboard.getCurrentUrl().contains(String.format(DASHBOARD_URL, adminUser)), adminPassword + " is logged in successfully.");

        LOG.info("STEP 3 - Click on the \"User menu\" -> \"Logout\" option");
        toolbarUserMenu.clickLogout();
        Assert.assertTrue(aimsPage.isCopyrightDisplayed(), "\"Copyright\" is displayed");

    }

}
