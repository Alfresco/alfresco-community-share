package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Claudia Agache on 7/8/2016.
 */
@PageObject
public class RequestSentDialog extends ConfirmationDialog
{
    @FindBy(css = "div.dijitDialogTitleBar")
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
}
