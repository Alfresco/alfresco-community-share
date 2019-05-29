package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ChangePasswordTest extends ContextAwareWebTest
{
    @Autowired
    private UserProfilePage userProfilePage;

    @Autowired
    private ChangePasswordPage changePasswordPage;

    @Autowired
    private UserDashboardPage userDashboardPage;

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String user = "user" + uniqueIdentifier;
    private final String newPasswordText = "newpassword" + uniqueIdentifier;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C2226")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void changeUserPassword()
    {
        LOG.info("STEP1: Navigate to 'Change Password' page");
        changePasswordPage.navigate(user);
        assertEquals(userProfilePage.getPageTitle(), "Alfresco » Change User Password", "Displayed page=");

        LOG.info("STEP2: Fill in 'Old Password', 'New Password' and 'Confirm New Password' fields. \n" + "Click 'OK' button");
        changePasswordPage.typeOldPassword(password);
        changePasswordPage.typeNewPassword(newPasswordText);
        changePasswordPage.typeConfirmNewPassword(newPasswordText);
        changePasswordPage.clickOkButton();
        assertEquals(userProfilePage.getPageTitle(), "Alfresco » User Profile Page", "Displayed page=");

        LOG.info("STEP3: Login with the new password " + newPasswordText);
        setupAuthenticatedSession(user, newPasswordText);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
    }
}