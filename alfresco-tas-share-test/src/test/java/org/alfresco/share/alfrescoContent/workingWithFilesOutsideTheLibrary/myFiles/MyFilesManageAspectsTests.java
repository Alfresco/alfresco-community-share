package org.alfresco.share.alfrescoContent.workingWithFilesOutsideTheLibrary.myFiles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.constants.ShareGroups;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.alfrescoContent.aspects.AspectsForm;
import org.alfresco.po.share.alfrescoContent.buildingContent.NewFolderDialog;
import org.alfresco.po.share.site.ItemActions;
import org.alfresco.share.BaseTest;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class MyFilesManageAspectsTests extends BaseTest
{
    //@Autowired
    private AspectsForm aspectsForm;
    private NewFolderDialog newFolderDialog;

    //@Autowired
    private MyFilesPage myFilesPage;

    private String TestFolder = String.format("TestFolder%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    @BeforeMethod(alwaysRun = true)
    public void createPrecondition()
    {
        log.info("PreCondition 1: Creating a TestUser");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        authenticateUsingCookies(user.get());

        myFilesPage = new MyFilesPage(webDriver);
        aspectsForm = new AspectsForm(webDriver);
        newFolderDialog = new NewFolderDialog(webDriver);

        log.info("PreCondition 2: Creating a Folder");
        myFilesPage
            .navigate()
            .assertBrowserPageTitleIs("Alfresco » My Files");
        myFilesPage
            .click_CreateButton()
            .click_FolderLink();
        newFolderDialog
            .typeName(TestFolder)
            .typeTitle("TestTitle")
            .typeDescription("TestDescription")
            .clickSave();
        myFilesPage
            .isContentNameDisplayed("TestFolder");

    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C7814")
    @Test(groups = { TestGroup.SANITY, TestGroup.CONTENT })
    public void checkManageAspectsForm()
    {
        log.info("Step1: Click 'More'->'Manage Aspects' action for created folder and verify the Manage Aspects form");
        myFilesPage
            .select_ItemAction(TestFolder,ItemActions.MANAGE_ASPECTS);

        Assert.assertTrue(aspectsForm.isAspectsFormTitleDisplayed(), "Aspects for the file form is not displayed");
        Assert.assertTrue(aspectsForm.isAvailableToAddPanelDisplayed(), "Available to Add panel is not displayed");
        Assert.assertTrue(aspectsForm.isCurrentlySelectedPanel(), "Currently Selected panel is not displayed");
        Assert.assertTrue(aspectsForm.areAddButtonsDisplayed(), "Add buttons are not displayed for all the available to add aspects");
        Assert.assertTrue(aspectsForm.areRemoveButtonsDisplayed(), "Remove buttons are not displayed for all the selected aspects");
        Assert.assertTrue(aspectsForm.isSaveButtonDisplayed(), "Apply Changes button is not displayed");
        Assert.assertTrue(aspectsForm.isCancelButtonDisplayed(), "Cancel button is not displayed");
        Assert.assertTrue(aspectsForm.isCloseButtonDisplayed(), "Close button is not displayed");

    }

    @TestRail (id = "C7810")
    @AlfrescoTest (jira = "/XAT-10499")
    @Test (groups = { TestGroup.SANITY, TestGroup.CONTENT, ShareGroups.SHARE_PRIORITY_1})
    public void manageAspectsApplyChanges()  {
        log.info("Step1: Click 'More'->'Manage Aspects' action for the created folder");
        myFilesPage
            .select_ItemAction(TestFolder,ItemActions.MANAGE_ASPECTS);

        log.info("Step2: From 'Available to Add' list, click 'Add' icon next to an aspect and verify it's displayed in 'Currently Selected' list");
        Assert.assertTrue(aspectsForm.isAspectPresentOnAvailableAspectList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
        aspectsForm
            .addAspect("Classifiable");
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");

        log.info("Step3: Click 'Apply Changes' and verify the aspect is added");
        aspectsForm
            .clickApplyChangesButton();
        myFilesPage
            .selectItemAction("TestFolder", ItemActions.MANAGE_ASPECTS);
        Assert.assertTrue(aspectsForm.isAspectPresentOnCurrentlySelectedList("Classifiable"), "Aspect is not added to 'Currently Selected' list");
    }

}
