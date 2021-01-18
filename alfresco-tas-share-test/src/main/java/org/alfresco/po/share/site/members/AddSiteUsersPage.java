package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class AddSiteUsersPage extends SiteCommon<AddSiteUsersPage>
{
    private final By searchForUsersBox = By.cssSelector("div[class='finder-wrapper']");
    private final By invitationListBox = By.cssSelector( "div[class='invitationlist']");
    private final By addedUsersBox = By.cssSelector("div[class='added-users-list']");
    private final By addedUsersRows = By.cssSelector("div[class='added-users-list'] tbody[class='yui-dt-data'] tr");
    private final By addUsersButton = By.cssSelector("[id*='default-invite-button-button']");
    private final By externalUserFirstNameInput = By.cssSelector("[id*='default-firstname']");
    private final By externalUserLastNameInput = By.cssSelector("[id*='default-lastname']");
    private final By externalUserEmailInput = By.cssSelector("[id*='default-email']");
    private final By externalUserAddButton = By.cssSelector("[id*='add-email-button-button']");
    private final By setAllRolesToButton = By.cssSelector("[id*='selectallroles-button-button']");
    private final By infoIcon = By.cssSelector("[id*='info-button-button']");
    private final By infoBalloonText = By.cssSelector(".alf-info-balloon .text");
    private final By infoBalloonCloseButton = By.cssSelector(".alf-info-balloon .closeButton");
    private final By seeMoreLink = By.cssSelector(".alf-info-balloon a");
    private final By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private final By infoBalloon = By.xpath("//div[contains(@class,'yui-module') and contains(@style, 'visibility: visible')]");
    private final By searchResultsRows = By.cssSelector("div[class*='results'] tbody[class='yui-dt-data'] tr");
    private final By setUserRoleRows = By.cssSelector("div[class='invitationlist'] tbody[class='yui-dt-data'] tr");
    private final By addedUsersTally = By.cssSelector(".added-users-list-tally");
    private final By searchBoxInput = By.cssSelector("input[id*='default-search-text']");
    private final By selectRoleButton = By.cssSelector("td[class*='role'] button");
    private final By removeButton = By.cssSelector("td[class*='remove'] .removeIcon");
    private final By userRoleValue = By.cssSelector(".detail");
    private final By panelMessage = By.cssSelector(".yui-dt-message .yui-dt-liner");

    public AddSiteUsersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-users", getCurrentSiteName());
    }

    public AddSiteUsersPage searchForUser(String user)
    {
        WebElement searchBox = webElementInteraction.findElement(searchForUsersBox);
        webElementInteraction.clearAndType(searchBox, user);
        webElementInteraction.clickElement(searchBox.findElement(By.cssSelector("button")));

        int attempt = 0;
        //clicking 5 times on Search button because sometimes it returns incorrect "No users found"
        while (!user.isEmpty() && webElementInteraction.findElements(searchResultsRows).isEmpty() && attempt < 5)
        {
            webElementInteraction.clickElement(searchBox.findElement(By.cssSelector("button")));
            attempt++;
            webElementInteraction.waitInSeconds(1);
        }
        return this;
    }

    public WebElement selectUser(String username)
    {
        return webElementInteraction.findFirstElementWithValue(searchResultsRows, username);
    }

    public boolean isUserDisplayedInSearchResults(String user)
    {
        return !webElementInteraction.findElements(searchResultsRows).isEmpty() && selectUser(user) != null;
    }

    public boolean isSelectUserButtonEnabled(String username)
    {
        return selectUser(username).findElement(By.cssSelector("button")).isEnabled();
    }

    public void clickSelectUserButton(String username)
    {
        selectUser(username).findElement(By.cssSelector("button"));
        webElementInteraction.waitUntilElementsAreVisible(setUserRoleRows);
    }

    private void selectRoleFromDropDown(String role)
    {
        for (WebElement dropDownOption : webElementInteraction.findElements(dropDownOptionsList))
        {
            if (dropDownOption.getText().equals(role))
            {
                dropDownOption.click();
                break;
            }
        }
    }

    public void setAllRolesTo(String role)
    {
        webElementInteraction.findElement(setAllRolesToButton);
        selectRoleFromDropDown(role);
    }

    public void setUserRole(String userName, String role)
    {
        webElementInteraction.findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton);
        selectRoleFromDropDown(role);
    }

    public String getUserRole(String userName)
    {
        webElementInteraction.waitUntilElementsAreVisible(setUserRoleRows);
        return webElementInteraction.findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton).getText();
    }

    public void removeUser(String userName)
    {
        webElementInteraction.findFirstElementWithValue(setUserRoleRows, userName).findElement(removeButton);
    }

    public void clickAddUsers()
    {
        while (isWaitPopupDisplayed())
        {
            webElementInteraction.waitUntilElementDisappears(waitPopup);
        }
        webElementInteraction.findElement(addUsersButton);
        waitUntilNotificationMessageDisappears();
    }

    public String getAddedUsersTally()
    {
        waitUntilNotificationMessageDisappears();
        webElementInteraction.waitUntilElementIsVisibleWithRetry(addedUsersTally, 10);
        return webElementInteraction.findElement(addedUsersBox).findElement(addedUsersTally).getText();
    }

    public AddSiteUsersPage assertTotalUserAddedIs(int nrOfUsers)
    {
        String value = language.translate("addUsersPage.addedUsersTally") + nrOfUsers;
        webElementInteraction.waitUntilElementContainsText(webElementInteraction.findElement(addedUsersTally), value);
        Assert.assertEquals(webElementInteraction.findElement(addedUsersTally).getText(), value, "Total added user is correct");
        return this;
    }

    public boolean isUserAddedToSite(String userName)
    {
        webElementInteraction.waitUntilElementsAreVisible(addedUsersRows);
        return webElementInteraction.findFirstElementWithValue(addedUsersRows, userName) != null;
    }

    public String getUserRoleValue(String userName)
    {
        return webElementInteraction.findFirstElementWithValue(addedUsersRows, userName).findElement(userRoleValue).getText();
    }

    public void addExternalUser(String firstName, String lastName, String email)
    {
        webElementInteraction.findElement(externalUserFirstNameInput).sendKeys(firstName);
        webElementInteraction.findElement(externalUserLastNameInput).sendKeys(lastName);
        webElementInteraction.findElement(externalUserEmailInput).sendKeys(email);
        webElementInteraction.findElement(externalUserAddButton);
    }

    public boolean isSearchForUsersPanelDisplayed()
    {
        return webElementInteraction.findElement(searchForUsersBox).isDisplayed();
    }

    public boolean isSearchBoxInputDisplayed()
    {
        return webElementInteraction.findElement(searchForUsersBox).findElement(searchBoxInput).isDisplayed();
    }

    public boolean isSearchButtonEnabled()
    {
        return webElementInteraction.findElement(searchForUsersBox).findElement(By.cssSelector("button")).isEnabled();
    }

    public String getSearchForUsersPanelMessage()
    {
        return webElementInteraction.findElement(searchForUsersBox).findElement(panelMessage).getText();
    }

    public boolean isSetUserRolePanelDisplayed()
    {
        return webElementInteraction.isElementDisplayed(invitationListBox);
    }

    public boolean isSetAllRolesToButtonDisplayed()
    {
        return webElementInteraction.isElementDisplayed(setAllRolesToButton);
    }

    public boolean isInfoIconDisplayed()
    {
        return webElementInteraction.isElementDisplayed(infoIcon);
    }

    public String getSetUserRolePanelMessage()
    {
        return webElementInteraction.findElement(invitationListBox).findElement(panelMessage).getText();
    }

    public boolean isAddUsersToSitePanelDisplayed()
    {
        return webElementInteraction.isElementDisplayed(addedUsersBox);
    }

    public boolean isAddUsersButtonEnabled()
    {
        return webElementInteraction.findElement(addUsersButton).isEnabled();
    }

    public String getAddUsersToSitePanelMessage()
    {
        return webElementInteraction.findElement(addedUsersBox).findElement(panelMessage).getText();
    }

    public boolean isAddExternalUsersPanelDisplayed()
    {
        return webElementInteraction.isElementDisplayed(externalUserFirstNameInput) &&
            webElementInteraction.isElementDisplayed(externalUserLastNameInput) &&
            webElementInteraction.isElementDisplayed(externalUserEmailInput) &&
            webElementInteraction.isElementDisplayed(externalUserAddButton);
    }

    public void clickInfoIcon()
    {
        webElementInteraction.findElement(infoIcon);
        webElementInteraction.waitUntilElementIsVisible(infoBalloon);
    }

    public boolean isInfoBalloonDisplayed()
    {
        return !webElementInteraction.findElements(infoBalloon).isEmpty();
    }

    public String getInfoBalloonText()
    {
        return webElementInteraction.findElement(infoBalloonText).getText();
    }

    public void closeInfoBalloon()
    {
        webElementInteraction.clickElement(infoBalloonCloseButton);
        webElementInteraction.waitUntilElementDisappears(infoBalloon);
    }

    public void clickSeeMoreLink()
    {
        webElementInteraction.clickElement(seeMoreLink);
    }
}