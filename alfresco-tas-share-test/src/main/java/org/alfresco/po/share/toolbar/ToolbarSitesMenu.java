package org.alfresco.po.share.toolbar;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@PageObject
public class ToolbarSitesMenu extends Toolbar
{
    @FindBy (css = "#HEADER_SITES_MENU_dropdown .alf-menu-group:first-child > *:first-child")
    private WebElement recentSitesSection;

    @FindAll (@FindBy (css = "td[id^='HEADER_SITES_MENU_RECENT_']"))
    private List<WebElement> recentSitesRowList;

    @FindBy (css = "#HEADER_SITES_MENU_dropdown .alf-menu-group:last-child > *:first-child")
    private WebElement usefulSection;

    @FindBy (id = "HEADER_SITES_MENU_MY_SITES_text")
    private WebElement mySites;

    @FindBy (id = "HEADER_SITES_MENU_SITE_FINDER_text")
    private WebElement siteFinder;

    @FindBy (id = "HEADER_SITES_MENU_CREATE_SITE_text")
    private WebElement createSite;

    @FindBy (id = "HEADER_SITES_MENU_FAVOURITES_text")
    private WebElement favorites;

    @FindAll (@FindBy (css = "td[id^='HEADER_SITES_MENU_FAVOURITE_']"))
    private List<WebElement> favoriteSitesRowList;

    private By addCurrentSiteToFavorites = By.id("HEADER_SITES_MENU_ADD_FAVOURITE_text");
    private By removeCurrentSiteFromFavorites = By.id("HEADER_SITES_MENU_REMOVE_FAVOURITE_text");

    public boolean isRecentSitesSectionDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(recentSitesSection);
    }

    public void clickRecentSite(String siteName)
    {
        sitesLink.click();
        browser.findFirstElementWithValue(recentSitesRowList, siteName).click();
    }

    public boolean isSiteInRecentSites(String siteName)
    {
        sitesLink.click();
        return recentSitesRowList.size() > 0 && browser.findFirstElementWithValue(recentSitesRowList, siteName) != null;
    }

    public boolean isUsefulSectionDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(usefulSection);
    }

    public boolean isMySitesDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(mySites);
    }

    public void clickMySites()
    {
        sitesLink.click();
        mySites.click();
    }

    public boolean isSiteFinderDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(siteFinder);
    }

    public void clickSiteFinder()
    {
        sitesLink.click();
        siteFinder.click();
    }

    public boolean isCreateSiteDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(createSite);
    }

    public void clickCreateSite()
    {
        sitesLink.click();
        createSite.click();
    }

    public boolean isFavoritesDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(favorites);
    }

    public boolean isAddCurrentSiteToFavoritesDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(addCurrentSiteToFavorites);
    }

    public boolean isRemoveCurrentSiteFromFavoritesDisplayed()
    {
        sitesLink.click();
        return browser.isElementDisplayed(removeCurrentSiteFromFavorites);
    }

    public void clickAddCurrentSiteToFavorites()
    {
        sitesLink.click();
        browser.findElement(addCurrentSiteToFavorites).click();
        browser.refresh();
    }

    public void clickRemoveCurrentSiteFromFavorites()
    {
        sitesLink.click();
        browser.findElement(removeCurrentSiteFromFavorites).click();
        browser.refresh();
    }

    public boolean isSiteFavorite(String siteName)
    {
        sitesLink.click();
        favorites.click();
        return favoriteSitesRowList.size() > 0 && browser.findFirstElementWithValue(favoriteSitesRowList, siteName) != null;
    }

    public void clickFavoriteSite(String siteName)
    {
        sitesLink.click();
        favorites.click();
        browser.findFirstElementWithValue(favoriteSitesRowList, siteName).click();
    }
}
