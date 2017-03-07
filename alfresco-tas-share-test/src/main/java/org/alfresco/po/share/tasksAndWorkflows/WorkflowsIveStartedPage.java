package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarTasksMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import ru.yandex.qatools.htmlelements.element.Link;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class WorkflowsIveStartedPage extends SharePage<WorkflowsIveStartedPage> implements AccessibleByMenuBar
{
    @Autowired
    ToolbarTasksMenu toolbarTasksMenu;

    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    @RenderWebElement
    @FindBy(id = "HEADER_TITLE")
    private Link workflowsIveStartedTitle;

    @FindBy(css = "[id$='default-startWorkflow-button-button']")
    private WebElement startWorkflow;

    @FindBy(css = "div[id$='_default-workflows'] tr[class*='yui-dt-rec']")
    protected List<WebElement> workflowRowList;

    @FindBy(css = "div[id*='_all-filter'] div h2")
    private WebElement workflowsFilter;

    @FindBy(css = "div[id*='_due-filter'] div h2")
    private WebElement dueFilter;

    @FindBy(css = "div[id*='_started-filter'] div h2")
    private WebElement startedFilter;

    @FindBy(css = "div[id*='_priority-filter'] div h2")
    private WebElement priorityFilter;

    @FindBy(css = "div[id*='_workflow-type-filter'] div h2")
    private WebElement workflowTypeFilter;

    @FindBy(css = "div[class*='workflow-list-bar'] div h2")
    private WebElement activeWorkflows;


    protected By viewHistoryLink = By.cssSelector("div[class*='workflow-view-link'] a");
    protected By cancelWorkflowLink = By.cssSelector("div[class*='workflow-cancel-link'] a");
    protected By workflowTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    protected By cancelWorkflowYesButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='Yes']");
    protected By cancelWorkflowNoButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='No']");
    protected By completedFilter = By.xpath("//a[text() = 'Completed']");
    protected By deleteWorkflowLink = By.cssSelector("div[class*='workflow-delete-link'] a");
    protected By deleteWorkflowYesButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='Yes']");
    protected By deleteWorkflowNoButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='No']");

    @SuppressWarnings("unchecked")
    @Override
    public WorkflowsIveStartedPage navigateByMenuBar()
    {
        toolbarTasksMenu.clickWorkflowsIveStarted();
        return (WorkflowsIveStartedPage) renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-workflows#filter=workflows|active";
    }

    public boolean isStartWorkflowDisplayed()
    {
        return browser.isElementDisplayed(startWorkflow);
    }

    public List<String> getActiveWorkflows()
    {
        List<String> allWorkflowsNames = new ArrayList<String>();
        List<WebElement> workflows = browser.findElements(By.cssSelector("div[id$='_default-workflows'] tr[class*='yui-dt-rec']"));
        for (WebElement specificTask : workflows)
        {
            String title = specificTask.findElement(By.cssSelector("h3 a")).getText();
            allWorkflowsNames.add(title);
        }
        return allWorkflowsNames;
    }

    /**
     * Retrieves the link that match the task name.
     *
     * @param workflowName String
     * @return WebElement that match the task name
     */
    public WebElement selectWorkflow(String workflowName)
    {
        return browser.findFirstElementWithValue(workflowRowList, workflowName);
    }

    public void clickCancelWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(cancelWorkflowLink).click();
        if (areYouSure)
        {
            browser.findElement(cancelWorkflowYesButton).click();
        }
        else browser.findElement(cancelWorkflowNoButton).click();
    }

    public void clickCompletedFilter()
    {
        browser.findElement(completedFilter).click();
    }

    public void clickDeleteWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(deleteWorkflowLink).click();
        if (areYouSure)
        {
            browser.findElement(deleteWorkflowYesButton).click();
        }
        else browser.findElement(deleteWorkflowNoButton).click();
    }

    public WorkflowDetailsPage clickViewHistory(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(viewHistoryLink).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

    public boolean isViewHistoryOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        return selectedTask.findElement(viewHistoryLink).isDisplayed();
    }

    public boolean isCancelWorkflowOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        return selectedTask.findElement(cancelWorkflowLink).isDisplayed();
    }

    public boolean isWorkflowsFilterDisplayed()
    {
        return workflowsFilter.isDisplayed();
    }

    public boolean isDueFilterDisplayed()
    {
        return dueFilter.isDisplayed();
    }

    public boolean isStartedFilterDisplayed()
    {
        return startedFilter.isDisplayed();
    }

    public boolean isPriorityFilterDisplayed()
    {
        return priorityFilter.isDisplayed();
    }

    public boolean isWorkflowTypeFilterDisplayed()
    {
        return workflowTypeFilter.isDisplayed();
    }

    public boolean isActiveWorkflowsBarDisplayed()
    {
        return activeWorkflows.isDisplayed();
    }

    public WorkflowDetailsPage clickOnWorkflowTitle(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        browser.mouseOver(selectedTask);
        selectedTask.findElement(workflowTitle).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }
}
