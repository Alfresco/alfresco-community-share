package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mirela Tifui on 3/20/2017.
 */
public class ActionsManagePermissionsTests extends ContextAwareWebTest
{

    @Autowired
    RepositoryPage repositoryPage;

    @Autowired
    ManagePermissionsPage managePermissionsPage;

    @Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    private String userName = String.format("0_0C202757User%s", RandomData.getRandomAlphanumeric());
    private String userC202758_1 = String.format("C202758_1%s", RandomData.getRandomAlphanumeric());
    private String userC202758_2 = String.format("C202758_2%s", RandomData.getRandomAlphanumeric());
    private String fname1 = "FirstN1";
    private String lname1 = "LastN1";
    private String fname2 = "FirstN2";
    private String lname2 = "LastN2";
    private String path = "/";
    private String folderName = String.format("C202758Folder%s", RandomData.getRandomAlphanumeric());
    private String pathC202758 = folderName;
    private String file = "Manage_permissions_test_file";
    private String userC202776 = "C202759_1" + RandomData.getRandomAlphanumeric();
    private String folderC202776 = String.format("C202776Folder%s", RandomData.getRandomAlphanumeric());
    private String subFolderC202776 = String.format("C202776Subfolder%s", RandomData.getRandomAlphanumeric());
    private String pathfolderC202776 = "/";
    private String pathSubfolder = folderC202776;
    private String pathForFile = folderC202776 + "/" + subFolderC202776;
    private String fileNameC202776 = String.format("C202776File%s", RandomData.getRandomAlphanumeric());
    private String fileContent = "C202776 Test file content";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202758_1, password, userC202758_1 + "@test.com", fname1, lname1);
        userService.create(adminUser, adminPassword, userC202758_2, password, userC202758_2 + "@test.com", fname2, lname2);
        userService.create(adminUser, adminPassword, userC202776, password, userC202776 + "@test.com", fname1, lname1);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createFolderInRepository(adminUser, adminPassword, folderC202776, pathfolderC202776);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolderC202776, pathSubfolder);
        contentService.createDocumentInRepository(adminUser, adminPassword, pathForFile, CMISUtil.DocumentType.TEXT_PLAIN, fileNameC202776, fileContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {

        userService.delete(adminUser, adminPassword, userName);
        userService.delete(adminUser, adminPassword, userC202758_1);
        userService.delete(adminUser, adminPassword, userC202758_2);
        userService.delete(adminUser, adminPassword, userC202776);

        contentService.deleteTreeByPath(adminUser, adminPassword, folderC202776);
        contentService.deleteTreeByPath(adminUser, adminPassword, folderName);

        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC202758_1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC202776);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userC202758_2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

    }

    @TestRail (id = "C202757")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void managePermissionOption()
    {
        setupAuthenticatedSession(userName, password);
        String identifier = String.format("%s %s", fname1, lname1);
        LOG.info("Step 1: Click on Repository link in the toolbar");
        repositoryPage.navigate();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");
        LOG.info("Step 2: Navigate to " + userName + " folder;");
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(userName), userName + " is not displayed in Repository Page");
        LOG.info("Step 3: Click Manage Permissions link in More menu for user's home folder;");
        repositoryPage.clickDocumentLibraryItemAction(userName, "Manage Permissions", managePermissionsPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Manage Permissions", "User is not on Manage Permissions Page");
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
        repositoryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
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
        repositoryPage.navigate();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");
        LOG.info("Step 2: Click Manage Permissions link in More menu for " + folderName + " folder");
        repositoryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Manage Permissions", "User is not on Manage Permissions Page");
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
        repositoryPage.navigate();
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Repository Browser", "User is not on the Repository Page");
        LOG.info("Step 7: Open " + folderName + " folder and try to edit/delete uploaded file");
        repositoryPage.clickOnFolderName(folderName);
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(file), file + " is not displayed in repository");
        List<String> notExpectedActions = Arrays.asList("Edit in Microsoft Office™", "Edit in Google Docs™", "Edit Properties", "Upload New Version",
            "Edit Offline", "Delete Document", "Manage Permissions");
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
        repositoryPage.clickDocumentLibraryItemAction(folderC202776, "Manage Permissions", managePermissionsPage);
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
        repositoryPage.clickDocumentLibraryItemAction(subFolderC202776, "Manage Permissions", managePermissionsPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Manage Permissions", "User is not on Manage Permissions Page");
        LOG.info("Step 4: Check inherited permissions");
        Assert.assertTrue(managePermissionsPage.getInheritedPermissions(identifierUser1).contains("Coordinator"));
        LOG.info("Step 5: Return to Repository, TestSubfolder and check available actions for TestFile");
        repositoryPage.navigate();
        repositoryPage.clickOnFolderName(folderC202776);
        repositoryPage.clickOnFolderName(subFolderC202776);
        List<String> expectedActions = Arrays.asList("Edit in Google Docs™", "Edit Properties", "Edit in Alfresco", "Edit Offline", "Delete Document");
        Assert.assertTrue(repositoryPage.areActionsAvailableForLibraryItem(fileNameC202776, expectedActions), "Expected actions");
        LOG.info("Step 6: Edit TestFile and save changes");
        repositoryPage.clickDocumentLibraryItemAction(fileNameC202776, "Edit in Alfresco", editInAlfrescoPage);
        Assert.assertEquals(repositoryPage.getPageTitle(), "Alfresco » Edit in Alfresco Share", "User is not on Edit In Alfresco page");
        editInAlfrescoPage.typeContent(updateContent);
        editInAlfrescoPage.clickSaveButton();
        repositoryPage.renderedPage();
        repositoryPage.clickOnFile(fileNameC202776);
        Assert.assertEquals(documentDetailsPage.getContentText(), updateContent, fileNameC202776 + " was not updated");
    }
}