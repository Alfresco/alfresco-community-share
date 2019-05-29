package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

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
        // return browser.isElementDisplayed(browser.waitUntilElementVisible(editTagInputField));
    }

    public String getRenameLabel()
    {
        return renameLabel.getText();
    }

    public String getRequiredSymbol()
    {
        return requiredSymbol.getText();
    }

    public boolean isOkButtonDisplayed()
    {
        return browser.isElementDisplayed(okButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    /**
     * Type text in rename tag input field from Edit Tag dialog. Click 'Ok' button
     *
     * @param updatedTag new tag
     */
    public void renameTag(String updatedTag)
    {
        editTagInputField.clear();
        editTagInputField.sendKeys(updatedTag);
        okButton.click();
        browser.waitInSeconds(5); //due to "Tag updated - edited tags may disappear temporarily from search results while re-indexing occurs."
    }
}