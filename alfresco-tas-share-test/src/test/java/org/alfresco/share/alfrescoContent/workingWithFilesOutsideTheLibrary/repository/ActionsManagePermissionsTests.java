package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.alfresco.common.Utils.srcRoot;

import java.io.File;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Mirela Tifui on 3/20/2017.
 */
public class ActionsManagePermissionsTests extends BaseTest
{
    RepositoryPage repositoryPage;
    ManagePermissionsPage managePermissionsPage;
    UploadContent uploadContent;
    EditInAlfrescoPage editInAlfrescoPage;
    DocumentDetailsPage documentDetailsPage;
    DeleteDialog deleteDialog;
    private CreateContentPage createContentPage;
    private NewFolderDialog newFolderDialog;
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private String subFolder = "Subfolder" + RandomData.getRandomAlphanumeric();
    private UserModel testUser1;
    private UserModel testUser2;
    private FolderModel testFolder;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);
        managePermissionsPage = new ManagePermissionsPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        createContentPage = new CreateContentPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().createFolder(testFolder).assertThat().existsInRepo();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);

        log.info("Delete the Created Folder from Admin Page");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigateByMenuBar()
            .select_ItemsAction(testFolder.getName(),ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C202757")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void managePermissionOption()
    {
        authenticateUsingLoginPage(testUser1);
        log.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");

        log.info("Step 2: Navigate to 'User Homes' folder");
        repositoryPage
            .click_FolderName("User Homes")
            .assertFileIsDisplayed(testUser1.getUsername());

        log.info("Step 3: Click Manage Permissions link in More menu for user's home folder;");
        repositoryPage
            .select_ItemsAction(testUser1.getUsername(), ItemActions.MANAGE_REPO_PERMISSIONS);

        log.info("Step 4: Verify Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.isInheritPermissionsButtonDisplayed(),"Inherit Permissions button is not displayed");
        Assert.assertTrue(managePermissionsPage.isAddUserGroupButtonDisplayed(), "Add User/Group button is not displayed");
        Assert.assertTrue(managePermissionsPage.isTheSaveButtonDisplayed(), "The Save button is not displayed");
        Assert.assertTrue(managePermissionsPage.isCancelButtonDisplayed(), "The Cancel button is not displayed");
        Assert.assertTrue(managePermissionsPage.isLocallySetPermissionsListDisplayed(), "Locally Set Permissions is not displayed on the Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.getRowDetails(testUser1.getUsername()).contains("All"), "All Role is not available for " +testUser1.getUsername());
        Assert.assertTrue(managePermissionsPage.getRowDetails("ROLE_OWNER").contains("All"), "All Role is not available for ROLE_OWNER");
        Assert.assertTrue(managePermissionsPage.isDeleteButtonAvailable(testUser1.getUsername()), "Delete button is not available for " + testUser1.getUsername());
    }

    @TestRail (id = "C202758")
    @Test
    public void savingChanges()
    {

        log.info("Precondition: Assign Role as Coordinator for" +testUser1 +testUser2 +"for the created folder" );
        repositoryPage
            .navigateByMenuBar()
            .select_ItemsAction(testFolder.getName(), ItemActions.MANAGE_REPO_PERMISSIONS);
        managePermissionsPage
            .clickAddUserGroupButton()
            .sendSearchInput(testUser1.getUsername())
            .clickSearchButton()
            .clickAddButtonForUser(testUser1.getUsername())
            .clickRoleButton(testUser1.getUsername())
            .select_Role("Coordinator")
            .clickAddUserGroupButton()
            .sendSearchInput(testUser2.getUsername())
            .clickSearchButton()
            .clickAddButtonForUser(testUser2.getUsername())
            .clickRoleButton(testUser2.getUsername())
            .select_Role("Coordinator")
            .clickSave();

        log.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");

        log.info("Step 2: Click Manage Permissions link in More menu for " + testFolder + " folder");
        repositoryPage
            .select_ItemsAction(testFolder.getName(), ItemActions.MANAGE_REPO_PERMISSIONS);
        Assert.assertTrue(managePermissionsPage.getRowDetails(testUser1.getUsername()).contains("Coordinator"));
        Assert.assertTrue(managePermissionsPage.getRowDetails(testUser2.getUsername()).contains("Coordinator"));

        log.info("Step 3: Change User2 role to \"Consumer\"");
        managePermissionsPage
            .clickRoleButton(testUser2.getUsername())
            .select_Role("Consumer");
        Assert.assertTrue(managePermissionsPage.getRowDetails(testUser2.getUsername()).contains("Consumer"));

        log.info("Step 4: Click Save");
        managePermissionsPage
            .clickSave();

        log.info("Step 5: Upload file into " +testFolder);
        repositoryPage
            .navigateByMenuBar()
            .isFileNameDisplayed(testFolder.getName());
        repositoryPage
            .clickOnFolderName(testFolder.getName());
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isFileNameDisplayed(testFile);

        log.info("Step 6: Log in User2");
        authenticateUsingLoginPage(testUser2);
        repositoryPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");

        log.info("Step 7: Open " + testFolder + " folder and try to edit/delete uploaded file");
        repositoryPage
            .clickOnFolderName(testFolder.getName())
            .assertFileIsDisplayed(testFile);
        repositoryPage
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.DELETE_DOCUMENT)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.EDIT_PROPERTIES)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.EDIT_IN_GOOGLE_DOCS)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.EDIT_IN_MICROSOFT_OFFICE)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.EDIT_OFFLINE)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.UPLOAD_NEW_VERSION)
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFile,ItemActions.MANAGE_REPO_PERMISSIONS);
    }

    @TestRail (id = "C202776")
    @Test
    public void inheritPermissionsButton()
    {
        String updatedContent = "Updated test content for C202776";

        log.info("PreCondition: Set user1 Rights to Coordinator for" +testFolder);
        repositoryPage
            .navigateByMenuBar()
            .select_ItemsAction(testFolder.getName(), ItemActions.MANAGE_REPO_PERMISSIONS);
        managePermissionsPage
            .clickAddUserGroupButton()
            .sendSearchInput(testUser1.getUsername())
            .clickSearchButton()
            .clickAddButtonForUser(testUser1.getUsername())
            .clickRoleButton(testUser1.getUsername())
            .select_Role("Coordinator")
            .clickSave();

        log.info("PreCondition: Login with testUser1 & Create"+ subFolder +"inside" + testFolder +"and upload testFile inside SubFolder");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .clickOnFolderName(testFolder.getName())
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(subFolder)
            .clickSave();
        repositoryPage
            .clickOnFolderName(subFolder);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isFileNameDisplayed(testFile);

        log.info("Step 1: Navigate to Repository");
        repositoryPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Repository Browser");

        log.info("Step 2: On the Repository page click on TestFolder");
        repositoryPage
            .clickOnFolderName(testFolder.getName());

        log.info("Step 3: Mouseover TestSubfolder and click on Manage Permissions action");
        repositoryPage
            .select_ItemsAction(subFolder, ItemActions.MANAGE_REPO_PERMISSIONS);

        log.info("Step 4: Check inherited permissions");
        Assert.assertTrue(managePermissionsPage.getInheritedPermissions(testUser1.getUsername()).contains("Coordinator"));

        log.info("Step 5: Return to Repository, TestSubfolder and check available actions for TestFile");
        repositoryPage
            .navigateByMenuBar()
            .clickOnFolderName(testFolder.getName())
            .clickOnFolderName(subFolder)
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFile, ItemActions.EDIT_IN_GOOGLE_DOCS)
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFile, ItemActions.EDIT_PROPERTIES)
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFile, ItemActions.EDIT_IN_ALFRESCO)
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFile, ItemActions.EDIT_OFFLINE)
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFile, ItemActions.DELETE_DOCUMENT);

        log.info("Step 6: Edit TestFile and save changes");
        repositoryPage
            .select_ItemsAction(testFile, ItemActions.EDIT_IN_ALFRESCO)
            .assertBrowserPageTitleIs("Alfresco » Edit in Alfresco Share");
        editInAlfrescoPage
            .typeContent(updatedContent)
            .clickSaveButton();
        repositoryPage
            .clickOnFile(testFile);
        documentDetailsPage
            .assertFileContentEquals(updatedContent);
    }
}