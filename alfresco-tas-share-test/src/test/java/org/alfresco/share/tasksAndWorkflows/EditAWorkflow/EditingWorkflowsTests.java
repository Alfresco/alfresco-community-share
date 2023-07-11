package org.alfresco.share.tasksAndWorkflows.EditAWorkflow;

import static org.alfresco.po.enums.TaskStatus.CANCELLED;
import static org.alfresco.po.enums.TaskStatus.COMPLETED;
import static org.alfresco.po.enums.TaskStatus.IN_PROGRESS;
import static org.alfresco.po.enums.TaskStatus.NOT_STARTED;
import static org.alfresco.po.enums.TaskStatus.ON_HOLD;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.TaskDetailsPage;
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
public class EditingWorkflowsTests extends BaseTest
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
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC8464 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> userC8465 = new ThreadLocal<>();

    @TestRail (id = "C8463")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editWorkflow() throws InterruptedException {
        log.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        String comment = "C8463";

        log.info("PreCondition: Creating test user");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
        taskDetailsPage = new TaskDetailsPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);

        workflow.startNewTask(user1.get().getUsername(), user1.get().getPassword(), workflowName, new Date(), user1.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        authenticateUsingLoginPage(user1.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'Workflows I've Started' option.");
        workflowsIveStartedPage.navigate();

        log.info("STEP 2: From 'Workflows I've Started' page click on the title of the workflow and verify details.");
        workflowsIveStartedPage.clickOnWorkflowTitle(workflowName);
        Assert.assertTrue(workflowDetailsPage.getWorkflowDetailsHeader().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getPriority().contains("Medium"));
        Assert.assertTrue(workflowDetailsPage.getStartedByUser().contains(user1.get().getUsername()));
        Assert.assertTrue(workflowDetailsPage.getMessage().contains(workflowName));
        Assert.assertTrue(workflowDetailsPage.getAssignedToUser().contains(user1.get().getUsername()));

        log.info("STEP 3: From 'Workflows Details' page click on 'Task Details' button.");
        workflowDetailsPage.clickTaskDetailsButton();
        Assert.assertTrue(taskDetailsPage.getTaskDetailsHeader().contains(workflowName));

        log.info("STEP 4: From 'Task Details' page click on 'Edit' button.");
        taskDetailsPage.clickEditButton();
        editTaskPage.selectStatus(IN_PROGRESS);
        editTaskPage.writeComment(comment);

        log.info("STEP 5: Click on 'Save and Close' button and verify changes are saved.");
        editTaskPage.clickOnSaveButton();
        Assert.assertTrue(taskDetailsPage.getStatus().contains("In Progress"));
        Assert.assertTrue(taskDetailsPage.getComment().contains(comment));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C8464")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editTask()
    {
        log.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        String comment = "C8464";

        log.info("PreCondition: Creating test user");
        userC8464.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        workflow.startNewTask(userC8464.get().getUsername(), userC8464.get().getPassword(), workflowName, new Date(), userC8464.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        authenticateUsingLoginPage(userC8464.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();

        log.info("STEP 2: Hover over the name of the workflow and click on 'Edit Task' button.");
        myTasksPage.editTask(workflowName);
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getOwner().contains(userC8464.get().getUsername()));

        log.info("STEP 3: Modify the details and click on 'Save and Close' button.");
        editTaskPage.selectStatus(ON_HOLD);
        editTaskPage.writeComment(comment);
        editTaskPage.clickOnSaveButton();
        Assert.assertTrue(myTasksPage.getStatus(workflowName).contains("On Hold"));

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userC8464.get().getUsername());
        deleteUsersIfNotNull(userC8464.get());
    }

    @TestRail (id = "C8465")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS, "tobefixed" })
    public void verifyEditTaskForm()
    {
        log.info("Precondition: Create user and a workflow.");
        String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
        log.info("PreCondition: Creating test user");
        userC8465.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());
        myTasksPage = new MyTasksPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        workflow.startNewTask(userC8465.get().getUsername(), userC8465.get().getPassword(), workflowName, new Date(), userC8465.get().getUsername(), CMISUtil.Priority.Normal, null, false);
        authenticateUsingLoginPage(userC8465.get());

        log.info("STEP 1: From 'Tasks' dropdown click 'My Tasks' option.");
        myTasksPage.navigateByMenuBar();

        log.info("STEP 2: Hover over the name of the workflow and click on 'Edit Task' button.");
        myTasksPage.editTask(workflowName);

        log.info("STEP 3: Verify items are displayed on 'Edit Task' page.");
        Assert.assertTrue(editTaskPage.getMessage().contains(workflowName));
        Assert.assertTrue(editTaskPage.getOwner().contains(userC8465.get().getUsername()));
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

        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + userC8465.get().getUsername());
        deleteUsersIfNotNull(userC8465.get());
    }
}
