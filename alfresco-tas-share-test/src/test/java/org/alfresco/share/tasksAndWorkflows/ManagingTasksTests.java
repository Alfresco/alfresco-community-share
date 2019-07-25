package org.alfresco.share.tasksAndWorkflows;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.SiteService;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneePopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
public class ManagingTasksTests extends ContextAwareWebTest
{
    @Autowired
    WorkflowService workflowService;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SelectAssigneePopUp selectAssigneePopUp;

    @Autowired
    MyTasksPage myTasksPage;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    ViewTaskPage viewTaskPage;

    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String testUser = String.format("testUser%s", uniqueIdentifier);
    private String C8521username = String.format("C8521user%s", uniqueIdentifier);
    private String C8551usernameA = String.format("C8551userA%s", uniqueIdentifier);
    private String C8551usernameB = String.format("C8551userB%s", uniqueIdentifier);
    private String C8551usernameC = String.format("C8551userC%s", uniqueIdentifier);
    private String C8596username = String.format("C8596user%s", uniqueIdentifier);
    private String C8596usernameA = String.format("C8596userA%s", uniqueIdentifier);
    private String C8596usernameB = String.format("C8596userB%s", uniqueIdentifier);
    private String C8596usernameC = String.format("C8596userC%s", uniqueIdentifier);
    private String C8551group = "C8551group_" + uniqueIdentifier;
    private String C8596group = "C8559group_" + uniqueIdentifier;
    private String C8551subgroup = "C8551subgroup_" + uniqueIdentifier;
    private String siteName = String.format("siteName%s", uniqueIdentifier);
    private String taskName = String.format("taskName%s", uniqueIdentifier);
    private String taskTypeAndStatus = "Task, In Progress";
    private String docName = String.format("docName%s", uniqueIdentifier);
    private String docContent = String.format("docContent%s", uniqueIdentifier);
    private List<String> docs = new ArrayList<>();
    private Date timeDate = new Date();

