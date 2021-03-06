package org.alfresco.po.share.site.accessingExistingSites;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class RequestSentDialog extends ConfirmationDialog
{
    private final By dialogTitle = By.cssSelector("div.dijitDialogTitleBar");

    public RequestSentDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickOKButton()
    {
        waitUntilElementIsVisible(By.cssSelector("span[widgetid*='alfresco_buttons_AlfButton'] span")).click();
    }
}
