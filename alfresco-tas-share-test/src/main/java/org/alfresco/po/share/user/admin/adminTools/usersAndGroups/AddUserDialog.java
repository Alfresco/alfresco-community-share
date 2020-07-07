package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import static org.alfresco.common.Utils.retryUntil;

/**
 * @author Laura.Capsa
 */
@PageObject
public class AddUserDialog extends ShareDialog
{
    protected String searchedUsername = "a[href$='%s/profile']";

    @RenderWebElement
    @FindBy (css = "span[id*='peoplepicker']")
    private WebElement dialogTitle;

    @RenderWebElement
    @FindBy (css = "div[id*='search-peoplefinder'] input")
    private WebElement searchInputField;

    @RenderWebElement
    @FindBy (css = "div[id*='search-peoplefinder'] button[id*='search']")
    private WebElement searchButton;

    @FindAll (@FindBy (css = ".itemname"))
    private List<WebElement> searchResultsList;

    @FindAll (@FindBy (css = "td[class*='actions'] button"))
    private List<WebElement> addButtonsList;
    @RenderWebElement
    @FindBy (css = "div[id*='default-peoplepicker'] a[class='container-close']")
    private WebElement closeButton;

    /**
     * @return 'Add User' dialog's title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public boolean isSearchInputFieldDisplayed()
    {
        return browser.isElementDisplayed(searchInputField);
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchButton);
    }

    private void fillInSearchInput(String textToSearch)
    {
        searchInputField.clear();
        searchInputField.sendKeys(textToSearch);
    }

    public void clickSearchButton()
    {
        searchButton.click();
    }

    /**
     * Fill in search input field with a user name and click 'Search' button
     *
     * @param userToSearch typed in search input field
     */
    public void searchUser(String userToSearch)
    {
        int retry = 0;
        typeAndSearch(userToSearch);
        boolean found = isUserDisplayed(userToSearch);
        while (retry < 10 && !found)
        {
            typeAndSearch(userToSearch);
            found = isUserDisplayed(userToSearch);
            retry++;
            Utility.waitToLoopTime(1, String.format("Waiting for user %s to be found.", userToSearch));
        }
    }

    private AddUserDialog typeAndSearch(String userToSearch)
    {
        fillInSearchInput(userToSearch);
        clickSearchButton();
        return (AddUserDialog) this.renderedPage();
    }

    /**
     * @return list of search results
     */
    public ArrayList<String> getSearchResultsName()
    {
        ArrayList<String> searchResults = new ArrayList<>();

        for (WebElement result : searchResultsList)
            searchResults.add(result.getText());

        return searchResults;
    }

    /**
     * @param searchResult name of the item from search results list
     * @return position of searchResult in list. -1 if searchResult isn't displayed
     */
    public int getItemIndexFromSearchResults(String searchResult)
    {
        ArrayList<String> searchResultsList = getSearchResultsName();
        return searchResultsList.indexOf(searchResult);
    }

    /**
     * Click 'Add' button for a search result item
     *
     * @param searchResult to be added
     */
    public void clickAddButtonForUser(String searchResult)
    {
        int index = getItemIndexFromSearchResults(searchResult);
        addButtonsList.get(index).click();
    }

    /**
     * Checking if user is displayed in 'Add User' popup list
     *
     * @param username - the username surrounded by parentheses
     * @return true if user is displayed, else false
     */
    public boolean isUserDisplayed(String username)
    {
        return browser.isElementDisplayed(By.cssSelector(String.format(searchedUsername, username)));
    }

    public boolean isAddButtonDisplayed(String searchResult)
    {
        int index = getItemIndexFromSearchResults(searchResult);
        return browser.isElementDisplayed(addButtonsList.get(index));
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }
}
