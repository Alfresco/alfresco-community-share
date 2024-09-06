package org.alfresco.share.searching;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.LiveSearchPageSupport;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

/**
 * Created by Mirela Tifui on 2/16/2018.
 */
public class LiveSearchTests extends BaseTest
{
    //@Autowired
    Toolbar toolbar;
    //@Autowired
    SiteDashboardPage siteDashboardPage;
    EditUserPage editUserPage;
    //@Autowired
    DocumentLibraryPage documentLibraryPage;
    //@Autowired
    UserDashboardPage userDashboardPage;
    LiveSearchPageSupport liveSearchPageSupport;
    UserProfilePage userProfilePage;

    //@Autowired
    DocumentDetailsPage documentDetailsPage;
    CreateContentPage createContent;
    //@Autowired
    SearchPage searchPage;
    private UserModel testUser;

    private SiteModel testSite;

    private String userName = "testUser" + RandomData.getRandomAlphanumeric();

    private String siteName = "siteName" + RandomData.getRandomAlphanumeric();
    private String docName = "docName" + RandomData.getRandomAlphanumeric();

    @BeforeMethod (alwaysRun = true)
    public void testSetup()
    {
        editUserPage = new EditUserPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        liveSearchPageSupport = new LiveSearchPageSupport(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingLoginPage(getAdminUser());
        testUser = dataUser.usingAdmin().createRandomTestUser();
        log.info("Edit user data & give specific name to " + testUser);
        UserModel editUser = testUser;
        String firstName = "faceted";
        editUserPage.navigate(editUser)
            .editFirstName(firstName)
            .clickSaveChanges();
    }

    @AfterMethod (alwaysRun = true)
    public void testCleanup()
    {
        deleteUsersIfNotNull(testUser);
        deleteSitesIfNotNull(testSite);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE })
    public void testCheckNoLiveSearchResults()
    {
        log.info("Step 1: Search for info that does not exist and check that results are not displayed in search results");
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        log.info("Edit site data & give specific name to " + testSite);
        authenticateUsingLoginPage(testUser);
        SiteModel editSite = testSite;
        String description = "FacetedSearchSite";
        siteDashboardPage.navigateToEditSiteDetailsDialog(editSite.getId());
        siteDashboardPage.editSiteDescription(description);
        documentLibraryPage.navigateToDocumentLibraryPage();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar("t@#$%^!@ds");
        Assert.assertFalse(liveSearchPageSupport.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isSitesTitleDisplayed(), "Sites title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isScopeRepositoryDisplayed("t@#$%^!@ds"), "Repository scope is displayed");
        Assert.assertFalse(liveSearchPageSupport.isScopeSitesDisplayed(), "Scope site is displayed");
        Assert.assertFalse(liveSearchPageSupport.areAnyDocumentElementsDisplayed(), "Documents are displayed");
        Assert.assertFalse(liveSearchPageSupport.areAnyPeopleElementsDisplayed(), "People elements are displayed");
        Assert.assertFalse(liveSearchPageSupport.areAnySiteElementsDisplayed(), "Site elements are displayed");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testCheckLiveSearchScopeOptionsSiteContext()
    {
        log.info("Step 1: Check that when not in site context, the Live Search dropdown does not contain the scope options");
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        log.info("Edit site data & give specific name to " + testSite);
        authenticateUsingLoginPage(testUser);
        SiteModel editSite = testSite;
        String description = "FacetedSearchSite";
        siteDashboardPage.navigateToEditSiteDetailsDialog(editSite.getId());
        siteDashboardPage.editSiteDescription(description);
        documentLibraryPage.navigateToDocumentLibraryPage();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        userDashboardPage.navigate(testUser);
        toolbar.searchInToolbar(docName);
        Assert.assertFalse(liveSearchPageSupport.isScopeRepositoryDisplayed(docName), "Scope Repository is displayed");
        Assert.assertFalse(liveSearchPageSupport.isScopeSitesDisplayed(), "Scope Sites is displayed");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testCheckLiveSearchScopeOptionsRepoContext()
    {
        log.info("Step 1: Check that when in site, the Live Search dropdown contains the scope options");
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        log.info("Edit site data & give specific name to " + testSite);
        authenticateUsingLoginPage(testUser);
        SiteModel editSite = testSite;
        String description = "FacetedSearchSite";
        siteDashboardPage.navigateToEditSiteDetailsDialog(editSite.getId());
        siteDashboardPage.editSiteDescription(description);
        documentLibraryPage.navigateToDocumentLibraryPage();
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        siteDashboardPage.navigate();
        toolbar.searchInToolbar(docName);
        Assert.assertTrue(liveSearchPageSupport.isScopeRepositoryDisplayed(docName), "Scope Repository is not displayed");
        Assert.assertTrue(liveSearchPageSupport.isScopeSitesDisplayed(), "Scope Sites is not displayed");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testCheckSiteScopeSiteName()
    {
        log.info("Step 1: Check the site name in Search Site scope option");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        String expectedInfo = "Search in site '" + testSite.getTitle() + "'";
        toolbar.searchInToolbar(docName);
        Assert.assertEquals(liveSearchPageSupport.getScopeSiteText(testSite.getTitle()), expectedInfo, testSite + " is not available in scope site");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchDocumentResult()
    {
        log.info("Step 1: Check that the document search result contains document name, site name and user name");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(docName);
        Assert.assertTrue(liveSearchPageSupport.getDocumentDetails(docName).contains(docName), "Document details does not contain document name");
        Assert.assertTrue(liveSearchPageSupport.getDocumentDetails(docName).contains(testSite.getTitle()), "Document details does not contain site name");
        Assert.assertTrue(liveSearchPageSupport.getDocumentDetails(docName).contains(testUser.getUsername()), "Document details does not contain user name");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testExpandLiveSearchDocumentResult()
    {
        log.info("Step 1: Expand document search results");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        userDashboardPage.navigate(testUser);
        toolbar.searchInToolbar("jpg");
        liveSearchPageSupport.clickMore();
        Assert.assertTrue(liveSearchPageSupport.getResultsListSize() > 0, "List has no results");
        liveSearchPageSupport.closeLiveSearchResults();
        Assert.assertFalse(liveSearchPageSupport.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isSitesTitleDisplayed(), "Sites title is displayed");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testClickOnDocumentTitle()
    {
        log.info("Step 1: Clicks on the document name in the document search result and checks that the document's details page is displayed");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(docName);
        toolbar.isResultDisplayedLiveSearch(docName);
        liveSearchPageSupport.clickDocumentName(docName);
        Assert.assertEquals(documentDetailsPage.getWebDriver().getTitle(), "Alfresco » Document Details", "User is not redirected to the document details page");
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, docName + " is not displayed on the Document Details page");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testClickOnDocumentSiteName()
    {
        log.info("Step 1: Clicks on document site name in the document search result and checks that document site library page is displayed");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(docName);
        toolbar.isResultDisplayedLiveSearch(docName);
        liveSearchPageSupport.clickSiteName(testSite.getTitle());
        Assert.assertEquals(documentLibraryPage.getWebDriver().getTitle(), "Alfresco » Document Library", "User is not redirected to Document Library");
        Assert.assertEquals(documentLibraryPage.getSiteName(), testSite.getTitle(), "User is not redirected to " + siteName + " document library");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH,TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testClickOnDocumentUserName()
    {
        log.info("Step 1: Click on document user name in document search result and checks that user profile page is displayed");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .typeDescription("test content")
            .clickCreate();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(docName);
        toolbar.isResultDisplayedLiveSearch(docName);
        liveSearchPageSupport.clickUserName(testUser.getUsername());
        Assert.assertEquals(userProfilePage.getWebDriver().getTitle(), "Alfresco » User Profile Page", "User is not redirected to User Profile page");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchPeopleResult()
    {
        String expected = "[faceted "+ testUser.getLastName()+" (" + testUser.getUsername() + ")]";
        log.info("Step 1: Search for username and checks that it is displayed in people search results");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(testUser.getUsername());
        Assert.assertEquals(liveSearchPageSupport.getPeopleResults(), expected, testUser.getUsername() + " is not available in the list of results");
        log.info("Step 2: Click user name and check that user profile is displayed");
        liveSearchPageSupport.clickPeopleUserName(testUser.getUsername());
        Assert.assertEquals(userProfilePage.getWebDriver().getTitle(), "Alfresco » User Profile Page", "User not redirected to user page");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchSitesResult()
    {
        log.info("Step 1: Search for site and checks that site name is displayed in site results");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        String expected = "[" + testSite.getTitle() + "]";
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(testSite.getTitle());
        toolbar.isResultDisplayedLiveSearch(testSite.getTitle());
        Assert.assertEquals(liveSearchPageSupport.getSiteResults(), expected, testSite.getTitle() + " is not displayed");
        log.info("Step 2: Click the site name and check that site dashboard is displayed");
        liveSearchPageSupport.clickSiteNameLiveSearch(testSite.getTitle());
        Assert.assertEquals(siteDashboardPage.getWebDriver().getTitle(), "Alfresco » Site Dashboard", "User is not redirected to Site Dashboard");
        Assert.assertEquals(siteDashboardPage.getSiteName(), testSite.getTitle(), "Site dashboard is not displayed");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchInSiteResults()
    {
        String searchTerm = "docName";
        log.info("Step 1: When clicking on Search Site scope, Document results are from the current site");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPageSupport.selectSiteContext();
        Assert.assertFalse(liveSearchPageSupport.areResultsFromOtherSitesReturned(testSite.getTitle()), "Other sites content is displayed: " + liveSearchPageSupport.getSites());
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchInRepositoryResults()
    {
        String searchTerm = "docName";
        log.info("Step 1: When clicking on Search Repository scope, Document results are from the all sites");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPageSupport.selectRepoContext();
        Assert.assertFalse(liveSearchPageSupport.areResultsFromOtherSitesReturned(testSite.getTitle()), "Other sites content is displayed: " + liveSearchPageSupport.getSites());
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchToFacetedInSiteScope()
    {
        String searchTerm = "docName";
        log.info("Step 1: When clicking on Search Site scope and then Enter scope is set to site");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPageSupport.selectSiteContext();
        toolbar.search(searchTerm);
        Assert.assertEquals(searchPage.getSearchInDropdownSelectedValue(), testSite.getTitle(), testSite.getTitle() + " is not the context displayed on the search results page");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchToFacetedInRepositoryScope()
    {
        String searchTerm = "docName";
        log.info("Step 1: When clicking on Search Repository scope and then Enter scope is set to repository");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPageSupport.selectRepoContext();
        toolbar.search(searchTerm);
        Assert.assertEquals(searchPage.getSearchInDropdownSelectedValue(), "Repository", "Repository is not the context displayed on the search results page");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "SearchTests", TestGroup.NOT_SUPPORTED_ON_SINGLE_PIPELINE  })
    public void testLiveSearchNoResultsInSite()
    {
        String searchTerm = "jpg";
        log.info("Check that when in a site context, if there are no results in the site but there are results in the Repository,the scope options are visible");
        authenticateUsingLoginPage(testUser);
        testSite = dataSite.usingUser(testUser).createPublicRandomSite();
        documentLibraryPage.navigate(testSite);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPageSupport.selectSiteContext();
        Assert.assertFalse(liveSearchPageSupport.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPageSupport.isSitesTitleDisplayed(), "Sites title is displayed");
        Assert.assertTrue(liveSearchPageSupport.isScopeRepositoryDisplayed(searchTerm), "Repository scope is not displayed");
        Assert.assertTrue(liveSearchPageSupport.isScopeSitesDisplayed(), "Scope site is not displayed");
    }
}
