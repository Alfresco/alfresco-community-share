package org.alfresco.po.share;

import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UploadFileDialog extends ShareDialog
{
    private By dialogBody = By.cssSelector("div[id*='default-dialog_c'][style*='visibility: visible']");

    @FindBy (css = "input.dnd-file-selection-button")
    private WebElement uploadInput;

    @FindBy (css = "[class*='fileupload-progressFailure']")
    private WebElement uploadFailedTransformationMessage;

    @RenderWebElement
    @FindBy (css = "div[id*='dnd-upload'] a[class*='close']")
    private WebElement closeUploadDialogButton;

    public void uploadFile(FileModel file)
    {
        uploadFile(Utility.setNewFile(file).getAbsolutePath());
    }

    public void uploadFile(String location)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver) (browser.getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        uploadInput.sendKeys(location);
    }

    public <T> SharePage uploadFileAndRenderPage(String location, SharePage<T> page)
    {
        LOG.info("Upload file from {} and render page", location);
        uploadFile(location);
        browser.waitUntilElementDisappears(dialogBody);
        
        return (SharePage) page.renderedPage();
    }

    public boolean isUploadFailedMessageDisplayed()
    {
        return browser.isElementDisplayed(uploadFailedTransformationMessage);
    }

    /**
     * Close dialog
     */
    @Override
    public void clickClose()
    {
        browser.waitUntilElementClickable(closeUploadDialogButton).click();
    }

    public void waitForUploadDialogToDisappear()
    {
        browser.waitUntilElementDisappears(dialogBody);
    }
}
