package org.alfresco.po.share.site;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeSiteDashboardPage extends DashboardCustomization<CustomizeSiteDashboardPage>
{
    @Autowired
    private SiteDashboardPage siteDashboardPage;

    private String currentSiteName;

    public String getCurrentSiteName()
    {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName)
    {
        this.currentSiteName = currentSiteName;
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/customise-site-dashboard", getCurrentSiteName());
    }

    public CustomizeSiteDashboardPage navigate(String siteId)
    {
        setCurrentSiteName(siteId);
        return (CustomizeSiteDashboardPage) renderedPage();
    }

    public CustomizeSiteDashboardPage navigate(SiteModel site)
    {
        return navigate(site.getId());
    }
}
