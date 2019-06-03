package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.tasksAndWorkflows.ActiveTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.CompletedTasksPage;
import org.alfresco.po.share.tasksAndWorkflows.EditTaskPage;
import org.alfresco.po.share.tasksAndWorkflows.StartWorkflowPage;
import org.alfresco.po.share.tasksAndWorkflows.ViewTaskPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
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
    public String finalRejectedMessage = "The document was reviewed and rejected.";
    @Autowired
    EditTaskPage editTaskPage;

    @Autowired
    StartWorkflowPage startWorkflowPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.my-tasks")
    private WebElement dashletContainer;

    @FindAll (@FindBy (css = "div.my-tasks div.bd ul.first-of-type li a"))
    private List<WebElement> dropDownTasksList;

    @FindBy (css = "a[href*='start-workflow']")
    private Link startWorkFlowLink;

    @FindBy (css = "a[href*='active']")
    private Link activeTasksLink;

    @FindBy (css = "a[href*='completed']")
    private Link completedTasksLink;

    @FindBy (css = "div.dashlet.my-tasks button[id$='default-filters-button']")
    private WebElement filterTaskButton;

    @FindBy (css = "[id*=page-report]")
    private WebElement quantatyOfTasks;

    @FindAll (@FindBy (css = "div.dashlet.my-tasks [class=yui-dt-data] tr"))
    private List<WebElement> taskRowList;

    @FindAll (@FindBy (css = "div.dashlet.my-tasks [class=yui-dt-data] tr td[class*='title'] a"))
    private List<WebElement> tasksNameList;

    private By editIcon = By.cssSelector(".edit-task");
    private By viewIcon = By.cssSelector(".view-task");
    private By taskDetails = By.cssSelector("div[id$='default-tasks'] tr[class*='yui-dt-rec']");
    private By taskNames = By.cssSelector("h3 a[href*='task-edit']");
    private By taskTypeAndStatus = By.cssSelector("div.my-tasks div.yui-dt-liner div");
    private By taskDueDate = By.cssSelector("div.my-tasks div.yui-dt-liner h4 span");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    /**
     * Method to verify that Start Workflow link is displayed
     */
    public boolean isStartWorkFlowLinkDisplayed()
    {
        return startWorkFlowLink.isDisplayed();
    }

    /**
     * Click on Start Workflow link
     */

    public StartWorkflowPage clickOnStartWorkFlowLink()
    {
        startWorkFlowLink.click();
        return (StartWorkflowPage) startWorkflowPage.renderedPage();
    }

    /**
     * Method to verify that Active Tasks link is displayed
     */
    public boolean isActiveTasksLinkDisplayed()
    {
        return activeTasksLink.isDisplayed();

    }

    /**
     * Click on Active Tasks link
     */

    public ActiveTasksPage clickOnActiveTasksLink()
    {
        activeTasksLink.click();
        return new ActiveTasksPage();
    }

    /**
     * Method to verify that Completed Tasks link is displayed
     */
    public boolean isCompletedTasksLinkDisplayed()
    {

        return completedTasksLink.isDisplayed();
    }

    /**
     * Click on Completed Tasks link
     */

    public CompletedTasksPage clickOnCompletedTasksLink()
    {
        completedTasksLink.click();
        return new CompletedTasksPage();
    }

    /**
     * Method to verify that filter tasks is displayed
     */
    public boolean isFilterTaskButtonDisplayed()
    {
        return filterTaskButton.isDisplayed();
    }

    /**
     * Click on task name from My Tasks Dashlet
     *
     * @param taskName String
     * @return editTaskPage
     */
    public EditTaskPage clickOnTaskNameLink(String taskName)
    {
        browser.findFirstElementWithValue(taskNames, taskName).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    /**
     * Get list of tasks displayed in my task dashlet.
     */
    public int getNoOfTasks()
    {
        return getTaskSections().size();
    }

    /**
     * Get quantity of tasks displayed in my task dashlet.
     */
    public String getTaskNavigation()
    {
        return quantatyOfTasks.getText();
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
     * Get the current selected option from filter
     *
     * @return
     */
    public String getSelectedOption()
    {
        String actualOption = filterTaskButton.getText();
        actualOption = actualOption.substring(0, actualOption.length() - 2);
        return actualOption;
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
     * Get list of entire task sections (that contains task details) displayed in my task dashlet.
     */
    public List<WebElement> getTaskSections()
    {
        return taskRowList;
    }

    /**
     * Retrieves the link that match the task name.
     *
     * @param taskName identifier
     * @return task link that matches taskName
     */
    public WebElement selectTaskNames(final String taskName)
    {
        return browser.findFirstElementWithValue(taskNames, taskName);
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
    public ViewTaskPage clickViewTask(String taskName)
    {
        Parameter.checkIsMandotary("Task name", taskName);
        WebElement taskRow = selectTaskDetailsRow(taskName);
        browser.mouseOver(taskRow);
        taskRow.findElement(viewIcon).click();
        return new ViewTaskPage();
    }

    /**
     * Edit task from My Task Dashlet.
     *
     * @param taskName String
     * @return HtmlPage
     */
    public EditTaskPage clickEditTask(String taskName)
    {
        Parameter.checkIsMandotary("Task name", taskName);
        WebElement taskRow = selectTaskDetailsRow(taskName);
        browser.mouseOver(taskRow);
        taskRow.findElement(editIcon).click();
        return (EditTaskPage) editTaskPage.renderedPage();
    }

    /**
     * Check that edit and view icons are displayed for each task
     */
    public void checkEditAndViewIconsForEachTask()
    {
        for (WebElement taskRow : taskRowList)
        {
            taskRow.findElement(viewIcon).isDisplayed();
            taskRow.findElement(editIcon).isDisplayed();
        }

    }

    /**
     * This method returns the task names list
     */
    public List<String> getTaskNamesList()
    {
        List<String> taskNameList = new ArrayList<>();
        for (WebElement taskRow : taskRowList)
        {
            taskNameList.add(taskRow.findElement(taskNames).getText());
        }
        return taskNameList;

    }

    /**
     * This method returns the task type and status list
     */
    public List<String> getTaskTypeAndStatusList()
    {
        List<String> taskTypeAndStatusList = new ArrayList<>();
        for (WebElement taskRow : taskRowList)
        {
            taskTypeAndStatusList.add(taskRow.findElement(taskTypeAndStatus).getText());
        }
        return taskTypeAndStatusList;

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