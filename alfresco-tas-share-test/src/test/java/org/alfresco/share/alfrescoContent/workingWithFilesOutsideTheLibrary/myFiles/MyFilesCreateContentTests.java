package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesCreateContentTests extends ContextAwareWebTest
{
    private final String folderTemplateName = "Software Engineering Project";
    private final String fileTemplateName = String.format("fileTemplate%s", RandomData.getRandomAlphanumeric());
    private final String title = "googleDoc title";
    private final String googleDocName = "googleDoc title.docx";
    private final String googleDocSpreadsheet = "googleDoc title.xlsx";
    private final String googleDocPresentation = "googleDoc title.pptx";
    private final String docContent = "googleDoccontent";
    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    @Autowired
    private MyFilesPage myFilesPage;
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

    @BeforeClass (alwaysRun = true)
    public void createPrecondition()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, "Data Dictionary/Node Templates", CMISUtil.DocumentType.TEXT_PLAIN, fileTemplateName, "some content");

        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        contentService.deleteContentByPath(adminUser, adminPassword, "Data Dictionary/Node Templates/" + fileTemplateName);

    }

    @TestRail (id = "C7650")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreatePlainTextFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();
        Assert.assertTrue(createContent.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        Assert.assertTrue(createContent.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        Assert.assertTrue(createContent.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        Assert.assertTrue(createContent.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        Assert.assertTrue(createContent.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        Assert.assertTrue(createContent.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        Assert.assertTrue(createContent.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
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
        createContent.sendInputForName("C7650 test name");
        createContent.sendInputForContent("C7650 test content");
        createContent.sendInputForTitle("C7650 test title");
        createContent.sendInputForDescription("C7650 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        assertEquals(documentDetailsPage.getContentText(), "C7650 test content", "\"C7650 test content \" is not the content displayed in preview");
        assertEquals(documentDetailsPage.getFileName(), "C7650 test name", "\"C7650 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7696")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateHTMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

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
        createContent.sendInputForName("C7696 test name");
        createContent.sendInputForHTMLContent("C7696 test content");
        createContent.sendInputForTitle("C7696 test title");
        createContent.sendInputForDescription("C7696 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7696 test name", "\"C7696 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7697")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateXMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

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
        createContent.sendInputForName("C7697 test name");
        createContent.sendInputForContent("C7697 test content");
        createContent.sendInputForTitle("C7697 test title");
        createContent.sendInputForDescription("C7697 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C7697 test content", "\"C7697 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7697 test name", "\"C6978 test name\" is not the file name for the file in preview");
    }

    @TestRail (id = "C7653")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesCreateFolderFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        myFilesPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create folder from template");
        Assert.assertTrue(createContent.isFolderTemplateDisplayed(folderTemplateName));
        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnFolderTemplate(folderTemplateName, createFolderFromTemplate);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate.fillInDetails("Test Folder", "Test Title", "Test Description");
        createFolderFromTemplate.clickSaveButton();
        Assert.assertEquals(notification.getDisplayedNotification(), String.format("Folder '%s' created", "Test Folder"));
        notification.waitUntilNotificationDisappears();
        Assert.assertTrue(myFilesPage.getFoldersList().contains("Test Folder"), "Subfolder not found");
        Assert.assertTrue(myFilesPage.getExplorerPanelDocuments().contains("Test Folder"), "Subfolder not found in Documents explorer panel");
    }

    @TestRail (id = "C12858")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesCreateFileFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("STEP 1: Click 'Create' then 'Create file from template'.");
        myFilesPage.clickCreateButton();
        createContent.clickCreateFromTemplateButton("Create document from template");
        Assert.assertTrue(createContent.isFileTemplateDisplayed(fileTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createContent.clickOnDocumentTemplate(fileTemplateName, myFilesPage);
        Assert.assertEquals(notification.getDisplayedNotification(), String.format("Created content based on template '%s'", fileTemplateName), "Notification message appears");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));
    }

    @TestRail (id = "C7693")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        myFilesPage.clickCreateButton();
        createContent.clickGoogleDocsDoc();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocName), "Document is unlocked");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName));
    }

    @TestRail (id = "C7694")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        myFilesPage.clickCreateButton();
        createContent.clickGoogleDocsSpreadsheet();

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocSpreadsheet), "Document is unlocked");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
    }

    @TestRail (id = "C7695")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        myFilesPage.clickCreateButton();
        createContent.clickGoogleDocsPresentation();

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocPresentation), "Document is unlocked");
        Assert.assertEquals(googleDocs.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocPresentation));
    }
}

