package org.alfresco.po.adminconsole;

import org.alfresco.po.adminconsole.AdminNavigator.NavigationLink;
import org.alfresco.po.adminconsole.consoles.TenantConsolePage;
import org.alfresco.po.adminconsole.consoles.WorkflowConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagementPage;
import org.alfresco.po.adminconsole.repositoryservices.ActivitiesFeedPage;
import org.alfresco.po.adminconsole.supporttools.NodeBrowserQueryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class ConsoleExampleTest extends ContextAwareWebTest
{
    @Autowired
    ActivitiesFeedPage activitiesFeed;

    @Autowired
    TenantConsolePage tenantConsole;

    @Autowired
    WorkflowConsolePage workflowConsole;

    @Autowired
    NodeBrowserQueryPage nodeBrowser;

    @Autowired
    DirectoryManagementPage directoryManagementPage;

    @Test
    public void testingAdminConsole()
    {
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();

        for (NavigationLink link : NavigationLink.values())
        {
            nodeBrowser.getNavigator().goTo(link, applicationContext);
        }
    }

    @Test
    public void testingDirectoryManagement()
    {
        directoryManagementPage.setBrowser(getBrowser());
        directoryManagementPage.navigate();
        directoryManagementPage.getAuthenticationDetails();
        directoryManagementPage.setName("Test123");
        directoryManagementPage.selectType(DirectoryManagementPage.Type.EXTERNAL);
        directoryManagementPage.selectCIFSAuthentication(DirectoryManagementPage.CifsAuthenticationOptions.DISABLED);
        directoryManagementPage.selectBrowserBasedAutomaticLogin(DirectoryManagementPage.BrowserBasedAutomaticLogin.DISABLED);
        directoryManagementPage.clickSave();
        //directoryManagementPage.getAuthenticationDetails().get(0).clickAction("Test");

    }
}
