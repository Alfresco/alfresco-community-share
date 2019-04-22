package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Razvan.Dorobantu
 */
public class ManageFileAndFolderPermissionsTest extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private ManagePermissionsPage managePermissionsPage;

    private final String docContent = "content of the file.";
    private final String testUser1 = String.format("testUser1%s", RandomData.getRandomAlphanumeric());
    private final String testUser2 = String.format("testUser2%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String testFileName = "testDoc.txt";
    private final String testFolderName = "testFolder";

    @BeforeClass(alwaysRun = true)
    public void createPrecondition()
    {
        userService.create(adminUser, adminPassword, testUser1, password, testUser1 + domain, testUser1, "lastName");
        userService.create(adminUser, adminPassword, testUser2, password, testUser2 + domain, testUser2, "lastName");
        siteService.create(testUser1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, testFileName, docContent);
        contentService.createFolder(testUser1, password, testFolderName, siteName);
    }
    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, testUser1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser1);
        userService.delete(adminUser,adminPassword, testUser2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser2);
        siteService.delete(adminUser, adminPassword,siteName);
    }


    @TestRail(id = "C6092")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void cancelManagePermissions()
    {
        setupAuthenticatedSession(testUser1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: On the Document Library page click on Manage Permissions option for the file.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);

        LOG.info("STEP2: On the Manage Permissions page check the header title is correct.");
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFileName, "Manage Permissions: " + testFileName + " title displayed.");

        LOG.info("STEP3: On the Manage Permissions page click the Cancel button and return to Document Library page.");
        managePermissionsPage.clickButton("Cancel");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileName), String.format("The file [%s] is not present", testFileName));
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7121")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void saveManagePermissions()
    {
        setupAuthenticatedSession(testUser1, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: On the Document Library page click on Manage Permissions option for the file.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFileName, "Manage Permissions: " + testFileName + " title displayed.");

        LOG.info("STEP2: On the Manage Permissions page click on Add User/Group button and add permissions for testUser2.");
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        managePermissionsPage.clickButton("Save");
        getBrowser().waitInSeconds(5);

        LOG.info("STEP3: Return to Manage Permissions page for the file and check if permissions were added successfully.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        getBrowser().waitInSeconds(5);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        managePermissionsPage.clickButton("Cancel");
        getBrowser().waitInSeconds(5);

        LOG.info("STEP4: On the Document Library page click on Manage Permissions option for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolderName, "Manage Permissions: " + testFolderName + " title displayed.");

        LOG.info("STEP5: On the Manage Permissions page click on Add User/Group button and add permissions for testUser2.");
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        managePermissionsPage.clickButton("Save");
        getBrowser().waitInSeconds(5);

        LOG.info("STEP6: Return to Manage Permissions page for the folder and check if permissions were added successfully.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7124")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void turnOffInheritPermissions()
    {
        setupAuthenticatedSession(testUser1, password);

        LOG.info("Precondition: Navigate to Manage Permissions page for the file.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFileName, "Manage Permissions: " + testFileName + " title displayed.");

        LOG.info("STEP1: On the Manage Permissions page click on Inherit Permissions button and verify dialog is displayed.");
        managePermissionsPage.clickInheritPermissionsButton();
        Assert.assertTrue(managePermissionsPage.isTurnOffPermissionInheritanceDialogDisplayed(), "Turn off inherit permissions dialog isn't displayed.");

        LOG.info("STEP2: On the Inherit Permissions dialog click on Yes option and verify the Inherit Permissions table is not displayed.");
        managePermissionsPage.clickAreYouSureDialog(ManagePermissionsPage.ButtonType.Yes);
        Assert.assertFalse(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is displayed.");
        Assert.assertFalse(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");

        LOG.info("STEP3: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP4: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        Assert.assertFalse(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is displayed.");
        Assert.assertFalse(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");
        managePermissionsPage.clickButton("Cancel");

        LOG.info("STEP5: On the Document Library page click on Manage Permissions option for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolderName, "Manage Permissions: " + testFolderName + " title displayed.");

        LOG.info("STEP6: On the Manage Permissions page click on Inherit Permissions button and verify dialog is displayed.");
        managePermissionsPage.clickInheritPermissionsButton();
        Assert.assertTrue(managePermissionsPage.isTurnOffPermissionInheritanceDialogDisplayed(), "Turn off inherit permissions dialog isn't displayed.");

        LOG.info("STEP7: On the Inherit Permissions dialog click on Yes option and verify the Inherit Permissions table is not displayed.");
        managePermissionsPage.clickAreYouSureDialog(ManagePermissionsPage.ButtonType.Yes);
        Assert.assertFalse(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is displayed.");
        Assert.assertFalse(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");

        LOG.info("STEP8: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP9: On the Document Library page click on Manage Permissions option for the folder and verify if changes were saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        Assert.assertFalse(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is displayed.");
        Assert.assertFalse(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7125")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void turnOnInheritPermissions()
    {
        setupAuthenticatedSession(testUser1, password);

        LOG.info("Precondition: Navigate to Manage Permission page for the file and set Inherit Permissions to off.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.toggleInheritPermissions(false, ManagePermissionsPage.ButtonType.Yes);
        managePermissionsPage.clickButton("Save");

        LOG.info("Precondition: Navigate to Manage Permission page for the folder and set Inherit Permissions to off.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.toggleInheritPermissions(false, ManagePermissionsPage.ButtonType.Yes);
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP1: Navigate to Manage Permissions page for the file and turn on Inherit Permissions.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.clickInheritPermissionsButton();
        Assert.assertTrue(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is not displayed.");
        Assert.assertTrue(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");

        LOG.info("STEP2: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP3: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        Assert.assertTrue(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is not displayed.");
        Assert.assertTrue(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");
        managePermissionsPage.clickButton("Cancel");

        LOG.info("STEP4: Navigate to Manage Permissions page for the folder and turn on Inherit Permissions.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.clickInheritPermissionsButton();
        Assert.assertTrue(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is not displayed.");
        Assert.assertTrue(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");

        LOG.info("STEP5: On the Manage Permissions page click on Save button and return to Document Library page.");
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP6: On the Document Library page click on Manage Permissions option for the file and verify if changes were saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        Assert.assertTrue(managePermissionsPage.isInheritPermissionsTableEnabled(), "Inherit Permissions section is not displayed.");
        Assert.assertTrue(managePermissionsPage.isInheritButtonStatusEnabled(), "Inherit Permissions button status is incorrect.");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7143")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void deleteUser()
    {
        LOG.info("Precondition: Login as testUser1 and add local permissions for testUser2 for the file and folder.");
        setupAuthenticatedSession(testUser1, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        managePermissionsPage.clickButton("Save");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP1: Verify locally set permissions for the file.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        assertTrue(managePermissionsPage.getRole(testUser2).equals("Site Contributor"), String.format("User [%s] has incorrect role.", testUser2));

        LOG.info("STEP2: Delete permissions for testUser2.");
        managePermissionsPage.deleteUserOrGroupFromPermission(testUser2);
        getBrowser().waitInSeconds(5);
        assertFalse(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is added in permissions.", testUser2));

        LOG.info("STEP3: Click Save button and return to Document Library page.");
        managePermissionsPage.clickButton("Save");

        LOG.info("STEP4: Navigate to Manage Permissions page for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);

        LOG.info("STEP5: Verify locally set permissions.");
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        assertTrue(managePermissionsPage.getRole(testUser2).equals("Site Contributor"), String.format("User [%s] has incorrect role.", testUser2));

        LOG.info("STEP6: Delete permissions for testUser2.");
        managePermissionsPage.deleteUserOrGroupFromPermission(testUser2);
        getBrowser().waitInSeconds(5);
        assertFalse(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is added in permissions.", testUser2));

        LOG.info("STEP7: Click Save button and then return to Manage Permissions page for the folder.");
        managePermissionsPage.clickButton("Save");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolderName, "Manage Permissions: " + testFolderName + " title displayed.");
        assertFalse(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is added in permissions.", testUser2));

        LOG.info("STEP8: Logout from testUser1 and Login as testUser2 and navigate to Document Library page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(testUser2, password);
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP9: Navigate to Manage Permissions page and check permissions for file/folder were removed.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFileName, "Manage Permissions"), "Action is available");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolderName, "Manage Permissions"), "Actions is available");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7123")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void cancelChangesFromManagePermissions()
    {
        LOG.info("Precondition: Login as testUser1 and navigate to Manage Permissions for the testFile.");
        setupAuthenticatedSession(testUser1, password);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);

        LOG.info("STEP1: On the Manage Permissions page click on Add User/Group button and search for testUser2.");
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));

        LOG.info("STEP2: On the Manage Permissions page click the Cancel button and return to Document Library page.");
        managePermissionsPage.clickButton("Cancel");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileName), String.format("The file [%s] is not present", testFileName));

        LOG.info("STEP3: Return to Manage Permissions for the file and verify changes are not saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFileName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFileName, "Manage Permissions: " + testFileName + " title displayed.");
        assertFalse(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        managePermissionsPage.clickButton("Cancel");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFolderName), String.format("The file [%s] is not present", testFolderName));

        LOG.info("STEP4: Navigate to Manage Permissions for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);

        LOG.info("STEP5: On the Manage Permissions page click on Add User/Group button and search for testUser2.");
        managePermissionsPage.searchAndAddUserAndGroup(testUser2);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));

        LOG.info("STEP6: On the Manage Permissions page click the Cancel button and return to Document Library page.");
        managePermissionsPage.clickButton("Cancel");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFolderName), String.format("The file [%s] is not present", testFolderName));

        LOG.info("STEP7: Return to Manage Permissions for the folder and verify changes are not saved.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolderName, "Manage Permissions: " + testFolderName + " title displayed.");
        assertFalse(managePermissionsPage.isPermissionAddedForUser(testUser2), String.format("User [%s] is not added in permissions.", testUser2));
        cleanupAuthenticatedSession();
    }
}
