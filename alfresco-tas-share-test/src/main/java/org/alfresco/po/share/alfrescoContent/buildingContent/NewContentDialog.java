package org.alfresco.po.share.alfrescoContent.buildingContent;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Laura.Capsa
 */
@PageObject
public class NewContentDialog extends ShareDialog
{
    @FindBy(css = "button[id*='submit']")
    private WebElement saveButton;

    @FindBy(css = "button[id*='form-cancel']")
    private WebElement cancelButton;

    @RenderWebElement
    @FindBy(css = ".form-field input[id*='name']")
    private WebElement nameField;

    @FindBy(css = ".form-field input[id*='title']")
    private WebElement titleField;

    @FindBy(css = ".form-field textarea[id*='description']")
    private WebElement descriptionField;

    @FindBy(css = "label .mandatory-indicator")
    private WebElement mandatoryIndicator;

    public boolean isSaveButttonDisplayed()
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
        saveButton.click();
    }

    public void clickCancelButton()
    {
        cancelButton.click();
    }

    public void fillInNameField(String name)
    {
        nameField.sendKeys(name);
    }

    public void fillInDetails(String name, String title, String description)
    {
        fillInNameField(name);
        titleField.sendKeys(title);
        descriptionField.sendKeys(description);
    }
}