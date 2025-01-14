package org.alfresco.share.userRolesAndPermissions.collaborator;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.UserService;

import org.alfresco.po.share.DeleteDialog;

import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

@Slf4j
public class CollaboratorFoldersAndFilesTests extends BaseTest {
    //@Autowired
    DocumentLibraryPage documentLibraryPage;

    //@Autowired
    DocumentDetailsPage documentDetailsPage;

    //@Autowired
    DeleteDialog deleteDialog;

    //@Autowired
    AspectsForm aspectsForm;

    //@Autowired
    SocialFeatures social;

    //@Autowired
    EditPropertiesDialog editFilePropertiesDialog;

    //@Autowired
    SelectDialog selectDialog;

    //@Autowired
    CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    //@Autowired
    ManagePermissionsPage managePermissionsPage;
    @Autowired
    protected UserService userService;
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName1 = new ThreadLocal<>();
    private String folderName;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName1 is created");
        siteName1.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName.get().getId(), "SiteCollaborator");
        userService.createSiteMember(getAdminUser().getUsername(), getAdminUser().getPassword(), user.get().getUsername(), siteName1.get().getId(), "SiteCollaborator");

        documentLibraryPage = new DocumentLibraryPage(webDriver);
        social = new SocialFeatures(webDriver);
        editFilePropertiesDialog = new EditPropertiesDialog(webDriver);
        selectDialog = new SelectDialog(webDriver);
        copyMoveUnzipToDialog = new CopyMoveUnzipToDialog(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        managePermissionsPage = new ManagePermissionsPage(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        social = new SocialFeatures(webDriver);
        documentDetailsPage = new DocumentDetailsPage(webDriver);

        authenticateUsingLoginPage(user.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteSitesIfNotNull(siteName1.get());
        deleteUsersIfNotNull(user.get());
    }

    @TestRail(id = "C8814")
    @Test(groups = {TestGroup.SANITY, TestGroup.USER_ROLES})
    public void collaboratorLikeUnlike() {
        String deletePath = String.format("Sites/%s/documentLibrary", siteName.get().getTitle());
        String testContentC8814 = String.format("FileC8814%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions.");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, testContentC8814, "test content");
        documentLibraryPage.navigate(siteName.get());
        log.info("Step 1: Hover over the testContent 'Like' button.");
        assertTrue(documentLibraryPage.isLikeButtonDisplayed(testContentC8814), "Documents link is not present");
        assertEquals(social.getLikeButtonMessage(testContentC8814), "Like this document", "Like Button message=");
        assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");
        log.info("Step 2: Click on the content's 'Like' button.");
        social.clickLikeButton(testContentC8814);
        assertEquals(social.getNumberOfLikes(testContentC8814), 1, testContentC8814 + "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(testContentC8814), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(testContentC8814), "Unlike", "Like Button message=");
        log.info("Step 3: Hover over the content's 'Like' button.");
        assertEquals(social.getLikeButtonEnabledText(testContentC8814), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(testContentC8814), 1, "The number of likes=");
        log.info("Step 4: Click on the content's 'Unlike' button.");
        social.clickUnlike(testContentC8814);
        assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");
        contentService.deleteContentByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), String.format("%s/%s", deletePath, testContentC8814));
    }

    @TestRail (id = "C8815")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES, TestGroup.INTEGRATION })
    public void collaboratorFavoriteUnfavorite()
    {
        String testContentC8815 = String.format("FileC8815%s", RandomData.getRandomAlphanumeric());
        log.info("Preconditions.");
        contentService.createDocument(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), DocumentType.TEXT_PLAIN, testContentC8815, "test content");
        documentLibraryPage.navigate(siteName.get());
        log.info("Step 1: Hover over the content's 'Favorite' button.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Add document to favorites", "The text 'Add document to favorites' is displayed");
        log.info("Step 2: Click on the 'Favorite' button.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertTrue(documentLibraryPage.isFileFavorite(testContentC8815), "Step 2: The file is not favorited.");
        log.info("Step 3: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testContentC8815), "Document is displayed in My favorites list!");
        log.info("Step 4: Hover over the content's yellow star.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Remove document from favorites", "'Remove document from favorites' is not displayed");
        log.info("Step 5: Click the yellow star.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertFalse(documentLibraryPage.isFileFavorite(testContentC8815), "The file is still 'Favorite'");
        log.info("Step 6: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");
        String deletePath = String.format("Sites/%s/documentLibrary", siteName.get().getTitle());
        contentService.deleteContentByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), String.format("%s/%s", deletePath, testContentC8815));
    }

     @TestRail (id = "C8818")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorEditBasicDetailsBySelf()
     {
     folderName = String.format("FolderC8818%s", RandomData.getRandomAlphanumeric());
     String editTag = String.format("editTag%s", RandomData.getRandomAlphanumeric());
     String editedName = String.format("editedName%s", RandomData.getRandomAlphanumeric());
     String editedTitle = String.format("editedTitle%s", RandomData.getRandomAlphanumeric());
     String editedDescription = String.format("editedDescription%s", RandomData.getRandomAlphanumeric());
     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     log.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
     assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");
     log.info("Step 2: In the 'Name' field enter a name for the folder.");
     editFilePropertiesDialog.setName(editedName);
     log.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle').");
     editFilePropertiesDialog.setTitle(editedTitle);
     log.info("Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
     editFilePropertiesDialog.setDescription(editedDescription);
     log.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
     editFilePropertiesDialog.clickSelectTags();
     log.info("Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
     selectDialog.typeTag(editTag);
     selectDialog.clickCreateNewIcon();
     selectDialog.clickOk();
     log.info("Step 7: Click 'Save' button.");
     editFilePropertiesDialog.clickSave();
     assertTrue(documentLibraryPage.isContentNameDisplayed(editedName), "Edited document name is not found");
     assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle), " The title of edited document is not correct.");
     assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription, "The description of edited document is not correct");
     contentService.deleteFolder(getAdminUser().getUsername(), getAdminUser().getPassword(), siteName.get().getId(), editedName);
     }

     @TestRail (id = "C8819")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorEditBasicDetailsByOthers()
     {
     folderName = String.format("FolderC8819%s", RandomData.getRandomAlphanumeric());
     String editTag2 = String.format("editTag2%s", RandomData.getRandomAlphanumeric());
     String editedName = String.format("editedName%s", RandomData.getRandomAlphanumeric());
     String editedTitle = String.format("editedTitle%s", RandomData.getRandomAlphanumeric());
     String editedDescription = String.format("editedDescription%s", RandomData.getRandomAlphanumeric());
     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     log.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.EDIT_PROPERTIES);
     assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not sdisplayed");
     log.info("Step 2: In the 'Name' field enter a name for the folder.");
     editFilePropertiesDialog.setName(editedName);
     log.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle1').");
     editFilePropertiesDialog.setTitle(editedTitle);
     log.info("Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
     editFilePropertiesDialog.setDescription(editedDescription);
     log.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
     editFilePropertiesDialog.clickSelectTags();
     log.info("Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
     selectDialog.typeTag(editTag2);
     selectDialog.clickCreateNewIcon();
     selectDialog.clickOk();
     log.info("Step 7: Click 'Save' button.");
     editFilePropertiesDialog.clickSave();
     assertTrue(documentLibraryPage.isContentNameDisplayed(editedName), "Edited document name is not found");
     assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle), " The title of edited document is not correct.");
     assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription, "The description of edited document is not correct");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), editedName);
     }

     @TestRail (id = "C8816")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorRenameBySelf()
     {
     folderName = String.format("FolderC8816%s", RandomData.getRandomAlphanumeric());
     String newFolderName = "newFolderNameC8816";
     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     log.info("Step 1: Hover over the content name.");
     assertTrue(documentLibraryPage.isRenameIconDisplayed(folderName), "'Rename' icon is not displayed.");
     log.info("Step 2: Click on 'Rename' icon.");
     documentLibraryPage.clickRenameIcon(folderName);
     assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
     assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");
     log.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
     documentLibraryPage.typeContentName(newFolderName);
     documentLibraryPage.clickButtonFromRenameContent("Save");
     assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), folderName + " name updated to: " + newFolderName);
     assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), newFolderName);
     }

     @TestRail (id = "C8817")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorRenameByOthers()
     {
     folderName = String.format("FolderC8817%s", RandomData.getRandomAlphanumeric());
     String newFolderName = "newFolderNameC8817";
     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get().getId());
     log.info("Step 1: Hover over the content name.");
     assertTrue(documentLibraryPage.isRenameIconDisplayed(folderName), "'Rename' icon is not displayed.");
     log.info("Step 2: Click on 'Rename' icon.");
     documentLibraryPage.clickRenameIcon(folderName);
     assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
     assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");
     log.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
     documentLibraryPage.typeContentName(newFolderName);
     documentLibraryPage.clickButtonFromRenameContent("Save");
     assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), folderName + " name updated to: " + newFolderName);
     assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), newFolderName);
     }

     @TestRail (id = "C8823")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorMoveBySelf() throws InterruptedException {
     folderName = String.format("Folder1C8823%s", RandomData.getRandomAlphanumeric());
     String folderName2 = String.format("Folder2C8823%s", RandomData.getRandomAlphanumeric());
     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName1.get().getId());
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName2, siteName1.get().getId());
     documentLibraryPage.navigate(siteName1.get());
     log.info("Step 1: Hover over 'testFolder3', Click 'More...' link, Click 'Move to...''.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.MOVE_TO);
     assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + folderName + " to...", "Displayed pop up");
     log.info("Step 2: Set the destination to 'All Sites'.");
     copyMoveUnzipToDialog.selectAllSitesDestination();
     //assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName1), siteName1 + " displayed in 'Site' section");
     log.info("Step 3: Select your site name.");
     copyMoveUnzipToDialog.selectSite(new SiteModel(siteName1.get().getId()));
     ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", folderName, folderName2));
     //assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(), "Step 5: Selected path is not correct.");
     log.info("Step 4: Select 'testFolder4' for the path.");
     copyMoveUnzipToDialog.selectFolder(new FolderModel(folderName2));
     log.info("Step 5: Click 'Move' button. Verify the displayed folders.");
     copyMoveUnzipToDialog.clickMoveButton();
     assertFalse(documentLibraryPage.isFileNameDisplayed(folderName), folderName + " displayed in Documents");
     log.info("Step 6: Open the 'testFolder4' created in preconditions and verify displayed folders.");
     documentLibraryPage.clickOnFolderName(folderName2);
     assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Displayed folders in " + folderName2);
     }

     @TestRail (id = "C8824")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorMoveByOthers() {
     folderName = String.format("FolderC8824%s", RandomData.getRandomAlphanumeric());
     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     log.info("Step 1: Hover over 'testFolder1'.");
     log.info("Step 2: Click 'More...' link. The Move to option is not available.");
     List<String> notExpectedActions = Arrays.asList("Move to...");
     assertTrue(documentLibraryPage.areActionsNotAvailableForLibraryItem(folderName, notExpectedActions),"Move to..." + " option is displayed for " + folderName);
     }

     @TestRail (id = "C8822")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorCopyTo()
     {
     folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());
     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     log.info("Step 1: Hover over the created folder and click 'Copy to...'.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.COPY_TO);
     assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderName + " to...", "Displayed pop up");
     log.info("Step 2: Set the destination to 'testFolder'.");
     copyMoveUnzipToDialog.selectAllSitesDestination();
     //assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName), siteName + " displayed in 'Site' section");
     copyMoveUnzipToDialog.selectSite(new SiteModel(siteName.get().getId()));
     copyMoveUnzipToDialog.selectFolder(new FolderModel(folderName));
     log.info("Step 3: Click 'Copy' button");
     copyMoveUnzipToDialog.clickCopyToButton();
     assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");
     log.info("Step 4: Verify displayed folders from Documents.");
     documentLibraryPage.navigate(siteName.get());
     assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents");
     log.info("Step 5: Open the 'testfolder2' created in preconditions and verify displayed folders.");
     documentLibraryPage.clickOnFolderName(folderName);
     assertTrue(documentLibraryPage.getFoldersList().toString().contains(folderName), "Displayed folders in " + folderName);
     }

     @TestRail (id = "C8822")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorDeleteBySelf()
     {
     folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());

     log.info("Step 1: Mouse Over and click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.DELETE_FOLDER);
     assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
     assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderName));

     log.info("Step 2: Click 'Delete' button");
     deleteDialog.confirmDeletion();
     assertFalse(documentLibraryPage.isFileNameDisplayed(folderName), "Documents item list is refreshed and is empty");
     assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(folderName),
     "'DelFolder' is not visible in 'Library' section of the browsing pane.");
     }

     @TestRail (id = "C8822")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorDeleteByOthers()
     {
     folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());

     log.info("Step 1: Hover 'DelFolder' name from the content item list.");
     log.info("Step 2: Click on 'More...' link. The Delete folder option is not available.");
     List<String> notExpectedActions = Arrays.asList("Delete Folder");
     assertTrue(documentLibraryPage.areActionsNotAvailableForLibraryItem(folderName, notExpectedActions),"Delete Folder" + " option is displayed for " + folderName);
     }

     @TestRail (id = "C8827")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorManagePermissionsBySelf()
     {
     folderName = String.format("FolderC8827%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get().getId());

     log.info("Step 1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_PERMISSIONS);

     log.info("Step 2: Make some changes. Add User/Group button. Search for testUser. Click Add Button.");
     managePermissionsPage.searchAndAddUserAndGroup(user.get().getUsername());

     log.info("Step 3: Click 'Save' button");
     managePermissionsPage.clickSave();

     log.info("Step 4: Click 'More' menu, 'Manage Permissions' options.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_PERMISSIONS);
     assertTrue(managePermissionsPage.isPermissionAddedForUser(user.get().getUsername()), String.format("User [%s] is not added in permissions.", user));
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8828")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorManagePermissionsByOthers() {
     folderName = String.format("FolderC8828%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());

        log.info("Step 1: Mouse over and click on 'More...' button. 'Manage Permissions' option from 'More' menu must not be displayed.");
         List<String> notExpectedActions = Arrays.asList("Manage Permissions");
         assertTrue(documentLibraryPage.areActionsNotAvailableForLibraryItem(folderName, notExpectedActions),"Manage Permissions" + " option is displayed for " + folderName);
         contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
    }

     @TestRail (id = "C8829")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorManageAspectsBySelf() {
     folderName = String.format("FolderC8829%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());

     log.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);

     log.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
     aspectsForm.addAspect("Classifiable");
     assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");

     log.info("Step 3: Click on 'Apply changes' button.");
     aspectsForm.clickApplyChangesButton();
     //Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8830")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorManageAspectsByOthers()
     {
     folderName = String.format("FolderC8830%s", RandomData.getRandomAlphanumeric());

     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());

     log.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.MANAGE_ASPECTS);

     log.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
     aspectsForm.addAspect("Classifiable");
     assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");

     log.info("Step 3: Click on 'Apply changes' button.");
     aspectsForm.clickApplyChangesButton();
     //Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8834")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorAddComment()
     {
     folderName = String.format("FolderC8834%s", RandomData.getRandomAlphanumeric());
     String comment = "Test comment for C8834";

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get().getId());

     log.info("Step 1: Hover over a document and press \"Comment\"");
     social.clickCommentLink(folderName);
     assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

     log.info("Step 2: In the 'Comments' area of 'Folder Details' page write a comment and press 'Add Comment' button");
     documentDetailsPage.addComment(comment);
     assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment content is not as expected.");

     log.info("Step 3: Go back to 'Document Library' page");
     documentLibraryPage.navigate();
     assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8835")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorEditCommentBySelf() {
     folderName = String.format("FolderC8835%s", RandomData.getRandomAlphanumeric());
     String comment = "Test comment for C8835";
     String editedComment = "Test comment edited for C8835";

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     social.clickCommentLink(folderName);
     documentDetailsPage.addComment(comment);
     documentLibraryPage.navigate();

     log.info("Step 1: Hover over a document and click on 'View Details' button.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);

     log.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
     assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment), "Edit button is not displayed for comment");
     documentDetailsPage.clickOnEditComment(comment);
     assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

     log.info("Step 3: Edit comment text and click on Save.");
     documentDetailsPage.editComment(editedComment);
     documentDetailsPage.clickOnSaveButtonEditComment();
     documentLibraryPage.navigate();
     assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);
     assertEquals(documentDetailsPage.getCommentContent(editedComment), editedComment, "Edited comment text is not correct");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8836")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorEditCommentByOthers()
     {
     folderName = String.format("FolderC8836%s", RandomData.getRandomAlphanumeric());
     String comment1 = "Test comment for C8836";
     String editedComment1 = "Test comment for C8836. Edited.";

     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     social.clickCommentLink(folderName);
     documentDetailsPage.addComment(comment1);
     documentLibraryPage.navigate();

     log.info("Step 1: Hover over a document and click on 'View Details' button.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);

     log.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
     assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment1), "Edit button is not displayed for comment");
     documentDetailsPage.clickOnEditComment(comment1);
     assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

     log.info("Step 3: Edit comment text and click on Save.");
     documentDetailsPage.editComment(editedComment1);
     documentDetailsPage.clickOnSaveButtonEditComment();
     documentLibraryPage.navigate();
     assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);
     assertEquals(documentDetailsPage.getCommentContent(editedComment1), editedComment1, "Edited comment text is not correct");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8837")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorDeleteCommentBySelf()
     {
     folderName = String.format("FolderC8837%s", RandomData.getRandomAlphanumeric());
     String comment2 = "Test comment for C8837";

     log.info("Preconditions.");
     contentService.createFolder(user.get().getUsername(), user.get().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get().getId());
     social.clickCommentLink(folderName);
     documentDetailsPage.addComment(comment2);
     documentLibraryPage.navigate();

     log.info("Step 1: Hover over a document and click on 'View Details' button.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);

     log.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
     documentDetailsPage.clickDeleteComment(comment2);
     assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

     log.info("Step 3: Click 'Delete' button.");
     documentDetailsPage.clickDeleteOnDeleteComment();
     assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

     @TestRail (id = "C8838")
     @Test (groups = { TestGroup.SANITY, TestGroup.USER_ROLES })
     public void collaboratorDeleteCommentByOthers()
     {
     folderName = String.format("FolderC8838%s", RandomData.getRandomAlphanumeric());
     String comment3 = "Test comment for C8838";

     log.info("Preconditions.");
     contentService.createFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), folderName, siteName.get().getId());
     documentLibraryPage.navigate(siteName.get());
     social.clickCommentLink(folderName);
     documentDetailsPage.addComment(comment3);
     documentLibraryPage.navigate();

     log.info("Step 1: Hover over a document and click on 'View Details' button.");
     documentLibraryPage.selectItemAction(folderName, ItemActions.VIEW_DETAILS);

     log.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
     documentDetailsPage.clickDeleteComment(comment3);
     assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

     log.info("Step 3: Click 'Delete' button.");
     documentDetailsPage.clickDeleteOnDeleteComment();
     assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");
     contentService.deleteFolder(getAdminUser().getUsername(),getAdminUser().getPassword(), siteName.get().getId(), folderName);
     }

}