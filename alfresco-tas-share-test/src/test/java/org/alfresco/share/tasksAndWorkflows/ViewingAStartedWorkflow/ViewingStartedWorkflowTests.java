package org.alfresco.share.tasksAndWorkflows.ViewingAStartedWorkflow;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
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

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());

    @TestRail(id = "C8425")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewWorkflowIveStarted()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigateByMenuBar();

        LOG.info("STEP 2: Click on 'View History' option for the workflow created in Precondition.");
        workflowsIveStartedPage.clickViewHistory(workflowName);

        LOG.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail(id = "C8426")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewWorkflowAssignedToOtherUser()
    {
        LOG.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        String user2 = String.format("User2%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), user2, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();

        LOG.info("STEP 2: From 'My Tasks' page click 'Workflow Details' button.");
        myTasksPage.clickViewWorkflow(workflowName);

        LOG.info("STEP 3: Verify the details are correct.");
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"), "Priority is: " + workflowDetailsPage.getPriority());
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user2));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        userService.delete(adminUser,adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
    }

    @TestRail(id = "C8427")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewWorkflowIveStartedOptions()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        LOG.info("STEP 2: Verify 'View History' and 'Cancel Workflow' options are displayed for the workflow.");
        Assert.assertTrue(workflowsIveStartedPage.isCancelWorkflowOptionDisplayed(workflowName));
        Assert.assertTrue(workflowsIveStartedPage.isViewHistoryOptionDisplayed(workflowName));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail(id = "C8428")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewOptionsAvailableForWorkflowsAssignedToOthers()
    {
        LOG.info("Precondition: Create 2 users and a workflow assigned by user1 to user2.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        String user2 = String.format("User2%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), user2, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(user2, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        LOG.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        userService.delete(adminUser,adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);
    }

    @TestRail(id = "C8458")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void verifyWorkflowIveStartedPage()
    {
        LOG.info("Precondition: Create a user and login.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

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
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail(id = "C8461")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void verifyMyTasksPage()
    {
        LOG.info("Precondition: Create a user and login.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        LOG.info("STEP 2: Verify filters are displayed on the left side of the page.");
        Assert.assertTrue(myTasksPage.isDueFilterDisplayed());
        Assert.assertTrue(myTasksPage.isPriorityFilterDisplayed());
        Assert.assertTrue(myTasksPage.isTasksFilterDisplayed());
        Assert.assertTrue(myTasksPage.isAssigneeFilterDisplayed());

        LOG.info("STEP 3: Verify 'Active Tasks' bar is displayed.");
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed());

        LOG.info("STEP 3: Verify 'Start Workflow' button is displayed.");
        Assert.assertTrue(myTasksPage.isStartWorkflowDisplayed());
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail(id = "C8460")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewOptionsAvailableForTasksIveStarted()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        LOG.info("STEP 2: Verify 'Edit Task', 'View Task' and 'View Workflow' options are displayed for the workflow.");
        Assert.assertTrue(myTasksPage.isEditTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewTaskOptionDisplayed(workflowName));
        Assert.assertTrue(myTasksPage.isViewWorkflowOptionDisplayed(workflowName));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail(id = "C8500")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void viewMyTaskAndWorkflowDetailsPages()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s",RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        LOG.info("STEP 2: From 'Workflows I've Started' page click on the title of the workflow and verify details.");
        workflowsIveStartedPage.clickOnWorkflowTitle(workflowName);
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(testUser));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(testUser));

        LOG.info("STEP 3: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigate();

        LOG.info("STEP 4: From 'My Tasks' page click on the title of the task and verify details.");
        myTasksPage.clickOnTaskTitle(workflowName);
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getPriority().contains("Medium"));
        Assert.assertTrue(editTaskPage.getOwner().contains(testUser));
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

}
