package org.alfresco.po.share;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import java.util.List;

public class SiteFinderPage extends SharePage2<SiteFinderPage> implements AccessibleByMenuBar
{
    protected By siteRowList = By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']");
    @RenderWebElement
    private By searchButton = By.cssSelector("div[class$=search-button] button");
    @RenderWebElement
    private By searchField = By.cssSelector("div[class$=search-text] input");
    private By searchMessage = By.cssSelector("tbody[class*='message'] div");
    private By siteDescriptionList = By.cssSelector("div[class='sitedescription']");
    private By cancelRequestButton = By.cssSelector("td[class*='yui-dt-col-button'] button");
    private By siteVisibility = By.cssSelector("span[class*='visibility']");
    private By siteNameLink = By.cssSelector("h3.sitename > a");

    public SiteFinderPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/site-finder";
    }

    public SiteFinderPage assertSiteFinderPageIsOpened()
    {
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "Site Finder page is opened");
        return this;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SiteFinderPage navigateByMenuBar()
    {
        return (SiteFinderPage) new Toolbar(browser).clickSites().clickSiteFinder().renderedPage();
    }

    public void searchSite(String site)
    {
        clearAndType(getBrowser().findElement(searchField), site);
        getBrowser().waitUntilElementClickable(searchButton).sendKeys(Keys.ENTER);
        this.renderedPage();
    }

    public boolean isSiteFound(String siteName)
    {
        return getBrowser().isElementDisplayed(selectSite(siteName));
    }

    public void searchSiteWithRetry(String siteName)
    {
        int retry = 0;
        searchSite(siteName);
        boolean found = isSiteFound(siteName);
        while (retry < 10 && !found)
        {
            searchSite(siteName);
            found = isSiteFound(siteName);
            retry++;
            Utility.waitToLoopTime(1, String.format("Waiting for site %s to be found.", siteName));
        }
    }

    public boolean checkSiteWasFound(String siteName)
    {
        int counter = 0;
        try
        {
            int retry = 0;
            if (retry < 3 && selectSite(siteName) == null)
            {
                searchSite(siteName);
                if (selectSite(siteName) == null)
                    LOG.info("site was not was found");
                else
                    LOG.info(selectSite(siteName).toString() + " was found");
                retry++;
            }
            return selectSite(siteName) != null;
        } catch (TimeoutException e)
        {
            while (!isSiteFound(siteName) && counter < 5)
            {
                getBrowser().refresh();
                this.renderedPage();
                getBrowser().findElement(searchButton).click();
                getBrowser().waitUntilElementIsVisibleWithRetry(By.cssSelector("div[id$='_default-sites'] tr[class^='yui-dt-rec']"), 3);
                LOG.info("Site not found " + e.getMessage().toString());
                counter++;
                return selectSite(siteName) != null;
            }
        }
        return false;
    }

    public WebElement selectSite(final String siteName)
    {
        return getBrowser().findFirstElementWithValue(siteRowList, siteName);
    }

    /**
     * Get the site's buttons
     *
     * @param siteName
     * @return
     */
    public List<WebElement> getTheButtonsForSite(String siteName)
    {
        Parameter.checkIsMandotary("Site name", siteName);
        WebElement siteRow = selectSite(siteName);
        return getBrowser().waitUntilElementsVisible(siteRow.findElements(By.cssSelector("button")));
    }

    /**
     * Check if a site has the specified button
     *
     * @param siteName   String
     * @param buttonName String
     * @return true if button is displayed for site, false otherwise
     */
    public boolean isButtonDisplayedForSite(String siteName, String buttonName)
    {
        getBrowser().waitUntilElementsVisible(getTheButtonsForSite(siteName));
        for (WebElement button : getTheButtonsForSite(siteName))
        {
            if (button.getText().equals(buttonName))
                return true;
        }
        return false;
    }

    /**
     * Click the specified site's button
     *
     * @param siteName String
     * @return HtmlPage
     */
    public void clickSiteButton(String siteName, String buttonName)
    {
        getBrowser().waitUntilElementsVisible(getTheButtonsForSite(siteName));
        for (WebElement button : getTheButtonsForSite(siteName))
            if (button.getText().equals(buttonName))
            {
                getBrowser().waitUntilElementClickable(button, 30).click();
                if (!buttonName.equals("Delete"))
                    getBrowser().waitUntilElementDoesNotContainText(button, buttonName);
                break;
            }
    }

    public SiteDashboardPage accessSite(final String siteName)
    {
        selectSite(siteName).findElement(siteNameLink).click();
        return (SiteDashboardPage) new SiteDashboardPage(browser).renderedPage();
    }

    public boolean isSearchFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(searchField);
    }

    public SiteFinderPage assertSearchFieldIsDisplayed()
    {
        Assert.assertTrue(getBrowser().isElementDisplayed(searchField), "Search field is displayed");
        return this;
    }

    public boolean isSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton);
    }

    public String getSearchMessage()
    {
        return getBrowser().findElement(searchMessage).getText();
    }

    public boolean isSearchResultsListDisplayed()
    {
        return getBrowser().findElements(siteRowList).size() > 0;
    }

    public boolean isSiteNameListDisplayed()
    {
        return getBrowser().findDisplayedElementsFromLocator(siteNameLink).size() > 0;
    }

    public boolean isSiteDescriptionListDisplayed()
    {
        return getBrowser().findElements(siteDescriptionList).size() > 0;
    }

    public boolean isSiteVisibilityListDisplayed()
    {
        return getBrowser().isElementDisplayed(siteVisibility);
    }

    public String getVisibilityLabel()
    {
        return getBrowser().waitUntilElementVisible(siteVisibility).getText();
    }

    public String getButtonCancelRequestText(String textExpected)
    {
        WebElement cancelButton = getBrowser().findElement(cancelRequestButton);
        getBrowser().waitUntilElementContainsText(cancelButton, textExpected);
        return cancelButton.getText();
    }
}