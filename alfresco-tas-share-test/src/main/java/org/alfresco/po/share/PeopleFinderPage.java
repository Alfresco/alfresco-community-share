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
        assertTrue(webElementInteraction.getCurrentUrl().contains(getRelativePath()), "People finder page is opened");
        return this;
    }

    public void typeSearchInput(String searchInput)
    {
        webElementInteraction.clearAndType(webElementInteraction.findElement(searchInputField), searchInput);
    }

    public void clickSearch()
    {
        webElementInteraction.clickElement(searchButton);
    }

    public void clickSearchAndWaitForResults()
    {
        webElementInteraction.clickElement(searchButton); //TODO redo method search and wait for results
    }

    public void search(String searchInput)
    {
        typeSearchInput(searchInput);
        clickSearchAndWaitForResults();
    }

    public String getNoResultsText()
    {
        return webElementInteraction.findElement(noResults).getText();
    }

    public boolean isSearchButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchButton);
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchInputField);
    }

    public String getSearchInputFieldValue()
    {
        return webElementInteraction.findElement(searchInputField).getAttribute("value").trim();
    }

    public String getSearchInputFieldPlaceholder()
    {
        return webElementInteraction.findElement(searchInputField).getAttribute("placeholder").trim();
    }

    public boolean isHelpMessageDisplayed()
    {
        return webElementInteraction.isElementDisplayed(searchHelpMessage);
    }

    public String getSearchHelpMessage()
    {
        return webElementInteraction.findElement(searchHelpMessage).getText();
    }

    public String getSearchResultsInfo()
    {
        return webElementInteraction.findElement(searchResultsInfo).getText();
    }

    public WebElement selectUser(String username)
    {
        return webElementInteraction.findFirstElementWithValue(searchResultsList, username);
    }

    public boolean isUserDisplayed(String username)
    {
        return selectUser(username) != null;
    }

    public boolean isUserAvatarDisplayed(String username)
    {
        return webElementInteraction.isElementDisplayed(selectUser(username).findElement(avatar));
    }

    public UserProfilePage clickUserLink(String username)
    {
        selectUser(username).findElement(By.cssSelector("a"));
        return new UserProfilePage(webDriver);
    }

    public boolean isFollowButtonDisplayed(String username)
    {
        return webElementInteraction.isElementDisplayed(selectUser(username).findElement(By.cssSelector("button")));
    }

    public int getNumberOfSearchResults()
    {
        return webElementInteraction.findElements(searchResultsList).size();
    }
}
