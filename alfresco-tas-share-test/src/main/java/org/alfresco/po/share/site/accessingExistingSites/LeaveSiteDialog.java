package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 7/5/2016.
 */
@PageObject
public class LeaveSiteDialog extends ConfirmationDialog
{
    @FindBy (id = "ALF_SITE_SERVICE_DIALOG")
    private WebElement dialogContainer;

    @RenderWebElement
    @FindBy (id = "ALF_SITE_SERVICE_DIALOG_title")
    private WebElement dialogTitle;

    @FindBy (css = "div[id='ALF_SITE_SERVICE_DIALOG'] span[class$='alfresco-buttons-AlfButton'] span[id^='alfresco_buttons_AlfButton']")
    private Button dialogCancelButton;

    @FindBy(css = ".footer span[class*='call-to-action'] span[role='button']")
    private WebElement okButton;

    /**
     * Obtain the title of Leave Site dialog
     *
     * @return the title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    /**
     * Check if Cancel button is displayed
     *
     * @return true if it's displayed, false otherwise
     */
    public boolean isCancelButtonDisplayed()
    {
        return dialogCancelButton.isDisplayed();
    }

    public void clickCancelButton()
    {
        dialogCancelButton.click();
        getBrowser().waitUntilElementDisappears(By.cssSelector("div[id='ALF_SITE_SERVICE_DIALOG']"));
    }

    public boolean isLeaveSiteDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogContainer);
    }


    public void clickOKButton()
    {
        okButton.click();
        waitUntilMessageDisappears();
    }
}
