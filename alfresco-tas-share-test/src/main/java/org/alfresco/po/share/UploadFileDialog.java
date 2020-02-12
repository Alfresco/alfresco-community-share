package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class UploadFileDialog extends ShareDialog
{
    @FindBy (css = "div[id*='default-dialog_h']")
    private WebElement updateFileDialogTitle;

    @FindBy (css = "input.dnd-file-selection-button")
    private WebElement uploadInput;

    @FindBy (css = "input.dnd-file-selection-button")
    private WebElement uploadButton;

    @FindBy (css = "[class*='fileupload-progressFailure']")
    private WebElement uploadFailedTransformationMessage;

    @FindBy (css = "div[id*='dnd-upload'] a[class*='close']")
    private WebElement closeUploadDialogButton;

    @Autowired
    private Environment env;

    public void uploadFile(String location)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver) (browser.getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        uploadInput.sendKeys(location);
    }

    public boolean isUploadFailedMessageDisplayed()
    {
        return browser.isElementDisplayed(uploadFailedTransformationMessage);
    }

    /**
     * Close dialog
     */
    public void clickClose()
    {
        browser.waitUntilElementClickable(closeUploadDialogButton).click();
    }

    public void clickSelectFilesToUpload()

    {
        uploadButton.click();

    }
}
