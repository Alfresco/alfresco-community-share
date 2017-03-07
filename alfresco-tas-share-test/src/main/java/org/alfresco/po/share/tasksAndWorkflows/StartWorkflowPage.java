package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertTrue;

@PageObject
public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    @Autowired
    DocumentLibraryPage documentLibraryPage;

    @Autowired
    SelectAssigneeToWorkflowPopUp selectAssigneeToWorkflowPopUp;

    @Autowired
    SelectAssigneesToWorkflowPopUp selectAssigneesToWorkflowPopUp;

    @Autowired
    SelectGroupAssigneeToWorkflowPopUp selectGroupAssigneeToWorkflowPopUp;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SelectAssigneePopUp selectAssigneePopUp;
    
    @FindBy(css = "button[id*='default-workflow-definition']")
    private WebElement startWorkflowButton;

    @FindBy(css = "[id*=default-workflow-definition-menu] ul")
    private WebElement workflowMenu;

    @FindAll(@FindBy(css = "[id*=default-workflow-definition-menu] li .title"))
    private List<WebElement> dropdownOptions;

    @FindBy(css = "textarea[id*=workflowDescription]")
    private WebElement workflowDescriptionTextarea;

    @FindBy(css = ".datepicker-icon")
    private WebElement datePickerIcon;

    @FindBy(css = "div[id*=workflowDueDate]")
    private WebElement chooseWorkflowDate;

    @FindBy(css = "[id*=workflowPriority]")
    private WebElement workflowPriority;

    @FindAll(@FindBy(css = "[id*=workflowPriority] option"))
    private List<WebElement> workflowPrioritiesList;

    @FindBy(css = "[id*=assignee-cntrl-itemGroupActions] button")
    private WebElement selectButton;

    @FindAll(@FindBy(css = "[id*=workflowDueDate-cntrl_cell] a"))
    private List<WebElement> calendarDates;

    @FindBy(css = "button[id*='form-submit']")
    private WebElement submitWorkflow;

    @FindBy(css = ".form-field h3 a")
    private List<WebElement> itemsList;

    @FindBy(css = "[id*=assignees-cntrl-itemGroupActions] button")
    private WebElement selectAssigneesButton;

    @FindBy(css = "[id*=groupAssignee-cntrl-itemGroupActions] button")
    private WebElement selectGroupAssigneeButton;

    @FindBy(css = "[id*=form-cancel-button]")
    private WebElement cancelStartWorkflow;

    @FindAll(@FindBy(css = "[id*=default_prop_bpm_status] option"))
    private List<WebElement> taskStatusList;

    @FindBy(css = "[id*=default-reassign-button]")
    private WebElement reassignButton;;
    
    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/start-workflow", getCurrentSiteName());
    }

    public boolean selectAWorkflow()
    {
        startWorkflowButton.click();
        return workflowMenu.isDisplayed();
    }

    public void selectWorkflowToStartFromDropdownList(String workflow)
    {
        browser.waitInSeconds(1);
        try
        {
            browser.selectOptionFromFilterOptionsList(workflow, dropdownOptions);
            browser.waitInSeconds(2);
            assertTrue(startWorkflowButton.getText().contains(workflow), "Incorrect workflow selected");
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("Workflow option not present" + nse.getMessage());
            throw new PageOperationException(workflow + " option not present.");
        }
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        browser.waitUntilElementsVisible(By.cssSelector("textarea[id*=workflowDescription]"));
        workflowDescriptionTextarea.click();
        workflowDescriptionTextarea.sendKeys(workflowDescription);
    }

    public boolean clickOnDatePickerIcon()
    {
        datePickerIcon.click();
        browser.waitInSeconds(1);
        return chooseWorkflowDate.isDisplayed();
    }

    public void selectWorkflowPriority(String priority)
    {
        try
        {
            browser.selectOptionFromFilterOptionsList(priority, workflowPrioritiesList);
            browser.waitInSeconds(1);
            assertTrue(workflowPriority.getText().contains(priority), "Incorrect priority selected");
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("Priority option not present" + nse.getMessage());
            throw new PageOperationException(priority + " option not present.");
        }
    }

    public SelectAssigneeToWorkflowPopUp clickOnSelectAssigneeButton()
    {
        selectButton.click();
        return (SelectAssigneeToWorkflowPopUp) selectAssigneeToWorkflowPopUp.renderedPage();
    }

    public void selectCurrentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("d");
        Date currentDate = new Date();

        String today = dateFormat.format(currentDate);
        for (WebElement date : calendarDates)
        {
            if (date.getText().equals(today))
            {
                browser.mouseOver(date);
                date.click();
            }
        }
        browser.waitInSeconds(1);
    }

    public void clickStartWorkflow()
    {
        submitWorkflow.click();
    }

    /**
     * @return content items from Start Workflow form -> "Items" section
     */
    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (int i = 0; i < itemsList.size(); i++)
        {
            itemsTextList.add(itemsList.get(i).getText());
        }
        return itemsTextList.toString();
    }

    public SelectAssigneesToWorkflowPopUp clickOnSelectAssigneesButton()
    {
        selectAssigneesButton.click();
        return (SelectAssigneesToWorkflowPopUp) selectAssigneesToWorkflowPopUp.renderedPage();
    }

    public SelectGroupAssigneeToWorkflowPopUp clickOnSelectGroupAssigneeButton()
    {
        selectGroupAssigneeButton.click();
        return (SelectGroupAssigneeToWorkflowPopUp) selectGroupAssigneeToWorkflowPopUp.renderedPage();
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
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
        submitWorkflow.click();
        return (UserDashboardPage) userDashboardPage.renderedPage();
    }

    public SelectAssigneePopUp clickOnReassignButton()
    {
        reassignButton.click();
        return (SelectAssigneePopUp) selectAssigneePopUp.renderedPage();
    }

    public boolean isAlertPresent()
    {
        try
        {
        browser.switchTo().alert();
        return true;
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
        return false;
        }
    }
}