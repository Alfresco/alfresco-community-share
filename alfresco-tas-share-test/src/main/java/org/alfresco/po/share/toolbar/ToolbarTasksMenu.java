package org.alfresco.po.share.toolbar;

import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ToolbarTasksMenu extends Toolbar
{
    @FindBy(id = "HEADER_MY_TASKS_text")
    private WebElement myTasks;

    @FindBy(id = "HEADER_MY_WORKFLOWS_text")
    private WebElement workflowsIveStarted;

    public boolean isMyTasksDisplayed()
    {
        tasksLink.click();
        return browser.isElementDisplayed(myTasks);
    }

    public boolean isWorkflowsIveStartedDisplayed()
    {
        tasksLink.click();
        return browser.isElementDisplayed(workflowsIveStarted);
    }

    public void clickMyTasks()
    {
        tasksLink.click();
        myTasks.click();
    }

    public void clickWorkflowsIveStarted()
    {
        tasksLink.click();
        workflowsIveStarted.click();
    }
}
