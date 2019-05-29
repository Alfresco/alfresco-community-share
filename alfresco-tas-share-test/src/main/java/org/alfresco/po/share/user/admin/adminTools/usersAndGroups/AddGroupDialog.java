package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laura.Capsa
 */
@PageObject
public class AddGroupDialog extends ShareDialog
{
    @RenderWebElement
    @FindBy (css = "span[id*='grouppicker-title']")
    private WebElement dialogTitle;

    @RenderWebElement
    @FindBy (css = "div[id*='search-groupfinder'] input")
    private WebElement searchInputField;

    @RenderWebElement
    @FindBy (css = "div[id*='search-groupfinder'] button[id*='search']")
    private WebElement searchButton;

    @FindAll (@FindBy (css = ".itemname"))
    private List<WebElement> searchResultsList;

    @FindAll (@FindBy (css = ".detail"))
    private List<WebElement> searchResultsIdList;

    @FindAll (@FindBy (css = "td[class*='actions'] button"))
    private List<WebElement> addButtonsList;

    /**
     * @return 'Add Group' dialog's title
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

    private void clickSearchButton()
    {
        searchButton.click();
    }

    /**
     * Fill in search input field with a group name and click 'Search' button
     *
     * @param groupToSearch typed in search input field
     */
    public void searchGroup(String groupToSearch)
    {
        fillInSearchInput(groupToSearch);
        clickSearchButton();
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
     * @return list of search results
     */
    public ArrayList<String> getSearchResultsId()
    {
        ArrayList<String> searchResults = new ArrayList<>();

        for (WebElement result : searchResultsIdList)
            searchResults.add(result.getText());

        return searchResults;
    }

    /**
     * @param searchResult name of the item from search results list
     * @return position of searchResult in list. -1 if searchResult isn't displayed
     */
    private int getItemIndexFromSearchResults(String searchResult)
    {
        ArrayList<String> searchResultsList = getSearchResultsName();
        return searchResultsList.indexOf(searchResult);
    }

    /**
     * Click 'Add' button for a search result item
     *
     * @param searchResult to be added
     */
    public void clickAddButtonForGroup(String searchResult)
    {
        getBrowser().waitInSeconds(8);
        getBrowser().waitUntilElementsVisible(addButtonsList);
        int index = getItemIndexFromSearchResults(searchResult);
        addButtonsList.get(index).click();
    }
}
