package org.alfresco.share.searching.advancedSearch;

import static org.alfresco.common.Utils.testDataFolder;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by Mirela Tifui on 12/12/2017.
 */
public class AdvancedSearchTests extends ContextAwareWebTest
{
    //@Autowired
    AdvancedSearchPage advancedSearchPage;
    //@Autowired
    SearchPage searchPage;
    //s@Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    EditPropertiesDialog editPropertiesDialog;
    SoftAssert softAssert = new SoftAssert();
    private String userName = "advancedSearch" + RandomData.getRandomAlphanumeric();
    private String siteName = "siteSearch" + RandomData.getRandomAlphanumeric();
    private String xmlDoc = "0_XMLdoc_" + RandomData.getRandomAlphanumeric();
    private String excelDoc = "0_ExcelDoc" + RandomData.getRandomAlphanumeric();
    private String HTMLDoc = "0_HTMLDoc" + RandomData.getRandomAlphanumeric();
    private String folderName = "0_Folder" + RandomData.getRandomAlphanumeric();
    private String textFile = "myFile.txt";

    @BeforeClass (alwaysRun = true)
    private void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "Advanced", TestGroup.SEARCH);
        siteService.create(userName, password, domain, siteName, "site description", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.XML, xmlDoc, "contentXml");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.MSEXCEL, excelDoc, "contentExcel");
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.HTML, HTMLDoc, "contentHtml");
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.uploadFileInSite(userName, password, siteName, testDataFolder + textFile);
        setupAuthenticatedSession(userName, password);
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH })
    public void searchByMimeTypeTest()
    {
        LOG.info("Step 1: Navigate to advanced search and select XML mimetype");
        advancedSearchPage.navigate();
        Assert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage.selectMimetype("text/xml");
        advancedSearchPage.click1stSearch();
        softAssert.assertTrue(searchPage.isResultFoundWithRetry(xmlDoc), xmlDoc + " is not displayed");
        softAssert.assertFalse(searchPage.isResultFound(excelDoc), excelDoc + " is displayed");
        softAssert.assertFalse(searchPage.isResultFound(HTMLDoc), HTMLDoc + " is displayed");
        LOG.info("Step 2: Navigate to advanced search page and select Excel mimetype");
        advancedSearchPage.navigate();
        advancedSearchPage.selectMimetype("application/vnd.ms-excel");
        advancedSearchPage.click1stSearch();
        softAssert.assertFalse(searchPage.isResultFound(xmlDoc), xmlDoc + " is displayed");
        softAssert.assertTrue(searchPage.isResultFound(excelDoc), excelDoc + " is not displayed");
        softAssert.assertFalse(searchPage.isResultFound(HTMLDoc), HTMLDoc + " is displayed");
        softAssert.assertAll();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH })
    public void validateInvalidDateTest()
    {
        LOG.info("Step 1: Navigate to Advanced Search page and input invalid from date");
        advancedSearchPage.navigate();
        softAssert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage.setFromDate("0/06/2017");
        advancedSearchPage.click1stSearch();
        softAssert.assertEquals(searchPage.getNoSearchResultsText(), language.translate("searchPage.searchSuggestionNoSearchResuls"), "no search result suggestion not correct");
        LOG.info("Step 2: Navigate to Advanced Search page and input invalid to date");
        advancedSearchPage.navigate();
        advancedSearchPage.setToDate("25/06/0000");
        advancedSearchPage.click1stSearch();
        softAssert.assertEquals(searchPage.getNoSearchResultsText(), language.translate("searchPage.searchSuggestionNoSearchResuls"), "no search result suggestion not correct");
        softAssert.assertAll();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation" })
    public void checkThatWhenFileIsSearchedNoFolderIsReturned()
    {
        LOG.info("Step 1: Navigate to advanced search and search for .txt file, verify that no folders are returned");
        advancedSearchPage.navigate();
        softAssert.assertEquals(advancedSearchPage.getSelectedContentTypeOption(), "Content ▾", "Content is not the selected Look for option");
        advancedSearchPage.typeName(textFile);
        advancedSearchPage.click1stSearch();
        softAssert.assertFalse(searchPage.isAnyFolderReturnedInResults(), "Folder type is returned in results");
        softAssert.assertTrue(searchPage.isResultFound(textFile), textFile + " is not displayed in search results");
        softAssert.assertAll();
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation" })
    public void checkFolderSearchWithAllDetailsProvided()
    {
        //precondition - add description and folder title - this is done from the UI as there is no method in te framework to perform this actions
        String folderTitle = "Folder title";
        String description = "This is a test folder";
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        editPropertiesDialog.setTitle(folderTitle);
        editPropertiesDialog.setDescription(description);
        editPropertiesDialog.clickSave();

        LOG.info("Step 1: Navigate to Advanced Search and search for folder providing all details");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage.typeName(folderName);
        advancedSearchPage.typeDescription(description);
        advancedSearchPage.typeTitle(folderTitle);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFound(folderName), folderName + " is not displayed in search results");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH, "BugValidation" })
    public void folderKeywordSearchTest()
    {
        LOG.info("Step 1: Go to Advanced Search page, select Folder type, type in keyword and check that only folder results are returned");
        advancedSearchPage.navigate();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage.typeKeywords(folderName);
        advancedSearchPage.click1stSearch();
        softAssert.assertTrue(searchPage.isResultFound(folderName), folderName + " is not displayed in search results");
        softAssert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "Folder type is not returned in results");
        softAssert.assertFalse(searchPage.isAnyFileReturnedInResults(), "File type is returned in results");
        softAssert.assertAll();
    }
}