    @BeforeClass (alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, C8521username, password, C8521username + domain, "C8521firstName", "C8521lastName");
        userService.create(adminUser, adminPassword, C8551usernameA, password, C8551usernameA + domain, "C8551firstNameA", "C8551lastNameA");
        userService.create(adminUser, adminPassword, C8551usernameB, password, C8551usernameB + domain, "C8551firstNameB", "C8551lastNameB");
        userService.create(adminUser, adminPassword, C8551usernameC, password, C8551usernameC + domain, "C8551firstNameC", "C8551lastNameC");
        userService.create(adminUser, adminPassword, C8596username, password, C8596username + domain, "C8596firstName", "C8596lastName");
        userService.create(adminUser, adminPassword, C8596usernameA, password, C8596usernameA + domain, "C8596firstNameA", "C8596lastNameA");
        userService.create(adminUser, adminPassword, C8596usernameB, password, C8596usernameB + domain, "C8596firstNameB", "C8596lastNameB");
        userService.create(adminUser, adminPassword, C8596usernameC, password, C8596usernameC + domain, "C8596firstNameC", "C8596lastNameC");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        docs.add(docName);
        groupService.createGroup(adminUser, adminPassword, C8551group);
        groupService.createGroup(adminUser, adminPassword, C8596group);
        groupService.addSubGroup(adminUser, adminPassword, C8551group, C8551subgroup);
        groupService.addUserToGroup(adminUser, adminPassword, C8596group, C8596usernameA);
        groupService.addUserToGroup(adminUser, adminPassword, C8596group, C8596usernameB);
        groupService.addUserToGroup(adminUser, adminPassword, C8596group, C8596usernameC);
        workflowService.startNewTask(testUser, password, taskName, timeDate, testUser, Priority.High, siteName, docs, false);
        workflowService.startGroupReview(C8596username, password, taskName, timeDate, C8596group, Priority.Normal, null, 50, true);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        workflowService.deleteWorkflow(testUser, password, workflowService.getWorkflowId(testUser, password, testUser));
        workflowService.deleteWorkflow(C8596username, password, workflowService.getWorkflowId(C8596username, password, C8596username));
        userService.delete(adminUser, adminPassword, testUser);
        userService.delete(adminUser, adminPassword, C8521username);
        userService.delete(adminUser, adminPassword, C8551usernameA);
        userService.delete(adminUser, adminPassword, C8551usernameB);
        userService.delete(adminUser, adminPassword, C8551usernameC);
        userService.delete(adminUser, adminPassword, C8596username);
        userService.delete(adminUser, adminPassword, C8596usernameA);
        userService.delete(adminUser, adminPassword, C8596usernameB);
        userService.delete(adminUser, adminPassword, C8596usernameC);
        groupService.removeSubgroupFromGroup(adminUser, adminPassword, C8551group, C8551subgroup);
        groupService.removeGroup(adminUser, adminPassword, C8551group);
        groupService.removeGroup(adminUser, adminPassword, C8596group);
    }


    @TestRail (id = "C8520")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editTaskFromTasksDashletChangeStatus()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'My Tasks' dashlet click 'Edit Task' icon.");
        myTasksDashlet.clickEditTask(taskName);

        LOG.info("STEP 2: Change the status of the workflow (eg. 'In Progress') then click 'Save and Close' button.");
        startWorkflowPage.selectTaskStatus("In Progress");
        startWorkflowPage.saveAndClose();
        assertTrue(myTasksDashlet.getTaskTypeAndStatus(taskName).equals(taskTypeAndStatus),
            "Wrong type and status! Expected " + taskTypeAndStatus + "but found: " + myTasksDashlet.getTaskTypeAndStatus(taskName));
    }

    @TestRail (id = "C8521")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editTaskFromTasksDashletReassign()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'My Tasks' dashlet click 'Edit Task' icon.");
        myTasksDashlet.clickEditTask(taskName);

        LOG.info("STEP 2: Click 'Reassign' button, reassign the task to '" + C8521username + "' then click 'ok' button.");
        startWorkflowPage.clickOnReassignButton();
        selectAssigneePopUp.enterUserToSearch(C8521username);
        selectAssigneePopUp.clickOnSearchButton();
        selectAssigneePopUp.clickOnSelectButton();
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(C8521username, password);
        userDashboardPage.navigate(C8521username);
        assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");
    }

    @TestRail (id = "C8551")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void groupTaskSubgroup()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        LOG.info("Precondition 1. Some users are added to the parent group.");
        groupService.addUserToGroup(adminUser, adminPassword, C8551group, C8551usernameA);

        LOG.info("Precondition 2. Some users are added to the subgroup.");
        groupService.addUserToGroup(adminUser, adminPassword, C8551subgroup, C8551usernameB);
        groupService.addUserToGroup(adminUser, adminPassword, C8551subgroup, C8551usernameC);

        LOG.info("STEP 1: Click Start workflow action (e.g. from My tasks dashlet).");
        myTasksPage.navigate();
        myTasksPage.clickStartWorkflowButton();

        LOG.info("STEP 2: Select Group Review and approve type.");
        startWorkflowPage.selectAWorkflow("Review and Approve (group review)");

        LOG.info("STEP 3: From 'Assignee' subsection click 'Select' button.");
        startWorkflowPage.clickGroupSelectButton();

        LOG.info("STEP 4: Enter a group name and click 'Search' button.");
        selectPopUpPage.search(C8551group);

        LOG.info("STEP 5: Click Add ('+') button and then Ok.");
        selectPopUpPage.clickAddIcon(C8551group);
        selectPopUpPage.clickOkButton();

        //Check if the selected group is displayed on the form.
        Assert.assertEquals(startWorkflowPage.getSelectedReviewerName(), C8551group, "The displayed Assignee name is not the same with the chosen one");

        //Select a message text for the task
        String taskDescription = "PRR Test " + uniqueIdentifier;
        startWorkflowPage.addWorkflowDescription(taskDescription);

        LOG.info("STEP 6: Click Start workflow button.");
        startWorkflowPage.clickStartWorkflow(myTasksPage);
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed after clicking on 'Submit New Workflow' button.");

        LOG.info("STEP 7: Login Share as every subgroup member and verify task is displayed in My tasks dashlet");
        userService.logout();
        //Verify the first user:
        setupAuthenticatedSession(C8551usernameB, password);

        myTasksPage.navigate();
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed as default when navigating to My Tasks page.");
        Assert.assertEquals(myTasksPage.getTaskTitle(), taskDescription, "The created task is not displayed in 'Active Tasks' list.");

        //Verify the second user:
        setupAuthenticatedSession(C8551usernameC, password);

        myTasksPage.navigate();
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed as default when navigating to My Tasks page.");
        Assert.assertEquals(myTasksPage.getTaskTitle(), taskDescription, "The created task is not displayed in 'Active Tasks' list.");
    }

    @TestRail (id = "C8596")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void groupReviewApproveReject()
    {
        String firstUserMessage = "This is the text message from the first user.";
        String secondUserMessage = "This is the text message from the second user.";
        String thirdUserMessage = "This is the text message from the third user.";
        String C8596fullNameA = "C8596firstNameA " + "C8596lastNameA";
        String C8596fullNameB = "C8596firstNameB " + "C8596lastNameB";

        LOG.info("Precondition: Login as '" + C8596usernameA + "' user.");
        setupAuthenticatedSession(C8596usernameA, password);

        LOG.info("STEP 1: Open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameA);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");
        myTasksDashlet.clickOnTaskNameLink(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        LOG.info("STEP 2: Press Approve/reject Button;");
        editTaskPage.approve(firstUserMessage, userDashboardPage);
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");

        LOG.info("STEP 3: Log in as '" + C8596usernameB + "' user;");
        setupAuthenticatedSession(C8596usernameB, password);

        LOG.info("STEP 4.1: Check if Workflow History block information that User1 has approved the task is present;");
        userDashboardPage.navigate(C8596usernameB);

        //Check if My Task dashlet is displayed.
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");

        //Click on 'View task' icon and check if the user can access 'Workflow Details' page.
        myTasksDashlet.clickViewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Task Details"), "View task page should be displayed!!");
        viewTaskPage.clickWorkflowDetailsLink();
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Workflow Details"), "View Workflow Details page should be displayed!!");

        //Check if History block is displayed with the information that C8596fullNameA has provided in STEP 2.
        Assert.assertTrue(workflowDetailsPage.isHistoryBlockPresent(), "History block is not present but is should!!");
        Assert.assertEquals(workflowDetailsPage.getHistoryOutcome(C8596fullNameA), editTaskPage.getOutcomeApproveText(), "The outcome from History block does not coincide with the action that user '" + C8596usernameA + "' did.");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameA), firstUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");

        LOG.info("STEP 4.2: Go back to user's Homepage and open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameB);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");
        myTasksDashlet.clickOnTaskNameLink(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        LOG.info("STEP 5: Press Approve/reject Button;");
        editTaskPage.reject(secondUserMessage, userDashboardPage);
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");

        LOG.info("STEP 6: Log in as '" + C8596usernameC + "' user;");
        setupAuthenticatedSession(C8596usernameC, password);

        LOG.info("STEP 7.1: Check if Workflow History block information that '" + C8596fullNameA + "' has approved and '" + C8596fullNameB + "' has rejected the task is present;");
        userDashboardPage.navigate(C8596usernameC);

        LOG.info("=> Check if My Task dashlet is displayed.");
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");

        LOG.info("=> Click on 'View task' icon and check if the user can access 'Workflow Details' page.");
        myTasksDashlet.clickViewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Task Details"), "View task page should be displayed!!");
        viewTaskPage.clickWorkflowDetailsLink();
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Workflow Details"), "View Workflow Details page should be displayed!!");

        LOG.info("=> Check if History block is displayed with the information that '" + C8596fullNameA + "' has provided in STEP 2 and user '" + C8596fullNameB + "' in STEP 5.");
        Assert.assertTrue(workflowDetailsPage.isHistoryBlockPresent(), "History block is not present but is should!!");
        Assert.assertEquals(workflowDetailsPage.getHistoryOutcome(C8596fullNameA), editTaskPage.getOutcomeApproveText(), "The outcome from History block does not coincide with the action that user '" + C8596usernameA + "' did.");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameA), firstUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");
        Assert.assertEquals(workflowDetailsPage.getHistoryOutcome(C8596fullNameB), editTaskPage.getOutcomeRejectText(), "The outcome from History block does not coincide with the action that user '" + C8596usernameB + "' did.");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameB), secondUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");

        LOG.info("STEP 7.2: Go back to user's Homepage and open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameC);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is not displayed in user's 'My Tasks' dashlet, but it should.");
        myTasksDashlet.clickOnTaskNameLink(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        LOG.info("STEP 8: Press Approve/reject Button;");
        editTaskPage.approve(thirdUserMessage, userDashboardPage);
        Assert.assertFalse(myTasksDashlet.isTaskPresent(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");

        LOG.info("STEP 9: Login Share as Creator task and verify task is displayed in My tasks dashlet;");
        setupAuthenticatedSession(C8596username, password);
        userDashboardPage.navigate(C8596username);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        Assert.assertTrue(myTasksDashlet.isTaskPresent(myTasksDashlet.finalApproveMessage), "The '" + myTasksDashlet.finalApproveMessage + "' message task is not displayed in user's 'My Tasks' dashlet, but it should.");

    }
}