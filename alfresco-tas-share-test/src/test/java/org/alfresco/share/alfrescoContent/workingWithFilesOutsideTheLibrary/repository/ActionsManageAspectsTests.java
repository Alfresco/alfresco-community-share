package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.repository;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.FolderModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ActionsManageAspectsTests extends BaseTest
{
    private RepositoryPage repositoryPage;
    private DeleteDialog deleteDialog;
    private UserModel testUser1;
    private FolderModel testFolder;
    private FolderModel testFolderC13763;
    private FolderModel testFolderC13764;
    private AspectsForm aspectsForm;
    private static final String classifiableAspect = "Classifiable";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        repositoryPage = new RepositoryPage(webDriver);
        deleteDialog = new DeleteDialog(webDriver);
        aspectsForm = new AspectsForm(webDriver);

        log.info("PreCondition1: Any test user is created");
        testUser1 = dataUser.usingAdmin().createRandomTestUser();
        getCmisApi().authenticateUser(getAdminUser());

        log.info("Create a Folder in Admin Repository-> User Homes ");
        authenticateUsingLoginPage(getAdminUser());

        testFolder = FolderModel.getRandomFolderModel();
        getCmisApi().usingAdmin().usingUserHome(testUser1.getUsername()).createFolder(testFolder).assertThat().existsInRepo();

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C8254")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkAspectsForm()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername())
            .assertFileIsDisplayed(testFolder.getName());

        log.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        repositoryPage
            .select_ItemsAction(testFolder.getName(), ItemActions.MANAGE_ASPECTS);
        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not displayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not displayed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel is not displayed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Changes button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");
    }

    @TestRail (id = "C8250")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void manageAspectsApplyChanges()
    {
        authenticateUsingLoginPage(testUser1);
        repositoryPage
            .navigateByMenuBar()
            .click_FolderName("User Homes")
            .clickOnFolderName(testUser1.getUsername());

        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage
            .selectItemAction(testFolder.getName(), ItemActions.MANAGE_ASPECTS);

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
            .selectItemAction(testFolder.getName(), ItemActions.MANAGE_ASPECTS);
        aspectsForm
            .assertAspactPresentInCurrentlySelectedList(classifiableAspect);
    }

    @TestRail (id = "C13763")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void manageAspectsForNonAdminOnRepositoryMainFolder() throws Exception {

        log.info("PreCondition: Create a Folder in Repository & Login with Non Admin User - Navigate to Repository Page");
        testFolderC13763 = FolderModel.getRandomFolderModel();
        getCmisApi().usingRoot().createFolder(testFolderC13763).assertThat().existsInRepo();

        authenticateUsingLoginPage(testUser1);
        repositoryPage.navigateByMenuBar();

        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage
            .mouseOverContentItem(testFolderC13763.getName());
        repositoryPage
            .assertIsMoreMenuNotDisplayed(testFolderC13763.getName())
            .assertActionItem_Not_AvailableInTheRepositoryLibraryItems(testFolderC13763.getName(),ItemActions.MANAGE_ASPECTS);
    }

    @TestRail (id = "C13764")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void manageAspectsAdminOnRepositoryMainFolder() throws Exception {

        log.info("PreCondition: Admin Create a Folder in Repository & Navigate to Repository Page");
        testFolderC13764 = FolderModel.getRandomFolderModel();
        getCmisApi().usingRoot().createFolder(testFolderC13764).assertThat().existsInRepo();
        repositoryPage.navigateByMenuBar();

        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        repositoryPage
            .assertFileIsDisplayed(testFolderC13764.getName());
        repositoryPage
            .mouseOverContentItem(testFolderC13764.getName());
        repositoryPage
            .assertIsMoreMenuDisplayed(testFolderC13764.getName())
            .assertActionItem_AvailableInTheRepositoryLibraryItems(testFolderC13764.getName(), ItemActions.MANAGE_ASPECTS);

    }

}
