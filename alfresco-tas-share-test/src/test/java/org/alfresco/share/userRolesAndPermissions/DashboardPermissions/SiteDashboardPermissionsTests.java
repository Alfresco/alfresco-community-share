package org.alfresco.share.userRolesAndPermissions.DashboardPermissions;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.site.SiteDashboardPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Mirela Tifui on 11/24/2016.
 */
public class SiteDashboardPermissionsTests extends BaseTest
{
    SiteDashboardPage siteDashboardPage;
    @Autowired
    UserService userService;
    private final ThreadLocal<UserModel> userConsumer = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userContributor = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userCollaborator = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userSiteManager = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition:Create test users");
        userConsumer.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userContributor.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userCollaborator.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        userSiteManager.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userConsumer.get().getUsername(), siteName.get().getId(), "SiteConsumer");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userContributor.get().getUsername(), siteName.get().getId(), "SiteContributor");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userCollaborator.get().getUsername(), siteName.get().getId(), "SiteCollaborator");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), userSiteManager.get().getUsername(), siteName.get().getId(), "SiteManager");

        siteDashboardPage = new SiteDashboardPage(webDriver);

    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userConsumer.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userContributor.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userCollaborator.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userSiteManager.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(userConsumer.get());
        deleteUsersIfNotNull(userContributor.get());
        deleteUsersIfNotNull(userCollaborator.get());
        deleteUsersIfNotNull(userSiteManager.get());

    }


    @TestRail (id = "C8723")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void siteDashboardPermissionsConsumerRole()
    {
        authenticateUsingLoginPage(userConsumer.get());
        siteDashboardPage.navigate(siteName.get());

        log.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");

    }

    @TestRail (id = "C8724")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsContributorRole()
    {
        authenticateUsingLoginPage(userContributor.get());
        siteDashboardPage.navigate(siteName.get());

        log.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");
    }

    @TestRail (id = "C8725")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsCollaboratorRole()
    {
        authenticateUsingLoginPage(userCollaborator.get());
        siteDashboardPage.navigate(siteName.get());

        log.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.openSiteConfiguration();
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertFalse(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Delete Site"), "Delete Site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");
    }

    @TestRail (id = "C8726")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })

    public void siteDashboardPermissionsSiteManagerRole()
    {
        authenticateUsingLoginPage(userSiteManager.get());
        siteDashboardPage.navigate(siteName.get());

        log.info("Step 1: Mouse over site configuration option and check available options");
        siteDashboardPage.openSiteConfiguration();
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Dashboard"), "Customize dashboard is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Edit Site Details"), "Edit site details is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Customize Site"), "Customize site is displayed");
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Leave Site"), "Leave Site is not displayed");
    }
}
