package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;

/**
 * Created by Claudia Agache on 8/23/2017.
 */
@PageObject
public class AuthenticationTestDialog extends AdminConsoleDialog
{
    private By usernameInput = By.id("test-username");
    private By passwordInput = By.id("test-password");
    private By runTestButton = By.className("inline");
    private By results = By.id("test-results");

    public String runTest(UserModel userModel)
    {
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementVisible(usernameInput).sendKeys(userModel.getUsername());
        browser.waitUntilElementVisible(passwordInput).sendKeys(userModel.getPassword());
        browser.waitUntilElementClickable(runTestButton, properties.getExplicitWait()).click();
        String result = browser.waitUntilElementVisible(results).getText();
        browser.switchTo().defaultContent();
        return result;
    }
}
