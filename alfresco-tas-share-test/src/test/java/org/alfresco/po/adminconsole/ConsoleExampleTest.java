package org.alfresco.po.adminconsole;

import junit.framework.Assert;
import org.alfresco.po.adminconsole.AdminNavigator.NavigationLink;
import org.alfresco.po.adminconsole.consoles.TenantConsolePage;
import org.alfresco.po.adminconsole.consoles.WorkflowConsolePage;
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

    @Test
    public void testingAdminConsole() {
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();

        for (NavigationLink link : NavigationLink.values()) {
            nodeBrowser.getNavigator().goTo(link, applicationContext);
        }
    }

    @Test
    public void testRows() {
        setupAuthenticatedSession(adminUser, adminPassword);
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();
        nodeBrowser.clickRootList();
        nodeBrowser.getProperties();
        nodeBrowser.getProperties().get(0).clickDelete();
        nodeBrowser.getProperties().get(1).clickDelete();
        nodeBrowser.getChildren().get(2).clickAction(NodeBrowserQueryPage.Actions.revertPermissions.getAction());
        nodeBrowser.getChildren().get(1).clickAction(NodeBrowserQueryPage.Actions.delete.getAction());
    }
}
