package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.BasePages;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ToolbarSitesMenu extends BasePages
{
    private By recentSitesSection = By.xpath("//tr[starts-with(@id,'HEADER_SITES_MENU_RECENT')]/../../../../div[@class='alf-menu-group-title']");
    private By recentSitesRowList = By.cssSelector("td[id^='HEADER_SITES_MENU_RECENT_']");
    private By usefulSection = By.cssSelector("#HEADER_SITES_MENU_dropdown .alf-menu-group:last-child > *:first-child");
    @RenderWebElement
    private By mySites = By.id("HEADER_SITES_MENU_MY_SITES_text");
    private By siteFinder = By.id("HEADER_SITES_MENU_SITE_FINDER_text");
    private By createSite = By.id("HEADER_SITES_MENU_CREATE_SITE_text");
    private By favorites = By.id("HEADER_SITES_MENU_FAVOURITES_text");
    private By sitesDopdown = By.id("HEADER_SITES_MENU_dropdown");
    private By favoriteSitesRowList = By.cssSelector("td[id^='HEADER_SITES_MENU_FAVOURITE_'] a");
    private By favoriteDropDown = By.id("HEADER_SITES_MENU_FAVOURITES_dropdown");
    private By addCurrentSiteToFavorites = By.id("HEADER_SITES_MENU_ADD_FAVOURITE_text");
    private By removeCurrentSiteFromFavorites = By.id("HEADER_SITES_MENU_REMOVE_FAVOURITE_text");

    public ToolbarSitesMenu(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsDisplayed()
    {
        LOG.info("Assert Recent Sites section is displayed");
        assertTrue(getBrowser().isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsNotDisplayed()
    {
        LOG.info("Assert Recent Sites section is NOT displayed");
        assertFalse(getBrowser().isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public SiteDashboardPage clickRecentSite(String siteName)
    {
        LOG.info(String.format("Click recent site: %s", siteName));
        List<WebElement> sites = getBrowser().findElements(recentSitesRowList);
        getBrowser().findFirstElementWithValue(sites, siteName).click();
        return (SiteDashboardPage) new SiteDashboardPage(browser).renderedPage();
    }

    public SiteDashboardPage clickRecentSite(SiteModel site)
    {
        return clickRecentSite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsInRecentSites(String siteName)
    {
        LOG.info(String.format("Assert site %s is displayed in recent sites", siteName));
        List<WebElement> recentSites = getBrowser().findElements(recentSitesRowList);
        assertTrue(getBrowser().findFirstElementWithValue(recentSites, siteName) != null,
            String.format("Assert site %s is found in Recent Sites section", siteName));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsInRecentSites(SiteModel site)
    {
        return assertSiteIsInRecentSites(site.getTitle());
    }

    public ToolbarSitesMenu assertUsefulSectionIsDisplayed()
    {
        LOG.info("Assert Useful section is displayed");
        assertTrue(getBrowser().isElementDisplayed(usefulSection), "Useful section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertMySitesIsDisplayed()
    {
        LOG.info("Assert My Sites link is displayed");
        assertTrue(getBrowser().isElementDisplayed(mySites), "My Sites is displayed");
        return this;
    }

    public UserSitesListPage clickMySites()
    {
        LOG.info("Click My Sites");
        getBrowser().findElement(mySites).click();
        return (UserSitesListPage) new UserSitesListPage(browser).renderedPage();
    }

    public ToolbarSitesMenu assertSiteFinderIsDisplayed()
    {
        LOG.info("Assert Site Finder link is displayed");
        assertTrue(getBrowser().isElementDisplayed(siteFinder), "Site Finder is displayed");
        return this;
    }

    public SiteFinderPage clickSiteFinder()
    {
        LOG.info("Click Site Finder");
        getBrowser().findElement(siteFinder).click();
        return (SiteFinderPage) new SiteFinderPage(browser).renderedPage();
    }

    public ToolbarSitesMenu assertCreateSiteIsDisplayed()
    {
        LOG.info("Assert Create Site is displayed");
        assertTrue(getBrowser().isElementDisplayed(createSite), "Create Site is displayed");
        return this;
    }

    public CreateSiteDialog clickCreateSite()
    {
        LOG.info("Click Create Site");
        getBrowser().findElement(createSite).click();
        return (CreateSiteDialog) new CreateSiteDialog(browser).renderedPage();
    }

    public ToolbarSitesMenu assertFavoritesIsDisplayed()
    {
        LOG.info("Assert Favorites link is displayed");
        assertTrue(getBrowser().isElementDisplayed(favorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertAddCurrentSiteToFavoritesDisplayed()
    {
        LOG.info("Assert Add current site to favorites is displayed");
        assertTrue(getBrowser().isElementDisplayed(addCurrentSiteToFavorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsDisplayed()
    {
        LOG.info("Assert Remove Current Site from Favorites is displayed");
        assertTrue(getBrowser().isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsNotDisplayed()
    {
        LOG.info("Assert Remove Current Site from Favorites is NOT displayed");
        assertFalse(getBrowser().isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public void clickAddCurrentSiteToFavorites()
    {
        LOG.info("Click Add current site to favorites");
        getBrowser().waitUntilElementVisible(addCurrentSiteToFavorites).click();
    }

    public void clickRemoveCurrentSiteFromFavorites()
    {
        LOG.info("Click Remove current site from favorites");
        getBrowser().waitUntilElementVisible(removeCurrentSiteFromFavorites).click();
        getBrowser().waitUntilElementDisappears(removeCurrentSiteFromFavorites);
    }

    private boolean isSiteFavorite(String siteName)
    {
        getBrowser().findElement(favorites).click();
        getBrowser().waitUntilElementVisible(favoriteDropDown);
        return getBrowser().findFirstElementWithValue(favoriteSitesRowList, siteName) != null;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(String siteName)
    {
        LOG.info(String.format("Assert site %s is found in favorites", siteName));
        assertTrue(isSiteFavorite(siteName), String.format("Site %s is found in favorites", siteName));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(SiteModel site)
    {
        return assertSiteIsFavorite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsNotFavorite(String siteName)
    {
        LOG.info(String.format("Assert site %s is NOT found in favorites", siteName));
        assertFalse(isSiteFavorite(siteName), String.format("Site %s is found in favorites"));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsNotFavorite(SiteModel site)
    {
        return assertSiteIsNotFavorite(site.getTitle());
    }

    public SiteDashboardPage clickFavoriteSite(String siteName)
    {
        LOG.info(String.format("Select site %s from favorites", siteName));
        getBrowser().findElement(favorites).click();
        getBrowser().waitUntilElementVisible(favoriteDropDown);
        getBrowser().findFirstElementWithValue(favoriteSitesRowList, siteName).click();
        return (SiteDashboardPage) new SiteDashboardPage(browser).renderedPage();
    }

    public SiteDashboardPage clickFavoriteSite(SiteModel site)
    {
        return clickFavoriteSite(site.getTitle());
    }
}
