package org.alfresco.share.userProfile;

import org.alfresco.po.share.user.profile.UserContentPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserProfileContentTests extends ContextAwareWebTest
{
    @Autowired
    private UserContentPage userContentPage;

    private UserModel userNoContent;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userNoContent = dataUser.usingAdmin().createRandomTestUser();
    }


    @TestRail (id = "C2552")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void noAddedOrModifiedContent()
    {
        setupAuthenticatedSession(userNoContent);
        userContentPage.navigate(userNoContent)
            .assertRecentlyAddedLabelIsDisplayed()
            .assertRecentlyModifiedLabelIsDisplayed()
            .assertNoAddedContentMessageIsDisplayed()
            .assertNoModifiedContentMessageIsDisplayed()
            .assertNoAddedContentMessageIsCorrect()
            .assertNoModifiedContentMessageIsCorrect();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        removeUserFromAlfresco(userNoContent);
    }
}
