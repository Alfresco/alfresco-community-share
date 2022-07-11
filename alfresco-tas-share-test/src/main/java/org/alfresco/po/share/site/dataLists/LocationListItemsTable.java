package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LocationListItemsTable extends ListItemTable
{
    protected WebElement webElement;
    protected By titleColumn = By.cssSelector("td[class*='cm_title'] div");
    protected By address1Column = By.cssSelector("td[class*='locationAddress1'] div");
    protected By address2Column = By.cssSelector("td[class*='locationAddress2'] div");
    protected By address3Column = By.cssSelector("td[class*='locationAddress3'] div");
    protected By zipCodeColumn = By.cssSelector("td[class*='locationZip'] div");
    protected By stateColumn = By.cssSelector("td[class*='locationState'] div");
    protected By countryColumn = By.cssSelector("td[class*='locationCountry'] div");
    protected By descriptionColumn = By.cssSelector("td[class*='cm_description'] div");
    protected By attachmentsColumn = By.cssSelector("td[class*='cm_attachments'] div");

    public LocationListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getTitleColumn()
    {
        return browser.findElement(titleColumn);
    }

    public WebElement getAddress1Column()
    {
        return browser.findElement(address1Column);
    }

    public WebElement getAddress2Column()
    {
        return browser.findElement(address2Column);
    }

    public WebElement getAddress3Column()
    {
        return browser.findElement(address3Column);
    }

    public WebElement getZipCodeColumn()
    {
        return browser.findElement(zipCodeColumn);
    }

    public WebElement getStateColumn()
    {
        return browser.findElement(stateColumn);
    }

    public WebElement getCountryColumn()
    {
        return browser.findElement(countryColumn);
    }

    public WebElement getDescriptionColumn()
    {
        return browser.findElement(descriptionColumn);
    }

    public WebElement getAttachmentsColumn()
    {
        return browser.findElement(attachmentsColumn);
    }
}
