package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.site.DocumentLibraryPage.CreateMenuOption;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.FileType;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.FolderModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Razvan.Dorobantu
 */
@Slf4j
public class MyFilesCreateContentTests extends BaseTest
{
    private final String folderTemplateName = "Software Engineering Project";
    private final String templateContent = "template content";
    private final String title = "googleDoc title";
    private final String googleDocName = "googleDoc title.docx";
    private final String googleDocSpreadsheet = "googleDoc title.xlsx";
    private final String googleDocPresentation = "googleDoc title.pptx";
    private final String docContent = "googleDoccontent";
    private MyFilesPage myFilesPage;
    private DocumentDetailsPage documentDetailsPage;
    private CreateContentPage createContent;
    private NewFolderDialog createFolderFromTemplate;

    private GoogleDocsCommon googleDocs;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();


    @BeforeMethod(alwaysRun = true)
    public void createPrecondition()
    {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        myFilesPage = new MyFilesPage(webDriver);
        createContent = new CreateContentPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        createFolderFromTemplate = new NewFolderDialog(webDriver);
        googleDocs = new GoogleDocsCommon();
        createFolderFromTemplate = new NewFolderDialog(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7650")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void myFilesCreatePlainTextFile()
    {
        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("Step 1: Click Create... button");
        myFilesPage
            .areCreateOptionsAvailable();
        myFilesPage
            .click_CreateButton();

       log.info("Step 2: Click \"Plain Text...\" option.");
        myFilesPage
            .clickCreateContentOption(CreateMenuOption.PLAIN_TEXT);
        createContent
            .assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName("C7650 test name")
            .typeContent("C7650 test content")
            .typeTitle("C7650 test title")
            .typeDescription("C7650 test description");

        log.info("Step 4: Click the Create button");
        createContent
            .clickCreate();
        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details");

        log.info("Step 5 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "Plain Text");

        log.info("Step 6: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7650 test content")
            .assertIsFileNameDisplayedOnPreviewPage("C7650 test name");
    }

    @TestRail (id = "C7696")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateHTMLFile()
    {
        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("Step 1: Click Create... button");
        myFilesPage
            .areCreateOptionsAvailable();
        myFilesPage
            .click_CreateButton();

        log.info("Step 2: Click \"HTML...\" option.");
        myFilesPage
            .clickCreateContentOption(CreateMenuOption.HTML);
        createContent
            .assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName("C7696 test name")
            .sendInputForHTMLContent("C7696 test content")
            .typeTitle("C7696 test title")
            .typeDescription("C7696 test description");

        log.info("Step 4: Click the Create button");
        createContent
            .clickCreate();
        documentDetailsPage
            .assertPageTitleEquals("Alfresco » Document Details");

        log.info("Step 5 : Verify the mimetype for the created file.");
        documentDetailsPage
            .assertPropertyValueEquals("Mimetype", "HTML");

        log.info("Step 6: Verify the document's preview");
        documentDetailsPage
            .assertFileContentEquals("C7696 test content")
            .assertIsFileNameDisplayedOnPreviewPage("C7696 test name");
    }

    @TestRail (id = "C7697")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateXMLFile()
    {
        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("Step 1: Click Create... button");
        myFilesPage
            .areCreateOptionsAvailable();
        myFilesPage
            .click_CreateButton();

        log.info("Step 2: Click \"XML...\" option.");
        myFilesPage
            .clickCreateContentOption(CreateMenuOption.XML);
        createContent
            .assertBrowserPageTitleIs("Alfresco » Create Content");

        log.info("Step 3: Fill in the name, content, title and description fields");
        createContent
            .typeName("C7697 test name")
            .typeContent("C7697 test content")
            .typeTitle("C7697 test title")
            .typeDescription("C7697 test description");

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
            .assertFileContentEquals("C7697 test content")
            .assertIsFileNameDisplayedOnPreviewPage("C7697 test name");
    }

    @TestRail (id = "C7653")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFolderFromTemplate()
    {
        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage.navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("STEP 1: Click 'Create' then 'Create folder from template'.");
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(CreateMenuOption.CREATE_FOLDER_FROM_TEMPLATE)
            .isTemplateDisplayed("Software Engineering Project");

        log.info("STEP 2: Select the template: 'Software Engineering Project'");
        myFilesPage
            .clickOnTemplate(folderTemplateName);
        createFolderFromTemplate
            .assertIsNameFieldValueEquals(folderTemplateName);

        log.info("STEP 3: Insert data into input fields and save.");
        createFolderFromTemplate
            .fillInDetails("Test Folder", "Test Title", "Test Description")
            .clickSave();

        log.info("STEP 4: Verify the Folder is Present in MyFiles Page.");
        myFilesPage
            .assertIsFolderPresentInList("Test Folder");
    }

    @TestRail (id = "C12858")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesCreateFileFromTemplate()
    {
        log.info("Precondition: To Create a Template File");
        FolderModel nodeTemplates = new FolderModel("Node Templates");
        nodeTemplates.setCmisLocation("/Data Dictionary/Node Templates");
        FileModel templateFile = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, templateContent);
        getCmisApi().usingResource(nodeTemplates).createFile(templateFile);

        log.info("Precondition: Login as user and navigate to My Files page.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");

        log.info("STEP 1: Click 'Create' then 'Create file from template'.");
        myFilesPage
            .click_CreateButton()
            .click_CreateFromTemplateOption(CreateMenuOption.CREATE_DOC_FROM_TEMPLATE)
            .isTemplateDisplayed(templateFile.getName());;

        log.info("STEP 2: Select the template: 'templateFile'");
        myFilesPage
            .create_FileFromTemplate(templateFile);

        log.info("STEP 3: Verify the Folder is Present in MyFiles Page.");
        myFilesPage
            .assertIsContantNameDisplayed(templateFile.getName());

        log.info("Delete the Template File'");
        getCmisApi().usingResource(templateFile).delete();
    }

    @TestRail (id = "C7693")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsDocument() throws Exception {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
//        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Document'.");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_DOCUMENT);
 //       Assert.assertTrue(googleDocs.isAuthorizeWithGoogleDocsDisplayed(), "Authorize with Google Docs popup is not displayed");

        log.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleDocsWindowandAndEditContent(title, docContent);
//        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Document"), "The file created with Google Docs is not present");
//        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
//        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Document");
//        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocName), "Document is unlocked");
//        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
//        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName));
    }

    @TestRail (id = "C7694")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsSpreadsheet() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
//        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Spreadsheet'");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_SPREADSHEET);

        log.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 3: Edit the document in the Google Docs tab.");
        googleDocs.switchToGoogleSheetsWindowandAndEditContent(title, docContent);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Spreadsheet.xlsx"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Spreadsheet");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocSpreadsheet), "Document is unlocked");
        Assert.assertFalse(googleDocs.isGoogleDriveIconDisplayed());
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocSpreadsheet));
    }

    @TestRail (id = "C7695")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesCreateGoogleDocsPresentation() throws Exception
    {
        googleDocs.loginToGoogleDocs();
        log.info("Precondition: Login as user, authorize google docs and navigate to My Files page.");
        myFilesPage.navigate();
//        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");

        log.info("Step 1: Click 'Create' button and select the type 'Google Docs Presentation'");
        myFilesPage.clickCreateButton();
        myFilesPage.clickGoogleDocsOption(CreateMenuOption.GOOGLE_DOCS_PRESENTATION);

        log.info("Step 2: Click Ok button on the Authorize ");
        googleDocs.clickOkButtonOnTheAuthPopup();

        log.info("Step 3: Edit the document in the Google Docs tab ");
        googleDocs.switchToGooglePresentationsAndEditContent(title);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed("Untitled Presentation"), "The file created with Google Docs is not present");
        Assert.assertTrue(googleDocs.isLockedDocumentMessageDisplayed(), "Locked label is not displayed");
        Assert.assertTrue(googleDocs.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step 4: Click Check in Google Doc button for the created document and verify it's not locked anymore.");
        googleDocs.checkInGoogleDoc("Untitled Presentation");
        Assert.assertFalse(myFilesPage.isInfoBannerDisplayed(googleDocPresentation), "Document is unlocked");
        Assert.assertEquals(googleDocs.isGoogleDriveIconDisplayed(), false);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocPresentation));
    }
}

