package org.alfresco.share.tasksAndWorkflows.ViewingAStartedWorkflow;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.*;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author Razvan.Dorobantu
 */
public class ViewingStartedWorkflowTests extends ContextAwareWebTest
{
    @Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    EditTaskPage editTaskPage;

    private String workflowName = "taskName" + DataUtil.getUniqueIdentifier();

    @TestRail(id = "C8425")
    @Test
    public void viewWorkflowIveStarted()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Click on 'View History' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickViewHistory(workflowName);
        browser.waitInSeconds(5);

        LOG.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser));
    }

    @TestRail(id = "C8426")
    @Test
    public void viewWorkflowAssignedToOtherUser()
    {
        LOG.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        String user2 = "User2" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + "@tests.com", user2, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), user2, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: From 'My Tasks' page click 'Workflow Details' button.");
        myTasksPage.clickViewWorkflow(workflowName);
        browser.waitInSeconds(5);

        LOG.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user2));
    }

    @TestRail(id = "C8427")
    @Test
    public void viewWorkflowIveStartedOptions()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Verify 'View History' and 'Cancel Workflow' options are displayed for the workflow.");
        Assert.assertTrue(workflowsIveStartedPage.isCancelWorkflowOptionDisplayed(workflowName));
        Assert.assertTrue(workflowsIveStartedPage.isViewHistoryOptionDisplayed(workflowName));
    }

    @TestRail(id = "C8428")
    @Test
    public void viewOptionsAvailableForWorkflowsAssignedToOthers()
    {
        LOG.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        String user2 = "User2" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + "@tests.com", user2, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), user2, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));
    }

    @TestRail(id = "C8458")
    @Test
    public void verifyWorkflowIveStartedPage()
    {
        LOG.info("Precondition: Create a user and login.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Verify filters are displayed on the left side of the page.");
        Assert.assertTrue(workflowsIveStartedPage.isDueFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isPriorityFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isStartedFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isWorkflowsFilterDisplayed());
        Assert.assertTrue(workflowsIveStartedPage.isWorkflowTypeFilterDisplayed());

        LOG.info("STEP 3: Verify 'Active Workflows' bar is displayed.");
        Assert.assertTrue(workflowsIveStartedPage.isActiveWorkflowsBarDisplayed());

        LOG.info("STEP 3: Verify 'Start Workflow' button is displayed.");
        Assert.assertTrue(workflowsIveStartedPage.isStartWorkflowDisplayed());
    }

    @TestRail(id = "C8461")
    @Test
    public void verifyMyTasksPage()
    {
        LOG.info("Precondition: Create a user and login.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Verify filters are displayed on the left side of the page.");
        Assert.assertTrue(myTasksPage.isDueFilterDisplayed());
        Assert.assertTrue(myTasksPage.isPriorityFilterDisplayed());
        Assert.assertTrue(myTasksPage.isTasksFilterDisplayed());
        Assert.assertTrue(myTasksPage.isAssigneeFilterDisplayed());

        LOG.info("STEP 3: Verify 'Active Tasks' bar is displayed.");
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed());

        LOG.info("STEP 3: Verify 'Start Workflow' button is displayed.");
        Assert.assertTrue(myTasksPage.isStartWorkflowDisplayed());
    }

    @TestRail(id = "C8460")
    @Test
    public void viewOptionsAvailableForTasksIveStarted()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));
    }

    @TestRail(id = "C8500")
    @Test
    public void viewMyTaskAndWorkflowDetailsPages()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = "testUser" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, testUser, password, testUser + "@tests.com", testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 2: From 'Workflows I've Started' page click on the title of the workflow and verify details.");
        workflowsIveStartedPage.clickOnWorkflowTitle(workflowName);
        browser.waitInSeconds(5);
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser));

        LOG.info("STEP 3: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
        browser.waitInSeconds(5);

        LOG.info("STEP 4: From 'My Tasks' page click on the title of the task and verify details.");
        myTasksPage.clickOnTaskTitle(workflowName);
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getPriority().contains("Medium"));
        Assert.assertTrue(editTaskPage.getOwner().contains(testUser));
    }

}
