package org.alfresco.po.share;

import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FileModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
public class UploadFileDialog extends BaseDialogComponent
{
    private final By dialogBody = By.cssSelector("div[id*='default-dialog_c'][style*='visibility: visible']");
    private final By uploadInput = By.cssSelector("input.dnd-file-selection-button");
    private final By uploadFailedTransformationMessage = By.cssSelector("[class*='fileupload-progressFailure']");
    private final By closeUploadDialogButton = By.cssSelector("div[id*='dnd-upload'] a[class*='close']");

    public UploadFileDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public void uploadFile(FileModel file)
    {
        uploadFile(Utility.setNewFile(file).getAbsolutePath());
    }

    public void uploadFile(String location)
    {
        webElementInteraction.waitUntilElementIsVisible(dialogBody);
        webElementInteraction.clearAndType(uploadInput, location);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
    }

    public <T> T uploadFile(String location, SharePage2<T> page)
    {
        LOG.info("Upload file from {}", location);
        uploadFile(location);
        webElementInteraction.waitUntilElementDisappears(dialogBody);
        
        return (T) this;
    }

    public boolean isUploadFailedMessageDisplayed()
    {
        return webElementInteraction.isElementDisplayed(uploadFailedTransformationMessage);
    }

    @Override
    public void clickClose()
    {
        webElementInteraction.clickElement(closeUploadDialogButton);
    }

    public void waitForUploadDialogToDisappear()
    {
        webElementInteraction.waitUntilElementDisappears(dialogBody);
    }
}
