package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.constants.ShareGroups;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyMeetingWorkspacesDashlet;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class MyMeetingWorkspacesTests extends AbstractUserDashboardDashletsTests
{
    private MyMeetingWorkspacesDashlet myMeetingWorkspacesDashlet;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myMeetingWorkspacesDashlet = new MyMeetingWorkspacesDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.MY_MEETING_WORKSPACES, 1, 3);
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2772")
    @AlfrescoTest(jira = "XAT-8256")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD, ShareGroups.SHARE_PRIORITY_1})
    public void meetingWorkspacesDashlet()
    {
        userDashboardPage.navigate(user.get());
        myMeetingWorkspacesDashlet.assertDashletTitleEquals(language.translate("myMeetingWorkspacesDashlet.title"))
            .assertNoMeetingWorkspacesMessageIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.MY_MEETING_WORKSPACES)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myMeetingWorkspacesDashlet.helpBalloonMessage"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}
