package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class ConsumerFoldersAndFilesTests extends ContextAwareWebTest
{
    @Autowired
    UploadContent uploadContent;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SharedFilesPage sharedFilesPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    SocialFeatures social;

    @Autowired
    CopyMoveUnzipToDialog copyMoveToDialog;

    @Autowired
    SiteUsersPage siteUsersPage;
    String comment = String.format("Comment%s", RandomData.getRandomAlphanumeric());
    private String user = String.format("Consumer%s", RandomData.getRandomAlphanumeric());
    private String user8865 = String.format("user8865%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("ConsumerDescription%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("ConsumerSite%s", RandomData.getRandomAlphanumeric());
    private String site8865 = String.format("site8865%s", RandomData.getRandomAlphanumeric());
    private String fileC8761 = "C8761 test file";
    private String fileC8762 = "C8762 test file";
    private String fileC8763 = "C8763 test file";
    private String fileC8770 = "C8770 test file";
    private String fileC8784 = "C8784 test file";
    private String fileC8865consumer = "C8865consumer test file";
    private String fileC8865collaborator = "C8865collaborator test file";
    String filePath8865 = testDataFolder + fileC8865collaborator;
    private String testContent = "testContent";
    private String folderC8761 = "C8761 folder";
    private String folderC8784 = "C8784 folder";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        userService.create(adminUser, adminPassword, user8865, password, user8865 + domain, user8865, user8865);
        siteService.create(adminUser, adminPassword, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, site8865, description, SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteConsumer");
        userService.createSiteMember(adminUser, adminPassword, user8865, site8865, "SiteCollaborator");
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8761, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8762, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8763, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8770, testContent);
        contentService.createDocument(adminUser, adminPassword, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileC8784, testContent);
        contentService.createDocument(adminUser, adminPassword, site8865, CMISUtil.DocumentType.TEXT_PLAIN, fileC8865consumer, testContent);
        contentService.createFolder(adminUser, adminPassword, folderC8761, siteName);
        contentService.createFolder(adminUser, adminPassword, folderC8784, siteName);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        userService.delete(adminUser, adminPassword, user8865);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user8865);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, site8865);

    }

    @TestRail (id = "C8761")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerViewFolderItemDetailsPage()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: On to Document Library page verify the user has access to View the folder details page");
        documentLibraryPage.clickDocumentLibraryItemAction(folderC8761, ItemActions.VIEW_DETAILS, documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");
        LOG.info("Step 2: Verify the user has access to View the file details page");
        documentDetailsPage.clickDocumentsLink();
        documentLibraryPage.clickOnFile(fileC8761);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "User is not on the Document Details page");
    }

    @TestRail (id = "C8762")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerLikeUnlikeFile()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(documentLibraryPage.isLikeButtonDisplayed(fileC8762), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(fileC8762), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 0, "The number of likes is not correct");
        LOG.info("Step 2: Click on the Like button");
        social.clickLikeButton(fileC8762);
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(fileC8762), "Like button is not enabled");
        LOG.info("Step 3: Hover over the file Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(fileC8762), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 1, "The number of likes is not correct");
        LOG.info("Step 4: Click on Unlike link.");
        social.clickUnlike(fileC8762);
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 0, "The number of likes is not correct");
        Assert.assertEquals(social.getLikeButtonMessage(fileC8762), "Like this document", "Like Button message is not correct");
    }

    @TestRail (id = "C8763")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerFavoriteUnfavoriteFile()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("STEP 1: Check the favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");
        LOG.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");
        LOG.info("STEP 3: Hover over the file 'Favorite' link");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileC8763), "Add document to favorites", "The text 'Add document to favorites' is displayed");
        LOG.info("STEP 4: Click on the 'Favorite' link");
        documentLibraryPage.clickFavoriteLink(fileC8763);
        getBrowser().waitInSeconds(4);
        assertTrue(documentLibraryPage.isFileFavorite(fileC8763), "The gray star and text 'Favorite' are replaced by a golden star");
        LOG.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8763), "Document is displayed in My favorites list!");
    }

    @TestRail (id = "C8765")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerEditPropertiesForContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to rename file and folder");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco is not available.");
        Assert.assertFalse(documentLibraryPage.isRenameIconDisplayed(fileC8761), "Rename icon is not displayed.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderC8761, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertFalse(documentLibraryPage.isRenameIconDisplayed(folderC8761), "Rename icon is not displayed.");
    }

    @TestRail (id = "C8770")
    @Test (groups = { TestGroup.SANITY, "user-roles", "tobefixed" })
    public void consumerCopyContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("STEP1: Hover over the file. STEP2: Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.clickDocumentLibraryItemAction(fileC8770, ItemActions.COPY_TO, copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + fileC8770 + " to...", "Displayed pop up");
        LOG.info("STEP3: Set the destination to 'Shared Files'");
        copyMoveToDialog.clickDestinationButton("Shared Files");
        LOG.info("STEP4: Click 'Copy' button");
        copyMoveToDialog.clickCopyButton(documentLibraryPage);
        LOG.info("STEP5: Verify displayed files from Documents");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8770), fileC8770 + " displayed in 'Documents'");
        LOG.info("STEP6: Go to 'Shared Files', from toolbar and verify the displayed files");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(fileC8770), fileC8770 + " displayed in 'Shared Files'. List of 'Shared Files' documents=" + sharedFilesPage.getFilesList().toString());
    }

    @TestRail (id = "C8772")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerMoveContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.MOVE_TO), "Move to... is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderC8761, ItemActions.MOVE_TO), "Move to... is not available.");
    }

    @TestRail (id = "C8774")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerDeleteContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.DELETE_DOCUMENT), "Delete Document is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderC8761, ItemActions.DELETE_FOLDER), "Delete Folder is not available.");
    }

    @TestRail (id = "C8776")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerManagePermissionsForContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.MANAGE_PERMISSIONS), "Manage Permissions is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderC8761, ItemActions.MANAGE_PERMISSIONS), "Manage Permissions is not available.");
    }

    @TestRail (id = "C8778")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerManageAspectsForContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8761, ItemActions.MANAGE_ASPECTS), "Manage Aspects is not available.");
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderC8761, ItemActions.MANAGE_ASPECTS), "Manage Aspects is not available.");
    }

    @TestRail (id = "C8780")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerChangeTypeForContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: On to Document Library page click on 'View Details' option for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderC8761, ItemActions.VIEW_DETAILS, documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");
        LOG.info("Step 2: Verify the user does not have access to change the type.");
        Assert.assertFalse(documentDetailsPage.isActionAvailable("Change Type"), "Change Type action is not available.");
    }

    @TestRail (id = "C8782")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerAddCommentForContent()
    {
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: Verify the user does not have access to add comments to content.");
        Assert.assertFalse(documentLibraryPage.isCommentButtonDisplayed(fileC8761), "Comment button is not available.");
        Assert.assertFalse(documentLibraryPage.isCommentButtonDisplayed(folderC8761), "Comment button is not available.");
    }

    @TestRail (id = "C8784")
    @Test (groups = { TestGroup.SANITY, "user-roles" })
    public void consumerEditAndDeleteCommentForContent()
    {
        contentAction.addComment(adminUser, adminPassword, siteName, fileC8784, comment);
        contentAction.addComment(adminUser, adminPassword, siteName, folderC8784, comment);
        setupAuthenticatedSession(user, password);
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        LOG.info("Step 1: On to Document Library page click on 'View Details' option for the folder.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderC8784, ItemActions.VIEW_DETAILS, documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");
        LOG.info("Step 2: Verify the user does not have access to edit the comment.");
        Assert.assertFalse(documentDetailsPage.isEditButtonDisplayedForComment(comment), "Edit comment action is not available.");
        Assert.assertFalse(documentDetailsPage.isDeleteButtonDisplayedForComment(comment), "Edit comment action is not available.");
    }

    @TestRail (id = "C8865")
    @Test (groups = { TestGroup.SANITY, "user-roles", "tobefixed" })
    public void consumerPermissionRetention()
    {
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user8865, password);
        documentLibraryPage.navigate(site8865);
//        ((RemoteWebDriver)(documentLibraryPage.getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        uploadContent.uploadContent(filePath8865);
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8865collaborator), String.format("File %s was not uploaded", fileC8865collaborator));
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(adminUser, adminPassword);
        LOG.info("Step 1: Change the current role to 'Consumer'");
        siteUsersPage.navigate(site8865);
        assertTrue(siteUsersPage.isASiteMember(user8865 + " " + user8865));
        assertEquals(siteUsersPage.getRole(user8865), "Collaborator ▾", user8865 + " has role=");
        siteUsersPage.changeRoleForMember("Consumer", user8865);
        assertEquals(siteUsersPage.getRole(user8865), "Consumer ▾", user8865 + " has role=");
        LOG.info("Step 2: Logout and login with testUser credentials.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user8865, password);
        LOG.info("Step 3: Navigate to Document Library page.");
        documentLibraryPage.navigate(site8865);
        getBrowser().waitInSeconds(4);
        Assert.assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(fileC8865consumer, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertTrue(documentLibraryPage.isActionAvailableForLibraryItem(fileC8865collaborator, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user, password);
    }
}
