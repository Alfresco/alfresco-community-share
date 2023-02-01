package org.alfresco.po.share;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PeopleFinderPage extends SharePage2<PeopleFinderPage> implements AccessibleByMenuBar
{
    private final By searchResultsList = By.cssSelector("tbody[class='yui-dt-data'] tr");
    private final By pageTitle = By.xpath("//h1[@class = 'alfresco-header-Title']");

    private final By notification = By.cssSelector("div.bd span.message");
    private final By peopleFinderPage = By.cssSelector("span[id='HEADER_PEOPLE_text']");


    private final By searchInputField = By.cssSelector("input[id$='default-search-text']");
    private final By searchButton = By.cssSelector("button[id$='default-search-button-button']");
    private final By searchHelpMessage = By.cssSelector("[id*='default-help']");
    private final By searchResultsInfo = By.cssSelector("[id*='default-results-info']");
    private final By noResults = By.cssSelector(".yui-dt-empty");
    private final By avatar = By.cssSelector(".avatar");

    public PeopleFinderPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/people-finder";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public PeopleFinderPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickPeople();
    }

    public PeopleFinderPage assertPeopleFinderPageIsOpened()
    {
        assertTrue(getCurrentUrl().contains(getRelativePath()), "People finder page is opened");
        return this;
    }

    public void typeSearchInput(String searchInput)
    {
        clearAndType(findElement(searchInputField), searchInput);
    }

    public void clickSearch()
    {
        clickElement(searchButton);
    }
    public PeopleFinderPage navigateToPeopleFinderPage()
    {
        clickElement(peopleFinderPage);
        return this;
    }

    public PeopleFinderPage getNotification()
    {
        findElement(notification).getText();
        return this;
    }

    public String getPageTitle()
    {
        return getElementText(pageTitle);
    }

    public void clickSearchAndWaitForResults()
    {
        clickElement(searchButton);
        waitInSeconds(3);
    }

    public void search(String searchInput)
    {
        typeSearchInput(searchInput);
        clickSearchAndWaitForResults();
    }

    public String getNoResultsText()
    {
        return getElementText(noResults);
    }

    public boolean isSearchButtonDisplayed()
    {
        return isElementDisplayed(searchButton);
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return isElementDisplayed(searchInputField);
    }

    public String getSearchInputFieldValue()
    {
        return findElement(searchInputField).getAttribute("value").trim();
    }

    public String getSearchInputFieldPlaceholder()
    {
        return findElement(searchInputField).getAttribute("placeholder").trim();
    }

    public boolean isHelpMessageDisplayed()
    {
        return isElementDisplayed(searchHelpMessage);
    }

    public String getSearchHelpMessage()
    {
        return getElementText(searchHelpMessage);
    }

    public String getSearchResultsInfo()
    {
        return getElementText(searchResultsInfo);
    }

    public WebElement selectUser(String username)
    {
        return findFirstElementWithValue(searchResultsList, username);
    }

    public boolean isUserDisplayed(String username)
    {
        return selectUser(username) != null;
    }

    public boolean isUserAvatarDisplayed(String username)
    {
        return isElementDisplayed(selectUser(username).findElement(avatar));
    }

    public UserProfilePage clickUserLink(String username)
    {
        selectUser(username).findElement(By.cssSelector("a")).click();
        return new UserProfilePage(webDriver);
    }

    public boolean isFollowButtonDisplayed(String username)
    {
        return isElementDisplayed(selectUser(username).findElement(By.cssSelector("button")));
    }

    public int getNumberOfSearchResults()
    {
        return findElements(searchResultsList).size();
    }
}
