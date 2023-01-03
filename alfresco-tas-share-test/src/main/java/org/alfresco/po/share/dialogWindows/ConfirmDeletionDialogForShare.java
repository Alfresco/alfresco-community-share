package org.alfresco.po.share.dialogWindows;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmDeletionDialogForShare extends BaseDialogComponent {
    private final By closeButton = By.cssSelector("span.dijitDialogCloseIcon");
    private final By deleteButton = By.cssSelector("span[id='ALF_DELETE_CONTENT_DIALOG_CONFIRMATION_label']");
    private final By cancelButton = By.cssSelector("span[id='ALF_DELETE_CONTENT_DIALOG_CANCELLATION_label']");

    public ConfirmDeletionDialogForShare(ThreadLocal<WebDriver> webDriver) {
        super(webDriver);
    }

    public void clickCloseButton() {
        waitUntilElementIsVisible(closeButton);
        clickElement(closeButton);
    }

    public void clickCancelButton() {
        waitInSeconds(2);
        clickElement(cancelButton);
    }

    public void clickDeleteButton() {
        waitUntilElementIsVisible(deleteButton);
        clickElement(deleteButton);
    }
}

