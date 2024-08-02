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
    private By notification = By.cssSelector("div.bd span.message");

    public DeleteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getHeader()
    {
        return getElementText(dialogHeader);
    }

    public DeleteDialog assertDeleteDialogHeaderEqualsTo(String expectedHeader)
    {
        log.info("Assert dialog header equals to {}", expectedHeader);
        assertEquals(getElementText(dialogHeader), expectedHeader, String.format("Dialog header is not equals to %s", expectedHeader));
        return this;
    }

    public String getMessage()
    {
        waitInSeconds(3);
        return getElementText(message);
    }

    public DeleteDialog assertConfirmDeleteMessageForContentEqualsTo(String deletedObject)
    {
        log.info("Assert confirm delete message is correct for content {}", deletedObject);
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
        log.info("Assert confirm delete message equals with expected {}", expectedMessage);
        assertEquals(getElementText(message), expectedMessage, String.format("Delete confirm message not equals to %s", expectedMessage));
        return this;
    }

    public void confirmDeletion()
    {
        log.info("Confirm deletion");
        clickElement(deleteButton);
        waitUntilNotificationMessageDisappears();
        if(isElementDisplayed(notificationMessageLocator))
        {
            log.info("Wait for the second message");
            waitUntilNotificationMessageDisappears();
        }
    }

    public void confirmDelete()
    {
        log.info("Confirm deletion");
        clickElement(deleteButton);
        waitInSeconds(1);
    }

    public void clickCancel()
    {
        clickElement(cancelButton);
        waitUntilElementDisappears(dialogBody);
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(cancelButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return isElementDisplayed(deleteButton);
    }

    public DeleteDialog assertDeleteButtonIsDisplayed()
    {
        log.info("Assert Delete button is displayed");
        assertTrue(isElementDisplayed(deleteButton), "Delete button is displayed.");
        return this;
    }

    public DeleteDialog assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is displayed.");
        return this;
    }

    public DeleteDialog assertDeleteDialogHeaderEquals(String header)
    {
        log.info("Assert Delete Dialog Header is equals {}", header);
        assertEquals(getHeader(), header, String.format("Header is not matched with %s",header));
        return this;
    }

    public DeleteDialog assertVerifyDisplayedNotification(String expectedMessage)
    {
        assertEquals(findElement(notification).getText(), expectedMessage,"Check Notification message");
        return this;
    }
}