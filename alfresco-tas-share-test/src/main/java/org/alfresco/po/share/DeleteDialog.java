package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DeleteDialog extends ShareDialog2
{
    @RenderWebElement
    public By deleteButton = By.cssSelector("#prompt .ft .button-group>span:nth-of-type(1) button");
    @RenderWebElement
    private By dialogHeader = By.id("prompt_h");
    private By cancelButton = By.cssSelector("span[class*='default'] button");
    private By message = By.cssSelector("#prompt_h + div.bd");

    public DeleteDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getHeader()
    {
        return getElementText(dialogHeader);
    }

    public String getMessage()
    {
        return getElementText(message);
    }

    public DeleteDialog assertConfirmDeleteMessageIsCorrect(String deletedObject)
    {
        LOG.info("Assert confirm delete message is correct");
        assertEquals(getElementText(message), String.format(language.translate("confirmDeletion.message"), deletedObject),
            "Delete confirm message is correct");
        return this;
    }

    public void clickDelete()
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(cancelButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(deleteButton);
    }

    public DeleteDialog assertDeleteButtonIsDisplayed()
    {
        LOG.info("Assert Delete button is displayed");
        assertTrue(getBrowser().isElementDisplayed(deleteButton), "Delete button is displayed.");
        return this;
    }

    public DeleteDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed.");
        return this;
    }
}