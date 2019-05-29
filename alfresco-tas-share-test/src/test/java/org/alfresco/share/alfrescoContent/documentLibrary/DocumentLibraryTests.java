package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DocumentLibraryTests extends ContextAwareWebTest
{
    @Autowired
    private DocumentLibraryPage documentLibraryPage;

    @Autowired
    private HeaderMenuBar headerMenuBar;

    @Autowired
    private DocumentsFilters filters;

    @Autowired
    private SiteDashboardPage sitePage;

    private final String random = RandomData.getRandomAlphanumeric();
    private final String user = "User" + random;
    private final String description = "SiteDescription" + random;
    private final String siteName = String.format("C6907Site-%s", RandomData.getRandomAlphanumeric());
    private final String docContent = "C6936 Doc content";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C6907")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void accessTheDocumentLibrary()
    {
        LOG.info("Step 1: Navigate to testSite's dashboard page.");
        sitePage.navigate(siteName);
        assertTrue(sitePage.isDocumentLibraryLinkDisplayed(), "Document Library link is not displayed");

        LOG.info("Step 2: Click on Document Library link.");
        sitePage.clickDocumentLibrary();
        getBrowser().waitInSeconds(5);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
    }

    @TestRail (id = "C6908")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void verifyTheDocumentLibraryMainPage()
    {
        LOG.info("Step 1: Navigate to testSite's dashboard and click on Document Library link.");
        sitePage.navigate(siteName);
        sitePage.clickDocumentLibrary();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");

        LOG.info("Step 2: Verify the items displayed on the left panel from the Document Library page.");
        assertTrue(filters.isDocumentsDropDownDisplayed(), "Documents link is not present");
        assertTrue(filters.isallDocumentsFilterDisplayed(), "All documents filter is not displayed");
        assertTrue(filters.isIMEditingFilterDisplayed(), "I'm editing filter is not present");
        assertTrue(filters.isOthersAreEditingFilterDisplayed(), "Others are editing filter is not present");
        assertTrue(filters.isRecentlyModifiedFilterDisplayed(), "Recently modified filter is not present");
        assertTrue(filters.isRecentlyAddedFilterDisplayed(), "Recently added filter is not present");
        assertTrue(filters.isMyFavoritesFilterDisplayed(), "My Favorites filter is not present");
        assertTrue(filters.isLibraryLinkDisplayed(), "Library link is not displayed");
        assertTrue(filters.isDocumentsLinkPresent(), "Documents link is not present under Library");
        assertTrue(filters.isCategorisFilterDisplayed(), "Categories filter is not displayed");
        assertTrue(filters.isCategoriesRootDisplayed(), "Categories root is not displayed");
        assertTrue(filters.checkIfTagsFilterIsPresent(), "Tags filter is not present");

        LOG.info("Step 3: Verify the items displayed on the main area from the Document Library page.");
        assertTrue(documentLibraryPage.isCreateButtonDisplayed(), "Create button is not present");
        assertTrue(documentLibraryPage.isUploadButtonDisplayed(), "Upload button is not displayed");
        assertTrue(headerMenuBar.isSelectButtonDisplayed(), "Select button is not displayed");
        assertTrue(headerMenuBar.isSelectItemsMenuDisplayedDisabled(), "Select items menu is displayed and disabled by default");
        assertTrue(documentLibraryPage.isSortButtonDisplayed(), "Sort button is not displayed");
        assertTrue(documentLibraryPage.isSortByFieldButtonDisplayed(), "Sort by field (By default Name) button is not displayed");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Options menu button is not displayed");
        assertTrue(documentLibraryPage.isPaginationDisplayed(), "Page number is not displayed");
    }

    @TestRail (id = "C6935")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAvailableOptionsForFolder()
    {
        String folderName = "folder-C6935-" + random;

        LOG.info("Step 1: Navigate to testSite's document library page.");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
        assertEquals(documentLibraryPage.getCurrentSiteName(), siteName);

        LOG.info("Step 2: Create any folder in the site's document library (e.g. testFolder).");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "test folder is not displayed");

        LOG.info("Step 3: Hover over the created folder.");
        List<String> expectedActions = Arrays.asList("Download as Zip", "View Details", "Edit Properties", "Copy to...", "Move to...", "Manage Rules", "Delete Folder", "Manage Aspects", "Manage Permissions");
        assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem(folderName, expectedActions), "Expected actions");
    }

    @TestRail (id = "C6936")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAvailableOptionsForFile()
    {
        String docName = "docName-C6936-" + random;

        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);

        LOG.info("Step 1: Navigate to testSite's document library page.");
        documentLibraryPage.navigate(siteName);
        getBrowser().waitInSeconds(5);

        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
        assertEquals(documentLibraryPage.getCurrentSiteName(), siteName);

        LOG.info("Step 2: Create any file in the site's document library (e.g. testFile).");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " document is displayed");

        LOG.info("Step 3: Hover over the created file.");
        List<String> expectedActions = Arrays.asList("Download", "View In Browser", "Edit in Google Docs™", "Edit Properties", "Upload New Version", "Edit in Alfresco",
            "Edit Offline", "Copy to...", "Move to...", "Delete Document", "Start Workflow", "Manage Permissions");
        assertTrue(documentLibraryPage.areActionsAvailableForLibraryItem(docName, expectedActions), "Expected actions");
    }

    @TestRail (id = "C6938")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void viewFolderContents()
    {
        String folderNameC6938 = "C6938-folder-" + random;
        String testFileC6938 = "C6938-file1-" + random;
        String testFile1C6938 = "C6938-file2-" + random;
        String testFile2C6938 = "C6938-file3-" + random;

        LOG.info("Preconditions: Create test user, site, folder and files");
        contentService.createFolder(user, password, folderNameC6938, siteName);
        contentService.createDocumentInFolder(user, password, siteName, folderNameC6938, DocumentType.TEXT_PLAIN, testFileC6938, "C6938 Document content");
        contentService.createDocumentInFolder(user, password, siteName, folderNameC6938, DocumentType.TEXT_PLAIN, testFile1C6938, "C6938 Document content");
        contentService.createDocumentInFolder(user, password, siteName, folderNameC6938, DocumentType.TEXT_PLAIN, testFile2C6938, "C6938 Document content");

        LOG.info("Step 1: Navigate to testSite's document library page.");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
        assertEquals(documentLibraryPage.getCurrentSiteName(), siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderNameC6938), folderNameC6938 + " is displayed");

        LOG.info("Step 2: Click on testFolder.");
        documentLibraryPage.clickOnFolderName(folderNameC6938);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileC6938), testFileC6938 + " is displayed in " + folderNameC6938);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile1C6938), testFile1C6938 + " is displayed in " + folderNameC6938);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile2C6938), testFile2C6938 + " is displayed in " + folderNameC6938);

        LOG.info("Step 3: Verify the breadcrumb path.");

        assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, " + folderNameC6938 + "]", "Breadcrumb path is not correct");
    }
}