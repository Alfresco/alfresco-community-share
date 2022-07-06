package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChangePasswordTest extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private ChangePasswordPage changePasswordPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(dataUser.usingAdmin().createRandomTestUser());
        changePasswordPage = new ChangePasswordPage(webDriver);
    }

    @TestRail (id = "C2226")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER , TestGroup.INTEGRATION})
    public void changeUserPassword()
    {
        authenticateUsingCookies(user.get());
        String newPassword = user.get().getPassword() + "--new";
        changePasswordPage.navigate(user.get())
            .assertChangePasswordPageIsOpened()
            .assertBrowserPageTitleIs(language.translate("changeUserPassword.browser.pageTitle"))
            .changePassword("password", newPassword)
            .assertUserProfilePageIsOpened();
        user.get().setPassword(newPassword);
        authenticateUsingCookies(user.get());
        userDashboardPage.navigate(user.get()).assertUserDashboardPageIsOpened();
    }

    @TestRail (id = "C2227, 2229")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, TestGroup.INTEGRATION })
    public void incorrectOldOrNewPasswordTests()
    {
        authenticateUsingCookies(user.get());
        changePasswordPage.navigate(user.get())
            .changePasswordAndExpectError(user.get().getPassword() + "-invalid",
                    user.get().getPassword() + "-1",
                    user.get().getPassword() + "-1")
            .changePasswordAndExpectError(user.get().getPassword(),
                    user.get().getPassword() + "-1",
                    user.get().getPassword() + "2");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}