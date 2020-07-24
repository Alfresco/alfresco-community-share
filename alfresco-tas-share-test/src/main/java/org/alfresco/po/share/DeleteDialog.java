package org.alfresco.po.share;

import org.alfresco.common.Timeout;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
    @FindBy (xpath = "//div[@id='prompt_c' and contains(@style, 'visibility: visible')]//button[text()='Delete']")
    public WebElement deleteButton;
    @RenderWebElement
    @FindBy (id = "prompt_h")
    private WebElement dialogHeader;
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

    public DeleteDialog assertConfirmDeleteMessageIsCorrect(String deletedObject)
    {
        LOG.info("Assert confirm delete message is correct");
        assertEquals(message.getText(), String.format(language.translate("confirmDeletion.message"), deletedObject),
            "Delete confirm message is correct");
        return this;
    }

    public <T> SharePage clickDelete(SharePage page)
    {
        getBrowser().waitUntilElementClickable(deleteButton).click();
        waitUntilMessageDisappears();
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