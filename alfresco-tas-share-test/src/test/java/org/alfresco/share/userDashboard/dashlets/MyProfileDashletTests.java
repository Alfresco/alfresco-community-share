package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyProfileDashlet;
import org.alfresco.po.share.user.profile.EditUserProfilePage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.alfresco.common.Utils.testDataFolder;

public class MyProfileDashletTests extends AbstractUserDashboardDashletsTests
{
    private MyProfileDashlet myProfileDashlet;
    private EditUserProfilePage editUserProfilePage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myProfileDashlet = new MyProfileDashlet(webDriver);
        editUserProfilePage = new EditUserProfilePage(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.MY_PROFILE, 1, 3);
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2141")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkMyProfileDashlet()
    {
        String jobTitle = String.format("jobTitle%s", RandomData.getRandomAlphanumeric());
        String telephone = String.format("0123456789");
        String skype = String.format("skype%s", RandomData.getRandomAlphanumeric());
        String im = String.format("im%s", RandomData.getRandomAlphanumeric());

        editUserProfilePage.navigate(user.get())
            .setAboutInformation(user.get().getFirstName(), user.get().getLastName(), jobTitle, "", "")
            .setContactInformation(telephone, "", user.get().getEmailAddress(), skype, im, "")
            .uploadNewPhoto(testDataFolder + "newavatar.jpg")
            .clickSave();
        userDashboardPage.navigate(user.get());
        myProfileDashlet.assertDashletTitleEquals(language.translate("myProfileDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_PROFILE)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myProfileDashlet.helpBalloonMessage"))
            .closeHelpBalloon()
            .assertViewFullProfileButtonIsDisplayed()
            .assertAvatarIsDisplayed()
            .assertNameIsEnabled()
            .assertEmailIsEnabled()
            .assertNameIs(user.get().getFirstName() + " " + user.get().getLastName())
            .assertJobTitleIs(jobTitle)
            .assertEmailIs(user.get().getEmailAddress())
            .assertTelephoneIs(telephone)
            .assertSkypeIs(skype)
            .assertInstantMessagesEqualTo(im)
            .assertEmailHrefIsCorrect(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
