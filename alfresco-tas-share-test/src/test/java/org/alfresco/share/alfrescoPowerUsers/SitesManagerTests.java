package org.alfresco.share.alfrescoPowerUsers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.common.EnvProperties;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.SystemErrorPage;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class SitesManagerTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String name = "name";
    private final String user1 = "user1-" + random;
    private final String user2 = "user2-" + random;
    private final String user3 = "user3-" + random;
    private final String user4 = "user4-" + random;
    private final String siteAdmin = "siteAdmin-" + random;
    private final String alfrescoAdmin = "alfrescoAdmin-" + random;
    private final String siteAdminGroup = "SITE_ADMINISTRATORS";
    private final String alfrescoAdminGroup = "ALFRESCO_ADMINISTRATORS";
    private final String site1 = "0-site-C8674-" + random;
    private final String site2 = "0-site-C8675-" + random;
    private final String site3 = "0-site-C8676-" + random;
    private final String site4 = "0-site-C8680-" + random;
    private final String site5 = "site-C8696-" + random;
    private final String site6 = "site-C8689-" + random;
    private final String siteDescription = "Site Description";
    @Autowired
    private Toolbar toolbar;
    @Autowired
    private AdminToolsPage adminToolsPage;
    @Autowired
    private SitesManagerPage sitesManagerPage;
    @Autowired
    private SiteDashboardPage siteDashboardPage;
    @Autowired
    private DeleteSiteDialog deleteSiteDialog;
    @Autowired
    private EnvProperties envProperties;
    @Autowired
    private SystemErrorPage systemErrorPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, domain, name, user1);
        userService.create(adminUser, adminPassword, user2, password, domain, name, user2);
        userService.create(adminUser, adminPassword, user3, password, domain, name, user3);
        userService.create(adminUser, adminPassword, user4, password, domain, name, user4);
        userService.create(adminUser, adminPassword, siteAdmin, password, domain, name, siteAdmin);
        userService.create(adminUser, adminPassword, alfrescoAdmin, password, domain, name, alfrescoAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, siteAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, user4);
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoAdminGroup, alfrescoAdmin);
        siteService.create(siteAdmin, password, domain, site1, siteDescription, SiteService.Visibility.MODERATED);
        siteService.create(siteAdmin, password, domain, site2, siteDescription, SiteService.Visibility.MODERATED);
        siteService.create(siteAdmin, password, domain, site3, siteDescription, SiteService.Visibility.PRIVATE);
        siteService.create(siteAdmin, password, domain, site4, siteDescription, SiteService.Visibility.PUBLIC);
        siteService.create(siteAdmin, password, domain, site5, siteDescription, SiteService.Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, site6, siteDescription, SiteService.Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        userService.delete(adminUser, adminPassword, user3);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user3);

        userService.delete(adminUser, adminPassword, user4);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user4);

        userService.delete(adminUser, adminPassword, siteAdmin);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteAdmin);

        userService.delete(adminUser, adminPassword, alfrescoAdmin);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + alfrescoAdmin);

        siteService.delete(adminUser, adminPassword, site1);
        siteService.delete(adminUser, adminPassword, site2);
        siteService.delete(adminUser, adminPassword, site3);
        siteService.delete(adminUser, adminPassword, site4);
        siteService.delete(adminUser, adminPassword, site5);
        siteService.delete(adminUser, adminPassword, site6);

    }

    @TestRail (id = "C8701")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyPresenceInToolbarBasicUser()
    {
        setupAuthenticatedSession(user1, password);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Verify presence of 'Sites Manager' tab");
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");
    }

    @TestRail (id = "C8674")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifySiteManagerPage()
    {
        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Verify Sites Manager table header");
        ArrayList<String> expectedTableHeader = new ArrayList<>(Arrays.asList("Site Name", "Site Description", "Visibility", "I'm a Site Manager", "Actions"));
        assertEquals(sitesManagerPage.getTableHeader(), expectedTableHeader, "Site Manager table header=");

        LOG.info("STEP2: Verify " + site1 + " 's details");
        assertEquals(sitesManagerPage.getSiteDescription(site1), siteDescription, site1 + " 's description=");
        assertEquals(sitesManagerPage.getSiteVisibility(site1), "Moderated", site1 + " 's visibility=");
        assertTrue(sitesManagerPage.isUserSiteManager(site1), site1 + " 's value of 'I'm a Site Manager'= Yes.");
    }

    @TestRail (id = "C8675")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPublic()
    {
        String visibility = "Public";

        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site2);
        sitesManagerPage.updateSiteVisibility(site2, visibility);
        assertTrue(sitesManagerPage.isSuccesIndicatorDisplayed(site2), "Update " + site2 + " visibility: Succes indicator is displayed.");
        assertEquals(sitesManagerPage.getSiteVisibility(site2), visibility, site2 + " 's visibility on Sites Manager page=");

        LOG.info("STEP2: Navigate to " + site2 + " 's Dashboard");
        siteDashboardPage.navigate(site2);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site2 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8676")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToModerated()
    {
        String visibility = "Moderated";

        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site3);
        sitesManagerPage.updateSiteVisibility(site3, visibility);
        assertTrue(sitesManagerPage.isSuccesIndicatorDisplayed(site3), "Update " + site3 + " visibility: Succes indicator is displayed.");
        assertEquals(sitesManagerPage.getSiteVisibility(site3), visibility, site3 + " 's visibility on Sites Manager page=");

        LOG.info("STEP2: Navigate to " + site3 + " 's Dashboard");
        siteDashboardPage.navigate(site3);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site3 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8680")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPrivate()
    {
        String visibility = "Private";

        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site4);
        sitesManagerPage.updateSiteVisibility(site4, visibility);
        assertTrue(sitesManagerPage.isSuccesIndicatorDisplayed(site4), "Update " + site4 + " visibility: Succes indicator is displayed.");
        assertEquals(sitesManagerPage.getSiteVisibility(site4), visibility, site4 + " 's visibility on Sites Manager page=");

        LOG.info("STEP2: Navigate to " + site4 + " 's Dashboard");
        siteDashboardPage.navigate(site4);
        assertEquals(siteDashboardPage.getPageTitle(), "Alfresco » Site Dashboard", "Displayed page=");
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site4 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToAlfrescoAdminGroup()
    {
        setupAuthenticatedSession(user2, password);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Verify presence of 'Admin Tools' in toolbar");
        assertFalse(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP2: User1 is added to ALFRESCO_ADMINISTRATORS group. User logs out and logins to share.");
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoAdminGroup, user2);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertTrue(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP3: Navigate to 'Admin Tools' from toolbar");
        toolbar.clickAdminTools();
        assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools", "Displayed page=");

        LOG.info("STEP4: Click 'Sites Manager' option from left side panel");
        adminToolsPage.navigateToNodeFromToolsPanel(language.translate("adminTools.sitesManagerNode"), sitesManagerPage);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");
    }

    @TestRail (id = "C8682")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToSiteAdminGroup()
    {
        setupAuthenticatedSession(user3, password);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Verify presence of 'Sites Manager' in toolbar");
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP2: User is added to SITE_ADMINISTRATORS group. User logs out and logins to share.");
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, user3);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user3, password);
        assertTrue(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP3: Click 'Sites Manager' from toolbar");
        toolbar.clickSitesManager();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");
    }

    @TestRail (id = "C8683")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void removeUserFromSiteAdmin()
    {
        setupAuthenticatedSession(user4, password);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Verify presence of 'Sites Manager' in toolbar");
        assertTrue(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP2: Click 'Sites Manager' from toolbar");
        toolbar.clickSitesManager();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP3: User1 is removed from SITE_ADMINISTRATORS group. User1 logs out and logins to share.");
        groupService.removeUserFromGroup(adminUser, adminPassword, siteAdminGroup, user4);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user4, password);
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");
    }

    @TestRail (id = "C8684")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void removeUserFromAlfrescoAdmin()
    {
        setupAuthenticatedSession(alfrescoAdmin, password);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        LOG.info("STEP1: Verify presence of 'Admin Tools' in toolbar");
        assertTrue(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP2: Navigate to 'Admin Tools' from toolbar");
        toolbar.clickAdminTools();
        assertEquals(adminToolsPage.getPageTitle(), "Alfresco » Admin Tools", "Displayed page=");

        LOG.info("STEP3: Click 'Sites Manager' option from left side panel");
        adminToolsPage.navigateToNodeFromToolsPanel(language.translate("adminTools.sitesManagerNode"), sitesManagerPage);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP4: User1 is removed from ALFRESCO_ADMINISTRATORS group. User logs out and logins to share.");
        groupService.removeUserFromGroup(adminUser, adminPassword, alfrescoAdminGroup, alfrescoAdmin);
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(alfrescoAdmin, password);
        assertFalse(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");
    }

    @TestRail (id = "C8689")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void siteAdminBecomeSitesManager()
    {
        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: For " + site6 + ", click 'Actions' menu and select 'Become Site Manager' link");
        sitesManagerPage.clickActionForManagedSiteRow(site6, language.translate("sitesManager.becomeSiteManager"), sitesManagerPage);
        assertTrue(sitesManagerPage.isUserSiteManager(site6), "'I'm a Site Manager' value=Yes.");
    }

    @TestRail (id = "C8696")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void deleteSiteAsSiteAdmin()
    {
        setupAuthenticatedSession(siteAdmin, password);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
        sitesManagerPage.clickActionForManagedSiteRow(site5, "Delete Site", deleteSiteDialog);
        assertEquals(deleteSiteDialog.getConfirmMessageFromSitesManager(), String.format(language.translate("deleteSite.confirmFromSitesManager"), site5));

        LOG.info("STEP3: Confirm site deletion by clicking 'Ok' button");
        deleteSiteDialog.clickDeleteFromSitesManager();
        assertFalse(sitesManagerPage.isSiteDisplayed(site5), site5 + " is displayed in Sites Manager table.");

        LOG.info("STEP4: Navigate by link to " + site5 + " 's dashboard");
        String url = envProperties.getShareUrl() + "/page/site/" + site5 + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getPageTitle(), "Alfresco Share » System Error", "Displayed page=");
    }
}