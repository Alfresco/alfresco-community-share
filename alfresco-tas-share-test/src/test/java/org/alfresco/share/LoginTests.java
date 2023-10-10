package org.alfresco.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.alfresco.po.share.LoginPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends BaseTest
{
    private final String password = "password";
    private String randomString = RandomData.getRandomAlphanumeric();
    private UserModel validUser;
    private final String[] specialUsers = {
            randomString + "isaías",
            randomString + "user.name",
            randomString + "test3&test3",
            randomString + "test5=test5" };

    private final List<UserModel> specialUserList = Collections.synchronizedList(new ArrayList<>());
    private final UserModel specialPassUser = new UserModel("specialPassUser" + randomString, "abc@123");
    private final UserModel testUserC2084 = new UserModel("testUserC2084" + randomString, password);

    private LoginPage loginPage;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        validUser = dataUser.usingAdmin().createRandomTestUser();
        dataUser.createUser(testUserC2084);
        dataUser.createUser(specialPassUser);
        Arrays.stream(specialUsers).map(specialUser ->
                dataUser.createUser(specialUser, password)).forEach(specialUserList::add);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeEach()
    {
        loginPage = new LoginPage(webDriver);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(validUser, testUserC2084, specialPassUser);
        specialUserList.forEach(this::deleteUsersIfNotNull);
    }

    @TestRail(id = "C2080")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void loginValidCredentials()
    {
        loginPage.navigate()
            .assertLoginPageIsOpened()
            .assertLoginPageTitleIsCorrect().login(validUser);
        
        userDashboardPage.assertUserDashboardPageIsOpened()
            .assertUserDashboardPageTitleIsCorrect()
            .assertPageHeaderIsCorrect(validUser);
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void loginInvalidCredentials()
    {
        loginPage.navigate().login("fakeUser", "fakePassword");
        loginPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2082")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void loginInvalidPassword()
    {
        loginPage.navigate().login(validUser.getUsername(), "fakePassword");
        loginPage
            .assertAuthenticationErrorIsDisplayed()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2083")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void invalidUserRedirectedToLoginPage()
    {
        userDashboardPage.navigateWithoutRender(validUser);
        loginPage.assertLoginPageIsOpened().login("user123", "wrongpass");
        loginPage.assertAuthenticationErrorIsDisplayed();
    }

    @TestRail(id = "C2084")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
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
        
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail(id = "C2085")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void loginUserWithSpecialChar()
    {
        specialUserList.forEach(specialUser -> {
            loginPage.navigate().login(specialUser);
            
            userDashboardPage.assertPageHeaderIsCorrect(specialUser);
            deleteAllCookiesIfNotNull();
        });
    }

    @TestRail(id = "C2086")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH, TestGroup.INTEGRATION })
    public void loginUserWithSpecialPassword()
    {
        loginPage.navigate();
        loginPage.login(specialPassUser);
        
        userDashboardPage.assertUserDashboardPageIsOpened();
    }
}