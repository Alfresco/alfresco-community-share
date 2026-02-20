package org.alfresco.po.share.dialogWindows;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/27/2017.
 */
@PageObject
public class ConfirmDeletionDialogShare extends ShareDialog
{
    @RenderWebElement
    @FindBy (id = "ALF_DELETE_CONTENT_DIALOG")
    private WebElement confirmDeleteDialog;

    @FindBy (css = "span.dijitDialogCloseIcon")
    private WebElement closeButton;

    @FindBy (css = "span[id='ALF_DELETE_CONTENT_DIALOG_CONFIRMATION_label']")
    private WebElement deleteButton;

    @FindBy (css = "span[id='ALF_DELETE_CONTENT_DIALOG_CANCELLATION_label']")
    private WebElement cancelButton;

    public void clickCloseButton()
    {
        browser.mouseOver(closeButton);
        closeButton.click();
    }

    public void clickCancelButton()
    {
        browser.waitUntilElementVisible(cancelButton);
        cancelButton.click();
    }

    public void clickDeleteButton()
    {
        getBrowser().waitUntilElementClickable(deleteButton);
        deleteButton.click();
    }
}
