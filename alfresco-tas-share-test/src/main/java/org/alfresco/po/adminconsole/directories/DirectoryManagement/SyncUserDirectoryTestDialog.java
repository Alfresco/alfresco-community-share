package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import static org.alfresco.utility.report.log.Step.STEP;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by Claudia Agache on 8/23/2017.
 */
@PageObject
public class SyncUserDirectoryTestDialog extends AdminConsoleDialog
{
    List<String> syncGroups = new ArrayList<>();
    List<String> syncUsers = new ArrayList<>();
    int noGroups;
    int noUsers;
    private By runTestButton = By.className("inline");
    private By result = By.id("test-auth-passed");
    private By resultSyncActive = By.id("test-auth-active");
    private By groupsList = By.cssSelector("#groupList td");
    private By usersList = By.cssSelector("#userList td");
    private By testDisgnostics = By.cssSelector(".results>tbody>tr>td:nth-of-type(1)");

    public List<String> runTest()
    {
        STEP("Click Run Test button");
        List<String> results = new ArrayList<>();
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(runTestButton, properties.getExplicitWait()).click();
        results.add(browser.waitUntilElementVisible(result).getText());
        results.add(browser.waitUntilElementVisible(resultSyncActive).getText());

        String groupsDiagnostic = browser.waitUntilElementsVisible(testDisgnostics).get(0).getText();
        String usersDiagnostic = browser.waitUntilElementsVisible(testDisgnostics).get(1).getText();
        noGroups = Integer.parseInt(groupsDiagnostic.substring(groupsDiagnostic.indexOf(" ") + 1, groupsDiagnostic.lastIndexOf(" ")));
        noUsers = Integer.parseInt(usersDiagnostic.substring(usersDiagnostic.indexOf(" ") + 1, usersDiagnostic.lastIndexOf(" ")));

        if (!browser.waitUntilElementsVisible(groupsList).isEmpty())
            for (WebElement group : browser.waitUntilElementsVisible(groupsList))
            {
                syncGroups.add(group.getText().replaceFirst("GROUP_", ""));
            }

        if (!browser.waitUntilElementsVisible(usersList).isEmpty())
            for (WebElement user : browser.waitUntilElementsVisible(usersList))
            {
                syncUsers.add(user.getText());
            }
        browser.switchTo().defaultContent();
        return results;
    }

    public List<String> getSynchronizedGroups()
    {
        return syncGroups;
    }

    public List<String> getSynchronizedUsers()
    {
        return syncUsers;
    }

    public int getNoGroups()
    {
        return noGroups;
    }

    public int getNoUsers()
    {
        return noUsers;
    }

}
