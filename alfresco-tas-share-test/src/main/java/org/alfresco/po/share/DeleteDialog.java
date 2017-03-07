package org.alfresco.po.share;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
@PageObject
public class DeleteDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy(id = "prompt_h")
    private WebElement dialogHeader;

    @RenderWebElement
    @FindBy(xpath = "//div[@id='prompt_c' and contains(@style, 'visibility: visible')]//button[text()='Delete']")
    public WebElement deleteButton;

    @FindBy(css = "span[class*='default'] button")
    private WebElement cancelButton;

    @FindBy(css = "#prompt_h + div.bd")
    private WebElement message;

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

    public void clickDelete()
    {
        deleteButton.click();
        browser.waitInSeconds(5);
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