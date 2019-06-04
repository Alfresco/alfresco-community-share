package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Alex Argint
 */
public class EditFilesPropertiesTest extends ContextAwareWebTest
{

    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private EditPropertiesDialog editFilePropertiesDialog;

    @Autowired
    private SelectDialog selectDialog;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String docName;
    private String folderName;

    private void setup(String id)
    {
        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + RandomData.getRandomAlphanumeric();
        uniqueIdentifier = uniqueIdentifier.toLowerCase();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        docName = "PlainText" + uniqueIdentifier;
        folderName = "Folder" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(userName, password, folderName, siteName);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
    }

    @TestRail (id = "C7005")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFileProperties()
    {
        LOG.info("Starting test C7005");
        setup("C7005");

        LOG.info("Step 1: Hover over a file and click 'Edit Properties'");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Edit Properties", editFilePropertiesDialog);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName("DocEditName");

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle("DocEditTitle");

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription("DocEditDescription");

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag("editTag" + uniqueIdentifier);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();

        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("DocEditName"), "Edited document name is not found");
        Assert.assertEquals(documentLibraryPage.getItemTitle("DocEditName"), "(DocEditTitle)", "The title of edited document is not correct");
        Assert.assertEquals(documentLibraryPage.getItemDescription("DocEditName"), "DocEditDescription", "The description of edited document is not correct");
        Assert.assertEquals(documentLibraryPage.getTags("DocEditName"), "[edittag" + uniqueIdentifier + "]", "The tag of the edited document is not correct");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C7013")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void editFolderProperties()
    {
        LOG.info("Starting test C7005");
        setup("C7005");

        LOG.info("Step 1: Hover over a folder and click 'Edit Properties'");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editFilePropertiesDialog);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not sdisplayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editFilePropertiesDialog.setName("FolderEditName");

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editFilePropertiesDialog.setTitle("FolderEditTitle");

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editFilePropertiesDialog.setDescription("FolderEditDescription");

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editFilePropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag("editTag" + uniqueIdentifier);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();

        LOG.info("Step 7: Click 'Save' And verify that document details have been updated");
        editFilePropertiesDialog.clickSave();

        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("FolderEditName"), "Edited document name is not found");
        Assert.assertEquals(documentLibraryPage.getItemTitle("FolderEditName"), "(FolderEditTitle)", "The title of edited document is not correct");
        Assert.assertEquals(documentLibraryPage.getItemDescription("FolderEditName"), "FolderEditDescription",
            "The description of edited document is not correct");
        Assert.assertEquals(documentLibraryPage.getTags("FolderEditName"), "[edittag" + uniqueIdentifier + "]",
            "The tag of the edited document is not correct");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}
