package org.alfresco.share.alfrescoContent.workingWithFilesAndFolders.editingFiles;

import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.testng.Assert;
import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.Test;

/**
 * Created by Alex Argint
 */
public class EditFilesPropertiesTest extends ContextAwareWebTest
{

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    EditPropertiesDialog editFilePropertiesDialog;

    @Autowired
    SelectDialog selectDialog;

    private String uniqueIdentifier;
    private String userName;
    private String siteName;
    private String description;
    private String docName;
    private String folderName;

    public void setup(String id)
    {
        LOG.info("Preconditions for test " + id);
        uniqueIdentifier = "-" + id + "-" + DataUtil.getUniqueIdentifier();
        uniqueIdentifier = uniqueIdentifier.toLowerCase();
        siteName = "siteName" + uniqueIdentifier;
        userName = "User" + uniqueIdentifier;
        description = "description" + uniqueIdentifier;
        docName = "PlainText" + uniqueIdentifier;
        folderName = "Folder" + uniqueIdentifier;

        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createFolder(userName, DataUtil.PASSWORD, folderName, siteName);
        content.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, description);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
    }

    @TestRail(id = "C7005")
    @Test
    public void editFileProperties()
    {
        LOG.info("Starting test C7005");
        setup("C7005");

        LOG.info("Step 1: Hover over a file and click 'Edit Properties'");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickMoreMenu(docName);
        documentLibraryPage.clickEditProperties(docName);
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
    }

    @TestRail(id = "C7013")
    @Test
    public void editFolderProperties()
    {
        LOG.info("Starting test C7005");
        setup("C7005");

        LOG.info("Step 1: Hover over a folder and click 'Edit Properties'");
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickEditProperties(folderName);
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
    }
}
