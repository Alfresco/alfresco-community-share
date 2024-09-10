package org.alfresco.share.userRolesAndPermissions.collaborator;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
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
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.report.Bug.Status;
import org.alfresco.utility.model.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.alfresco.common.Utils.isFileInDirectory;
import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Rusu Andrei
 */

@Slf4j
public class CollaboratorFilesOnlyTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    SiteService siteService;
    @Autowired
    GoogleDocsCommon googleDocsCommon;
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
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName2 = new ThreadLocal<>();
    private final String fileContent = String.format("test content%s", RandomData.getRandomAlphanumeric());
    private final String textFilePlainCreatedBySelf = String.format("textFilePlainSelf file%s", RandomData.getRandomAlphanumeric());
    private final String startWorkflowFile = "StartWorkflow " + RandomData.getRandomAlphanumeric();
    private final String textFilePlainCreatedByOtherUser = String.format("textFilePlainOther file%s", RandomData.getRandomAlphanumeric());
    private final String msWordFileCreatedBySelf = String.format("msWordFilePlainSelf file%s", RandomData.getRandomAlphanumeric());
    private final String msWordFileCreatedByOther = String.format("msWordFilePlainOther file%s", RandomData.getRandomAlphanumeric());
    private Toolbar toolbar;
    private DocumentLibraryPage documentLibraryPage;
    private DocumentDetailsPage documentDetailsPage;
    private CreateContentPage create;
    private UploadContent uploadContent;
    private EditInAlfrescoPage editInAlfrescoPage;
    private StartWorkflowPage startWorkflowPage;
    private SelectPopUpPage selectPopUpPage;
    private WorkflowDetailsPage workflowDetailsPage;
    private MyTasksPage myTasksPage;
    private FileModel msWordFilePlainSelf, msWordFilePlainOther;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());
        siteName2.set(getDataSite().usingUser(getAdminUser()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteCollaborator");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName2.get().getId(), "SiteCollaborator");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user2.get().getUsername(), siteName.get().getId(), "SiteCollaborator");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, textFilePlainCreatedBySelf, fileContent);
        contentService.createDocument(user2.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, textFilePlainCreatedByOtherUser, fileContent);
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.MSWORD, msWordFileCreatedBySelf, fileContent);
        contentService.createDocument(user2.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.MSWORD, msWordFileCreatedByOther, fileContent);
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, startWorkflowFile, fileContent);

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        create = new CreateContentPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        toolbar = new Toolbar(webDriver);
        msWordFilePlainSelf = FileModel.getRandomFileModel(FileType.MSWORD2007);
        msWordFilePlainOther = FileModel.getRandomFileModel(FileType.MSWORD2007);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(siteName2.get());
        deleteUsersIfNotNull(user.get());
        deleteUsersIfNotNull(user2.get());
    }


    @TestRail (id = "C8938")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorCreateContent() {
        log.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: On the Document Library Page click on Create button.");
        documentLibraryPage.clickCreateButton();
        log.info("Step 2: From the Create Options menu select Create Plain Text.");
        documentLibraryPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        log.info("Step 3: Provide input for: Name= 'C8938test', Title= 'C8938test', Description= 'C8938test' and click the 'Create' button.");
        create.typeName("C8938test");
        create.typeContent("C8938 content");
        create.typeTitle("C8938test");
        create.typeDescription("C8938test");
        create.clickCreateButton();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");
        documentDetailsPage.assertPropertyValueEquals("Mimetype", "Plain Text");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8938test", "\"C8938test\" is not the file name for the file in preview");
        documentDetailsPage.assertPropertyValueEquals("Title", "C8938test");
        documentDetailsPage.assertPropertyValueEquals("Description", "C8938test");
    }

    @TestRail (id = "C8939")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorUploadContent()
    {
        log.info("Precondition: testSite Document Library page is opened.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("STEP1: On the Document Library page click on the Upload button..");
        uploadContent.uploadContent(testFilePath);
        log.info("STEP2: Choose the testFile to upload and confirm Upload.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile), String.format("File [%s] is displayed", testFile));
    }

    @TestRail (id = "C8940")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "DownloadTest" })
    public void collaboratorDownloadContent()
    {
        log.info("Step 1: Mouse over the testDocument from Document Library");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(textFilePlainCreatedBySelf, ItemActions.DOWNLOAD), "\"Download\" is not available ");
        log.info("Step 2: Click the Download Button. Check the file was saved locally");
        documentLibraryPage.selectItemAction(textFilePlainCreatedBySelf, ItemActions.DOWNLOAD);
        documentLibraryPage.acceptAlertIfDisplayed();
        Assert.assertTrue(isFileInDirectory(textFilePlainCreatedBySelf, null), "The file was not found in the specified location");
    }

    @Bug (id = "SHA-2055", status = Status.FIXED)
    @TestRail (id = "C8941")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorViewInBrowser()
    {
        String fileName = "C8941" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.VIEW_IN_BROWSER), "\"View In Browser\" is not available ");
        log.info("Step 2: Click 'View in browser.'");
        documentLibraryPage.selectItemAction(fileName, ItemActions.VIEW_IN_BROWSER);
      //  Assert.assertEquals(documentLibraryPage.switchToNewWindowAngGetContent(), fileContent,
        //    "File content is not correct or file has not be opened in new window");
    }

    @TestRail (id = "C8947")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorEditInlineBySelf()
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.selectItemAction(textFilePlainCreatedBySelf, ItemActions.EDIT_IN_ALFRESCO);
        log.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.enterDocumentDetails(updatedDocName, updatedContent, updatedTitle, updatedDescription);
        editInAlfrescoPage.clickSaveButton();
        log.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName));
        documentLibraryPage.clickOnFile(updatedDocName);
        assertEquals(documentDetailsPage.getContentText(), updatedContent);
    }

    @TestRail (id = "C8948")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorEditInlineByOthers()
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 2: Click Edit in Alfresco.");
        documentLibraryPage.selectItemAction(textFilePlainCreatedByOtherUser, ItemActions.EDIT_IN_ALFRESCO);
        log.info("Step 3: Edit content and save changes.");
        editInAlfrescoPage.enterDocumentDetails(updatedDocName1, updatedContent1, updatedTitle1, updatedDescription1);
        editInAlfrescoPage.clickSaveButton();

        log.info("Step4: Click on testFile to open file and check content.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(updatedDocName1));
        documentLibraryPage.clickOnFile(updatedDocName1);
        assertEquals(documentDetailsPage.getContentText(), updatedContent1);
    }

    @TestRail (id = "C8957")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorCancelEditingBySelf()
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedBySelf), String.format("Document %s is not present", msWordFileCreatedBySelf));
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedBySelf, ItemActions.EDIT_OFFLINE), "Edit Offline is not available for " + msWordFileCreatedBySelf);
        log.info("Step 2& Step 3: Click edit offline and Check the testFile status in Document Library.");
        documentLibraryPage.selectItemAction(msWordFileCreatedBySelf, ItemActions.EDIT_OFFLINE);
        Assert.assertTrue(documentLibraryPage.getInfoBannerText(msWordFileCreatedBySelf).contains("This document is locked"), documentLibraryPage.getInfoBannerText(msWordFileCreatedBySelf));
        log.info("Step 4: Mouse over testFile name and check available actions.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFileCreatedBySelf, ItemActions.CANCEL_EDITING), "Cancel Editing is not available for " + msWordFileCreatedBySelf);
        log.info("Step 5: Click Cancel editing action..");
        documentLibraryPage.selectItemAction(msWordFileCreatedBySelf, ItemActions.CANCEL_EDITING);
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(msWordFileCreatedBySelf), "Locked message is still displayed");
    }

