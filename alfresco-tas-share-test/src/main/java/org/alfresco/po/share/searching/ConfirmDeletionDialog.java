package org.alfresco.po.share.searching;

import org.alfresco.po.share.site.accessingExistingSites.ConfirmationDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmDeletionDialog extends ConfirmationDialog
{
    private final By dialogNoButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CANCEL_label");
    private final By dialogTitle = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_title");
    private final By confirmDeletion = By.xpath("(//span[text()='Yes'])[19]");

    public ConfirmDeletionDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickNoButton()
    {
        clickElement(dialogNoButton);
    }
    public ConfirmationDialog clickYesButton()
    {
        clickElement(confirmDeletion);
        return this;
    }
}
