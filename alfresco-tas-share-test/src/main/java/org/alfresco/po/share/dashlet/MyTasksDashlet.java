package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.MyTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MyTasksDashlet extends Dashlet<MyTasksDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.my-tasks");
    private final By dropDownTasksList = By.cssSelector("div.my-tasks div.bd ul.first-of-type li a");
    private final By startWorkFlowLink = By.cssSelector("a[href$='start-workflow']");
    private final By activeTasksLink = By.cssSelector("div[class='toolbar flat-button'] a[href$='filter=workflows|active']");
    private final By completedTasksLink = By.cssSelector("div[class='toolbar flat-button'] a[href$='filter=workflows|completed']");
    private final By filterTaskButton = By.cssSelector("div.dashlet.my-tasks button[id$='default-filters-button']");
    private final By quantityOfTasks = By.cssSelector("span[id*=page-report]");
    private final By taskRowList = By.cssSelector("div.dashlet.my-tasks [class=yui-dt-data] tr");
    private final By tasksNameList = By.cssSelector("div.dashlet.my-tasks [class=yui-dt-data] tr td[class*='title'] a");
    private final By emptyDashletMessage = By.cssSelector("div[class^='dashlet my-tasks'] .empty");
    private final By editIcon = By.cssSelector(".edit-task");
    private final By viewIcon = By.cssSelector(".view-task");
    private final By taskDetails = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private final By taskNames = By.cssSelector("td[class$='title'] a");
    private final By taskTypeAndStatus = By.cssSelector("div.yui-dt-liner div");
    private final By taskDueDate = By.cssSelector("div.yui-dt-liner h4 span");

    private String taskRow = "//div[starts-with(@class,'dashlet my-tasks')]//a[text()='%s']/../../../..";
    public String finalApproveMessage = "The document was reviewed and approved."; // TODO: add this label to translation

    public MyTasksDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    public WebElement getTaskRow(String taskName)
    {
        return webElementInteraction.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(taskRow, taskName)), 1, WAIT_10.getValue());
    }

    public MyTasksDashlet assertStartWorkflowIsDisplayed()
    {
        LOG.info("Assert Start Workflow is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(startWorkFlowLink), "Start workflow is displayed");
        return this;
    }

    public MyTasksDashlet assertEmptyDashletMessageIsCorrect()
    {
        LOG.info("Assert My Task dashlet has correct empty message");
        assertEquals(webElementInteraction.getElementText(emptyDashletMessage), language.translate("myTasksDashlet.empty"));
        return this;
    }

    public StartWorkflowPage clickStartWorkFlow()
    {
        LOG.info("Click Start Workflow");
        webElementInteraction.clickElement(startWorkFlowLink);
        return new StartWorkflowPage(webDriver);
    }

    public MyTasksDashlet assertActiveTasksButtonIsDisplayed()
    {
        LOG.info("Assert Active Tasks button is displayed");
        webElementInteraction.waitUntilElementIsVisible(activeTasksLink);
        assertTrue(webElementInteraction.isElementDisplayed(activeTasksLink), "Active Tasks button is displayed");
        return this;
    }

    public MyTasksPage clickActiveTasksLink()
    {
        webElementInteraction.clickElement(activeTasksLink);
        return new MyTasksPage(webDriver);
    }

    public MyTasksDashlet assertCompletedTasksButtonIsDisplayed()
    {
        LOG.info("Assert Completed Tasks button is displayed");
        webElementInteraction.waitUntilElementIsVisible(completedTasksLink);
        assertTrue(webElementInteraction.isElementDisplayed(completedTasksLink), "Completed Tasks button is displayed");
        return this;
    }

    public MyTasksPage clickOnCompletedTasksLink()
    {
        webElementInteraction.clickElement(completedTasksLink);
        return new MyTasksPage(webDriver);
    }

    public MyTasksDashlet assertFilterTasksIsDisplayed()
    {
        LOG.info("Assert Filter Tasks is displayed");
        webElementInteraction.waitUntilElementIsVisible(filterTaskButton);
        assertTrue(webElementInteraction.isElementDisplayed(filterTaskButton), "Filter Tasks is displayed");
        return this;
    }

    public EditTaskPage clickTaskName(String taskName)
    {
        getTaskRow(taskName).findElement(taskNames).click();
        return new EditTaskPage(webDriver);
    }

    public MyTasksDashlet assertTasksNavigationIs(int page, int numberOfTasks)
    {
        assertEquals(webElementInteraction.getElementText(quantityOfTasks),
            String.format(language.translate("myTasksDashlet.navigation"), page, numberOfTasks, numberOfTasks));
        return this;
    }

    public WebElement selectTask(final String taskName)
    {
        return webElementInteraction.findFirstElementWithValue(tasksNameList, taskName);
    }

    public boolean isTaskPresent(String taskName)
    {
        return webElementInteraction.isElementDisplayed(selectTask(taskName));
    }

    public WebElement selectTaskDetailsRow(final String taskName)
    {
        return webElementInteraction.findFirstElementWithValue(taskDetails, taskName);
    }

    public ViewTaskPage viewTask(String taskName)
    {
        LOG.info(String.format("Edit task %s", taskName));
        WebElement taskRow = getTaskRow(taskName);
        webElementInteraction.mouseOver(activeTasksLink);
        webElementInteraction.mouseOver(taskRow.findElement(taskNames));
        webElementInteraction.waitUntilElementHasAttribute(taskRow, "class", "highlighted");
        taskRow.findElement(viewIcon).click();
        return new ViewTaskPage(webDriver);
    }

    public EditTaskPage editTask(String taskName)
    {
        LOG.info(String.format("Edit task %s", taskName));
        WebElement taskRow = getTaskRow(taskName);
        webElementInteraction.mouseOver(activeTasksLink);
        webElementInteraction.mouseOver(taskRow.findElement(taskNames));
        webElementInteraction.waitUntilElementHasAttribute(taskRow, "class", "highlighted");
        taskRow.findElement(editIcon).click();
        return new EditTaskPage(webDriver);
    }

    public MyTasksDashlet assertEditIsDisplayedForTask(String taskName)
    {
        LOG.info(String.format("Assert task %s has edit button", taskName));
        WebElement taskRow = getTaskRow(taskName);
        webElementInteraction.mouseOver(taskRow.findElement(taskNames));
        assertTrue(webElementInteraction.isElementDisplayed(taskRow.findElement(editIcon)), "Edit icon is displayed");
        return this;
    }

    public MyTasksDashlet assertViewIsDisplayedForTask(String taskName)
    {
        LOG.info(String.format("Assert task %s has view button", taskName));
        WebElement taskRow = getTaskRow(taskName);
        webElementInteraction.mouseOver(taskRow.findElement(taskNames));
        assertTrue(webElementInteraction.isElementDisplayed(taskRow.findElement(viewIcon)), "View icon is displayed");
        return this;
    }

    public MyTasksDashlet assertTaskIsDisplayed(String taskName)
    {
        LOG.info(String.format("Assert tasks %s is displayed", taskName));
        assertTrue(webElementInteraction.isElementDisplayed(getTaskRow(taskName)), String.format("Task %s is displayed", taskName));
        return this;
    }

    public MyTasksDashlet assertTaskIsNotDisplayed(String taskName)
    {
        LOG.info(String.format("Assert tasks %s is displayed", taskName));
        Assert.assertFalse(webElementInteraction.isElementDisplayed(By.xpath(String.format(taskRow, taskName))),
            String.format("Task %s is displayed", taskName));
        return this;
    }

    public MyTasksDashlet assertTaskIsNotStartedYet(String taskName)
    {
        LOG.info(String.format("Assert task %s is not started yet", taskName));
        assertEquals(getTaskRow(taskName).findElement(taskTypeAndStatus).getText(),
            language.translate("myTasksDashlet.notStartedYet"));
        return this;
    }

    public MyTasksDashlet assertDueDateIsCorrect(String taskName, Date expectedDate)
    {
        SimpleDateFormat dt = new SimpleDateFormat("dd MMMM, YYYY");
        String actualDate = getTaskRow(taskName).findElement(taskDueDate).getText();
        LOG.info(String.format("Assert task due date is: %s", expectedDate));
        assertEquals(actualDate, dt.format(expectedDate));
        return this;
    }

    public List<String> getTaskDueDateList()
    {
        List<String> taskTypeAndStatusList = new ArrayList<>();
        List<WebElement> taskList = webElementInteraction.waitUntilElementsAreVisible(taskRowList);
        for (WebElement taskRow : taskList)
        {
            if (taskRow.findElements(taskDueDate).size() > 0)
            {
                taskTypeAndStatusList.add(taskRow.findElements(taskDueDate).get(0).getText());
            }
        }
        return taskTypeAndStatusList;
    }

    public MyTasksDashlet selectOptionFromTaskFilters(String taskOption)
    {
        webElementInteraction.clickElement(filterTaskButton);
        webElementInteraction.selectOptionFromFilterOptionsList(taskOption,
            webElementInteraction.waitUntilElementsAreVisible(dropDownTasksList));
        assertTrue(webElementInteraction.getElementText(filterTaskButton).contains(taskOption), "Incorrect filter selected");

        return this;
    }

    public String getTaskTypeAndStatus(String taskName)
    {
        WebElement taskRow = selectTaskDetailsRow(taskName);
        return webElementInteraction.getElementText(taskRow.findElement(taskTypeAndStatus));
    }
}