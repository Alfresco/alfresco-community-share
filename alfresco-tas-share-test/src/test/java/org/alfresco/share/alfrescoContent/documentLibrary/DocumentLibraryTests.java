package org.alfresco.share.alfrescoContent.documentLibrary;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DocumentLibraryTests extends ContextAwareWebTest
{
    @Autowired private DocumentLibraryPage documentLibraryPage;

    @Autowired private HeaderMenuBar headerMenuBar;

    @Autowired private DocumentsFilters filters;

    @Autowired private SiteDashboardPage sitePage;

    private final String random = DataUtil.getUniqueIdentifier();
    private final String user = "User" + random;
    private final String description = "SiteDescription" + random;
    private final String siteName = "C6907Site-" + DataUtil.getUniqueIdentifier();
    private final String docContent = "C6936 Doc content";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
        siteService.create(user, password, domain, siteName, description, Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C6907")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void accessTheDocumentLibrary()
    {
        LOG.info("Step 1: Navigate to testSite's dashboard page.");
        sitePage.navigate(siteName);
        assertTrue(sitePage.isDocumentLibraryLinkDisplayed(), "Document Library link is not displayed");

        LOG.info("Step 2: Click on Document Library link.");
        sitePage.clickDocumentLibrary();
        System.out.println(documentLibraryPage.getPageTitle());
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
    }

    @TestRail(id = "C6908")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
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

    @TestRail(id = "C6935")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
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
        documentLibraryPage.mouseOverContentItem(folderName);
        documentLibraryPage.clickMore();
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Download as Zip"), "Download as zip is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "View Details"), "View Details is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Edit Properties"), "Edit Properties is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Copy to..."), "Copy to... is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Move to..."), "Move to... is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Manage Rules"), "Manage Rules is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Delete Folder"), "Delete Folder is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Manage Permissions"), "Manage Permissions is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Manage Aspects"), "Manage Aspects is not available");
    }

    @TestRail(id = "C6936")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
    public void checkAvailableOptionsForFile()
    {
        String docName = "docName-C6936-" + random;

        LOG.info("Step 1: Navigate to testSite's document library page.");
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library");
        assertEquals(documentLibraryPage.getCurrentSiteName(), siteName);

        LOG.info("Step 2: Create any file in the site's document library (e.g. testFile).");
        contentService.createDocument(user, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        documentLibraryPage.renderedPage();
        getBrowser().refresh();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), docName + " document is displayed");

        LOG.info("Step 3: Hover over the created file.");
        documentLibraryPage.mouseOverFileName(docName);
        documentLibraryPage.clickMore();
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Download"), "Download is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "View In Browser"), "View In Browser is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit in Google Docs™"), "Edit in Google Docs™ is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit Properties"), "Edit Properties is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Upload New Version"), "Upload New Version is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit in Alfresco"), "Edit in Alfresco is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Edit Offline"), "Edit Offline is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Copy to..."), "Copy to... is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Move to..."), "Move to... is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Delete Document"), "Delete Document is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Start Workflow"), "Start Workflow is not available");
        assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(docName, "Manage Permissions"), "Manage Permissions is not available");
    }

    @TestRail(id = "C6938")
    @Test(groups = { TestGroup.SANITY, TestGroup.ALFRESCO_CONTENT})
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
//        getBrowser().waitUntilElementClickable(documentLibraryPage.subfolderDocListTree(folderNameC6938), 10L);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFileC6938), testFileC6938 + " is displayed in " + folderNameC6938);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile1C6938), testFile1C6938 + " is displayed in " + folderNameC6938);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFile2C6938), testFile2C6938 + " is displayed in " + folderNameC6938);

        LOG.info("Step 3: Verify the breadcrumb path.");

        assertEquals(documentLibraryPage.getBreadcrumbList(), "[Documents, " + folderNameC6938 + "]", "Breadcrumb path is not correct");
    }
}