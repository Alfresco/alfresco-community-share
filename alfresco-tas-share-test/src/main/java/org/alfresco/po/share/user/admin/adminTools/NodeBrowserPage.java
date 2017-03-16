package org.alfresco.po.share.user.admin.adminTools;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Razvan.Dorobantu
 */
@PageObject
public class NodeBrowserPage extends SharePage<NodeBrowserPage> implements AccessibleByMenuBar
{
    @Autowired
    private Toolbar toolbar;

    @Autowired
    AdminToolsPage adminToolsPage;

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

        SEARCH_TYPE(String searchType) {
            this.searchType = searchType;
        }

        public String getSearchType() {
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

        SELECT_STORE(String storeType) {
            this.storeType = storeType;
        }

        public String getStoreType() {
            return this.storeType;
        }
    }

    @RenderWebElement
    @FindBy(xpath = "//div[@class='title']/label[text() = 'Node Browser']")
    private WebElement nodeBrowserDiv;

    @FindBy(css = "div.search-text textarea")
    private WebElement searchInput;

    private By searchTypeDropdownButton = By.cssSelector("button[id$='_default-lang-menu-button-button']");
    private By storeTypeDropdownButton = By.cssSelector("button[id$='_default-store-menu-button-button']");
    private By searchButton = By.cssSelector("button[id$='_default-search-button-button']");
    private By nameColumn = By.cssSelector("table thead tr th a[href$='name']");
    private By parentColumn = By.cssSelector("table thead tr th a[href$='qnamePath']");
    private By referenceColumn = By.cssSelector("table thead tr th a[href$='nodeRef']");

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage navigateByMenuBar()
    {
        toolbar.clickAdminTools();
        browser.waitInSeconds(5);
        adminToolsPage.navigateToNodeFromToolsPanel("Node Browser");
        return (NodeBrowserPage) renderedPage();
    }

    @Override
    public String getRelativePath() { return "share/page/console/admin-console/node-browser"; }

    public void selectSearchType(SEARCH_TYPE searchType)
    {
        By option = By.xpath(String.format("//div[contains(@class, 'yuimenu') and contains(@style, 'visible')]//li[contains(@class, 'yuimenuitem')]/a[text()='%s']", searchType.getSearchType()));
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                browser.findElement(searchTypeDropdownButton).click();
                browser.waitInSeconds(3);
                browser.findElement(option).click();
                browser.waitInSeconds(3);
                break;
            }
            catch (NoSuchElementException | StaleElementReferenceException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(5);
            }
        }
    }

    public void selectStoreType(SELECT_STORE storeType)
    {
        By option = By.xpath(String.format("//div[contains(@class, 'yuimenu') and contains(@style, 'visible')]//li[contains(@class, 'yuimenuitem')]/a[text()='%s']", storeType.getStoreType()));
        int counter = 1;
        while (counter < 3)
        {
            try
            {
                browser.findElement(storeTypeDropdownButton).click();
                browser.waitInSeconds(3);
                browser.findElement(option).click();
                browser.waitInSeconds(3);
                break;
            }
            catch (NoSuchElementException | StaleElementReferenceException e)
            {
                counter++;
                browser.refresh();
                browser.waitInSeconds(5);
            }
        }
    }

    public boolean isSearchTypeSelected(SEARCH_TYPE searchType)
    {
        return browser.findElement(searchTypeDropdownButton).getText().equals(searchType.getSearchType());
    }

    public boolean isStoreTypeSelected(SELECT_STORE storeType)
    {
        return browser.findElement(storeTypeDropdownButton).getText().equals(storeType.getStoreType());
    }

    public void clickSearchButton() { browser.waitUntilElementClickable(searchButton, 5).click(); }

    public boolean isSearchButtonPresent() { return browser.findElement(searchButton).isDisplayed(); }

    public boolean isNameColumnPresent() { return browser.findElement(nameColumn).isDisplayed(); }

    public boolean isParentColumnPresent() { return browser.findElement(parentColumn).isDisplayed(); }

    public boolean isReferenceColumnPresent() { return browser.findElement(referenceColumn).isDisplayed(); }

    public void writeInSearchInput(String searchItem)
    {
        searchInput.clear();
        browser.waitInSeconds(2);
        searchInput.sendKeys(searchItem);
        browser.waitInSeconds(2);
    }

    public Map<String, List<String>> getResults()
    {
        Map<String, List<String>> results = new HashMap<>();
        List<WebElement> nameRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='col-name'] div a"));
        List<WebElement> parentRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='namePath'] div"));
        List<WebElement> referenceRows = browser.findElements(By.cssSelector("div[id$='-datatable'] td[class*='nodeRef'] div a"));

        for (int i=0; i<nameRows.size(); i++)
        {
            String trc = nameRows.get(i).getText();
            results.put(trc.substring(trc.lastIndexOf(":") + 1), Arrays.asList(parentRows.get(i).getText(), referenceRows.get(i).getText()));
        }
        return results;
    }
}
