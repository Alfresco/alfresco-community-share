package org.alfresco.po.share.site.dataLists;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class EditItemPopUp extends CreateNewItemPopUp
{
    public EditItemPopUp(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public void editContent(String field, String content)
    {
        getBrowser().waitUntilElementVisible(By.cssSelector(String.format(fieldLocator, field)));
        getBrowser().findElement(By.cssSelector(String.format(fieldLocator, field))).clear();
        addContent(field, content);
    }
}
