package org.alfresco.po.share;

import org.alfresco.common.Timeout;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
@Primary
@PageObject
public class DeleteDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (xpath = "//div[@id='prompt_c' and contains(@style, 'visibility: visible')]//button[text()='Delete']")
    public WebElement deleteButton;
    @RenderWebElement
    @FindBy (id = "prompt_h")
    private WebElement dialogHeader;
    @FindBy (css = "span[class*='default'] button")
    private WebElement cancelButton;

    @FindBy (css = "#prompt_h + div.bd")
    private WebElement message;

    /** "<object> has been deleted.." popup */
    private static final By MESSAGE_DELETED = By.className("div.bd span.message");

    public String getHeader()
    {
        return dialogHeader.getText();
    }

    /**
     * @return Dialog's displayed message
     */
    public String getMessage()
    {
        return message.getText();
    }

    public <T> SharePage clickDelete(SharePage page)
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        try
        {
            getBrowser().waitUntilElementVisible(MESSAGE_DELETED, Timeout.SHORT.getTimeoutSeconds());
            getBrowser().waitUntilElementDisappears(MESSAGE_DELETED);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }

        return (SharePage) page.renderedPage();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return browser.isElementDisplayed(deleteButton);
    }
}