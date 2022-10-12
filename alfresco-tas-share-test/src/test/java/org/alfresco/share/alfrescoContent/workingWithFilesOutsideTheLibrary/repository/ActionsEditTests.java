package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsLogIn;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.alfresco.common.Utils.testDataFolder;

@Slf4j
public class ActionsEditTests extends BaseTest
{
    //@Autowired
    private RepositoryPage repositoryPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    //@Autowired
    private EditPropertiesDialog editFilePropertiesDialog;
    // @Autowired
    private SelectDialog selectDialog;
    private GoogleDocsLogIn googleDocLogIn;
    //@Autowired
    private EditInAlfrescoPage editInAlfrescoPage;
    private String editedFileName = String.format("editedDocName%s", RandomData.getRandomAlphanumeric()) ;
    private final String editedFolderName = String.format("editedFolderName%s", RandomData.getRandomAlphanumeric());
    private final String editedTitle = "editedTitle";
    private final String editedContent = "C7762 edited content in Alfresco";
    private final String editedDescription = "edited description in Alfresco";
    private final String tagName = String.format("editTag_%s", RandomData.getRandomAlphanumeric());
    private final String user = String.format("C8156User%s", RandomData.getRandomAlphanumeric());
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String sharedFolderName = "Shared";
    private MyFilesPage myFilesPage;
    //@Autowired
    private NewFolderDialog newFolderDialog;
    private UploadContent uploadContent;
    private final String password = "password";
    private UserModel testUser1;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create Folder and File in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        googleDocLogIn = new GoogleDocsLogIn(webDriver);
        selectDialog = new SelectDialog(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        repositoryPage = new RepositoryPage(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);

    }
    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
    }


    @TestRail (id = "C7737")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void repositoryEditFilesProperties()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);
        log.info("Step 1: Hover over the test file and click 'Edit Properties' action");
        repositoryPage
            .selectItemActionFormFirstThreeAvailableOptions(testFile, ItemActions.EDIT_PROPERTIES);
        editFilePropertiesDialog
            .assertVerifyEditPropertiesElementsAreDisplayed();

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFileName);

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDescription);

        log.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        log.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        log.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        log.info("Step 8: verify that document details have been updated");
        repositoryPage
            .assertIsContantNameDisplayed(editedFileName);
        repositoryPage
            .assertItemTitleEquals(editedFileName,editedTitle);
        repositoryPage
            .assertItemDescriptionEquals(editedFileName,editedDescription);
        repositoryPage
            .assertTagNamesDisplayed(editedFileName,Collections.singletonList(tagName.toLowerCase()).toString(),editedFileName);
        repositoryPage
            .select_ItemsAction(editedFileName, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C7745")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void repositoryEditFolderProperties()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared and create a folder ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName("TestFolder")
            .typeTitle("TestTitle")
            .typeDescription("TestDescription")
            .clickSave();
        myFilesPage
            .isContentNameDisplayed("TestFolder");

        log.info(" Hover over a folder and click 'Edit Properties'");
        myFilesPage
            .selectItemActionFormFirstThreeAvailableOptions("TestFolder", ItemActions.EDIT_PROPERTIES);
        editFilePropertiesDialog
            .assertVerifyEditPropertiesElementsAreDisplayed();
        log.info(" In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName(editedFolderName);
        log.info(" In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(editedTitle);
        log.info(" In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(editedDescription);
        log.info(" Click the 'Select' button in the tags section");
        editFilePropertiesDialog
            .clickSelectTags();
        log.info(" Type a tag name and click create");
        selectDialog
            .typeTag(tagName)
            .clickCreateNewIcon()
            .clickOk();

        log.info(" Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog
            .clickSave();
        log.info(" Verify the new title for the document");
        repositoryPage
            .assertIsContantNameDisplayed(editedFolderName);

        repositoryPage
            .assertItemTitleEquals(editedFolderName,editedTitle);
        repositoryPage
            .assertItemDescriptionEquals(editedFolderName,editedDescription);
        repositoryPage
            .assertTagNamesDisplayed(editedFolderName,Collections.singletonList(tagName.toLowerCase()).toString(),editedFolderName);

        repositoryPage
            .select_ItemsAction(editedFolderName, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C7767")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void repositoryEditFileInAlfresco()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);


        log.info("Hover over the test file and click Edit in Alfresco option");
        myFilesPage
            .select_ItemAction(testFile, ItemActions.EDIT_IN_ALFRESCO);

        log.info(" Edit the document's properties by sending new input");
        editInAlfrescoPage
            .typeName(editedFileName)
            .typeContent(editedContent)
            .typeTitle(editedTitle)
            .typeDescription(editedDescription);

        log.info(" Click Save button");
        editInAlfrescoPage
            .clickSaveButton();

        log.info(" Verify the new title for the document");
        myFilesPage
            .assertIsContantNameDisplayed(editedFileName);

        log.info(" Click on document title to open the document's details page");
        myFilesPage
            .clickOnFile(editedFileName);

        log.info(" Verify the document's content");
        documentDetailsPage
            .assertFileContentEquals(editedContent);

        log.info(" Verify Title and Description fields");
        documentDetailsPage
            .assertContentTittleEquals(editedTitle)
            .assert_ContentDescriptionEquals(editedDescription);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .select_ItemsAction(editedFileName, ItemActions.DELETE_DOCUMENT)
            .clickOnDeleteButtonOnDeletePrompt();

    }

    @TestRail (id = "C7782")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void repositoryEditFilesInGoogleDocs()
    {
        log.info("Precondition: Login to share and navigate to Repository->Shared ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);
        log.info(" LogIn to googledoc Authentication");
        googleDocLogIn
            .loginToGoogleDocs();
        log.info("Hover over the test file and click Edit in Google Docs option");
        myFilesPage
            .select_ItemAction("testFile", ItemActions.EDIT_IN_GOOGLE_DOCS);
        googleDocLogIn
            .clickOkButton();
        log.info(" Navigate to Google Doc tab and edit title , Content ");
        googleDocLogIn
            .switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);
        log.info(" Verify the file is locked and Google Drive icon is displayed");
        googleDocLogIn
            .assertisLockedIconDisplayed();
        googleDocLogIn
            .assertisLockedDocumentMessageDisplayed();
        googleDocLogIn
            .assertisGoogleDriveIconDisplayed();
        log.info("Click on document and  Verify the document title");
        googleDocLogIn
            .checkInGoogleDoc(testFile);
        googleDocLogIn
            .clickOkButton();
        myFilesPage
            .isContentNameDisplayed(editedTitle);
        log.info("Verify the document's content");
        myFilesPage
            .clickOnFile(editedTitle);
        documentDetailsPage
            .assertFileContentContains(editedContent);
        log.info("Delete file ");
        documentDetailsPage
            .clickOpenedFloder();
        repositoryPage
            .select_ItemsAction(editedTitle, ItemActions.DELETE_FOLDER)
            .clickOnDeleteButtonOnDeletePrompt();

    }
}
