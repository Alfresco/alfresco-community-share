package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Rusu.Andrei
 */

public class CreateTests extends ContextAwareWebTest
{
    @Autowired
    private SharedFilesPage sharedFilesPage;
    @Autowired
    private DocumentDetailsPage documentDetailsPage;
    @Autowired
    private CreateContent createContent;
    @Autowired
    private CreateFolderFromTemplate createFolderFromTemplate;
    @Autowired
    private GoogleDocsCommon googleDocs;
    @Autowired
    private Notification notification;


    private final String folderTemplateName = "Software Engineering Project";
    private final String fileTemplateName = DataUtil.getUniqueIdentifier() + "fileTemplate.txt";
    private final String user = "user" + DataUtil.getUniqueIdentifier();
    private final String user2 = "user2-" + DataUtil.getUniqueIdentifier();

    private final String title = "googleDocTitle";
    private final String googleDocName = "googleDocTitle.docx";
    private final String googleDocSpreadsheet = "googleDocTitle.xlsx";
    private final String googleDocPresentation = "googleDocTitle.pptx";
    private final String docContent = "googleDoccontent";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
    }

    @TestRail(id = "C7929")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreatePlainTextFile()
    {

        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();
        Assert.assertTrue(createContent.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        Assert.assertTrue(createContent.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        Assert.assertTrue(createContent.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        Assert.assertTrue(createContent.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        Assert.assertTrue(createContent.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        Assert.assertTrue(createContent.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        Assert.assertTrue(createContent.isCreateFromTemplateAvailable("Create document from template"),
                "Create... Create document from template is not displayed");
        Assert.assertTrue(createContent.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        createContent.clickPlainTextButton();
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(createContent.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
        Assert.assertTrue(createContent.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(createContent.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(createContent.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(createContent.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(createContent.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(createContent.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(createContent.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.sendInputForName("C7929TestName");
        createContent.sendInputForContent("C7929 test content");
        createContent.sendInputForTitle("C7929 test title");
        createContent.sendInputForDescription("C7929 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "C7929 test content", "\"C7929 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7929TestName", "\"C7929TestName\" is not the file name for the file in preview");

        LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed("C7929TestName"), String.format("File [%s] is displayed", "C7929TestName"));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/C7929TestName");
    }

    @TestRail(id = "C7937")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateHTMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"HTML...\" option.");
        createContent.clickHTML();
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(createContent.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(createContent.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(createContent.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(createContent.isHTMLContentFieldDisplayed(), "The Content field is not displayed on the create form");
        Assert.assertTrue(createContent.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(createContent.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(createContent.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(createContent.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.sendInputForName("C7937TestName");
        createContent.sendInputForHTMLContent("C7937 test content");
        createContent.sendInputForTitle("C7937 test title");
        createContent.sendInputForDescription("C7937 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        documentDetailsPage.renderedPage();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7937TestName", "\"C7937TestName\" is not the file name for the file in preview");

        LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed("C7937TestName"), String.format("File [%s] is displayed", "C7937TestName"));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/C7937TestName");
    }

    @TestRail(id = "C7938")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateXMLFile()
    {

        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"XML...\" option.");
        createContent.clickXMLButton();
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(createContent.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(createContent.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(createContent.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(createContent.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(createContent.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(createContent.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(createContent.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(createContent.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.sendInputForName("C7938TestName");
        createContent.sendInputForContent("C7938 test content");
        createContent.sendInputForTitle("C7938 test title");
        createContent.sendInputForDescription("C7938 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not XML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C7938 test content",
                "\"C7938 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7938TestName", "\"C7938TestName\" is not the file name for the file in preview");

        LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed("C7938TestName"), String.format("File [%s] is displayed", "C7938TestName"));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/C7938TestName");
    }

    @TestRail(id = "C7931")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateFolderFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        sharedFilesPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create folder from template");

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnTemplate(folderTemplateName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate.fillInDetails("TestFolderC7931", "Test Title C7931", "Test Description C7931");
        createFolderFromTemplate.clickSaveButton();
        assertEquals(notification.getDisplayedNotification(), String.format("Folder '%s' created", "TestFolderC7931"));
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("TestFolderC7931"), "Subfolder not found");
        Assert.assertTrue(sharedFilesPage.getExplorerPanelDocuments().contains("TestFolderC7931"), "Subfolder not found in Documents explorer panel");

        LOG.info("Step 4: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed("TestFolderC7931"), String.format("File [%s] is displayed", "TestFolderC7931"));

        cleanupAuthenticatedSession();
        contentService.deleteTreeByPath(adminUser, adminPassword, "Shared/TestFolderC7931");
    }

    @TestRail(id = "C7932")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateDocumentFromTemplate()
    {
        LOG.info("Precondition: Login as admin user and create a file template.");
        contentService.createDocumentInRepository(adminUser, adminPassword, "Data Dictionary/Node Templates", CMISUtil.DocumentType.TEXT_PLAIN, fileTemplateName, "some content");

        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("STEP 1: Click 'Create' then 'Create document from template'.");
        sharedFilesPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create document from template");
        Assert.assertTrue(createContent.isTemplateDisplayed(fileTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnTemplate(fileTemplateName, sharedFilesPage);
        Assert.assertEquals(notification.getDisplayedNotification(), String.format("Created content based on template '%s'", fileTemplateName), "Notification message appears");
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));

        LOG.info("Step 3: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("File [%s] is displayed", fileTemplateName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + fileTemplateName);
    }

    @TestRail(id = "C7934")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateGoogleDocsDocument() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        sharedFilesPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName));

        LOG.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName), String.format("File [%s] is displayed", googleDocName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + googleDocName);
    }

    @TestRail(id = "C7935")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        sharedFilesPage.clickCreateButton();
        createContent.clickGoogleDocsSpreadsheet();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();
        getBrowser().waitInSeconds(7);

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet));

        LOG.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet), String.format("File [%s] is displayed", googleDocSpreadsheet));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + googleDocSpreadsheet);
    }

    @TestRail(id = "C7936")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void sharedFilesCreateGoogleDocsPresentation() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        sharedFilesPage.clickCreateButton();
        createContent.clickGoogleDocsPresentation();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation));

        LOG.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation), String.format("File [%s] is displayed", googleDocPresentation));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + googleDocPresentation);
    }
}
