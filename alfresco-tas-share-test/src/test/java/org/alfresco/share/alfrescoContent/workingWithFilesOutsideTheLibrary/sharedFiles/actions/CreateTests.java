package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
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
 * @author Rusu.Andrei
 */

public class CreateTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String folderTemplateName = "Software Engineering Project";
    private final String fileTemplateName = RandomData.getRandomAlphanumeric() + "fileTemplate.txt";
    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String user2 = String.format("user2-%s", RandomData.getRandomAlphanumeric());
    private final String title = "googleDocTitle";
    private final String googleDocName = "googleDocTitle.docx";
    private final String googleDocSpreadsheet = "googleDocTitle.xlsx";
    private final String googleDocPresentation = "googleDocTitle.pptx";
    private final String docContent = "googleDoccontent";
    //@Autowired
    private SharedFilesPage sharedFilesPage;
   // @Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private CreateContentPage createContent;
    //@Autowired
    private NewFolderDialog createFolderFromTemplate;
    @Autowired
    private GoogleDocsCommon googleDocs;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
    }

    @AfterClass
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
    }

    @TestRail (id = "C7929")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed"  })
    public void sharedFilesCreatePlainTextFile()
    {
        String testName = "C7929TestName" + random;

        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();
        Assert.assertTrue(sharedFilesPage.areCreateOptionsAvailable(), "Create menu options are not available");

        LOG.info("Step 2: Click \"Plain Text...\" option.");
        sharedFilesPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName(testName);
        createContent.typeContent("C7929 test content");
        createContent.typeTitle("C7929 test title");
        createContent.typeDescription("C7929 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "Plain Text", "Mimetype property is not Plain Text");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getContentText(), "C7929 test content", "\"C7929 test content \" is not the content displayed in preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), testName, testName + " is not the file name for the file in preview");

        LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(testName), String.format("File [%s] is displayed", testName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + testName);
    }

    @TestRail (id = "C7937")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateHTMLFile()
    {
        String testName = "C7937TestName" + random;
        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"HTML...\" option.");
        sharedFilesPage.clickCreateContentOption(CreateMenuOption.HTML);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName(testName);
        createContent.sendInputForHTMLContent("C7937 test content");
        createContent.typeTitle("C7937 test title");
        createContent.typeDescription("C7937 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "File is not previewed in Document Details Page");

        LOG.info("Step 5 : Verify the mimetype for the created file.");
        Assert.assertEquals(documentDetailsPage.getPropertyValue("Mimetype:"), "HTML", "Mimetype property is not HTML");

        LOG.info("Step 6: Verify the document's preview");
        Assert.assertEquals(documentDetailsPage.getFileName(), testName, testName + " is not the file name for the file in preview");

        LOG.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(testName), String.format("File [%s] is displayed", testName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + testName);
    }

    @TestRail (id = "C7938")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateXMLFile()
    {
        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        LOG.info("Step 2: Click \"XML...\" option.");
        sharedFilesPage.clickCreateContentOption(CreateMenuOption.XML);
        Assert.assertEquals(createContent.getPageTitle(), "Alfresco » Create Content", "Create content page is not opened");

        LOG.info("Step 3: Fill in the name, content, title and description fields");
        createContent.typeName("C7938TestName");
        createContent.typeContent("C7938 test content");
        createContent.typeTitle("C7938 test title");
        createContent.typeDescription("C7938 test description");

        LOG.info("Step 4: Click the Create button");
        createContent.clickCreate();
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

    @TestRail (id = "C7931")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateFolderFromTemplate()
    {
        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE);

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        sharedFilesPage.clickOnTemplate(folderTemplateName);
        Assert.assertEquals(createFolderFromTemplate.getNameFieldValue(), folderTemplateName);

        LOG.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate.fillInDetails("TestFolderC7931", "Test Title C7931", "Test Description C7931");
        createFolderFromTemplate.clickSave();
        sharedFilesPage.renderedPage();
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

    @TestRail (id = "C7932")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateDocumentFromTemplate()
    {
        LOG.info("Precondition: Login as admin user and create a file template.");
        contentService.createDocumentInRepository(adminUser, adminPassword, "Data Dictionary/Node Templates", CMISUtil.DocumentType.TEXT_PLAIN, fileTemplateName, "some content");

        LOG.info("Precondition: Login as user and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();

        LOG.info("STEP 1: Click 'Create' then 'Create document from template'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE);
        Assert.assertTrue(sharedFilesPage.isTemplateDisplayed(fileTemplateName));

        LOG.info("STEP 2: Select the template: 'Software Engineering Project'");
        sharedFilesPage.clickOnTemplate(fileTemplateName);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("Content: %s is not displayed.", fileTemplateName));

        LOG.info("Step 3: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(fileTemplateName), String.format("File [%s] is displayed", fileTemplateName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + fileTemplateName);
        contentService.deleteContentByPath(adminUser, adminPassword, "Data Dictionary/Node Templates/" + fileTemplateName);

    }

    @TestRail (id = "C7934")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void sharedFilesCreateGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Document.docx"), "The file created with Google Docs is not present");
        assertEquals(sharedFilesPage.getInfoBannerText("Untitled Document.docx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
        sharedFilesPage.renderedPage();
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocName), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        LOG.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName), String.format("File [%s] is displayed", googleDocName));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled document" + googleDocName);
    }

    @TestRail (id = "C7935")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void sharedFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_SPREADSHEET);

        LOG.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertEquals(sharedFilesPage.getInfoBannerText("Untitled Spreadsheet.xlsx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        sharedFilesPage.renderedPage();
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocSpreadsheet), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        LOG.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet), String.format("File [%s] is displayed", googleDocSpreadsheet));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled spreadsheet" + googleDocSpreadsheet);
    }

    @TestRail (id = "C7936")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void sharedFilesCreateGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        LOG.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        LOG.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_PRESENTATION);

        googleDocs.clickOkButtonOnTheAuthPopup();

        LOG.info("Step 2: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Presentation.pptx"), "The file created with Google Docs is not present");
        assertEquals(sharedFilesPage.getInfoBannerText("Untitled Presentation.pptx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step 3: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        sharedFilesPage.renderedPage();
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocPresentation), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        LOG.info("Step 4: Login with testUser2 and navigate to Shared Files page.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation), String.format("File [%s] is displayed", googleDocPresentation));

        cleanupAuthenticatedSession();
        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled presentation" + googleDocPresentation);
    }
}
