package org.alfresco.share;

import static org.alfresco.share.TestUtils.ALFRESCO_ADMIN_GROUP;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class ToolbarTests extends BaseTest
{
    private SiteDashboardPage siteDashboardPage;
    private UserProfilePage userProfilePage;
    private Toolbar toolbar;

    private final ThreadLocal<UserModel> normalUser = new ThreadLocal<>();

    @BeforeClass(alwaysRun = true)
    public void dataPrep()
    {
        normalUser.set(dataUser.usingAdmin().createRandomTestUser());
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteDashboardPage = new SiteDashboardPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        toolbar = new Toolbar(webDriver);
    }

    @TestRail (id = "C2091, C8701")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyAlfrescoToolbarItemsWithNormalUser()
    {
        authenticateUsingCookies(normalUser.get());
        userDashboardPage.navigate(normalUser.get());
        toolbar.assertToolbarIsDisplayed()
            .assertHomeIsDisplayed()
            .assertMyFilesIsDisplayed()
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
        toolbar.closeMenu();
        toolbar.clickTasks()
            .assertMyTasksIsDisplayed()
            .assertWorkflowIStartedIsDisplayed();
        toolbar.closeMenu();
        toolbar.clickUserMenu()
            .assertUserDashboardIsDisplayed()
            .assertMyProfileIsDisplayed()
            .assertHelpIsDisplayed()
            .assertSetCurrentPageAsHomeIsDisplayed()
            .assertUseDashboardAsHomeIsDisplayed()
            .assertLogoutIsDisplayed();
        if(dataAIS.isEnabled())
        {
            toolbar.clickUserMenu().assertChangePasswordIsNotDisplayed();
        }
        else
        {
            toolbar.clickUserMenu().assertChangePasswordIsDisplayed();
        }
    }

    @TestRail (id = "C2862")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void theToolbarIsAlwaysAvailableAtTheTopOfThePage()
    {
        authenticateUsingCookies(normalUser.get());
        userProfilePage.navigate(normalUser.get());
        toolbar.assertToolbarIsDisplayed();
    }

    @TestRail (id = "C2863, C8684, C8681")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void adminToolsAreAvailableOnlyForSystemAdministrators()
    {
        UserModel adminUser = dataUser.createRandomTestUser();
        dataGroup.usingUser(adminUser).addUserToGroup(ALFRESCO_ADMIN_GROUP);

        authenticateUsingLoginPage(adminUser);
        userDashboardPage.navigate(adminUser);
        toolbar.assertAdminToolsIsDisplayed()
            .clickAdminTools().assertAdminApplicationPageIsOpened();
        dataGroup.removeUserFromGroup(ALFRESCO_ADMIN_GROUP, adminUser);

        authenticateUsingLoginPage(adminUser);
        toolbar.assertAdminToolsIsNotDisplayed();

        dataUser.usingAdmin().deleteUser(adminUser);
    }

    // Should be re enabled after acs 7.0.0 is released
    @TestRail (id = "C2864")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER }, enabled = false)
    public void verifyTheLinksFromTheUserMenu()
    {
        authenticateUsingCookies(normalUser.get());
        userDashboardPage.navigate(normalUser.get());
        toolbar.clickUserMenu().clickUserDashboard()
            .assertUserDashboardPageIsOpened();

        toolbar.clickUserMenu().clickMyProfile()
            .assertUserProfilePageIsOpened();

        toolbar.clickUserMenu().clickHelp().assertHelpWillOpenDocumentationPage();

        userProfilePage.navigate(normalUser.get());
        toolbar.clickUserMenu().clickSetCurrentPageAsHome();
        toolbar.clickHome();
        userProfilePage.assertUserProfilePageIsOpened();

        toolbar.clickUserMenu().clickSetDashBoardAsHome();
        toolbar.clickHome();
        userDashboardPage.assertUserDashboardPageIsOpened();

        if(!dataAIS.isEnabled())
        {
            toolbar.clickUserMenu().clickChangePassword().assertChangePasswordPageIsOpened();
        }
        toolbar.clickUserMenu().clickLogout().assertLoginPageIsOpened();
    }

    @TestRail (id = "C2865")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTasksMenu()
    {
        authenticateUsingCookies(normalUser.get());
        userDashboardPage.navigate(normalUser.get());
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
        UserModel siteUser = dataUser.createRandomTestUser();

        SiteModel firstSite = dataSite.usingUser(siteUser).createPublicRandomSite();
        SiteModel secondSite = dataSite.usingUser(siteUser).createPublicRandomSite();
        authenticateUsingCookies(siteUser);
        siteDashboardPage.navigate(firstSite);
        siteDashboardPage.navigate(secondSite);

        userDashboardPage.navigate(siteUser);
        toolbar.clickSites()
            .assertRecentSitesSectionIsDisplayed()
            .assertSiteIsInRecentSites(firstSite)
            .assertSiteIsInRecentSites(secondSite)
                .clickRecentSite(firstSite).assertSiteDashboardPageIsOpened();

        toolbar.clickSites().clickMySites().assertSiteIsDisplayed(firstSite)
            .assertSiteIsDisplayed(secondSite);
        toolbar.clickSites().clickSiteFinder().assertSiteFinderPageIsOpened();

        toolbar.clickSites().clickCreateSite()
            .assertCreateSiteDialogIsDisplayed().clickCloseXButton();

        toolbar.clickSites().assertSiteIsFavorite(firstSite).assertSiteIsFavorite(secondSite);
        toolbar.clickSites().clickFavoriteSite(firstSite).assertSiteDashboardPageIsOpened();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        toolbar.clickSites().clickRemoveCurrentSiteFromFavorites();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsNotDisplayed();
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        dataSite.usingAdmin().deleteSite(firstSite);
        dataSite.usingAdmin().deleteSite(secondSite);
        dataUser.usingAdmin().deleteUser(siteUser);
    }

    @TestRail (id = "C2867")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromAlfrescoToolbar()
    {
        authenticateUsingCookies(normalUser.get());
        userDashboardPage.navigate(normalUser.get());
        toolbar.clickHome();
        userDashboardPage.assertUserDashboardPageIsOpened();

        toolbar.clickMyFiles().assertMyFilesPageIsOpened();
        toolbar.clickSharedFiles().assertSharedFilesPageIsOpened();
        toolbar.clickPeople().assertPeopleFinderPageIsOpened();
        toolbar.clickRepository().assertRepositoryPageIsOpened();
        toolbar.clickAdvancedSearch().assertAdvancedSearchPageIsOpened();
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(normalUser.get());
    }
}
