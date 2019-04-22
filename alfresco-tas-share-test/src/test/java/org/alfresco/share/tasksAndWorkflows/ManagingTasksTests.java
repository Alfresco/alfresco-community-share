package org.alfresco.share.tasksAndWorkflows;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.tasksAndWorkflows.SelectAssigneePopUp;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * @author iulia.cojocea
 */
public class ManagingTasksTests extends ContextAwareWebTest
{
    @Autowired
    WorkflowService workflow;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @Autowired
    MyTasksDashlet myTasksDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SelectAssigneePopUp selectAssigneePopUp;

    private String testUser = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String docName = String.format("docName%s", RandomData.getRandomAlphanumeric());
    private String docContent = String.format("docContent%s", RandomData.getRandomAlphanumeric());
    private List<String> docs = new ArrayList<>();
    private String taskName = String.format("taskName%s", RandomData.getRandomAlphanumeric());
    private String taskTypeAndStatus = "Task, In Progress";

    @BeforeClass(alwaysRun = true)
    public void testSetup()
    {
        userService.create(adminUser, adminPassword, testUser, password, testUser + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, "firstName2", "lastName2");
        siteService.create(testUser, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        contentService.createDocument(testUser, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, docName, docContent);
        docs.add(docName);
        workflow.startNewTask(testUser, password, taskName, new Date(), testUser, Priority.High, siteName, docs, false);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, testUser);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser);
        userService.delete(adminUser,adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser,adminPassword,siteName );
    }


    @TestRail(id = "C8520")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
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

    @TestRail(id = "C8521")
    @Test(groups = { TestGroup.SANITY, TestGroup.TASKS})
    public void editTaskFromTasksDashletReassign()
    {
        LOG.info("Precondition");
        setupAuthenticatedSession(testUser, password);

        LOG.info("STEP 1: From 'My Tasks' dashlet click 'Edit Task' icon.");
        myTasksDashlet.clickEditTask(taskName);

        LOG.info("STEP 2: Click 'Reassign' button, reassign the task to 'user2' then click 'ok' button.");
        startWorkflowPage.clickOnReassignButton();
        selectAssigneePopUp.enterUserToSearch(user2);
        selectAssigneePopUp.clickOnSearchButton();
        selectAssigneePopUp.clickOnSelectButton();
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        userDashboardPage.navigate(user2);
        assertTrue(myTasksDashlet.isTaskPresent(taskName), "Task is not present in Active tasks");
    }
}