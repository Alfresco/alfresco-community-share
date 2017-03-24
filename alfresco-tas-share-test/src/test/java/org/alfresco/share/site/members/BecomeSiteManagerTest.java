package org.alfresco.share.site.members;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/1/2016.
 */
public class BecomeSiteManagerTest extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteUsersPage siteUsers;

    @Autowired
    SitesManagerPage sitesManager;

    private String uniqueIdentifier;
    private String user1;
    private String user2;
    private String siteName;

    @TestRail(id = "C2848")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionIsMissingForNonAdmins()
    {
        // preconditions
        uniqueIdentifier = "C2848-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        user2 = "testUser2-" + uniqueIdentifier;
        siteName = "SiteName-" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");

        setupAuthenticatedSession(user2, password);

        LOG.info("Navigate to '" + siteName + "' site's dashboard and click 'Site configuration options' icon.");
        siteDashboard.navigate(siteName);
        siteDashboard.clickSiteConfiguration();

        assertFalse(siteDashboard.isOptionListedInSiteConfigurationDropDown("Become Site Manager"),
                "'Become Site Manager' action should NOT be available in the 'Site Configuration Options' drop-down menu.");

    }

    @TestRail(id = "C2849")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionWhenAdminIsSiteManager()
    {
        // preconditions
        uniqueIdentifier = "C2849-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier + "-SiteName";

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
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
        assertFalse(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should NOT be displayed when clicking on 'Actions' button.");
        assertTrue(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Delete Site"),
                "'Delete Site' action should be displayed when clicking on 'Actions' button.");
    }

    @TestRail(id = "C2850")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerUsingSiteConfigurationIcon()
    {
        // preconditions
        uniqueIdentifier = "C2850-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = "SiteName-" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
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
        siteDashboard.clickOptionInSiteConfigurationDropDown("Become Site Manager", siteDashboard);
        siteDashboard.renderedPage();
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
    }

    @TestRail(id = "C2852")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionAvailableForAdmins()
    {
        // preconditions
        uniqueIdentifier = "0-C2852-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
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
        assertTrue(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should be displayed when clicking on 'Actions' button.");
    }

    @TestRail(id = "C2854")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerFromSitesManagerAdminIsSiteMember()
    {
        // preconditions
        uniqueIdentifier = "0-C2854-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, adminUser, siteName, "SiteCollaborator");

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 2: Verify \"I'm a Site Manager\" column.");
        assertFalse(sitesManager.isUserSiteManager(siteName), "'No' value is present in \"I'm a Site Manager\" column for '" + siteName + "'.");

        LOG.info("STEP 3: Click 'Actions' for '" + siteName + "'.");
        assertTrue(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should be displayed when clicking on 'Actions' button.");

        LOG.info("STEP 4: Click 'Become Site Manager' option then verify again \"I'm a Site Manager\" column.");
        sitesManager.clickActionForManagedSiteRow(siteName, "Become Site Manager", sitesManager);
        assertTrue(sitesManager.isUserSiteManager(siteName), "'Yes' value is present in \"I'm a Site Manager\" column for '" + siteName + "'.");

        LOG.info("STEP 5: Click again 'Actions' for '" + siteName + "'.");
        assertFalse(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should NOT be displayed when clicking on 'Actions' button.");

        LOG.info("STEP 6: Click on the site's name.");
        sitesManager.clickSiteNameLink(siteName);

        LOG.info("STEP 7: Verify the listed users: admin user is listed, with 'Manager' role.");
        assertTrue(siteUsers.isRoleSelected("Manager", adminName), "Admin user should be listed, with 'Manager' role.");

    }

    @TestRail(id = "C2856")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerActionAvailableForAdminNotSiteMember()
    {
        // preconditions
        uniqueIdentifier = "0-C2856-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);

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
        assertTrue(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should be displayed when clicking on 'Actions' button.");
    }

    @TestRail(id = "C2861")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerFromSitesManagerAdminIsNotSiteMember()
    {
        // preconditions
        uniqueIdentifier = "0-C2861-" + DataUtil.getUniqueIdentifier();
        user1 = "testUser1-" + uniqueIdentifier;
        siteName = uniqueIdentifier;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);

        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("STEP 1: Navigate 'Admin Tools' -> 'Sites Manager' page.");
        sitesManager.navigate();

        LOG.info("STEP 2: Verify \"I'm a Site Manager\" column.");
        assertFalse(sitesManager.isUserSiteManager(siteName), "'No' value is present in \"I'm a Site Manager\" column for '" + siteName + "'.");

        LOG.info("STEP 3: Click 'Actions' for '" + siteName + "'.");
        assertTrue(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should be displayed when clicking on 'Actions' button.");

        LOG.info("STEP 4: Click 'Become Site Manager' option then verify again \"I'm a Site Manager\" column.");
        sitesManager.clickActionForManagedSiteRow(siteName, "Become Site Manager", sitesManager);
        assertTrue(sitesManager.isUserSiteManager(siteName), "'Yes' value is present in \"I'm a Site Manager\" column for '" + siteName + "'.");

        LOG.info("STEP 5: Click again 'Actions' for '" + siteName + "'.");
        assertFalse(sitesManager.isActionAvailableForManagedSiteRow(siteName, "Become Site Manager"),
                "'Become Site Manager' action should NOT be displayed when clicking on 'Actions' button.");

        LOG.info("STEP 6: Click on the site's name.");
        sitesManager.clickSiteNameLink(siteName);

        LOG.info("STEP 7: Verify the listed users: admin user is listed, with 'Manager' role.");
        assertTrue(siteUsers.isRoleSelected("Manager", adminName), "Admin user should be listed, with 'Manager' role.");
    }
}
