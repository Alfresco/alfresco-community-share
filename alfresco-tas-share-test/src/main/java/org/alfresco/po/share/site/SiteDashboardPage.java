package org.alfresco.po.share.site;

import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class SiteDashboardPage extends SiteCommon<SiteDashboardPage>
{
    @RenderWebElement
    @FindBy (css = "div[class*='grid columnSize']")
    private WebElement dashboardLayout;

    @FindBy (css = ".msg.dashlet-padding>h2")
    private WebElement welcomeMessage;

    @FindBy (css = "div[class*='colleagues']")
    private WebElement siteMembersBox;

    @RenderWebElement
    @FindBy (css = "div[id='HEADER_TITLE_VISIBILITY'] span")
    private WebElement siteVisibility;

    @FindAll (@FindBy (css = "div[style*='visible'] tr[id^='HEADER']>td[id$='text']"))
    private List<WebElement> siteConfigurationOptions;

    @FindBy (id = "HEADER_SITE_MORE_PAGES")
    private WebElement morePagesDropDown;

    private By moreOptions = By.cssSelector("#HEADER_SITE_MORE_PAGES_GROUP a");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/dashboard", getCurrentSiteName());
    }

    /**
     * Get the number of columns from Site Dashboard
     *
     * @return number of columns
     */
    public int getNumerOfColumns()
    {
        String strCol = dashboardLayout.getAttribute("class");
        return Character.getNumericValue(strCol.charAt(strCol.length() - 1));
    }

    /**
     * Verify if a dashlet is located in the right column
     *
     * @param dashlet          Dashlets dashlet to verify
     * @param column           int column (must be > 0 <=4)
     * @param locationInColumn int location in column (must be > 0 <=5)
     * @return true if found
     */
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
            return browser
                .isElementDisplayed(By.xpath(String.format("//div[@class='title']/span[contains(@id, 'component-%d-%d')][1]", column, locationInColumn)));
        }
        String dashletLocation = String.format("//div[text()='%s']/../../../div[contains(@id,'component-%d-%d')]", dashlet.getDashletName(), column,
            locationInColumn);
        return browser.isElementDisplayed(By.xpath(dashletLocation));
    }

    /**
     * Get the "Welcome" message from "Site Profile" dashlet
     *
     * @return String message
     */
    public String getWelcomeMessageText()
    {
        return welcomeMessage.getText();
    }

    public String getSiteVisibility()
    {
        browser.waitUntilElementVisible(siteVisibility);
        return siteVisibility.getText();
    }

    /**
     * Search for an option in Site Configuration Options dropdown list
     *
     * @param option String - option to be found in dropdown list
     * @return Boolean true if entry is found, false if not
     */
    public boolean isOptionListedInSiteConfigurationDropDown(String option)
    {
        List<String> availableOptions = new ArrayList<>();
        List<WebElement> siteConfigurationOptions = browser.findElements(By.cssSelector("div[style*='visible'] tr[id^='HEADER']>td[id$='text']"));
        getBrowser().waitUntilElementsVisible(siteConfigurationOptions);

        for (WebElement siteConfigurationOption : siteConfigurationOptions)
        {
            availableOptions.add(siteConfigurationOption.getText());
            if (siteConfigurationOption.getText().equals(option))
                return true;
        }
        LOG.info("Available options are: " + availableOptions.toString());
        return false;
    }

    /**
     * Click on the specified option from Site Configuration Options dropdown list
     *
     * @param option String - option to be clicked in dropdown list
     * @return
     */
    public void clickOptionInSiteConfigurationDropDown(String option, HtmlPage page)
    {
        SiteConfigurationOptions[] siteConfigurationOptions = SiteConfigurationOptions.values();
        for (SiteConfigurationOptions configurationOption : siteConfigurationOptions)
        {
            if (configurationOption.getOptionText().equals(option))
            {
                browser.waitUntilElementVisible(configurationOption.getOptionLocator()).click();
                break;
            }
        }
        getBrowser().waitUntilElementDisappears(By.cssSelector("span.message"));
    }

    /**
     * Click on 'Customize Site' option from Site Configuration Options dropdown list
     */
    public void clickCustomizeSite()
    {
        browser.waitUntilElementVisible(SiteConfigurationOptions.CUSTOMIZE_SITE.getOptionLocator()).click();
    }

    /**
     * Search for a link in More menu
     *
     * @param link String - option to be found in menu
     * @return Boolean true if entry is found, false if not
     */
    public boolean isLinkDisplayedInMoreMenu(String link)
    {
        List<WebElement> moreOptionsList = browser.waitUntilElementsVisible(moreOptions);
        for (WebElement option : moreOptionsList)
        {
            if (option.getText().equals(link))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Click on the specified link from More menu
     *
     * @param link String - option to be clicked in menu
     * @return current page
     */
    public void clickLinkFromMoreMenu(String link)
    {
        clickMoreLink();
        List<WebElement> moreOptionsList = browser.waitUntilElementsVisible(moreOptions);
        for (WebElement option : moreOptionsList)
        {
            if (option.getText().equals(link))
            {
                option.click();
                break;
            }
        }
    }

    /**
     * Verify if a page is added to site dashboard
     *
     * @param page SitePageType the page
     * @return true if page is added
     */
    public boolean isPageAddedToDashboard(SitePageType page)
    {
        if (browser.isElementDisplayed(page.getDashboardLocator()) == true)
        {
            return true;
        } else
        {
            if (isMoreLinkDisplayed())
            {
                clickMoreLink();
                return browser.isElementDisplayed(page.getDashboardLocator());
            }
            return false;
        }
    }

    /**
     * Click More link
     */
    public void clickMoreLink()
    {
        morePagesDropDown.click();
    }

    /**
     * Click on the specified link from header navigation menu
     *
     * @param page
     */
    public void clickLinkFromHeaderNavigationMenu(SitePageType page)
    {
        browser.findElement(page.getDashboardLocator()).findElement(By.cssSelector("a")).click();
    }

    /**
     * Verify if More link is displayed
     *
     * @return true if displayed
     */
    public boolean isMoreLinkDisplayed()
    {
        return browser.isElementDisplayed(morePagesDropDown);
    }

    /**
     * Get the display name for a page
     *
     * @param page SitePageType the page
     * @return String display name
     */
    public String getPageDisplayName(SitePageType page)
    {
        WebElement pageElem = browser.findElement(By.cssSelector(page.getDashboardCssLocator()));
        return pageElem.findElement(By.cssSelector("span a")).getText();
    }

    public boolean somethingWentWrongMessage()
    {
        return browser.isElementDisplayed(By.xpath("//div[contains(text(),'wrong with this page...')]"));
    }
}
