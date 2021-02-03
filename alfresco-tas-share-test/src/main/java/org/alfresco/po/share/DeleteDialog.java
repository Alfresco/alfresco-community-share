package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.utility.model.ContentModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class DeleteDialog extends BaseDialogComponent
{
    private final By deleteButton = By.cssSelector("#prompt .ft .button-group>span:nth-of-type(1) button");
    private final By dialogHeader = By.id("prompt_h");
    private final By cancelButton = By.cssSelector("span[class*='default'] button");
    private final By message = By.cssSelector("#prompt_h + div.bd");
    private final By dialogBody = By.id("prompt_c");

    public DeleteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getHeader()
    {
        return webElementInteraction.getElementText(dialogHeader);
    }

    public DeleteDialog assertDeleteDialogHeaderEqualsTo(String expectedHeader)
    {
        log.info("Assert dialog header equals to {}", expectedHeader);
        assertEquals(webElementInteraction.getElementText(dialogHeader), expectedHeader, String.format("Dialog header is not equals to %s", expectedHeader));
        return this;
    }

    public String getMessage()
    {
        return webElementInteraction.findElement(message).getText();
    }

    public DeleteDialog assertConfirmDeleteMessageForContentEqualsTo(String deletedObject)
    {
        log.info("Assert confirm delete message is correct for content {}", deletedObject);
        assertEquals(webElementInteraction.getElementText(message), String.format(language.translate("confirmDeletion.message"), deletedObject),
                "Delete confirm message is correct");
        return this;
    }

    public DeleteDialog assertConfirmDeleteMessageForContentEqualsTo(ContentModel deletedContent)
    {
        return assertConfirmDeleteMessageForContentEqualsTo(deletedContent.getName());
    }

    public DeleteDialog assertConfirmDeleteMessageEqualsTo(String expectedMessage)
    {
        log.info("Assert confirm delete message equals with expected {}", expectedMessage);
        assertEquals(webElementInteraction.getElementText(message), expectedMessage, String.format("Delete confirm message not equals to %s", expectedMessage));
        return this;
    }

    public void clickDelete()
    {
        log.info("Click Delete");
        webElementInteraction.clickElement(deleteButton);
        waitUntilNotificationMessageDisappears();
        if(webElementInteraction.isElementDisplayed(notificationMessageLocator))
        {
            log.info("Wait for the second message");
            waitUntilNotificationMessageDisappears();
        }
    }

    public void clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(cancelButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(deleteButton);
    }

    public DeleteDialog assertDeleteButtonIsDisplayed()
    {
        log.info("Assert Delete button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(deleteButton), "Delete button is displayed.");
        return this;
    }

    public DeleteDialog assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is displayed.");
        return this;
    }
}