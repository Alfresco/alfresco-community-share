package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RequestSentDialog extends ConfirmationDialog
{
    @RenderWebElement
    private final By dialogTitle = By.cssSelector("div.dijitDialogTitleBar");

    public RequestSentDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public void clickOKButton()
    {
        webElementInteraction.waitUntilElementIsVisible(By.cssSelector("span[widgetid*='alfresco_buttons_AlfButton'] span")).click();
    }
}
