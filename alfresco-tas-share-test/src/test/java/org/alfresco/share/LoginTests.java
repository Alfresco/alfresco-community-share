package org.alfresco.share;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.common.EnvProperties;
import org.alfresco.po.share.LoginPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends BaseTest
{
    private String randomString = RandomData.getRandomAlphanumeric();
    private String dashBoardUrl = "share/page/user/%s/dashboard";
    private UserModel validUser;
    private final String[] specialUsers = {
            randomString + "isaías",
            randomString + "user.name",
            randomString + "test3&test3",
            randomString + "test5=test5" };
    private List<UserModel> specialUserList = new ArrayList<>();
    private final UserModel specialPassUser = new UserModel("specialPassUser" + randomString, "abc@123");
    private UserModel testUserC2084 = new UserModel("testUserC2084" + randomString, password);

    private LoginPage loginPage;

    @Autowired
    private EnvProperties properties;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        loginPage = new LoginPage(browser);
        validUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.createUser(testUserC2084);
        dataUser.createUser(specialPassUser);
        Arrays.stream(specialUsers).map(specialUser ->
                dataUser.createUser(specialUser, password)).forEach(user -> specialUserList.add(user));
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(validUser, testUserC2084, specialPassUser);
        specialUserList.forEach(specialUser -> {
            removeUserFromAlfresco(specialUser);
        });
    }

    @TestRail(id = "C2080")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginValidCredentials()
    {
        loginPage.navigate()
            .assertLoginPageIsOpened()
            .assertLoginPageTitleIsCorrect().login(validUser);
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened()
            .assertUserDashboardPageTitleIsCorrect()
            .assertPageHeaderIsCorrect(validUser);
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidCredentials()
    {
        loginPage.navigate().login("fakeUser", "fakePassword");
        loginPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2082")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidPassword()
    {
        loginPage.navigate().login(validUser.getUsername(), "fakePassword");
        loginPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2083")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void invalidUserRedirectedTologinPage() throws URISyntaxException, MalformedURLException {
        getBrowser().navigate().to(properties.getShareUrl().toURI()
            .resolve(String.format(dashBoardUrl, validUser.getUsername())).toURL());
        loginPage.renderedPage();
        loginPage.assertLoginPageIsOpened().login("user123", "wrongpass");
        loginPage.assertAuthenticationErrorIsDisplayed();
    }

    @TestRail(id = "C2084")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
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
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail(id = "C2085")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialChar()
    {
        specialUserList.forEach(specialUser -> {
            loginPage.navigate().login(specialUser);
            userDashboardPage.renderedPage();
            userDashboardPage.assertPageHeaderIsCorrect(specialUser);
            getBrowser().manage().deleteAllCookies();
        });
    }

    @TestRail(id = "C2086")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialPassword()
    {
        loginPage.navigate();
        loginPage.login(specialPassUser);
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }
}