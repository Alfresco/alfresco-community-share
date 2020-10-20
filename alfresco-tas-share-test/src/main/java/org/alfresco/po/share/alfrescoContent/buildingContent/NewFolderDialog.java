package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;
import org.testng.Assert;

@PageObject
public class NewFolderDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "div[id$='_default-createFolder-dialogTitle']")
    protected WebElement dialogTitle;

    @RenderWebElement
    @FindBy (css = "button[id$='submit-button']")
    private WebElement saveButton;

    @FindBy (css = "button[id$='form-cancel-button']")
    private WebElement cancelButton;

    @RenderWebElement
    @FindBy (css = "input[name='prop_cm_name']")
    private WebElement nameField;

    @FindBy (css = "input[name='prop_cm_title']")
    private WebElement titleField;

    @FindBy (css = "textarea[name='prop_cm_description']")
    private WebElement descriptionField;

    @FindBy (css = "label[for$='createFolder_prop_cm_name'] .mandatory-indicator")
    private WebElement mandatoryIndicator;

    public NewFolderDialog assertDialogTitleIs(String expectedTitle)
    {
        Assert.assertEquals(dialogTitle.getText(), expectedTitle, "Dialog title is correct");
        return this;
    }

    public NewFolderDialog assertSaveButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(saveButton), "Save button is displayed");
        return this;
    }

    public NewFolderDialog assertCancelButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public NewFolderDialog assertNameInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(nameField), "Name input is displayed");
        return this;
    }

    public NewFolderDialog assertTitleInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(titleField), "Title input is displayed");
        return this;
    }

    public NewFolderDialog assertDescriptionInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(descriptionField), "Description input is displayed");
        return this;
    }

    public NewFolderDialog assertMandatoryIndicatorForNameIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(mandatoryIndicator), "Mandatory indicator for name input is displayed");
        return this;
    }

    public void clickSave()
    {
        getBrowser().waitUntilElementClickable(saveButton).click();
        waitUntilMessageDisappears();
    }

    public void clickCancel()
    {
        cancelButton.click();
    }

    public NewFolderDialog typeName(String name)
    {
        Utils.clearAndType(nameField, name);
        return this;
    }

    public NewFolderDialog fillInDetails(String name, String title, String description)
    {
        Utils.clearAndType(nameField, name);
        Utils.clearAndType(titleField, title);
        Utils.clearAndType(descriptionField, description);
        return this;
    }

    public String getNameFieldValue()
    {
        return nameField.getAttribute("value");
    }

    public boolean isNewFolderPopupDisplayed()
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals("New Folder");
    }

    public boolean isNameMarkedAsInvalid()
    {
        try
        {
            browser.waitUntilElementHasAttribute(nameField, "class", "invalid");
            return true;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public NewFolderDialog assertNameIsInvalid()
    {
        browser.waitUntilElementHasAttribute(nameField, "class", "invalid");
        Assert.assertTrue(nameField.getAttribute("class").contains("invalid"),  "Name field is invalid");
        return this;
    }

    public NewFolderDialog assertNameContainsIllegalCharactersMessageIsDisplayed()
    {
        Assert.assertTrue(nameField.getAttribute("title").equals(language.translate("newFolderDialog.invalidName")));
        return this;
    }
}