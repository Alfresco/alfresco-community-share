package org.alfresco.share.site.siteDashboard;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SiteContentDashletTests extends ContextAwareWebTest
{
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

    private final String fileName = String.format("testFile%s", DataUtil.getUniqueIdentifier());
    private final String fileName2 = String.format("testFile2%s", DataUtil.getUniqueIdentifier());
    private final String uploadFileName = "testFile1";
    private final String docContent = "testContent";
    private final String userName1 = String.format("User1%s", DataUtil.getUniqueIdentifier());
    private String userName2 = String.format("User1%s", DataUtil.getUniqueIdentifier());
    private String siteName = String.format("Site%s", DataUtil.getUniqueIdentifier());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, userName1, userName1, "fName1", "lName1");
        userService.create(adminUser, adminPassword, userName2, userName2, userName2, "fName2", "lName2");

        siteService.create(userName1, userName1, domain, siteName, "testDescription", Visibility.PUBLIC);
        contentService.createDocument(userName1, userName1, siteName, DocumentType.TEXT_PLAIN, fileName, docContent);
        contentService.createDocument(userName1, userName1, siteName, DocumentType.TEXT_PLAIN, fileName2, docContent);
        contentService.uploadFileInSite(userName1, userName1, siteName, testDataFolder + uploadFileName);
        userService.createSiteMember(adminUser, adminPassword, userName2, siteName, "SiteManager");
    }

    @TestRail(id = "C5425")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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

    @TestRail(id = "C5413")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void noDocumentsAdded()
    {
        String siteName1 = String.format("Site%s", DataUtil.getUniqueIdentifier());

        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        siteService.create(userName1, userName1, domain, siteName1, "testDescription", Visibility.PUBLIC);
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
        assertEquals(siteContentDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.SITE_CONTENT), true, "'Help' icon is displayed.");

        LOG.info("STEPS 5,6 - Click on ? icon. Verify the message displayed for Site Content dashlet");
        siteContentDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_CONTENT);
        assertEquals(siteContentDashlet.getHelpBalloonMessage(), language.translate("siteContentDashlet.helpBalloonMessage"), "'Help' balloon message=");

        LOG.info("STEPS 7 - Click X icon on pop-up balloon and verify it is closed.");
        siteContentDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_CONTENT);
        siteContentDashlet.closeHelpBalloon();
        assertEquals(siteContentDashlet.isBalloonDisplayed(), false, "'Help' balloon closed.");

    }

    @TestRail(id = "C5456")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void favoriteItem() throws Exception
    {
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard, click on Detailed View for Site Content dashlet");
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

    @TestRail(id = "C5457")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
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

    @TestRail(id = "C5457")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void siteContentDashletDetailedView()
    {
        LOG.info("Preconditions: create site, add another user to site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName2, userName2);
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickDocumentLibraryItemAction(fileName2, "Edit in Alfresco", editInAlfrescoPage);
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

    @TestRail(id = "C5458")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addCommentOnItem()
    {
        LOG.info("Preconditions: create site, add document to site, login to Share, navigate to site's dashboard");
        setupAuthenticatedSession(userName1, userName1);
        siteDashboard.navigate(siteName);

        LOG.info("STEP 1 - Click on the Comment link for the document visible on Site Content dashlet.");
        siteContentDashlet.clickDetailedViewButton();
        siteContentDashlet.clickCommentLink(fileName);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Details Page is opened");
        Assert.assertTrue(documentDetailsPage.isCommentBoxOpened(), "Comment box opened");

        LOG.info("STEP 2 - Add text in the Comment box and click Add Comment to add your comment to the document.");
        documentDetailsPage.addComment("testComment");
        Assert.assertEquals(documentDetailsPage.getCommentsListSize(), 1, "Number of comments= ");
    }

}
