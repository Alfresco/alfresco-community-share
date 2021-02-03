package org.alfresco.po.share;

import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteFinderPage extends SharePage2<SiteFinderPage> implements AccessibleByMenuBar
{
    private final By siteRowList = By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']");
    private final By searchButton = By.cssSelector("div[class$=search-button] button");
    private final By searchField = By.cssSelector("div[class$=search-text] input");
    private final By searchMessage = By.cssSelector("tbody[class*='message'] div");
    private final By siteDescriptionList = By.cssSelector("div[class='sitedescription']");
    private final By cancelRequestButton = By.cssSelector("td[class*='yui-dt-col-button'] button");
    private final By siteVisibility = By.cssSelector("span[class*='visibility']");
    private final By siteNameLink = By.cssSelector("h3.sitename > a");

    public SiteFinderPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/site-finder";
    }

    public SiteFinderPage assertSiteFinderPageIsOpened()
    {
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "Site Finder page is opened");
        return this;
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SiteFinderPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickSites().clickSiteFinder();
    }

    public void searchSite(String site)
    {
        webElementInteraction.clearAndType(searchField, site);
        webElementInteraction.waitUntilElementIsVisible(searchButton).sendKeys(Keys.ENTER);
    }

    public boolean isSiteFound(String siteName)
    {
        return webElementInteraction.isElementDisplayed(selectSite(siteName));
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
        try
        {
            if (selectSite(siteName) == null)
            {
                searchSite(siteName);
                if (selectSite(siteName) == null)
                    log.info("site was not was found");
                else
                    log.info(selectSite(siteName).toString() + " was found");
            }
            return selectSite(siteName) != null;
        } catch (TimeoutException e)
        {
            //todo needs refactor!
            while (!isSiteFound(siteName))
            {
                webElementInteraction.refresh();
                webElementInteraction.findElement(searchButton);
                webElementInteraction.waitUntilElementIsVisibleWithRetry(By.cssSelector("div[id$='_default-sites'] tr[class^='yui-dt-rec']"), 3);
                log.info("Site not found ", e.getMessage());
                return selectSite(siteName) != null;
            }
        }
        return false;
    }

    public WebElement selectSite(final String siteName)
    {
        return webElementInteraction.findFirstElementWithValue(siteRowList, siteName);
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
        return webElementInteraction.waitUntilElementsAreVisible(siteRow.findElements(By.cssSelector("button")));
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
        webElementInteraction.waitUntilElementsAreVisible(getTheButtonsForSite(siteName));
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
        webElementInteraction.waitUntilElementsAreVisible(getTheButtonsForSite(siteName));
        for (WebElement button : getTheButtonsForSite(siteName))
            if (button.getText().equals(buttonName))
            {
                webElementInteraction.clickElement(button);
                if (!buttonName.equals("Delete"))
                    webElementInteraction.waitUntilElementDoesNotContainText(button, buttonName);
                break;
            }
    }

    public SiteDashboardPage accessSite(final String siteName)
    {
        selectSite(siteName).findElement(siteNameLink);
        return new SiteDashboardPage(webDriver);
    }

    public boolean isSearchFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchField);
    }

    public boolean isSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchButton);
    }

    public String getSearchMessage()
    {
        return webElementInteraction.findElement(searchMessage).getText();
    }

    public boolean isSearchResultsListDisplayed()
    {
        return webElementInteraction.findElements(siteRowList).size() > 0;
    }

    public boolean isSiteNameListDisplayed()
    {
        return webElementInteraction.findDisplayedElementsFromLocator(siteNameLink).size() > 0;
    }

    public boolean isSiteDescriptionListDisplayed()
    {
        return webElementInteraction.findElements(siteDescriptionList).size() > 0;
    }

    public boolean isSiteVisibilityListDisplayed()
    {
        return webElementInteraction.isElementDisplayed(siteVisibility);
    }

    public String getVisibilityLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(siteVisibility).getText();
    }

    public String getButtonCancelRequestText(String textExpected)
    {
        WebElement cancelButton = webElementInteraction.findElement(cancelRequestButton);
        webElementInteraction.waitUntilElementContainsText(cancelButton, textExpected);
        return cancelButton.getText();
    }
}