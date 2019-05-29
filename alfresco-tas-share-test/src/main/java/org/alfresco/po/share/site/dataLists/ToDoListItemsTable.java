package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ToDoListItemsTable extends ListItemTable
{
    protected By titleColumn = By.cssSelector("td[class*='todoTitle'] div");
    protected By dueDateColumn = By.cssSelector("td[class*='todoDueDate'] div");
    protected By priorityColumn = By.cssSelector("td[class*='todoPriority'] div");
    protected By statusColumn = By.cssSelector("td[class*='todoStatus'] div");
    protected By assigneeColumn = By.cssSelector("td[class*='assignee'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='attachments'] div");
    protected By checkColumn = By.cssSelector("input[id*='checkbox']");
    protected By edit = By.cssSelector("a[title='Edit']");
    protected By duplicate = By.cssSelector("a[title='Duplicate']");
    protected By delete = By.cssSelector("a[title='Delete']");

    public ToDoListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getTitleColumn()
    {
        return webElement.findElement(titleColumn);
    }

    public WebElement getDueDateColumn()
    {
        return webElement.findElement(dueDateColumn);
    }

    public WebElement getPriorityColumn()
    {
        return webElement.findElement(priorityColumn);
    }

    public WebElement getStatusColumn()
    {
        return webElement.findElement(statusColumn);
    }

    public WebElement getAssigneeColumn()
    {
        return webElement.findElement(assigneeColumn);
    }

    public WebElement getAttachmentsColumn()
    {
        return webElement.findElement(attachmentsColumn);
    }
}
