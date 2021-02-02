package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DeleteListPopUp extends BaseDialogComponent
{
    private final By deleteButton = By.cssSelector("span[class='button-group'] span[class*='primary-button'] button");
    private final By cancelButton = By.cssSelector("span[class='button-group'] span[class*='default'] span button");

    protected DeleteListPopUp(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickDeleteButton()
    {
        webElementInteraction.clickElement(deleteButton);
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
    }
}
