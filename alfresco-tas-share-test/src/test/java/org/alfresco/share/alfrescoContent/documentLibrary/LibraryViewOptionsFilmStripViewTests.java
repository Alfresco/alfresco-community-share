package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentService;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LibraryViewOptionsFilmStripViewTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    ContentService contentService;

    private String user = "C2247User" + DataUtil.getUniqueIdentifier();
    private String description = "C2247SiteDescription" + DataUtil.getUniqueIdentifier();
    private String siteName = "C2247Site" + DataUtil.getUniqueIdentifier();
    private String docName = "testFile1";
    private String docContent = "C2247 content";
    private String docName1 = "C2247 test file 1";
    private String folderName = "folderName";
    private String testDataFolder = srcRoot + "testdata" + File.separator;
    private String videoFile = "Wildlife";
    private String picture = "Lighthouse";

    @BeforeClass
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName1, "Document content");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + videoFile + ".wmv");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + picture + ".jpg");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C2246")
    @Test
    public void filmstripViewPresence()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Expand the Options menu and check that the Filmstrip View is present");

        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        assertTrue(documentLibraryPage.isviewOptionDisplayed("Filmstrip View"), "Film Strip view is not displayed");
    }

    @TestRail(id = "C2247")
    @Test
    public void filmstripViewDisplayingItems()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Check the Filmstrip view action presence in the Options menu.");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        assertTrue(documentLibraryPage.isviewOptionDisplayed("Filmstrip View"), "Filmstrip view is displayed");

        LOG.info("Step 2: Click on Filmstrip view action.");
        documentLibraryPage.clickOptionsButton();
        documentLibraryPage.selectViewFromOptionsMenu("Filmstrip View");
        browser.waitInSeconds(5);
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(docName), docName, docName + " is displayed in filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(folderName), folderName, folderName + " is displayed in filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(videoFile + ".wmv"), videoFile + ".wmv",
                "videoFile is not displayed in the filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(picture + ".jpg"), picture + ".jpg",
                "picture is not displayed in the filmstrip view");

        assertTrue(documentLibraryPage.isDownArrowPointerDisplayed(), "Down arrow pointer is displayed");
        assertTrue(documentLibraryPage.isRightArrowPointerDisplayed(), "Right arrow pointer is displayed");
        documentLibraryPage.clickTheRightArrowPointer();
        assertTrue(documentLibraryPage.isLeftArrowPointerDisplayed(), "Left arrow pointer is displayed");
    }
}