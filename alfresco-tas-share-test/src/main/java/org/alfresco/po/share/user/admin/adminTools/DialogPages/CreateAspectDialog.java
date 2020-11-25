package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelDetailsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class CreateAspectDialog extends BaseDialogComponent
{
    @RenderWebElement
    private final By createAspectWidow = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG");
    private final By createButton = By.id("CMM_CREATE_PROPERTYGROUP_DIALOG_OK_label");
    @RenderWebElement
    private final By nameField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='name']");
    private final By displayLabelField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG input[name='title']");
    private final By descriptionField = By.cssSelector("#CMM_CREATE_PROPERTYGROUP_DIALOG div.control textarea");

    public CreateAspectDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
