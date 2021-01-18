package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ImportModelDialog extends BaseDialogComponent
{
    private final By importModelWindow = By.cssSelector("div[id='CMM_IMPORT_DIALOG']");
    private final By importButton = By.cssSelector("span[id='CMM_IMPORT_DIALOG_OK']");
    private final By importModelTitle = By.cssSelector("div[class='dijitDialogTitleBar'] span[id ='CMM_IMPORT_DIALOG_title']");
    private final By cancelButton = By.cssSelector("span[aria-labelledby='CMM_IMPORT_DIALOG_CANCEL_label']");
    private final By fileInput = By.cssSelector(".alfresco-html-FileInput");

    public ImportModelDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ImportModelDialog assertImportModelDialogOpened()
    {
        assertTrue(webElementInteraction.isElementDisplayed(importModelWindow), "Import model dialog is opened");
        return this;
    }

    public ImportModelDialog assertImportModelTitleIsCorrect()
    {
        assertEquals(webElementInteraction.getElementText(importModelTitle), language.translate("importModelDialog.title"));
        return this;
    }

    public ImportModelDialog assertBrowserButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(fileInput) , "Browse button is not displayed");
        return this;
    }

    public ImportModelDialog assertImportButtonDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(importButton), "Import button is not displayed");
        return this;
    }

    public ImportModelDialog assertCancelButtonDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is not displayed");
        return this;
    }

    public ImportModelDialog importFile(String filePath)
    {
        webElementInteraction.clearAndType(fileInput, filePath);
        return this;
    }

    public ModelManagerPage clickImportButton()
    {
        webElementInteraction.clickElement(importButton);
        ModelManagerPage modelManagerPage = new ModelManagerPage(webDriver);
        modelManagerPage.waitUntilLoadingMessageDisappears();
        return modelManagerPage;
    }
}
