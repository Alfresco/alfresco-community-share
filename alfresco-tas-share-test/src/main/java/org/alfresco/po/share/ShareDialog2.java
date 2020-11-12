package org.alfresco.po.share;

import org.openqa.selenium.By;

/**
 * @author Bogdan.Bocancea
 */
public abstract class ShareDialog2 extends SharePageObject2
{
    protected By closeButton = By.cssSelector( "div.dijitDialog:not([style*='display: none']) .dijitDialogCloseIcon," +
        "div.yui-dialog:not([style*='visibility: hidden']) [class*='close']");

    public void clickClose()
    {
        getBrowser().waitUntilElementClickable(closeButton).click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeButton);
    }
}
