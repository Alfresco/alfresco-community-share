package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class ChangeContentTypeDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id*='changeType-dialogTitle']");
    @RenderWebElement
    private final By typeDropdown = By.cssSelector("select[id*='changeType']");
    private final By mandatory = By.cssSelector("form div[class='yui-u']");
    private final By okButton = By.cssSelector("button[id$='changeType-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='changeType-cancel-button']");
    private final By dialogBody = By.cssSelector("div[id$='changeType-dialog']");

    public ChangeContentTypeDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public boolean isDialogDisplayed()
    {
        return getBrowser().isElementDisplayed(dialogBody);
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
