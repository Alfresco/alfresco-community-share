package org.alfresco.share.site;

import org.alfresco.common.DataUtil;
import org.alfresco.common.EnvProperties;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Laura.Capsa
 */

public class DeleteSiteTests extends ContextAwareWebTest
{
    @Autowired
    EnvProperties envProperties;
    @Autowired
    ToolbarSitesMenu toolbarSitesMenu;

    @Autowired
    Toolbar toolbar;

    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    DeleteSiteDialog deleteSiteDialog;

    @Autowired
    SearchPage searchFromToolbarPage;

    @Autowired
    SystemErrorPage systemErrorPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    SitesManagerPage sitesManagerPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @TestRail(id = "C2280")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromSiteFinder()
    {
        String user = "userC2280" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteNameC2280" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();
        String fileName = "fileC2280-" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "fileC2280");
        setupAuthenticatedSession(user, password);
        getBrowser().refresh();

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbarSitesMenu.clickSiteFinder();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Delete"), true, "Delete option is displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), true, "Leave option is displayed.");

        LOG.info("STEP4: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteName, "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteName + "''?"), true,
                "Confirm delete message is correct.");

        LOG.info("STEP5: Click on \"Delete\" button from popup");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true,
                "Second confirm delete message is correct");

        LOG.info("STEP6: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
        siteFinderPage.searchSite(siteName);
        assertFalse(siteFinderPage.checkSiteWasFound(siteName), "The site isn't displayed on \"Site Finder\" page.");

        LOG.info("STEP7: Search for the file created within the site");
        toolbar.search(fileName);
        searchFromToolbarPage.renderedPage();
        getBrowser().waitInSeconds(2);
        assertEquals(searchFromToolbarPage.getNumberOfResultsText(), "0 - results found", "Search page: number of results");

        LOG.info("STEP8: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteName + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2281")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsContributorFromSiteFinder()
    {
        String user1 = "1UserC2281" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2281" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteContributor");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbarSitesMenu.clickSiteFinder();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Delete"), false, "Delete option is not displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), true, "Leave option is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2282")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsCollaboratorFromSiteFinder()
    {
        String user1 = "1UserC2282" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2282" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbarSitesMenu.clickSiteFinder();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Delete"), false, "Delete option is not displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), true, "Leave option is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2283")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsConsumerFromSiteFinder()
    {
        String user1 = "1UserC2283" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2283" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteConsumer");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbarSitesMenu.clickSiteFinder();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found.");

        LOG.info("STEP3: Verify options available for the site");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Delete"), false, "Delete option is not displayed.");
        assertEquals(siteFinderPage.isButtonDisplayedForSite(siteName, "Leave"), true, "Leave option is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2284")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeleteSiteFromSiteFinder()
    {
        String user = "UserC2284" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);

        LOG.info("STEP1: Navigate to \"Site Finder\" page (from Alfresco Toolbar -> Sites menu -> Site Finder)");
        toolbarSitesMenu.clickSiteFinder();
        assertEquals(siteFinderPage.isSearchFieldDisplayed(), true, "Site Finder page is displayed.");

        LOG.info("STEP2: Search for the created site");
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found.");

        LOG.info("STEP3: Click on 'Delete' button");
        siteFinderPage.clickSiteButton(siteName, "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteName + "''?"), true,
                "Confirm delete message is correct.");

        LOG.info("STEP4: Click on 'Cancel' button");
        deleteSiteDialog.clickCancel();
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found, it wasn't deleted.");

        LOG.info("STEP5: Click again on 'Delete' button");
        siteFinderPage.clickSiteButton(siteName, "Delete");
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteName + "''?"), true,
                "Confirm delete message is correct.");

        LOG.info("STEP6: Click on 'Delete' button");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true,
                "Second confirm delete message is correct");
        deleteSiteDialog.clickNo();
        siteFinderPage.searchSite(siteName);
        assertEquals(siteFinderPage.checkSiteWasFound(siteName), true, "Site is found, it wasn't deleted.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2286")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsManagerFromDashlet()
    {
        String user = "UserC2286" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();
        String fileName = "fileC2286-" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, description, Site.Visibility.PUBLIC);
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, "fileC2286");
        setupAuthenticatedSession(user, password);
        getBrowser().refresh();

        LOG.info("STEP1&2: Hover over the created site from \"My sites\" dashlet. Click on \"Delete\" button");
        mySitesDashlet.clickDeleteSiteIconForSite(siteName);
        assertEquals(deleteSiteDialog.isPopupDisplayed(), true, "Delete popup is displayed.");
        assertEquals(deleteSiteDialog.getConfirmMessage().equals(language.translate("deleteSite.confirm") + siteName + "''?"), true,
                "Confirm delete message is correct.");

        LOG.info("STEP3: Click again on \"Delete\" button.");
        deleteSiteDialog.clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessage().contains(language.translate("deleteSite.confirmAgain")), true,
                "Second confirm delete message is correct");

        LOG.info("STEP4: Click \"Yes\" button");
        deleteSiteDialog.clickYes();
        getBrowser().refresh();
        assertEquals(mySitesDashlet.isSitePresent(siteName), false, "Site isn't displayed in 'MySites' dashlet.");

        LOG.info("STEP5: Search for the file created within the site");
        toolbar.search(fileName);
        assertEquals(searchFromToolbarPage.getNumberOfResultsText(), "0 - results found", "No results");

        LOG.info("STEP6: Open created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteName + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2287")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsContributorFromDashlet()
    {
        String user1 = "1UserC2287" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2287" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteContributor");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Hover over the created site on \"My sites\" dashlet");
        mySitesDashlet.hoverSite(siteName);
        assertEquals(mySitesDashlet.isDeleteButtonDisplayed(siteName), false, "Delete button isn't displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2288")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsCollaboratorFromDashlet()
    {
        String user1 = "1UserC2288" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2288" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Hover over the created site on \"My sites\" dashlet");
        mySitesDashlet.hoverSite(siteName);
        assertEquals(mySitesDashlet.isDeleteButtonDisplayed(siteName), false, "Delete button isn't displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2289")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsConsumerFromDashlet()
    {
        String user1 = "1UserC2289" + DataUtil.getUniqueIdentifier();
        String user2 = "2UserC2289" + DataUtil.getUniqueIdentifier();
        String siteName = "SiteName" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteConsumer");
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP1: Hover over the created site on \"My sites\" dashlet");
        mySitesDashlet.hoverSite(siteName);
        assertEquals(mySitesDashlet.isDeleteButtonDisplayed(siteName), false, "Delete button isn't displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2291")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteSiteAsAdminFromSiteManager()
    {
        String user1 = "userC2291" + DataUtil.getUniqueIdentifier();
        String siteName = "0-C2291-" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP1: Open \"Site Manager\" page");
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.isSitesTableDisplayed(), true, "Site Manager page is displayed.");

        LOG.info("STEP2: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
        sitesManagerPage.clickActionForManagedSiteRow(siteName, "Delete Site", deleteSiteDialog);
        assertEquals(deleteSiteDialog.getConfirmMessageFromSitesManager(), String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));

        LOG.info("STEP3: Confirm site deletion");
        deleteSiteDialog.clickDeleteFromSitesManager();
        assertEquals(sitesManagerPage.isActionAvailableForManagedSiteRow(siteName, "Delete Site"), false,
                "The site is deleted - it is no longer displayed in \"manage sites\" table.\n");

        LOG.info("STEP4: Open the created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteName + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getErrorHeader(), language.translate("systemError.header"), "Error message is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2292")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeleteSiteFromSitesManager()
    {
        String user1 = "UserC2292" + DataUtil.getUniqueIdentifier();
        String siteName = "0-C2292-" + DataUtil.getUniqueIdentifier();
        String description = "Description" + DataUtil.getUniqueIdentifier();

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, Site.Visibility.PUBLIC);
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP1: Open \"Site Manager\" page");
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.isSitesTableDisplayed(), true, "Site Manager page is displayed.");

        LOG.info("STEP2: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
        sitesManagerPage.clickActionForManagedSiteRow(siteName, "Delete Site", deleteSiteDialog);
        assertEquals(deleteSiteDialog.getConfirmMessageFromSitesManager(), String.format(language.translate("deleteSite.confirmFromSitesManager"), siteName));

        LOG.info("STEP3: Click \"Cancel\" button");
        deleteSiteDialog.clickCancelFromSitesManager();
        assertEquals(sitesManagerPage.findManagedSiteRowByNameFromPaginatedResults(siteName).getSiteName().getText(), siteName, "Site is displayed.");

        LOG.info("STEP4: Open the created site by link");
        String url = envProperties.getShareUrl() + "/page/site/" + siteName + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(siteDashboardPage.getCurrentUrl(), url.replace(":80/","/"), "User is successfully redirected to the site dashboard.");

        cleanupAuthenticatedSession();
    }
}