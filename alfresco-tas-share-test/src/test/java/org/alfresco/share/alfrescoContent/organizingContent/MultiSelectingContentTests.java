package org.alfresco.share.alfrescoContent.organizingContent;

import static org.alfresco.po.enums.SelectMenuOptions.ALL;
import static org.alfresco.po.enums.SelectMenuOptions.INVERT_SELECTION;
import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.dataprep.WorkflowService.WorkflowType;
import org.alfresco.po.enums.SelectMenuOptions;
import org.alfresco.po.share.site.DocumentLibraryPage2;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class MultiSelectingContentTests extends BaseTest
{
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    private DocumentLibraryPage2 documentLibraryPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        documentLibraryPage = new DocumentLibraryPage2(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C7546")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectItemsByCheckbox()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFile(testFile).createFolder(testFolder);

        documentLibraryPage.navigate(site.get())
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
        getCmisApi().usingSite(site.get())
            .createFolder(folder)
            .createFile(textFile)
            .createFile(xmlFile);

        documentLibraryPage.navigate(site.get())
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
            .selectOptionFromSelectMenu(INVERT_SELECTION)
            .assertContentsAreChecked(textFile, xmlFile)
            .assertContentsAreNotChecked(folder);
        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(ALL)
            .assertContentsAreChecked(xmlFile, textFile, folder);
        documentLibraryPage.clickSelectMenu()
            .selectOptionFromSelectMenu(SelectMenuOptions.NONE)
            .assertContentsAreNotChecked(xmlFile, textFile, folder);
    }

    @TestRail (id = "C8410")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectMultipleDocumentsAndStartWorkflow()
    {
        FileModel firstFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FileModel secondFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().usingSite(site.get())
            .createFile(firstFile)
            .createFile(secondFile);

        documentLibraryPage.navigate(site.get())
            .checkContent(firstFile, secondFile)
            .clickSelectedItems()
            .clickStartWorkflowFromSelectedItems()
            .selectWorkflowType(WorkflowType.NewTask)
            .assertItemsAreDisplayed(firstFile, secondFile);
    }

    @TestRail (id = "C7554")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifySelectedItemsDelete()
    {
        FileModel testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        FolderModel testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFile(testFile).createFolder(testFolder);

        documentLibraryPage.navigate(site.get())
            .checkContent(testFolder, testFile)
            .clickSelectedItems()
            .clickDeleteFromSelectedItems()
            .confirmDeletion();

        documentLibraryPage.usingContent(testFile).assertContentIsNotDisplayed();
        documentLibraryPage.usingContent(testFolder).assertContentIsNotDisplayed();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}