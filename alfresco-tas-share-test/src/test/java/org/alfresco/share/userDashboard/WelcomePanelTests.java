package org.alfresco.share.userDashboard;

import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WelcomePanelTests extends ContextAwareWebTest
{
    private UserModel testUser;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        testUser = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(testUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(testUser);
    }

    @TestRail (id = "C202855")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkWelcomePanel()
    {
        userDashboard.assertWelcomePanelIsDisplayed()
            .assertWelcomePanelMessageIsCorrect()
            .assertHideWelcomePanelButtonIsDisplayed()
            .assertAlfrescoDocumentationPageIsOpenedFromWelcomePanel();
    }

    @TestRail (id = "C202856, C202857")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void hideAndRestoreWelcomePanel()
    {
        userDashboard.clickHideWelcomePanel()
            .assertHideWelcomePanelDialogContentIsCorrect()
            .clickOK();
        userDashboard.assertWelcomePanelIsNotDisplayed()
            .clickCustomizeUserDashboard()
            .assertShowOnDashboardIsNotSelected()
            .assertHideFromDashboardIsSelected()
            .selectShowGetStartedPanelOnDashboard()
            .clickOk();
        userDashboard.assertWelcomePanelIsDisplayed()
            .clickCustomizeUserDashboard()
            .assertShowOnDashboardIsSelected()
            .selectHideStartedPanelFromDashboard()
            .clickOk();
        userDashboard.assertWelcomePanelIsNotDisplayed();
    }
}
