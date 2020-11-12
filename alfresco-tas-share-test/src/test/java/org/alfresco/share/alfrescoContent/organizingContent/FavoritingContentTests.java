package org.alfresco.share.alfrescoContent.organizingContent;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.DocumentsFilters;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 9/1/2016.
 */
public class FavoritingContentTests extends ContextAwareWebTest
{
    private final String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private final String siteName1 = String.format("siteC7501%s", RandomData.getRandomAlphanumeric());
    private final String siteName2 = String.format("siteC7502%s", RandomData.getRandomAlphanumeric());
    private final String siteName3 = String.format("siteC7503%s", RandomData.getRandomAlphanumeric());
    private final String siteName4 = String.format("siteC7504%s", RandomData.getRandomAlphanumeric());
    private final String folderName = String.format("testFolder%s", RandomData.getRandomAlphanumeric());
    private final String docName = String.format("testDoc%s", RandomData.getRandomAlphanumeric());
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        siteService.create(testUser, password, domain, siteName1, siteName1, SiteService.Visibility.PUBLIC);
        siteService.create(testUser, password, domain, siteName2, siteName2, SiteService.Visibility.PUBLIC);
        siteService.create(testUser, password, domain, siteName3, siteName3, SiteService.Visibility.PUBLIC);
        siteService.create(testUser, password, domain, siteName4, siteName4, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName1, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentService.createFolder(testUser, password, folderName, siteName2);
        contentService.createDocument(testUser, password, siteName3, DocumentType.TEXT_PLAIN, docName, "Document content");
        contentAction.setFileAsFavorite(testUser, password, siteName3, docName);
        contentService.createFolder(testUser, password, folderName, siteName4);
        contentAction.setFolderAsFavorite(testUser, password, siteName4, folderName);

        setupAuthenticatedSession(testUser, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);
        siteService.delete(adminUser, adminPassword, siteName3);
        siteService.delete(adminUser, adminPassword, siteName4);
    }

    @TestRail (id = "C7501")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void favoriteFile()
    {
        documentLibraryPage.navigate(siteName1);
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        LOG.info("STEP 1: Check the favorite items list.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");

        LOG.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is displayed in Documents list!");

        LOG.info("STEP 3: Hover over the file 'Favorite' link");
        assertEquals(documentLibraryPage.getFavoriteTooltip(docName), "Add document to favorites", "The text 'Add document to favorites' is displayed");

        LOG.info("STEP 4: Click on the 'Favorite' link");
        documentLibraryPage.clickFavoriteLink(docName);
        getBrowser().waitInSeconds(4);
        assertTrue(documentLibraryPage.isFileFavorite(docName), "The gray star and text 'Favorite' are replaced by a golden star");

        LOG.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        getBrowser().refresh();
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is displayed in My favorites list!");
    }

    @TestRail (id = "C7502")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void favoriteFolder()
    {
        documentLibraryPage.navigate(siteName2);
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        LOG.info("STEP 1: Check the favorite items list.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");

        LOG.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");

        LOG.info("STEP 3: Hover over the file 'Favorite' link");
        assertEquals(documentLibraryPage.getFavoriteTooltip(folderName), "Add folder to favorites", "The text 'Add folder to favorites' is displayed");

        LOG.info("STEP 4: Click on the 'Favorite' link");
        documentLibraryPage.clickFavoriteLink(folderName);
        assertTrue(documentLibraryPage.isFileFavorite(folderName), "The gray star and text 'Favorite' are replaced by a golden star");

        LOG.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is displayed in My favorites list!");
    }

    @TestRail (id = "C7503")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeFavoriteForFile()
    {
        documentLibraryPage.navigate(siteName3);
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        LOG.info("STEP 1: Check the favorite items list.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "Document is displayed in My favorites list!");

        LOG.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");

        LOG.info("STEP 3: Hover over the file golden star");
        assertEquals(documentLibraryPage.getFavoriteTooltip(docName), "Remove document from favorites", "The star is replaced by a X button and the text 'Remove document from favorites' is displayed");

        LOG.info("STEP 4: Click the golden star");
        documentLibraryPage.clickFavoriteLink(docName);
        assertFalse(documentLibraryPage.isFileFavorite(docName), "The golden star is replaced by a gray star and the text 'Favorite'");

        LOG.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");
    }

    @TestRail (id = "C7504")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void removeFavoriteForFolder()
    {
        documentLibraryPage.navigate(siteName4);
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        LOG.info("STEP 1: Check the favorite items list.");
        assertTrue(documentLibraryPage.getFoldersList().contains(folderName), "Folder is displayed in My favorites list!");

        LOG.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");

        LOG.info("STEP 3: Hover over the file golden star");
        assertEquals(documentLibraryPage.getFavoriteTooltip(folderName), "Remove folder from favorites", "The star is replaced by a X button and the text 'Remove folder from favorites' is displayed");

        LOG.info("STEP 4: Click the golden star");
        documentLibraryPage.clickFavoriteLink(folderName);
        assertFalse(documentLibraryPage.isFileFavorite(folderName), "The golden star is replaced by a gray star and the text 'Favorite'");

        LOG.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");
    }
}
