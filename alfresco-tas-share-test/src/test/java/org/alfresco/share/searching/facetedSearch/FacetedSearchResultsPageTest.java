package org.alfresco.share.searching.facetedSearch;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchManagerPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j

/**
 * Created by Mirela Tifui on 1/31/2018.
 */
public class FacetedSearchResultsPageTest extends BaseTest
{
    SearchPage searchPage;
    Toolbar toolbar;
    DocumentDetailsPage documentDetailsPage;
    DocumentLibraryPage documentLibraryPage;
    AdvancedSearchPage advancedSearchPage;
    SearchManagerPage searchManagerPage;
    UserProfilePage userProfilePage;
    private CreateContentPage createContent;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private String docName = "testDoc" + RandomData.getRandomAlphanumeric();
    private  String missingDoc = "t38ta@";
    private String searchTerm = "facetedSearch_";
     private String searchTerm1 = "ipsum";
    private FileModel testFile;
    private String folderName = "folderName" + RandomData.getRandomAlphanumeric();



    @BeforeMethod (alwaysRun = true)
    public void testSetup()
    {
        log.info("Precondition2: Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());
        log.info("Precondition3: Test Site is created");
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        log.info("Precondition4: Creating random files in the site under document library.");
        testFile = FileModel.getRandomFileModel(FileType.MSWORD2007);
        getCmisApi().usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        searchPage = new SearchPage(webDriver);
        searchManagerPage = new SearchManagerPage(webDriver);
        userProfilePage = new UserProfilePage(webDriver);
        toolbar = new Toolbar(webDriver);
        createContent = new CreateContentPage(webDriver);


    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchEmptyResult()  {
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search(missingDoc);
       searchPage
           .navigate();
        searchPage
            .setSearchExpression(missingDoc);
        Assert.assertEquals(searchPage.getNoSearchResultsText(), language.newTranslate("Search suggestions:\n" +
            "Check your spelling.\n" +
            "Using fewer words in the search may increase the number of results.\n" +
            "You can search for any property of an item including any tags, the description, its content, dates, creator, and modifier."), "no search result suggestion not correct");
        Assert.assertEquals(searchPage.getNumberOfResultsText(), "0 - results found", "Results number is not 0");

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "SinglePipelineFailure" })
    public void testSelectSearchResultByName()
    {
        log.info("Step 1: Search for document");
        documentLibraryPage
            .navigate(site.get());
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(docName)
            .clickCreate();
        documentLibraryPage
            .navigate();
        toolbar
            .searchAndEnterAgain(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName), docName + " is not displayed");
        log.info("Step 2: Click document name and check that correct document is opened on Document Details page");
        searchPage
            .clickOnContentName(docName);
        Assert.assertEquals(documentDetailsPage.getFileName(), docName, "Document title is not correct");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "SinglePipelineFailure" })
    public void testSelectSearchResultOfTypeFolder() {
        log.info("Step 1: Search for folder");
        documentLibraryPage
            .navigate(site.get());
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.FOLDER);
        createContent
            .typeName(folderName)
            .clickCreate();
        documentLibraryPage
            .navigate();
        toolbar
            .searchAndEnterAgain(folderName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName), folderName + " is not displayed");
        log.info("Step 2: Click folder name");
        searchPage
            .clickOnContentName(folderName);
        assertEquals(documentLibraryPage.getBreadcrumbList(), Arrays.asList(folderName).toString());
    }

   @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testPagination()
    {
        log.info("Step 1: Search for all results containing letter a");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search("a");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0"), "Results are not returned");
        String searchResultNumber = searchPage.getNumberOfResultsText().substring(0, 2);
        int searchResultsNo = Integer.parseInt(searchResultNumber);
        System.out.println("number of results: " + searchResultNumber);
        int currentResultNo = Integer.parseInt(searchPage.getNumberOfResultsText().substring(0, 2));
        Assert.assertTrue(currentResultNo >= searchResultsNo, "Result number is lower than the one before scrolling");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testSearchSortDescTest()
    {

        log.info("Step 1: Search for files containing 'ipsum'");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search("a");
        searchPage
            .clickDetailedView();
        searchPage
            .clickSortDropdown();
        searchPage
            .clickSortDropdownOption("Modified date");
        Assert.assertTrue(searchPage.isSortOrderToggleButtonDisplayed(), "Sort button is not displayed");
        Assert.assertTrue(searchPage.isAscendingOrderSet(), "Ascending order is not set by default");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0 - results found"), "No results are returned after sort");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
        log.info("Step 2: Click toggle button");
        searchPage
            .clickToggleButton();
        Assert.assertTrue(searchPage.isDescendingOrderSet(), "Descending order is not set");
        Assert.assertFalse(searchPage.getNumberOfResultsText().equals("0 - results found"), "No results are returned after sort");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testSearchSortTest()
    {
        log.info("Step 1: Search for files, sort by name and check that the list of results is arranged by name");
        documentLibraryPage
            .navigate(site.get());
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(searchTerm)
            .clickCreate();
        documentLibraryPage
            .navigate();
        toolbar
            .searchAndEnterAgain(searchTerm);
        searchPage
            .assertCreatedDataIsDisplayed(searchTerm);
        searchPage
            .clickSortDropdown();
        searchPage
            .clickSortDropdownOption("Name");
        Assert.assertTrue(searchPage.isSortOrderToggleButtonDisplayed(), "Sort button is not displayed");
        Assert.assertEquals(searchPage.getSortFilter(), "Name", "Name is not the sort filter selected");
        Assert.assertFalse(searchPage.getResultsListSize() == 0, "No results are displayed");
        Assert.assertTrue(searchPage.areResultsSortedByName(), "Results are not sorted by name");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testCheckAvailableSortFilters()
    {
        log.info("Step 1: Check that all expected filters are available on the search results page.");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search("a");
        searchPage
            .clickSortDropdown();
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
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testGetResultCount()
    {
        log.info("Step 1: Search for existing files and confirm that the number of displayed results is correct.");
        documentLibraryPage
            .navigate(site.get());
        documentLibraryPage
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT);
        createContent
            .typeName(searchTerm)
            .clickCreate();
        documentLibraryPage
            .navigate();
        toolbar
            .searchAndEnterAgain(searchTerm);
        System.out.print("List size: " + searchPage.getResultsListSize());
        Assert.assertEquals((searchPage.getNumberOfResultsText().substring(0, 1)), "1", "The number of displayed results is not correct.");
        Assert.assertEquals(searchPage.getResultsListSize(), 1, "Results number is not correct");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" })
    public void testSelectFacet()
    {
        log.info("Step 1: Search for files then filter by mimeType, check that results are filtered correctly");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .searchAndEnterAgain(testFile.getName());
        searchPage
            .selectFileTypeFilter("Microsoft Word 2007");
        Assert.assertEquals(searchPage.getNumberOfResultsText().substring(0, 1), "1", "Results are not filtered correctly");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testSearchSelectViewTest() {

        log.info("Step 1: Search for file and check that results are returned in detailed view by default and that changing the view will not affect the results displayed");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search(searchTerm1);
        System.out.println("List size: " + searchPage.getResultsListSize());
        Assert.assertTrue(searchPage.getResultsListSize() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is not the active view");
        searchPage
            .clickGalleryView();
        Assert.assertTrue(searchPage.isSearchResultsListInGalleryView(), "Detailed view is not the active view");
        Assert.assertTrue(searchPage.getGalleryViewResultsNumber() != 0, "No results are displayed");
        searchPage
            .clickDetailedView();
        Assert.assertTrue(searchPage.getResultsListSize() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInDetailedView(), "Detailed view is not the active view");
        searchPage
            .clickGalleryView();
        Assert.assertTrue(searchPage.getGalleryViewResultsNumber() != 0, "No results are displayed");
        Assert.assertTrue(searchPage.isSearchResultsListInGalleryView(), "Gallery view is not the active view");
        searchPage
            .openFileFromGalleryView("Project Overview.ppt");
        Assert.assertEquals(documentDetailsPage.getFileName(), "Project Overview.ppt");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickImagePreviewTest() {
        log.info("Step 1: Search for .jpg extension and check that the image is previewed from the search results");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search(".jpg");
        Assert.assertTrue(searchPage.isResultFoundWithRetry("graph.JPG"));
        searchPage
            .clickContentThumbnailByName("graph.JPG");
        Assert.assertEquals(searchPage.getPreviewedImageName(), "graph.JPG");
        searchPage
            .closePicturePreview();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testFilePreview()
    {
        log.info("Step 1: Search for .jpg extension and check that the document is previewed from the search results");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search("ipsum");
        Assert.assertTrue(searchPage.isResultFoundWithRetry("Project Overview.ppt"));
        searchPage
            .clickContentThumbnailByName("Project Overview.ppt");
        Assert.assertTrue(searchPage.isContentPreviewed("Project Overview.ppt"), "File is not previewed");
        searchPage
            .closeFilePreview();
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickConfigureSearchTest()
    {

        log.info("Step 1: Check that user that has been added to search administrators can access the Search Manager page from the Search Results page");
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(getAdminUser());
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search(searchTerm1);
        searchPage
            .navigate();
        Assert.assertTrue(searchPage.isConfigureSearchButtonDisplayed());
        searchPage
            .clickSearchManagerLink();
       Assert.assertTrue(webDriver.get().getTitle().equals("Alfresco » Search Manager"));
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testClickLinksOnSearchItemTest() {
        log.info("Step 1: From the search page click on the modified by link");
        documentLibraryPage
            .navigate(site.get());
        toolbar
            .search("ipsum");
        searchPage
            .clickModifiedBy("Mike Jackson");
        Assert.assertEquals(webDriver.get().getTitle(), "Alfresco » User Profile Page", "User is not redirected to the User Profile page");
        Assert.assertTrue(userProfilePage.assertCheckUserName(), "User Name does not match");

    }
}
