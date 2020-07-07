package org.alfresco.po.share.tasksAndWorkflows;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class MyTasksPage extends SharePage<MyTasksPage> implements AccessibleByMenuBar
{
    @FindBy (css = "div[id$='default-tasks'] tr[class*='yui-dt-rec']")
    protected List<WebElement> taskRowList;
    protected By editTaskLink = By.cssSelector("div[class*='task-edit'] a");
    protected By viewTaskLink = By.cssSelector("div[class*='task-view'] a");
    protected By viewWorkflowLink = By.cssSelector("div[class*='workflow-view'] a");
    protected By taskTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    protected String completeTaskName = "Request to join %s site";
    protected String status = "//a[@title = 'Edit Task' and text() = '%s']/../../div[@class = 'status']/span";
    protected String statusCompleted = "//a[@title = 'View Task' and text() = '%s']/../../div[@class = 'status']/span";
    @Autowired
    ToolbarTasksMenu toolbarTasksMenu;
    @Autowired
    EditTaskPage editTaskPage;
    @Autowired
    ViewTaskPage viewTaskPage;
    @Autowired
    WorkflowDetailsPage workflowDetailsPage;
    @Autowired
    StartWorkflowPage startWorkflowPage;
    @RenderWebElement
    @FindBy (css = "h2[id$='default-filterTitle']")
    public WebElement activeTasksBar;
    @RenderWebElement
    @FindBy (css = ".alfresco-datatable.tasks")
    private WebElement tasksBody;
    @FindBy (css = "a[rel='completed']")
    private WebElement completedTasksButton;
    @FindBy (css = "[id$='default-startWorkflow-button-button']")
    private WebElement startWorkflow;
    @FindBy (css = "div[id*='_all-filter'] div h2")
    private WebElement tasksFilter;
    @FindBy (css = "div[id*='_due-filter'] div h2")
    private WebElement dueFilter;
    @FindBy (css = "div[id*='_priority-filter'] div h2")
    private WebElement priorityFilter;
    @RenderWebElement
    @FindBy (css = "div[id*='_assignee-filter'] div h2")
    private WebElement assigneeFilter;

    @SuppressWarnings ("unchecked")
    @Override
    public MyTasksPage navigateByMenuBar()
    {
        toolbarTasksMenu.clickMyTasks();
        return (MyTasksPage) renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks";
    }

    /**
     * Retrieves the link that match the task name.
     *
     * @param taskName String
     * @return WebElement that match the task name
     */
    public WebElement selectTask(final String taskName)
    {
        return browser.findFirstElementWithValue(taskRowList, taskName);
    }

    /**
     * Check if the task was found
     *
     * @param taskName String
     * @return true if the task was found, else return false
     */
    public Boolean checkTaskWasFound(String taskName)
    {
        return selectTask(taskName) != null;
    }

    public EditTaskPage clickEditTask(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        WebElement editAction = selectedTask.findElement(editTaskLink);
        browser.mouseOver(editAction);
        browser.waitUntilElementClickable(editAction).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public void clickCompletedTasks()
    {
        completedTasksButton.click();
        this.renderedPage();
        browser.waitUntilElementContainsText(activeTasksBar, "Completed Tasks");
    }

    public ViewTaskPage clickViewTask(String taskName)
    {
        browser.mouseOver(selectTask(taskName));
        selectTask(String.format(completeTaskName, taskName)).findElement(viewTaskLink).click();
        return (ViewTaskPage) viewTaskPage.renderedPage();
    }

    public boolean isStartWorkflowDisplayed()
    {
        return browser.isElementDisplayed(startWorkflow);
    }

    public StartWorkflowPage clickStartWorkflowButton()
    {
        browser.waitUntilElementClickable(startWorkflow).click();
        return (StartWorkflowPage) startWorkflowPage.renderedPage();
    }

    public String getTaskTitle()
    {
        return browser.waitUntilElementVisible(taskTitle).getText();
    }

    public WorkflowDetailsPage clickViewWorkflow(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(viewWorkflowLink).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

    public boolean isEditTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return browser.isElementDisplayed(selectedTask, editTaskLink);
    }

    public boolean isViewTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return browser.isElementDisplayed(selectedTask, viewTaskLink);
    }

    public boolean isViewWorkflowOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return browser.isElementDisplayed(selectedTask, viewWorkflowLink);
    }

    public boolean isTasksFilterDisplayed()
    {
        return browser.isElementDisplayed(tasksFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return browser.isElementDisplayed(dueFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return browser.isElementDisplayed(priorityFilter);
    }

    public boolean isAssigneeFilterDisplayed()
    {
        return browser.isElementDisplayed(assigneeFilter);
    }

    public boolean isActiveTasksBarDisplayed()
    {
        return browser.isElementDisplayed(activeTasksBar);
    }

    public EditTaskPage clickOnTaskTitle(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(taskTitle).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public String getStatus(String workflowName)
    {
        return browser.findElement(By.xpath(String.format(status, workflowName))).getText();
    }

    public String getStatusCompleted(String workflowName)
    {
        return browser.findElement(By.xpath(String.format(statusCompleted, workflowName))).getText();
    }
}