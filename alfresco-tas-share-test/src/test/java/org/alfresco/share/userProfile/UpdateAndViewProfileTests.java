package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.EditUserProfilePage;
import org.alfresco.po.share.user.profile.MyProfileNavigation;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class UpdateAndViewProfileTests extends ContextAwareWebTest
{
    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    MyProfileNavigation myProfileNavigation;

    @Autowired
    EditUserProfilePage editUserPage;

    private String user = String.format("profileUser%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, user, user + domain, "fName", "lName");
        setupAuthenticatedSession(user, user);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }

    @TestRail (id = "C2110")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void checkUserProfilePage()
    {
        LOG.info("Step 1 - Click on 'My Profile' link on header bar");
        userProfilePage.navigate(user);

        LOG.info("Step 2 - Check the panes displayed on the page");
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed());
        Assert.assertTrue(userProfilePage.isCompanyDetailsHeaderDisplayed());
        Assert.assertTrue(userProfilePage.isContactInfoHeaderDisplayed());

        LOG.info("Step 3 - Check the tabs displayed on the page");
        Assert.assertTrue(myProfileNavigation.isInfoDisplayed());
        Assert.assertTrue(myProfileNavigation.isSitesDisplayed());
        Assert.assertTrue(myProfileNavigation.isContentDisplayed());
        Assert.assertTrue(myProfileNavigation.isFollowingDisplayed());
        Assert.assertTrue(myProfileNavigation.isFollowingMeDisplayed());
        Assert.assertTrue(myProfileNavigation.isChangePasswordDisplayed());
        Assert.assertTrue(myProfileNavigation.isNotificationDisplayed());
        Assert.assertTrue(myProfileNavigation.isTrashcanDisplayed());
    }

    @TestRail (id = "C2190")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void checkEditUserProfilePage()
    {
        editUserPage.navigate(user);
        LOG.info("Step 1 - Verify 'About' pane");
        Assert.assertTrue(editUserPage.isFirstNameDisplayed());
        Assert.assertTrue(editUserPage.isLastNameDisplayed());
        Assert.assertTrue(editUserPage.isJobTitleDisplayed());
        Assert.assertTrue(editUserPage.isLocationDisplayed());
        Assert.assertTrue(editUserPage.isSummaryDisplayed());

        LOG.info("Step 2 - Verify 'Photo' pane");
        Assert.assertTrue(editUserPage.isPhotoDisplayed());

        LOG.info("Step 3 - Verify 'Contact Information' pane");
        Assert.assertTrue(editUserPage.isTelephoneDisplayed());
        Assert.assertTrue(editUserPage.isMobileDisplayed());
        Assert.assertTrue(editUserPage.isEmailDisplayed());
        Assert.assertTrue(editUserPage.isSkypeDisplayed());
        Assert.assertTrue(editUserPage.isIMDisplayed());
        Assert.assertTrue(editUserPage.isGoogleUserNameDisplayed());

        LOG.info("Step 4 - Verify 'Company Details' pane");
        Assert.assertTrue(editUserPage.isCompanyNameDisplayed());
        Assert.assertTrue(editUserPage.isCompanyAddress1Displayed());
        Assert.assertTrue(editUserPage.isCompanyAddress2Displayed());
        Assert.assertTrue(editUserPage.isCompanyAddress3Displayed());
        Assert.assertTrue(editUserPage.isCompanyPostCodeDisplayed());
        Assert.assertTrue(editUserPage.isCompanyTelephoneDisplayed());
        Assert.assertTrue(editUserPage.isCompanyFaxDisplayed());
        Assert.assertTrue(editUserPage.isCompanyEmailDisplayed());
    }

    @TestRail (id = "C2142")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateUserProfile()
    {
        String[] userDetails = { "John", "Snow", "Lord", "Winterfell", "Spoiler: I'm alive", "john.show@got.com", "0233", "0749", "john.show", "House Stark",
            "North", "8765", "8989", "0021", "stark@got.com" };

        LOG.info("Step 1 - Click 'Edit Profile'");
        editUserPage.navigate(user);

        LOG.info(String.format("Step 2 - Edit '%s' profile details, making changes and adding new information as desired", user));
        editUserPage.setAboutInformation("", "", "Manager", "UK London Office", "");

        LOG.info("Step 3 - Click 'Cancel' to discard editing");
        userProfilePage = editUserPage.clickCancel();
        Assert.assertTrue(userProfilePage.getAboutUserInfo().isEmpty());

        LOG.info("Step 4 - Click 'Edit Profile' again");
        userProfilePage.clickEditProfile();

        LOG.info(String.format("Step 5 - Edit '%s' profile details, making changes and adding new information as desired again.", user));
        editUserPage.setAboutInformation(userDetails[0], userDetails[1], userDetails[2], userDetails[3], userDetails[4]);
        editUserPage.setContactInformation(userDetails[6], userDetails[7], userDetails[5], userDetails[8], userDetails[8], userDetails[8]);
        editUserPage.setCompanyDetails(userDetails[9], userDetails[10], "", "", userDetails[11], userDetails[12], userDetails[13], userDetails[14]);

        LOG.info("Step 6 - Click 'Save Change' button");
        editUserPage.clickSave();
        List<String> userAboutInfo = userProfilePage.getAboutUserInfo();
        Assert.assertTrue(userAboutInfo.get(0).equals(userDetails[2]));
        Assert.assertTrue(userAboutInfo.get(1).equals(userDetails[9]));
        Assert.assertTrue(userAboutInfo.get(2).equals(userDetails[3]));

        Map<String, String> userInfo = userProfilePage.getUserInformation();
        int i = 5;
        for (Map.Entry<String, String> entry : userInfo.entrySet())
        {
            if (entry.getKey().equals("Skype") || entry.getKey().equals("IM") || entry.getKey().equals("Google Username"))
            {
                Assert.assertTrue(entry.getValue().equals(userDetails[8]));
                i = 9;
            } else if (entry.getKey().equals("Address"))
            {
                Assert.assertTrue(entry.getValue().equals(userDetails[10] + "\n" + userDetails[11]));
                i = 12;
            } else
            {
                Assert.assertTrue(entry.getValue().equals(userDetails[i]));
                i++;
            }
        }
    }

    @TestRail (id = "C2152")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void uploadNewPhoto()
    {
        editUserPage.navigate(user);
        String noPhoto = editUserPage.getPhotoSrc();
        editUserPage.clickUpload().clickClose();
        Assert.assertTrue(noPhoto.equals(editUserPage.getPhotoSrc()));
        editUserPage.uploadNewPhoto(testDataFolder + "newavatar.jpg");
        Assert.assertNotSame("Photo upload failed", noPhoto, editUserPage.getPhotoSrc());
    }
}
