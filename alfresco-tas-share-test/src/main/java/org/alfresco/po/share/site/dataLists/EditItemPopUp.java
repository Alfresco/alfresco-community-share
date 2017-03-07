package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.By;

@PageObject
public class EditItemPopUp extends CreateNewItemPopUp
{
    public void editContent(String field, String content)
    {
        browser.waitUntilElementVisible(By.cssSelector(String.format(fieldLocator, field)));
        browser.findElement(By.cssSelector(String.format(fieldLocator, field))).clear();
        addContent(field, content);
    }
}
