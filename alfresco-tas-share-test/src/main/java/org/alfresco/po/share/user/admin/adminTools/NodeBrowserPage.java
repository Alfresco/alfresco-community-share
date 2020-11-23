package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class NodeBrowserPage extends SharePage2<NodeBrowserPage>
{
    private By searchInput = By.cssSelector("div.search-text textarea");

    @RenderWebElement
    private By searchTypeDropdownButton = By.cssSelector("button[id$='_default-lang-menu-button-button']");
    @RenderWebElement
    private By storeTypeDropdownButton = By.cssSelector("button[id$='_default-store-menu-button-button']");
    private By searchButton = By.cssSelector("button[id$='_default-search-button-button']");
    @RenderWebElement
    private By searchResults = By.cssSelector(".search-main");
    private By results = By.cssSelector(".yui-dt-data > tr");
    private By resultNoItemsFound = By.cssSelector(".yui-dt-empty > div");
    private By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");
    private By options = By.cssSelector(".yuimenu.visible li>a");
    private By visibleDropdown = By.cssSelector(".yui-button-menu.yui-menu-button-menu.visible");
    private String fileNameRow = "//a[text()='cm:%s']/../../..";
    private By parentRows = By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div");
    private By referenceRows = By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a");
    private String loadingMessage = "//div[contains(text(), '%s')]";

    public NodeBrowserPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/node-browser";
    }

    public NodeBrowserPage selectSearchType(SEARCH_TYPE searchType)
    {
        getBrowser().waitUntilElementVisible(searchTypeDropdownButton);
        getBrowser().waitUntilElementClickable(searchTypeDropdownButton).click();
        getBrowser().waitUntilElementVisible(visibleDropdown);
        getBrowser().findFirstElementWithValue(options, searchType.getSearchType()).click();

        return (NodeBrowserPage) this.renderedPage();
    }

    public NodeBrowserPage selectStoreType(SELECT_STORE storeType)
    {
        clickElement(storeTypeDropdownButton);
        getBrowser().waitUntilElementVisible(visibleDropdown);
        getBrowser().findFirstElementWithValue(options, storeType.getStoreType()).click();

        return (NodeBrowserPage) this.renderedPage();
    }

    public NodeBrowserPage assertSearchTypeIsSelected(SEARCH_TYPE searchType)
    {
        assertTrue(getElementText(searchTypeDropdownButton).equals(searchType.getSearchType()));
        return this;
    }

    public NodeBrowserPage assertStoreTypeIsSelected(SELECT_STORE storeType)
    {
        assertTrue(getElementText(storeTypeDropdownButton).equals(storeType.getStoreType()));
        return this;
    }

    public NodeBrowserPage clickSearch()
    {
        getBrowser().waitUntilElementClickable(searchButton).click();
        waitForResult();
        return (NodeBrowserPage) this.renderedPage();
    }

    private void waitForResult()
    {
        getBrowser().waitUntilElementDisappears(By.xpath(String.format(loadingMessage, language.translate("nodeBrowser.searching"))));
        getBrowser().waitUntilElementIsPresent(By.xpath(String.format(loadingMessage, language.translate("nodeBrowser.searchTook"))));
        getBrowser().waitUntilElementVisible(By.xpath(String.format(loadingMessage,  language.translate("nodeBrowser.searchTook"))));
    }

    public NodeBrowserPage assertSearchButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public NodeBrowserPage assertAllColumnsAreDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(nameColumn), "Name column is displayed");
        assertTrue(getBrowser().isElementDisplayed(parentColumn), "Parent column is displayed");
        assertTrue(getBrowser().isElementDisplayed(referenceColumn), "Reference column is displayed");

        return this;
    }

    public NodeBrowserPage searchFor(String searchItem)
    {
        clearAndType(searchInput, searchItem);
        return this;
    }

    private WebElement getResultRow(String name)
    {
        return getBrowser().waitWithRetryAndReturnWebElement(By.xpath(String.format(fileNameRow, name)), 1, WAIT_15);
    }

    public String getParentFor(String fileName)
    {
        return getResultRow(fileName).findElement(parentRows).getText();
    }

    public NodeBrowserPage assertParentIs(String file, String parent)
    {
        LOG.info(String.format("Assert parent for %s is %s", file, parent));
        assertTrue(getParentFor(file).contains(parent), String.format("Parent result for %s is wrong.", file));
        return this;
    }

    public NodeBrowserPage assertParentForFileIsSite(FileModel file, SiteModel site)
    {
        return assertParentIs(file.getName(), site.getId());
    }

    public String getReferenceFor(String fileName)
    {
        return getResultRow(fileName).findElement(referenceRows).getText();
    }

    public NodeBrowserPage assertReferenceForFileIsCorrect(FileModel file)
    {
        LOG.info(String.format("Assert reference for file %s is correct", file.getName()));
        assertTrue(getReferenceFor(file.getName()).contains(file.getNodeRefWithoutVersion()),
            "Reference is correct");
        return this;
    }

    public NodeBrowserPage assertReferenceContainsValue(String reference)
    {
        LOG.info("Assert reference %s is displayed in results");
        WebElement referenceRow = getBrowser().waitUntilElementVisible(referenceRows);
        getBrowser().waitUntilElementContainsText(referenceRow, reference);
        assertTrue(referenceRow.getText().contains(reference));

        return this;
    }

    public NodeBrowserPage assertNoItemsFoundIsDisplayed()
    {
        getBrowser().waitUntilElementVisible(resultNoItemsFound);
        assertTrue(getBrowser().isElementDisplayed(resultNoItemsFound), "No items found is displayed");
        return this;
    }

    public NodeBrowserPage assertNoItemsFoundLabelIsCorrect()
    {
        getBrowser().waitUntilElementVisible(resultNoItemsFound);
        assertEquals(getElementText(resultNoItemsFound), language.translate("nodeBrowser.noItemsFound"));
        return this;
    }

    public enum SEARCH_TYPE
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

        SEARCH_TYPE(String searchType)
        {
            this.searchType = searchType;
        }

        public String getSearchType()
        {
            return this.searchType;
        }
    }

    public enum SELECT_STORE
    {
        ALFRESCO_USER_STORE("user://alfrescoUserStore"),
        SYSTEM("system://system"),
        LIGHT_WEIGHT_STORE("workspace://lightWeightVersionStore"),
        VERSION_2_STORE("workspace://version2Store"),
        ARCHIVE_SPACES_STORE("archive://SpacesStore"),
        WORKSPACE_SPACES_STORE("workspace://SpacesStore");

        private String storeType;

        SELECT_STORE(String storeType)
        {
            this.storeType = storeType;
        }

        public String getStoreType()
        {
            return this.storeType;
        }
    }
}
