package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class CreateAspectDialog extends ShareDialog2
{
    @RenderWebElement
    private By createAspectWidow = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG");
    private By createButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label");
    @RenderWebElement
    private By cancelButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_CANCEL_label");
    private By nameField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='name']");
    private By displayLabelField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='title']");
    private By descriptionField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG div.control textarea");

    public CreateAspectDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public CreateAspectDialog assertCreateAspectDialogIsOpened()
    {
        assertTrue(getBrowser().isElementDisplayed(createAspectWidow), "Create aspect dialog is opened");
        return this;
    }

    public CreateAspectDialog typeName(String name)
    {
        clearAndType(nameField, name);
        return this;
    }

    public CreateAspectDialog typeDisplayLabel(String displayLabel)
    {
        clearAndType(displayLabelField, displayLabel);
        return this;
    }

    public CreateAspectDialog typeDescription(String description)
    {
        clearAndType(descriptionField, description);
        return this;
    }

    public ModelDetailsPage clickCreate()
    {
        getBrowser().waitUntilElementClickable(createButton).click();
        ModelDetailsPage modelDetailsPage = new ModelDetailsPage(browser);
        modelDetailsPage.waiUntilLoadingMessageDisappears();

        return (ModelDetailsPage) modelDetailsPage.renderedPage();
    }
}
