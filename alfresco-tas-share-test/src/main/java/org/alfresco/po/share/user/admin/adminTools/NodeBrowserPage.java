package org.alfresco.po.share.user.admin.adminTools;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_5;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.enums.SearchType;
import org.alfresco.po.enums.StoreType;
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
    private final By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private final By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private final By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");
    private final By options = By.cssSelector(".yuimenu.visible li>a");
    private final By visibleDropdown = By.cssSelector("div[class*='yui-button-menu yui-menu-button-menu'][style*='visibility: visible;']");
    private final By parentRows = By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div");
    private final By referenceRows = By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a");

    private final String fileNameRow = "//a[text()='cm:%s']/../../..";

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
        waitUntilElementIsVisible(searchTypeDropdownButton);
        mouseOver(findElement(searchTypeDropdownButton), 3000);

        waitUntilElementIsVisible(buttonStateAfterHover);
        clickElement(buttonStateAfterHover);

        waitUntilElementIsVisible(visibleDropdown);
        clickElement(findFirstElementWithValue(options, searchType.getSearchType()));

        return this;
    }

    public NodeBrowserPage assertSearchTypeIsSelected(SearchType searchType)
    {
        waitUntilElementIsVisible(searchTypeDropdownButton);
        mouseOver(findElement(searchTypeDropdownButton), 3000);
        waitUntilElementIsVisible(buttonStateAfterHover);
        assertEquals(searchType.getSearchType(), getElementText(searchTypeDropdownButton));
        return this;
    }

    public NodeBrowserPage assertStoreTypeIsSelected(StoreType storeType)
    {
        assertEquals(storeType.getStoreType(), getElementText(storeTypeDropdownButton));
        return this;
    }

    public NodeBrowserPage clickSearch()
    {
        WebElement search = findElement(searchButton);
        mouseOver(search, 3000);
        clickElement(searchButtonAfterHover);
        return this;
    }

    public NodeBrowserPage assertSearchButtonIsDisplayed()
    {
        waitUntilElementIsVisible(searchButton);
        assertTrue(isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public NodeBrowserPage assertAllColumnsAreDisplayed()
    {
        waitUntilElementIsVisible(nameColumn);
        waitUntilElementIsVisible(parentColumn);
        waitUntilElementIsVisible(referenceColumn);

        assertTrue(isElementDisplayed(nameColumn), "Name column is displayed");
        assertTrue(isElementDisplayed(parentColumn), "Parent column is displayed");
        assertTrue(isElementDisplayed(referenceColumn), "Reference column is displayed");

        return this;
    }

    public NodeBrowserPage searchFor(String searchItem)
    {
        clearAndType(searchInput, searchItem);
        return this;
    }

    /**
     * Method to get document row with retry
     *
     * @param contentName content name
     * @return result row web element
     *
     * @implNote The default value of solr indexing resources in database is 10 seconds. In order to
     * avoid reaching maximum number of retries and have failing tests, we decided to increase that
     * wait time within while loop to 5 seconds.
     * <p>
     * https://github.com/Alfresco/SearchServices/blob/master/search-services/alfresco-search/src/main/resources/solr/instance/templates/rerank/conf/solrcore.properties#L58
     */
    private WebElement getResultRowWithRetry(String contentName)
    {
        By fileRow = By.xpath(String.format(fileNameRow, contentName));

        int retryCount = 0;
        while (retryCount < RETRY_TIME_80.getValue() && !isElementDisplayed(fileRow))
        {
            log.warn("Content {} not displayed - retry: {}", contentName, retryCount);
            refresh();
            waitInSeconds(WAIT_5.getValue());
            retryCount++;
        }
        return waitUntilElementIsVisible(fileRow);
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
        WebElement referenceRow = waitUntilElementIsVisible(referenceRows);
        waitUntilElementContainsText(referenceRow, reference);
        assertTrue(getElementText(referenceRows).contains(reference));

        return this;
    }

    public NodeBrowserPage assertNoItemsFoundLabelEquals(String expectedLabel)
    {
        waitUntilElementContainsText(resultNoItemsFound, language.translate("nodeBrowser.noItemsFound"));
        assertEquals(getElementText(resultNoItemsFound), expectedLabel,
            String.format("No items found label %s not equals ", expectedLabel));
        return this;
    }
}
