package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;

public class EditModelDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By saveButton = By.id("CMM_EDIT_MODEL_DIALOG_OK_label");
    private final By cancelButton = By.id("CMM_EDIT_MODEL_DIALOG_CANCEL_label");
    @RenderWebElement
    private final By editModelWindow = By.id("CMM_EDIT_MODEL_DIALOG");
    @RenderWebElement
    private final By namespaceField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='namespace']");
    private final By prefixField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='prefix']");
    private final By nameField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='name']");
    private final By creatorField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='author']");
    private final By descriptionField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] textarea[name='description']");

    public EditModelDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public EditModelDialog editNamespace(String nameSpace)
    {
        clearAndType(namespaceField, nameSpace);
        return this;
    }

    public EditModelDialog assertNamespaceIs(String expectedNamespace)
    {
        assertEquals(getBrowser().findElement(namespaceField).getAttribute("value"),
            expectedNamespace, "Namespace text is correct");
        return this;
    }

    public EditModelDialog editPrefix(String prefix)
    {
        clearAndType(prefixField, prefix);
        return this;
    }

    public EditModelDialog assertPrefixIs(String expectedPrefix)
    {
        assertEquals(getBrowser().findElement(prefixField).getAttribute("value"),
            expectedPrefix, "Prefix text is correct");
        return this;
    }

    public EditModelDialog editCreator(String creator)
    {
        clearAndType(creatorField, creator);
        return this;
    }

    public EditModelDialog assertCreatorIs(String expectedCreator)
    {
        assertEquals(getBrowser().findElement(creatorField).getAttribute("value"),
            expectedCreator, "Creator text is correct");
        return this;
    }

    public EditModelDialog editDescription(String description)
    {
        clearAndType(descriptionField, description);
        return this;
    }

    public EditModelDialog assertNameFieldIsDisabled()
    {
        assertEquals(getBrowser().findElement(nameField).getAttribute("aria-disabled"),
            String.valueOf(true), "Name field is disabled");
        return this;
    }

    public ModelManagerPage clickSave()
    {
        getBrowser().waitUntilElementClickable(saveButton).click();
        ModelManagerPage modelManagerPage = new ModelManagerPage(browser);
        modelManagerPage.waiUntilLoadingMessageDisappears();

        return (ModelManagerPage) modelManagerPage.renderedPage();
    }

    public ModelManagerPage clickCancel()
    {
        getBrowser().waitUntilElementClickable(cancelButton).click();
        return (ModelManagerPage) new ModelManagerPage(browser).renderedPage();
    }
}
