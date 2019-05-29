package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentView;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet.DocumentsFilter;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyDocumentsTests extends ContextAwareWebTest
{
    @Autowired
    MyDocumentsDashlet myDocumentsDashlet;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String userName1;
    private String siteName1;

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        cleanupAuthenticatedSession();
        userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName1, password, domain, siteName1, "description", SiteService.Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        siteService.delete(adminUser, adminPassword, siteName1);
    }

    @TestRail (id = "C2134")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void detailedView()
    {
        LOG.info("STEP 1 - Create document then update its content");
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        String file = "TestDoc";
        Assert.assertFalse(contentService.createDocument(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file, file).getId().isEmpty());
        contentService.updateDocumentContent(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file, RandomStringUtils.randomAlphanumeric(10));
        contentService.updateDocumentContent(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file, RandomStringUtils.randomAlphanumeric(20));
        contentAction.likeContent(userName2, password, siteName1, file);
        setupAuthenticatedSession(userName1, password);
        myDocumentsDashlet.waitForDocument();

        LOG.info("STEP 2 - Check document title and small thumbnail");
        Assert.assertEquals(myDocumentsDashlet.getDocumentsLinks().get(0), file, "Document name is not correct");
        Assert.assertTrue(myDocumentsDashlet.isSmallThumbnailDisplayed(file), "Small thumbnail is not displayed");

        LOG.info("STEP 3 - Set document view to Detailed View and check comment and like sections");
        myDocumentsDashlet.setDocumentView(DocumentView.DetailedView.toString());
        Assert.assertTrue(myDocumentsDashlet.isCommentLinkDisplayed(file), "Comment link is not displayed for document");
        Assert.assertTrue(myDocumentsDashlet.isLikeLinkDisplayed(file), "Like link is not displayed for document");
        Assert.assertEquals(myDocumentsDashlet.getNumberOfLikes(file), 1, "Document has wrong number of likes");
        userDashboardPage.navigate(userName1);

        LOG.info("STEP 4 - Add/Remove document from favorites");
        Assert.assertTrue(myDocumentsDashlet.isAddToFavoritesPresent(file), "Document should not be set as favourite");
        myDocumentsDashlet.addDocumentToFavorites(file);
        Assert.assertTrue(myDocumentsDashlet.isDocumentFavourite(file), "Document should be set as favourite");
        myDocumentsDashlet.removeDocumentFromFavorites(file);
        Assert.assertTrue(myDocumentsDashlet.isAddToFavoritesPresent(file), "Document should not be set as favourite");

        LOG.info("STEP 5 - Check document description, size, modified information, version and large thumbnail");
        Assert.assertEquals(myDocumentsDashlet.getDocumentDescription(file), "No Description", "Document should have no description");
        Assert.assertEquals(myDocumentsDashlet.getDocumentSize(file), "20 bytes", "Document size is not correct");
        Assert.assertEquals(myDocumentsDashlet.getModifiedInformation(file), "Modified just now in " + siteName1,
            "Document modified information is not correct");
        Assert.assertEquals(myDocumentsDashlet.getDocumentVersion(file), "1.2", "Document version is not correct");
        Assert.assertTrue(myDocumentsDashlet.isLargeThumbnailDisplayed(file), "Large thumbnail is not displayed");

        mySitesDashlet.accessSite(siteName1);
        userDashboardPage.navigate(userName1);

        LOG.info("STEP 6 - Check favorite icon on document details page");
        myDocumentsDashlet.addDocumentToFavorites(file);
        Assert.assertTrue(myDocumentsDashlet.isDocumentFavourite(file), "Document should be set as favourite");
        myDocumentsDashlet.accessDocument(file);
        Assert.assertTrue(documentDetailsPage.isDocumentFavourite());
        userDashboardPage.navigate(userName1);
        myDocumentsDashlet.removeDocumentFromFavorites(file);
        userDashboardPage.navigate(userName1);
        Assert.assertTrue(myDocumentsDashlet.isAddToFavoritesPresent(file), "Document should not be set as favourite");
        userDashboardPage.navigate(userName1);
        myDocumentsDashlet.pressDocumentThumbnail(file);
        Assert.assertTrue(documentDetailsPage.isAddToFavoriteLinkDisplayed(), "Add to favorites link is displayed, document is not favorite");

        LOG.info("STEP 7 - Like document, check number of likes");
        contentAction.likeContent(userName1, password, siteName1, file);
        userDashboardPage.navigate(userName1);
        Assert.assertTrue(myDocumentsDashlet.isLikedIconDisplayed(file), "Like icon is not displayed for document");
        Assert.assertEquals(myDocumentsDashlet.getNumberOfLikes(file), 2, "Document has wrong number of likes");
        myDocumentsDashlet.likeDocument(file);
        Assert.assertTrue(myDocumentsDashlet.isLikeLinkDisplayed(file), "Like icon is not displayed for document");
        Assert.assertEquals(myDocumentsDashlet.getNumberOfLikes(file), 1, "Document has wrong number of likes");

        LOG.info("STEP 8 - Press add comment, check that comment section is displayed on document details page");
        myDocumentsDashlet.addComment(file);
        Assert.assertTrue(documentDetailsPage.isAddCommentBlockDisplayed(), "Add comment block is not displayed on document details page");

        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
    }

    @TestRail (id = "C2138")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void filterDocuments()
    {
        LOG.info("STEP 1 - Create 3 documents, one is checked out for edit and one is favorite");
        String file1 = String.format("File1%s", RandomData.getRandomAlphanumeric());
        String file2 = String.format("File2%s", RandomData.getRandomAlphanumeric());
        String file3 = String.format("File3%s", RandomData.getRandomAlphanumeric());
        contentService.createDocument(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file1, file1);
        contentService.createDocument(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file2, file2);
        contentService.createDocument(userName1, password, siteName1, DocumentType.TEXT_PLAIN, file3, file3);

        contentAction.checkOut(userName1, password, siteName1, file2);
        contentAction.setFileAsFavorite(userName1, password, siteName1, file3);

        setupAuthenticatedSession(userName1, password);
        myDocumentsDashlet.waitForDocument();

        LOG.info("STEP 2 - Filter Editing documents, check that only file2 is listed, " + file2);
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.Editing.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "Number of displayed documents is 1");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent(file2), file2 + " is present");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent(file1), file1 + " is not present");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent(file3), file3 + " is not present");

        LOG.info("STEP 3 - Filter Recently Modified documents, check that all files are listed: " + file1 + file2 + file3);
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.RecentlyModified.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(3), "Number of displayed documents is 3");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent(file1), file1 + " is present");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent(file2), file2 + " is present");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent(file3), file3 + " is present");

        LOG.info("STEP 4 - Filter My Favorites documents, check that only file3 is listed, " + file3);
        myDocumentsDashlet.filterMyDocuments(DocumentsFilter.MyFavorites.toString());
        myDocumentsDashlet.refreshWhileErrorLoadingDocumentsIsDisplayed();
        Assert.assertTrue(myDocumentsDashlet.areNumberOfDocumentsDisplayed(1), "Number of displayed documents is 1");
        Assert.assertTrue(myDocumentsDashlet.isDocumentPresent(file3), file3 + " is present");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent(file1), file1 + " is not present");
        Assert.assertFalse(myDocumentsDashlet.isDocumentPresent(file2), file2 + " is not present");
    }
}