package org.alfresco.share.site.siteDashboard;

import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import static org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractSiteDashboardDashletsTests extends BaseTest
{
    @Autowired
    private SiteService siteService;

    protected SiteDashboardPage siteDashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void initSiteDashboardPage()
    {
        siteDashboardPage = new SiteDashboardPage(webDriver);
    }

    protected void addDashlet(UserModel user, SiteModel siteModel, SiteDashlet dashlet, int columnNumber, int position)
    {
        siteService.addDashlet(
            user.getUsername(),
            user.getPassword(),
            siteModel.getId(),
            dashlet,
            DashletLayout.TWO_COLUMNS_WIDE_RIGHT,
            columnNumber,
            position
        );
    }
}
