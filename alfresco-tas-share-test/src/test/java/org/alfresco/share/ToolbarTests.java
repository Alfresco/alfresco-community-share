package org.alfresco.share;

import static org.alfresco.share.ContextAwareWebTest.ALFRESCO_ADMIN_GROUP;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ToolbarTests extends BaseTest
{
    private SiteDashboardPage siteDashboardPage;
    private UserProfilePage userProfilePage;

    private UserModel normalUser, adminUser, siteUser;

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        normalUser = dataUser.usingAdmin().createRandomTestUser();
        adminUser = dataUser.createRandomTestUser();
        siteUser = dataUser.createRandomTestUser();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteDashboardPage = new SiteDashboardPage(browser);
        userProfilePage = new UserProfilePage(browser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(normalUser, adminUser, siteUser);
    }

    @TestRail (id = "C2091, C8701")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyAlfrescoToolbarItemsWithNormalUser()
    {
        setupAuthenticatedSession(normalUser);
        userDashboardPage.navigate(normalUser);
        toolbar.assertToolbarIsDisplayed()
            .assertHomeIsDisplayed()
            .assertMyFilesIsDisplayed()
            .assertSharedFilesIsDisplayed()
            .assertSitesIsDisplayed()
            .assertTasksIsDisplayed()
            .assertPeopleIsDisplayed()
            .assertRepositoryIsDisplayed()
            .assertUserMenuIsDisplayed()
            .assertSearchInputIsDisplayed()
            .assertAdminToolsIsNotDisplayed()
            .assertSitesManagerIsNotDisplayed()
                .clickSites()
                    .assertRecentSitesSectionIsNotDisplayed()
                    .assertUsefulSectionIsDisplayed()
                    .assertMySitesIsDisplayed()
                    .assertSiteFinderIsDisplayed()
                    .assertCreateSiteIsDisplayed()
                    .assertFavoritesIsDisplayed();
        toolbar.clickTasks()
            .assertMyTasksIsDisplayed()
            .assertWorkflowIStartedIsDisplayed();
        toolbar.clickUserMenu()
            .assertUserDashboardIsDisplayed()
            .assertMyProfileIsDisplayed()
            .assertHelpIsDisplayed()
            .assertSetCurrentPageAsHomeIsDisplayed()
            .assertUseDashboardAsHomeIsDisplayed()
            .assertChangePasswordIsDisplayed()
            .assertLogoutIsDisplayed();
    }

    @TestRail (id = "C2862")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void theToolbarIsAlwaysAvailableAtTheTopOfThePage()
    {
        setupAuthenticatedSession(normalUser);
        userProfilePage.navigate(normalUser);
        toolbar.assertToolbarIsDisplayed();
    }

    @TestRail (id = "C2863, C8684, C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void adminToolsAreAvailableOnlyForSystemAdministrators()
    {
        dataGroup.usingUser(adminUser).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        setupAuthenticatedSession(adminUser);
        userDashboardPage.navigate(adminUser);
        toolbar.assertAdminToolsIsDisplayed()
            .clickAdminTools().assertAdminApplicationPageIsOpened();
        dataGroup.removeUserFromGroup(ALFRESCO_ADMIN_GROUP, adminUser);
        setupAuthenticatedSession(adminUser);
        toolbar.assertAdminToolsIsNotDisplayed();
    }

    @TestRail (id = "C2864")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTheUserMenu()
    {
        setupAuthenticatedSession(normalUser);
        userDashboardPage.navigate(normalUser);
        toolbar.clickUserMenu().clickUserDashboard()
            .assertUserDashboardPageIsOpened();

        toolbar.clickUserMenu().clickMyProfile()
            .assertUserProfilePageIsOpened();

        toolbar.clickUserMenu().clickHelp().assertHelpWillOpenDocumentationPage();

        userProfilePage.navigate(normalUser);
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        toolbar.clickHome();
        userProfilePage.renderedPage();
        userProfilePage.assertUserProfilePageIsOpened();

        toolbar.clickUserMenu().clickSetDashBoardAsHome();
        toolbar.clickHome();
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();

        toolbar.clickUserMenu().clickChangePassword().assertChangePasswordPageIsOpened();
        toolbar.clickUserMenu().clickLogout().assertLoginPageIsOpened();
    }

    @TestRail (id = "C2865")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTasksMenu()
    {
        setupAuthenticatedSession(normalUser);
        userDashboardPage.navigate(normalUser);
        toolbar.clickTasks().clickMyTasks()
            .assertMyTasksPageIsOpened()
            .assertStartWorkflowIsDisplayed();

        toolbar.clickTasks().clickWorkflowsIStarted()
            .assertWorkflowIStartedPageIsOpened()
            .assertStartWorkflowIsDisplayed();
    }

    @TestRail (id = "C2866")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromSitesMenu()
    {
        SiteModel site1 = dataSite.usingUser(siteUser).createPublicRandomSite();
        SiteModel site2 = dataSite.usingUser(siteUser).createPublicRandomSite();
        setupAuthenticatedSession(siteUser);
        siteDashboardPage.navigate(site1);
        siteDashboardPage.navigate(site2);

        userDashboardPage.navigate(siteUser);
        toolbar.clickSites()
            .assertRecentSitesSectionIsDisplayed()
            .assertSiteIsInRecentSites(site1)
            .assertSiteIsInRecentSites(site2)
                .clickRecentSite(site1).assertSiteDashboardPageIsOpened();

        toolbar.clickSites().clickMySites().assertSiteIsDisplayed(site1)
            .assertSiteIsDisplayed(site2);
        toolbar.clickSites().clickSiteFinder().assertSiteFinderPageIsOpened();

        toolbar.clickSites().clickCreateSite()
            .assertCreateSiteDialogIsDisplayed().clickCloseXButton();

        toolbar.clickSites().assertSiteIsFavorite(site1).assertSiteIsFavorite(site2);
        toolbar.clickSites().clickFavoriteSite(site1).assertSiteDashboardPageIsOpened();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        toolbar.clickSites().clickRemoveCurrentSiteFromFavorites();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsNotDisplayed();
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        dataSite.usingAdmin().deleteSite(site1);
        dataSite.usingAdmin().deleteSite(site2);
    }

    @TestRail (id = "C2867")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromAlfrescoToolbar()
    {
        setupAuthenticatedSession(normalUser);
        userDashboardPage.navigate(normalUser);
        toolbar.clickHome();
        userDashboardPage.renderedPage();
        userDashboardPage.assertUserDashboardPageIsOpened();

        toolbar.clickMyFiles().assertMyFilesPageIsOpened();
        toolbar.clickSharedFiles().assertSharedFilesPageIsOpened();
        toolbar.clickPeople().assertPeopleFinderPageIsOpened();
        toolbar.clickRepository().assertRepositoryPageIsOpened();
        toolbar.clickAdvancedSearch().assertAdvancedSearchPageIsOpened();
    }
}
