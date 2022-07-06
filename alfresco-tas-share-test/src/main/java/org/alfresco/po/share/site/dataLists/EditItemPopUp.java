package org.alfresco.po.share.site.dataLists;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditItemPopUp extends CreateNewItemPopUp
{
    public EditItemPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void editContent(String field, String content)
    {
        waitUntilElementIsVisible(By.cssSelector(String.format(fieldLocator, field)));
        findElement(By.cssSelector(String.format(fieldLocator, field))).clear();
        addContent(field, content);
    }
}
