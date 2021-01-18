package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditModelDialog extends BaseDialogComponent
{
    private final By saveButton = By.id("CMM_EDIT_MODEL_DIALOG_OK_label");
    private final By cancelButton = By.id("CMM_EDIT_MODEL_DIALOG_CANCEL_label");
    private final By namespaceField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='namespace']");
    private final By prefixField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='prefix']");
    private final By nameField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='name']");
    private final By creatorField = By.cssSelector("div[id='CMM_EDIT_MODEL_DIALOG'] input[name='author']");

    public EditModelDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public EditModelDialog editNamespace(String nameSpace)
    {
        webElementInteraction.clearAndType(namespaceField, nameSpace);
        return this;
    }

    public EditModelDialog assertNamespaceIs(String expectedNamespace)
    {
        assertEquals(webElementInteraction.findElement(namespaceField).getAttribute("value"),
            expectedNamespace, "Namespace text is correct");
        return this;
    }

    public EditModelDialog editPrefix(String prefix)
    {
        webElementInteraction.clearAndType(prefixField, prefix);
        return this;
    }

    public EditModelDialog assertPrefixIs(String expectedPrefix)
    {
        assertEquals(webElementInteraction.findElement(prefixField).getAttribute("value"),
            expectedPrefix, "Prefix text is correct");
        return this;
    }

    public EditModelDialog editCreator(String creator)
    {
        webElementInteraction.clearAndType(creatorField, creator);
        return this;
    }

    public EditModelDialog assertCreatorIs(String expectedCreator)
    {
        assertEquals(webElementInteraction.findElement(creatorField).getAttribute("value"),
            expectedCreator, "Creator text is correct");
        return this;
    }

    public EditModelDialog assertNameFieldIsDisabled()
    {
        assertEquals(webElementInteraction.findElement(nameField).getAttribute("aria-disabled"),
            String.valueOf(true), "Name field is disabled");
        return this;
    }

    public ModelManagerPage clickSave()
    {
        webElementInteraction.waitUntilElementIsVisible(saveButton);
        webElementInteraction.clickElement(saveButton);
        ModelManagerPage modelManagerPage = new ModelManagerPage(webDriver);
        modelManagerPage.waitUntilLoadingMessageDisappears();

        return new ModelManagerPage(webDriver);
    }

    public ModelManagerPage clickCancel()
    {
        webElementInteraction.waitUntilElementIsVisible(cancelButton);
        webElementInteraction.clickElement(cancelButton);
        return new ModelManagerPage(webDriver);
    }
}
