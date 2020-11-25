package org.alfresco.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends BaseTests
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

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
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
        getLoginPage().navigate()
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
        getLoginPage().navigate().login("fakeUser", "fakePassword");
        getLoginPage()
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2082")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidPassword()
    {
        getLoginPage().navigate().login(validUser.getUsername(), "fakePassword");
        getLoginPage()
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2083")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void invalidUserRedirectedTologinPage()
    {
        navigate(String.format(dashBoardUrl, validUser.getUsername()));
        getLoginPage().renderedPage();
        getLoginPage().assertLoginPageIsOpened().login("user123", "wrongpass");
        getLoginPage().assertAuthenticationErrorIsDisplayed();
    }

    @TestRail(id = "C2084")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginAutoComplete()
    {
        getLoginPage().navigate().autoCompleteUsername(testUserC2084.getUsername());
        getLoginPage().typePassword(password);
        getLoginPage().clickLogin();
        if (getLoginPage().isAuthenticationErrorDisplayed())
        {
            getLoginPage().autoCompleteUsername(testUserC2084.getUsername());
            getLoginPage().typePassword(password);
            getLoginPage().clickLogin();
        }
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail(id = "C2085")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialChar()
    {
        specialUserList.forEach(specialUser -> {
            getLoginPage().navigate().login(specialUser);
            userDashboardPage.renderedPage();
            userDashboardPage.assertPageHeaderIsCorrect(specialUser);
            cleanupAuthenticatedSession();
        });
    }

    @TestRail(id = "C2086")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginUserWithSpecialPassword()
    {
        getLoginPage().navigate();
        getLoginPage().login(specialPassUser);
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();
    }
}