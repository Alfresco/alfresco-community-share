package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.SharePageObject2;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class Toolbar extends SharePageObject2
{
    @RenderWebElement
    private By toolbar = By.id("SHARE_HEADER");
    @RenderWebElement
    private By homeLink = By.cssSelector("div[widgetid='HEADER_HOME']");
    private By myFilesLink = By.id("HEADER_MY_FILES_text");
    private By sharedFilesLink = By.id("HEADER_SHARED_FILES_text");
    private By sitesLink = By.id("HEADER_SITES_MENU_text");
    private By tasksLink = By.id("HEADER_TASKS_text");
    private By peopleLink = By.id("HEADER_PEOPLE_text");
    private By repositoryLink = By.id("HEADER_REPOSITORY_text");
    private By sitesManagerLink = By.id("HEADER_SITES_CONSOLE_text");
    private By adminToolsLink = By.id("HEADER_ADMIN_CONSOLE_text");
    private By userMenuLink = By.id("HEADER_USER_MENU_POPUP");
    private By searchIcon = By.cssSelector(".alf-search-icon");
    private By advancedSearchLink = By.id("HEADER_SEARCH_BOX_ADVANCED_SEARCH_text");
    private By searchBoxInput = By.id("HEADER_SEARCHBOX_FORM_FIELD");
    private By clearSearchBox = By.cssSelector(".alfresco-header-SearchBox-clear");
    private By searchResultsInToolbar = By.cssSelector("div.alf-livesearch-item>a");
    private By searchButton = By.cssSelector("span.alfresco-buttons-AlfButton.confirmationButton.call-to-action.dijitButton");

    public Toolbar(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public Toolbar assertToolbarIsDisplayed()
    {
        LOG.info("Assert toolbar is displayed");
        assertTrue(getBrowser().isElementDisplayed(toolbar), "Toolbar is displayed");
        return this;
    }

    public ToolbarSitesMenu clickSites()
    {
        LOG.info("Click Sites");
        getBrowser().waitUntilElementClickable(sitesLink).click();
        return (ToolbarSitesMenu) new ToolbarSitesMenu(browser).renderedPage();
    }

    public Toolbar assertHomeIsDisplayed()
    {
        LOG.info("Assert Home is displayed");
        assertTrue(getBrowser().isElementDisplayed(homeLink), "Home is displayed");
        return this;
    }

    public void clickHome()
    {
        getBrowser().waitUntilElementVisible(homeLink);
        getBrowser().waitUntilElementClickable(homeLink).click();
    }

    public Toolbar assertMyFilesIsDisplayed()
    {
        LOG.info("Assert My Files is displayed");
        assertTrue(getBrowser().isElementDisplayed(myFilesLink), "My Files is displayed");
        return this;
    }

    public MyFilesPage clickMyFiles()
    {
        LOG.info("Click My Files");
        getBrowser().waitUntilElementClickable(myFilesLink).click();
        return (MyFilesPage) new MyFilesPage(browser).renderedPage();
    }

    public Toolbar assertSharedFilesIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        assertTrue(getBrowser().isElementDisplayed(sharedFilesLink), "Shared Files is displayed");
        return this;
    }

    public SharedFilesPage clickSharedFiles()
    {
        LOG.info("Click Shared Files");
        getBrowser().waitUntilElementClickable(sharedFilesLink).click();
        return (SharedFilesPage) new SharedFilesPage(browser).renderedPage();
    }

    public Toolbar assertSitesIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        assertTrue(getBrowser().isElementDisplayed(sitesLink), "Sites is displayed");
        return this;
    }

    public Toolbar assertTasksIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        assertTrue(getBrowser().isElementDisplayed(tasksLink), "Tasks is displayed");
        return this;
    }

    public ToolbarTasksMenu clickTasks()
    {
        LOG.info("Click Tasks");
        getBrowser().waitUntilElementClickable(tasksLink).click();
        return (ToolbarTasksMenu) new ToolbarTasksMenu(browser).renderedPage();
    }

    public Toolbar assertPeopleIsDisplayed()
    {
        LOG.info("Assert People is displayed");
        assertTrue(getBrowser().isElementDisplayed(peopleLink), "People is displayed");
        return this;
    }

    public PeopleFinderPage clickPeople()
    {
        LOG.info("Click People");
        getBrowser().waitUntilElementClickable(peopleLink).click();
        return (PeopleFinderPage) new PeopleFinderPage(browser).renderedPage();
    }

    public Toolbar assertRepositoryIsDisplayed()
    {
        LOG.info("Assert Repository is displayed");
        assertTrue(getBrowser().isElementDisplayed(repositoryLink), "Repository is displayed");
        return this;
    }

    public RepositoryPage clickRepository()
    {
        LOG.info("Click Repository");
        getBrowser().waitUntilElementClickable(repositoryLink).click();
        return (RepositoryPage) new RepositoryPage(browser).renderedPage();
    }

    public Toolbar assertSitesManagerIsDisplayed()
    {
        LOG.info("Assert Sites Manager is displayed");
        assertTrue(getBrowser().isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public Toolbar assertSitesManagerIsNotDisplayed()
    {
        LOG.info("Assert Sites Manager is NOT displayed");
        assertFalse(getBrowser().isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public SitesManagerPage clickSitesManager()
    {
        LOG.info("Click Sites Manager");
        getBrowser().waitUntilElementClickable(sitesManagerLink).click();
        return (SitesManagerPage) new SitesManagerPage(browser).renderedPage();
    }

    public Toolbar assertAdminToolsIsDisplayed()
    {
        LOG.info("Assert Admin Tools link is displayed");
        assertTrue(getBrowser().isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public Toolbar assertAdminToolsIsNotDisplayed()
    {
        LOG.info("Assert Admin Tools link is not displayed");
        assertFalse(getBrowser().isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public AdminToolsPage clickAdminTools()
    {
        LOG.info("Click Admin Tools");
        getBrowser().waitUntilElementClickable(adminToolsLink).click();
        return (AdminToolsPage) new AdminToolsPage(browser).renderedPage();
    }

    public Toolbar assertUserMenuIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        assertTrue(getBrowser().isElementDisplayed(userMenuLink), "User Menu link is displayed");
        return this;
    }

    public ToolbarUserMenu clickUserMenu()
    {
        LOG.info("Click User menu");
        getBrowser().waitUntilElementClickable(userMenuLink).click();
        return (ToolbarUserMenu) new ToolbarUserMenu(browser).renderedPage();
    }

    public String getSearchBoxPlaceholder()
    {
        return getBrowser().findElement(searchBoxInput).getAttribute("placeholder");
    }

    public Toolbar assertSearchInputIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        assertTrue(getBrowser().isElementDisplayed(searchBoxInput), "Search input is displayed");
        return this;
    }

    public Toolbar assertSearchIconIsDisplayed()
    {
        LOG.info("Assert Search icon is displayed");
        assertTrue(getBrowser().isElementDisplayed(searchIcon), "Search icon is displayed");
        return this;
    }

    public boolean isClearSearchBoxDisplayed()
    {
        return getBrowser().isElementDisplayed(clearSearchBox);
    }

    public AdvancedSearchPage clickAdvancedSearch()
    {
        LOG.info("Click Advanced Search page");
        getBrowser().waitUntilElementClickable(searchIcon).click();
        getBrowser().waitUntilElementVisible(advancedSearchLink).click();
        return (AdvancedSearchPage) new AdvancedSearchPage(browser).renderedPage();
    }

    public void searchInToolbar(String searchTerm)
    {
        clearAndType(getBrowser().waitUntilElementVisible(searchBoxInput), searchTerm);
    }

    public SearchPage search(String searchTerm)
    {
        searchInToolbar(searchTerm);
        getBrowser().findElement(searchBoxInput).sendKeys(Keys.RETURN);
        waitUntilNotificationMessageDisappears();
        SearchPage searchPage = new SearchPage(browser);
        searchPage.waitForPageToLoad();
        return (SearchPage) searchPage.renderedPage();
    }

    public void clickResult(String query)
    {
        List<WebElement> searchResults = getBrowser().waitUntilElementsVisible(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                result.click();
        }
    }

    public boolean isLiveSearchResultsListDisplayed()
    {
        return  getBrowser().findElements(searchResultsInToolbar).size() > 0;
    }

    public boolean isResultDisplayedInLiveSearch(String query)
    {
        int j = 0;
        while (!getBrowser().isElementDisplayed(By.xpath("//div[contains(@class, 'alf-live-search')]//div")) && j < 4)
        {
            getBrowser().refresh();
            this.searchInToolbar(query);
            j++;
        }
        getBrowser().waitUntilElementsVisible(By.xpath("//div[contains(@class, 'alf-live-search')]//div"));
        getBrowser().waitUntilElementsVisible(By.cssSelector("div.alf-livesearch-item>a"));

        List<WebElement> searchResults = getBrowser().findElements(searchResultsInToolbar);
        for (WebElement aSearchResultsInToolbar : searchResults)
        {
            if (aSearchResultsInToolbar.getText().contains(query))
                return true;
        }
        return false;
    }

    public void clickResultFromLiveSearch(String resultToBeClicked)
    {
        List<WebElement> results = getBrowser().waitUntilElementsVisible(searchResultsInToolbar);
        for (WebElement result : results)
        {
            if (result.getText().contains(resultToBeClicked))
            {
                result.click();
            }
        }
    }

    public void clickSearchButton()
    {
        getBrowser().waitUntilElementClickable(searchButton).click();
    }
}