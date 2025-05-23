package org.alfresco.share.tasksAndWorkflows.DeletingAWorkflow;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
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
public class DeletingWorkflowsTests extends BaseTest
{
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    // @Autowired
    MyTasksPage myTasksPage;

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();

    @TestRail (id = "C8501")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void deleteWorkflow()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);

        String workflowID = workflow.startNewTask(user1.get().getUsername(), user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        workflow.taskDone(user1.get().getUsername(), user1.get().getPassword(), workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        workflow.taskDone(user1.get().getUsername(), user1.get().getPassword(), workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is not completed.", workflowName));

        log.info("STEP 2: Click on 'Completed' option from the left side panel");
        workflowsIveStartedPage.clickCompletedFilter();
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertTrue(workflows.contains(workflowName), String.format("Workflow: %s is completed.", workflowName));

        log.info("STEP 3: Click on 'Delete Workflow' option.");
        workflowsIveStartedPage.clickDeleteWorkflow(workflowName, true);
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is not deleted.", workflowName));

        log.info("STEP 4: Verify the deleted workflow is not present in 'My Tasks' page.");
        myTasksPage.navigate();
        myTasksPage.navigateToCompletedTasks();
        myTasksPage.assertNoTaskIsDisplayed();
//        Assert.assertFalse(myTasksPage.assertTaskNameEqualsTo(workflowName), String.format("Workflow: %s is found in 'My Tasks' page.", workflowName));

        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C8502")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void deleteWorkflowCancel()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);

        String workflowID = workflow.startNewTask(user1.get().getUsername(), user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        workflow.taskDone(user1.get().getUsername(), user1.get().getPassword(), workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        workflow.taskDone(user1.get().getUsername(), user1.get().getPassword(), workflowID, WorkflowService.TaskStatus.COMPLETED, "C8434");
        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is not completed.", workflowName));

        log.info("STEP 2: Click on 'Completed' option from the left side panel");
        workflowsIveStartedPage.clickCompletedFilter();
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertTrue(workflows.contains(workflowName), String.format("Workflow: %s is completed.", workflowName));

        log.info("STEP 3: Click on 'cancel' option.");
        workflowsIveStartedPage.clickDeleteWorkflow(workflowName, false);
        workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertTrue(workflows.contains(workflowName), String.format("Workflow: %s is not deleted.", workflowName));

        deleteUsersIfNotNull(user1.get());
    }
}