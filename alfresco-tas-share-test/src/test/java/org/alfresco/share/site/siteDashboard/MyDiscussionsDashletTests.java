package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDiscussionsDashletTests extends ContextAwareWebTest
{

    @Autowired
    SiteDashboardPage siteDashboardPage;
    
    @Autowired
    MyDiscussionsDashlet myDiscussionsDashlet;

    private String userName = "User" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addDashlet(userName, DataUtil.PASSWORD, siteName, SiteDashlet.MY_DISCUSSIONS, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }
    
    @TestRail(id = "C2791")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void myDiscussionsDashlet()
    {
        siteDashboardPage.navigate(siteName);
        
        logger.info("Step 1: Verify 'My Discussions' dahslet");
        assertEquals(myDiscussionsDashlet.getDashletTitle(), "My Discussions");
        assertEquals(myDiscussionsDashlet.getDefaultMessage(), "There are no topics matching your filters.");
        
        logger.info("Step 2: Verify 'My Topics' filter");
        assertTrue(myDiscussionsDashlet.checkTopicDropdownOptions());
        
        logger.info("Step 3: Verify 'Topics updated in the last day' filter");
        assertTrue(myDiscussionsDashlet.checkHistoryDropdownOptions());
        
        logger.info("Step 4: Click Help icon");
        myDiscussionsDashlet.clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS);
        assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        assertEquals(myDiscussionsDashlet.getHelpBalloonMessage(), "Discussion Forum dashlet.\nView your latest posts on the Discussion Forum.");
        
        logger.info("Step 5: Close ballon popup");
        myDiscussionsDashlet.closeHelpBalloon();
        assertFalse(myDiscussionsDashlet.isBalloonDisplayed());
    }
}
