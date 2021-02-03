package org.alfresco.po.share.alfrescoContent.workingWithFilesAndFolders;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

@Slf4j
public class ChangeContentTypeDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id*='changeType-dialogTitle']");
    private final By typeDropdown = By.cssSelector("select[id*='changeType']");
    private final By mandatory = By.cssSelector("form div[class='yui-u']");
    private final By okButton = By.cssSelector("button[id$='changeType-ok-button']");
    private final By cancelButton = By.cssSelector("button[id$='changeType-cancel-button']");
    private final By dialogBody = By.cssSelector("div[id$='changeType-dialog']");

    public ChangeContentTypeDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return webElementInteraction.getElementText(dialogTitle);
    }

    public boolean isDialogDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dialogBody);
    }

    public void clickOkButton()
    {
        WebElement ok = webElementInteraction.waitUntilElementIsVisible(okButton);
        webElementInteraction.mouseOver(ok);
        webElementInteraction.clickElement(ok);
        waitUntilNotificationMessageDisappears();
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
        webElementInteraction.waitUntilElementDisappears(dialogTitle);
    }

    public ChangeContentTypeDialog selectOption(String optionName)
    {
        log.info("Select option {}", optionName);
        webElementInteraction.waitInSeconds(1);
        Select dropdown = new Select(webElementInteraction.waitUntilElementIsVisible(typeDropdown));
        dropdown.selectByVisibleText(optionName);

        return this;
    }

    public boolean isDropdownMandatory()
    {
        return webElementInteraction.isElementDisplayed(mandatory);
    }
}
