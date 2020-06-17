package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.alfresco.common.Utils.clearAndType;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 11/29/2016.
 */
@PageObject
public class EditModelDialogPage extends ShareDialog
{
    @RenderWebElement
    @FindBy(id = "CMM_EDIT_MODEL_DIALOG_OK_label")
    private WebElement saveButton;

    @FindBy(id =  "CMM_EDIT_MODEL_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @RenderWebElement
    @FindBy(id = "CMM_EDIT_MODEL_DIALOG")
    private WebElement editModelWindow;

    @RenderWebElement
    @FindBy (css = "div[id='CMM_EDIT_MODEL_DIALOG'] input[name='namespace']")
    private WebElement namespaceField;

    @FindBy (css = "div[id='CMM_EDIT_MODEL_DIALOG'] input[name='prefix']")
    private WebElement prefixField;

    @RenderWebElement
    @FindBy (css = "div[id='CMM_EDIT_MODEL_DIALOG'] input[name='name']")
    private WebElement nameField;

    @FindBy (css = "div[id='CMM_EDIT_MODEL_DIALOG'] input[name='author']")
    private WebElement creatorField;

    @FindBy (css = "div[id='CMM_EDIT_MODEL_DIALOG'] textarea[name='description']")
    private WebElement descriptionField;

    @Autowired
    private ModelManagerPage modelManagerPage;

    public void editNamespace(String nameSpace)
    {
        clearAndType(namespaceField, nameSpace);
    }

    public void editPrefix(String prefix)
    {
        clearAndType(prefixField, prefix);
    }

    public void editCreator(String creator)
    {
        clearAndType(creatorField, creator);
    }

    public void editDescription(String description)
    {
        clearAndType(descriptionField, description);
    }

    public String getNameFieldStatus()
    {
        return nameField.getAttribute("aria-disabled");
    }

    public boolean isEditModelDialogDisplayed()
    {
        return browser.isElementDisplayed(editModelWindow);
    }

    public ModelManagerPage clickSaveButton()
    {
        browser.waitUntilElementClickable(saveButton).click();
        modelManagerPage.refresh();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public ModelManagerPage clickCancelButton()
    {
        browser.waitUntilElementClickable(cancelButton).click();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
