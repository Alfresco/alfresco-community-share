package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyTasksDashletTest extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyTasksDashlet myTasksDashlet;

    private UserModel addUserAfterWorkflow, taskUser;
    private TaskModel taskModel;
    private GroupModel testGroup;
    private SiteModel site;
    private FileModel file;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        addUserAfterWorkflow = dataUser.usingAdmin().createRandomTestUser();
        taskUser = dataUser.createRandomTestUser();
        site = dataSite.usingUser(taskUser).createPublicRandomSite();
        file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        cmisApi.authenticateUser(taskUser).usingSite(site).createFile(file);
        taskModel = dataWorkflow.usingUser(taskUser)
            .usingSite(site).usingResource(file).createNewTaskAndAssignTo(taskUser);

        testGroup = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(taskUser).addUserToGroup(testGroup);

    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(addUserAfterWorkflow, taskUser);
        dataGroup.usingAdmin().deleteGroup(testGroup);
        dataSite.usingAdmin().deleteSite(site);
    }

    @TestRail (id = "C2122")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkMyTasksDashlet()
    {
        setupAuthenticatedSession(taskUser);
        myTasksDashlet.assertDashletTitleEquals(language.translate("myTasksDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_TASKS)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageIs(language.translate("myTasksDashlet.balloonMessage"))
            .assertStartWorkflowIsDisplayed()
            .assertCompletedTasksButtonIsDisplayed()
            .assertActiveTasksButtonIsDisplayed()
            .assertFilterTasksIsDisplayed()
                .assertTaskIsDisplayed(taskModel.getMessage())
                .assertViewIsDisplayedForTask(taskModel.getMessage())
                .assertEditIsDisplayedForTask(taskModel.getMessage())
                .assertTaskIsNotStartedYet(taskModel.getMessage())
                .assertDueDateIsCorrect(taskModel.getMessage(), taskModel.getDueDate())
                .assertTasksNavigationIs(1, 1)
                .clickStartWorkFlow()
                    .assertStartWorkflowPageIsOpened();

        userDashboard.navigate(taskUser);
        myTasksDashlet.clickActiveTasksLink().assertActiveTasksTitleIsDisplayed();

        userDashboard.navigate(taskUser);
        myTasksDashlet.clickOnCompletedTasksLink().assertCompletedTasksTitleIsDisplayed();

        userDashboard.navigate(taskUser);
        myTasksDashlet.clickTaskName(taskModel.getMessage()).assertEditTaskPageIsOpened();

        userDashboard.navigate(taskUser);
        myTasksDashlet.editTask(taskModel.getMessage()).assertEditTaskPageIsOpened();

        userDashboard.navigate(taskUser);
        myTasksDashlet.viewTask(taskModel.getMessage()).assertViewTaskPageIsOpened();
    }

    @TestRail (id = "C8548, C8597")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void userAddedAfterWorkflowStarts()
    {
        dataWorkflow.usingUser(taskUser).usingSite(site).usingResource(file)
            .createGroupReviewTaskAndAssignTo(testGroup);
        dataGroup.usingUser(addUserAfterWorkflow).addUserToGroup(testGroup);
        setupAuthenticatedSession(addUserAfterWorkflow);

        LOG.info("STEP 4: Verify user's 'My task' dashlet;");
        userDashboard.navigate(addUserAfterWorkflow);
        myTasksDashlet.assertEmptyDashletMessageIsCorrect();
    }
}
