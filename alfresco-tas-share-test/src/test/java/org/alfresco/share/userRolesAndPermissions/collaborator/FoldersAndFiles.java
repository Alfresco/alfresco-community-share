package org.alfresco.share.userRolesAndPermissions.collaborator;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.dataprep.CMISUtil.DocumentType;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SelectDestinationDialog;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.CreateContent;
import org.alfresco.po.share.alfrescoContent.document.DocumentCommon;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.po.share.alfrescoContent.document.GoogleDocsCommon;
import org.alfresco.po.share.alfrescoContent.document.SocialFeatures;
import org.alfresco.po.share.alfrescoContent.document.UploadContent;
import org.alfresco.po.share.alfrescoContent.organizingContent.CopyMoveUnzipToDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.DeleteDocumentOrFolderDialog;
import org.alfresco.po.share.alfrescoContent.organizingContent.taggingAndCategorizingContent.SelectDialog;
import org.alfresco.po.share.alfrescoContent.pageCommon.HeaderMenuBar;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditInAlfrescoPage;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.EditPropertiesDialog;
import org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders.ManagePermissionsPage;
import org.alfresco.po.share.dashlet.MyDocumentsDashlet;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.DocumentLibraryPage.DocumentsFilters;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneeToWorkflowPopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Rusu Andrei
 */

public class FoldersAndFiles extends ContextAwareWebTest {

  @Autowired
  DocumentCommon documentCommon;
  
  @Autowired
  SiteDashboardPage siteDashboardPage;

  @Autowired
  UserDashboardPage userDashboardPage;

  @Autowired
  MyDocumentsDashlet myDocumentsDashlet;

  @Autowired
  DocumentLibraryPage documentLibraryPage;

  @Autowired
  DocumentDetailsPage documentDetailsPage;

  @Autowired
  CreateContent create;

  @Autowired
  UploadContent uploadContent;

  @Autowired
  HeaderMenuBar headerMenuBar;

  @Autowired
  DeleteDialog deleteDialog;

  @Autowired
  EditInAlfrescoPage editInAlfrescoPage;

  @Autowired
  GoogleDocsCommon googleDocsCommon;

  @Autowired
  StartWorkflowPage startWorkflowPage;

  @Autowired
  SelectPopUpPage selectPopUpPage;

  @Autowired
  SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;

  @Autowired
  AspectsForm aspectsForm;

  @Autowired
  ToolbarTasksMenu toolbarTasksMenu;

  @Autowired
  Toolbar toolbar;

  @Autowired
  WorkflowService workflow;

  @Autowired
  WorkflowDetailsPage workflowDetailsPage;

  @Autowired
  MyTasksPage myTasksPage;

  @Autowired
  EditTaskPage editTaskPage;

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

  @Autowired
  DocumentDetailsPage documentPreviewPage;
  
  @Autowired
  SelectDestinationDialog selectDestinationDialog;

  // setupTest
  private String user = "UserC" + DataUtil.getUniqueIdentifier();
  private String siteName = "SiteC" + DataUtil.getUniqueIdentifier();
  private String siteName1 = "SiteC1" + DataUtil.getUniqueIdentifier();
  private String siteName2 = "SiteC2" + DataUtil.getUniqueIdentifier();
  private String siteName3 = "SiteC3" + DataUtil.getUniqueIdentifier();
  private String siteName4 = "SiteC4" + DataUtil.getUniqueIdentifier();
  private String siteName5 = "SiteC5" + DataUtil.getUniqueIdentifier();
  private String siteName6 = "SiteC6" + DataUtil.getUniqueIdentifier();
  private String description = "SiteC description";

  @BeforeClass
  public void setupTest() {

    userService.create(adminUser, adminPassword, user, password, user + "@tests.com", user, user);
    siteService.create(adminUser, adminPassword, domain, siteName, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName1, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName2, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName3, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName4, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName5, description, Visibility.PUBLIC);
    siteService.create(adminUser, adminPassword, domain, siteName6, description, Visibility.PUBLIC);
    userService.createSiteMember(adminUser, adminPassword, user, siteName, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName1, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName2, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName3, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName4, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName5, "SiteCollaborator");
    userService.createSiteMember(adminUser, adminPassword, user, siteName6, "SiteCollaborator");
    setupAuthenticatedSession(user, password);

  }

