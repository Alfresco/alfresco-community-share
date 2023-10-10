package org.alfresco.po.share.user.admin.adminTools;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class EditTagDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("div[id*='edit-tag-dialogTitle']");
    private final By renameLabel = By.cssSelector("form[id*='edit-tag'] label");
    private final By requiredSymbol = By.cssSelector("form[id*='edit-tag'] div[class='yui-u']");
    private final By editTagInputField = By.cssSelector("input[name='name']");
    private final By okButton = By.cssSelector("button[id*='ok']");
    private final By cancelButton = By.cssSelector("button[id*='tag-cancel']");

    public EditTagDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public EditTagDialog assertRenameTagLabelIsCorrect()
    {
        log.info("Assert Rename Tag label is correct");
        assertEquals(getElementText(renameLabel), language.translate("editTag.renameLabel"), "Rename label is");
        return this;
    }

    public EditTagDialog assertRequiredSymbolIsDisplayed()
    {
        log.info("Assert required symbol is displayed");
        assertEquals(getElementText(requiredSymbol), " *", "Required symbol is displayed");
        return this;
    }

    public EditTagDialog assertOkButtonIsDisplayed()
    {
        log.info("Assert Ok button is displayed");
        assertTrue(isElementDisplayed(okButton), "Ok button is displayed");
        return this;
    }

    public EditTagDialog assertCancelButtonIsDisplayed()
    {
        log.info("Assert Cancel button is displayed");
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public TagManagerPage renameTag(String updatedTag)
    {
        log.info("Rename tag to {}", updatedTag);
        clearAndType(editTagInputField, updatedTag);
        WebElement ok = waitUntilElementIsVisible(okButton);
        mouseOver(ok);
        clickElement(ok);
        waitUntilNotificationMessageDisappears();

        return new TagManagerPage(webDriver);
    }
}