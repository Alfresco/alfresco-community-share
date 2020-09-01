package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.dialog.ConfigureRSSFeedDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RssFeedDashletTests extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    ConfigureRSSFeedDialog configureRSSFeedDialog;

    @Autowired
    EnterFeedURLPopUp enterFeedURLPopUp;


    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private int noOfFeeds = 10;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
    }

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @AfterMethod (alwaysRun = true)
    public void afterMethod()
    {
        cleanupAuthenticatedSession();
    }

    @Bug (id = "MNT-17064", status = Bug.Status.FIXED)
    @TestRail (id = "C2795")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed"  })
    public void rssFeedDashlet()
    {
        LOG.info("Step 1: Verify \"RSS Feed\" dashlet");
        siteDashboardPage.navigate(siteName);

        rssFeedDashlet.configureDashlet();
        enterFeedURLPopUp.setUrlField("http://feeds.reuters.com/reuters/businessNews");
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("10");
        enterFeedURLPopUp.checkNewWindow();
        getBrowser().waitInSeconds(5);
        enterFeedURLPopUp.clickOkButton();

        rssFeedDashlet.renderedPage();
        Assert.assertEquals(rssFeedDashlet.getFeedsListSize(), noOfFeeds);

        LOG.info("Step 2: Verify title bar actions from \"RSS Feed\" dashlet");
        getBrowser().waitInSeconds(4);
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


    @Bug (id = "MNT-17064", status = Bug.Status.FIXED)
    @Test (groups = { TestGroup.SHARE, "SiteDashboard", "Acceptance" })
    public void configureExternalRss()
    {
        String url = "http://rss.cnn.com/rss/edition_europe.rss";
        LOG.info("Step 1: Navigate to site dashboard page");
        siteDashboardPage.navigate(siteName);
        rssFeedDashlet.renderedPage();
        Assert.assertTrue(rssFeedDashlet.isConfigureDashletIconDisplayed(), "Configure RSS Feed dashlet is not displayed");
        Assert.assertEquals(rssFeedDashlet.getDashletTitle(), language.translate("rssFeedDashlet.DefaultTitleText"), "Default dashlet title is not correct.");
        LOG.info("Step 2: Click edit dashlet icon and edit external RSS");
        rssFeedDashlet.configureDashlet();
        Assert.assertTrue(siteDashboardPage.isRSSFeedConfigurationDialogDisplayed(), "Configuration dialog is not opened");
        configureRSSFeedDialog.typeInURL(url);
        configureRSSFeedDialog.clickOkButton();
        Assert.assertEquals(rssFeedDashlet.getDashletTitle(), "CNN.com - RSS Channel - Regions - Europe", "Dahlet title is not correct o has not been edited.");
    }


    @Bug (id = "MNT-17064", status = Bug.Status.FIXED)
    @Test (groups = { TestGroup.SHARE, "SiteDashboard", "Acceptance" })
    public void configureNumberOfHeadlines()
    {
        LOG.info("Step 1: Navigate to site dashboard page");
        siteDashboardPage.navigate(siteName);
        rssFeedDashlet.renderedPage();
        LOG.info("Step 2: Open configure RSS Feed and edit the number of headlines to display");
        rssFeedDashlet.configureDashlet();
        configureRSSFeedDialog.selectNumberOfItemsToDisplay("5");
        configureRSSFeedDialog.clickOkButton();
        Assert.assertEquals(rssFeedDashlet.getFeedsListSize(), 5, "Number of headlines is not 5 as configured.");
    }
}