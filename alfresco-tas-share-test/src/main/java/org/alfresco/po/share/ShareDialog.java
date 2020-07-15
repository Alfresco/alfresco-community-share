package org.alfresco.po.share;

import org.alfresco.common.Language;
import org.alfresco.common.Timeout;
import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Bogdan.Bocancea
 */
public abstract class ShareDialog extends HtmlPage
{
    @Autowired
    public Language language;

    @FindBy (css = "div.dijitDialog:not([style*='display: none']) .dijitDialogCloseIcon," +
            "div.yui-dialog:not([style*='visibility: hidden']) [class*='close']")
    protected WebElement closeButton;

    /** "<object> has been deleted.." popup */
    private static final By MESSAGE_LOCATOR = By.className("div.bd span.message");

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

    /**
     * Method for wait while balloon message about some changes hide.
     */
    @Override
    public void waitUntilMessageDisappears()
    {
        try
        {
            getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, Timeout.SHORT.getTimeoutSeconds());
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
    }
}
