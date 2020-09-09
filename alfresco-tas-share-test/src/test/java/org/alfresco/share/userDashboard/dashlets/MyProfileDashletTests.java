package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyProfileDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.EditUserProfilePage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyProfileDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyProfileDashlet myProfileDashlet;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    private EditUserProfilePage editUserProfilePage;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.MY_PROFILE, 1);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2141")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkMyProfileDashlet()
    {
        String jobTitle = String.format("jobTitle%s", RandomData.getRandomAlphanumeric());
        String telephone = String.format("0123456789");
        String skype = String.format("skype%s", RandomData.getRandomAlphanumeric());
        String im = String.format("im%s", RandomData.getRandomAlphanumeric());
        editUserProfilePage.navigate(user)
            .setAboutInformation(user.getFirstName(), user.getLastName(), jobTitle, "", "")
            .setContactInformation(telephone, "", user.getEmailAddress(), skype, im, "")
            .uploadNewPhoto(testDataFolder + "newavatar.jpg")
            .clickSave();
        userDashboard.navigate(user);
        myProfileDashlet.assertDashletTitleIs(language.translate("myProfileDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_PROFILE)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("myProfileDashlet.helpBalloonMessage"))
            .closeHelpBalloon()
            .assertViewFullProfileButtonIsDisplayed()
            .assertAvatarIsDisplayed()
            .assertNameIsEnabled()
            .assertEmailIsEnabled()
            .assertNameIs(user.getFirstName() + " " + user.getLastName())
            .assertJobTitleIs(jobTitle)
            .assertEmailIs(user.getEmailAddress())
            .assertTelephoneIs(telephone)
            .assertSkypeIs(skype)
            .assertIMIs(im)
            .assertEmailHrefIsCorrect(user)
                .clickViewFullProfile().assertUserProfilePageIsOpened();
        userDashboard.navigate(user);
        myProfileDashlet.clickOnName().assertUserProfilePageIsOpened();
    }
}
