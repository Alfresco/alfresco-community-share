package org.alfresco.po.share;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageObject
public class SiteFinderPage extends SharePage<SiteFinderPage> implements AccessibleByMenuBar
{
    @Autowired
    ToolbarSitesMenu toolbarSitesMenu;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    @FindBy(css = "div[class$=search-button] button")
    private WebElement searchButton;

    @RenderWebElement
    @FindBy(css = "div[class$=search-text] input")
    private WebElement searchField;

    @RenderWebElement
    @FindBy(className = "results")
    private WebElement resultsPane;

    @FindAll(@FindBy(css = "div[id$='default-sites'] tr[class*='yui-dt-rec']"))
    protected List<WebElement> siteRowList;

    @FindBy(css = "tbody[class*='message'] div")
    private WebElement searchMessage;

    @FindBy(css = "div[class='sitedescription']")
    private List<WebElement> siteDescriptionList;

    @FindBy(css="td[class*='yui-dt-col-button'] button")
    private WebElement cancelRequestButton;

    private By siteVisibility = By.cssSelector("span[class*='visibility']");
    private By siteNameLink = By.cssSelector("h3.sitename > a");

    @Override
    public String getRelativePath()
    {
        return "share/page/site-finder";
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteFinderPage navigateByMenuBar()
    {
        toolbarSitesMenu.clickSiteFinder();
        return (SiteFinderPage) renderedPage();
    }

    public void searchSite(String site) {
        searchField.clear();
        searchField.sendKeys(site);
        searchButton.click();
        this.renderedPage();
    }

    /**
     * Check if the site was found
     *
     * @param siteName String
     * @return true if the site was found, else return false
     */
    public boolean checkSiteWasFound(String siteName)
    {
        return selectSite(siteName) != null;
    }

    /**
     * Retrieves the link that match the site name.
     *
     * @param siteName String
     * @return WebElement that match the site name
     */
    public WebElement selectSite(final String siteName)
    {
        getBrowser().waitUntilElementsVisible(siteRowList);
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
        return siteRow.findElements(By.cssSelector("button"));
    }

    /**
     * Check if a site has the specified button
     *
     * @param siteName String
     * @param buttonName String
     * @return true if button is displayed for site, false otherwise
     */
    public boolean isButtonDisplayedForSite(String siteName, String buttonName)
    {
        for (WebElement button : getTheButtonsForSite(siteName))
            if (button.getText().equals(buttonName))
                return true;
        return false;
    }

    /**
     * Click the specified site's button
     *
     * @param siteName String
     * @return HtmlPage
     */
    public void clickSiteButton(String siteName, String buttonName) {
        getBrowser().waitUntilElementsVisible(getTheButtonsForSite(siteName));
        for (WebElement button : getTheButtonsForSite(siteName))
            if (button.getText().equals(buttonName)) {
                browser.waitUntilElementClickable(button, 30).click();
                browser.waitInSeconds(5);
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

    public String getButtonCancelRequestText(String textExpected) {
        getBrowser().waitUntilElementContainsText(cancelRequestButton, textExpected);
        return cancelRequestButton.getText();
    }

    public void clickRequestToJoinButton(String siteName) {
        getBrowser().waitUntilElementClickable(By.cssSelector("td[class*='yui-dt-col-button'] button"), 6L).click();
    }
}
