package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateAspectDialog extends BaseDialogComponent
{
    private final By createAspectWidow = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG");
    private final By createButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label");
    private final By nameField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='name']");
    private final By displayLabelField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='title']");
    private final By descriptionField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG div.control textarea");

    public CreateAspectDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public CreateAspectDialog assertCreateAspectDialogIsOpened()
    {
        assertTrue(webElementInteraction.isElementDisplayed(createAspectWidow), "Create aspect dialog is opened");
        return this;
    }

    public CreateAspectDialog typeName(String name)
    {
        webElementInteraction.clearAndType(nameField, name);
        return this;
    }

    public CreateAspectDialog typeDisplayLabel(String displayLabel)
    {
        webElementInteraction.clearAndType(displayLabelField, displayLabel);
        return this;
    }

    public CreateAspectDialog typeDescription(String description)
    {
        webElementInteraction.clearAndType(descriptionField, description);
        return this;
    }

    public ModelDetailsPage clickCreate()
    {
        webElementInteraction.waitUntilElementIsVisible(createButton);
        webElementInteraction.clickElement(createButton);
        ModelDetailsPage modelDetailsPage = new ModelDetailsPage(webDriver);
        modelDetailsPage.waitUntilLoadingMessageDisappears();

        return modelDetailsPage;
    }
}
