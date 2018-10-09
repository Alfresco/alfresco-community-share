package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MyDiscussionsDashletTests extends ContextAwareWebTest
{

    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    MyDiscussionsDashlet myDiscussionsDashlet;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.MY_DISCUSSIONS, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C2791")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void myDiscussionsDashlet()
    {
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.navigateErrorClick();
        LOG.info("Step 1: Verify 'My Discussions' dahslet");

        assertEquals(myDiscussionsDashlet.getDashletTitle(), "My Discussions");
        assertEquals(myDiscussionsDashlet.getDefaultMessage(), "There are no topics matching your filters.");
        
        LOG.info("Step 2: Verify 'My Topics' filter");
        assertTrue(myDiscussionsDashlet.checkTopicDropdownOptions());
        
        LOG.info("Step 3: Verify 'Topics updated in the last day' filter");
        assertTrue(myDiscussionsDashlet.checkHistoryDropdownOptions());
        
        LOG.info("Step 4: Click Help icon");
        myDiscussionsDashlet.clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS);
        assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        assertEquals(myDiscussionsDashlet.getHelpBalloonMessage(), "Discussion Forum dashlet.\nView your latest posts on the Discussion Forum.");
        
        LOG.info("Step 5: Close ballon popup");
        myDiscussionsDashlet.closeHelpBalloon();
        assertFalse(myDiscussionsDashlet.isBalloonDisplayed());
    }
}
