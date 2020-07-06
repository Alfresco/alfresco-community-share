package org.alfresco.share.alfrescoPowerUsers;

import org.alfresco.dataprep.SiteService.Visibility;
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
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class SitesManagerTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String name = "name";
    private UserModel user1, user2, user3, user4, siteAdmin;
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
    private SystemErrorPage systemErrorPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user1 = dataUser.usingAdmin().createRandomTestUser();
        user2 = dataUser.createRandomTestUser();
        user3 = dataUser.createRandomTestUser();
        user4 = dataUser.createRandomTestUser();
        siteAdmin = dataUser.createRandomTestUser();
        userService.create(adminUser, adminPassword, alfrescoAdmin, password, domain, name, alfrescoAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, siteAdmin.getUsername());
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, user4.getUsername());
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoAdminGroup, alfrescoAdmin);
        siteService.create(siteAdmin.getUsername(), password, domain, site1, siteDescription, Visibility.MODERATED);
        siteService.create(siteAdmin.getUsername(), password, domain, site2, siteDescription, Visibility.MODERATED);
        siteService.create(siteAdmin.getUsername(), password, domain, site3, siteDescription, Visibility.PRIVATE);
        siteService.create(siteAdmin.getUsername(), password, domain, site4, siteDescription, Visibility.PUBLIC);
        siteService.create(siteAdmin.getUsername(), password, domain, site5, siteDescription, Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, site6, siteDescription, Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user1);
        removeUserFromAlfresco(user2);
        removeUserFromAlfresco(user3);
        removeUserFromAlfresco(user4);
        removeUserFromAlfresco(siteAdmin);

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
        setupAuthenticatedSession(user1);
        LOG.info("STEP1: Verify presence of 'Sites Manager' tab");
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");
    }

    @TestRail (id = "C8674")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifySiteManagerPage()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP1: Verify Sites Manager table header");
        ArrayList<String> expectedTableHeader = new ArrayList<>(Arrays.asList("Site Name", "Site Description", "Visibility", "I'm a Site Manager", "Actions"));
        assertEquals(sitesManagerPage.getTableHeader(), expectedTableHeader, "Site Manager table header=");

        LOG.info("STEP2: Verify " + site1 + " 's details");
        sitesManagerPage.usingSite(site1)
            .assertSiteDescriptionIs(siteDescription)
            .assertSiteVisibilityIs(Visibility.MODERATED)
            .assertSiteManagerIsYes();
    }

    @TestRail (id = "C8675")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPublic()
    {
        String visibility = "Public";

        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site2);
        sitesManagerPage.usingSite(site2)
            .changeSiteVisibility(Visibility.PUBLIC)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.PUBLIC);

        LOG.info("STEP2: Navigate to " + site2 + " 's Dashboard");
        siteDashboardPage.navigate(site2);
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site2 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8676")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToModerated()
    {
        String visibility = "Moderated";

        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site3);
        sitesManagerPage.usingSite(site3)
            .changeSiteVisibility(Visibility.MODERATED)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.MODERATED);

        LOG.info("STEP2: Navigate to " + site3 + " 's Dashboard");
        siteDashboardPage.navigate(site3);
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site3 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8680")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void updateSiteVisibilityToPrivate()
    {
        String visibility = "Private";
        dataSite.createPublicRandomSite();
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();

        LOG.info("STEP1: Select " + visibility + " from \"Visibility\" dropdown for " + site4);
        sitesManagerPage.usingSite(site4)
            .changeSiteVisibility(Visibility.PRIVATE)
            .assertSuccessIndicatorIsDisplayed()
            .assertSiteVisibilityIs(Visibility.PRIVATE);

        LOG.info("STEP2: Navigate to " + site4 + " 's Dashboard");
        siteDashboardPage.navigate(site4);
        assertEquals(siteDashboardPage.getSiteVisibility(), visibility, site4 + " 's visibility on Site Dashboard page=");
    }

    @TestRail (id = "C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToAlfrescoAdminGroup()
    {
        setupAuthenticatedSession(user2);

        LOG.info("STEP1: Verify presence of 'Admin Tools' in toolbar");
        assertFalse(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP2: User1 is added to ALFRESCO_ADMINISTRATORS group. User logs out and logins to share.");
        groupService.addUserToGroup(adminUser, adminPassword, alfrescoAdminGroup, user2.getUsername());
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2);
        assertTrue(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP3: Navigate to 'Admin Tools' from toolbar");
        toolbar.clickAdminTools();

        LOG.info("STEP4: Click 'Sites Manager' option from left side panel");
        adminToolsPage.navigateToNodeFromToolsPanel(language.translate("adminTools.sitesManagerNode"), sitesManagerPage);
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");
    }

    @TestRail (id = "C8682")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyUserIsAddedToSiteAdminGroup()
    {
        setupAuthenticatedSession(user3);

        LOG.info("STEP1: Verify presence of 'Sites Manager' in toolbar");
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP2: User is added to SITE_ADMINISTRATORS group. User logs out and logins to share.");
        groupService.addUserToGroup(adminUser, adminPassword, siteAdminGroup, user3.getUsername());
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user3);
        assertTrue(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP3: Click 'Sites Manager' from toolbar");
        toolbar.clickSitesManager();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");
    }

    @TestRail (id = "C8683")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void removeUserFromSiteAdmin()
    {
        setupAuthenticatedSession(user4);

        LOG.info("STEP1: Verify presence of 'Sites Manager' in toolbar");
        assertTrue(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");

        LOG.info("STEP2: Click 'Sites Manager' from toolbar");
        toolbar.clickSitesManager();
        assertEquals(sitesManagerPage.getPageTitle(), "Alfresco » Sites Manager", "Displayed page=");

        LOG.info("STEP3: User1 is removed from SITE_ADMINISTRATORS group. User1 logs out and logins to share.");
        groupService.removeUserFromGroup(adminUser, adminPassword, siteAdminGroup, user4.getUsername());
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user4);
        assertFalse(toolbar.isSitesManagerDisplayed(), "'Sites Manager' option is displayed in toolbar.");
    }

    @TestRail (id = "C8684")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void removeUserFromAlfrescoAdmin()
    {
        setupAuthenticatedSession(alfrescoAdmin, password);

        LOG.info("STEP1: Verify presence of 'Admin Tools' in toolbar");
        assertTrue(toolbar.isAdminToolsDisplayed(), "'Admin Tools' option is displayed in toolbar.");

        LOG.info("STEP2: Navigate to 'Admin Tools' from toolbar");
        toolbar.clickAdminTools();

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
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();
        sitesManagerPage.usingSite(site6).becomeSiteManager().assertSiteManagerIsYes();
    }

    @TestRail (id = "C8696")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void deleteSiteAsSiteAdmin()
    {
        setupAuthenticatedSession(siteAdmin);
        sitesManagerPage.navigate();

        LOG.info("STEP1: Click on \"Actions\" -> \"Delete\" button for \"siteA\"");
        sitesManagerPage.usingSite(site5).clickDelete();
        assertEquals(deleteSiteDialog.getConfirmMessageFromSitesManager(), String.format(language.translate("deleteSite.confirmFromSitesManager"), site5));

        LOG.info("STEP3: Confirm site deletion by clicking 'Ok' button");
        deleteSiteDialog.clickDeleteFromSitesManager();
        sitesManagerPage.waitForLoadingSitesMessageToDisappear();
        sitesManagerPage.usingSite(site5).assertSiteIsNotDisplayed();

        LOG.info("STEP4: Navigate by link to " + site5 + " 's dashboard");
        String url = properties.getShareUrl() + "/page/site/" + site5 + "/dashboard";
        getBrowser().navigate().to(url);
        assertEquals(systemErrorPage.getPageTitle(), "Alfresco Share » System Error", "Displayed page=");
    }
}