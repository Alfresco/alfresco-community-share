package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Bogdan.Bocancea
 */
public abstract class ShareDialog extends HtmlPage
{
    @FindBy (css = "a.container-close")
    protected WebElement closeButton;

    /**
     * Close dialog
     */
    public void clickClose()
    {
        closeButton.click();
    }

    public boolean isCloseButtonDisplayed()
    {
        return closeButton.isDisplayed();
    }
}
