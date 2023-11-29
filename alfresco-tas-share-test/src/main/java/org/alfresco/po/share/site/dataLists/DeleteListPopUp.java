package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeleteListPopUp extends BaseDialogComponent
{
    private final By deleteButton = By.cssSelector("span[class='button-group'] span[class*='primary-button'] button");
    private final By cancelButton = By.cssSelector("span[class='button-group'] span[class*='default'] span button");

    public DeleteListPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickDeleteButton()
    {
        clickElement(deleteButton);
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
    }
}
