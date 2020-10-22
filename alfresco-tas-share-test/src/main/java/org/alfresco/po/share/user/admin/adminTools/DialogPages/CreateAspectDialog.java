package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.common.Utils;
import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class CreateAspectDialog extends ShareDialog
{
    @Autowired
    ModelDetailsPage modelDetailsPage;

    @RenderWebElement
    @FindBy (id = "CMM_CREATE_PROPERTYGROUP_DIALOG")
    private WebElement createAspectWidow;

    @FindBy(id = "CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label")
    private WebElement createButton;

    @RenderWebElement
    @FindBy (id = "CMM_CREATE_PROPERTYGROUP_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @FindBy (css = "#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='name']")
    private WebElement nameField;

    @FindBy (css = "#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='title']")
    private WebElement displayLabelField;

    @FindBy (css = "#CMM_CREATE_PROPERTYGROUP_DIALOG div.control textarea")
    private WebElement descriptionField;

    public CreateAspectDialog assertCreateAspectDialogIsOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(createAspectWidow), "Create aspect dialog is opened");
        return this;
    }

    public CreateAspectDialog typeName(String name)
    {
        Utils.clearAndType(nameField, name);
        return this;
    }

    public CreateAspectDialog typeDisplayLabel(String displayLabel)
    {
        Utils.clearAndType(displayLabelField, displayLabel);
        return this;
    }

    public CreateAspectDialog typeDescription(String description)
    {
        Utils.clearAndType(descriptionField, description);
        return this;
    }

    public ModelDetailsPage clickCreate()
    {
        browser.waitUntilElementClickable(createButton).click();
        modelDetailsPage.waiUntilLoadingMessageDisappears();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }
}
