package org.alfresco.share.alfrescoContent.organizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.EmptyTrashcanDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.profile.UserTrashcanPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class TrashcanTests extends ContextAwareWebTest
{
    private final String random = RandomData.getRandomAlphanumeric();
    private final String userName = "profileUser-" + random;
    private final String description = "Description-" + random;
    private final String fileContent = "file content.";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    //@Autowired
    private HeaderMenuBar headerMenuBar;
    //@Autowired
    private DeleteDialog deleteDialog;
    @Autowired
    private UserTrashcanPage userTrashcanPage;
    @Autowired
    private EmptyTrashcanDialog emptyTrashcanDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }

    @TestRail (id = "C10506")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void emptyTrashcan()
    {
        String siteName = "site-C10506-" + random;
        String fileName = "fileName-C10506-" + random;
        String folderName = "folder-C10506-" + random;
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Select all items and delete them by selecting 'Delete' option from 'Selected Items...' menu. Confirm deletion");
        headerMenuBar.clickSelectMenu();
        headerMenuBar.clickSelectOption("All");
        assertTrue(documentLibraryPage.isContentSelected(fileName), fileName + " is selected.");
        assertTrue(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 2, folderName + "\n" + fileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.clickDelete();
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[]", "Document Library folders=");

        LOG.info("STEP2: Open the user menu on the toolbar and click 'My Profile' then the 'Trashcan' tab");
        userTrashcanPage.navigate(userName);
        assertEquals(userTrashcanPage.getPageTitle(), "Alfresco » User Trashcan", "Displayed page=");

        LOG.info("STEP3: Click 'Empty' button");
        userTrashcanPage.clickEmptyButton();
        assertEquals(emptyTrashcanDialog.getDialogHeader(), language.translate("emptyTrashcan.title"), "Displayed dialog=");
        assertEquals(emptyTrashcanDialog.getMessage(), language.translate("emptyTrashcan.message"), "'Empty trashcan' dialog displayed message=");

        LOG.info("STEP4: Click 'OK' button");
        emptyTrashcanDialog.clickButton("OK");
        assertEquals(userTrashcanPage.getNoItemsMessage(), "No items exist", "Empty trash");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C7572")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void trashcanDeleteFile()
    {
        String siteName = "site-C7572-" + random;
        String fileName = "fileName-C7572-" + random;
        String folderName = "folder-C7572-" + random;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Select all items and delete them by selecting 'Delete' option from 'Selected Items...' menu. Confirm deletion");
        documentLibraryPage.clickCheckBox(fileName);
        assertTrue(documentLibraryPage.isContentSelected(fileName), fileName + " is selected.");
        assertFalse(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, fileName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.clickDelete();
        assertEquals(documentLibraryPage.getFilesList().toString(), "[]", "Document Library files=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), Collections.singletonList(folderName).toString(), "Document Library folders=");

        LOG.info("STEP2: Open the user menu on the toolbar and click 'My Profile' then the 'Trashcan' tab");
        userTrashcanPage.navigate(userName);
        assertEquals(userTrashcanPage.getPageTitle(), "Alfresco » User Trashcan", "Displayed page=");

        LOG.info("STEP3: Click 'Empty' button");
        userTrashcanPage.clickEmptyButton();
        assertEquals(emptyTrashcanDialog.getDialogHeader(), language.translate("emptyTrashcan.title"), "Displayed dialog=");
        assertEquals(emptyTrashcanDialog.getMessage(), language.translate("emptyTrashcan.message"), "'Empty trashcan' dialog displayed message=");

        LOG.info("STEP4: Click 'OK' button");
        emptyTrashcanDialog.clickButton("OK");
        assertEquals(userTrashcanPage.getNoItemsMessage(), "No items exist", "Empty trash");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C7573")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void trashcanDeleteFolder()
    {
        String siteName = "site-C7573-" + random;
        String fileName = "fileName-C7573-" + random;
        String folderName = "folder-C7573-" + random;

        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName, fileContent);
        contentService.createFolder(userName, password, folderName, siteName);

        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed=");

        LOG.info("STEP1: Select all items and delete them by selecting 'Delete' option from 'Selected Items...' menu. Confirm deletion");
        documentLibraryPage.clickCheckBox(folderName);
        assertFalse(documentLibraryPage.isContentSelected(fileName), fileName + " is selected.");
        assertTrue(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
        headerMenuBar.clickSelectedItemsMenu();
        headerMenuBar.clickSelectedItemsOption("Delete");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmMultipleDeleteDialog.message"), 1, folderName),
            "'Confirm multiple delete' dialog message=");
        deleteDialog.clickDelete();

        assertEquals(documentLibraryPage.getFilesList().toString(), Collections.singletonList(fileName).toString(), "Document Library files=");
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[]", "Document Library folders=");

        LOG.info("STEP2: Open the user menu on the toolbar and click 'My Profile' then the 'Trashcan' tab");
        userTrashcanPage.navigate(userName);
        assertEquals(userTrashcanPage.getPageTitle(), "Alfresco » User Trashcan", "Displayed page=");

        LOG.info("STEP3: Click 'Empty' button");
        userTrashcanPage.clickEmptyButton();
        assertEquals(emptyTrashcanDialog.getDialogHeader(), language.translate("emptyTrashcan.title"), "Displayed dialog=");
        assertEquals(emptyTrashcanDialog.getMessage(), language.translate("emptyTrashcan.message"), "'Empty trashcan' dialog displayed message=");

        LOG.info("STEP4: Click 'OK' button");
        emptyTrashcanDialog.clickButton("OK");
        assertEquals(userTrashcanPage.getNoItemsMessage(), "No items exist", "Empty trash");
        siteService.delete(adminUser, adminPassword, siteName);

    }
}