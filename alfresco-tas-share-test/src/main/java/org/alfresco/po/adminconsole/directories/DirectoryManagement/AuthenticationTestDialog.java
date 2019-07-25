package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.HashMap;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 8/23/2017.
 */
@PageObject
public class AuthenticationTestDialog extends AdminConsoleDialog
{
    String message = "";
    HashMap<String, String> diagnostic = new HashMap<>();
    private By runTestButton = By.cssSelector(".buttons>.inline");
    private By resultLocator = By.id("test-results");
    private By messageLocator = By.cssSelector("#test-diagnostics>div");
    private By diagnosticLocator = By.cssSelector(".results>tbody>tr");

    public String runTest(UserModel userModel) throws Exception
    {
        typeValueInField(userModel.getUsername(), "Username:");
        typeValueInField(userModel.getPassword(), "Password:");

        STEP("Click Run Test button");
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(runTestButton, properties.getExplicitWait()).click();
        String result = browser.waitUntilElementVisible(resultLocator).getText();
        if (browser.isElementDisplayed(messageLocator))
        {
            message = browser.waitUntilElementVisible(messageLocator).getText();
            for (WebElement diagnosticRow : browser.waitUntilElementsVisible(diagnosticLocator))
            {
                diagnostic.put(diagnosticRow.findElement(By.cssSelector("td:nth-of-type(1)")).getText(), diagnosticRow.findElement(By.cssSelector("td:nth-of-type(2)")).getText());
            }
        }
        browser.switchTo().defaultContent();
        return result;
    }

    public String getMessage()
    {
        return message;
    }

    public HashMap<String, String> getDiagnostic()
    {
        return diagnostic;
    }
}
