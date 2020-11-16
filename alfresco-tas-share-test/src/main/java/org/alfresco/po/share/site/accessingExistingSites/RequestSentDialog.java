package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;

public class RequestSentDialog extends ConfirmationDialog
{
    @RenderWebElement
    private By dialogTitle = By.cssSelector("div.dijitDialogTitleBar");

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickOKButton()
    {
        getBrowser().waitUntilElementVisible(By.cssSelector("span[widgetid*='alfresco_buttons_AlfButton'] span")).click();
    }
}
