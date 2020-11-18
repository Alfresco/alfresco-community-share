package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

public class CreateModelDialogPage extends ShareDialog2
{
    @RenderWebElement
    private By namespaceField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='namespace']");
    private By prefixField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='prefix']");
    @RenderWebElement
    private By nameField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='name']");
    private By creatorField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//input[@name='author']");
    private By descriptionField = By.xpath("//div[@id='CMM_CREATE_MODEL_DIALOG']//textarea[@name='description']");
    private By createButton = By.cssSelector("span[widgetid='CMM_CREATE_MODEL_DIALOG_OK']>span");

    public CreateModelDialogPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public void sendNamespaceText(String namespace)
    {
        getBrowser().waitUntilElementVisible(namespaceField);
        clearAndType(namespaceField, namespace);
    }

    public void sendPrefixText(String prefix)
    {
        getBrowser().waitUntilElementVisible(prefixField);
        clearAndType(prefixField, prefix);
    }

    public void sendNameText(String name)
    {
        getBrowser().waitUntilElementVisible(nameField);
        clearAndType(nameField, name);
    }

    public void sendCreatorText(String creator)
    {
        getBrowser().waitUntilElementVisible(creatorField);
        clearAndType(creatorField, creator);
    }

    public void sendDescription(String description)
    {
        getBrowser().waitUntilElementVisible(descriptionField);
        clearAndType(descriptionField, description);
    }

    public ModelManagerPage clickCreateButton()
    {
        getBrowser().waitUntilElementVisible(createButton);
        getBrowser().waitUntilElementClickable(createButton).click();
        return (ModelManagerPage) new ModelManagerPage(browser).renderedPage();
    }
}
