package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.CreateFileFromTemplate;
import org.alfresco.po.share.alfrescoContent.CreateFolderFromTemplate;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author Rusu.Andrei
 */

public class CreateTests extends ContextAwareWebTest {
  
  @Autowired
  SharedFilesPage sharedFilesPage;

  @Autowired
  SiteDashboardPage sitePage;

  @Autowired
  DocumentDetailsPage documentDetailsPage;

  @Autowired
  CreateContent create;
  
  @Autowired
  HeaderMenuBar headerMenuBar;
  
  @Autowired
  private UploadContent uploadContent;

  @Autowired
  CreateFolderFromTemplate createFolderFromTemplate;

  @Autowired
  CreateFileFromTemplate createFileFromTemplate;

  @Autowired
  GoogleDocsCommon googleDocs;
  
  @Autowired
  DeleteDialog deleteDialog;


  String folderTemplateName = "Software Engineering Project";
  String fileTemplateName = DataUtil.getUniqueIdentifier() + "fileTemplate.txt";
  String fileTemplatePath = testDataFolder + fileTemplateName;
  String random = DataUtil.getUniqueIdentifier();
  String user2 = "user2-" + random;
  private String title = "googleDoc title";
  private String googleDocName = "googleDoc title.docx";
  private String googleDocSpreadsheet = "googleDoc title.xlsx";
  private String googleDocPresentation = "googleDoc title.pptx";
  private String docContent = "googleDoccontent";

  @BeforeClass
  public void setupTest()
  {
      userService.create(adminUser, adminPassword, user2, password, user2 + "@tests.com", user2, user2);
  }
  
