package org.alfresco.share.userProfile;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.user.profile.UserContentPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserProfileContentTests extends ContextAwareWebTest
{
    @Autowired
    UserContentPage userContentPage;

    private String userName1;
    private String RecentlyAddedLabelText;
    private String NoAddedContentMessageText;
    private String RecentlyModfiedLabelText;
    private String NoModifiedContentMessage;

    @BeforeClass
    public void setup()
    {
        super.setup();
        userName1 = "User1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName1, userName1, userName1 + domain, "fName1", "lName1");
        setupAuthenticatedSession(userName1, userName1);

        RecentlyAddedLabelText = language.translate("userProfileContent.recentlyAddedLabel");
        NoAddedContentMessageText = language.translate("userProfileContent.noAddedContentMessage");
        RecentlyModfiedLabelText = language.translate("userProfileContent.recentlyModifiedLabel");
        NoModifiedContentMessage = language.translate("userProfileContent.noModifiedContentMessage");
    }

    @TestRail(id = "C2552")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void noAddedOrModifiedContent()
    {
        LOG.info("STEP1: Go to 'My Profile --> 'Content' page and verify the content");

        userContentPage.navigate(userName1);
        Assert.assertTrue(userContentPage.isRecentlyAddedLabelDisplayed());
        Assert.assertTrue(userContentPage.isNoAddedContentMessageDisplayed());
        Assert.assertTrue(userContentPage.isRecentlyModfiedLabelDisplayed());
        Assert.assertTrue(userContentPage.isNoModifiedContentMessageDisplayed());
        Assert.assertEquals(userContentPage.getRecentlyAddedLabelText(), RecentlyAddedLabelText);
        Assert.assertEquals(userContentPage.getNoAddedContentMessageText(), NoAddedContentMessageText);
        Assert.assertEquals(userContentPage.getRecentlyModfiedLabelText(), RecentlyModfiedLabelText);
        Assert.assertEquals(userContentPage.getNoModifiedContentMessage(), NoModifiedContentMessage);
    }

    @AfterClass
    public void tearDown()
    {
        userService.delete(adminUser, adminPassword, userName1);
    }
}
