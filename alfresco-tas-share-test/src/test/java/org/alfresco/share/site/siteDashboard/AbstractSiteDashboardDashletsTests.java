package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.model.SiteModel;

public abstract class AbstractSiteDashboardDashletsTests extends ContextAwareWebTest
{
    //@Autowired
    protected CustomizeSiteDashboardPage customizeSiteDashboardPage;

    //@Autowired
    protected SiteDashboardPage siteDashboardPage;

    protected void addDashlet(SiteModel siteModel, Dashlets dashlets, int columnNumber)
    {
        customizeSiteDashboardPage.navigate(siteModel);
        customizeSiteDashboardPage.addDashlet(dashlets, columnNumber);
        customizeSiteDashboardPage.clickOk();
        siteDashboardPage.renderedPage();
    }
}
