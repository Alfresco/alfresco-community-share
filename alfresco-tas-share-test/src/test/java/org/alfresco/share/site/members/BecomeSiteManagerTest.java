package org.alfresco.share.site.members;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/1/2016.
 */
public class BecomeSiteManagerTest extends ContextAwareWebTest
{
   // @Autowired
    SiteDashboardPage siteDashboard;

   // @Autowired
    SiteUsersPage siteUsers;

    //@Autowired
    SitesManagerPage sitesManager;

    private String uniqueIdentifier;
    private String user1;
    private String user2;
    private String siteName;

    @TestRail (id = "C2848")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionIsMissingForNonAdmins()
    {
        // preconditions
        uniqueIdentifier = String.format("C2848-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        user2 = "testUser2-" + uniqueIdentifier;
        siteName = "SiteName-" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");

        setupAuthenticatedSession(user2, password);

        LOG.info("Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should NOT be available in the 'Site Configuration Options' drop-down menu.");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C2849")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionWhenAdminIsSiteManager()
    {
        // preconditions
        uniqueIdentifier = String.format("C2849-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier + "-SiteName";

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, adminUser, siteName, "SiteManager");

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should NOT be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"),
            "'Customize Dashboard' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Edit Site Details"),
            "'Edit Site Details' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize Site"),
            "'Customize Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Leave Site"),
            "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 2: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 3: Click 'Actions' for '" + siteName + "'. 'Become Site Manager' action is not available. Only 'Delete Site' action is available.");
        sitesManager.usingSite(siteName).assertBecomeManagerOptionIsNotAvailable()
            .assertDeleteSiteOptionIsAvailable();

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2850")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerUsingSiteConfigurationIcon()
    {
        // preconditions
        uniqueIdentifier = String.format("C2850-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = "SiteName-" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, adminUser, siteName, "SiteCollaborator");

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Leave Site"),
            "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"),
            "'Customize Dashboard' action should NOT be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Edit Site Details"),
            "'Edit Site Details' action should NOT be available in the 'Site Configuration Options' drop-down menu.");
        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize site"),
            "'Customize site' action should NOT be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 2: Click on 'Become Site Manager' action. Click again 'Site configuration options' icon.");
        siteDashboard.clickOptionInSiteConfigurationDropDown("Become Site Manager");
        siteDashboard.waitUntilNotificationMessageDisappears();
        siteDashboard.clickSiteConfiguration();

        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should NOT be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"),
            "'Customize Dashboard' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Edit Site Details"),
            "'Edit Site Details' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Customize Site"),
            "'Customize Site' action should be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Leave Site"),
            "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 3: Click 'Site Members' link.");
        siteUsers.navigate(siteName);

        assertTrue(siteUsers.isRoleSelected("Manager", adminName), "Admin user should be listed, with 'Manager' role.");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2852")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionAvailableForAdmins()
    {
        // preconditions
        uniqueIdentifier = String.format("0-C2852-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, adminUser, siteName, "SiteCollaborator");

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 2: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 3: Click 'Actions' for '" + siteName + "'. 'Become Site Manager' action is available.");
        sitesManager.usingSite(siteName).assertBecomeManagerOptionIsAvailable();
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2854")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerFromSitesManagerAdminIsSiteMember()
    {
        // preconditions
        uniqueIdentifier = String.format("0-C2854-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, adminUser, siteName, "SiteCollaborator");

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 2: Verify \"I'm a Site Manager\" column.");
        sitesManager.usingSite(siteName).assertSiteManagerIsNo()
            .becomeSiteManager()
            .assertSiteManagerIsYes()
            .assertBecomeManagerOptionIsNotAvailable();

        LOG.info("STEP 6: Click on the site's name.");
        sitesManager.usingSite(siteName).clickSiteName();

        LOG.info("STEP 7: Verify the listed users: admin user is listed, with 'Manager' role.");
        assertTrue(siteUsers.isRoleSelected("Manager", adminName), "Admin user should be listed, with 'Manager' role.");
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2856")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionAvailableForAdminNotSiteMember()
    {
        // preconditions
        uniqueIdentifier = String.format("0-C2856-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
            "'Become Site Manager' action should NOT be available in the 'Site Configuration Options' drop-down menu.");
        assertTrue(siteDashboard.isOptionListedInSiteConfigurationDropDown("Join Site"),
            "'Leave Site' action should be available in the 'Site Configuration Options' drop-down menu.");

        LOG.info("STEP 2: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 3: Click 'Actions' for '" + siteName + "'. 'Become Site Manager' action is available.");
        sitesManager.usingSite(siteName).assertBecomeManagerOptionIsAvailable();

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2861")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerFromSitesManagerAdminIsNotSiteMember()
    {
        // preconditions
        uniqueIdentifier = String.format("0-C2861-%s", RandomData.getRandomAlphanumeric());
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 2: Verify \"I'm a Site Manager\" column.");
        sitesManager.usingSite(siteName)
            .assertSiteManagerIsNo()
            .becomeSiteManager()
            .assertBecomeManagerOptionIsNotAvailable()
            .assertSiteManagerIsYes()
            .clickSiteName();

        LOG.info("STEP 7: Verify the listed users: admin user is listed, with 'Manager' role.");
        assertTrue(siteUsers.isRoleSelected("Manager", adminName), "Admin user should be listed, with 'Manager' role.");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        siteService.delete(adminUser, adminPassword, siteName);
    }
}
