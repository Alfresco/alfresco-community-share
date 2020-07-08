package org.alfresco.share;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends ContextAwareWebTest
{
    @Autowired
    private LoginPage loginPage;

    @Autowired
    private UserDashboardPage userDashboard;

    private String dashBoardUrl = "share/page/user/%s/dashboard";
    private UserModel validUser;
    private final String[] specialUsers = { "isaías", "user.name", "test3&test3", "test5=test5" };
    private List<UserModel> specialUserList = new ArrayList<>();
    private final UserModel specialPassUser = new UserModel("specialPassUser", "abc@123");
    private UserModel testUserC2084 = new UserModel("testUserC2084", password);

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        validUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.createUser(testUserC2084);
        dataUser.createUser(specialPassUser);
        Arrays.stream(specialUsers).map(specialUser
            -> dataUser.createUser(specialUser, password)).forEach(user -> specialUserList.add(user));
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(validUser, testUserC2084, specialPassUser);
        specialUserList.forEach(specialUser -> {
            removeUserFromAlfresco(specialUser);
        });
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupSession()
    {
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2080")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginValidCredentials()
    {
        LOG.info("STEP1: Navigate to Login page");
        loginPage.navigate().assertPageIsOpened().assertLoginPageTitleIsCorrect()
            .login(validUser);
        userDashboard.renderedPage();
        userDashboard.assertPageIsOpened().assertUserDashboardPageTitleIsCorrect()
            .assertPageHeaderIsCorrect(validUser);
    }

    @TestRail (id = "C2081")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidCredentials()
    {
        loginPage.navigate().login("fakeUser", "fakePassword");
        loginPage.assertAuthenticationErrorIsDisplayed().assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail (id = "C2082")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidPassword()
    {
        loginPage.navigate().login(validUser.getUsername(), "fakePassword");
        loginPage.assertAuthenticationErrorIsDisplayed().assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail (id = "C2083")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void invalidUserRedirectedToLoginPage()
    {
        LOG.info("STEP1: In any browser, enter the URL for any page from Share in the address bar");
        navigate(String.format(dashBoardUrl, validUser.getUsername()));
        loginPage.renderedPage();
        loginPage.assertPageIsOpened().login("user123", "wrongpass");
        loginPage.assertAuthenticationErrorIsDisplayed();
    }

    @TestRail (id = "C2084")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginAutoComplete()
    {
        loginPage.navigate().autoCompleteUsername(testUserC2084.getUsername());
        loginPage.typePassword(password);
        loginPage.clickLogin();
        if (loginPage.isAuthenticationErrorDisplayed())
        {
            loginPage.autoCompleteUsername(testUserC2084.getUsername());
            loginPage.typePassword(password);
            loginPage.clickLogin();
        }
        userDashboard.renderedPage();
        userDashboard.assertPageIsOpened();
    }

    @TestRail (id = "C2085")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialChar()
    {
        specialUserList.forEach(specialUser -> {
            loginPage.navigate().login(specialUser);
            userDashboard.renderedPage();
            userDashboard.assertPageHeaderIsCorrect(specialUser);
            cleanupSession();
        });
    }

    @TestRail (id = "C2086")
    @Test (groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialPassword()
    {
        loginPage.navigate();
        loginPage.login(specialPassUser);
        userDashboard.renderedPage();
        userDashboard.assertPageIsOpened();
    }
}