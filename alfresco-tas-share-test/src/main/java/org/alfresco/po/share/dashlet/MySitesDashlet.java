package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

@PageObject
public class MySitesDashlet extends Dashlet<MySitesDashlet>
{
    @RenderWebElement
    @FindBy(css = "div.dashlet.my-sites")
    protected HtmlElement dashletContainer;

    @FindBy(css = "div[id$='default-sites']")
    protected HtmlElement sitesListContainer;

    @FindAll(@FindBy(css = "h3.site-title > a"))
    protected List<WebElement> sitesLinksList;

    @FindAll(@FindBy(css = "div[id$='default-sites'] tr[class*='yui-dt-rec']"))
    protected List<HtmlElement> siteRowList;

    @FindAll(@FindBy(css = "div#prompt div.ft span span button"))
    protected List<WebElement> confirmationDeleteButtons;

    @FindBy(css = "div[class*='my-sites'] div span span button")
    protected WebElement myFavoritesButton;

    @FindAll(@FindBy(css = "div[class*='my-sites'] div.bd ul li"))
    protected List<WebElement> myfavoritesOptions;

    @FindBy(css = "div[class*='my-sites'] div span span a")
    protected Link createSiteLink;

    @FindBy(css = "div[class*='my-sites'] div[class*='empty']")
    protected HtmlElement defaultSiteText;

    private By deleteButton = By.cssSelector("a[class^='delete-site']");

    public enum SitesFilter
    {
        All,
        MyFavorites
        {
            public String toString()
            {
                return "My Favorites";
            }
        },
        Recent
    }

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    @Override
    public String getRelativePath()
    {
        return super.getRelativePath();
    }

    /**
     * Get list of sites link displayed in my sites dashlet.
     */
    public List<WebElement> getSitesLinks()
    {
        return sitesLinksList;
    }

    /**
     * Retrieves the link that match the site name.
     * 
     * @param name identifier
     * @return {@link Link} that matches siteName
     */
    public WebElement selectSite(final String siteName)
    {
        return browser.findFirstElementWithValue(sitesLinksList, siteName);
    }

    /**
     * Get list of entire site sections (that contains site details) displayed in my sites dashlet.
     */
    public List<HtmlElement> getSitesSections()
    {
        return siteRowList;
    }

