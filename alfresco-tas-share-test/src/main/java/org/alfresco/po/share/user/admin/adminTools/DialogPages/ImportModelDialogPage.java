package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.po.share.user.admin.adminTools.ModelManagerPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ImportModelDialogPage extends ShareDialog
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

    public boolean isImportModelWindowDisplayed()
    {
        return browser.isElementDisplayed(importModelWindow);
    }

    public String getImportModelWindowTitle()
    {
        return importModelTitle.getText();
    }

    public boolean isBrowserButtonDisplayed()
    {
        return browser.isElementDisplayed(fileInput);
    }

    public boolean isImportButtonDisplayed()
    {
        return browser.isElementDisplayed(importButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public void importFile(String filePath)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)browser.getWrappedDriver()).setFileDetector(new LocalFileDetector());
        }
        fileInput.sendKeys(filePath);
    }

    public ModelManagerPage clickImportButton()
    {
        browser.waitUntilElementClickable(importButton).click();
        modelManagerPage.refresh();
        return (ModelManagerPage) modelManagerPage.renderedPage();
    }
}
