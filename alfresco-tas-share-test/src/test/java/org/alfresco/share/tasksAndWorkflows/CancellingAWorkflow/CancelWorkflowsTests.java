package org.alfresco.share.tasksAndWorkflows.CancellingAWorkflow;

import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class CancelWorkflowsTests extends ContextAwareWebTest
{
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    //@Autowired
    MyTasksPage myTasksPage;

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());

    @TestRail (id = "C8434")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed"})
    public void cancelWorkflow()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        LOG.info("STEP 2: Click on 'Cancel Workflow' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickCancelWorkflow(workflowName, true);
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is cancelled.", workflowName));

        LOG.info("STEP 3: Verify the workflow is not present in 'My Tasks' page.");
        myTasksPage.navigate();
        Assert.assertFalse(myTasksPage.checkTaskWasFound(workflowName), String.format("Workflow: %s is present in 'My Tasks' page.", workflowName));
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }
}
