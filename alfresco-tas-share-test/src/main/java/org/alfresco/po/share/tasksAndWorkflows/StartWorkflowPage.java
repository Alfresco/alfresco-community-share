package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SelectPopUpPage selectPopUpPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SelectAssigneePopUp selectAssigneePopUp;

    @RenderWebElement
    @FindBy (css = "button[id*='default-workflow-definition']")
    private WebElement startWorkflowButton;

    @FindBy (css = "[id*=default-workflow-definition-menu] ul")
    private WebElement workflowMenu;

    @FindAll (@FindBy (css = "[id*=default-workflow-definition-menu] li .title"))
    private List<WebElement> dropdownOptions;

    @FindBy (css = "textarea[id*=workflowDescription]")
    private WebElement workflowDescriptionTextarea;

    @FindBy (css = "input[id*='workflowDueDate-cntrl-date']")
    private WebElement workflowDueDate;

    @FindBy (css = ".datepicker-icon")
    private WebElement datePickerIcon;

    @FindBy (css = "div[id*=workflowDueDate]")
    private WebElement chooseWorkflowDate;

    @FindBy (css = "[id*=workflowPriority]")
    private WebElement workflowPriority;

    @FindAll (@FindBy (css = "[id*=workflowPriority] option"))
    private List<WebElement> workflowPrioritiesList;

//    @FindBy(css = "[id*=assignee-cntrl-itemGroupActions] button")
//    private WebElement selectButton;

    @FindBy (css = "td.today>a")
    private WebElement calendarToday;

    @FindBy (css = "button[id*='form-submit']")
    private WebElement submitWorkflow;

    @FindBy (css = ".form-field h3 a")
    private List<WebElement> itemsList;
//
//    @FindBy(css = "[id*=assignees-cntrl-itemGroupActions] button")
//    private WebElement selectAssigneesButton;

    private By selectAssigneeButton = By.xpath("//div[@class='object-finder']//*[@class = 'show-picker']//button[text()='Select']");


    @FindBy (css = "[id*=form-cancel-button]")
    private WebElement cancelStartWorkflow;

    @FindAll (@FindBy (css = "[id*=default_prop_bpm_status] option"))
    private List<WebElement> taskStatusList;

    @FindBy (css = "[id*=default-reassign-button]")
    private WebElement reassignButton;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/start-workflow", getCurrentSiteName());
    }

    public void selectAWorkflow(String workflow)
    {
        startWorkflowButton.click();
        browser.waitUntilElementVisible(workflowMenu);
        browser.selectOptionFromFilterOptionsList(workflow, dropdownOptions);
        browser.waitUntilElementContainsText(startWorkflowButton, workflow);
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        browser.waitUntilElementVisible(By.cssSelector("textarea[id*=workflowDescription]"));
        workflowDescriptionTextarea.click();
        workflowDescriptionTextarea.sendKeys(workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        browser.waitUntilElementClickable(datePickerIcon).click();
        browser.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
        browser.mouseOver(calendarToday);
        calendarToday.click();
        browser.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
    }

    public void selectWorkflowPriority(String priority)
    {
        browser.selectOptionFromFilterOptionsList(priority, workflowPrioritiesList);
        browser.waitUntilElementHasAttribute(browser.findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public HtmlPage clickStartWorkflow(HtmlPage page)
    {
        //workaround for "MNT-17015"
        getBrowser().waitInSeconds(8);

        getBrowser().waitUntilElementVisible(submitWorkflow);
        getBrowser().waitUntilElementClickable(submitWorkflow);

        browser.clickJS(submitWorkflow);
        if (browser.isElementDisplayed(submitWorkflow))
            browser.clickJS(submitWorkflow);
        if (isAlertPresent())
        {
            browser.handleModalDialogAcceptingAlert();
        }
        dismissErrorPopup();
        if (browser.isElementDisplayed(submitWorkflow))
            browser.clickJS(submitWorkflow);
        return page.renderedPage();
    }

    /**
     * @return content items from Start Workflow form -> "Items" section
     */
    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (WebElement anItemsList : itemsList)
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public SelectPopUpPage clickOnSelectButton()
    {
        WebElement selectElement = browser.waitUntilElementVisible(selectAssigneeButton);
        browser.waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
        //workaround for "MNT-17015"
        browser.clickJS(cancelStartWorkflow);
        if (browser.isElementDisplayed(cancelStartWorkflow))
            cancelStartWorkflow.click();
        return (DocumentLibraryPage) documentLibraryPage.renderedPage();
    }

    public void selectTaskStatus(String status)
    {
        for (WebElement taskStatus : taskStatusList)
        {
            if (taskStatus.getAttribute("value").equals(status))
            {
                taskStatus.click();
                break;
            }
        }
    }

    public UserDashboardPage saveAndClose()
    {
        getBrowser().waitUntilElementClickable(submitWorkflow).click();
        return (UserDashboardPage) userDashboardPage.renderedPage();
    }

    public SelectAssigneePopUp clickOnReassignButton()
    {
        getBrowser().waitUntilElementClickable(reassignButton).click();
        return (SelectAssigneePopUp) selectAssigneePopUp.renderedPage();
    }

    public boolean isAlertPresent()
    {
        try
        {
            browser.switchTo().alert();
            return true;
        } catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }

    private void dismissErrorPopup()
    {
        if (browser.isElementDisplayed(By.xpath("//div[@id='prompt_h' and text()='Workflow could not be started']")))
            browser.waitUntilElementVisible(By.cssSelector("div#prompt button")).click();
    }
}