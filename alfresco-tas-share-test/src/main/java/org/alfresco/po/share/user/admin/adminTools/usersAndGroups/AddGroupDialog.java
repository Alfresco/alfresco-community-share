package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alfresco.po.share.ShareDialog;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

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

    @RenderWebElement
    @FindBy (css = "div[id*='default-grouppicker'] a[class='container-close']")
    private WebElement closeButton;

    /**
     * @return 'Add Group' dialog's title
     */
    public String getDialogTitle()
    {
        return dialogTitle.getText();
    }

    public AddGroupDialog assertAddGroupDialogTitleIsCorrect()
    {
        Assert.assertEquals(dialogTitle.getText(), language.translate("adminTools.groups.addGroupDialog.title"));
        return this;
    }

    public AddGroupDialog assertSearchInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    public AddGroupDialog assertSearchButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
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
    public AddGroupDialog searchGroup(String groupToSearch)
    {
        fillInSearchInput(groupToSearch);
        clickSearchButton();
        return this;
    }

    /**
     * @return list of search results
     */
    private ArrayList<String> getSearchResultsName()
    {
        browser.waitUntilElementsVisible(searchResultsList);
        return searchResultsList.stream().map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
    }

    public AddGroupDialog assertGroupDisplayNameIsFound(GroupModel groupModel)
    {
        Assert.assertTrue(getSearchResultsName().contains(groupModel.getDisplayName()),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }
    
    public AddGroupDialog assertGroupIdIsFound(GroupModel groupModel)
    {
        Assert.assertTrue(getSearchResultsId().contains(String.format("ID: GROUP_%s", groupModel.getGroupIdentifier())),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    /**
     * @return list of search results
     */
    public ArrayList<String> getSearchResultsId()
    {
        browser.waitUntilElementsVisible(searchResultsIdList);
        return searchResultsIdList.stream().map(WebElement::getText).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @param searchResult name of the item from search results list
     * @return position of searchResult in list. -1 if searchResult isn't displayed
     */
    public int getItemIndexFromSearchResults(String searchResult)
    {
        return  getSearchResultsName().indexOf(searchResult);
    }

    /**
     * Click 'Add' button for a search result item
     *
     * @param groupModel to be added
     */
    public void addGroup(GroupModel groupModel)
    {
        getBrowser().waitUntilElementsVisible(addButtonsList);
        int index = getItemIndexFromSearchResults(groupModel.getDisplayName());
        addButtonsList.get(index).click();
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
