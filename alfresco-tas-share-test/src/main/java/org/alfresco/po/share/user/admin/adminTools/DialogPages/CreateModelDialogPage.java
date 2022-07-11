package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateModelDialogPage extends BaseDialogComponent
{
    private final By namespaceField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='namespace']");
    private final By prefixField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='prefix']");
    private final By nameField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='name']");
    private final By createButton = By.cssSelector("span[widgetid='CMM_CREATE_MODEL_DIALOG_OK']>span");

    public CreateModelDialogPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void sendNamespaceText(String namespace)
    {
        waitUntilElementIsVisible(namespaceField);
        clearAndType(namespaceField, namespace);
    }

    public void sendPrefixText(String prefix)
    {
        waitUntilElementIsVisible(prefixField);
        clearAndType(prefixField, prefix);
    }

    public void sendNameText(String name)
    {
        waitUntilElementIsVisible(nameField);
        clearAndType(nameField, name);
    }

    public ModelManagerPage clickCreateButton()
    {
        waitUntilElementIsVisible(createButton);
        clickElement(createButton);
        return new ModelManagerPage(webDriver);
    }
}
