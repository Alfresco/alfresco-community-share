package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeaveSiteDialog extends ConfirmationDialog
{
    private final By dialogContainer = By.id("ALF_SITE_SERVICE_DIALOG");
    @RenderWebElement
    private final By dialogTitle = By.id("ALF_SITE_SERVICE_DIALOG_title");
    private final By dialogCancelButton = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] span[class$='alfresco-buttons-AlfButton'] span[id^='alfresco_buttons_AlfButton']");
    private final By okButton = By.cssSelector(".footer span[class*='call-to-action'] span[role='button']");

    public LeaveSiteDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public boolean isCancelButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dialogCancelButton);
    }

    public void clickCancelButton()
    {
        webElementInteraction.findElement(dialogCancelButton).click();
        webElementInteraction.waitUntilElementDisappears(By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']"));
    }

    public boolean isLeaveSiteDialogDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dialogContainer);
    }


    public void clickOKButton()
    {
        webElementInteraction.findElement(okButton).click();
        waitUntilNotificationMessageDisappears();
    }
}
