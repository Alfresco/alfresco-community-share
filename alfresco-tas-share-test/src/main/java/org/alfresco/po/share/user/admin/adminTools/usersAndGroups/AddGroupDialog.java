package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.GroupModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddGroupDialog extends BaseDialogComponent
{
    private final By dialogTitle = By.cssSelector("span[id*='grouppicker-title']");
    private final By searchInputField = By.cssSelector("div[id*='search-groupfinder'] input");
    private final By searchButton = By.cssSelector("div[id*='search-groupfinder'] button[id*='search']");
    private final By searchResultsList = By.cssSelector(".itemname");
    private final By searchResultsIdList = By.cssSelector(".detail");
    private final By addButtonsList = By.cssSelector("td[class*='actions'] button");
    private final By closeButton = By.cssSelector("div[id*='default-grouppicker'] a[class='container-close']");

    public AddGroupDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public AddGroupDialog assertAddGroupDialogTitleIsCorrect()
    {
        assertEquals(findElement(dialogTitle).getText(), language.translate("adminTools.groups.addGroupDialog.title"));
        return this;
    }

    public AddGroupDialog assertSearchInputIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    public AddGroupDialog assertSearchButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    private void fillInSearchInput(String textToSearch)
    {
        clearAndType(searchInputField, textToSearch);
    }

    private void clickSearchButton()
    {
        clickElement(searchButton);
    }

    public AddGroupDialog searchGroup(String groupToSearch)
    {
        fillInSearchInput(groupToSearch);
        clickSearchButton();
        return this;
    }

    private ArrayList<String> getSearchResultsName()
    {
        return waitUntilElementsAreVisible(searchResultsList)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public AddGroupDialog assertGroupDisplayNameIsFound(GroupModel groupModel)
    {
        assertTrue(getSearchResultsName().contains(groupModel.getDisplayName()),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }
    
    public AddGroupDialog assertGroupIdIsFound(GroupModel groupModel)
    {
        assertTrue(getSearchResultsId().contains(String.format("ID: GROUP_%s", groupModel.getGroupIdentifier())),
            String.format("Group %s was found", groupModel.getDisplayName()));
        return this;
    }

    public ArrayList<String> getSearchResultsId()
    {
        return waitUntilElementsAreVisible(searchResultsIdList)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getItemIndexFromSearchResults(String searchResult)
    {
        return getSearchResultsName().indexOf(searchResult);
    }

    public void addGroup(GroupModel groupModel)
    {
        waitUntilElementsAreVisible(addButtonsList);
        int index = getItemIndexFromSearchResults(groupModel.getDisplayName());
        WebElement button = findElements(addButtonsList).get(index);
        mouseOver(button);
        clickElement(button);
    }

    public boolean isCloseButtonDisplayed()
    {
        return isElementDisplayed(closeButton);
    }
}
