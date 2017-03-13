package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Razvan.Dorobantu
 */
public class MyFilesEditTests extends ContextAwareWebTest
{
    @Autowired
    MyFilesPage myFilesPage;

    @Autowired
    SiteDashboardPage sitePage;
    
    @Autowired
    DocumentDetailsPage detailsPage;

    @Autowired
    EditPropertiesDialog editFilePropertiesDialog;

    @Autowired
    SelectDialog selectDialog;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired
    EditInAlfrescoPage editInAlfrescoPage;

    @Autowired
    GoogleDocsCommon docsCommon;

    @Autowired
    DocumentCommon documentCommon;

    @Autowired
    private UploadContent uploadContent;

    String googleDocName = DataUtil.getUniqueIdentifier() + "googleDoc.docx";
    String googleDocPath = testDataFolder + googleDocName;
    String docName = DataUtil.getUniqueIdentifier() + "testDoc.txt";
    String docNamePath = testDataFolder + docName;
    String editedDocName = "editedDocName" + DataUtil.getUniqueIdentifier();
    String editedFolderName = "editedFolderName" + DataUtil.getUniqueIdentifier();
    String editedTitle = "editedTitle";
    String editedContent = "edited content in Alfresco";
    String editedDescription = "edited description in Alfresco";
    String tag = "editTag" + DataUtil.getUniqueIdentifier();
    String folderName = "Folder" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C8186")
    @Test
    public void myFilesEditFileProperties()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(docNamePath);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(docName), String.format("Document %s is not present", docName));

        LOG.info("Step 1: Hover over a file and click 'Edit Properties'");
        myFilesPage.mouseOverFileName(docName);
        myFilesPage.clickMoreMenu(docName);
        myFilesPage.clickEditProperties(docName);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedDocName);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDocName);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tag);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();

        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedDocName), "Edited document name is not found");
        Assert.assertEquals(myFilesPage.getItemTitle(editedDocName), "(" + editedTitle + ")", "The title of edited document is not correct");
        Assert.assertEquals(myFilesPage.getItemDescription(editedDocName), editedDocName, "The description of edited document is not correct");
        Assert.assertEquals(myFilesPage.getTags(editedDocName), "[" + tag.toLowerCase() + "]", "The tag of the edited document is not correct");
    }

    @TestRail(id = "C8191")
    @Test
    public void myFilesEditFolderProperties()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        myFilesPage.clickCreateButton();
        myFilesPage.clickFolderLink();
        newContentDialog.fillInNameField(folderName);
        newContentDialog.clickSaveButton();
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Step 1: Hover over a folder and click 'Edit Properties'");
        myFilesPage.mouseOverContentItem(folderName);
        myFilesPage.clickEditProperties(folderName);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not sdisplayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFolderName);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedFolderName);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tag);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();

        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedFolderName), "Edited document name is not found");
        Assert.assertEquals(myFilesPage.getItemTitle(editedFolderName), "(" + editedTitle + ")", "The title of edited document is not correct");
        Assert.assertEquals(myFilesPage.getItemDescription(editedFolderName), editedFolderName,
                "The description of edited document is not correct");
        Assert.assertEquals(myFilesPage.getTags(editedFolderName), "[" + tag.toLowerCase() + "]",
                "The tag of the edited document is not correct");
    }

    @TestRail(id = "C8212")
    @Test()
    public void myFilesEditFileInAlfresco()
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(docNamePath);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(docName), String.format("Document %s is not present", docName));

        logger.info("Step1: Hover over the test file and click Edit in Alfresco option");
        myFilesPage.clickCheckBox(docName);
        myFilesPage.clickDocumentLibraryItemAction(docName, language.translate("documentLibrary.contentActions.editInAlfresco"),
                editInAlfrescoPage);
        getBrowser().waitInSeconds(2);

        logger.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(editedDocName, editedContent, editedTitle, editedDescription);

        logger.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");
        getBrowser().waitInSeconds(4);

        logger.info("Step4: Verify the new title for the document");
        Assert.assertTrue(docsCommon.isDocumentNameUpdated(editedDocName), "Document name is not updated");

        logger.info("Step5: Click on document title to open the document's details page");
        docsCommon.clickOnUpdatedName(editedDocName);

        logger.info("Step6: Verify the document's content");
        Assert.assertEquals(detailsPage.getContentText(), editedContent);

        logger.info("Step7: Verify Title and Description fields");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedTitle), "Updated title is not displayed");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedDescription), "Updated description is not displayed");
    }

    @TestRail(id = "C8227")
    @Test()
    public void myFilesEditFilesInGoogleDocs() throws Exception
    {
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        String user = "user" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        setupAuthenticatedSession(user, password);
        sitePage.clickMyFilesLink();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        uploadContent.uploadContent(googleDocPath);
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName), String.format("Document %s is not present", googleDocName));

        logger.info("Step1: Hover over the test file and click Edit in Google Docs option");
        docsCommon.loginToGoogleDocs();
        myFilesPage.mouseOverFileName(googleDocName);
        myFilesPage.clickMoreMenu(googleDocName);
        docsCommon.editInGoogleDocs();

        logger.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        docsCommon.clickOkButton();

        logger.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        docsCommon.confirmFormatUpgrade();
        getBrowser().waitInSeconds(7);
        docsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);

        logger.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        Assert.assertTrue(docsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(docsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(docsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        logger.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        docsCommon.checkInGoogleDoc(googleDocName);
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), true);

        logger.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        docsCommon.clickOkButton();
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), false);

        logger.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(docsCommon.isDocumentNameUpdated(editedTitle), "Name of the document was not updated");

        logger.info("Steps9, 10: Click on the document title and verify it's preview");
        docsCommon.clickOnUpdatedName(editedTitle);
        Assert.assertTrue(detailsPage.getContentText().contains(editedContent));
    }
}
