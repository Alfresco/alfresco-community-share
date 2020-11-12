package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LibraryViewOptionsFilmStripViewTests extends ContextAwareWebTest
{
    private final String user = String.format("C2247User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C2247SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String siteName = String.format("C2247Site%s", RandomData.getRandomAlphanumeric());
    private final String docName = "testFile1";
    private final String docContent = "C2247 content";
    private final String docName1 = "C2247 test file 1";
    private final String folderName = "folderName";
    private final String videoFile = "Video2.WMV";
    private final String picture = "Lighthouse.jpg";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.createFolder(user, password, folderName, siteName);
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, docName, docContent);
        contentService.createDocumentInFolder(user, password, siteName, folderName, DocumentType.TEXT_PLAIN, docName1, "Document content");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + videoFile);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + picture);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2246")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void filmstripViewPresence()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Expand the Options menu and check that the Filmstrip View is present");

        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu is not displayed");
        documentLibraryPage.clickOptionsButton();
        assertTrue(documentLibraryPage.isviewOptionDisplayed("Filmstrip View"), "Film Strip view is not displayed");
    }

    @TestRail (id = "C2247")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
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
        getBrowser().waitInSeconds(5);
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(docName), docName, docName + " is displayed in filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(folderName), folderName, folderName + " is displayed in filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(videoFile), videoFile,
            "videoFile is not displayed in the filmstrip view");
        assertEquals(documentLibraryPage.getLabelDisplayedInFilmstripView(picture), picture,
            "picture is not displayed in the filmstrip view");

        assertTrue(documentLibraryPage.isDownArrowPointerDisplayed(), "Down arrow pointer is displayed");
        assertTrue(documentLibraryPage.isRightArrowPointerDisplayed(), "Right arrow pointer is displayed");
        documentLibraryPage.clickTheRightArrowPointer();
        assertTrue(documentLibraryPage.isLeftArrowPointerDisplayed(), "Left arrow pointer is displayed");
    }
}