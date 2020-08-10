package org.alfresco.po.share.toolbar;

import java.util.List;

import org.alfresco.common.EnvProperties;
import org.alfresco.common.Language;
import org.alfresco.common.Timeout;
import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.common.Utils;
import org.alfresco.po.share.MyFilesPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.alfrescoContent.RepositoryPage;
import org.alfresco.po.share.alfrescoContent.SharedFilesPage;
import org.alfresco.po.share.searching.AdvancedSearchPage;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.user.admin.SitesManagerPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.testng.Assert;

@Primary
@PageObject
public class Toolbar extends HtmlPage
{
    @Autowired
    protected Language language;

    @RenderWebElement
    @FindBy (id = "SHARE_HEADER")
    protected WebElement toolbar;

    @FindBy (css = "div[widgetid='HEADER_HOME']")
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

    @FindBy (id = "HEADER_USER_MENU_POPUP")
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
    private SearchPage searchPage;

    @Autowired
    private AdminToolsPage adminToolsPage;

    @Autowired
    private SitesManagerPage sitesManagerPage;

    @FindBy (css = "div.alf-livesearch-item>a")
    private List<WebElement> searchResultsInToolbar;

    @FindBy (css = "span.alfresco-buttons-AlfButton.confirmationButton.call-to-action.dijitButton")
    private WebElement searchButton;

    public By MESSAGE_LOCATOR = By.className("div.bd span.message");

    @Autowired
    protected AIMSPage aimsPage;

    @Autowired
    protected LoginPage loginPage;

    @Autowired
    protected EnvProperties envProperties;

    @Autowired
    private ToolbarSitesMenu toolbarSitesMenu;

    @Autowired
    private ToolbarTasksMenu toolbarTasksMenu;

    @Autowired
    private ToolbarUserMenu toolbarUserMenu;

    @Autowired
    private MyFilesPage myFilesPage;

    @Autowired
    private SharedFilesPage sharedFilesPage;

    @Autowired
    private PeopleFinderPage peopleFinderPage;

    @Autowired
    private RepositoryPage repositoryPage;

    @Autowired
    private AdvancedSearchPage advancedSearchPage;

    public Toolbar assertToolbarIsDisplayed()
    {
        LOG.info("Assert toolbar is displayed");
        Assert.assertTrue(browser.isElementDisplayed(toolbar), "Toolbar is displayed");
        return this;
    }

    public ToolbarSitesMenu clickSites()
    {
        LOG.info("Click Sites");
        sitesLink.click();
        return (ToolbarSitesMenu) toolbarSitesMenu.renderedPage();
    }

    public Toolbar assertHomeIsDisplayed()
    {
        LOG.info("Assert Home is displayed");
        Assert.assertTrue(browser.isElementDisplayed(homeLink), "Home is displayed");
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
        Assert.assertTrue(browser.isElementDisplayed(myFilesLink), "My Files is displayed");
        return this;
    }

    public MyFilesPage clickMyFiles()
    {
        LOG.info("Click My Files");
        myFilesLink.click();
        return (MyFilesPage) myFilesPage.renderedPage();
    }

