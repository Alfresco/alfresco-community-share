package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AddUserDialog extends BaseDialogComponent
{
    protected String searchedUsername = "a[href$='%s/profile']";

    private final By dialogTitle = By.cssSelector("span[id*='peoplepicker']");
    private final By searchInputField = By.cssSelector("div[id*='search-peoplefinder'] input");
    private final By searchButton = By.cssSelector("div[id*='search-peoplefinder'] button[id*='search']");
    private final By searchResultsList = By.cssSelector(".itemname");
    private final By searchInput = By.xpath("//input[@name='-']");
    private final By setRole = By.xpath("//button[text()='Select Role ▾']");
    private final By selectManagerRole = By.xpath("//a[text()='Manager']");
    private final By addUserButton = By.xpath("//button[text()='Add Users']");
    private final By searchIn = By.xpath("//button[text()='Search']");
    private final By selectButton = By.xpath("//button[text()='Select']");

    private final By addButtonsList = By.cssSelector("td[class*='actions'] button");
    private final By closeButton = By.cssSelector("div[id*='default-peoplepicker'] a[class='container-close']");
    private final By searchingStatus = By.cssSelector("div[id$='search-peoplefinder-results'] .yui-dt-empty > div");

    public AddUserDialog(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public AddUserDialog assertAddUserDialogTitleIsCorrect()
    {
        assertEquals(findElement(dialogTitle).getText(), language.translate("adminTools.groups.addUserDialog.title"));
        return this;
    }

    public AddUserDialog assertSearchButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public AddUserDialog assertSearchInputIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    private void fillInSearchInput(String textToSearch)
    {
        clearAndType(searchInputField, textToSearch);
    }

    public void clickSearchButton()
    {
        try
        {
            WebElement search = waitUntilElementIsVisible(searchButton);
            clickElement(search);
            waitUntilElementIsVisible(searchingStatus, WAIT_2.getValue());
            waitUntilElementDisappears(searchingStatus, WAIT_2.getValue());
        }
        catch (TimeoutException e)
        {
            log.error("Timeout for searching. Continue...");
        }
    }

    public AddUserDialog searchUser(String userToSearch)
    {
        int retry = 0;
        typeInSearch(userToSearch);
        clickSearchButton();
        while (retry < WAIT_60.getValue() && !isUserDisplayed(userToSearch))
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
    public AddUserDialog addUserToSite(String userModel)
    {
        clearAndType(searchInput, userModel);
        clickElement(searchIn);
        waitInSeconds(3);
        return  this;
    }

    private AddUserDialog typeInSearch(String userToSearch)
    {
        fillInSearchInput(userToSearch);
        return this;
    }

    public AddUserDialog clickSelect(){
        clickElement(selectButton);
        waitInSeconds(2);
        return this;
    }
    public AddUserDialog setUserRole(){

        clickElement(setRole);
        clickElement(selectManagerRole);
        waitInSeconds(2);
        return this;
    }
    public AddUserDialog clickAddUser(){
        waitInSeconds(2);
        clickElement(addUserButton);
        waitInSeconds(2);

        return this;
    }



    public ArrayList<String> getSearchResultsName()
    {
        return findElements(searchResultsList).stream()
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
        WebElement button = findElements(addButtonsList).get(index);
        mouseOver(button);
        clickElement(button);
    }

    public void addUser(UserModel userModel)
    {
        clickAddButtonForUser(String.format("%s %s (%s)",
            userModel.getFirstName(), userModel.getLastName(), userModel.getUsername()));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isUserDisplayed(String username)
    {
        return isElementDisplayed(By.cssSelector(String.format(searchedUsername, username)));
    }

    public AddUserDialog assertAddButtonIsDisplayedForUser(UserModel user)
    {
        int index = getItemIndexFromSearchResults(getUserFormat(user));
        assertTrue(isElementDisplayed(findElements(addButtonsList).get(index)));
        return this;
    }

    public boolean isCloseButtonDisplayed()
    {
        return isElementDisplayed(closeButton);
    }

    private String getUserFormat(UserModel userModel)
    {
        return String.format("%s %s (%s)", userModel.getFirstName(), userModel.getLastName(), userModel.getUsername());
    }
}
