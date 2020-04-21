package org.alfresco.share.userRolesAndPermissions.collaborator;

import static java.util.Arrays.asList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.DocumentsFilters;
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
 * Created by Rusu Andrei
 */

public class CollaboratorFoldersAndFilesTests extends ContextAwareWebTest
{
    @Autowired
    DocumentCommon documentCommon;

    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    DeleteDialog deleteDialog;

    @Autowired
    AspectsForm aspectsForm;

    @Autowired
    SocialFeatures social;

    @Autowired
    EditPropertiesDialog editFilePropertiesDialog;

    @Autowired
    SelectDialog selectDialog;

    @Autowired
    CopyMoveUnzipToDialog copyMoveUnzipToDialog;

    @Autowired
    DeleteDocumentOrFolderDialog deleteDialogFolder;

    @Autowired
    ManagePermissionsPage managePermissionsPage;

    private String user = String.format("Collaborator%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteC%s", RandomData.getRandomAlphanumeric());
    private final String deletePath = String.format("Sites/%s/documentLibrary", siteName);
    private String siteName1 = String.format("SiteC1%s", RandomData.getRandomAlphanumeric());
    private String folderName;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(adminUser, adminPassword, domain, siteName, "SiteC description", SiteService.Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, siteName1, "SiteC description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user, siteName1, "SiteCollaborator");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName1);
    }

