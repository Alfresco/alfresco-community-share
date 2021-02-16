package org.alfresco.po.share.user.admin;

import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.site.SiteManagerDeleteSiteDialog;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SitesManagerPage extends SharePage2<SitesManagerPage> implements AccessibleByMenuBar
{
    private final By tableHeadList = By.cssSelector("thead .label");
    private final By sitesTable = By.id("DOCLIB_DOCUMENT_LIST");
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

    public ManagerSiteActionComponent usingSite(String site)
    {
        return new ManagerSiteActionComponent(this, webElementInteraction, site, new SiteManagerDeleteSiteDialog(webDriver), language);
    }

    public ManagerSiteActionComponent usingSite(SiteModel site)
    {
        return usingSite(site.getTitle());
    }
}