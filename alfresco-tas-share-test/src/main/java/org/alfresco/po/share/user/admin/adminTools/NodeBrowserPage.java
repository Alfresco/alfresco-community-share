package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.Wait.*;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class NodeBrowserPage extends SharePage2<NodeBrowserPage>
{
    private final By searchInput = By.cssSelector("div.search-text textarea");
    private final By searchTypeDropdownButton = By.xpath(".//button[normalize-space()='fts-alfresco']");
    private final By buttonStateAfterHover = By.cssSelector("span[class*='yui-menu-button-hover']");
    private final By storeTypeDropdownButton = By.cssSelector("button[id$='_default-store-menu-button-button']");
    private final By searchButton = By.cssSelector("button[id$='_default-search-button-button']");
    private final By searchButtonAfterHover = By.cssSelector("span[class*='yui-push-button-hover']");
    private final By resultNoItemsFound = By.cssSelector("td[class='yui-dt-empty'] div[class='yui-dt-liner']");
    private final By zeroResultsFound = By.cssSelector("div[class='search-main'] div[id*='default-search-bar']");
    private final By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private final By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private final By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");
    private final By options = By.cssSelector(".yuimenu.visible li>a");
    private final By visibleDropdown = By.cssSelector("div[class*='yui-button-menu yui-menu-button-menu'][style*='visibility: visible;']");
    private final By parentRows = By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div");
    private final By referenceRows = By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a");

    private final String fileNameRow = "//a[text()='cm:%s']/../../..";
    private final String loadingMessage = "//div[contains(text(), '%s')]";

    public NodeBrowserPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/node-browser";
    }

    public NodeBrowserPage selectSearchType(SearchType searchType)
    {
        webElementInteraction.waitUntilElementIsVisible(searchTypeDropdownButton, 3000);
        webElementInteraction.mouseOver(webElementInteraction.findElement(searchTypeDropdownButton), 3000);
        webElementInteraction.waitUntilElementIsVisible(buttonStateAfterHover, 3000);
        webElementInteraction.clickElement(buttonStateAfterHover, 3000);
        webElementInteraction.waitUntilElementIsVisible(visibleDropdown);
        webElementInteraction.findFirstElementWithValue(options, searchType.getSearchType()).click();

        return this;
    }

    public NodeBrowserPage assertSearchTypeIsSelected(SearchType searchType)
    {
        webElementInteraction.mouseOver(webElementInteraction.findElement(searchTypeDropdownButton), 3000);
        webElementInteraction.waitUntilElementIsVisible(buttonStateAfterHover, 3000);
        assertEquals(searchType.getSearchType(), webElementInteraction.getElementText(searchTypeDropdownButton));
        return this;
    }

    public NodeBrowserPage assertStoreTypeIsSelected(StoreType storeType)
    {
        assertEquals(storeType.getStoreType(), webElementInteraction.getElementText(storeTypeDropdownButton));
        return this;
    }

    public NodeBrowserPage clickSearch()
    {
        WebElement search = webElementInteraction.findElement(searchButton);
        webElementInteraction.mouseOver(search, 3000);
        webElementInteraction.clickElement(searchButtonAfterHover);
        webElementInteraction.waitUntilElementDisappears(By.xpath(String.format(loadingMessage, language.translate("nodeBrowser.searching"))), WAIT_5.getValue());
        webElementInteraction.waitUntilElementIsVisible(searchButton, 3000);
        waitForResult();
        return this;
    }

    private void waitForResult()
    {
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(loadingMessage,  language.translate("nodeBrowser.searchTook"))), 3000);
    }

    public NodeBrowserPage assertSearchButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public NodeBrowserPage assertAllColumnsAreDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(nameColumn);
        webElementInteraction.waitUntilElementIsVisible(parentColumn);
        webElementInteraction.waitUntilElementIsVisible(referenceColumn);

        assertTrue(webElementInteraction.isElementDisplayed(nameColumn), "Name column is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(parentColumn), "Parent column is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(referenceColumn), "Reference column is displayed");

        return this;
    }

    public NodeBrowserPage searchFor(String searchItem)
    {
        webElementInteraction.clearAndType(searchInput, searchItem);
        return this;
    }

    private WebElement getResultRowWithRetry(String contentName)
    {
        By fileRow = By.xpath(String.format(fileNameRow, contentName));
        int retry = 0;

        while (retry < WAIT_60.getValue() && !webElementInteraction.isElementDisplayed(fileRow))
        {
            
            log.error("Wait until content {} is found", contentName);
            webElementInteraction.refresh();
            webElementInteraction.waitInSeconds(WAIT_2.getValue());
            retry++;
        }
        return webElementInteraction.waitUntilElementIsVisible(fileRow);
    }

    public String getParentFor(String fileName)
    {
        return getResultRowWithRetry(fileName).findElement(parentRows).getText();
    }

    public NodeBrowserPage assertParentIs(String file, String parent)
    {
        log.info("Assert parent for {} is {}", file, parent);
        assertTrue(getParentFor(file).contains(parent), String.format("Parent result for %s is wrong.", file));
        return this;
    }

    public NodeBrowserPage assertParentForFileIsSite(FileModel file, SiteModel site)
    {
        return assertParentIs(file.getName(), site.getId());
    }

    public String getReferenceFor(String fileName)
    {
        return getResultRowWithRetry(fileName).findElement(referenceRows).getText();
    }

    public NodeBrowserPage assertReferenceForFileIsCorrect(FileModel file)
    {
        log.info("Assert reference for file {} is correct ", file.getName());
        assertTrue(getReferenceFor(file.getName()).contains(file.getNodeRefWithoutVersion()),
            "Reference is correct");
        return this;
    }

    public NodeBrowserPage assertReferenceContainsValue(String reference)
    {
        log.info("Assert reference %s is displayed in results");
        WebElement referenceRow = webElementInteraction.waitUntilElementIsVisible(referenceRows);
        webElementInteraction.waitUntilElementContainsText(referenceRow, reference);
        assertTrue(referenceRow.getText().contains(reference));

        return this;
    }

    public NodeBrowserPage assertNoItemsFoundLabelEquals(String expectedLabel)
    {
        webElementInteraction.waitUntilElementContainsText(zeroResultsFound, "found 0 results");
        assertEquals(webElementInteraction.getElementText(resultNoItemsFound), expectedLabel,
            String.format("No items found label %s not equals ", expectedLabel));
        return this;
    }

    //todo: move into separate file
    public enum SearchType
    {
        STORE_ROOT("storeroot"),
        NODEREF("noderef"),
        XPATH("xpath"),
        LUCENE("lucene"),
        FTS_ALFRESCO("fts-alfresco"),
        CMIS_STRICT("cmis-strict"),
        CMIS_ALFRESCO("cmis-alfresco"),
        DB_AFTS("db-afts"),
        DB_CMIS("db-cmis");

        private String searchType;

        SearchType(String searchType)
        {
            this.searchType = searchType;
        }

        public String getSearchType()
        {
            return this.searchType;
        }
    }

    //todo: move this into separate file
    public enum StoreType
    {
        ALFRESCO_USER_STORE("user://alfrescoUserStore"),
        SYSTEM("system://system"),
        LIGHT_WEIGHT_STORE("workspace://lightWeightVersionStore"),
        VERSION_2_STORE("workspace://version2Store"),
        ARCHIVE_SPACES_STORE("archive://SpacesStore"),
        WORKSPACE_SPACES_STORE("workspace://SpacesStore");

        private String storeType;

        StoreType(String storeType)
        {
            this.storeType = storeType;
        }

        public String getStoreType()
        {
            return this.storeType;
        }
    }
}
