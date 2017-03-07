package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RssFeedTest extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String userName = "User" + DataUtil.getUniqueIdentifier();
    private int noOfFeeds = 10;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        userService.addDashlet(userName, password, UserDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C2162")
    @Test
    public void rssFeedDashlet()
    {
        logger.info("Step 1: Verify \"RSS Feed\" dashlet");
        userDashboardPage.navigate(userName);
        assertEquals(rssFeedDashlet.getFeedsListSize(), noOfFeeds, "Number of feeds");

        logger.info("Step 2: Verify title bar actions from \"RSS Feed\" dashlet");
        assertTrue(rssFeedDashlet.isConfigureDashletIconDisplayed(), "Configure Dashlet Icon is displayed");
        assertTrue(rssFeedDashlet.isHelpIconDisplayed(DashletHelpIcon.RSS_FEED), "Help balloon is displayed");

        logger.info("Step 3: Click \"?\" icon");
        rssFeedDashlet.clickOnHelpIcon(DashletHelpIcon.RSS_FEED);
        assertTrue(rssFeedDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        assertEquals(rssFeedDashlet.getHelpBalloonMessage(), language.translate("rssFeedDashlet.helpBalloonMessage"), "Help balloon text");

        logger.info("Step 3: Click \"X\" icon on balloon popup");
        rssFeedDashlet.closeHelpBalloon();
        Assert.assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }
}