package org.alfresco.po.share;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author Bogdan.Bocancea
 *
 * This class represents a dialog component template and should be extended by
 * each class which use specific dialog component members/methods.
 */
public abstract class BaseDialogComponent extends BasePage
{
    protected By closeButton = By.cssSelector( "div.dijitDialog:not([style*='display: none']) .dijitDialogCloseIcon," +
        "div.yui-dialog:not([style*='visibility: hidden']) [class*='close']");

    protected BaseDialogComponent(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void clickClose()
    {
        clickElement(closeButton);
    }

    public boolean isCloseButtonDisplayed()
    {
        return isElementDisplayed(closeButton);
    }
}
