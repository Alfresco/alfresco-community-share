package org.alfresco.share.tasksAndWorkflows;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.GroupService;
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
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j

/**
 * @author iulia.cojocea
 */
public class ManagingTasksTests extends BaseTest
{
    @Autowired
    WorkflowService workflowService;

    @Autowired
    protected GroupService groupService;
    //@Autowired
    StartWorkflowPage startWorkflowPage;

    //@Autowired
    MyTasksDashlet myTasksDashlet;

    // @Autowired
    UserDashboardPage userDashboardPage;

    //@Autowired
    SelectAssigneePopUp selectAssigneePopUp;

    //@Autowired
    MyTasksPage myTasksPage;

    //@Autowired
    SelectPopUpPage selectPopUpPage;

    //@Autowired
    EditTaskPage editTaskPage;

    //@Autowired
    ViewTaskPage viewTaskPage;

    //@Autowired
    WorkflowDetailsPage workflowDetailsPage;

    private String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private String C8551group = "C8551group_" + uniqueIdentifier;
    private String C8596group = "C8559group_" + uniqueIdentifier;
    private String C8551subgroup = "C8551subgroup_" + uniqueIdentifier;
    private String taskName = String.format("taskName%s", uniqueIdentifier);
    private String taskTypeAndStatus = "Task, In Progress";
    private String docName = String.format("docName%s", uniqueIdentifier);
    private String docContent = String.format("docContent%s", uniqueIdentifier);
    private List<String> docs = new ArrayList<>();
    private Date timeDate = new Date();
    private final ThreadLocal<UserModel> testUser = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8521username = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8551usernameA = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8551usernameB = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8551usernameC = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8596username = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8596usernameA = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8596usernameB = new ThreadLocal<>();
    private final ThreadLocal<UserModel> C8596usernameC = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void testSetup()
    {
        log.info("Precondition: Create user and a workflow.");
        testUser.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8521username.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8551usernameA.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8551usernameB.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8551usernameC.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8596username.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8596usernameA.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8596usernameB.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        C8596usernameC.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteName is created");
        siteName.set(getDataSite().usingUser(testUser.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(testUser.get());

        myTasksDashlet = new MyTasksDashlet(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectAssigneePopUp = new SelectAssigneePopUp(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        myTasksPage = new MyTasksPage(webDriver);
        viewTaskPage = new ViewTaskPage(webDriver);
        editTaskPage = new EditTaskPage(webDriver);

        contentService.createDocument(testUser.get().getUsername(), testUser.get().getPassword(), siteName.get().getId(), CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        docs.add(docName);
        groupService.createGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8551group);
        groupService.createGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8596group);
        groupService.addSubGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8551group, C8551subgroup);
        groupService.addUserToGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8596group, C8596usernameA.get().getUsername());
        groupService.addUserToGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8596group, C8596usernameB.get().getUsername());
        groupService.addUserToGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8596group, C8596usernameC.get().getUsername());
        workflowService.startNewTask(testUser.get().getUsername(), testUser.get().getPassword(), taskName, timeDate, testUser.get().getUsername(), Priority.High, siteName.get().getId(), docs, false);
        workflowService.startGroupReview(C8596username.get().getUsername(), C8596username.get().getPassword(), taskName, timeDate, C8596group, Priority.Normal, null, 50, true);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        workflowService.deleteWorkflow(testUser.get().getUsername(), testUser.get().getPassword(), workflowService.getWorkflowId(testUser.get().getUsername(), testUser.get().getPassword(), testUser.get().getUsername()));
        workflowService.deleteWorkflow(C8596username.get().getUsername(), C8596username.get().getPassword(), workflowService.getWorkflowId(C8596username.get().getUsername(), C8596username.get().getPassword(), C8596username.get().getUsername()));

