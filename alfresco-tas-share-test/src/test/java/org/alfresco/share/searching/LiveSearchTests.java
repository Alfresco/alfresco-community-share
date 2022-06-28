package org.alfresco.share.searching;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.LiveSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 2/16/2018.
 */
public class LiveSearchTests extends ContextAwareWebTest
{
    //@Autowired
    Toolbar toolbar;
    @Autowired
    LiveSearchPage liveSearchPage;
    //@Autowired
    SiteDashboardPage siteDashboardPage;
   //@Autowired
    DocumentLibraryPage documentLibraryPage;
    //@Autowired
    UserDashboardPage userDashboardPage;
    //@Autowired
    DocumentDetailsPage documentDetailsPage;
    //@Autowired
    UserProfilePage userProfilePage;
    //@Autowired
    SearchPage searchPage;

    private String userName = "testUser" + RandomData.getRandomAlphanumeric();
    private String siteName = "siteName" + RandomData.getRandomAlphanumeric();
    private String docName = "docName" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "faceted", TestGroup.SEARCH);
        siteService.create(userName, password, domain, siteName, "FacetedSearchSite", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "test content");
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckNoLiveSearchResults()
    {
        LOG.info("Step 1: Search for info that does not exist and check that results are not displayed in search results");
        setupAuthenticatedSession(userName, password);
        toolbar.searchInToolbar("t@#$%^!@ds");
        Assert.assertFalse(liveSearchPage.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPage.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPage.isSitesTitleDisplayed(), "Sites title is displayed");
        Assert.assertFalse(liveSearchPage.isScopeRepositoryDisplayed(), "Repository scope is displayed");
        Assert.assertFalse(liveSearchPage.isScopeSitesDisplayed(), "Scope site is displayed");
        Assert.assertFalse(liveSearchPage.areAnyDocumentElementsDisplayed(), "Documents are displayed");
        Assert.assertFalse(liveSearchPage.areAnyPeopleElementsDisplayed(), "People elements are displayed");
        Assert.assertFalse(liveSearchPage.areAnySiteElementsDisplayed(), "Site elements are displayed");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckLiveSearchScopeOptionsSiteContext()
    {
        LOG.info("Step 1: Check that when not in site context, the Live Search dropdown does not contain the scope options");
        setupAuthenticatedSession(userName, password);
        toolbar.searchInToolbar(docName);
        Assert.assertFalse(liveSearchPage.isScopeRepositoryDisplayed(), "Scope Repository is displayed");
        Assert.assertFalse(liveSearchPage.isScopeSitesDisplayed(), "Scope Sites is displayed");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckLiveSearchScopeOptionsRepoContext()
    {
        LOG.info("Step 1: Check that when in site, the Live Search dropdown contains the scope options");
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        Assert.assertTrue(liveSearchPage.isScopeRepositoryDisplayed(), "Scope Repository is not displayed");
        Assert.assertTrue(liveSearchPage.isScopeSitesDisplayed(), "Scope Sites is not displayed");
        setupAuthenticatedSession(userName, password);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckSiteScopeSiteName()
    {
        LOG.info("Step 1: Check the site name in Search Site scope option");
        String expectedInfo = "Search in site '" + siteName + "'";
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        Assert.assertEquals(liveSearchPage.getScopeSiteText(siteName), expectedInfo, siteName + " is not available in scope site");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchDocumentResult()
    {
        LOG.info("Step 1: Check that the document search result contains document name, site name and user name");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        Assert.assertTrue(liveSearchPage.getDocumentDetails(docName).contains(docName), "Document details does not contain document name");
        Assert.assertTrue(liveSearchPage.getDocumentDetails(docName).contains(siteName), "Document details does not contain site name");
        Assert.assertTrue(liveSearchPage.getDocumentDetails(docName).contains(userName), "Document details does not contain user name");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testExpandLiveSearchDocumentResult()
    {
        LOG.info("Step 1: Expand document search results");
        setupAuthenticatedSession(userName, password);
        userDashboardPage.navigate(userName);
        toolbar.searchInToolbar("jpg");
        liveSearchPage.clickMore();
        Assert.assertTrue(liveSearchPage.getResultsListSize() > 0, "List has no results");
        liveSearchPage.closeLiveSearchResults();
        Assert.assertFalse(liveSearchPage.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPage.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPage.isSitesTitleDisplayed(), "Sites title is displayed");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickOnDocumentTitle()
    {
        LOG.info("Step 1: Clicks on the document name in the document search result and checks that the document's details page is displayed");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        liveSearchPage.clickDocumentName(docName);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Document Details", "User is not redirected to the document details page");
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, docName + " is not displayed on the Document Details page");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickOnDocumentSiteName()
    {
        LOG.info("Step 1: Clicks on document site name in the document search result and checks that document site library page is displayed");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        liveSearchPage.clickSiteName(siteName);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Document Library", "User is not redirected to Document Library");
        Assert.assertEquals(documentLibraryPage.getSiteName(), siteName, "User is not redirected to " + siteName + " document library");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickOnDocumentUserName()
    {
        LOG.info("Step 1: Click on document user name in document search result and checks that user profile page is displayed");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(docName);
        liveSearchPage.clickUserName(userName);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » User Profile Page", "User is not redirected to User Profile page");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchPeopleResult()
    {
        String expected = "[faceted search (" + userName + ")]";
        LOG.info("Step 1: Search for username and checks that it is displayed in people search results");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(userName);
        Assert.assertEquals(liveSearchPage.getPeopleResults(), expected, userName + " is not available in the list of results");
        LOG.info("Step 2: Click user name and check that user profile is displayed");
        liveSearchPage.clickPeopleUserName(userName);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » User Profile Page", "User not redirected to user page");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchSitesResult()
    {
        String expected = "[" + siteName + "]";
        LOG.info("Step 1: Search for site and checks that site name is displayed in site results");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(siteName);
        Assert.assertEquals(liveSearchPage.getSiteResults(), expected, siteName + " is not displayed");
        LOG.info("Step 2: Click the site name and check that site dashboard is displayed");
        liveSearchPage.clickSiteNameLiveSearch(siteName);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Site Dashboard", "User is not redirected to Site Dashboard");
        Assert.assertEquals(siteDashboardPage.getSiteName(), siteName, "Site dashboard is not displayed");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchInSiteResults()
    {
        String searchTerm = "docName";
        LOG.info("Step 1: When clicking on Search Site scope, Document results are from the current site");
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPage.selectSiteContext();
        Assert.assertFalse(liveSearchPage.areResultsFromOtherSitesReturned(siteName), "Other sites content is displayed: " + liveSearchPage.getSites());
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchInRepositoryResults()
    {
        String searchTerm = "docName";
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: When clicking on Search Repository scope, Document results are from the all sites");
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPage.selectRepoContext();
        Assert.assertTrue(liveSearchPage.areResultsFromOtherSitesReturned(siteName), "Other sites content is displayed: " + liveSearchPage.getSites());
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchToFacetedInSiteScope()
    {
        String searchTerm = "docName";
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: When clicking on Search Site scope and then Enter scope is set to site");
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPage.selectSiteContext();
        toolbar.search(searchTerm);
        Assert.assertEquals(searchPage.getSearchInDropdownSelectedValue(), siteName, siteName + " is not the context displayed on the search results page");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchToFacetedInRepositoryScope()
    {
        String searchTerm = "docName";
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: When clicking on Search Repository scope and then Enter scope is set to repository");
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPage.selectRepoContext();
        toolbar.search(searchTerm);
        Assert.assertEquals(searchPage.getSearchInDropdownSelectedValue(), "Repository", "Repository is not the context displayed on the search results page");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testLiveSearchNoResultsInSite()
    {
        String searchTerm = "jpg";
        setupAuthenticatedSession(userName, password);
        LOG.info("Check that when in a site context, if there are no results in the site but there are results in the Repository,the scope options are visible");
        documentLibraryPage.navigate(siteName);
        toolbar.searchInToolbar(searchTerm);
        liveSearchPage.selectSiteContext();
        Assert.assertFalse(liveSearchPage.isDocumentsTitleDisplayed(), "Documents title is displayed");
        Assert.assertFalse(liveSearchPage.isPeopleTitleDisplayed(), "People title is displayed");
        Assert.assertFalse(liveSearchPage.isSitesTitleDisplayed(), "Sites title is displayed");
        Assert.assertTrue(liveSearchPage.isScopeRepositoryDisplayed(), "Repository scope is not displayed");
        Assert.assertTrue(liveSearchPage.isScopeSitesDisplayed(), "Scope site is not displayed");
    }
}
