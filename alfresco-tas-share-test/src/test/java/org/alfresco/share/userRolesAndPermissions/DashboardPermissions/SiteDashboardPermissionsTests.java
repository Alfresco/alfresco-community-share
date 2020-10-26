package org.alfresco.share.userRolesAndPermissions.DashboardPermissions;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/24/2016.
 */
public class SiteDashboardPermissionsTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String userConsumer = String.format("C8723ConsumerUser%s", RandomData.getRandomAlphanumeric());
    private String userContributor = "C8724ContributorUser" + RandomData.getRandomAlphanumeric();
    private String userCollaborator = "C8725CollaboratorUser" + RandomData.getRandomAlphanumeric();
    private String userSiteManager = "C8726SiteManagerUser" + RandomData.getRandomAlphanumeric();
    private String description = String.format("C8723SiteDescription%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("C8723%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userConsumer, password, userConsumer + domain, userConsumer, userConsumer);
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        userService.create(adminUser, adminPassword, userCollaborator, password, userCollaborator + domain, userCollaborator, userCollaborator);
        userService.create(adminUser, adminPassword, userSiteManager, password, userSiteManager + domain, userSiteManager, userSiteManager);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userConsumer, siteName, "SiteConsumer");
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");
        userService.createSiteMember(adminUser, adminPassword, userCollaborator, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, userSiteManager, siteName, "SiteManager");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userConsumer);
        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);
        userService.delete(adminUser, adminPassword, userCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userCollaborator);
        userService.delete(adminUser, adminPassword, userSiteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userSiteManager);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    @TestRail (id = "C8723")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void siteDashboardPermissionsConsumerRole()
    {
        setupAuthenticatedSession(userConsumer, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8724")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsContributorRole()
    {
        setupAuthenticatedSession(userContributor, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8725")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsCollaboratorRole()
    {
        setupAuthenticatedSession(userCollaborator, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8726")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsSiteManagerRole()
    {
        setupAuthenticatedSession(userSiteManager, password);
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");

        cleanupAuthenticatedSession();
    }
}
