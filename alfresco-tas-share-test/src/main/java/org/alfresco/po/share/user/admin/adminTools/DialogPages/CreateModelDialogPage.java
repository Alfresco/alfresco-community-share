package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class CreateModelDialogPage extends ShareDialog
{
    @RenderWebElement
    @FindBy(xpath ="//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='namespace']")
    private WebElement namespaceField;

    @FindBy(xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='prefix']")
    private WebElement prefixField;

    @RenderWebElement
    @FindBy(xpath ="//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @RenderWebElement
    @FindBy(id="CMM_CREATE_MODEL_DIALOG")
    private WebElement createModelDialog;

    @FindBy(xpath ="//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='author']")
    private WebElement creatorField;

    @FindBy(xpath ="//div[@id='CMM_CREATE_MODEL_DIALOG']//textarea[@name='description']")
    private WebElement descriptionField;

    @RenderWebElement
    private By createButton = By.id("CMM_CREATE_MODEL_DIALOG_OK_label");

    private By cancelButton = By.id("CMM_CREATE_MODEL_DIALOG_CANCEL_label");
    private By closeWindowButton = By.cssSelector("div[id='CMM_CREATE_MODEL_DIALOG'] .dijitDialogCloseIcon");

    public void sendNamespaceText(String namespace)
    {
        browser.waitUntilElementVisible(namespaceField);
        namespaceField.clear();
        namespaceField.sendKeys(namespace);
    }

    public void sendPrefixText(String prefix)
    {
        browser.waitUntilElementVisible(prefixField);
        prefixField.clear();
        prefixField.sendKeys(prefix);
    }

    public void sendNameText(String name)
    {
        browser.waitUntilElementVisible(nameField);
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void sendCreatorText(String creator)
    {
        browser.waitUntilElementVisible(creatorField);
        creatorField.clear();
        creatorField.sendKeys(creator);
    }

    public void sendDescription(String description)
    {
        browser.waitUntilElementVisible(descriptionField);
        descriptionField.clear();
        descriptionField.sendKeys(description);
    }

    public void clickCreateButton()
    {
        browser.waitUntilElementClickable(createButton, 6);
        browser.findElement(createButton).click();
    }
}
