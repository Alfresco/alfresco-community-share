package org.alfresco.po.share.user.admin;

import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.DeleteSiteDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.toolbar.Toolbar;
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
import java.util.List;

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
    DeleteSiteDialog deleteSiteDialog;

    @Autowired
    SiteUsersPage siteUsers;

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

    private By sitesLoadingMessage = By.cssSelector("div[class$='alfresco-lists-AlfList--loading']");

    @Override
    public String getRelativePath()
    {
        return "share/page/console/sites-console/manage-sites";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public SitesManagerPage navigateByMenuBar()
    {
        toolbar.clickSitesManager();
        return (SitesManagerPage) renderedPage();
    }

    /**
     * @return list of string representing the header of Site Manager table
     */
    public ArrayList<String> getTableHeader()
    {
        ArrayList<String> tableHeaderText = new ArrayList<>();
        for (WebElement aTableHeadList : tableHeadList)
        {
            tableHeaderText.add(aTableHeadList.getText());
        }
        return tableHeaderText;
    }

    /**
     * Find a managed site row by name from paginated results.
     *
     * @param siteName the required site name
     * @return the managed site row
     */
    public WebElement findManagedSiteRowByNameFromPaginatedResults(String siteName)
    {
        List<WebElement> siteList = browser.findElements(By.cssSelector("tr.alfresco-lists-views-layouts-Row"));
        do
        {
            for(WebElement siteRow : siteList)
            {
                if(siteRow.getText().contains(siteName))
                {
                    return siteRow;
                }
            }
            listPagination.clickNextButton();
        }
        while (listPagination.hasNextPage());
        return null;
    }

    public boolean isSitesTableDisplayed()
    {
        return browser.isElementDisplayed(sitesTable);
    }

    public void waitForLoadingSitesMessageToDisappear()
    {
        try
        {
            browser.waitUntilElementVisible(sitesLoadingMessage,3);
        }
        catch (TimeoutException e)
        {
            //continue
        }
        browser.waitUntilElementDisappears(sitesLoadingMessage);
    }

    public ManagerSiteAction usingSite(String site)
    {
        return new ManagerSiteAction(this, site, deleteSiteDialog);
    }

    public class ManagerSiteAction
    {
        private SitesManagerPage sitesManagerPage;
        private String siteName;
        private DeleteSiteDialog deleteSiteDialog;
        private By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
        private By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
        private By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager");
        private By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
        private By successIndicator = By.cssSelector(".indicator.success");
        private By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
        private String siteAction = "div.dijitPopup[style*=visible] tr[title='%s']";

        public ManagerSiteAction(SitesManagerPage sitesManagerPage, String siteName, DeleteSiteDialog deleteSiteDialog)
        {
            this.sitesManagerPage = sitesManagerPage;
            this.siteName = siteName;
            this.deleteSiteDialog = deleteSiteDialog;
        }

        public WebElement getSiteRow()
        {
            return sitesManagerPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        }

        public ManagerSiteAction assertSiteIsDisplayed()
        {
            Assert.assertNotNull(getSiteRow(), String.format("Site %s is displayed", siteName));
            return this;
        }

        public ManagerSiteAction assertSiteIsNotDisplayed()
        {
            Assert.assertFalse(browser.isElementDisplayed(getSiteRow()), String.format("Site %s is displayed", siteName));
            return this;
        }

        public ManagerSiteAction becomeSiteManager()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            WebElement becomeBtn = browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
            browser.waitUntilElementVisible(becomeBtn).click();
            sitesManagerPage.waitForLoadingSitesMessageToDisappear();
            browser.waitUntilElementContainsText(getSiteRow().findElement(siteRowSiteManager), "Yes");
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsYes()
        {
            Assert.assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(), "Yes","Is site manager");
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsNo()
        {
            Assert.assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(), "No","Is site manager");
            return this;
        }

        public DeleteSiteDialog clickDelete()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite")).click();
            deleteSiteDialog.renderedPage();
            return deleteSiteDialog;
        }

        public ManagerSiteAction deleteSite()
        {
            clickDelete();
            deleteSiteDialog.clickDeleteFromSitesManager();
            sitesManagerPage.waitForLoadingSitesMessageToDisappear();
            browser.waitUntilElementDisappears(getSiteRow());
            return this;
        }

        public ManagerSiteAction changeSiteVisibility(Visibility visibility)
        {
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            getSiteRow().findElement(siteRowVisibility).click();
            browser.findFirstElementWithValue(dropdownOptionsList, visibilityValue).click();
            browser.waitUntilElementVisible(getSiteRow().findElement(successIndicator));
            return this;
        }

        public ManagerSiteAction assertSiteVisibilityIs(Visibility visibility)
        {
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            Assert.assertEquals(getSiteRow().findElement(siteRowVisibility).getText(), visibilityValue,
                "Site visibility is correct");
            return this;
        }

        public ManagerSiteAction assertSuccessIndicatorIsDisplayed()
        {
            Assert.assertTrue(browser.isElementDisplayed(getSiteRow().findElement(successIndicator)), "Success indicator is displayed");
            return this;
        }

        public ManagerSiteAction assertSiteDescriptionIs(String expectedValue)
        {
            Assert.assertEquals(getSiteRow().findElement(siteRowDescription).getText(), expectedValue, "Site description is correct");
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsAvailable()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            Assert.assertTrue(browser.isElementDisplayed(browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsNotAvailable()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            Assert.assertFalse(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsAvailable()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            Assert.assertTrue(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsNotAvailable()
        {
            getSiteRow().findElement(siteRowActionsButton).click();
            Assert.assertFalse(browser.isElementDisplayed( browser.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public SiteUsersPage clickSiteName()
        {
            getSiteRow().findElement(siteRowName).click();
            return (SiteUsersPage) siteUsers.renderedPage();
        }
    }
}