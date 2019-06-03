package org.alfresco.po.share.tasksAndWorkflows;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
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

    @Autowired
    SelectDocumentPopupPage selectDocumentPopupPage;

    @RenderWebElement
    @FindBy (css = "button[id*='default-workflow-definition']")
    private WebElement startWorkflowDropDown;

    @FindBy (css = "div[class='form-fields']")
    private WebElement workflowForm;

    @FindBy (css = "div[class*='itemtype']")
    private WebElement selectedReviewerName;

    @FindBy (css = "[id*=default-workflow-definition-menu] ul")
    private WebElement workflowMenu;

    @FindAll (@FindBy (css = "[id*=default-workflow-definition-menu] li .title"))
    private List<WebElement> dropdownOptions;

    @FindBy (css = "textarea[id*=workflowDescription]")
    private WebElement workflowDescriptionTextarea;

    @FindBy (css = "input[id*='workflowDueDate-cntrl-date']")
    private WebElement workflowDueDate;

    @FindBy (css = "input[id$='workflowDueDate']")
    private WebElement workflowDueDateInput;

    @FindBy (css = ".datepicker-icon")
    private WebElement datePickerIcon;

    @FindBy (css = "div[id*=workflowDueDate]")
    private WebElement chooseWorkflowDate;

    @FindBy (css = "[id*=workflowPriority]")
    private WebElement workflowPriority;

    @FindAll (@FindBy (css = "[id*=workflowPriority] option"))
    private List<WebElement> workflowPrioritiesList;


    @FindBy (css = "td.today>a")
    private WebElement calendarToday;

    @FindBy (css = "div[id$='workflowDueDate-cntrl']")
    private WebElement calendar;

    @FindBy (css = "button[id*='form-submit']")
    private WebElement submitWorkflow;

    @FindBy (css = ".form-field h3 a")
    private List<WebElement> itemsList;

    @FindBy (css = "div[id$='assignee-cntrl-itemGroupActions'] button")
    private WebElement selectAssigneeButton;

    @FindBy (css = "div[id$='assignees-cntrl-itemGroupActions'] button")
    private WebElement selectAssigneesButton;

    @FindBy (css = "div[id$='groupAssignee-cntrl-itemGroupActions'] button")
    private WebElement selectGroupButton;

    @FindBy (css = "[id*=form-cancel-button]")
    private WebElement cancelStartWorkflow;

    @FindAll (@FindBy (css = "[id*=default_prop_bpm_status] option"))
    private List<WebElement> taskStatusList;

    @FindBy (css = "[id*=default-reassign-button]")
    private WebElement reassignButton;

    @FindBy (xpath = "//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Add']")
    private WebElement addItemsButton;

    @FindBy (xpath = "//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Remove All']")
    private WebElement removeeAllItemsButton;

    @FindBy (css = "input[id*='sendEMailNotifications'][type='checkbox']")
    private WebElement sendEMailNotificationsCheckbox;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/start-workflow", getCurrentSiteName());
    }

    public String getSelectedReviewerName()
    {
        return selectedReviewerName.getText();
    }

    public void selectAWorkflow(String workflow)
    {
        if (!isWorkflowMenuVisible())
        {
            startWorkflowDropDown.click();
        }
        browser.waitUntilElementVisible(workflowMenu);
        browser.selectOptionFromFilterOptionsList(workflow, dropdownOptions);
        browser.waitUntilElementContainsText(startWorkflowDropDown, workflow);
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        browser.waitUntilElementVisible(By.cssSelector("textarea[id*=workflowDescription]"));
        workflowDescriptionTextarea.click();
        workflowDescriptionTextarea.sendKeys(workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        try
        {

            if (!calendar.isDisplayed())
            {
                browser.waitUntilElementClickable(datePickerIcon).click();
            }

            browser.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
            browser.mouseOver(calendarToday);
            calendarToday.click();
            browser.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
        } catch (Exception e)
        {
            LOG.info("Couldn't choose current date from Date Picker.");
        }
    }

    public void clickDatePickerIcon()
    {
        browser.waitUntilElementClickable(datePickerIcon).click();
        browser.waitUntilWebElementIsDisplayedWithRetry(calendar);
    }

    public boolean isCalendarDisplayed()
    {
        return browser.waitUntilElementVisible(calendar).isDisplayed();
    }

    public String getWorkflowDueDateInputValue()
    {
        return workflowDueDateInput.getAttribute("value");
    }

    public boolean isTodayDisplayed()
    {
        return browser.isElementDisplayed(calendarToday);
    }

    public String getWorkflowDueDate()
    {
        return workflowDueDate.getAttribute("value");
    }

    public void selectWorkflowPriority(String priority)
    {
        browser.selectOptionFromFilterOptionsList(priority, workflowPrioritiesList);
        browser.waitUntilElementHasAttribute(browser.findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public HtmlPage clickStartWorkflow(HtmlPage page)
    {
        //workaround for "MNT-17015"

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

    /**
     * Method that is clicking on Select Assignee button.
     * Method used on workflow that requires MAX one assigned user as: New Task and Review And Approve (single reviewer).
     *
     * @return the popup from where user will be selected.
     */
    public SelectPopUpPage clickOnSelectButtonSingleAssignee()
    {
        WebElement selectElement = browser.waitUntilElementVisible(selectAssigneeButton);
        browser.waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    /**
     * Method that is clicking on Select Assignees button.
     * Method used on workflow that requires MIN one assigned user as: Review And Approve (one or more reviewers).
     *
     * @return the popup from where users will be selected.
     */
    public SelectPopUpPage clickOnSelectButtonMultipleAssignees()
    {
        WebElement selectElement = browser.waitUntilElementVisible(selectAssigneesButton);
        browser.waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    /**
     * Method that is clicking on Select Assignee Group button.
     * Method used on workflow that requires an assigned group as: Review And Approve (group review), Review and Approve (pooled review).
     *
     * @return the popup from where groups will be selected.
     */
    public SelectPopUpPage clickGroupSelectButton()
    {
        WebElement selectElement = browser.waitUntilElementVisible(selectGroupButton);
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

    public boolean isWorkflowFormDisplayed()
    {
        return browser.waitUntilElementVisible(workflowForm).isDisplayed();
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

    public boolean isStartWorkflowDropDownVisible()
    {
        return browser.isElementDisplayed(startWorkflowDropDown);
    }

    public boolean isWorkflowMenuVisible()
    {
        return browser.isElementDisplayed(workflowMenu);
    }

    public void clickStartWorkflowDropDown()
    {
        startWorkflowDropDown.click();
    }

    public boolean isWorkflowFormVisible()
    {
        return browser.isElementDisplayed(workflowForm);
    }

    public void toggleSendEmailCheckBox(Boolean isChecked)
    {
        if (!sendEMailNotificationsCheckbox.getAttribute("value").equalsIgnoreCase(isChecked.toString()))
        {
            browser.waitUntilElementClickable(sendEMailNotificationsCheckbox).click();
        }
    }

    public String getSendEmailCheckBoxValue()
    {
        return browser.findElement(By.cssSelector("input[id*='sendEMailNotifications'][type='hidden']")).getAttribute("value");
    }

    public void clickRemoveAllItemsButton()
    {
        browser.waitUntilElementClickable(removeeAllItemsButton).click();
        this.renderedPage();
    }

    public SelectDocumentPopupPage clickAddItemsButton()
    {
        addItemsButton.click();
        return (SelectDocumentPopupPage) selectDocumentPopupPage.renderedPage();
    }

}