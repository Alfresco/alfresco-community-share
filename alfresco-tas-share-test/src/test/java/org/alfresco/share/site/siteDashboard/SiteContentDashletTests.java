package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class SiteContentDashletTests extends ContextAwareWebTest
{
    private final String fileName = String.format("testFile%s", RandomData.getRandomAlphanumeric());
    private final String fileName2 = String.format("testFile2%s", RandomData.getRandomAlphanumeric());
    private final String uploadFileName = "testFile1";
    private final String docContent = "testContent";
    private final String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    @Autowired
    SiteContentDashlet siteContentDashlet;
    @Autowired
    DocumentDetailsPage documentDetailsPage;
    @Autowired
    SiteDashboardPage siteDashboard;
    @Autowired
    DocumentLibraryPage documentLibraryPage;
    @Autowired
    EditInAlfrescoPage editInAlfrescoPage;
    @Autowired
    SocialFeatures socialFeatures;
    private String userName2 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
    private String siteNameFilters = "siteFiltersTest" + RandomData.getRandomAlphanumeric();
    private String userNameFilters = "userFilters" + RandomData.getRandomAlphanumeric();
    private String docName = "FiltersTestDoc" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, userName1, userName1, "fName1", "lName1");
        userService.create(adminUser, adminPassword, userName2, userName2, userName2, "fName2", "lName2");
        userService.create(adminUser, adminPassword, userNameFilters, password, userNameFilters, "Share", "Filters");
        siteService.create(userName1, userName1, domain, siteName, "testDescription", SiteService.Visibility.PUBLIC);
        siteService.create(userNameFilters, password, domain, siteNameFilters, "testFiltersDescription", SiteService.Visibility.PUBLIC);
        contentService.createDocument(userName1, userName1, siteName, DocumentType.TEXT_PLAIN, fileName, docContent);
        contentService.createDocument(userName1, userName1, siteName, DocumentType.TEXT_PLAIN, fileName2, docContent);
        contentService.uploadFileInSite(userName1, userName1, siteName, testDataFolder + uploadFileName);
        contentService.createDocument(userNameFilters, password, siteNameFilters, DocumentType.TEXT_PLAIN, docName, docContent);
        userService.createSiteMember(adminUser, adminPassword, userName2, siteName, "SiteManager");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C5425")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void siteContentDashletSimpleView()
    {
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1 - Click \"Simple view\" icon and verify the info displayed");
        siteContentDashlet.clickSimpleViewButton();
        Assert.assertTrue(siteContentDashlet.isSmallThumbnailDisplayed(uploadFileName), "Small thumbnail for testFile1 is expected to be displayed");
        Assert.assertTrue(siteContentDashlet.isFileLinkPresent(uploadFileName), "Link for testFile1 is expected to be displayed");
        Assert.assertTrue(siteContentDashlet.isDocDetailsSectionPresent(uploadFileName), "Document details for testFile1 are expected to be displayed");
        Assert.assertTrue(siteContentDashlet.isSmallThumbnailDisplayed(fileName), "Small thumbnail for " + fileName + "is expected to be displayed");
        Assert.assertTrue(siteContentDashlet.isFileLinkPresent(fileName), "Link for " + fileName + " is expected to be displayed");
        Assert.assertTrue(siteContentDashlet.isDocDetailsSectionPresent(fileName), "Document details for " + fileName + "are expected to be displayed");
        LOG.info("STEP 2 - Click file name link and verify the document details page is displayed");
        siteContentDashlet.clickFileLink(fileName);
        Assert.assertTrue(documentDetailsPage.isDocDetailsPageHeaderDisplayed(), "Document page header displayed");
        LOG.info("STEP 3 - Hover over the small thumbnail icon and verify bigger preview is displayed");
        siteDashboard.navigate(siteName);
        siteContentDashlet.clickSimpleViewButton();
        siteContentDashlet.mouseHoverSmallThumbail(fileName);
        Assert.assertTrue(siteContentDashlet.isBigPreviewDisplayed(), "Preview for " + fileName + "is expected to be displayed");
        siteContentDashlet.mouseHoverSmallThumbail(uploadFileName);
        Assert.assertTrue(siteContentDashlet.isBigPreviewDisplayed(), "Preview for testFile1 displayed");
    }

    @TestRail (id = "C5413")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void noDocumentsAdded()
    {
        String siteName1 = String.format("Site%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        siteService.create(userName1, userName1, domain, siteName1, "testDescription", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName1);
        LOG.info("STEP 1 - Verify Site Content Filter dashlet.");
        siteContentDashlet.clickDefaultFilterButton();
        Assert.assertTrue(siteContentDashlet.isFilterDisplayed("I've Recently Modified"), "I've Recently Modified filter not displyed");
        Assert.assertTrue(siteContentDashlet.isFilterDisplayed("I'm Editing"), "I'm Editing filter not displyed");
        Assert.assertTrue(siteContentDashlet.isFilterDisplayed("My Favorites"), "My Favorites filter not displyed");
        LOG.info("STEP 2 - Verify the presence of Simple View button.");
        Assert.assertTrue(siteContentDashlet.isSimpleViewButtonPresent(), "Simple View button displayed");
        LOG.info("STEP 3 - Verify the presence of Detailed View button.");
        Assert.assertTrue(siteContentDashlet.isDetailedViewButtonPresent(), "Detailed View button displayed");
        LOG.info("STEP 4 - Verify the presence of Help button.");
        assertEquals(siteContentDashlet.assertDashletHelpIconIsDisplayed(Dashlet.DashletHelpIcon.SITE_CONTENT), true, "'Help' icon is displayed.");
        LOG.info("STEPS 5,6 - Click on ? icon. Verify the message displayed for Site Content dashlet");
        siteContentDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_CONTENT);
        assertEquals(siteContentDashlet.getHelpBalloonMessage(), language.translate("siteContentDashlet.helpBalloonMessage"), "'Help' balloon message=");
        LOG.info("STEPS 7 - Click X icon on pop-up balloon and verify it is closed.");
        siteContentDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_CONTENT);
        siteContentDashlet.closeHelpBalloon();
        assertEquals(siteContentDashlet.isBalloonDisplayed(), false, "'Help' balloon closed.");
        siteService.delete(adminUser, adminPassword, siteName1);

    }

    @TestRail (id = "C5456")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void favoriteItem() throws Exception
    {
        LOG.info(
            "Preconditions: create site, add document to site, login to Share, navigate to site's dashboard, click on Detailed View for Site Content dashlet");
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName);
        LOG.info("STEPS 1 - For the file available in the Site Content Dashlet click on Favorite icon");
        siteContentDashlet.clickDetailedViewButton();
        siteContentDashlet.addFileToFavorites(fileName);
        Assert.assertTrue(siteContentDashlet.isFileAddedToFavorites(fileName), "Document is favorited");
        LOG.info("STEPS 2 - Click on the Favorite icon to remove document from Favorites list");
        siteContentDashlet.removeFileFromFavorites(fileName);
        Assert.assertTrue(siteContentDashlet.isAddToFavoritesLinkDisplayed(fileName), "Document is removed from favorites");
    }

    @TestRail (id = "C5457")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void likeUnlikeItem()
    {
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1 - Click the Like button for the document visible on the Site Content dashlet.");
        siteContentDashlet.clickDetailedViewButton();
        siteContentDashlet.likeFile(fileName);
        assertEquals(siteContentDashlet.getNumberOfLikes(fileName), 1, "Number of likes=");
        LOG.info("STEP 2 - Click the Unlike button for the document visible on the Site Content dashlet.");
        siteContentDashlet.unlikeFile(fileName);
        assertEquals(siteContentDashlet.getNumberOfLikes(fileName), 0, "Number of likes=");
    }

    @TestRail (id = "C5457")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void siteContentDashletDetailedView()
    {
        LOG.info("Preconditions: create site, add another user to site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName2, userName2);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName2, ItemActions.EDIT_IN_ALFRESCO, editInAlfrescoPage);
        editInAlfrescoPage.sendDocumentDetailsFields("editedName", "editedContent", "editedTitle", "editedDescription");
        editInAlfrescoPage.clickButton("Save");
        socialFeatures.clickLikeButton("editedName");
        LOG.info("STEP 1 - Click \"Detailed view\" icon and verify the info displayed");
        siteDashboard.navigate(siteName);
        siteContentDashlet.clickDetailedViewButton();
        Assert.assertTrue(siteContentDashlet.isBigThumbnailDisplayed("editedName"), "Big thumbnail for file displayed");
        Assert.assertTrue(siteContentDashlet.isFileLinkPresent("editedName"), "Link for file displayed");
        Assert.assertTrue(siteContentDashlet.isFileVersionDisplayed("editedName", "1.1"), "File version displayed");
        Assert.assertTrue(siteContentDashlet.isDocDetailsSectionPresent("editedName"), "Document details section displayed");
        Assert.assertTrue(siteContentDashlet.areFileDetailsDisplayed("editedName", siteName), "Correct details displayed");
        Assert.assertTrue(siteContentDashlet.isDocumentSizeDisplayed("editedName", "13 bytes"), "Document size displayed");
        Assert.assertTrue(siteContentDashlet.isFileDescription("editedName", "editedDescription"), "description for file displayed");
        Assert.assertTrue(siteContentDashlet.isAddToFavoritesLinkDisplayed("editedName"), "Add to favorites link displayed");
        Assert.assertTrue(siteContentDashlet.isUnlikeLinkDisplayed("editedName"), "Unlike button displayed");
        Assert.assertTrue(siteContentDashlet.isCommentLinkDisplayed("editedName"), "comment link displayed");
        assertEquals(siteContentDashlet.getNumberOfLikes("editedName"), 1, "Number of likes=");
        LOG.info("STEP 2 - Click on document's name link.");
        siteContentDashlet.clickFileLink("editedName");
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Details Page is opened");
        LOG.info("STEP 3 - Click on thumbnail icon.");
        siteDashboard.navigate(siteName);
        siteContentDashlet.clickDetailedViewButton();
        siteContentDashlet.clickBigThumbnailForFile("editedName");
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Details Page is opened");
    }

    @TestRail (id = "C5458")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed"  })
    public void addCommentOnItem()
    {
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName);
        LOG.info("STEP 1 - Click on the Comment link for the document visible on Site Content dashlet.");
        siteContentDashlet.clickCommentLink(fileName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Details Page is opened");
        Assert.assertTrue(documentDetailsPage.isCommentBoxOpened(), "Comment box opened");
        LOG.info("STEP 2 - Add text in the Comment box and click Add Comment to add your comment to the document.");
        documentDetailsPage.addComment("testComment");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "testComment", "testComment comment content is not visible");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SITE_DASHBOARD })
    public void checkAllAvailableFilters()
    {
        SoftAssert softAssert = new SoftAssert();
        LOG.info("Step 1: Navigate to Site dashboard and check available filters");
        setupAuthenticatedSession(userNameFilters, password);
        siteDashboard.navigate(siteNameFilters);
        softAssert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 2, 1), "Site Content dashlet is not available");
        siteContentDashlet.clickDefaultFilterButton();
        softAssert.assertTrue(siteContentDashlet.isFilterDisplayed("I've Recently Modified"));
        softAssert.assertTrue(siteContentDashlet.isFilterDisplayed("I'm Editing"));
        softAssert.assertTrue(siteContentDashlet.isFilterDisplayed("My Favorites"));
        LOG.info("Step 2: Apply My Favorites Filter");
        siteContentDashlet.selectFilter("My Favorites");
        softAssert.assertFalse(siteContentDashlet.isFileLinkPresent(docName));
        LOG.info("Step 3: Apply My Favorites Filter");
        siteContentDashlet.clickDefaultFilterButton();
        siteContentDashlet.selectFilter("I'm Editing");
        softAssert.assertFalse(siteContentDashlet.isFileLinkPresent(docName));
        LOG.info("Step 4: Apply I've Recently Modified Filter");
        siteContentDashlet.clickDefaultFilterButton();
        siteContentDashlet.selectFilter("I've Recently Modified");
        softAssert.assertTrue(siteContentDashlet.isFileLinkPresent(docName));
        LOG.info("Step 5: Add document to favorites and check My Favorites filter");
        siteContentDashlet.addFileToFavorites(docName);
        siteContentDashlet.clickDefaultFilterButton();
        siteContentDashlet.selectFilter("My Favorites");
        softAssert.assertTrue(siteContentDashlet.isFileLinkPresent(docName));
        LOG.info("Step 6: Edit offline and check I'm Editing filter");
        documentLibraryPage.navigate(siteNameFilters);
        documentLibraryPage.clickDocumentLibraryItemAction(docName, ItemActions.EDIT_OFFLINE, documentLibraryPage);
        siteDashboard.navigate(siteNameFilters);
        siteContentDashlet.clickDefaultFilterButton();
        siteContentDashlet.selectFilter("I'm Editing");
        softAssert.assertTrue(siteContentDashlet.isFileLinkPresent(docName));
    }
}
