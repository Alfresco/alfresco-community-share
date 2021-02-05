package org.alfresco.po.share.user.admin;

import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.SiteService.Visibility;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.SiteModel;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SitesManagerPage extends SharePage2<SitesManagerPage> implements AccessibleByMenuBar
{
    private final By tableHeadList = By.cssSelector("thead .label");
    private final By sitesTable = By.id("DOCLIB_DOCUMENT_LIST");
    private final By dropdownOptionsList = By.cssSelector("div.dijitPopup[style*=visible] td.dijitMenuItemLabel");
    private final By siteRowsElements = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private final By nextPageButton = By.id("DOCLIB_PAGINATION_MENU_PAGE_FORWARD");
    private final By dataFailure = By.cssSelector(".data-failure");

    public SitesManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public SitesManagerPage navigate()
    {
        super.navigate();
        waitUntilDataErrorMessageDisappears();
        waitUntilLoadingMessageDisappears();
        return this;
    }

    private void waitUntilDataErrorMessageDisappears()
    {
        int i = 0;
        while(i < WAIT_5.getValue() && webElementInteraction.isElementDisplayed(dataFailure))
        {
            log.error("Data error is displayed. Retry navigate to Site Manager page {}", i);
            webElementInteraction.refresh();
            waitToLoopTime(WAIT_2.getValue());
            i++;
        }
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
        return new Toolbar(webDriver).clickSitesManager();
    }

    public void waitForSitesTableHeaderToBeDisplayed()
    {
        try
        {
            webElementInteraction.waitUntilElementIsVisible(tableHeadList);
        }
        catch (TimeoutException e)
        {
            waitUntilDataErrorMessageDisappears();
        }
    }

    public SitesManagerPage assertSiteManagerPageIsOpened()
    {
        log.info("Assert Site Manager page is opened");
        assertTrue(webElementInteraction.getCurrentUrl().contains("manage-sites"), "Site Manager page is opened");
        return this;
    }

    public SitesManagerPage assertTableHasAllColumns()
    {
        waitForSiteRowsWithRetry();waitForSitesTableHeaderToBeDisplayed();
        List<String> expectedTableHeader = Collections.synchronizedList(new ArrayList<>(Arrays.asList
            (language.translate("adminTools.siteManager.siteName"),
             language.translate("adminTools.siteManager.siteDescription"),
             language.translate("adminTools.siteManager.visibility"),
             language.translate("adminTools.siteManager.imASiteManager"),
             language.translate("adminTools.siteManager.actions"))));
        List<WebElement> tableList = webElementInteraction.waitUntilElementsAreVisible(tableHeadList);
        ArrayList<String> tableHeaderText = tableList.stream().map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
        assertEquals(tableHeaderText, expectedTableHeader, "All table columns are displayed");
        return this;
    }

    public WebElement findManagedSiteRowByNameFromPaginatedResults(String siteName)
    {
        do
        {
            if(webElementInteraction.isElementDisplayed(dataFailure))
            {
                log.error("Data error is displayed. Refresh Site Manager page");
                webElementInteraction.refresh();
                webElementInteraction.waitInSeconds(WAIT_2.getValue());
                waitForSitesTableHeaderToBeDisplayed();
            }
            waitForSiteRowsWithRetry();
            List<WebElement> siteList = webElementInteraction.findElements(siteRowsElements);
            for (WebElement siteRow : siteList)
            {
                if (webElementInteraction.getElementText(siteRow).contains(siteName))
                {
                    return siteRow;
                }
            }
            if (hasNextPage())
            {
                clickNextButton();
            }
            else
            {
                break;
            }
        }
        while (!webElementInteraction.findElements(siteRowsElements).isEmpty());
        return null;
    }

    private void waitForSiteRowsWithRetry()
    {
        int retryCount = 0;
        while (retryCount < WAIT_10.getValue() && !webElementInteraction.isElementDisplayed(siteRowsElements))
        {
            log.error("Wait for site rows to be displayed");
            navigate();
            webElementInteraction.waitInSeconds(WAIT_1.getValue());
            waitUntilLoadingMessageDisappears();
            retryCount++;
        }
    }

    private boolean hasNextPage()
    {
        return webElementInteraction.waitUntilElementIsVisible(nextPageButton)
            .getAttribute("aria-disabled").equals("false");
    }

    public void clickNextButton()
    {
        if (hasNextPage())
        {
            webElementInteraction.clickElement(nextPageButton);
            waitUntilLoadingMessageDisappears();
            webElementInteraction.waitUntilElementsAreVisible(siteRowsElements);
        }
    }

    public boolean isSitesTableDisplayed()
    {
        return webElementInteraction.isElementDisplayed(sitesTable);
    }

    public ManagerSiteAction usingSite(String site)
    {
        return new ManagerSiteAction(this, site, new SiteManagerDeleteSiteDialog(webDriver));
    }

    public ManagerSiteAction usingSite(SiteModel site)
    {
        return usingSite(site.getTitle());
    }

    //todo move into separate file
    public class ManagerSiteAction
    {
        private final SitesManagerPage sitesManagerPage;
        private final String siteName;
        private final SiteManagerDeleteSiteDialog deleteSiteDialog;
        private final By siteRowName = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName span.inner");
        private final By siteRowActionsButton = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions div.dijitPopupMenuItem");
        private final By siteRowSiteManager = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteManager .value");
        private final By siteRowVisibility = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility table");
        private final By siteRowVisibilityArrow = By.cssSelector("input[class$='dijitArrowButtonInner']");
        private final By successIndicator = By.cssSelector("div[class='indicator success']");
        private final By siteRowDescription = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");

        public ManagerSiteAction(SitesManagerPage sitesManagerPage, String siteName, SiteManagerDeleteSiteDialog deleteSiteDialog)
        {
            this.sitesManagerPage = sitesManagerPage;
            this.siteName = siteName;
            this.deleteSiteDialog = deleteSiteDialog;

            log.info("Using site: {}", siteName);
        }

        public WebElement getSiteRow()
        {
            return sitesManagerPage.findManagedSiteRowByNameFromPaginatedResults(siteName);
        }

        public ManagerSiteAction assertSiteIsNotDisplayed()
        {
            log.info("Assert site is not displayed");
            assertNull(getSiteRow(), String.format("Site %s is displayed", siteName));
            return this;
        }

        private void clickActionsButton()
        {
            WebElement actionsButton = getSiteRow().findElement(siteRowActionsButton);
            webElementInteraction.mouseOver(actionsButton);
            webElementInteraction.clickElement(actionsButton);
        }

        public ManagerSiteAction becomeSiteManager()
        {
            log.info("Become site manager");
            clickActionsButton();
            webElementInteraction.waitUntilElementsAreVisible(dropdownOptionsList);
            WebElement becomeBtn = webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"));
            webElementInteraction.mouseOver(becomeBtn);
            webElementInteraction.clickElement(becomeBtn);
            sitesManagerPage.waitUntilLoadingMessageDisappears();
            WebElement siteRow = getSiteRow();
            webElementInteraction.waitUntilChildElementIsPresent(siteRow, siteRowSiteManager);
            if(siteRow.findElement(siteRowSiteManager).getText().equals(language.translate("adminTools.siteManager.no")))
            {
                log.error("Retry action Become Site Manager");
                navigate();
                clickActionsButton();
                webElementInteraction.waitUntilElementIsVisible(becomeBtn);
                webElementInteraction.clickElement(becomeBtn);
            }
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsYes()
        {
            log.info("Assert I'm site manager is set to Yes");
            assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
                language.translate("adminTools.siteManager.yes"),"Is site manager");
            return this;
        }

        public ManagerSiteAction assertSiteManagerIsNo()
        {
            log.info("Assert I'm site manager is set to No");
            assertEquals(getSiteRow().findElement(siteRowSiteManager).getText(),
                language.translate("adminTools.siteManager.no"),"Is site manager");
            return this;
        }

        public SiteManagerDeleteSiteDialog clickDelete()
        {
            log.info("Click Delete");
            clickActionsButton();
            webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite")).click();
            return deleteSiteDialog;
        }

        public ManagerSiteAction deleteSite()
        {
            log.info("Delete site");
            clickDelete();
            deleteSiteDialog.clickDeleteFromSitesManager();
            webElementInteraction.waitUntilElementDisappears(getSiteRow());
            return this;
        }

        public ManagerSiteAction changeSiteVisibility(Visibility visibility)
        {
            log.info("Change site visibility to {}", visibility.toString());
            WebElement siteRow = getSiteRow();
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);

            webElementInteraction.clickElement(siteRow.findElement(siteRowVisibilityArrow));
            List<WebElement> options = webElementInteraction.waitUntilElementsAreVisible(dropdownOptionsList);
            webElementInteraction.waitInSeconds(WAIT_2.getValue());
            WebElement option = webElementInteraction.findFirstElementWithValue(options, visibilityValue);
            webElementInteraction.mouseOver(option);
            webElementInteraction.clickElement(option);
            webElementInteraction.waitUntilChildElementIsPresent(getSiteRow(), successIndicator);

            return this;
        }

        public ManagerSiteAction assertSiteVisibilityEquals(Visibility visibility)
        {
            log.info("Assert site visibility is: {}", visibility.toString());
            String visibilityValue = visibility.toString().toLowerCase();
            visibilityValue = StringUtils.capitalize(visibilityValue);
            assertEquals(getSiteRow().findElement(siteRowVisibility).getText(), visibilityValue,
                "Site visibility is correct");
            return this;
        }

        public ManagerSiteAction assertSuccessIndicatorIsDisplayed()
        {
            log.info("Assert success indicator is displayed");
            assertTrue(webElementInteraction.isElementDisplayed(getSiteRow().findElement(successIndicator)), "Success indicator is displayed");
            return this;
        }

        public ManagerSiteAction assertSiteDescriptionIs(String expectedValue)
        {
            log.info(String.format("Assert site description is %s", expectedValue));
            assertEquals(getSiteRow().findElement(siteRowDescription).getText(), expectedValue, "Site description is correct");
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsAvailable()
        {
            log.info("Assert Become site manager option is available");
            webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
            assertTrue(webElementInteraction.isElementDisplayed(webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertBecomeManagerOptionIsNotAvailable()
        {
            log.info("Assert Become site manager option is NOT available");
            webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
            assertFalse(webElementInteraction.isElementDisplayed( webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.becomeSiteManager"))));
            return this;
        }

        public ManagerSiteAction assertDeleteSiteOptionIsAvailable()
        {
            log.info("Assert Delete Site option is available");
            webElementInteraction.clickElement(getSiteRow().findElement(siteRowActionsButton));
            assertTrue(webElementInteraction.isElementDisplayed(webElementInteraction.findFirstElementWithValue(dropdownOptionsList,
                sitesManagerPage.language.translate("sitesManager.deleteSite"))));
            return this;
        }

        public SiteUsersPage clickSiteName()
        {
            log.info("Click Site Name");
            webElementInteraction.clickElement(getSiteRow().findElement(siteRowName));
            return new SiteUsersPage(webDriver);
        }
    }
}