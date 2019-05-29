package org.alfresco.po.share;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    public void uploadFile(String location)
    {
        uploadInput.sendKeys(location);
    }

    public void clickSelectFilesToUpload()

    {
        uploadButton.click();

    }
}
