package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 9/2/2016.
 */
public class DeletingContentTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private DeleteDocumentOrFolderDialog deleteDialog;

    @Autowired
    Notification notification;

    private final String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String folderNameD = String.format("delFolder%s", RandomData.getRandomAlphanumeric());
    private final String subFolder = String.format("delSubfolder%s", RandomData.getRandomAlphanumeric());
    private final String folderNameC = String.format("cancelFolder%s", RandomData.getRandomAlphanumeric());
    private final String docName = String.format("testDoc%s", RandomData.getRandomAlphanumeric());
    private final String folderPathInRepository = "Sites/"+siteName+"/documentLibrary/";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(testUser, password);
    }

    @TestRail(id = "C9544")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void deleteDocument()
    {
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Document content");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file STEP2: Click 'More...' link. Click 'Delete Document' link");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Delete Document", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), docName));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
//        assertEquals(notification.getDisplayedNotification(), String.format(language.translate("documentLibrary.deletedNotification"), docName), "'testDoc' was deleted pop-up is displayed.");

        LOG.info("STEP4: Verify that the file was deleted");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName), "Documents item list is refreshed and is empty");
    }

    @TestRail(id = "C6968")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void deleteFolder()
    {
        contentService.createFolder(testUser, password, folderNameD, siteName);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolder, folderPathInRepository+folderNameD+"/");
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP1: Hover over the file. STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderNameD, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderNameD));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
//        assertEquals(notification.getDisplayedNotification(), String.format(language.translate("documentLibrary.deletedNotification"), folderNameD), "'delFolder' was deleted pop-up is displayed.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderNameD), "Documents item list is refreshed and is empty");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(folderNameD), "'DelFolder' is not visible in 'Library' section of the browsing pane.");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(subFolder), "'DelSubfolder' is not visible in 'Library' section of the browsing pane.");
    }

    @TestRail(id = "C6968")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void cancelDeletingFolder()
    {
        contentService.createFolder(testUser, password, folderNameC, siteName);
        contentService.createFolderInRepository(adminUser, adminPassword, subFolder, folderPathInRepository+folderNameC+"/");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickFolderFromExplorerPanel(folderNameC);

        LOG.info("STEP1: Hover 'DelSubfolder' name from the content item list. STEP2: Click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(subFolder, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), subFolder));

        LOG.info("STEP3: Click 'Cancel' button");
        deleteDialog.clickCancel();
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Arrays.asList("Documents", folderNameC));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Document Library breadcrumb");
        assertTrue(documentLibraryPage.isContentNameDisplayed(subFolder), "User returns to 'DelFolder' item list which contains 'DelSubfolder'.");
        assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(folderNameC), "'DelFolder' is still visible in 'Library' section of the browsing pane.");
        assertTrue(documentLibraryPage.getExplorerPanelDocuments().contains(subFolder), "'DelSubfolder' is still visible in 'Library' section of the browsing pane.");
    }
}
