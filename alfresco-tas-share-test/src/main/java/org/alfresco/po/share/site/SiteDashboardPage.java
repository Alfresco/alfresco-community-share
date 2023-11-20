package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.alfresco.dataprep.SiteService.Visibility;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author bogdan.bocancea
 */
@Slf4j
public class SiteDashboardPage extends SiteCommon<SiteDashboardPage>
{
    private final By siteHeaderTitle = By.cssSelector("#HEADER_TITLE a");
    private final By siteVisibility = By.cssSelector("div[id='HEADER_TITLE_VISIBILITY'] span");
    private final By morePagesDropDown = By.id("HEADER_SITE_MORE_PAGES");
    private final By moreOptions = By.cssSelector("#HEADER_SITE_MORE_PAGES_GROUP a");
    private final By siteDescription = By.xpath("//textarea[@name=\"description\"]");
    private final By saveButton = By.xpath("//span[text()=\"Save\"]");
    private final By siteDataListDashlet = By.xpath("//div[text()=\"Site Data Lists\"]");
    private final By dropdownoptions = By.xpath("//tr[@role=\"menuitem\"]");
    private final String dropdownOption = "//div[@class='alf-menu-groups' and contains(@style, 'visible')]//td[contains(@id, 'HEADER') and text()='%s']";
    private final String dashletLocation = "//div[text()='%s']/../../../div[contains(@id,'component-%d-%d')]";

    private final By clickCustomizeSite = By.xpath("//*[@id=\"HEADER_CUSTOMIZE_SITE_text\"]/a");

