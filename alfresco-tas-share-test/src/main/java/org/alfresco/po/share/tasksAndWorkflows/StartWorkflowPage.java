package org.alfresco.po.share.tasksAndWorkflows;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
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

public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    //todo: check better locator
    private final By startWorkflowDropDown = By.cssSelector("button[id*='default-workflow-definition']");
    private final By workflowForm = By.cssSelector("div[class='form-fields']");
    private final By selectedReviewerName = By.cssSelector("div[class*='itemtype']");
    private final By workflowMenu = By.cssSelector("[id*=default-workflow-definition-menu] ul");
    private final By dropdownOptions = By.cssSelector("[id*=default-workflow-definition-menu] li .title");
    private final By workflowDescriptionTextarea = By.cssSelector("textarea[id*=workflowDescription]");
    private final By workflowDueDate = By.cssSelector("input[id*='workflowDueDate-cntrl-date']");
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
        assertTrue(webElementInteraction.isElementDisplayed(startWorkflowDropDown), "Start workflow dropdown is not displayed");
        return this;
    }

    public String getSelectedReviewerName()
    {
        return webElementInteraction.getElementText(selectedReviewerName);
    }

    public void selectAWorkflow(String workflow)
    {
        if (!isWorkflowMenuVisible())
        {
            webElementInteraction.clickElement(startWorkflowDropDown);
        }
        webElementInteraction.waitUntilElementIsVisible(workflowMenu);
        List<WebElement> options = webElementInteraction.findElements(dropdownOptions);
        webElementInteraction.selectOptionFromFilterOptionsList(workflow, options);
        webElementInteraction.waitUntilElementContainsText(startWorkflowDropDown, workflow);
    }

    public StartWorkflowPage selectWorkflowType(WorkflowType workflowType)
    {
        LOG.info("Select workflow type {}", workflowType);
        String workflowTypeValue = getWorkflowTypeFilterValue(workflowType);
        webElementInteraction.mouseOver(webElementInteraction.findElement(startWorkflowDropDown));
        webElementInteraction.waitUntilElementIsVisible(startWorkflowDropDown);
        webElementInteraction.clickElement(startWorkflowDropDown);
        webElementInteraction.waitUntilElementIsVisible(workflowMenu);
        List<WebElement> options = webElementInteraction.findElements(dropdownOptions);
        webElementInteraction.selectOptionFromFilterOptionsList(workflowTypeValue, options);
        webElementInteraction.waitUntilElementContainsText(startWorkflowDropDown, workflowTypeValue);

        return this;
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        WebElement textArea = webElementInteraction.waitUntilElementIsVisible(workflowDescriptionTextarea);
        webElementInteraction.clickElement(textArea);
        webElementInteraction.clearAndType(textArea, workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        try
        {
            if (!webElementInteraction.isElementDisplayed(calendar))
            {
                webElementInteraction.clickElement(datePickerIcon);
            }

            webElementInteraction.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
            WebElement calendarElement = webElementInteraction.findElement(calendarToday);
            webElementInteraction.mouseOver(calendarElement);
            webElementInteraction.clickElement(calendarElement);
            webElementInteraction.waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
        }
        catch (Exception e)
        {
            LOG.info("Couldn't choose current date from Date Picker.");
        }
    }

    public void clickDatePickerIcon()
    {
        webElementInteraction.clickElement(datePickerIcon);
        webElementInteraction.waitUntilElementIsVisible(calendar);
    }

    public boolean isCalendarDisplayed()
    {
        return webElementInteraction.waitUntilElementIsVisible(calendar).isDisplayed();
    }

    public String getWorkflowDueDateInputValue()
    {
        return webElementInteraction.findElement(workflowDueDateInput).getAttribute("value");
    }

    public boolean isTodayDisplayed()
    {
        return webElementInteraction.isElementDisplayed(calendarToday);
    }

    public String getWorkflowDueDate()
    {
        return webElementInteraction.findElement(workflowDueDate).getAttribute("value");
    }

    public void selectWorkflowPriority(String priority)
    {
        List<WebElement> priorities = webElementInteraction.findElements(workflowPrioritiesList);
        webElementInteraction.selectOptionFromFilterOptionsList(priority, priorities);
        webElementInteraction.waitUntilElementHasAttribute(webElementInteraction.findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public void clickStartWorkflow()
    {
        //workaround for "MNT-17015"

        WebElement submit = webElementInteraction.waitUntilElementIsVisible(submitWorkflow);
        webElementInteraction.clickElement(submitWorkflow);

        webElementInteraction.clickJS(submit);
        if (webElementInteraction.isElementDisplayed(submitWorkflow))
            webElementInteraction.clickJS(submit);
        if (isAlertPresent())
        {
            webElementInteraction.acceptAlert();
        }
        dismissErrorPopup();
        if (webElementInteraction.isElementDisplayed(submit))
            webElementInteraction.clickJS(submit);
    }

    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (WebElement anItemsList : webElementInteraction.findElements(itemsList))
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public SelectPopUpPage clickOnSelectButtonSingleAssignee()
    {
        WebElement selectElement = webElementInteraction.waitUntilElementIsVisible(selectAssigneeButton);
        webElementInteraction.clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public SelectPopUpPage clickOnSelectButtonMultipleAssignees()
    {
        WebElement selectElement = webElementInteraction.waitUntilElementIsVisible(selectAssigneesButton);
        webElementInteraction.clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public SelectPopUpPage clickGroupSelectButton()
    {
        WebElement selectElement = webElementInteraction.waitUntilElementIsVisible(selectGroupButton);
        webElementInteraction.clickElement(selectElement);
        return new SelectPopUpPage(webDriver);
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
        //workaround for "MNT-17015"
        webElementInteraction.clickJS(webElementInteraction.findElement(cancelStartWorkflow));
        if (webElementInteraction.isElementDisplayed(cancelStartWorkflow))
        {
            webElementInteraction.clickElement(cancelStartWorkflow);
        }

        return new DocumentLibraryPage(webDriver);
    }

    public void selectTaskStatus(String status)
    {
        for (WebElement taskStatus : webElementInteraction.findElements(taskStatusList))
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
        webElementInteraction.clickElement(submitWorkflow);
        return new UserDashboardPage(webDriver);
    }

    public SelectAssigneePopUp clickOnReassignButton()
    {
        webElementInteraction.clickElement(reassignButton);
        return new SelectAssigneePopUp(webDriver);
    }

    public boolean isWorkflowFormDisplayed()
    {
        return webElementInteraction.waitUntilElementIsVisible(workflowForm).isDisplayed();
    }

    public boolean isAlertPresent()
    {
        try
        {
            webElementInteraction.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException noAlertPresentException)
        {
            return false;
        }
    }

    private void dismissErrorPopup()
    {
        if (webElementInteraction.isElementDisplayed(By.xpath("//div[@id='prompt_h' and text()='Workflow could not be started']")))
            webElementInteraction.waitUntilElementIsVisible(By.cssSelector("div#prompt button"));
    }

    public boolean isStartWorkflowDropDownVisible()
    {
        return webElementInteraction.isElementDisplayed(startWorkflowDropDown);
    }

    public boolean isWorkflowMenuVisible()
    {
        return webElementInteraction.isElementDisplayed(workflowMenu);
    }

    public void clickStartWorkflowDropDown()
    {
        webElementInteraction.clickElement(startWorkflowDropDown);
    }

    public boolean isWorkflowFormVisible()
    {
        return webElementInteraction.isElementDisplayed(workflowForm);
    }

    public void toggleSendEmailCheckBox(Boolean isChecked)
    {
        if (!webElementInteraction.findElement(sendEMailNotificationsCheckbox).getAttribute("value").equalsIgnoreCase(isChecked.toString()))
        {
            webElementInteraction.clickElement(sendEMailNotificationsCheckbox);
        }
    }

    public String getSendEmailCheckBoxValue()
    {
        return webElementInteraction.findElement(By.cssSelector("input[id*='sendEMailNotifications'][type='hidden']")).getAttribute("value");
    }

    public void clickRemoveAllItemsButton()
    {
        webElementInteraction.clickElement(removeAllItemsButton);
    }

    public SelectDocumentPopupPage clickAddItemsButton()
    {
        webElementInteraction.clickElement(addItemsButton);
        return new SelectDocumentPopupPage(webDriver);
    }

    public StartWorkflowPage assertItemsAreDisplayed(FileModel... files)
    {
        webElementInteraction.waitUntilElementsAreVisible(itemsAreaRows);
        for(FileModel file : files)
        {
            LOG.info("Assert file {} is displayed in items", file.getName());
            assertTrue(webElementInteraction
                    .isElementDisplayed(By.xpath(String.format(attachedDocumentRow, file.getName()))),
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