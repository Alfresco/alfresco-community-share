package org.alfresco.share.userDashboard.dashlets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
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

public class RssFeedTest extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    EnterFeedURLPopUp enterFeedURLPopUp;


    @Autowired
    UserDashboardPage userDashboardPage;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private int noOfFeeds = 10;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        userService.addDashlet(userName, password, UserDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C2162")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed" })
    public void rssFeedDashlet()
    {
        LOG.info("Step 1: Verify \"RSS Feed\" dashlet");
        userDashboardPage.navigate(userName);

        rssFeedDashlet.clickOnConfigureRssFeedDashlet();
        enterFeedURLPopUp.fillUrlField("http://feeds.reuters.com/reuters/businessNews");
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("10");
        enterFeedURLPopUp.checkNewWindowCheckbox();
        enterFeedURLPopUp.clickOkButton();

        assertEquals(rssFeedDashlet.getFeedsListSize(), noOfFeeds, "Number of feeds");

        LOG.info("Step 2: Verify title bar actions from \"RSS Feed\" dashlet");
        assertTrue(rssFeedDashlet.isConfigureDashletIconDisplayed(), "Configure Dashlet Icon is displayed");
        assertTrue(rssFeedDashlet.isHelpIconDisplayed(DashletHelpIcon.RSS_FEED), "Help balloon is displayed");

        LOG.info("Step 3: Click \"?\" icon");
        rssFeedDashlet.clickOnHelpIcon(DashletHelpIcon.RSS_FEED);
        assertTrue(rssFeedDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        assertEquals(rssFeedDashlet.getHelpBalloonMessage(), language.translate("rssFeedDashlet.helpBalloonMessage"), "Help balloon text");

        LOG.info("Step 3: Click \"X\" icon on balloon popup");
        rssFeedDashlet.closeHelpBalloon();
        Assert.assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }
}