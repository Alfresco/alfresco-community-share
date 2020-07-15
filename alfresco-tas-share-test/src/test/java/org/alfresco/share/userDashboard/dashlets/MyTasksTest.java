package org.alfresco.share.userDashboard.dashlets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.ActiveTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.CompletedTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AddUserDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyTasksTest extends ContextAwareWebTest
{
    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    StartWorkflowPage startWorkFlowPage;

    @Autowired
    ActiveTasksPage activeTasksPage;

    @Autowired
    CompletedTasksPage completedTasksPage;

    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    ViewTaskPage viewTaskPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    GroupsPage groupsPage;

    @Autowired
    AddUserDialog addUserDialog;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String C2122userName = String.format("C2122userName%s", uniqueIdentifier);
    private String C8548userNameA = String.format("C8548userNameA%s", uniqueIdentifier);
    private String C8548userNameB = String.format("C8548userNameB%s", uniqueIdentifier);
    //private String C8548userNameC = String.format("C8548userNameC%s", uniqueIdentifier);
    private String C8548group = String.format("C8548group%s", uniqueIdentifier);
    private String taskName = "PRR_NewTask";

    private Date taskDate = new Date();
    private String taskNotStartedStatus = "Task, Not Yet Started";

    private UserModel addUserAfterWorkflow;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, C2122userName, password, C2122userName + domain, "C2122firstName", "C2122lastName");
        userService.create(adminUser, adminPassword, C8548userNameA, password, C8548userNameA + domain, "C8548firstNameA", "C8548lastNameA");
        userService.create(adminUser, adminPassword, C8548userNameB, password, C8548userNameB + domain, "C8548firstNameB", "C8548lastNameB");
        //userService.create(adminUser, adminPassword, C8548userNameC, password, C8548userNameC + domain, "C8548firstNameC", "C8548lastNameC");

        addUserAfterWorkflow = dataUser.usingAdmin().createRandomTestUser();

        groupService.createGroup(adminUser, adminPassword, C8548group);
        groupService.addUserToGroup(adminUser, adminPassword, C8548group, C8548userNameA);
        groupService.addUserToGroup(adminUser, adminPassword, C8548group, C8548userNameB);
        workflowService.startNewTask(C2122userName, password, taskName, taskDate, C2122userName, Priority.Normal, null, true);
        workflowService.startGroupReview(C8548userNameA, password, taskName, taskDate, C8548group, Priority.Normal, null, 50, true);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        //TODO find a method to delete the created users and workflow.
        cleanupAuthenticatedSession();
        workflowService.deleteWorkflow(C2122userName, password, workflowService.getWorkflowId(C2122userName, password, C2122userName));
        workflowService.deleteWorkflow(C8548userNameA, password, workflowService.getWorkflowId(C8548userNameA, password, C8548userNameA));
        userService.delete(adminUser, adminPassword, C2122userName);
        userService.delete(adminUser, adminPassword, C8548userNameA);
        userService.delete(adminUser, adminPassword, C8548userNameB);
        groupService.removeGroup(adminUser, adminPassword, C8548group);

        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + C2122userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + C8548userNameA);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + C8548userNameB);
        removeUserFromAlfresco(addUserAfterWorkflow);
    }

    @TestRail (id = "C2122")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD, "tobefixed" })
    public void someTasksAssigned()
    {
        setupAuthenticatedSession(C2122userName, password);
        LOG.info("Step 1: Verify My Tasks dashlet");
        userDashboardPage.navigate(C2122userName);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks");
        myTasksDashlet.isStartWorkFlowLinkDisplayed();
        myTasksDashlet.isActiveTasksLinkDisplayed();
        myTasksDashlet.isCompletedTasksLinkDisplayed();
        myTasksDashlet.isFilterTaskButtonDisplayed();
        getBrowser().waitInSeconds(5);
        Assert.assertEquals(myTasksDashlet.getTaskNavigation(), "1 - " + myTasksDashlet.getNoOfTasks() + " of " + myTasksDashlet.getNoOfTasks());
        myTasksDashlet.checkEditAndViewIconsForEachTask();
        myTasksDashlet.isHelpIconDisplayed(DashletHelpIcon.MY_TASKS);

        LOG.info("Step 2: Verify info for the created task");
        Assert.assertTrue(myTasksDashlet.getTaskNamesList().get(0).equals(taskName), "Wrong task name!");
        Assert.assertTrue(myTasksDashlet.getTaskTypeAndStatusList().get(0).equals(taskNotStartedStatus), "Wrong type and status!");
        SimpleDateFormat dt = new SimpleDateFormat("dd MMMM, YYYY");
        System.out.println(dt.format(taskDate));
        Assert.assertTrue(myTasksDashlet.getTaskDueDateList().get(0).equals(dt.format(taskDate)), "Wrong due date!");

        LOG.info("Step 3: Click Start WorkFlow link");
        myTasksDashlet.clickOnStartWorkFlowLink();
        Assert.assertTrue(startWorkFlowPage.getPageTitle().equals("Alfresco » Start Workflow"), "Start Workflow page should be displayed!");

        LOG.info("Step 4: Go back to User Dashboard. Click Active Tasks link.");
        userDashboardPage.navigate(C2122userName);
        myTasksDashlet.clickOnActiveTasksLink();
        Assert.assertTrue(activeTasksPage.getPageTitle().equals("Alfresco » My Tasks"), "My Tasks page should be displayed!");

        LOG.info("Step 5: Go back to User Dashboard. Click Completed Tasks link.");
        userDashboardPage.navigate(C2122userName);
        myTasksDashlet.clickOnCompletedTasksLink();
        Assert.assertTrue(completedTasksPage.getPageTitle().equals("Alfresco » My Tasks"), "My Tasks page should be displayed!");

        LOG.info("Step 6: Go back to User Dashboard. Click on task's name link.");
        userDashboardPage.navigate(C2122userName);
        myTasksDashlet.clickOnTaskNameLink(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().equals("Alfresco » Edit Task"), "Edit task page should be displayed!");

        LOG.info("Step 7: Go back to User Dashboard. Click Edit Task icon near the task");
        userDashboardPage.navigate(C2122userName);
        myTasksDashlet.clickEditTask(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().equals("Alfresco » Edit Task"), "Edit task page should be displayed!");

        LOG.info("Step 7: Go back to User Dashboard. Click View Task icon");
        userDashboardPage.navigate(C2122userName);
        myTasksDashlet.clickViewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().equals("Alfresco » Task Details"), "View task page should be displayed!!");

        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C8548, C8597")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void userAddedAfterWorkflowStarts()
    {
        LOG.info("Precondition 1: User with administrator rights are logged into Share;");
        setupAuthenticatedSession(adminUser, adminPassword);
        groupsPage.navigate();
        groupsPage.writeInSearchInput(C8548group)
            .clickBrowse()
            .selectGroup(C8548group)
            .clickAddUser(2)
                .searchUser(addUserAfterWorkflow)
                .assertUserIsFound(addUserAfterWorkflow)
                .assertAddButtonIsDisplayedForUser(addUserAfterWorkflow)
                .addUser(addUserAfterWorkflow);
        groupsPage.assertColumnContainsUser(2, addUserAfterWorkflow);

        setupAuthenticatedSession(addUserAfterWorkflow);

        LOG.info("STEP 4: Verify user's 'My task' dashlet;");
        userDashboardPage.navigate(addUserAfterWorkflow);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");
        cleanupAuthenticatedSession();
    }
}
