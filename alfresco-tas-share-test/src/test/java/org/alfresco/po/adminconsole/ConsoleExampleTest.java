package org.alfresco.po.adminconsole;

import java.util.ArrayList;

import org.alfresco.po.adminconsole.AdminNavigator.NavigationLink;
import org.alfresco.po.adminconsole.consoles.TenantConsolePage;
import org.alfresco.po.adminconsole.consoles.WorkflowConsolePage;
import org.alfresco.po.adminconsole.directories.DirectoryManagementPage;
import org.alfresco.po.adminconsole.repositoryservices.ActivitiesFeedPage;
import org.alfresco.po.adminconsole.supporttools.NodeBrowserQueryPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.exception.TestConfigurationException;
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

    @Test (groups = { "unit" })
    public void testingAdminConsole()
    {
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();

        for (NavigationLink link : NavigationLink.values())
        {
            nodeBrowser.getNavigator().goTo(link, applicationContext);
        }
    }

    @Test (groups = { "unit" })
    public void testRows() throws TestConfigurationException
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();
        nodeBrowser.clickSearchAdvancedSettings();
        nodeBrowser.setMaxResults(10);
        nodeBrowser.setSkipCount(30);
        nodeBrowser.clickRootList();
        nodeBrowser.getProperties();
        nodeBrowser.getProperties().get(0).clickDelete();
        nodeBrowser.getProperties().get(1).clickDelete();
        nodeBrowser.getChildren().get(2).clickAction(NodeBrowserQueryPage.Actions.REVERT_PERMISSIONS.getAction());
        nodeBrowser.getChildren().get(1).clickAction(NodeBrowserQueryPage.Actions.DELETE.getAction());
        nodeBrowser.assertNodeInformationIs("Type", "ys:store_root");
        ArrayList<String> expectedAspects = new ArrayList<String>();
        expectedAspects.add("sys:aspect_root");
        expectedAspects.add("sys:jj");
        expectedAspects.add("sys:localized");
        nodeBrowser.assertAspectsArePresent(expectedAspects);
    }

    @Test (groups = { "unit" })
    public void testingDirectoryManagement()
    {
        directoryManagementPage.setBrowser(getBrowser());
        directoryManagementPage.navigate();
        directoryManagementPage.getAuthenticationDetails();
        directoryManagementPage.setName("Test123");
        directoryManagementPage.selectType(DirectoryManagementPage.Type.EXTERNAL);
        directoryManagementPage.clickSynchronizationSettings();
        directoryManagementPage.getAuthenticationDetails().get(0).clickAction("Test");
    }

}