  @TestRail(id = "C8814")
  @Test
  public void collaboratorLikeUnlike() {

    String testContentC8814 = "C8814 file" + DataUtil.getUniqueIdentifier();
    String fileContent = "test content" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    contentService.createDocument(user, password, siteName3, DocumentType.TEXT_PLAIN,
        testContentC8814, fileContent);
    documentLibraryPage.navigate(siteName3);

    LOG.info("Step 1: Hover over the testContent 'Like' button.");
    assertTrue(documentLibraryPage.isLikeButtonDisplayed(testContentC8814),
        "Documents link is not present");
    assertEquals(social.getLikeButtonMessage(testContentC8814), "Like this document",
        "Like Button message=");
    assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");

    LOG.info("Step 2: Click on the content's 'Like' button.");
    social.clickLikeButton(testContentC8814);
    assertEquals(social.getNumberOfLikes(testContentC8814), 1,
        testContentC8814 + "The number of likes=");
    assertTrue(social.isLikeButtonEnabled(testContentC8814), "Like button is enabled");
    assertEquals(social.getLikeButtonMessage(testContentC8814), "Unlike", "Like Button message=");

    LOG.info("Step 3: Hover over the content's 'Like' button.");
    assertEquals(social.getLikeButtonEnabledText(testContentC8814), "Unlike",
        "Unlike is displayed");
    assertEquals(social.getNumberOfLikes(testContentC8814), 1, "The number of likes=");

    LOG.info("Step 4: Click on the content's 'Unlike' button.");
    social.clickUnlike(testContentC8814);
    assertEquals(social.getNumberOfLikes(testContentC8814), 0, "The number of likes=");

  }

