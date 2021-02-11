package org.alfresco.po.share;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteFinderPage extends SharePage2<SiteFinderPage> implements AccessibleByMenuBar
{
    private final By siteRowList = By.cssSelector("div[id$='default-sites'] tr[class*='yui-dt-rec']");
    private final By searchSiteButton = By.cssSelector("div[class$=search-button] button");
    private final By searchSiteInput = By.cssSelector("div[class$=search-text] input");
    private final By searchMessage = By.cssSelector("tbody[class*='message'] div");
    private final By siteDescriptionList = By.cssSelector("div[class='sitedescription']");
    private final By siteVisibility = By.cssSelector("span[class*='visibility']");
    private final By siteNameLink = By.cssSelector("h3.sitename > a");
    private final By requestToJoinButton = By.xpath(".//button[text()='Request to Join']");

    private final String siteNamePath = "//tr[contains(@class, 'yui-dt-even')]//a[text()='%s']/../../../..";

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

    public boolean isSiteNameDisplayed(String siteName)
    {
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(siteNamePath, siteName)));
    }

    private WebElement getSiteRow(String siteName)
    {
        return webElementInteraction
            .waitWithRetryAndReturnWebElement(By.xpath(String.format(siteNamePath, siteName)),
                WAIT_2.getValue(), RETRY_TIME_80.getValue());
    }

    public SiteFinderPage searchSiteWithName(String siteName)
    {
        WebElement searchInput =  webElementInteraction.findElement(searchSiteInput);
        webElementInteraction.clearAndType(searchInput, siteName);
        webElementInteraction.clickElement(searchSiteButton);
        searchSiteWithRetry(siteName);

        return this;
    }

    private void searchSiteWithRetry(String siteName)
    {
        int retryCounter = 0;
        while (!isSiteNameDisplayed(siteName) && retryCounter < RETRY_TIME_80.getValue())
        {
            log.warn("Site {} not displayed - retry: {}", siteName, retryCounter);
            webElementInteraction.clickElement(searchSiteButton);
            waitToLoopTime(WAIT_2.getValue());
            retryCounter++;
        }
        throwExceptionIfUsernameNotDisplayed(isSiteNameDisplayed(siteName), retryCounter);
    }

    private void throwExceptionIfUsernameNotDisplayed(boolean isUsernameDisplayed, int retryCount)
    {
        if (!isUsernameDisplayed)
        {
            throw new NoSuchElementException(
                String.format("Site name was not displayed in the given retries %d", retryCount));
        }
    }

    public WebElement selectSite(String siteName)
    {
        return webElementInteraction.findFirstElementWithValue(siteRowList, siteName);
    }

    public List<WebElement> getTheButtonsForSite(String siteName)
    {
        WebElement siteRow = selectSite(siteName);
        return webElementInteraction.waitUntilElementsAreVisible(siteRow.findElements(By.cssSelector("button")));
    }

    public boolean isButtonDisplayedForSite(String siteName, String buttonName)
    {
        webElementInteraction.waitUntilElementsAreVisible(getTheButtonsForSite(siteName));
        for (WebElement button : getTheButtonsForSite(siteName))
        {
            if (button.getText().equals(buttonName))
            {
                return true;
            }
        }
        return false;
    }

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

    public void requestToJoinSite(String siteName)
    {
        webElementInteraction.clickElement(getSiteRow(siteName).findElement(requestToJoinButton));
        waitUntilNotificationMessageDisappears();
        if(webElementInteraction.isElementDisplayed(notificationMessageLocator))
        {
            log.info("Wait for the second message");
            waitUntilNotificationMessageDisappears();
        }
    }

    public SiteDashboardPage accessSite(String siteName)
    {
        selectSite(siteName).findElement(siteNameLink);
        return new SiteDashboardPage(webDriver);
    }

    public boolean isSearchFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchSiteInput);
    }

    public boolean isSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchSiteButton);
    }

    public String getSearchMessage()
    {
        return webElementInteraction.findElement(searchMessage).getText();
    }

    public boolean isSearchResultsListDisplayed()
    {
        return !webElementInteraction.findElements(siteRowList).isEmpty();
    }

    public boolean isSiteNameListDisplayed()
    {
        return !webElementInteraction.findDisplayedElementsFromLocator(siteNameLink).isEmpty();
    }

    public boolean isSiteDescriptionListDisplayed()
    {
        return !webElementInteraction.findElements(siteDescriptionList).isEmpty();
    }

    public boolean isSiteVisibilityListDisplayed()
    {
        return webElementInteraction.isElementDisplayed(siteVisibility);
    }

    public String getVisibilityLabel()
    {
        return webElementInteraction.waitUntilElementIsVisible(siteVisibility).getText();
    }
}