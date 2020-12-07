package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author bogdan.bocancea
 */
public class SiteDashboardPage extends SiteCommon<SiteDashboardPage>
{
    private EditSiteDetailsDialog editSiteDetailsDialog;

    private final By siteHeaderTitle = By.cssSelector("#HEADER_TITLE a");
    @RenderWebElement
    private final By dashboardLayout = By.cssSelector("div[class*='grid columnSize']");
    @RenderWebElement
    private final By siteVisibility = By.cssSelector("div[id='HEADER_TITLE_VISIBILITY'] span");
    private final By morePagesDropDown = By.id("HEADER_SITE_MORE_PAGES");
    private final By moreOptions = By.cssSelector("#HEADER_SITE_MORE_PAGES_GROUP a");
    private final String dashletLocation = "//div[text()='%s']/../../../div[contains(@id,'component-%d-%d')]";

    public SiteDashboardPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/dashboard", getCurrentSiteName());
    }

    public SiteDashboardPage assertSiteDashboardPageIsOpened()
    {
        LOG.info("Assert site dashboard page is opened");
        getBrowser().waitUntilElementVisible(siteVisibility);
        assertTrue(getBrowser().isElementDisplayed(siteVisibility), "Site dashboard page is opened");
        return this;
    }

    public SiteDashboardPage assertSiteHeaderTitleIs(SiteModel expectedSite)
    {
        assertEquals(getBrowser().findElement(siteHeaderTitle).getText(), expectedSite.getTitle(), "Site header title is correct");
        return this;
    }

    public int getNumerOfColumns()
    {
        String strCol = getBrowser().findElement(dashboardLayout).getAttribute("class");
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
            return getBrowser().isElementDisplayed(By.xpath(String.format(
                "//div[@class='title']/span[contains(@id, 'component-%d-%d')][1]", column, locationInColumn)));
        }
        return getBrowser().isElementDisplayed(By.xpath(String.format(
            dashletLocation, dashlet.getDashletName(), column, locationInColumn)));
    }

    public String getSiteVisibility()
    {
        return getBrowser().waitUntilElementVisible(siteVisibility).getText();
    }

    public SiteDashboardPage assertSiteVisibilityIs(SiteService.Visibility visibility)
    {
        LOG.info(String.format("Assert site visibility is: %s", visibility.toString()));
        assertEquals(getBrowser().waitUntilElementVisible(siteVisibility).getText().toUpperCase(), visibility.toString());
        return this;
    }

    public boolean isSiteVisibilityDisplayed()
    {
        return getBrowser().isElementDisplayed(siteVisibility);
    }

    public boolean isOptionListedInSiteConfigurationDropDown(String option)
    {
        List<String> availableOptions = new ArrayList<>();

        List<WebElement> siteConfigurationOptions = getBrowser().findElements(configurationOptions);
        getBrowser().waitUntilElementsVisible(siteConfigurationOptions);

        for (WebElement siteConfigurationOption : siteConfigurationOptions)
        {
            availableOptions.add(siteConfigurationOption.getText());
            if (siteConfigurationOption.getText().equals(option))
            {
                return true;
            }
        }
        LOG.info("Available options are: " + availableOptions.toString());
        return false;
    }

    public void clickOptionInSiteConfigurationDropDown(String option)
    {
        SiteConfigurationOptions[] siteConfigurationOptions = SiteConfigurationOptions.values();
        for (SiteConfigurationOptions configurationOption : siteConfigurationOptions)
        {
            if (configurationOption.getOptionText().equals(option))
            {
                getBrowser().waitUntilElementVisible(configurationOption.getOptionLocator()).click();
                break;
            }
        }
    }

    public void clickCustomizeSite()
    {
        getBrowser().waitUntilElementVisible(SiteConfigurationOptions.CUSTOMIZE_SITE.getOptionLocator()).click();
    }

    public boolean isLinkDisplayedInMoreMenu(String link)
    {
        List<WebElement> moreOptionsList = getBrowser().waitUntilElementsVisible(moreOptions);
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
        List<WebElement> moreOptionsList = getBrowser().waitUntilElementsVisible(moreOptions);
        moreOptionsList.stream()
            .filter(option -> option.getText().equals(link))
            .findFirst().ifPresent(WebElement::click);
    }

    public boolean isPageAddedToDashboard(SitePageType page)
    {
        if (getBrowser().isElementDisplayed(page.getDashboardLocator()) == true)
        {
            return true;
        }
        else
        {
            if (isMoreLinkDisplayed())
            {
                clickMoreLink();
                return getBrowser().isElementDisplayed(page.getDashboardLocator());
            }
            return false;
        }
    }

    public void clickMoreLink()
    {
        getBrowser().waitUntilElementVisible(morePagesDropDown).click();
    }

    public void clickLinkFromHeaderNavigationMenu(SitePageType page)
    {
        getBrowser().findElement(page.getDashboardLocator()).findElement(By.cssSelector("a")).click();
    }

    public boolean isMoreLinkDisplayed()
    {
        return getBrowser().isElementDisplayed(morePagesDropDown);
    }

    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = getBrowser().findElement(By.cssSelector(page.getDashboardCssLocator()));
        return pageElem.findElement(By.cssSelector("span a")).getText();
    }

    public EditSiteDetailsDialog navigateToEditSiteDetailsDialog(String siteId)
    {
        navigate(siteId);
        clickSiteConfiguration();
        clickOptionInSiteConfigurationDropDown("Edit Site Details");
        return (EditSiteDetailsDialog) editSiteDetailsDialog.renderedPage();
    }

    public boolean somethingWentWrongMessage()
    {
        return getBrowser().isElementDisplayed(By.xpath("//div[contains(text(),'wrong with this page...')]"));
    }
}
