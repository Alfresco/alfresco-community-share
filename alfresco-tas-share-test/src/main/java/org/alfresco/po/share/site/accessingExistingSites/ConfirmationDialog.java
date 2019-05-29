package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 7/22/2016.
 */
public abstract class ConfirmationDialog extends HtmlPage
{
    //@RenderWebElement
    //@FindBy(xpath = "//div[contains(@class, 'dialogDisplayed')]//span[contains(@id, 'alfresco_buttons_AlfButton') and (text()='OK' or text()='Yes')]")
    @FindBy (id = "ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CONFIRM_label")
    protected Button dialogOkButton;

    @FindBy (css = "div.dialog-body")
    //@FindBy(css = "div.dialogDisplayed div.dijitDialogPaneContent>div.dialog-body")
    protected WebElement dialogMessage;

    @FindBy (css = "span.dijitDialogCloseIcon")
    //@FindBy(css = "div.dialogDisplayed span.dijitDialogCloseIcon")
    protected WebElement dialogCloseButton;


    @FindBy (css = "span[widgetid='ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CONFIRM'] >span")
    protected WebElement confirmDeletionButton;


    /**
     * Obtain the message of the dialog
     *
     * @return the message
     */
    public String getDialogMessage()
    {
        return browser.waitUntilElementVisible(dialogMessage).getText();
    }

    /**
     * Click on the OK button
     */
    public void clickOKButton()
    {
        getBrowser().waitUntilElementClickable(confirmDeletionButton).click();
        getBrowser().clickJS(confirmDeletionButton);
    }

    /**
     * Check if Ok button is displayed
     *
     * @return true if it's displayed, false otherwise
     */
    public boolean isOkButtonDisplayed()
    {
//        return dialogOkButton.isDisplayed();
        return browser.waitUntilElementVisible(By.cssSelector("span[class$='dijitButtonContents']")).isDisplayed();
    }

    /**
     * Check if Close button is displayed
     *
     * @return true if it's displayed, false otherwise
     */
    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(dialogCloseButton);
    }

    public void clickCloseButton()
    {
        dialogCloseButton.click();
    }
}
