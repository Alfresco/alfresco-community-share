package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PageObject
public class EditTagDialog extends ShareDialog
{
    @Autowired
    private TagManagerPage tagManagerPage;

    @RenderWebElement
    @FindBy (css = "div[id*='edit-tag-dialogTitle']")
    private WebElement dialogTitle;

    @FindBy (css = "form[id*='edit-tag'] label")
    private WebElement renameLabel;

    @FindBy (css = "form[id*='edit-tag'] div[class='yui-u']")
    private WebElement requiredSymbol;

    @RenderWebElement
    @FindBy (css = "input[id*='edit-tag']")
    private WebElement editTagInputField;

    @RenderWebElement
    @FindBy (css = "button[id*='ok']")
    private WebElement okButton;

    @FindBy (css = "button[id*='tag-cancel']")
    private WebElement cancelButton;

    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public EditTagDialog assertRenameTagLabelIsCorrect()
    {
        LOG.info("Assert Rename Tag label is correct");
        assertEquals(renameLabel.getText(), language.translate("editTag.renameLabel"), "Rename label is");
        return this;
    }

    public EditTagDialog assertRequiredSymbolIsDisplayed()
    {
        LOG.info("Assert required symbol is displayed");
        assertEquals(requiredSymbol.getText(), " *", "Required symbol is displayed");
        return this;
    }

    public EditTagDialog assertOkButtonIsDisplayed()
    {
        LOG.info("Assert Ok button is displayed");
        assertTrue(browser.isElementDisplayed(okButton), "Ok button is displayed");
        return this;
    }

    public EditTagDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    /**
     * Type text in rename tag input field from Edit Tag dialog. Click 'Ok' button
     *
     * @param updatedTag new tag
     */
    public TagManagerPage renameTag(String updatedTag)
    {
        LOG.info(String.format("Rename tag to: %s", updatedTag));
        Utils.clearAndType(editTagInputField, updatedTag);
        okButton.click();
        tagManagerPage.waitUntilMessageDisappears();
        return (TagManagerPage) tagManagerPage.renderedPage();
    }
}