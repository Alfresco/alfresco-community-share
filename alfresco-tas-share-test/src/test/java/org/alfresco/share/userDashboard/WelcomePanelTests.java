package org.alfresco.share.userDashboard;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class WelcomePanelTests extends BaseTest
{
    private ThreadLocal<UserModel> testUser = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        testUser.set(dataUser.usingAdmin().createRandomTestUser());
        setupAuthenticatedSession(testUser.get());
        userDashboardPage.navigate(testUser.get());
    }

    @TestRail (id = "C202855")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkWelcomePanel()
    {
        userDashboardPage.assertWelcomePanelIsDisplayed()
            .assertWelcomePanelMessageIsCorrect()
            .assertHideWelcomePanelButtonIsDisplayed()
            .assertAlfrescoDocumentationPageIsOpenedFromWelcomePanel();
    }

    @TestRail (id = "C202856, C202857")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void hideAndRestoreWelcomePanel()
    {
        userDashboardPage.clickHideWelcomePanel()
            .assertHideWelcomePanelDialogContentIsCorrect()
            .clickOK();
        userDashboardPage.assertWelcomePanelIsNotDisplayed()
            .clickCustomizeUserDashboard()
            .assertShowOnDashboardIsNotSelected()
            .assertHideFromDashboardIsSelected()
            .selectShowGetStartedPanelOnDashboard()
            .clickOk();
        userDashboardPage.assertWelcomePanelIsDisplayed()
            .clickCustomizeUserDashboard()
            .assertShowOnDashboardIsSelected()
            .selectHideStartedPanelFromDashboard()
            .clickOk();
        userDashboardPage.assertWelcomePanelIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser.get());
    }
}
