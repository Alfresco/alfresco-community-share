package org.alfresco.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests2 extends BaseTests {

    public static final String EXPECTED_ERROR_MESSAGE = "Your authentication details haven't been recognized or Alfresco Content Services may not be available at this time.";
    private UserModel userModel;

    private LoginPage2 loginPage2;
    private UserDashboardPage userDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        userModel = dataUser.usingAdmin().createRandomTestUser();
        super.beforeEachTest();

        loginPage2 = new LoginPage2(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
    }

    @TestRail(id = "C2080")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void shouldSuccessfullyLogin() {
        loginPage2.login(userModel.getUsername(), userModel.getPassword());

        assertTrue(userDashboardPage.isLoggedInSuccessfully(), "Login failed");
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void shouldDisplayErrorMessageWhenLoginWithWrongPassword() {
        loginPage2.login(userModel.getUsername(), "pass123");

        assertEquals(loginPage2.getErrorMessage(EXPECTED_ERROR_MESSAGE), EXPECTED_ERROR_MESSAGE, "Error message not equals");
    }

    @TestRail(id = "C2081")
    @Test(groups = { TestGroup.SANITY, TestGroup.AUTH })
    public void shouldDisplayErrorMessageWhenLoginWithWrongUser() {
        loginPage2.login("wrong user", userModel.getPassword());

        assertEquals(loginPage2.getErrorMessage(EXPECTED_ERROR_MESSAGE),
            EXPECTED_ERROR_MESSAGE, "Error message not equals");
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest()
    {
        super.afterEachTest();
    }
}
