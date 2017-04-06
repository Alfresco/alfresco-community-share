package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

@PageObject
public class MyTasksPage extends SharePage<MyTasksPage> implements AccessibleByMenuBar
{
    @Autowired
    ToolbarTasksMenu toolbarTasksMenu;

    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    ViewTaskPage viewTaskPage;

    @RenderWebElement
    @FindBy(css = "h2[id$='default-filterTitle']")
    private WebElement activeTasksBar;

    @FindBy(css = "div[id$='default-tasks'] tr[class*='yui-dt-rec']")
    protected List<WebElement> taskRowList;

    @FindBy(css = "a[rel='completed']")
    private WebElement completedTasksButton;

    @FindBy(css = "[id$='default-startWorkflow-button-button']")
    private WebElement startWorkflow;

    @FindBy(css = "div[id*='_all-filter'] div h2")
    private WebElement tasksFilter;

    @FindBy(css = "div[id*='_due-filter'] div h2")
    private WebElement dueFilter;

    @FindBy(css = "div[id*='_priority-filter'] div h2")
    private WebElement priorityFilter;

    @RenderWebElement
    @FindBy(css = "div[id*='_assignee-filter'] div h2")
    private WebElement assigneeFilter;

    protected By editTaskLink = By.cssSelector("div[class*='task-edit'] a");
    protected By viewTaskLink = By.cssSelector("div[class*='task-view'] a");
    protected By viewWorkflowLink = By.cssSelector("div[class*='workflow-view'] a");
    protected By taskTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    protected String completeTaskName = "Request to join %s site";
    protected String status = "//a[@title = 'Edit Task' and text() = '%s']/../../div[@class = 'status']/span";

    @SuppressWarnings("unchecked")
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
        selectedTask.findElement(editTaskLink).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public MyTasksPage clickCompletedTasks()
    {
        completedTasksButton.click();
        return (MyTasksPage) this.renderedPage();
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

    public WorkflowDetailsPage clickViewWorkflow(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(viewWorkflowLink).click();
        return new WorkflowDetailsPage();
    }

    public boolean isEditTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return selectedTask.findElement(editTaskLink).isDisplayed();
    }

    public boolean isViewTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return selectedTask.findElement(viewTaskLink).isDisplayed();
    }

    public boolean isViewWorkflowOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        browser.mouseOver(selectedTask);
        return selectedTask.findElement(viewWorkflowLink).isDisplayed();
    }

    public boolean isTasksFilterDisplayed()
    {
        return tasksFilter.isDisplayed();
    }

    public boolean isDueFilterDisplayed()
    {
        return dueFilter.isDisplayed();
    }

    public boolean isPriorityFilterDisplayed()
    {
        return priorityFilter.isDisplayed();
    }

    public boolean isAssigneeFilterDisplayed()
    {
        return assigneeFilter.isDisplayed();
    }

    public boolean isActiveTasksBarDisplayed()
    {
        return activeTasksBar.isDisplayed();
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
}
