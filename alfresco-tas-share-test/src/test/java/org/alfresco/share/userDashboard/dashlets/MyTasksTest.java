package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.dataprep.CMISUtil.Priority;
import org.alfresco.dataprep.WorkflowService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyTasksDashlet;
import org.alfresco.po.share.tasksAndWorkflows.*;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    @Autowired WorkflowService workflowService;
    
    private String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
    private String taskName = "NewTask";
    private Date taskDate = new Date();
    private String taskTypeAndStatus = "Task, Not Yet Started";
    
    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        setupAuthenticatedSession(userName, password);
        workflowService.startNewTask(userName, password, taskName, taskDate, userName, Priority.Low, null, true);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }
    
    @TestRail(id="C2122")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void someTasksAssigned()
    {

        LOG.info("Step 1: Verify My Tasks dashlet");
        userDashboardPage.navigate(userName);
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
        Assert.assertTrue(myTasksDashlet.getTaskTypeAndStatusList().get(0).equals(taskTypeAndStatus), "Wrong type and status!");
        SimpleDateFormat dt = new SimpleDateFormat("dd MMMM, YYYY");
        System.out.println(dt.format(taskDate));
        Assert.assertTrue(myTasksDashlet.getTaskDueDateList().get(0).equals(dt.format(taskDate)), "Wrong due date!");

        LOG.info("Step 3: Click Start WorkFlow link");
        myTasksDashlet.clickOnStartWorkFlowLink();
        Assert.assertTrue(startWorkFlowPage.getPageTitle().equals("Alfresco » Start Workflow"), "Start Workflow page should be displayed!");
        
        LOG.info("Step 4: Go back to User Dashboard. Click Active Tasks link.");
        userDashboardPage.navigate(userName);
        myTasksDashlet.clickOnActiveTasksLink();
        Assert.assertTrue(activeTasksPage.getPageTitle().equals("Alfresco » My Tasks"), "My Tasks page should be displayed!");
        
        LOG.info("Step 5: Go back to User Dashboard. Click Completed Tasks link.");
        userDashboardPage.navigate(userName);
        myTasksDashlet.clickOnCompletedTasksLink();
        Assert.assertTrue(completedTasksPage.getPageTitle().equals("Alfresco » My Tasks"), "My Tasks page should be displayed!");
        
        LOG.info("Step 6: Go back to User Dashboard. Click on task's name link.");
        userDashboardPage.navigate(userName);
        myTasksDashlet.clickOnTaskNameLink(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().equals("Alfresco » Edit Task"), "Edit task page should be displayed!");
        
        LOG.info("Step 7: Go back to User Dashboard. Click Edit Task icon near the task");
        userDashboardPage.navigate(userName);
        myTasksDashlet.clickEditTask(taskName);
        Assert.assertTrue(editTaskPage.getPageTitle().equals("Alfresco » Edit Task"), "Edit task page should be displayed!");
        
        LOG.info("Step 7: Go back to User Dashboard. Click View Task icon");
        userDashboardPage.navigate(userName);
        myTasksDashlet.clickViewTask(taskName);
        Assert.assertTrue(viewTaskPage.getPageTitle().equals("Alfresco » Task Details"), "View task page should be displayed!!");
    }
}
