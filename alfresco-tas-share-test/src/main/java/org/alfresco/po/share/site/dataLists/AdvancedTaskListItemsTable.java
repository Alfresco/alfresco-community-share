package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdvancedTaskListItemsTable extends ListItemTable
{
    protected WebElement webElement;
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By startDateColumn = By.cssSelector("td[class*='ganttStartDate'] div");
    protected By endDateColumn = By.cssSelector("td[class*='ganttEndDate'] div");
    protected By assigneeColumn = By.cssSelector("td[class*='taskAssignee'] div");
    protected By priorityColumn = By.cssSelector("td[class*='taskPriority'] div");
    protected By statusColumn = By.cssSelector("td[class*='taskStatus'] div");
    protected By completeColumn = By.cssSelector("td[class*='ganttPercentComplete'] div");
    protected By commentsColumn = By.cssSelector("td[class*='taskComments'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");

    public AdvancedTaskListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getStartDateColumn()
    {
        return webElement.findElement(startDateColumn);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getEndDateColumn()
    {
        return browser.findElement(endDateColumn);
    }

    public WebElement getAssigneeColumn()
    {
        return browser.findElement(assigneeColumn);
    }

    public WebElement getPriorityColumn()
    {
        return browser.findElement(priorityColumn);
    }

    public WebElement getStatusColumn()
    {
        return browser.findElement(statusColumn);
    }

    public WebElement getCompleteColumn()
    {
        return browser.findElement(completeColumn);
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
