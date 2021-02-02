package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WorkflowsIveStartedPage extends SharePage2<WorkflowsIveStartedPage> implements AccessibleByMenuBar
{
    private WorkflowDetailsPage workflowDetailsPage;

    private final By workflowRowList = By.cssSelector("div[id$='_default-workflows'] tr[class*='yui-dt-rec']");
    private final By viewHistoryLink = By.cssSelector("div[class*='workflow-view-link'] a");
    private final By cancelWorkflowLink = By.cssSelector("div[class*='workflow-cancel-link'] a");
    private final By workflowTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    private final By cancelWorkflowYesButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='Yes']");
    private final By cancelWorkflowNoButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='No']");
    private final By completedFilter = By.xpath("//a[text() = 'Completed']");
    private final By deleteWorkflowLink = By.cssSelector("div[class*='workflow-delete-link'] a");
    private final By deleteWorkflowYesButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='Yes']");
    private final By deleteWorkflowNoButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='No']");
    private final By startWorkflow = By.cssSelector("[id$='default-startWorkflow-button-button']");
    private final By workflowsFilter = By.cssSelector("div[id*='_all-filter'] div h2");
    private final By dueFilter = By.cssSelector("div[id*='_due-filter'] div h2");
    private final By startedFilter = By.cssSelector("div[id*='_started-filter'] div h2");
    private final By priorityFilter = By.cssSelector("div[id*='_priority-filter'] div h2");
    private final By workflowTypeFilter = By.cssSelector("div[id*='_workflow-type-filter'] div h2");
    private final By activeWorkflows = By.cssSelector("div[class*='workflow-list-bar'] div h2");

    public WorkflowsIveStartedPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public WorkflowsIveStartedPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickTasks().clickWorkflowsIStarted();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-workflows";
    }

    public WorkflowsIveStartedPage assertWorkflowIStartedPageIsOpened()
    {
        LOG.info("Assert Workflow I've Started page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Workflow I've started page is opened");
        return this;
    }

    public WorkflowsIveStartedPage assertStartWorkflowIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(startWorkflow), "Start workflow button is displayed");
        return this;
    }

    public List<String> getActiveWorkflows()
    {
        List<String> allWorkflowsNames = Collections.synchronizedList(new ArrayList<>());
        List<WebElement> workflows = webElementInteraction.findElements(By.cssSelector("div[id$='_default-workflows'] tr[class*='yui-dt-rec']"));
        for (WebElement specificTask : workflows)
        {
            String title = specificTask.findElement(By.cssSelector("h3 a")).getText();
            allWorkflowsNames.add(title);
        }
        return allWorkflowsNames;
    }

    public WebElement selectWorkflow(String workflowName)
    {
        return webElementInteraction.findFirstElementWithValue(workflowRowList, workflowName);
    }

    public WorkflowsIveStartedPage clickCancelWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        webElementInteraction.waitUntilElementIsVisible(cancelWorkflowLink);
        selectedTask.findElement(cancelWorkflowLink).click();
        if (areYouSure)
        {
            webElementInteraction.findElement(cancelWorkflowYesButton).click();
        }
        else {
            webElementInteraction.findElement(cancelWorkflowNoButton).click();
        }
        webElementInteraction.refresh();
        return this;
    }

    public void clickCompletedFilter()
    {
        webElementInteraction.findElement(completedFilter).click();
        //TODO change with label from language file
        webElementInteraction.waitUntilElementContainsText(webElementInteraction.findElement(activeWorkflows), "Completed Workflows");
    }

    public WorkflowsIveStartedPage clickDeleteWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        selectedTask.findElement(deleteWorkflowLink).click();
        if (areYouSure)
        {
            webElementInteraction.waitUntilElementIsVisible(deleteWorkflowYesButton).click();
        } else webElementInteraction.findElement(deleteWorkflowNoButton).click();
        webElementInteraction.refresh();
        return this;
    }

    public WorkflowDetailsPage clickViewHistory(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        selectedTask.findElement(viewHistoryLink).click();
        return new WorkflowDetailsPage(webDriver);
    }

    public boolean isViewHistoryOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        return selectedTask.findElement(viewHistoryLink).isDisplayed();
    }

    public boolean isCancelWorkflowOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        return selectedTask.findElement(cancelWorkflowLink).isDisplayed();
    }

    public boolean isWorkflowsFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(workflowsFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dueFilter);
    }

    public boolean isStartedFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(startedFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(priorityFilter);
    }

    public boolean isWorkflowTypeFilterDisplayed()
    {
        return webElementInteraction.isElementDisplayed(workflowTypeFilter);
    }

    public boolean isActiveWorkflowsBarDisplayed()
    {
        return webElementInteraction.isElementDisplayed(activeWorkflows);
    }

    public WorkflowDetailsPage clickOnWorkflowTitle(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        webElementInteraction.mouseOver(selectedTask);
        selectedTask.findElement(workflowTitle).click();
        return new WorkflowDetailsPage(webDriver);
    }
}
