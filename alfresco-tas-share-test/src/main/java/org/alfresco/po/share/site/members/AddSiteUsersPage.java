package org.alfresco.po.share.site.members;

import static org.alfresco.common.RetryTime.RETRY_TIME_80;
import static org.alfresco.common.Wait.WAIT_2;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.SiteCommon;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class AddSiteUsersPage extends SiteCommon<AddSiteUsersPage>
{
    private final int BEGIN_INDEX = 0;
    private final String NEW_LINE = "\n";

    private final By searchForUsersBox = By.cssSelector("div[class='finder-wrapper']");
    private final By searchUserInput = By.cssSelector("input[id*=people-finder]");
    private final By searchUserButton = By.cssSelector("button[id*=people-finder]");
    private final By invitationListBox = By.cssSelector( "div[class='invitationlist']");
    private final By addedUsersBox = By.cssSelector("div[class='added-users-list']");
    private final By addUsersButton = By.cssSelector("[id*='default-invite-button-button']");
    private final By externalUserFirstNameInput = By.cssSelector("[id*='default-firstname']");
    private final By externalUserLastNameInput = By.cssSelector("[id*='default-lastname']");
    private final By externalUserEmailInput = By.cssSelector("[id*='default-email']");
    private final By externalUserAddButton = By.cssSelector("[id*='add-email-button-button']");
    private final By setAllRolesToButton = By.cssSelector("[id*='selectallroles-button-button']");
    private final By infoIcon = By.cssSelector("[id*='info-button-button']");
    private final By infoBalloonText = By.cssSelector(".alf-info-balloon .text");
    private final By seeMoreLink = By.cssSelector(".alf-info-balloon a");
    private final By dropDownOptionsList = By.cssSelector("div.visible ul.first-of-type li a");
    private final By addedUsersTally = By.cssSelector(".added-users-list-tally");
    private final By selectRoleButton = By.cssSelector("td[class*='role'] button");
    private final By removeButton = By.cssSelector("td[class*='remove'] .removeIcon");
    private final By userRoleValue = By.cssSelector(".detail");
    private final By panelMessage = By.cssSelector(".yui-dt-message .yui-dt-liner");
    private final By selectButton = By.cssSelector("td[headers$='actions '] button");

    private final String searchUserRow = "//span[text()='(%s)']/../../../..";
    private final String addedUserRow = "//h3[text()='%s']/../..";
    private final String setUserRoleRows = "//div[@class='invitationlist']//tbody[@class='yui-dt-data']//span[text()='(%s)']/../../../..";

    public AddSiteUsersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/add-users", getCurrentSiteName());
    }

    public AddSiteUsersPage searchUserWithName(String username)
    {
        WebElement searchInput = findElement(searchUserInput);
        clearAndType(searchInput, username);
        clickElement(searchUserButton);
        searchUserWithRetry(username);

        return this;
    }

    private void searchUserWithRetry(String username)
    {
        int retryCount = 0;
        while (!isUsernameDisplayed(username) && retryCount < RETRY_TIME_80.getValue())
        {
            log.warn("Username {} not displayed - retry: {}", username, retryCount);
            clickElement(searchUserButton);
            waitInSeconds(WAIT_2.getValue());
            retryCount++;
        }
        throwExceptionIfUsernameNotDisplayed(isUsernameDisplayed(username), retryCount);
    }

    private boolean isUsernameDisplayed(String userName)
    {
        return isElementDisplayed(By.xpath(String.format(searchUserRow, userName)));
    }

    private void throwExceptionIfUsernameNotDisplayed(boolean isUsernameDisplayed, int retryCount)
    {
        if (!isUsernameDisplayed)
        {
            throw new NoSuchElementException(
                String.format("Username was not displayed in the given retries %d", retryCount));
        }
    }

    private WebElement getUserRow(String username)
    {
        return findElement(By.xpath(String.format(searchUserRow, username)));
    }

    private WebElement getRoleRow(String username)
    {
        return findElement(By.xpath(String.format(setUserRoleRows, username)));
    }

    public AddSiteUsersPage clickSelectUserButton(String username)
    {
        WebElement selectButtonElement = getUserRow(username).findElement(selectButton);
        clickElement(selectButtonElement);
        return this;
    }

    public AddSiteUsersPage removeUser(String userName)
    {
        WebElement removeButtonElement = findElement(By.xpath(String.format(setUserRoleRows, userName)))
            .findElement(removeButton);

        clickElement(removeButtonElement);
        return this;
    }

    private void selectRoleFromDropDown(String role)
    {
        for (WebElement dropDownOption : findElements(dropDownOptionsList))
        {
            if (dropDownOption.getText().equals(role))
            {
                clickElement(dropDownOption);
                break;
            }
        }
    }

    public AddSiteUsersPage setAllRolesTo(String role)
    {
        clickElement(setAllRolesToButton);
        selectRoleFromDropDown(role);
        return this;
    }

    public AddSiteUsersPage setUserRole(String userName, String role)
    {
        WebElement selectRoleElement = getRoleRow(userName).findElement(selectRoleButton);
        clickElement(selectRoleElement);

        selectRoleFromDropDown(role);
        return this;
    }

    public void addUsersToSite()
    {
        while (isWaitPopupDisplayed())
        {
            waitUntilElementDisappears(waitPopup);
        }
        clickElement(addUsersButton);
        waitUntilNotificationMessageDisappears();
    }

    public AddSiteUsersPage assertTotalUserIs(int nrOfUsers)
    {
        String value = language.translate("addUsersPage.addedUsersTally") + " " +nrOfUsers;
        waitUntilElementIsVisible(addedUsersTally);
        waitUntilElementContainsText(findElement(addedUsersTally), value);
        assertEquals(getElementText(addedUsersTally), value, "Total added user is correct");
        return this;
    }

    public AddSiteUsersPage assertUserNameEqualsTo(String expectedUserName)
    {
        String actualUsername = getElementText(By.xpath(String.format(addedUserRow, expectedUserName)));
        String formattedActualUsername = actualUsername
            .substring(BEGIN_INDEX, actualUsername.indexOf(NEW_LINE));

        assertEquals(formattedActualUsername, expectedUserName,
            String.format("Added user name not equals %s ", expectedUserName));
        return this;
    }

    public AddSiteUsersPage assertUserRoleEqualsTo(String username, String expectedRole)
    {
        String actualUserRole = waitUntilElementIsVisible(By.xpath(String.format(addedUserRow, username)))
            .findElement(userRoleValue).getText();

        assertEquals(actualUserRole, expectedRole,
            String.format("User role not equals %s ", expectedRole));
        return this;
    }

    public void addExternalUser(String firstName, String lastName, String email)
    {
        findElement(externalUserFirstNameInput).sendKeys(firstName);
        findElement(externalUserLastNameInput).sendKeys(lastName);
        findElement(externalUserEmailInput).sendKeys(email);
        findElement(externalUserAddButton);
    }

    public AddSiteUsersPage assertSearchUserMessageEqualsTo(String expectedEmptyMessage)
    {
        log.info("Assert search user message equals to {}", expectedEmptyMessage);
        String actualEmptyMessage = findElement(searchForUsersBox)
            .findElement(panelMessage).getText();

        assertEquals(actualEmptyMessage, expectedEmptyMessage,
            String.format("Search for empty message not equals %s ", expectedEmptyMessage));
        return this;
    }

    public AddSiteUsersPage assertSetUserRoleInfoIconIsDisplayed()
    {
        log.info("Assert set user role info icon is displayed");
        assertTrue(isElementDisplayed(infoIcon),
            "Info icon is not displayed");
        return this;
    }

    public AddSiteUsersPage assertSetUserRoleMessageEqualsTo(String expectedSetUserRoleEmptyMessage)
    {
        log.info("Assert set user role message equals to {}", expectedSetUserRoleEmptyMessage);
        String actualEmptyMessage = findElement(invitationListBox)
            .findElement(panelMessage).getText();

        assertEquals(actualEmptyMessage, expectedSetUserRoleEmptyMessage, String
            .format("Set user role empty message not equals %s ", expectedSetUserRoleEmptyMessage));
        return this;
    }

    public AddSiteUsersPage assertAddUsersToSiteMessageEqualsTo(String expectedAddUserToSiteEmptyMessage)
    {
        log.info("Assert add user message equals to {}", expectedAddUserToSiteEmptyMessage);
        String actualEmptyMessage = findElement(addedUsersBox)
            .findElement(panelMessage).getText();

        assertEquals(actualEmptyMessage, expectedAddUserToSiteEmptyMessage, String
            .format("Add user to site empty message not equals %s ",
                expectedAddUserToSiteEmptyMessage));
        return this;
    }

    public AddSiteUsersPage clickInfoIcon()
    {
        clickElement(infoIcon);
        return this;
    }

    public AddSiteUsersPage assertInfoBalloonTextEqualsTo(String expectedInfoBalloonMessage)
    {
        log.info("Assert info balloon message equals to {}", expectedInfoBalloonMessage);
        String actualInfoBalloonMessage = getElementText(infoBalloonText);

        assertEquals(actualInfoBalloonMessage, expectedInfoBalloonMessage,
            String.format("Info balloon text not equals %s", expectedInfoBalloonMessage));
        return this;
    }

    public AddSiteUsersPage assertPageTitleOpenedInNewTabEquals(String expectedTitle)
    {
        log.info("Assert page title opened in new tab contains {}", expectedTitle);
        String actualTitle = getTitle();

        assertTrue(actualTitle.contains(expectedTitle),
            String.format("Page title opened in new tab not contains %s", expectedTitle));
        return this;
    }

    public AddSiteUsersPage clickSeeMoreLink()
    {
        log.info("Click See More link button");
        clickElement(seeMoreLink);
        return this;
    }
}