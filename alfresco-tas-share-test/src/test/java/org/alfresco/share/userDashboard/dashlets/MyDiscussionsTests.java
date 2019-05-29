package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDiscussionsTests extends ContextAwareWebTest
{

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    MyDiscussionsDashlet myDiscussionsDashlet;

    private String userName;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        userService.addDashlet(userName, password, UserDashlet.MY_DISCUSSIONS, DashletLayout.THREE_COLUMNS, 3, 1);

        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C2774")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void myDiscussionsDashlet()
    {
        userDashboardPage.navigate(userName);

        LOG.info("Step 1: Verify 'My Discussions' dahslet");
        Assert.assertEquals(myDiscussionsDashlet.getDashletTitle(), "My Discussions");
        Assert.assertEquals(myDiscussionsDashlet.getDefaultMessage(), "There are no topics matching your filters.");

        LOG.info("Step 2: Verify 'My Topics' filter");
        getBrowser().refresh();
        Assert.assertTrue(myDiscussionsDashlet.checkTopicDropdownOptions());

        LOG.info("Step 3: Verify 'Topics updated in the last day' filter");
        Assert.assertTrue(myDiscussionsDashlet.checkHistoryDropdownOptions());

        LOG.info("Step 4: Click Help icon");
        myDiscussionsDashlet.clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS);
        Assert.assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        Assert.assertEquals(myDiscussionsDashlet.getHelpBalloonMessage(), "Discussion Forum dashlet.\nView your latest posts on the Discussion Forum.");

        LOG.info("Step 5: Close ballon popup");
        myDiscussionsDashlet.closeHelpBalloon();
        Assert.assertFalse(myDiscussionsDashlet.isBalloonDisplayed());
    }
}
