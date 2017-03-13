package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class EditTests extends ContextAwareWebTest
{
    @Autowired
    private SharedFilesPage sharedFilesPage;

    @Autowired
    private HeaderMenuBar headerMenuBar;

    @Autowired
    private DocumentDetailsPage documentDetailsPage;

    @Autowired
    private EditPropertiesDialog editFilePropertiesDialog;

    @Autowired
    private EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    private GoogleDocsCommon googleDocsCommon;

    @Autowired
    private SelectDialog selectDialog;

    @Autowired
    private DeleteDialog deleteDialog;

    @Autowired
    private UploadContent uploadContent;

    private final String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    private final String user = "User" + uniqueIdentifier;
    private final String path = "Shared";
    private final String docName1 = "Doc-C7953-" + uniqueIdentifier;
    private final String updatedDocName1 = "UpdatedDocName-C7953-" + uniqueIdentifier;
    private final String docName2 = "Doc-C7979-" + uniqueIdentifier;
    private final String updatedDocName2 = "UpdatedDocName-C7979-" + uniqueIdentifier;
    private final String docName3 = "Doc-C13760-" + uniqueIdentifier;
    private final String folderName = "folder-" + uniqueIdentifier;
    private final String updatedFolderName = "UpdatedFolderName-C7958-" + uniqueIdentifier;
    private final String updatedTitle = "Updated Title" + uniqueIdentifier;
    private final String updatedDescription = "Updated Description";
    private final String tagName = "tag" + uniqueIdentifier;
    private final String googleDocName = uniqueIdentifier + "googleDoc.docx";
    private final String googleDocPath = testDataFolder + googleDocName;

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName1, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName2, "");
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName3, "");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
    }

    @TestRail(id = "C7953")
    @Test
    public void editFileProperties()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("Step 1: Hover over file and click 'Edit Properties' action");
        sharedFilesPage.clickDocumentLibraryItemAction(docName1, language.translate("documentLibrary.contentActions.editProperties"), editFilePropertiesDialog);
        getBrowser().waitInSeconds(2);
        assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(updatedDocName1);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(updatedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(updatedDescription);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        getBrowser().waitInSeconds(3);
        assertTrue(sharedFilesPage.isContentNameDisplayed(updatedDocName1), updatedDocName1 + " is displayed.");
        assertEquals(sharedFilesPage.getItemTitle(updatedDocName1), "(" + updatedTitle + ")", updatedDocName1 + " - document's title=");
        assertEquals(sharedFilesPage.getItemDescription(updatedDocName1), updatedDescription, updatedDocName1 + "- document's description=");
        assertEquals(sharedFilesPage.getTags(updatedDocName1), Collections.singletonList(tagName.toLowerCase()).toString(),
                updatedDocName1 + "- document's tag=");
    }

    @TestRail(id = "C7958")
    @Test
    public void editFolderProperties()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        getBrowser().waitInSeconds(3);
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("Step 1: Hover over file and click 'Edit Properties'");
        sharedFilesPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.editProperties"),
                editFilePropertiesDialog);
        getBrowser().waitInSeconds(2);
        assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(updatedFolderName);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(updatedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(updatedDescription);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        getBrowser().waitInSeconds(3);
        assertTrue(sharedFilesPage.isContentNameDisplayed(updatedFolderName), updatedFolderName + " is displayed.");
        assertEquals(sharedFilesPage.getItemTitle(updatedFolderName), "(" + updatedTitle + ")", updatedFolderName + " - document's title=");
        assertEquals(sharedFilesPage.getItemDescription(updatedFolderName), updatedDescription, updatedFolderName + "- document's description=");
        assertEquals(sharedFilesPage.getTags(updatedFolderName), Collections.singletonList(tagName.toLowerCase()).toString(),
                updatedFolderName + "- document's tag=");
    }

    @TestRail(id = "C7979")
    @Test()
    public void editFileInAlfresco()
    {
        String updatedContent = "Content updated C7979";

        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("Step1: Hover over the test file and click Edit in Alfresco option");
        sharedFilesPage.clickDocumentLibraryItemAction(docName2, language.translate("documentLibrary.contentActions.editInAlfresco"), editInAlfrescoPage);

        LOG.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(updatedDocName2, updatedContent, updatedTitle, updatedDescription);

        LOG.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");
        sharedFilesPage.renderedPage();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("Step4: Verify the new title for the document");
        assertTrue(sharedFilesPage.isContentNameDisplayed(updatedDocName2));

        LOG.info("Step5: Click on document title to open the document's details page");
        sharedFilesPage.clickOnFile(updatedDocName2);
        documentDetailsPage.renderedPage();
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Displayed page=");

        LOG.info("Step6: Verify the document's content");
        assertEquals(documentDetailsPage.getContentText(), updatedContent, updatedDocName2 + " 's content=");

        LOG.info("Step7: Verify Title and Description fields");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedTitle), "Updated title is not displayed");
        assertTrue(documentDetailsPage.isPropertyValueDisplayed(updatedDescription), "Updated description is not displayed");
    }

    @TestRail(id = "C7994")
    @Test()
    public void editFileInGoogleDocs() throws Exception
    {
        String editedInGoogleDocsTitle = uniqueIdentifier + "editedTestFile.docx";
        String editedInGoogleDocsContent = "Edited";

        LOG.info("Preconditions: Login to Share/Google Docs and navigate to Shared Files page; upload a .docx file");
        googleDocsCommon.loginToGoogleDocs();
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        sharedFilesPage.renderedPage();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        uploadContent.uploadContent(googleDocPath);
        getBrowser().waitInSeconds(5);

        LOG.info("Step1: Hover over the test file and click Edit in Google Docs option");
        sharedFilesPage.clickDocumentLibraryItemAction(googleDocName, "Edit in Google Docs™", googleDocsCommon);
        getBrowser().waitInSeconds(5);
        googleDocsCommon.clickTheOkButtonOnTheAuthorizeWithGoogleDocsPopup();

        LOG.info("Step2,3: Provide edited input to Google Docs file and close Google Docs tab");
        getBrowser().waitInSeconds(15);
        googleDocsCommon.switchToGoogleDocsWindowandAndEditContent(editedInGoogleDocsTitle, editedInGoogleDocsContent);

        LOG.info("Step4: Verify the file is locked and Google Drive icon is displayed");
        sharedFilesPage.renderedPage();
        assertTrue(googleDocsCommon.isLockedIconDisplayed(), "Locked icon displayed");
        assertTrue(googleDocsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked displayed");
        assertTrue(googleDocsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon displayed");

        LOG.info("Step5: Click Check In Google Doc™ and verify Version Information pop-up");
        googleDocsCommon.checkInGoogleDoc(googleDocName);
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), true, "Version information pop-up is displayed.");

        LOG.info("Step6: Click OK button on Version Information and verify the pop-up is closed");
        googleDocsCommon.clickOkButton();
        getBrowser().waitInSeconds(10);
        assertEquals(googleDocsCommon.isVersionInformationPopupDisplayed(), false, "Version information pop-up is displayed.");
        getBrowser().refresh();
        sharedFilesPage.renderedPage();

        LOG.info("Step7: Verify document's title");
        assertTrue(googleDocsCommon.isDocumentNameUpdated(editedInGoogleDocsTitle), "Name of the document is updated with " + editedInGoogleDocsTitle);

        LOG.info("Steps8: Click on the document title and verify it's preview");
        googleDocsCommon.clickOnUpdatedName(editedInGoogleDocsTitle);
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.getContentText().replaceAll("\\s+", "").contains("Edited"),
                String.format("Document: %s has incorrect contents.", editedInGoogleDocsTitle));

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C13760")
    @Test
    public void optionNotDisplayed()
    {
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");

        LOG.info("STEP1: Hover over the file");
        sharedFilesPage.mouseOverFileName(docName3);
        getBrowser().waitInSeconds(4);
        assertFalse(sharedFilesPage.isMoreMenuDisplayed(docName3), "More menu displayed.");
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(docName3, language.translate("documentLibrary.contentActions.editInGoogleDocs")),
                language.translate("documentLibrary.contentActions.editInGoogleDocs") + " option is displayed for " + docName3);
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(docName3, language.translate("documentLibrary.contentActions.editProperties")),
                language.translate("documentLibrary.contentActions.editProperties") + " option is displayed for " + docName3);
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(docName3, language.translate("documentLibrary.contentActions.editInAlfresco")),
                language.translate("documentLibrary.contentActions.editInAlfresco") + " option is displayed for " + docName3);

        cleanupAuthenticatedSession();
    }

    @AfterClass
    public void cleanUp()
    {
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + updatedDocName1);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + updatedDocName2);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + docName3);
        contentService.deleteContentByPath(adminUser, adminPassword, path + "/" + updatedFolderName);
    }
}