package org.alfresco.po.share.toolbar;

import java.util.List;

import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

@Primary
@PageObject
public class Toolbar extends HtmlPage
{
    @RenderWebElement
    @FindBy (id = "SHARE_HEADER")
    protected WebElement toolbar;
    @FindBy (css = "[widgetid=\"HEADER_HOME\"]")
    protected WebElement homeLink;
    @FindBy (id = "HEADER_MY_FILES_text")
    protected WebElement myFilesLink;
    @FindBy (id = "HEADER_SHARED_FILES_text")
    protected WebElement sharedFilesLink;
    @FindBy (id = "HEADER_SITES_MENU_text")
    protected WebElement sitesLink;
    @FindBy (id = "HEADER_TASKS_text")
    protected WebElement tasksLink;
    @FindBy (id = "HEADER_PEOPLE_text")
    protected WebElement peopleLink;
    @FindBy (id = "HEADER_REPOSITORY_text")
    protected WebElement repositoryLink;
    @FindBy (id = "HEADER_SITES_CONSOLE_text")
    protected WebElement sitesManagerLink;
    @FindBy (id = "HEADER_ADMIN_CONSOLE_text")
    protected WebElement adminToolsLink;
    @FindBy (css = "div[id='HEADER_USER_MENU_POPUP']")
    protected WebElement userMenuLink;
    @RenderWebElement
    @FindBy (css = ".alf-search-icon")
    protected WebElement searchIcon;
    @FindBy (id = "HEADER_SEARCH_BOX_ADVANCED_SEARCH_text")
    protected WebElement advancedSearchLink;
    @RenderWebElement
    @FindBy (id = "HEADER_SEARCHBOX_FORM_FIELD")
    protected WebElement searchBoxInput;
    @FindAll (@FindBy (css = "div.alf-live-search-sites-list div.alf-livesearch-item>a"))
    protected List<WebElement> searchSitesList;
    @FindBy (css = ".alfresco-header-SearchBox-clear")
    protected WebElement clearSearchBox;
    @Autowired
    SearchPage searchPage;
    @Autowired
    private AdminToolsPage adminToolsPage;
    @Autowired
    private SitesManagerPage sitesManagerPage;
    @FindBy (css = "div.alf-livesearch-item>a")
    private List<WebElement> searchResultsInToolbar;

    @FindBy (css = "span.alfresco-buttons-AlfButton.confirmationButton.call-to-action.dijitButton")
    private WebElement searchButton;

    @FindBy (id = "HEADER_MY_FILES")
    private WebElement myFilesButton;

    public boolean isToolbarDisplayed()
    {
        return browser.isElementDisplayed(toolbar);
    }

    public boolean isHomeDisplayed()
    {
        return browser.isElementDisplayed(homeLink);
    }

    public void clickHome()
    {
        getBrowser().waitUntilElementVisible(homeLink);
        getBrowser().waitUntilElementClickable(homeLink).click();
    }

    public boolean isMyFilesDisplayed()
    {
        return browser.isElementDisplayed(myFilesLink);
    }

    public void clickMyFiles()
    {
        myFilesLink.click();
    }

    public boolean isSharedFilesDisplayed()
    {
        return browser.isElementDisplayed(sharedFilesLink);
    }

    public void clickSharedFiles()
    {
        sharedFilesLink.click();
        browser.waitInSeconds(1);
    }

    public boolean isSitesDisplayed()
    {
        return browser.isElementDisplayed(sitesLink);
    }

    public boolean isTasksDisplayed()
    {
        return browser.isElementDisplayed(tasksLink);
    }

    public boolean isPeopleDisplayed()
    {
        return browser.isElementDisplayed(peopleLink);
    }

    public void clickPeople()
    {
        peopleLink.click();
    }

    public boolean isRepositoryDisplayed()
    {
        return browser.isElementDisplayed(repositoryLink);
    }

    public void clickRepository()
    {
        repositoryLink.click();
    }