    @TestRail (id = "C8814")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorLikeUnlike()
    {
        setupAuthenticatedSession(user, password);
        String testContentC8814 = String.format("FileC8814%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, testContentC8814, "test content");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over the testContent 'Like' button.");
        assertTrue(documentLibraryPage.isLikeButtonDisplayed(testContentC8814), "Documents link is not present");
        assertEquals(social.getLikeButtonMessage(testContentC8814), "Like this document", "Like Button message=");
        assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");
        LOG.info("Step 2: Click on the content's 'Like' button.");
        social.clickLikeButton(testContentC8814);
        assertEquals(social.getNumberOfLikes(testContentC8814), 1, testContentC8814 + "The number of likes=");
        assertTrue(social.isLikeButtonEnabled(testContentC8814), "Like button is enabled");
        assertEquals(social.getLikeButtonMessage(testContentC8814), "Unlike", "Like Button message=");
        LOG.info("Step 3: Hover over the content's 'Like' button.");
        assertEquals(social.getLikeButtonEnabledText(testContentC8814), "Unlike", "Unlike is displayed");
        assertEquals(social.getNumberOfLikes(testContentC8814), 1, "The number of likes=");
        LOG.info("Step 4: Click on the content's 'Unlike' button.");
        social.clickUnlike(testContentC8814);
        assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, testContentC8814));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8815")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorFavoriteUnfavorite()
    {
        setupAuthenticatedSession(user, password);
        String testContentC8815 = String.format("FileC8815%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, testContentC8815, "test content");
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over the content's 'Favorite' button.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Add document to favorites", "The text 'Add document to favorites' is displayed");
        LOG.info("Step 2: Click on the 'Favorite' button.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertTrue(documentLibraryPage.isFileFavorite(testContentC8815), "Step 2: The file is not favorited.");
        LOG.info("Step 3: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testContentC8815), "Document is displayed in My favorites list!");
        LOG.info("Step 4: Hover over the content's yellow star.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Remove document from favorites", "'Remove document from favorites' is not displayed");
        LOG.info("Step 5: Click the yellow star.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertFalse(documentLibraryPage.isFileFavorite(testContentC8815), "The file is still 'Favorite'");
        LOG.info("Step 6: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");
        contentService.deleteContentByPath(adminUser, adminPassword, String.format("%s/%s", deletePath, testContentC8815));
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8818")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorEditBasicDetailsBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8818%s", RandomData.getRandomAlphanumeric());
        String editTag = String.format("editTag%s", RandomData.getRandomAlphanumeric());
        String editedName = String.format("editedName%s", RandomData.getRandomAlphanumeric());
        String editedTitle = String.format("editedTitle%s", RandomData.getRandomAlphanumeric());
        String editedDescription = String.format("editedDescription%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editFilePropertiesDialog);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not displayed");
        LOG.info("Step 2: In the 'Name' field enter a name for the folder.");
        editFilePropertiesDialog.setName(editedName);
        LOG.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle').");
        editFilePropertiesDialog.setTitle(editedTitle);
        LOG.info("Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
        editFilePropertiesDialog.setDescription(editedDescription);
        LOG.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
        editFilePropertiesDialog.clickSelectTags();
        LOG.info("Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
        selectDialog.renderedPage();
        selectDialog.typeTag(editTag);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        LOG.info("Step 7: Click 'Save' button.");
        editFilePropertiesDialog.clickSave();
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedName), "Edited document name is not found");
        Assert.assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle), " The title of edited document is not correct.");
        Assert.assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription, "The description of edited document is not correct");
        contentService.deleteFolder(adminUser, adminPassword, siteName, editedName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8819")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditBasicDetailsByOthers()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8819%s", RandomData.getRandomAlphanumeric());
        String editTag2 = String.format("editTag2%s", RandomData.getRandomAlphanumeric());
        String editedName = String.format("editedName%s", RandomData.getRandomAlphanumeric());
        String editedTitle = String.format("editedTitle%s", RandomData.getRandomAlphanumeric());
        String editedDescription = String.format("editedDescription%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editFilePropertiesDialog);
        Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(), "Some elements of the 'Edit Properties' dialog are not sdisplayed");
        LOG.info("Step 2: In the 'Name' field enter a name for the folder.");
        editFilePropertiesDialog.setName(editedName);
        LOG.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle1').");
        editFilePropertiesDialog.setTitle(editedTitle);
        LOG.info("Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
        editFilePropertiesDialog.setDescription(editedDescription);
        LOG.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
        editFilePropertiesDialog.clickSelectTags();
        LOG.info("Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
        selectDialog.typeTag(editTag2);
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();
        LOG.info("Step 7: Click 'Save' button.");
        editFilePropertiesDialog.clickSave();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedName), "Edited document name is not found");
        Assert.assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle), " The title of edited document is not correct.");
        Assert.assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription, "The description of edited document is not correct");
        contentService.deleteFolder(adminUser, adminPassword, siteName, editedName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8816")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorRenameBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8816%s", RandomData.getRandomAlphanumeric());
        String newFolderName = "newFolderNameC8816";
        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        LOG.info("Step 1: Hover over the content name.");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(folderName), "'Rename' icon is not displayed.");
        LOG.info("Step 2: Click on 'Rename' icon.");
        documentLibraryPage.clickRenameIcon(folderName);
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");
        LOG.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
        documentLibraryPage.typeContentName(newFolderName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), folderName + " name updated to: " + newFolderName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");
        contentService.deleteFolder(adminUser, adminPassword, siteName, newFolderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8817")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorRenameByOthers()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8817%s", RandomData.getRandomAlphanumeric());
        String newFolderName = "newFolderNameC8817";
        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");
        LOG.info("Step 1: Hover over the content name.");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(folderName), "'Rename' icon is not displayed.");
        LOG.info("Step 2: Click on 'Rename' icon.");
        documentLibraryPage.clickRenameIcon(folderName);
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");
        LOG.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
        documentLibraryPage.typeContentName(newFolderName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), folderName + " name updated to: " + newFolderName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");
        contentService.deleteFolder(adminUser, adminPassword, siteName, newFolderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8823")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed"})
    public void collaboratorMoveBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("Folder1C8823%s", RandomData.getRandomAlphanumeric());
        String folderName2 = String.format("Folder2C8823%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName1);
        contentService.createFolder(user, password, folderName2, siteName1);
        documentLibraryPage.navigate(siteName1);
        LOG.info("Step 1: Hover over 'testFolder3', Click 'More...' link, Click 'Move to...''.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Move to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + folderName + " to...", "Displayed pop up");
        LOG.info("Step 2: Set the destination to 'All Sites'.");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName1), siteName1 + " displayed in 'Site' section");
        LOG.info("Step 3: Select your site name.");
        copyMoveUnzipToDialog.clickSite(siteName1);
        ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", folderName, folderName2));
        assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(), "Step 5: Selected path is not correct.");
        LOG.info("Step 4: Select 'testFolder4' for the path.");
        copyMoveUnzipToDialog.clickPathFolder(folderName2);
        LOG.info("Step 5: Click 'Move' button. Verify the displayed folders.");
        copyMoveUnzipToDialog.clickMoveButton(documentLibraryPage);
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Move to dialog not displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents");
        LOG.info("Step 6: Open the 'testFolder4' created in preconditions and verify displayed folders.");
        documentLibraryPage.clickOnFolderName(folderName2);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Displayed folders in " + folderName2);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8824")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorMoveByOthers()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8824%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over 'testFolder1'.");
        LOG.info("Step 2: Click 'More...' link. The Move to option is not available.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Move to..."), ("Move to...") + " option is displayed for " + folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8822")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorCopyTo()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());
        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        LOG.info("Step 1: Hover over the created folder and click 'Copy to...'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Copy to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + folderName + " to...", "Displayed pop up");
        LOG.info("Step 2: Set the destination to 'testFolder'.");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName), siteName + " displayed in 'Site' section");
        copyMoveUnzipToDialog.clickSite(siteName);
        copyMoveUnzipToDialog.clickPathFolder(folderName);
        LOG.info("Step 3: Click 'Copy' button");
        copyMoveUnzipToDialog.clickCopyButton(documentLibraryPage);
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");
        LOG.info("Step 4: Verify displayed folders from Documents.");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), folderName + " displayed in Documents");
        LOG.info("Step 5: Open the 'testfolder2' created in preconditions and verify displayed folders.");
        documentLibraryPage.clickOnFolderName(folderName);
        Assert.assertTrue(documentLibraryPage.getFoldersList().toString().contains(folderName), "Displayed folders in " + folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8822")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorDeleteBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse Over and click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), folderName));

        LOG.info("Step 2: Click 'Delete' button");
        deleteDialogFolder.confirmDocumentOrFolderDelete();
        assertFalse(documentLibraryPage.isContentNameDisplayed(folderName), "Documents item list is refreshed and is empty");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(folderName),
            "'DelFolder' is not visible in 'Library' section of the browsing pane.");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8822")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorDeleteByOthers()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8822%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover 'DelFolder' name from the content item list.");
        LOG.info("Step 2: Click on 'More...' link. The Delete folder option is not available.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Delete Folder"),
            ("Delete Folder") + " option is displayed for " + folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8827")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorManagePermissionsBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8827%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + folderName, "Manage Permissions: " + folderName + " title displayed.");

        LOG.info("Step 2: Make some changes. Add User/Group button. Search for testUser. Click Add Button.");
        managePermissionsPage.searchAndAddUserAndGroup(user);

        LOG.info("Step 3: Click 'Save' button");
        managePermissionsPage.clickButton("Save");
        documentLibraryPage.renderedPage();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("Step 4: Click 'More' menu, 'Manage Permissions' options.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + folderName, "Manage Permissions: " + folderName + " title displayed.");
        assertTrue(managePermissionsPage.isPermissionAddedForUser(user), String.format("User [%s] is not added in permissions.", user));
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8828")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManagePermissionsByOthers()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8828%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse over and click on 'More...' button. 'Manage Permissions' option from 'More' menu must not be displayed.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(folderName, "Manage Permissions"),
            "Manage Permissions" + " option is not displayed for " + folderName);
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8829")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorManageAspectsBySelf()
    {
        setupAuthenticatedSession(user, password);
        folderName = String.format("FolderC8829%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step 3: Click on 'Apply changes' button.");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);
        Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8830")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManageAspectsByOthers()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8830%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step 3: Click on 'Apply changes' button.");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);
        Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8834")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorAddComment()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8834%s", RandomData.getRandomAlphanumeric());
        String comment = "Test comment for C8834";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(folderName);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("Step 2: In the 'Comments' area of 'Folder Details' page write a comment and press 'Add Comment' button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment content is not as expected.");

        LOG.info("Step 3: Go back to 'Document Library' page");
        documentLibraryPage.navigate();
        assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8835")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorEditCommentBySelf()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8835%s", RandomData.getRandomAlphanumeric());
        String comment = "Test comment for C8835";
        String editedComment = "Test comment edited for C8835";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(folderName);
        documentDetailsPage.addComment(comment);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
        Assert.assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment), "Edit button is not displayed for comment");
        documentDetailsPage.clickOnEditComment(comment);
        Assert.assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

        LOG.info("Step 3: Edit comment text and click on Save.");
        documentDetailsPage.editComment(editedComment);
        documentDetailsPage.clickOnSaveButtonEditComment();
        documentLibraryPage.navigate();
        Assert.assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getCommentContent(editedComment), editedComment, "Edited comment text is not correct");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8836")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditCommentByOthers()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8836%s", RandomData.getRandomAlphanumeric());
        String comment1 = "Test comment for C8836";
        String editedComment1 = "Test comment for C8836. Edited.";

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(folderName);
        documentDetailsPage.addComment(comment1);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
        Assert.assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment1), "Edit button is not displayed for comment");
        documentDetailsPage.clickOnEditComment(comment1);
        Assert.assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

        LOG.info("Step 3: Edit comment text and click on Save.");
        documentDetailsPage.editComment(editedComment1);
        documentDetailsPage.clickOnSaveButtonEditComment();
        documentLibraryPage.navigate();
        Assert.assertEquals(social.getNumberOfComments(folderName), 1, "Number of comments=");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getCommentContent(editedComment1), editedComment1, "Edited comment text is not correct");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8837")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorDeleteCommentBySelf()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8837%s", RandomData.getRandomAlphanumeric());
        String comment2 = "Test comment for C8837";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(folderName);
        documentDetailsPage.addComment(comment2);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
        documentDetailsPage.clickDeleteComment(comment2);
        Assert.assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

        LOG.info("Step 3: Click 'Delete' button.");
        documentDetailsPage.clickDeleteOnDeleteComment();
        getBrowser().waitUntilElementVisible(documentDetailsPage.noComments);
        Assert.assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8838")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER, "tobefixed" })
    public void collaboratorDeleteCommentByOthers()
    {
        setupAuthenticatedSession(user, password);

        folderName = String.format("FolderC8838%s", RandomData.getRandomAlphanumeric());
        String comment3 = "Test comment for C8838";

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(folderName);
        documentDetailsPage.addComment(comment3);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
        documentDetailsPage.clickDeleteComment(comment3);
        Assert.assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

        LOG.info("Step 3: Click 'Delete' button.");
        documentDetailsPage.clickDeleteOnDeleteComment();
        getBrowser().waitUntilElementVisible(documentDetailsPage.noComments);
        Assert.assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");
        contentService.deleteFolder(adminUser, adminPassword, siteName, folderName);

        cleanupAuthenticatedSession();
    }

}
