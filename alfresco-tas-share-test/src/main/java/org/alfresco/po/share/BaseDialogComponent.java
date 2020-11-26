package org.alfresco.po.share;

import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

/**
 * @author Bogdan.Bocancea
 */
public abstract class BaseDialogComponent extends BasePage
{
    protected By closeButton = By.cssSelector( "div.dijitDialog:not([style*='display: none']) .dijitDialogCloseIcon," +
        "div.yui-dialog:not([style*='visibility: hidden']) [class*='close']");

    public BaseDialogComponent(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public void clickClose()
    {
        getBrowser().waitUntilElementClickable(closeButton).click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeButton);
    }
}
