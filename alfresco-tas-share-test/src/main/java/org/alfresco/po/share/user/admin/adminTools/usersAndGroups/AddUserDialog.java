package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.BaseDialogComponent;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AddUserDialog extends BaseDialogComponent
{
    protected String searchedUsername = "a[href$='%s/profile']";

    @RenderWebElement
    private final By dialogTitle = By.cssSelector("span[id*='peoplepicker']");
    @RenderWebElement
    private final By searchInputField = By.cssSelector("div[id*='search-peoplefinder'] input");
    @RenderWebElement
    private final By searchButton = By.cssSelector("div[id*='search-peoplefinder'] button[id*='search']");
    private final By searchResultsList = By.cssSelector(".itemname");
    private final By addButtonsList = By.cssSelector("td[class*='actions'] button");
    private final By closeButton = By.cssSelector("div[id*='default-peoplepicker'] a[class='container-close']");

    public AddUserDialog(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public AddUserDialog assertAddUserDialogTitleIsCorrect()
    {
        assertEquals(getBrowser().findElement(dialogTitle).getText(), language.translate("adminTools.groups.addUserDialog.title"));
        return this;
    }

    public AddUserDialog assertSearchButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public AddUserDialog assertSearchInputIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchInputField), "Search input is displayed");
        return this;
    }

    private void fillInSearchInput(String textToSearch)
    {
        clearAndType(searchInputField, textToSearch);
    }

    public void clickSearchButton()
    {
        getBrowser().waitUntilElementClickable(searchButton).click();
    }

    public AddUserDialog searchUser(String userToSearch)
    {
        int retry = 0;
        typeAndSearch(userToSearch);
        boolean found = isUserDisplayed(userToSearch);
        while (retry < WAIT_30 && !found)
        {
            typeAndSearch(userToSearch);
            found = isUserDisplayed(userToSearch);
            retry++;
            Utility.waitToLoopTime(1);
            LOG.error(String.format("Waiting for user %s to be found.", userToSearch));
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

    public ArrayList<String> getSearchResultsName()
    {
        return getBrowser().findElements(searchResultsList).stream()
            .map(WebElement::getText)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public AddUserDialog assertUserIsFound(UserModel userModel)
    {
        assertTrue(getSearchResultsName().contains(getUserFormat(userModel)));
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
        getBrowser().findElements(addButtonsList).get(index).click();
    }

    public void addUser(UserModel userModel)
    {
        clickAddButtonForUser(String.format("%s %s (%s)",
            userModel.getFirstName(), userModel.getLastName(), userModel.getUsername()));
        waitUntilNotificationMessageDisappears();
    }

    public boolean isUserDisplayed(String username)
    {
        return getBrowser().isElementDisplayed(By.cssSelector(String.format(searchedUsername, username)));
    }

    public boolean isAddButtonDisplayed(String searchResult)
    {
        int index = getItemIndexFromSearchResults(searchResult);
        return getBrowser().isElementDisplayed(getBrowser().findElements(addButtonsList).get(index));
    }

    public AddUserDialog assertAddButtonIsDisplayedForUser(UserModel user)
    {
        int index = getItemIndexFromSearchResults(getUserFormat(user));
        assertTrue(getBrowser().isElementDisplayed(getBrowser().findElements(addButtonsList).get(index)));
        return this;
    }

    public boolean isCloseButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(closeButton);
    }

    private String getUserFormat(UserModel userModel)
    {
        return String.format("%s %s (%s)", userModel.getFirstName(), userModel.getLastName(), userModel.getUsername());
    }
}
