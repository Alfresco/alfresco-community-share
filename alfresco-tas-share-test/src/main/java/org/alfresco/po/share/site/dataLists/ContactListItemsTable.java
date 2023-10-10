package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ContactListItemsTable extends ListItemTable
{

    protected By firstNameColumn = By.cssSelector("td[class*='contactFirstName'] div");
    protected By lastNameColumn = By.cssSelector("td[class*='contactLastName'] div");
    protected By emailColumn = By.cssSelector("td[class*='contactEmail'] div");
    protected By companyColumn = By.cssSelector("td[class*='contactCompany'] div");
    protected By jobTitleColumn = By.cssSelector("td[class*='contactJobTitle'] div");
    protected By officePhoneColumn = By.cssSelector("td[class*='contactPhoneOffice'] div");
    protected By mobilePhoneColumn = By.cssSelector("td[class*='contactPhoneMobile'] div");
    protected By notesColumn = By.cssSelector("td[class*='contactNotes'] div");
    protected By checkColumn = By.cssSelector("input[id*='checkbox']");

    public ContactListItemsTable(WebElement webElement, WebBrowser browser)
    {
        super(webElement, browser);
    }

    public WebElement getCheckBoxColumn()
    {
        return webElement.findElement(checkColumn);
    }

    public WebElement getFirstNameColumn()
    {
        return webElement.findElement(firstNameColumn);
    }

    public WebElement getLastNameColumn()
    {
        return webElement.findElement(lastNameColumn);
    }

    public WebElement getEmailColumn()
    {
        return webElement.findElement(emailColumn);
    }

    public WebElement getCompanyColumn()
    {
        return webElement.findElement(companyColumn);
    }

    public WebElement getJobTitleColumn()
    {
        return webElement.findElement(jobTitleColumn);
    }

    public WebElement getOfficePhoneColumn()
    {
        return webElement.findElement(officePhoneColumn);
    }

    public WebElement getMobilePhoneColumn()
    {
        return webElement.findElement(mobilePhoneColumn);
    }

    public WebElement getNotesColumn()
    {
        return webElement.findElement(notesColumn);
    }

}
