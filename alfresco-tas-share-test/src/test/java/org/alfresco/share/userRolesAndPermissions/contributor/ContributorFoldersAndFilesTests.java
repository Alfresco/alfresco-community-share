package org.alfresco.share.userRolesAndPermissions.contributor;

import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ChangeContentTypeDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.DocumentLibraryPage.DocumentsFilters;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.testng.Assert.*;

public class ContributorFoldersAndFilesTests extends ContextAwareWebTest
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SocialFeatures socialFeatures;

    @Autowired
    EditPropertiesDialog editPropertiesDialog;

    @Autowired
    SelectDialog selectDialog;

    @Autowired
    CopyMoveUnzipToDialog copyMoveToDialog;

    @Autowired
    private DeleteDocumentOrFolderDialog deleteDialog;

    @Autowired
    ManagePermissionsPage managePermissionsPage;

    @Autowired
    AspectsForm aspectsForm;

    @Autowired
    DocumentDetailsPage documentDetailsPage;

    @Autowired
    ChangeContentTypeDialog changeContentTypeDialog;

    @Autowired
    EditPropertiesPage editPropertiesPage;

    private String userContributor;
    private final String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("SiteDescription%s", RandomData.getRandomAlphanumeric());
    private final String adminFile = String.format("AdminFile%s", RandomData.getRandomAlphanumeric());
    private final String adminFolder = String.format("AdminFolder%s", RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userContributor = String.format("Contributor%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        siteService.create(adminUser, adminPassword, domain, siteName, description, Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName, "SiteContributor");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, adminFile, "Some content");
        contentService.createFolder(adminUser, adminPassword, adminFolder, siteName);
        setupAuthenticatedSession(userContributor, password);
    }

    @TestRail(id = "C8787")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void likeAndUnlike()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over 'Like' button for the test file");
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(fileName), "Like this document", "Correct message for 'Like' button");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 0, "Number of likes = 0");

        LOG.info("Step2: Click on the content's 'Like' button.");
        socialFeatures.clickLikeButton(fileName);
        Assert.assertTrue(socialFeatures.isLikeButtonEnabled(fileName), "Like button enabled");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 1, "Number of likes is 1");

        LOG.info("Step3: Hover over the content's 'Like' button.");
        Assert.assertEquals(socialFeatures.getLikeButtonEnabledText(fileName), "Unlike", "Unlike is not displayed");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 1, "Number of likes = 1");

        LOG.info("Step4: Click on the content's 'Unlike' button.");
        socialFeatures.clickUnlike(fileName);
        Assert.assertEquals(socialFeatures.getLikeButtonMessage(fileName), "Like this document", "Correct message for 'Like' button");
        Assert.assertEquals(socialFeatures.getNumberOfLikes(fileName), 0, "Number of likes = 0");

    }

    @TestRail(id = "C8788")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void favoriteAndUnfavorite()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over 'Favorite' button for the test file");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileName), "Add document to favorites", "The text 'Add document to favorites'  displayed");

        LOG.info("Step2: Click on the 'Favorite' button");
        documentLibraryPage.clickFavoriteLink(fileName);
        assertTrue(documentLibraryPage.isFileFavorite(fileName), "The gray star and text 'Favorite' replaced by a golden star");

        LOG.info("Step3: Navigate to 'My Favorites' and check favorite items list.");
        documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
        assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header, "My Favorites documents displayed.");
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "File displayed in My favorites list!");

        LOG.info("Step4: Hover over the content's yellow star");
        assertEquals(documentLibraryPage.getFavoriteTooltip(fileName), "Remove document from favorites",
                "The star is replaced by a X button and the text 'Remove document from favorites' displayed");
    }

    @TestRail(id = "C8789")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void renameItemAddedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String newFileName = "newFileName";

        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test file");
        assertTrue(documentLibraryPage.isRenameIconDisplayed(fileName), "'Rename' icon displayed.");

        LOG.info("Step2: Click on 'Rename' icon");
        documentLibraryPage.clickRenameIcon(fileName);
        assertTrue(documentLibraryPage.isContentNameInputField(), "File name is text input field.");
        assertTrue(documentLibraryPage.verifyButtonsFromRenameContent("Save", "Cancel"), "Rename content buttons");

        LOG.info("Step3: Fill in the input field with a new name and click 'Save' button");
        documentLibraryPage.typeContentName(newFileName);
        documentLibraryPage.clickButtonFromRenameContent("Save");
        assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName), fileName + " name updated to: " + newFileName);
        assertFalse(documentLibraryPage.isContentNameInputField(), "File is input field.");
        contentService.deleteDocument(adminUser, adminPassword, siteName, newFileName);
    }

    @TestRail(id = "C8790")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void renameItemAddedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test file. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test file. Verify 'Rename' icon is missing.");
        assertFalse(documentLibraryPage.isRenameIconDisplayed(adminFile), "'Rename' icon displayed.");
    }

    @TestRail(id = "C8791")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void editBasicDetailsCreatedBySelf()
    {
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(userContributor, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Preconditions: Navigate to Document Library for the page for the test site");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the created folder and click 'Edit Properties' action.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Edit Properties", editPropertiesDialog);
        Assert.assertTrue(editPropertiesDialog.verifyAllElementsAreDisplayed(), "All elements from 'Edit Properties' dialog displayed");

        LOG.info("Step 2: In the 'Name' field enter a valid name");
        editPropertiesDialog.setName("FolderEditName");

        LOG.info("Step 3: In the 'Title' field enter a valid title");
        editPropertiesDialog.setTitle("FolderEditTitle");

        LOG.info("Step 4: In the 'Description' field enter a valid description");
        editPropertiesDialog.setDescription("FolderEditDescription");

        LOG.info("Step 5: Click the 'Select' button in the tags section");
        editPropertiesDialog.clickSelectTags();

        LOG.info("Step 6: Type a tag name and click create");
        selectDialog.typeTag("edittag");
        selectDialog.clickCreateNewIcon();
        selectDialog.clickOk();

        LOG.info("Step 7: Click 'Save' and verify that document details have been updated");
        editPropertiesDialog.clickSave();
        documentLibraryPage.renderedPage();
        Assert.assertTrue(documentLibraryPage.isContentNameDisplayed("FolderEditName"), "Edited name displayed");
        Assert.assertEquals(documentLibraryPage.getItemTitle("FolderEditName"), "(FolderEditTitle)", "Correct title of edited item");
        Assert.assertEquals(documentLibraryPage.getItemDescription("FolderEditName"), "FolderEditDescription", "Correct description of edited item");
        Assert.assertEquals(documentLibraryPage.getTags("FolderEditName"), "[edittag]", "Correct tag of the edited item");

    }

    @TestRail(id = "C8792")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void editBasicDetailsCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Edit Properties' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, "Edit Properties"), "Edit Properties action displayed");
    }

    @TestRail(id = "C8795")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void contributorCopy()
    {
        String siteName1 = String.format("SiteName1%s", RandomData.getRandomAlphanumeric());
        String siteName2 = String.format("SiteName2%s", RandomData.getRandomAlphanumeric());
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        siteService.create(adminUser, adminPassword, domain, siteName1, description, Visibility.PUBLIC);
        siteService.create(userContributor, password, domain, siteName2, description, Site.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userContributor, siteName1, "SiteContributor");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName1);
        documentLibraryPage.navigate(siteName1);
        LOG.info("Steps1,2,3: Hover over the created folder. Click 'More...' link. Click 'Copy to...' link");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Copy to...", copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Copy " + folderName + " to...", "Displayed pop up");

        LOG.info("Step4: Set the destination to 'All sites'. Select a site.");
        copyMoveToDialog.clickDestinationButton("All Sites");
        ArrayList<String> expectedPath_destination = new ArrayList<>(asList("Documents", folderName));
        assertEquals(copyMoveToDialog.getPathList(), expectedPath_destination.toString(), "Path");
        copyMoveToDialog.clickSite(siteName2);
        ArrayList<String> expectedPath = new ArrayList<>(Collections.singletonList("Documents"));
        assertEquals(copyMoveToDialog.getPathList(), expectedPath.toString(), "Path");

        LOG.info("Step5: Click 'Copy' button");
        copyMoveToDialog.clickButtton("Copy");
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog displayed");

        LOG.info("Step6: Verify displayed folder has been copied");
        documentLibraryPage.navigate(siteName2);
        assertTrue(documentLibraryPage.isContentNameDisplayed(folderName), "Displayed folders=");
    }

    @TestRail(id = "C8796")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void moveContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createFolder(adminUser, adminPassword, folderName, siteName);
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Steps1, 2,3: Hover over the file. Click 'More...' link. Click 'Move to...'");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Move to...", copyMoveToDialog);
        assertEquals(copyMoveToDialog.getDialogTitle(), "Move " + fileName + " to...", "Displayed pop up");

        LOG.info("Step4: Set the destination to 'All Sites'");
        copyMoveToDialog.clickDestinationButton("All Sites");

        LOG.info("Step5: Select 'site1'");
        copyMoveToDialog.clickSite(siteName);

        LOG.info("Step6: Set the folder created in preconditions as path");
        copyMoveToDialog.clickPathFolder(folderName);

        LOG.info("Step7: Click 'Move' button. Verify the displayed files");
        copyMoveToDialog.clickButtton("Move");
        documentLibraryPage.renderedPage();
        assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Move to' dialog not displayed");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed in Documents");

        LOG.info("Step8: Open the folder created in preconditions and verify displayed files");
        documentLibraryPage.clickOnFolderName(folderName);
        assertTrue(documentLibraryPage.isContentNameDisplayed(fileName), "Displayed files in " + folderName);
    }

    @TestRail(id = "C8797")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void moveContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Move to...' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, "Move to..."), "Move to... action displayed");
    }

    @TestRail(id = "C8798")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void deleteContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Steps1, 2: Hover over the file. Click 'More...' link. Click 'Delete Document' link");
        documentLibraryPage.clickDocumentLibraryItemAction(fileName, "Delete Document", deleteDialog);
        assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteDocument"), "'Delete Document' pop-up displayed");
        assertEquals(deleteDialog.getMessage(), String.format(language.translate("confirmDeletion.message"), fileName));

        LOG.info("STEP3: Click 'Delete' button");
        deleteDialog.confirmDocumentOrFolderDelete();
        assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library", "Displayed page=");
        assertFalse(documentLibraryPage.isContentNameDisplayed(fileName), fileName + " displayed in Document Library of " + siteName);

    }

    @TestRail(id = "C8799")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void deleteContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Delete Document' action is missing.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFile, "Delete Document"), "Delete action displayed");
    }

    @TestRail(id = "C8800")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void managePermissionsContentCreatedBySelf()
    {
        String folderName = "FolderName";
        String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        String fullName = user2 + " " + user2;

        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(userContributor, password, folderName, siteName);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);

        LOG.info("Steps2: On the Manage Permissions page click on Add User/Group button and add permissions for a test user.");
        managePermissionsPage.searchAndAddUserOrGroup(user2, 0);

        LOG.info("Steps3: Click 'Save' button");
        managePermissionsPage.clickButton("Save");
        documentLibraryPage.renderedPage();

        LOG.info("Step4: Return to Manage Permissions page for the file and check if permissions were added successfully.");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Permissions", managePermissionsPage);
        assertTrue(managePermissionsPage.isPermissionAddedForUser(fullName), String.format("User [%s] added in permissions.", user2));

    }

    @TestRail(id = "C8801")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void managePermissionsForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Manage Permissions' action is missing.");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, "Manage Permissions"), "Manage Permissions action displayed");

    }

    @TestRail(id = "C8802")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void manageAspectsForContentCreatedBySelf()
    {
        String folderName = String.format("folderName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createFolder(userContributor, password, folderName, siteName);
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu");
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);

        LOG.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm.addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

        LOG.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm.clickApplyChangesButton(documentLibraryPage);
        documentLibraryPage.clickDocumentLibraryItemAction(folderName, "Manage Aspects", aspectsForm);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is present on 'Available to Add' list");

    }

    @TestRail(id = "C8803")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void manageAspectsForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test site and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Hover over the test folder. Click on 'More...' link. Verify 'Manage Aspects' action is missing");
        assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(adminFolder, "Manage Aspects"), "Manage Aspects action displayed");

    }

    @TestRail(id = "C8804")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void changeTypeForContentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the page test site, as Contributor user.");
        contentService.createDocument(userContributor, password, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Verify document's Properties list");
        documentLibraryPage.clickOnFile(fileName);
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Mimetype", "Size", "Creator",
                "Created Date", "Modifier", "Modified Date"), "Displayed properties:");

        LOG.info("Step2: From 'Document Actions' list click 'Change Type' option");
        documentDetailsPage.clickDocumentActionsOption("Change Type");
        changeContentTypeDialog.renderedPage();
        assertEquals(changeContentTypeDialog.getDialogTitle(), "Change Type", "Displayed dialog: ");

        LOG.info("Step3: Select 'Article' from 'New Type' dropdown and click 'Ok' button");
        changeContentTypeDialog.selectOption("Article");
        changeContentTypeDialog.clickButton("OK");
        getBrowser().refresh();
        documentDetailsPage.renderedPage();
        assertTrue(documentDetailsPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Creator", "Created Date", "Modifier",
                "Modified Date", "Template Name", "Mimetype", "Size", "Primary Image", "Secondary Image", "Related Articles"), "Displayed properties:");

        LOG.info("Step6: Click 'Edit Properties' option from 'Document Actions' section");
        documentDetailsPage.clickEditProperties();
        assertEquals(editPropertiesPage.getPageTitle(), "Alfresco » Edit Properties", "Page displayed:");
        assertTrue(editPropertiesPage.arePropertiesDisplayed("Name", "Title", "Description", "Author", "Template Name", "Tags", "Primary Image",
                "Secondary Image", "Related Articles"), "Displayed properties:");
    }

    @TestRail(id = "C8805")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void changeTypeForContentCreatedByOthers()
    {
        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Go to document details page and verify 'Change Type' action is missing");
        documentLibraryPage.clickOnFile(adminFile);
        assertFalse(documentDetailsPage.isActionAvailable("Change Type"), "Change type action displayed");
    }

    @TestRail(id = "C8807")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void addComment()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");

        LOG.info("Step 2: Return to Document Library page and check that the comment counter for the file has increased");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(socialFeatures.getNumberOfComments(fileName), 1, "Increased number of comments");

    }

    @TestRail(id = "C8808")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void editCommentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");

        LOG.info("Step2: Edit the comment for the file");
        documentDetailsPage.clickEditComment("commentAddedByContributor");
        documentDetailsPage.editComment("commentEditedByContributor");
        documentDetailsPage.clickOnSaveButtonEditComment();
        getBrowser().waitInSeconds(2);
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentEditedByContributor", "Comment edited successfully by contributor");
    }

    @TestRail(id = "C8809")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void editCommentCreatedByOthers()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library page for the test site, as Admin user; add a comment for the test file");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        contentAction.addComment(adminUser, adminPassword, siteName, fileName, "commentAddedByAdmin");

        LOG.info("Step1:Login as Contributor and navigate to document details page for test file; verify the comment added by admin is displayed, without 'Edit' option");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByAdmin", "Comment added by admin is visible to Contributor user");
        Assert.assertFalse(documentDetailsPage.isEditButtonDisplayedForComment("commentAddedByAdmin"), "Edit option missing for Contributor user");

    }

    @TestRail(id = "C8810")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void deleteCommentCreatedBySelf()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test sites and test folder. Navigate to Document Library for the test site, as Contributor user.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        documentLibraryPage.navigate(siteName);

        LOG.info("Step1: Add a comment for the file");
        socialFeatures.clickCommentLink(fileName);
        documentDetailsPage.addCommentToItem("commentAddedByContributor");
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByContributor", "Comment added successfully by contributor");

        LOG.info("Step2: Delete the comment for the file");
        documentDetailsPage.clickDeleteComment("commentAddedByContributor");
        documentDetailsPage.clickDeleteOnDeleteComment();

        LOG.info("Step3: Verify comment is successfully deleted");
        documentLibraryPage.navigate(siteName);
        Assert.assertEquals(socialFeatures.getNumberOfComments(fileName), 0, "0 comments for the test file");
    }

    @TestRail(id = "C8811")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void deleteCommentCreatedByOthers()
    {
        String fileName = String.format("fileName%s", RandomData.getRandomAlphanumeric());

        LOG.info("Preconditions: Create test user, test site, test file. Navigate to Document Library for the test site, as Admin user; add a comment to the file.");
        contentService.createDocument(adminUser, adminPassword, siteName, DocumentType.TEXT_PLAIN, fileName, "FileContent");
        contentAction.addComment(adminUser, adminPassword, siteName, fileName, "commentAddedByAdmin");

        LOG.info("Step1: Login as contributor user and navigate to Document Details page for the test site. Verify the comment added by admin is displayed, with no 'Delete' option.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(fileName);
        Assert.assertEquals(documentDetailsPage.getCommentContent(), "commentAddedByAdmin", "Comment added by admin is visible to Contributor user");
        Assert.assertFalse(documentDetailsPage.isDeleteButtonDisplayedForComment("commentAddedByAdmin"), "Delete option missing for Contributor user");
    }

    @TestRail(id = "C8812")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void viewItemDetailsPage()
    {
        LOG.info("Step1: Navigate to document library page and click on the created file. Verify the preview for the file is successfully displayed on the Document details page.");
        documentLibraryPage.navigate(siteName);
        documentLibraryPage.clickOnFile(adminFile);
        assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Document Details", "Page displayed");
        assertEquals(documentDetailsPage.getContentText(), "Some content", "File preview displayed");
    }

}
