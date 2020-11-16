package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;

public class LeaveSiteDialog extends ConfirmationDialog
{
    private By dialogContainer = By.id("ALF_SITE_SERVICE_DIALOG");
    @RenderWebElement
    private By dialogTitle = By.id("ALF_SITE_SERVICE_DIALOG_title");
    private By dialogCancelButton = By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG'] span[class$='alfresco-buttons-AlfButton'] span[id^='alfresco_buttons_AlfButton']");
    private By okButton = By.cssSelector(".footer span[class*='call-to-action'] span[role='button']");

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public boolean isCancelButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogCancelButton);
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(dialogCancelButton).click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']"));
    }

    public boolean isLeaveSiteDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogContainer);
    }


    public void clickOKButton()
    {
        getBrowser().findElement(okButton).click();
        waitUntilNotificationMessageDisappears();
    }
}
