package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.sharedFiles.actions;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.constants.ShareGroups;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.alfresco.test.AlfrescoTest;

@Slf4j
public class SharedFilesManageAspectsTests extends BaseTest
{

    //@Autowired
    private AspectsForm aspectsForm;

    //@Autowired
    private SharedFilesPage sharedFilesPage;
    private DeleteDialog deleteDialog;
    private static final String classifiableAspect = "Classifiable";

    private UserModel testUser1;
    private UserModel testUser2;
    private final String user2 = "User2" + org.alfresco.utility.data.RandomData.getRandomAlphanumeric();
    private final String user1 = "User1" + org.alfresco.utility.data.RandomData.getRandomAlphanumeric();
    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        final String password = "password";


        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createUser(user1, password);
        testUser2 = dataUser.usingAdmin().createUser(user2, password);
        getCmisApi().authenticateUser(getAdminUser());

        aspectsForm = new AspectsForm(webDriver);
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
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1 })
    public void checkManageAspectActions() throws Exception
    {
        FolderModel testFolderC8038 = FolderModel.getRandomFolderModel();
        log.info("Precondition: create folder in Shared Files as user1");
        getCmisApi().authenticateUser(testUser1).usingUser(testUser1).usingShared().createFolder(testFolderC8038).assertThat().existsInRepo();

        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .assertIsFolderPresentInList(testFolderC8038.getName());

        log.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        sharedFilesPage
            .selectItemAction(testFolderC8038.getName(), ItemActions.MANAGE_ASPECTS);

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
        sharedFilesPage
            .selectItemAction(testFolderC8038.getName(), ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }

    @TestRail (id = "C8034")
    @AlfrescoTest(jira = "AT-10714")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1 })
    public void manageAspectsApplyChanges() throws Exception
    {
        FolderModel testFolderC8034 = FolderModel.getRandomFolderModel();
        log.info("Precondition: create folder in Shared Files as user1");
        getCmisApi().authenticateUser(testUser1).usingUser(testUser1).usingShared().createFolder(testFolderC8034).assertThat().existsInRepo();

        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .assertIsFolderPresentInList(testFolderC8034.getName());

        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        sharedFilesPage
            .selectItemAction(testFolderC8034.getName(), ItemActions.MANAGE_ASPECTS);

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        aspectsForm
            .assertAspactPresentInAvailableList(classifiableAspect)
            .addAspect(classifiableAspect);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(classifiableAspect);

        log.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm
            .clickApplyChangesButton();
        sharedFilesPage
            .selectItemAction(testFolderC8034.getName(), ItemActions.MANAGE_ASPECTS);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(classifiableAspect);

        aspectsForm
            .clickCloseButton();
        log.info("Delete folder");
        sharedFilesPage
            .selectItemAction(testFolderC8034.getName(), ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }


    @TestRail (id = "C13761")
    @AlfrescoTest(jira = "XAT-10716")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1 })
    public void manageAspectsActionMissing() throws Exception
    {
        FolderModel testFolderC13761 = FolderModel.getRandomFolderModel();
        log.info("Precondition: create folder in Shared Files as user2");
        getCmisApi().authenticateUser(testUser2).usingUser(testUser2).usingShared().createFolder(testFolderC13761).assertThat().existsInRepo();

        log.info("login wih user1 and navigate shared folder  ");
        authenticateUsingLoginPage(testUser1);
        sharedFilesPage
            .navigateByMenuBar()
            .assertIsFolderPresentInList(testFolderC13761.getName());

        log.info("Verify the Manage Aspects action is not available for another user's folder");
        sharedFilesPage
            .assertActionItem_Not_AvailableInTheDocumentLibraryItems(testFolderC13761.getName(), ItemActions.MANAGE_ASPECTS);

        log.info("Delete folder");
        authenticateUsingLoginPage(getAdminUser());
        sharedFilesPage
            .navigateByMenuBar()
            .selectItemAction(testFolderC13761.getName(), ItemActions.DELETE_FOLDER);
        deleteDialog
            .confirmDeletion();

    }
}
