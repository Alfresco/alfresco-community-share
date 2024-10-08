package org.alfresco.po.share.toolbar;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ToolbarTasksMenu extends BasePage
{
    private final By myTasks = By.cssSelector("td[id='HEADER_MY_TASKS_text']");
    private final By workflowsIveStarted = By.cssSelector("td[id='HEADER_MY_WORKFLOWS_text']");

    public ToolbarTasksMenu(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ToolbarTasksMenu assertMyTasksIsDisplayed()
    {
        assertTrue(isElementDisplayed(myTasks), "My Tasks link is displayed");
        return this;
    }

    public ToolbarTasksMenu assertWorkflowIStartedIsDisplayed()
    {
        assertTrue(isElementDisplayed(workflowsIveStarted), "Workflow I started link is displayed");
        return this;
    }

    public MyTasksPage clickMyTasks()
    {
        waitInSeconds(3);
        clickElement(myTasks);
        return new MyTasksPage(webDriver);
    }

    public WorkflowsIveStartedPage clickWorkflowsIStarted()
    {
        clickElement(workflowsIveStarted);
        return new WorkflowsIveStartedPage(webDriver);
    }
}
