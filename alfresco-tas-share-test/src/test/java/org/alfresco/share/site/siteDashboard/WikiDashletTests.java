package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SelectWikiPagePopUp;
import org.alfresco.po.share.dashlet.WikiDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WikiDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    WikiDashlet wikiDashlet;

    @Autowired
    SelectWikiPagePopUp selectWikiPage;

    private String user = "user" + DataUtil.getUniqueIdentifier();
    private String siteName;
    private String wikiPageTitle = "C5433" + DataUtil.getUniqueIdentifier();
    private String wikiContent = "C5433WikiContent" + DataUtil.getUniqueIdentifier();
    private String wikiPageTitle1 = "C5553" + DataUtil.getUniqueIdentifier();
    private String wikiContent1 = "C5553WikiContent" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5428")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void wikiDashletNoWikiPageCreated()
    {
        //precondition
        siteName = "Site-C5428-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, SiteDashlet.WIKI, DashletLayout.THREE_COLUMNS, 3, 1);

        LOG.info("Step 1: Verify Wiki dashlet displays Help icon");
        siteDashboard.navigate(siteName);
        Assert.assertTrue(wikiDashlet.isHelpIconDisplayed(DashletHelpIcon.WIKI), "Wiki Dashlet has help icon.");

        LOG.info("Step 2: Verify Wiki dashlet displays Edit icon");
        Assert.assertTrue(wikiDashlet.isConfigureDashletIconDisplayed(), "Wiki Dashlet has configure icon.");

        LOG.info("Step 3: Verify the text displayed in Wiki dashlet");
        Assert.assertEquals(wikiDashlet.getWikiMessage(), "No page is configured", "Message: 'No page is configured' is displayed.");

        LOG.info("Step 4: Click Help icon and veify the text displayed in the Balloon pop-up");
        wikiDashlet.clickOnHelpIcon(DashletHelpIcon.WIKI);
        Assert.assertTrue(wikiDashlet.isBalloonDisplayed(), "Wiki Help balloon is displayed.");
        assertEquals(wikiDashlet.getHelpBalloonMessage(),
                "This dashlet shows a page selected from the site's wiki." + "\nNavigate to the wiki to see all related content.");

        LOG.info("Step 5: Close balloon popup");
        wikiDashlet.closeHelpBalloon();
        Assert.assertFalse(wikiDashlet.isBalloonDisplayed(), "Wiki Help balloon is not displayed.");

        LOG.info("Step 6: Click the Edit icon");
        wikiDashlet.clickOnConfigureDashletIcon();
        Assert.assertEquals(selectWikiPage.getEditPopupText(), "There are no pages available to select");

        LOG.info("Step 7: Close the Edit Pop-up by clicking the X button");
        selectWikiPage.clickCloseButton();
    }

    @TestRail(id = "C5433")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void configureWikiDashletOneWikiPageAvailable()
    {
        /**
         * Precondition: Add Wiki page to Wiki
         */
        siteName = "Site-C5433-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, SiteDashlet.WIKI, DashletLayout.THREE_COLUMNS, 3, 1);
        sitePagesService.createWiki(user, password, siteName, wikiPageTitle, wikiContent, null);

        /**
         * Test Steps
         */

        LOG.info("Step 1: Navigate to site Dashboard and click on Edit");
        siteDashboard.navigate(siteName);
        wikiDashlet.clickOnConfigureDashletIcon();
        Assert.assertEquals(selectWikiPage.getEditWikiPageFormTitle(), "Select Wiki Page");

        LOG.info("Step 2: Verify the Configure dashlet form is displayed and that it has all elements expected");
        Assert.assertTrue(selectWikiPage.isSelectAPageDropDownDisplayed());
        Assert.assertTrue(selectWikiPage.isOkButtonDisplayed());
        Assert.assertTrue(selectWikiPage.isCancelButtonDisplayed());
        Assert.assertTrue(selectWikiPage.isCloseButtonDisplayed());

        LOG.info("Step 3: Verify Select a Page drop-down list.");
        selectWikiPage.clickDropDownListOnSelectWikiPage();
        Assert.assertEquals(selectWikiPage.getWikiPageName(), wikiPageTitle, "Created wiki page is ");

        LOG.info("Step 4: Click On Cancel and verify that the Wiki dashlet is not updated");
        selectWikiPage.clickCancelButton();
        Assert.assertEquals(wikiDashlet.getWikiDashletTitle(), "Wiki");
        Assert.assertEquals(wikiDashlet.getWikiDashletContentText(), "No page is configured");

        LOG.info("Step 5: Click on Edit to return to the Configure Wiki Dashlet");
        wikiDashlet.clickOnConfigureDashletIcon();
        Assert.assertEquals(selectWikiPage.getEditWikiPageFormTitle(), "Select Wiki Page");

        LOG.info("Step 6: Choose any wiki page from the list.");
        selectWikiPage.clickOkButton();

        LOG.info(
                "Step 7: Site Dashboard page is displayed, Site Wiki dashlet has the name of chosen wiki page, wiki page content is displayed on the dashlet.");
        getBrowser().waitInSeconds(2);
        String expectedRelativePath = "share/page/site/" + siteName + "/dashboard";
        assertEquals(siteDashboard.getRelativePath(), expectedRelativePath, "User is redirected to site dashboard");
        String expectedWikiDashletName = "Wiki - " + wikiPageTitle;
        Assert.assertEquals(wikiDashlet.getWikiDashletTitle(), expectedWikiDashletName);
        Assert.assertEquals(wikiDashlet.getWikiDashletContentText(), wikiContent);

        /**
         * Cleanup
         */
        sitePagesService.deleteWikiPage(adminUser, adminPassword, siteName, wikiPageTitle);

    }

    @TestRail(id = "C5553")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void configureWikiDashletMultipleWikiPagesAvailable()

    {
        /**
         * Precondition: Add wiki pages to Wiki
         */
        siteName = "Site-C5553-" + DataUtil.getUniqueIdentifier();
        siteService.create(user, password, domain, siteName, siteName, Visibility.PUBLIC);
        siteService.addDashlet(user, password, siteName, SiteDashlet.WIKI, DashletLayout.THREE_COLUMNS, 3, 1);
        sitePagesService.createWiki(user, password, siteName, wikiPageTitle, wikiContent, null);
        sitePagesService.createWiki(user, password, siteName, wikiPageTitle1, wikiContent1, null);

        /**
         * Test Steps
         */
        LOG.info("Step 1: Navigate to site Dashboard / Wiki Dashlet and click on Edit");
        siteDashboard.navigate(siteName);
        wikiDashlet.clickOnConfigureDashletIcon();
        Assert.assertEquals(selectWikiPage.getEditWikiPageFormTitle(), "Select Wiki Page");

        LOG.info("Step 2: Select first wiki page from the Select a Page drop down and click OK");
        selectWikiPage.selectWikiPageFromList(1);
        selectWikiPage.clickOkButton();
        String expectedWikiDashletName = "Wiki - " + wikiPageTitle1;
        Assert.assertEquals(wikiDashlet.getWikiDashletTitle(), expectedWikiDashletName);

        LOG.info("Step 3: Select second wiki page from the Select a Page drop down and click Cancel");
        wikiDashlet.clickOnConfigureDashletIcon();
        selectWikiPage.selectWikiPageFromList(2);
        selectWikiPage.clickCancelButton();
        Assert.assertEquals(wikiDashlet.getWikiDashletTitle(), expectedWikiDashletName);
        /**
         * Cleanup
         */
        sitePagesService.deleteWikiPage(adminUser, adminPassword, siteName, wikiPageTitle);
        sitePagesService.deleteWikiPage(adminUser, adminPassword, siteName, wikiPageTitle1);
    }
}
