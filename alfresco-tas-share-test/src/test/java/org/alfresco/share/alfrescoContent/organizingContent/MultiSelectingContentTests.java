package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.dataprep.WorkflowService.WorkflowType;
import org.alfresco.po.share.alfrescoContent.AlfrescoContentPage.SelectMenuOptions;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MultiSelectingContentTests extends BaseTest
{
    private static final String FILE_CONTENT = "Share file content";

    private UserModel user;
    private SiteModel site;

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(browser);
        setupAuthenticatedSession(user);
    }

    @TestRail (id = "C7546")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectItemsByCheckbox()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site).createFile(testFile).createFolder(testFolder);

        documentLibraryPage.navigate(site)
            .checkContent(testFolder)
            .assertSelectedItemsMenuIsEnabled()
            .checkContent(testFolder)
            .assertSelectedItemsMenuIsDisabled()
            .checkContent(testFile)
            .checkContent(testFolder)
            .assertSelectedItemsMenuIsEnabled();
    }

    @TestRail (id = "C7548")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectItemsByMenu()
    {
        FileModel textFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel xmlFile = FileModel.getRandomFileModel(FileType.XML, FILE_CONTENT);
        FolderModel folder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site)
            .createFolder(folder)
            .createFile(textFile)
            .createFile(xmlFile);

        documentLibraryPage.navigate(site)
            .clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.DOCUMENTS)
            .assertContentsAreChecked(xmlFile, textFile)
            .assertContentsAreNotChecked(folder)
            .assertSelectedItemsMenuIsEnabled()
            .clickSelectedItems()
            .assertActionsInSelectedItemsMenuEqualTo(
                language.translate("documentLibrary.contentActions.downloadAsZip"),
                language.translate("documentLibrary.contentActions.copyTo"),
                language.translate("documentLibrary.contentActions.moveTo"),
                language.translate("documentLibrary.contentActions.startWorkflow"),
                language.translate("documentLibrary.selectedItemsMenu.delete"),
                language.translate("documentLibrary.selectedItemsMenu.deselectAll"));

        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.FOLDERS)
            .assertContentsAreChecked(folder)
            .assertContentsAreNotChecked(xmlFile, textFile)
            .assertSelectedItemsMenuIsEnabled()
            .clickSelectedItems()
            .assertActionsInSelectedItemsMenuEqualTo(
                language.translate("documentLibrary.contentActions.downloadAsZip"),
                language.translate("documentLibrary.contentActions.copyTo"),
                language.translate("documentLibrary.contentActions.moveTo"),
                language.translate("documentLibrary.selectedItemsMenu.delete"),
                language.translate("documentLibrary.selectedItemsMenu.deselectAll"));

        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.INVERT_SELECTION)
            .assertContentsAreChecked(textFile, xmlFile)
            .assertContentsAreNotChecked(folder);
        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.ALL)
            .assertContentsAreChecked(xmlFile, textFile, folder);
        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.NONE)
            .assertContentsAreNotChecked(xmlFile, textFile, folder);
    }

    @TestRail (id = "C8410")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectMultipleDocumentsAndStartWorkflow()
    {
        FileModel file1 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel file2 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site)
            .createFile(file1)
            .createFile(file2);

        documentLibraryPage.navigate(site)
            .checkContent(file1, file2)
            .clickSelectedItems()
            .clickStartWorkflowFromSelectedItems()
            .selectWorkflowType(WorkflowType.NewTask)
            .assertItemsAreDisplayed(file1, file2);
    }

    @TestRail (id = "C7554")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectedItemsDelete()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site).createFile(testFile).createFolder(testFolder);

        documentLibraryPage.navigate(site)
            .checkContent(testFolder, testFile)
            .clickSelectedItems()
            .clickDeleteFromSelectedItems()
            .clickDelete();

        documentLibraryPage.usingContent(testFile).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(testFolder).assertContentIsNotDisplayed();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        deleteSites(site);
    }
}