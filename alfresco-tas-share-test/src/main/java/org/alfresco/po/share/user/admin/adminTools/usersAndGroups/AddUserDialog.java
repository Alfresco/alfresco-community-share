package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_40;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddUserDialog extends BaseDialogComponent
{
    protected String searchedUsername = "a[href$='%s/profile']";

    private final By dialogTitle = By.cssSelector("span[id*='peoplepicker']");
    private final By searchInputField = By.cssSelector("div[id*='search-peoplefinder'] input");
    private final By searchButton = By.cssSelector("div[id*='search-peoplefinder'] button[id*='search']");
    private final By searchResultsList = By.cssSelector(".itemname");
    private final By addButtonsList = By.cssSelector("td[class*='actions'] button");
    private final By closeButton = By.cssSelector("div[id*='default-peoplepicker'] a[class='container-close']");
    private final By searchingStatus = By.cssSelector("div[id$='search-peoplefinder-results'] .yui-dt-empty > div");

    public AddUserDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public AddUserDialog assertAddUserDialogTitleIsCorrect()
    {
        assertEquals(webElementInteraction.findElement(dialogTitle).getText(), language.translate("adminTools.groups.addUserDialog.title"));
        return this;
    }

    public AddUserDialog assertSearchButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public AddUserDialog assertSearchInputIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    private void fillInSearchInput(String textToSearch)
    {
        webElementInteraction.clearAndType(searchInputField, textToSearch);
    }

    public void clickSearchButton()
    {
        try
        {
            WebElement search = webElementInteraction.waitUntilElementIsVisible(searchButton);
            webElementInteraction.clickElement(search);
            webElementInteraction.waitUntilElementIsVisible(searchingStatus, WAIT_2.getValue());
            webElementInteraction.waitUntilElementDisappears(searchingStatus, WAIT_2.getValue());
        }
        catch (TimeoutException e)
        {
            LOG.info("Timeout for searching. Continue...");
        }
    }

    public AddUserDialog searchUser(String userToSearch)
    {
        int retry = 0;
        typeInSearch(userToSearch);
        clickSearchButton();
        boolean found = isUserDisplayed(userToSearch);
        while (retry < WAIT_40.getValue() && !found)
        {
            waitToLoopTime(WAIT_1.getValue());
            clickSearchButton();
            retry++;
        }
        return this;
    }

    public AddUserDialog searchUser(UserModel userModel)
    {
        return searchUser(userModel.getUsername());
    }

    private AddUserDialog typeInSearch(String userToSearch)
    {
        fillInSearchInput(userToSearch);
        return this;
    }

    public ArrayList<String> getSearchResultsName()
    {
        return webElementInteraction.findElements(searchResultsList).stream()
            .map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public AddUserDialog assertUserIsFound(UserModel userModel)
    {
        assertTrue(getSearchResultsName().contains(getUserFormat(userModel)), String.format("User %s was not found", userModel.getUsername()));
        return this;
    }

    public int getItemIndexFromSearchResults(String searchResult)
    {
        ArrayList<String> searchResultsList = getSearchResultsName();
        return searchResultsList.indexOf(searchResult);
    }

    public void clickAddButtonForUser(String searchResult)
    {
        int index = getItemIndexFromSearchResults(searchResult);
        WebElement button = webElementInteraction.findElements(addButtonsList).get(index);
        webElementInteraction.mouseOver(button);
        webElementInteraction.clickElement(button);
    }

    public void addUser(UserModel userModel)
    {
        clickAddButtonForUser(String.format("%s %s (%s)",
            userModel.getFirstName(), userModel.getLastName(), userModel.getUsername()));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isUserDisplayed(String username)
    {
        return webElementInteraction.isElementDisplayed(By.cssSelector(String.format(searchedUsername, username)));
    }

    public AddUserDialog assertAddButtonIsDisplayedForUser(UserModel user)
    {
        int index = getItemIndexFromSearchResults(getUserFormat(user));
        assertTrue(webElementInteraction.isElementDisplayed(webElementInteraction.findElements(addButtonsList).get(index)));
        return this;
    }

    public boolean isCloseButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(closeButton);
    }

    private String getUserFormat(UserModel userModel)
    {
        return String.format("%s %s (%s)", userModel.getFirstName(), userModel.getLastName(), userModel.getUsername());
    }
}
