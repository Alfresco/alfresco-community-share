package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SharedFilesManageAspectsTests extends BaseTest
{

    //@Autowired
    private AspectsForm aspectsForm;

    //@Autowired
    private SharedFilesPage sharedFilesPage;
    private MyFilesPage myFilesPage;
    private DeleteDialog deleteDialog;

    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String folderNameC8038 = String.format("testFolderC8038%s", RandomData.getRandomAlphanumeric());
    private String folderNameC8034 = String.format("testFolderC8038%s", RandomData.getRandomAlphanumeric());
    private String folderNameC13761 = String.format("testFolderC13761%s", RandomData.getRandomAlphanumeric());

    private final String sharedFolderName = "Shared";
    private RepositoryPage repositoryPage;
    private NewFolderDialog newFolderDialog;
    private DocumentLibraryPage documentLibraryPage;
    private static final String classifiableAspect = "Classifiable";

    private UserModel testUser1;
    private UserModel testUser2;
    private final String user2 = "User2" + RandomData.getRandomAlphanumeric();
    private final String user1 = "User1" + RandomData.getRandomAlphanumeric();
    private final String password = "password";
    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {


        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());

        repositoryPage = new RepositoryPage(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        myFilesPage = new MyFilesPage(webDriver);
        newFolderDialog = new  NewFolderDialog(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        documentLibraryPage = new DocumentLibraryPage(webDriver);
        sharedFilesPage = new SharedFilesPage(webDriver);
         deleteDialog = new DeleteDialog(webDriver);

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(testUser1);
        deleteUsersIfNotNull(testUser2);

    }

    @TestRail (id = "C8038")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void checkManageAspectActions()
    {
        log.info("Precondition: create folder in Shared folder from user ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderNameC8038)
            .clickSave();
        myFilesPage
            .isContentNameDisplayed(folderNameC8038);

        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        log.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        repositoryPage
            .select_ItemsAction(folderNameC8038, ItemActions.MANAGE_ASPECTS);

        aspectsForm
            .assertIsAspectsFormTitleDisplayed()
            .assertIsAvailableToAddPanelDisplayed()
            .assertIsCurrentlySelectedPanel()
            .assertAreAddButtonsDisplayed()
            .assertmIsApplyChangesButtonDisplayed()
            .assertAreRemoveButtonsDisplayed()
            .assertmIsCancelButtonDisplayed()
            .assertmIsCloseButtonDisplayed();
        aspectsForm
            .clickCloseButton();
        log.info("Delete folder");
        repositoryPage
            .select_ItemsAction(folderNameC8038, ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }

    @TestRail (id = "C8034")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, "tobefixed" })
    public void manageAspectsApplyChanges()
    {
        log.info("Precondition: create folder in Shared folder from user ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderNameC8034)
            .clickSave();
        myFilesPage
            .isContentNameDisplayed(folderNameC8034);

        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);

        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage
            .selectItemAction(folderNameC8034, ItemActions.MANAGE_ASPECTS);

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm
            .assertAspactPresentInAvailableList(classifiableAspect)
            .addAspect(classifiableAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(classifiableAspect);

        log.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm
            .clickApplyChangesButton();
        repositoryPage
            .selectItemAction(folderNameC8034, ItemActions.MANAGE_ASPECTS);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(classifiableAspect);

        aspectsForm
            .clickCloseButton();
        log.info("Delete folder");
        repositoryPage
            .select_ItemsAction(folderNameC8034, ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }


    @TestRail (id = "C13761")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void manageAspectsActionMissing()
    {
        log.info("Precondition: create folder in Shared folder from user2 ");
        authenticateUsingLoginPage(testUser2);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(folderNameC13761)
            .clickSave();
        myFilesPage
            .isContentNameDisplayed(folderNameC13761);
        log.info("login wih user1 and navigate shared folder  ");
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .isContentNameDisplayed(folderNameC13761);

        log.info(" Hover over the folder and validated more option is not available ");
        sharedFilesPage
            .mouseOverContentItem(folderNameC13761);
        documentLibraryPage
            .assertisMoreMenuNotDisplayed(folderNameC13761);
        List<String> notExpectedActions = Arrays
            .asList("Manage Aspects");
        documentLibraryPage.assertActionsNoteAvailableForLibrary(folderNameC13761,notExpectedActions);
        log.info("Delete folder");
        authenticateUsingLoginPage(getAdminUser());
        repositoryPage
            .navigate();
        repositoryPage
            .click_FolderName(sharedFolderName);
        repositoryPage
            .select_ItemsAction(folderNameC13761, ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }
}
