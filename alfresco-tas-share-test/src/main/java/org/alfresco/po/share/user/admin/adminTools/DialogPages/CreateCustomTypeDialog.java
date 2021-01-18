package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateCustomTypeDialog extends BaseDialogComponent
{
    private final By createButton = By.id("CMM_CREATE_TYPE_DIALOG_OK");
    private final By cancelButton = By.id("CMM_CREATE_TYPE_DIALOG_CANCEL_label");
    private final By nameField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='name']");
    private final By displayLabelField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='title']");
    private final By parentTypeElement = By.cssSelector(".dijitSelectLabel");
    private final By parentTypeElements = By.cssSelector("table[role='listbox'] > tbody > tr[id^='dijit_MenuItem']");

    public CreateCustomTypeDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ModelDetailsPage clickCreate()
    {
        webElementInteraction.clickElement(createButton);
        ModelDetailsPage modelDetailsPage = new ModelDetailsPage(webDriver);
        modelDetailsPage.waitUntilLoadingMessageDisappears();
        return modelDetailsPage;
    }

    public CreateCustomTypeDialog selectParentType(String parentType)
    {
        webElementInteraction.clickElement(parentTypeElement, 1000);
        for (WebElement type : webElementInteraction.findElements(parentTypeElements))
        {
            if (type.getAttribute("aria-label").equals(parentType))
            {
                webElementInteraction.clickElement(type);
            }
        }
        return this;
    }

    public void clickCancelButton()
    {
        webElementInteraction.clickElement(cancelButton);
    }

    public CreateCustomTypeDialog typeName(String name)
    {
        webElementInteraction.clearAndType(nameField, name);
        return this;
    }

    public CreateCustomTypeDialog typeDisplayLabel(String displayLabel)
    {
        webElementInteraction.clearAndType(displayLabelField, displayLabel);
        return this;
    }
}
