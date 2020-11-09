package org.alfresco.po.share;

import okio.Utf8;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.ContentModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 8/9/2016.
 */
@Primary
@PageObject
public class DeleteDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "#prompt .ft .button-group>span:nth-of-type(1) button")
    public WebElement deleteButton;

    @RenderWebElement
    @FindBy (id = "prompt_h")
    private WebElement dialogHeader;

    @FindBy (css = "span[class*='default'] button")
    private WebElement cancelButton;

    @FindBy (css = "#prompt_h + div.bd")
    private WebElement message;

    private By dialogBody = By.id("prompt_c");

    public String getHeader()
    {
        return dialogHeader.getText();
    }

    public DeleteDialog assertDeleteDialogHeaderEqualsTo(String expectedHeader)
    {
        LOG.info("Assert dialog header equals to {}", expectedHeader);
        assertEquals(dialogHeader.getText(), expectedHeader, String.format("Dialog header is not equals to %s", expectedHeader));
        return this;
    }

    public String getMessage()
    {
        return message.getText();
    }

    public DeleteDialog assertConfirmDeleteMessageEqualsTo(String deletedObject)
    {
        LOG.info("Assert confirm delete message is correct for content {}", deletedObject);
        assertEquals(message.getText(), String.format(language.translate("confirmDeletion.message"), deletedObject),
            "Delete confirm message is correct");
        return this;
    }

    public DeleteDialog assertConfirmDeleteMessageEqualsTo(ContentModel deletedContent)
    {
        return assertConfirmDeleteMessageEqualsTo(deletedContent.getName());
    }

    public void clickDelete()
    {
        LOG.info("Click Delete");
        browser.waitUntilElementClickable(deleteButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public <T> SharePage clickDelete(SharePage page)
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilMessageDisappears();
        return (SharePage) page.renderedPage();
    }

    public void clickCancel()
    {
        browser.waitUntilElementClickable(cancelButton).click();
        browser.waitUntilElementDisappears(dialogBody);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return browser.isElementDisplayed(deleteButton);
    }

    public DeleteDialog assertDeleteButtonIsDisplayed()
    {
        LOG.info("Assert Delete button is displayed");
        assertTrue(browser.isElementDisplayed(deleteButton), "Delete button is displayed.");
        return this;
    }

    public DeleteDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed.");
        return this;
    }
}