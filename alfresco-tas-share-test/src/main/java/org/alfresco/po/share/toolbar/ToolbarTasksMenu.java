package org.alfresco.po.share.toolbar;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ToolbarTasksMenu extends Toolbar
{
    @FindBy (id = "HEADER_MY_TASKS_text")
    private WebElement myTasks;

    @FindBy (id = "HEADER_MY_WORKFLOWS_text")
    private WebElement workflowsIveStarted;

    public boolean isMyTasksDisplayed()
    {
        getBrowser().waitUntilElementClickable(tasksLink).click();
        return browser.isElementDisplayed(myTasks);
    }

    public boolean isWorkflowsIveStartedDisplayed()
    {
        getBrowser().waitUntilElementClickable(tasksLink).click();
        return browser.isElementDisplayed(workflowsIveStarted);
    }

    public void clickMyTasks()
    {
        getBrowser().waitUntilElementClickable(tasksLink).click();
        getBrowser().waitUntilElementClickable(myTasks).click();
    }

    public void clickWorkflowsIveStarted()
    {
        getBrowser().waitUntilElementClickable(tasksLink).click();
        getBrowser().waitUntilElementClickable(workflowsIveStarted).click();
    }
}
