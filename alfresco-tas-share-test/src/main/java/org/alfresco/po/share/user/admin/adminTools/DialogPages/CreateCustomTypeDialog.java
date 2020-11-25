package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertTrue;

public class CreateCustomTypeDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By createCustomTypeWindow = By.id("CMM_CREATE_TYPE_DIALOG");
    @RenderWebElement
    private final By createButton = By.cssSelector("span[widgetid='CMM_CREATE_TYPE_DIALOG_OK']>span");
    @RenderWebElement
    private final By cancelButton = By.id("CMM_CREATE_TYPE_DIALOG_CANCEL_label");
    private final By nameField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='name']");
    private final By displayLabelField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='title']");
    private final By descriptionField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//div[@class='control']//textarea");
    private final By parentTypeElement = By.cssSelector(".dijitSelectLabel");
    private final By parentTypeElements = By.cssSelector("table[role='listbox'] > tbody > tr[id^='dijit_MenuItem']");

    public CreateCustomTypeDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public ModelDetailsPage clickCreate()
    {
        getBrowser().waitUntilElementClickable(createButton).click();
        ModelDetailsPage modelDetailsPage = new ModelDetailsPage(browser);
        modelDetailsPage.waiUntilLoadingMessageDisappears();
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }

    public void selectParentType(String parentType)
    {
        getBrowser().waitUntilElementVisible(parentTypeElement).click();
        for (WebElement type : getBrowser().findElements(parentTypeElements))
        {
            if (type.getAttribute("aria-label").equals(parentType))
            {
                getBrowser().clickJS(type);
            }
        }
    }

    public void clickCancelButton()
    {
        getBrowser().findElement(cancelButton).click();
    }

    public CreateCustomTypeDialog typeName(String name)
    {
        clearAndType(nameField, name);
        return this;
    }

    public CreateCustomTypeDialog typeDisplayLabel(String displayLabel)
    {
        clearAndType(displayLabelField, displayLabel);
        return this;
    }

    public void sendDescriptionFieldInput(String description)
    {
        clearAndType(descriptionField, description);
    }

    public CreateCustomTypeDialog assertCreateCustomTypeWindowDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(createCustomTypeWindow), "Create custom type button is displayed");
        return this;
    }
}
