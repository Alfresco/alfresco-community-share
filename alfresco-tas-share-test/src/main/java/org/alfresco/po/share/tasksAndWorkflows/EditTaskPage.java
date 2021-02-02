package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class EditTaskPage extends SharePage2<EditTaskPage>
{
    private final By taskEditHeader = By.cssSelector( "div.task-edit-header h1");
    private final By commentTextArea = By.cssSelector("textarea[id$='bpm_comment']");
    private final By approveButton = By.cssSelector("button[id$='approve-button']");
    private final By rejectButton = By.cssSelector("button[id$='reject-button']");
    private final By message = By.cssSelector("div.form-field div.viewmode-field span[data-datatype$='text']");
    private final By owner = By.cssSelector("div.form-field div.viewmode-field span[id$='_prop_taskOwner'] a");
    private final By identifier = By.xpath("//span[@class = 'viewmode-label' and text() = 'Identifier:']");
    private final By priority = By.xpath("//span[contains(@class,'viewmode-value') and not(@data-datatype) and not(@id)]");
    private final By dueDate = By.xpath("//span[@class = 'viewmode-label' and text() = 'Due:']");
    private final By claimButton = By.cssSelector("button[id*='claim']");
    private final By releaseToPoolButton = By.cssSelector("button[id*='release-button']");
    private final By taskDoneButton = By.cssSelector("button[id$='Next-button']");
    private final By itemsList = By.cssSelector(".form-field h3 a");
    private final By statusDropdown = By.cssSelector("select[title = 'Status']");
    private final By saveButton = By.cssSelector("button[id$='form-submit-button']");
    private final By reassignButton = By.cssSelector("button[id$='reassign-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By addItemsButton = By.cssSelector("div[id$='itemGroupActions'] button");

    private String outcomeApprove;
    private String outcomeReject;

    public EditTaskPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/task-edit";
    }

    public EditTaskPage assertEditTaskPageIsOpened()
    {
        LOG.info("Assert Edit Task page is opened");
        webElementInteraction.waitUntilElementIsVisible(taskEditHeader);
        assertTrue(webElementInteraction.isElementDisplayed(taskEditHeader), "Edit task header is displayed");
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Edit Task page is opened");
        return this;
    }

    public void approve(String comment)
    {
        this.outcomeApprove = "Approved";
        webElementInteraction.clearAndType(commentTextArea, comment);
        webElementInteraction.clickElement(approveButton);
    }

    public void reject(String comment)
    {
        webElementInteraction.waitUntilElementIsVisible(commentTextArea);
        webElementInteraction.clearAndType(commentTextArea, comment);
        webElementInteraction.waitUntilElementIsVisible(rejectButton);
        clickRejectButton();
    }

    public void clickRejectButton()
    {
        webElementInteraction.clickElement(rejectButton);
    }

    public String getOutcomeApproveText()
    {
        return outcomeApprove;
    }

    public String getOutcomeRejectText()
    {
        return outcomeReject;
    }

    public String getEditTaskHeader()
    {
        return webElementInteraction.getElementText(taskEditHeader);
    }

    public String getComment()
    {
        return webElementInteraction.getElementText(commentTextArea);
    }

    public String getMessage()
    {
        return webElementInteraction.getElementText(message);
    }

    public String getOwner()
    {
        return webElementInteraction.getElementText(owner);
    }

    public String getPriority()
    {
        return webElementInteraction.getElementText(priority);
    }

    public boolean isIdentifierPresent()
    {
        return webElementInteraction.isElementDisplayed(identifier);
    }

    public boolean isDueDatePresent()
    {
        return webElementInteraction.isElementDisplayed(dueDate);
    }

    public boolean isSaveButtonPresent()
    {
        return webElementInteraction.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonPresent()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public boolean isTaskDoneButtonPresent()
    {
        return webElementInteraction.isElementDisplayed(taskDoneButton);
    }

    public boolean isReassignButtonPresent()
    {
        return webElementInteraction.isElementDisplayed(reassignButton);
    }

    public boolean isAddItemsButtonPresent()
    {
        return webElementInteraction.isElementDisplayed(addItemsButton);
    }

    public void selectStatus(TaskStatus status)
    {
        Select select = new Select(webElementInteraction.findElement(statusDropdown));
        webElementInteraction.waitUntilElementIsVisible(statusDropdown);
        select.selectByValue(status.getStatus());
    }

    public void writeComment(String comment)
    {
        webElementInteraction.clearAndType(commentTextArea, comment);
    }

    public void clickOnSaveButton()
    {
        webElementInteraction.clickElement(saveButton);
    }

    public boolean isStatusOptionPresent(TaskStatus status)
    {
        Select select = new Select(webElementInteraction.findElement(statusDropdown));
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()))
                return true;
        return false;
    }

    public boolean isStatusOptionSelected(TaskStatus status)
    {
        Select select = new Select(webElementInteraction.findElement(statusDropdown));
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()) && value.isSelected())
                return true;
        return false;
    }

    public EditTaskPage clickClaimButton()
    {
        webElementInteraction.waitUntilElementIsVisible(claimButton).click();
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public EditTaskPage clickReleaseToPoolButton()
    {
        webElementInteraction.clickElement(releaseToPoolButton);
        webElementInteraction.waitUntilElementIsVisible(claimButton);
        return this;
    }

    public void clickTaskDoneButton()
    {
        webElementInteraction.clickElement(taskDoneButton);
    }

    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        List<WebElement> items = webElementInteraction.waitUntilElementsAreVisible(itemsList);
        for (WebElement anItemsList : items)
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public enum TaskStatus
    {
        NOT_STARTED("Not Yet Started"),
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");

        private String status;

        TaskStatus(String status)
        {
            this.status = status;
        }

        public String getStatus()
        {
            return this.status;
        }
    }
}