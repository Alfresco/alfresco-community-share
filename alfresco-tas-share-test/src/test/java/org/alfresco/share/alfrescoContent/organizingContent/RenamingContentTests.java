package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class RenamingContentTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    String firstName = "FirstName";
    String lastName = "LastName";
    String description = "Description-" + DataUtil.getUniqueIdentifier();
    String docContent = "content of the file.";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C7419")
    @Test
    public void renameFileByEditIcon()
    {
        String siteName = "Site-C7419-" + DataUtil.getUniqueIdentifier();
        String docName = "Doc-C7419-" + DataUtil.getUniqueIdentifier();
        String newFileName = "newFileNameC7419";
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the file name");
        documentLibraryPage.mouseOverFileName(docName);
        assertTrue(documentLibraryPage.isRenameIconDisplayed(), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        ArrayList<String> expectedButtons = new ArrayList<>(Arrays.asList("Save", "Cancel"));
        assertEquals(documentLibraryPage.verifyButtonsFromRenameContent(expectedButtons), expectedButtons.toString(), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Save\" button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName), docName + " name updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7420")
    @Test
    public void renameFolderByEditIcon()
    {
        String siteName = "Site-C7420-" + DataUtil.getUniqueIdentifier();
        String newFolderName = "new folder name C7420";
        String folderName = "Folder-C7420-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createFolder(userName, password, folderName, siteName);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the folder name");
        documentLibraryPage.mouseOverContentItem(folderName);
        assertTrue(documentLibraryPage.isRenameIconDisplayed(), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        ArrayList<String> expectedButtons = new ArrayList<>(Arrays.asList("Save", "Cancel"));
        assertEquals(documentLibraryPage.verifyButtonsFromRenameContent(expectedButtons), expectedButtons.toString(), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Save\" button");
        documentLibraryPage.typeContentName(newFolderName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), newFolderName + " name updated to: " + newFolderName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C7431")
    @Test()
    public void cancelRenamingContent()
    {
        String siteName = "Site-C7431-" + DataUtil.getUniqueIdentifier();
        String docName = "Doc-C7431-" + DataUtil.getUniqueIdentifier();
        String newFileName = "new file name C7431";
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, DataUtil.PASSWORD, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("STEP1: Hover over the file name");
        documentLibraryPage.mouseOverFileName(docName);
        assertTrue(documentLibraryPage.isRenameIconDisplayed(), "'Rename' icon is displayed.");

        LOG.info("STEP2: Click on \"Rename\" icon");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        ArrayList<String> expectedButtons = new ArrayList<>(Arrays.asList("Save", "Cancel"));
        assertEquals(documentLibraryPage.verifyButtonsFromRenameContent(expectedButtons), expectedButtons.toString(), "Rename content buttons");

        LOG.info("STEP3: Fill in input field with a new name and click \"Cancel\" button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Cancel");
        assertFalse(documentLibraryPage.isContentNameDisplayed(newFileName), docName + " name isn't updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");

        cleanupAuthenticatedSession();
    }
}