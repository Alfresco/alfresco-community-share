package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeleteModelDialog extends BaseDialogComponent
{
    private final By deleteModelDialogLocator = By.cssSelector("div[id='CMM_DELETE_MODEL_DIALOG']");
    private final By deleteButton = By.xpath(".//span[normalize-space()='Delete']");
    private final By cancelButton = By.xpath(".//span[normalize-space()='Cancel']");
    private final By deleteModelDialogText = By.xpath("//div[@id='CMM_DELETE_MODEL_DIALOG']//div[@class='dialog-body']");

    public DeleteModelDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getDeleteModelDialogText()
    {
        return webElementInteraction.getElementText(deleteModelDialogText);
    }

    public DeleteModelDialog assertDeleteModelDialogTextIsCorrect(String modelName)
    {
        assertEquals(getDeleteModelDialogText(), String.format(language.translate("deleteModelDialog.text"), modelName));
        return this;
    }

    public DeleteModelDialog assertDeleteModelDialogIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(deleteModelDialogLocator), "Delete model dialog is displayed");
        return this;
    }

    public ModelManagerPage clickDelete()
    {
        webElementInteraction.waitUntilElementIsVisible(deleteButton);
        webElementInteraction.clickElement(deleteButton);
        webElementInteraction.waitUntilElementDisappears(deleteModelDialogLocator);
        ModelManagerPage modelManagerPage = new ModelManagerPage(webDriver);
        modelManagerPage.waitUntilLoadingMessageDisappears();
        return new ModelManagerPage(webDriver);
    }

    public DeleteModelDialog assertCancelButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }
}
