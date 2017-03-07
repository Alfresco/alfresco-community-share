package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RssFeedDashletTests extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String userName = "User" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
    private int noOfFeeds = 10;

    @BeforeClass
    public void setup()
    {
        super.setup();
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addDashlet(userName, DataUtil.PASSWORD, siteName, SiteDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C2795")
    @Test
    public void rssFeedDashlet()
    {
        logger.info("Step 1: Verify \"RSS Feed\" dashlet");
        siteDashboardPage.navigate(siteName);
        Assert.assertEquals(rssFeedDashlet.getFeedsListSize(), noOfFeeds);

        logger.info("Step 2: Verify title bar actions from \"RSS Feed\" dashlet");
        Assert.assertTrue(rssFeedDashlet.isConfigureDashletIconDisplayed(), "Configure Dashlet Icon is displayed");
        Assert.assertTrue(rssFeedDashlet.isHelpIconDisplayed(DashletHelpIcon.RSS_FEED), "Help balloon is displayed");

        logger.info("Step 3: Click \"?\" icon");
        rssFeedDashlet.clickOnHelpIcon(DashletHelpIcon.RSS_FEED);
        Assert.assertTrue(rssFeedDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        Assert.assertEquals(rssFeedDashlet.getHelpBalloonMessage(), language.translate("rssFeedDashlet.helpBalloonMessage"), "Help balloon text");

        logger.info("Step 3: Click \"X\" icon on balloon popup");
        rssFeedDashlet.closeHelpBalloon();
        Assert.assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }
}