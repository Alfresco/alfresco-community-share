package org.alfresco.share.userRolesAndPermissions.collaborator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.report.Bug.Status;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Rusu Andrei
 */

public class CollaboratorFilesOnlyTests extends ContextAwareWebTest
{
    private final String testFile = RandomData.getRandomAlphanumeric() + "-testFile-C8939-.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String newVersionFile = RandomData.getRandomAlphanumeric() + "-NewFile-C8942" + ".txt";
    private final String newVersionFilePath = testDataFolder + newVersionFile;
    private final String newVersionFile2 = RandomData.getRandomAlphanumeric() + "-NewFile-C8943" + ".txt";
    private final String newVersionFilePath2 = testDataFolder + newVersionFile2;
    private final String updatedDocName = String.format("UpdatedDocName-C8947-%s", RandomData.getRandomAlphanumeric());
    private final String updatedContent = "edited in Alfresco test content C8947";
    private final String updatedTitle = "updated title C8947";
    private final String updatedDescription = "updated description C8947";
    private final String updatedDocName1 = String.format("UpdatedDocName-C8948-%s", RandomData.getRandomAlphanumeric());
    private final String updatedContent1 = "edited in Alfresco test content C8948";
    private final String updatedTitle1 = "updated title C8948";
    private final String updatedDescription1 = "updated description C8948";
    private final String editedTitle = "editedTitle";
    private final String editedContent = "edited content in Google Docs";
    private final String editedTitle1 = "editedTitle1";
    private final String editedContent1 = "edited content in Google Docs1";
    private final String user = String.format("Collaborator%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("SiteC%s", RandomData.getRandomAlphanumeric());
    private final String siteName2 = String.format("SiteC2%s", RandomData.getRandomAlphanumeric());
    private final String fileContent = String.format("test content%s", RandomData.getRandomAlphanumeric());
    private final String textFilePlainCreatedBySelf = String.format("textFilePlainSelf file%s", RandomData.getRandomAlphanumeric());
    private final String startWorkflowFile = "StartWorkflow " + RandomData.getRandomAlphanumeric();
    private final String textFilePlainCreatedByOtherUser = String.format("textFilePlainOther file%s", RandomData.getRandomAlphanumeric());
    private final String user2 = String.format("Collaborator2%s", RandomData.getRandomAlphanumeric());
    private final String msWordFileCreatedBySelf = String.format("msWordFilePlainSelf file%s", RandomData.getRandomAlphanumeric());
    private final String msWordFileCreatedByOther = String.format("msWordFilePlainOther file%s", RandomData.getRandomAlphanumeric());
    private final String deletePath = String.format("Sites/%s/documentLibrary", siteName);
    @Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private CreateContent create;
    @Autowired
    private UploadContent uploadContent;
    @Autowired
    private EditInAlfrescoPage editInAlfrescoPage;
    @Autowired
    private GoogleDocsCommon googleDocsCommon;
    @Autowired
    private StartWorkflowPage startWorkflowPage;
    @Autowired
    private SelectPopUpPage selectPopUpPage;
    @Autowired
    private ToolbarTasksMenu toolbarTasksMenu;
    @Autowired
    private WorkflowDetailsPage workflowDetailsPage;
    @Autowired
    private MyTasksPage myTasksPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user2, password, user + domain, user2, user2);
        siteService.create(adminUser, adminPassword, domain, siteName, "SiteC description", SiteService.Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, siteName2, "SiteC description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user, siteName2, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user2, siteName, "SiteCollaborator");
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, textFilePlainCreatedBySelf, fileContent);
        contentService.createDocument(user2, password, siteName, DocumentType.TEXT_PLAIN, textFilePlainCreatedByOtherUser, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.MSWORD, msWordFileCreatedBySelf, fileContent);
        contentService.createDocument(user2, password, siteName, DocumentType.MSWORD, msWordFileCreatedByOther, fileContent);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, startWorkflowFile, fileContent);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName2);

    }


    @TestRail (id = "C8938")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorCreateContent()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: On the Document Library Page click on Create button.");
        documentLibraryPage.clickCreateButton();
        LOG.info("Step 2: From the Create Options menu select Create Plain Text.");
        documentLibraryPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
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
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/C8938test", deletePath));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8939")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorUploadContent()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName);
        LOG.info("STEP1: On the Document Library page click on the Upload button..");
        uploadContent.uploadContent(testFilePath);
        LOG.info("STEP2: Choose the testFile to upload and confirm Upload.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, testFile));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8940")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorDownloadContent()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testDocument from Document Library");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFilePlainCreatedBySelf, ItemActions.DOWNLOAD), "\"Download\" is not available ");
        LOG.info("Step 2: Click the Download Button. Check the file was saved locally");
        documentLibraryPage.clickDocumentLibraryItemAction(textFilePlainCreatedBySelf, ItemActions.DOWNLOAD, documentLibraryPage);
        documentLibraryPage.acceptAlertIfDisplayed();
        Assert.assertTrue(isFileInDirectory(textFilePlainCreatedBySelf, null), "The file was not found in the specified location");
    }

    @Bug (id = "SHA-2055", status = Status.FIXED)
    @TestRail (id = "C8941")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorViewInBrowser()
    {
        String fileName = "C8941" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.VIEW_IN_BROWSER), "\"View In Browser\" is not available ");
        LOG.info("Step 2: Click 'View in browser.'");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.VIEW_IN_BROWSER, documentLibraryPage);
        Assert.assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), fileContent,
            "File content is not correct or file has not be opened in new window");
    }

    @TestRail (id = "C8947")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorEditInlineBySelf()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFilePlainCreatedBySelf, ItemActions.EDIT_IN_ALFRESCO,
            editInAlfrescoPage);
        LOG.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.sendDocumentDetailsFields(updatedDocName, updatedContent, updatedTitle, updatedDescription);
        editInAlfrescoPage.clickButton("Save");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName));
        documentLibraryPage.clickOnFile(updatedDocName);
        assertEquals(documentDetailsPage.getContentText(), updatedContent);
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedTitle), "Updated title is not displayed");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedDescription), "Updated description is not displayed");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, updatedDocName));
    }

    @TestRail (id = "C8948")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorEditInlineByOthers()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.clickDocumentLibraryItemAction(textFilePlainCreatedByOtherUser, ItemActions.EDIT_IN_ALFRESCO,
            editInAlfrescoPage);
        LOG.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.sendDocumentDetailsFields(updatedDocName1, updatedContent1, updatedTitle1, updatedDescription1);
        editInAlfrescoPage.clickButton("Save");

        LOG.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName1));
        documentLibraryPage.clickOnFile(updatedDocName1);
        assertEquals(documentDetailsPage.getContentText(), updatedContent1);
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedTitle1), "Updated title is not displayed");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedDescription1), "Updated description is not displayed");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, updatedDocName1));
    }

    @TestRail (id = "C8957")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorCancelEditingBySelf()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedBySelf), String.format("Document %s is not present", msWordFileCreatedBySelf));
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedBySelf, ItemActions.EDIT_OFFLINE), "Edit Offline is not available for " + msWordFileCreatedBySelf);
        LOG.info("Step 2& Step 3: Click edit offline and Check the testFile status in Document Library.");
        documentLibraryPage.clickDocumentLibraryItemAction(msWordFileCreatedBySelf, ItemActions.EDIT_OFFLINE, documentLibraryPage);
        Assert.assertTrue(documentLibraryPage.getInfoBannerText(msWordFileCreatedBySelf).contains("This document is locked"), documentLibraryPage.getInfoBannerText(msWordFileCreatedBySelf));
        LOG.info("Step 4: Mouse over testFile name and check available actions.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedBySelf, ItemActions.CANCEL_EDITING), "Cancel Editing is not available for " + msWordFileCreatedBySelf);
        LOG.info("Step 5: Click Cancel editing action..");
        documentLibraryPage.clickDocumentLibraryItemAction(msWordFileCreatedBySelf, ItemActions.CANCEL_EDITING, documentLibraryPage);
        documentLibraryPage.navigate(siteName);
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(msWordFileCreatedBySelf), "Locked message is still displayed");
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8962")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorStartWorkflow()
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(startWorkflowFile), String.format("Document %s is not present", startWorkflowFile));
        LOG.info("Step 2: Click Start Workflow.");
        documentLibraryPage.clickDocumentLibraryItemAction(startWorkflowFile, ItemActions.START_WORKFLOW, startWorkflowPage);
        LOG.info("Step 3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow("New Task");
        LOG.info("Step 4: On the new task workflow form provide the inputs and click on Start Workflow button.");
        startWorkflowPage.addWorkflowDescription("test workflow");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("Medium");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user);
        selectPopUpPage.clickAddIcon("(" + user + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow(documentLibraryPage);
        LOG.info("Step 5: Go to Toolbar and click Tasks/ My Tasks.");
        toolbarTasksMenu.clickMyTasks();
        myTasksPage.renderedPage();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » My Tasks", "My Tasks page is not opened");
        LOG.info("Step 6: Check and confirm that the Task created at step 4 with details. ");
        myTasksPage.refresh();
        myTasksPage.clickViewWorkflow("test workflow");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(user));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, startWorkflowFile));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8942")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorUploadNewVersionSelfCreated()
    {
        String fileName = "C8942" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(
            documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION),
            "Upload new version action is not available for " + fileName);
        LOG.info("Step 2: Click Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION, uploadContent);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");
        LOG.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        documentLibraryPage.renderedPage();
        getBrowser().waitInSeconds(5);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(textFilePlainCreatedBySelf), textFilePlainCreatedBySelf + " is displayed.");
        LOG.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile, String.format("Name of %s is wrong.", newVersionFile));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFile));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8943")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void collaboratorUploadNewVersionOtherUserCreated()
    {
        String fileName = "C8943" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user2, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(
            documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION),
            "Upload new version action is not available for " + fileName);
        LOG.info("Step 2: Click Upload New Version");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION, uploadContent);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");
        LOG.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath2, "comments", UploadContent.Version.Major);
        documentLibraryPage.renderedPage();
        getBrowser().waitInSeconds(5);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile2), String.format("File [%s] is displayed", newVersionFile2));
        Assert.assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " is displayed.");
        LOG.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile2);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile2, String.format("Name of %s is wrong.", newVersionFile2));
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, newVersionFile2));
    }

    @TestRail (id = "C8953")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void collaboratorCheckOutGoogleDocBySelf() throws Exception
    {
        setupAuthenticatedSession(user, password);
        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedBySelf), String.format("Document %s is not present", msWordFileCreatedBySelf));
        LOG.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        //    googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.clickDocumentLibraryItemAction(msWordFileCreatedBySelf, ItemActions.EDIT_IN_GOOGLE_DOCS, googleDocsCommon);
        googleDocsCommon.clickOkButton();
        LOG.info("Step 3: Check the testFile status in Document Library.");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(googleDocsCommon.lockedIcon);
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");
        LOG.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);
        LOG.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverContentItem(msWordFileCreatedBySelf);
        LOG.info("Step 6: Click Check In Google");
        googleDocsCommon.checkInGoogleDoc(msWordFileCreatedBySelf);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);
        LOG.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);
        LOG.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(editedTitle), "Locked label displayed");
        Assert.assertEquals(googleDocsCommon.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedTitle), "Name of the document was not updated");
        LOG.info("Step 9: Click on the testFile name to preview document.");
        documentLibraryPage.clickOnFile(editedTitle);
        LOG.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail (id = "C8954")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void collaboratorCheckOutGoogleDocByOthers() throws Exception
    {
        setupAuthenticatedSession(user, password);

        LOG.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedByOther), String.format("Document %s is not present", msWordFileCreatedByOther));
        LOG.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.clickDocumentLibraryItemAction(msWordFileCreatedByOther, ItemActions.EDIT_IN_GOOGLE_DOCS, googleDocsCommon);
        googleDocsCommon.clickOkButton();
        googleDocsCommon.confirmDocumentFormatUpgradeYes();

        LOG.info("Step 3: Check the testFile status in Document Library.");
        getBrowser().waitUntilElementVisible(googleDocsCommon.lockedIcon);
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");
        LOG.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle1, editedContent1);
        LOG.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverContentItem(msWordFileCreatedByOther);
        LOG.info("Step 6: Click Check In Google Doc™.");
        googleDocsCommon.checkInGoogleDoc(msWordFileCreatedByOther);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);
        LOG.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        getBrowser().waitUntilElementDisappears(By.xpath("//span[contains(text(), 'Checking In Google Doc™...')]"), 5L);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);
        LOG.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(editedTitle1), "Locked label displayed");
        Assert.assertEquals(googleDocsCommon.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedTitle1), "Name of the document was not updated");
        LOG.info("Step 9: Click on the testFile name to preview document.");
        documentLibraryPage.clickOnFile(editedTitle1);
        LOG.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail (id = "C8945")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office", "tobefixed"})
    public void editOnlineCreatedBySelf()
    {
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over testFile and check available actions.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedBySelf, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office™ is not available");
        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail (id = "C8946")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineCreatedByOtherUser()
    {
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over testFile and check that Edit in Microsoft Office™ is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedByOther, ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office™ is not available");
        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail (id = "C8949")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void editOfflineCreatedBySelf()
    {
        setupAuthenticatedSession(user, password);
        String fileName = "C8949" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline is not available");
        // TODO edit Offline has not yet been automated
    }

    @TestRail (id = "C8950")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "tobefixed" })
    public void editOfflineCreatedByOtherUser()
    {
        String fileName = "C8950" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user2, password, siteName, DocumentType.TEXT_PLAIN, fileName, fileContent);
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline is not available");
        // TODO edit Offline has not yet been automated
    }
}


