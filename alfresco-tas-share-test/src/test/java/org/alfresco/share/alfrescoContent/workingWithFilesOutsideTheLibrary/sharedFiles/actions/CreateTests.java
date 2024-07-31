package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Rusu.Andrei
 */

public class CreateTests extends BaseTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String folderTemplateName = "Software Engineering Project";
    private final String title = "googleDocTitle";
    private final String googleDocName = "googleDocTitle.docx";
    private final String googleDocSpreadsheet = "googleDocTitle.xlsx";
    private final String googleDocPresentation = "googleDocTitle.pptx";
    private final String docContent = "googleDoccontent";
    private final String templateContent = "template content";
    private SharedFilesPage sharedFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private CreateContentPage createContent;
    private NewFolderDialog createFolderFromTemplate;
    private DeleteDialog deleteDialog;
//    @Autowired
    private GoogleDocsCommon googleDocs;
    private UserModel testUser1;
    private UserModel testUser2;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        sharedFilesPage = new SharedFilesPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        createFolderFromTemplate = new NewFolderDialog(webDriver);
        deleteDialog = new DeleteDialog(webDriver);

        log.info("PreCondition1: Two test users are created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        testUser2 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());
    }

    @AfterMethod
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);
    }

    @TestRail (id = "C7929")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreatePlainTextFile()
    {
        String testName = "C7929TestName" + random;

        log.info("Precondition: Login as user and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();
        Assert.assertTrue(sharedFilesPage.areCreateOptionsAvailable(), "Create menu options are not available");

        log.info("Step 2: Click \"Plain Text...\" option.");
        sharedFilesPage.clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        createContent.assertBrowserPageTitleIs("Alfresco » Create Content");


        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName(testName)
            .typeContent("C7929 test content")
            .typeTitle("C7929 test title")
            .typeDescription("C7929 test description");

        log.info("Step 4: Click the Create button");
        createContent
            .clickCreate();
        documentDetailsPage
            .assertBrowserPageTitleIs("Alfresco » Document Details");

        log.info("Step 5 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "Plain Text");

        log.info("Step 6: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7929 test content")
            .assertIsFileNameDisplayedOnPreviewPage(testName);

        log.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsDisplayed(testName);

        log.info("Delete the created File");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testName, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C7937")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateHTMLFile()
    {
        String testName = "C7937TestName" + random;
        log.info("Precondition: Login as user and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        log.info("Step 2: Click \"HTML...\" option.");
        sharedFilesPage.clickCreateContentOption(CreateMenuOption.HTML);
        createContent.assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName(testName)
            .sendInputForHTMLContent("C7937 test content")
            .typeTitle("C7937 test title")
            .typeDescription("C7937 test description");

        log.info("Step 4: Click the Create button");
        createContent
            .clickCreate()
            .assertBrowserPageTitleIs("Alfresco » Document Details");

        log.info("Step 5 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "HTML");

        log.info("Step 6: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7937 test content")
            .assertIsFileNameDisplayedOnPreviewPage(testName);

        log.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsDisplayed(testName);

        log.info("Delete the created File");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testName, ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C7938")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateXMLFile()
    {
        log.info("Precondition: Login as user and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("Step 1: Click Create... button");
        sharedFilesPage.clickCreateButton();

        log.info("Step 2: Click \"XML...\" option.");
        sharedFilesPage
            .clickCreateContentOption(CreateMenuOption.XML)
            .assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName("C7938TestName")
            .typeContent("C7938 test content")
            .typeTitle("C7938 test title")
            .typeDescription("C7938 test description");;

        log.info("Step 4: Click the Create button");
        createContent
            .clickCreate();
        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details");

        log.info("Step 5 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "XML");

        log.info("Step 6: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7938 test content")
            .assertIsFileNameDisplayedOnPreviewPage("C7938TestName");

        log.info("Step 7: Login with testUser2 and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsDisplayed("C7938TestName");

        log.info("Delete the created File");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction("C7938TestName", ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C7931")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void sharedFilesCreateFolderFromTemplate()
    {
        log.info("Precondition: Login as user and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .assertBrowserPageTitleIs("Alfresco » Shared Files");

        log.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE);

        log.info("STEP 2: Select the template: 'Software Engineering Project'");
        sharedFilesPage.isTemplateDisplayed(folderTemplateName);
        sharedFilesPage.clickOnTemplate(folderTemplateName);
        createFolderFromTemplate.assertIsNameFieldValueEquals(folderTemplateName);

        log.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate
            .fillInDetails("TestFolderC7931", "Test Title C7931", "Test Description C7931")
            .clickSave();
        sharedFilesPage
            .assertIsFolderPresentInList("TestFolderC7931")
            .assertExplorerPanelDocuments("TestFolderC7931");

        log.info("Step 4: Login with testUser2 and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigateByMenuBar()
            .assertIsFolderPresentInList("TestFolderC7931");

        log.info("Delete the created Folder");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction("TestFolderC7931", ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();
    }

    @TestRail (id = "C7932")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "FlakyTests" })
    public void sharedFilesCreateDocumentFromTemplate()
    {
        log.info("Precondition: Login as admin user and create a file template.");
        FolderModel nodeTemplates = new FolderModel("Node Templates");
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        FileModel templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);
        getCmisApi().usingResource(nodeTemplates).createFile(templateFile);

        log.info("Precondition: Login as user and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage.navigateByMenuBar();

        log.info("STEP 1: Click 'Create' then 'Create document from template'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickCreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE);
        sharedFilesPage.isTemplateDisplayed(templateFile.getName());

        log.info("STEP 2: Select the template: 'Software Engineering Project'");
        sharedFilesPage.clickOnTemplate(templateFile.getName());
        sharedFilesPage.assertFileIsDisplayed(templateFile.getName());

        log.info("Step 3: Login with testUser2 and navigate to Shared Files page.");
        authenticateUsingLoginPage(testUser2);
        sharedFilesPage
            .navigateByMenuBar()
            .assertFileIsDisplayed(templateFile.getName());

        log.info("Delete the Template File'");
        getCmisApi().usingResource(templateFile).delete();

        log.info("Delete the created File");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(templateFile.getName(), ItemActions.DELETE_DOCUMENT);
        deleteDialog
            .confirmDeletion();

    }

    @TestRail (id = "C7934")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS },enabled = false)
    public void sharedFilesCreateGoogleDocsDocument() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
//        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
//        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
        Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        log.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Document.docx"), "The file created with Google Docs is not present");
        assertEquals(sharedFilesPage.getInfoBannerText("Untitled Document.docx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");

        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocName), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        log.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
//        cleanupAuthenticatedSession();
//        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocName), String.format("File [%s] is displayed", googleDocName));

//        cleanupAuthenticatedSession();
//        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled document" + googleDocName);
    }

    @TestRail (id = "C7935")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void sharedFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
//        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
//        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_SPREADSHEET);

        log.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertEquals(sharedFilesPage.getInfoBannerText("Untitled Spreadsheet.xlsx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");

        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocSpreadsheet), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        log.info("Step 5: Login with testUser2 and navigate to Shared Files page.");
//        cleanupAuthenticatedSession();
//        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
//        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocSpreadsheet), String.format("File [%s] is displayed", googleDocSpreadsheet));

//        cleanupAuthenticatedSession();
//        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled spreadsheet" + googleDocSpreadsheet);
    }

    @TestRail (id = "C7936")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS }, enabled = false)
    public void sharedFilesCreateGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to Shared Files page.");
//        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
//        Assert.assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        sharedFilesPage.clickCreateButton();
        sharedFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_PRESENTATION);

        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 2: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed("Untitled Presentation.pptx"), "The file created with Google Docs is not present");
        assertEquals(sharedFilesPage.getInfoBannerText("Untitled Presentation.pptx"), "This document is locked by you.", "Document appears to be locked");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 3: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");

        Assert.assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation));
        Assert.assertFalse(sharedFilesPage.isInfoBannerDisplayed(googleDocPresentation), "Locked label displayed");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());

        log.info("Step 4: Login with testUser2 and navigate to Shared Files page.");
//        cleanupAuthenticatedSession();
//        setupAuthenticatedSession(user2, password);
        sharedFilesPage.navigate();
//        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(googleDocPresentation), String.format("File [%s] is displayed", googleDocPresentation));

//        cleanupAuthenticatedSession();
//        contentService.deleteContentByPath(adminUser, adminPassword, "Shared/" + "Untitled presentation" + googleDocPresentation);
    }
}
