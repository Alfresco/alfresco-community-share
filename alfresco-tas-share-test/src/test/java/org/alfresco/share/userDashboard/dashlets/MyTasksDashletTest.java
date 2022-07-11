package org.alfresco.share.userDashboard.dashlets;

import static org.alfresco.share.TestUtils.FILE_CONTENT;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.DataWorkflow;
import org.alfresco.utility.model.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MyTasksDashletTest extends AbstractUserDashboardDashletsTests
{
    private MyTasksDashlet myTasksDashlet;

    private final ThreadLocal<DataWorkflow> dataWorkflow = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myTasksDashlet = new MyTasksDashlet(webDriver);
        dataWorkflow.set(applicationContext.getBean(DataWorkflow.class));

        user.set(dataUser.usingAdmin().createRandomTestUser());
        site.set(dataSite.usingUser(user.get()).createPublicRandomSite());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2122")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkMyTasksDashlet()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        getCmisApi().authenticateUser(user.get())
            .usingSite(site.get()).createFile(file).assertThat().existsInRepo();
        TaskModel taskModel = dataWorkflow.get().usingUser(user.get())
            .usingSite(site.get()).usingResource(file).createNewTaskAndAssignTo(user.get());

        userDashboardPage.navigate(user.get());
        myTasksDashlet.assertDashletTitleEquals(language.translate("myTasksDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_TASKS)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myTasksDashlet.balloonMessage"))
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

        userDashboardPage.navigate(user.get());
        myTasksDashlet.clickActiveTasksLink().assertActiveTasksTitleIsDisplayed();

        userDashboardPage.navigate(user.get());
        myTasksDashlet.clickOnCompletedTasksLink().assertCompletedTasksTitleIsDisplayed();

        userDashboardPage.navigate(user.get());
        myTasksDashlet.clickTaskName(taskModel.getMessage()).assertEditTaskPageIsOpened();

        userDashboardPage.navigate(user.get());
        myTasksDashlet.editTask(taskModel.getMessage()).assertEditTaskPageIsOpened();

        userDashboardPage.navigate(user.get());
        myTasksDashlet.viewTask(taskModel.getMessage()).assertViewTaskPageIsOpened();
    }

    @TestRail (id = "C8548, C8597")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void userAddedAfterWorkflowStarts()
    {
        FileModel file = FileModel.getRandomFileModel(FileType.TEXT_PLAIN, FILE_CONTENT);
        GroupModel testGroup = dataGroup.usingAdmin().createRandomGroup();
        UserModel user2 = getDataUser().usingAdmin().createRandomTestUser();

        dataWorkflow.get().usingUser(user.get())
            .usingSite(site.get()).usingResource(file)
                .createGroupReviewTaskAndAssignTo(testGroup);
        dataGroup.usingUser(user2).addUserToGroup(testGroup);

        userDashboardPage.navigate(user2);
        myTasksDashlet.assertEmptyDashletMessageIsCorrect();

        dataGroup.usingAdmin().deleteGroup(testGroup);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
