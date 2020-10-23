package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyMeetingWorkspacesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyMeetingWorkspacesTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyMeetingWorkspacesDashlet myMeetingWorkspacesDashlet;

    private UserModel user;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.MY_MEETING_WORKSPACES, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2772")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void meetingWorkspacesDashlet()
    {
        myMeetingWorkspacesDashlet.assertDashletTitleEquals(language.translate("myMeetingWorkspacesDashlet.title"))
            .assertNoMeetingWorkspacesMessageIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.MY_MEETING_WORKSPACES)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("myMeetingWorkspacesDashlet.helpBalloonMessage"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }
}
