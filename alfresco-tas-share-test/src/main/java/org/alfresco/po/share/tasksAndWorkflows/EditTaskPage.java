package org.alfresco.po.share.tasksAndWorkflows;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class EditTaskPage extends SharePage<EditTaskPage>
{
    @Autowired
    MyTasksPage myTasksPage;
    
    @RenderWebElement
    @FindBy(css = "div.task-edit-header h1")
    private WebElement taskEditHeader;

    @RenderWebElement
    @FindBy(css = "textarea[id$='bpm_comment']")
    private WebElement commentTextArea;
    
    @FindBy(css = "button[id$='approve-button']")
    private WebElement approveButton;
    
    @FindBy(css = "button[id$='reject-button']")
    private WebElement rejectButton;

    @FindBy(css = "div.form-field div.viewmode-field span[data-datatype$='text']")
    private WebElement message;

    @FindBy(css = "div.form-field div.viewmode-field span[id$='_prop_taskOwner'] a")
    private WebElement owner;

    @FindBy(xpath = "//span[@class = 'viewmode-label' and text() = 'Identifier:']")
    private WebElement identifier;

    @FindBy(xpath = "//span[contains(@class,'viewmode-value') and not(@data-datatype) and not(@id)]")
    private WebElement priority;

    @FindBy(xpath = "//span[@class = 'viewmode-label' and text() = 'Due:']")
    private WebElement dueDate;

    private By statusDropdown = By.cssSelector("select[title = 'Status']");
    private By saveButton = By.cssSelector("button[id$='form-submit-button']");
    private By reassignButton = By.cssSelector("button[id$='reassign-button']");
    private By taskDoneButton = By.cssSelector("button[id$='Next-button']");
    private By cancelButton = By.cssSelector("button[id$='form-cancel-button']");
    private By addItemsButton = By.cssSelector("div[id$='itemGroupActions'] button");

    public enum TaskStatus
    {
        NOT_STARTED("Not Yet Started"),
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");

        private String status;

        TaskStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/task-edit";
    }
    
    public <T> SharePage approve(String comment, SharePage<T> page)
    {
        commentTextArea.sendKeys(comment);
        approveButton.click();
        return page;
    }
    
    public <T> SharePage reject(String comment, SharePage<T> page)
    {
        commentTextArea.sendKeys(comment);
        rejectButton.click();
        return page;
    }

    public String getEditTaskHeader() { return taskEditHeader.getText(); }

    public String getComment()
    {
        return commentTextArea.getText();
    }

    public String getMessage() { return message.getText(); }

    public String getOwner() { return owner.getText(); }

    public String getPriority() { return priority.getText(); }

    public boolean isIdentifierPresent() { return identifier.isDisplayed(); }

    public boolean isDueDatePresent() { return dueDate.isDisplayed(); }

    public boolean isSaveButtonPresent() { return browser.findElement(saveButton).isDisplayed(); }

    public boolean isCancelButtonPresent() { return browser.findElement(cancelButton).isDisplayed(); }

    public boolean isTaskDoneButtonPresent() { return browser.findElement(taskDoneButton).isDisplayed(); }

    public boolean isReassignButtonPresent() { return browser.findElement(reassignButton).isDisplayed(); }

    public boolean isAddItemsButtonPresent() { return browser.findElement(addItemsButton).isDisplayed(); }

    public void selectStatus(TaskStatus status)
    {
        Select select = new Select(browser.findElement(statusDropdown));
        browser.waitInSeconds(2);
        select.selectByValue(status.getStatus());
    }

    public void writeComment(String comment)
    {
        commentTextArea.clear();
        commentTextArea.sendKeys(comment);
    }

    public <T> SharePage clickOnSaveButton(SharePage<T> page)
    {
        browser.findElement(saveButton).click();
        return page;
    }

    public boolean isStatusOptionPresent(TaskStatus status)
    {
        Select select = new Select(browser.findElement(statusDropdown));
        browser.waitInSeconds(2);
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()))
                return true;
        return false;
    }

    public boolean isStatusOptionSelected(TaskStatus status)
    {
        Select select = new Select(browser.findElement(statusDropdown));
        browser.waitInSeconds(2);
        List<WebElement> options = select.getOptions();
        for (WebElement value : options)
            if (value.getAttribute("value").contains(status.getStatus()) && value.isSelected())
                return true;
        return false;
    }
}
