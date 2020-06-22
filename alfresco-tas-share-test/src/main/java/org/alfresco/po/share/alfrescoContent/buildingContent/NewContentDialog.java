package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Primary;

/**
 * @author Laura.Capsa
 */
@PageObject
@Primary
public class NewContentDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "div[id*='_default-createFolder-dialogTitle']")
    protected WebElement dialogTitle;

    @RenderWebElement
    @FindBy (css = "button[id*='submit']")
    private WebElement saveButton;

    @FindBy (css = "button[id*='form-cancel']")
    private WebElement cancelButton;

    @FindBy (css = ".form-field input[id*='name']")
    private WebElement nameField;

    @FindBy (css = ".form-field input[id*='title']")
    private WebElement titleField;

    @FindBy (css = ".form-field textarea[id*='description']")
    private WebElement descriptionField;

    @FindBy (css = "label .mandatory-indicator")
    private WebElement mandatoryIndicator;

    @FindBy (css = ".text div")
    private WebElement tooltipErrorMessage;

    public boolean isSaveButtonDisplayed()
    {
        browser.waitUntilElementClickable(saveButton, 40);
        return browser.isElementDisplayed(saveButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public boolean isNameFieldDisplayed()
    {
        browser.waitUntilElementClickable(nameField, 40);
        return browser.isElementDisplayed(nameField);
    }

    public boolean isTitleFieldDisplayed()
    {
        return browser.isElementDisplayed(titleField);
    }

    public boolean isDescriptionFieldDisplayed()
    {
        return browser.isElementDisplayed(descriptionField);
    }

    public boolean isMandatoryIndicatorDisplayed()
    {
        return browser.isElementDisplayed(mandatoryIndicator);
    }

    public void clickSaveButton()
    {
        getBrowser().waitUntilElementClickable(saveButton).click();
        waitUntilMessageDisappears();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public void fillInNameField(String name)
    {
        Utils.clearAndType(nameField, name);
    }

    public void fillInDetails(String name, String title, String description)
    {
        fillInNameField(name);
        Utils.clearAndType(titleField, title);
        Utils.clearAndType(descriptionField, description);
    }

    public String getNameFieldValue()
    {
        return nameField.getAttribute("value");
    }

    public boolean isNewFolderPopupDisplayed()
    {
        return browser.isElementDisplayed(dialogTitle) && dialogTitle.getText().equals("New Folder");
    }

    public boolean isTooltipErrorMessageDisplayed()
    {
        return browser.isElementDisplayed(tooltipErrorMessage);
    }
}