package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.EnterFeedURLPopUp;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class AlfrescoAddonsRssFeedDashletTests extends ContextAwareWebTest
{
    @Autowired
    RssFeedDashlet rssFeedDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    EnterFeedURLPopUp enterFeedURLPopUp;
    SoftAssert softAssert = new SoftAssert();
    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String siteNameShare = "RssFeed" + RandomData.getRandomAlphanumeric();
    private String userNameShare = "RssFeedUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true, enabled = false)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteName, SiteDashlet.ADDONS_RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 1);
        userService.create(adminUser, adminPassword, userNameShare, password, userNameShare + domain, "RssFeed", "User");
        siteService.create(userNameShare, password, domain, siteNameShare, "rssFeedSite", SiteService.Visibility.PUBLIC);
        siteService.addDashlet(userName, password, siteNameShare, SiteDashlet.ADDONS_RSS_FEED, DashletLayout.THREE_COLUMNS, 3, 2);
    }

    @AfterClass (alwaysRun = true, enabled = false)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        userService.delete(adminUser, adminPassword, userNameShare);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userNameShare);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteNameShare);
    }

    @TestRail (id = "C2793")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES }, enabled = false)
    public void configureAlfrescoAddonsRssFeedDashlet()
    {
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: Click 'Configure this dashlet' icon");
        siteDashboardPage.navigate(siteName);
        rssFeedDashlet.configureDashlet();
        assertEquals(enterFeedURLPopUp.getPopUpTitle(), "Enter Feed URL:", "'Enter Feed URL' form is displayed");

        LOG.info("Step 2: Fill in 'URL' field with valid data");
        enterFeedURLPopUp.setUrlField("http://feeds.reuters.com/reuters/businessNews");

        LOG.info("Step 3: From 'Number of items to display' drop-down list choose any value (e.g: 5)");
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("5");
        assertTrue(enterFeedURLPopUp.isValueSelectedFromNoItemsToDisplayDropDown("5"), "The value is displayed");

        LOG.info("Step 4: Click 'Open links in new window' check-box");
        enterFeedURLPopUp.checkNewWindow();
        assertTrue(enterFeedURLPopUp.isNewWindowCheckBoxChecked(), "Check-box is selected");

        LOG.info("Step 5: Press OK button.");
        enterFeedURLPopUp.clickOkButton();
        rssFeedDashlet.renderedPage();
        assertTrue(rssFeedDashlet.getDashletTitle().contains("Reuters: Business News"), "Site Dashboard page is displayed, feed information is updated: "
            + rssFeedDashlet.getDashletTitle());

        //Store the current window handle
        String currentWindow = getBrowser().getWindowHandle();

        LOG.info("Step 6: Click on any RSS news");
        rssFeedDashlet.clickOnRssLink(1);
        getBrowser().waitInSeconds(5);

        //Switch to new window opened

//        for (String winHandle : getBrowser().getWindowHandles())
//        {
//            getBrowser().switchTo().window(winHandle);
//            if (getBrowser().getCurrentUrl().contains("http://www.reuters.com"))
//            {
//                break;
//            } else
//            {
//                getBrowser().switchTo().window(currentWindow);
//            }
//        }

        assertTrue(getBrowser().getCurrentUrl().contains("https://www.reuters.com"), "After clicking on RSS link, the title is: " + getBrowser().getCurrentUrl());
        closeWindowAndSwitchBack();
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SITE_DASHBOARD }, enabled = false)
    public void verifyDashletTest()
    {
        setupAuthenticatedSession(userNameShare, password);
        siteDashboardPage.navigate(siteNameShare);
        LOG.info("Step 1: Verify the RSS Feed default title");
        softAssert.assertEquals(rssFeedDashlet.getDashletTitle(), language.translate("rssAddonsFeedDashlet.DefaultTitle"), "dashet title is not correct.");
        LOG.info("Step 2: Verify the help icon");
        softAssert.assertTrue(rssFeedDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.RSS_FEED), "The help icon is not displayed");
        rssFeedDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.RSS_FEED);
        softAssert.assertEquals(rssFeedDashlet.getHelpBalloonMessage(), language.translate("rssAddonsFeedDashlet.helpBalloonMessage"), "help message is not correct");
        rssFeedDashlet.closeHelpBalloon();
        softAssert.assertFalse(rssFeedDashlet.isBalloonDisplayed(), "Help balloon is still displayed");
        LOG.info("Step 3: Verify headlines");
        rssFeedDashlet.configureDashlet();
        enterFeedURLPopUp.selectNumberOfItemsToDisplay("5");
        enterFeedURLPopUp.clickOkButton();
        softAssert.assertEquals(rssFeedDashlet.getFeedsListSize(), 5, "Feed size is not as expected");
        softAssert.assertAll();
    }
}