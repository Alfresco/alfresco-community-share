package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    protected SiteCommon(ThreadLocal<WebDriver> webDriver)
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

    public void clickSiteConfiguration()
    {
        webElementInteraction.waitUntilElementIsVisible(siteConfiguration);
        webElementInteraction.clickElement(siteConfiguration);
        webElementInteraction.waitUntilElementsAreVisible(configurationOptions);
    }

    public boolean isWaitPopupDisplayed()
    {
        return webElementInteraction.isElementDisplayed(waitPopup);
    }

    public void clickSiteMembers()
    {
        while (isWaitPopupDisplayed())
        {
            webElementInteraction.waitUntilElementDisappears(waitPopup);
        }
        webElementInteraction.clickElement(members);
    }

    public boolean isSiteMembersLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(members);
    }

    public void clickSiteDashboard()
    {
        webElementInteraction.waitUntilElementIsVisible(dashboard);
        webElementInteraction.clickElement(dashboard);
    }

    public boolean isSiteDashboardLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(dashboard);
    }

    public void clickDocumentLibrary()
    {
        webElementInteraction.waitUntilElementIsVisible(documentLibrary);
        webElementInteraction.clickElement(documentLibrary);
    }

    public boolean isDocumentLibraryLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(documentLibrary);
    }

    public void clickAddUsersIcon()
    {
        webElementInteraction.waitUntilElementIsVisible(addUser);
        webElementInteraction.clickElement(addUser);
    }

    public T navigate(String siteId)
    {
        setCurrentSiteName(siteId);
        return (T) navigate();
    }

    public T navigate(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        return (T) navigate();
    }

    public void navigateWithoutRender(SiteModel site)
    {
        setCurrentSiteName(site.getId());
        navigateWithoutRender();
    }

    public String getSiteName()
    {
        return webElementInteraction.getElementText(siteName);
    }
}