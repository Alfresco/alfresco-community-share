package org.alfresco.share.tasksAndWorkflows.CancellingAWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
public class CancelWorkflowsTests extends ContextAwareWebTest
{
    @Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    @Autowired
    MyTasksPage myTasksPage;

    private String workflowName = "taskName" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C8434")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void cancelWorkflow()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();
        //getBrowser().waitInSeconds(5);
        workflowsIveStartedPage.renderedPage();

        LOG.info("STEP 2: Click on 'Cancel Workflow' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickCancelWorkflow(workflowName, true);
        getBrowser().waitInSeconds(2);
        getBrowser().refresh();
        List<String> workflows = workflowsIveStartedPage.getActiveWorkflows();
        Assert.assertFalse(workflows.contains(workflowName), String.format("Workflow: %s is cancelled.", workflowName));

        LOG.info("STEP 3: Verify the workflow is not present in 'My Tasks' page.");
        myTasksPage.navigateByMenuBar();
        //getBrowser().waitInSeconds(5);
        myTasksPage.renderedPage();
        Assert.assertFalse(myTasksPage.checkTaskWasFound(workflowName), String.format("Workflow: %s is present in 'My Tasks' page.", workflowName));
    }
}
