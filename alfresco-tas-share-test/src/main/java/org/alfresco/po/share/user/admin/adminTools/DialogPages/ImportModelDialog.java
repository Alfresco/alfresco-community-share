package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.po.share.user.admin.adminTools.modelManager.ModelManagerPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

public class ImportModelDialog extends ShareDialog2
{
    @RenderWebElement
    private By importModelWindow = By.id("CMM_IMPORT_DIALOG");
    @RenderWebElement
    private By importButton = By.id("CMM_IMPORT_DIALOG_OK");
    private By importModelTitle = By.cssSelector("div[class='dijitDialogTitleBar'] span[id ='CMM_IMPORT_DIALOG_title']");
    private By cancelButton = By.id("CMM_IMPORT_DIALOG_CANCEL_label");
    private By fileInput = By.cssSelector(".alfresco-html-FileInput");

    public ImportModelDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public ImportModelDialog assertImportModelDialogOpened()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(importModelWindow), "Import model dialog is opened");
        return this;
    }

    public ImportModelDialog assertImportModelTitleIsCorrect()
    {
        Assert.assertEquals(getElementText(importModelTitle), language.translate("importModelDialog.title"));
        return this;
    }

    public ImportModelDialog assertBrowserButtonIsDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(fileInput) , "Browse button is displayed");
        return this;
    }

    public ImportModelDialog assertImportButtonDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(importButton), "Import button is displayed");
        return this;
    }

    public ImportModelDialog assertCancelButtonDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public ImportModelDialog importFile(String filePath)
    {
        if (properties.getEnv().getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)getBrowser().getWrappedDriver()).setFileDetector(new LocalFileDetector());
        }
        getBrowser().findElement(fileInput).sendKeys(filePath);
        return this;
    }

    public ModelManagerPage clickImportButton()
    {
        getBrowser().waitUntilElementClickable(importButton).click();
        ModelManagerPage modelManagerPage = new ModelManagerPage(browser);
        modelManagerPage.waiUntilLoadingMessageDisappears();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
