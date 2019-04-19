package org.alfresco.share.alfrescoContent.organizingContent;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 9/13/2016.
 */
public class LocateItemsAndFoldersTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    private final String testUser = String.format("user%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String folderName = String.format("locateFolder%s", RandomData.getRandomAlphanumeric());
    private final String docName = String.format("locateDoc%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createFolder(testUser, password, folderName, siteName);
        contentAction.setFolderAsFavorite(testUser, password, siteName, folderName);

        setupAuthenticatedSession(testUser, password);
    }

    @Bug(id = "MNT-17556")
    @TestRail(id = "C7516")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void locateFileDetailedView()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: From 'Options' dropdown choose 'Detailed View' option");
        documentLibraryPage.selectViewFromOptionsMenu("Detailed View");

        LOG.info("STEP 2: Choose a view option from left side explorer pane -> 'Documents' section");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.RecentlyAdded.title);
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.xpath("//div[contains(@class, 'message') and text()='Documents Added Recently']"));
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.RecentlyAdded.header, "Header=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " is displayed in Recently added documents list.");

        LOG.info("STEP3: Hover over the file name and click 'Locate file' link from 'More' menu");
        documentLibraryPage.clickDocumentLibraryItemAction(docName, "Locate File", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "User is redirected to location of the created document.");
        assertTrue(documentLibraryPage.isContentSelected(docName), docName + " is selected.");
        assertFalse(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
    }

    @Bug(id = "MNT-17556")
    @TestRail(id = "C7517")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT})
    public void locateFolderDetailedView()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("STEP 1: From 'Options' dropdown choose 'Detailed View' option");
        documentLibraryPage.selectViewFromOptionsMenu("Detailed View");

        LOG.info("STEP 2: Choose a view option from left side explorer pane -> 'Documents' section");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentLibraryPage.DocumentsFilters.Favorites.header,
                "My Favorites documents are displayed.");

        LOG.info("STEP3: Hover over the folder name and click 'Locate folder' link from 'More' menu");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Locate Folder", documentLibraryPage);
        ArrayList<String> breadcrumbExpected = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(documentLibraryPage.getBreadcrumbList(), breadcrumbExpected.toString(), "Breadcrumb=");
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "User is redirected to location of the created folder.");
        assertTrue(documentLibraryPage.isContentSelected(folderName), folderName + " is selected.");
        assertFalse(documentLibraryPage.isContentSelected(docName), docName + " is selected.");
    }
}
