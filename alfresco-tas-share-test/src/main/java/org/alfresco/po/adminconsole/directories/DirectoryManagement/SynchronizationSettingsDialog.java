package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.po.adminconsole.directories.DirectoryManagementPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SynchronizationSettingsDialog extends AdminConsoleDialog
{
    @Autowired
    DirectoryManagementPage directoryManagementPage;

    private By save = By.cssSelector(".buttons>input[type='submit']");

    public DirectoryManagementPage clickSave()
    {
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(save, properties.getExplicitWait()).click();
        browser.switchTo().defaultContent();
        return (DirectoryManagementPage) directoryManagementPage.renderedPage();
    }
}
