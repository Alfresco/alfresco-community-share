package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public AddUserDialog assertAddUserDialogTitleIsCorrect()
    {
        Assert.assertEquals(dialogTitle.getText(), language.translate("adminTools.groups.addUserDialog.title"));
        return this;
    }

    public AddUserDialog assertSearchButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public AddUserDialog assertSearchInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
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
    public AddUserDialog searchUser(String userToSearch)
    {
        int retry = 0;
        typeAndSearch(userToSearch);
        boolean found = isUserDisplayed(userToSearch);
        while (retry < 15 && !found)
        {
            typeAndSearch(userToSearch);
            found = isUserDisplayed(userToSearch);
            retry++;
            Utility.waitToLoopTime(1, String.format("Waiting for user %s to be found.", userToSearch));
        }
        return this;
    }

    public AddUserDialog searchUser(UserModel userModel)
    {
        return searchUser(userModel.getUsername());
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
        return searchResultsList.stream()
            .map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public AddUserDialog assertUserIsFound(UserModel userModel)
    {
        Assert.assertTrue(getSearchResultsName().contains(getUserFormat(userModel)));
        return this;
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

    public void addUser(UserModel userModel)
    {
        clickAddButtonForUser(String.format("%s %s (%s)",
            userModel.getFirstName(), userModel.getLastName(), userModel.getUsername()));
        waitUntilMessageDisappears();
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

    public AddUserDialog assertAddButtonIsDisplayedForUser(UserModel user)
    {
        int index = getItemIndexFromSearchResults(getUserFormat(user));
        Assert.assertTrue(browser.isElementDisplayed(addButtonsList.get(index)));
        return this;
    }

    public boolean isCloseButtonDisplayed()
    {
        return browser.isElementDisplayed(closeButton);
    }

    private String getUserFormat(UserModel userModel)
    {
        return String.format("%s %s (%s)", userModel.getFirstName(), userModel.getLastName(), userModel.getUsername());
    }
}
