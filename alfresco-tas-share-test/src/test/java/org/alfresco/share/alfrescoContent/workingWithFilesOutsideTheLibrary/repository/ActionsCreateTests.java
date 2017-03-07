package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.crypto.Data;

public class ActionsCreateTests extends ContextAwareWebTest
{
    @Autowired
    CreateContent create;

    @Autowired
    RepositoryPage repository;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    private String user = "C8156User" + DataUtil.getUniqueIdentifier();
    private String description = "C8156SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C8156SiteName" + DataUtil.getUniqueIdentifier();
    private String path = "Data Dictionary/Node Templates";
    private String docName = "C8159template2" + DataUtil.getUniqueIdentifier();
    private String docContent = "C8159 template content";
    private String pathFolderTemplate = "Data Dictionary/Space Templates";
    private String folderName = "C8158" + DataUtil.getUniqueIdentifier();

    @BeforeClass

    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, pathFolderTemplate);
        setupAuthenticatedSession(user, password);

    }

    @TestRail(id = "C8156")
    @Test

    public void createPlainTextDocumentInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickOnContent("User Homes");
        repository.clickOnContent(user);

        LOG.info("Step 1: Click Create... button.");
        repository.clickCreateButton();
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
        create.sendInputForName("C8156 name");
        create.sendInputForContent("C8156 content");
        create.sendInputForTitle("C8156 title");
        create.sendInputForDescription("C8156 description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        browser.waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "C8156 content", "\"C8156 content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8156 name", "\"C8156 name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C8161")
    @Test

    public void createHTMLDocumentInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickOnContent("User Homes");
        repository.clickOnContent(user);

        LOG.info("Step 1: Click Create... button");
        repository.clickCreateButton();
        Assert.assertTrue(create.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        Assert.assertTrue(create.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        Assert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        Assert.assertTrue(create.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        Assert.assertTrue(create.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        Assert.assertTrue(create.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");

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
        create.sendInputForName("C8161 test name");
        create.sendInputForHTMLContent("C8161 test content");
        create.sendInputForTitle("C8161 test title");
        create.sendInputForDescription("C8161 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        browser.waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8161 test name", "\"C8161 test name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C8162")
    @Test

    public void createXMLFile()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickOnContent("User Homes");
        repository.clickOnContent(user);

        LOG.info("Step 1: Click Create... button");
        repository.clickCreateButton();
        Assert.assertTrue(create.isFolderOptionAvailableUnderCreate(), "Create... Folder is not available");
        Assert.assertTrue(create.isPlainTextButtonDisplayed(), "Create... Plain Text... is not available");
        Assert.assertTrue(create.isHTMLButtonDisplayed(), "Create... HTML... is not available");
        Assert.assertTrue(create.isGoogleDocsDocumentDisplayed(), "Create... Google Docs Document... is not available");
        Assert.assertTrue(create.isGoogleDocsSpreadsheetDisplayed(), "Create... Google Docs Spreadsheet... is not available");
        Assert.assertTrue(create.isGoogleDocsPresentationDisplayed(), "Create... Google Docs Presentation... is not available");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create document from template"), "Create... Create document from template is not displayed");
        Assert.assertTrue(create.isCreateFromTemplateAvailable("Create folder from template"), "Create... Create folder from template is not displayed");

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
        create.sendInputForName("C8162 test name");
        create.sendInputForContent("C8162 test content");
        create.sendInputForTitle("C8162 test title");
        create.sendInputForDescription("C8162 test description");

        LOG.info("Step 4: Click the Create button");
        create.clickCreateButton();
        browser.waitInSeconds(1);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "XML", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText().trim(), "C8162 test content",
                "\"C8162 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), "C8162 test name", "\"C8162 test name\" is not the file name for the file in preview");
    }

    @TestRail(id = "C8159")
    @Test

    public void createDocumentFromTemplate()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickOnContent("User Homes");
        repository.clickOnContent(user);

        LOG.info("Step 1:Click 'Create' then click 'Create document from template'.");

        repository.clickCreateButton();
        create.clickCreateDocumentFromTemplate("Create document from template");
        browser.waitInSeconds(2);
        Assert.assertTrue(create.isTemplateDisplayed(docName), "Template is not displayed");

        LOG.info("Step 2: Select the template and check that the new file is created with the content from the template used");

        create.clickOnTemplate(docName);
        browser.waitInSeconds(2);
        Assert.assertTrue(repository.isContentDisplayed(docName), "Newly created document is not displayed in Repository/UserHomes ");
        repository.clickOnContent(docName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Document is not previewed");
        Assert.assertEquals(documentDetailsPage.getContentText(), docContent);
    }

    @TestRail(id = "C8158")
    @Test

    public void createFolderFromTemplateInRepository()
    {
        // Preconditions
        setupAuthenticatedSession(user, password);
        repository.navigate();
        repository.clickOnContent("User Homes");
        repository.clickOnContent(user);

        LOG.info("Step 1: Select the Create menu > Create folder from templates");
        repository.clickCreateButton();
        create.clickCreateFolderFromTemplate("Create folder from template");
        browser.waitInSeconds(2);
        Assert.assertTrue(create.isTemplateDisplayed(folderName), "Template is not displayed");

        LOG.info("Step 2: Select the test template, provide title and description and check that the new folder is created");

        create.clickOnTemplate(folderName);
        //browser.waitInSeconds(2);
        Assert.assertTrue(create.isCreateFolderFromTemplateFromDisplayed(), "Create Folder From Template form is not displayed");
        
        LOG.info("Step 3: Provide data for Create Folder From Template Form");
        create.provideTitleForCreateFolderFromTemplate("C8158 folder");
        create.provideDecsriptionForCreateFolderFromTemplate("C8158 folder description");
        create.clickSaveButtonOnCreateFormTemplate();
        
        browser.waitInSeconds(2);
        Assert.assertTrue(repository.isContentDisplayed(folderName), "Newly created document is not displayed in Repository/UserHomes ");  
    }

    @TestRail(id ="C13745")
    @Test
    
    public void checkThatUserWithoutAdminPermissionsCannotCreateInMainRepository()
    {
        setupAuthenticatedSession(user, password);
        repository.navigate();
        LOG.info("Step 1: Check the Create button.");
        Assert.assertEquals(repository.getCreateButtonStatusDisabled(), "true", "The Create Button is not disabled");
        Assert.assertEquals(repository.getUploadButtonStatusDisabled(), "true", "The Upload Button is not disabled");

        LOG.info("Step 2: Click the Create button");
        repository.clickCreateButtonWithoutWait();
        Assert.assertFalse(repository.isCreateContentMenuDisplayed(), "Create Content menu is displayed when the Create button is clicked");
        cleanupAuthenticatedSession();
    }
    
    @TestRail(id =" C13746")
    @Test 
    
    public void checkThatTheCreateOptionIsAvailableForAdminInMainRepository()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        repository.navigate();
        LOG.info("Step 1: Check the Create button.");
        Assert.assertEquals(repository.getCreateButtonStatusDisabled(), null, "The Create Button is disabled");
        Assert.assertEquals(repository.getUploadButtonStatusDisabled(), null, "The Upload Button is disabled");

        LOG.info("Step 2: Click the Create button");
        repository.clickCreateButtonWithoutWait();
        Assert.assertTrue(repository.isCreateContentMenuDisplayed(), "Create Content menu is not displayed when the Create button is clicked");
        cleanupAuthenticatedSession();     
    }
}
