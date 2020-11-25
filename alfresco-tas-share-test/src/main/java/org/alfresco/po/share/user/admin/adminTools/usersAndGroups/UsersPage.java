package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.BasePages;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UsersPage extends BasePages<UsersPage>
{
    @RenderWebElement
    private By newUserButton = By.cssSelector("button[id$='_default-newuser-button-button']");
    @RenderWebElement
    private By uploadUsersButton = By.cssSelector("button[id*='uploadusers']");
    private By userSearchInputField = By.cssSelector("input[id*='search-text']");
    private By searchButton = By.cssSelector("button[id*='search']");
    private By fileInput = By.cssSelector("input[id*='default-filedata-file']");
    private By uploadButton = By.cssSelector("#template_x002e_html-upload_x002e_console_x0023_default-upload-button-button");
    private By tableHeaderElements = By.cssSelector("div[class$='results'] table>thead>tr:nth-of-type(1) span");
    private By searchTextResult = By.cssSelector("div[id$='default-search-bar']");

    private String userRow = "//td[contains(@headers, 'userName')]//div[text()='%s']/../..";

    public UsersPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users";
    }

    public CreateUserPage clickNewUserButton()
    {
        LOG.info("Click New User");
        WebElement newUserElement = getBrowser().findElement(newUserButton);
        getBrowser().waitUntilElementVisible(newUserElement);
        getBrowser().mouseOver(newUserElement);
        newUserElement.click();
        return (CreateUserPage) new CreateUserPage(browser).renderedPage();
    }

    public UsersPage searchUserWithRetry(String user)
    {
        LOG.info(String.format("Search for user with retry: %s", user));
        return searchUser(user, user);
    }

    public UsersPage searchForWithRetry(String searchKeyword, String userToWaitFor)
    {
        return searchUser(searchKeyword, userToWaitFor);
    }

    private void typeUserAndClickSearch(String searchKeyword)
    {
        clearAndType(userSearchInputField, searchKeyword);
        WebElement search = getBrowser().findElement(searchButton);
        getBrowser().mouseOver(search);
        search.click();
    }

    private UsersPage searchUser(String searchKeyword, String userToWaitFor)
    {
        typeUserAndClickSearch(searchKeyword);
        int counter = 0;
        boolean found = isUserFound(userToWaitFor);
        while (!found && counter <= 10)
        {
            Utility.waitToLoopTime(1, String.format("Wait for user to be displayed: %s", userToWaitFor));
            typeUserAndClickSearch(searchKeyword);
            found = isUserFound(userToWaitFor);
            counter++;
        }
        return this;
    }

    public UsersPage assertSearchTextIsCorrect(String searchText, int results)
    {
        String expectedValue = String.format(language.translate("adminTools.user.search.text"), searchText, results);
        getBrowser().waitUntilElementContainsText(getBrowser().findElement(searchTextResult), expectedValue);
        assertEquals(getBrowser().findElement(searchTextResult).getText(), expectedValue);
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
        LOG.info(String.format("Search for user: %s", user));
        clearAndType(userSearchInputField, user);
        getBrowser().waitUntilElementClickable(searchButton).click();
        return this;
    }

    public boolean isUserFound(String user)
    {
        return getBrowser().isElementDisplayed(By.xpath(String.format(userRow, user)));
    }

    public WebElement getUserRow(String userName)
    {
        return getBrowser().waitWithRetryAndReturnWebElement(By.xpath(String.format(userRow, userName)), 1, WAIT_15);
    }

    public UsersPage assertSearchInputIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(userSearchInputField), "Search input is displayed");
        return this;
    }

    public UsersPage assertSearchButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public UsersPage assertNewUserButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(newUserButton), "New User button is displayed");
        return this;
    }

    public UsersPage assertImportUsersButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(uploadUsersButton), "Upload users button is displayed");
        return this;
    }

    public UploadUserResultsPage uploadUsers(String filePath)
    {
        if (tasProperties.getEnv().getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)(getBrowser().getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        getBrowser().findElement(uploadUsersButton).click();
        getBrowser().waitUntilElementVisible(fileInput).sendKeys(filePath);
        getBrowser().findElement(uploadButton).click();

        return (UploadUserResultsPage) new UploadUserResultsPage(browser).renderedPage();
    }

    public UsersPage assertDeleteUserNotificationIsDisplayed()
    {
        LOG.info("Assert delete user notification is displayed");
        assertEquals(notificationMessageThread.get(), language.translate("adminTools.user.deleteUser.notification"));
        return this;
    }

    public UsersPage assertAllTableHeadersAreDisplayed()
    {
        LOG.info("Assert all table headers are displayed");
        List<String> tableHeaders = getBrowser().getTextFromElementList(getBrowser().findElements(tableHeaderElements));
        List<String> expectedTableHeaders = new ArrayList<>();
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

        public WebBrowser getBrowser()
        {
            return usersPage.getBrowser();
        }

        public WebElement getUserRow()
        {
            return usersPage.getUserRow(user.getUsername());
        }

        public UserRowAction assertUserIsDisabled()
        {
            LOG.info(String.format("Assert user %s is disabled", user.getUsername()));
            assertTrue(getBrowser().isElementDisplayed(getUserRow().findElement(userDisableIcon)),
                "User disabled icon is displayed");
            return this;
        }

        public UserRowAction assertUserIsFound()
        {
            LOG.info(String.format("Assert user %s is found", userName));
            assertTrue(usersPage.isUserFound(userName), String.format("User %s was found", userName));
            return this;
        }

        public UserRowAction assertUserIsNotFound()
        {
            LOG.info(String.format("Assert user %s is found", userName));
            Assert.assertFalse(usersPage.isUserFound(userName), String.format("User %s was found", userName));
            return this;
        }

        public UserRowAction assertQuotaIs(String expectedValue)
        {
            LOG.info(String.format("Assert quota value is: %s", expectedValue));
            assertEquals(getUserRow().findElement(quotaElement).getText(), expectedValue,
                "Quota value is displayed");
            return this;
        }

        public UserProfileAdminToolsPage selectUserFullName()
        {
            getUserRow().findElement(userFullName).click();
            return (UserProfileAdminToolsPage) new UserProfileAdminToolsPage(browser).renderedPage();
        }

        public UserRowAction assertUserDeleteIconIsDisplayed()
        {
            assertTrue(getBrowser().isElementDisplayed(getUserRow().findElement(userDeletedIcon)),
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
