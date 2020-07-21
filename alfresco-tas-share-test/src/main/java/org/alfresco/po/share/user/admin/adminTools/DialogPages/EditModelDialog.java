package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.alfresco.common.Utils.clearAndType;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 11/29/2016.
 */
@PageObject
public class EditModelDialog extends ShareDialog
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

    public EditModelDialog editNamespace(String nameSpace)
    {
        clearAndType(namespaceField, nameSpace);
        return this;
    }

    public EditModelDialog assertNamespaceIs(String expectedNamespace)
    {
        Assert.assertEquals(namespaceField.getAttribute("value"), expectedNamespace, "Namespace text is correct");
        return this;
    }

    public EditModelDialog editPrefix(String prefix)
    {
        clearAndType(prefixField, prefix);
        return this;
    }

    public EditModelDialog assertPrefixIs(String expectedPrefix)
    {
        Assert.assertEquals(prefixField.getAttribute("value"), expectedPrefix, "Prefix text is correct");
        return this;
    }

    public EditModelDialog editCreator(String creator)
    {
        clearAndType(creatorField, creator);
        return this;
    }

    public EditModelDialog assertCreatorIs(String expectedCreator)
    {
        Assert.assertEquals(creatorField.getAttribute("value"), expectedCreator, "Creator text is correct");
        return this;
    }

    public EditModelDialog editDescription(String description)
    {
        clearAndType(descriptionField, description);
        return this;
    }

    public EditModelDialog assertNameFieldIsDisabled()
    {
        Assert.assertEquals(nameField.getAttribute("aria-disabled"), String.valueOf(true), "Name field is disabled");
        return this;
    }

    public ModelManagerPage clickSave()
    {
        browser.waitUntilElementClickable(saveButton).click();
        modelManagerPage.waitForLoadingMessageToDisappear();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public ModelManagerPage clickCancel()
    {
        browser.waitUntilElementClickable(cancelButton).click();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
