package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.UserContentPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserProfileContentTests extends BaseTest
{
    private UserContentPage userContentPage;

    private UserModel userNoContent;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userNoContent = dataUser.usingAdmin().createRandomTestUser();

        userContentPage = new UserContentPage(webDriver);
    }

    @TestRail (id = "C2552")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void noAddedOrModifiedContent()
    {
        authenticateUsingCookies(userNoContent);
        userContentPage.navigate(userNoContent)
            .assertRecentlyAddedLabelIsDisplayed()
            .assertRecentlyModifiedLabelIsDisplayed()
            .assertNoAddedContentMessageIsDisplayed()
            .assertNoModifiedContentMessageIsDisplayed()
            .assertNoAddedContentMessageIsCorrect()
            .assertNoModifiedContentMessageIsCorrect();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown()
    {
        deleteUsersIfNotNull(userNoContent);
    }
}