  @TestRail(id = "C7929")
  @Test
  public void sharedFilesCreatePlainTextFile() {

    LOG.info("Precondition: Login as user and navigate to Shared Files page.");
    String user = "user" + DataUtil.getUniqueIdentifier();
    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    setupAuthenticatedSession(user, password);
    sharedFilesPage.navigate();
    Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

    LOG.info("Step 1: Click Create... button");
    sharedFilesPage.clickCreateButton();
    Assert.assertTrue(create.isFolderOptionAvailableUnderCreate(),
        "Create... Folder is not available");
    Assert.assertTrue(create.isPlainTextButtonDisplayed(),
        "Create... Plain Text... is not available");
    Assert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");
    Assert.assertTrue(create.isGoogleDocsDocumentDisplayed(),
        "Create... Google Docs Document... is not available");
    Assert.assertTrue(create.isGoogleDocsSpreadsheetDisplayed(),
        "Create... Google Docs Spreadsheet... is not available");
    Assert.assertTrue(create.isGoogleDocsPresentationDisplayed(),
        "Create... Google Docs Presentation... is not available");
    Assert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"),
        "Create... Create document from template is not displayed");
    Assert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"),
        "Create... Create folder from template is not displayed");

    LOG.info("Step 2: Click \"Plain Text...\" option.");
    create.clickPlainTextButton();
    Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content",
        "Create content page is not opened");
    Assert.assertTrue(create.isCreateFormDisplayed(), "Create Plain Text form is not displayed");
    Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(),
        "The Name field is not displayed on the create form");
    Assert.assertTrue(create.isMandatoryMarketPresentForNameField(),
        "The Name field mandatory marker is not present");
    Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(),
        "The Content field is not displayed on the create form");
    Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(),
        "The Title field is not displayed on the create form");
    Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(),
        "The Description field is not displayed on the create form");
    Assert.assertTrue(create.isCreateButtonPresent(),
        "The Create button is not displayed on the create form");
    Assert.assertTrue(create.isCancelButtonPresent(),
        "The Cancel button is not displayed on the create form");

    LOG.info("Step 3: Fill in the name, content, title and description fields");
    create.sendInputForName("C7929 test name");
    create.sendInputForContent("C7929 test content");
    create.sendInputForTitle("C7929 test title");
    create.sendInputForDescription("C7929 test description");

    LOG.info("Step 4: Click the Create button");
    create.clickCreateButton();
    getBrowser().waitInSeconds(1);
    Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details",
        "File is not previewed in Document Details Page");

    LOG.info("Step 5 : Verify the mimetype for the created file.");
    Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text",
        "Mimetype property is not Plain Text");

    LOG.info("Step 6: Verify the document's preview");
    Assert.assertEquals(documentDetailsPage.getContentText(), "C7929 test content",
        "\"C7929 test content \" is not the content displayed in preview");
    Assert.assertEquals(documentDetailsPage.getFileName(), "C7929 test name",
        "\"C7929 test name\" is not the file name for the file in preview");
    
    LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
    cleanupAuthenticatedSession();
    setupAuthenticatedSession(user2, password);
    sharedFilesPage.navigate();
    assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    assertTrue(sharedFilesPage.isContentNameDisplayed("C7929 test name"), String.format("File [%s] is displayed", "C7929 test name"));

    cleanupAuthenticatedSession();
    
  }

  @TestRail(id = "C7937")
  @Test
  public void sharedFilesCreateHTMLFile() {

    LOG.info("Precondition: Login as user and navigate to Shared Files page.");
    String user = "user" + DataUtil.getUniqueIdentifier();
    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    setupAuthenticatedSession(user, password);
    sharedFilesPage.navigate();
    Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

    LOG.info("Step 1: Click Create... button");
    sharedFilesPage.clickCreateButton();

    LOG.info("Step 2: Click \"HTML...\" option.");
    create.clickHTML();
    Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content",
        "Create content page is not opened");
    Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
    Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(),
        "The Name field is not displayed on the create form");
    Assert.assertTrue(create.isMandatoryMarketPresentForNameField(),
        "The Name field mandatory marker is not present");
    Assert.assertTrue(create.isHTMLContentFieldDisplayed(),
        "The Content field is not displayed on the create form");
    Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(),
        "The Title field is not displayed on the create form");
    Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(),
        "The Description field is not displayed on the create form");
    Assert.assertTrue(create.isCreateButtonPresent(),
        "The Create button is not displayed on the create form");
    Assert.assertTrue(create.isCancelButtonPresent(),
        "The Cancel button is not displayed on the create form");

    LOG.info("Step 3: Fill in the name, content, title and description fields");
    create.sendInputForName("C7937 test name");
    create.sendInputForHTMLContent("C7937 test content");
    create.sendInputForTitle("C7937 test title");
    create.sendInputForDescription("C7937 test description");

    LOG.info("Step 4: Click the Create button");
    create.clickCreateButton();
    getBrowser().waitInSeconds(1);
    documentDetailsPage.renderedPage();
    Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details",
        "File is not previewed in Document Details Page");

    LOG.info("Step 5 : Verify the mimetype for the created file.");
    Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML",
        "Mimetype property is not HTML");

    LOG.info("Step 6: Verify the document's preview");
    Assert.assertEquals(documentDetailsPage.getFileName(), "C7937 test name",
        "\"C7937 test name\" is not the file name for the file in preview");
    
    LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
    cleanupAuthenticatedSession();
    setupAuthenticatedSession(user2, password);
    sharedFilesPage.navigate();
    assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    assertTrue(sharedFilesPage.isContentNameDisplayed("C7937 test name"), String.format("File [%s] is displayed", "C7937 test name"));

    cleanupAuthenticatedSession();
    
  }

  @TestRail(id = "C7938")
  @Test
  public void sharedFilesCreateXMLFile() {

    LOG.info("Precondition: Login as user and navigate to Shared Files page.");
    String user = "user" + DataUtil.getUniqueIdentifier();
    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    setupAuthenticatedSession(user, password);
    sharedFilesPage.navigate();
    Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

    LOG.info("Step 1: Click Create... button");
    sharedFilesPage.clickCreateButton();

    LOG.info("Step 2: Click \"XML...\" option.");
    create.clickXMLButton();
    Assert.assertEquals(create.getPageTitle(), "Alfresco » Create Content",
        "Create content page is not opened");
    Assert.assertTrue(create.isCreateFormDisplayed(), "Create form is not displayed");
    Assert.assertTrue(create.isNameFieldDisplayedOnTheCreateForm(),
        "The Name field is not displayed on the create form");
    Assert.assertTrue(create.isMandatoryMarketPresentForNameField(),
        "The Name field mandatory marker is not present");
    Assert.assertTrue(create.isContentFieldDisplayedOnTheCreateForm(),
        "The Content field is not displayed on the create form");
    Assert.assertTrue(create.isTitleFieldDisplayedOnTheCreateForm(),
        "The Title field is not displayed on the create form");
    Assert.assertTrue(create.isDescriptionFieldDisplayedOnTheCreateForm(),
        "The Description field is not displayed on the create form");
    Assert.assertTrue(create.isCreateButtonPresent(),
        "The Create button is not displayed on the create form");
    Assert.assertTrue(create.isCancelButtonPresent(),
        "The Cancel button is not displayed on the create form");

    LOG.info("Step 3: Fill in the name, content, title and description fields");
    create.sendInputForName("C7938 test name");
    create.sendInputForContent("C7938 test content");
    create.sendInputForTitle("C7938 test title");
    create.sendInputForDescription("C7938 test description");

    LOG.info("Step 4: Click the Create button");
    create.clickCreateButton();
    getBrowser().waitInSeconds(1);
    Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details",
        "File is not previewed in Document Details Page");

    LOG.info("Step 5 : Verify the mimetype for the created file.");
    Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML",
        "Mimetype property is not XML");

    LOG.info("Step 6: Verify the document's preview");
    Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C7938 test content",
        "\"C7938 test content \" is not the content displayed in preview");
    Assert.assertEquals(documentDetailsPage.getFileName(), "C7938 test name",
        "\"C7938 test name\" is not the file name for the file in preview");
    
    LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
    cleanupAuthenticatedSession();
    setupAuthenticatedSession(user2, password);
    sharedFilesPage.navigate();
    assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    assertTrue(sharedFilesPage.isContentNameDisplayed("C7938 test name"), String.format("File [%s] is displayed", "C7938 test name"));

    cleanupAuthenticatedSession();
  }

  @TestRail(id = "C7931")
  @Test
  public void sharedFilesCreateFolderFromTemplate() {

    LOG.info("Precondition: Login as user and navigate to Shared Files page.");
    String user = "user" + DataUtil.getUniqueIdentifier();
    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    setupAuthenticatedSession(user, password);
    sharedFilesPage.navigate();
    Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

    LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
    createFolderFromTemplate.clickCreateContentButton();
    createFolderFromTemplate.hoverOverCreateFolderFromTemplateLink();
    Assert.assertTrue(createFolderFromTemplate.isListOfAvailableTemplatesDisplayed());

    LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
    createFolderFromTemplate.clickOnFolderTemplate(folderTemplateName);
    getBrowser().waitInSeconds(5);
    Assert.assertTrue(createFolderFromTemplate.isCreateFolderFromTemplatePopupDisplayed());
    Assert.assertEquals(createFolderFromTemplate.getTemplateFolderNameFieldValue(),
        folderTemplateName);

    LOG.info("STEP 3: Insert data into input fields and save.");
    createFolderFromTemplate.insertNameInput("Test Folder C7931");
    createFolderFromTemplate.insertTitleInput("Test Title C7931");
    createFolderFromTemplate.insertDescriptionInput("Test Description C7931");
    createFolderFromTemplate.clickButton("Save");
    Assert.assertTrue(createFolderFromTemplate.isFolderCreatedMessageDisplayed());
    Assert.assertTrue(createFolderFromTemplate.checkIfSubfolderExists("Test Folder C7931"),
        "Subfolder not found");
    Assert.assertEquals(createFolderFromTemplate.getFolderNameFromExplorerPanel(), "Test Folder C7931");
    
    LOG.info("Step 4: Login with testUser2 and navigate to Shared Files page.");
    cleanupAuthenticatedSession();
    setupAuthenticatedSession(user2, password);
    sharedFilesPage.navigate();
    assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
    assertTrue(sharedFilesPage.isContentNameDisplayed("Test Folder C7931"), String.format("File [%s] is displayed", "Test Folder C7931"));

    cleanupAuthenticatedSession();
  }
  
  @TestRail(id = "C7932")
  @Test
  public void sharedFilesCreateDocumentFromTemplate()
  {
      LOG.info("Precondition: Login as admin user and create a file template.");
      String user = "user" + DataUtil.getUniqueIdentifier();
      userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
      setupAuthenticatedSession(adminUser, adminPassword);
      sitePage.clickRepositoryLink();
      sharedFilesPage.clickOnFolderName("Data Dictionary");
      getBrowser().waitInSeconds(2);
      sharedFilesPage.clickOnFolderName("Node Templates");
      getBrowser().waitInSeconds(2);
      uploadContent.uploadContent(fileTemplatePath);
      cleanupAuthenticatedSession();

      LOG.info("Precondition: Login as user and navigate to Shared Files page.");
      setupAuthenticatedSession(user, password);
      sharedFilesPage.navigate();
      Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

      LOG.info("STEP 1: Click 'Create' then 'Create document from template'.");
      createFileFromTemplate.clickCreateContentButton();
      createFileFromTemplate.hoverOverCreateDocumentFromTemplateLink();
      Assert.assertTrue(createFileFromTemplate.isListOfAvailableTemplatesDisplayed());

      LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
      createFileFromTemplate.clickOnFileTemplate(fileTemplateName);
      getBrowser().waitInSeconds(1);
      Assert.assertTrue(createFileFromTemplate.isDocumentCreatedMessageDisplayed());
      Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));
      
      LOG.info("Step 3: Login with testUser2 and navigate to Shared Files page.");
      cleanupAuthenticatedSession();
      setupAuthenticatedSession(user2, password);
      sharedFilesPage.navigate();
      assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
      assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("File [%s] is displayed", fileTemplateName));

      cleanupAuthenticatedSession();
  }
  
  @TestRail(id = "C7934")
  @Test
  public void sharedFilesCreateGoogleDocsDocument() throws Exception
  {
      LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
      String user = "user" + DataUtil.getUniqueIdentifier();
      userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
      setupAuthenticatedSession(user, password);
      googleDocs.loginToGoogleDocs();
      getBrowser().waitInSeconds(3);
      sharedFilesPage.navigate();
      Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

      LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
      sharedFilesPage.clickCreateButton();
      create.clickGoogleDocsDoc();
      Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

      LOG.info("Step 2: Click Ok button on the Authorize ");
      googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
      getBrowser().waitInSeconds(5);

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
  }
  
  @TestRail(id = "C7935")
  @Test
  public void sharedFilesCreateGoogleDocsSpreadsheet() throws Exception
  {
      LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
      String user = "user" + DataUtil.getUniqueIdentifier();
      userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
      setupAuthenticatedSession(user, password);
      googleDocs.loginToGoogleDocs();
      getBrowser().waitInSeconds(3);
      sharedFilesPage.navigate();
      Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

      LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
      sharedFilesPage.clickCreateButton();
      create.clickGoogleDocsSpreadsheet();
      Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

      LOG.info("Step 2: Click Ok button on the Authorize ");
      googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
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
  }
  
  @TestRail(id = "C7936")
  @Test
  public void sharedFilesCreateGoogleDocsPresentation() throws Exception
  {
    LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
    String user = "user" + DataUtil.getUniqueIdentifier();
    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    setupAuthenticatedSession(user, password);
    googleDocs.loginToGoogleDocs();
    getBrowser().waitInSeconds(3);
    sharedFilesPage.navigate();
    Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

    LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
    sharedFilesPage.clickCreateButton();
    create.clickGoogleDocsPresentation();
    Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

    LOG.info("Step 2: Click Ok button on the Authorize ");
    googleDocs.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();
    getBrowser().waitInSeconds(5);

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
  }
  
  @AfterClass
  public void cleanUp()
  {
      setupAuthenticatedSession(adminUser, adminPassword);

      LOG.info("Delete All from 'Shared Files'");
      sharedFilesPage.navigate();
      headerMenuBar.clickSelectMenu();
      headerMenuBar.clickSelectOption(language.translate("documentLibrary.breadcrumb.select.all"));
      headerMenuBar.clickSelectedItemsMenu();
      headerMenuBar.clickSelectedItemsOption(language.translate("documentLibrary.breadcrumb.selectedItems.delete"));
      deleteDialog.clickDelete();
  }
  
  

}
