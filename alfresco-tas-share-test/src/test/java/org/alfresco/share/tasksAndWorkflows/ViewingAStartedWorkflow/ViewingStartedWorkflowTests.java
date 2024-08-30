package org.alfresco.share.tasksAndWorkflows.ViewingAStartedWorkflow;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
@Slf4j
/**
 * @author Razvan.Dorobantu
 */
public class ViewingStartedWorkflowTests extends BaseTest
{
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflowService;

    //@Autowired
    WorkflowDetailsPage workflowDetailsPage;

    // @Autowired
    MyTasksPage myTasksPage;

    //@Autowired
    EditTaskPage editTaskPage;

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> c284893testUser = new ThreadLocal<>();
    private final ThreadLocal<UserModel> c284893user2 = new ThreadLocal<>();

    @TestRail (id = "C8425")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewWorkflowIveStarted()
    {
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);

        log.info("Precondition: Create user and a workflow.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), testUser.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();

        log.info("STEP 2: Click on 'View History' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickViewHistory(workflowName);

        log.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser.get().getUsername()));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser.get().getUsername()));

        deleteUsersIfNotNull(testUser.get());
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
    }

    @TestRail (id = "C8426")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewWorkflowAssignedToOtherUser()
    {
        myTasksPage = new MyTasksPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);

        log.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), user2.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(user2.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();

        log.info("STEP 2: From 'My Tasks' page click 'Workflow Details' button.");
        myTasksPage.clickViewWorkflow(workflowName);

        log.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"), "Priority is: " + workflowDetailsPage.getPriority());
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser.get().getUsername()));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user2.get().getUsername()));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteUsersIfNotNull(user2.get());
    }

    @TestRail (id = "C8427")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewWorkflowIveStartedOptions()
    {
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);

        log.info("Precondition: Create user and a workflow.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), testUser.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: Verify 'View History' and 'Cancel Workflow' options are displayed for the workflow.");
        Assert.assertTrue(workflowsIveStartedPage.isCancelWorkflowOptionDisplayed(workflowName));
        Assert.assertTrue(workflowsIveStartedPage.isViewHistoryOptionDisplayed(workflowName));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C8428")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewOptionsAvailableForWorkflowsAssignedToOthers()
    {
        myTasksPage = new MyTasksPage(webDriver);

        log.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), user2.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(user2.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        log.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user2.get().getUsername());
        deleteUsersIfNotNull(user2.get());
    }

    @TestRail (id = "C8458")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void verifyWorkflowIveStartedPage()
    {
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);

        log.info("Precondition: Create a user and login.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: Verify filters are displayed on the left side of the page.");
        Assert.assertTrue(workflowsIveStartedPage.isDueFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isPriorityFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isStartedFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isWorkflowsFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isWorkflowTypeFilterDisplayed());

        log.info("STEP 3: Verify 'Active Workflows' bar is displayed.");
        Assert.assertTrue(workflowsIveStartedPage.isActiveWorkflowsBarDisplayed());

        log.info("STEP 3: Verify 'Start Workflow' button is displayed.");
        workflowsIveStartedPage.assertStartWorkflowIsDisplayed();

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C8461")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void verifyMyTasksPage()
    {
        myTasksPage = new MyTasksPage(webDriver);

        log.info("Precondition: Create a user and login.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        log.info("STEP 2: Verify filters are displayed on the left side of the page.");
        Assert.assertTrue(myTasksPage.isDueFilterDisplayed());
        Assert.assertTrue(myTasksPage.isPriorityFilterDisplayed());
        Assert.assertTrue(myTasksPage.isTasksFilterDisplayed());
        Assert.assertTrue(myTasksPage.isAssigneeFilterDisplayed());

        log.info("STEP 3: Verify 'Active Tasks' bar is displayed.");
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed());

        log.info("STEP 3: Verify 'Start Workflow' button is displayed.");
        myTasksPage.assertStartWorkflowIsDisplayed();

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C8460")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewOptionsAvailableForTasksIveStarted()
    {
        myTasksPage = new MyTasksPage(webDriver);

        log.info("Precondition: Create user and a workflow.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), testUser.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        log.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C8500")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewMyTaskAndWorkflowDetailsPages()
    {
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);

        log.info("Precondition: Create user and a workflow.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), workflowName, new Date(), testUser.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: From 'Workflows I've Started' page click on the title of the workflow and verify details.");
        workflowsIveStartedPage.clickOnWorkflowTitle(workflowName);
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser.get().getUsername()));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser.get().getUsername()));

        log.info("STEP 3: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        log.info("STEP 4: From 'My Tasks' page click on the title of the task and verify details.");
        myTasksPage.clickOnTaskTitle(workflowName);
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getPriority().contains("Medium"));
        Assert.assertTrue(editTaskPage.getOwner().contains(testUser.get().getUsername()));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + testUser.get().getUsername());
        deleteUsersIfNotNull(testUser.get());
    }

    @TestRail (id = "C284893")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void viewWorkflowAssignedToNonAdminUserTaskCompleted()
    {
        myTasksPage = new MyTasksPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);

        log.info("Precondition: Create 2 users and a workflow assigned by " + c284893testUser + " to " + c284893user2 + ".");
        c284893testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        c284893user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowService.startNewTask(c284893testUser.get().getUsername(), c284893testUser.get().getPassword(), workflowName, new Date(), c284893user2.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        authenticateUsingLoginPage(c284893user2.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        myTasksPage.assertBrowserPageTitleIs("Alfresco » My Tasks");

        log.info("STEP 2: From 'My Tasks' page click 'Workflow Details' button for the workflow created in precondition.");
        workflowDetailsPage = myTasksPage.clickViewWorkflow(workflowName);

        log.info("STEP 3: Verify the details of the workflow created in precondition are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"), "Priority is:" + workflowDetailsPage.getPriority());

        log.info("STEP 4: Click on Task Done action.");
        myTasksPage.navigate();
        myTasksPage.editTask(workflowName);
        editTaskPage.clickTaskDoneButton();
        myTasksPage.navigateToCompletedTasks();
        Assert.assertTrue(myTasksPage.getStatusCompleted(workflowName).contains("Completed"));

        log.info("STEP 5: log in with " + c284893testUser + ", open My task page");
        authenticateUsingCookies(c284893testUser.get());
        myTasksPage.navigateByMenuBar();
        myTasksPage.assertBrowserPageTitleIs("Alfresco » My Tasks");

        log.info("STEP 6: Click on Workflow name, verify the workflow details");
        myTasksPage.clickOnTaskTitle(workflowName);
        myTasksPage.navigate();
        myTasksPage.editTask(workflowName);
        editTaskPage.clickTaskDoneButton();
        myTasksPage.navigateToCompletedTasks();
        Assert.assertTrue(myTasksPage.getStatusCompleted(workflowName).contains("Completed"));

        log.info("STEP 7: Navigate to WorkFlow I've started page, on section Completed, click on workflow name created by you and assigned to the user and see the details ");
        workflowsIveStartedPage.navigateByMenuBar();
        workflowsIveStartedPage.clickCompletedFilter();
        workflowsIveStartedPage.clickOnWorkflowTitle(workflowName);
        Assert.assertTrue(workflowDetailsPage.getStatus().contains("Workflow is Complete"));
    }


}