    /**
     * Retrieves the link that match the site name.
     * 
     * @param name identifier
     * @return {@link Link} that matches siteName
     */
    public WebElement selectSiteDetailsRow(final String siteName)
    {
        return browser.findFirstElementWithValue(By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']"), siteName);
    }

    /**
     * Returns if the site is favorite.
     * 
     * @param siteName Site Name checked for is Favorite.
     * @return boolean
     */
    public boolean isSiteFavourite(String siteName)
    {
        try
        {
            Parameter.checkIsMandotary("Site name", siteName);
            WebElement siteRow = selectSiteDetailsRow(siteName);

            // If site is favourite, anchor does not contain any text. Checking
            // length of text rather than string 'Favourite' to support i18n.
            return !(siteRow.findElement(By.cssSelector("a[class^='favourite-action']")).getText().length() > 1);
        }
        catch (NoSuchElementException | TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Delete site from the delete icon of My Sites Dashlet.
     * 
     * @param siteName String
     * @return HtmlPage
     */
    public void clickDeleteSiteIconForSite(String siteName)
    {
        Parameter.checkIsMandotary("Site name", siteName);
        WebElement siteRow = selectSiteDetailsRow(siteName);
        browser.mouseOver(siteRow);
        siteRow.findElement(deleteButton).click();
    }

    /**
     * Click on button on Delete Site dialog.
     * 
     * @param deleteOption String Delete/Cancel/Yes/No options
     */
    public void confirmDeleteSite(String deleteOption)
    {
        Parameter.checkIsMandotary("Delete Option for Delete Site dialog", deleteOption);
        WebElement buttonToBeClicked = browser.findFirstElementWithExactValue(confirmationDeleteButtons, deleteOption);
        Parameter.checkIsMandotary("Button for Delete Site dialog", buttonToBeClicked);
        buttonToBeClicked.click();
    }

    /**
     * Select an option from "All" dropdown from My Sites Dashlets.
     * 
     * @param type FavouriteType
     * @return HtmlPage
     */
    public MySitesDashlet selectOptionFromSiteFilters(String siteOption)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(myFavoritesButton);
        try
        {
            myFavoritesButton.click();
            browser.selectOptionFromFilterOptionsList(siteOption, myfavoritesOptions);
            Assert.assertTrue(browser.isOptionSelectedForFilter(siteOption, myFavoritesButton));
            return (MySitesDashlet) this.renderedPage();
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("My Sites option not present" + nse.getMessage());
            throw new PageOperationException(siteOption + " option not present.");
        }

    }

    /**
     * Click on "Favorite" link for a site in My Sites Dashlet.
     * 
     * @param siteName String
     */
    public void clickOnFavoriteLink(String siteName)
    {
        Parameter.checkIsMandotary("Site name", siteName);
        try
        {
            WebElement siteRow = (WebElement) selectSiteDetailsRow(siteName);
            browser.mouseOver(siteRow);
            siteRow.findElement(By.cssSelector("a[class*='favourite-action']")).click();
        }
        catch (NoSuchElementException nse)
        {
            LOG.error("My Site  Dashlet is not present", nse);
        }
    }

    /**
     * Method to verify Create Site button is displayed
     */
    public boolean isCreateSiteButtonDisplayed()
    {
        try
        {
            return createSiteLink.isDisplayed();
        }
        catch (TimeoutException createSiteLink)
        {

        }
        return false;
    }

    /**
     * Click on Create Site button
     */
    public void clickCreateSiteButton()
    {
        createSiteLink.click();
    }

    /**
     * Method to check if a site name is displayed in My Sites Dashlet
     *
     * @param siteName String
     * @return True if Site exists
     */
    public boolean isSitePresent(String siteName)
    {
        browser.waitUntilElementVisible(myFavoritesButton);
        try
        {
            WebElement siteLink = selectSite(siteName);
            if (siteLink != null)
            {
                return true;
            }
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOG.error("Time out while finding site", e);
            return false;
        }
        return false;
    }

    /**
     * Check that all site filters are available in My Sites dashlet
     * 
     * @return true if filter has proper values
     */
    public boolean isMySitesFilterDisplayed()
    {
        boolean isFilterValid = false;

        myFavoritesButton.click();

        for (WebElement typeFav : myfavoritesOptions)
        {
            String value = typeFav.getText();
            if (value.equals(SitesFilter.MyFavorites.toString()))
            {
                value = SitesFilter.MyFavorites.name();
            }

            if (EnumUtils.isValidEnum(SitesFilter.class, value))
            {
                isFilterValid = true;
            }
            else
            {
                isFilterValid = false;
                break;
            }
        }

        return isFilterValid;
    }

    /**
     * Get displayed text when there is no site listed
     * 
     * @return
     */
    public String getDefaultSiteText()
    {
        return defaultSiteText.getText();
    }

    /**
     * Open site page
     * 
     * @param siteName
     * @return
     */
    public SiteDashboardPage accessSite(final String siteName)
    {
        browser.findFirstElementWithValue(sitesLinksList, siteName).click();
        return new SiteDashboardPage();
    }

    /**
     * Get number of sites in My Sites dashlet
     * 
     * @return
     */
    public int getNumberOfSites()
    {
        return getSitesLinks().size();
    }

    public void hoverSite(String siteName)
    {
        Parameter.checkIsMandotary("Site name", siteName);
        WebElement siteRow = selectSiteDetailsRow(siteName);
        browser.mouseOver(siteRow);
    }

    public boolean isDeleteButtonDisplayed(String siteName)
    {
        return browser.isElementDisplayed(deleteButton);
    }
}