    public boolean isSitesManagerDisplayed()
    {
        return browser.isElementDisplayed(sitesManagerLink);
    }

    public SitesManagerPage clickSitesManager()
    {
        sitesManagerLink.click();

        return (SitesManagerPage) sitesManagerPage.renderedPage();
    }

    public boolean isAdminToolsDisplayed()
    {
        return browser.isElementDisplayed(adminToolsLink);
    }

    public AdminToolsPage clickAdminTools()
    {
        adminToolsLink.click();
        return (AdminToolsPage) adminToolsPage.renderedPage();
    }

    public boolean isUserMenuDisplayed()
    {
        return browser.isElementDisplayed(userMenuLink);
    }

    public String getSearchBoxPlaceholder()
    {
        return searchBoxInput.getAttribute("placeholder");
    }

    public boolean isSearchBoxDisplayed()
    {
        return browser.isElementDisplayed(searchBoxInput);
    }

    public boolean isSearchIconDisplayed()
    {
        return browser.isElementDisplayed(searchIcon);
    }

    public boolean isClearSearchBoxDisplayed()
    {
        return browser.isElementDisplayed(clearSearchBox);
    }

    public void clickAdvancedSearch()
    {
        searchIcon.click();
        advancedSearchLink.click();
    }

    /**
     * Type in toolbar
     */
    public void searchInToolbar(String item)
    {
        searchBoxInput.clear();
        searchBoxInput.sendKeys(item);
    }

    /**
     * Type in toolbar and press 'Enter'
     */
    public SearchPage search(String keys)
    {
        searchBoxInput.clear();
        searchBoxInput.sendKeys(keys);
        searchBoxInput.sendKeys(Keys.RETURN);

        return (SearchPage) searchPage.renderedPage();
    }

    public SearchPage sendEnterAction()
    {
        searchBoxInput.sendKeys(Keys.RETURN);

        return (SearchPage) searchPage.renderedPage();
    }

    /**
     * Click a result from live search results list
     *
     * @param query search result to be clicked on
     * @param page  that will be rendered
     * @return html rendered page
     */
    public HtmlPage clickResult(String query, HtmlPage page)
    {
        for (WebElement result : searchResultsInToolbar)
        {
            if (result.getText().contains(query))
                result.click();
        }
        return page.renderedPage();
    }

    /**
     * Verify live search results are displayed
     */
    public boolean isLiveSearchResultsListDisplayed()
    {
        return searchResultsInToolbar.size() > 0;
    }

    /**
     * Verify query is displayed in live search result
     */
    public boolean isResultDisplayedInLiveSearch(String query)
    {
        int j = 0;
        while (!browser.isElementDisplayed(By.xpath("//div[contains(@class, 'alf-live-search')]//div")) && j < 4)
        {
            browser.refresh();
            this.searchInToolbar(query);
            j++;
        }
        browser.waitUntilElementsVisible(By.xpath("//div[contains(@class, 'alf-live-search')]//div"));
        browser.waitUntilElementsVisible(By.cssSelector("div.alf-livesearch-item>a"));

        for (WebElement aSearchResultsInToolbar : searchResultsInToolbar)
        {
            if (aSearchResultsInToolbar.getText().contains(query))
                return true;
        }
        return false;
    }

    /**
     * Click a result from live search results list
     *
     * @param resultToBeClicked search result to be clicked on
     */
    public void clickResultFromLiveSearch(String resultToBeClicked)
    {
        for (WebElement result : searchResultsInToolbar)
        {
            if (result.getText().contains(resultToBeClicked))
                result.click();
        }
    }

    public void clickSearchButton()
    {
        searchButton.click();
    }

    public boolean isMyFilesLinkDisplayed()
    {
        return myFilesButton.isDisplayed();
    }

    public void clickMyFilesInToolbar()
    {
        myFilesButton.click();
    }

    public void searchWithoutEnter(String searchTerm)
    {
        searchBoxInput.clear();
        searchBoxInput.sendKeys(searchTerm);
    }
}