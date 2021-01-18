package org.alfresco.po.share.site.dataLists;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class DeleteListPopUp extends ShareDialog
{
    @FindBy (css = "span[class='button-group'] span[class*='primary-button'] button")
    protected WebElement deleteButton;
    @FindBy (css = "span[class='button-group'] span[class*='default'] span button")
    protected WebElement cancelButton;

    public void clickDeleteButton()
    {
        deleteButton.click();
    }

    public void clickCancelButton()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(cancelButton, 20);
        cancelButton.click();
    }
}
