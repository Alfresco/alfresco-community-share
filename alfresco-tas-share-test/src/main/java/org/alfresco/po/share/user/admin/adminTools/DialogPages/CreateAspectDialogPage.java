package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelDetailsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class CreateAspectDialogPage extends ShareDialog
{
    @Autowired
    ModelDetailsPage modelDetailsPage;

    @RenderWebElement
    @FindBy (id = "CMM_CREATE_PROPERTYGROUP_DIALOG")
    private WebElement createAspectWidow;

    private By createButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label");

    @RenderWebElement
    @FindBy (id = "CMM_CREATE_PROPERTYGROUP_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//input[@name='title']")
    private WebElement displayLabelField;

    @FindBy (xpath = "//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//div[@class='control']//textarea")
    private WebElement descriptionField;

    public boolean isCreateAspectWindowDisplayed()
    {
        return browser.isElementDisplayed(createAspectWidow);
    }

    public void sendNameInput(String name)
    {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void sendDisplayLabelInput(String displayLabel)
    {
        displayLabelField.clear();
        displayLabelField.sendKeys(displayLabel);
    }

    public void sendDescriptionFieldInput(String description)
    {
        descriptionField.clear();
        descriptionField.sendKeys(description);
    }

    public ModelDetailsPage clickCreateButton()
    {
        browser.findElement(createButton).click();
        browser.waitInSeconds(2);
        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }
}
