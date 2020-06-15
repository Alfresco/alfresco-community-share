package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Bogdan.Bocancea
 */
public abstract class ShareDialog extends HtmlPage
{
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
