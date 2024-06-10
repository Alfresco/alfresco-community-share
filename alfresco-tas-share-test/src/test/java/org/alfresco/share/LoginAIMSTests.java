package org.alfresco.share;

import org.alfresco.po.share.LoginAimsPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginAIMSTests extends BaseTest
{
    private LoginAimsPage loginAimsPage;
    private Toolbar toolbar;

    private final String password = "password";
    private String randomString = RandomData.getRandomAlphanumeric();
    private UserModel validUser;
    private final String[] specialUsers = {
        randomString + "isaías",
        randomString + "user.name",
        randomString + "test3+test3",
        randomString + "test5-test5" };
    private List<UserModel> specialUserList = new ArrayList<>();
    private final UserModel specialPassUser = new UserModel("specialPassUser" + randomString, "abc@123");

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        validUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.createUser(specialPassUser);
        Arrays.stream(specialUsers).map(specialUser ->
            dataUser.createUser(specialUser, password)).forEach(user -> specialUserList.add(user));
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        loginAimsPage = new LoginAimsPage(webDriver);
        toolbar = new Toolbar(webDriver);
    }

    @TestRail(id = "C2080")
    @Test(groups = TestGroup.SSO)
    public void loginValidCredentials()
    {
        loginAimsPage.navigate()
            .assertLoginPageIsOpened()
            .assertLoginPageTitleIsCorrect().login(validUser);
        userDashboardPage.assertUserDashboardPageIsOpened()
            .assertUserDashboardPageTitleIsCorrect()
            .assertPageHeaderIsCorrect(validUser);
    }

    @TestRail(id = "C2081")
    @Test(groups = TestGroup.SSO)
    public void loginInvalidCredentials()
    {
        loginAimsPage.navigate().login("fakeUser", "fakePassword");
        loginAimsPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2082")
    @Test(groups = TestGroup.SSO)
    public void loginInvalidPassword()
    {
        loginAimsPage.navigate().login(validUser.getUsername(), "fakePassword");
        loginAimsPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2083")
    @Test(groups = TestGroup.SSO)
    public void invalidUserRedirectedToLoginPage()
    {
        userDashboardPage.navigateWithoutRender(validUser);
        loginAimsPage.assertLoginPageIsOpened().login("user123", "wrongpass");
        loginAimsPage.assertAuthenticationErrorIsDisplayed();
    }

    @TestRail(id = "C2085")
    @Test(groups = TestGroup.SSO)
    public void loginUserWithSpecialChar()
    {
        specialUserList.forEach(specialUser -> {
            loginAimsPage.navigate().login(specialUser);
            userDashboardPage.assertPageHeaderIsCorrect(specialUser);
            toolbar.clickUserMenu().clickLogout();
        });
    }

    @TestRail(id = "C2086")
    @Test(groups = TestGroup.SSO)
    public void loginUserWithSpecialPassword()
    {
        loginAimsPage.navigate();
        loginAimsPage.login(specialPassUser);
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(validUser, specialPassUser);
        specialUserList.forEach(specialUser -> {
            deleteUsersIfNotNull(specialUser);
        });
    }
}
