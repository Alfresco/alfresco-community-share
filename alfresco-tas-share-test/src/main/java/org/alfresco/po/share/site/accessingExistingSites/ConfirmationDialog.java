package org.alfresco.po.share.site.accessingExistingSites;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public abstract class ConfirmationDialog extends BaseDialogComponent
{
    private final By dialogBody = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG");
    private final By confirmDeletionButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CONFIRM");
    private final By dialogMessage = By.cssSelector("div.dialog-body");
    private final By dialogCloseButton = By.cssSelector("span.dijitDialogCloseIcon");

    protected ConfirmationDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogMessage()
    {
        return getElementText(dialogMessage);
    }

    public void clickOKButton()
    {
        log.info("Click OK button");
        WebElement ok = waitUntilElementIsVisible(confirmDeletionButton);
        mouseOver(ok);
        clickElement(ok);
        waitUntilNotificationMessageDisappears();
        if(isElementDisplayed(dialogBody))
        {
            log.error("Failed to click Ok button. Retry click.");
            clickElement(ok);
        }
    }

    public boolean isOkButtonDisplayed()
    {
        return waitUntilElementIsVisible(By.cssSelector("span[class$='dijitButtonContents']")).isDisplayed();
    }

    @Override
    public boolean isCloseButtonDisplayed()
    {
        return isElementDisplayed(dialogCloseButton);
    }

    public void clickCloseButton()
    {
        clickElement(dialogCloseButton);
    }
}
