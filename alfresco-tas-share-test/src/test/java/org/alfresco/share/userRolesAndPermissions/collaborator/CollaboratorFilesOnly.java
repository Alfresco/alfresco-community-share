package org.alfresco.share.userRolesAndPermissions.collaborator;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Rusu Andrei
 */

public class CollaboratorFilesOnly extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    CreateContent create;

    @Autowired
    UploadContent uploadContent;

    @Autowired
    DocumentCommon documentCommon;

    @Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    GoogleDocsCommon googleDocsCommon;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;

    @Autowired
    ToolbarTasksMenu toolbarTasksMenu;

    @Autowired
    Toolbar toolbar;

    @Autowired
    WorkflowService workflow;

    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    @Autowired
    MyTasksPage myTasksPage;

    // Upload
    String testFile = DataUtil.getUniqueIdentifier() + "-testFile-C8939-.txt";
    String testFilePath = testDataFolder + testFile;
    String newVersionFile = DataUtil.getUniqueIdentifier() + "-NewFile-C8942" + ".txt";
    String newVersionFilePath = testDataFolder + newVersionFile;
    String newVersionFile2 = DataUtil.getUniqueIdentifier() + "-NewFile-C8943" + ".txt";
    String newVersionFilePath2 = testDataFolder + newVersionFile2;
    String updatedDocName = "UpdatedDocName-C8947-" + DataUtil.getUniqueIdentifier();
    String updatedContent = "edited in Alfresco test content C8947";
    String updatedTitle = "updated title C8947";
    String updatedDescription = "updated description C8947";
    String updatedDocName1 = "UpdatedDocName-C8948-" + DataUtil.getUniqueIdentifier();
    String updatedContent1 = "edited in Alfresco test content C8948";
    String updatedTitle1 = "updated title C8948";
    String updatedDescription1 = "updated description C8948";
    String editedTitle = "editedTitle";
    String editedContent = "edited content in Google Docs";
    String editedTitle1 = "editedTitle1";
    String editedContent1 = "edited content in Google Docs1";
    // Create
    private String user = "UserC" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteC" + DataUtil.getUniqueIdentifier();
    private String siteName2 = "SiteC2" + DataUtil.getUniqueIdentifier();
    // Download
    private String path = "Shared";
    private String fileNameC8940 = "C8940 file" + DataUtil.getUniqueIdentifier();
    private String fileContent = "test content" + DataUtil.getUniqueIdentifier();
    private Alert alert;
    private String downloadPath = srcRoot + "testdata";
    private File downloadDirectory;
    // View in Browser
    private String fileNameC8941 = "C8941 file" + DataUtil.getUniqueIdentifier();
    private String fileContent2 = "test content" + DataUtil.getUniqueIdentifier();
    // Upload New Version by self
    private String fileNameC8942 = "C8942 file" + DataUtil.getUniqueIdentifier();
    private String fileContent3 = "test content" + DataUtil.getUniqueIdentifier();
    // Upload New Version by other user
    private String user2 = "UserC" + DataUtil.getUniqueIdentifier();
    private String fileNameC8943 = "C8943 file" + DataUtil.getUniqueIdentifier();
    private String fileContent4 = "test content" + DataUtil.getUniqueIdentifier();
    // Edit Inline by self
    private String fileNameC8947 = "C8947 file" + DataUtil.getUniqueIdentifier();
    private String fileContent5 = "test content" + DataUtil.getUniqueIdentifier();
    // Edit Inline by others
    private String fileNameC8948 = "C8948 file" + DataUtil.getUniqueIdentifier();
    private String fileContent6 = "test content" + DataUtil.getUniqueIdentifier();
    // Check Out Google Docs By Self
    private String fileNameC8953 = "C8953 file" + DataUtil.getUniqueIdentifier();
    private String fileContent7 = "test content" + DataUtil.getUniqueIdentifier();
    // Check out Google Docs By Others
    private String fileNameC8954 = "C8954 file" + DataUtil.getUniqueIdentifier();
    private String fileContent8 = "test content" + DataUtil.getUniqueIdentifier();
    // Cancel editing locked by self
    private String fileNameC8957 = "C8957 file" + DataUtil.getUniqueIdentifier();
    private String fileContent9 = "test content" + DataUtil.getUniqueIdentifier();
    // Start Workflow
    private String fileNameC8962 = "C8962 file" + DataUtil.getUniqueIdentifier();
    private String fileContent10 = "test content" + DataUtil.getUniqueIdentifier();

    private boolean isFileInDirectory(String fileName, String extension)
    {
        downloadDirectory = new File(downloadPath);
        File[] directoryContent = downloadDirectory.listFiles();

        for (File aDirectoryContent : directoryContent)
        {
            if (extension == null)
            {
                if (aDirectoryContent.getName().equals(fileName))
                    return true;
            }
            else
            {
                if (aDirectoryContent.getName().equals(fileName + extension))
                    return true;
            }
        }

        return false;
    }

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        userService.create(adminUser, adminPassword, user2, password, user + "@tests.com", user2, user2);
        siteService.create(adminUser, adminPassword, domain, siteName, "SiteC description", Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, siteName2, "SiteC description", Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user, siteName2, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user2, siteName, "SiteCollaborator");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C8938")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorCreateContent()
    {
        LOG.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: On the Document Library Page click on Create button.");
        documentLibraryPage.clickCreateButton();
        Assert.assertTrue(documentLibraryPage.isCreateContentMenuDisplayed(), "Create content menu is not displayed");

        LOG.info("Step 2: From the Create Options menu select Create Plain Text.");
        create.clickPlainTextButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");

        LOG.info("Step 3: Provide input for: Name= 'C8938test', Title= 'C8938test', Description= 'C8938test' and click the 'Create' button.");
        create.sendInputForName("C8938test");
        create.sendInputForTitle("C8938test");
        create.sendInputForDescription("C8938test");
        create.clickCreateButton();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8938test", "\"C8938test\" is not the file name for the file in preview");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Title:"), "C8938test", "\"C8938test\" is not the file title for the file in preview");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Description:"), "C8938test",
                "\"C8938test\" is not the file description for the file in preview");
    }

    @TestRail(id = "C8939")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorUploadContent()
    {
        LOG.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: On the Document Library page click on the Upload button..");
        uploadContent.uploadContent(testFilePath);

        LOG.info("STEP2: Choose the testFile to upload and confirm Upload.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));
    }

    @TestRail(id = "C8940")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorDownloadContent()
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8940, fileContent);

        LOG.info("Step 1: Mouse over the testDocument from Document Library");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 2: Click the Download Button. Check the file was saved locally");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8940, "Download", documentLibraryPage);

        if (documentCommon.isAlertPresent())
        {
            alert = getBrowser().switchTo().alert();
            LOG.info(alert.getText());
            alert.accept();
        }
        Assert.assertTrue(isFileInDirectory(fileNameC8940, null), "The file was not found in the specified location");
    }

    @TestRail(id = "C8941")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorViewInBrowser()
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8941, fileContent2);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 2: Click 'View in getBrowser().'");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8941, "View In Browser", documentLibraryPage);
    }

    @TestRail(id = "C8947")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditInlineBySelf()
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8947, fileContent5);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8947, language.translate("documentLibrary.contentActions.editInAlfresco"),
                editInAlfrescoPage);

        LOG.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.sendDocumentDetailsFields(updatedDocName, updatedContent, updatedTitle, updatedDescription);
        editInAlfrescoPage.clickButton("Save");
        documentLibraryPage.renderedPage();
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName));
        documentLibraryPage.clickOnFile(updatedDocName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getContentText(), updatedContent);
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedTitle), "Updated title is not displayed");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedDescription), "Updated description is not displayed");
    }

    @TestRail(id = "C8948")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditInlineByOthers()
    {
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileNameC8948, fileContent6);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8948, language.translate("documentLibrary.contentActions.editInAlfresco"),
                editInAlfrescoPage);

        LOG.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.sendDocumentDetailsFields(updatedDocName1, updatedContent1, updatedTitle1, updatedDescription1);
        editInAlfrescoPage.renderedPage();
        editInAlfrescoPage.clickButton("Save");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");

        LOG.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName1));
        documentLibraryPage.clickOnFile(updatedDocName1);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");
        assertEquals(documentDetailsPage.getContentText(), updatedContent1);
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedTitle1), "Updated title is not displayed");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedDescription1), "Updated description is not displayed");
    }

    @TestRail(id = "C8957")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorCancelEditingBySelf()
    {
        contentService.createDocument(user, password, siteName, DocumentType.MSWORD, fileNameC8957, fileContent9);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileNameC8957), String.format("Document %s is not present", fileNameC8957));

        LOG.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.mouseOverContentItem(fileNameC8957);
        googleDocsCommon.editInGoogleDocs();
        googleDocsCommon.clickOkButton();
        googleDocsCommon.renderedPage();
        googleDocsCommon.confirmFormatUpgrade();
        Assert.assertEquals(googleDocsCommon.getConfirmationPopUpMessage(), "Editing in Google Docs™...", "Checking in Google Doc is not found.");
        getBrowser().waitUntilElementDisappears(googleDocsCommon.confirmationPopup, 15L);

        LOG.info("Step 3: Check the testFile status in Document Library.");
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Mouse over testFile name and check available actions.");
        LOG.info("Step 5: Click Cancel editing action..");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8957, "Cancel Editing in Google Docs™", documentLibraryPage);
        googleDocsCommon.confirmFormatUpgrade();
        Assert.assertEquals(googleDocsCommon.getConfirmationPopUpMessage(), "Cancel Editing in Google Docs™", "Cancel Editing in Google Doc is not found.");
        getBrowser().waitUntilElementDisappears(googleDocsCommon.confirmationPopup, 15L);
    }

    @Bug(id = "MNT-17015")
    @TestRail(id = "C8962")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorStartWorkflow()
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8962, fileContent10);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileNameC8962), String.format("Document %s is not present", fileNameC8962));

        LOG.info("Step 2: Click Start Workflow.");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8962, "Start Workflow", startWorkflowPage);

        LOG.info("Step 3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow();
        startWorkflowPage.selectWorkflowToStartFromDropdownList("New Task");

        LOG.info("Step 4: On the new task workflow form provide the inputs and click on Start Workflow button.");
        startWorkflowPage.addWorkflowDescription("test workflow");
        startWorkflowPage.clickOnDatePickerIcon();
        startWorkflowPage.selectCurrentDate();
        startWorkflowPage.selectWorkflowPriority("Medium");
        startWorkflowPage.clickOnSelectAssigneeButton();
        selectAssigneeToWorkflowPopUp.searchUser(user);
        selectPopUpPage.clickAddIcon("(" + user + ")");
        selectAssigneeToWorkflowPopUp.clickOkButton();
        startWorkflowPage.clickStartWorkflow();

        LOG.info("Step 5: Go to Toolbar and click Tasks/ My Tasks.");
        toolbarTasksMenu.clickMyTasks();
        myTasksPage.renderedPage();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » My Tasks", "My Tasks page is not opened");

        LOG.info("Step 6: Check and confirm that the Task created at step 4 with details. ");
        myTasksPage.refresh();
        myTasksPage.clickViewWorkflow("test workflow");
        workflowDetailsPage.renderedPage();
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(user));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user));
    }

    @TestRail(id = "C8942")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorUploadNewVersionSelfCreated()
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8942, fileContent3);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(
                documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8942, language.translate("documentLibrary.contentAction.uploadNewVersion")),
                "Upload new version action is not available for " + fileNameC8942);

        LOG.info("Step 2: Click Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8942, language.translate("documentLibrary.contentAction.uploadNewVersion"), uploadContent);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");

        LOG.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        documentLibraryPage.renderedPage();
        // getBrowser().waitUntilElementVisible(documentLibraryPage.content(newVersionFile));
        getBrowser().waitInSeconds(5);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(fileNameC8942), fileNameC8942 + " is displayed.");

        LOG.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile, String.format("Name of %s is wrong.", newVersionFile));
    }

    @TestRail(id = "C8943")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorUploadNewVersionOtherUserCreated()
    {
        contentService.createDocument(user2, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8943, fileContent4);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(
                documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8943, language.translate("documentLibrary.contentAction.uploadNewVersion")),
                "Upload new version action is not available for " + fileNameC8943);

        LOG.info("Step 2: Click Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileNameC8943, language.translate("documentLibrary.contentAction.uploadNewVersion"), uploadContent);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");

        LOG.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath2, "comments", UploadContent.Version.Major);
        documentLibraryPage.renderedPage();
        // getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.content(newVersionFile), 6);
        getBrowser().waitInSeconds(5);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile2), String.format("File [%s] is displayed", newVersionFile2));
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(fileNameC8943), fileNameC8943 + " is displayed.");

        LOG.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile2);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile2, String.format("Name of %s is wrong.", newVersionFile2));
    }

    @TestRail(id = "C8953")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorCheckOutGoogleDocBySelf() throws Exception
    {
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8953, fileContent7);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileNameC8953), String.format("Document %s is not present", fileNameC8953));

        LOG.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.mouseOverFileName(fileNameC8953);
        googleDocsCommon.editInGoogleDocs();
        googleDocsCommon.clickOkButton();
        googleDocsCommon.renderedPage();

        LOG.info("Step 3: Check the testFile status in Document Library.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(googleDocsCommon.lockedIcon);
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);

        LOG.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverFileName(fileNameC8953);

        LOG.info("Step 6: Click Check In Google");
        googleDocsCommon.checkInGoogleDoc(fileNameC8953);
        getBrowser().waitUntilElementDisappears(By.xpath("//span[contains(text(), 'Check In Google Doc™')]"), 9L);
        getBrowser().waitUntilElementVisible(By.xpath("//div[contains(text(), 'Version Information')]"));
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);

        LOG.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        googleDocsCommon.renderedPage();
        getBrowser().waitUntilElementDisappears(By.xpath("//span[contains(text(), 'Checking In Google Doc™...')]"), 9L);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);

        LOG.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertEquals(googleDocsCommon.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocsCommon.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(googleDocsCommon.isDocumentNameUpdated(editedTitle), "Name of the document was not updated");

        LOG.info("Step 9: Click on the testFile name to preview document.");
        googleDocsCommon.clickOnUpdatedName(editedTitle);

        LOG.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail(id = "C8954")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorCheckOutGoogleDocByOthers() throws Exception
    {
        contentService.createDocument(user2, password, siteName, DocumentType.TEXT_PLAIN, fileNameC8954, fileContent8);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileNameC8954), String.format("Document %s is not present", fileNameC8954));

        LOG.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.mouseOverFileName(fileNameC8954);
        googleDocsCommon.editInGoogleDocs();
        googleDocsCommon.clickOkButton();
        googleDocsCommon.renderedPage();

        LOG.info("Step 3: Check the testFile status in Document Library.");
        getBrowser().waitUntilElementVisible(googleDocsCommon.lockedIcon);
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle1, editedContent1);

        LOG.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverFileName(fileNameC8954);

        LOG.info("Step 6: Click Check In Google Doc™.");
        googleDocsCommon.checkInGoogleDoc(fileNameC8954);
        getBrowser().waitUntilElementDisappears(By.xpath("//span[contains(text(), 'Check In Google Doc™')]"), 5L);
        getBrowser().waitUntilElementVisible(By.xpath("//div[contains(text(), 'Version Information')]"));
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);

        LOG.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        googleDocsCommon.renderedPage();
        getBrowser().waitUntilElementDisappears(By.xpath("//span[contains(text(), 'Checking In Google Doc™...')]"), 5L);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);

        LOG.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertEquals(googleDocsCommon.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocsCommon.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(googleDocsCommon.isDocumentNameUpdated(editedTitle1), "Name of the document was not updated");

        LOG.info("Step 9: Click on the testFile name to preview document.");
        googleDocsCommon.clickOnUpdatedName(editedTitle1);

        LOG.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail(id = "C8945")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })

    public void editOnlineCreatedBySelf()
    {
        String fileNameC8945 = "C8945TestFile";
        String fileContent = "C8945 content";
        contentService.createDocument(user, password, siteName2, DocumentType.MSWORD, fileNameC8945, fileContent);
        documentLibraryPage.navigate(siteName2);

        LOG.info("Step 1: Mouse over testFile and check available actions.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8945, "Edit in Microsoft Office™"),
                "Edit in Microsoft Office™ is not available");

        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail(id = "C8946")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })

    public void editOnlineCreatedByOtherUser()
    {
        String fileNameC8946 = "C8946TestFile";
        String content = "C8946 content";
        contentService.createDocument(adminUser, adminPassword, siteName2, DocumentType.MSWORD, fileNameC8946, content);
        documentLibraryPage.navigate(siteName2);

        LOG.info("Step 1: Mouse over testFile and check that Edit in Microsoft Office™ is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8946, "Edit in Microsoft Office™"),
                "Edit in Microsoft Office™ is not available");

        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail(id = "C8949")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })

    public void editOfflineCreatedBySelf()
    {
        String fileNameC8949 = "C8949TestFile";
        String content = "C8949 content";
        contentService.createDocument(user, password, siteName2, DocumentType.TEXT_PLAIN, fileNameC8949, content);
        documentLibraryPage.navigate(siteName2);

        LOG.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8949, "Edit Offline"), "Edit Offline is not available");

        // TODO edit Offline has not yet been automated
    }

    @TestRail(id = "C8950")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })

    public void editOfflineCreatedByOtherUser()
    {
        String fileNameC8950 = "C8950TestFile";
        String content = "C8950 content";
        contentService.createDocument(adminUser, adminPassword, siteName2, DocumentType.TEXT_PLAIN, fileNameC8950, content);
        documentLibraryPage.navigate(siteName2);

        LOG.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileNameC8950, "Edit Offline"), "Edit Offline is not available");

        // TODO edit Offline has not yet been automated
    }
}
