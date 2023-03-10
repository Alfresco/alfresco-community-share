package org.alfresco.po.share.site;

import static org.alfresco.common.Wait.WAIT_10;

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

    public T openSiteConfiguration()
    {
        waitUntilElementIsVisible(siteConfiguration);
        clickElement(siteConfiguration);
        waitUntilElementsAreVisible(configurationOptions);

        return (T) this;
    }

    public boolean isWaitPopupDisplayed()
    {
        return isElementDisplayed(waitPopup);
    }

    public void clickSiteMembers()
    {
        while (isWaitPopupDisplayed())
        {
            waitUntilElementDisappears(waitPopup);
        }
        clickElement(members);
    }

    public boolean isSiteMembersLinkDisplayed()
    {
        return isElementDisplayed(members);
    }

    public void clickSiteDashboard()
    {
        waitUntilElementIsVisible(dashboard);
        clickElement(dashboard);
    }

    public boolean isSiteDashboardLinkDisplayed()
    {
        return isElementDisplayed(dashboard);
    }

    public void navigateToDocumentLibraryPage()
    {
        waitUntilElementIsVisible(documentLibrary);
        clickElement(documentLibrary);
        waitUrlContains("documentlibrary", WAIT_10.getValue());
    }

    public boolean isDocumentLibraryLinkDisplayed()
    {
        return isElementDisplayed(documentLibrary);
    }

    public void clickAddUsersIcon()
    {
        waitUntilElementIsVisible(addUser);
        clickElement(addUser);
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
        waitInSeconds(2);
        return getElementText(siteName);
    }
}