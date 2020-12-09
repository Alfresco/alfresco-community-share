package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public abstract class ConfirmationDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By confirmDeletionButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CONFIRM");
    private final By dialogMessage = By.cssSelector("div.dialog-body");
    private final By dialogCloseButton = By.cssSelector("span.dijitDialogCloseIcon");

    public ConfirmationDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getDialogMessage()
    {
        return getBrowser().waitUntilElementVisible(dialogMessage).getText();
    }

    public void clickOKButton()
    {
        getBrowser().waitUntilElementClickable(confirmDeletionButton).click();
    }

    public boolean isOkButtonDisplayed()
    {
        return getBrowser().waitUntilElementVisible(By.cssSelector("span[class$='dijitButtonContents']")).isDisplayed();
    }

    @Override
    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogCloseButton);
    }

    public void clickCloseButton()
    {
        getBrowser().findElement(dialogCloseButton).click();
    }
}
