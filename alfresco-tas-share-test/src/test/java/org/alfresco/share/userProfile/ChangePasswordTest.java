package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class ChangePasswordTest extends BaseTest
{
    private final String password = "password";
    private UserModel user, changeUser;
    private ChangePasswordPage changePasswordPage;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        changeUser = dataUser.usingAdmin().createRandomTestUser();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        changePasswordPage = new ChangePasswordPage(browser);
    }

    @TestRail (id = "C2226")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void changeUserPassword()
    {
        setupAuthenticatedSession(user);
        String newPassword = user.getPassword() + "--new";
        changePasswordPage.navigate(user)
            .assertChangePasswordPageIsOpened()
            .assertBrowserPageTitleIs(language.translate("changeUserPassword.browser.pageTitle"))
            .changePassword(password, newPassword)
            .assertUserProfilePageIsOpened();
        user.setPassword(newPassword);
        setupAuthenticatedSession(user);
        userDashboardPage.navigate(user).assertUserDashboardPageIsOpened();
    }

    @TestRail (id = "C2227, 2229")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void incorrectOldOrNewPasswordTests()
    {
        setupAuthenticatedSession(changeUser);
        changePasswordPage.navigate(user)
            .changePasswordAndExpectError(user.getPassword() + "-invalid",
                    user.getPassword() + "-1",
                    user.getPassword() + "-1")
            .changePasswordAndExpectError(user.getPassword(),
                    user.getPassword() + "-1",
                    user.getPassword() + "2");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user, changeUser);
    }
}