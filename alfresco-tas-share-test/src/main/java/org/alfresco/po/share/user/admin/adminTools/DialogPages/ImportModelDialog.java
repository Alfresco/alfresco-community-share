package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ImportModelDialog extends ShareDialog
{
    @Autowired
    private Environment env;

    @Autowired
    private ModelManagerPage modelManagerPage;

    @RenderWebElement
    @FindBy (id = "CMM_IMPORT_DIALOG")
    private WebElement importModelWindow;

    @RenderWebElement
    @FindBy (id = "CMM_IMPORT_DIALOG_OK")
    private WebElement importButton;

    @FindBy (css = "div[class='dijitDialogTitleBar'] span[id ='CMM_IMPORT_DIALOG_title']")
    private WebElement importModelTitle;

    @FindBy (id = "CMM_IMPORT_DIALOG_CANCEL_label")
    private WebElement cancelButton;

    @RenderWebElement
    @FindBy (css = ".alfresco-html-FileInput")
    private WebElement fileInput;

    public ImportModelDialog assertImportModelDialogOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(importModelWindow), "Import model dialog is opened");
        return this;
    }

    public ImportModelDialog assertImportModelTitleIsCorrect()
    {
        Assert.assertEquals(importModelTitle.getText(), language.translate("importModelDialog.title"));
        return this;
    }

    public ImportModelDialog assertBrowserButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(fileInput) , "Browse button is displayed");
        return this;
    }

    public ImportModelDialog assertImportButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(importButton), "Import button is displayed");
        return this;
    }

    public ImportModelDialog assertCancelButtonDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public ImportModelDialog importFile(String filePath)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)browser.getWrappedDriver()).setFileDetector(new LocalFileDetector());
        }
        fileInput.sendKeys(filePath);
        return this;
    }

    public ModelManagerPage clickImportButton()
    {
        browser.waitUntilElementClickable(importButton).click();
        modelManagerPage.waiUntilLoadingMessageDisappears();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
