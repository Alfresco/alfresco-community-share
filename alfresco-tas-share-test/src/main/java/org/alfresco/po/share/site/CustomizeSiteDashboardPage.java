package org.alfresco.po.share.site;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
public class CustomizeSiteDashboardPage extends DashboardCustomization<CustomizeSiteDashboardPage>
{
    private String currentSiteName;

    public CustomizeSiteDashboardPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

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
        return this;
    }

    public CustomizeSiteDashboardPage navigate(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        navigate();
        return this;
    }
}
