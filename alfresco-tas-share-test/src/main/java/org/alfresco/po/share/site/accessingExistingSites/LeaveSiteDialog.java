package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 7/5/2016.
 */
@PageObject
public class LeaveSiteDialog  extends ConfirmationDialog
{
    @FindBy(id = "ALF_SITE_SERVICE_DIALOG")
    private WebElement dialog;

    @RenderWebElement
    @FindBy(id = "ALF_SITE_SERVICE_DIALOG_title")
    private WebElement dialogTitle;

    @FindBy(xpath = "//div[contains(@class, 'dijitDialogPaneContent')]//span[contains(@id, 'alfresco_buttons_AlfButton') and text()='Cancel']")
    private Button dialogCancelButton;

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
    }

    /**
     * Check if popup is hidden or displayed
     *
     * @return true if hidden, false if displayed
     */
    public boolean isPopupHidden()
    {
        String elementClass = dialog.getAttribute("class");
        if(elementClass.contains("dialogHidden"))
            return true;
        return false;
    }
}
