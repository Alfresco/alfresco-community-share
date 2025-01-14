package org.alfresco.share.searching.advancedSearch;


import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
@Slf4j

/**
 * Created by Mirela Tifui on 12/12/2017.
 */
public class AdvancedSearchTests extends BaseTest
{

    AdvancedSearchPage advancedSearchPage;
    SearchPage searchPage;
    DocumentLibraryPage documentLibraryPage;
    DocumentDetailsPage documentDetailsPage;
    private FileModel testFile;
    private FileModel testFile1;
    private FileModel testFile2;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    EditPropertiesDialog editPropertiesDialog;
    SoftAssert softAssert = new SoftAssert();
    private String folderName = "test Folder";
    private String textFile = "myFile.txt";
    private  String folderTitle = "Folder title";
    private   String description = "This is a test folder";

    @BeforeMethod(alwaysRun = true)
    private void setupTest(){
        log.info("Precondition2: Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());
        log.info("Precondition3: Test Site is created");
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        log.info("Precondition4: Creating random files in the site under document library.");
        testFile = FileModel.getRandomFileModel(FileType.XML);
        getCmisApi().usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();
        testFile1 = FileModel.getRandomFileModel(FileType.MSEXCEL);
        getCmisApi().usingSite(site.get()).createFile(testFile1).assertThat().existsInRepo();
        testFile2 = FileModel.getRandomFileModel(FileType.HTML);
        getCmisApi().usingSite(site.get()).createFile(testFile2).assertThat().existsInRepo();
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        searchPage = new SearchPage(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void testCleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());

    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "SinglePipelineFailure"})
    public void searchByMimeTypeTest()
    {
        log.info("Step 1: Navigate to advanced search and select XML mimetype");
        documentLibraryPage
            .navigate(site.get());
        advancedSearchPage
            .navigate();
        Assert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage
            .selectMimetype("text/xml");
        advancedSearchPage
            .clickFirstSearchButtonAgain();
        searchPage
            .assertIsXmlFileDisplayed(".xml");
        searchPage
            .assertIsXlsFileNotDisplayed(".xls");
        searchPage
            .assertIsHtmlFileNotDisplayed(".html");
        log.info("Step 2: Navigate to advanced search page and select Excel mimetype");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .selectMimetype("application/vnd.ms-excel");
        advancedSearchPage
            .clickFirstSearchButtonAgain();
        searchPage
            .assertIsXmlFileNotDisplayed(".xml");
        searchPage
            .assertIsXlsFileDisplayed(".xls");
        searchPage
            .assertIsHtmlFileNotDisplayed(".html");

    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH })
    public void validateInvalidDateTest()
    {
        log.info("Step 1: Navigate to Advanced Search page and input invalid from date");
        advancedSearchPage
            .navigate();
        softAssert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage
            .setFromDate("0/06/2017");
        advancedSearchPage
            .clickFirstSearchButton();
        softAssert.assertEquals(searchPage.getNoSearchResultsText(), language.translate("searchPage.searchSuggestionNoSearchResuls"), "no search result suggestion not correct");
        log.info("Step 2: Navigate to Advanced Search page and input invalid to date");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .setToDate("25/06/0000");
        advancedSearchPage
            .clickFirstSearchButton();
        softAssert.assertEquals(searchPage.getNoSearchResultsText(), language.translate("searchPage.searchSuggestionNoSearchResuls"), "no search result suggestion not correct");

    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation" })
    public void checkThatWhenFileIsSearchedNoFolderIsReturned() {
        log.info("Step 1: Navigate to advanced search and search for .txt file, verify that no folders are returned");
        documentLibraryPage
            .navigate(site.get())
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.PLAIN_TEXT)
            .typeName(textFile)
            .clickCreate();
        documentLibraryPage.navigate();
        advancedSearchPage
            .navigate();
        softAssert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage
            .typeName(textFile);
        advancedSearchPage
            .clickFirstSearchButtonAndRefresh();
        softAssert.assertFalse(searchPage.isAnyFolderReturnedInResults(), "Folder type is returned in results");
        softAssert.assertTrue(searchPage.isResultFoundWithList(textFile), textFile + " is not displayed in search results");
        softAssert.assertAll();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation" })
    public void checkFolderSearchWithAllDetailsProvided() {
        log.info("precondition - create a Folder with all fields");
        documentLibraryPage.navigate(site.get())
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.FOLDER)
            .typeName(folderName)
            .typeTitle(folderTitle)
            .typeDescription(description);
        editPropertiesDialog.clickSave();
        log.info("Step 1: Navigate to Advanced Search and search for folder providing all details");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage
            .typeNameFolder(folderName);
        advancedSearchPage
            .folderTypeTitle(folderTitle);
        advancedSearchPage
            .folderTypeDescription(description);
        advancedSearchPage
            .clickFirstSearchButton();
        Assert.assertTrue(searchPage.isResultFound(folderName), folderName + " is not displayed in search results");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation", "SinglePipelineFailure" })
    public void folderKeywordSearchTest()
    {
        log.info("precondition - create a Folder ");
        documentLibraryPage.navigate(site.get())
            .clickCreateContentOption(DocumentLibraryPage.CreateMenuOption.FOLDER)
            .typeName(folderName)
            .typeTitle(folderTitle)
            .typeDescription(description);
        editPropertiesDialog
            .clickSave();
        log.info("Step 1: Go to Advanced Search page, select Folder type, type in keyword and check that only folder results are returned");
        advancedSearchPage
            .navigate();
        advancedSearchPage
            .clickOnLookForDropdown();
        advancedSearchPage
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage
            .typeKeywords(folderName);
        advancedSearchPage
            .clickOnFirstSearchButton();
        softAssert.assertTrue(searchPage.isResultFound(folderName), folderName + " is not displayed in search results");
        softAssert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "Folder type is not returned in results");
        softAssert.assertFalse(searchPage.isAnyFileReturnedInResults(), "File type is returned in results");
        softAssert.assertAll();
    }
}
