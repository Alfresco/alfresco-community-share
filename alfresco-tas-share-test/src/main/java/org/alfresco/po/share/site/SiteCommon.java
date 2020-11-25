package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;

public abstract class SiteCommon<T> extends SharePage2<SiteCommon<T>>
{
    protected By waitPopup = By.cssSelector(".wait");
    protected By addUser = By.cssSelector("img.alf-user-icon");
    protected By dashboard = By.cssSelector("#HEADER_SITE_DASHBOARD a");
    protected By documentLibrary = By.cssSelector("#HEADER_SITE_DOCUMENTLIBRARY a");
    protected By members = By.cssSelector("#HEADER_SITE_MEMBERS a");
    protected By siteConfiguration = By.id("HEADER_SITE_CONFIGURATION_DROPDOWN");
    protected By siteName = By.cssSelector("h1[id='HEADER_TITLE'] a.alfresco-navigation-_HtmlAnchorMixin");
    protected By configurationOptions = By.cssSelector("div[style*='visible'] tr[id^='HEADER']>td[id$='text']");

    private String currentSiteName;

    public String getCurrentSiteName()
    {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName)
    {
        this.currentSiteName = currentSiteName;
    }

    public void clickSiteConfiguration()
    {
        getBrowser().findElement(siteConfiguration).click();
        getBrowser().waitUntilElementsVisible(configurationOptions);
    }

    public boolean isWaitPopupDisplayed()
    {
        return getBrowser().isElementDisplayed(waitPopup);
    }

    public void clickSiteMembers()
    {
        while (isWaitPopupDisplayed() == true)
        {
            getBrowser().waitUntilElementDisappears(waitPopup);
        }
        getBrowser().findElement(members).click();
    }

    public boolean isSiteMembersLinkDisplayed()
    {
        return getBrowser().isElementDisplayed(members);
    }

    public void clickSiteDashboard()
    {
        getBrowser().findElement(dashboard).click();
    }

    public boolean isSiteDashboardLinkDisplayed()
    {
        return getBrowser().isElementDisplayed(dashboard);
    }

    public void clickDocumentLibrary()
    {
        getBrowser().findElement(documentLibrary).click();
    }

    public boolean isDocumentLibraryLinkDisplayed()
    {
        return getBrowser().isElementDisplayed(documentLibrary);
    }

    public void clickAddUsersIcon()
    {
        getBrowser().findElement(addUser).click();
    }

    public T navigate(String siteId)
    {
        setCurrentSiteName(siteId);
        return (T) navigate().renderedPage();
    }

    public T navigate(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        return (T) navigate().renderedPage();
    }

    public String getSiteName()
    {
        return getBrowser().waitUntilElementVisible(siteName).getText();
    }
}