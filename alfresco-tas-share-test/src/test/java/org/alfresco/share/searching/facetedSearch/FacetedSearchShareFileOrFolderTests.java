package org.alfresco.share.searching.facetedSearch;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.dialogWindows.ConfirmDeletionDialogShare;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 12/27/2017.
 */
public class FacetedSearchShareFileOrFolderTests extends ContextAwareWebTest
{
    @Autowired
    SearchPage searchPage;
    @Autowired
    Toolbar toolbar;
    @Autowired
    ConfirmDeletionDialogShare confirmDeletionDialog;
    @Autowired
    AdvancedSearchPage advancedSearchPage;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    private String userName = "facetedSearch" + RandomData.getRandomAlphanumeric();
    private String siteName = "searchSite" + RandomData.getRandomAlphanumeric();
    private String docName = "facetedSearchDoc" + RandomData.getRandomAlphanumeric();
    private String docContent = "file_content";
    private String folderName = "facetedFolder" + RandomData.getRandomAlphanumeric();
    private String folderTitle = "title_" + RandomData.getRandomAlphanumeric();
    private String description = "This_is_a_test_folder";
    private String fileTitle = "file_" + RandomData.getRandomAlphanumeric();
    private String fileDescription = "file_description";

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "faceted", "search");
        siteService.create(userName, password, domain, siteName, "FacetedSearchSite", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, ItemActions.EDIT_PROPERTIES, editPropertiesDialog);
        editPropertiesDialog.setTitle(folderTitle);
        editPropertiesDialog.setDescription(description);
        editPropertiesDialog.clickSave();
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.EDIT_PROPERTIES, editPropertiesDialog);
        editPropertiesDialog.setTitle(fileTitle);
        editPropertiesDialog.setDescription(fileDescription);
        editPropertiesDialog.clickSave();
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", "Search" })
    public void testSelectActionDeleteConfirmCancel()
    {
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        LOG.info("Step 1: Select document and select Delete, on the Confirm deletion dialog click close to close dialog");
        searchPage.clickCheckbox(docName);
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Delete");
        confirmDeletionDialog.clickCloseButton();
        Assert.assertFalse(searchPage.isConfirmDeletionDialogDisplayed(), "Confirm deletion dialog is still displayed");
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        LOG.info("Step 2: Select document and select Delete, on the Confirm deletion dialog click cancel to cancel deletion");
        searchPage.clickCheckbox(docName);
        searchPage.clickSelectedItemsDropdown();
        searchPage.clickOptionFromSelectedItemsDropdown("Delete");
        confirmDeletionDialog.clickCancelButton();
        Assert.assertFalse(searchPage.isConfirmDeletionDialogDisplayed(), "Confirm deletion dialog is still displayed");
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByName()
    {
        LOG.info("Step 1: Search by name and check that document name is highlighted in search results");
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByNameAdvancedSearch()
    {
        LOG.info("Step 1: Navigate to advanced search page and search for document by document name, check that name is highlighted in search results");
        advancedSearchPage.navigate();
        advancedSearchPage.typeName(docName);
        advancedSearchPage.click2ndSearchButton();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByNameSearch()
    {
        LOG.info("Step 1: On the search page search for folder by name and check that folder name is highlighted");
        toolbar.search(folderName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isNameHighlighted(folderName), folderName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByNameAdvancedSearch()
    {
        LOG.info("Step 1: On the advanced search search by folder name and check that folder is returned in search results and that the folder name is highlighted");
        advancedSearchPage.navigateByMenuBar();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage.typeName(folderName);
        advancedSearchPage.click1stSearch();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isNameHighlighted(folderName), folderName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByTitleSearch()
    {
        LOG.info("Step 1: Navigate to search page and search for folder name, check that folder name is highlighted in search results");
        toolbar.search(folderTitle);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByTitleAdvancedSearch()
    {
        LOG.info("Step 1: On the advanced search, search by folder title and check that folder is returned in search results and that the folder title is highlighted");
        advancedSearchPage.navigateByMenuBar();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage.setTitle(folderTitle);
        advancedSearchPage.click2ndSearchButton();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByTitleAdvancedSearch()
    {
        LOG.info("Step 1: On the advanced search, search by file title and check that file is returned in search results and that the file title is highlighted");
        advancedSearchPage.navigateByMenuBar();
        advancedSearchPage.typeTitle(fileTitle);
        advancedSearchPage.click2ndSearchButton();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByTitleSearch()
    {
        LOG.info("Step 1: Search for file name, check that file name is highlighted in search results");
        toolbar.search(fileTitle);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByDescription()
    {
        LOG.info("Step 1: Search for file description, check that file description is highlighted in search results");
        toolbar.search(fileDescription);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFileByDescriptionAdvancedSearch()
    {
        LOG.info("Step 1: Navigate to advanced search page, search for file description, check that file description is highlighted in search results");
        advancedSearchPage.navigateByMenuBar();
        advancedSearchPage.typeDescription(fileDescription);
        advancedSearchPage.click2ndSearchButton();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByDescription()
    {
        LOG.info("Step 1: Search for folder description, check that folder description is highlighted in search results");
        toolbar.search(description);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isDescriptionHighlighted(description), description + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchFolderByDescriptionAdvancedSearch()
    {
        LOG.info("Step 1: Navigate to advanced search page, search for file description, check that file description is highlighted in search results");
        advancedSearchPage.navigateByMenuBar();
        advancedSearchPage.clickOnLookForDropdown();
        advancedSearchPage.clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"));
        advancedSearchPage.typeDescriptionText(description);
        advancedSearchPage.click2ndSearchButton();
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertTrue(searchPage.isDescriptionHighlighted(description), description + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyName()
    {
        LOG.info("Step 1: Search by name: property and check that name is highlighted on search results");
        String searchExpression = "name:" + docName;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyTitle()
    {
        LOG.info("Step 1: Search by title: property and check that title is highlighted on search results");
        String searchExpression = "title:" + fileTitle;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyDescription()
    {
        LOG.info("Step 1: Search by description: property and check that description is highlighted on search results");
        String searchExpression = "description:" + fileDescription;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyContent()
    {
        LOG.info("Step 1: Search by content: property and check that content is highlighted on search results");
        String searchExpression = "content:" + docContent;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isContentHighlighted(docContent), docContent + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyCm_Name()
    {
        LOG.info("Step 1: Search by cm_name: property and check that the name is highlighted on search results");
        String searchExpression = "cm_name:" + docName;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByPropertyTEXT()
    {
        LOG.info("Step 1: Search by TEXT: property and check that the name is highlighted on search results");
        String searchExpression = "TEXT:" + docContent;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isContentHighlighted(docContent), docContent + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByWildcardAsterisk()
    {
        LOG.info("Step 1: Search using * and check that the properties are highlighted on search results");
        String searchExpression = "file*";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
        Assert.assertTrue(searchPage.isContentHighlighted(docContent), docContent + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByWildcardsEqual()
    {
        LOG.info("Step 1: Search using = and check that only the name is highlighted on search results");
        String searchExpression = "=" + docName;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
    }

    //@Test(groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testHighlightedSearchByWildcardQuestionMark()
    {
        LOG.info("Step 1: Search using ? and check that the name is highlighted on search results");
        String searchExpression = "??file_";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
        Assert.assertTrue(searchPage.isContentHighlighted(docContent), docContent + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testItemsNotHighlighted()
    {
        LOG.info("Step 1: Search using doc name and confirm that site name, location and username properties are not highlighted");
        toolbar.search(docName);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(docContent), docContent + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testNoItemIsHighlightedSearchByCreatedProperty()
    {
        LOG.info("Step 1: Search by created property and check that no item is highlighted");
        String searchExpression = "created:today";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertTrue(searchPage.isResultFoundWithRetry(folderName));
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(docContent), docContent + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testNoItemIsHighlightedSearchByAsterisk()
    {
        LOG.info("Step 1: Search by * property and check that no item is highlighted");
        String searchExpression = "*";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isAnyFileReturnedInResults(), "No files are returned in results");
        Assert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "No folders are returned in results");
        Assert.assertFalse(searchPage.confirmNoItemIsHighlighted(), "Items are highlighted on the search page");
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(docContent), docContent + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", "Search" })
    public void testGalleryViewIsNotHighlighted()
    {
        LOG.info("Step 1: Search in Gallery view and check that no item is highlighted");
        String searchExpression = "faceted*";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isAnyFileReturnedInResults(), "No files are returned in results");
        Assert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "No folders are returned in results");
        searchPage.clickGalleryView();
        Assert.assertFalse(searchPage.confirmNoItemIsHighlighted(), "Items are highlighted on the search page");
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(docContent), docContent + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
        searchPage.clickDetailedView();
    }
}
