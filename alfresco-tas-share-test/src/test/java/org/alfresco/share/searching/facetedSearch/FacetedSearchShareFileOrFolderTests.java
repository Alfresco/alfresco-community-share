package org.alfresco.share.searching.facetedSearch;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.dialogWindows.ConfirmDeletionDialogForShare;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.alfresco.share.TestUtils.FILE_CONTENT1;

@Slf4j
/**
 * Created by Mirela Tifui on 12/27/2017.
 */
public class FacetedSearchShareFileOrFolderTests extends BaseTest
{
    SearchPage searchPage;
    Toolbar toolbar;
    AdvancedSearchPage advancedSearchPage;
    DocumentLibraryPage documentLibraryPage;
    ConfirmDeletionDialogForShare confirmDeletionDialog;
    EditPropertiesDialog editPropertiesDialog;
    private String testSite;
    private FileModel testFile;
    private FolderModel testFolder;
    private DeleteDialog deleteDialog;
    private String docName = "facetedSearchDoc" + RandomData.getRandomAlphanumeric();
    private String fileName = "facetedSearchDoc_" + RandomData.getRandomAlphanumeric();
    private String docContent = "file_content";
    private String folderName = "facetedFolder_" + RandomData.getRandomAlphanumeric();
    private String folderTitle = "title_" + RandomData.getRandomAlphanumeric();
    private String description = "This_is_a_test_folder";
    private String fileTitle = "file_" + RandomData.getRandomAlphanumeric();
    private String fileDescription = "file_description";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void testSetup()
    {
        log.info("Precondition1: Any test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());

        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        testSite = site.get().getTitle();

        log.info("Precondition4: Creating random files in the site under document library.");
        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT1);
        getCmisApi().authenticateUser(user.get()).usingSite(site.get()).createFile(testFile).assertThat().existsInRepo();

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(testFolder).assertThat().existsInRepo();

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        advancedSearchPage = new AdvancedSearchPage(webDriver);
        confirmDeletionDialog = new ConfirmDeletionDialogForShare(webDriver);

        documentLibraryPage.navigate(testSite);
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(testFolder.getName(), ItemActions.EDIT_PROPERTIES);
        editPropertiesDialog.setName(folderName);
        editPropertiesDialog.setTitle(folderTitle);
        editPropertiesDialog.setDescription(description);
        editPropertiesDialog.clickSave();
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(testFile.getName(), ItemActions.EDIT_PROPERTIES);
        editPropertiesDialog.setName(fileName);
        editPropertiesDialog.setTitle(fileTitle);
        editPropertiesDialog.setDescription(fileDescription);
        editPropertiesDialog.clickSave();
    }

