package org.alfresco.share.userRolesAndPermissions.collaborator;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil.DocumentType;
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
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

/**
 * Created by Rusu Andrei
 */

public class FoldersAndFiles extends ContextAwareWebTest
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

    // setupTest
    private String user = "UserC" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteC" + DataUtil.getUniqueIdentifier();
    private String siteName1 = "SiteC1" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(adminUser, adminPassword, domain, siteName, "SiteC description", Visibility.PUBLIC);
        siteService.create(adminUser, adminPassword, domain, siteName1, "SiteC description", Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, user, siteName1, "SiteCollaborator");
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C8814")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorLikeUnlike()
    {
        String testContentC8814 = "C8814 file" + DataUtil.getUniqueIdentifier();

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

    }

    @TestRail(id = "C8815")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorFavoriteUnfavorite()
    {
        String testContentC8815 = "C8815 file" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createDocument(user, password, siteName, DocumentType.TEXT_PLAIN, testContentC8815, "test content");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over the content's 'Favorite' button.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Add document to favorites",
                "The text 'Add document to favorites' is displayed");

        LOG.info("Step 2: Click on the 'Favorite' button.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertTrue(documentLibraryPage.isFileFavorite(testContentC8815), "Step 2: The file is not favorited.");

        LOG.info("Step 3: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(testContentC8815), "Document is displayed in My favorites list!");

        LOG.info("Step 4: Hover over the content's yellow star.");
        assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815), "Remove document from favorites",
                "'Remove document from favorites' is not displayed");

        LOG.info("Step 5: Click the yellow star.");
        documentLibraryPage.clickFavoriteLink(testContentC8815);
        assertFalse(documentLibraryPage.isFileFavorite(testContentC8815), "The file is still 'Favorite'");

        LOG.info("Step 6: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents are displayed.");
        assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items", "There are no favorite items.");

    }

    @TestRail(id = "C8818")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditBasicDetailsBySelf()
    {

        String folderName = "Folder" + DataUtil.getUniqueIdentifier();
        String editTag = "editTag" + DataUtil.getUniqueIdentifier();
        String editedName = "editedName" + DataUtil.getUniqueIdentifier();
        String editedTitle = "editedTitle" + DataUtil.getUniqueIdentifier();
        String editedDescription = "editedDescription" + DataUtil.getUniqueIdentifier();

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
        editFilePropertiesDialog.renderedPage();
        editFilePropertiesDialog.clickSave();
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedName), "Edited document name is not found");
        Assert.assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle), " The title of edited document is not correct.");
        Assert.assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription, "The description of edited document is not correct");

    }

    @TestRail(id = "C8819")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditBasicDetailsByOthers()
    {

        String folderName2 = "Folder2" + DataUtil.getUniqueIdentifier();
        String editTag2 = "editTag2" + DataUtil.getUniqueIdentifier();
        String editedName = "editedName" + DataUtil.getUniqueIdentifier();
        String editedTitle = "editedTitle" + DataUtil.getUniqueIdentifier();
        String editedDescription = "editedDescription" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, folderName2, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName2, "Edit Properties", editFilePropertiesDialog);
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

    }

    @TestRail(id = "C8816")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorRenameBySelf()
    {

        String testContentC8816 = "C8816 file" + DataUtil.getUniqueIdentifier();
        String newFolderName = "newFolderNameC8816";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testContentC8816, siteName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("Step 1: Hover over the content name.");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(testContentC8816), "'Rename' icon is not displayed.");

        LOG.info("Step 2: Click on 'Rename' icon.");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
        documentLibraryPage.typeContentName(newFolderName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName), testContentC8816 + " name updated to: " + newFolderName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");

    }

    @TestRail(id = "C8817")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorRenameByOthers()
    {

        String testContentC8817 = "C8817 file" + DataUtil.getUniqueIdentifier();
        String newFileName1 = "newFolderNameC8817";

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testContentC8817, siteName);
        documentLibraryPage.navigate(siteName);
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("Step 1: Hover over the content name.");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(testContentC8817), "'Rename' icon is not displayed.");

        LOG.info("Step 2: Click on 'Rename' icon.");
        documentLibraryPage.clickRenameIcon();
        assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
        documentLibraryPage.typeContentName(newFileName1);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName1), testContentC8817 + " name updated to: " + newFileName1);
        assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");

    }

    @TestRail(id = "C8823")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorMoveBySelf()
    {
        String testFolder3 = "Folder3" + DataUtil.getUniqueIdentifier();
        String testFolder4 = "Folder4" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder3, siteName1);
        contentService.createFolder(user, password, testFolder4, siteName1);
        documentLibraryPage.navigate(siteName1);

        LOG.info("Step 1: Hover over 'testFolder3', Click 'More...' link, Click 'Move to...''.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder3, "Move to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + testFolder3 + " to...", "Displayed pop up");

        LOG.info("Step 2: Set the destination to 'All Sites'.");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName1), siteName1 + " displayed in 'Site' section");

        LOG.info("Step 3: Select your site name.");
        copyMoveUnzipToDialog.clickSite(siteName1);
        ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", testFolder3, testFolder4));
        assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(), "Step 5: Selected path is not correct.");

        LOG.info("Step 4: Select 'testFolder4' for the path.");
        copyMoveUnzipToDialog.clickPathFolder(testFolder4);

        LOG.info("Step 5: Click 'Move' button. Verify the displayed folders.");
        copyMoveUnzipToDialog.clickButtton("Move");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Move to dialog not displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(testFolder3), testFolder3 + " displayed in Documents");

        LOG.info("Step 6: Open the 'testFolder4' created in preconditions and verify displayed folders.");
        documentLibraryPage.clickOnFolderName(testFolder4);
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(testFolder3), "Displayed folders in " + testFolder4);

    }

    @TestRail(id = "C8824")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorMoveByOthers()
    {
        String testFolder5 = "Folder5" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder5, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over 'testFolder1'.");
        LOG.info("Step 2: Click 'More...' link. The Move to option is not available.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolder5, "Move to..."), ("Move to...") + " option is displayed for " + testFolder5);

    }

    @TestRail(id = "C8822")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorCopyTo()
    {

        String testFolder6 = "Folder6" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder6, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over the created folder and click 'Copy to...'.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder6, "Copy to...", copyMoveUnzipToDialog);
        assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + testFolder6 + " to...", "Displayed pop up");

        LOG.info("Step 2: Set the destination to 'testFolder'.");
        copyMoveUnzipToDialog.clickDestinationButton("All Sites");
        assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName), siteName + " displayed in 'Site' section");
        copyMoveUnzipToDialog.clickSite(siteName);
        copyMoveUnzipToDialog.clickPathFolder(testFolder6);

        LOG.info("Step 3: Click 'Copy' button");
        copyMoveUnzipToDialog.clickButtton("Copy");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");

        LOG.info("Step 4: Verify displayed folders from Documents.");
        documentLibraryPage.navigate(siteName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(testFolder6), testFolder6 + " displayed in Documents");

        LOG.info("Step 5: Open the 'testfolder2' created in preconditions and verify displayed folders.");
        documentLibraryPage.clickOnFolderName(testFolder6);
        Assert.assertTrue(documentLibraryPage.getFoldersList().toString().contains(testFolder6), "Displayed folders in " + testFolder6);

    }

    @TestRail(id = "C8822")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorDeleteBySelf()
    {

        String testFolder7 = "Folder7" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder7, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse Over and click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder7, "Delete Folder", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"), "'Delete Folder' pop-up is displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), testFolder7));

        LOG.info("Step 2: Click 'Delete' button");
        deleteDialogFolder.confirmDocumentOrFolderDelete();
        assertFalse(documentLibraryPage.isContentNameDisplayed(testFolder7), "Documents item list is refreshed and is empty");
        assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(testFolder7),
                "'DelFolder' is not visible in 'Library' section of the browsing pane.");

    }

    @TestRail(id = "C8822")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorDeleteByOthers()
    {

        String testFolder8 = "Folder8" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder8, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover 'DelFolder' name from the content item list.");
        LOG.info("Step 2: Click on 'More...' link. The Delete folder option is not available.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolder8, "Delete Folder"),
                ("Delete Folder") + " option is displayed for " + testFolder8);

    }

    @TestRail(id = "C8827")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManagePermissionsBySelf()
    {

        String testFolder9 = "Folder9" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder9, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder9, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolder9, "Manage Permissions: " + testFolder9 + " title displayed.");

        LOG.info("Step 2: Make some changes. Add User/Group button. Search for testUser. Click Add Button.");
        managePermissionsPage.searchAndAddUserAndGroup(user);

        LOG.info("Step 3: Click 'Save' button");
        managePermissionsPage.clickButton("Save");
        documentLibraryPage.renderedPage();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Page displayed");

        LOG.info("Step 4: Click 'More' menu, 'Manage Permissions' options.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder9, "Manage Permissions", managePermissionsPage);
        assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolder9, "Manage Permissions: " + testFolder9 + " title displayed.");
        assertTrue(managePermissionsPage.isPermissionAddedForUser(user), String.format("User [%s] is not added in permissions.", user));

    }

    @TestRail(id = "C8828")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManagePermissionsByOthers()
    {

        String testFolder10 = "Folder10" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder10, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Mouse over and click on 'More...' button. 'Manage Permissions' option from 'More' menu must not be displayed.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolder10, "Manage Permissions"),
                "Manage Permissions" + " option is not displayed for " + testFolder10);

    }

    @TestRail(id = "C8829")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManageAspectsBySelf()
    {

        String testFolder11 = "Folder11" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder11, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder11, "Manage Aspects", aspectsForm);

        LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
        aspectsForm.addElement(0);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step 3: Click on 'Apply changes' button.");
        aspectsForm.clickApplyChangesButton();
        Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");

    }

    @TestRail(id = "C8830")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorManageAspectsByOthers()
    {

        String testFolder12 = "Folder12" + DataUtil.getUniqueIdentifier();

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder12, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder12, "Manage Aspects", aspectsForm);

        LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
        aspectsForm.addElement(0);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step 3: Click on 'Apply changes' button.");
        aspectsForm.clickApplyChangesButton();
        Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");

    }

    @TestRail(id = "C8834")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorAddComment()
    {

        String testFolder13 = "Folder13" + DataUtil.getUniqueIdentifier();
        String comment = "Test comment for C8834";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder13, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step 1: Hover over a document and press \"Comment\"");
        social.clickCommentLink(testFolder13);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details", "Displayed page=");

        LOG.info("Step 2: In the 'Comments' area of 'Folder Details' page write a comment and press 'Add Comment' button");
        documentDetailsPage.addComment(comment);
        assertEquals(documentDetailsPage.getCommentContent(), comment, "Comment content is not as expected.");

        LOG.info("Step 3: Go back to 'Document Library' page");
        documentLibraryPage.navigate();
        assertEquals(social.getNumberOfComments(testFolder13), 1, "Number of comments=");

    }

    @TestRail(id = "C8835")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditCommentBySelf()
    {

        String testFolder14 = "Folder14" + DataUtil.getUniqueIdentifier();
        String comment = "Test comment for C8835";
        String editedComment = "Test comment edited for C8835";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder14, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(testFolder14);
        documentDetailsPage.addComment(comment);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder14, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
        Assert.assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment), "Edit button is not displayed for comment");
        documentDetailsPage.clickOnEditComment(comment);
        Assert.assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

        LOG.info("Step 3: Edit comment text and click on Save.");
        documentDetailsPage.editComment(editedComment);
        documentDetailsPage.clickOnSaveButtonEditComment();
        documentLibraryPage.navigate();
        Assert.assertEquals(social.getNumberOfComments(testFolder14), 1, "Number of comments=");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder14, "View Details", documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getCommentContent(editedComment), editedComment, "Edited comment text is not correct");

    }

    @TestRail(id = "C8836")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorEditCommentByOthers()
    {

        String testFolder15 = "Folder15" + DataUtil.getUniqueIdentifier();
        String comment1 = "Test comment for C8836";
        String editedComment1 = "Test comment edited for C8836";

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder15, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(testFolder15);
        documentDetailsPage.addComment(comment1);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder15, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
        Assert.assertTrue(documentDetailsPage.isEditButtonDisplayedForComment(comment1), "Edit button is not displayed for comment");
        documentDetailsPage.clickOnEditComment(comment1);
        Assert.assertTrue(documentDetailsPage.isEditCommentDisplayed(), "Edit comment is not displayed");

        LOG.info("Step 3: Edit comment text and click on Save.");
        documentDetailsPage.editComment(editedComment1);
        documentDetailsPage.clickOnSaveButtonEditComment();
        documentLibraryPage.navigate();
        Assert.assertEquals(social.getNumberOfComments(testFolder15), 1, "Number of comments=");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder15, "View Details", documentDetailsPage);
        Assert.assertEquals(documentDetailsPage.getCommentContent(editedComment1), editedComment1, "Edited comment text is not correct");

    }

    @TestRail(id = "C8837")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorDeleteCommentBySelf()
    {

        String testFolder16 = "Folder16" + DataUtil.getUniqueIdentifier();
        String comment2 = "Test comment for C8837";

        LOG.info("Preconditions.");
        contentService.createFolder(user, password, testFolder16, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(testFolder16);
        documentDetailsPage.addComment(comment2);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder16, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
        documentDetailsPage.clickDeleteComment(comment2);
        Assert.assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

        LOG.info("Step 3: Click 'Delete' button.");
        documentDetailsPage.clickDeleteOnDeleteComment();
        getBrowser().waitUntilElementVisible(documentDetailsPage.noComments);
        Assert.assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");

    }

    @TestRail(id = "C8838")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER })
    public void collaboratorDeleteCommentByOthers()
    {

        String testFolder17 = "Folder17" + DataUtil.getUniqueIdentifier();
        String comment3 = "Test comment for C8838";

        LOG.info("Preconditions.");
        contentService.createFolder(adminUser, adminPassword, testFolder17, siteName);
        documentLibraryPage.navigate(siteName);
        social.clickCommentLink(testFolder17);
        documentDetailsPage.addComment(comment3);
        documentLibraryPage.navigate();

        LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
        documentLibraryPage.clickDocumentLibraryItemAction(testFolder17, "View Details", documentDetailsPage);

        LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
        documentDetailsPage.clickDeleteComment(comment3);
        Assert.assertTrue(documentDetailsPage.isDeleteCommentPromptDisplayed(), "Delete Comment prompt is not displayed");

        LOG.info("Step 3: Click 'Delete' button.");
        documentDetailsPage.clickDeleteOnDeleteComment();
        getBrowser().waitUntilElementVisible(documentDetailsPage.noComments);
        Assert.assertEquals(documentDetailsPage.getNoCommentsText(), "No comments", "No comments notification is not displayed");

    }

}
