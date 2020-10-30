package org.alfresco.share;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class LoginTests extends ContextAwareWebTest
{
    private String randomString = RandomData.getRandomAlphanumeric();
    private String dashBoardUrl = "share/page/user/%s/dashboard";
    private UserModel validUser;
    private final String[] specialUsers = { randomString + "isaías",
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
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupSession()
    {
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2080")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginValidCredentials()
    {
        LOG.info("STEP1: Navigate to Login page");
        getLoginPage().navigate()
            .assertLoginPageTitleIsCorrect().login(validUser);
        userDashboard.assertUserDashboardPageIsOpened();
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidCredentials()
    {
        getLoginPage().navigate().login("fakeUser", "fakePassword");
        getLoginPage()
            .assertAuthenticationErrorMessageIsCorrect();
    }

    @TestRail(id = "C2082")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void loginInvalidPassword()
    {
        getLoginPage().navigate().login(validUser.getUsername(), "fakePassword");
        getLoginPage()
            .assertAuthenticationErrorMessageIsCorrect();
    }
}