package org.alfresco.share.site.members;

import static org.alfresco.po.enums.GroupRoles.MANAGER;
import static org.alfresco.po.share.site.SiteConfigurationOptions.BECOME_SITE_MANAGER;
import static org.alfresco.po.share.site.SiteConfigurationOptions.CUSTOMIZE_DASHBOARD;
import static org.alfresco.po.share.site.SiteConfigurationOptions.CUSTOMIZE_SITE;
import static org.alfresco.po.share.site.SiteConfigurationOptions.EDIT_SITE_DETAILS;
import static org.alfresco.po.share.site.SiteConfigurationOptions.JOIN_SITE;
import static org.alfresco.po.share.site.SiteConfigurationOptions.LEAVE_SITE;
import static org.alfresco.utility.constants.UserRole.SiteCollaborator;
import static org.alfresco.utility.constants.UserRole.SiteManager;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BecomeSiteManagerTest extends BaseTest
{
    private SiteDashboardPage siteDashboardPage;
    private SiteUsersPage siteUsersPage;
    private SitesManagerPage sitesManagerPage;

    private final ThreadLocal<UserModel> userModel = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteModel = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        userModel.set(dataUser.usingAdmin().createRandomTestUser());
        siteModel.set(dataSite.usingUser(userModel.get()).createPublicRandomSite());

        setupAuthenticatedSession(userModel.get());

        siteDashboardPage = new SiteDashboardPage(webDriver);
        sitesManagerPage = new SitesManagerPage(webDriver);
        siteUsersPage = new SiteUsersPage(webDriver);
    }

    @TestRail(id = "C2848")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void becomeSiteManagerActionShouldNotBeDisplayedForCollaboratorUser()
    {
        UserModel collaborator = dataUser.usingAdmin().createRandomTestUser();
        dataUser.usingUser(userModel.get())
            .addUserToSite(collaborator, siteModel.get(), SiteCollaborator);

        setupAuthenticatedSession(collaborator);

        siteDashboardPage
            .navigate(siteModel.get())
            .clickSiteConfiguration()
            .assertOptionNotEqualsTo(BECOME_SITE_MANAGER.getValue());
    }

    @TestRail(id = "C2849")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void becomeSiteManagerActionShouldNotBeDisplayedWhenAdminIsSiteManager()
    {
        dataUser.usingUser(userModel.get())
            .addUserToSite(getAdminUser(), siteModel.get(), SiteManager);

        setupAuthenticatedSession(getAdminUser());

        sitesManagerPage
            .navigate()
            .usingSite(siteModel.get())
            .openActionsDropDown()
            .assertDeleteSiteOptionIsDisplayed()
            .assertBecomeSiteManagerOptionIsNotDisplayed();
    }

    @TestRail(id = "C2850")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldBecomeSiteManagerUsingSiteConfigurationIcon()
    {
        dataUser.usingUser(userModel.get())
            .addUserToSite(getAdminUser(), siteModel.get(), SiteCollaborator);

        setupAuthenticatedSession(getAdminUser());

        siteDashboardPage
            .navigate(siteModel.get())
            .clickSiteConfiguration()
            .assertOptionEqualsTo(BECOME_SITE_MANAGER.getValue())
            .assertOptionEqualsTo(LEAVE_SITE.getValue());

        siteDashboardPage
            .assertOptionNotEqualsTo(CUSTOMIZE_DASHBOARD.getValue())
            .assertOptionNotEqualsTo(EDIT_SITE_DETAILS.getValue())
            .assertOptionNotEqualsTo(CUSTOMIZE_SITE.getValue());

        siteDashboardPage
            .selectOptionFromSiteConfigurationDropDown(BECOME_SITE_MANAGER.getValue())
            .waitUntilNotificationMessageDisappears();

        siteDashboardPage.clickSiteConfiguration();
        siteDashboardPage
            .assertOptionNotEqualsTo(BECOME_SITE_MANAGER.getValue())
            .assertOptionEqualsTo(CUSTOMIZE_DASHBOARD.getValue())
            .assertOptionEqualsTo(EDIT_SITE_DETAILS.getValue())
            .assertOptionEqualsTo(LEAVE_SITE.getValue());

        siteUsersPage
            .navigate(siteModel.get())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), getAdminUser().getFirstName());
    }

    @TestRail(id = "C2854")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void shouldBecomeSiteManagerFromSitesManagerWhenAdminIsSiteMember()
    {
        dataUser.usingUser(userModel.get())
            .addUserToSite(getAdminUser(), siteModel.get(), SiteCollaborator);

        setupAuthenticatedSession(getAdminUser());

        sitesManagerPage
            .navigate()
            .usingSite(siteModel.get())
            .assertSiteManagerIsNo()
            .becomeSiteManager()
            .assertSiteManagerIsYes()
            .assertBecomeSiteManagerOptionIsNotDisplayed();

        sitesManagerPage
            .usingSite(siteModel.get())
            .clickSiteName();

        siteUsersPage
            .navigate(siteModel.get())
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), getAdminUser().getFirstName());
    }

    @TestRail(id = "C2856")
    @Test(groups = {TestGroup.SANITY, TestGroup.SITES})
    public void becomeSiteManagerActionShouldBeDisplayedWhenAdminIsNotSiteMember()
    {
        setupAuthenticatedSession(getAdminUser());

        siteDashboardPage
            .navigate(siteModel.get())
            .clickSiteConfiguration();

        siteDashboardPage
            .assertOptionNotEqualsTo(BECOME_SITE_MANAGER.getValue())
            .assertOptionEqualsTo(JOIN_SITE.getValue());

        sitesManagerPage
            .navigate()
            .usingSite(siteModel.get())
            .openActionsDropDown()
            .assertBecomeSiteManagerOptionIsDisplayed();
    }

    @TestRail (id = "C2861")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void becomeSiteManagerFromSiteManagerPageWhenAdminIsNotSiteMember()
    {
        setupAuthenticatedSession(getAdminUser());
        sitesManagerPage.navigate();

        sitesManagerPage.usingSite(siteModel.get())
            .assertSiteManagerIsNo()
            .becomeSiteManager()
            .assertBecomeSiteManagerOptionIsNotDisplayed()
            .assertSiteManagerIsYes()
            .clickSiteName();

        siteUsersPage
            .assertSelectedRoleEqualsTo(MANAGER.getValue(), getAdminUser().getFirstName());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteSitesIfNotNull(siteModel.get());
        deleteUsersIfNotNull(userModel.get());
    }
}
