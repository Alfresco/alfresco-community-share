package org.alfresco.po.share.user.admin;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.exception.PageRenderTimeException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by Claudia Agache on 7/1/2016.
 */
@PageObject
public class SitesManagerPage extends SharePage<SitesManagerPage> implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @Autowired
    private ListPagination listPagination;

    @Autowired
    protected SiteManagerDeleteSiteDialog deleteSiteDialog;

    @Autowired
    protected SiteUsersPage siteUsers;

    @RenderWebElement
    @FindBy (css = "thead .label")
    private List<WebElement> tableHeadList;

    @RenderWebElement
    @FindBy (id = "DOCLIB_DOCUMENT_LIST")
    private WebElement sitesTable;

    @FindAll (@FindBy (css = "tr.alfresco-lists-views-layouts-Row"))
    private List<WebElement> siteRows;

    @FindAll (@FindBy (css = "div.dijitPopup[style*=visible] td.dijitMenuItemLabel"))
    private List<WebElement> dropdownOptionsList;

    private By siteRowsElements = By.cssSelector("tr.alfresco-lists-views-layouts-Row");

    @Override
    public String getRelativePath()
    {
        return "share/page/console/sites-console/manage-sites";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SitesManagerPage navigateByMenuBar()
    {
        return toolbar.clickSitesManager();
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
        assertTrue(browser.getCurrentUrl().contains("manage-sites"), "Site Manager page is opened");
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
        ArrayList<String> tableHeaderText = tableHeadList.stream().map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(tableHeaderText, expectedTableHeader, "All table columns are displayed");
        return this;
    }

    /**
     * Find a managed site row by name from paginated results.
     *
     * @param siteName the required site name
     * @return the managed site row
     */
    public WebElement findManagedSiteRowByNameFromPaginatedResults(String siteName)
    {
        do
        {
            browser.waitUntilElementIsPresent(siteRowsElements);
            browser.waitUntilElementVisible(siteRowsElements);
            List<WebElement> siteList = browser.findElements(siteRowsElements);
            for(WebElement siteRow : siteList)
            {
                if(siteRow.getText().contains(siteName))
                {
                    return siteRow;
                }
            }
            if(listPagination.hasNextPage())
            {
                listPagination.clickNextButton();
                waiUntilLoadingMessageDisappears();
            }
            else
            {
                break;
            }
        }
        while (browser.findElements(siteRowsElements).size() > 0);
        return null;
    }

    public boolean isSitesTableDisplayed()
    {
        return browser.isElementDisplayed(sitesTable);
    }

    public ManagerSiteAction usingSite(String site)
    {
        return new ManagerSiteAction(this, site, deleteSiteDialog);
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
            browser.mouseOver(actionsButton);
            actionsButton.click();
        }

        public ManagerSiteAction becomeSiteManager()
        {
            LOG.info("Become site manager");
            clickActionsButton();
            browser.waitUntilElementsVisible(dropdownOptionsList);
            WebElement becomeBtn = browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
            browser.mouseOver(becomeBtn);
            becomeBtn.click();
            sitesManagerPage.waiUntilLoadingMessageDisappears();
            browser.waitUntilChildElementIsPresent(getSiteRow(), siteRowSiteManager);
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
            browser.findFirstElementWithValue(dropdownOptionsList,
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
            browser.waitUntilElementDisappears(getSiteRow());
            return this;
        }

        public ManagerSiteAction changeSiteVisibility(Visibility visibility)
        {
            LOG.info("Change site visibility to {}", visibility.toString());
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            getSiteRow().findElement(siteRowVisibility).click();
            browser.waitUntilElementsVisible(dropdownOptionsList);
            WebElement option = browser.findFirstElementWithValue(dropdownOptionsList, visibilityValue);
            browser.waitUntilElementClickable(option);
            browser.clickJS(option);
            browser.waitUntilChildElementIsPresent(getSiteRow(), successIndicator);

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
            assertTrue(browser.isElementDisplayed(getSiteRow().findElement(successIndicator)), "Success indicator is displayed");
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
            assertTrue(browser.isElementDisplayed(browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsNotAvailable()
        {
            LOG.info("Assert Become site manager option is NOT available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertFalse(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsAvailable()
        {
            LOG.info("Assert Delete Site option is available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertTrue(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsNotAvailable()
        {
            LOG.info("Assert Delete Site option is NOT available");
            getSiteRow().findElement(siteRowActionsButton).click();
            assertFalse(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public SiteUsersPage clickSiteName()
        {
            LOG.info("Click Site Name");
            getSiteRow().findElement(siteRowName).click();
            return (SiteUsersPage) siteUsers.renderedPage();
        }
    }
}