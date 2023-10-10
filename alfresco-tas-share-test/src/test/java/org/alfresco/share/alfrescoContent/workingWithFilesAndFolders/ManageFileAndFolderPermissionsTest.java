package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
@Slf4j
public class ManageFileAndFolderPermissionsTest extends BaseTest
{
    private DocumentLibraryPage documentLibraryPage;
    private ManagePermissionsPage managePermissionsPage;

    private FolderModel folderToCheck;
    private FileModel fileToCheck;

    private UserModel testUser1;
    private UserModel testUser2;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Creating a testuser1 and site1 created by user1");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        site.set(getDataSite().usingUser(testUser1).createPublicRandomSite());

        log.info("Creating another user testuser2");
        testUser2 = dataUser.usingAdmin().createRandomTestUser();

        getCmisApi().authenticateUser(testUser1);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        managePermissionsPage = new ManagePermissionsPage(webDriver);

        log.info("Create Folder and File in document library.");
        folderToCheck = FolderModel.getRandomFolderModel();
        getCmisApi().usingSite(site.get()).createFolder(folderToCheck).assertThat().existsInRepo();

        fileToCheck = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, "description");
        getCmisApi().usingSite(site.get()).createFile(fileToCheck).assertThat().existsInRepo();
        authenticateUsingCookies(testUser1);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }

    @TestRail (id = "C6092")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelManagePermissions()
    {
        log.info("Navigate to the Site Document library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("STEP1: On the Document Library page click on Manage Permissions option for the file.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP2: On the Manage Permissions page check the header title is correct."
            + "STEP3: On the Manage Permissions page click the Cancel button and return to Document Library page."
            + "And again go to the manage permission page for the file and verify that permission should not be added.");
        managePermissionsPage
            .assertManagePermissionPageHeaderTitleEquals(fileToCheck.getName())
            .clickCancel();

        documentLibraryPage
            .assertFileIsDisplayed(fileToCheck.getName())
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername())
            .clickCancel();

        log.info("Step3: Repeat Step1 and Step2 for folder");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("On the Manage Permissions page check the header title is correct."
            + "And On the Manage Permissions page click the Cancel button and return to Document Library page."
            + "And again go to the manage permission page for the folder and verify that permission should not be added.");
        managePermissionsPage
            .assertManagePermissionPageHeaderTitleEquals(folderToCheck.getName())
            .clickCancel();

        documentLibraryPage
            .assertFileIsDisplayed(folderToCheck.getName())
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername());
    }

    @TestRail (id = "C7121")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void saveManagePermissions()
    {
        log.info("Navigate to the Site Document Library.");
        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("STEP1: On the Document Library page click on Manage Permissions option for the file.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP2: On the Manage Permissions page click on Add User/Group button and add permissions for testUser2.");
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage.clickSave();

        log.info("STEP3: Return to Manage Permissions page for the file and check if permissions were added successfully.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername())
            .clickCancel();

        log.info("STEP4: On the Document Library page click on Manage Permissions option for the folder.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP5: On the Manage Permissions page click on Add User/Group button and add permissions for testUser2.");
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage.clickSave();

        log.info("STEP6: Return to Manage Permissions page for the folder and check if permissions were added successfully.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername());
    }

    @TestRail (id = "C7124")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void turnOffInheritPermissions()
    {
        log.info("Precondition: Navigate to Manage Permissions page for the file.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP1: On the Manage Permissions page click on Inherit Permissions button and verify dialog is displayed.");
        managePermissionsPage
            .clickInheritPermissionsButton();
        managePermissionsPage
            .assertIsTurnOffPermissionInheritanceDialogDisplayed();

        log.info("STEP2: On the Inherit Permissions dialog click on Yes option and verify the Inherit Permissions table is not displayed."
            + "STEP3: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage
            .clickAreYouSureDialog(ManagePermissionsPage.ButtonType.Yes);
        managePermissionsPage
            .assertNoInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusDisabled()
            .clickSave();

        log.info("STEP4: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertNoInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusDisabled()
            .clickCancel();

        log.info("STEP5: On the Document Library page click on Manage Permissions option for the folder.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP6: On the Manage Permissions page click on Inherit Permissions button and verify dialog is displayed.");
        managePermissionsPage
            .clickInheritPermissionsButton();
        managePermissionsPage
            .assertIsTurnOffPermissionInheritanceDialogDisplayed();

        log.info("STEP7: On the Inherit Permissions dialog click on Yes option and verify the Inherit Permissions table is not displayed."
            + "STEP8: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage
            .clickAreYouSureDialog(ManagePermissionsPage.ButtonType.Yes);
        managePermissionsPage
            .assertNoInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusDisabled()
            .clickSave();

        log.info("STEP9: On the Document Library page click on Manage Permissions option for the folder and verify if changes were saved.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);
        managePermissionsPage
            .assertNoInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusDisabled();
    }

    @TestRail (id = "C7125")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void turnOnInheritPermissions()
    {
        log.info("Precondition1: Navigate to Manage Permission page for the file and set Inherit Permissions to off.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .toggleInheritPermissions(false, ManagePermissionsPage.ButtonType.Yes)
            .clickSave();

        log.info("Precondition2: Navigate to Manage Permission page for the folder and set Inherit Permissions to off.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);
        managePermissionsPage
            .toggleInheritPermissions(false, ManagePermissionsPage.ButtonType.Yes)
            .clickSave();

        log.info("STEP1: Navigate to Manage Permissions page for the file and turn on Inherit Permissions and click on Save Button.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .clickInheritPermissionsButton();

        managePermissionsPage
            .assertIsInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusEnabled()
            .clickSave();

        log.info("STEP2: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertIsInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusEnabled()
            .clickCancel();

        log.info("STEP3: Navigate to Manage Permissions page for the folder and turn on Inherit Permissions and click on Save Button.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .clickInheritPermissionsButton();

        managePermissionsPage
            .assertIsInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusEnabled()
            .clickSave();

        log.info("STEP4: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertIsInheritPermissionsTableEnabled()
            .assertIsInheritButtonStatusEnabled();
    }

    @TestRail (id = "C7143")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void deleteUser()
    {
        log.info("Navigate to the Site Document Library and go to the Manage Permission page of file.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("Add user permission as testuser2 for file and click on save button.");
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage.clickSave();

        log.info("Now Add the user permission as testuser2 for folder.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage.clickSave();

        log.info("STEP1: Verify locally set permissions for the file.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername())
            .assertUserRoleAtPermissionPageEquals("Site Contributor", testUser2.getUsername());

        log.info("STEP2: Delete permissions for testUser2 and verify that the permission deleted and then click on Save Button.");
        managePermissionsPage
            .deleteUserOrGroupFromPermission(testUser2.getUsername());
        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername())
            .clickSave();

        log.info("STEP3: Navigate to Manage Permissions page for the folder.");
        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP5: Verify locally set permissions.");
        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername())
            .assertUserRoleAtPermissionPageEquals("Site Contributor", testUser2.getUsername());

        log.info("STEP6: Delete permissions for testUser2.");
        managePermissionsPage
            .deleteUserOrGroupFromPermission(testUser2.getUsername());

        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername());

        log.info("STEP7: Click Save button and then return to Manage Permissions page for the folder.");
        managePermissionsPage.clickSave();

        documentLibraryPage
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);
        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername());

        log.info("STEP8: Login as testUser2 and navigate to Document Library page.");
        authenticateUsingCookies(testUser2);

        documentLibraryPage
            .navigate(site.get().getTitle());

        log.info("STEP9: Navigate to Manage Permissions page and check permissions for file/folder were removed.");
        documentLibraryPage
            .assertActionItem_Not_AvailableInTheDocumentLibraryItems(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS)
            .assertActionItem_Not_AvailableInTheDocumentLibraryItems(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);
    }

    @TestRail (id = "C7123")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void cancelChangesFromManagePermissions()
    {
        log.info("Navigate to the Mange permission page for File.");
        documentLibraryPage
            .navigate(site.get().getTitle())
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP1: On the Manage Permissions page click on Add User/Group button and search for testUser2.");
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername());

        log.info("STEP2: On the Manage Permissions page click the Cancel button and return to Document Library page.");
        managePermissionsPage.clickCancel();

        documentLibraryPage
            .assertFileIsDisplayed(fileToCheck.getName());

        log.info("STEP3: Return to Manage Permissions for the file and verify changes are not saved.");
        documentLibraryPage
            .selectItemAction(fileToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername())
            .clickCancel();

        log.info("STEP4: Navigate to Manage Permissions for the folder.");
        documentLibraryPage
            .assertFileIsDisplayed(folderToCheck.getName())
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        log.info("STEP5: On the Manage Permissions page click on Add User/Group button and search for testUser2."
            + "STEP6: On the Manage Permissions page click the Cancel button and return to Document Library page."
            + "STEP7: Return to Manage Permissions for the folder and verify changes are not saved.");
        managePermissionsPage
            .searchAndAddUserAndGroup(testUser2.getUsername());
        managePermissionsPage
            .assertIsPermissionAddedForUser(testUser2.getUsername())
            .clickCancel();

        documentLibraryPage
            .assertFileIsDisplayed(folderToCheck.getName())
            .selectItemAction(folderToCheck.getName(), ItemActions.MANAGE_PERMISSIONS);

        managePermissionsPage
            .assertNoPermissionAddedForUser(testUser2.getUsername());
    }
}
