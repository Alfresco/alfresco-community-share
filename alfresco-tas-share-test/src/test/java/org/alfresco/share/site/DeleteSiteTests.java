package org.alfresco.share.site;

import org.alfresco.common.EnvProperties;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class DeleteSiteTests extends ContextAwareWebTest
{
    @Autowired
    EnvProperties envProperties;

    //@Autowired
    Toolbar toolbar;

    //@Autowired
    SiteFinderPage siteFinderPage;

    //@Autowired
    DeleteSiteDialog deleteSiteDialog;

    //@Autowired
    SearchPage searchFromToolbarPage;

    //@Autowired
    SystemErrorPage systemErrorPage;

    //@Autowired
    MySitesDashlet mySitesDashlet;

    //@Autowired
    SitesManagerPage sitesManagerPage;

    //@Autowired
    SiteDashboardPage siteDashboardPage;

    String userC2280 = String.format("userC2280%s", RandomData.getRandomAlphanumeric());
    String siteNameC2280_1 = String.format("SiteNameC2280%s", RandomData.getRandomAlphanumeric());
    String fileNameC2280 = String.format("fileC2280-%s", RandomData.getRandomAlphanumeric());
    String fileNameC2280_1 = String.format("fileC2280-%s", RandomData.getRandomAlphanumeric());
    String siteNameC2280_2 = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    String userC2281 = String.format("1UserC2281%s", RandomData.getRandomAlphanumeric());
    String siteNameUserCanNotDelete = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    String userc2282 = String.format("1UserC2282%s", RandomData.getRandomAlphanumeric());
    String userC2283 = String.format("1UserC2283%s", RandomData.getRandomAlphanumeric());
    String userC2284 = String.format("UserC2284%s", RandomData.getRandomAlphanumeric());
    String siteNameC2284 = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    String userC2289_1 = String.format("1UserC2289%s", RandomData.getRandomAlphanumeric());
    String userC2289_2 = String.format("2UserC2289%s", RandomData.getRandomAlphanumeric());
    String userC2291 = String.format("userC2291%s", RandomData.getRandomAlphanumeric());
    String siteNameC2291 = String.format("0-C2291-%s", RandomData.getRandomAlphanumeric());
    String siteNameC2292 = String.format("0-C2292-%s", RandomData.getRandomAlphanumeric());
    SoftAssert softAssert = new SoftAssert();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userC2280, password, userC2280 + domain, "firstName", "lastName");
        siteService.create(userC2280, password, domain, siteNameC2280_1, description, SiteService.Visibility.PUBLIC);
        siteService.create(userC2280, password, domain, siteNameC2280_2, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userC2280, password, siteNameC2280_1, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC2280, "fileC2280");
        contentService.createDocument(userC2280, password, siteNameC2280_2, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC2280_1, "fileC2280");
        userService.create(adminUser, adminPassword, userC2281, password, userC2281 + domain, "firstName", "lastName");
        siteService.create(adminUser, adminPassword, domain, siteNameUserCanNotDelete, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userC2281, siteNameUserCanNotDelete, "SiteContributor");
        userService.create(adminUser, adminPassword, userc2282, password, userc2282 + domain, "firstName", "lastName");
        userService.createSiteMember(adminUser, adminPassword, userc2282, siteNameUserCanNotDelete, "SiteCollaborator");
        userService.create(adminUser, adminPassword, userC2283, password, userC2283 + domain, "firstName", "lastName");
        userService.createSiteMember(adminUser, adminPassword, userC2283, siteNameUserCanNotDelete, "SiteConsumer");
        userService.create(adminUser, adminPassword, userC2284, password, userC2284 + domain, "firstName", "lastName");
        siteService.create(userC2284, password, domain, siteNameC2284, description, SiteService.Visibility.PUBLIC);
        userService.create(adminUser, adminPassword, userC2289_1, password, userC2289_1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, userC2289_2, password, userC2289_2 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, userC2291, password, userC2291 + domain, "firstName", "lastName");
        siteService.create(userC2291, password, domain, siteNameC2291, description, SiteService.Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, siteNameC2292, description, SiteService.Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userC2280);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2280);
        userService.delete(adminUser, adminPassword, userC2281);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2281);
        userService.delete(adminUser, adminPassword, userc2282);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userc2282);
        userService.delete(adminUser, adminPassword, userC2283);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2283);
        userService.delete(adminUser, adminPassword, userC2284);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2284);

        userService.delete(adminUser, adminPassword, userC2289_1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2289_1);
        userService.delete(adminUser, adminPassword, userC2289_2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2289_2);
        userService.delete(adminUser, adminPassword, userC2291);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC2291);


        siteService.delete(adminUser, adminPassword, siteNameC2280_1);
        siteService.delete(adminUser, adminPassword, siteNameC2280_2);
        siteService.delete(adminUser, adminPassword, siteNameUserCanNotDelete);
        siteService.delete(adminUser, adminPassword, siteNameC2284);
        siteService.delete(adminUser, adminPassword, siteNameC2291);
        siteService.delete(adminUser, adminPassword, siteNameC2292);
    }

    @TestRail (id = "C2280")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromSiteFinder()
    {
        setupAuthenticatedSession(userC2280, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigate();
        assertEquals(getBrowser().getTitle(), "Alfresco ?? Site Finder", "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameC2280_1);
//        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2280_1), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameC2280_1, "Delete"), true, "Delete option is displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameC2280_1, "Leave"), true, "Leave option is displayed.");

        LOG.info("STEP4: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2280_1, "Delete");
        assertTrue(deleteSiteDialog.isPopupDisplayed(), "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2280_1 + "''?"), true, "Confirm delete message is correct.");

        LOG.info("STEP5: Click on \"Delete\" button from popup");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");

        LOG.info("STEP6: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
//        siteFinderPage.searchSite(siteNameC2280_1);
        assertFalse(siteFinderPage.isSiteNameDisplayed(siteNameC2280_1), "The site isn't displayed on \"Site Finder\" page.");

        LOG.info("STEP7: Search for the file created within the site");
        userDashboard.navigateByMenuBar();
        toolbar.search(fileNameC2280);
        assertFalse(searchFromToolbarPage.isResultFound(fileNameC2280), String.format("File is found %s", fileNameC2280));

        LOG.info("STEP8: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2280_1 + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2286")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromDashlet()
    {
        setupAuthenticatedSession(userC2280, password);
        userDashboard.navigate(userC2280);

        LOG.info("STEP1&2: Hover over the created site from \"My sites\" dashlet. Click on \"Delete\" button");
        mySitesDashlet.clickDelete(siteNameC2280_2);
        assertTrue(deleteSiteDialog.isPopupDisplayed(), "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2280_2 + "''?"), true, "Confirm delete message is correct.");

        LOG.info("STEP3: Click again on \"Delete\" button.");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");

        LOG.info("STEP4: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
        assertFalse(mySitesDashlet.isSitePresent(siteNameC2280_2), "Site isn't displayed in 'MySites' dashlet.");

        LOG.info("STEP5: Search for the file created within the site");
        toolbar.search(siteNameC2280_2);
        assertEquals(searchFromToolbarPage.getNumberOfResultsText(), "0 - results found", "No results");

        LOG.info("STEP6: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2280_2 + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2281, C2287")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsContributorFromSiteFinder()
    {
        setupAuthenticatedSession(userC2281, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbar.clickSites().clickSiteFinder();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete);
//        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Leave"), true, "Leave option is displayed.");
        userDashboard.navigate(userC2281);

        LOG.info("Step 4: Verify options available on My Sites Dashlet");
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete);
        softAssert.assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2282, C2288")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsCollaboratorFromSiteFinder()
    {
        setupAuthenticatedSession(userc2282, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbar.clickSites().clickSiteFinder();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete);
//        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Leave"), true, "Leave option is displayed.");

        LOG.info("Step 4: Verify options available on My Sites Dashlet");
        userDashboard.navigateByMenuBar();
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete);
        softAssert.assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2283, C2289")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsConsumerFromSiteFinder()
    {
        setupAuthenticatedSession(userC2283, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigateByMenuBar();
        softAssert.assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameUserCanNotDelete);
//        softAssert.assertEquals(siteFinderPage.checkSiteWasFound(siteNameUserCanNotDelete), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Delete"), false, "Delete option is not displayed.");
        softAssert.assertEquals(siteFinderPage.isButtonDisplayedForSite(siteNameUserCanNotDelete, "Leave"), true, "Leave option is displayed.");

        LOG.info("Step 4: Verify options available on My Sites Dashlet");
        userDashboard.navigate(userC2283);
        mySitesDashlet.hoverSite(siteNameUserCanNotDelete);
        assertEquals(mySitesDashlet.isDeleteButtonDisplayed(), false, "Delete button isn't displayed.");
        softAssert.assertAll();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2284")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeleteSiteFromSiteFinder()
    {
        setupAuthenticatedSession(userC2284, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        siteFinderPage.navigateByMenuBar();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSiteWithName(siteNameC2284);
//        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284), true, "Site is found.");

        LOG.info("STEP3: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2284, "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2284 + "''?"), true, "Confirm delete message is correct.");

        LOG.info("STEP4: Click on 'Cancel' button");
        deleteSiteDialog.clickCancel();
        siteFinderPage.searchSiteWithName(siteNameC2284);
//        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284), true, "Site is found, it wasn't deleted.");

        LOG.info("STEP5: Click again on 'Delete' button");
        siteFinderPage.clickSiteButton(siteNameC2284, "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteNameC2284 + "''?"), true, "Confirm delete message is correct.");

        LOG.info("STEP6: Click on 'Delete' button");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true, "Second confirm delete message is correct");
        deleteSiteDialog.clickNo();
        siteFinderPage.searchSiteWithName(siteNameC2284);
//        assertEquals(siteFinderPage.checkSiteWasFound(siteNameC2284), true, "Site is found, it wasn't deleted.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2292")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeleteSiteFromSitesManager()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP1: Open \"Site Manager\" page");
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.isSitesTableDisplayed(), true, "Site Manager page is displayed.");

        LOG.info("STEP2: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
//        sitesManagerPage.usingSite(siteModel).clickDelete().assertConfirmMessageFromSiteManagerIsCorrect(siteNameC2292)
//            .clickCancelFromSitesManager();
//        sitesManagerPage.usingSite(siteNameC2292).assertSiteIsDisplayed();

        LOG.info("STEP4: Open the created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteNameC2292 + "/dashboard";
        getBrowser().navigate().to(url);
//        assertEquals(siteDashboardPage.getCurrentUrl(), url.replace(":80/", "/"), "User is successfully redirected to the site dashboard.");
        cleanupAuthenticatedSession();
    }
}