package org.alfresco.po.share.tasksAndWorkflows;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.TaskStatus;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

@Slf4j
public class EditTaskPage extends SharePage2<EditTaskPage>
{
    private final By taskEditHeader = By.cssSelector( "div.task-edit-header h1");
    private final By commentTextArea = By.cssSelector("textarea[id$='bpm_comment']");
    private final By approveButton = By.cssSelector("button[id$='approve-button']");
    private final By rejectButton = By.cssSelector("button[id$='reject-button']");
    private final By buttonReject = By.xpath("//button[text()=\"Reject\"]");
    private final By buttonApprove = By.xpath("//button[text()=\"Approve\"]");
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
    private final By saveAndCloseButton = By.xpath("//button[text()=\"Save and Close\"]");
    private final By reassignButton = By.cssSelector("button[id$='reassign-button']");
    private final By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private final By addItemsButton = By.cssSelector("div[id$='itemGroupActions'] button");

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
        log.info("Assert Edit Task page is opened");
        waitUntilElementIsVisible(taskEditHeader);
        assertTrue(isElementDisplayed(taskEditHeader), "Edit task header is displayed");
        assertTrue(getCurrentUrl().contains(getRelativePath()), "Edit Task page is opened");
        return this;
    }

    public EditTaskPage insertTaskComment(String comment)
    {
        log.info("Insert task comment {}", comment);
        clearAndType(commentTextArea, comment);
        return this;
    }

    public EditTaskPage approveTask()
    {
        log.info("Approve task");
        clickElement(approveButton);
        waitUntilElementDisappears(approveButton);
        return this;
    }

    public void rejectTask()
    {
        waitUntilElementIsVisible(rejectButton);
        clickElement(rejectButton);
    }

    public String getEditTaskHeader()
    {
        return getElementText(taskEditHeader);
    }

    public String getComment()
    {
        return getElementText(commentTextArea);
    }

    public String getMessage()
    {
        return getElementText(message);
    }

    public String getOwner()
    {
        return getElementText(owner);
    }

    public String getPriority()
    {
        return getElementText(priority);
    }

    public boolean isIdentifierPresent()
    {
        return isElementDisplayed(identifier);
    }

    public boolean isDueDatePresent()
    {
        return isElementDisplayed(dueDate);
    }

    public boolean isSaveButtonPresent()
    {
        return isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonPresent()
    {
        return isElementDisplayed(cancelButton);
    }

    public boolean isTaskDoneButtonPresent()
    {
        return isElementDisplayed(taskDoneButton);
    }

    public boolean isReassignButtonPresent()
    {
        return isElementDisplayed(reassignButton);
    }

    public boolean isAddItemsButtonPresent()
    {
        return isElementDisplayed(addItemsButton);
    }

    public void selectStatus(TaskStatus status)
    {
        Select select = new Select(findElement(statusDropdown));
        waitUntilElementIsVisible(statusDropdown);
        select.selectByValue(status.getStatus().toString());
    }

    public void writeComment(String comment)
    {
        waitInSeconds(2);
        insertTaskComment(comment);
    }

    public void clickOnSaveButton()
    {
        waitInSeconds(3);
        findElement(saveAndCloseButton).click();
    }

    public boolean isStatusOptionPresent(TaskStatus status)
    {
        Select select = new Select(findElement(statusDropdown));
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()))
                return true;
        return false;
    }

    public boolean isStatusOptionSelected(TaskStatus status)
    {
        Select select = new Select(findElement(statusDropdown));
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()) && value.isSelected())
                return true;
        return false;
    }

    public EditTaskPage clickClaimButton()
    {
        clickElement(claimButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public EditTaskPage clickReleaseToPoolButton()
    {
        clickElement(releaseToPoolButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public EditTaskPage assertReleaseToPoolButtonIsDisplayed()
    {
        log.info("Assert Release to pool button is displayed");
        waitUntilElementIsVisible(releaseToPoolButton);
        assertTrue(isElementDisplayed(releaseToPoolButton));

        return this;
    }

    public EditTaskPage assertClaimButtonIsDisplayed()
    {
        log.info("Assert Claim button is displayed");
        waitUntilElementIsVisible(claimButton);
        assertTrue(isElementDisplayed(claimButton));

        return this;
    }

    public void clickTaskDoneButton()
    {
        clickElement(taskDoneButton);
    }

    public String getItemsList()
    {
        ArrayList<String> itemsTextList = new ArrayList<>();
        List<WebElement> items = findElements(itemsList);
        for (WebElement anItemsList : items)
        {
            itemsTextList.add(anItemsList.getText());
        }
        return itemsTextList.toString();
    }

    public void clickRejectButton() {
        waitInSeconds(3);
        findElement(buttonReject).click();
    }
    public String getPage_Title()
    {
        return webDriver.get().getTitle();
    }

    public void commentAndClickApprove(String message) {
        waitUntilElementIsVisible(commentTextArea);
        clearAndType(commentTextArea, message);
        waitInSeconds(2);
        findElement(buttonApprove).click();
    }

    public void commentAndClickReject(String message) {
        waitUntilElementIsVisible(commentTextArea);
        clearAndType(commentTextArea, message);
        waitInSeconds(2);
        findElement(buttonReject).click();
    }
}