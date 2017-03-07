package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.ShareDialog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 12/6/2016.
 */
@PageObject
public class CreateAspectDialogPage extends ShareDialog
{
    @RenderWebElement
    private By createAspectWidow = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG");

    @RenderWebElement
    private By createButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label");

    @RenderWebElement
    private By cancelButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_CANCEL_label");

    @FindBy(xpath ="//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//input[@name='name']")
    private WebElement nameField;

    @FindBy(xpath ="//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//input[@name='title']")
    private WebElement displayLabelField;

    @FindBy(xpath = "//div[@id ='CMM_CREATE_PROPERTYGROUP_DIALOG']//div[@class='control']//textarea")
    private WebElement descriptionField;

    public boolean isCreateAspectWindowDisplayed()
    {
        browser.waitUntilElementVisible(createAspectWidow);
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

    public HtmlPage clickCreateButton(HtmlPage page)
    {
        browser.findElement(createButton).click();
        return page.renderedPage();
    }
}
