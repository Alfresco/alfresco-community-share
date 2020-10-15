package org.alfresco.po.share.site;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class CustomizeSiteDashboardPage<T> extends DashboardCustomization<CustomizeSiteDashboardPage<T>>
{
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

    public T navigate(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        return (T) navigate().renderedPage();
    }
}
