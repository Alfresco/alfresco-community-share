package org.alfresco.po.adminconsole.directories.DirectoryManagement;

import static org.alfresco.utility.report.log.Step.STEP;

import org.alfresco.po.adminconsole.AdminConsoleDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;

/**
 * Created by Mirela Tifui on 7/4/2017.
 * This class is modeling the "Sync User Directories" window found on /alfresco/s/enterprise/admin/admin-directorymanagement when Run Synchronize button is clicked
 */
@PageObject
public class SyncUserDirectoriesDialog extends AdminConsoleDialog
{
    private By syncButton = By.className("inline");
    private By syncMessage = By.id("sync-message");

    public String clickSync()
    {
        STEP("Click Sync button");
        browser.switchTo().frame(dialogFrame);
        browser.waitUntilElementClickable(syncButton, properties.getExplicitWait()).click();
        String result = browser.waitUntilElementVisible(syncMessage).getText();
        browser.switchTo().defaultContent();
        return result;
    }
}
