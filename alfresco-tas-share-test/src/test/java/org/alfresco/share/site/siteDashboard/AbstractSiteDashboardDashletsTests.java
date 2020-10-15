package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.model.SiteModel;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSiteDashboardDashletsTests extends ContextAwareWebTest
{
    protected static final String EXPECTED_DIALOG_TITLE = "Enter Feed URL:";
    protected static final String RSS_FEED_URL = "https://www.feedforall.com/sample.xml";
    protected static final String DASHLET_TITLE = "FeedForAll Sample Feed";
    protected static final String NUMBER_OF_ITEMS_DISPLAYED = "5";
    protected static final int SECOND_LINK = 1;
    protected static final String EXPECTED_URL = "feedforall.com";

    @Autowired
    protected CustomizeSiteDashboardPage customizeSiteDashboardPage;

    @Autowired
    private SiteDashboardPage siteDashboardPage;

    protected void addDashlet(SiteModel siteModel, Dashlets dashlets, int columnNumber)
    {
        customizeSiteDashboardPage.navigate(siteModel);
        customizeSiteDashboardPage.addDashlet(dashlets, columnNumber);
        customizeSiteDashboardPage.clickOk();
        siteDashboardPage.renderedPage();
    }
}
