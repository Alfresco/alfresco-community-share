package org.alfresco.po.share.site.members;

import java.util.List;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

@PageObject
public class AddSiteUsersPage extends SiteCommon<AddSiteUsersPage>
{
    @RenderWebElement
    @FindBy (css = "div[class='finder-wrapper']")
    protected WebElement searchForUsersBox;

    @RenderWebElement
    @FindBy (css = "div[class='invitationlist']")
    protected WebElement invitationListBox;

    @RenderWebElement
    @FindBy (css = "div[class='added-users-list']")
    protected WebElement addedUsersBox;

    @FindBy (css = "div[class='instructions-bar']")
    protected WebElement instructionsBar;

    @FindBy (css = "a[id*='site-groups-link']")
    protected WebElement siteGroups;

    @FindAll (@FindBy (css = "div[class='added-users-list'] tbody[class='yui-dt-data'] tr"))
    protected List<WebElement> addedUsersRows;
    @RenderWebElement
    @FindBy (css = "[id*='default-invite-button-button']")
    protected WebElement addUsersButton;
    @FindBy (css = "[id*='default-firstname']")
    protected WebElement externalUserFirstNameInput;
    @FindBy (css = "[id*='default-lastname']")
    protected WebElement externalUserLastNameInput;
    @FindBy (css = "[id*='default-email']")
    protected WebElement externalUserEmailInput;
    @FindBy (css = "[id*='add-email-button-button']")
    protected WebElement externalUserAddButton;
    @FindBy (css = "[id*='selectallroles-button-button']")
    protected WebElement setAllRolesToButton;
    @FindBy (css = "[id*='info-button-button']")
    protected WebElement infoIcon;
    @FindBy (css = ".alf-info-balloon .text")
    protected WebElement infoBalloonText;
    @FindBy (css = ".alf-info-balloon .closeButton")
    protected WebElement infoBalloonCloseButton;
    @FindBy (css = ".alf-info-balloon a")
    protected WebElement seeMoreLink;
    @FindAll (@FindBy (css = "div.visible ul.first-of-type li a"))
    private List<WebElement> dropDownOptionsList;
    private By infoBalloon = By.xpath("//div[contains(@class,'yui-module') and contains(@style, 'visibility: visible')]");
    private By searchResultsRows = By.cssSelector("div[class*='results'] tbody[class='yui-dt-data'] tr");
    private By setUserRoleRows = By.cssSelector("div[class='invitationlist'] tbody[class='yui-dt-data'] tr");
    private By addedUsersTally = By.cssSelector(".added-users-list-tally");
    private By searchBoxInput = By.cssSelector("input[id*='default-search-text']");
    private By selectRoleButton = By.cssSelector("td[class*='role'] button");
    private By removeButton = By.cssSelector("td[class*='remove'] .removeIcon");
    private By userRoleValue = By.cssSelector(".detail");
    private By panelMessage = By.cssSelector(".yui-dt-message .yui-dt-liner");

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-users", getCurrentSiteName());
    }

    /**
     * Open Groups page by pressing Groups link on top of the page
     */
    public SiteGroupsPage openGroupsPage()
    {
        siteGroups.click();
        return new SiteGroupsPage();
    }

    /**
     * Search for specific user on add users page
     *
     * @param user
     * @return
     */
    public AddSiteUsersPage searchForUser(String user)
    {
        searchForUsersBox.findElement(searchBoxInput).clear();
        searchForUsersBox.findElement(searchBoxInput).sendKeys(user);
        searchForUsersBox.findElement(By.cssSelector("button")).click();

        int attempt = 0;
        //clicking 5 times on Search button because sometimes it returns incorrect "No users found"
        while (!user.isEmpty() && browser.findElements(searchResultsRows).size() == 0 && attempt < 5)
        {
            searchForUsersBox.findElement(By.cssSelector("button")).click();
            attempt++;
            browser.waitInSeconds(3);
        }
        return (AddSiteUsersPage) this.renderedPage();
    }

    /**
     * Select user details in search results
     *
     * @param username
     * @return
     */
    public WebElement selectUser(String username)
    {
        return browser.findFirstElementWithValue(searchResultsRows, username);
    }

    public boolean isUserDisplayedInSearchResults(String user)
    {
        return browser.findElements(searchResultsRows).size() != 0 && selectUser(user) != null;
    }

    /**
     * Checks whether Select button for an user is enabled or disabled
     *
     * @param username
     * @return
     */
    public boolean isSelectUserButtonEnabled(String username)
    {
        return selectUser(username).findElement(By.cssSelector("button")).isEnabled();
    }

    /**
     * Click on the Select button for the specified user in Search for users panel
     *
     * @param username
     */
    public void clickSelectUserButton(String username)
    {
        selectUser(username).findElement(By.cssSelector("button")).click();
        browser.waitUntilElementsVisible(setUserRoleRows);
    }

    private void selectRoleFromDropDown(String role)
    {
        for (WebElement dropDownOption : dropDownOptionsList)
        {
            if (dropDownOption.getText().equals(role))
            {
                dropDownOption.click();
                break;
            }
        }
    }

    /**
     * Set all roles to the specified value selected from 'Set all roles to' dropdown
     *
     * @param role
     */
    public void setAllRolesTo(String role)
    {
        setAllRolesToButton.click();
        selectRoleFromDropDown(role);
    }

    /**
     * Set role to the specified value for the given user
     *
     * @param userName
     * @param role
     */
    public void setUserRole(String userName, String role)
    {
        browser.findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton).click();
        selectRoleFromDropDown(role);
    }

    /**
     * Get User role from 'Set User Role' panel
     *
     * @param userName
     * @return user role
     */
    public String getUserRole(String userName)
    {
        browser.waitUntilElementsVisible(setUserRoleRows);
        return browser.findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton).getText();
    }

    /**
     * Click on 'Remove' icon for the specified user, from 'Set User Role' panel
     *
     * @param userName
     */
    public void removeUser(String userName)
    {
        browser.findFirstElementWithValue(setUserRoleRows, userName).findElement(removeButton).click();
    }

    public void clickAddUsers()
    {
        while (isWaitPopupDisplayed() == true)
        {
            browser.waitUntilElementDisappears(waitPopup);
        }
        addUsersButton.click();
        waitUntilMessageDisappears();
    }

    public String getAddedUsersTally()
    {
        waitUntilMessageDisappears();
        browser.waitUntilElementIsVisibleWithRetry(addedUsersTally, 10);
        return addedUsersBox.findElement(addedUsersTally).getText();
    }

    public AddSiteUsersPage assertTotalUserAddedIs(int nrOfUsers)
    {
        String value = language.translate("addUsersPage.addedUsersTally") + nrOfUsers;
        browser.waitUntilElementContainsText(browser.findElement(addedUsersTally), value);
        Assert.assertEquals(browser.findElement(addedUsersTally).getText(), value, "Total added user is correct");
        return this;
    }

    public boolean isUserAddedToSite(String userName)
    {
        return browser.findFirstElementWithValue(addedUsersRows, userName) != null;
    }

    /**
     * Get User role from 'Add Users To Site' panel
     *
     * @param userName
     * @return user role
     */
    public String getUserRoleValue(String userName)
    {
        return browser.findFirstElementWithValue(addedUsersRows, userName).findElement(userRoleValue).getText();
    }

    public void addExternalUser(String firstName, String lastName, String email)
    {
        externalUserFirstNameInput.sendKeys(firstName);
        externalUserLastNameInput.sendKeys(lastName);
        externalUserEmailInput.sendKeys(email);
        externalUserAddButton.click();
    }

    public boolean isSearchForUsersPanelDisplayed()
    {
        return searchForUsersBox.isDisplayed();
    }

    public boolean isSearchBoxInputDisplayed()
    {
        return searchForUsersBox.findElement(searchBoxInput).isDisplayed();
    }

    public boolean isSearchButtonEnabled()
    {
        return searchForUsersBox.findElement(By.cssSelector("button")).isEnabled();
    }

    public String getSearchForUsersPanelMessage()
    {
        return searchForUsersBox.findElement(panelMessage).getText();
    }

    public boolean isSetUserRolePanelDisplayed()
    {
        return browser.isElementDisplayed(invitationListBox);
    }

    public boolean isSetAllRolesToButtonDisplayed()
    {
        return browser.isElementDisplayed(setAllRolesToButton);
    }

    public boolean isInfoIconDisplayed()
    {
        return browser.isElementDisplayed(infoIcon);
    }

    public String getSetUserRolePanelMessage()
    {
        return invitationListBox.findElement(panelMessage).getText();
    }

    public boolean isAddUsersToSitePanelDisplayed()
    {
        return browser.isElementDisplayed(addedUsersBox);
    }

    public boolean isAddUsersButtonEnabled()
    {
        return addUsersButton.isEnabled();
    }

    public String getAddUsersToSitePanelMessage()
    {
        return addedUsersBox.findElement(panelMessage).getText();
    }

    public boolean isAddExternalUsersPanelDisplayed()
    {
        return browser.isElementDisplayed(externalUserFirstNameInput) &&
            browser.isElementDisplayed(externalUserLastNameInput) &&
            browser.isElementDisplayed(externalUserEmailInput) &&
            browser.isElementDisplayed(externalUserAddButton);
    }

    public void clickInfoIcon()
    {
        infoIcon.click();
        browser.waitUntilElementVisible(infoBalloon);
    }

    public boolean isInfoBalloonDisplayed()
    {
        return browser.findElements(infoBalloon).size() != 0;
    }

    public String getInfoBalloonText()
    {
        return infoBalloonText.getText();
    }

    public void closeInfoBalloon()
    {
        infoBalloonCloseButton.click();
        browser.waitUntilElementDisappears(infoBalloon, 30);
    }

    public void clickSeeMoreLink()
    {
        seeMoreLink.click();
    }
}