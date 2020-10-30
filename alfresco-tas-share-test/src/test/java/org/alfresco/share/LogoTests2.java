package org.alfresco.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LogoTests2 extends BaseTests {

    private UserModel userModel;

    private LoginPage2 loginPage2;
    private UserDashboardPage userDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void beforeEachTest() {
        userModel = dataUser.createRandomTestUser();
        super.beforeEachTest();

        loginPage2 = new LoginPage2(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        loginPage2.login(userModel.getUsername(), userModel.getPassword());
    }

    @Test
    public void shouldDisplayLogoDetails() {
        userDashboardPage.openAboutPage();

        assertEquals(userDashboardPage.getVersion().substring(0, 14), "Alfresco Share");
        assertTrue(userDashboardPage.getLicenseHolder(), "Is empty");
    }

    @AfterMethod(alwaysRun = true)
    public void afterEachTest() {
        super.afterEachTest();
    }

//    @AfterClass(alwaysRun = true)
//    public void afterEachClass() {
//        super.afterEachClass();
//    }
}
