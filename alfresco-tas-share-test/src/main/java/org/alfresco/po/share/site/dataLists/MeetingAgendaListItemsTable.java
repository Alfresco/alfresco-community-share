package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class MeetingAgendaListItemsTable extends ListItemTable
{

    protected WebElement webElement;
    protected By referenceColumn = By.cssSelector("td[class*='meetingAgendaRef'] div");
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By timeColumn = By.cssSelector("td[class*='meetingAgendaTime'] div");
    protected By ownerColumn = By.cssSelector("td[class*='meetingAgendaOwner'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");
    protected By checkColumn = By.cssSelector("input[id*='checkbox']");

    public MeetingAgendaListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getReferenceColumn()
    {
        return browser.findElement(referenceColumn);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getTimeColumn()
    {
        return browser.findElement(timeColumn);
    }

    public WebElement getOwnerColumn()
    {
        return browser.findElement(ownerColumn);
    }

    public WebElement getAttachmentsColumn()
    {
        return browser.findElement(attachmentsColumn);
    }
}