//    @Bug (id = "MNT-17015", status = Bug.Status.FIXED)
    @TestRail (id = "C8962")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorStartWorkflow()
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(startWorkflowFile), String.format("Document %s is not present", startWorkflowFile));
        log.info("Step 2: Click Start Workflow.");
        documentLibraryPage.selectItemAction(startWorkflowFile, ItemActions.START_WORKFLOW);
        log.info("Step 3: From the Select Workflow drop-down select New Task Workflow.");
        startWorkflowPage.selectAWorkflow("New Task");
        log.info("Step 4: On the new task workflow form provide the inputs and click on Start Workflow button.");
        startWorkflowPage.addWorkflowDescription("test workflow");
        startWorkflowPage.selectCurrentDateFromDatePicker();
        startWorkflowPage.selectWorkflowPriority("Medium");
        startWorkflowPage.clickOnSelectButtonSingleAssignee();
        selectPopUpPage.search(user.get().getUsername());
        selectPopUpPage.clickAddIcon("(" + user.get().getUsername() + ")");
        selectPopUpPage.clickOkButton();
        startWorkflowPage.clickStartWorkflow();
        log.info("Step 5: Go to Toolbar and click Tasks/ My Tasks.");
        toolbar.clickTasks().clickMyTasks();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » My Tasks", "My Tasks page is not opened");
        log.info("Step 6: Check and confirm that the Task created at step 4 with details. ");
        myTasksPage.clickViewWorkflow("test workflow");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(user.get().getUsername()));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains("test workflow"));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user.get().getUsername()));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8942")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorUploadNewVersionSelfCreated()
    {
        String fileName = "C8942" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(
            documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION),
            "Upload new version action is not available for " + fileName);
        log.info("Step 2: Click Upload New Version");
        documentLibraryPage.selectItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");
        log.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath, "comments", UploadContent.Version.Major);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile), String.format("File [%s] is displayed", newVersionFile));
        log.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile, String.format("Name of %s is wrong.", newVersionFile));
    }

    @Bug (id = "MNT-18059", status = Status.FIXED)
    @TestRail (id = "C8943")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void collaboratorUploadNewVersionOtherUserCreated()
    {
        String fileName = "C8943" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(
            documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.UPLOAD_NEW_VERSION),
            "Upload new version action is not available for " + fileName);
        log.info("Step 2: Click Upload New Version");
        documentLibraryPage.selectItemAction(fileName, ItemActions.UPLOAD_NEW_VERSION);
        Assert.assertTrue(uploadContent.isUploadFilesToDialogDisplayed(), "Upload Files To Dialog is not displayed");
        log.info("Step 3: Select the updated version of testFile and confirm upload.");
        uploadContent.updateDocumentVersion(newVersionFilePath2, "comments", UploadContent.Version.Major);
        assertTrue(documentLibraryPage.isContentNameDisplayed(newVersionFile2), String.format("File [%s] is displayed", newVersionFile2));
        log.info("Step 4: Check the testFile content.");
        documentLibraryPage.clickOnFile(newVersionFile2);
        assertEquals(documentDetailsPage.getContentText(), "updated by upload new version", String.format("Contents of %s are wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileVersion(), "2.0", String.format("Version of %s is wrong.", newVersionFile2));
        assertEquals(documentDetailsPage.getFileName(), newVersionFile2, String.format("Name of %s is wrong.", newVersionFile2));
    }

    @TestRail (id = "C8953")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void collaboratorCheckOutGoogleDocBySelf() throws Exception
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedBySelf), String.format("Document %s is not present", msWordFileCreatedBySelf));
        log.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.selectItemAction(msWordFileCreatedBySelf, ItemActions.EDIT_IN_GOOGLE_DOCS);
        googleDocsCommon.clickOkButton();
        log.info("Step 3: Check the testFile status in Document Library.");
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");
        log.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);
        log.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverContentItem(msWordFileCreatedBySelf);
        log.info("Step 6: Click Check In Google");
        googleDocsCommon.checkInGoogleDoc(msWordFileCreatedBySelf);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);
        log.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);
        log.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(editedTitle), "Locked label displayed");
        Assert.assertEquals(googleDocsCommon.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedTitle), "Name of the document was not updated");
        log.info("Step 9: Click on the testFile name to preview document.");
        documentLibraryPage.clickOnFile(editedTitle);
        log.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail (id = "C8954")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void collaboratorCheckOutGoogleDocByOthers() throws Exception
    {
        log.info("Step 1: Mouse over the testFile and check available actions");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(msWordFileCreatedByOther), String.format("Document %s is not present", msWordFileCreatedByOther));
        log.info("Step 2: Click Check out to Google docs or Edit in Google Docs.");
        googleDocsCommon.loginToGoogleDocs();
        documentLibraryPage.selectItemAction(msWordFileCreatedByOther, ItemActions.EDIT_IN_GOOGLE_DOCS);
        googleDocsCommon.clickOkButton();
        googleDocsCommon.confirmDocumentFormatUpgradeYes();

        log.info("Step 3: Check the testFile status in Document Library.");
        Assert.assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");
        log.info("Step 4: Go back to the Google docs browser window and edit testFile.");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle1, editedContent1);
        log.info("Step 5: Mouse over testFile name and check available actions.");
        documentLibraryPage.mouseOverContentItem(msWordFileCreatedByOther);
        log.info("Step 6: Click Check In Google Doc™.");
        googleDocsCommon.checkInGoogleDoc(msWordFileCreatedByOther);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true);
        log.info("Step 7: Click Ok on the Version Information window.");
        googleDocsCommon.clickOkButton();
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false);
        log.info("Step 8: Check the testFile status and confirm that file has been unlocked.");
        Assert.assertFalse(documentLibraryPage.isInfoBannerDisplayed(editedTitle1), "Locked label displayed");
        Assert.assertEquals(googleDocsCommon.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedTitle1), "Name of the document was not updated");
        log.info("Step 9: Click on the testFile name to preview document.");
        documentLibraryPage.clickOnFile(editedTitle1);
        log.info("Step 10: Check the testFile content.");
        Assert.assertTrue(documentDetailsPage.getContentText().contains(editedContent));
    }

    @TestRail (id = "C8945")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineCreatedBySelf() {
        log.info("Create File in document library.");
        getCmisApi().usingSite(siteName.get()).createFile(msWordFilePlainSelf).assertThat().existsInRepo();
        documentLibraryPage.navigate(siteName.get().getTitle()).assertFileIsDisplayed(msWordFilePlainSelf.getName());
        log.info("Step 1: Mouse over testFile and check available actions.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFilePlainSelf.getName(), ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office™ is not available");
        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail (id = "C8946")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, "office" })
    public void editOnlineCreatedByOtherUser()
    {
        log.info("Create File in document library.");
        getCmisApi().usingSite(siteName.get()).createFile(msWordFilePlainOther).assertThat().existsInRepo();
        documentLibraryPage.navigate(siteName.get().getTitle()).assertFileIsDisplayed(msWordFilePlainOther.getName());
        log.info("Step 1: Mouse over testFile and check that Edit in Microsoft Office™ is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(msWordFilePlainOther.getName(), ItemActions.EDIT_IN_MICROSOFT_OFFICE),
            "Edit in Microsoft Office™ is not available");
        // TODO edit in MSOffice has not yet been automated
    }

    @TestRail (id = "C8949")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editOfflineCreatedBySelf()
    {
        String fileName = "C8949" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline is not available");
        // TODO edit Offline has not yet been automated
    }

    @TestRail (id = "C8950")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editOfflineCreatedByOtherUser()
    {
        String fileName = "C8950" + RandomData.getRandomAlphanumeric();
        contentService.createDocument(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, fileContent);
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step 1: Mouse over testFile and check that Edit Offline is one of the available actions");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileName, ItemActions.EDIT_OFFLINE), "Edit Offline is not available");
        // TODO edit Offline has not yet been automated
    }
}


