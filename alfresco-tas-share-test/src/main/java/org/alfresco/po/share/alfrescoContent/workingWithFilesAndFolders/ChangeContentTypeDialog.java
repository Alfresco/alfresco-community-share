package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.alfrescoContent.document.DocumentDetailsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ChangeContentTypeDialog extends ShareDialog2
{
    private By dialogTitle = By.cssSelector("div[id*='changeType-dialogTitle']");
    @RenderWebElement
    private By typeDropdown = By.cssSelector("select[id*='changeType']");
    private By mandatory = By.cssSelector("form div[class='yui-u']");
    private By okButton = By.cssSelector("button[id$='changeType-ok-button']");
    private By cancelButton = By.cssSelector("button[id$='changeType-cancel-button']");

    public ChangeContentTypeDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancelButton()
    {
        getBrowser().waitUntilElementClickable(cancelButton).click();
        getBrowser().waitUntilElementDisappears(dialogTitle);
    }

    public ChangeContentTypeDialog selectOption(String optionName)
    {
        Select dropdown = new Select(getBrowser().findElement(typeDropdown));
        dropdown.selectByVisibleText(optionName);
        return this;
    }

    public boolean isDropdownMandatory()
    {
        return getBrowser().isElementDisplayed(mandatory);
    }
}
