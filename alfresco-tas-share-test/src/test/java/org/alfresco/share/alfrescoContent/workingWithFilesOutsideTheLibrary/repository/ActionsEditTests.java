package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewContentDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ActionsEditTests extends ContextAwareWebTest

{

    @Autowired private RepositoryPage repositoryPage;

    @Autowired
    SiteDashboardPage sitePage;

    @Autowired private DocumentDetailsPage detailsPage;

    @Autowired private EditPropertiesDialog editFilePropertiesDialog;

    @Autowired private SelectDialog selectDialog;

    @Autowired
    NewContentDialog newContentDialog;

    @Autowired private EditInAlfrescoPage editInAlfrescoPage;

    @Autowired private GoogleDocsCommon docsCommon;

    @SuppressWarnings("rawtypes")
    @Autowired private DocumentCommon documentCommon;

    private String uniqueIdentifier;
    private String folderName;
    private String editedFolderName;
    private String editedTitle;
    private String editedDescription;
    private String tagName;
    private String editedFileName;
    private String editFileUsr;
    private String editFolderUsr;
    private String editInAlfUsr;
    private String editFileInGDUsr;
    private String lName;
    private String fName;
    private String fileContent;
    private String fileName;
    private String editedContent;
    private String editFilePath;
    private String editFolderPath;
    private String editInAlfrescoPath;
    private String editFileInGDPath;

    @BeforeClass(alwaysRun = true)
    public void setupTest()

    {

        uniqueIdentifier = DataUtil.getUniqueIdentifier();
        folderName = "testFolder" + uniqueIdentifier;
        editedFolderName = "UpdatedFolderName" + uniqueIdentifier;
        editedTitle = "Updated Title" + uniqueIdentifier;
        editedDescription = "Updated Description";
        tagName = "tag" + uniqueIdentifier;
        editedFileName = "EditedFileName" + uniqueIdentifier;
        editFileUsr = "EditFileUsr" + uniqueIdentifier;
        editFolderUsr = "EditFolderUsr" + uniqueIdentifier;
        editInAlfUsr = "EditInAlfrescoUsr" + uniqueIdentifier;
        editFileInGDUsr = "EditInGoogleDocsUsr" + uniqueIdentifier;
        lName = "testLastName";
        fName = "testFirstName";
        fileContent = "Test Content";
        fileName = "testFile" + uniqueIdentifier;
        editedFileName = "editedFileName" + uniqueIdentifier;
        editFilePath = "User Homes/" + editFileUsr;
        editFolderPath = "User Homes/" + editFolderUsr;
        editInAlfrescoPath = "User Homes/" + editInAlfUsr;
        editFileInGDPath = "User Homes/" + editFileInGDUsr;
        editedContent = "edited test content";

        userService.create(adminUser, adminPassword, editFileUsr, password, editFileUsr + "@tests.com", lName, fName);
        userService.create(adminUser, adminPassword, editFolderUsr, password, editFolderUsr + "@tests.com", lName, fName);
        userService.create(adminUser, adminPassword, editInAlfUsr, password, editInAlfUsr + "@tests.com", lName, fName);
        userService.create(adminUser, adminPassword, editFileInGDUsr, password, editFileInGDUsr + "@tests.com", lName, fName);
        contentService.createDocumentInRepository(editFileUsr, password, editFilePath, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(editInAlfUsr, password, editInAlfrescoPath, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(editFileInGDUsr, password, editFileInGDPath, DocumentType.MSWORD, fileName, fileContent);
        contentService.createFolderInRepository(editFolderUsr, password, folderName, editFolderPath);
    }

    @TestRail(id = "C7737")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void repositoryEditFilesProperties()
    {
        logger.info("Precondition: Login to share and navigate to Repository->User Homes->Test User page ");
        setupAuthenticatedSession(editFileUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFileUsr);
        getBrowser().waitInSeconds(2);

        logger.info("Step 1: Hover over the test file and click 'Edit Properties' action");

        repositoryPage.mouseOverContentItem(fileName);
        repositoryPage.clickOnAction(fileName, "Edit Properties");
        getBrowser().waitInSeconds(2);

        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "'Edit Properties' dialog box is not correctly displayed");

        logger.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFileName);

        logger.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDescription);

        logger.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        logger.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        logger.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        assertTrue(repositoryPage.isContentNameDisplayed(editedFileName), editedFileName + " is displayed.");
        assertEquals(repositoryPage.getItemTitle(editedFileName), "(" + editedTitle + ")", editedFileName + " - document's title=");
        assertEquals(repositoryPage.getItemDescription(editedFileName), editedDescription, editedFileName + "- document's description=");
        assertEquals(repositoryPage.getTags(editedFileName), Collections.singletonList(tagName.toLowerCase()).toString(), editedFileName + "- document's tag=");
    }

    @TestRail(id = "C7745")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void repositoryEditFolderProperties()
    {
        logger.info("Precondition: Login to Share and navigate to Repository->User Homes->Test User page");
        setupAuthenticatedSession(editFolderUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFolderUsr);
        getBrowser().waitInSeconds(2);

        logger.info("Step 1: Hover over folder and click 'Edit Properties'");
        repositoryPage.mouseOverContentItem(folderName);
        repositoryPage.clickEditProperties(folderName);
        getBrowser().waitInSeconds(2);
        assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        logger.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFolderName);

        logger.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        logger.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDescription);

        logger.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        logger.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        logger.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        assertTrue(repositoryPage.isContentNameDisplayed(editedFolderName), editedFolderName + " is displayed.");
        assertEquals(repositoryPage.getItemTitle(editedFolderName), "(" + editedTitle + ")", editedFolderName + " - document's title=");
        assertEquals(repositoryPage.getItemDescription(editedFolderName), editedDescription, editedFolderName + "- document's description=");
        assertEquals(repositoryPage.getTags(editedFolderName), Collections.singletonList(tagName.toLowerCase()).toString(), editedFolderName + "- document's tag=");

        getBrowser().cleanUpAuthenticatedSession();
    }

    @TestRail(id = "C7767")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void repositoryEditFileInAlfresco()
    {
        logger.info("Precondition: Precondition: Login to Share and navigate to Repository->User Homes->Test User page");

        setupAuthenticatedSession(editInAlfUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editInAlfUsr);

        logger.info("Step1: Hover over the test file and click Edit in Alfresco option");
        repositoryPage.clickDocumentLibraryItemAction(fileName, "Edit in Alfresco", editInAlfrescoPage);

        logger.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(editedFileName, editedContent, editedTitle, editedDescription);

        logger.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");

        getBrowser().waitInSeconds(4);

        logger.info("Step4: Verify the new title for the document");
        Assert.assertTrue(docsCommon.isDocumentNameUpdated(editedFileName), "Document name is not updated");

        logger.info("Step5: Click on document title to open the document's details page");
        docsCommon.clickOnUpdatedName(editedFileName);
        getBrowser().waitInSeconds(7);

        logger.info("Step6: Verify the document's content");
        Assert.assertEquals(detailsPage.getContentText(), editedContent);

        logger.info("Step7: Verify Title and Description fields");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedTitle), "Updated title is not displayed");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedDescription), "Updated description is not displayed");

        getBrowser().cleanUpAuthenticatedSession();
    }

    @TestRail(id = "C7782")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void repositoryEditFilesInGoogleDocs() throws Exception
    {
        logger.info("Precondition: Precondition: Login to Share and navigate to Repository->User Homes->Test User page");
        setupAuthenticatedSession(editFileInGDUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFileInGDUsr);
        docsCommon.loginToGoogleDocs();

        logger.info("Step1: Hover over the test file and click Edit in Google Docs option");
        repositoryPage.mouseOverContentItem(fileName);
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
        docsCommon.checkInGoogleDoc(fileName);
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

        getBrowser().cleanUpAuthenticatedSession();
    }
}
