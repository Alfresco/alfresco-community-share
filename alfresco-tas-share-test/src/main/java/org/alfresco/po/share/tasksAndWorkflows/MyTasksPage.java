package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MyTasksPage extends SharePage2<MyTasksPage> implements AccessibleByMenuBar
{
    private EditTaskPage editTaskPage;
    private ViewTaskPage viewTaskPage;
    private WorkflowDetailsPage workflowDetailsPage;

    private final String completeTaskName = "Request to join %s site";
    private final String status = "//a[@title = 'Edit Task' and text() = '%s']/../../div[@class = 'status']/span";
    private final String statusCompleted = "//a[@title = 'View Task' and text() = '%s']/../../div[@class = 'status']/span";

    private final By taskRowList = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private final By editTaskLink = By.cssSelector("div[class*='task-edit'] a");
    private final By viewTaskLink = By.cssSelector("div[class*='task-view'] a");
    private final By viewWorkflowLink = By.cssSelector("div[class*='workflow-view'] a");
    private final By taskTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    private final By taskbarTitle = By.cssSelector("h2[id$='default-filterTitle']");
    private final By tasksBody = By.cssSelector(".alfresco-datatable.tasks");
    private final By completedTasksButton = By.cssSelector("a[rel='completed']");
    private final By startWorkflow = By.cssSelector("[id$='default-startWorkflow-button-button']");
    private final By tasksFilter = By.cssSelector("div[id*='_all-filter'] div h2");
    private final By dueFilter = By.cssSelector("div[id*='_due-filter'] div h2");
    private final By priorityFilter = By.cssSelector( "div[id*='_priority-filter'] div h2");
    private final By assigneeFilter = By.cssSelector("div[id*='_assignee-filter'] div h2");

    public MyTasksPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyTasksPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickTasks().clickMyTasks();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks";
    }

    public MyTasksPage assertMyTasksPageIsOpened()
    {
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "My Tasks page is opened");
        return this;
    }

    public MyTasksPage assertActiveTasksTitleIsDisplayed()
    {
        LOG.info("Assert Active tasks title is displayed");
        assertEquals(webElementInteraction.findElement(taskbarTitle).getText(), language.translate("myTasksPage.active.title"));
        return this;
    }

    public MyTasksPage assertCompletedTasksTitleIsDisplayed()
    {
        LOG.info("Assert Completed tasks title is displayed");
        assertEquals(webElementInteraction.findElement(taskbarTitle).getText(), language.translate("myTasksPage.completed.title"));
        return this;
    }

    public WebElement selectTask(final String taskName)
    {
        return webElementInteraction.findFirstElementWithValue(taskRowList, taskName);
    }

    public boolean checkTaskWasFound(String taskName)
    {
        return selectTask(taskName) != null;
    }

    public EditTaskPage clickEditTask(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        WebElement editAction = selectedTask.findElement(editTaskLink);
        webElementInteraction.mouseOver(editAction);
        webElementInteraction.clickElement(editAction);
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public void clickCompletedTasks()
    {
        webElementInteraction.clickElement(completedTasksButton);
        webElementInteraction.waitUntilElementContainsText(webElementInteraction.findElement(taskbarTitle), "Completed Tasks");
    }

    public ViewTaskPage clickViewTask(String taskName)
    {
        webElementInteraction.mouseOver(selectTask(taskName));
        webElementInteraction.clickElement(selectTask(String.format(completeTaskName, taskName)).findElement(viewTaskLink));
        return (ViewTaskPage) viewTaskPage.renderedPage();
    }

    public boolean isStartWorkflowDisplayed()
    {
        return webElementInteraction.isElementDisplayed(startWorkflow);
    }

    public MyTasksPage assertStartWorkflowIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(startWorkflow), "Start workflow is displayed");
        return this;
    }

    public StartWorkflowPage clickStartWorkflowButton()
    {
        webElementInteraction.clickElement(startWorkflow);
        StartWorkflowPage startWorkflowPage = new StartWorkflowPage(webDriver);
        return startWorkflowPage;
    }

    public String getTaskTitle()
    {
        return webElementInteraction.waitUntilElementIsVisible(taskTitle).getText();
    }

    public WorkflowDetailsPage clickViewWorkflow(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        webElementInteraction.clickElement(selectedTask.findElement(viewWorkflowLink));
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

    public boolean isEditTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        return webElementInteraction.isElementDisplayed(selectedTask, editTaskLink);
    }

    public boolean isViewTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        return webElementInteraction.isElementDisplayed(selectedTask, viewTaskLink);
    }

    public boolean isViewWorkflowOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        return webElementInteraction.isElementDisplayed(selectedTask, viewWorkflowLink);
    }

    public boolean isTasksFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(tasksFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dueFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(priorityFilter);
    }

    public boolean isAssigneeFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(assigneeFilter);
    }

    public boolean isActiveTasksBarDisplayed()
    {
        return webElementInteraction.isElementDisplayed(taskbarTitle);
    }

    public EditTaskPage clickOnTaskTitle(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        webElementInteraction.mouseOver(selectedTask);
        webElementInteraction.clickElement(selectedTask.findElement(taskTitle));
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public String getStatus(String workflowName)
    {
        return webElementInteraction.findElement(By.xpath(String.format(status, workflowName))).getText();
    }

    public String getStatusCompleted(String workflowName)
    {
        return webElementInteraction.findElement(By.xpath(String.format(statusCompleted, workflowName))).getText();
    }
}