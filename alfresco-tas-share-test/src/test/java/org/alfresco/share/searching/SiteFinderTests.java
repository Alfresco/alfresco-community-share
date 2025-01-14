package org.alfresco.share.searching;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UserProfileAdminToolsPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
@Slf4j

/**
 * @author Laura.Capsa
 */
public class SiteFinderTests extends BaseTest
{
    //@Autowired
    SiteFinderPage siteFinderPage;

    //@Autowired
    UserDashboardPage userDashboardPage;
    Toolbar toolbar;
    UserProfileAdminToolsPage userProfileAdminToolsPage;
    SearchPage searchPage;
    UserProfilePage userProfilePage;
    DocumentDetailsPage documentDetailsPage;
    CreateContentPage createContent;
    SiteDashboardPage siteDashboardPage;
    EditUserPage editUserPage;
    DocumentLibraryPage documentLibraryPage;
    private UserModel testUser1;
    private UserModel testUser2;

    private SiteModel testSite1;
    private SiteModel testSite2;



    String userFirstName = "firstName";
    String userLastName = "lastName";
    String user2LastName = "lastName2";

    String userName = "test user";

    String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        editUserPage = new EditUserPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        siteFinderPage = new SiteFinderPage(webDriver);
        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        userProfileAdminToolsPage = new UserProfileAdminToolsPage(webDriver);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(getAdminUser());
    }

    @AfterMethod
    public void testCleanup()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
        deleteSitesIfNotNull(testSite1);
        deleteSitesIfNotNull(testSite2);
    }


    @TestRail (id = "C5876")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void siteFinderPage()
    {
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        log.info("Edit user data & give specific name to " + testUser1);
        UserModel editUser1 = testUser1;
        editUserPage.navigate(editUser1)
            .editFirstName(userFirstName)
            .editLastNameField(userLastName)
            .clickSaveChanges();
        log.info("Edit user data & give specific name to " + testUser2);
        UserModel editUser2 = testUser2;
        editUserPage.navigate(editUser2)
            .editFirstName(userFirstName)
            .editLastNameField(user2LastName)
            .clickSaveChanges();
        testSite1 = dataSite.usingUser(testUser1).createModeratedRandomSite();
        testSite2 = dataSite.usingUser(testUser1).createPrivateRandomSite();
        log.info("Edit site data & give specific name to " + testSite1);
        authenticateUsingLoginPage(testUser1);
        SiteModel editSite1 = testSite1;
        siteDashboardPage.navigateToEditSiteDetailsDialog(editSite1.getId());
        siteDashboardPage.editSiteDescription(description);
        documentLibraryPage.navigateToDocumentLibraryPage();
        authenticateUsingLoginPage(testUser1);
        SiteModel editSite2 = testSite2;
        siteDashboardPage.navigateToEditSiteDetailsDialog(editSite2.getId());
        siteDashboardPage.editSiteDescription(description);
        authenticateUsingLoginPage(testUser1);
        siteFinderPage.navigate();
        log.info("STEP1: Verify page title");
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Finder", "Page title");
        log.info("STEP2: Verify search section");
        assertTrue(siteFinderPage.isSearchFieldDisplayed(), "Search input field is displayed");
        assertTrue(siteFinderPage.isSearchButtonDisplayed(), "Search button is displayed");
        assertEquals(siteFinderPage.getSearchMessage(), language.translate("siteFinder.helpMessage"), "Help message");

        log.info("STEP3: Fill in search field and click \"Search\" button");
        siteFinderPage.searchSiteWithName(testSite1.getTitle());
        assertTrue(siteFinderPage.isSearchResultsListDisplayed(), "Search results list contains list of sites");
        assertTrue(siteFinderPage.isSiteNameListDisplayed(), "Search results list contains site title");
        assertTrue(siteFinderPage.isSiteDescriptionListDisplayed(), "Search results list contains site description");
        assertTrue(siteFinderPage.isSiteVisibilityListDisplayed(), "Search results list contains site visibility");
        assertTrue(siteFinderPage.isButtonDisplayedForSite(testSite1.getTitle(), "Leave"), "Leave button displayed for site " + testSite1.getTitle());
        assertTrue(siteFinderPage.isButtonDisplayedForSite(testSite1.getTitle(), "Delete"), "Delete button displayed for site " + testSite1.getTitle());

        log.info("STEP4: Click a site link from the search results");
        siteFinderPage.accessSite(testSite1.getTitle());
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Dashboard", "User is redirected to " + testSite1.getTitle() + " site dashboard page ");
    }

    @TestRail (id = "C7574")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void usernameWithSpaceCanAccessSiteFinder()
    {
        testUser1 = dataUser.usingAdmin().usingUserHome(userName).createRandomTestUser();
        log.info("Edit user data & give specific name to " + testUser1);
        authenticateUsingCookies(testUser1);
        log.info("STEP1: Click \"Sites\" -> \"Site Finder\" link from the toolbar");
        toolbar.clickSites().clickSiteFinder();
        assertEquals(siteFinderPage.getPageTitle(), "Alfresco » Site Finder", "Site Finder page is displayed");
    }

    @TestRail (id = "C5814")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void fullOrPartialSiteName()
    {
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testSite1 = dataSite.usingUser(testUser1).createModeratedRandomSite();
        authenticateUsingCookies(testUser1);
        siteFinderPage.navigate();

        log.info("STEP1: Enter the partial site name in the search field and click the search button");
        siteFinderPage.searchSiteName(testSite1.getTitle().substring(0, 17));
        assertTrue(siteFinderPage.checkSiteWasFound(testSite1.getTitle()), "Site " + testSite1.getTitle() + " is displayed in search result section");

        log.info("STEP2: Enter the full site name in the search field and click the search button");
        siteFinderPage.searchSiteName(testSite1.getTitle());
        assertTrue(siteFinderPage.checkSiteWasFound(testSite1.getTitle()), "Site " + testSite1.getTitle() + " is displayed in search result section");
    }

    @TestRail (id = "C7169")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void moderatedSiteLabel()
    {
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testSite1 = dataSite.usingUser(testUser1).createModeratedRandomSite();
        authenticateUsingCookies(testUser1);
        siteFinderPage.navigate();

        log.info("STEP1: Enter the moderated site's name into the search field and click the search button");
        siteFinderPage.searchSiteName(testSite1.getTitle());
        assertTrue(siteFinderPage.checkSiteWasFound(testSite1.getTitle()), "Site " + testSite1.getTitle() + "is displayed in search result section");
        assertEquals(siteFinderPage.getVisibilityLabel(), "Moderated", " \"Moderated\" label is displayed below " + testSite1.getTitle() + " site");
    }

    @TestRail (id = "C7195")
    @Test (groups = { TestGroup.SANITY, TestGroup.SEARCH })
    public void privateSiteLabel()
    {
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        testSite2 = dataSite.usingUser(testUser1).createPrivateRandomSite();
        authenticateUsingCookies(testUser1);
        siteFinderPage.navigate();

        log.info("STEP1: Enter the private site's name into the search field and click the search button");
        siteFinderPage.searchSiteName(testSite2.getTitle());
        assertTrue(siteFinderPage.checkSiteWasFound(testSite2.getTitle()), "Site " + testSite2.getTitle() + "is displayed in search result section");
        assertEquals(siteFinderPage.getVisibilityLabel(), "Private", " \"Private\" label is displayed below " + testSite2.getTitle() + " site");

        log.info("STEP2: Logout and log in as user2");
        authenticateUsingCookies(testUser2);
        log.info("STEP3: Open \"Site Finder\" page");
        siteFinderPage.navigate();

        log.info("STEP1: Enter the private site's name into the search field and click the search button");
        siteFinderPage.searchSiteName(testSite2.getTitle());
        assertEquals(siteFinderPage.getSearchMessage(), language.translate("siteFinder.noResults"), "Displayed message:");
        assertFalse(siteFinderPage.checkSiteWasFound(testSite2.getTitle()), "No results displayed in search result section");
        authenticateUsingCookies(testUser1);

    }
}
