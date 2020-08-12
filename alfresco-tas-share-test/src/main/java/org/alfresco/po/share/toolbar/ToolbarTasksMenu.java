package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.WorkflowsIveStartedPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class ToolbarTasksMenu extends Toolbar
{
    @RenderWebElement
    @FindBy (id = "HEADER_MY_TASKS_text")
    private WebElement myTasks;

    @RenderWebElement
    @FindBy (id = "HEADER_MY_WORKFLOWS_text")
    private WebElement workflowsIveStarted;

    @Autowired
    private MyTasksPage myTasksPage;

    @Autowired
    private WorkflowsIveStartedPage workflowsIveStartedPage;

    public ToolbarTasksMenu assertMyTasksIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(myTasks), "My Tasks link is displayed");
        return this;
    }

    public ToolbarTasksMenu assertWorkflowIStartedIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(workflowsIveStarted), "Workflow I started link is displayed");
        return this;
    }

    public MyTasksPage clickMyTasks()
    {
        getBrowser().waitUntilElementClickable(myTasks).click();
        return (MyTasksPage) myTasksPage.renderedPage();
    }

    public WorkflowsIveStartedPage clickWorkflowsIStarted()
    {
        getBrowser().waitUntilElementClickable(workflowsIveStarted).click();
        return (WorkflowsIveStartedPage) workflowsIveStartedPage.renderedPage();
    }
}
