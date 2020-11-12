package org.alfresco.po.share.site.members;

import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class AddSiteUsersPage extends SiteCommon<AddSiteUsersPage>
{
    private SiteGroupsPage siteGroupsPage;

    @RenderWebElement
    private By searchForUsersBox = By.cssSelector("div[class='finder-wrapper']");
    @RenderWebElement
    private By invitationListBox = By.cssSelector( "div[class='invitationlist']");
    @RenderWebElement
    private By addedUsersBox = By.cssSelector("div[class='added-users-list']");
    private By instructionsBar = By.cssSelector("div[class='instructions-bar']");
    private By siteGroups = By.cssSelector("a[id*='site-groups-link']");
    private By addedUsersRows = By.cssSelector("div[class='added-users-list'] tbody[class='yui-dt-data'] tr");
    protected By addUsersButton = By.cssSelector("[id*='default-invite-button-button']");
    protected By externalUserFirstNameInput = By.cssSelector("[id*='default-firstname']");
    protected By externalUserLastNameInput = By.cssSelector("[id*='default-lastname']");
    protected By externalUserEmailInput = By.cssSelector("[id*='default-email']");
    protected By externalUserAddButton = By.cssSelector("[id*='add-email-button-button']");
    protected By setAllRolesToButton = By.cssSelector("[id*='selectallroles-button-button']");
    protected By infoIcon = By.cssSelector("[id*='info-button-button']");
    protected By infoBalloonText = By.cssSelector(".alf-info-balloon .text");
    protected By infoBalloonCloseButton = By.cssSelector(".alf-info-balloon .closeButton");
    protected By seeMoreLink = By.cssSelector(".alf-info-balloon a");
    private By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private By infoBalloon = By.xpath("//div[contains(@class,'yui-module') and contains(@style, 'visibility: visible')]");
    private By searchResultsRows = By.cssSelector("div[class*='results'] tbody[class='yui-dt-data'] tr");
    private By setUserRoleRows = By.cssSelector("div[class='invitationlist'] tbody[class='yui-dt-data'] tr");
    private By addedUsersTally = By.cssSelector(".added-users-list-tally");
    private By searchBoxInput = By.cssSelector("input[id*='default-search-text']");
    private By selectRoleButton = By.cssSelector("td[class*='role'] button");
    private By removeButton = By.cssSelector("td[class*='remove'] .removeIcon");
    private By userRoleValue = By.cssSelector(".detail");
    private By panelMessage = By.cssSelector(".yui-dt-message .yui-dt-liner");

    public AddSiteUsersPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
        siteGroupsPage = new SiteGroupsPage(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-users", getCurrentSiteName());
    }

    public SiteGroupsPage openGroupsPage()
    {
        getBrowser().findElement(siteGroups).click();
        return (SiteGroupsPage) siteGroupsPage.renderedPage();
    }

    public AddSiteUsersPage searchForUser(String user)
    {
        WebElement searchBox = getBrowser().findElement(searchForUsersBox);
        searchBox.findElement(searchBoxInput).clear();
        searchBox.findElement(searchBoxInput).sendKeys(user);
        searchBox.findElement(By.cssSelector("button")).click();

        int attempt = 0;
        //clicking 5 times on Search button because sometimes it returns incorrect "No users found"
        while (!user.isEmpty() && getBrowser().findElements(searchResultsRows).size() == 0 && attempt < 5)
        {
            searchBox.findElement(By.cssSelector("button")).click();
            attempt++;
            getBrowser().waitInSeconds(1);
        }
        return (AddSiteUsersPage) this.renderedPage();
    }

    public WebElement selectUser(String username)
    {
        return getBrowser().findFirstElementWithValue(searchResultsRows, username);
    }

    public boolean isUserDisplayedInSearchResults(String user)
    {
        return getBrowser().findElements(searchResultsRows).size() != 0 && selectUser(user) != null;
    }

    public boolean isSelectUserButtonEnabled(String username)
    {
        return selectUser(username).findElement(By.cssSelector("button")).isEnabled();
    }

    public void clickSelectUserButton(String username)
    {
        selectUser(username).findElement(By.cssSelector("button")).click();
        getBrowser().waitUntilElementsVisible(setUserRoleRows);
    }

    private void selectRoleFromDropDown(String role)
    {
        for (WebElement dropDownOption : getBrowser().findElements(dropDownOptionsList))
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
        getBrowser().findElement(setAllRolesToButton).click();
        selectRoleFromDropDown(role);
    }

    public void setUserRole(String userName, String role)
    {
        getBrowser().findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton).click();
        selectRoleFromDropDown(role);
    }

    public String getUserRole(String userName)
    {
        getBrowser().waitUntilElementsVisible(setUserRoleRows);
        return getBrowser().findFirstElementWithValue(setUserRoleRows, userName).findElement(selectRoleButton).getText();
    }

    public void removeUser(String userName)
    {
        getBrowser().findFirstElementWithValue(setUserRoleRows, userName).findElement(removeButton).click();
    }

    public void clickAddUsers()
    {
        while (isWaitPopupDisplayed() == true)
        {
            getBrowser().waitUntilElementDisappears(waitPopup);
        }
        getBrowser().findElement(addUsersButton).click();
        waitUntilNotificationMessageDisappears();
    }

    public String getAddedUsersTally()
    {
        waitUntilNotificationMessageDisappears();
        getBrowser().waitUntilElementIsVisibleWithRetry(addedUsersTally, 10);
        return getBrowser().findElement(addedUsersBox).findElement(addedUsersTally).getText();
    }

    public AddSiteUsersPage assertTotalUserAddedIs(int nrOfUsers)
    {
        String value = language.translate("addUsersPage.addedUsersTally") + nrOfUsers;
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(addedUsersTally), value);
        Assert.assertEquals(getBrowser().findElement(addedUsersTally).getText(), value, "Total added user is correct");
        return this;
    }

    public boolean isUserAddedToSite(String userName)
    {
        getBrowser().waitUntilElementsVisible(addedUsersRows);
        return getBrowser().findFirstElementWithValue(addedUsersRows, userName) != null;
    }

    public String getUserRoleValue(String userName)
    {
        return getBrowser().findFirstElementWithValue(addedUsersRows, userName).findElement(userRoleValue).getText();
    }

    public void addExternalUser(String firstName, String lastName, String email)
    {
        getBrowser().findElement(externalUserFirstNameInput).sendKeys(firstName);
        getBrowser().findElement(externalUserLastNameInput).sendKeys(lastName);
        getBrowser().findElement(externalUserEmailInput).sendKeys(email);
        getBrowser().findElement(externalUserAddButton).click();
    }

    public boolean isSearchForUsersPanelDisplayed()
    {
        return getBrowser().findElement(searchForUsersBox).isDisplayed();
    }

    public boolean isSearchBoxInputDisplayed()
    {
        return getBrowser().findElement(searchForUsersBox).findElement(searchBoxInput).isDisplayed();
    }

    public boolean isSearchButtonEnabled()
    {
        return getBrowser().findElement(searchForUsersBox).findElement(By.cssSelector("button")).isEnabled();
    }

    public String getSearchForUsersPanelMessage()
    {
        return getBrowser().findElement(searchForUsersBox).findElement(panelMessage).getText();
    }

    public boolean isSetUserRolePanelDisplayed()
    {
        return getBrowser().isElementDisplayed(invitationListBox);
    }

    public boolean isSetAllRolesToButtonDisplayed()
    {
        return getBrowser().isElementDisplayed(setAllRolesToButton);
    }

    public boolean isInfoIconDisplayed()
    {
        return getBrowser().isElementDisplayed(infoIcon);
    }

    public String getSetUserRolePanelMessage()
    {
        return getBrowser().findElement(invitationListBox).findElement(panelMessage).getText();
    }

    public boolean isAddUsersToSitePanelDisplayed()
    {
        return getBrowser().isElementDisplayed(addedUsersBox);
    }

    public boolean isAddUsersButtonEnabled()
    {
        return getBrowser().findElement(addUsersButton).isEnabled();
    }

    public String getAddUsersToSitePanelMessage()
    {
        return getBrowser().findElement(addedUsersBox).findElement(panelMessage).getText();
    }

    public boolean isAddExternalUsersPanelDisplayed()
    {
        return getBrowser().isElementDisplayed(externalUserFirstNameInput) &&
            getBrowser().isElementDisplayed(externalUserLastNameInput) &&
            getBrowser().isElementDisplayed(externalUserEmailInput) &&
            getBrowser().isElementDisplayed(externalUserAddButton);
    }

    public void clickInfoIcon()
    {
        getBrowser().findElement(infoIcon).click();
        getBrowser().waitUntilElementVisible(infoBalloon);
    }

    public boolean isInfoBalloonDisplayed()
    {
        return getBrowser().findElements(infoBalloon).size() != 0;
    }

    public String getInfoBalloonText()
    {
        return getBrowser().findElement(infoBalloonText).getText();
    }

    public void closeInfoBalloon()
    {
        getBrowser().findElement(infoBalloonCloseButton).click();
        getBrowser().waitUntilElementDisappears(infoBalloon, 30);
    }

    public void clickSeeMoreLink()
    {
        getBrowser().findElement(seeMoreLink).click();
    }
}