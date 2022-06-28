package org.alfresco.po.share;

import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Bogdan.Bocancea
 */
public abstract class ShareDialog extends SharePageObject
{
    //@Autowired
    public Toolbar toolbar;

    @FindBy (css = "div.dijitDialog:not([style*='display: none']) .dijitDialogCloseIcon," +
            "div.yui-dialog:not([style*='visibility: hidden']) [class*='close']")
    protected WebElement closeButton;

    /**
     * Close dialog
     */
    public void clickClose()
    {
        browser.waitUntilElementClickable(closeButton).click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }
}
