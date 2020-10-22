package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    public NewFolderDialog assertDialogTitleEquals(String expectedTitle)
    {
        LOG.info("Assert dialog title is {}", expectedTitle);
        assertEquals(dialogTitle.getText(), expectedTitle, "Dialog title is not correct");
        return this;
    }

    public NewFolderDialog assertSaveButtonIsDisplayed()
    {
        LOG.info("Assert Save button is displayed");
        assertTrue(browser.isElementDisplayed(saveButton), "Save button is not displayed");
        return this;
    }

    public NewFolderDialog assertCancelButtonIsDisplayed()
    {
        LOG.info("Assert Cancel button is displayed");
        assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public NewFolderDialog assertNameInputIsDisplayed()
    {
        LOG.info("Assert name input is displayed");
        assertTrue(browser.isElementDisplayed(nameField), "Name input is not displayed");
        return this;
    }

    public NewFolderDialog assertTitleInputIsDisplayed()
    {
        LOG.info("Assert title input is displayed");
        assertTrue(browser.isElementDisplayed(titleField), "Title input is not displayed");
        return this;
    }

    public NewFolderDialog assertDescriptionInputIsDisplayed()
    {
        LOG.info("Assert description input is displayed");
        assertTrue(browser.isElementDisplayed(descriptionField), "Description input is not displayed");
        return this;
    }

    public NewFolderDialog assertMandatoryIndicatorForNameIsDisplayed()
    {
        LOG.info("Assert mandatory indicator is displayed");
        assertTrue(browser.isElementDisplayed(mandatoryIndicator), "Mandatory indicator for name input is displayed");
        return this;
    }

    public void clickSave()
    {
        LOG.info("Click Save");
        getBrowser().waitUntilElementClickable(saveButton).click();
        waitUntilMessageDisappears();
    }

    public void clickCancel()
    {
        LOG.info("Click Cancel");
        cancelButton.click();
    }

    public NewFolderDialog typeName(String name)
    {
        LOG.info("Type name {}", name);
        Utils.clearAndType(nameField, name);
        return this;
    }

    public NewFolderDialog fillInDetails(String name, String title, String description)
    {
        LOG.info("Set name, title and description");
        clearAndType(nameField, name);
        clearAndType(titleField, title);
        clearAndType(descriptionField, description);
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

    public NewFolderDialog assertNameInputIsInvalid()
    {
        LOG.info("Assert name input is invalid");
        browser.waitUntilElementHasAttribute(nameField, "class", "invalid");
        assertTrue(nameField.getAttribute("class").contains("invalid"),  "Name field is invalid");
        return this;
    }

    public NewFolderDialog assertNameInputContainsIllegalCharactersMessageIsDisplayed()
    {
        LOG.info("Assert name contains illegal characters message is displayed");
        assertTrue(nameField.getAttribute("title").equals(language.translate("newFolderDialog.invalidName")));
        return this;
    }
}