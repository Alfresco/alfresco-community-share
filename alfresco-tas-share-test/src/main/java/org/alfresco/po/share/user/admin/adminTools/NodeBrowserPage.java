package org.alfresco.po.share.user.admin.adminTools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.common.Utils;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.element.Table;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class NodeBrowserPage extends AdminToolsPage
{
    @FindBy (xpath = "//div[@class='title']/label[text() = 'Node Browser']")
    private WebElement nodeBrowserDiv;
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
    @RenderWebElement
    @FindBy (css = "div[id$='_default-datatable']")
    private Table resultsTable;
    private By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");
    private By options = By.cssSelector(".yuimenu.visible li>a");
    private By visibleDropdown = By.cssSelector(".yui-button-menu.yui-menu-button-menu.visible");

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

    public boolean isSearchTypeSelected(SEARCH_TYPE searchType)
    {
        return searchTypeDropdownButton.getText().equals(searchType.getSearchType());
    }

    public boolean isStoreTypeSelected(SELECT_STORE storeType)
    {
        return storeTypeDropdownButton.getText().equals(storeType.getStoreType());
    }

    public NodeBrowserPage clickSearchButton()
    {
        browser.waitUntilElementClickable(searchButton).click();
        return (NodeBrowserPage) this.renderedPage();
    }

    public boolean isSearchButtonPresent()
    {
        return browser.isElementDisplayed(searchButton);
    }

    public boolean isNameColumnPresent()
    {
        return browser.isElementDisplayed(nameColumn);
    }

    public boolean isParentColumnPresent()
    {
        return browser.isElementDisplayed(parentColumn);
    }

    public boolean isReferenceColumnPresent()
    {
        return browser.isElementDisplayed(referenceColumn);
    }

    public void writeInSearchInput(String searchItem)
    {
        Utils.clearAndType(searchInput, searchItem);
    }

    private Map<String, List<String>> getResults()
    {
        Map<String, List<String>> results = new HashMap<>();
        List<WebElement> nameRows = browser.waitUntilElementsVisible(By.cssSelector("div[id$='-datatable'] td[class*='col-name'] div a"));
        List<WebElement> parentRows = browser.waitUntilElementsVisible(By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div"));
        List<WebElement> referenceRows = browser.waitUntilElementsVisible(By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a"));

        for (int i = 0; i < nameRows.size(); i++)
        {
            String trc = nameRows.get(i).getText();
            results.put(trc.substring(trc.lastIndexOf(":") + 1), Arrays.asList(parentRows.get(i).getText(), referenceRows.get(i).getText()));
        }
        return results;
    }

    public String getParentFor(String fileName)
    {
        return getResults().get(fileName).get(0);
    }

    public List<List<String>> getRowText()
    {
        return resultsTable.getRowsAsString();
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
