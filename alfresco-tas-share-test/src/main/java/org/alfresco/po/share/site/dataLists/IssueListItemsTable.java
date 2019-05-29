package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IssueListItemsTable extends ListItemTable
{
    protected WebElement webElement;
    protected By issueIDColumn = By.cssSelector("td[class*='issueID'] div");
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By assignedToColumn = By.cssSelector("td[class*='issueAssignedTo'] div");
    protected By statusColumn = By.cssSelector("td[class*='issueStatus'] div");
    protected By priorityColumn = By.cssSelector("td[class*='issuePriority'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By dueDateColumn = By.cssSelector("td[class*='issueDueDate'] div");
    protected By commentsColumn = By.cssSelector("td[class*='issueComments'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");

    public IssueListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getIssueIDColumn()
    {
        return browser.findElement(issueIDColumn);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getAssignedToColumn()
    {
        return browser.findElement(assignedToColumn);
    }

    public WebElement getStatusColumn()
    {
        return browser.findElement(statusColumn);
    }

    public WebElement getPriorityColumn()
    {
        return browser.findElement(priorityColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getDueDateColumn()
    {
        return browser.findElement(dueDateColumn);
    }

    public WebElement getCommentsColumn()
    {
        return browser.findElement(commentsColumn);
    }

    public WebElement getAttachmentsColumn()
    {
        return browser.findElement(attachmentsColumn);
    }
}
