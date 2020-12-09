package org.alfresco.po.share;

import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DeleteDialog extends BaseDialogComponent
{
    @RenderWebElement
    public By deleteButton = By.cssSelector("#prompt .ft .button-group>span:nth-of-type(1) button");
    @RenderWebElement
    private final By dialogHeader = By.id("prompt_h");
    private final By cancelButton = By.cssSelector("span[class*='default'] button");
    private final By message = By.cssSelector("#prompt_h + div.bd");
    private final By dialogBody = By.id("prompt_c");

    public DeleteDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getHeader()
    {
        return getBrowser().findElement(dialogHeader).getText();
    }

    public DeleteDialog assertDeleteDialogHeaderEqualsTo(String expectedHeader)
    {
        LOG.info("Assert dialog header equals to {}", expectedHeader);
        assertEquals(getElementText(dialogHeader), expectedHeader, String.format("Dialog header is not equals to %s", expectedHeader));
        return this;
    }

    public String getMessage()
    {
        return getBrowser().findElement(message).getText();
    }

    public DeleteDialog assertConfirmDeleteMessageForContentEqualsTo(String deletedObject)
    {
        LOG.info("Assert confirm delete message is correct for content {}", deletedObject);
        assertEquals(getElementText(message), String.format(language.translate("confirmDeletion.message"), deletedObject),
                "Delete confirm message is correct");
        return this;
    }

    public DeleteDialog assertConfirmDeleteMessageForContentEqualsTo(ContentModel deletedContent)
    {
        return assertConfirmDeleteMessageForContentEqualsTo(deletedContent.getName());
    }

    public DeleteDialog assertConfirmDeleteMessageEqualsTo(String expectedMessage)
    {
        LOG.info("Assert confirm delete message equals with expected {}", expectedMessage);
        assertEquals(getElementText(message), expectedMessage, String.format("Delete confirm message not equals to %s", expectedMessage));
        return this;
    }

    public void clickDelete()
    {
        LOG.info("Click Delete");
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilNotificationMessageDisappears();
        if(getBrowser().isElementDisplayed(notificationMessageLocator))
        {
            LOG.info("Wait for the second message");
            waitUntilNotificationMessageDisappears();
        }
    }

    public <T> SharePage clickDelete(SharePage page)
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilNotificationMessageDisappears();
        return (SharePage) page.renderedPage();
    }

    public void clickCancel()
    {
        getBrowser().waitUntilElementClickable(cancelButton).click();
        getBrowser().waitUntilElementDisappears(dialogBody);
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