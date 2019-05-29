package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class VisitorFeedbackListItemsTable extends ListItemTable
{

    protected WebElement webElement;
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By dueDateColumn = By.cssSelector("td[class*='todoDueDate'] div");
    protected By priorityColumn = By.cssSelector("td[class*='todoPriority'] div");
    protected By statusColumn = By.cssSelector("td[class*='todoStatus'] div");
    protected By assigneeColumn = By.cssSelector("td[class*='assignee'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");
    protected By checkColumn = By.cssSelector("input[id*='checkbox']");

    public VisitorFeedbackListItemsTable(WebElement webElement, WebBrowser browser)
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