    public Toolbar assertSharedFilesIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        Assert.assertTrue(browser.isElementDisplayed(sharedFilesLink), "Shared Files is displayed");
        return this;
    }

    public SharedFilesPage clickSharedFiles()
    {
        LOG.info("Click Shared Files");
        sharedFilesLink.click();
        return (SharedFilesPage) sharedFilesPage.renderedPage();
    }

    public Toolbar assertSitesIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        Assert.assertTrue(browser.isElementDisplayed(sitesLink), "Sites is displayed");
        return this;
    }

    public Toolbar assertTasksIsDisplayed()
    {
        LOG.info("Assert Shared Files is displayed");
        Assert.assertTrue(browser.isElementDisplayed(tasksLink), "Tasks is displayed");
        return this;
    }

    public ToolbarTasksMenu clickTasks()
    {
        LOG.info("Click Tasks");
        tasksLink.click();
        return (ToolbarTasksMenu) toolbarTasksMenu.renderedPage();
    }

    public Toolbar assertPeopleIsDisplayed()
    {
        LOG.info("Assert People is displayed");
        Assert.assertTrue(browser.isElementDisplayed(peopleLink), "People is displayed");
        return this;
    }

    public PeopleFinderPage clickPeople()
    {
        LOG.info("Click People");
        peopleLink.click();
        return (PeopleFinderPage) peopleFinderPage.renderedPage();
    }

    public Toolbar assertRepositoryIsDisplayed()
    {
        LOG.info("Assert Repository is displayed");
        Assert.assertTrue(browser.isElementDisplayed(repositoryLink), "Repository is displayed");
        return this;
    }

    public RepositoryPage clickRepository()
    {
        LOG.info("Click Repository");
        repositoryLink.click();
        return (RepositoryPage) repositoryPage.renderedPage();
    }

    public boolean isSitesManagerDisplayed()
    {
        return browser.isElementDisplayed(sitesManagerLink);
    }

    public Toolbar assertSitesManagerIsDisplayed()
    {
        LOG.info("Assert Sites Manager is displayed");
        Assert.assertTrue(browser.isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public Toolbar assertSitesManagerIsNotDisplayed()
    {
        LOG.info("Assert Sites Manager is NOT displayed");
        Assert.assertFalse(browser.isElementDisplayed(sitesManagerLink), "Sites Manager link is displayed");
        return this;
    }

    public SitesManagerPage clickSitesManager()
    {
        LOG.info("Click Sites Manager");
        sitesManagerLink.click();
        return (SitesManagerPage) sitesManagerPage.renderedPage();
    }

    public Toolbar assertAdminToolsIsDisplayed()
    {
        LOG.info("Assert Admin Tools link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public Toolbar assertAdminToolsIsNotDisplayed()
    {
        LOG.info("Assert Admin Tools link is not displayed");
        Assert.assertFalse(browser.isElementDisplayed(adminToolsLink), "Admin tools link is displayed");
        return this;
    }

    public AdminToolsPage clickAdminTools()
    {
        LOG.info("Click Admin Tools");
        browser.waitUntilElementVisible(adminToolsLink).click();
        return (AdminToolsPage) adminToolsPage.renderedPage();
    }

    public Toolbar assertUserMenuIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(userMenuLink), "User Menu link is displayed");
        return this;
    }

    public ToolbarUserMenu clickUserMenu()
    {
        LOG.info("Click User menu");
        userMenuLink.click();
        return (ToolbarUserMenu) toolbarUserMenu.renderedPage();
    }

    public String getSearchBoxPlaceholder()
    {
        return searchBoxInput.getAttribute("placeholder");
    }

    public Toolbar assertSearchInputIsDisplayed()
    {
        LOG.info("Assert User Menu link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(searchBoxInput), "Search input is displayed");
        return this;
    }

    public Toolbar assertSearchIconIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchIcon), "Search icon is displayed");
        return this;
    }

    public boolean isClearSearchBoxDisplayed()
    {
        return browser.isElementDisplayed(clearSearchBox);
    }

    public AdvancedSearchPage clickAdvancedSearch()
    {
        LOG.info("Click Advanced Search page");
        searchIcon.click();
        browser.waitUntilElementVisible(advancedSearchLink).click();
        return (AdvancedSearchPage) advancedSearchPage.renderedPage();
    }

    /**
     * Type in toolbar
     */
    public void searchInToolbar(String searchTerm)
    {
        Utils.clearAndType(searchBoxInput, searchTerm);
    }

    /**
     * Type in toolbar and press 'Enter'
     */
    public SearchPage search(String searchTerm)
    {
        searchInToolbar(searchTerm);
        searchBoxInput.sendKeys(Keys.RETURN);
        waitUntilMessageDisappears();
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

    public void waitUntilMessageDisappears()
    {
        try
        {
            getBrowser().waitUntilElementVisible(MESSAGE_LOCATOR, Timeout.SHORT.getTimeoutSeconds());
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
    }
}