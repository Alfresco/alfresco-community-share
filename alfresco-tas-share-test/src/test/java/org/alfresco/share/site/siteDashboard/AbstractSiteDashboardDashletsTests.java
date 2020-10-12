package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSiteDashboardDashletsTests extends ContextAwareWebTest
{
    @Autowired
    private CustomizeSiteDashboardPage customizeSiteDashboardPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    protected final String sampleRssFeed = "https://www.feedforall.com/sample.xml";
    protected final String sampleRssFeedTitle = "feedforall.com";

    protected void addDashlet(Dashlets dashlets, int columnNumber)
    {
        customizeSiteDashboardPage.navigate();
        customizeSiteDashboardPage.addDashlet(dashlets, columnNumber);
        customizeSiteDashboardPage.clickOk();
        siteDashboardPage.renderedPage();
    }
}
