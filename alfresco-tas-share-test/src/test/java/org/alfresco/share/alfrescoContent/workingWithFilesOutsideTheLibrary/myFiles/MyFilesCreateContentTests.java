package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.CreateFileFromTemplate;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesCreateContentTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    SiteDashboardPage sitePage;
    
    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    CreateContent create;

    @Autowired
    private UploadContent uploadContent;

    @Autowired
    CreateFolderFromTemplate createFolderFromTemplate;

    @Autowired
    CreateFileFromTemplate createFileFromTemplate;

    @Autowired
    GoogleDocsCommon googleDocs;

    String folderTemplateName = "Software Engineering Project";
    String fileTemplateName = DataUtil.getUniqueIdentifier() + "fileTemplate.txt";
    String fileTemplatePath = testDataFolder + fileTemplateName;
    private String title = "googleDoc title";
    private String googleDocName = "googleDoc title.docx";
    private String googleDocSpreadsheet = "googleDoc title.xlsx";
    private String googleDocPresentation = "googleDoc title.pptx";
    private String docContent = "googleDoccontent";

    @TestRail(id = "C7650")
    @Test
    public void myFilesCreatePlainTextFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();
        Assert.assertTrue(create.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        Assert.assertTrue(create.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        Assert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        Assert.assertTrue(create.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        Assert.assertTrue(create.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        Assert.assertTrue(create.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        create.clickPlainTextButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C7650 test name");
        create.sendInputForContent("C7650 test content");
        create.sendInputForTitle("C7650 test title");
        create.sendInputForDescription("C7650 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "C7650 test content", "\"C7650 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7650 test name", "\"C7650 test name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C7696")
    @Test
    public void myFilesCreateHTMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"HTML...\" option.");
        create.clickHTML();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isHTMLContentFieldDisplayed(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C7696 test name");
        create.sendInputForHTMLContent("C7696 test content");
        create.sendInputForTitle("C7696 test title");
        create.sendInputForDescription("C7696 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7696 test name", "\"C7696 test name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C7697")
    @Test
    public void myFilesCreateXMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click Create... button");
        myFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"XML...\" option.");
        create.clickXMLButton();
        Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");
        Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
        Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(), "The Name field is not displayed on the create form");
        Assert.assertTrue(create.isMandatoryMarketPresentForNameField(), "The Name field mandatory marker is not present");
        Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(), "The Content field is not displayed on the create form");
        Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(), "The Title field is not displayed on the create form");
        Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(), "The Description field is not displayed on the create form");
        Assert.assertTrue(create.isCreateButtonPresent(), "The Create button is not displayed on the create form");
        Assert.assertTrue(create.isCancelButtonPresent(), "The Cancel button is not displayed on the create form");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        create.sendInputForName("C7697 test name");
        create.sendInputForContent("C7697 test content");
        create.sendInputForTitle("C7697 test title");
        create.sendInputForDescription("C7697 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        getBrowser().waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C7697 test content", "\"C7697 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C7697 test name", "\"C6978 test name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C7653")
    @Test
    public void myFilesCreateFolderFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        createFolderFromTemplate.clickCreateContentButton();
        createFolderFromTemplate.hoverOverCreateFolderFromTemplateLink();
        Assert.assertTrue(createFolderFromTemplate.isListOfAvailableTemplatesDisplayed());

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createFolderFromTemplate.clickOnFolderTemplate(folderTemplateName);
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
        Assert.assertEquals(createFolderFromTemplate.getTemplateFolderNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate.insertNameInput("Test Folder");
        createFolderFromTemplate.insertTitleInput("Test Title");
        createFolderFromTemplate.insertDescriptionInput("Test Description");
        createFolderFromTemplate.clickButton("Save");
        Assert.assertTrue(createFolderFromTemplate.isFolderCreatedMessageDisplayed());
        Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Test Folder"), "Subfolder not found");
        Assert.assertEquals(createFolderFromTemplate.getFolderNameFromExplorerPanel(), "Test Folder");
    }

    @TestRail(id = "C12858")
    @Test
    public void myFilesCreateFileFromTemplate()
    {
        LOG.info("Precondition: Login as admin user and create a file template.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(adminUser, adminPassword);
        sitePage.clickRepositoryLink();
        myFilesPage.clickOnFolderName("Data Dictionary");
        getBrowser().waitInSeconds(2);
        myFilesPage.clickOnFolderName("Node Templates");
        getBrowser().waitInSeconds(2);
        uploadContent.uploadContent(fileTemplatePath);
        cleanupAuthenticatedSession();

        LOG.info("Precondition: Login as user and navigate to My Files page.");
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("STEP 1: Click 'Create' then 'Create file from template'.");
        createFileFromTemplate.clickCreateContentButton();
        createFileFromTemplate.hoverOverCreateDocumentFromTemplateLink();
        Assert.assertTrue(createFileFromTemplate.isListOfAvailableTemplatesDisplayed());

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        createFileFromTemplate.clickOnFileTemplate(fileTemplateName);
        getBrowser().waitInSeconds(1);
        Assert.assertTrue(createFileFromTemplate.isDocumentCreatedMessageDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));
    }

    @TestRail(id = "C7693")
    @Test
    public void myFilesCreateGoogleDocsDocument() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        myFilesPage.clickCreateButton();
        create.clickGoogleDocsDoc();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName));
    }

    @TestRail(id = "C7694")
    @Test
    public void myFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        myFilesPage.clickCreateButton();
        create.clickGoogleDocsSpreadsheet();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(7);

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
    }

    @TestRail(id = "C7695")
    @Test
    public void myFilesCreateGoogleDocsPresentation() throws Exception
    {
        LOG.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        googleDocs.loginToGoogleDocs();
        getBrowser().waitInSeconds(3);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        myFilesPage.clickCreateButton();
        create.clickGoogleDocsPresentation();
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
        getBrowser().waitInSeconds(5);

        LOG.info("Step 3: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        getBrowser().waitInSeconds(9);
        Assert.assertEquals(googleDocs.checkLockedLAbelIsDisplayed(), false);
        Assert.assertEquals(googleDocs.checkGoogleDriveIconIsDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocPresentation));
    }
}

