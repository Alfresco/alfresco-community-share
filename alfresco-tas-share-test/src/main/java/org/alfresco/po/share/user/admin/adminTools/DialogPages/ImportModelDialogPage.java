package org.alfresco.po.share.user.admin.adminTools.DialogPages;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by Mirela Tifui on 11/28/2016.
 */
@PageObject
public class ImportModelDialogPage extends ShareDialog
{
    @RenderWebElement
    @FindBy (id = "CMM_IMPORT_DIALOG_OK")
    private WebElement importButton;

    @RenderWebElement
    private By cancelButton = By.id("CMM_IMPORT_DIALOG_CANCEL_label");

    @RenderWebElement
    private By browserButton = By.cssSelector(".alfresco-html-FileInput");

    @RenderWebElement
    private By importModelWindow = By.id("CMM_IMPORT_DIALOG");

    @FindBy (css = "div[class='dijitDialogTitleBar'] span[id ='CMM_IMPORT_DIALOG_title']")
    private WebElement importModelTitle;

    private By closeButton = By.cssSelector("span[class='dijitDialogCloseIcon']");

    public boolean isImportModelWindowDisplayed()
    {
        return browser.isElementDisplayed(importModelWindow);
    }

    public String getImportModelWindowTitle()
    {
        return importModelTitle.getText();
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }

    public boolean isBrowserButtonDisplayed()
    {
        return browser.isElementDisplayed(browserButton);
    }

    public boolean isImportButtonDisplayed()
    {
        return browser.isElementDisplayed(importButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        return browser.isElementDisplayed(cancelButton);
    }

    public void clickChooseFilesButton()
    {
        browser.findElement(browserButton).click();
    }

    public void importFile(String filePath)
    {
        browser.findElement(browserButton).sendKeys(filePath);
    }

    public void clickImportButton()
    {
        browser.waitUntilElementClickable(importButton).click();
    }

}
