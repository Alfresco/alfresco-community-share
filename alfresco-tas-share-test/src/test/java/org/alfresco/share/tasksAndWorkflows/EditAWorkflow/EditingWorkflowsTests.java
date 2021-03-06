package org.alfresco.share.tasksAndWorkflows.EditAWorkflow;

import static org.alfresco.po.enums.TaskStatus.CANCELLED;
import static org.alfresco.po.enums.TaskStatus.COMPLETED;
import static org.alfresco.po.enums.TaskStatus.IN_PROGRESS;
import static org.alfresco.po.enums.TaskStatus.NOT_STARTED;
import static org.alfresco.po.enums.TaskStatus.ON_HOLD;

import java.util.Date;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.TaskDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
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
public class EditingWorkflowsTests extends ContextAwareWebTest
{
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;

    @Autowired
    WorkflowService workflow;

    //@Autowired
    WorkflowDetailsPage workflowDetailsPage;

    //@Autowired
    TaskDetailsPage taskDetailsPage;

   // @Autowired
    MyTasksPage myTasksPage;

    //@Autowired
    EditTaskPage editTaskPage;

    private String workflowName = String.format("taskName%s", RandomData.getRandomAlphanumeric());

    @TestRail (id = "C8463")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void editWorkflow()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        String comment = "C8463";
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

        LOG.info("STEP 3: From 'Workflows Details' page click on 'Task Details' button.");
        workflowDetailsPage.clickTaskDetailsButton();
        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workflowName));

        LOG.info("STEP 4: From 'Task Details' page click on 'Edit' button.");
        taskDetailsPage.clickEditButton();
        editTaskPage.selectStatus(IN_PROGRESS);
        getBrowser().waitInSeconds(2);
        editTaskPage.writeComment(comment);

        LOG.info("STEP 5: Click on 'Save and Close' button and verify changes are saved.");
        editTaskPage.clickOnSaveButton();
        Assert.assertTrue(taskDetailsPage.getStatus().contains("In Progress"));
        Assert.assertTrue(taskDetailsPage.getComment().contains(comment));

        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail (id = "C8464")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void editTask()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        String comment = "C8464";
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
//        Assert.assertTrue(myTasksPage.assertTaskNameEqualsTo(workflowName));

        LOG.info("STEP 2: Hover over the name of the workflow and click on 'Edit Task' button.");
        myTasksPage.editTask(workflowName);
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getOwner().contains(testUser));

        LOG.info("STEP 3: Modify the details and click on 'Save and Close' button.");
        editTaskPage.selectStatus(ON_HOLD);
        getBrowser().waitInSeconds(2);
        editTaskPage.writeComment(comment);
        editTaskPage.clickOnSaveButton();
        Assert.assertTrue(myTasksPage.getStatus(workflowName).contains("On Hold"));
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }

    @TestRail (id = "C8465")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void verifyEditTaskForm()
    {
        LOG.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, testUser, "lastName");
        workflow.startNewTask(testUser, password, workflowName, new Date(), testUser, CMISUtil.Priority.Normal, null, false);
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();
//        Assert.assertTrue(myTasksPage.assertTaskNameEqualsTo(workflowName));

        LOG.info("STEP 2: Hover over the name of the workflow and click on 'Edit Task' button.");
        myTasksPage.editTask(workflowName);

        LOG.info("STEP 3: Verify items are displayed on 'Edit Task' page.");
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getOwner().contains(testUser));
        Assert.assertTrue(editTaskPage.getPriority().contains("Medium"));
        Assert.assertTrue(editTaskPage.getComment().equals(""));
        Assert.assertTrue(editTaskPage.isIdentifierPresent());
        Assert.assertTrue(editTaskPage.isDueDatePresent());
        Assert.assertTrue(editTaskPage.isSaveButtonPresent());
        Assert.assertTrue(editTaskPage.isCancelButtonPresent());
        Assert.assertTrue(editTaskPage.isTaskDoneButtonPresent());
        Assert.assertTrue(editTaskPage.isReassignButtonPresent());
        Assert.assertTrue(editTaskPage.isAddItemsButtonPresent());
        Assert.assertTrue(editTaskPage.isStatusOptionSelected(NOT_STARTED));
        Assert.assertTrue(editTaskPage.isStatusOptionPresent(IN_PROGRESS));
        Assert.assertTrue(editTaskPage.isStatusOptionPresent(ON_HOLD));
        Assert.assertTrue(editTaskPage.isStatusOptionPresent(CANCELLED));
        Assert.assertTrue(editTaskPage.isStatusOptionPresent(COMPLETED));
        userService.delete(adminUser, adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
    }
}
