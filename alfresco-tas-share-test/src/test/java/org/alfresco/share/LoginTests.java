package org.alfresco.share;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends ContextAwareWebTest
{
    @Autowired
    private LoginPage loginPage;

    @Autowired
    private UserDashboardPage userDashboard;

    private final String validUser = "validUser" + System.currentTimeMillis();
    private final String[] specialUsers = { "isaías", "user.name", "test3&test3", "test5=test5" };
    private final String specialPassUser = "specialPassUser";
    private final String specialPassword = "abc@123";
    private String dashBoardUrl, authError;
    private String testUserC2084 = "testUserC2084";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        dashBoardUrl = "share/page/user/%s/dashboard";
        authError = language.translate("login.authError");
        userService.create(adminUser, adminPassword, validUser, password, validUser + domain, "Valid", "User");
        userService.create(adminUser, adminPassword, testUserC2084, password, testUserC2084 + domain, "testUserC2084", "testUserC2084");

        userService.create(adminUser, adminPassword, specialPassUser, specialPassword, specialPassUser + domain, specialPassUser, "lname");
        for (String specialUser : specialUsers)
        {
            userService.create(adminUser, adminPassword, specialUser, password, specialUser + domain, specialUser, "lname");
        }
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2080")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginValidCredentials()
    {
        LOG.info("STEP1: Navigate to Login page");
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with valid credentials and click 'Login' button");
        loginPage.login(validUser, password);
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(userDashboard.customizeUserDashboard);
        assertEquals(userDashboard.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginInvalidCredentials()
    {
        LOG.info("STEP1: Navigate to Login page");
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with invalid username & password. Click 'Login' button");
        loginPage.login("fakeUser", "fakePassword");
        assertEquals(loginPage.getAuthenticationError(), authError, "Authentication error message=");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2082")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginInvalidPassword()
    {
        LOG.info("STEP1: Navigate to Login page");
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        LOG.info("STEP2: Fill in username and password fields with valid username and invalid password. Click 'Login' button");
        loginPage.login(validUser, "fakePassword");
        assertEquals(loginPage.getAuthenticationError(), authError, "Authentication error message=");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2083")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void invalidUserRedirectedToLoginPage()
    {
        LOG.info("STEP1: In any browser, enter the URL for any page from Share in the address bar");
        navigate(String.format(dashBoardUrl, validUser));
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page= ");

        LOG.info("STEP2: Enter invalid user name (e.g.: user123) or password (e.g.: wrongpass)");
        loginPage.login("user123", "wrongpass");
        assertEquals(loginPage.getAuthenticationError(), authError, "Authentication error message=");

        LOG.info("STEP3: Login as " + validUser);
        setupAuthenticatedSession(validUser, password);
        userDashboard.navigate(validUser);
        assertTrue(userDashboard.getCurrentUrl().contains(String.format(dashBoardUrl, validUser)), "Url contains: " + String.format(dashBoardUrl, validUser));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2084")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginAutoComplete()
    {
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        loginPage.autoCompleteUsername(testUserC2084);
        loginPage.typePassword(password);
        loginPage.clickLogin();
        if (loginPage.isAuthenticationErrorDisplayed())
        {
            loginPage.autoCompleteUsername(testUserC2084);
            System.out.println("Password: "+password);
            loginPage.typePassword(password);
            loginPage.clickLogin();
        }
        userDashboard.renderedPage();

        assertEquals(loginPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
        assertTrue(userDashboard.getCurrentUrl().contains(String.format(dashBoardUrl, testUserC2084)), testUserC2084 + " is logged in successfully.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2085")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginUserWithSpecialChar()
    {
        for (String specialUser : specialUsers)
        {
            LOG.info("STEP1: Navigate to Share Login page.");
            loginPage.navigate();
            assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

            LOG.info("STEP2: Fill in username and password fields with valid credentials and click 'Login' button");
            loginPage.login(specialUser, password);
            userDashboard.renderedPage();
            assertEquals(userDashboard.getPageHeader(), specialUser + " lname Dashboard", specialUser + "'s dashboard is displayed.");

            cleanupAuthenticatedSession();
        }
    }

    @TestRail(id = "C2086")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH})
    public void loginUserWithSpecialPassword()
    {
        LOG.info("STEP1: Navigate to Share Login page.");
        loginPage.navigate();
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page=");

        LOG.info(
                "STEP2: Fill in username and password fields with credentials for the user having password with \"@\" character.\n" + "Click \"Login\" button");
        loginPage.login(specialPassUser, specialPassword);
        userDashboard.renderedPage();
        assertEquals(userDashboard.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
        assertEquals(userDashboard.getPageHeader(), specialPassUser + " lname Dashboard", specialPassUser + "'s dashboard is displayed.");

        cleanupAuthenticatedSession();
    }
}