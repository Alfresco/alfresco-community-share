package org.alfresco.po.share.searching;

import org.alfresco.po.share.site.accessingExistingSites.ConfirmationDialog;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class ConfirmDeletionDialog extends ConfirmationDialog
{
    private final By dialogNoButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CANCEL_label");
    @RenderWebElement
    private final By dialogTitle = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_title");

    public ConfirmDeletionDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickNoButton()
    {
        getBrowser().findElement(dialogNoButton).click();
    }
}
