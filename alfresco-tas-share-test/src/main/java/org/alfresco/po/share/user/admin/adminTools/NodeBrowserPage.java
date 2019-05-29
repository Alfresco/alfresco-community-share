package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class NodeBrowserPage extends AdminToolsPage
{
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

    public void clickSearchButton()
    {
        browser.waitUntilElementClickable(searchButton, 5).click();
        browser.waitInSeconds(3);
    }

    public boolean isSearchButtonPresent()
    {
        return searchButton.isDisplayed();
    }

    public boolean isNameColumnPresent()
    {
        return browser.findElement(nameColumn).isDisplayed();
    }

    public boolean isParentColumnPresent()
    {
        return browser.findElement(parentColumn).isDisplayed();
    }

    public boolean isReferenceColumnPresent()
    {
        return browser.findElement(referenceColumn).isDisplayed();
    }

    public void writeInSearchInput(String searchItem)
    {
        searchInput.clear();
        browser.waitInSeconds(2);
        searchInput.sendKeys(searchItem);
        browser.waitInSeconds(2);
    }

    private Map<String, List<String>> getResults()
    {
        Map<String, List<String>> results = new HashMap<>();
        List<WebElement> nameRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='col-name'] div a"));
        List<WebElement> parentRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div"));
        List<WebElement> referenceRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a"));

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
}
