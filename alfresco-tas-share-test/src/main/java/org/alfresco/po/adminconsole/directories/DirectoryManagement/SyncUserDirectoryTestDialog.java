package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.utility.report.log.Step.STEP;

/**
 * Created by Claudia Agache on 8/23/2017.
 */
@PageObject
public class SyncUserDirectoryTestDialog extends AdminConsoleDialog
{
    private By runTestButton = By.className("inline");
    private By result = By.id("test-auth-passed");
    private By resultSyncActive = By.id("test-auth-active");
    private By groupsList = By.cssSelector("#groupList td");
    private By usersList = By.cssSelector("#userList td");

    List<String> syncGroups = new ArrayList<>();
    List<String> syncUsers = new ArrayList<>();
    public List<String> runTest()
    {
        STEP("Click Run Test button");
        List<String> results = new ArrayList<>();
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(runTestButton, properties.getExplicitWait()).click();
        results.add(browser.waitUntilElementVisible(result).getText());
        results.add(browser.waitUntilElementVisible(resultSyncActive).getText());
        if(!browser.waitUntilElementsVisible(groupsList).isEmpty())
        for (WebElement group : browser.waitUntilElementsVisible(groupsList)) {
            syncGroups.add(group.getText().replaceFirst("GROUP_",""));
        }

        if(!browser.waitUntilElementsVisible(usersList).isEmpty())
        for (WebElement user : browser.waitUntilElementsVisible(usersList)) {
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
}
