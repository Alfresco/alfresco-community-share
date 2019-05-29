package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
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
    @FindBy (id = "prompt_h")
    private WebElement dialogHeader;

    @RenderWebElement
    @FindBy (xpath = "//div[@id='prompt_c' and contains(@style, 'visibility: visible')]//button[text()='Delete']")
    public WebElement deleteButton;

    @FindBy (css = "span[class*='default'] button")
    private WebElement cancelButton;

    @FindBy (css = "#prompt_h + div.bd")
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
        getBrowser().waitUntilElementVisible(deleteButton);
        getBrowser().waitUntilElementClickable(deleteButton).click();
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