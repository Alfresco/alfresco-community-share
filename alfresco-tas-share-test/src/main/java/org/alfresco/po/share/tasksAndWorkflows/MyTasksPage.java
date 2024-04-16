package org.alfresco.po.share.tasksAndWorkflows;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyTasksPage extends SharePage2<MyTasksPage> implements AccessibleByMenuBar
{
    private final int BEGIN_INDEX = 0;
    private final String EMPTY_SPACE = " ";

    private final By taskRowList = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private final By editTaskLink = By.cssSelector("div[class*='task-edit'] a");
    private final By viewTaskLink = By.cssSelector("div[class*='task-view'] a");
    private final By viewWorkflowLink = By.cssSelector("div[class*='workflow-view'] a");
    private final By taskTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    private final By taskbarTitle = By.cssSelector("h2[id$='default-filterTitle']");
    private final By completedTasksButton = By.cssSelector("a[rel='completed']");
    private final By startWorkflow = By.cssSelector("[id$='default-startWorkflow-button-button']");
    private final By tasksFilter = By.cssSelector("div[id*='_all-filter'] div h2");
    private final By dueFilter = By.cssSelector("div[id*='_due-filter'] div h2");
    private final By priorityFilter = By.cssSelector( "div[id*='_priority-filter'] div h2");
    private final By assigneeFilter = By.cssSelector("div[id*='_assignee-filter'] div h2");
    private final By noTaskMessage = By.xpath("//div[text()='No tasks']");

    private final String completeTaskName = "Request to join %s site";
    private final String status = "//a[@title = 'Edit Task' and text() = '%s']/../../div[@class = 'status']/span";
    private final String statusCompleted = "//a[@title = 'View Task' and text() = '%s']/../../div[@class = 'status']/span";
    private final String activeTaskUrl = "filter=workflows|active";

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

    public MyTasksPage navigateToMyTasks()
    {
        return navigateByMenuBar();
    }

    public MyTasksPage assertRejectedTaskIsNotDisplayedInActiveTasks(String taskName)
    {
        log.info("Assert rejected task is not displayed {}", taskName);
        boolean isTaskNameDisplayed = isElementDisplayed(By.xpath(String.format(completeTaskName, taskName)));
        assertFalse(isTaskNameDisplayed, String.format("Task name %s is displayed", taskName));
        return this;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-tasks";
    }

    public MyTasksPage assertMyTasksPageIsOpened()
    {
        assertTrue(getCurrentUrl().contains(getRelativePath()), "My Tasks page is opened");
        return this;
    }

    public MyTasksPage assertActiveTasksTitleIsDisplayed()
    {
        log.info("Assert Active tasks title is displayed");
        waitUrlContains(activeTaskUrl, WAIT_10.getValue());
        assertEquals(getElementText(taskbarTitle), language.translate("myTasksPage.active.title"));
        return this;
    }

    public MyTasksPage assertCompletedTasksTitleIsDisplayed()
    {
        log.info("Assert Completed tasks title is displayed");
        assertEquals(getElementText(taskbarTitle), language.translate("myTasksPage.completed.title"));
        return this;
    }

    private WebElement getTaskName(String taskName)
    {
        waitInSeconds(5);
        return findFirstElementWithValue(taskRowList, taskName);
    }

    public MyTasksPage assertTaskNameEqualsTo(String expectedTaskName)
    {
        log.info("Assert task name equals to {}", expectedTaskName);
        String actualTaskName = getActualTaskName(expectedTaskName);

        assertEquals(actualTaskName, expectedTaskName,
            String.format("Task name not equals to %s ", expectedTaskName));
        return this;
    }

    public MyTasksPage assertNoTaskIsDisplayed(){
        waitInSeconds(2);
        assertTrue(isElementDisplayed(noTaskMessage), "Check task List");
        return this;
    }

    private String getActualTaskName(String expectedTaskName)
    {
        String taskName = getElementText(getTaskName(expectedTaskName));
        String cutRequestToJoinText = taskName.substring(taskName.indexOf(expectedTaskName));
        return cutRequestToJoinText.substring(BEGIN_INDEX, cutRequestToJoinText.indexOf(EMPTY_SPACE));
    }

    public EditTaskPage editTask(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        mouseOver(selectedTask);
        WebElement editAction = selectedTask.findElement(editTaskLink);
        mouseOver(editAction);
        clickElement(editAction);
        return new EditTaskPage(webDriver);
    }

    public MyTasksPage navigateToCompletedTasks()
    {
        clickElement(completedTasksButton);
        waitUntilElementContainsText(findElement(taskbarTitle), "Completed Tasks");
        return this;
    }

    public ViewTaskPage viewTask(String taskName)
    {
        mouseOver(getTaskName(taskName));
        clickElement(getTaskName(String.format(completeTaskName, taskName)).findElement(viewTaskLink));
        return new ViewTaskPage(webDriver);
    }

    public MyTasksPage assertStartWorkflowIsDisplayed()
    {
        assertTrue(isElementDisplayed(startWorkflow), "Start workflow is displayed");
        return this;
    }

    public StartWorkflowPage clickStartWorkflowButton()
    {
        clickElement(startWorkflow);
        return new StartWorkflowPage(webDriver);
    }

    public String getTaskTitle()
    {
        return waitUntilElementIsVisible(taskTitle).getText();
    }

    public WorkflowDetailsPage clickViewWorkflow(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        waitInSeconds(3);
        mouseOver(selectedTask);
        clickElement(selectedTask.findElement(viewWorkflowLink));
        return new WorkflowDetailsPage(webDriver);
    }

    public boolean isEditTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        mouseOver(selectedTask);
        return isElementDisplayed(selectedTask, editTaskLink);
    }

    public boolean isViewTaskOptionDisplayed(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        mouseOver(selectedTask);
        return isElementDisplayed(selectedTask, viewTaskLink);
    }

    public boolean isViewWorkflowOptionDisplayed(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        mouseOver(selectedTask);
        return isElementDisplayed(selectedTask, viewWorkflowLink);
    }

    public boolean isTasksFilterDisplayed()
    {
        return isElementDisplayed(tasksFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return isElementDisplayed(dueFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return isElementDisplayed(priorityFilter);
    }

    public boolean isAssigneeFilterDisplayed()
    {
        return isElementDisplayed(assigneeFilter);
    }

    public boolean isActiveTasksBarDisplayed()
    {
        waitUntilElementIsVisible(taskbarTitle);
        return isElementDisplayed(taskbarTitle);
    }

    public EditTaskPage clickOnTaskTitle(String taskName)
    {
        WebElement selectedTask = getTaskName(taskName);
        mouseOver(selectedTask);
        clickElement(selectedTask.findElement(taskTitle));
        return new EditTaskPage(webDriver);
    }

    public String getStatus(String workflowName)
    {
        return getElementText(By.xpath(String.format(status, workflowName)));
    }

    public String getStatusCompleted(String workflowName)
    {
        return getElementText(By.xpath(String.format(statusCompleted, workflowName)));
    }
}