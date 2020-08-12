package org.alfresco.share;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ToolbarTests extends ContextAwareWebTest
{
    @Autowired
    private UserDashboardPage userDashboardPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private UserProfilePage userProfilePage;

    private UserModel normalUser, adminUser;

    @BeforeClass(alwaysRun = true)
    public void authenticateAdminUser()
    {
        normalUser = dataUser.usingAdmin().createRandomTestUser();
        adminUser = dataUser.createRandomTestUser();
        dataGroup.usingUser(adminUser).addUserToGroup(ALFRESCO_ADMIN_GROUP);
        setupAuthenticatedSession(normalUser);
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(normalUser, adminUser);
    }

    @TestRail (id = "C2091, C8701")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyAlfrescoToolbarItemsWithNormalUser()
    {
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
                    .assertFavoritesIsDisplayed()
                .clickTasks()
                    .assertMyTasksIsDisplayed()
                    .assertWorkflowIStartedIsDisplayed()
                .clickUserMenu()
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
        userProfilePage.navigate(normalUser);
        toolbar.assertToolbarIsDisplayed();
    }

    @TestRail (id = "C2863, C8684, C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void adminToolsAreAvailableOnlyForSystemAdministrators()
    {
        try
        {
            setupAuthenticatedSession(adminUser);
            toolbar.assertAdminToolsIsDisplayed()
                .clickAdminTools().assertAdminToolsPageIsOpened();
            dataGroup.removeUserFromGroup(ALFRESCO_ADMIN_GROUP, adminUser);
            setupAuthenticatedSession(adminUser);
            toolbar.assertAdminToolsIsNotDisplayed();
        }
        finally
        {
            setupAuthenticatedSession(normalUser);
        }
    }

    @TestRail (id = "C2864")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTheUserMenu()
    {
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
        SiteModel site1 = dataSite.usingUser(normalUser).createPublicRandomSite();
        SiteModel site2 = dataSite.usingUser(normalUser).createPublicRandomSite();
        siteDashboardPage.navigate(site1);
        siteDashboardPage.navigate(site2);

        userDashboardPage.navigate(normalUser);
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
