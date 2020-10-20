package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.FileInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@PageObject
public class UsersPage extends AdminToolsPage
{
    @Autowired
    private CreateUserPage createUsers;

    @Autowired
    private UploadResults uploadResults;

    @Autowired
    private UserProfileAdminToolsPage userProfileAdminToolsPage;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-newuser-button-button']")
    private WebElement newUserButton;

    @RenderWebElement
    @FindBy (css = "button[id*='uploadusers']")
    private WebElement uploadUsersButton;

    @FindBy (css = "input[id*='search-text']")
    private WebElement userSearchInputField;

    @FindBy (css = "button[id*='search']")
    private WebElement searchButton;

    @FindBy (css = "input[id*='default-filedata-file']")
    private FileInput fileInput;

    @FindBy (css = "#template_x002e_html-upload_x002e_console_x0023_default-upload-button-button")
    private WebElement uploadButton;

    @FindAll (@FindBy (css = "div[class$='results'] table>thead>tr:nth-of-type(1) span"))
    private List<WebElement> tableHeaderElements;

    @FindBy (css = "div[id$='default-search-bar']")
    private FileInput searchTextResult;

    private String userRow = "//td[contains(@headers, 'userName')]//div[text()='%s']/../..";

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users";
    }

    public CreateUserPage clickNewUser()
    {
        LOG.info("Click New User");
        browser.waitUntilElementClickable(newUserButton).click();
        return (CreateUserPage) createUsers.renderedPage();
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

    private UsersPage searchUser(String searchKeyword, String userToWaitFor)
    {
        userSearchInputField.clear();
        userSearchInputField.sendKeys(searchKeyword);
        searchButton.click();
        int counter = 0;
        boolean found = isUserFound(userToWaitFor);
        while (!found && counter <= 5)
        {
            Utility.waitToLoopTime(1, String.format("Wait for user to be displayed: %s", userToWaitFor));
            searchButton.click();
            found = isUserFound(userToWaitFor);
            counter++;
        }
        return this;
    }

    public UsersPage assertSearchTextIsCorrect(String searchText, int results)
    {
        String expectedValue = String.format(language.translate("adminTools.user.search.text"), searchText, results);
        browser.waitUntilElementContainsText(searchTextResult, expectedValue);
        Assert.assertEquals(searchTextResult.getText(), expectedValue);
        return this;
    }

    public UsersPage searchUserWithRetry(UserModel user)
    {
        return searchUserWithRetry(user.getUsername());
    }

    public UsersPage assertSuccessfullyCreatedNewUserNotificationIsDisplayed()
    {
        Assert.assertEquals(LAST_MODIFICATION_MESSAGE, language.translate("adminTools.users.createUserNotification"),
            "Create user notification is displayed");
        return this;
    }

    public UsersPage searchUser(String user)
    {
        LOG.info(String.format("Search for user: %s", user));
        userSearchInputField.clear();
        userSearchInputField.sendKeys(user);
        searchButton.click();
        return this;
    }

    /**
     * Method to check whether the user is found
     *
     * @param user String
     * @return true if the user is found, else false
     */
    public boolean isUserFound(String user)
    {
        return browser.isElementDisplayed(By.xpath(String.format(userRow, user)));
    }

    public WebElement getUserRow(String userName)
    {
        return browser.waitUntilElementVisible(By.xpath(String.format(userRow, userName)));
    }

    public UsersPage assertSearchInputIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(userSearchInputField), "Search input is displayed");
        return this;
    }

    public UsersPage assertSearchButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search button is displayed");
        return this;
    }

    public UsersPage assertNewUserButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(newUserButton), "New User button is displayed");
        return this;
    }

    public UsersPage assertImportUsersButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(uploadUsersButton), "Upload users button is displayed");
        return this;
    }

    public UploadResults uploadUsers(String filePath)
    {
        if (env.getProperty("grid.enabled").equals("true"))
        {
            ((RemoteWebDriver)(browser.getWrappedDriver())).setFileDetector(new LocalFileDetector());
        }
        uploadUsersButton.click();
        browser.waitUntilElementVisible(fileInput).sendKeys(filePath);
        uploadButton.click();
        return (UploadResults) uploadResults.renderedPage();
    }

    public UsersPage assertDeleteUserNotificationIsDisplayed()
    {
        Assert.assertEquals(LAST_MODIFICATION_MESSAGE, language.translate("adminTools.user.deleteUser.notification"));
        return this;
    }

    public UsersPage assertAllTableHeadersAreDisplayed()
    {
        List<String> tableHeaders = browser.getTextFromElementList(tableHeaderElements);
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
        Assert.assertTrue(tableHeaders.containsAll(expectedTableHeaders), "All table headers are displayed");
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
            Assert.assertTrue(getBrowser().isElementDisplayed(getUserRow().findElement(userDisableIcon)),
                "User disabled icon is displayed");
            return this;
        }

        public UserRowAction assertUserIsFound()
        {
            LOG.info(String.format("Assert user %s is found", userName));
            Assert.assertTrue(usersPage.isUserFound(userName), String.format("User %s was found", userName));
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
            Assert.assertEquals(getUserRow().findElement(quotaElement).getText(), expectedValue,
                "Quota value is displayed");
            return this;
        }

        public UserProfileAdminToolsPage selectUserFullName()
        {
            getUserRow().findElement(userFullName).click();
            return (UserProfileAdminToolsPage) userProfileAdminToolsPage.renderedPage();
        }

        public UserRowAction assertUserDeleteIconIsDisplayed()
        {
            Assert.assertTrue(getBrowser().isElementDisplayed(getUserRow().findElement(userDeletedIcon)),
                "User delete icon is displayed");
            return this;
        }

        public UserRowAction assertDeletedIsDisplayed()
        {
            Assert.assertEquals(getUserRow().findElement(deleted).getText(), language.translate("adminTools.user.deleted"));
            return this;
        }
    }
}
