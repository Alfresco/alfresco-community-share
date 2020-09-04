package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.CreateSiteDialog;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.utility.exception.PageOperationException;
import org.alfresco.utility.model.SiteModel;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;

@PageObject
public class MySitesDashlet extends Dashlet<MySitesDashlet>
{
    @Autowired
    private SiteDashboardPage siteDashboardPage;

    @Autowired
    private CreateSiteDialog createSiteDialog;

    @Autowired
    private DeleteSiteDialog deleteSiteDialog;

    @RenderWebElement
    @FindBy (css = "div.dashlet.my-sites")
    protected HtmlElement dashletContainer;

    @RenderWebElement
    @FindBy (css = "div[id$='default-sites']")
    protected HtmlElement sitesListContainer;

    @FindAll (@FindBy (css = "h3.site-title > a"))
    protected List<WebElement> sitesLinksList;

    @FindAll (@FindBy (css = "div[id$='default-sites'] tr[class*='yui-dt-rec']"))
    protected List<HtmlElement> siteRowList;

    @FindAll (@FindBy (css = "div#prompt div.ft span span button"))
    protected List<WebElement> confirmationDeleteButtons;

    @FindBy (css = "div[class*='my-sites'] div span span button")
    protected WebElement sitesFilterButton;

    @FindAll (@FindBy (css = "div[class*='my-sites'] div.bd ul li"))
    protected List<WebElement> myfavoritesOptions;

    @FindBy (css = "a[id$='createSite-button']")
    protected WebElement createSiteLink;

    @FindBy (css = "div[class^='dashlet my-sites'] div[class*='empty']")
    protected WebElement defaultSiteEmptyText;

    private String siteRow = "//a[text()='%s']/../../../..";
    private By siteTitleElement = By.cssSelector(".site-title a");
    private By favoriteEnabled = By.cssSelector("span[class='item item-social'] a[class$='enabled']");
    private By deleteButton = By.cssSelector("a[class^='delete-site']");
    private By favoriteAction = By.cssSelector("a[class^='favourite-action']");

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

    private WebElement getSiteRow(String siteName)
    {
        return browser.waitWithRetryAndReturnWebElement(
            By.xpath(String.format(siteRow, siteName)), 1, 10);
    }

    public MySitesDashlet assertSiteIsDisplayed(SiteModel siteModel)
    {
        Assert.assertTrue(browser.isElementDisplayed(getSiteRow(siteModel.getTitle())),
            String.format("Site %s is displayed", siteModel.getTitle()));
        return this;
    }

    public MySitesDashlet assertSiteIsNotDisplayed(SiteModel site)
    {
        Assert.assertFalse(browser.isElementDisplayed(By.xpath(String.format(siteRow, site.getTitle()))),
            String.format("Site %s is displayed", site.getTitle()));
        return this;
    }

    /**
     * Retrieves the link that match the site name.
     *
     * @param siteName identifier
     * @return {@link Link} that matches siteName
     */
    public WebElement selectSite(final String siteName)
    {
        return browser.findFirstElementWithValue(sitesLinksList, siteName);
    }

    /**
     * Retrieves the link that match the site name.
     *
     * @param siteName identifier
     * @return {@link Link} that matches siteName
     */
    public WebElement selectSiteDetailsRow(final String siteName)
    {
        return browser.findFirstElementWithValue(By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']"), siteName);
    }

    public boolean isSiteFavorite(String siteName)
    {
        Parameter.checkIsMandotary("Site name", siteName);
        selectSiteDetailsRow(siteName);
        return getBrowser().isElementDisplayed(favoriteEnabled);
    }
    
    public DeleteSiteDialog clickDelete(String siteName)
    {
        WebElement siteRow = getSiteRow(siteName);
        browser.mouseOver(createSiteLink);
        browser.mouseOver(siteRow.findElement(siteTitleElement));
        siteRow.findElement(deleteButton).click();
        return (DeleteSiteDialog) deleteSiteDialog.renderedPage();
    }

    public DeleteSiteDialog clickDelete(SiteModel site)
    {
        return clickDelete(site.getTitle());
    }

    public MySitesDashlet selectOptionFromSiteFilters(SitesFilter sitesFilter)
    {
        browser.waitUntilElementVisible(sitesFilterButton);
        sitesFilterButton.click();
        browser.selectOptionFromFilterOptionsList(getFilterValue(sitesFilter), myfavoritesOptions);
        return (MySitesDashlet) this.renderedPage();

    }

    public MySitesDashlet clickFavorite(String siteName)
    {
        getSiteRow(siteName).findElement(favoriteAction).click();
        return this;
    }

    public MySitesDashlet clickFavorite(SiteModel site)
    {
        return clickFavorite(site.getTitle());
    }

    public MySitesDashlet assertCreateSiteButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(createSiteLink), "Create site button is displayed");
        return this;
    }

    /**
     * Click on Create Site button
     */
    public CreateSiteDialog clickCreateSiteButton()
    {
        getBrowser().waitUntilElementClickable(createSiteLink).click();
        return (CreateSiteDialog) createSiteDialog.renderedPage();
    }

    /**
     * Method to check if a site name is displayed in My Sites Dashlet
     *
     * @param siteName String
     * @return True if Site exists
     */
    public boolean isSitePresent(String siteName)
    {
        browser.waitUntilElementVisible(sitesFilterButton);
        try
        {
            WebElement siteLink = selectSite(siteName);
            if (siteLink != null)
            {
                return true;
            }
        } catch (TimeoutException | NoSuchElementException e)
        {
            LOG.error("Time out while finding site", e);
            return false;
        }
        return false;
    }

    public MySitesDashlet assertSitesFilterButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(sitesFilterButton), "Sites filter button is displayed");
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
        Assert.assertEquals(sitesFilterButton.getText().substring(0, sitesFilterButton.getText().length() - 2),
            getFilterValue(filter), "Selected filter is correct");
        return this;
    }

    public MySitesDashlet assertEmptySitesMessageIsDisplayed()
    {
        Assert.assertEquals(defaultSiteEmptyText.getText(), language.translate("mySitesDashlet.noSites"));
        return this;
    }

    /**
     * Open site page
     *
     * @param siteName
     * @return SiteDashboardPage
     */
    public SiteDashboardPage accessSite(String siteName)
    {
        getSiteRow(siteName).findElement(siteTitleElement).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public SiteDashboardPage accessSite(SiteModel site)
    {
        return accessSite(site.getTitle());
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

    public enum SitesFilter
    {
        ALL,
        MY_FAVORITES,
        RECENT
    }
}
