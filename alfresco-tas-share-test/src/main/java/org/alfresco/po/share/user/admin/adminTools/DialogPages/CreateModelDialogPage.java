package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.alfresco.common.Utils.clearAndType;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class CreateModelDialogPage extends ShareDialog
{
    @RenderWebElement
    @FindBy (xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='namespace']")
    private WebElement namespaceField;

    @FindBy (xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='prefix']")
    private WebElement prefixField;

    @RenderWebElement
    @FindBy (xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @FindBy (xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='author']")
    private WebElement creatorField;

    @FindBy (xpath = "//div[@id='CMM_CREATE_MODEL_DIALOG']//textarea[@name='description']")
    private WebElement descriptionField;

    @FindBy (css = "span[widgetid='CMM_CREATE_MODEL_DIALOG_OK']>span")
    private WebElement createButton;

    @Autowired
    private ModelManagerPage modelManagerPage;

    public void sendNamespaceText(String namespace)
    {
        browser.waitUntilElementVisible(namespaceField);
        clearAndType(namespaceField, namespace);
    }

    public void sendPrefixText(String prefix)
    {
        browser.waitUntilElementVisible(prefixField);
        clearAndType(prefixField, prefix);
    }

    public void sendNameText(String name)
    {
        browser.waitUntilElementVisible(nameField);
        clearAndType(nameField, name);
    }

    public void sendCreatorText(String creator)
    {
        browser.waitUntilElementVisible(creatorField);
        clearAndType(creatorField, creator);
    }

    public void sendDescription(String description)
    {
        browser.waitUntilElementVisible(descriptionField);
        clearAndType(descriptionField, description);
    }

    public ModelManagerPage clickCreateButton()
    {
        getBrowser().waitUntilElementVisible(createButton);
        getBrowser().waitUntilElementClickable(createButton).click();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
