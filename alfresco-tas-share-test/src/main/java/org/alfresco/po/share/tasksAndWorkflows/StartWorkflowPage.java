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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class StartWorkflowPage extends SiteCommon<StartWorkflowPage>
{
    @RenderWebElement
    private By startWorkflowDropDown = By.cssSelector("button[id*='default-workflow-definition']");
    private By workflowForm = By.cssSelector("div[class='form-fields']");
    private By selectedReviewerName = By.cssSelector("div[class*='itemtype']");
    private By workflowMenu = By.cssSelector("[id*=default-workflow-definition-menu] ul");
    private By dropdownOptions = By.cssSelector("[id*=default-workflow-definition-menu] li .title");
    private By workflowDescriptionTextarea = By.cssSelector("textarea[id*=workflowDescription]");
    private By workflowDueDate = By.cssSelector("input[id*='workflowDueDate-cntrl-date']");
    private By workflowDueDateInput = By.cssSelector("input[id$='workflowDueDate']");
    private By datePickerIcon = By.cssSelector(".datepicker-icon");
    private By chooseWorkflowDate = By.cssSelector("div[id*=workflowDueDate]");
    private By workflowPriority = By.cssSelector("[id*=workflowPriority]");
    private By workflowPrioritiesList = By.cssSelector("[id*=workflowPriority] option");
    private By calendarToday = By.cssSelector("td.today>a");
    private By calendar = By.cssSelector("div[id$='workflowDueDate-cntrl']");
    private By submitWorkflow = By.cssSelector("button[id*='form-submit']");
    private By itemsList = By.cssSelector(".form-field h3 a");
    private By selectAssigneeButton = By.cssSelector("div[id$='assignee-cntrl-itemGroupActions'] button");
    private By selectAssigneesButton = By.cssSelector("div[id$='assignees-cntrl-itemGroupActions'] button");
    private By selectGroupButton = By.cssSelector("div[id$='groupAssignee-cntrl-itemGroupActions'] button");
    private By cancelStartWorkflow = By.cssSelector("[id*=form-cancel-button]");
    private By taskStatusList = By.cssSelector("[id*=default_prop_bpm_status] option");
    private By reassignButton = By.cssSelector("[id*=default-reassign-button]");
    private By addItemsButton = By.xpath("//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Add']");
    private By removeeAllItemsButton = By.xpath("//div[contains(@id,'packageItems-cntrl-itemGroupActions')] //button[text()='Remove All']");
    private By sendEMailNotificationsCheckbox = By.cssSelector("input[id*='sendEMailNotifications'][type='checkbox']");
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
        return getElementText(selectedReviewerName);
    }

    public void selectAWorkflow(String workflow)
    {
        if (!isWorkflowMenuVisible())
        {
            getBrowser().waitUntilElementClickable(startWorkflowDropDown).click();
        }
        getBrowser().waitUntilElementVisible(workflowMenu);
        List<WebElement> options = getBrowser().findElements(dropdownOptions);
        getBrowser().selectOptionFromFilterOptionsList(workflow, options);
        getBrowser().waitUntilElementContainsText(startWorkflowDropDown, workflow);
    }

    public StartWorkflowPage selectWorkflowType(WorkflowType workflowType)
    {
        String workflowTypeValue = getWorkflowTypeFilterValue(workflowType);
        LOG.info("Select workflow type {}", workflowTypeValue);
        getBrowser().mouseOver(getBrowser().findElement(startWorkflowDropDown));
        getBrowser().findElement(startWorkflowDropDown).click();
        getBrowser().waitUntilElementVisible(workflowMenu);
        List<WebElement> options = getBrowser().findElements(dropdownOptions);
        getBrowser().selectOptionFromFilterOptionsList(workflowTypeValue, options);
        getBrowser().waitUntilElementContainsText(startWorkflowDropDown, workflowTypeValue);

        return this;
    }

    public void addWorkflowDescription(String workflowDescription)
    {
        WebElement textArea = getBrowser().waitUntilElementVisible(workflowDescriptionTextarea);
        textArea.click();
        textArea.sendKeys(workflowDescription);
    }

    public void selectCurrentDateFromDatePicker()
    {
        try
        {

            if (!getBrowser().isElementDisplayed(calendar))
            {
                getBrowser().waitUntilElementClickable(datePickerIcon).click();
            }

            getBrowser().waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: block");
            WebElement calendarElement = getBrowser().findElement(calendarToday);
            getBrowser().mouseOver(calendarElement);
            calendarElement.click();
            getBrowser().waitUntilElementHasAttribute(chooseWorkflowDate, "style", "display: none");
        }
        catch (Exception e)
        {
            LOG.info("Couldn't choose current date from Date Picker.");
        }
    }

    public void clickDatePickerIcon()
    {
        getBrowser().waitUntilElementClickable(datePickerIcon).click();
        getBrowser().waitUntilElementVisible(calendar);
    }

    public boolean isCalendarDisplayed()
    {
        return getBrowser().waitUntilElementVisible(calendar).isDisplayed();
    }

    public String getWorkflowDueDateInputValue()
    {
        return getBrowser().findElement(workflowDueDateInput).getAttribute("value");
    }

    public boolean isTodayDisplayed()
    {
        return getBrowser().isElementDisplayed(calendarToday);
    }

    public String getWorkflowDueDate()
    {
        return getBrowser().findElement(workflowDueDate).getAttribute("value");
    }

    public void selectWorkflowPriority(String priority)
    {
        List<WebElement> priorities = getBrowser().findElements(workflowPrioritiesList);
        getBrowser().selectOptionFromFilterOptionsList(priority, priorities);
        getBrowser().waitUntilElementHasAttribute(getBrowser().findElement(By.xpath("//*[contains(@id, 'workflowPriority')]//option[text()='" + priority + "']")), "selected", "true");
    }

    public void clickStartWorkflow()
    {
        //workaround for "MNT-17015"

        WebElement submit = getBrowser().waitUntilElementVisible(submitWorkflow);
        getBrowser().waitUntilElementClickable(submitWorkflow);

        getBrowser().clickJS(submit);
        if (getBrowser().isElementDisplayed(submitWorkflow))
            getBrowser().clickJS(submit);
        if (isAlertPresent())
        {
            getBrowser().handleModalDialogAcceptingAlert();
        }
        dismissErrorPopup();
        if (getBrowser().isElementDisplayed(submit))
            getBrowser().clickJS(submit);
    }

    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        for (WebElement anItemsList : getBrowser().findElements(itemsList))
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public SelectPopUpPage clickOnSelectButtonSingleAssignee()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectAssigneeButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) new SelectPopUpPage(browser).renderedPage();
    }

    public SelectPopUpPage clickOnSelectButtonMultipleAssignees()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectAssigneesButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) new SelectPopUpPage(browser).renderedPage();
    }

    public SelectPopUpPage clickGroupSelectButton()
    {
        WebElement selectElement = getBrowser().waitUntilElementVisible(selectGroupButton);
        getBrowser().waitUntilElementClickable(selectElement).click();
        return (SelectPopUpPage) new SelectPopUpPage(browser).renderedPage();
    }

    public DocumentLibraryPage cancelStartWorkflow()
    {
        //workaround for "MNT-17015"
        getBrowser().clickJS(getBrowser().findElement(cancelStartWorkflow));
        if (getBrowser().isElementDisplayed(cancelStartWorkflow))
        {
            getBrowser().findElement(cancelStartWorkflow).click();
        }

        return (DocumentLibraryPage) new DocumentLibraryPage(browser).renderedPage();
    }

    public void selectTaskStatus(String status)
    {
        for (WebElement taskStatus : getBrowser().findElements(taskStatusList))
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
        return (SelectAssigneePopUp) new SelectAssigneePopUp(browser).renderedPage();
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
        }
        catch (NoAlertPresentException noAlertPresentException)
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
        getBrowser().findElement(startWorkflowDropDown).click();
    }

    public boolean isWorkflowFormVisible()
    {
        return getBrowser().isElementDisplayed(workflowForm);
    }

    public void toggleSendEmailCheckBox(Boolean isChecked)
    {
        if (!getBrowser().findElement(sendEMailNotificationsCheckbox).getAttribute("value").equalsIgnoreCase(isChecked.toString()))
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
        getBrowser().findElement(addItemsButton).click();
        return (SelectDocumentPopupPage) new SelectDocumentPopupPage(browser).renderedPage();
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