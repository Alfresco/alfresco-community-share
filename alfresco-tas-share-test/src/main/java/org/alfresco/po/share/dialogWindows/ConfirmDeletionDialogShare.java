package org.alfresco.po.share.dialogWindows;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

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
    private Button closeButton;

    @FindBy (css = "span[id='ALF_DELETE_CONTENT_DIALOG_CONFIRMATION_label']")
    private Button deleteButton;

    @FindBy (css = "span[id='ALF_DELETE_CONTENT_DIALOG_CANCELLATION_label']")
    private Button cancelButton;

    public void clickCloseButton()
    {
        browser.mouseOver(closeButton.getWrappedElement());
        closeButton.click();
    }

    public void clickCancelButton()
    {
        browser.waitUntilElementVisible(cancelButton.getWrappedElement());
        cancelButton.click();
    }

    public void clickDeleteButton()
    {
        getBrowser().waitUntilElementClickable(deleteButton.getWrappedElement());
        deleteButton.click();
    }
}
