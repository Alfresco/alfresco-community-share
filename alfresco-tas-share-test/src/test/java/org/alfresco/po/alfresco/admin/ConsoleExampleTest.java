package org.alfresco.po.alfresco.admin;

import org.alfresco.po.alfresco.admin.AdminNavigator.NavigationLink;
import org.alfresco.po.alfresco.admin.consoles.TenantConsolePage;
import org.alfresco.po.alfresco.admin.consoles.WorkflowConsolePage;
import org.alfresco.po.alfresco.admin.repositoryservices.ActivitiesFeedPage;
import org.alfresco.po.alfresco.admin.supporttools.NodeBrowserQueryPage;
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
    public void testingAdminConsole()
    {
        nodeBrowser.setBrowser(getBrowser());
        nodeBrowser.navigate();

        for (NavigationLink link : NavigationLink.values())
        {
            nodeBrowser.getNavigator().goTo(link, applicationContext);
        }
    }
}
