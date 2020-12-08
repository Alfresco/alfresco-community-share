package org.alfresco.po.share;

import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertTrue;

public class PeopleFinderPage extends SharePage2<PeopleFinderPage> implements AccessibleByMenuBar
{
    private final By searchResultsList = By.cssSelector("tbody[class='yui-dt-data'] tr");

    @RenderWebElement
    private final By searchInputField = By.cssSelector("input[id$='default-search-text']");
    private final By searchButton = By.cssSelector("button[id$='default-search-button-button']");
    private final By searchHelpMessage = By.cssSelector("[id*='default-help']");
    private final By searchResultsInfo = By.cssSelector("[id*='default-results-info']");
    private final By noResults = By.cssSelector(".yui-dt-empty");
    private final By avatar = By.cssSelector(".avatar");

    public PeopleFinderPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
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
        return (PeopleFinderPage) new Toolbar(browser).clickPeople().renderedPage();
    }

    public PeopleFinderPage assertPeopleFinderPageIsOpened()
    {
        assertTrue(getBrowser().getCurrentUrl().contains(getRelativePath()), "People finder page is opened");
        return this;
    }

    public void typeSearchInput(String searchInput)
    {
        clearAndType(getBrowser().findElement(searchInputField), searchInput);
    }

    public void clickSearch()
    {
        getBrowser().waitUntilElementClickable(searchButton).click();
    }

    public void clickSearchAndWaitForResults()
    {
        getBrowser().waitUntilElementClickable(searchButton).click(); //TODO redo method search and wait for results
        /*int i = 0;

        while ((searchResultsList.size() == 0 || browser.isElementDisplayed(noResults.getWrappedElement())) && i < 5)
        {
            searchButton.click();
            browser.waitInSeconds(5);
            i++;
        }*/
    }

    public void search(String searchInput)
    {
        typeSearchInput(searchInput);
        clickSearchAndWaitForResults();
    }

    public String getNoResultsText()
    {
        return getBrowser().findElement(noResults).getText();
    }

    public boolean isSearchButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(searchButton);
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return getBrowser().isElementDisplayed(searchInputField);
    }

    public String getSearchInputFieldValue()
    {
        return getBrowser().findElement(searchInputField).getAttribute("value").trim();
    }

    public String getSearchInputFieldPlaceholder()
    {
        return getBrowser().findElement(searchInputField).getAttribute("placeholder").trim();
    }

    public boolean isHelpMessageDisplayed()
    {
        return getBrowser().isElementDisplayed(searchHelpMessage);
    }

    public String getSearchHelpMessage()
    {
        return getBrowser().findElement(searchHelpMessage).getText();
    }

    public String getSearchResultsInfo()
    {
        return getBrowser().findElement(searchResultsInfo).getText();
    }

    public WebElement selectUser(String username)
    {
        return getBrowser().findFirstElementWithValue(searchResultsList, username);
    }

    public boolean isUserDisplayed(String username)
    {
        return selectUser(username) != null;
    }

    public boolean isUserAvatarDisplayed(String username)
    {
        return getBrowser().isElementDisplayed(selectUser(username).findElement(avatar));
    }

    public UserProfilePage clickUserLink(String username)
    {
        selectUser(username).findElement(By.cssSelector("a")).click();
        return (UserProfilePage) new UserProfilePage(browser).renderedPage();
    }

    public boolean isFollowButtonDisplayed(String username)
    {
        return getBrowser().isElementDisplayed(selectUser(username).findElement(By.cssSelector("button")));
    }

    public int getNumberOfSearchResults()
    {
        return getBrowser().findElements(searchResultsList).size();
    }
}
