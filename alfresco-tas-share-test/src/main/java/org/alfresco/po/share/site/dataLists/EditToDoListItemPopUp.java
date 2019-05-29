package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Select;

import java.util.List;

@PageObject
public class EditToDoListItemPopUp extends CreateNewItemPopUp
{
    @FindBy (css = ".hd")
    protected List<WebElement> editDataItemPopup;

    @FindBy (css = "input[id*='todoTitle']")
    protected WebElement toDoListTitleInputField;

    @FindBy (css = "input[id*='todoDueDate']")
    protected WebElement toDoListDueDateInputField;

    @FindBy (css = "input[id*='time']")
    protected WebElement toDoListDueDateTimeInputField;

    @FindBy (css = "input[id*='todoPriority']")
    protected WebElement toDoListPriorityInputField;

    @FindBy (css = "select[id*='todoStatus']")
    protected WebElement toDoListStatusDropDownList;

    @FindBy (css = "textarea[id*='todoNotes']")
    protected WebElement toDoListItemNotes;

    @FindBy (css = "div[id*='assoc_dl_assignee-cntrl-currentValueDisplay'] div")
    protected WebElement currentAssigneDisplayed;

    @FindBy (css = ".form-fields div[id*='assoc_dl_assignee-cntrl'] .show-picker button")
    protected WebElement assigneeSelectButton;

    @FindBy (css = "div[id*='assignee-cntrl-currentValueDisplay'] div")
    protected WebElement assigneeCurrentValueDisplayed;

    @FindBy (css = "div[id*='attachments-cntrl-currentValueDisplay']")
    protected WebElement attachmentsCurrentValueDisplayed;

    @FindBy (css = "div[id*='attachments-cntrl-itemGroupActions'] button")
    protected WebElement selectAttachmentsButton;

    @FindBy (css = "div[id*='attachments-cntrl-picker-folderUpContainer'] button")
    protected WebElement folderUpButton;

    @FindBy (css = "div[id*='attachments-cntrl-picker-results'] tr.yui-dt-odd span.addIcon")
    protected WebElement addIconForDocumentLibrary;

    @FindBy (css = "button[id*='attachments-cntrl-ok-button']")
    protected WebElement attachmentsOkButton;

    @FindBy (css = "span.yui-submit-button button")
    protected WebElement createNewItemPopUpSaveButton;

    private By listItems = By.cssSelector("div[id$='default-grid'] table tbody[class='yui-dt-data'] tr");
    ToDoListItemsTable tableRow;

    public boolean isEditDataItemPopUpDisplayed()
    {
        for (WebElement element : editDataItemPopup)
        {
            if (element.getText().equalsIgnoreCase("Edit Data Item"))
                return true;
        }
        return false;
    }

    public String getToDoListItemTitle()
    {
        return toDoListTitleInputField.getAttribute("value");
    }

    public String getToDoListItemDueDate()
    {
        return toDoListDueDateInputField.getAttribute("value");
    }

    public String getToDoListItemPriority()
    {
        return toDoListPriorityInputField.getAttribute("value");
    }

    public void editToDoListItemTime(String time)
    {
        toDoListDueDateTimeInputField.clear();
        toDoListDueDateTimeInputField.sendKeys(time);
    }

    public String getToDoListItemDueTime()
    {
        return toDoListDueDateTimeInputField.getAttribute("value");
    }

    public String getToDoListItemNotes()
    {
        return toDoListItemNotes.getAttribute("value");
    }

    public String getToDoListItemStatus()
    {
        return toDoListStatusDropDownList.getAttribute("value");
    }

    public String getToDoListItemAssignee()
    {
        return assigneeCurrentValueDisplayed.getText();
    }


    public String getToDoListItemattachment()
    {
        return attachmentsCurrentValueDisplayed.getAttribute("value");
    }

    public void addAttachmentDocumentLibrary()
    {
        browser.waitInSeconds(2);
        selectAttachmentsButton.click();
        browser.waitInSeconds(1);
        folderUpButton.click();
        browser.waitInSeconds(1);
        addIconForDocumentLibrary.click();
        browser.waitInSeconds(1);
        attachmentsOkButton.click();
        browser.waitInSeconds(1);
    }

    public void selectToDoListItemStatus(String status)
    {
        Select select = new Select(toDoListStatusDropDownList);
        select.selectByVisibleText(status);
    }

    public void clickSelectAssigneeButton()
    {
        assigneeSelectButton.click();
    }

    public void createNewItemPopUpSaveButton()
    {
        createNewItemPopUpSaveButton.click();
    }

    public String getListItemTitleValue()
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getTitleColumn().getText();
    }

    public boolean getListItemDueDateValue(String toDoListRowValue)
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getDueDateColumn().getText().contains(toDoListRowValue);
    }

    public String getListItemPriorityValue()
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getPriorityColumn().getText();
    }

    public String getListItemStatusValue()
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getStatusColumn().getText();
    }

    public boolean isListItemAssigneeValueEdited(String assigneeName)
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getAssigneeColumn().getText().contains(assigneeName);
    }

    public boolean isListItemAttachmentValueEdited(String attachment)
    {
        browser.waitUntilElementIsDisplayedWithRetry(listItems);
        WebElement row = browser.findElement(listItems);

        tableRow = new ToDoListItemsTable(row, browser);
        return tableRow.getAttachmentsColumn().getText().contains(attachment);
    }
}
