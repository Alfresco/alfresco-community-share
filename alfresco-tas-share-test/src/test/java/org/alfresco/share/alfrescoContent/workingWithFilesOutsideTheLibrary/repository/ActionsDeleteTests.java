package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
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
public class ActionsDeleteTests extends BaseTest
{
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;
    private HeaderMenuBar headerMenuBar;
    private UserModel testUser1;
    private FileModel testFile;
    private FileModel testFileC13749;
    private FileModel testFileC13751;
    private FolderModel testFolder;
    private FolderModel testFolderC13749;
    private FolderModel testFolderC13751;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {

        repositoryPage = new RepositoryPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        headerMenuBar = new HeaderMenuBar(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create a Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingUserHome(testUser1.getUsername()).createFolder(testFolder).assertThat().existsInRepo();

        testFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingAdmin().usingUserHome(testUser1.getUsername()).createFile(testFile).assertThat().existsInRepo();
        authenticateUsingCookies(getAdminUser());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }

    @TestRail (id = "C8308")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void deleteDocument()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername())
            .assertFileIsDisplayed(testFile.getName());

        log.info("Step 1: Hover over the file you want to delete and press More, select Delete Document");
        repositoryPage
            .select_ItemsAction(testFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .assertConfirmDeleteMessageForContentEqualsTo(testFile.getName())
            .assertDeleteButtonIsDisplayed()
            .assertCancelButtonIsDisplayed();

        log.info("Step 2: Press \"Delete\"");
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .navigateByMenuBar()
            .assertFileIsNotDisplayed(testFile.getName());
    }

    @TestRail (id = "C8309")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void deleteFolder()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername())
            .assertFileIsDisplayed(testFolder.getName());

        log.info("Step 1: Hover over the file you want to delete and press More, select Delete Document");
        repositoryPage
            .select_ItemsAction(testFolder.getName(), ItemActions.DELETE_FOLDER);
        deleteDialog
            .assertConfirmDeleteMessageForContentEqualsTo(testFolder.getName())
            .assertDeleteButtonIsDisplayed()
            .assertCancelButtonIsDisplayed();

        log.info("Step 2: Press \"Delete\"");
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .navigateByMenuBar()
            .assertFileIsNotDisplayed(testFolder.getName());
    }

    @TestRail (id = "C13749")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void nonAdminUserCanNotDeleteFileOrFolderFromTheMainRepository() throws Exception {

        log.info("PreCondition: Any File & Folder is Created in Admin Repository ");
        testFileC13749 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingRoot().createFile(testFileC13749).assertThat().existsInRepo();

        testFolderC13749 = FolderModel.getRandomFolderModel();
        getCmisApi().usingRoot().createFolder(testFolderC13749).assertThat().existsInRepo();

        log.info("PreCondition: User1 is logged into share & Navigate to Repository page  ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage.navigate();

        log.info("Step 1: Mouse over file name and check that the More and the Delete Document option is not available");
        repositoryPage
            .mouseOverContentItem(testFileC13749.getName());
        repositoryPage
            .assertIsMoreMenuNotDisplayed(testFileC13749.getName())
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFileC13749.getName(),ItemActions.DELETE_DOCUMENT);

        log.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder option is not available");
        repositoryPage
            .mouseOverContentItem(testFolderC13749.getName());
        repositoryPage
            .assertIsMoreMenuNotDisplayed(testFolderC13749.getName())
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFolderC13749.getName(),ItemActions.DELETE_FOLDER);

        log.info("Delete the Created File & Folder from Admin Page");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigateByMenuBar()
            .select_ItemsAction(testFileC13749.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .select_ItemsAction(testFolderC13749.getName(),ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C13751")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void adminUserCanDeleteFileOrFolderInMainRepository() throws Exception {

        log.info("PreCondition: Any File & Folder is Created in Admin Repository ");
        testFileC13751 = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingRoot().createFile(testFileC13751).assertThat().existsInRepo();

        testFolderC13751 = FolderModel.getRandomFolderModel();
        getCmisApi().usingRoot().createFolder(testFolderC13751).assertThat().existsInRepo();

        log.info("PreCondition: Admin is logged into share & Navigate to Repository page  ");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage.navigate();

        log.info("Step 1: Mouse over file name and check that the More and the  Delete Document options are available");
        repositoryPage
            .mouseOverContentItem(testFileC13751.getName());
        repositoryPage
            .assertIsMoreMenuDisplayed(testFileC13751.getName())
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFileC13751.getName(),ItemActions.DELETE_DOCUMENT);

        log.info("Step 2: Mouse over folder name and check that the More and the  Delete Folder options are available");
        repositoryPage
            .mouseOverContentItem(testFolderC13751.getName());
        repositoryPage
            .assertIsMoreMenuDisplayed(testFolderC13751.getName())
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFolderC13751.getName(),ItemActions.DELETE_FOLDER);

        log.info("Delete the Created File & Folder from Admin Page");
        repositoryPage
            .navigateByMenuBar().select_ItemsAction(testFileC13751.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
        repositoryPage
            .select_ItemsAction(testFolderC13751.getName(),ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();
    }
}
