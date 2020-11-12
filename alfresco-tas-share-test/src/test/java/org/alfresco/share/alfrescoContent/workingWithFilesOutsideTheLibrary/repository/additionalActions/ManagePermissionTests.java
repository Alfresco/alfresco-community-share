package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository.additionalActions;

import java.io.File;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 3/20/2017.
 */
public class ManagePermissionTests extends ContextAwareWebTest
{
    //@Autowired
    RepositoryPage repositoryPage;

    //@Autowired
    ManagePermissionsPage managePermissionsPage;

    //@Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    //@Autowired
    DeleteDialog deleteDialog;

    private String userName = "0_0C202757User" + RandomData.getRandomAlphanumeric();
    private String userC202758_1 = "C202758_1" + RandomData.getRandomAlphanumeric();
    private String userC202758_2 = "C202758_2" + RandomData.getRandomAlphanumeric();
    private String userC202762_1 = "C202762_1" + RandomData.getRandomAlphanumeric();
    private String userC202762_2 = "C202762_2" + RandomData.getRandomAlphanumeric();
    private String fname1 = "FirstN1";
    private String lname1 = "LastN1";
    private String fname2 = "FirstN2";
    private String lname2 = "LastN2";
    private String path = "/";
    private String folderName = "0ManagePermissionsFolder" + RandomData.getRandomAlphanumeric();
    private String pathC202758 = "0ManagePermissionsFolder";
    private String pathForFileC202762 = "0ManagePermissionsFolder";
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String file = "Manage_permissions_test_file";
    private String userC202776 = "C202759_1" + RandomData.getRandomAlphanumeric();
    private String folderC202776 = "C202776Folder" + RandomData.getRandomAlphanumeric();
    private String subFolderC202776 = "C202776Subfolder" + RandomData.getRandomAlphanumeric();
    private String pathfolderC202776 = "/";
    private String pathSubfolder = "C202776Folder";
    private String pathForFile = "C202776Folder/C202776Subfolder";
    private String fileNameC202776 = "C202776File" + RandomData.getRandomAlphanumeric();
    private String fileContent = "C202776 Test file content";
    private String C202764user = "C202764User";
    private String groupName = "C202762GroupName";

