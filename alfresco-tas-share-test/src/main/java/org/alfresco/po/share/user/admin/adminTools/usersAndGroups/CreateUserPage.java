package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class CreateUserPage extends SharePage2<CreateUserPage>
{
    public static final String URL = "share/page/console/admin-console/users#state=panel%3Dcreate";

    private By searchForGroupsMessage = By.cssSelector(".create-main .yui-dt-empty div");

    @RenderWebElement
    private By firstNameInputField = By.cssSelector("input[id$='firstname']");
    private By lastNameInputField = By.cssSelector("input[id$='lastname']");
    private By emailInputField = By.cssSelector("input[id$='email']");
    private By usernameInputField = By.cssSelector("input[id$='default-create-username']");
    private By passwordInputField = By.cssSelector("input[id$='password']");
    private By verifyPasswordInputField = By.cssSelector("input[id$='verifypassword']");
    private By groupFinderInputField = By.cssSelector("input[id$='groupfinder-search-text']");
    private By searchButton = By.cssSelector("button[id*='groupfinder']");
    private By quotaInputField = By.cssSelector("input[id$='quota']");
    private By quotaType = By.cssSelector("select[id*='quotatype']");
    private By disableAccountCheckBox = By.cssSelector("input[id*='disableaccount']");
    @RenderWebElement
    private By createUserButton = By.cssSelector("button[id$='createuser-ok-button-button']");
    private By createUserAndStartAnotherButton = By.cssSelector("button[id$='createuser-another-button-button']");
    private By cancelButton = By.cssSelector(".createuser-cancel-button button");
    private By duplicateUserPrompt = By.cssSelector("div[id='prompt']");
    private By duplicateUserPromptButton = By.cssSelector("div[id='prompt'] button");
    private By groupRows = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
    private String addedGroup = "//div[@class='groupselection-row']//span[text()='%s']";

    public CreateUserPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    @Override
    public String getRelativePath()
    {
        return URL;
    }

    public CreateUserPage assertCreateUserPageIsOpened()
    {
        assertTrue(getBrowser().isElementDisplayed(firstNameInputField), "Create user page is opened");
        return this;
    }

    public CreateUserPage assertAllInputsFromInfoSectionAreDisplayed()
    {
        LOG.info("Assert all input fields from Info Section are displayed");
        assertTrue(getBrowser().isElementDisplayed(firstNameInputField), "First name is displayed");
        assertTrue(getBrowser().isElementDisplayed(lastNameInputField), "Last name is displayed");
        assertTrue(getBrowser().isElementDisplayed(emailInputField), "Email is displayed");

        return this;
    }

    public CreateUserPage assertAllElementsFromAboutUserAreDisplayed()
    {
        LOG.info("Assert all elements from About User are displayed");
        assertTrue(getBrowser().isElementDisplayed(usernameInputField), "User name is displayed");
        assertTrue(getBrowser().isElementDisplayed(passwordInputField), "Password is displayed");
        assertTrue(getBrowser().isElementDisplayed(verifyPasswordInputField), "Verify password is displayed");
        assertTrue(getBrowser().isElementDisplayed(groupFinderInputField), "Group finder is displayed");
        assertTrue(getBrowser().isElementDisplayed(quotaInputField), "Quota finder is displayed");
        assertTrue(getBrowser().isElementDisplayed(quotaType), "Quota type is displayed");
        assertTrue(getBrowser().isElementDisplayed(searchButton), "Search type is displayed");
        assertEquals(getBrowser().findElement(searchForGroupsMessage).getText(), language.translate("adminTools.user.searchGroupsMessage"));

        return this;
    }

    public CreateUserPage assertAllButtonsAreDisplayed()
    {
        LOG.info("Assert all buttons are displayed");
        assertTrue(getBrowser().isElementDisplayed(createUserButton), "Create button is displayed");
        assertTrue(getBrowser().isElementDisplayed(createUserAndStartAnotherButton),
            "Create and Start another button is displayed");
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");

        return this;
    }

    public CreateUserPage setFirstName(String firstName)
    {
        clearAndType(firstNameInputField, firstName);
        return this;
    }

    public CreateUserPage setLastName(String lastName)
    {
        clearAndType(lastNameInputField, lastName);
        return this;
    }

    public CreateUserPage setEmail(String email)
    {
        clearAndType(emailInputField, email);
        return this;
    }

    public CreateUserPage setUsername(String userName)
    {
        clearAndType(usernameInputField, userName);
        return this;
    }

    public CreateUserPage setPassword(String password)
    {
        clearAndType(passwordInputField, password);
        return this;
    }

    public CreateUserPage setVerifyPassword(String verifyPassword)
    {
        clearAndType(verifyPasswordInputField, verifyPassword);
        return this;
    }

    public CreateUserPage setQuota(String quotaValue)
    {
        clearAndType(quotaInputField, quotaValue);
        return this;
    }

    public CreateUserPage typeMandatoryFields(UserModel user)
    {
        this.setFirstName(user.getFirstName())
            .setLastName(user.getLastName())
            .setEmail(user.getEmailAddress())
            .setUsername(user.getUsername())
            .setPassword(user.getPassword())
            .setVerifyPassword(user.getPassword());
        return this;
    }

    public CreateUserPage addUserToGroup(GroupModel group)
    {
        clearAndType(groupFinderInputField, group.getGroupIdentifier());

        clickElement(searchButton);
        List<WebElement> searchRows = getBrowser().waitUntilElementsVisible(groupRows);
        searchRows.stream()
            .filter(searchRow -> searchRow.getText().contains(group.getGroupIdentifier()))
            .forEach(searchRow -> getBrowser().waitUntilChildElementIsPresent(searchRow,
                By.cssSelector("span[class$='button'] span button")).click());
        getBrowser().waitUntilElementVisible(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        return this;
    }

    public CreateUserPage removeGroup(GroupModel group)
    {
        LOG.info(String.format("Remove group %s", group.getGroupIdentifier()));
        WebElement groupToRemove = getBrowser().findElement(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        groupToRemove.click();
        getBrowser().waitUntilElementDisappears(groupToRemove);
        return this;
    }

    public CreateUserPage assertGroupIsAdded(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is added", group.getGroupIdentifier()));
        assertTrue(getBrowser().isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public CreateUserPage assertGroupIsNotAdded(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is not added", group.getGroupIdentifier()));
        assertFalse(getBrowser().isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public UsersPage clickCreate()
    {
        LOG.info("Click Create");
        getBrowser().waitUntilElementClickable(createUserButton).click();
        waitUntilNotificationMessageDisappears();
        return (UsersPage) new UsersPage(browser).renderedPage();
    }

    public CreateUserPage clickCreateButtonAndExpectFailure()
    {
        getBrowser().waitUntilElementClickable(createUserButton).click();
        waitUntilNotificationMessageDisappears();
        return (CreateUserPage) this.renderedPage();
    }

    public CreateUserPage clickCreateUserAndStartAnother()
    {
        LOG.info("Click Create User And Start Another");
        getBrowser().waitUntilElementClickable(createUserAndStartAnotherButton).click();
        waitUntilNotificationMessageDisappears();
        return (CreateUserPage) this.renderedPage();
    }

    public UsersPage clickCancel()
    {
        clickElement(cancelButton);
        return (UsersPage) new UsersPage(browser).renderedPage();
    }

    public CreateUserPage assertAllFieldsAreEmpty()
    {
        LOG.info("Assert all input fields are empty");
        assertTrue(getBrowser().findElement(firstNameInputField).getAttribute("value").isEmpty(), "First Name field is empty");
        assertTrue(getBrowser().findElement(lastNameInputField).getAttribute("value").isEmpty(), "Last Name field is empty");
        assertTrue(getBrowser().findElement(emailInputField).getAttribute("value").isEmpty(), "Email field is empty");
        assertTrue(getBrowser().findElement(passwordInputField).getAttribute("value").isEmpty(), "Password field is empty");
        assertTrue(getBrowser().findElement(verifyPasswordInputField).getAttribute("value").isEmpty(), "Verify password field is empty");
        assertTrue(getBrowser().findElement(groupFinderInputField).getAttribute("value").isEmpty(), "Group field is empty");
        assertTrue(getBrowser().findElement(quotaInputField).getAttribute("value").isEmpty(), "Quota field is empty");

        return this;
    }

    public CreateUserPage assertPasswordDoesntMatchNotificationIsDisplayed()
    {
        assertEquals(LAST_MODIFICATION_MESSAGE, language.translate("adminTools.user.passwordMatchNotification"),
            "Password doesn't match notification is displayed");
        return this;
    }

    public CreateUserPage checkDisableAccount()
    {
        clickElement(disableAccountCheckBox);
        return this;
    }

    public CreateUserPage assertSelectedQuotaTypeIs(String expectedQuotaType)
    {
        Select quota = new Select(getBrowser().findElement(quotaType));
        assertEquals(quota.getFirstSelectedOption().getText(), expectedQuotaType);
        return this;
    }

    public boolean isDuplicateUserPromptDisplayed()
    {
        return getBrowser().isElementDisplayed(duplicateUserPrompt);
    }

    public void clickDuplicateUserPromptButton()
    {
        clickElement(duplicateUserPromptButton);
    }
}
