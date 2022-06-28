package org.alfresco.po.share.site.accessingExistingSites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeaveSiteDialog extends ConfirmationDialog
{
    private final By dialogContainer = By.id("ALF_SITE_SERVICE_DIALOG");
    private final By dialogTitle = By.id("ALF_SITE_SERVICE_DIALOG_title");
    private final By dialogCancelButton = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] span[class$='alfresco-buttons-AlfButton'] span[id^='alfresco_buttons_AlfButton']");
    private final By okButton = By.cssSelector(".footer span[class*='call-to-action'] span[role='button']");

    public LeaveSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public boolean isCancelButtonDisplayed()
    {
        return isElementDisplayed(dialogCancelButton);
    }

    public void clickCancelButton()
    {
        findElement(dialogCancelButton).click();
        waitUntilElementDisappears(By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']"));
    }

    public boolean isLeaveSiteDialogDisplayed()
    {
        return isElementDisplayed(dialogContainer);
    }


    public void clickOKButton()
    {
        findElement(okButton).click();
        waitUntilNotificationMessageDisappears();
    }
}
