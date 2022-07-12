package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContentPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.alfresco.common.Utils.testDataFolder;
@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class MyFilesEditTests extends BaseTest
{
    private final String testFile = RandomData.getRandomAlphanumeric() + "testFile.txt";
    private final String testFilePath = testDataFolder + testFile;
    private String editedDocName = String.format("editedDocName%s", RandomData.getRandomAlphanumeric()) ;
    private final String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc";
    private final String editedFolderName = String.format("editedFolderName%s", RandomData.getRandomAlphanumeric());
    private final String editedTitle = "editedTitle";
    private final String editedContent = "edited content in Alfresco";
    private final String editedDescription = "edited description in Alfresco";
    private final String tag = String.format("editTag_%s", RandomData.getRandomAlphanumeric());
    private DocumentDetailsPage documentDetailsPage;
    private CreateContentPage createContent;
    private NewFolderDialog createFolderFromTemplate;
    private MyFilesPage myFilesPage;
    private EditPropertiesDialog editFilePropertiesDialog;
    private SelectDialog selectDialog;
    private EditInAlfrescoPage editInAlfrescoPage;
    private UploadContent uploadContent;
    @Autowired
    private GoogleDocsCommon docsCommon;
    private NewFolderDialog newFolderDialog;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    @BeforeMethod(alwaysRun = true)
    public void createUser()
    {
        log.info("PreCondition: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        myFilesPage = new MyFilesPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);
        selectDialog = new SelectDialog(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);
        editInAlfrescoPage = new EditInAlfrescoPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }


    @TestRail (id = "C8186")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditFileProperties()
    {
        log.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .isContentNameDisplayed(testFile);

        log.info("Step 1: Hover over a file and click 'Edit Properties'");
        myFilesPage
            .selectItemActionFormFirstThreeAvailableOptions(testFile, ItemActions.EDIT_PROPERTIES);
        editFilePropertiesDialog
            .assertVerifyEditPropertiesElementsAreDisplayed();

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName("editedDocName");

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(editedTitle);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(editedDocName);

        log.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog
            .clickSelectTags();

        log.info("Step 6: Type a tag name and click create");
        selectDialog
            .typeTag(tag)
            .clickCreateNewIcon()
            .clickOk();

        log.info("Step 7: Click Save");
        editFilePropertiesDialog
            .clickSave();

        log.info("Step 8: verify that document details have been updated");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedDocName), "Edited document name is not found");
        Assert.assertEquals(myFilesPage.getItemTitle(editedDocName), "(" + editedTitle + ")", "The title of edited document is not correct");
        Assert.assertEquals(myFilesPage.getItemDescription(editedDocName), editedDocName, "The description of edited document is not correct");
        Assert.assertEquals(myFilesPage.getTags(editedDocName), "[" + tag.toLowerCase() + "]", "The tag of the edited document is not correct");
    }

    @TestRail (id = "C8191")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditFolderProperties() throws Exception {
        log.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
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

        log.info("Step 1: Hover over a folder and click 'Edit Properties'");
        myFilesPage
            .selectItemActionFormFirstThreeAvailableOptions("TestFolder", ItemActions.EDIT_PROPERTIES);
        editFilePropertiesDialog
            .assertVerifyEditPropertiesElementsAreDisplayed();

        log.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog
            .setName(editedFolderName);

        log.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog
            .setTitle(editedTitle);

        log.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog
            .setDescription(editedFolderName);

        log.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog
            .clickSelectTags();

        log.info("Step 6: Type a tag name and click create");
        selectDialog
            .typeTag(tag)
            .clickCreateNewIcon()
            .clickOk();

        log.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog
            .clickSave();


        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedFolderName), "Edited document name is not found");
        Assert.assertEquals(myFilesPage.getItemTitle(editedFolderName), "(" + editedTitle + ")", "The title of edited document is not correct");
        Assert.assertEquals(myFilesPage.getItemDescription(editedFolderName), editedFolderName, "The description of edited document is not correct");
        Assert.assertEquals(myFilesPage.getTags(editedFolderName), "[" + tag.toLowerCase() + "]", "The tag of the edited document is not correct");
    }

    @TestRail (id = "C8212")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditFileInAlfresco() {
        log.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        uploadContent
            .uploadContent(testFilePath);
        myFilesPage
            .isContentNameDisplayed(testFile);

        log.info("Step1: Hover over the test file and click Edit in Alfresco option");
        myFilesPage
            .select_ItemAction("testFile", ItemActions.EDIT_IN_ALFRESCO);

        log.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage
            .typeName(editedDocName)
            .typeContent(editedContent)
            .typeTitle(editedTitle)
            .typeDescription(editedDescription);

        log.info("Step3: Click Save button");
        editInAlfrescoPage
            .clickSaveButton();

        log.info("Step4: Verify the new title for the document");
        myFilesPage
            .assertIsContantNameDisplayed(editedDocName);

        log.info("Step5: Click on document title to open the document's details page");
        myFilesPage
            .clickOnFile(editedDocName);

        log.info("Step6: Verify the document's content");
        documentDetailsPage
            .assertFileContentEquals(editedContent);

        log.info("Step7: Verify Title and Description fields");
        documentDetailsPage
            .assertContentTittleEquals(editedTitle)
            .assert_ContentDescriptionEquals(editedDescription);
    }

    @TestRail (id = "C8227")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesEditFilesInGoogleDocs() throws Exception
    {
//        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.MSWORD, googleDocName, "some content");
        log.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage.navigate();
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName), String.format("Document %s is not present", googleDocName));

        log.info("Step1: Hover over the test file and click Edit in Google Docs option");
        docsCommon.loginToGoogleDocs();
        myFilesPage.selectItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);

        log.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        docsCommon.clickOkButton();

        log.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        docsCommon.confirmFormatUpgrade();
//        getBrowser().waitInSeconds(7);
        docsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);

        log.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        Assert.assertTrue(docsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(docsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(docsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        log.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        docsCommon.checkInGoogleDoc(googleDocName);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), true);

        log.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        docsCommon.clickOkButton();
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), false);

        log.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedTitle), "Name of the document was not updated");

        log.info("Steps9, 10: Click on the document title and verify it's preview");
        myFilesPage.clickOnFile(editedTitle);
//        Assert.assertTrue(detailsPage.getContentText().contains(editedContent));
    }
}
