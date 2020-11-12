package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.ShareDialog2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AddGroupDialog extends ShareDialog2
{
    @RenderWebElement
    private By dialogTitle = By.cssSelector("span[id*='grouppicker-title']");
    @RenderWebElement
    private By searchInputField = By.cssSelector("div[id*='search-groupfinder'] input");
    private By searchButton = By.cssSelector("div[id*='search-groupfinder'] button[id*='search']");
    private By searchResultsList = By.cssSelector(".itemname");
    private By searchResultsIdList = By.cssSelector(".detail");
    private By addButtonsList = By.cssSelector("td[class*='actions'] button");
    private By closeButton = By.cssSelector("div[id*='default-grouppicker'] a[class='container-close']");

    public AddGroupDialog(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public AddGroupDialog assertAddGroupDialogTitleIsCorrect()
    {
        assertEquals(getBrowser().findElement(dialogTitle).getText(), language.translate("adminTools.groups.addGroupDialog.title"));
        return this;
    }

    public AddGroupDialog assertSearchInputIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    public AddGroupDialog assertSearchButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    private void fillInSearchInput(String textToSearch)
    {
        clearAndType(searchInputField, textToSearch);
    }

    private void clickSearchButton()
    {
        getBrowser().findElement(searchButton).click();
    }

    public AddGroupDialog searchGroup(String groupToSearch)
    {
        fillInSearchInput(groupToSearch);
        clickSearchButton();
        return this;
    }

    private ArrayList<String> getSearchResultsName()
    {
        return getBrowser().waitUntilElementsVisible(searchResultsList)
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
        return getBrowser().waitUntilElementsVisible(searchResultsIdList)
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
        getBrowser().waitUntilElementsVisible(addButtonsList);
        int index = getItemIndexFromSearchResults(groupModel.getDisplayName());
        getBrowser().findElements(addButtonsList).get(index).click();
    }

    public boolean isAddButtonDisplayed(String searchResult)
    {
        int index = getItemIndexFromSearchResults(searchResult);
        return getBrowser().isElementDisplayed(getBrowser().findElements(addButtonsList).get(index));
    }

    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeButton);
    }
}