        groupService.removeSubgroupFromGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8551group, C8551subgroup);
        groupService.removeGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8551group);
        groupService.removeGroup(getAdminUser().getUsername(), getAdminUser().getPassword(), C8596group);

        deleteSitesIfNotNull(siteName.get());

        deleteUsersIfNotNull(testUser.get());
        deleteUsersIfNotNull(C8521username.get());
        deleteUsersIfNotNull(C8551usernameA.get());
        deleteUsersIfNotNull(C8551usernameB.get());
        deleteUsersIfNotNull(C8551usernameC.get());
        deleteUsersIfNotNull(C8596username.get());
        deleteUsersIfNotNull(C8596usernameA.get());
        deleteUsersIfNotNull(C8596usernameB.get());
        deleteUsersIfNotNull(C8596usernameC.get());
    }


    @TestRail (id = "C8520")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editTaskFromTasksDashletChangeStatus()
    {
        log.info("Precondition");
        authenticateUsingLoginPage(testUser.get());

        log.info("STEP 1: From 'My Tasks' dashlet click 'Edit Task' icon.");
        myTasksDashlet.editTask(taskName);

        log.info("STEP 2: Change the status of the workflow (eg. 'In Progress') then click 'Save and Close' button.");
        startWorkflowPage.selectTaskStatus("In Progress");
        startWorkflowPage.saveAndClose();
        assertTrue(myTasksDashlet.getTaskTypeAndStatus(taskName).equals(taskTypeAndStatus),
            "Wrong type and status! Expected " + taskTypeAndStatus + "but found: " + myTasksDashlet.getTaskTypeAndStatus(taskName));
    }

    @TestRail (id = "C8521")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void editTaskFromTasksDashletReassign()
    {
        log.info("Precondition");
        authenticateUsingCookies(testUser.get());

        log.info("STEP 1: From 'My Tasks' dashlet click 'Edit Task' icon.");
        myTasksDashlet.editTask(taskName);

        log.info("STEP 2: Click 'Reassign' button, reassign the task to '" + C8521username + "' then click 'ok' button.");
        startWorkflowPage.clickOnReassignButton();
        selectAssigneePopUp.enterUserToSearch(C8521username.get().getUsername());
        selectAssigneePopUp.clickOnSearchButton();
        selectAssigneePopUp.clickOnSelectButton();

        authenticateUsingCookies(C8521username.get());
        userDashboardPage.navigate(C8521username.get());
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
    }

    @TestRail (id = "C8551")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void groupTaskSubgroup()
    {
        authenticateUsingCookies(getAdminUser());

        log.info("Precondition 1. Some users are added to the parent group.");
        groupService.addUserToGroup(getAdminUser().getUsername(),getAdminUser().getPassword(), C8551group, C8551usernameA.get().getUsername());

        log.info("Precondition 2. Some users are added to the subgroup.");
        groupService.addUserToGroup(getAdminUser().getUsername(),getAdminUser().getPassword(), C8551subgroup, C8551usernameB.get().getUsername());
        groupService.addUserToGroup(getAdminUser().getUsername(),getAdminUser().getPassword(), C8551subgroup, C8551usernameC.get().getUsername());

        log.info("STEP 1: Click Start workflow action (e.g. from My tasks dashlet).");
        myTasksPage.navigate();
        myTasksPage.clickStartWorkflowButton();

        log.info("STEP 2: Select Group Review and approve type.");
        startWorkflowPage.selectAWorkflow("Review and Approve (group review)");

        log.info("STEP 3: From 'Assignee' subsection click 'Select' button.");
        startWorkflowPage.clickGroupSelectButton();

        log.info("STEP 4: Enter a group name and click 'Search' button.");
        selectPopUpPage.search(C8551group);

        log.info("STEP 5: Click Add ('+') button and then Ok.");
        selectPopUpPage.clickAddIcon(C8551group);
        selectPopUpPage.clickOkButton();

        //Check if the selected group is displayed on the form.
        Assert.assertEquals(startWorkflowPage.getSelectedReviewerName(), C8551group, "The displayed Assignee name is not the same with the chosen one");

        //Select a message text for the task
        String taskDescription = "PRR Test " + uniqueIdentifier;
        startWorkflowPage.addWorkflowDescription(taskDescription);

        log.info("STEP 6: Click Start workflow button.");
        startWorkflowPage.clickStartWorkflow();
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed after clicking on 'Submit New Workflow' button.");

        log.info("STEP 7: login Share as every subgroup member and verify task is displayed in My tasks dashlet");
        //Verify the first user:
        authenticateUsingCookies(C8551usernameB.get());

        myTasksPage.navigate();
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed as default when navigating to My Tasks page.");
        Assert.assertEquals(myTasksPage.getTaskTitle(), taskDescription, "The created task is not displayed in 'Active Tasks' list.");

        //Verify the second user:
        authenticateUsingCookies(C8551usernameC.get());

        myTasksPage.navigate();
        Assert.assertTrue(myTasksPage.isActiveTasksBarDisplayed(), "Active Task page is not displayed as default when navigating to My Tasks page.");
        Assert.assertEquals(myTasksPage.getTaskTitle(), taskDescription, "The created task is not displayed in 'Active Tasks' list.");
    }

    @TestRail (id = "C8596")
    @Test (groups = { TestGroup.SANITY, TestGroup.TASKS })
    public void groupReviewApproveReject() throws InterruptedException {
        String firstUserMessage = "This is the text message from the first user.";
        String secondUserMessage = "This is the text message from the second user.";
        String thirdUserMessage = "This is the text message from the third user.";
        String C8596fullNameA = C8596usernameA.get().getFirstName() + " " + C8596usernameA.get().getLastName();
        String C8596fullNameB = C8596usernameB.get().getFirstName() + " " + C8596usernameB.get().getLastName();

        log.info("Precondition: login as '" + C8596usernameA + "' user.");
        authenticateUsingCookies(C8596usernameA.get());

        log.info("STEP 1: Open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameA.get());
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
        myTasksDashlet.clickTaskName(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        log.info("STEP 2: Press Approve/reject Button;");
        editTaskPage.commentAndClickApprove(firstUserMessage);

        log.info("STEP 3: log in as '" + C8596usernameB + "' user;");
        authenticateUsingCookies(C8596usernameB.get());

        log.info("STEP 4.1: Check if Workflow History block information that User1 has approved the task is present;");
        userDashboardPage.navigate(C8596usernameB.get());

        //Check if My Task dashlet is displayed.
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo(taskName);

        //Click on 'View task' icon and check if the user can access 'Workflow Details' page.
        myTasksDashlet.viewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Task Details"), "View task page should be displayed!!");
        viewTaskPage.clickWorkflowDetailsLink();
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Workflow Details"), "View Workflow Details page should be displayed!!");

        //Check if History block is displayed with the information that C8596fullNameA has provided in STEP 2.
        Assert.assertTrue(workflowDetailsPage.isHistoryBlockPresent(), "History block is not present but is should!!");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameA), firstUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");

        log.info("STEP 4.2: Go back to user's Homepage and open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameB.get());
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
        myTasksDashlet.clickTaskName(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        log.info("STEP 5: Press Approve/reject Button;");
        editTaskPage.commentAndClickReject(secondUserMessage);
//        Assert.assertFalse(myTasksDashlet.assertTaskNameEqualsTo(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");

        log.info("STEP 6: log in as '" + C8596usernameC + "' user;");
        authenticateUsingCookies(C8596usernameC.get());

        log.info("STEP 7.1: Check if Workflow History block information that '" + C8596fullNameA + "' has approved and '" + C8596fullNameB + "' has rejected the task is present;");
        userDashboardPage.navigate(C8596usernameC.get());

        log.info("=> Check if My Task dashlet is displayed.");
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo(taskName);

        log.info("=> Click on 'View task' icon and check if the user can access 'Workflow Details' page.");
        myTasksDashlet.viewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Task Details"), "View task page should be displayed!!");
        viewTaskPage.clickWorkflowDetailsLink();
        Assert.assertTrue(viewTaskPage.getPageTitle().contains("Workflow Details"), "View Workflow Details page should be displayed!!");

        log.info("=> Check if History block is displayed with the information that '" + C8596fullNameA + "' has provided in STEP 2 and user '" + C8596fullNameB + "' in STEP 5.");
        Assert.assertTrue(workflowDetailsPage.isHistoryBlockPresent(), "History block is not present but is should!!");
        Assert.assertEquals(workflowDetailsPage.getHistoryOutcome(C8596fullNameA), "Approved", "The outcome from History block does not coincide with the action that user '" + C8596usernameA + "' did.");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameA), firstUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");
        Assert.assertEquals(workflowDetailsPage.getHistoryOutcome(C8596fullNameB), "Rejected", "The outcome from History block does not coincide with the action that user '" + C8596usernameB + "' did.");
        Assert.assertEquals(workflowDetailsPage.getHistoryComment(C8596fullNameB), secondUserMessage, "The Comment from History block does not coincide with the comment added by user '" + C8596usernameA + "'.");

        log.info("STEP 7.2: Go back to user's Homepage and open task from My Tasks dashlet;");
        userDashboardPage.navigate(C8596usernameC.get());
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo(taskName);
        myTasksDashlet.clickTaskName(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().contains("Edit Task"), "Edit task page should be displayed!");

        log.info("STEP 8: Press Approve/reject Button;");
        editTaskPage.commentAndClickReject(thirdUserMessage);
//        Assert.assertFalse(myTasksDashlet.assertTaskNameEqualsTo(taskName), "'" + taskName + "' task is displayed in user's 'My Tasks' dashlet, but it shouldn't.");

        log.info("STEP 9: login Share as Creator task and verify task is displayed in My tasks dashlet;");
        authenticateUsingCookies(C8596username.get());
        userDashboardPage.navigate(C8596username.get());
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
        myTasksDashlet.assertTaskNameEqualsTo("The document was reviewed and rejected.");
    }
}