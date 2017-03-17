package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class DeleteTests extends ContextAwareWebTest
{
    @Autowired private SharedFilesPage sharedFilesPage;

    @Autowired
    HeaderMenuBar headerMenuBar;

    @Autowired private DeleteDialog deleteDialog;

    private final String uniqueIdentifier = DataUtil.getUniqueIdentifier();
    private final String user = "User" + uniqueIdentifier;
    private final String docName = "DocC8014-" + uniqueIdentifier;
    private final String fileContent = "Doc content";
    private final String folderName = "Folder-C8015-" + uniqueIdentifier;
    private final String docName2 = "DocC13759-" + uniqueIdentifier;
    private final String folderName2 = "FolderC13759-" + uniqueIdentifier;
    private final String path = "Shared/";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName, fileContent);
        contentService.createFolderInRepository(adminUser, adminPassword, folderName, path);
        contentService.createDocumentInRepository(adminUser, adminPassword, path, CMISUtil.DocumentType.TEXT_PLAIN, docName2, "");
        contentService.createFolderInRepository(adminUser, adminPassword, folderName2, path);
    }

    @TestRail(id = "C8014")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void deleteDocument()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        getBrowser().waitInSeconds(4);

        LOG.info("STEP1: Hover over the file you want to delete");
        sharedFilesPage.mouseOverContentItem(docName);

        LOG.info("STEP2: Click 'More' menu -> \"Delete Document\"");
        sharedFilesPage.clickDocumentLibraryItemAction(docName, language.translate("documentLibrary.contentActions.deleteDocument"), deleteDialog);
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), docName), "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is displayed.");

        LOG.info("STEP3: Press \"Delete\"");
        deleteDialog.clickDelete();
        assertFalse(sharedFilesPage.isContentNameDisplayed(docName), docName + " is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C8015")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void deleteFolder()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        getBrowser().waitInSeconds(4);

        LOG.info("STEP1: Hover over the file you want to delete and press \"More\"");
        sharedFilesPage.mouseOverContentItem(folderName);
        getBrowser().waitInSeconds(3);
        assertTrue(sharedFilesPage.isMoreMenuDisplayed(folderName), "'More' menu displayed for " + folderName);

        LOG.info("STEP2: Press \"Delete Folder\"");
        sharedFilesPage.clickDocumentLibraryItemAction(folderName, language.translate("documentLibrary.contentActions.deleteFolder"), deleteDialog);

        assertEquals(deleteDialog.getMessage(), String.format(language.translate("documentLibrary.deleteDialogMessage"), folderName),
                "Delete dialog message= ");
        assertTrue(deleteDialog.isDeleteButtonDisplayed(), "'Delete' button is displayed.");
        assertTrue(deleteDialog.isCancelButtonDisplayed(), "'Cancel' button is displayed.");

        LOG.info("STEP3: Press \"Delete\"");
        deleteDialog.clickDelete();
        assertFalse(sharedFilesPage.isContentNameDisplayed(folderName), folderName + " is displayed.");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C13759")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void optionNotDisplayed()
    {
        setupAuthenticatedSession(user, password);
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        getBrowser().waitInSeconds(4);

        LOG.info("STEP1: Hover over " + docName2);
        sharedFilesPage.mouseOverFileName(docName2);
        getBrowser().waitInSeconds(3);
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(docName2, language.translate("documentLibrary.contentActions.deleteDocument")),
                language.translate("documentLibrary.contentActions.deleteDocument") + " option is displayed for " + docName2);

        LOG.info("STEP1: Hover over " + folderName2);
        sharedFilesPage.mouseOverFolder(folderName2);
        getBrowser().waitInSeconds(3);
        assertFalse(sharedFilesPage.isActionAvailableForLibraryItem(folderName2, language.translate("documentLibrary.contentActions.deleteDocument")),
                language.translate("documentLibrary.contentActions.deleteDocument") + " option is displayed for " + folderName2);
    }

    @AfterClass
    public void cleanUp()
    {
        contentService.deleteContentByPath(adminUser, adminPassword, path + docName2);
        contentService.deleteContentByPath(adminUser, adminPassword, path + folderName2);
    }
}