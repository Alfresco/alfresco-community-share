package org.alfresco.po.share.toolbar;

import java.util.List;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

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

    @FindBy (css = "div[id='HEADER_SITES_MENU_dropdown']")
    private WebElement sitesDopdown;

    @FindAll (@FindBy (css = "td[id^='HEADER_SITES_MENU_FAVOURITE_']"))
    private List<WebElement> favoriteSitesRowList;

    @FindBy (id = "HEADER_SITES_MENU_FAVOURITES_dropdown")
    private WebElement favoriteDropDown;


    private By favoritesDropDown = By.id("HEADER_SITES_MENU_FAVOURITES_dropdown");
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


    /**
     * This method is checking if a given site is marked as favorite.
     * if (!isSitesDopdownDisplayed()) - used to check if the Sites dropdown menu is displayed.
     *
     * @param siteName - the name of the site that is checked
     * @return true if the site is displayed in Sites -> Favorites dropdown menu
     * false if the site is not displayed in Sites -> Favorites dropdown menu
     */
    public boolean isSiteFavorite(String siteName)
    {
        if (!isSitesDopdownDisplayed())
        {
            sitesLink.click();
            browser.waitUntilElementVisible(sitesDopdown);
        }

        favorites.click();
        browser.findElement(favoritesDropDown);
        browser.waitUntilWebElementIsDisplayedWithRetry(favoriteDropDown);
        return favoriteSitesRowList.size() > 0 && browser.findFirstElementWithValue(favoriteSitesRowList, siteName) != null;
    }

    /**
     * This method is checking if Sites dropdown menu is displayed.
     *
     * @return true if Sites dropdown is displayed
     * false if Sites dropdown is displayed
     */
    public boolean isSitesDopdownDisplayed()
    {
        return browser.isElementDisplayed(sitesDopdown);
    }

    public void clickFavoriteSite(String siteName)
    {
        sitesLink.click();
        favorites.click();
        browser.findFirstElementWithValue(favoriteSitesRowList, siteName).click();
    }
}
