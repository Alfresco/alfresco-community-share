package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertTrue;
import static org.alfresco.common.Wait.WAIT_10;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.WorkflowService.WorkflowType;
import org.alfresco.po.share.site.DocumentLibraryPage;
import org.alfresco.po.share.site.SelectDocumentPopupPage;
import org.alfresco.po.share.site.SelectPopUpPage;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    private final By startWorkflowDropDown = By.cssSelector("button[id*='default-workflow-definition']");
    private final By workflowForm = By.cssSelector("div[class='form-fields']");
    private final By selectedReviewerName = By.cssSelector("div[class*='itemtype']");
    private final By workflowMenu = By.cssSelector("[id*=default-workflow-definition-menu] ul");
    private final By dropdownOptions = By.cssSelector("[id*=default-workflow-definition-menu] li .title");
    private final By workflowDescriptionTextarea = By.cssSelector("textarea[id*=workflowDescription]");
    private final By workflowDueDateInput = By.cssSelector("input[id$='workflowDueDate']");
    private final By datePickerIcon = By.cssSelector(".datepicker-icon");
    private final By chooseWorkflowDate = By.cssSelector("div[id*=workflowDueDate]");
    private final By workflowPrioritiesList = By.cssSelector("[id*=workflowPriority] option");
    private final By calendarToday = By.cssSelector("td.today>a");
    private final By calendar = By.cssSelector("div[id$='workflowDueDate-cntrl']");
    private final By submitWorkflow = By.cssSelector("button[id*='form-submit']");
    private final By itemsList = By.cssSelector(".form-field h3 a");
    private final By selectAssigneeButton = By.cssSelector("div[id$='assignee-cntrl-itemGroupActions'] button");
    private final By selectAssigneesButton = By.cssSelector("div[id$='assignees-cntrl-itemGroupActions'] button");
    private final By selectGroupButton = By.cssSelector("div[id$='groupAssignee-cntrl-itemGroupActions'] button");
    private final By cancelStartWorkflow = By.cssSelector("[id*=form-cancel-button]");
    private final By taskStatusList = By.cssSelector("[id*=default_prop_bpm_status] option");
    private final By reassignButton = By.cssSelector("[id*=default-reassign-button]");
    private final By addItemsButton = By.xpath("//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Add']");
    private final By removeAllItemsButton = By.xpath("//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Remove All']");
    private final By sendEMailNotificationsCheckbox = By.cssSelector("input[id*='sendEMailNotifications'][type='checkbox']");
    private final By itemsAreaRows = By.cssSelector("div[id$='packageItems-cntrl-currentValueDisplay'] .yui-dt-data>tr");
    private final String attachedDocumentRow = "//a[text()='%s']/../../../..";
    private final String startWorkflowUrl = "start-workflow";

    public StartWorkflowPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/start-workflow", getCurrentSiteName());
    }

    public StartWorkflowPage assertStartWorkflowPageIsOpened()
    {
        log.info("Assert Start Workflow page is opened");
        waitUrlContains("start-workflow", WAIT_10.getValue());
        assertTrue(getCurrentUrl().contains(startWorkflowUrl), "Start workflow page is not displayed");
        return this;
    }

    public String getSelectedReviewerName()
    {
        return getElementText(selectedReviewerName);
    }

    public void selectAWorkflow(String workflow)
    {
        if (!isWorkflowMenuVisible())
        {
            clickElement(startWorkflowDropDown);
        }
        waitUntilElementIsVisible(workflowMenu);
        List<WebElement> options = findElements(dropdownOptions);
        selectOptionFromFilterOptionsList(workflow, options);
        waitUntilElementContainsText(startWorkflowDropDown, workflow);
    }

    public StartWorkflowPage selectWorkflowType(WorkflowType workflowType)
    {
        log.info("Select workflow type {}", workflowType);
        String workflowTypeValue = getWorkflowTypeFilterValue(workflowType);
        mouseOver(findElement(startWorkflowDropDown));
        waitUntilElementIsVisible(startWorkflowDropDown);
        clickElement(startWorkflowDropDown);
        waitUntilElementIsVisible(workflowMenu);
        List<WebElement> options = findElements(dropdownOptions);
        selectOptionFromFilterOptionsList(workflowTypeValue, options);
        waitUntilElementContainsText(startWorkflowDropDown, workflowTypeValue);

        return this;
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        WebElement textArea = waitUntilElementIsVisible(workflowDescriptionTextarea);
        clickElement(textArea);
        clearAndType(textArea, workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        try
        {
            if (!isElementDisplayed(calendar))
            {
                clickElement(datePickerIcon);
            }

            waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
            WebElement calendarElement = findElement(calendarToday);
            mouseOver(calendarElement);
            clickElement(calendarElement);
            waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
        }
        catch (Exception e)
        {
            log.error("Couldn't choose current date from Date Picker.");
        }
    }

    public void clickDatePickerIcon()
    {
        clickElement(datePickerIcon);
        waitUntilElementIsVisible(calendar);
    }

    public boolean isCalendarDisplayed()
    {
        return waitUntilElementIsVisible(calendar).isDisplayed();
    }

    public String getWorkflowDueDateInputValue()
    {
        return findElement(workflowDueDateInput).getAttribute("value");
    }

    public void selectWorkflowPriority(String priority)
    {
        List<WebElement> priorities = findElements(workflowPrioritiesList);
        selectOptionFromFilterOptionsList(priority, priorities);
        waitUntilElementHasAttribute(findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public void clickStartWorkflow()
    {
        //workaround for "MNT-17015"

        WebElement submit = waitUntilElementIsVisible(submitWorkflow);
        clickElement(submitWorkflow);

        clickJS(submit);
        if (isElementDisplayed(submitWorkflow))
            clickJS(submit);
        if (isAlertPresent())
        {
            acceptAlert();
        }
        dismissErrorPopup();
        if (isElementDisplayed(submit))
            clickJS(submit);
    }

    public String getItemsList()
    {
        waitInSeconds(2);
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (WebElement anItemsList : findElements(itemsList))
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public SelectPopUpPage clickOnSelectButtonSingleAssignee()
    {
        WebElement selectElement = waitUntilElementIsVisible(selectAssigneeButton);
        clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public SelectPopUpPage clickOnSelectButtonMultipleAssignees()
    {
        WebElement selectElement = waitUntilElementIsVisible(selectAssigneesButton);
        clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public SelectPopUpPage clickGroupSelectButton()
    {
        WebElement selectElement = waitUntilElementIsVisible(selectGroupButton);
        clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
        //workaround for "MNT-17015"
        clickJS(findElement(cancelStartWorkflow));
        if (isElementDisplayed(cancelStartWorkflow))
        {
            clickElement(cancelStartWorkflow);
        }

        return new DocumentLibraryPage(webDriver);
    }

    public void selectTaskStatus(String status)
    {
        for (WebElement taskStatus : findElements(taskStatusList))
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
        clickElement(submitWorkflow);
        return new UserDashboardPage(webDriver);
    }

    public SelectAssigneePopUp clickOnReassignButton()
    {
        clickElement(reassignButton);
        return new SelectAssigneePopUp(webDriver);
    }

    public boolean isWorkflowFormDisplayed()
    {
        return waitUntilElementIsVisible(workflowForm).isDisplayed();
    }

    public boolean isAlertPresent()
    {
        try
        {
            switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }

    private void dismissErrorPopup()
    {
        if (isElementDisplayed(By.xpath("//div[@id='prompt_h' and text()='Workflow could not be started']")))
            waitUntilElementIsVisible(By.cssSelector("div#prompt button"));
    }

    public boolean isStartWorkflowDropDownVisible()
    {
        return isElementDisplayed(startWorkflowDropDown);
    }

    public boolean isWorkflowMenuVisible()
    {
        return isElementDisplayed(workflowMenu);
    }

    public void clickStartWorkflowDropDown()
    {
        clickElement(startWorkflowDropDown);
    }

    public boolean isWorkflowFormVisible()
    {
        waitUntilElementIsVisible(workflowForm);
        return isElementDisplayed(workflowForm);
    }

    public void toggleSendEmailCheckBox(Boolean isChecked)
    {
        if (!findElement(sendEMailNotificationsCheckbox).getAttribute("value").equalsIgnoreCase(isChecked.toString()))
        {
            clickElement(sendEMailNotificationsCheckbox);
        }
    }

    public String getSendEmailCheckBoxValue()
    {
        return findElement(By.cssSelector("input[id*='sendEMailNotifications'][type='hidden']")).getAttribute("value");
    }

    public void clickRemoveAllItemsButton()
    {
        clickElement(removeAllItemsButton);
    }

    public SelectDocumentPopupPage clickAddItemsButton()
    {
        clickElement(addItemsButton);
        return new SelectDocumentPopupPage(webDriver);
    }

    public StartWorkflowPage assertItemsAreDisplayed(FileModel... files)
    {
        waitUntilElementsAreVisible(itemsAreaRows);
        for(FileModel file : files)
        {
            log.info("Assert file {} is displayed in items", file.getName());
            assertTrue(isElementDisplayed(By.xpath(String.format(attachedDocumentRow, file.getName()))),
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