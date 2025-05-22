package org.alfresco.share.tasksAndWorkflows.CancellingAWorkflow;

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
public class CancelWorkflowsTests extends BaseTest
{
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    //@Autowired
    MyTasksPage myTasksPage;

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();

    @TestRail (id = "C8434")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void cancelWorkflow()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);

        workflow.startNewTask(user1.get().getUsername(),user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: Click on 'Cancel Workflow' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickCancelWorkflow(workflowName, true);
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is cancelled.", workflowName));

        log.info("STEP 3: Verify the workflow is not present in 'My Tasks' page.");
        myTasksPage.navigate();
        myTasksPage.assertNoTaskIsDisplayed();

        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C8497")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void cancelWorkflowFromDetailsPage()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);

        workflow.startNewTask(user1.get().getUsername(),user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: Click on 'Cancel Workflow' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickCancelWorkflow(workflowName, true);
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is cancelled.", workflowName));

        log.info("STEP 3: Verify the workflow is not present in 'My Tasks' page.");
        myTasksPage.navigate();
        myTasksPage.assertNoTaskIsDisplayed();

        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C8433")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void cancelWorkflowCancel()
    {
        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);

        workflow.startNewTask(user1.get().getUsername(),user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);

        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: Click on 'Cancel Workflow cancel' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickCancelWorkflow(workflowName, false);
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertTrue(workflows.contains(workflowName), String.format("Workflow: %s is cancelled.", workflowName));

        deleteUsersIfNotNull(user1.get());
    }
}