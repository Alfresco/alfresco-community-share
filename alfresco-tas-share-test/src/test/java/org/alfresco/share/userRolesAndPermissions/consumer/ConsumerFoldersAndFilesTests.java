package org.alfresco.share.userRolesAndPermissions.consumer;

import static org.alfresco.common.Utils.testDataFolder;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.po.share.site.members.SiteUsersPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class ConsumerFoldersAndFilesTests extends BaseTest
{
    UploadContent uploadContent;

    DocumentLibraryPage documentLibraryPage;

    SharedFilesPage sharedFilesPage;

    DocumentDetailsPage documentDetailsPage;

    SocialFeatures social;

    CopyMoveUnzipToDialog copyMoveToDialog;

    SiteUsersPage siteUsersPage;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ContentActions contentAction;

    String comment = String.format("Comment%s", RandomData.getRandomAlphanumeric());
    private String fileC8761 = "C8761 test file";
    private String fileC8762 = "C8762 test file";
    private String fileC8763 = "C8763 test file";
    private String fileC8770 = "C8770 test file";
    private String fileC8784 = "C8784 test file";
    private String fileC8865consumer = "C8865consumer test file.txt";
    private String fileC8865collaborator = "C8865collaborator test file.txt";
    String filePath8865 = testDataFolder + fileC8865collaborator;
    private String testContent = "testContent";
    private String folderC8761 = "C8761 folder";
    private String folderC8784 = "C8784 folder";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user8865 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site8865 = new ThreadLocal<>();

    @BeforeMethod (alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Precondition: Any Test user is created");
        user8865.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        site8865.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(),getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteConsumer");
        userService.createSiteMember(getAdminUser().getUsername(),getAdminUser().getPassword(), user8865.get().getUsername(), site8865.get().getId(), "SiteCollaborator");
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8761, testContent);
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8762, testContent);
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8763, testContent);
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8770, testContent);
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8784, testContent);
        contentService.createDocument(getAdminUser().getUsername(),getAdminUser().getPassword(), site8865.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, fileC8865consumer, testContent);
        contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderC8761, siteName.get().getId());
        contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderC8784, siteName.get().getId());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        social = new SocialFeatures(webDriver);
        copyMoveToDialog = new CopyMoveUnzipToDialog(webDriver);
        sharedFilesPage = new SharedFilesPage(webDriver);
        uploadContent = new UploadContent(webDriver);
        siteUsersPage = new SiteUsersPage(webDriver);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user8865.get().getUsername());

        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(site8865.get());
        deleteUsersIfNotNull(user.get());
        deleteUsersIfNotNull(user8865.get());

    }

    @TestRail (id = "C8761")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerViewFolderItemDetailsPage()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: On to Document Library page verify the user has access to View the folder details page");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(folderC8761, ItemActions.VIEW_DETAILS);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");

        log.info("Step 2: Verify the user has access to View the file details page");
        documentDetailsPage.clickDocumentsLink();
        documentLibraryPage.clickOnFile(fileC8761);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "User is not on the Document Details page");
    }

    @TestRail (id = "C8762")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerLikeUnlikeFile()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Hover over the file Like link.");
        Assert.assertTrue(documentLibraryPage.isLikeButtonDisplayed(fileC8762), "Documents link is not present");
        Assert.assertEquals(social.getLikeButtonMessage(fileC8762), "Like this document", "Like Button message is not correct");
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 0, "The number of likes is not correct");

        log.info("Step 2: Click on the Like button");
        social.clickLikeButton(fileC8762);
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 1, "The number of likes is not correct");
        Assert.assertTrue(social.isLikeButtonEnabled(fileC8762), "Like button is not enabled");

        log.info("Step 3: Hover over the file Like link.");
        Assert.assertEquals(social.getLikeButtonEnabledText(fileC8762), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 1, "The number of likes is not correct");

        log.info("Step 4: Click on Unlike link.");
        social.clickUnlike(fileC8762);
        Assert.assertEquals(social.getNumberOfLikes(fileC8762), 0, "The number of likes is not correct");
        Assert.assertEquals(social.getLikeButtonMessage(fileC8762), "Like this document", "Like Button message is not correct");
    }

    @TestRail (id = "C8763")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerFavoriteUnfavoriteFile()
    {
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("STEP 1: Check the favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");

        log.info("STEP 2: Navigate to site1 Document Library");
        documentLibraryPage.clickFolderFromExplorerPanel("Documents");

        log.info("STEP 3: Hover over the file 'Favorite' link");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileC8763), "Add document to favorites", "The text 'Add document to favorites' is displayed");

        log.info("STEP 4: Click on the 'Favorite' link");
        documentLibraryPage.clickFavoriteLink(fileC8763);
        assertTrue(documentLibraryPage.isFileFavorite(fileC8763), "The gray star and text 'Favorite' are replaced by a golden star");

        log.info("STEP 5: Navigate to 'My Favorites' and check favorite items list");
        documentLibraryPage.clickDocumentsFilterOption(DocumentLibraryPage.DocumentsFilters.Favorites.title);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8763), "Document is displayed in My favorites list!");
    }

    @TestRail (id = "C8765")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerEditPropertiesForContent() {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to rename file and folder");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.EDIT_IN_ALFRESCO), "Edit in Alfresco is not available.");
        Assert.assertFalse(documentLibraryPage.isRenameIconDisplayed(fileC8761), "Rename icon is not displayed.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(folderC8761, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertFalse(documentLibraryPage.isRenameIconDisplayed(folderC8761), "Rename icon is not displayed.");
    }

    @TestRail (id = "C8770")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerCopyContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");
        log.info("STEP1: Hover over the file. STEP2: Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(fileC8770, ItemActions.COPY_TO);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + fileC8770 + " to...", "Displayed pop up");

        log.info("STEP3: Set the destination to 'Shared Files'");
        copyMoveToDialog.selectSharedFilesDestination();

        log.info("STEP4: Click 'Copy' button");
        copyMoveToDialog.clickCopyToButton();

        log.info("STEP5: Verify displayed files from Documents");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8770), fileC8770 + " displayed in 'Documents'");

        log.info("STEP6: Go to 'Shared Files', from toolbar and verify the displayed files");
        sharedFilesPage.navigate();
        assertEquals(sharedFilesPage.getPageTitle(), "Alfresco » Shared Files", "Displayed page=");
        assertTrue(sharedFilesPage.isContentNameDisplayed(fileC8770), fileC8770 + " displayed in 'Shared Files'. List of 'Shared Files' documents=" + sharedFilesPage.getFilesList().toString());
    }

    @TestRail (id = "C8772")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerMoveContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.MOVE_TO), "Move to... is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(folderC8761, ItemActions.MOVE_TO), "Move to... is not available.");
    }

    @TestRail (id = "C8774")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerDeleteContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.DELETE_DOCUMENT), "Delete Document is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(folderC8761, ItemActions.DELETE_FOLDER), "Delete Folder is not available.");
    }

    @TestRail (id = "C8776")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerManagePermissionsForContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.MANAGE_PERMISSIONS), "Manage Permissions is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(folderC8761, ItemActions.MANAGE_PERMISSIONS), "Manage Permissions is not available.");
    }

    @TestRail (id = "C8778")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerManageAspectsForContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to Move the file and folder");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8761, ItemActions.MANAGE_ASPECTS), "Manage Aspects is not available.");
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(folderC8761, ItemActions.MANAGE_ASPECTS), "Manage Aspects is not available.");
    }

    @TestRail (id = "C8780")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerChangeTypeForContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: On to Document Library page click on 'View Details' option for the folder.");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(folderC8761, ItemActions.VIEW_DETAILS);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");

        log.info("Step 2: Verify the user does not have access to change the type.");
        Assert.assertFalse(documentDetailsPage.isActionAvailable("Change Type"), "Change Type action is not available.");
    }

    @TestRail (id = "C8782")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void consumerAddCommentForContent()
    {
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: Verify the user does not have access to add comments to content.");
        Assert.assertFalse(documentLibraryPage.isCommentButtonDisplayed(fileC8761), "Comment button is not available.");
        Assert.assertFalse(documentLibraryPage.isCommentButtonDisplayed(folderC8761), "Comment button is not available.");
    }

    @TestRail (id = "C8784")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void consumerEditAndDeleteCommentForContent()
    {
        contentAction.addComment(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), fileC8784, comment);
        contentAction.addComment(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderC8784, comment);
        authenticateUsingLoginPage(user.get());
        documentLibraryPage.navigate(siteName.get());
        Assert.assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "User is not on the Document Library page");

        log.info("Step 1: On to Document Library page click on 'View Details' option for the folder.");
        documentLibraryPage.selectItemActionFormFirstThreeAvailableOptions(folderC8784, ItemActions.VIEW_DETAILS);
        Assert.assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "User is not on the Folder Details page");

        log.info("Step 2: Verify the user does not have access to edit the comment.");
        Assert.assertFalse(documentDetailsPage.isEditButtonDisplayedForComment(comment), "Edit comment action is not available.");
        Assert.assertFalse(documentDetailsPage.isDeleteButtonDisplayedForComment(comment), "Edit comment action is not available.");
    }

    @TestRail (id = "C8865")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void consumerPermissionRetention()
    {
        authenticateUsingLoginPage(user8865.get());

        documentLibraryPage.navigate(site8865.get());
        uploadContent.uploadContent(filePath8865);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(fileC8865collaborator), String.format("File %s was not uploaded", fileC8865collaborator));

        authenticateUsingLoginPage(getAdminUser());

        log.info("Step 1: Change the current role to 'Consumer'");
        siteUsersPage.navigate(site8865.get());
        siteUsersPage.assertSiteMemberNameEqualsTo(user8865.get().getFirstName() + " " + user8865.get().getLastName());
        assertEquals(siteUsersPage.getRole(user8865.get().getUsername()), "Collaborator ▾", user8865 + " has role=");
        siteUsersPage.changeRoleForMember("Consumer", user8865.get().getUsername());
        assertEquals(siteUsersPage.getRole(user8865.get().getUsername()), "Consumer ▾", user8865 + " has role=");

        log.info("Step 2: Logout and login with testUser credentials.");
        authenticateUsingLoginPage(user8865.get());

        log.info("Step 3: Navigate to Document Library page.");
        documentLibraryPage.navigate(site8865.get());
        Assert.assertFalse(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8865consumer, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");
        Assert.assertTrue(documentLibraryPage.checkActionAvailableForLibraryItem(fileC8865collaborator, ItemActions.EDIT_PROPERTIES), "Edit Properties is not available.");

        authenticateUsingLoginPage(user.get());

    }
}
