package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 11/29/2016.
 */
@PageObject
public class EditModelDialogPage extends ShareDialog
{
    @RenderWebElement
    private By saveButton = By.id("CMM_EDIT_MODEL_DIALOG_OK_label");

    @RenderWebElement
    private By cancelButton = By.id("CMM_EDIT_MODEL_DIALOG_CANCEL_label");

    @RenderWebElement
    private By editModelWindow = By.id("CMM_EDIT_MODEL_DIALOG");

    @RenderWebElement
    @FindBy (xpath = "//div[@id='CMM_EDIT_MODEL_DIALOG']//input[@name='namespace']")
    private WebElement namespaceField;

    @FindBy (xpath = "//div[@id='CMM_EDIT_MODEL_DIALOG']//input[@name='prefix']")
    private WebElement prefixField;

    @RenderWebElement
    @FindBy (xpath = "//div[@id='CMM_EDIT_MODEL_DIALOG']//input[@name='name']")
    private WebElement nameField;


    @FindBy (xpath = "//div[@id='CMM_EDIT_MODEL_DIALOG']//input[@name='author']")
    private WebElement creatorField;

    @FindBy (xpath = "//div[@id='CMM_EDIT_MODEL_DIALOG']//textarea[@name='description']")
    private WebElement descriptionField;

    public void editNamespace(String nameSpace)
    {
        namespaceField.clear();
        namespaceField.sendKeys(nameSpace);
    }

    public void editPrefix(String prefix)
    {
        prefixField.clear();
        prefixField.sendKeys(prefix);
    }

    public void editCreator(String creator)
    {
        creatorField.clear();
        creatorField.sendKeys(creator);
    }

    public void editDescription(String description)
    {
        descriptionField.clear();
        descriptionField.sendKeys(description);
    }

    public String getNameFieldStatus()
    {
        return nameField.getAttribute("aria-disabled");
    }

    public boolean isEditModelDialogDisplayed()
    {
        return browser.isElementDisplayed(editModelWindow);
    }

    public void clickSaveButton()
    {
        browser.findElement(saveButton).click();
    }

    public void clickCancelButton()
    {
        browser.findElement(cancelButton).click();
    }
}
