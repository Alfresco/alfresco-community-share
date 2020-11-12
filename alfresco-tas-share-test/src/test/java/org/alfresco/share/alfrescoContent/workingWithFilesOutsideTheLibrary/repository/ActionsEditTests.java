package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ActionsEditTests extends ContextAwareWebTest
{
    @Autowired
    NewFolderDialog newContentDialog;
    @Autowired
    private RepositoryPage repositoryPage;
    //@Autowired
    private SiteDashboardPage sitePage;
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

    @SuppressWarnings ("rawtypes")
    @Autowired
    private DocumentCommon documentCommon;

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

    @BeforeClass (alwaysRun = true)
    public void setupTest()

    {

        uniqueIdentifier = RandomData.getRandomAlphanumeric();
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

        userService.create(adminUser, adminPassword, editFileUsr, password, editFileUsr + domain, lName, fName);
        userService.create(adminUser, adminPassword, editFolderUsr, password, editFolderUsr + domain, lName, fName);
        userService.create(adminUser, adminPassword, editInAlfUsr, password, editInAlfUsr + domain, lName, fName);
        userService.create(adminUser, adminPassword, editFileInGDUsr, password, editFileInGDUsr + domain, lName, fName);
        contentService.createDocumentInRepository(editFileUsr, password, editFilePath, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(editInAlfUsr, password, editInAlfrescoPath, DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createDocumentInRepository(editFileInGDUsr, password, editFileInGDPath, DocumentType.MSWORD, fileName, fileContent);
        contentService.createFolderInRepository(editFolderUsr, password, folderName, editFolderPath);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, editFileUsr);
        userService.delete(adminUser, adminPassword, editFolderUsr);
        userService.delete(adminUser, adminPassword, editInAlfUsr);
        userService.delete(adminUser, adminPassword, editFileInGDUsr);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + editFileUsr);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + editFolderUsr);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + editInAlfUsr);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + editFileInGDUsr);

    }

    @TestRail (id = "C7737")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void repositoryEditFilesProperties()
    {
        LOG.info("Precondition: Login to share and navigate to Repository->User Homes->Test User page ");
        setupAuthenticatedSession(editFileUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFileUsr);

        LOG.info("Step 1: Hover over the test file and click 'Edit Properties' action");
        repositoryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_PROPERTIES);

        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "'Edit Properties' dialog box is not correctly displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFileName);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDescription);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        assertTrue(repositoryPage.isContentNameDisplayed(editedFileName), editedFileName + " is displayed.");
        assertEquals(repositoryPage.getItemTitle(editedFileName), "(" + editedTitle + ")", editedFileName + " - document's title=");
        assertEquals(repositoryPage.getItemDescription(editedFileName), editedDescription, editedFileName + "- document's description=");
        assertEquals(repositoryPage.getTags(editedFileName), Collections.singletonList(tagName.toLowerCase()).toString(), editedFileName + "- document's tag=");
    }

    @TestRail (id = "C7745")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void repositoryEditFolderProperties()
    {
        LOG.info("Precondition: Login to Share and navigate to Repository->User Homes->Test User page");
        setupAuthenticatedSession(editFolderUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFolderUsr);

        LOG.info("Step 1: Hover over folder and click 'Edit Properties'");
        repositoryPage.clickDocumentLibraryItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName(editedFolderName);

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle(editedTitle);

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription(editedDescription);

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag(tagName);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        editFilePropertiesDialog.isTagSelected(tagName.toLowerCase());

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();
        assertTrue(repositoryPage.isContentNameDisplayed(editedFolderName), editedFolderName + " is displayed.");
        assertEquals(repositoryPage.getItemTitle(editedFolderName), "(" + editedTitle + ")", editedFolderName + " - document's title=");
        assertEquals(repositoryPage.getItemDescription(editedFolderName), editedDescription, editedFolderName + "- document's description=");
        assertEquals(repositoryPage.getTags(editedFolderName), Collections.singletonList(tagName.toLowerCase()).toString(), editedFolderName + "- document's tag=");

        getBrowser().cleanUpAuthenticatedSession();
    }

    @TestRail (id = "C7767")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void repositoryEditFileInAlfresco()
    {
        LOG.info("Precondition: Precondition: Login to Share and navigate to Repository->User Homes->Test User page");

        setupAuthenticatedSession(editInAlfUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editInAlfUsr);

        LOG.info("Step1: Hover over the test file and click Edit in Alfresco option");
        repositoryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_IN_ALFRESCO);

        LOG.info("Step2: Edit the document's properties by sending new input");
        editInAlfrescoPage.sendDocumentDetailsFields(editedFileName, editedContent, editedTitle, editedDescription);

        LOG.info("Step3: Click Save button");
        editInAlfrescoPage.clickButton("Save");

        LOG.info("Step4: Verify the new title for the document");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(editedFileName), "Document name is not updated");

        LOG.info("Step5: Click on document title to open the document's details page");
        repositoryPage.clickOnFile(editedFileName);

        LOG.info("Step6: Verify the document's content");
        Assert.assertEquals(detailsPage.getContentText(), editedContent);

        LOG.info("Step7: Verify Title and Description fields");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedTitle), "Updated title is not displayed");
        Assert.assertTrue(documentCommon.isPropertyValueDisplayed(editedDescription), "Updated description is not displayed");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C7782")
    @Test (groups = { TestGroup.SANITY, TestGroup.GOOGLE_DOCS })
    public void repositoryEditFilesInGoogleDocs() throws Exception
    {
        LOG.info("Precondition: Precondition: Login to Share and navigate to Repository->User Homes->Test User page");
        setupAuthenticatedSession(editFileInGDUsr, password);
        repositoryPage.navigate();
        repositoryPage.clickFolderFromExplorerPanel("User Homes");
        repositoryPage.clickOnFolderName(editFileInGDUsr);
        docsCommon.loginToGoogleDocs();

        LOG.info("Step1: Hover over the test file and click Edit in Google Docs option");
        repositoryPage.clickDocumentLibraryItemAction(fileName, ItemActions.EDIT_IN_GOOGLE_DOCS);

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
        docsCommon.checkInGoogleDoc(fileName);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), true);

        LOG.info("Step7: Click OK button on Version Information and verify the pop-up is closed");
        docsCommon.clickOkButton();
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(docsCommon.isVersionInformationPopupDisplayed(), false);

        LOG.info("Step8: Verify the title for the document is changed");
        Assert.assertTrue(repositoryPage.isContentNameDisplayed(editedTitle), "Name of the document was not updated");

        LOG.info("Steps9, 10: Click on the document title and verify it's preview");
        repositoryPage.clickOnFile(editedTitle);
        Assert.assertTrue(detailsPage.getContentText().contains(editedContent));

        cleanupAuthenticatedSession();
    }
}
