package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyMeetingWorkspacesDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyMeetingWorkspacesTests extends ContextAwareWebTest
{
    
    @Autowired
    UserDashboardPage userDashboardPage;
    
    @Autowired
    MyMeetingWorkspacesDashlet myMeetingWorkspacesDashlet;
    
    private String userName;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.addDashlet(userName, password, DashboardCustomization.UserDashlet.MY_MEETING_WORKSPACES, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C2772")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void meetingWorkspacesDashlet()
    {  
        userDashboardPage.navigate(userName);
        
        LOG.info("Step 1: Verify 'My Meeting Workspaces' dahslet");
        Assert.assertEquals(myMeetingWorkspacesDashlet.getDashletTitle(), "My Meeting Workspaces");
        Assert.assertEquals(myMeetingWorkspacesDashlet.getDefaultMessage(), "No meeting workspaces to display");
        
        LOG.info("Step 2: Click Help icon");
        myMeetingWorkspacesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_MEETING_WORKSPACES);
        Assert.assertTrue(myMeetingWorkspacesDashlet.isBalloonDisplayed());
        Assert.assertEquals(myMeetingWorkspacesDashlet.getHelpBalloonMessage(), "A Meeting Workspace is a type of site that is created outside of Alfresco Content Services. This dashlet lists all of your Meeting Workspace sites."
               +"\nFrom here you can navigate to a Meeting Workspace site. You can also delete a site, if you have the correct permissions.");
        
        LOG.info("Step 3: Close ballon popup");
        myMeetingWorkspacesDashlet.closeHelpBalloon();
        Assert.assertFalse(myMeetingWorkspacesDashlet.isBalloonDisplayed());
    }
}
