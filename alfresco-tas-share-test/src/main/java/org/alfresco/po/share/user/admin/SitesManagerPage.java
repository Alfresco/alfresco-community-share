package org.alfresco.po.share.user.admin;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class SitesManagerPage extends SharePage2<SitesManagerPage> implements AccessibleByMenuBar
{
    @RenderWebElement
    private By tableHeadList = By.cssSelector("thead .label");
    @RenderWebElement
    private By sitesTable = By.id("DOCLIB_DOCUMENT_LIST");
    private By dropdownOptionsList = By.cssSelector("div.dijitPopup[style*=visible] td.dijitMenuItemLabel");
    private By siteRowsElements = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private By nextPageButton = By.id("DOCLIB_PAGINATION_MENU_PAGE_FORWARD");

    public SitesManagerPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/sites-console/manage-sites";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SitesManagerPage navigateByMenuBar()
    {
        return new Toolbar(browser).clickSitesManager();
    }

    @Override
    public SitesManagerPage navigate()
    {
        try
        {
            super.navigate();
            waiUntilLoadingMessageDisappears();
            return this;
        }
        catch(TimeoutException | PageRenderTimeException e)
        {
            LOG.error("Reload Site Manager page");
            return super.navigate();
        }
    }

    public SitesManagerPage assertSiteManagerPageIsOpened()
    {
        LOG.info("Assert Site Manager page is opened");
        assertTrue(getBrowser().getCurrentUrl().contains("manage-sites"), "Site Manager page is opened");
        return this;
    }

    public SitesManagerPage assertTableHasAllColumns()
    {
        ArrayList<String> expectedTableHeader = new ArrayList<>(Arrays.asList
            (language.translate("adminTools.siteManager.siteName"),
             language.translate("adminTools.siteManager.siteDescription"),
             language.translate("adminTools.siteManager.visibility"),
             language.translate("adminTools.siteManager.imASiteManager"),
             language.translate("adminTools.siteManager.actions")));
        List<WebElement> tableList = getBrowser().waitUntilElementsVisible(tableHeadList);
        ArrayList<String> tableHeaderText = tableList.stream().map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(tableHeaderText, expectedTableHeader, "All table columns are displayed");
        return this;
    }

    public WebElement findManagedSiteRowByNameFromPaginatedResults(String siteName)
    {
        do
        {
            getBrowser().waitUntilElementIsPresent(siteRowsElements);
            getBrowser().waitUntilElementVisible(siteRowsElements);
            List<WebElement> siteList = getBrowser().findElements(siteRowsElements);
            for(WebElement siteRow : siteList)
            {
                if(siteRow.getText().contains(siteName))
                {
                    return siteRow;
                }
            }
            if(hasNextPage())
            {
                clickNextButton();
                waiUntilLoadingMessageDisappears();
            }
            else
            {
                break;
            }
        }
        while (getBrowser().findElements(siteRowsElements).size() > 0);
        return null;
    }

    public boolean hasNextPage()
    {
        return getBrowser().findElement(nextPageButton).getAttribute("aria-disabled").equals("false");
    }

    public void clickNextButton()
    {
        if (hasNextPage())
        {
            getBrowser().waitUntilElementClickable(nextPageButton).click();
        }
    }

    public boolean isSitesTableDisplayed()
    {
        return getBrowser().isElementDisplayed(sitesTable);
    }

    public ManagerSiteAction usingSite(String site)
    {
        return new ManagerSiteAction(this, site, new SiteManagerDeleteSiteDialog(browser));
    }

    public ManagerSiteAction usingSite(SiteModel site)
    {
        return usingSite(site.getTitle());
    }

    public class ManagerSiteAction
    {
        private SitesManagerPage sitesManagerPage;
        private String siteName;
        private SiteManagerDeleteSiteDialog deleteSiteDialog;
        private By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
        private By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
        private By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager .value");
        private By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
        private By siteRowVisibilityArrow = By.cssSelector("input[class$='dijitArrowButtonInner']");
        private By successIndicator = By.cssSelector("div[class='indicator success']");
        private By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
        private String siteAction = "div.dijitPopup[style*=visible] tr[title='%s']";

        public ManagerSiteAction(SitesManagerPage sitesManagerPage, String siteName, SiteManagerDeleteSiteDialog deleteSiteDialog)
        {
            this.sitesManagerPage = sitesManagerPage;
            this.siteName = siteName;
            this.deleteSiteDialog = deleteSiteDialog;

            LOG.info(String.format("Using site: %s", siteName));
        }

        public WebElement getSiteRow()
        {
            return sitesManagerPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        }

        public ManagerSiteAction assertSiteIsDisplayed()
        {
            LOG.info("Assert site is displayed");
            Assert.assertNotNull(getSiteRow(), String.format("Site %s is displayed", siteName));
            return this;
        }

        public ManagerSiteAction assertSiteIsNotDisplayed()
        {
            LOG.info("Assert site is not displayed");
            Assert.assertNull(getSiteRow(), String.format("Site %s is displayed", siteName));
            return this;
        }

        private void clickActionsButton()
        {
            WebElement actionsButton = getSiteRow().findElement(siteRowActionsButton);
            getBrowser().mouseOver(actionsButton);
            actionsButton.click();
        }

        public ManagerSiteAction becomeSiteManager()
        {
            LOG.info("Become site manager");
            clickActionsButton();
            getBrowser().waitUntilElementsVisible(dropdownOptionsList);
            WebElement becomeBtn = getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
            getBrowser().mouseOver(becomeBtn);
            becomeBtn.click();
            sitesManagerPage.waiUntilLoadingMessageDisappears();
            getBrowser().waitUntilChildElementIsPresent(getSiteRow(), siteRowSiteManager);
            if(getSiteRow().findElement(siteRowSiteManager).getText().equals(language.translate("adminTools.siteManager.no")))
            {
                LOG.error("Retry action Become Site Manager");
                clickActionsButton();
                becomeBtn.click();
                sitesManagerPage.waiUntilLoadingMessageDisappears();
            }
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsYes()
        {
            LOG.info("Assert I'm site manager is set to Yes");
            assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
                language.translate("adminTools.siteManager.yes"),"Is site manager");
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsNo()
        {
            LOG.info("Assert I'm site manager is set to No");
            assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
                language.translate("adminTools.siteManager.no"),"Is site manager");
            return this;
        }

        public SiteManagerDeleteSiteDialog clickDelete()
        {
            LOG.info("Click Delete");
            clickActionsButton();
            getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite")).click();
            deleteSiteDialog.renderedPage();
            return deleteSiteDialog;
        }

        public ManagerSiteAction deleteSite()
        {
            LOG.info("Delete site");
            clickDelete();
            deleteSiteDialog.clickDeleteFromSitesManager();
            sitesManagerPage.waiUntilLoadingMessageDisappears();
            getBrowser().waitUntilElementDisappears(getSiteRow());
            return this;
        }

        public ManagerSiteAction changeSiteVisibility(Visibility visibility)
        {
            LOG.info("Change site visibility to {}", visibility.toString());
            WebElement siteRow = getSiteRow();
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            siteRow.findElement(siteRowVisibilityArrow).click();
            List<WebElement> options = getBrowser().waitUntilElementsVisible(dropdownOptionsList);
            WebElement option = getBrowser().findFirstElementWithValue(options, visibilityValue);
            option.click();

            return this;
        }

        public ManagerSiteAction assertSiteVisibilityEquals(Visibility visibility)
        {
            LOG.info("Assert site visibility is: {}", visibility.toString());
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            assertEquals(getSiteRow().findElement(siteRowVisibility).getText(), visibilityValue,
                "Site visibility is correct");
            return this;
        }

        public ManagerSiteAction assertSuccessIndicatorIsDisplayed()
        {
            LOG.info("Assert success indicator is displayed");
            getBrowser().waitUntilChildElementIsPresent(getSiteRow(), successIndicator);
            assertTrue(getBrowser().isElementDisplayed(getSiteRow().findElement(successIndicator)), "Success indicator is displayed");
            return this;
        }

        public ManagerSiteAction assertSiteDescriptionIs(String expectedValue)
        {
            LOG.info(String.format("Assert site description is %s", expectedValue));
            assertEquals(getSiteRow().findElement(siteRowDescription).getText(), expectedValue, "Site description is correct");
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsAvailable()
        {
            LOG.info("Assert Become site manager option is available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertTrue(getBrowser().isElementDisplayed(getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsNotAvailable()
        {
            LOG.info("Assert Become site manager option is NOT available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertFalse(getBrowser().isElementDisplayed( getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsAvailable()
        {
            LOG.info("Assert Delete Site option is available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertTrue(getBrowser().isElementDisplayed(getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsNotAvailable()
        {
            LOG.info("Assert Delete Site option is NOT available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertFalse(getBrowser().isElementDisplayed(getBrowser().findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public SiteUsersPage clickSiteName()
        {
            LOG.info("Click Site Name");
            getSiteRow().findElement(siteRowName).click();
            return (SiteUsersPage) new SiteUsersPage(browser).renderedPage();
        }
    }
}