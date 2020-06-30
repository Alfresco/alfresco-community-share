package org.alfresco.po.share;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class SiteFinderPage extends SharePage<SiteFinderPage> implements AccessibleByMenuBar
{
    @FindAll (@FindBy (css = "div[id$='default-sites'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> siteRowList;
    @Autowired
    ToolbarSitesMenu toolbarSitesMenu;
    @Autowired
    SiteDashboardPage siteDashboardPage;
    @RenderWebElement
    @FindBy (css = "div[class$=search-button] button")
    private WebElement searchButton;
    @RenderWebElement
    @FindBy (css = "div[class$=search-text] input")
    private WebElement searchField;
    @RenderWebElement
    @FindBy (className = "results")
    private WebElement resultsPane;
    @FindBy (css = "tbody[class*='message'] div")
    private WebElement searchMessage;

    @FindBy (css = "div[class='sitedescription']")
    private List<WebElement> siteDescriptionList;

    @FindBy (css = "td[class*='yui-dt-col-button'] button")
    private WebElement cancelRequestButton;

    private By siteVisibility = By.cssSelector("span[class*='visibility']");
    private By siteNameLink = By.cssSelector("h3.sitename > a");

    @Override
    public String getRelativePath()
    {
        return "share/page/site-finder";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SiteFinderPage navigateByMenuBar()
    {
        toolbarSitesMenu.clickSiteFinder();
        return (SiteFinderPage) renderedPage();
    }

    public void searchSite(String site)
    {
        Utils.clearAndType(searchField, site);
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
        while (retry < 10 && !isSiteFound(siteName))
        {
            searchSite(siteName);
            retry++;
            Utility.waitToLoopTime(1, String.format("Waiting for site %s to be found.", siteName));
        }
    }

    /**
     * Check if the site was found
     *
     * @param siteName String
     * @return true if the site was found, else return false
     */
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
                browser.refresh();
                this.renderedPage();
                searchButton.click();
                getBrowser().waitUntilElementIsVisibleWithRetry(By.cssSelector("div[id$='_default-sites'] tr[class^='yui-dt-rec']"), 3);
                LOG.info("Site not found " + e.getMessage().toString());
                counter++;
                return selectSite(siteName) != null;
            }
        }
        return false;
    }


    /**
     * Retrieves the link that match the site name.
     *
     * @param siteName String
     * @return WebElement that match the site name
     */
    public WebElement selectSite(final String siteName)
    {
        return browser.findFirstElementWithValue(siteRowList, siteName);
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
        return browser.waitUntilElementsVisible(siteRow.findElements(By.cssSelector("button")));
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
        browser.waitUntilElementsVisible(getTheButtonsForSite(siteName));
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
                browser.waitUntilElementClickable(button, 30).click();
                if (!buttonName.equals("Delete"))
                    browser.waitUntilElementDoesNotContainText(button, buttonName);
                break;
            }
    }

    /**
     * Open site page
     *
     * @param siteName
     * @return {@link}SiteDashboardPage
     */
    public SiteDashboardPage accessSite(final String siteName)
    {
        selectSite(siteName).findElement(siteNameLink).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }

    public boolean isSearchFieldDisplayed()
    {
        return browser.isElementDisplayed(searchField);
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    public String getSearchMessage()
    {
        return searchMessage.getText();
    }

    public boolean isSearchResultsListDisplayed()
    {
        return siteRowList.size() > 0;
    }

    public boolean isSiteNameListDisplayed()
    {
        return browser.findDisplayedElementsFromLocator(siteNameLink).size() > 0;
    }

    public boolean isSiteDescriptionListDisplayed()
    {
        return siteDescriptionList.size() > 0;
    }

    public boolean isSiteVisibilityListDisplayed()
    {
        return browser.isElementDisplayed(siteVisibility);
    }

    public String getVisibilityLabel()
    {
        return browser.waitUntilElementVisible(siteVisibility).getText();
    }

    public String getButtonCancelRequestText(String textExpected)
    {
        getBrowser().waitUntilElementContainsText(cancelRequestButton, textExpected);
        return cancelRequestButton.getText();
    }
}