    public SiteDashboardPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/dashboard", getCurrentSiteName());
    }

    public void waitForSiteDashboardPageToBeLoaded()
    {
        waitUntilElementIsVisible(siteHeaderTitle);
    }

    public SiteDashboardPage assertSiteDashboardPageIsOpened()
    {
        log.info("Assert site dashboard page is opened");
        waitUntilElementIsVisible(siteVisibility);
        assertTrue(isElementDisplayed(siteVisibility), "Site dashboard page is opened");
        return this;
    }

    public SiteDashboardPage assertSiteHeaderTitleIs(SiteModel expectedSite)
    {
        assertEquals(getElementText(siteHeaderTitle), expectedSite.getTitle(), "Site header title is correct");
        return this;
    }

    public boolean isDashletAddedInPosition(final Dashlets dashlet, final int column, final int locationInColumn)
    {
        if (column < 1 || column > 4)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }
        if (locationInColumn < 1 || locationInColumn > 5)
        {
            throw new IllegalArgumentException("Location in column should be between 1 and 5");
        }
        if (dashlet.equals(Dashlets.WEB_VIEW))
        {
            return isElementDisplayed(By.xpath(String.format(
                "//div[@class='title']/span[contains(@id, 'component-%d-%d')][1]", column, locationInColumn)));
        }
        return isElementDisplayed(By.xpath(String.format(
            dashletLocation, dashlet.getDashletName(), column, locationInColumn)));
    }

    public SiteDashboardPage assertDashletIsAddedInPosition(Dashlets dashlet, int column, int locationInColumn)
    {
        log.info("Assert dashlet {} is added in column {} at position {}", dashlet.getDashletName(), column, locationInColumn);
        assertTrue(isDashletAddedInPosition(dashlet, column, locationInColumn));
        return this;
    }

    public SiteDashboardPage assertSiteVisibilityEqualsTo(String expectedSiteVisibility)
    {
        log.info("Assert site visibility is {}", expectedSiteVisibility);
        String actualSiteVisibility = getElementText(siteVisibility);
        assertTrue(actualSiteVisibility.equalsIgnoreCase(expectedSiteVisibility),
            String.format("Site visibility not equals to %s", expectedSiteVisibility));
        return this;
    }

    public SiteDashboardPage assertSiteVisibilityEqualsTo(Visibility expectedSiteVisibility)
    {
        return assertSiteVisibilityEqualsTo(expectedSiteVisibility.name());
    }

    public SiteDashboardPage assertSiteVisibilityIs(SiteService.Visibility visibility)
    {
        log.info("Assert site visibility is {}", visibility.toString());
        String actualSiteVisibility = getElementText(siteVisibility).toUpperCase();

        assertEquals(actualSiteVisibility, visibility.toString(),
            String.format("Site visibility not equals %s", visibility.toString()));

        return this;
    }

    public boolean isSiteVisibilityDisplayed()
    {
        return isElementDisplayed(siteVisibility);
    }

    private boolean isDropdownOptionEqualsTo(String expectedOption)
    {
        List<WebElement> siteConfigurations = waitUntilElementsAreVisible(configurationOptions);
        return isOptionEqualsTo(expectedOption, siteConfigurations);
    }

    private boolean isOptionEqualsTo(String expectedOption, List<WebElement> options)
    {
        for (WebElement option : options)
        {
            if (isOptionTextEqualsTo(expectedOption, option))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isOptionTextEqualsTo(String option, WebElement siteConfigurationOption)
    {
        return siteConfigurationOption.getText().equals(option);
    }

    public SiteDashboardPage assertOptionNotEqualsTo(String expectedOption)
    {
        log.info("Assert option not equals {}", expectedOption);
        assertFalse(isDropdownOptionEqualsTo(expectedOption),
            String.format("Option equals with %s ", expectedOption));
        return this;
    }

    public SiteDashboardPage assertOptionEqualsTo(String expectedOption)
    {
        log.info("Assert option equals to {}", expectedOption);
        assertTrue(isDropdownOptionEqualsTo(expectedOption),
            String.format("Option not equals to %s ", expectedOption));
        return this;
    }
    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public SiteDashboardPage selectOptionFromSiteConfigurationDropDown(String option)
    {
        log.info("Select option from site configuration dropdown {}", option);
        clickElement(By.xpath(String.format(dropdownOption, option)));
        return this;
    }

    public boolean isLinkDisplayedInMoreMenu(String link)
    {
        List<WebElement> moreOptionsList = waitUntilElementsAreVisible(moreOptions);
        for (WebElement option : moreOptionsList)
        {
            if (option.getText().equals(link))
            {
                return true;
            }
        }
        return false;
    }

    public void clickLinkFromMoreMenu(String link)
    {
        clickMoreLink();
        List<WebElement> moreOptionsList = waitUntilElementsAreVisible(moreOptions);
        moreOptionsList.stream()
            .filter(option -> option.getText().equals(link))
            .findFirst().ifPresent(WebElement::click);
    }

    public boolean isPageAddedToDashboard(SitePageType page)
    {
        if (isElementDisplayed(page.getDashboardLocator()))
        {
            return true;
        }
        else
        {
            if (isMoreLinkDisplayed())
            {
                clickMoreLink();
                return isElementDisplayed(page.getDashboardLocator());
            }
            return false;
        }
    }

    public void clickMoreLink()
    {
        waitUntilElementIsVisible(morePagesDropDown);
        clickElement(morePagesDropDown);
    }

    public void clickLinkFromHeaderNavigationMenu(SitePageType page)
    {
        findElement(page.getDashboardLocator()).findElement(By.cssSelector("a")).click();
    }

    public boolean isMoreLinkDisplayed()
    {
        return isElementDisplayed(morePagesDropDown);
    }

    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = findElement(By.cssSelector(page.getDashboardCssLocator()));
        return pageElem.findElement(By.cssSelector("span a")).getText();
    }

    public EditSiteDetailsDialog navigateToEditSiteDetailsDialog(String siteId)
    {
        navigate(siteId);
        openSiteConfiguration();
        selectOptionFromSiteConfigurationDropDown("Edit Site Details");
        return new EditSiteDetailsDialog();
    }
    public EditSiteDetailsDialog editSiteDescription(String description) {
        WebElement SiteDescription = waitUntilElementIsVisible(siteDescription);
        clearAndType(SiteDescription, description);
        clickElement(saveButton);
        return new EditSiteDetailsDialog();
    }
    public boolean somethingWentWrongMessage()
    {
        return isElementDisplayed(By.xpath("//div[contains(text(),'wrong with this page...')]"));
    }
    public SiteDashboardPage assertsiteDataListDashletIsAdded()
    {
        log.info("Verify Dashlet is Added to Site Dashboard page");
        waitUntilElementIsVisible(siteDataListDashlet);
        assertTrue(isElementDisplayed(siteDataListDashlet));
        return this;
    }
    public boolean isOptionListedInSiteConfigurationDropDown(String option)
    {
        List<WebElement> moreOptionsList = findElements(dropdownoptions);
        for (WebElement options : moreOptionsList)
        {
            if (options.getText().equals(option))
            {
                return true;
            }
        }
        return false;
    }
    public SiteDashboardPage pageRefresh()
    {
        waitInSeconds(10);
        getWebDriver().navigate().refresh();
        waitInSeconds(10);
        getWebDriver().navigate().refresh();
        return this;
    }

    public SiteDashboardPage selectCustomizeSite()
    {
        log.info("Select option from site configuration dropdown {}");
        clickElement(clickCustomizeSite);
        return this;
    }
}
