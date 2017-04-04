package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class RenamingContentTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    private final String userName = String.format("profileUser-%s", DataUtil.getUniqueIdentifier());
    private final String docContent = "content of the file.";
    private final String siteName = String.format("Site-%s", DataUtil.getUniqueIdentifier());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        siteService.create(userName, password, domain, siteName, "Description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C7419")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void renameFileByEditIcon()
    {
        String docName = String.format("Doc-C7419-%s", DataUtil.getUniqueIdentifier());
        String newFileName = "newFileNameC7419";
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the file name");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(docName), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Save\" button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName), docName + " name updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");
    }

    @TestRail(id = "C7420")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void renameFolderByEditIcon()
    {
        String newFolderName = "new folder name C7420";
        String folderName = String.format("Folder-C7420-%s", DataUtil.getUniqueIdentifier());
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the folder name");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(folderName), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Save\" button");
        documentLibraryPage.typeContentName(newFolderName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), newFolderName + " name updated to: " + newFolderName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");
    }

    @TestRail(id = "C7431")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void cancelRenamingContent()
    {
        String docName = String.format("Doc-C7431-%s", DataUtil.getUniqueIdentifier());
        String newFileName = "new file name C7431";
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the file name");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(docName), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Cancel\" button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Cancel");
        assertFalse(documentLibraryPage.isContentNameDisplayed(newFileName), docName + " name isn't updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");
    }
}