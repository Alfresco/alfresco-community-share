package org.alfresco.po.share.site;

import org.alfresco.po.share.DashboardCustomization;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.browser.WebBrowser;

/**
 * @author bogdan.bocancea
 */
public class CustomizeSiteDashboardPage<T> extends DashboardCustomization<CustomizeSiteDashboardPage<T>>
{
    private String currentSiteName;

    public CustomizeSiteDashboardPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
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
        return (CustomizeSiteDashboardPage) renderedPage();
    }

    public T navigate(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        return (T) navigate().renderedPage();
    }
}
