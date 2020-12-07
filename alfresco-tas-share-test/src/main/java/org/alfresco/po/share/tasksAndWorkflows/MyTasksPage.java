package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
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
    @RenderWebElement
    private final By taskbarTitle = By.cssSelector("h2[id$='default-filterTitle']");
    @RenderWebElement
    private final By tasksBody = By.cssSelector(".alfresco-datatable.tasks");
    private final By completedTasksButton = By.cssSelector("a[rel='completed']");
    @RenderWebElement
    private final By startWorkflow = By.cssSelector("[id$='default-startWorkflow-button-button']");
    private final By tasksFilter = By.cssSelector("div[id*='_all-filter'] div h2");
    private final By dueFilter = By.cssSelector("div[id*='_due-filter'] div h2");
    private final By priorityFilter = By.cssSelector( "div[id*='_priority-filter'] div h2");
    private final By assigneeFilter = By.cssSelector("div[id*='_assignee-filter'] div h2");

    public MyTasksPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @SuppressWarnings ("unchecked")
    @Override
    public MyTasksPage navigateByMenuBar()
    {
        return (MyTasksPage) new Toolbar(browser).clickTasks().clickMyTasks().renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks";
    }

    public MyTasksPage assertMyTasksPageIsOpened()
    {
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "My Tasks page is opened");
        return this;
    }

    public MyTasksPage assertActiveTasksTitleIsDisplayed()
    {
        LOG.info("Assert Active tasks title is displayed");
        assertEquals(getBrowser().findElement(taskbarTitle).getText(), language.translate("myTasksPage.active.title"));
        return this;
    }

    public MyTasksPage assertCompletedTasksTitleIsDisplayed()
    {
        LOG.info("Assert Completed tasks title is displayed");
        assertEquals(getBrowser().findElement(taskbarTitle).getText(), language.translate("myTasksPage.completed.title"));
        return this;
    }

    public WebElement selectTask(final String taskName)
    {
        return getBrowser().findFirstElementWithValue(taskRowList, taskName);
    }

    public boolean checkTaskWasFound(String taskName)
    {
        return selectTask(taskName) != null;
    }

    public EditTaskPage clickEditTask(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        WebElement editAction = selectedTask.findElement(editTaskLink);
        getBrowser().mouseOver(editAction);
        getBrowser().waitUntilElementClickable(editAction).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public void clickCompletedTasks()
    {
        getBrowser().findElement(completedTasksButton).click();
        this.renderedPage();
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(taskbarTitle), "Completed Tasks");
    }

    public ViewTaskPage clickViewTask(String taskName)
    {
        getBrowser().mouseOver(selectTask(taskName));
        selectTask(String.format(completeTaskName, taskName)).findElement(viewTaskLink).click();
        return (ViewTaskPage) viewTaskPage.renderedPage();
    }

    public boolean isStartWorkflowDisplayed()
    {
        return getBrowser().isElementDisplayed(startWorkflow);
    }

    public MyTasksPage assertStartWorkflowIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(startWorkflow), "Start workflow is displayed");
        return this;
    }

    public StartWorkflowPage clickStartWorkflowButton()
    {
        getBrowser().waitUntilElementClickable(startWorkflow).click();
        StartWorkflowPage startWorkflowPage = new StartWorkflowPage(browser);
        startWorkflowPage.renderedPage();
        return startWorkflowPage;
    }

    public String getTaskTitle()
    {
        return getBrowser().waitUntilElementVisible(taskTitle).getText();
    }

    public WorkflowDetailsPage clickViewWorkflow(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        selectedTask.findElement(viewWorkflowLink).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

    public boolean isEditTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        return getBrowser().isElementDisplayed(selectedTask, editTaskLink);
    }

    public boolean isViewTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        return getBrowser().isElementDisplayed(selectedTask, viewTaskLink);
    }

    public boolean isViewWorkflowOptionDisplayed(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        return getBrowser().isElementDisplayed(selectedTask, viewWorkflowLink);
    }

    public boolean isTasksFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(tasksFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(dueFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(priorityFilter);
    }

    public boolean isAssigneeFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(assigneeFilter);
    }

    public boolean isActiveTasksBarDisplayed()
    {
        return getBrowser().isElementDisplayed(taskbarTitle);
    }

    public EditTaskPage clickOnTaskTitle(String taskName)
    {
        WebElement selectedTask = selectTask(taskName);
        getBrowser().mouseOver(selectedTask);
        selectedTask.findElement(taskTitle).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public String getStatus(String workflowName)
    {
        return getBrowser().findElement(By.xpath(String.format(status, workflowName))).getText();
    }

    public String getStatusCompleted(String workflowName)
    {
        return getBrowser().findElement(By.xpath(String.format(statusCompleted, workflowName))).getText();
    }
}