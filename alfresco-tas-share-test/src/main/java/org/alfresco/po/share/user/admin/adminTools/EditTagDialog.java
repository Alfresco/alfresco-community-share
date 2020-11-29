package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class EditTagDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By dialogTitle = By.cssSelector("div[id*='edit-tag-dialogTitle']");
    private final By renameLabel = By.cssSelector("form[id*='edit-tag'] label");
    private final By requiredSymbol = By.cssSelector("form[id*='edit-tag'] div[class='yui-u']");
    @RenderWebElement
    private final By editTagInputField = By.cssSelector("input[name='name']");
    private final By okButton = By.cssSelector("button[id*='ok']");
    private final By cancelButton = By.cssSelector("button[id*='tag-cancel']");

    public EditTagDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public String getDialogTitle()
    {
        return getElementText(dialogTitle);
    }

    public EditTagDialog assertRenameTagLabelIsCorrect()
    {
        LOG.info("Assert Rename Tag label is correct");
        assertEquals(getElementText(renameLabel), language.translate("editTag.renameLabel"), "Rename label is");
        return this;
    }

    public EditTagDialog assertRequiredSymbolIsDisplayed()
    {
        LOG.info("Assert required symbol is displayed");
        assertEquals(getElementText(requiredSymbol), " *", "Required symbol is displayed");
        return this;
    }

    public EditTagDialog assertOkButtonIsDisplayed()
    {
        LOG.info("Assert Ok button is displayed");
        assertTrue(getBrowser().isElementDisplayed(okButton), "Ok button is displayed");
        return this;
    }

    public EditTagDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public TagManagerPage renameTag(String updatedTag)
    {
        LOG.info(String.format("Rename tag to: %s", updatedTag));
        clearAndType(editTagInputField, updatedTag);
        WebElement ok = getBrowser().waitUntilElementVisible(okButton);
        getBrowser().mouseOver(ok);
        getBrowser().waitUntilElementClickable(ok).click();
        waitUntilNotificationMessageDisappears();
        LOG.error("Message for rename tag (for travis debug) {}", notificationMessageThread.get());
        return (TagManagerPage) new TagManagerPage(browser).renderedPage();
    }
}