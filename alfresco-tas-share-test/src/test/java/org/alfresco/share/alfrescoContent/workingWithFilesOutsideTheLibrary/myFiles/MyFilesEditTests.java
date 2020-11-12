package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.ItemActions;
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
 * @author Razvan.Dorobantu
 */
public class MyFilesEditTests extends ContextAwareWebTest
{
    private final String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String googleDocName = RandomData.getRandomAlphanumeric() + "googleDoc";
    private final String docNameC8186 = RandomData.getRandomAlphanumeric() + "testDocC8186";
    private final String docNameC8212 = RandomData.getRandomAlphanumeric() + "testDocC8212";
    private final String editedFolderName = String.format("editedFolderName%s", RandomData.getRandomAlphanumeric());
    private final String editedTitle = "editedTitle";
    private final String editedContent = "edited content in Alfresco";
    private final String editedDescription = "edited description in Alfresco";
    ;
    private final String tag = String.format("editTag%s", RandomData.getRandomAlphanumeric());
    private final String folderName = String.format("Folder%s", RandomData.getRandomAlphanumeric());
    private final String myFilesPath = "User Homes/" + user;
    //@Autowired
    private MyFilesPage myFilesPage;
    @Autowired
    private DocumentDetailsPage detailsPage;
    @Autowired
    private EditPropertiesDialog editFilePropertiesDialog;
    @Autowired
    private SelectDialog selectDialog;
    //@Autowired
    private EditInAlfrescoPage editInAlfrescoPage;
    @Autowired
    private GoogleDocsCommon docsCommon;
    @Autowired
    private DocumentCommon documentCommon;
    private String editedDocName;

    @BeforeClass (alwaysRun = true)
    public void createUser()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
    }


    @TestRail (id = "C8186")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void myFilesEditFileProperties()
    {
        editedDocName = String.format("editedDocName%s", RandomData.getRandomAlphanumeric());
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, docNameC8186, "some content");
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(docNameC8186), String.format("Document %s is not present", docNameC8186));

        LOG.info("Step 1: Hover over a file and click 'Edit Properties'");
        myFilesPage.clickDocumentLibraryItemAction(docNameC8186, ItemActions.EDIT_PROPERTIES);
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

    @TestRail (id = "C8191")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesEditFolderProperties()
    {
        contentService.createFolderInRepository(user, password, folderName, myFilesPath);
        LOG.info("Precondition: Login as user, navigate to My Files page and create a folder.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        assertTrue(myFilesPage.isContentNameDisplayed(folderName), folderName + " displayed in My Files documents list.");

        LOG.info("Step 1: Hover over a folder and click 'Edit Properties'");
        myFilesPage.clickDocumentLibraryItemAction(folderName, ItemActions.EDIT_PROPERTIES);
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
        Assert.assertEquals(myFilesPage.getItemDescription(editedFolderName), editedFolderName, "The description of edited document is not correct");
        Assert.assertEquals(myFilesPage.getTags(editedFolderName), "[" + tag.toLowerCase() + "]", "The tag of the edited document is not correct");
    }

    @TestRail (id = "C8212")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void myFilesEditFileInAlfresco()
    {
        editedDocName = String.format("editedDocName%s", RandomData.getRandomAlphanumeric());
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.TEXT_PLAIN, docNameC8212, "some content");
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage.navigate();
        Assert.assertEquals(myFilesPage.getPageTitle(), "Alfresco » My Files");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(docNameC8212), String.format("Document %s is not present", docNameC8212));

        LOG.info("Step1: Hover over the test file and click Edit in Alfresco option");
        myFilesPage.clickDocumentLibraryItemAction(docNameC8212, ItemActions.EDIT_IN_ALFRESCO);

        LOG.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(editedDocName, editedContent, editedTitle, editedDescription);

        LOG.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");

        LOG.info("Step4: Verify the new title for the document");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedDocName), "Document name is not updated");

        LOG.info("Step5: Click on document title to open the document's details page");
        myFilesPage.clickOnFile(editedDocName);

        LOG.info("Step6: Verify the document's content");
        Assert.assertEquals(detailsPage.getContentText(), editedContent);

        LOG.info("Step7: Verify Title and Description fields");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedTitle), "Updated title is not displayed");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedDescription), "Updated description is not displayed");
    }

    @TestRail (id = "C8227")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void myFilesEditFilesInGoogleDocs() throws Exception
    {
        contentService.createDocumentInRepository(user, password, myFilesPath, CMISUtil.DocumentType.MSWORD, googleDocName, "some content");
        LOG.info("Precondition: Login as user, navigate to My Files page and create a plain text file.");
        myFilesPage.navigate();
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(googleDocName), String.format("Document %s is not present", googleDocName));

        LOG.info("Step1: Hover over the test file and click Edit in Google Docs option");
        docsCommon.loginToGoogleDocs();
        myFilesPage.clickDocumentLibraryItemAction(googleDocName, ItemActions.EDIT_IN_GOOGLE_DOCS);

        LOG.info("Step2: Click OK on the Authorize with Google Docs pop-up message");
        docsCommon.clickOkButton();

        LOG.info("Step3,4: Provide edited input to Google Docs file and close Google Docs tab");
        docsCommon.confirmFormatUpgrade();
        getBrowser().waitInSeconds(7);
        docsCommon.switchToGoogleDocsWindowandAndEditContent(editedTitle, editedContent);

        LOG.info("Step5: Verify the file is locked and Google Drive icon is displayed");
        Assert.assertTrue(docsCommon.isLockedIconDisplayed(), "Locked Icon is not displayed");
        Assert.assertTrue(docsCommon.isLockedDocumentMessageDisplayed(), "Message about the file being locked is not displayed");
        Assert.assertTrue(docsCommon.isGoogleDriveIconDisplayed(), "Google Drive icon is not displayed");

        LOG.info("Step6: Click Check In Google Doc™ and verify Version Information pop-up is displayed");
        docsCommon.checkInGoogleDoc(googleDocName);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), true);

        LOG.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        docsCommon.clickOkButton();
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), false);

        LOG.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(myFilesPage.isContentNameDisplayed(editedTitle), "Name of the document was not updated");

        LOG.info("Steps9, 10: Click on the document title and verify it's preview");
        myFilesPage.clickOnFile(editedTitle);
        Assert.assertTrue(detailsPage.getContentText().contains(editedContent));
    }
}
