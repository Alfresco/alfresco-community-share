package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyProfileDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.EditUserProfilePage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MyProfileTests extends ContextAwareWebTest
{
    @Autowired
    MyProfileDashlet myProfileDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    EditUserProfilePage editUserProfilePage;

    @TestRail(id = "C2141")
    @Test
    public void myLimitedProfileDashlet()
    {
        String userName = "User1" + DataUtil.getUniqueIdentifier();
        String jobTitle = "jobTitle" + DataUtil.getUniqueIdentifier();
        String telephone = "0123456789" + DataUtil.getUniqueIdentifier();
        String skype = "skype" + DataUtil.getUniqueIdentifier();
        String im = "im" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, userName, userName);
        userService.addDashlet(userName, DataUtil.PASSWORD, UserDashlet.MY_PROFILE, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
        editUserProfilePage.navigate(userName);
        editUserProfilePage.setAboutInformation(userName, userName, jobTitle, "", "");
        editUserProfilePage.setContactInformation(telephone, "", userName + domain, skype, im, "");
        editUserProfilePage.uploadNewPhoto(testDataFolder + "newavatar.jpg");
        editUserProfilePage.clickSave();

        userDashboardPage.navigate(userName);
        browser.waitInSeconds(3);
        myProfileDashlet.renderedPage();
        
        Assert.assertEquals(myProfileDashlet.getDashletTitle(), "My Profile");

        LOG.info("STEP 1 - Verify \"My Profile\" dashlet");
       
        Assert.assertTrue(myProfileDashlet.isHelpIconDisplayed(DashletHelpIcon.MY_PROFILE));
        Assert.assertTrue(myProfileDashlet.isViewFullProfileDisplayed());
        Assert.assertTrue(myProfileDashlet.isAvatarDisplayed());
        Assert.assertTrue(myProfileDashlet.isNameEnabled());
        Assert.assertTrue(myProfileDashlet.isEmailEnabled());
        Assert.assertEquals(myProfileDashlet.getName(), userName + " " + userName);
        browser.waitInSeconds(2);
        Assert.assertEquals(myProfileDashlet.getJobTitle(), jobTitle);
        Assert.assertEquals(myProfileDashlet.getEmail(), userName + domain);
        Assert.assertEquals(myProfileDashlet.getTelephone(), telephone);
        Assert.assertEquals(myProfileDashlet.getSkype(), skype);
        Assert.assertEquals(myProfileDashlet.getIM(), im);

        LOG.info("STEP 2 - Click \"?\" icon");
        myProfileDashlet.clickOnHelpIcon(DashletHelpIcon.MY_PROFILE);
        Assert.assertTrue(myProfileDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        Assert.assertEquals(myProfileDashlet.getHelpBalloonMessage(), language.translate("myProfileDashlet.helpBalloonMessage"), "Help balloon text");

        LOG.info("STEP 3 - Click \"X\" icon");
        myProfileDashlet.closeHelpBalloon();
        Assert.assertFalse(myProfileDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");

        LOG.info("STEP 4 - Click \"View Full Profile\" link");
        myProfileDashlet.clickOnViewFullProfile();
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "About header is displayed");

        LOG.info("STEP 5 - Click \"Home\"");
        userDashboardPage.navigateByMenuBar();
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "Customize user dashboard is displayed");

        LOG.info("STEP 6 - Click user's name link");
        myProfileDashlet.clickOnName();
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "About header is displayed");

        LOG.info("STEP 7 - Click \"Home\"");
        userDashboardPage.navigateByMenuBar();
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "Customize user dashboard option is displayed");

        LOG.info("STEP 8 - Click \"Email\" link");
        Assert.assertEquals(myProfileDashlet.getEmailHREF(), "mailto:" + userName + domain);

        LOG.info("STEP 9 - Click \"Home\"");
        userDashboardPage.navigateByMenuBar();
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "Customize user dashboard option is displayed");
    }
}
