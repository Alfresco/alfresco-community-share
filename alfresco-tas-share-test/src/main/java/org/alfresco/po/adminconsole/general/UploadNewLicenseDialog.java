package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by Claudia Agache on 5/9/2017.
 */
@PageObject
public class UploadNewLicenseDialog extends AdminConsoleDialog
{
    private By fileField = By.id("ul-fileHidden");
    private By uploadButton = By.id("ul-upload-button");
    private By resultLocator = By.id("ul-result");
    @Autowired
    private Environment env;

    public void selectLicense(String filePath)
    {
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementIsPresent(fileField).sendKeys(filePath);
        browser.switchTo().defaultContent();
    }

    public void uploadLicense(String filePath)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver) (browser.getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementIsPresent(fileField).sendKeys(filePath);
        browser.waitUntilElementClickable(uploadButton).click();
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