package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.BasePages;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class WorkflowsIveStartedPage extends BasePages<WorkflowsIveStartedPage> implements AccessibleByMenuBar
{
    @Autowired
    WorkflowDetailsPage workflowDetailsPage;

    protected By workflowRowList = By.cssSelector("div[id$='_default-workflows'] tr[class*='yui-dt-rec']");
    protected By viewHistoryLink = By.cssSelector("div[class*='workflow-view-link'] a");
    protected By cancelWorkflowLink = By.cssSelector("div[class*='workflow-cancel-link'] a");
    protected By workflowTitle = By.cssSelector("td[class$='yui-dt-col-title'] div h3 a");
    protected By cancelWorkflowYesButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='Yes']");
    protected By cancelWorkflowNoButton = By.xpath("//div[text()='Cancel workflow']/..//button[text()='No']");
    protected By completedFilter = By.xpath("//a[text() = 'Completed']");
    protected By deleteWorkflowLink = By.cssSelector("div[class*='workflow-delete-link'] a");
    protected By deleteWorkflowYesButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='Yes']");
    protected By deleteWorkflowNoButton = By.xpath("//div[text()='Delete workflow']/..//button[text()='No']");
    @RenderWebElement
    private By startWorkflow = By.cssSelector("[id$='default-startWorkflow-button-button']");
    @RenderWebElement
    private By workflowBody = By.cssSelector(".alfresco-datatable.workflows");
    private By workflowsFilter = By.cssSelector("div[id*='_all-filter'] div h2");
    private By dueFilter = By.cssSelector("div[id*='_due-filter'] div h2");
    private By startedFilter = By.cssSelector("div[id*='_started-filter'] div h2");
    private By priorityFilter = By.cssSelector("div[id*='_priority-filter'] div h2");
    private By workflowTypeFilter = By.cssSelector("div[id*='_workflow-type-filter'] div h2");
    private By activeWorkflows = By.cssSelector("div[class*='workflow-list-bar'] div h2");

    public WorkflowsIveStartedPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public WorkflowsIveStartedPage navigateByMenuBar()
    {
        return (WorkflowsIveStartedPage) new Toolbar(browser).clickTasks().clickWorkflowsIStarted().renderedPage();
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/my-workflows";
    }

    public WorkflowsIveStartedPage assertWorkflowIStartedPageIsOpened()
    {
        LOG.info("Assert Workflow I've Started page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Workflow I've started page is opened");
        return this;
    }

    public WorkflowsIveStartedPage assertStartWorkflowIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(startWorkflow), "Start workflow button is displayed");
        return this;
    }

    public List<String> getActiveWorkflows()
    {
        List<String> allWorkflowsNames = new ArrayList<>();
        List<WebElement> workflows = getBrowser().findElements(By.cssSelector("div[id$='_default-workflows'] tr[class*='yui-dt-rec']"));
        for (WebElement specificTask : workflows)
        {
            String title = specificTask.findElement(By.cssSelector("h3 a")).getText();
            allWorkflowsNames.add(title);
        }
        return allWorkflowsNames;
    }

    public WebElement selectWorkflow(String workflowName)
    {
        return getBrowser().findFirstElementWithValue(workflowRowList, workflowName);
    }

    public WorkflowsIveStartedPage clickCancelWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        getBrowser().waitUntilElementVisible(cancelWorkflowLink);
        selectedTask.findElement(cancelWorkflowLink).click();
        if (areYouSure)
        {
            getBrowser().findElement(cancelWorkflowYesButton).click();
        } else getBrowser().findElement(cancelWorkflowNoButton).click();
        getBrowser().refresh();
        return (WorkflowsIveStartedPage) this.renderedPage();
    }

    public void clickCompletedFilter()
    {
        getBrowser().findElement(completedFilter).click();
        this.renderedPage();
        //TODO change with label from language file
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(activeWorkflows), "Completed Workflows");
    }

    public WorkflowsIveStartedPage clickDeleteWorkflow(String workflowName, boolean areYouSure)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        selectedTask.findElement(deleteWorkflowLink).click();
        if (areYouSure)
        {
            getBrowser().waitUntilElementVisible(deleteWorkflowYesButton).click();
        } else getBrowser().findElement(deleteWorkflowNoButton).click();
        getBrowser().refresh();
        return (WorkflowsIveStartedPage) this.renderedPage();
    }

    public WorkflowDetailsPage clickViewHistory(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        selectedTask.findElement(viewHistoryLink).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }

    public boolean isViewHistoryOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        return selectedTask.findElement(viewHistoryLink).isDisplayed();
    }

    public boolean isCancelWorkflowOptionDisplayed(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        return selectedTask.findElement(cancelWorkflowLink).isDisplayed();
    }

    public boolean isWorkflowsFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(workflowsFilter);
    }

    public boolean isDueFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(dueFilter);
    }

    public boolean isStartedFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(startedFilter);
    }

    public boolean isPriorityFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(priorityFilter);
    }

    public boolean isWorkflowTypeFilterDisplayed()
    {
        return getBrowser().isElementDisplayed(workflowTypeFilter);
    }

    public boolean isActiveWorkflowsBarDisplayed()
    {
        return getBrowser().isElementDisplayed(activeWorkflows);
    }

    public WorkflowDetailsPage clickOnWorkflowTitle(String workflowName)
    {
        WebElement selectedTask = selectWorkflow(workflowName);
        getBrowser().mouseOver(selectedTask);
        selectedTask.findElement(workflowTitle).click();
        return (WorkflowDetailsPage) workflowDetailsPage.renderedPage();
    }
}
