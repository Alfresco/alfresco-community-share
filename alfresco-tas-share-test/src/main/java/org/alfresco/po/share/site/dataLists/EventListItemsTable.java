package org.alfresco.po.share.site.dataLists;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EventListItemsTable extends ListItemTable
{

    protected WebElement webElement;
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By locationColumn = By.cssSelector("td[class*='eventLocation'] div");
    protected By startDateColumn = By.cssSelector("td[class*='eventStartDate'] div");
    protected By endDateColumn = By.cssSelector("td[class*='eventEndDate'] div");
    protected By registrationsColumn = By.cssSelector("td[class*='eventRegistrations'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");
    protected By notesColumn = By.cssSelector("td[class*='eventNote'] div");

    public EventListItemsTable(WebElement webElement)
    {
        super(webElement);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getLocationColumn()
    {
        return browser.findElement(locationColumn);
    }

    public WebElement getStartDateColumn()
    {
        return browser.findElement(startDateColumn);
    }

    public WebElement getEndDateColumn()
    {
        return browser.findElement(endDateColumn);
    }

    public WebElement getRegistrationsColumn()
    {
        return browser.findElement(registrationsColumn);
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
