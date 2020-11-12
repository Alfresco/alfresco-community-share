package org.alfresco.share.alfrescoContent.documentLibrary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.pageCommon.DocumentsFilters;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ExploringTheLibraryDocumentsTests extends ContextAwareWebTest
{
    private final String user = String.format("C6320User%s", RandomData.getRandomAlphanumeric());
    private final String user1 = String.format("C6322User%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C6320SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String docName = "File-6320-0";
    private final String docName1 = "File-6320-1";
    private final String docName2 = "File-6320-2";
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;
    @Autowired
    private DocumentsFilters filters;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user1, password, user1 + "@tests.com", user1, user1);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
    }

    @TestRail (id = "C6320")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void viewAllDocumentsInLibrary()
    {
        String siteName = String.format("C6320SiteName%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        LOG.info("Step 1: Go To Documents section and click All Documents link");
        documentLibraryPage.navigate(siteName);
        filters.clickAllDocumentsFilter();
        getBrowser().waitInSeconds(1);
        assertEquals(filters.getNoContentText(), "No content items");

        LOG.info("Step 2: Upload several files into document libarary");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName + ".rtf");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName1 + ".docx");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName2 + ".docx");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "File-6320-0 is not displayed");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName1), "File-6320-1 is not displayed");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName2), "File-6320-2 is not displayed");

        LOG.info("Step 3: Open again document libarary and click All Documents");
        documentLibraryPage.navigate(siteName);
        filters.clickAllDocumentsFilter();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "File-6320-0 is not displayed");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName1), 6);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName1), "File-6320-1 is not displayed");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName2), 6);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName2), "File-6320-2 is not displayed");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6321")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void viewImEditingFiles()
    {
        String siteNameC6321 = String.format("C6321SiteName%s", RandomData.getRandomAlphanumeric());
//           userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteNameC6321, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);
        String docName = "testFile1";
        String docName1 = "testFile2";
        String docName2 = "testFile3";
        contentService.uploadFileInSite(user, password, siteNameC6321, testDataFolder + docName + ".txt");
        contentService.uploadFileInSite(user, password, siteNameC6321, testDataFolder + docName1 + ".txt");
        contentService.uploadFileInSite(user, password, siteNameC6321, testDataFolder + docName2 + ".txt");
        documentLibraryPage.navigate(siteNameC6321);

        LOG.info("Step 1: Click I'm editing filter");
        filters.clickIMEditingFilter();
        getBrowser().waitInSeconds(1);
        assertEquals(filters.getNoContentText(), "No content items");

        LOG.info("Step 2: Checkout testFile1 for offline editing");
        documentLibraryPage.navigate(siteNameC6321);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.EDIT_OFFLINE);

        LOG.info("Step 3: Open again Document Library and click I'm editing filter");

        documentLibraryPage.navigate(siteNameC6321);
        filters.clickIMEditingFilter();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName1), "testFile2 is displayed and it should not be displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName2), "testFile3 is displayed and it should not be displayed");
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "testFile1 is not displayed in the list of files currently beeing edited");
        assertEquals(documentLibraryPage.getInfoBannerText(docName), "This document is locked by you for offline editing.",
            "this document is locked by you message is not displayed");
        //     userService.delete(adminUser,adminPassword, user);
        //     contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteNameC6321);

    }

    @TestRail (id = "C10597")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void viewOthersAreEditingFiles()
    {
        String docName = "C10597-1";
        String docName1 = "C10597-2";
        String docName2 = "C10597-3";
        String expectedBannerText = "This document is locked by " + user + " " + user + ".";
        String siteName = String.format("C10597-site-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName + ".txt");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName1 + ".txt");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName2 + ".txt");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click Others are editing filter");
        filters.clickOthersAreEditing();
        getBrowser().waitInSeconds(2);
        assertEquals(filters.getNoContentText(), "No content items");

        LOG.info("Step 2: Check out file1.txt for offline editing.");
        contentAction.checkOut(user, password, siteName, docName + ".txt");

        LOG.info("Step 3: Logout and login to Share as user1 and check others are editing filter");
        userService.logout();
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);
        filters.clickOthersAreEditing();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "testFile1 is not displayed in the list of files beeing edited by other users");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName1), "testFile2 is displayed and it should not be displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(docName2), "testFile3 is displayed and it should not be displayed");
        assertEquals(documentLibraryPage.getInfoBannerText(docName), expectedBannerText);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C6325")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void viewFilesFromMyFavorites()
    {
        String docName = "C6325-1";
        String docName1 = "C6325-2";
        String docName2 = "C6325-3";

        String siteName = String.format("C6325-site-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName + ".txt");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName1 + ".txt");
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName2 + ".txt");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Go to Documents section and click My Favorites link");
        filters.clickMyFavouritesFilter();
        assertEquals(filters.getNoContentText(), "No content items");

        LOG.info("Step 2: Add test file to favorites");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickFavoriteLink(docName);
        assertTrue(documentLibraryPage.isFileFavorite(docName), "The gray star and text 'Favorite' are replaced by a golden star");

        LOG.info("Step 3: Open again site1's document library and and click on My Favorites link from Documents section.");
        documentLibraryPage.navigate(siteName);
        filters.clickMyFavouritesFilter();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "C6325-1 is not displayed in My favorites section");

        LOG.info("Step 4: Logout from Share and login as user2. Go to Documents section and click My Favorites link.");
        userService.logout();
        setupAuthenticatedSession(user1, password);
        documentLibraryPage.navigate(siteName);
        filters.clickMyFavouritesFilter();
        assertEquals(filters.getNoContentText(), "No content items");
        siteService.delete(adminUser, adminPassword, siteName);


    }

    @TestRail (id = "C10598")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void viewRecentlyAddedFiles()
    {
        String siteNameC10598 = String.format("C10598-site%s", RandomData.getRandomAlphanumeric());
        //    userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteNameC10598, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user, password);

        LOG.info("Step 1: Go to Documents section and click Recently Added link");
        documentLibraryPage.navigate(siteNameC10598);
        filters.clickRecentlyAddedFilter();
        assertEquals(filters.getNoContentText(), "No content items");

        LOG.info("Step 2: Create/Upload some new files in site1's document library");
        contentService.uploadFileInSite(user, password, siteNameC10598, testDataFolder + docName + ".rtf");
        contentService.uploadFileInSite(user, password, siteNameC10598, testDataFolder + docName1 + ".docx");
        contentService.uploadFileInSite(user, password, siteNameC10598, testDataFolder + docName2 + ".docx");

        LOG.info("Step 3: Open again site's document library and and click on Recently Added link from Documents section.");
        documentLibraryPage.navigate(siteNameC10598);
        filters.clickRecentlyAddedFilter();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName), 6);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName), "File is not displayed in Recently added section");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName1), 6);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName1), "File is not displayed in Recently added section");
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(documentLibraryPage.selectDocumentLibraryItemRow(docName2), 6);
        assertTrue(documentLibraryPage.isContentNameDisplayed(docName2), "File is not displayed in Recently added section");

        siteService.delete(adminUser, adminPassword, siteNameC10598);
    }
}