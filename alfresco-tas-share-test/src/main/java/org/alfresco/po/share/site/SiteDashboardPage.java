package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlets;
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
    private final By dashboardLayout = By.cssSelector("div[class*='grid columnSize']");
    private final By siteVisibility = By.cssSelector("div[id='HEADER_TITLE_VISIBILITY'] span");
    private final By morePagesDropDown = By.id("HEADER_SITE_MORE_PAGES");
    private final By moreOptions = By.cssSelector("#HEADER_SITE_MORE_PAGES_GROUP a");

    private final String dropdownOption = "//div[@class='alf-menu-groups' and contains(@style, 'visible')]//td[contains(@id, 'HEADER') and text()='%s']";
    private final String dashletLocation = "//div[text()='%s']/../../../div[contains(@id,'component-%d-%d')]";

    public SiteDashboardPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/dashboard", getCurrentSiteName());
    }

    public SiteDashboardPage assertSiteDashboardPageIsOpened()
    {
        log.info("Assert site dashboard page is opened");
        webElementInteraction.waitUntilElementIsVisible(siteVisibility);
        assertTrue(webElementInteraction.isElementDisplayed(siteVisibility), "Site dashboard page is opened");
        return this;
    }

    public SiteDashboardPage assertSiteHeaderTitleIs(SiteModel expectedSite)
    {
        assertEquals(webElementInteraction.getElementText(siteHeaderTitle), expectedSite.getTitle(), "Site header title is correct");
        return this;
    }

    public int getNumberOfColumns()
    {
        String strCol = webElementInteraction.findElement(dashboardLayout).getAttribute("class");
        return Character.getNumericValue(strCol.charAt(strCol.length() - 1));
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
            return webElementInteraction.isElementDisplayed(By.xpath(String.format(
                "//div[@class='title']/span[contains(@id, 'component-%d-%d')][1]", column, locationInColumn)));
        }
        return webElementInteraction.isElementDisplayed(By.xpath(String.format(
            dashletLocation, dashlet.getDashletName(), column, locationInColumn)));
    }

    public SiteDashboardPage assertSiteVisibilityEqualsTo(String expectedSiteVisibility)
    {
        String actualSiteVisibility = webElementInteraction.getElementText(siteVisibility);
        assertTrue(actualSiteVisibility.equalsIgnoreCase(expectedSiteVisibility));
        return this;
    }

    public SiteDashboardPage assertSiteVisibilityIs(SiteService.Visibility visibility)
    {
        log.info(String.format("Assert site visibility is: %s", visibility.toString()));
        String actualSiteVisibility = webElementInteraction.getElementText(siteVisibility).toUpperCase();

        assertEquals(actualSiteVisibility, visibility.toString(),
            String.format("Site visibility not equals %s", visibility.toString()));

        return this;
    }

    public boolean isSiteVisibilityDisplayed()
    {
        return webElementInteraction.isElementDisplayed(siteVisibility);
    }

    private boolean isDropdownOptionEqualsTo(String expectedOption)
    {
        List<WebElement> siteConfigurations = webElementInteraction
            .waitUntilElementsAreVisible(configurationOptions);

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

    public SiteDashboardPage selectOptionFromSiteConfigurationDropDown(String option)
    {
        log.info("Select option from site configuration dropdown {}", option);
        webElementInteraction.clickElement(By.xpath(String.format(dropdownOption, option)));
        return this;
    }

    public boolean isLinkDisplayedInMoreMenu(String link)
    {
        List<WebElement> moreOptionsList = webElementInteraction.waitUntilElementsAreVisible(moreOptions);
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
        List<WebElement> moreOptionsList = webElementInteraction.waitUntilElementsAreVisible(moreOptions);
        moreOptionsList.stream()
            .filter(option -> option.getText().equals(link))
            .findFirst().ifPresent(WebElement::click);
    }

    public boolean isPageAddedToDashboard(SitePageType page)
    {
        if (webElementInteraction.isElementDisplayed(page.getDashboardLocator()))
        {
            return true;
        }
        else
        {
            if (isMoreLinkDisplayed())
            {
                clickMoreLink();
                return webElementInteraction.isElementDisplayed(page.getDashboardLocator());
            }
            return false;
        }
    }

    public void clickMoreLink()
    {
        webElementInteraction.waitUntilElementIsVisible(morePagesDropDown);
        webElementInteraction.clickElement(morePagesDropDown);
    }

    public void clickLinkFromHeaderNavigationMenu(SitePageType page)
    {
        webElementInteraction.findElement(page.getDashboardLocator()).findElement(By.cssSelector("a")).click();
    }

    public boolean isMoreLinkDisplayed()
    {
        return webElementInteraction.isElementDisplayed(morePagesDropDown);
    }

    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = webElementInteraction.findElement(By.cssSelector(page.getDashboardCssLocator()));
        return pageElem.findElement(By.cssSelector("span a")).getText();
    }

    public EditSiteDetailsDialog navigateToEditSiteDetailsDialog(String siteId)
    {
        navigate(siteId);
        clickSiteConfiguration();
        selectOptionFromSiteConfigurationDropDown("Edit Site Details");
        return new EditSiteDetailsDialog();
    }

    public boolean somethingWentWrongMessage()
    {
        return webElementInteraction.isElementDisplayed(By.xpath("//div[contains(text(),'wrong with this page...')]"));
    }
}
