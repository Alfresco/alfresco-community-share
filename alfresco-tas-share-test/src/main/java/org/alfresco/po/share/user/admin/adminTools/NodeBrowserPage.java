package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.common.Utils;
import org.alfresco.utility.model.FileModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.List;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class NodeBrowserPage extends AdminToolsPage
{
    @FindBy (css = "div.search-text textarea")
    private WebElement searchInput;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-lang-menu-button-button']")
    private WebElement searchTypeDropdownButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-store-menu-button-button']")
    private WebElement storeTypeDropdownButton;

    @FindBy (css = "button[id$='_default-search-button-button']")
    private WebElement searchButton;

    @RenderWebElement
    @FindBy (css = ".search-main")
    private WebElement searchResults;

    @FindAll(@FindBy (css = ".yui-dt-data > tr"))
    protected List<WebElement> results;

    @FindBy (css = ".yui-dt-empty > div")
    private WebElement resultNoItemsFound;

    private By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");
    private By options = By.cssSelector(".yuimenu.visible li>a");
    private By visibleDropdown = By.cssSelector(".yui-button-menu.yui-menu-button-menu.visible");
    private String fileNameRow = "//a[text()='cm:%s']/../../..";
    private By parentRows = By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div");
    private By referenceRows = By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a");
    private String loadingMessage = "//div[contains(text(), '%s')]";

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/node-browser";
    }

    public NodeBrowserPage selectSearchType(SEARCH_TYPE searchType)
    {
        searchTypeDropdownButton.click();
        browser.waitUntilElementVisible(visibleDropdown);
        browser.findFirstElementWithValue(options, searchType.getSearchType()).click();
        return (NodeBrowserPage) this.renderedPage();
    }

    public NodeBrowserPage selectStoreType(SELECT_STORE storeType)
    {
        storeTypeDropdownButton.click();
        browser.waitUntilElementVisible(visibleDropdown);
        browser.findFirstElementWithValue(options, storeType.getStoreType()).click();
        return (NodeBrowserPage) this.renderedPage();
    }

    public NodeBrowserPage assertSearchTypeIsSelected(SEARCH_TYPE searchType)
    {
        Assert.assertTrue(searchTypeDropdownButton.getText().equals(searchType.getSearchType()));
        return this;
    }

    public NodeBrowserPage assertStoreTypeIsSelected(SELECT_STORE storeType)
    {
        Assert.assertTrue(storeTypeDropdownButton.getText().equals(storeType.getStoreType()));
        return this;
    }

    public NodeBrowserPage clickSearch()
    {
        browser.waitUntilElementClickable(searchButton).click();
        waitForResult();
        return (NodeBrowserPage) this.renderedPage();
    }

    private void waitForResult()
    {
        browser.waitUntilElementDisappears(By.xpath(String.format(loadingMessage, language.translate("nodeBrowser.searching"))));
        browser.waitUntilElementIsPresent(By.xpath(String.format(loadingMessage, language.translate("nodeBrowser.searchTook"))));
        browser.waitUntilElementVisible(By.xpath(String.format(loadingMessage,  language.translate("nodeBrowser.searchTook"))));
    }

    public NodeBrowserPage assertSearchButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public NodeBrowserPage assertAllColumnsAreDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(nameColumn), "Name column is displayed");
        Assert.assertTrue(browser.isElementDisplayed(parentColumn), "Parent column is displayed");
        Assert.assertTrue(browser.isElementDisplayed(referenceColumn), "Reference column is displayed");
        return this;
    }

    public NodeBrowserPage searchFor(String searchItem)
    {
        Utils.clearAndType(searchInput, searchItem);
        return this;
    }

    private WebElement getResultRow(String name)
    {
        By searchRow = By.xpath(String.format(fileNameRow, name));
        browser.waitUntilElementIsDisplayedWithRetry(searchRow, 1, WAIT_15_SEC);
        return browser.findElement(searchRow);
    }

    public String getParentFor(String fileName)
    {
        return getResultRow(fileName).findElement(parentRows).getText();
    }

    public NodeBrowserPage assertParentIs(String file, String parent)
    {
        LOG.info(String.format("Assert parent for %s is %s", file, parent));
        Assert.assertTrue(getParentFor(file).contains(parent), String.format("Parent result for %s is wrong.", file));
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
        Assert.assertTrue(getReferenceFor(file.getName()).contains(file.getNodeRefWithoutVersion()),
            "Reference is correct");
        return this;
    }

    public NodeBrowserPage assertReferenceContainsValue(String reference)
    {
        LOG.info("Assert reference %s is displayed in results");
        WebElement referenceRow = browser.waitUntilElementVisible(referenceRows);
        browser.waitUntilElementContainsText(referenceRow, reference);
        Assert.assertTrue(referenceRow.getText().contains(reference));
        return this;
    }

    public NodeBrowserPage assertNoItemsFoundIsDisplayed()
    {
        browser.waitUntilElementVisible(resultNoItemsFound);
        Assert.assertTrue(browser.isElementDisplayed(resultNoItemsFound), "No items found is displayed");
        return this;
    }

    public NodeBrowserPage assertNoItemsFoundLabelIsCorrect()
    {
        browser.waitUntilElementVisible(resultNoItemsFound);
        Assert.assertEquals(resultNoItemsFound.getText(), language.translate("nodeBrowser.noItemsFound"));
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
