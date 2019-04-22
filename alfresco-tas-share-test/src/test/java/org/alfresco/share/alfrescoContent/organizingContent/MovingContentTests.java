package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class MovingContentTests extends ContextAwareWebTest {
    @Autowired private DocumentLibraryPage documentLibraryPage;
    @Autowired private CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    private final String userName = String.format("profileUser-%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("Description-%s", RandomData.getRandomAlphanumeric());
    private final String docContent = "content of the file.";

    @BeforeClass(alwaysRun = true)
    public void setupTest() {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "FirstName", "LastName");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }


    @TestRail(id = "C7345")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void moveFile() {
        String siteName = String.format("Site-C7345-%s", RandomData.getRandomAlphanumeric());
        String docName = String.format("Doc-C7345-%s", RandomData.getRandomAlphanumeric());
        String folderName = String.format("Folder-C7345-%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createFolder(userName, password, folderName, siteName);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        LOG.info("STEP1: Hover over the file.Click 'More...' menu. Click 'Move to...'");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Move to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + docName + " to...", "Displayed pop-up=");
        LOG.info("STEP2: Set the destination to 'All Sites'. Select 'site1'");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        copyMoveUnzipToDialog.clickSite(siteName);
        ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", folderName));
        assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(), "Path=");
        LOG.info("STEP3: Set the folder created in preconditions as path");
        copyMoveUnzipToDialog.clickPathFolder(folderName);
        LOG.info("STEP4: Click 'Move' button");
        copyMoveUnzipToDialog.clickMoveButton(documentLibraryPage);
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog not displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName), docName + " displayed in Documents");
        LOG.info("STEP5: Open the folder created in preconditions");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Displayed files in " + folderName);
        siteService.delete(adminUser, adminPassword,siteName);

    }

    @TestRail(id = "C7346")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void moveFolder() {
        String siteName = String.format("Site-C7346-%s", RandomData.getRandomAlphanumeric());
        String docName = String.format("TestDoc-C7346-%s", RandomData.getRandomAlphanumeric());
        String folderName1 = String.format("folderName1-%s", RandomData.getRandomAlphanumeric());
        String folderName2 = String.format("folderName2-%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createFolder(userName, password, folderName1, siteName);
        contentService.createFolder(userName, password, folderName2, siteName);
        setupAuthenticatedSession(userName, password);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        LOG.info("STEP1: Hover over folder. From 'More...' menu, click 'Move to...' option");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName1, "Move to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + folderName1 + " to...", "Displayed pop-up=");
        LOG.info("STEP2: Set the destination to 'All Sites'");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName), siteName + " displayed in 'Site' section");
        LOG.info("STEP3: Select a site");
        copyMoveUnzipToDialog.clickSite(siteName);
        ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", folderName1, folderName2));
        assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(), "Path");
        LOG.info("STEP4: Set the folder created in preconditions as path. Click 'Move' button.");
        copyMoveUnzipToDialog.clickPathFolder(folderName2);
        copyMoveUnzipToDialog.clickMoveButton(documentLibraryPage);
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog is displayed.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderName1), folderName1 + " displayed in Documents.");
        LOG.info("STEP5: Open the folder created in preconditions");
        documentLibraryPage.clickOnFolderName(folderName2);
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[" + folderName1 + "]", "Displayed folders in " + folderName2);
        siteService.delete(adminUser, adminPassword,siteName);

    }
}