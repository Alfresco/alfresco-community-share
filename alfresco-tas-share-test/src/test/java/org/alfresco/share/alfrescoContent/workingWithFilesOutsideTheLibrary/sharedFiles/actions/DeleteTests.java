package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class DeleteTests extends BaseTest
{
    private SharedFilesPage sharedFilesPage;
    private DeleteDialog deleteDialog;
    private UserModel testUser1;
    private UserModel testUser2;
    private FileModel testFile;
    private FolderModel testFolder;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {

        sharedFilesPage = new SharedFilesPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());
    }

    @TestRail (id = "C8014")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void deleteDocument() throws Exception {
        log.info("PreCondition: Create a File in"+ testUser1 +"Shared Files Folder ");
        authenticateUsingLoginPage(testUser1);

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingShared().createFile(testFile).assertThat().existsInRepo();
        authenticateUsingCookies(testUser1);

        log.info("PreCondition: Login using Admin user & Navigate to Shared Files Folder ");
        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .isFileDisplayed(testFile.getName());

        log.info("STEP1: Hover over the file you want to delete");
        log.info("STEP2: Click 'More' menu -> \"Delete Document\"");
        sharedFilesPage
            .selectItemAction(testFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .assertConfirmDeleteMessageForContentEqualsTo(testFile.getName())
            .assertDeleteButtonIsDisplayed()
            .assertCancelButtonIsDisplayed();

        log.info("STEP3: Press \"Delete\"");
        deleteDialog
            .confirmDeletion();
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsNotDisplayed(testFile.getName());
    }

    @TestRail (id = "C8015")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void deleteFolder() throws Exception {

        log.info("PreCondition: Create a Folder in"+ testUser1 +"Shared Files Folder ");
        authenticateUsingLoginPage(testUser1);
        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingShared().createFolder(testFolder).assertThat().existsInRepo();

        log.info("PreCondition: Login using Admin user & Navigate to Shared Files Folder ");
        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .isFileDisplayed(testFolder.getName());

        log.info("STEP1: Hover over the file you want to delete and press \"More\"");
        log.info("STEP2: Press \"Delete Folder\"");
        sharedFilesPage
            .selectItemAction(testFolder.getName(), ItemActions.DELETE_FOLDER);

        deleteDialog
            .assertConfirmDeleteMessageForContentEqualsTo(testFolder.getName())
            .assertDeleteButtonIsDisplayed()
            .assertCancelButtonIsDisplayed();

        log.info("STEP3: Press \"Delete\"");
        deleteDialog
            .confirmDeletion();
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsNotDisplayed(testFolder.getName());
    }

    @TestRail (id = "C13759")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void optionNotDisplayed() throws Exception {
        log.info("PreCondition: Create a File & Folder in"+ testUser2 +"Shared Files Folder ");
        authenticateUsingLoginPage(testUser2);

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingShared().createFile(testFile).assertThat().existsInRepo();
        authenticateUsingCookies(testUser2);

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingShared().createFolder(testFolder).assertThat().existsInRepo();

        log.info("PreCondition: Login using"+ testUser1 +"& Navigate to Shared Files Folder ");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .isFileDisplayed(testFile.getName());

        log.info("STEP1: Hover over " + testFile);
        sharedFilesPage
            .assertActionItem_Not_AvailableInTheDocumentLibraryItems(testFile.getName(),ItemActions.DELETE_DOCUMENT);

        log.info("STEP2: Hover over " + testFolder);
        sharedFilesPage
            .assertActionItem_Not_AvailableInTheDocumentLibraryItems(testFolder.getName(), ItemActions.DELETE_FOLDER);

        log.info("Delete the Created File & Folder");
        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testFile.getName(),ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();

        sharedFilesPage
            .selectItemAction(testFolder.getName(),ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
    }
}