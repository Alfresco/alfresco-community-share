package org.alfresco.po.share;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextBlock;
import ru.yandex.qatools.htmlelements.element.TextInput;

@PageObject
public class PeopleFinderPage extends SharePage<PeopleFinderPage> implements AccessibleByMenuBar
{
    @FindAll (@FindBy (css = "tbody[class='yui-dt-data'] tr"))
    protected List<WebElement> searchResultsList;

    @Autowired
    UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy (css = "input[id$='default-search-text']")
    private TextInput searchInputField;

    @FindBy (css = "button[id$='default-search-button-button']")
    private Button searchButton;

    @FindBy (css = "[id*='default-help']")
    private TextBlock searchHelpMessage;

    @FindBy (css = "[id*='default-results-info']")
    private TextBlock searchResultsInfo;

    @FindBy (css = ".yui-dt-empty")
    private TextBlock noResults;

    private By avatar = By.cssSelector(".avatar");

    @Override
    public String getRelativePath()
    {
        return "share/page/people-finder";
    }

    @SuppressWarnings ("unchecked")
    @Override
    public PeopleFinderPage navigateByMenuBar()
    {
        return toolbar.clickPeople();
    }

    public PeopleFinderPage assertPeopleFinderPageIsOpened()
    {
        Assert.assertTrue(browser.getCurrentUrl().contains(getRelativePath()), "People finder page is opened");
        return this;
    }

    /**
     * Type searchInput
     *
     * @param searchInput
     */
    public void typeSearchInput(String searchInput)
    {
        Utils.clearAndType(searchInputField, searchInput);
    }

    /**
     * Click Search button
     */
    public void clickSearch()
    {
        searchButton.click();
    }

    /**
     * Click Search button
     */
    public void clickSearchAndWaitForResults()
    {
        browser.waitUntilElementClickable(searchButton.getWrappedElement(), properties.getImplicitWait());
        searchButton.click();
        browser.waitInSeconds(5);
        int i = 0;
        while ((searchResultsList.size() == 0 || browser.isElementDisplayed(noResults.getWrappedElement())) && i < 5)
        {
            searchButton.click();
            browser.waitInSeconds(5);
            i++;
        }
    }

    /**
     * Search for people
     *
     * @param searchInput
     */
    public void search(String searchInput)
    {
        typeSearchInput(searchInput);
        clickSearchAndWaitForResults();
    }

    /**
     * Get the message when no results are found
     *
     * @return String message
     */
    public String getNoResultsText()
    {
        return noResults.getText();
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton.getWrappedElement());
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return browser.isElementDisplayed(searchInputField.getWrappedElement());
    }

    public String getSearchInputFieldValue()
    {
        return searchInputField.getWrappedElement().getAttribute("value").trim();
    }

    public String getSearchInputFieldPlaceholder()
    {
        return searchInputField.getWrappedElement().getAttribute("placeholder").trim();
    }

    public boolean isHelpMessageDisplayed()
    {
        return browser.isElementDisplayed(searchHelpMessage.getWrappedElement());
    }

    public String getSearchHelpMessage()
    {
        return searchHelpMessage.getText();
    }

    public String getSearchResultsInfo()
    {
        return searchResultsInfo.getText();
    }

    public WebElement selectUser(String username)
    {
        return browser.findFirstElementWithValue(searchResultsList, username);
    }

    public boolean isUserDisplayed(String username)
    {
        return selectUser(username) != null;
    }

    public boolean isUserAvatarDisplayed(String username)
    {
        return browser.isElementDisplayed(selectUser(username).findElement(avatar));
    }

    public UserProfilePage clickUserLink(String username)
    {
        selectUser(username).findElement(By.cssSelector("a")).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public boolean isFollowButtonDisplayed(String username)
    {
        return browser.isElementDisplayed(selectUser(username).findElement(By.cssSelector("button")));
    }

    public int getNumberOfSearchResults()
    {
        return searchResultsList.size();
    }
}
