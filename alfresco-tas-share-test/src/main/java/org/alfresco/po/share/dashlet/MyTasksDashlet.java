package org.alfresco.po.share.dashlet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.po.share.tasksAndWorkflows.*;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class MyTasksDashlet extends Dashlet<MyTasksDashlet>
{
    public String finalApproveMessage = "The document was reviewed and approved.";

    @Autowired
    private EditTaskPage editTaskPage;

    //@Autowired
    private StartWorkflowPage startWorkflowPage;

   // @Autowired
    private MyTasksPage myTasksPage;

    @Autowired
    private ViewTaskPage viewTaskPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.my-tasks")
    private WebElement dashletContainer;

    @FindAll (@FindBy (css = "div.my-tasks div.bd ul.first-of-type li a"))
    private List<WebElement> dropDownTasksList;

    @FindBy (css = "a[href$='start-workflow']")
    private Link startWorkFlowLink;

    @FindBy (css = "div[class='toolbar flat-button'] a[href$='filter=workflows|active']")
    private WebElement activeTasksLink;

    @FindBy (css = "div[class='toolbar flat-button'] a[href$='filter=workflows|completed']")
    private Link completedTasksLink;

    @FindBy (css = "div.dashlet.my-tasks button[id$='default-filters-button']")
    private WebElement filterTaskButton;

    @FindBy (css = "span[id*=page-report]")
    private WebElement quantityOfTasks;

    @FindAll (@FindBy (css = "div.dashlet.my-tasks [class=yui-dt-data] tr"))
    private List<WebElement> taskRowList;

    @FindAll (@FindBy (css = "div.dashlet.my-tasks [class=yui-dt-data] tr td[class*='title'] a"))
    private List<WebElement> tasksNameList;

    @FindBy (css = "div[class^='dashlet my-tasks'] .empty")
    private WebElement emptyDashletMessage;

    private String taskRow = "//div[starts-with(@class,'dashlet my-tasks')]//a[text()='%s']/../../../..";
    private By editIcon = By.cssSelector(".edit-task");
    private By viewIcon = By.cssSelector(".view-task");
    private By taskDetails = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private By taskNames = By.cssSelector("td[class$='title'] a");
    private By taskTypeAndStatus = By.cssSelector("div.yui-dt-liner div");
    private By taskDueDate = By.cssSelector("div.yui-dt-liner h4 span");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    public WebElement getTaskRow(String taskName)
    {
        return browser.waitWithRetryAndReturnWebElement
            (By.xpath(String.format(taskRow, taskName)), 1, 10);
    }

    public MyTasksDashlet assertStartWorkflowIsDisplayed()
    {
        LOG.info("Assert Start Workflow is displayed");
        Assert.assertTrue(browser.isElementDisplayed(startWorkFlowLink), "Start workflow is displayed");
        return this;
    }

    public MyTasksDashlet assertEmptyDashletMessageIsCorrect()
    {
        Assert.assertEquals(emptyDashletMessage.getText(), language.translate("myTasksDashlet.empty"));
        return this;
    }

    /**
     * Click on Start Workflow link
     */
    public StartWorkflowPage clickStartWorkFlow()
    {
        startWorkFlowLink.click();
        return (StartWorkflowPage) startWorkflowPage.renderedPage();
    }

    public MyTasksDashlet assertActiveTasksButtonIsDisplayed()
    {
        LOG.info("Assert Active Tasks button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(activeTasksLink), "Active Tasks button is displayed");
        return this;
    }

    /**
     * Click on Active Tasks link
     */

    public MyTasksPage clickActiveTasksLink()
    {
        activeTasksLink.click();
        return (MyTasksPage) myTasksPage.renderedPage();
    }

    public MyTasksDashlet assertCompletedTasksButtonIsDisplayed()
    {
        LOG.info("Assert Completed Tasks button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(completedTasksLink), "Completed Tasks button is displayed");
        return this;
    }

    /**
     * Click on Completed Tasks link
     */

    public MyTasksPage clickOnCompletedTasksLink()
    {
        completedTasksLink.click();
        return (MyTasksPage) myTasksPage.renderedPage();
    }

    public MyTasksDashlet assertFilterTasksIsDisplayed()
    {
        LOG.info("Assert Filter Tasks is displayed");
        Assert.assertTrue(browser.isElementDisplayed(filterTaskButton), "Filter Tasks is displayed");
        return this;
    }

    /**
     * Click on task name from My Tasks Dashlet
     *
     * @param taskName String
     * @return editTaskPage
     */
    public EditTaskPage clickTaskName(String taskName)
    {
        getTaskRow(taskName).findElement(taskNames).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public MyTasksDashlet assertTasksNavigationIs(int page, int numberOfTasks)
    {
        Assert.assertEquals(quantityOfTasks.getText(),
            String.format(language.translate("myTasksDashlet.navigation"), page, numberOfTasks, numberOfTasks));
        return this;
    }

    /**
     * Retrieves the link that match the task name.
     *
     * @param taskName identifier
     * @return WebElement that matches taskName
     */
    public WebElement selectTask(final String taskName)
    {
        return browser.findFirstElementWithValue(tasksNameList, taskName);
    }

    /**
     * Method to check if a task name is displayed in My Task Dashlet
     *
     * @param taskName String
     * @return True if task exists
     */
    public boolean isTaskPresent(String taskName)
    {
        return browser.isElementDisplayed(selectTask(taskName));
    }

    /**
     * Retrieves task details
     *
     * @param taskName identifier
     * @return task details that matches taskName
     */
    public WebElement selectTaskDetailsRow(final String taskName)
    {
        return browser.findFirstElementWithValue(taskDetails, taskName);
    }

    /**
     * View task from My Task Dashlet.
     *
     * @param taskName String
     * @return HtmlPage
     */
    public ViewTaskPage viewTask(String taskName)
    {
        LOG.info(String.format("Edit task %s", taskName));
        WebElement taskRow = getTaskRow(taskName);
        browser.mouseOver(activeTasksLink);
        browser.mouseOver(taskRow.findElement(taskNames));
        browser.waitUntilElementHasAttribute(taskRow, "class", "highlighted");
        taskRow.findElement(viewIcon).click();
        return (ViewTaskPage) viewTaskPage.renderedPage();
    }

    /**
     * Edit task from My Task Dashlet.
     *
     * @param taskName String
     * @return HtmlPage
     */
    public EditTaskPage editTask(String taskName)
    {
        LOG.info(String.format("Edit task %s", taskName));
        WebElement taskRow = getTaskRow(taskName);
        browser.mouseOver(activeTasksLink);
        browser.mouseOver(taskRow.findElement(taskNames));
        browser.waitUntilElementHasAttribute(taskRow, "class", "highlighted");
        taskRow.findElement(editIcon).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    public MyTasksDashlet assertEditIsDisplayedForTask(String taskName)
    {
        LOG.info(String.format("Assert task %s has edit button", taskName));
        WebElement taskRow = getTaskRow(taskName);
        browser.mouseOver(taskRow.findElement(taskNames));
        Assert.assertTrue(browser.isElementDisplayed(taskRow.findElement(editIcon)), "Edit icon is displayed");
        return this;
    }

    public MyTasksDashlet assertViewIsDisplayedForTask(String taskName)
    {
        LOG.info(String.format("Assert task %s has view button", taskName));
        WebElement taskRow = getTaskRow(taskName);
        browser.mouseOver(taskRow.findElement(taskNames));
        Assert.assertTrue(browser.isElementDisplayed(taskRow.findElement(viewIcon)), "View icon is displayed");
        return this;
    }

    public MyTasksDashlet assertTaskIsDisplayed(String taskName)
    {
        LOG.info(String.format("Assert tasks %s is displayed", taskName));
        Assert.assertTrue(browser.isElementDisplayed(getTaskRow(taskName)), String.format("Task %s is displayed", taskName));
        return this;
    }

    public MyTasksDashlet assertTaskIsNotDisplayed(String taskName)
    {
        LOG.info(String.format("Assert tasks %s is displayed", taskName));
        Assert.assertFalse(browser.isElementDisplayed(By.xpath(String.format(taskRow, taskName))),
            String.format("Task %s is displayed", taskName));
        return this;
    }

    public MyTasksDashlet assertTaskIsNotStartedYet(String taskName)
    {
        LOG.info(String.format("Assert task %s is not started yet", taskName));
        Assert.assertEquals(getTaskRow(taskName).findElement(taskTypeAndStatus).getText(),
            language.translate("myTasksDashlet.notStartedYet"));
        return this;
    }

    public MyTasksDashlet assertDueDateIsCorrect(String taskName, Date expectedDate)
    {
        SimpleDateFormat dt = new SimpleDateFormat("dd MMMM, YYYY");
        String actualDate = getTaskRow(taskName).findElement(taskDueDate).getText();
        LOG.info(String.format("Assert task due date is: %s", expectedDate));
        Assert.assertEquals(actualDate, dt.format(expectedDate));
        return this;
    }

    /**
     * This method returns the due dates for each task list
     */
    public List<String> getTaskDueDateList()
    {
        List<String> taskTypeAndStatusList = new ArrayList<>();
        for (WebElement taskRow : taskRowList)
        {
            if (taskRow.findElements(taskDueDate).size() > 0)
            {
                taskTypeAndStatusList.add(taskRow.findElements(taskDueDate).get(0).getText());
            }
        }
        return taskTypeAndStatusList;
    }

    /**
     * Select an option from "Active Tasks" dropdown from My Tasks Dashlet.
     *
     * @param taskOption
     * @return HtmlPage
     */
    public MyTasksDashlet selectOptionFromTaskFilters(String taskOption)
    {
        try
        {
            filterTaskButton.click();
            browser.selectOptionFromFilterOptionsList(taskOption, dropDownTasksList);

            Assert.assertTrue(filterTaskButton.getText().contains(taskOption), "Incorrect filter selected");

            return (MyTasksDashlet) this.renderedPage();
        } catch (NoSuchElementException nse)
        {
            LOG.error("My Tasks option not present" + nse.getMessage());
            throw new PageOperationException(taskOption + " option not present.");
        }
    }

    /**
     * This method returns the task type and status for a taskNames
     */
    public String getTaskTypeAndStatus(String taskName)
    {
        WebElement taskRow = selectTaskDetailsRow(taskName);

        return taskRow.findElement(taskTypeAndStatus).getText();

    }
}