package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 7/8/2016.
 */
@PageObject
public class RequestSentDialog extends ConfirmationDialog
{
    @RenderWebElement
    @FindBy (css = "div.dijitDialogTitleBar")
    private WebElement dialogTitle;

    /**
     * Obtain the title of Request Sent dialog
     *
     * @return the title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    /**
     * Click on the OK button
     */
    public void clickOKButton()
    {
        browser.waitUntilElementVisible(By.cssSelector("span[widgetid*='alfresco_buttons_AlfButton'] span")).click();
    }
}
