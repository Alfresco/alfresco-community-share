package org.alfresco.po.share.toolbar;

import java.util.List;

import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class ToolbarSitesMenu extends Toolbar
{
    @FindBy (xpath = "//tr[starts-with(@id,'HEADER_SITES_MENU_RECENT')]/../../../../div[@class='alf-menu-group-title']")
    private WebElement recentSitesSection;

    @FindAll (@FindBy (css = "td[id^='HEADER_SITES_MENU_RECENT_']"))
    private List<WebElement> recentSitesRowList;

    @FindBy (css = "#HEADER_SITES_MENU_dropdown .alf-menu-group:last-child > *:first-child")
    private WebElement usefulSection;

    @RenderWebElement
    @FindBy (id = "HEADER_SITES_MENU_MY_SITES_text")
    private WebElement mySites;

    @FindBy (id = "HEADER_SITES_MENU_SITE_FINDER_text")
    private WebElement siteFinder;

    @FindBy (id = "HEADER_SITES_MENU_CREATE_SITE_text")
    private WebElement createSite;

    @FindBy (id = "HEADER_SITES_MENU_FAVOURITES_text")
    private WebElement favorites;

    @FindBy (id = "HEADER_SITES_MENU_dropdown")
    private WebElement sitesDopdown;

    @FindAll (@FindBy (css = "td[id^='HEADER_SITES_MENU_FAVOURITE_'] a"))
    private List<WebElement> favoriteSitesRowList;

    @FindBy (id = "HEADER_SITES_MENU_FAVOURITES_dropdown")
    private WebElement favoriteDropDown;

    private By addCurrentSiteToFavorites = By.id("HEADER_SITES_MENU_ADD_FAVOURITE_text");
    private By removeCurrentSiteFromFavorites = By.id("HEADER_SITES_MENU_REMOVE_FAVOURITE_text");

    private final SiteDashboardPage siteDashboardPage;
    private final UserSitesListPage userSitesListPage;
    private final SiteFinderPage siteFinderPage;
    private final CreateSiteDialog createSiteDialog;

    public ToolbarSitesMenu(SiteDashboardPage siteDashboardPage,
                            UserSitesListPage userSitesListPage,
                            SiteFinderPage siteFinderPage,
                            CreateSiteDialog createSiteDialog)
    {
        this.siteDashboardPage = siteDashboardPage;
        this.userSitesListPage = userSitesListPage;
        this.siteFinderPage = siteFinderPage;
        this.createSiteDialog = createSiteDialog;
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsDisplayed()
    {
        LOG.info("Assert Recent Sites section is displayed");
        Assert.assertTrue(browser.isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRecentSitesSectionIsNotDisplayed()
    {
        LOG.info("Assert Recent Sites section is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(recentSitesSection), "Recent sites section is displayed");
        return this;
    }

    public SiteDashboardPage clickRecentSite(String siteName)
    {
        LOG.info(String.format("Click recent site: %s", siteName));
        browser.findFirstElementWithValue(recentSitesRowList, siteName).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage clickRecentSite(SiteModel site)
    {
        return clickRecentSite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsInRecentSites(String siteName)
    {
        LOG.info(String.format("Assert site %s is displayed in recent sites", siteName));
        Assert.assertTrue(recentSitesRowList.size() > 0 && browser.findFirstElementWithValue(recentSitesRowList, siteName) != null,
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
        Assert.assertTrue(browser.isElementDisplayed(usefulSection), "Useful section is displayed");
        return this;
    }

    public ToolbarSitesMenu assertMySitesIsDisplayed()
    {
        LOG.info("Assert My Sites link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(mySites), "My Sites is displayed");
        return this;
    }

    public UserSitesListPage clickMySites()
    {
        LOG.info("Click My Sites");
        mySites.click();
        return (UserSitesListPage) userSitesListPage.renderedPage();
    }

    public ToolbarSitesMenu assertSiteFinderIsDisplayed()
    {
        LOG.info("Assert Site Finder link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(siteFinder), "Site Finder is displayed");
        return this;
    }

    public SiteFinderPage clickSiteFinder()
    {
        LOG.info("Click Site Finder");
        siteFinder.click();
        return (SiteFinderPage) siteFinderPage.renderedPage();
    }

    public ToolbarSitesMenu assertCreateSiteIsDisplayed()
    {
        LOG.info("Assert Create Site is displayed");
        Assert.assertTrue(browser.isElementDisplayed(createSite), "Create Site is displayed");
        return this;
    }

    public CreateSiteDialog clickCreateSite()
    {
        LOG.info("Click Create Site");
        createSite.click();
        return (CreateSiteDialog) createSiteDialog.renderedPage();
    }

    public ToolbarSitesMenu assertFavoritesIsDisplayed()
    {
        LOG.info("Assert Favorites link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(favorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertAddCurrentSiteToFavoritesDisplayed()
    {
        LOG.info("Assert Add current site to favorites is displayed");
        Assert.assertTrue(browser.isElementDisplayed(addCurrentSiteToFavorites), "Favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsDisplayed()
    {
        LOG.info("Assert Remove Current Site from Favorites is displayed");
        Assert.assertTrue(browser.isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public ToolbarSitesMenu assertRemoveCurrentSiteFromFavoritesIsNotDisplayed()
    {
        LOG.info("Assert Remove Current Site from Favorites is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(removeCurrentSiteFromFavorites), "Remove current site from favorites is displayed");
        return this;
    }

    public void clickAddCurrentSiteToFavorites()
    {
        LOG.info("Click Add current site to favorites");
        browser.waitUntilElementVisible(addCurrentSiteToFavorites).click();
    }

    public void clickRemoveCurrentSiteFromFavorites()
    {
        LOG.info("Click Remove current site from favorites");
        browser.waitUntilElementVisible(removeCurrentSiteFromFavorites).click();
        browser.waitUntilElementDisappears(removeCurrentSiteFromFavorites);
    }

    private boolean isSiteFavorite(String siteName)
    {
        favorites.click();
        browser.waitUntilElementVisible(favoriteDropDown);
        return browser.findFirstElementWithValue(favoriteSitesRowList, siteName) != null;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(String siteName)
    {
        LOG.info(String.format("Assert site %s is found in favorites", siteName));
        Assert.assertTrue(isSiteFavorite(siteName), String.format("Site %s is found in favorites", siteName));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsFavorite(SiteModel site)
    {
        return assertSiteIsFavorite(site.getTitle());
    }

    public ToolbarSitesMenu assertSiteIsNotFavorite(String siteName)
    {
        LOG.info(String.format("Assert site %s is NOT found in favorites", siteName));
        Assert.assertFalse(isSiteFavorite(siteName), String.format("Site %s is found in favorites"));
        return this;
    }

    public ToolbarSitesMenu assertSiteIsNotFavorite(SiteModel site)
    {
        return assertSiteIsNotFavorite(site.getTitle());
    }

    public SiteDashboardPage clickFavoriteSite(String siteName)
    {
        LOG.info(String.format("Select site %s from favorites", siteName));
        favorites.click();
        browser.waitUntilElementVisible(favoriteDropDown);
        browser.findFirstElementWithValue(favoriteSitesRowList, siteName).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage clickFavoriteSite(SiteModel site)
    {
        return clickFavoriteSite(site.getTitle());
    }
}
