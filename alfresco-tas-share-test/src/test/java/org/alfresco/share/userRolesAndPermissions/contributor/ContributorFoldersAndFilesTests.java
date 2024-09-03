package org.alfresco.share.userRolesAndPermissions.contributor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.ContentActions;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.DocumentsFilters;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ContributorFoldersAndFilesTests extends BaseTest
{
    @Autowired
    UserService userService;
    @Autowired
    ContentActions contentAction;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName1 = new ThreadLocal<>();
    private final String adminFile = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String adminFolder = String.format("AdminFolder%s", RandomData.getRandomAlphanumeric());
    private DeleteDialog deleteDialog;
    private DocumentLibraryPage documentLibraryPage;
    private SocialFeatures socialFeatures;
    private EditPropertiesDialog editPropertiesDialog;
    private SelectDialog selectDialog;
    private CopyMoveUnzipToDialog copyMoveToDialog;
    private ManagePermissionsPage managePermissionsPage;
    private AspectsForm aspectsForm;
    private DocumentDetailsPage documentDetailsPage;
    private ChangeContentTypeDialog changeContentTypeDialog;
    private EditPropertiesPage editPropertiesPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName1 is created");
        siteName1.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteContributor");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName1.get().getId(), "SiteContributor");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, adminFile, "Some content");
        contentService.createFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), adminFolder, siteName.get().getId());

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        selectDialog = new SelectDialog(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        managePermissionsPage = new ManagePermissionsPage(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);
        socialFeatures = new SocialFeatures(webDriver);
        editPropertiesDialog = new EditPropertiesDialog(webDriver);
        copyMoveToDialog = new CopyMoveUnzipToDialog(webDriver);
        changeContentTypeDialog = new ChangeContentTypeDialog(webDriver);
        editPropertiesPage = new EditPropertiesPage(webDriver);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(siteName1.get());
        deleteUsersIfNotNull(user.get());
    }


    @TestRail (id = "C8787")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void likeAndUnlike()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over 'Like' button for the test file");
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(fileName), "Like this document", "Correct message for 'Like' button");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 0, "Number of likes = 0");
        log.info("Step2: Click on the content's 'Like' button.");
        socialFeatures.clickLikeButton(fileName);
        Assert.assertTrue(socialFeatures.isLikeButtonEnabled(fileName), "Like button enabled");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 1, "Number of likes is 1");
        log.info("Step3: Hover over the content's 'Like' button.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(fileName), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 1, "Number of likes = 1");
        log.info("Step4: Click on the content's 'Unlike' button.");
        socialFeatures.clickUnlike(fileName);
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(fileName), "Like this document", "Correct message for 'Like' button");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 0, "Number of likes = 0");
    }

    @TestRail (id = "C8788")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void favoriteAndUnfavorite()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over 'Favorite' button for the test file");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileName), "Add document to favorites", "The text 'Add document to favorites'  displayed");
        log.info("Step2: Click on the 'Favorite' button");
        documentLibraryPage.clickFavoriteLink(fileName);
        assertTrue(documentLibraryPage.isFileFavorite(fileName), "The gray star and text 'Favorite' replaced by a golden star");
        log.info("Step3: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents displayed.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "File displayed in My favorites list!");
        log.info("Step4: Hover over the content's yellow star");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileName), "Remove document from favorites",
            "The star is replaced by a X button and the text 'Remove document from favorites' displayed");
    }

    @TestRail (id = "C8789")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void renameItemAddedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newFileName = "newFileName";
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test file");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(fileName), "'Rename' icon displayed.");
        log.info("Step2: Click on 'Rename' icon");
        documentLibraryPage.clickRenameIcon(fileName);
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");
        log.info("Step3: Fill in the input field with a new name and click 'Save' button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName), fileName + " name updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");
    }

    @TestRail (id = "C8790")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void renameItemAddedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test file. Verify 'Rename' icon is missing.");
        assertFalse(documentLibraryPage.isRenameIconDisplayed(adminFile), "'Rename' icon displayed.");
    }

    @TestRail (id = "C8791")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editBasicDetailsCreatedBySelf()
    {
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the created folder and click 'Edit Properties' action.");
        documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");
        log.info("Step 2: In the 'Name' field enter a valid name");
        editPropertiesDialog.setName("FolderEditName");
        log.info("Step 3: In the 'Title' field enter a valid title");
        editPropertiesDialog.setTitle("FolderEditTitle");
        log.info("Step 4: In the 'Description' field enter a valid description");
        editPropertiesDialog.setDescription("FolderEditDescription");
        log.info("Step 5: Click the 'Select' button in the tags section");
        editPropertiesDialog.clickSelectTags();
        log.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag("edittag");
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        log.info("Step 7: Click 'Save' and verify that document details have been updated");
        editPropertiesDialog.clickSave();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("FolderEditName"), "Edited name displayed");
        Assert.assertEquals(documentLibraryPage.getItemTitle("FolderEditName"), "(FolderEditTitle)", "Correct title of edited item");
        Assert.assertEquals(documentLibraryPage.getItemDescription("FolderEditName"), "FolderEditDescription", "Correct description of edited item");
        Assert.assertEquals(documentLibraryPage.getTags("FolderEditName"), "[edittag]", "Correct tag of the edited item");
    }

    @TestRail (id = "C8792")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editBasicDetailsCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Edit Properties' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, ItemActions.EDIT_PROPERTIES), "Edit Properties action displayed");
    }

    @TestRail (id = "C8795")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void contributorCopy()
    {
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, siteName.get().getId());
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Steps1,2,3: Hover over the created folder. Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.selectConsumerItemAction(folderName, ItemActions.COPY_TO);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + folderName + " to...", "Displayed pop up");
        log.info("Step4: Set the destination to 'All sites'. Select a site.");
        copyMoveToDialog.selectAllSitesDestination();
        copyMoveToDialog.selectSite(new SiteModel(siteName1.get().getId()));
        log.info("Step5: Click 'Copy' button");
        copyMoveToDialog.clickCopyToButton();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog displayed");
        log.info("Step6: Verify displayed folder has been copied");
        documentLibraryPage.navigate(siteName1.get().getId());
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Displayed folders=");
    }

    @TestRail (id = "C8796")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void moveContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), folderName, siteName.get().getId());
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Steps1, 2,3: Hover over the file. Click 'More...' link. Click 'Move to...'");
        documentLibraryPage.selectItemAction(fileName, ItemActions.MOVE_TO);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Move " + fileName + " to...", "Displayed pop up");
        log.info("Step4: Set the destination to 'All Sites'");
        copyMoveToDialog.selectAllSitesDestination();
        log.info("Step5: Select 'site1'");
        copyMoveToDialog.selectSite(new SiteModel(siteName.get().getId()));
        log.info("Step6: Set the folder created in preconditions as path");
        copyMoveToDialog.selectFolder(new FolderModel(folderName));
        log.info("Step7: Click 'Move' button. Verify the displayed files");
        copyMoveToDialog.clickMoveButton();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog not displayed");
        log.info("Step8: Open the folder created in preconditions and verify displayed files");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "Displayed files in " + folderName);
    }

    @TestRail (id = "C8797")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void moveContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Move to...' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, ItemActions.MOVE_TO), "Move to... action displayed");
    }

    @TestRail (id = "C8798")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void deleteContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Steps1, 2: Hover over the file. Click 'More...' link. Click 'Delete Document' link");
        documentLibraryPage.selectItemAction(fileName, ItemActions.DELETE_DOCUMENT);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), fileName));
        log.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDeletion();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
    }

    @TestRail (id = "C8799")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void deleteContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Delete Document' action is missing.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, ItemActions.DELETE_DOCUMENT), "Delete action displayed");
    }

    @TestRail (id = "C8800")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void managePermissionsContentCreatedBySelf()
    {
        String folderName = "FolderName";
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_PERMISSIONS);
        log.info("Steps2: On the Manage Permissions page click on Add User/Group button and add permissions for a test user.");
        managePermissionsPage.searchAndAddUserAndGroup(user.get().getUsername());
        log.info("Steps3: Click 'Save' button");
        managePermissionsPage.clickSave();
        log.info("Step4: Return to Manage Permissions page for the file and check if permissions were added successfully.");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_PERMISSIONS);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(user.get().getUsername()), String.format("User [%s] is not added in permissions.", user));
    }

    @TestRail (id = "C8801")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void managePermissionsForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Manage Permissions' action is missing.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, ItemActions.MANAGE_PERMISSIONS), "Manage Permissions action displayed");
    }

    @TestRail (id = "C8802")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageAspectsForContentCreatedBySelf()
    {
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu");
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);
        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");
        log.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton();
        documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");
    }

    @TestRail (id = "C8803")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void manageAspectsForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Manage Aspects' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, ItemActions.MANAGE_ASPECTS), "Manage Aspects action displayed");
    }

    @TestRail (id = "C8804")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void changeTypeForContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the page test site, as Contributor user.");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Verify document's Properties list");
        documentLibraryPage.clickOnFile(fileName);
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator", "Created Date", "Modifier", "Modified Date"), "Displayed properties:");
        log.info("Step2: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");
        log.info("Step3: Select 'Article' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("Smart Folder Template");
        changeContentTypeDialog.clickOkButton();
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Auto Version - on update properties only", "Created Date", "Title", "Last thumbnail modifcation data", "Description", "Creator", "Name", "Locale", "Version Label", "Modifier",
            "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Author", "Encoding", "Size", "Mimetype"), "Displayed properties:");
        log.info("Step6: Click 'Edit Properties' option from 'Document Actions' section");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Auto Version - on update properties only", "Created Date", "Title", "Last thumbnail modifcation data", "Description", "Creator", "Name", "Content", "Locale", "Version Label", "Modifier",
            "Modified Date", "Auto Version", "Version Type", "Initial Version", "Last Accessed Date", "Author", "Encoding", "Size", "Mimetype"), "Displayed properties:");
    }

    @TestRail (id = "C8805")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void changeTypeForContentCreatedByOthers()
    {
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Go to document details page and verify 'Change Type' action is missing");
        documentLibraryPage.clickOnFile(adminFile);
        assertFalse(documentDetailsPage.isActionAvailable("Change Type"), "Change type action displayed");
    }

    @TestRail (id = "C8807")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void addComment()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");
        log.info("Step 2: Return to Document Library page and check that the comment counter for the file has increased");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(socialFeatures.getNumberOfComments(fileName), 1, "Increased number of comments");
    }

    @TestRail (id = "C8808")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editCommentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");
        log.info("Step2: Edit the comment for the file");
        documentDetailsPage.clickEditComment("commentAddedByContributor");
        documentDetailsPage.editComment("commentEditedByContributor");
        documentDetailsPage.clickOnSaveButtonEditComment();
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentEditedByContributor", "Comment edited successfully by contributor");
    }

    @TestRail (id = "C8809")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void editCommentCreatedByOthers()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Admin user; add a comment for the test file");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        contentAction.addComment(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), fileName, "commentAddedByAdmin");
        log.info("Step1:Login as Contributor and navigate to document details page for test file; verify the comment added by admin is displayed, without 'Edit' option");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByAdmin", "Comment added by admin is visible to Contributor user");
        Assert.assertFalse(documentDetailsPage.isEditButtonDisplayedForComment("commentAddedByAdmin"), "Edit option missing for Contributor user");
    }

    @TestRail (id = "C8810")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void deleteCommentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName.get().getId());
        log.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");
        log.info("Step2: Delete the comment for the file");
        documentDetailsPage.clickDeleteComment("commentAddedByContributor");
        documentDetailsPage.clickDeleteOnDeleteComment();
        log.info("Step3: Verify comment is successfully deleted");
        documentLibraryPage.navigate(siteName.get().getId());
        Assert.assertEquals(socialFeatures.getNumberOfComments(fileName), 0, "0 comments for the test file");
    }

    @TestRail (id = "C8811")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void deleteCommentCreatedByOthers()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions: Create test user, test site, test file. Navigate to Document Library for the test site, as Admin user; add a comment to the file.");
        contentService.createDocument(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, fileName, "FileContent");
        contentAction.addComment(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), fileName, "commentAddedByAdmin");
        log.info("Step1: Login as contributor user and navigate to Document Details page for the test site. Verify the comment added by admin is displayed, with no 'Delete' option.");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByAdmin", "Comment added by admin is visible to Contributor user");
        Assert.assertFalse(documentDetailsPage.isDeleteButtonDisplayedForComment("commentAddedByAdmin"), "Delete option missing for Contributor user");
    }

    @TestRail (id = "C8812")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
    public void viewItemDetailsPage()
    {
        log.info("Step1: Navigate to document library page and click on the created file. Verify the preview for the file is successfully displayed on the Document details page.");
        documentLibraryPage.navigate(siteName.get().getId());
        documentLibraryPage.clickOnFile(adminFile);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), "Some content", "File preview displayed");
    }

}
