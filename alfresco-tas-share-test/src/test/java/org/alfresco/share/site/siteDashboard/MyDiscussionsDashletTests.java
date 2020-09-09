package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyDiscussionsDashletTests extends ContextAwareWebTest
{

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    MyDiscussionsDashlet myDiscussionsDashlet;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.MY_DISCUSSIONS, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2791")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void myDiscussionsDashlet()
    {
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.navigateErrorClick();
        LOG.info("Step 1: Verify 'My Discussions' dahslet");

        myDiscussionsDashlet.assertDashletTitleIs(language.translate("myDiscussionDashlet.title"))
            .assertNoTopicsMessageIsDisplayed();

        LOG.info("Step 2: Verify 'My Topics' filter");
        myDiscussionsDashlet.assertTopicDropdownHasAllOptions()
            .assertHistoryDropdownHasAllOptions();

        LOG.info("Step 4: Click Help icon");
        myDiscussionsDashlet.clickOnHelpIcon(DashletHelpIcon.MY_DISCUSSIONS);
        assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        assertEquals(myDiscussionsDashlet.getHelpBalloonMessage(), "Discussion Forum dashlet.\nView your latest posts on the Discussion Forum.");

        LOG.info("Step 5: Close ballon popup");
        myDiscussionsDashlet.closeHelpBalloon();
        assertFalse(myDiscussionsDashlet.isBalloonDisplayed());
    }
}
