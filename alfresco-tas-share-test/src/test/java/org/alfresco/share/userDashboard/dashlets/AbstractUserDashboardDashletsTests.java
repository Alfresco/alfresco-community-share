package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.user.CustomizeUserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractUserDashboardDashletsTests extends ContextAwareWebTest
{
    @Autowired
    private CustomizeUserDashboardPage customizeUserDashboardPage;

    protected final String sampleRssFeed = "https://www.feedforall.com/sample.xml";
    protected final String sampleRssFeedTitle = "feedforall.com";

    protected void addDashlet(Dashlets dashlets, int columnNumber)
    {
        customizeUserDashboardPage.navigate();
        customizeUserDashboardPage.addDashlet(dashlets, columnNumber);
        customizeUserDashboardPage.clickOk();
        userDashboard.renderedPage();
    }
}
