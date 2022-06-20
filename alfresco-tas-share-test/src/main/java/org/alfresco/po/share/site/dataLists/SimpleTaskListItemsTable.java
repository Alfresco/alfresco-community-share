package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SimpleTaskListItemsTable extends ListItemTable
{

    protected WebElement webElement;
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By dueDateColumn = By.cssSelector("td[class*='simpletaskDueDate'] div");
    protected By priorityColumn = By.cssSelector("td[class*='simpletaskPriority'] div");
    protected By statusColumn = By.cssSelector("td[class*='simpletaskStatus'] div");
    protected By commentsColumn = By.cssSelector("td[class*='simpletaskComments'] div");

//    public SimpleTaskListItemsTable(WebElement webElement)
//    {
//        super(webElement);
//    }

    public SimpleTaskListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getDueDateColumn()
    {
        return browser.findElement(dueDateColumn);
    }

    public WebElement getPriorityColumn()
    {
        return browser.findElement(priorityColumn);
    }

    public WebElement getStatusColumn()
    {
        return browser.findElement(statusColumn);
    }

    public WebElement getCommentsColumn()
    {
        return browser.findElement(commentsColumn);
    }
}