  @TestRail(id = "C8815")
  @Test
  public void collaboratorFavoriteUnfavorite() {

    String testContentC8815 = "C8815 file" + DataUtil.getUniqueIdentifier();
    String fileContent1 = "test content" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    contentService.createDocument(user, password, siteName3, DocumentType.TEXT_PLAIN,
        testContentC8815, fileContent1);
    documentLibraryPage.navigate(siteName3);

    LOG.info("Step 1: Hover over the content's 'Favorite' button.");
    assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815),
        "Add document to favorites", "The text 'Add document to favorites' is displayed");

    LOG.info("Step 2: Click on the 'Favorite' button.");
    documentLibraryPage.clickFavoriteLink(testContentC8815);
    assertTrue(documentLibraryPage.isFileFavorite(testContentC8815),
        "Step 2: The file is not favorited.");

    LOG.info("Step 3: Navigate to 'My Favorites' and check favorite items list.");
    documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
    documentLibraryPage.renderedPage();
    assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header,
        "My Favorites documents are displayed.");
    assertTrue(documentLibraryPage.getFilesList().contains(testContentC8815),
        "Document is displayed in My favorites list!");

    LOG.info("Step 4: Hover over the content's yellow star.");
    assertEquals(documentLibraryPage.getFavoriteTooltip(testContentC8815),
        "Remove document from favorites", "'Remove document from favorites' is not displayed");

    LOG.info("Step 5: Click the yellow star.");
    documentLibraryPage.clickFavoriteLink(testContentC8815);
    assertFalse(documentLibraryPage.isFileFavorite(testContentC8815),
        "The file is still 'Favorite'");

    LOG.info("Step 6: Navigate to 'My Favorites' and check favorite items list.");
    documentLibraryPage.clickDocumentsFilterOption(DocumentsFilters.Favorites.title);
    documentLibraryPage.renderedPage();
    assertEquals(documentLibraryPage.getDocumentListHeader(), DocumentsFilters.Favorites.header,
        "My Favorites documents are displayed.");
    assertEquals(documentLibraryPage.getDocumentListMessage(), "No content items",
        "There are no favorite items.");

  }

  @TestRail(id = "C8818")
  @Test
  public void collaboratorEditBasicDetailsBySelf() {

    String folderName = "Folder" + DataUtil.getUniqueIdentifier();
    String editTag = "editTag" + DataUtil.getUniqueIdentifier();
    String editedName = "editedName" + DataUtil.getUniqueIdentifier();
    String editedTitle = "editedTitle" + DataUtil.getUniqueIdentifier();
    String editedDescription = "editedDescription" + DataUtil.getUniqueIdentifier();


    LOG.info("Preconditions.");
    content.createFolder(user, password, folderName, siteName);
    documentLibraryPage.navigate(siteName);

    LOG.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
    documentLibraryPage.mouseOverFolder(folderName);
    documentLibraryPage.clickEditProperties(folderName);
    Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(),
        "Some elements of the 'Edit Properties' dialog are not sdisplayed");

    LOG.info("Step 2: In the 'Name' field enter a name for the folder.");
    editFilePropertiesDialog.renderedPage();
    editFilePropertiesDialog.setName(editedName);

    LOG.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle').");
    editFilePropertiesDialog.setTitle(editedTitle);

    LOG.info(
        "Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
    editFilePropertiesDialog.setDescription(editedDescription);

    LOG.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
    editFilePropertiesDialog.clickSelectTags();

    LOG.info(
        "Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
    selectDialog.renderedPage();
    selectDialog.typeTag(editTag);
    selectDialog.clickCreateNewIcon();
    selectDialog.clickOk();

    LOG.info("Step 7: Click 'Save' button.");
    editFilePropertiesDialog.renderedPage();
    editFilePropertiesDialog.clickSave();
    documentLibraryPage.renderedPage();
    Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedName),
        "Edited document name is not found");
    Assert.assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle),
        " The title of edited document is not correct.");
    Assert.assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription,
        "The description of edited document is not correct");

  }

  @TestRail(id = "C8819")
  @Test
  public void collaboratorEditBasicDetailsByOthers() {

    String folderName2 = "Folder2" + DataUtil.getUniqueIdentifier();
    String editTag2 = "editTag2" + DataUtil.getUniqueIdentifier();
    String editedName = "editedName" + DataUtil.getUniqueIdentifier();
    String editedTitle = "editedTitle" + DataUtil.getUniqueIdentifier();
    String editedDescription = "editedDescription" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, folderName2, siteName);
    documentLibraryPage.navigate(siteName);

    LOG.info("Step 1: Hover over the created folder and click 'Edit Properties' action.");
    documentLibraryPage.mouseOverFolder(folderName2);
    documentLibraryPage.clickEditProperties(folderName2);
    Assert.assertTrue(editFilePropertiesDialog.verifyAllElementsAreDisplayed(),
        "Some elements of the 'Edit Properties' dialog are not sdisplayed");

    LOG.info("Step 2: In the 'Name' field enter a name for the folder.");
    editFilePropertiesDialog.setName(editedName);

    LOG.info("Step 3: In the 'Title' field enter a title for the folder ('editedTitle1').");
    editFilePropertiesDialog.setTitle(editedTitle);

    LOG.info(
        "Step 4: In the 'Description' field enter a description for the folder (e.g.: 'editedDescription').");
    editFilePropertiesDialog.setDescription(editedDescription);

    LOG.info("Step 5: Click 'Select' beneath the Tags label to edit the tag associations.");
    editFilePropertiesDialog.clickSelectTags();

    LOG.info(
        "Step 6: Type any tag name (e.g.: 'newtag') and click the checked icon and click 'OK' to save the changes.");
    selectDialog.typeTag(editTag2);
    selectDialog.clickCreateNewIcon();
    selectDialog.clickOk();

    LOG.info("Step 7: Click 'Save' button.");
    editFilePropertiesDialog.clickSave();
    Assert.assertTrue(documentLibraryPage.isContentNameDisplayed(editedName),
        "Edited document name is not found");
    Assert.assertTrue(documentLibraryPage.getItemTitle(editedName).contains(editedTitle),
        " The title of edited document is not correct.");
    Assert.assertEquals(documentLibraryPage.getItemDescription(editedName), editedDescription,
        "The description of edited document is not correct");

  }

  @TestRail(id = "C8816")
  @Test
  public void collaboratorRenameBySelf() {

    String testContentC8816 = "C8816 file" + DataUtil.getUniqueIdentifier();
    String newFolderName = "newFolderNameC8816";

    LOG.info("Preconditions.");
    contentService.createFolder(user, password, testContentC8816, siteName6);
    documentLibraryPage.navigate(siteName6);
    assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library",
        "Page displayed");

    LOG.info("Step 1: Hover over the content name.");
    documentLibraryPage.mouseOverContentItem(testContentC8816);
    assertTrue(documentLibraryPage.isRenameIconDisplayed(), "'Rename' icon is not displayed.");


    LOG.info("Step 2: Click on 'Rename' icon.");
    documentLibraryPage.clickRenameIcon();
    assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
    ArrayList<String> expectedButtons = new ArrayList<>(Arrays.asList("Save", "Cancel"));
    assertEquals(documentLibraryPage.verifyButtonsFromRenameContent(expectedButtons),
        expectedButtons.toString(), "Rename content buttons");

    LOG.info(
        "Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
    documentLibraryPage.typeContentName(newFolderName);
    documentLibraryPage.clickButtonFromRenameContent("Save");
    assertTrue(documentLibraryPage.isContentNameDisplayed(newFolderName),
        testContentC8816 + " name updated to: " + newFolderName);
    assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");

  }

  @TestRail(id = "C8817")
  @Test
  public void collaboratorRenameByOthers() {

    String testContentC8817 = "C8817 file" + DataUtil.getUniqueIdentifier();
    String newFileName1 = "newFolderNameC8817";

    LOG.info("Preconditions.");
    contentService.createFolder(adminUser, adminPassword, testContentC8817, siteName6);
    documentLibraryPage.navigate(siteName6);
    documentLibraryPage.renderedPage();
    assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library",
        "Page displayed");

    LOG.info("Step 1: Hover over the content name.");
    documentLibraryPage.mouseOverContentItem(testContentC8817);
    browser.waitInSeconds(1);
    assertTrue(documentLibraryPage.isRenameIconDisplayed(), "'Rename' icon is not displayed.");

    LOG.info("Step 2: Click on 'Rename' icon.");
    documentLibraryPage.clickRenameIcon();
    assertTrue(documentLibraryPage.isContentNameInputField(), "Folder name is text input field.");
    ArrayList<String> expectedButtons = new ArrayList<>(Arrays.asList("Save", "Cancel"));
    assertEquals(documentLibraryPage.verifyButtonsFromRenameContent(expectedButtons),
        expectedButtons.toString(), "Rename content buttons");

    LOG.info(
        "Step 3: Fill in the input field with a new name (e.g. newContentName) and click 'Save' button");
    documentLibraryPage.typeContentName(newFileName1);
    documentLibraryPage.clickButtonFromRenameContent("Save");
    assertTrue(documentLibraryPage.isContentNameDisplayed(newFileName1),
        testContentC8817 + " name updated to: " + newFileName1);
    assertFalse(documentLibraryPage.isContentNameInputField(), "Folder is input field.");

  }

  @TestRail(id = "C8823")
  @Test
  public void collaboratorMoveBySelf() {

    String testFolder3 = "Folder3" + DataUtil.getUniqueIdentifier();
    String testFolder4 = "Folder4" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder3, siteName4);
    content.createFolder(user, password, testFolder4, siteName4);
    documentLibraryPage.navigate(siteName4);

    LOG.info("Step 1: Hover over 'testFolder3', Click 'More...' link, Click 'Move to...''.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder3, "Move to...",
        copyMoveUnzipToDialog);
    assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Move " + testFolder3 + " to...",
        "Displayed pop up");

    LOG.info("Step 2: Set the destination to 'All Sites'.");
    copyMoveUnzipToDialog.clickDestinationButton("All Sites");
    assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName4),
        siteName + " displayed in 'Site' section");

    LOG.info("Step 3: Select your site name.");
    copyMoveUnzipToDialog.clickSite(siteName4);
    ArrayList<String> expectedPath = new ArrayList<>(asList("Documents", testFolder3, testFolder4));
    assertEquals(copyMoveUnzipToDialog.getPathList(), expectedPath.toString(),
        "Step 5: Selected path is not correct.");

    LOG.info("Step 4: Select 'testFolder4' for the path.");
    copyMoveUnzipToDialog.clickPathFolder(testFolder4);

    LOG.info("Step 5: Click 'Move' button. Verify the displayed folders.");
    copyMoveUnzipToDialog.clickButtton("Move");
    documentLibraryPage.renderedPage();
    assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "Move to dialog not displayed");
    assertFalse(documentLibraryPage.isContentNameDisplayed(testFolder3),
        testFolder3 + " displayed in Documents");

    LOG.info(
        "Step 6: Open the 'testFolder4' created in preconditions and verify displayed folders.");
    documentLibraryPage.clickOnFolderName(testFolder4);   
    Assert.assertTrue(documentLibraryPage.getFoldersList().toString().contains(testFolder3), "Displayed folders in " + testFolder4);

  }

  @TestRail(id = "C8824")
  @Test
  public void collaboratorMoveByOthers() {

    String testFolder5 = "Folder5" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder5, siteName5);
    documentLibraryPage.navigate(siteName5);

    LOG.info("Step 1: Hover over 'testFolder1'.");
    documentLibraryPage.mouseOverContentItem(testFolder5);

    LOG.info("Step 2: Click 'More...' link. The Move to option is not available.");
    documentLibraryPage.clickMoreMenu(testFolder5);
    assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolder5, "Move to..."),
        ("Move to...") + " option is displayed for " + testFolder5);

  }

  @TestRail(id = "C8822")
  @Test
  public void collaboratorCopyTo() {

    String testFolder6 = "Folder6" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder6, siteName1);
    documentLibraryPage.navigate(siteName1);

    LOG.info("Step 1: Hover over the created folder and click 'Copy to...'.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder6, "Copy to...",
        copyMoveUnzipToDialog);
    assertEquals(copyMoveUnzipToDialog.getDialogTitle(), "Copy " + testFolder6 + " to...",
        "Displayed pop up");

    LOG.info("Step 2: Set the destination to 'testFolder'.");
    copyMoveUnzipToDialog.clickDestinationButton("All Sites");
    assertTrue(copyMoveUnzipToDialog.isSiteDisplayedInSiteSection(siteName1),
        siteName + " displayed in 'Site' section");
    copyMoveUnzipToDialog.clickSite(siteName1);
    copyMoveUnzipToDialog.clickPathFolder(testFolder6);

    LOG.info("Step 3: Click 'Copy' button");
    copyMoveUnzipToDialog.clickButtton("Copy");
    assertTrue(documentLibraryPage.isOptionsMenuDisplayed(), "'Copy to' dialog not displayed");

    LOG.info("Step 4: Verify displayed folders from Documents.");
    documentLibraryPage.navigate(siteName1);
    assertTrue(documentLibraryPage.isContentNameDisplayed(testFolder6),
        testFolder6 + " displayed in Documents");

    LOG.info(
        "Step 5: Open the 'testfolder2' created in preconditions and verify displayed folders.");
    documentLibraryPage.clickOnFolderName(testFolder6);
    Assert.assertTrue(documentLibraryPage.getFoldersList().toString().contains(testFolder6),
        "Displayed folders in " + testFolder6);

  }

  @TestRail(id = "C8822")
  @Test
  public void collaboratorDeleteBySelf() {

    String testFolder7 = "Folder7" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder7, siteName);
    documentLibraryPage.navigate(siteName);

    LOG.info(
        "Step 1: Mouse Over and click on 'More...' link and choose 'Delete Folder' from the dropdown list.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder7, "Delete Folder", deleteDialog);
    assertEquals(deleteDialog.getHeader(), language.translate("documentLibrary.deleteFolder"),
        "'Delete Folder' pop-up is displayed");
    assertEquals(deleteDialog.getMessage(),
        String.format(language.translate("confirmDeletion.message"), testFolder7));

    LOG.info("Step 2: Click 'Delete' button");
    deleteDialogFolder.confirmDocumentOrFolderDelete();
    assertFalse(documentLibraryPage.isContentNameDisplayed(testFolder7),
        "Documents item list is refreshed and is empty");
    assertFalse(documentLibraryPage.getExplorerPanelDocuments().contains(testFolder7),
        "'DelFolder' is not visible in 'Library' section of the browsing pane.");

  }

  @TestRail(id = "C8822")
  @Test
  public void collaboratorDeleteByOthers() {

    String testFolder8 = "Folder8" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder8, siteName2);
    documentLibraryPage.navigate(siteName2);

    LOG.info("Step 1: Hover 'DelFolder' name from the content item list.");
    documentLibraryPage.mouseOverContentItem(testFolder8);

    LOG.info("Step 2: Click on 'More...' link. The Delete folder option is not available.");
    documentLibraryPage.clickMoreMenu(testFolder8);
    assertFalse(documentLibraryPage.isActionAvailableForLibraryItem(testFolder8, "Delete Folder"),
        ("Delete Folder") + " option is displayed for " + testFolder8);

  }

  @TestRail(id = "C8827")
  @Test
  public void collaboratorManagePermissionsBySelf() {

    String testFolder9 = "Folder9" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder9, siteName3);
    documentLibraryPage.navigate(siteName3);

    LOG.info(
        "Step 1: Hover for 'testFolder' and click on 'Manage Permissions' option from 'More' menu.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder9, "Manage Permissions",
        managePermissionsPage);
    // managePermissionsPage.renderedPage();
    assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolder9,
        "Manage Permissions: " + testFolder9 + " title displayed.");

    LOG.info(
        "Step 2: Make some changes. Add User/Group button. Search for testUser. Click Add Button.");
    managePermissionsPage.searchAndAddUserAndGroup(user);

    LOG.info("Step 3: Click 'Save' button");
    managePermissionsPage.clickButton("Save");
    documentLibraryPage.renderedPage();
    assertEquals(documentLibraryPage.getPageTitle(), "Alfresco » Document Library",
        "Page displayed");

    LOG.info("Step 4: Click 'More' menu, 'Manage Permissions' options.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder9, "Manage Permissions",
        managePermissionsPage);
    // managePermissionsPage.renderedPage();
    assertEquals(managePermissionsPage.getTitle(), "Manage Permissions: " + testFolder9,
        "Manage Permissions: " + testFolder9 + " title displayed.");
    assertTrue(managePermissionsPage.isPermissionAddedForUser(user),
        String.format("User [%s] is not added in permissions.", user));

  }

  @TestRail(id = "C8828")
  @Test
  public void collaboratorManagePermissionsByOthers() {

    String testFolder10 = "Folder10" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder10, siteName);
    documentLibraryPage.navigate(siteName);

    LOG.info(
        "Step 1: Mouse over and click on 'More...' button. 'Manage Permissions' option from 'More' menu must not be displayed.");
    assertFalse(
        documentLibraryPage.isActionAvailableForLibraryItem(testFolder10, "Manage Permissions"),
        "Manage Permissions" + " option is not displayed for " + testFolder10);

  }

  @TestRail(id = "C8829")
  @Test
  public void collaboratorManageAspectsBySelf() {

    String testFolder11 = "Folder11" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder11, siteName3);
    documentLibraryPage.navigate(siteName3);

    LOG.info(
        "Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
    documentLibraryPage.mouseOverFolder(testFolder11);
    documentLibraryPage.clickMoreMenu(testFolder11);
    documentDetailsPage.clickManageAspects();

    LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
    aspectsForm.addElement(0);
    Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"),
        "Aspect is not added to 'Currently Selected' list");
    Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"),
        "Aspect is present on 'Available to Add' list");

    LOG.info("Step 3: Click on 'Apply changes' button.");
    aspectsForm.clickApplyChangesButton();
    Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");
   
  }

  @TestRail(id = "C8830")
  @Test
  public void collaboratorManageAspectsByOthers() {

    String testFolder12 = "Folder12" + DataUtil.getUniqueIdentifier();

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder12, siteName3);
    documentLibraryPage.navigate(siteName3);

    LOG.info(
        "Step 1: Hover for 'testFolder' and click on 'Manage Aspects' option from 'More' menu'.");
    documentLibraryPage.mouseOverFolder(testFolder12);
    documentLibraryPage.clickMoreMenu(testFolder12);
    documentDetailsPage.clickManageAspects();

    LOG.info("Step 2: Make some changes, e.g: Add an aspect to your folder.");
    aspectsForm.addElement(0);
    Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"),
        "Aspect is not added to 'Currently Selected' list");
    Assert.assertFalse(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"),
        "Aspect is present on 'Available to Add' list");

    LOG.info("Step 3: Click on 'Apply changes' button.");
    aspectsForm.clickApplyChangesButton();
    Assert.assertTrue(documentCommon.getFadedDetailsList().contains("No Categories"), "Folder does not have Classifiable aspect added.");

  }

  @TestRail(id = "C8834")
  @Test
  public void collaboratorAddComment() {

    String testFolder13 = "Folder13" + DataUtil.getUniqueIdentifier();
    String comment = "Test comment for C8834";

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder13, siteName);
    documentLibraryPage.navigate(siteName);

    LOG.info("Step 1: Hover over a document and press \"Comment\"");
    social.clickCommentLink(testFolder13);
    assertEquals(documentDetailsPage.getPageTitle(), "Alfresco » Folder Details",
        "Displayed page=");

    LOG.info(
        "Step 2: In the 'Comments' area of 'Folder Details' page write a comment and press 'Add Comment' button");
    documentDetailsPage.addComment(comment);
    assertEquals(documentDetailsPage.getCommentContent(), comment,
        "Comment content is not as expected.");

    LOG.info("Step 3: Go back to 'Document Library' page");
    documentLibraryPage.navigate();
    assertEquals(social.getNumberOfComments(testFolder13), 1, "Number of comments=");

  }

  @TestRail(id = "C8835")
  @Test
  public void collaboratorEditCommentBySelf() {

    String testFolder14 = "Folder14" + DataUtil.getUniqueIdentifier();
    String comment = "Test comment for C8835";
    String editedComment = "Test comment edited for C8835";

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder14, siteName2);
    documentLibraryPage.navigate(siteName2);
    social.clickCommentLink(testFolder14);
    documentDetailsPage.addComment(comment);
    documentLibraryPage.navigate();

    LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder14, "View Details",
        documentPreviewPage);

    LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
    documentPreviewPage.renderedPage();
    Assert.assertTrue(documentPreviewPage.isEditButtonDisplayedForComment(comment),
        "Edit button is not displayed for comment");
    documentPreviewPage.clickOnEditComment(comment);
    Assert.assertTrue(documentPreviewPage.isEditCommentDisplayed(),
        "Edit comment is not displayed");

    LOG.info("Step 3: Edit comment text and click on Save.");
    documentPreviewPage.editComment(editedComment);
    documentPreviewPage.clickOnSaveButtonEditComment();
    documentLibraryPage.navigate();
    Assert.assertEquals(social.getNumberOfComments(testFolder14), 1, "Number of comments=");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder14, "View Details",
        documentPreviewPage);
    Assert.assertEquals(documentPreviewPage.getCommentContent(editedComment), editedComment,
        "Edited comment text is not correct");

  }

  @TestRail(id = "C8836")
  @Test
  public void collaboratorEditCommentByOthers() {

    String testFolder15 = "Folder15" + DataUtil.getUniqueIdentifier();
    String comment1 = "Test comment for C8836";
    String editedComment1 = "Test comment edited for C8836";

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder15, siteName2);
    documentLibraryPage.navigate(siteName2);
    social.clickCommentLink(testFolder15);
    documentDetailsPage.addComment(comment1);
    documentLibraryPage.navigate();

    LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder15, "View Details",
        documentPreviewPage);

    LOG.info("Step 2: Hover over the existing comment and click on 'Edit Comment' button.");
    Assert.assertTrue(documentPreviewPage.isEditButtonDisplayedForComment(comment1),
        "Edit button is not displayed for comment");
    documentPreviewPage.clickOnEditComment(comment1);
    Assert.assertTrue(documentPreviewPage.isEditCommentDisplayed(),
        "Edit comment is not displayed");

    LOG.info("Step 3: Edit comment text and click on Save.");
    documentPreviewPage.editComment(editedComment1);
    documentPreviewPage.clickOnSaveButtonEditComment();
    documentLibraryPage.navigate();
    Assert.assertEquals(social.getNumberOfComments(testFolder15), 1, "Number of comments=");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder15, "View Details",
        documentPreviewPage);
    Assert.assertEquals(documentPreviewPage.getCommentContent(editedComment1), editedComment1,
        "Edited comment text is not correct");

  }

  @TestRail(id = "C8837")
  @Test
  public void collaboratorDeleteCommentBySelf() {

    String testFolder16 = "Folder16" + DataUtil.getUniqueIdentifier();
    String comment2 = "Test comment for C8837";

    LOG.info("Preconditions.");
    content.createFolder(user, password, testFolder16, siteName1);
    documentLibraryPage.navigate(siteName1);
    social.clickCommentLink(testFolder16);
    documentDetailsPage.addComment(comment2);
    documentLibraryPage.navigate();

    LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder16, "View Details",
        documentPreviewPage);

    LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
    documentPreviewPage.clickDeleteComment(comment2);
    Assert.assertTrue(documentPreviewPage.isDeleteCommentPromptDisplayed(),
        "Delete Comment prompt is not displayed");

    LOG.info("Step 3: Click 'Delete' button.");
    documentPreviewPage.clickDeleteOnDeleteComment();
    browser.waitUntilElementVisible(documentPreviewPage.noComments);
    Assert.assertEquals(documentPreviewPage.getNoCommentsText(), "No comments",
        "No comments notification is not displayed");

  }

  @TestRail(id = "C8838")
  @Test
  public void collaboratorDeleteCommentByOthers() {

    String testFolder17 = "Folder17" + DataUtil.getUniqueIdentifier();
    String comment3 = "Test comment for C8838";

    LOG.info("Preconditions.");
    content.createFolder(adminUser, adminPassword, testFolder17, siteName1);
    documentLibraryPage.navigate(siteName1);
    social.clickCommentLink(testFolder17);
    documentDetailsPage.addComment(comment3);
    documentLibraryPage.navigate();

    LOG.info("Step 1: Hover over a document and click on 'View Details' button.");
    documentLibraryPage.clickDocumentLibraryItemAction(testFolder17, "View Details",
        documentPreviewPage);

    LOG.info("Step 2: Hover over the existing comment and click on 'Delete Comment' button.");
    documentPreviewPage.clickDeleteComment(comment3);
    Assert.assertTrue(documentPreviewPage.isDeleteCommentPromptDisplayed(),
        "Delete Comment prompt is not displayed");

    LOG.info("Step 3: Click 'Delete' button.");
    documentPreviewPage.clickDeleteOnDeleteComment();
    browser.waitUntilElementVisible(documentPreviewPage.noComments);
    Assert.assertEquals(documentPreviewPage.getNoCommentsText(), "No comments",
        "No comments notification is not displayed");

  }

}
