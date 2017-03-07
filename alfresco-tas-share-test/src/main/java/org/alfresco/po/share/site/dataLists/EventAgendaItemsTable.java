package org.alfresco.po.share.site.dataLists;

import org.alfresco.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EventAgendaItemsTable extends ListItemTable
{
    protected WebElement webElement;
    protected By referenceColumn = By.cssSelector("td[class*='eventAgendaRef'] div");
    protected By startTimeColumn = By.cssSelector("td[class*='eventAgendaStartTime'] div");
    protected By endTimeColumn = By.cssSelector("td[class*='eventAgendaEndTime'] div");
    protected By sessionNameColumn = By.cssSelector("td[class*='eventAgendaSessionName'] div");
    protected By presenterColumn = By.cssSelector("td[class*='eventAgendaPresenter'] div");
    protected By audienceColumn = By.cssSelector("td[class*='eventAgendaAudience'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");
    protected By notesColumn = By.cssSelector("td[class*='eventAgendaNotes'] div");

    public EventAgendaItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getReferenceColumn()
    {
        return browser.findElement(referenceColumn);
    }

    public WebElement getStartTimeColumn()
    {
        return browser.findElement(startTimeColumn);
    }

    public WebElement getEndTimeColumn()
    {
        return browser.findElement(endTimeColumn);
    }

    public WebElement getSessionNameColumn()
    {
        return browser.findElement(sessionNameColumn);
    }

    public WebElement getPresenterColumn()
    {
        return browser.findElement(presenterColumn);
    }

    public WebElement getAudienceColumn()
    {
        return browser.findElement(audienceColumn);
    }

    public WebElement getAttachmentsColumn()
    {
        return browser.findElement(attachmentsColumn);
    }

    public WebElement getNotesColumn()
    {
        return browser.findElement(notesColumn);
    }
}
