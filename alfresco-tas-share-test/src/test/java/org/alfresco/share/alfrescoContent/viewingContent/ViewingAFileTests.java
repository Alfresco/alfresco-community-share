package org.alfresco.share.alfrescoContent.viewingContent;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.alfresco.common.Utils.testDataFolder;
import static org.testng.Assert.assertEquals;

public class ViewingAFileTests extends ContextAwareWebTest
{
    private final String user = "C9917User1489451372540";
    private final String siteName = String.format("C9917SiteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("C9917SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String docName = "File-C9917";
    private final DateTime currentDate = new DateTime();
    //@Autowired
    private DocumentDetailsPage documentPreviewPage;
    //@Autowired
    private DocumentLibraryPage documentLibraryPage;

    //@Autowired
    private UserDashboardPage userDashboard;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        contentService.uploadFileInSite(user, password, siteName, testDataFolder + docName + ".docx");
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C9917")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void informationAndOptionsCheckAvailableInfo()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(docName);

        LOG.info("Step 2: Verify the file location and name information available.");
        Assert.assertEquals(documentPreviewPage.getFileName(), docName + ".docx");
        Assert.assertTrue(documentPreviewPage.isDocumentsLinkPresent(), "Documents link to return to Document Library is not displayed");

        LOG.info("Step 3: Verify and confirm that the file version is displayed.");
        Assert.assertEquals(documentPreviewPage.getFileVersion(), "1.0", "Wrong value for file version");

        LOG.info("Step 4: Verify and confirm that information regarding the last user who has modified the file is available.");
        Assert.assertEquals(documentPreviewPage.getItemModifier(), user + " " + user, "Wrong value for Modifier");

        LOG.info("Step 5: Verify that the date/time of the last modification is displayed.");
        Assert.assertTrue(documentPreviewPage.getModifyDate().contains(currentDate.toString("EEE d MMM yyyy")), "Wrong value for Modified on");

        LOG.info("Step 6: Verify that the file type is indicated by an icon to the left of the information section.");
        Assert.assertTrue(documentPreviewPage.isDocumentThumbnailDisplayed().contains("docx-file-48.png"));
    }

    @TestRail (id = "C9923")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void informationAndOptionsCheckLinkToReturnToDocumentLibrary()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(docName);

        LOG.info("Step 2: Verify the presence of the link to return to Document Library.");
        Assert.assertTrue(documentPreviewPage.isDocumentsLinkPresent(), "Documents link to return to Document Library is not displayed");

        LOG.info("Step 3: Click on the Documents link.");
        documentPreviewPage.clickDocumentsLink();
        String expectedRelativePath = "share/page/site/" + siteName + "/documentlibrary";
        assertEquals(documentLibraryPage.getRelativePath(), expectedRelativePath, "User is not redirected to Document Library");
    }

    @TestRail (id = "C9925")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void informationAndOptionsLikeOption()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(docName);

        LOG.info("Step 2: Verify that the Like option is available.");
        Assert.assertTrue(documentPreviewPage.isLikeButtonPresent(), "The like button is not displayed");

        LOG.info("Step 3: Click the Like button.");
        documentPreviewPage.clickOnLikeUnlike();
        Assert.assertEquals(documentPreviewPage.getLikesNo(), 1);

        LOG.info("Step 4: Click the Like button again to unlike the content");
        documentPreviewPage.clickOnLikeUnlike();
        Assert.assertEquals(documentPreviewPage.getLikesNo(), 0);
    }

    @TestRail (id = "C9926")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })

    public void informationAndOptionsFavoriteOption()
    {
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Click on the thumbnail or name of the file in the document library.");
        documentLibraryPage.clickOnFile(docName);

        LOG.info("Step 2: Verify that the Favorite option is available.");
        Assert.assertTrue(documentPreviewPage.isAddToFavoriteLinkDisplayed(), "Favorite button is not displayed");

        LOG.info("Step 3: Click the Favorite button.");
        documentPreviewPage.clickOnFavoriteUnfavoriteLink();
        Assert.assertTrue(documentPreviewPage.isDocumentFavourite(), "Document is not successfully favorited");

        LOG.info("Step 4: Click the Favorite button again.");
        documentPreviewPage.clickOnFavoriteUnfavoriteLink();
        Assert.assertFalse(documentPreviewPage.isDocumentFavourite(), "Document has not been unfavorited");
    }


    @TestRail (id = "C9936")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "office" })

    public void checkActionsAvailability()
    {
        LOG.info("Step 1: Click on document name");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(docName);

        LOG.info("Step 2: Check the document actions available");
        /*Assert.assertTrue(fileActions.isDocumentActionsBlockDisplayed(), "Document Actions block is not displayed");
        Assert.assertTrue(fileActions.isViewInBrowserDisplayed(), "View in browser action is not displayed");
        Assert.assertTrue(fileActions.isEditInGoogleDocsDisplayed(), "Edit in google docs action is not displayed");
        Assert.assertTrue(fileActions.isEditInMicrosoftOfficeDisplayed(), "Edit in Microsoft Office action is not displayed");
        Assert.assertTrue(fileActions.isUploadNewVersionDisplayed(), "Upload new version is not displayed");
        Assert.assertTrue(fileActions.isEditPropertiesDisplayed(), "Edit properties is not displayed");
        Assert.assertTrue(fileActions.isMoveToDisplayed(), "Move to is not displayed");
        Assert.assertTrue(fileActions.isCopyToDisplayed(), "Copy to is not displayed");
        Assert.assertTrue(fileActions.isDeleteDocumentDisplayed(), "Delete document is not displayed");
        Assert.assertTrue(fileActions.isStartWorkflowDisplayed(), "Start Workflow is not displayed");
        Assert.assertTrue(fileActions.isManagePermissionsDisplayed(), "Manage Permissions is not displayed");
        Assert.assertTrue(fileActions.isBecomeOwnerDisplayed(), "Become owner is not displayed");
        Assert.assertTrue(fileActions.isManageAspectsDisplayed(), "Manage aspects is not displayed");
        Assert.assertTrue(fileActions.isChangeTypeDisplayed(), "Change type is not displayed");

        LOG.info("Step 3: Check Tags presence");
        Assert.assertTrue(fileActions.isTagsBlockDisplayed(), "Tags block is not displayed");

        LOG.info("Step 4: Check that Share block present");
        Assert.assertTrue(fileActions.isShareBlockDisplayed(), "Share block is not displayed");
        Assert.assertTrue(fileActions.isShareLinkDisplayed(), "Share link section is not displayed in the Share block");

        LOG.info("Step 5: Check that the Properties block is displayed");
        Assert.assertTrue(fileActions.isPropertiesBlockDisplayed(), "Properties block is not displayed");

        LOG.info("Step 6: Check that the workflow block is displayed");
        Assert.assertTrue(fileActions.isWorkflowsBlockDisplayed(), "Workflows block is not displayed");

        LOG.info("Step 7: Check that Version History block is displayed ");
        Assert.assertTrue(fileActions.isVersionHistoryBlockDisplayed(), "Version History block is not displayed");*/
    }
}
