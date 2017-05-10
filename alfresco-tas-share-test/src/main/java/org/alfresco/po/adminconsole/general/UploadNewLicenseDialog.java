package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 5/9/2017.
 */
@PageObject
public class UploadNewLicenseDialog extends AdminConsoleDialog
{
    private By fileField = By.id("ul-fileHidden");
    private By uploadButton = By.id("ul-upload-button");
    private By resultLocator = By.id("ul-result");

    public void uploadLicense(String filePath)
    {
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementPresent(fileField).sendKeys(filePath);
        browser.waitUntilElementClickable(uploadButton, properties.getImplicitWait()).click();
        browser.switchTo().defaultContent();
    }

    public String getResult()
    {
        browser.switchTo().frame(dialogFrame);
        WebElement resultElement = browser.waitUntilElementVisible(resultLocator);
        browser.waitUntilElementDoesNotContainText(resultElement, "Uploading license...");
        String result = resultElement.getText();
        browser.switchTo().defaultContent();
        return result;
    }
}
