package org.alfresco.share.searching.facetedSearch;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
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
 * Created by Mirela Tifui on 1/31/2018.
 */
public class FacetedSearchResultsPageTest extends ContextAwareWebTest
{
    //@Autowired
    SearchPage searchPage;
    //@Autowired
    Toolbar toolbar;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    //@Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    SearchManagerPage searchManagerPage;
    //@Autowired
    UserProfilePage userProfilePage;

    private String userName = "user" + RandomData.getRandomAlphanumeric();
    private String docName = "testDoc" + RandomData.getRandomAlphanumeric();
    private String siteName = "siteName" + RandomData.getRandomAlphanumeric();
    private String documentContent = "docContent";
    private String folderName = "folderName" + RandomData.getRandomAlphanumeric();
    private String docNameFS0 = "facetedSearch_" + RandomData.getRandomAlphanumeric();
    private String docNameFS1 = "facetedSearch_" + RandomData.getRandomAlphanumeric();
    private String docNameFS2 = "facetedSearch_" + RandomData.getRandomAlphanumeric();


    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "name", "surname");
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_SEARCH_ADMINISTRATORS", userName);
        siteService.create(userName, password, domain, siteName, "test site", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, documentContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.MSWORD, docNameFS0, documentContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.MSWORD, docNameFS1, documentContent);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docNameFS2, documentContent);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteDocument(adminUser, adminPassword, siteName, docName);
        contentService.deleteDocument(adminUser, adminPassword, siteName, docNameFS0);
        contentService.deleteDocument(adminUser, adminPassword, siteName, docNameFS1);
        contentService.deleteDocument(adminUser, adminPassword, siteName, docNameFS2);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchEmptyResult()
    {
        String missingDoc = "t38ta@";
        toolbar.search(missingDoc);
        searchPage.navigateByMenuBar();
        searchPage.setSearchExpression(missingDoc);
        Assert.assertFalse(searchPage.isResultFound(missingDoc), missingDoc + " is displayed");
        Assert.assertEquals(searchPage.getNoSearchResultsText(), language.translate("searchPage.searchSuggestionNoSearchResuls"), "no search result suggestion not correct");
        Assert.assertEquals(searchPage.getNumberOfResultsText(), "0 - results found", "Results number is not 0");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSelectSearchResultByName()
    {
        LOG.info("Step 1: Search for document");
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName), docName + " is not displayed");

        LOG.info("Step 2: Click document name and check that correct document is opened on Document Details page");
        searchPage.clickContentName(docName);
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "Document title is not correct");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSelectSearchResultOfTypeFolder()
    {
        LOG.info("Step 1: Search for folder");
        toolbar.search(folderName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName), folderName + " is not displayed");

        LOG.info("Step 2: Click folder name");
        searchPage.clickContentName(folderName);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList("Documents", folderName).toString());
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testPagination()
    {
        LOG.info("Step 1: Search for all results containing letter a");
        toolbar.search("a");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0"), "Results are not returned");
        String searchResultNumber = searchPage.getNumberOfResultsText().substring(0, 2);
        int searchResultsNo = Integer.parseInt(searchResultNumber);
        searchPage.scrollSome(50);
        searchPage.scrollToPageBottom();
        System.out.println("number of results: " + searchResultNumber);
        int currentResultNo = Integer.parseInt(searchPage.getNumberOfResultsText().substring(0, 2));
        Assert.assertTrue(currentResultNo >= searchResultsNo, "Result number is lower thant the one before scrolling");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchSortDescTest()
    {
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: Search for files containing 'ipsum'");
        toolbar.search("a");
        searchPage.clickDetailedView();
        searchPage.clickSortDropdown();
        searchPage.clickSortDropdownOption("Modified date");
        Assert.assertTrue(searchPage.isSortOrderToggleButtonDisplayed(), "Sort button is not displayed");
        Assert.assertTrue(searchPage.isAscendingOrderSet(), "Ascending order is not set by default");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0 - results found"), "No results are returned after sort");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
        LOG.info("Step 2: Click toggle button");
        searchPage.clickToggleButton();
        Assert.assertTrue(searchPage.isDescendingOrderSet(), "Descending order is not set");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0 - results found"), "No results are returned after sort");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchSortTest()
    {
        LOG.info("Step 1: Search for files, sort by name and check that the list of results is arranged by name");
        String searchTerm = "facetedSearch";
        toolbar.search(searchTerm);
        Assert.assertTrue(searchPage.isAnyFileReturnedInResults());
        searchPage.clickSortDropdown();
        searchPage.clickSortDropdownOption("Name");
        Assert.assertTrue(searchPage.isSortOrderToggleButtonDisplayed(), "Sort button is not displayed");
        Assert.assertEquals(searchPage.getSortFilter(), "Name", "Name is not the sort filter selected");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
        Assert.assertTrue(searchPage.areResultsSortedByName(), "Results are not sorted by name");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckAvailableSortFilters()
    {
        LOG.info("Step 1: Check that all expected filters are available on the search results page.");
        toolbar.search("a");
        searchPage.clickSortDropdown();
        List<String> availableOptions = searchPage.getAvailableFilters();
        Assert.assertTrue(availableOptions.contains("Relevance"), "Relevance is not available");
        Assert.assertTrue(availableOptions.contains("Name"), "Name is not available");
        Assert.assertTrue(availableOptions.contains("Title"), "Title is not available");
        Assert.assertTrue(availableOptions.contains("Description"), "Description is not available");
        Assert.assertTrue(availableOptions.contains("Author"), "Author is not available");
        Assert.assertTrue(availableOptions.contains("Modifier"), "Modifier is not available");
        Assert.assertTrue(availableOptions.contains("Modified date"), "Modified date is not available");
        Assert.assertTrue(availableOptions.contains("Creator"), "Creator is not available");
        Assert.assertTrue(availableOptions.contains("Created date"), "Created date is not available");
        Assert.assertTrue(availableOptions.contains("Size"), "Size is not available");
        Assert.assertTrue(availableOptions.contains("Mime type"), "Mime type is not available");
        Assert.assertTrue(availableOptions.contains("Type"), "Type is not available");
        cleanupAuthenticatedSession();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testGetResultCount()
    {
        LOG.info("Step 1: Search for existing files and confirm that the number of displayed results is correct.");
        String searchTerm = "facetedSearch_";
        toolbar.search(searchTerm);
        System.out.print("List size: " + searchPage.getResultsListSize());
        Assert.assertEquals((searchPage.getNumberOfResultsText().substring(0, 1)), "4", "The number of displayed results is not correct.");
        Assert.assertEquals(searchPage.getResultsListSize(), 4, "Results number is not correct");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSelectFacet()
    {
        LOG.info("Step 1: Search for files then filter by mimeType, check that results are filtered correctly");
        toolbar.search("facetedSearch");
        searchPage.selectFileTypeFilter("Microsoft Word");
        Assert.assertEquals(searchPage.getNumberOfResultsText().substring(0, 1), "2", "Results are not filtered correctly");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchSelectViewTest()
    {
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: Search for file and check that results are returned in detailed view by default and that changing the view will not affect the results displayed");
        String searchTerm = "ipsum";
        toolbar.search(searchTerm);
        System.out.println("List size: " + searchPage.getResultsListSize());
        Assert.assertTrue(searchPage.getResultsListSize() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is not the active view");
        searchPage.clickGalleryView();
        Assert.assertTrue(searchPage.isSearchResultsListInGalleryView(), "Detailed view is not the active view");
        Assert.assertTrue(searchPage.getGalleryViewResultsNumber() != 0, "No results are displayed");
        searchPage.clickDetailedView();
        Assert.assertTrue(searchPage.getResultsListSize() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is not the active view");
        searchPage.clickGalleryView();
        Assert.assertTrue(searchPage.getGalleryViewResultsNumber() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInGalleryView(), "Gallery view is not the active view");
        searchPage.openFileFromGalleryView("Project Overview.ppt");
        Assert.assertEquals(documentDetailsPage.getFileName(), "Project Overview.ppt");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickImagePreviewTest()
    {
        LOG.info("Step 1: Search for .jpg extension and check that the image is previewed from the search results");
        toolbar.search(".jpg");
        Assert.assertTrue(searchPage.isResultFoundWithRetry("graph.JPG"));
        searchPage.clickContentThumbnailByName("graph.JPG");
        Assert.assertEquals(searchPage.getPreviewedImageName(), "graph.JPG");
        searchPage.closePicturePreview();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testFilePreview()
    {
        LOG.info("Step 1: Search for .jpg extension and check that the document is previewed from the search results");
        toolbar.search("ipsum");
        Assert.assertTrue(searchPage.isResultFoundWithRetry("Project Overview.ppt"));
        searchPage.clickContentThumbnailByName("Project Overview.ppt");
        Assert.assertTrue(searchPage.isContentPreviewed("Project Overview.ppt"), "File is not previewed");
        searchPage.closeFilePreview();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickConfigureSearchTest()
    {
        setupAuthenticatedSession(userName, password);
        LOG.info("Step 1: Check that user that has been added to search administrators can access the Search Manager page from the Search Results page");
        toolbar.search("ipsum");
        Assert.assertTrue(searchPage.isConfigureSearchButtonDisplayed());
        searchPage.clickConfigureButton(searchManagerPage);
        Assert.assertTrue(getBrowser().getTitle().equals("Alfresco » Search Manager"));
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickLinksOnSearchItemTest()
    {
        LOG.info("Step 1: From the search page click on the modified by link");
        toolbar.search("ipsum");
        searchPage.clickModifiedBy("Mike Jackson");
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » User Profile Page", "User is not redirected to the User Profile page");
        Assert.assertEquals(userProfilePage.getNameLabel(), "Mike Jackson", "User name not as expected");
    }
}
