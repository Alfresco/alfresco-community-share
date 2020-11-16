package org.alfresco.po.share.searching;

import org.alfresco.po.share.site.accessingExistingSites.ConfirmationDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

public class ConfirmDeletionDialog extends ConfirmationDialog
{
    private By dialogNoButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CANCEL_label");
    @RenderWebElement
    private By dialogTitle = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_title");

    public ConfirmDeletionDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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
