package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RssFeedDashletTests extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired EnterFeedURLPopUp enterFeedURLPopUp;


    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private int noOfFeeds = 5;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C2795")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void rssFeedDashlet()
    {
        LOG.info("Step 1: Verify \"RSS Feed\" dashlet");
        siteDashboardPage.navigate(siteName);

        rssFeedDashlet.clickOnConfigureRssFeedDashlet();
        enterFeedURLPopUp.fillUrlField("http://feeds.reuters.com/reuters/businessNews");
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("5");
        enterFeedURLPopUp.checkNewWindowCheckbox();
        getBrowser().waitInSeconds(5);
        enterFeedURLPopUp.clickOkButton();

        rssFeedDashlet.renderedPage();
        Assert.assertEquals(rssFeedDashlet.getFeedsListSize(), noOfFeeds);

        LOG.info("Step 2: Verify title bar actions from \"RSS Feed\" dashlet");
        Assert.assertTrue(rssFeedDashlet.isConfigureDashletIconDisplayed(), "Configure Dashlet Icon is displayed");
        Assert.assertTrue(rssFeedDashlet.isHelpIconDisplayed(DashletHelpIcon.RSS_FEED), "Help balloon is displayed");

        LOG.info("Step 3: Click \"?\" icon");
        rssFeedDashlet.clickOnHelpIcon(DashletHelpIcon.RSS_FEED);
        Assert.assertTrue(rssFeedDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        Assert.assertEquals(rssFeedDashlet.getHelpBalloonMessage(), language.translate("rssFeedDashlet.helpBalloonMessage"), "Help balloon text");

        LOG.info("Step 3: Click \"X\" icon on balloon popup");
        rssFeedDashlet.closeHelpBalloon();
        Assert.assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }
}