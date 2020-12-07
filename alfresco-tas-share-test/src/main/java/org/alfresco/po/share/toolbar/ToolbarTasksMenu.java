package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class ToolbarTasksMenu extends BasePage
{
    @RenderWebElement
    private final By myTasks = By.id("HEADER_MY_TASKS_text");
    private final By workflowsIveStarted = By.id("HEADER_MY_WORKFLOWS_text");

    public ToolbarTasksMenu(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public ToolbarTasksMenu assertMyTasksIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(myTasks), "My Tasks link is displayed");
        return this;
    }

    public ToolbarTasksMenu assertWorkflowIStartedIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(workflowsIveStarted), "Workflow I started link is displayed");
        return this;
    }

    public MyTasksPage clickMyTasks()
    {
        getBrowser().waitUntilElementClickable(myTasks).click();
        return (MyTasksPage) new MyTasksPage(browser).renderedPage();
    }

    public WorkflowsIveStartedPage clickWorkflowsIStarted()
    {
        getBrowser().waitUntilElementClickable(workflowsIveStarted).click();
        return (WorkflowsIveStartedPage) new WorkflowsIveStartedPage(browser).renderedPage();
    }
}
