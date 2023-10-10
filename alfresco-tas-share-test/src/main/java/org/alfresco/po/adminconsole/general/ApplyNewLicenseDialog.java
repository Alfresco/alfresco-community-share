package org.alfresco.po.adminconsole.general;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 5/9/2017.
 */
@PageObject
public class ApplyNewLicenseDialog extends AdminConsoleDialog
{
    private By resultLocator = By.id("result");

    public String getResult()
    {
        browser.switchTo().frame(dialogFrame);
        WebElement resultElement = browser.waitUntilElementVisible(resultLocator);
        browser.waitUntilElementDoesNotContainText(resultElement, "Applying license...");
        String result = resultElement.getText();
        browser.switchTo().defaultContent();
        return result;
    }
}
