package org.alfresco.po.share.site.accessingExistingSites;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Button;

public abstract class ConfirmationDialog extends ShareDialog2
{
    @RenderWebElement
    protected By confirmDeletionButton = By.id("ALF_CRUD_SERVICE_DELETE_CONFIRMATION_DIALOG_CONFIRM_label");
    protected By dialogMessage = By.cssSelector("div.dialog-body");
    protected By dialogCloseButton = By.cssSelector("span.dijitDialogCloseIcon");

    public String getDialogMessage()
    {
        return getBrowser().waitUntilElementVisible(dialogMessage).getText();
    }

    public void clickOKButton()
    {
        getBrowser().waitUntilElementClickable(confirmDeletionButton).click();
    }

    public boolean isOkButtonDisplayed()
    {
        return getBrowser().waitUntilElementVisible(By.cssSelector("span[class$='dijitButtonContents']")).isDisplayed();
    }

    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogCloseButton);
    }

    public void clickCloseButton()
    {
        getBrowser().findElement(dialogCloseButton).click();
    }
}
