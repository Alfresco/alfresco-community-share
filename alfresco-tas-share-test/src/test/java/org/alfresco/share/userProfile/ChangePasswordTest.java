package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ChangePasswordTest extends ContextAwareWebTest
{
    private UserModel user, userInvalidChange;

    @Autowired
    private ChangePasswordPage changePasswordPage;

    @Autowired
    private UserDashboardPage userDashboardPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        userInvalidChange = dataUser.createRandomTestUser();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user, userInvalidChange);
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
        userDashboardPage.assertUserDashboardPageIsOpened();
    }

    @TestRail (id = "C2227, 2229")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void incorrectOldOrNewPasswordTests()
    {
        setupAuthenticatedSession(userInvalidChange);
        changePasswordPage.navigate(userInvalidChange)
            .changePasswordAndExpectError(userInvalidChange.getPassword() + "-invalid",
                userInvalidChange.getPassword() + "-1",
                userInvalidChange.getPassword() + "-1")
            .changePasswordAndExpectError(userInvalidChange.getPassword(),
                userInvalidChange.getPassword() + "-1",
                userInvalidChange.getPassword() + "2");
    }
}