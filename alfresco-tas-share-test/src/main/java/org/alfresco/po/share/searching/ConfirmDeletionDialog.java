package org.alfresco.po.share.searching;

import org.alfresco.po.share.site.accessingExistingSites.ConfirmationDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

/**
 * Created by Claudia Agache on 8/24/2016.
 */
@PageObject
public class ConfirmDeletionDialog extends ConfirmationDialog
{
    @FindBy (id = "ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CANCEL_label")
    protected Button dialogNoButton;
    @RenderWebElement
    @FindBy (id = "ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_title")
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
     * Click on the No button
     */
    public void clickNoButton()
    {
        dialogNoButton.click();
    }
}
