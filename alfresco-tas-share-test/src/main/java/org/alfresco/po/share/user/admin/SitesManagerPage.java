package org.alfresco.po.share.user.admin;

import static org.alfresco.common.RetryTime.RETRY_TIME_15;
import static org.alfresco.common.RetryTime.RETRY_TIME_5;
import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SitesManagerPage extends SharePage2<SitesManagerPage> implements AccessibleByMenuBar
{
    private final By tableHeadList = By.cssSelector("thead .label");
    private final By sitesTable = By.id("DOCLIB_DOCUMENT_LIST");
    private final By siteRowsElements = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private final By nextPageButton = By.id("DOCLIB_PAGINATION_MENU_PAGE_FORWARD");
    private final By dataFailure = By.cssSelector(".data-failure");
    private final By loadedSitesTable = By.cssSelector("div[class='alfresco-lists-AlfList__views rendered-view']");

    public SitesManagerPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public synchronized SitesManagerPage navigate()
    {
        super.navigate();
        waitUntilDataErrorMessageDisappears();
        return this;
    }

    private void waitUntilDataErrorMessageDisappears()
    {
        int retryCount = 0;
        while(retryCount < RETRY_TIME_5.getValue() && isElementDisplayed(dataFailure))
        {
            log.warn("Data error is displayed - retry: {}", retryCount);
            refresh();
            waitToLoopTime(WAIT_2.getValue());
            retryCount++;
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

    public void waitForSitesTableHeaderToBeVisible()
    {
        try
        {
            waitUntilElementIsVisible(loadedSitesTable);
        }
        catch (TimeoutException e)
        {
            waitUntilDataErrorMessageDisappears();
        }
    }

    public SitesManagerPage assertSiteManagerPageIsOpened()
    {
        log.info("Assert Site Manager page is opened");
        assertTrue(getCurrentUrl().contains("manage-sites"), "Site Manager page is opened");
        return this;
    }

    public SitesManagerPage assertTableHasAllColumns()
    {
        waitForSiteRowsWithRetry();
        waitForSitesTableHeaderToBeVisible();

        List<String> expectedTableHeaderList = getTableHeaderList();
        List<WebElement> tableList = waitUntilElementsAreVisible(tableHeadList);

        ArrayList<String> actualTableHeaderTextList = getTableHeaderText(tableList);
        assertEquals(actualTableHeaderTextList, expectedTableHeaderList, "All table columns are displayed");
        return this;
    }

    private ArrayList<String> getTableHeaderText(List<WebElement> tableList)
    {
        return tableList.stream().map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> getTableHeaderList()
    {
        return Collections.synchronizedList(new ArrayList<>(Arrays.asList
            (language.translate("adminTools.siteManager.siteName"),
             language.translate("adminTools.siteManager.siteDescription"),
             language.translate("adminTools.siteManager.visibility"),
             language.translate("adminTools.siteManager.imASiteManager"),
             language.translate("adminTools.siteManager.actions"))));
    }

    public WebElement getSiteRowBasedOnSiteName(String siteName)
    {
        do
        {
            refreshAndWaitIfDataFailureIsDisplayed();
            waitForSiteRowsWithRetry();

            List<WebElement> siteList = findElements(siteRowsElements);
            for (WebElement siteRow : siteList)
            {
                if (getElementText(siteRow).contains(siteName))
                {
                    return siteRow;
                }
            }
            if (isNextPageButtonEnabled())
            {
                clickNextButton();
            }
            else
            {
                break;
            }
        }
        while (!findElements(siteRowsElements).isEmpty());
        return null;
    }

    private void refreshAndWaitIfDataFailureIsDisplayed()
    {
        int retryCount = 0;
        while (retryCount < RETRY_TIME_15.getValue() && isElementDisplayed(dataFailure))
        {
            log.warn("Data error is displayed. Refresh Site Manager page");
            refresh();
            waitInSeconds(WAIT_2.getValue());
            waitForSitesTableHeaderToBeVisible();
            retryCount++;
        }
    }

    private void waitForSiteRowsWithRetry()
    {
        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(siteRowsElements))
        {
            log.warn("Site rows not displayed - retry: {}", retryCount);
            navigate();
            waitInSeconds(WAIT_2.getValue());
            retryCount++;
        }
    }

    private boolean isNextPageButtonEnabled()
    {
        return waitUntilElementIsVisible(nextPageButton).getAttribute("aria-disabled").equals("false");
    }

    public void clickNextButton()
    {
        if (isNextPageButtonEnabled())
        {
            clickElement(nextPageButton);
            waitUntilElementsAreVisible(siteRowsElements);
        }
    }

    public boolean isSitesTableDisplayed()
    {
        return isElementDisplayed(sitesTable);
    }

    public ManagerSiteActionComponent usingSite(SiteModel site)
    {
        return usingSite(site.getTitle());
    }

    private ManagerSiteActionComponent usingSite(String site)
    {
        return new ManagerSiteActionComponent(webDriver, site);
    }
}