package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.EditUserProfilePage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class UpdateAndViewProfileTests extends BaseTests
{
    private UserProfilePage userProfilePage;
    private EditUserProfilePage editUserPage;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userProfilePage = new UserProfilePage(browser);
        editUserPage = new EditUserProfilePage(browser);

        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2110")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void checkUserProfilePage()
    {
        userProfilePage.navigate(user)
            .assertAboutHeaderIsDisplayed()
            .assertCompanyDetailsHeaderIsDisplayed()
            .assertContactInfoHeaderIsDisplayed()
                .assertInfoLinkIsDisplayed()
                .assertSitesLinkIsDisplayed()
                .assertContentLinkIsDisplayed()
                .assertImFollowingLinkIsDisplayed()
                .assertFollowingMeLinkIsDisplayed()
                .assertChangePasswordLinkIsDisplayed()
                .assertNotificationsLinkIsDisplayed()
                .assertTrashcanLinkIsDisplayed();
    }

    @TestRail (id = "C2142, C2190")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateUserProfile()
    {
        String[] userDetails = { "John", "Snow", "Lord", "Winterfell",
            "Spoiler: I'm alive", "john.show@got.com",
            "0233", "0749", "john.show", "House Stark",
            "North", "8765", "8989", "0021", "stark@got.com" };

        editUserPage.navigate(user)
            .setAboutInformation(userDetails[0], "", userDetails[2],  userDetails[3],  userDetails[4])
            .clickCancel()
            .assertUserInfoIsEmpty()
            .clickEditProfile();
        editUserPage.setAboutInformation(userDetails[0], userDetails[1], userDetails[2], userDetails[3], userDetails[4])
            .setContactInformation(userDetails[6], userDetails[7], userDetails[5], userDetails[8], userDetails[8], userDetails[8])
            .setCompanyDetails(userDetails[9], userDetails[10], "", "", userDetails[11],
                userDetails[12], userDetails[13], userDetails[14])
            .clickSave()
                .assertAboutUserHasValues(userDetails[2], userDetails[9], userDetails[3])
                .assertSummaryIs(userDetails[4])
                .assertContactInformationEmailIs(userDetails[5])
                .assertContactInformationTelephoneIs(userDetails[6])
                .assertContactInformationMobileIs(userDetails[7])
                .assertContactInformationSkypeIs(userDetails[8])
                .assertContactInformationIMIs(userDetails[8])
                .assertContactInformationGoogleUserNameIs(userDetails[8])
                .assertCompanyNameIs(userDetails[9])
                .assertCompanyAddressIs(userDetails[10] + "\n" + userDetails[11])
                .assertCompanyTelephoneIs(userDetails[12])
                .assertCompanyFaxIs(userDetails[13])
                .assertCompanyEmailIs(userDetails[14]);
    }

    @TestRail (id = "C2152, C2308")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewPhoto()
    {
        userProfilePage.navigate(user)
            .assertDefaultAvatarIsDisplayed()
            .clickEditProfile()
            .uploadNewPhoto(testDataFolder + "newavatar.jpg")
            .clickSave()
            .assertNewAvatarIsDisplayed()
            .clickEditProfile()
                .clickUseDefaultPhoto()
                .clickSave()
                .assertDefaultAvatarIsDisplayed();
    }
}
