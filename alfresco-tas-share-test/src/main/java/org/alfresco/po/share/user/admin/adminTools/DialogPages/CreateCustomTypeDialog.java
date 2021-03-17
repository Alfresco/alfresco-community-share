package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CreateCustomTypeDialog extends BaseDialogComponent
{
    private final By dialogBody = By.id("CMM_CREATE_TYPE_DIALOG");
    private final By createButton = By.id("CMM_CREATE_TYPE_DIALOG_OK");
    private final By cancelButton = By.id("CMM_CREATE_TYPE_DIALOG_CANCEL_label");
    private final By nameField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='name']");
    private final By displayLabelField = By.xpath("//div[@id ='CMM_CREATE_TYPE_DIALOG']//input[@name='title']");
    private final By parentTypeArrow = By.cssSelector("td[class$='dijitArrowButtonContainer'] > input");
    private final By parentTypeElements = By.cssSelector("table[role='listbox'] > tbody > tr[id^='dijit_MenuItem']");

    public CreateCustomTypeDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ModelDetailsPage clickCreate()
    {
        clickElement(createButton);
        waitUntilElementDisappears(dialogBody);
        return new ModelDetailsPage(webDriver);
    }

    public CreateCustomTypeDialog selectParentType(String parentType)
    {
        waitUntilElementIsVisible(parentTypeArrow);
        clickElement(parentTypeArrow);
        for (WebElement type : findElements(parentTypeElements))
        {
            if (type.getAttribute("aria-label").equals(parentType))
            {
                clickElement(type);
            }
        }
        return this;
    }

    public void clickCancelButton()
    {
        clickElement(cancelButton);
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
}
