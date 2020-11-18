package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.dataprep.WorkflowService.WorkflowType;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    @Autowired
    SelectPopUpPage selectPopUpPage;

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

    private By workflowFormArea = By.cssSelector("div[id$='default-startWorkflowForm-alf-id0-form-fields']");
    private By itemsAreaRows = By.cssSelector("div[id$='packageItems-cntrl-currentValueDisplay'] .yui-dt-data>tr");
    private String attachedDocumentRow = "//a[text()='%s']/../../../..";

    public StartWorkflowPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/start-workflow", getCurrentSiteName());
    }

    public StartWorkflowPage assertStartWorkflowPageIsOpened()
    {
        assertTrue(getBrowser().isElementDisplayed(startWorkflowDropDown), "Start workflow dropdown is not displayed");
        return this;
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
        getBrowser().waitUntilElementVisible(workflowMenu);
        getBrowser().selectOptionFromFilterOptionsList(workflow, dropdownOptions);
        getBrowser().waitUntilElementContainsText(startWorkflowDropDown, workflow);
    }

    public StartWorkflowPage selectWorkflowType(WorkflowType workflowType)
    {
        String workflowTypeValue = getWorkflowTypeFilterValue(workflowType);
        LOG.info("Select workflow type {}", workflowTypeValue);
        getBrowser().mouseOver(startWorkflowDropDown);
        startWorkflowDropDown.click();
        getBrowser().waitUntilElementVisible(workflowMenu);
        getBrowser().selectOptionFromFilterOptionsList(workflowTypeValue, dropdownOptions);
        getBrowser().waitUntilElementContainsText(startWorkflowDropDown, workflowTypeValue);

        return this;
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("textarea[id*=workflowDescription]"));
        workflowDescriptionTextarea.click();
        workflowDescriptionTextarea.sendKeys(workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        try
        {

            if (!calendar.isDisplayed())
            {
                getBrowser().waitUntilElementClickable(datePickerIcon).click();
            }

            getBrowser().waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
            getBrowser().mouseOver(calendarToday);
            calendarToday.click();
            getBrowser().waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
        } catch (Exception e)
        {
            LOG.info("Couldn't choose current date from Date Picker.");
        }
    }

    public void clickDatePickerIcon()
    {
        getBrowser().waitUntilElementClickable(datePickerIcon).click();
        getBrowser().waitUntilWebElementIsDisplayedWithRetry(calendar);
    }

    public boolean isCalendarDisplayed()
    {
        return getBrowser().waitUntilElementVisible(calendar).isDisplayed();
    }

    public String getWorkflowDueDateInputValue()
    {
        return workflowDueDateInput.getAttribute("value");
    }

    public boolean isTodayDisplayed()
    {
        return getBrowser().isElementDisplayed(calendarToday);
    }

    public String getWorkflowDueDate()
    {
        return workflowDueDate.getAttribute("value");
    }

    public void selectWorkflowPriority(String priority)
    {
        getBrowser().selectOptionFromFilterOptionsList(priority, workflowPrioritiesList);
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public void clickStartWorkflow()
    {
        //workaround for "MNT-17015"

        getBrowser().waitUntilElementVisible(submitWorkflow);
        getBrowser().waitUntilElementClickable(submitWorkflow);

        getBrowser().clickJS(submitWorkflow);
        if (getBrowser().isElementDisplayed(submitWorkflow))
            getBrowser().clickJS(submitWorkflow);
        if (isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        dismissErrorPopup();
        if (getBrowser().isElementDisplayed(submitWorkflow))
            getBrowser().clickJS(submitWorkflow);
    }

    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (WebElement anItemsList : itemsList)
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public SelectPopUpPage clickOnSelectButtonSingleAssignee()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectAssigneeButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    public SelectPopUpPage clickOnSelectButtonMultipleAssignees()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectAssigneesButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    public SelectPopUpPage clickGroupSelectButton()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectGroupButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) selectPopUpPage.renderedPage();
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
        //workaround for "MNT-17015"
        getBrowser().clickJS(cancelStartWorkflow);
        if (getBrowser().isElementDisplayed(cancelStartWorkflow))
            cancelStartWorkflow.click();
        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
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
        return (UserDashboardPage) new UserDashboardPage(browser).renderedPage();
    }

    public SelectAssigneePopUp clickOnReassignButton()
    {
        getBrowser().waitUntilElementClickable(reassignButton).click();
        return (SelectAssigneePopUp) selectAssigneePopUp.renderedPage();
    }

    public boolean isWorkflowFormDisplayed()
    {
        return getBrowser().waitUntilElementVisible(workflowForm).isDisplayed();
    }

    public boolean isAlertPresent()
    {
        try
        {
            getBrowser().switchTo().alert();
            return true;
        } catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }

    private void dismissErrorPopup()
    {
        if (getBrowser().isElementDisplayed(By.xpath("//div[@id='prompt_h' and text()='Workflow could not be started']")))
            getBrowser().waitUntilElementVisible(By.cssSelector("div#prompt button")).click();
    }

    public boolean isStartWorkflowDropDownVisible()
    {
        return getBrowser().isElementDisplayed(startWorkflowDropDown);
    }

    public boolean isWorkflowMenuVisible()
    {
        return getBrowser().isElementDisplayed(workflowMenu);
    }

    public void clickStartWorkflowDropDown()
    {
        startWorkflowDropDown.click();
    }

    public boolean isWorkflowFormVisible()
    {
        return getBrowser().isElementDisplayed(workflowForm);
    }

    public void toggleSendEmailCheckBox(Boolean isChecked)
    {
        if (!sendEMailNotificationsCheckbox.getAttribute("value").equalsIgnoreCase(isChecked.toString()))
        {
            getBrowser().waitUntilElementClickable(sendEMailNotificationsCheckbox).click();
        }
    }

    public String getSendEmailCheckBoxValue()
    {
        return getBrowser().findElement(By.cssSelector("input[id*='sendEMailNotifications'][type='hidden']")).getAttribute("value");
    }

    public void clickRemoveAllItemsButton()
    {
        getBrowser().waitUntilElementClickable(removeeAllItemsButton).click();
        this.renderedPage();
    }

    public SelectDocumentPopupPage clickAddItemsButton()
    {
        addItemsButton.click();
        return (SelectDocumentPopupPage) selectDocumentPopupPage.renderedPage();
    }

    private WebElement getItemRow(String itemName)
    {
        return getBrowser().waitWithRetryAndReturnWebElement(By.xpath(String.format(attachedDocumentRow, itemName)),WAIT_1, WAIT_10);
    }

    public StartWorkflowPage assertItemsAreDisplayed(FileModel... files)
    {
        getBrowser().waitUntilElementVisible(itemsAreaRows);
        for(FileModel file : files)
        {
            LOG.info("Assert file {} is displayed in items", file.getName());
            assertTrue(getBrowser().isElementDisplayed(By.xpath(String.format(attachedDocumentRow, file.getName()))),
                String.format("File %s is not displayed", file.getName()));
        }
        return this;
    }

    private String getWorkflowTypeFilterValue(WorkflowType workflowType)
    {
        String filterValue = "";
        switch (workflowType)
        {
            case NewTask:
                filterValue = language.translate("startWorkflowPage.workflowType.newTask");
                break;
            case GroupReview:
                filterValue = language.translate("startWorkflowPage.workflowType.groupReview");
                break;
            case MultipleReviewers:
                filterValue = language.translate("startWorkflowPage.workflowType.multipleReviewers");
                break;
            case SingleReviewer:
                filterValue = language.translate("startWorkflowPage.workflowType.singleReviewer");
                break;
            case PooledReview:
                filterValue = language.translate("startWorkflowPage.workflowType.pooledReview");
                break;
            default:
                break;
        }
        return filterValue;
    }
}