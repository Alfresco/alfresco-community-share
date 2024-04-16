package org.alfresco.po.share.toolbar;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.Utils;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.ApplicationPage;
import org.openqa.selenium.*;

@Slf4j
public class Toolbar extends BasePage
{
    private final By toolbarLocator = By.cssSelector("div[id='SHARE_HEADER']");
    private final By homeLink = By.cssSelector("div[widgetid='HEADER_HOME']");
    private final By userLink = By.xpath("//a[@title=\"User Management\"]");
    private final By newUserLink = By.xpath("//button[text()=\"New User\"]");

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
        log.info("Assert toolbar is displayed");
        assertTrue(isElementDisplayed(toolbarLocator), "Toolbar is displayed");
        return this;
    }
    public SearchPage searchAndEnterSearch(String searchTerm)
    {
        searchInToolbar(searchTerm);
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        waitInSeconds(5);
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        waitInSeconds(5);
        refresh();
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        waitInSeconds(5);
        refresh();
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        waitInSeconds(3);
        refresh();
        return new SearchPage(webDriver);

    }

    public ToolbarSitesMenu clickSites()
    {
        log.info("Click Sites");
        clickElement(sitesLink);
        ToolbarSitesMenu toolbarSitesMenu = new ToolbarSitesMenu(webDriver);
        toolbarSitesMenu.waitForMySitesMenuToBeOpened();
        return toolbarSitesMenu;
    }
    public ApplicationPage users()
    {
        log.info("Click Admin Tools");
        clickElement(userLink);
        return new ApplicationPage(webDriver);
    }
    public ApplicationPage newUsers()
    {
        log.info("Click Admin Tools");
        clickElement(newUserLink);
        return new ApplicationPage(webDriver);
    }

    public Toolbar assertHomeIsDisplayed()
    {
        log.info("Assert Home is displayed");
        assertTrue(isElementDisplayed(homeLink), "Home is displayed");
        return this;
    }

    public void clickHome()
    {
        clickElement(homeLink);
    }

    public Toolbar assertMyFilesIsDisplayed()
    {
        log.info("Assert My Files is displayed");
        assertTrue(isElementDisplayed(myFilesLink), "My Files is displayed");
        return this;
    }

    public MyFilesPage clickMyFiles()
    {
        log.info("Click My Files");
        clickElement(myFilesLink);
        return new MyFilesPage(webDriver);
    }

    public SharedFilesPage clickSharedFiles()
    {
        log.info("Click Shared Files");
        clickElement(sharedFilesLink);
        return new SharedFilesPage(webDriver);
    }

    public Toolbar assertSitesIsDisplayed()
    {
        log.info("Assert Shared Files is displayed");
        assertTrue(isElementDisplayed(sitesLink), "Sites is displayed");
        return this;
    }

    public Toolbar assertTasksIsDisplayed()
    {
        log.info("Assert Shared Files is displayed");
        assertTrue(isElementDisplayed(tasksLink), "Tasks is displayed");
        return this;
    }

    public ToolbarTasksMenu clickTasks()
    {
        log.info("Click Tasks");
        waitInSeconds(3);
        clickElement(tasksLink);
        return new ToolbarTasksMenu(webDriver);
    }

    public Toolbar assertPeopleIsDisplayed()
    {
        log.info("Assert People is displayed");
        assertTrue(isElementDisplayed(peopleLink), "People is displayed");
        return this;
    }

    public PeopleFinderPage clickPeople()
    {
        log.info("Click People");
        clickElement(peopleLink);
        return new PeopleFinderPage(webDriver);
    }

    public Toolbar assertRepositoryIsDisplayed()
    {
        log.info("Assert Repository is displayed");
        assertTrue(isElementDisplayed(repositoryLink), "Repository is displayed");
        return this;
    }

    public RepositoryPage clickRepository()
    {
        log.info("Click Repository");
        clickElement(repositoryLink);
        return new RepositoryPage(webDriver);
    }

    public Toolbar assertSitesManagerIsDisplayed()
    {
        log.info("Assert Sites Manager is displayed");
        assertTrue(isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public Toolbar assertSitesManagerIsNotDisplayed()
    {
        log.info("Assert Sites Manager is NOT displayed");
        assertFalse(isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public SitesManagerPage clickSitesManager()
    {
        log.info("Click Sites Manager");
        clickElement(sitesManagerLink);
        return new SitesManagerPage(webDriver);
    }

    public Toolbar assertAdminToolsIsDisplayed()
    {
        log.info("Assert Admin Tools link is displayed");
        assertTrue(isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public Toolbar assertAdminToolsIsNotDisplayed()
    {
        log.info("Assert Admin Tools link is not displayed");
        assertFalse(isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public ApplicationPage clickAdminTools()
    {
        log.info("Click Admin Tools");
        clickElement(adminToolsLink);
        return new ApplicationPage(webDriver);
    }

    public Toolbar assertUserMenuIsDisplayed()
    {
        log.info("Assert User Menu link is displayed");
        assertTrue(isElementDisplayed(userMenuLink), "User Menu link is displayed");
        return this;
    }

    public ToolbarUserMenu clickUserMenu()
    {
        log.info("Click User menu");
        clickElement(userMenuLink);
        return new ToolbarUserMenu(webDriver);
    }

    public String getSearchBoxPlaceholder()
    {
        return findElement(searchBoxInput).getAttribute("placeholder");
    }

    public Toolbar assertSearchInputIsDisplayed()
    {
        log.info("Assert User Menu link is displayed");
        assertTrue(isElementDisplayed(searchBoxInput), "Search input is displayed");
        return this;
    }

    public Toolbar assertSearchIconIsDisplayed()
    {
        log.info("Assert Search icon is displayed");
        assertTrue(isElementDisplayed(searchIcon), "Search icon is displayed");
        return this;
    }

    public boolean isClearSearchBoxDisplayed()
    {
        return isElementDisplayed(clearSearchBox);
    }

    public AdvancedSearchPage clickAdvancedSearch()
    {
        log.info("Click Advanced Search page");
        clickElement(searchIcon);
        clickElement(advancedSearchLink);
        return new AdvancedSearchPage(webDriver);
    }

    public void searchInToolbar(String searchTerm)
    {
        waitUntilElementIsVisible(searchBoxInput);
        try
        {
            clearAndType(searchBoxInput, searchTerm);
        }
        catch (ElementNotInteractableException e)
        {
            log.error("Failed to type in search input");
            refresh();
            clearAndType(searchBoxInput, searchTerm);
        }
    }

    public SearchPage search(String searchTerm)
    {
        searchInToolbar(searchTerm);
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        SearchPage searchPage = new SearchPage(webDriver);
        searchPage.waitForPageToLoad();
        return new SearchPage(webDriver);
    }
    public SearchPage searchAndEnterAgain(String searchTerm)
    {
        searchInToolbar(searchTerm);
        findElement(searchBoxInput).sendKeys(Keys.RETURN);
        SearchPage searchPage = new SearchPage(webDriver);
        searchPage.waitForPageToLoad();
        refresh();
        searchPage.clickSearchButtonAgain();
        return new SearchPage(webDriver);
    }

    public void clickResult(String query)
    {
        List<WebElement> searchResults = waitUntilElementsAreVisible(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                result.click();
        }
    }

    public boolean isLiveSearchResultsListDisplayed()
    {
        return findElements(searchResultsInToolbar).isEmpty();
    }

    public boolean is_LiveSearchResultsListDisplayed(String query)
    {
        waitInSeconds(3);
        List<WebElement> searchResults = findElements(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                return true;
        }
        return false;
    }

    public boolean isResultDisplayedInLiveSearch(String query)
    {
        int retryCounter = 0;
        while (!isElementDisplayed(By.xpath("//div[contains(@class, 'alf-live-search')]//div")) && retryCounter < 4)
        {
            waitInSeconds(2);
            refresh();
            searchInToolbar(query);
            retryCounter++;
        }
        waitUntilElementsAreVisible(By.xpath("//div[contains(@class, 'alf-live-search')]//div"));
        waitUntilElementsAreVisible(By.cssSelector("div.alf-livesearch-item>a"));

        waitInSeconds(2);
        List<WebElement> searchResults = findElements(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                return true;
        }
        return false;
    }
    public boolean isResultDisplayedLiveSearch(String query)
    {
        int retryCounter = 0;
        while (!isElementDisplayed(By.xpath("//div[contains(@class, 'alf-live-search')]//div")) && retryCounter < 8)
        {
            waitInSeconds(2);
            refresh();
            searchInToolbar(query);
            retryCounter++;
        }
        waitUntilElementsAreVisible(By.xpath("//div[contains(@class, 'alf-live-search')]//div"));
        waitUntilElementsAreVisible(By.cssSelector("div.alf-livesearch-item>a"));

        waitInSeconds(2);
        List<WebElement> searchResults = findElements(searchResultsInToolbar);
        for (WebElement result : searchResults)
        {
            if (result.getText().contains(query))
                return true;
        }
        return false;
    }

    public void clickResultFromLiveSearch(String resultToBeClicked)
    {
        List<WebElement> results = waitUntilElementsAreVisible(searchResultsInToolbar);
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
        clickElement(searchButton);
    }
    public void SearchAndEnter(String search)
    {
        waitInSeconds(3);
        Utils.clearAndType(waitUntilElementIsVisible(findElement(searchBoxInput)), search);
        findElement(searchBoxInput).sendKeys(Keys.ENTER);
        waitInSeconds(10);
        refresh();
        waitInSeconds(5);
        refresh();
        waitInSeconds(5);
        refresh();
        waitInSeconds(5);
        refresh();
        waitInSeconds(5);
        refresh();
    }

    public void closeMenu()
    {
        log.info("Closing opened menu");
        clickElement(bodyLocator);
    }
}