    @AfterMethod(alwaysRun = true)
    public void testCleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @Test(groups = { TestGroup.SHARE, "Acceptance", TestGroup.SEARCH })
    public void testSelectActionDeleteConfirmCancel()
    {
        toolbar
            .search(fileName);
        searchPage
            .assertCreatedDataIsDisplayed(fileName);

        log.info("Step 1: Select document and select Delete, on the Confirm deletion dialog click close to close dialog");
        searchPage
            .clickCheckbox(fileName)
            .clickSelectedItemsDropdown()
            .clickOptionFromSelectedItemsDropdown("Delete");
        confirmDeletionDialog
            .clickCloseButton();
        searchPage
            .assertConfirmDeletionDialogNotDisplayed();
        toolbar
            .search(fileName);
        searchPage
            .assertCreatedDataIsDisplayed(fileName);

        log.info("Step 2: Select document and select Delete, on the Confirm deletion dialog click cancel to cancel deletion");
        searchPage
            .clickCheckbox(fileName)
            .clickSelectedItemsDropdown()
            .clickOptionFromSelectedItemsDropdown("Delete");
        confirmDeletionDialog
            .clickCancelButton();
        searchPage
            .assertConfirmDeletionDialogNotDisplayed();
        toolbar
            .search(fileName).assertCreatedDataIsDisplayed(fileName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedSearchFileByName()
    {
        log.info("Step 1: Search by name and check that document name is highlighted in search results");
        toolbar
            .search(fileName);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsNameHighlighted(fileName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH })
    public void testHighlightedSearchFileByNameAdvancedSearch()
    {
        log.info("Step 1: Navigate to advanced search page and search for document by document name, check that name is highlighted in search results");
        advancedSearchPage
            .navigate()
            .typeName(fileName);
        advancedSearchPage
            .clickSecondSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsNameHighlighted(fileName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH }, enabled = true)
    public void testHighlightedSearchFolderByNameSearch()
    {
        log.info("Step 1: On the search page search for folder by name and check that folder name is highlighted");
        toolbar
            .search(folderName);
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsNameHighlighted(folderName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH }, enabled = true)
    public void testHighlightedSearchFolderByNameAdvancedSearch()
    {
        log.info("Step 1: On the advanced search search by folder name and check that folder is returned in search results and that the folder name is highlighted");
        advancedSearchPage
            .navigateByMenuBar()
            .clickOnLookForDropdown()
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"))
            .typeNameFolder(folderName).clickFirstSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsNameHighlighted(folderName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFolderByTitleSearch()
    {
        log.info("Step 1: Navigate to search page and search for folder name, check that folder name is highlighted in search results");
        toolbar
            .search(folderTitle);
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsTitleHighlighted(folderTitle);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFolderByTitleAdvancedSearch()
    {
        log.info("Step 1: On the advanced search, search by folder title and check that folder is returned in search results and that the folder title is highlighted");
        advancedSearchPage
            .navigateByMenuBar()
            .clickOnLookForDropdown()
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"))
            .folderTypeTitle(folderTitle);
        advancedSearchPage
            .clickSecondSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsTitleHighlighted(folderTitle);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFileByTitleAdvancedSearch()
    {
        log.info("Step 1: On the advanced search, search by file title and check that file is returned in search results and that the file title is highlighted");
        advancedSearchPage
            .navigateByMenuBar()
            .typeTitle(fileTitle)
            .clickSecondSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsTitleHighlighted(fileTitle);

    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFileByTitleSearch()
    {
        log.info("Step 1: Search for file name, check that file name is highlighted in search results");
        toolbar
            .search(fileTitle);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsTitleHighlighted(fileTitle);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFileByDescription()
    {
        log.info("Step 1: Search for file description, check that file description is highlighted in search results");
        toolbar
            .search(fileDescription);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsDescriptionHighlighted(fileDescription);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFileByDescriptionAdvancedSearch()
    {
        log.info("Step 1: Navigate to advanced search page, search for file description, check that file description is highlighted in search results");
        advancedSearchPage
            .navigateByMenuBar()
            .typeDescription(fileDescription)
            .clickSecondSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsDescriptionHighlighted(fileDescription);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFolderByDescription()
    {
        log.info("Step 1: Search for folder description, check that folder description is highlighted in search results");
        toolbar
            .search(description);
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsDescriptionHighlighted(description);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchFolderByDescriptionAdvancedSearch()
    {
        log.info("Step 1: Navigate to advanced search page, search for file description, check that file description is highlighted in search results");
        advancedSearchPage
            .navigateByMenuBar()
            .clickOnLookForDropdown()
            .clickOnLookForDropdownOption(language.translate("advancedSearchPage.lookForDropDown.folders.label"))
            .folderTypeDescription(description)
            .clickSecondSearchButton();
        searchPage
            .assertCreatedDataIsDisplayed(folderName)
            .assertIsDescriptionHighlighted(description);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyName()
    {
        log.info("Step 1: Search by name: property and check that name is highlighted on search results");
        String searchExpression = "name:" + fileName;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsNameHighlighted(fileName);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyTitle()
    {
        log.info("Step 1: Search by title: property and check that title is highlighted on search results");
        String searchExpression = "title:" + fileTitle;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsTitleHighlighted(fileTitle);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyDescription()
    {
        log.info("Step 1: Search by description: property and check that description is highlighted on search results");
        String searchExpression = "description:" + fileDescription;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsDescriptionHighlighted(fileDescription);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyContent()
    {
        log.info("Step 1: Search by content: property and check that content is highlighted on search results");
        String searchExpression = "content:" + FILE_CONTENT1;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsContentHighlighted(FILE_CONTENT1);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyCm_Name()
    {
        log.info("Step 1: Search by cm_name: property and check that the name is highlighted on search results");
        String searchExpression = "cm_name:" + fileName;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsNameHighlighted(fileName);
//        Assert.assertTrue(searchPage.isResultFoundWithList(fileName));
//        Assert.assertTrue(searchPage.isNameHighlighted(fileName), fileName + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testHighlightedSearchByPropertyTEXT()
    {
        log.info("Step 1: Search by TEXT: property and check that the name is highlighted on search results");
        String searchExpression = "TEXT:" + FILE_CONTENT1;
        toolbar
            .search(searchExpression);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsContentHighlighted(FILE_CONTENT1);
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "SinglePipelineFailure" },enabled = true)
    public void testHighlightedSearchByWildcardAsterisk() {
        log.info("Step 1: Search using * and check that the properties are highlighted on search results");
        String searchExpression = "file*";
        toolbar
            .search(searchExpression);
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(fileName)
            .assertNameNotHighlighted(fileName)
            .assertIsTitleHighlighted(fileTitle)
            .assertIsDescriptionHighlighted(fileDescription)
            .assertIsContentHighlighted(FILE_CONTENT1);
    }

    //@Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = false)
    public void testHighlightedSearchByWildcardsEqual()
    {
        log.info("Step 1: Search using = and check that only the name is highlighted on search results");
        String searchExpression = "=" + fileName;
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithList(fileName));
        Assert.assertTrue(searchPage.isNameHighlighted(fileName), fileName + " is not highlighted");
    }

    //@Test(groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH ,enabled = false})
    public void testHighlightedSearchByWildcardQuestionMark()
    {
        log.info("Step 1: Search using ? and check that the name is highlighted on search results");
        String searchExpression = "??file_";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithRetry(docName));
        Assert.assertFalse(searchPage.isNameHighlighted(docName), docName + " is not highlighted");
        Assert.assertTrue(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is not highlighted");
        Assert.assertTrue(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is not highlighted");
        Assert.assertTrue(searchPage.isContentHighlighted(docContent), docContent + " is not highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testItemsNotHighlighted()
    {
        log.info("Step 1: Search using doc name and confirm that site name, location and username properties are not highlighted");
        toolbar
            .search(fileName);
        searchPage
            .assertCreatedDataIsDisplayed(fileName)
            .assertIsNameHighlighted(fileName);
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(FILE_CONTENT1), FILE_CONTENT1 + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testNoItemIsHighlightedSearchByCreatedProperty() {
        log.info("Step 1: Search by created property and check that no item is highlighted");
        String searchExpression = "created:today";
        toolbar
            .search(searchExpression);
        searchPage
            .clickSortDropDown_CreatedDate()
            .assertCreatedDataIsDisplayed(fileName)
            .assertCreatedDataIsDisplayed(folderName);
        Assert.assertFalse(searchPage.isNameHighlighted(fileName), fileName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(FILE_CONTENT1), FILE_CONTENT1 + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH },enabled = true)
    public void testNoItemIsHighlightedSearchByAsterisk() {
        log.info("Step 1: Search by * property and check that no item is highlighted");
        String searchExpression = "*";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isAnyFileReturnedInResults(), "No files are returned in results");
        Assert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "No folders are returned in results");
        Assert.assertFalse(searchPage.confirmNoItemIsHighlighted(), "Items are highlighted on the search page");
        Assert.assertFalse(searchPage.isNameHighlighted(fileName), fileName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(FILE_CONTENT1), FILE_CONTENT1 + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
    }

    @Test (groups = { TestGroup.SHARE, "Regression", TestGroup.SEARCH, "Searching" },enabled = true)
    public void testGalleryViewIsNotHighlighted() {
        log.info("Step 1: Search in Gallery view and check that no item is highlighted");
        String searchExpression = "faceted*";
        toolbar.search(searchExpression);
        Assert.assertTrue(searchPage.isResultFoundWithList(fileName));
        Assert.assertTrue(searchPage.isAnyFileReturnedInResults(), "No files are returned in results");
        Assert.assertTrue(searchPage.isAnyFolderReturnedInResults(), "No folders are returned in results");
        searchPage.clickGalleryView();
        Assert.assertFalse(searchPage.confirmNoItemIsHighlighted(), "Items are highlighted on the search page");
        Assert.assertFalse(searchPage.isNameHighlighted(fileName), fileName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(fileTitle), fileTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(fileDescription), fileDescription + " is highlighted");
        Assert.assertFalse(searchPage.isContentHighlighted(FILE_CONTENT1), FILE_CONTENT1 + " is highlighted");
        Assert.assertFalse(searchPage.isSiteHighlighted(), "Site name is highlighted");
        Assert.assertFalse(searchPage.isModifiedOnHighlighted(), "Modified on details are highlighted");
        Assert.assertFalse(searchPage.isNameHighlighted(folderName), folderName + " is highlighted");
        Assert.assertFalse(searchPage.isTitleHighlighted(folderTitle), folderTitle + " is highlighted");
        Assert.assertFalse(searchPage.isDescriptionHighlighted(description), description + " is highlighted");
        searchPage.clickDetailedView();
    }
}
