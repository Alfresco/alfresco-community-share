package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.alfresco.common.RetryTime.RETRY_TIME_10;
import static org.alfresco.common.RetryTime.RETRY_TIME_30;
import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

@Slf4j
public class UsersPage extends SharePage2<UsersPage>
{
    private final By newUserButton = By.cssSelector("button[id$='_default-newuser-button-button']");
    private final By uploadUsersButton = By.cssSelector("button[id*='uploadusers']");
    private final By userSearchInputField = By.cssSelector("input[id*='search-text']");
    private final By searchButton = By.cssSelector("button[id*='search']");
    private final By fileInput = By.cssSelector("input[id*='default-filedata-file']");
    private final By uploadButton = By.cssSelector("#template_x002e_html-upload_x002e_console_x0023_default-upload-button-button");
    private final By tableHeaderElements = By.cssSelector("div[class$='results'] table>thead>tr:nth-of-type(1) span");
    private final By searchTextResult = By.cssSelector("div[id$='default-search-bar']");
    private final By usersList = By.xpath("//td[@headers='yui-dt3-th-userName ']");

    private final String userRow = "//td[contains(@headers, 'userName')]//div[text()='%s']/../..";

    public UsersPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users";
    }

    public CreateUserPage clickNewUserButton()
    {
        log.info("Click New User");
        WebElement newUserElement = waitUntilElementIsVisible(newUserButton);
        clickElement(newUserElement);
        return new CreateUserPage(webDriver);
    }

    public UsersPage searchUserWithRetry(String user)
    {
        log.info("Search user: {}", user);
        return searchUser(user, user);
    }

    public UsersPage searchForWithRetry(String searchKeyword, String userToWaitFor)
    {
        return searchUser(searchKeyword, userToWaitFor);
    }

    private void typeUserAndClickSearch(String searchKeyword)
    {
        waitUntilElementIsVisible(userSearchInputField);
        clearAndType(userSearchInputField, searchKeyword);
        WebElement search = findElement(searchButton);
        mouseOver(search);
        clickElement(search);
    }

    private UsersPage searchUser(String searchKeyword, String userToWaitFor)
    {
        typeUserAndClickSearch(searchKeyword);
        int counter = 0;
        boolean found = isUserFound(userToWaitFor);
        while (!found && counter <= RETRY_TIME_10.getValue())
        {
            waitToLoopTime(WAIT_2.getValue(), String.format("Wait for user to be displayed: %s", userToWaitFor));
            typeUserAndClickSearch(searchKeyword);
            found = isUserFound(userToWaitFor);
            counter++;
        }
        return this;
    }

    public UsersPage assertSearchTextIsCorrect(String searchText, int results)
    {
        String expectedValue = String.format(language.translate("adminTools.user.search.text"), searchText, results);
        waitUntilElementContainsText(findElement(searchTextResult), expectedValue);
        assertEquals(findElement(searchTextResult).getText(), expectedValue);
        return this;
    }

    public UsersPage searchUserWithRetry(UserModel user)
    {
        return searchUserWithRetry(user.getUsername());
    }

    public UsersPage assertSuccessfullyCreatedNewUserNotificationIsDisplayed()
    {
        assertEquals(notificationMessageThread.get(), language.translate("adminTools.users.createUserNotification"),
            "Create user notification is displayed");
        return this;
    }

    public UsersPage searchUser(String user)
    {
        log.info("Search for user: {}", user);
        clearAndType(userSearchInputField, user);
        clickElement(searchButton);
        return this;
    }
    public boolean UserList(String userName) {
        List<WebElement> ListofUsers = findElements(usersList);
        for (WebElement webElement : ListofUsers)
        {
            if (webElement.getText().contains(userName))
                return true;
        }
        return false;
    }
    public UsersPage assertIsUsersCreated(String user)
    {
        log.info("Verify user is Created");
        assertTrue(UserList(user), String.format("User %s is Not Created", user));
        return this;
    }

    public boolean isUserFound(String user)
    {
        return isElementDisplayed(By.xpath(String.format(userRow, user)));
    }
    public WebElement getUserRow(String userName)
    {
        return waitWithRetryAndReturnWebElement(By.xpath(String.format(userRow, userName)),
                WAIT_2.getValue(), RETRY_TIME_30.getValue());
    }

    public UsersPage assertSearchInputIsDisplayed()
    {
        assertTrue(isElementDisplayed(userSearchInputField), "Search input is displayed");
        return this;
    }

    public UsersPage assertSearchButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public UsersPage assertNewUserButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(newUserButton), "New User button is displayed");
        return this;
    }

    public UsersPage assertImportUsersButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(uploadUsersButton), "Upload users button is displayed");
        return this;
    }

    public UploadUserResultsPage uploadUsers(String filePath)
    {
        waitUntilElementIsVisible(uploadUsersButton);
        clickElement(uploadUsersButton);
        waitUntilElementIsVisible(fileInput);
        clearAndType(fileInput, filePath);
        clickElement(uploadButton);
        return new UploadUserResultsPage(webDriver);
    }

    public UsersPage assertDeleteUserNotificationIsDisplayed()
    {
        log.info("Assert delete user notification is displayed");
        assertEquals(notificationMessageThread.get(), language.translate("adminTools.user.deleteUser.notification"));
        return this;
    }

    public UsersPage assertAllTableHeadersAreDisplayed()
    {
        log.info("Assert all table headers are displayed");
        List<String> tableHeaders = getTextFromElementList(findElements(tableHeaderElements));
        List<String> expectedTableHeaders = Collections.synchronizedList(new ArrayList<>());
        expectedTableHeaders.add(language.translate("adminTools.user.table.name"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.userName"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.jobTitle"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.email"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.usage"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.quota"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.authorizationState"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.deleted"));
        expectedTableHeaders.add(language.translate("adminTools.user.table.action"));
        assertTrue(tableHeaders.containsAll(expectedTableHeaders), "All table headers are displayed");

        return this;
    }

    public UserRowAction usingUser(UserModel user)
    {
        return new UserRowAction(user, this);
    }

    //todo: move into separate file
    public class UserRowAction
    {
        private UsersPage usersPage;
        private UserModel user;
        private String userName;

        private By userDisableIcon = By.cssSelector("img[src*='account_disabled.png']");
        private By quotaElement = By.cssSelector("td[headers$='quota '] div");
        private By userFullName = By.cssSelector("td[headers$='fullName '] a");
        private By userDeletedIcon = By.cssSelector("div[style*='deleted-user-photo-64.png']");
        private By deleted = By.cssSelector("td[headers$='isDeleted '] div");

        public UserRowAction(UserModel user, UsersPage usersPage)
        {
            this.user = user;
            this.usersPage = usersPage;
            userName = user.getUsername();
        }

        public WebElement getUserRow()
        {
            return usersPage.getUserRow(user.getUsername());
        }

        public UserRowAction assertUserIsDisabled()
        {
            log.info("Assert user {} is disabled", user.getUsername());
            assertTrue(isElementDisplayed(getUserRow().findElement(userDisableIcon)),
                "User disabled icon is displayed");
            return this;
        }

        public UserRowAction assertUserIsFound()
        {
            log.info("Assert user {} is found", userName);
            assertTrue(usersPage.isUserFound(userName), String.format("User %s was found", userName));
            return this;
        }

        public UserRowAction assertUserIsNotFound()
        {
            log.info("Assert user {} is found", userName);
            assertFalse(usersPage.isUserFound(userName), String.format("User %s was found", userName));
            return this;
        }

        public UserRowAction assertQuotaIs(String expectedValue)
        {
            log.info("Assert quota value is: {}", expectedValue);
            assertEquals(getUserRow().findElement(quotaElement).getText(), expectedValue,
                "Quota value is displayed");
            return this;
        }

        public UserProfileAdminToolsPage selectUserFullName()
        {
            clickElement(getUserRow().findElement(userFullName));
            return new UserProfileAdminToolsPage(webDriver);
        }

        public UserRowAction assertUserDeleteIconIsDisplayed()
        {
            assertTrue(isElementDisplayed(getUserRow().findElement(userDeletedIcon)),
                "User delete icon is displayed");
            return this;
        }

        public UserRowAction assertDeletedIsDisplayed()
        {
            assertEquals(getUserRow().findElement(deleted).getText(), language.translate("adminTools.user.deleted"));
            return this;
        }
    }
}
