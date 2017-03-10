package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ListItemTable
{

    protected WebElement webElement;
    protected WebBrowser browser;
    
    protected By edit = By.cssSelector("a[title='Edit']");
    protected By duplicate = By.cssSelector("a[title='Duplicate']");
    protected By delete = By.cssSelector("a[title='Delete']");
    protected By actionsColumn = By.cssSelector("td[class*='col-actions'] div");
    
    public ListItemTable(WebElement webElement, WebBrowser browser)
    {
        this.webElement = webElement;
        this.browser = browser;
    }
    
    public WebElement getEditButton()
    {
        browser.mouseOver(webElement.findElement(actionsColumn));
        return webElement.findElement(actionsColumn).findElement(edit);
    }
    
    public WebElement getDuplicateButton()
    {
        browser.mouseOver(webElement.findElement(actionsColumn));
        return webElement.findElement(actionsColumn).findElement(duplicate);
    }
    
    public WebElement getDeleteButton()
    {
        browser.mouseOver(webElement.findElement(actionsColumn));
        return webElement.findElement(actionsColumn).findElement(delete);
    }
}
