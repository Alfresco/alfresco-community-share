package org.alfresco.po.share;

import org.alfresco.utility.Utility;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * @author bogdan.bocancea
 */
public class UploadFileDialog extends ShareDialog2
{
    @RenderWebElement
    private By dialogBody = By.cssSelector("div[id*='default-dialog_c'][style*='visibility: visible']");
    private By uploadInput = By.cssSelector("input.dnd-file-selection-button");
    private By uploadFailedTransformationMessage = By.cssSelector("[class*='fileupload-progressFailure']");
    @RenderWebElement
    private By closeUploadDialogButton = By.cssSelector("div[id*='dnd-upload'] a[class*='close']");

    public UploadFileDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public void uploadFile(FileModel file)
    {
        uploadFile(Utility.setNewFile(file).getAbsolutePath());
    }

    public void uploadFile(String location)
    {
        if (properties.isGridEnabled())
        {
            ((RemoteWebDriver) (getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        getBrowser().findElement(uploadInput).sendKeys(location);
        getBrowser().waitUntilElementDisappears(dialogBody);
    }

    public <T> SharePage2 uploadFileAndRenderPage(String location, SharePage2<T> page)
    {
        LOG.info("Upload file from {} and render page", location);
        uploadFile(location);
        getBrowser().waitUntilElementDisappears(dialogBody);
        
        return (SharePage2) page.renderedPage();
    }

    public boolean isUploadFailedMessageDisplayed()
    {
        return getBrowser().isElementDisplayed(uploadFailedTransformationMessage);
    }

    @Override
    public void clickClose()
    {
        getBrowser().waitUntilElementClickable(closeUploadDialogButton).click();
    }

    public void waitForUploadDialogToDisappear()
    {
        getBrowser().waitUntilElementDisappears(dialogBody);
    }
}
