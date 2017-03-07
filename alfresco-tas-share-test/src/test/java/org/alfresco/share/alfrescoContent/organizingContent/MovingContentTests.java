package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Laura.Capsa
 */
public class MovingContentTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    private final String userName = "profileUser-" + DataUtil.getUniqueIdentifier();
    private final String description = "Description-" + DataUtil.getUniqueIdentifier();
    private final String docContent = "content of the file.";

    @BeforeClass
    public void setupTest()
    {
        String lastName = "LastName";
        String firstName = "FirstName";
        userService.create(adminUser, adminPassword, userName, password, userName + domain, firstName, lastName);
    }

    @TestRail(id = "C7345")
    @Test
    public void moveFile()
    {
        String siteName = "Site-C7345-" + DataUtil.getUniqueIdentifier();
        String docName = "Doc-C7345-" + DataUtil.getUniqueIdentifier();
        String folderName = "Folder-C7345-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        content.createFolder(userName, password, folderName, siteName);

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
        copyMoveUnzipToDialog.clickButtton("Move");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog not displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName), docName + " displayed in Documents");

        LOG.info("STEP5: Open the folder created in preconditions");
        documentLibraryPage.clickOnFolderName(folderName);
        assertEquals(documentLibraryPage.getFilesList().toString(), "[" + docName + "]", "Displayed files in " + folderName);
    }

    @TestRail(id = "C7346")
    @Test
    public void moveFolder()
    {
        String siteName = "Site-C7346-" + DataUtil.getUniqueIdentifier();
        String docName = "TestDoc-C7346-" + DataUtil.getUniqueIdentifier();
        String folderName1 = "folderName1-" + DataUtil.getUniqueIdentifier();
        String folderName2 = "folderName2-" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, password, domain, siteName, description, Site.Visibility.PUBLIC);
        content.createDocument(userName, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        content.createFolder(userName, password, folderName1, siteName);
        content.createFolder(userName, password, folderName2, siteName);

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
        copyMoveUnzipToDialog.clickButtton("Move");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog is displayed.");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderName1), folderName1 + " displayed in Documents.");

        LOG.info("STEP5: Open the folder created in preconditions");
        documentLibraryPage.clickOnFolderName(folderName2);
        assertEquals(documentLibraryPage.getFoldersList().toString(), "[" + folderName1 + "]", "Displayed folders in " + folderName2);
    }
}