    private String fileNameC202762 = "C202762_File" + RandomData.getRandomAlphanumeric();

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202758_1, password, userC202758_1 + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202758_2, password, userC202758_2 + "@test.com", fname2, lname2);
        userService.create(adminUser, adminPassword, userC202776, password, userC202776 + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, C202764user, password, C202764user + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202762_1, password, userC202762_1 + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202762_2, password, userC202762_2 + "@test.com", fname2, lname2);
        groupService.createGroup(adminUser, adminPassword, groupName);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userC202762_1);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userC202762_2);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderC202776, pathfolderC202776);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolderC202776, pathSubfolder);
        contentService.createDocumentInRepository(adminUser, adminPassword, pathForFile, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC202776, fileContent);
        contentService.createDocumentInRepository(adminUser, adminPassword, pathForFileC202762, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC202762, fileContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        userService.delete(adminUser, adminPassword, userC202758_1);
        userService.delete(adminUser, adminPassword, userC202758_2);
        userService.delete(adminUser, adminPassword, userC202776);
        userService.delete(adminUser, adminPassword, C202764user);
        userService.delete(adminUser, adminPassword, userC202762_1);
        userService.delete(adminUser, adminPassword, userC202762_2);

        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userC202758_1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userC202758_2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userC202776);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + C202764user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userC202762_1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "User Homes/" + userC202762_2);
    }


    @TestRail (id = "C202757")
    @Test
    public void managePermissionOption()
    {
        setupAuthenticatedSession(userName, password);
        String identifier = fname1 + " " + lname1;

        LOG.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage.navigateByMenuBar();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 2: Navigate to " + userName + " folder;");
        repositoryPage.clickOnFolderName("User Homes");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(userName), userName + " is not displayed in Repository Page");

        LOG.info("Step 3: Click Manage Permissions link in More menu for user's home folder;");
        repositoryPage.clickDocumentLibraryItemAction(userName, ItemActions.MANAGE_REPO_PERMISSIONS);

        LOG.info("Step 4: Verify Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.isAddUserGroupButtonDisplayed(), "Add User/Group button is not displayed");
        Assert.assertTrue(managePermissionsPage.isTheSaveButtonDisplayed(), "The Save button is not displayed");
        Assert.assertTrue(managePermissionsPage.isCancelButtonDisplayed(), "The Cancel button is not displayed");
        Assert.assertTrue(managePermissionsPage.isLocallySetPermissionsListDisplayed(), "Locally Set Permissions is not displayed on the Manage Permissions page");
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifier).contains("All"), "All Role is not available for " + identifier);
        Assert.assertTrue(managePermissionsPage.getRowDetails("ROLE_OWNER").contains("All"), "All Role is not available for ROLE_OWNER");
        Assert.assertTrue(managePermissionsPage.isDeleteButtonAvailable(identifier), "Delete button is not available for " + identifier);
        //Assert.assertTrue(managePermissionsPage.isDeleteButtonAvailable("ROLE_OWNER"), "Delete button is not available for ROLE_OWNER");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C202758")
    @Test

    public void savingChanges()
    {
        String identifierUser1 = fname1 + " " + lname1;
        String identifierUser2 = fname2 + " " + lname2;
        LOG.info("Preconditions: ");
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);
        managePermissionsPage.clickAddUserGroupButton();
        managePermissionsPage.sendSearchInput(userC202758_1);
        managePermissionsPage.clickSearchButton();
        managePermissionsPage.clickAddButtonForUser(userC202758_1);
        managePermissionsPage.clickRoleButton(identifierUser1);
        managePermissionsPage.selectRole("Coordinator");
        managePermissionsPage.clickAddUserGroupButton();
        managePermissionsPage.sendSearchInput(userC202758_2);
        managePermissionsPage.clickSearchButton();
        managePermissionsPage.clickAddButtonForUser(userC202758_2);
        managePermissionsPage.clickRoleButton(identifierUser2);
        managePermissionsPage.selectRole("Coordinator");
        managePermissionsPage.clickSave();
        cleanupAuthenticatedSession();

        LOG.info("Step 1: Click on Repository link in the toolbar");
        setupAuthenticatedSession(userC202758_1, password);
        repositoryPage.navigateByMenuBar();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 2: Click Manage Permissions link in More menu for " + folderName + " folder");
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifierUser1).contains("Coordinator"));
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifierUser2).contains("Coordinator"));

        LOG.info("Step 3: Change User2 role to \"Consumer\"");
        managePermissionsPage.clickRoleButton(identifierUser2);
        managePermissionsPage.selectRole("Consumer");
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifierUser2).contains("Consumer"));

        LOG.info("Step 4: Click Save");
        managePermissionsPage.clickSave();

        LOG.info("Step 5: Upload file into " + folderName);
        contentService.uploadFileInRepository(userC202758_1, password, pathC202758, testDataFolder + file + ".docx");

        LOG.info("Step 6: Log in User2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userC202758_2, password);
        repositoryPage.navigateByMenuBar();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 7: Open " + folderName + " folder and try to edit/delete uploaded file");
        repositoryPage.clickOnFolderName(folderName);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(file), file + " is not displayed in repository");
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.DELETE_DOCUMENT), "Delete Document is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.EDIT_IN_GOOGLE_DOCS), "Edit in Google Docs™ is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.EDIT_IN_MICROSOFT_OFFICE), "Edit in Microsoft Office™ is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.EDIT_PROPERTIES), "Edit Properties is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.EDIT_OFFLINE), "Edit Offline is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.UPLOAD_NEW_VERSION), "Upload New Version is available for " + file);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(file, ItemActions.MANAGE_REPO_PERMISSIONS),
                "Manage Permissions is available for " + file);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C202776")
    @Test

    public void inheritPermissionsButton()
    {
        String identifierUser1 = fname1 + " " + lname1;
        String updateContent = "Updated test content for C202776";

        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigate();
        repositoryPage.clickDocumentLibraryItemAction(folderC202776, ItemActions.MANAGE_REPO_PERMISSIONS);
        managePermissionsPage.clickAddUserGroupButton();
        managePermissionsPage.sendSearchInput(userC202776);
        managePermissionsPage.clickSearchButton();
        managePermissionsPage.clickAddButtonForUser(userC202776);
        managePermissionsPage.clickRoleButton(identifierUser1);
        managePermissionsPage.selectRole("Coordinator");
        managePermissionsPage.clickSave();
        cleanupAuthenticatedSession();

        LOG.info("Step 1: Navigate to Repository");
        setupAuthenticatedSession(userC202776, password);
        repositoryPage.navigateByMenuBar();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 2: On the Repository page click on TestFolder");
        repositoryPage.clickOnFolderName(folderC202776);

        LOG.info("Step 3: Mouseover TestSubfolder and click on Manage Permissions action");
        repositoryPage.clickDocumentLibraryItemAction(subFolderC202776, ItemActions.MANAGE_REPO_PERMISSIONS);

        LOG.info("Step 4: Check inherited permissions");
        Assert.assertTrue(managePermissionsPage.getInheritedPermissions(identifierUser1).contains("Coordinator"));

        LOG.info("Step 5: Return to Repository, TestSubfolder and check available actions for TestFile");
        repositoryPage.navigate();
        repositoryPage.clickOnFolderName(folderC202776);
        repositoryPage.clickOnFolderName(subFolderC202776);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202776, ItemActions.EDIT_IN_GOOGLE_DOCS), "Edit in Google Docs™ is not available for " + fileNameC202776);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202776, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available for " + fileNameC202776);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202776, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco is not available for " + fileNameC202776);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202776, ItemActions.EDIT_OFFLINE), "Edit Offline is not available for " + fileNameC202776);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202776, ItemActions.DELETE_DOCUMENT), "Delete Document is not available for " + fileNameC202776);

        LOG.info("Step 6: Edit TestFile and save changes");
        repositoryPage.clickDocumentLibraryItemAction(fileNameC202776, ItemActions.EDIT_IN_ALFRESCO);
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Edit in Alfresco Share", "User is not on Edit In Alfresco page");
        editInAlfrescoPage.typeContent(updateContent);
        editInAlfrescoPage.clickSaveButton();
        repositoryPage.clickOnFile(fileNameC202776);
        Assert.assertEquals(documentDetailsPage.getContentText(), updateContent, fileNameC202776 + " was not updated");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C202764")
    @Test
    public void deleteUserWithARoleAssigned()
    {
        LOG.info("Preconditions: ");
        String identifierUser1 = fname1 + " " + lname1;
        setupAuthenticatedSession(adminUser, adminPassword);
        repositoryPage.navigateByMenuBar();
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);
        managePermissionsPage.clickAddUserGroupButton();
        managePermissionsPage.sendSearchInput(C202764user);
        managePermissionsPage.clickSearchButton();
        managePermissionsPage.clickAddButtonForUser(C202764user);
        managePermissionsPage.clickSave();

        LOG.info("Step 1: Click on 'Delete' icon in from of the user group");
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);
        Assert.assertTrue(managePermissionsPage.getRowDetails(identifierUser1).contains(identifierUser1));
        managePermissionsPage.deleteUserFromPermissionsList(identifierUser1);
        getBrowser().refresh();
        managePermissionsPage.renderedPage();

        Assert.assertEquals(managePermissionsPage.getLocallySetPermissionsTextWhenNoUsersAreAdded(), "No permissions set.", "Permissions are displayed");
        managePermissionsPage.clickSave();
        cleanupAuthenticatedSession();

        LOG.info("Step 2: Check that " + C202764user + " is not able to edit or manage permissions for " + folderName);
        setupAuthenticatedSession(C202764user, password);
        repositoryPage.navigateByMenuBar();
        repositoryPage.mouseOverContentItem(folderName);
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(folderName, ItemActions.MANAGE_REPO_PERMISSIONS));
        Assert.assertFalse(repositoryPage.isActionAvailableForLibraryItem(folderName, ItemActions.DELETE_FOLDER));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C202762")
    @Test

    public void addGroup()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage.navigateByMenuBar();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");

        LOG.info("Step 2: Click Manage Permissions link in More menu for \"Test\" folder");
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);

        LOG.info("Step 3: Click Add User/Group button");
        managePermissionsPage.clickAddUserGroupButton();
        Assert.assertTrue(managePermissionsPage.isAddUsersGroupsWindowDisplayed(), "Add users groups window is not displayed");

        LOG.info("Step 4, 5 & 6: Add " + groupName);
        managePermissionsPage.sendSearchInput(groupName);
        managePermissionsPage.clickSearchButton();
        managePermissionsPage.clickAddButtonForUser(groupName);
        Assert.assertTrue(managePermissionsPage.getRowDetails(groupName).contains("Consumer"));

        LOG.info("Step 7&8 & 9 : Change role to Coordinator, save changes and check saved changes");
        managePermissionsPage.clickRoleButton(groupName);
        managePermissionsPage.selectRole("Coordinator");
        managePermissionsPage.clickSave();
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.MANAGE_REPO_PERMISSIONS);
        Assert.assertTrue(managePermissionsPage.getRowDetails(groupName).contains(groupName), groupName + " is not available in Locally Set Permissions list ");
        Assert.assertTrue(managePermissionsPage.getRowDetails(groupName).contains("Coordinator"), "Coordinator is not available in Locally Set Permissions list for " + groupName);

        LOG.info("Step 10: Log in User 1");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userC202762_1, password);
        repositoryPage.navigateByMenuBar();
        repositoryPage.mouseOverContentItem(folderName);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(folderName, ItemActions.MANAGE_REPO_PERMISSIONS), "Manage Permissions is not available for " + folderName + " for " + userC202762_1);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(folderName, ItemActions.DELETE_FOLDER), "Delete Folder is not available for " + folderName + " for " + userC202762_1);
        repositoryPage.clickOnFolderName(folderName);
        repositoryPage.mouseOverContentItem(fileNameC202762);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202762, ItemActions.DELETE_DOCUMENT), "Delete Document is not available for " + fileNameC202762);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202762, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco is not available for " + fileNameC202762);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202762, ItemActions.EDIT_OFFLINE), "Edit Offline is not available for " + fileNameC202762);
        Assert.assertTrue(repositoryPage.isActionAvailableForLibraryItem(fileNameC202762, ItemActions.EDIT_IN_GOOGLE_DOCS), "Edit in Google Docs™ is not available for " + fileNameC202762);

        repositoryPage.clickDocumentLibraryItemAction(fileNameC202762, ItemActions.DELETE_DOCUMENT);
        deleteDialog.clickDelete();

        Assert.assertFalse(repositoryPage.isContentNameDisplayed(fileNameC202762), fileNameC202762 + " is still displayed in " + folderName);
        cleanupAuthenticatedSession();
    }
}
