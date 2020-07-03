package org.alfresco.share;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ToolbarTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    Toolbar toolbar;

    @Autowired
    ToolbarSitesMenu toolbarSitesMenu;

    @Autowired
    ToolbarTasksMenu toolbarTasksMenu;

    @Autowired
    ToolbarUserMenu toolbarUserMenu;

    @Autowired
    SitesManagerPage sitesManagerPage;

    @Autowired
    LoginPage loginPage;

    @Autowired
    UserProfilePage userProfilePage;

    @Autowired
    ChangePasswordPage changePasswordPage;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    UserSitesListPage userSitesListPage;

    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    CreateSiteDialog createSiteDialog;

    @Autowired
    PeopleFinderPage peopleFinderPage;

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    AdminToolsPage adminToolsPage;

    @Autowired
    AdvancedSearchPage advancedSearchPage;

    @Autowired
    SharedFilesPage sharedFilesPage;


    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C2091")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyAlfrescoToolbarItems()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Verify Alfresco toolbar");
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        LOG.info("STEP 2 - Verify \"Sites\" menu from Alfresco toolbar");
        Assert.assertTrue(toolbarSitesMenu.isRecentSitesSectionDisplayed(), "\"Recent\" Sites");
        Assert.assertTrue(toolbarSitesMenu.isUsefulSectionDisplayed(), "\"Useful\" is displayed");
        Assert.assertTrue(toolbarSitesMenu.isMySitesDisplayed(), "\"My Sites\" is displayed");
        Assert.assertTrue(toolbarSitesMenu.isSiteFinderDisplayed(), "\"Site Finder\" is displayed");
        Assert.assertTrue(toolbarSitesMenu.isCreateSiteDisplayed(), "\"Create site\" is displayed");
        Assert.assertTrue(toolbarSitesMenu.isFavoritesDisplayed(), "\"Favorites\" is displayed");

        LOG.info("STEP 3 - Verify \"Tasks\"menu");
        Assert.assertTrue(toolbarTasksMenu.isMyTasksDisplayed(), "\"My Tasks\" is displayed");
        Assert.assertTrue(toolbarTasksMenu.isWorkflowsIveStartedDisplayed(), "\"Workflows I've Started\" is displayed");

        LOG.info("STEP 4 - Verify \"User\" menu");
        Assert.assertTrue(toolbarUserMenu.isUserDashboardDisplayed(), "\"User Dashboard \"is displayed");
        Assert.assertTrue(toolbarUserMenu.isMyProfileDisplayed(), "\"My Profile\" is displayed");
        Assert.assertTrue(toolbarUserMenu.isHelpDisplayed(), "\"Help\" is displayed");
        Assert.assertTrue(toolbarUserMenu.isSetCurrentPageAsHomeDisplayed(), "\"Use Current Page\" is displayed");
        Assert.assertTrue(toolbarUserMenu.isDashBoardAsHomeDisplayed(), "\"Use My Dashboard\" is displayed");
        Assert.assertTrue(toolbarUserMenu.isChangePasswordDisplayed(), "\"Change password\" is displayed");
        Assert.assertTrue(toolbarUserMenu.isLogoutDisplayed(), "\"Logout\" is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2862")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void theToolbarIsAlwaysAvailableAtTheTopOfThePage()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Verify Alfresco toolbar");
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        LOG.info("STEP 2 - Navigate to any other page from Share");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(toolbar.isToolbarDisplayed(), "\"Alfresco toolbar\" is displayed");
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2863")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void adminToolsAreAvailableOnlyForSystemAdministrators()
    {
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userName1);
        setupAuthenticatedSession(userName2, password);

        LOG.info("STEP 1 - Verify the links present on Alfresco Toolbar");
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertFalse(toolbar.isAdminToolsDisplayed(), "\"Admin Tools\" isn't displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        LOG.info("STEP 2 - Logout and login as user1");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        userDashboardPage.navigate(userName1);

        LOG.info("STEP 3 - Verify the links present on Alfresco Toolbar");
        myFilesPage.navigate();
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertTrue(toolbar.isAdminToolsDisplayed(), "\"Admin Tools\" is displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);

        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

    }

    //this test will fail if we are testing a non released version
    @TestRail (id = "C2864")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTheUserMenu()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Go to Alfresco Toolbar. Click on the \"User menu\" -> \"User Dashboard\" link");
        toolbarUserMenu.clickUserDashboard();
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 2 - Click on the \"User menu\" -> \"My Profile\" link");
        userProfilePage.navigateByMenuBar();
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");

        LOG.info("STEP 3 - Click on the \"User menu\" -> \"Help\" link");
        toolbarUserMenu.clickHelp();

        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains("https://docs.alfresco.com/", 5);
        Assert.assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        getBrowser().closeWindowAndSwitchBack();

        LOG.info("STEP 4 - Go to any other page from Share. Click on the \"User menu\" -> \"Use Current Page\" option");
        userProfilePage.navigate(userName);
        toolbarUserMenu.clickSetCurrentPageAsHome();
        toolbarUserMenu.clickHome();
        Assert.assertTrue(userProfilePage.isAboutHeaderDisplayed(), "\"About\" header is displayed");

        LOG.info("STEP 5 - Click on the \"User menu\" -> \"Use My Dashboard\" option");
        toolbarUserMenu.clickSetDashBoardAsHome();
        toolbarUserMenu.clickHome();
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 6 - Click on the \"User menu\" -> \"Change Password\" option");
        changePasswordPage.navigateByMenuBar();
        Assert.assertTrue(changePasswordPage.isOldPasswordInputDisplayed(), "Old password input is displayed");

        LOG.info("STEP 7 - Click on the \"User menu\" -> \"Logout\" option");
        toolbarUserMenu.clickLogout();
        Assert.assertTrue(loginPage.isCopyrightDisplayed(), "\"Copyright\" is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2865")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromTasksMenu()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Go to Alfresco Toolbar. Click on the \"Tasks\" menu -> \"My Tasks\" link");
        myTasksPage.navigateByMenuBar();
        Assert.assertTrue(myTasksPage.isStartWorkflowDisplayed(), "\"Start Workflow\" is displayed");

        LOG.info("STEP 2 - Click on the \"Tasks\" menu -> \"Workflows I've started\" link");
        workflowsIveStartedPage.navigateByMenuBar();
        Assert.assertTrue(workflowsIveStartedPage.isStartWorkflowDisplayed(), "\"Start Workflow\" is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2866")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromSitesMenu()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        String siteName2 = String.format("Site2%s", RandomData.getRandomAlphanumeric());
        String siteName3 = String.format("Site3%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName1, "description", SiteService.Visibility.PUBLIC);
        siteService.setFavorite(userName, password, siteName1);
        siteService.create(userName, password, domain, siteName2, "description", SiteService.Visibility.PUBLIC);
        siteService.setFavorite(userName, password, siteName2);
        siteService.create(userName, password, domain, siteName3, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName1);
        siteDashboardPage.navigate(siteName2);
        siteDashboardPage.navigate(siteName3);

        LOG.info("STEP 1 - Click on \"Sites\" menu from Alfresco Toolbar. Verify \"Recent Sites\" section");
        Assert.assertTrue(toolbarSitesMenu.isSiteInRecentSites(siteName1), siteName1 + " is displayed in Recent Sites");
        Assert.assertTrue(toolbarSitesMenu.isSiteInRecentSites(siteName2), siteName2 + " is displayed in Recent Sites");
        Assert.assertTrue(toolbarSitesMenu.isSiteInRecentSites(siteName3), siteName3 + " is displayed in Recent Sites");

        LOG.info("STEP 2 - Click on \"site1\"");
        toolbarSitesMenu.clickRecentSite(siteName1);
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName1);

        LOG.info("STEP 3 - Click again on \"Sites\" menu. Click on \"My Sites\" link");
        toolbarSitesMenu.clickMySites();
        Assert.assertTrue(userSitesListPage.isSitePresent(siteName1), siteName1 + " is present");

        LOG.info("STEP 4 - Click again on \"Sites\" menu. Click on \"Site Finder\" link");
        toolbarSitesMenu.clickSiteFinder();
        Assert.assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search field is displayed");

        LOG.info("STEP 5 - Click again on \"Sites\" menu. Click on \"Create Site\" link");
        createSiteDialog.navigateByMenuBar();
        Assert.assertTrue(createSiteDialog.isDescriptionInputFieldDisplayed(), "Description input is displayed");
        createSiteDialog.clickCloseXButton();

        LOG.info("STEP 6 - Close \"Create Site\" form. Click again on \"Sites\" menu. Click on \"Favorites\" link");
        Assert.assertTrue(toolbarSitesMenu.isSiteFavorite(siteName1), siteName1 + " is favorite");
        Assert.assertTrue(toolbarSitesMenu.isSiteFavorite(siteName2), siteName2 + " is favorite");

        LOG.info("STEP 7 - Click on \"site1\" link");
        toolbarSitesMenu.clickFavoriteSite(siteName1);
        siteDashboardPage.renderedPage();
        Assert.assertEquals(siteDashboardPage.getPageHeader(), siteName1);

        LOG.info("STEP 8 - Verify again the \"Sites\" menu");
        Assert.assertTrue(toolbarSitesMenu.isRemoveCurrentSiteFromFavoritesDisplayed());

        LOG.info("STEP 9 - Click on \"Remove current site from Favorites\" option");
        toolbarSitesMenu.clickRemoveCurrentSiteFromFavorites();
        Assert.assertFalse(toolbarSitesMenu.isRemoveCurrentSiteFromFavoritesDisplayed(), "\"Remove current site from Favorites\" isn't displayed");
        Assert.assertTrue(toolbarSitesMenu.isAddCurrentSiteToFavoritesDisplayed(), "\"Add current site to Favorites\" is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);
        siteService.delete(adminUser, adminPassword, siteName3);

    }

    @TestRail (id = "C2867")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void verifyTheLinksFromAlfrescoToolbar()
    {
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Click on \"Home\" link from Alfresco Toolbar");
        toolbar.clickHome();
        Assert.assertTrue(userDashboardPage.getPageHeader().contains(userName), "Page header");

        LOG.info("STEP 2 - Click on \"My Files\" link from Alfresco Toolbar");
        myFilesPage.navigateByMenuBar();
        Assert.assertTrue(myFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 3 - Click on \"Shared Files\" link from Alfresco Toolbar");
        sharedFilesPage.navigateByMenuBar();
        Assert.assertTrue(sharedFilesPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 4 - Click on \"People\" link from Alfresco Toolbar");
        peopleFinderPage.navigateByMenuBar();
        Assert.assertTrue(peopleFinderPage.isSearchButtonDisplayed(), "Search button is displayed");

        LOG.info("STEP 5 - Click on \"Repository\" link from Alfresco Toolbar");
        repositoryPage.navigateByMenuBar();
        Assert.assertTrue(repositoryPage.isUploadButtonDisplayed(), "Upload button is displayed");

        LOG.info("STEP 6 - Click on \"Admin Tools\" link from Alfresco Toolbar");
        adminToolsPage.navigateByMenuBar();
        Assert.assertTrue(adminToolsPage.isAdminToolsDivDisplayed(), "Admin Tools is displayed");

        LOG.info("STEP 7 - Click on \"Search\" icon -> \"Advanced Search...\" link");
        advancedSearchPage.navigateByMenuBar();
        Assert.assertTrue(advancedSearchPage.isKeywordsSearchFieldDisplayed(), "Keywords search field is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);


    }

    @TestRail (id = "C2868")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void siteManagerIsAvailableOnlyForSiteAdministrators()
    {
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        groupService.addUserToGroup(adminUser, adminPassword, "SITE_ADMINISTRATORS", userName1);
        setupAuthenticatedSession(userName2, password);

        LOG.info("STEP 1 - Verify the links present on Alfresco Toolbar");
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertFalse(toolbar.isSitesManagerDisplayed(), "\"Sites manager\" isn't displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        LOG.info("STEP 2 - Logout and login as user1");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userName1, password);
        userDashboardPage.navigate(userName1);

        LOG.info("STEP 3 - Verify the links present on Alfresco Toolbar");
        Assert.assertTrue(toolbar.isHomeDisplayed(), "\"Home\" is displayed");
        Assert.assertTrue(toolbar.isMyFilesDisplayed(), "\"My\" Files is displayed");
        Assert.assertTrue(toolbar.isSharedFilesDisplayed(), "\"Shared Files\" is displayed");
        Assert.assertTrue(toolbar.isSitesDisplayed(), "\"Sites\" is displayed");
        Assert.assertTrue(toolbar.isTasksDisplayed(), "\"Tasks\" is displayed");
        Assert.assertTrue(toolbar.isPeopleDisplayed(), "\"People\" is displayed");
        Assert.assertTrue(toolbar.isRepositoryDisplayed(), "\"Repository\" is displayed");
        Assert.assertTrue(toolbar.isSitesManagerDisplayed(), "\"Sites manager\" isn't displayed");
        Assert.assertTrue(toolbar.isUserMenuDisplayed(), "\"User menu\" is displayed");
        Assert.assertTrue(toolbar.isSearchBoxDisplayed(), "\"Search\" is displayed");

        LOG.info("STEP 4 - Click on \"Sites Manager\" link");
        sitesManagerPage.navigateByMenuBar();
        Assert.assertTrue(sitesManagerPage.isSitesTableDisplayed(), "Sites table is displayed");

        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
    }
}
