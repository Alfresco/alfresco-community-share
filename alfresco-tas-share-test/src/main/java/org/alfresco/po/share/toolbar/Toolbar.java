package org.alfresco.po.share.toolbar;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Toolbar extends BasePage
{
    private final By toolbarLocator = By.cssSelector("div[id='SHARE_HEADER']");
    private final By homeLink = By.cssSelector("div[widgetid='HEADER_HOME']");
    private final By myFilesLink = By.cssSelector("span[id='HEADER_MY_FILES_text']");
    private final By sharedFilesLink = By.cssSelector("span[id='HEADER_SHARED_FILES_text']");
    private final By sitesLink = By.cssSelector("span[id='HEADER_SITES_MENU_text']");
    private final By tasksLink = By.cssSelector("span[id='HEADER_TASKS_text']");
    private final By peopleLink = By.cssSelector("span[id='HEADER_PEOPLE_text']");
    private final By repositoryLink = By.cssSelector("span[id='HEADER_REPOSITORY_text']");
    private final By sitesManagerLink = By.cssSelector("span[id='HEADER_SITES_CONSOLE_text']");
    private final By adminToolsLink = By.cssSelector("span[id='HEADER_ADMIN_CONSOLE_text']");
    private final By userMenuLink = By.cssSelector("div[id='HEADER_USER_MENU_POPUP']");
    private final By bodyLocator = By.cssSelector("body[id='Share']");
    private final By searchIcon = By.cssSelector(".alf-search-icon");
    private final By advancedSearchLink = By.cssSelector("td[id='HEADER_SEARCH_BOX_ADVANCED_SEARCH_text']");
    private final By searchBoxInput = By.cssSelector("input[id='HEADER_SEARCHBOX_FORM_FIELD']");
    private final By clearSearchBox = By.cssSelector(".alfresco-header-SearchBox-clear");
    private final By searchResultsInToolbar = By.cssSelector("div.alf-livesearch-item>a");
    private final By searchButton = By.cssSelector("span.alfresco-buttons-AlfButton.confirmationButton.call-to-action.dijitButton");

    public Toolbar(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public Toolbar assertToolbarIsDisplayed()
    {
        LOG.info("Assert toolbar is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(toolbarLocator), "Toolbar is displayed");
        return this;
    }

    public ToolbarSitesMenu clickSites()
    {
        LOG.info("Click Sites");
        webElementInteraction.clickElement(sitesLink);
        ToolbarSitesMenu toolbarSitesMenu = new ToolbarSitesMenu(webDriver);
        toolbarSitesMenu.waitForMySitesMenuToBeOpened();
        return toolbarSitesMenu;
    }

    public Toolbar assertHomeIsDisplayed()
    {
        LOG.info("Assert Home is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(homeLink), "Home is displayed");
        return this;
    }

    public void clickHome()
    {
        webElementInteraction.clickElement(homeLink);
    }

    public Toolbar assertMyFilesIsDisplayed()
    {
        LOG.info("Assert My Files is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(myFilesLink), "My Files is displayed");
        return this;
    }

    public MyFilesPage clickMyFiles()
    {
        LOG.info("Click My Files");
        webElementInteraction.clickElement(myFilesLink);
        return new MyFilesPage(webDriver);
    }

    public SharedFilesPage clickSharedFiles()
    {
        LOG.info("Click Shared Files");
        webElementInteraction.clickElement(sharedFilesLink);
        return new SharedFilesPage(webDriver);
    }

    public Toolbar assertSitesIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(sitesLink), "Sites is displayed");
        return this;
    }

    public Toolbar assertTasksIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(tasksLink), "Tasks is displayed");
        return this;
    }

    public ToolbarTasksMenu clickTasks()
    {
        LOG.info("Click Tasks");
        webElementInteraction.clickElement(tasksLink);
        return new ToolbarTasksMenu(webDriver);
    }

    public Toolbar assertPeopleIsDisplayed()
    {
        LOG.info("Assert People is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(peopleLink), "People is displayed");
        return this;
    }

    public PeopleFinderPage clickPeople()
    {
        LOG.info("Click People");
        webElementInteraction.clickElement(peopleLink);
        return new PeopleFinderPage(webDriver);
    }

    public Toolbar assertRepositoryIsDisplayed()
    {
        LOG.info("Assert Repository is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(repositoryLink), "Repository is displayed");
        return this;
    }

    public RepositoryPage clickRepository()
    {
        LOG.info("Click Repository");
        webElementInteraction.clickElement(repositoryLink);
        return new RepositoryPage(webDriver);
    }

    public Toolbar assertSitesManagerIsDisplayed()
    {
        LOG.info("Assert Sites Manager is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public Toolbar assertSitesManagerIsNotDisplayed()
    {
        LOG.info("Assert Sites Manager is NOT displayed");
        assertFalse(webElementInteraction.isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public SitesManagerPage clickSitesManager()
    {
        LOG.info("Click Sites Manager");
        webElementInteraction.clickElement(sitesManagerLink);
        return new SitesManagerPage(webDriver);
    }

    public Toolbar assertAdminToolsIsDisplayed()
    {
        LOG.info("Assert Admin Tools link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public Toolbar assertAdminToolsIsNotDisplayed()
    {
        LOG.info("Assert Admin Tools link is not displayed");
        assertFalse(webElementInteraction.isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public ApplicationPage clickAdminTools()
    {
        LOG.info("Click Admin Tools");
        webElementInteraction.clickElement(adminToolsLink);
        return new ApplicationPage(webDriver);
    }

    public Toolbar assertUserMenuIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(userMenuLink), "User Menu link is displayed");
        return this;
    }

    public ToolbarUserMenu clickUserMenu()
    {
        LOG.info("Click User menu");
        webElementInteraction.clickElement(userMenuLink);
        return new ToolbarUserMenu(webDriver);
    }

    public String getSearchBoxPlaceholder()
    {
        return webElementInteraction.findElement(searchBoxInput).getAttribute("placeholder");
    }

    public Toolbar assertSearchInputIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchBoxInput), "Search input is displayed");
        return this;
    }

    public Toolbar assertSearchIconIsDisplayed()
    {
        LOG.info("Assert Search icon is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchIcon), "Search icon is displayed");
        return this;
    }

    public boolean isClearSearchBoxDisplayed()
    {
        return webElementInteraction.isElementDisplayed(clearSearchBox);
    }

    public AdvancedSearchPage clickAdvancedSearch()
    {
        LOG.info("Click Advanced Search page");
        webElementInteraction.clickElement(searchIcon);
        webElementInteraction.clickElement(advancedSearchLink);
        return new AdvancedSearchPage(webDriver);
    }

    public void searchInToolbar(String searchTerm)
    {
        webElementInteraction.clearAndType(searchBoxInput, searchTerm);
    }

    public SearchPage search(String searchTerm)
    {
        searchInToolbar(searchTerm);
        webElementInteraction.findElement(searchBoxInput).sendKeys(Keys.RETURN);
        SearchPage searchPage = new SearchPage(webDriver);
        searchPage.waitForPageToLoad();
        return new SearchPage(webDriver);
    }

    public void clickResult(String query)
    {
        List<WebElement> searchResults = webElementInteraction.waitUntilElementsAreVisible(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                result.click();
        }
    }

    public boolean isLiveSearchResultsListDisplayed()
    {
        return  webElementInteraction.findElements(searchResultsInToolbar).isEmpty();
    }

    public boolean isResultDisplayedInLiveSearch(String query)
    {
        int j = 0;
        while (!webElementInteraction.isElementDisplayed(By.xpath("//div[contains(@class, 'alf-live-search')]//div")) && j < 4)
        {
            webElementInteraction.refresh();
            this.searchInToolbar(query);
            j++;
        }
        webElementInteraction.waitUntilElementsAreVisible(By.xpath("//div[contains(@class, 'alf-live-search')]//div"));
        webElementInteraction.waitUntilElementsAreVisible(By.cssSelector("div.alf-livesearch-item>a"));

        List<WebElement> searchResults = webElementInteraction.findElements(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                return true;
        }
        return false;
    }

    public void clickResultFromLiveSearch(String resultToBeClicked)
    {
        List<WebElement> results = webElementInteraction.waitUntilElementsAreVisible(searchResultsInToolbar);
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
        webElementInteraction.clickElement(searchButton);
    }

    public void closeMenu()
    {
        LOG.info("Closing opened menu");
        webElementInteraction.clickElement(bodyLocator);
    }
}