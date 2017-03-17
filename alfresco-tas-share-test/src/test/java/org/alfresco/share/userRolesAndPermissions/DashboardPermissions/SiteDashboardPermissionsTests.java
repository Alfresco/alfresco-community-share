package org.alfresco.share.userRolesAndPermissions.DashboardPermissions;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/24/2016.
 */
public class SiteDashboardPermissionsTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String userConsumer = "C8723ConsumerUser" + DataUtil.getUniqueIdentifier();
    private String userContributor = "C8724ContributorUser"+DataUtil.getUniqueIdentifier();
    private String userCollaborator = "C8725CollaboratorUser"+DataUtil.getUniqueIdentifier();
    private String userSiteManager = "C8726SiteManagerUser"+DataUtil.getUniqueIdentifier();
    private String description = "C8723SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C8723" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userConsumer, password, userConsumer + "@tests.com", userConsumer, userConsumer);
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + "@tests.com", userContributor, userContributor);
        userService.create(adminUser, adminPassword, userCollaborator, password, userCollaborator + "@tests.com", userCollaborator, userCollaborator);
        userService.create(adminUser, adminPassword, userSiteManager, password, userSiteManager + "@tests.com", userSiteManager, userSiteManager);
        siteService.create(adminUser, adminPassword, domain, siteName, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userConsumer, siteName, "SiteConsumer");
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");
        userService.createSiteMember(adminUser, adminPassword, userCollaborator, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, userSiteManager, siteName, "SiteManager");
    }

    @TestRail(id ="C8723")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
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

    @TestRail(id ="C8724")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})

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

    @TestRail(id ="C8725")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})

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

    @TestRail(id ="C8726")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})

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
