package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_20;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MyTasksDashlet extends Dashlet<MyTasksDashlet>
{
    private final String NEW_LINE = "\n";
    private final int BEGIN_INDEX = 0;
    private final String FILTER_ARROW = " ▾";

    private final By dashletContainer = By.cssSelector("div.dashlet.my-tasks");
    private final By dropDownTasksList = By.cssSelector("div.my-tasks div.bd ul.first-of-type li a");
    private final By startWorkFlowLink = By.cssSelector("a[href$='start-workflow']");
    private final By activeTasksLink = By.cssSelector("div[class='toolbar flat-button'] a[href$='filter=workflows|active']");
    private final By completedTasksLink = By.cssSelector("div[class='toolbar flat-button'] a[href$='filter=workflows|completed']");
    private final By filterTaskButton = By.cssSelector("div.dashlet.my-tasks button[id$='default-filters-button']");
    private final By quantityOfTasks = By.cssSelector("span[id*=page-report]");
    private final By tasksNameList = By.cssSelector("div.dashlet.my-tasks [class=yui-dt-data] tr td[class*='title'] a");
    private final By emptyDashletMessage = By.cssSelector("div[class^='dashlet my-tasks'] .empty");
    private final By editIcon = By.cssSelector(".edit-task");
    private final By viewIcon = By.cssSelector(".view-task");
    private final By taskDetails = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private final By taskNames = By.cssSelector("td[class$='title'] a");
    private final By taskTypeAndStatus = By.cssSelector("div.yui-dt-liner div");
    private final By taskDueDate = By.cssSelector("div.yui-dt-liner h4 span");

    private final String taskRow = "//div[contains(@class,'dashlet my-tasks')]//a[text()='%s']/../../../..";

    public MyTasksDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle() {
        return getElementText(
            waitUntilElementIsVisible(dashletContainer)
                .findElement(dashletTitle));
    }

    @Override
    public MyTasksDashlet assertDashletTitleEquals(String expectedDashletTitle)
    {
        assertEquals(getDashletTitle(), expectedDashletTitle);
        return this;
    }

    private WebElement getTaskRow(String taskName)
    {
        return waitWithRetryAndReturnWebElement(
            By.xpath(String.format(taskRow, taskName)), WAIT_2.getValue(), RETRY_TIME_20.getValue());
    }

    public MyTasksDashlet assertStartWorkflowIsDisplayed()
    {
        log.info("Assert Start Workflow is displayed");
        assertTrue(isElementDisplayed(startWorkFlowLink), "Start workflow is displayed");
        return this;
    }

    public MyTasksDashlet assertEmptyDashletMessageIsCorrect()
    {
        log.info("Assert My Task dashlet has correct empty message");
        assertEquals(getElementText(emptyDashletMessage), language.translate("myTasksDashlet.empty"));
        return this;
    }

    public StartWorkflowPage clickStartWorkFlow()
    {
        log.info("Click Start Workflow");
        clickElement(startWorkFlowLink);
        return new StartWorkflowPage(webDriver);
    }

    public MyTasksDashlet assertActiveTasksButtonIsDisplayed()
    {
        log.info("Assert Active Tasks button is displayed");
        waitUntilElementIsVisible(activeTasksLink);
        assertTrue(isElementDisplayed(activeTasksLink), "Active Tasks button is displayed");
        return this;
    }

    public MyTasksPage clickActiveTasksLink()
    {
        log.info("Click Active Tasks link");
        waitUntilElementIsVisible(activeTasksLink);
        clickElement(activeTasksLink);
        return new MyTasksPage(webDriver);
    }

    public MyTasksDashlet assertCompletedTasksButtonIsDisplayed()
    {
        log.info("Assert Completed Tasks button is displayed");
        waitUntilElementIsVisible(completedTasksLink);
        assertTrue(isElementDisplayed(completedTasksLink), "Completed Tasks button is displayed");
        return this;
    }

    public MyTasksPage clickOnCompletedTasksLink()
    {
        clickElement(completedTasksLink);
        return new MyTasksPage(webDriver);
    }

    public MyTasksDashlet assertFilterTasksIsDisplayed()
    {
        log.info("Assert Filter Tasks is displayed");
        waitUntilElementIsVisible(filterTaskButton);
        assertTrue(isElementDisplayed(filterTaskButton), "Filter Tasks is displayed");
        return this;
    }

    public EditTaskPage clickTaskName(String taskName)
    {
        clickElement(getTaskRow(taskName).findElement(taskNames));
        return new EditTaskPage(webDriver);
    }

    public MyTasksDashlet assertTasksNavigationIs(int page, int numberOfTasks)
    {
        assertEquals(getElementText(quantityOfTasks),
            String.format(language.translate("myTasksDashlet.navigation"), page, numberOfTasks, numberOfTasks));
        return this;
    }

    public WebElement selectTask(String taskName)
    {
        return findFirstElementWithValue(tasksNameList, taskName);
    }

    public MyTasksDashlet assertTaskNameEqualsTo(String expectedTaskName)
    {
        log.info("Assert task name equals to {}", expectedTaskName);
        waitInSeconds(3);
        String taskName = getElementText(getTaskRow(expectedTaskName));
        String actualTaskName = taskName.substring(BEGIN_INDEX, taskName.indexOf(NEW_LINE));

        assertEquals(actualTaskName, expectedTaskName,
            String.format("Task name not equals to %s ", expectedTaskName));
        return this;
    }

    public WebElement selectTaskDetailsRow(final String taskName)
    {
        return findFirstElementWithValue(taskDetails, taskName);
    }

    public ViewTaskPage viewTask(String taskName)
    {
        log.info("Edit task {}", taskName);
        WebElement taskRowElement = getTaskRow(taskName);
        mouseOver(activeTasksLink);
        mouseOver(taskRowElement.findElement(taskNames));
        waitUntilElementHasAttribute(taskRowElement, "class", "highlighted");
        clickElement(taskRowElement.findElement(viewIcon));
        return new ViewTaskPage(webDriver);
    }
    public ViewTaskPage view_Task(String taskName)
    {
        log.info("Edit task {}", taskName);
        WebElement taskRowElement = getTaskRow(taskName);
        mouseOver(activeTasksLink);
        mouseOver(taskRowElement.findElement(taskNames));
        waitInSeconds(2);
        clickElement(taskRowElement.findElement(viewIcon));
        return new ViewTaskPage(webDriver);
    }

    public EditTaskPage editTask(String taskName)
    {
        log.info("Edit task {}", taskName);
        waitInSeconds(3);
        WebElement taskRowElement = getTaskRow(taskName);
        mouseOver(taskRowElement);

        WebElement editButton = waitUntilElementIsVisible(editIcon);
        waitInSeconds(3);
        clickElement(editButton);
        return new EditTaskPage(webDriver);
    }

    public MyTasksDashlet assertEditIsDisplayedForTask(String taskName)
    {
        log.info("Assert task {} has edit button", taskName);
        WebElement taskRowElement = getTaskRow(taskName);
        mouseOver(taskRowElement.findElement(taskNames));
        assertTrue(isElementDisplayed(taskRowElement.findElement(editIcon)), "Edit icon is displayed");
        return this;
    }

    public MyTasksDashlet assertViewIsDisplayedForTask(String taskName)
    {
        log.info("Assert task {} has view button", taskName);
        WebElement taskRowElement = getTaskRow(taskName);
        mouseOver(taskRowElement.findElement(taskNames));
        assertTrue(isElementDisplayed(taskRowElement.findElement(viewIcon)), "View icon is displayed");
        return this;
    }

    public MyTasksDashlet assertTaskIsDisplayed(String taskName)
    {
        log.info("Assert tasks {} is displayed", taskName);
        assertTrue(isElementDisplayed(getTaskRow(taskName)), String.format("Task %s is displayed", taskName));
        return this;
    }

    public MyTasksDashlet assertTaskIsNotStartedYet(String taskName)
    {
        log.info("Assert task {} is not started yet", taskName);
        assertEquals(getTaskRow(taskName).findElement(taskTypeAndStatus).getText(),
            language.translate("myTasksDashlet.notStartedYet"));
        return this;
    }

    public MyTasksDashlet assertDueDateIsCorrect(String taskName, Date expectedDate)
    {
        SimpleDateFormat dt = new SimpleDateFormat("dd MMMM, YYYY");
        String actualDate = getTaskRow(taskName).findElement(taskDueDate).getText();
        log.info("Assert task due date is: {}", expectedDate);
        assertEquals(actualDate, dt.format(expectedDate));
        return this;
    }

    public MyTasksDashlet assertTaskOptionEqualsTo(String expectedTaskOption)
    {
        log.info("Assert task option equals to {}", expectedTaskOption);
        String taskOption = getElementText(filterTaskButton);
        String actualTaskOption = taskOption.substring(BEGIN_INDEX, taskOption.indexOf(FILTER_ARROW));
        assertEquals(actualTaskOption, expectedTaskOption, "Incorrect filter selected");

        return this;
    }

    public MyTasksDashlet selectFilterTaskOption(String taskOption)
    {
        log.info("Select filter task option {}", taskOption);
        clickElement(filterTaskButton);
        selectOptionFromFilterOptionsList(taskOption,
            waitUntilElementsAreVisible(dropDownTasksList));

        return this;
    }

    public String getTaskTypeAndStatus(String taskName)
    {
        WebElement taskRowElement = selectTaskDetailsRow(taskName);
        return getElementText(taskRowElement.findElement(taskTypeAndStatus));
    }
}