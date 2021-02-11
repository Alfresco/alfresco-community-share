package org.alfresco.po.share.dashlet;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.SitesFilter;
import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class MySitesDashlet extends Dashlet<MySitesDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.my-sites");
    private final By sitesLinksList = By.cssSelector("h3.site-title > a");
    private final By sitesFilterButton = By.cssSelector("div[class*='my-sites'] div span span button");
    private final By myFavoritesOptions = By.cssSelector("div[class*='my-sites'] div.bd ul li");
    private final By createSiteLink = By.cssSelector("a[id$='createSite-button']");
    private final By defaultSiteEmptyText = By.cssSelector("div[class^='dashlet my-sites'] div[class*='empty']");
    private final By siteTitleElement = By.cssSelector(".site-title a");
    private final By favoriteEnabled = By.cssSelector("span[class='item item-social'] a[class$='enabled']");
    private final By deleteButton = By.cssSelector("a[class^='delete-site']");
    private final By favoriteAction = By.cssSelector("a[class^='favourite-action']");

    private final String siteRow = "//div[starts-with(@class,'dashlet my-sites')]//a[text()='%s']/../../../..";

    public MySitesDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
                .findElement(dashletTitle));
    }

    private WebElement getSiteRow(String siteName)
    {
        return webElementInteraction.waitWithRetryAndReturnWebElement(
                By.xpath(String.format(siteRow, siteName)), WAIT_2.getValue(), RETRY_TIME_80.getValue());
    }

    public MySitesDashlet assertSiteIsDisplayed(SiteModel siteModel)
    {
        log.info("Assert site {} is  displayed", siteModel.getTitle());
        assertTrue(webElementInteraction.isElementDisplayed(getSiteRow(siteModel.getTitle())),
                String.format("Site %s is not displayed", siteModel.getTitle()));
        return this;
    }

    public MySitesDashlet assertSiteIsNotDisplayed(SiteModel site)
    {
        log.info("Assert site {} is not displayed", site.getTitle());
        By siteElement = By.xpath(String.format(siteRow, site.getTitle()));
        webElementInteraction.waitUntilElementDisappears(siteElement);
        assertFalse(webElementInteraction.isElementDisplayed(siteElement), String.format("Site %s is displayed", site.getTitle()));

        return this;
    }

    public WebElement selectSite(final String siteName)
    {
        return webElementInteraction.findFirstElementWithValue(sitesLinksList, siteName);
    }

    public WebElement selectSiteDetailsRow(final String siteName)
    {
        return webElementInteraction.findFirstElementWithValue(By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']"), siteName);
    }

    public boolean isSiteFavorite(String siteName)
    {
        selectSiteDetailsRow(siteName);
        return webElementInteraction.isElementDisplayed(favoriteEnabled);
    }

    public DeleteSiteDialog clickDelete(String siteName)
    {
        WebElement siteRowElement = getSiteRow(siteName);
        webElementInteraction.mouseOver(createSiteLink);
        webElementInteraction.mouseOver(siteRowElement.findElement(siteTitleElement));
        webElementInteraction.clickElement(siteRowElement.findElement(deleteButton));
        return new DeleteSiteDialog(webDriver);
    }

    public DeleteSiteDialog clickDelete(SiteModel site)
    {
        return clickDelete(site.getTitle());
    }

    public MySitesDashlet selectOptionFromSiteFilters(SitesFilter sitesFilter)
    {
        webElementInteraction.waitUntilElementIsVisible(sitesFilterButton);
        webElementInteraction.clickElement(sitesFilterButton);

        List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(myFavoritesOptions);
        webElementInteraction.selectOptionFromFilterOptionsList(getFilterValue(sitesFilter), options);
        return this;

    }

    public MySitesDashlet clickFavorite(String siteName)
    {
        webElementInteraction.clickElement(getSiteRow(siteName).findElement(favoriteAction));
        return this;
    }

    public MySitesDashlet clickFavorite(SiteModel site)
    {
        return clickFavorite(site.getTitle());
    }

    public MySitesDashlet assertCreateSiteButtonIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(createSiteLink);
        assertTrue(webElementInteraction.isElementDisplayed(createSiteLink), "Create site button is displayed");
        return this;
    }

    public CreateSiteDialog clickCreateSiteButton()
    {
        webElementInteraction.clickElement(createSiteLink);
        return new CreateSiteDialog(webDriver);
    }

    public boolean isSitePresent(String siteName)
    {
//        webElementInteraction.waitUntilElementIsVisible(sitesFilterButton);
        try
        {
            WebElement siteLink = selectSite(siteName);
            if (siteLink != null)
            {
                return true;
            }
        } catch (TimeoutException | NoSuchElementException e)
        {
            log.error("Time out while finding site", e);
            return false;
        }
        return false;
    }

    public MySitesDashlet assertSitesFilterButtonIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(sitesFilterButton);
        assertTrue(webElementInteraction.isElementDisplayed(sitesFilterButton), "Sites filter button is displayed");
        return this;
    }

    private String getFilterValue(SitesFilter filter)
    {
        String filterValue = "";
        switch (filter)
        {
            case ALL:
                filterValue = language.translate("mySitesDashlet.filter.all");
                break;
            case RECENT:
                filterValue = language.translate("mySitesDashlet.filter.recent");
                break;
            case MY_FAVORITES:
                filterValue = language.translate("mySitesDashlet.filter.favorites");
                break;
            default:
                break;
        }
        return filterValue;
    }

    public MySitesDashlet assertSelectedFilterIs(SitesFilter filter)
    {
        String value = webElementInteraction.getElementText(sitesFilterButton);
        assertEquals(value.substring(0, value.length() - 2),
                getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MySitesDashlet assertEmptySitesMessageIsDisplayed()
    {
        assertEquals(webElementInteraction.getElementText(defaultSiteEmptyText), language.translate("mySitesDashlet.noSites"));
        return this;
    }

    public void accessSite(String siteName)
    {
        webElementInteraction.clickElement(getSiteRow(siteName).findElement(siteTitleElement));
    }

    public SiteDashboardPage accessSite(SiteModel site)
    {
        accessSite(site.getTitle());
        return new SiteDashboardPage(webDriver);
    }

    public void hoverSite(String siteName)
    {
        WebElement siteRowElement = selectSiteDetailsRow(siteName);
        webElementInteraction.mouseOver(siteRowElement);
    }

    public boolean isDeleteButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(deleteButton);
    }
}