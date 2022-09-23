package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.cmis.CmisWrapper;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * @author Laura.Capsa
 */
public class EditTests extends BaseTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String user = "User" + RandomData.getRandomAlphanumeric();
    private final String user2 = "User2" + RandomData.getRandomAlphanumeric();
    private final String googleDocName = uniqueIdentifier + "googleDoc.docx";
    @Autowired
    public CmisWrapper cmisApi;
    //@Autowired
    private SharedFilesPage sharedFilesPage;
    //@Autowired
    private DocumentDetailsPage documentDetailsPage;
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private EditPropertiesDialog editFilePropertiesDialog;
    //@Autowired
    private EditInAlfrescoPage editInAlfrescoPage;
    @Autowired
    private GoogleDocsCommon googleDocsCommon;
    //@Autowired
    private SelectDialog selectDialog;
    //@Autowired
    private UploadContent uploadContent;
    private final String password = "password";
    private UserModel testUser1;
    private UserModel testUser2;
    private RepositoryPage repositoryPage;
    private MyFilesPage myFilesPage;
    private NewFolderDialog newFolderDialog;
    private final String sharedFolderName = "Shared";
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private final String editedFolderName = String.format("editedFolderName%s", RandomData.getRandomAlphanumeric());
    private final String editedTitle = "editedTitle";
    private final String editedContent = "C7762 edited content in Alfresco";
    private final String editedDescription = "edited description in Alfresco";
    private final String tagName = String.format("editTag_%s", RandomData.getRandomAlphanumeric());
    private String editedFileName = String.format("editedDocName%s", RandomData.getRandomAlphanumeric()) ;
    private final String folderTitle = "TestFOlderTitle";
    private final String folderDesc = "TestFolderDescription";
    private final String folderName ="TestFolder" + RandomData.getRandomAlphanumeric();


    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());

        sharedFilesPage = new SharedFilesPage(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
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
        deleteUsersIfNotNull(testUser2);

    }

    @TestRail (id = "C7953")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFileProperties()
    {

        log.info("Precondition: upload document in Shared folder from user2 ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

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
        log.info("verify that document details have been updated");
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

    @TestRail (id = "C7958")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFolderProperties() {
        log.info("Precondition: create folder in Shared folder from user2 ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderName)
            .typeTitle(folderTitle)
            .typeDescription(folderDesc)
            .clickSave();
        myFilesPage
            .isContentNameDisplayed(folderDesc);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        log.info(" Hover over a folder and click 'Edit Properties'");
        myFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(folderName, ItemActions.EDIT_PROPERTIES);
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

    @TestRail (id = "C7979")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFileInAlfresco()
    {
        log.info("Precondition: upload document in Shared folder from user2 ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);

        log.info("Login User1 with admin permissions and navigate to shared folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

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

    @TestRail (id = "C7994")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS },enabled = false)
    public void editFileInGoogleDocs() throws Exception
    {
        googleDocsCommon.loginToGoogleDocs();
        String googleDocPath = testDataFolder + googleDocName;
        String editedInGoogleDocsTitle = uniqueIdentifier + "editedTestFile.docx";
        String editedInGoogleDocsContent = "Edited";

        log.info("Preconditions: Login to Share/Google Docs and navigate to Shared Files page; upload a .docx file");
        //setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
//        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        uploadContent.uploadContent(googleDocPath);

        log.info("Step1: Hover over the test file and click Edit in Google Docs option");
        sharedFilesPage.selectItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);
        //getBrowser().waitInSeconds(5);
        googleDocsCommon.clickOkButtonOnTheAuthPopup();

        log.info("Step2,3: Provide edited input to Google Docs file and close Google Docs tab");
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedInGoogleDocsTitle, editedInGoogleDocsContent);

        log.info("Step4: Verify the file is locked and Google Drive icon is displayed");
        assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked icon displayed");
        assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked displayed");
        assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        log.info("Step5: Click Check In Google Doc™ and verify Version Information pop-up");
        googleDocsCommon.checkInGoogleDoc(googleDocName);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true, "Version information pop-up is displayed.");

        log.info("Step6: Click OK button on Version Information and verify the pop-up is closed");
        googleDocsCommon.clickOkButton();
        assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false, "Version information pop-up is displayed.");

        log.info("Step7: Verify document's title");
        assertTrue(sharedFilesPage.isContentNameDisplayed(editedInGoogleDocsTitle), "Name of the document is updated with " + editedInGoogleDocsTitle);

        log.info("Steps8: Click on the document title and verify it's preview");
        sharedFilesPage.clickOnFile(editedInGoogleDocsTitle);
        assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"),
            String.format("Document: %s has incorrect contents.", editedInGoogleDocsTitle));

        //cleanupAuthenticatedSession();
        //contentService.deleteContentByPath(adminUser, adminPassword, "/" + path + "/contents" + editedInGoogleDocsTitle);


    }

    @TestRail (id = "C13760")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void optionNotDisplayed() {
        log.info("Precondition: upload document in Shared folder from user2 ");
        authenticateUsingLoginPage(testUser2);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        uploadContent
            .uploadContent(testFilePath);
        repositoryPage
            .isContentNameDisplayed(testFile);

        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .isContentNameDisplayed(testFile);

        log.info("STEP1: Hover over the file");
        sharedFilesPage
            .mouseOverContentItem(testFile);
        documentLibraryPage
            .assertisMoreMenuNotDisplayed(testFile);
        List<String> expectedActions = Arrays
            .asList("Edit Properties", "Edit in Google Docs", "Edit in Alfresco Share","Edit Offline","Edit in Google Docs™");
        documentLibraryPage.assertActionsNoteAvailableForLibrary(testFile,expectedActions);


    }

}