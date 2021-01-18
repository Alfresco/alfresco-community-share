package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CreateUserPage extends SharePage2<CreateUserPage>
{
    private final By searchForGroupsMessage = By.cssSelector(".create-main .yui-dt-empty div");
    private final By firstNameInputField = By.cssSelector("input[id$='firstname']");
    private final By lastNameInputField = By.cssSelector("input[id$='lastname']");
    private final By emailInputField = By.cssSelector("input[id$='email']");
    private final By usernameInputField = By.cssSelector("input[id$='default-create-username']");
    private final By passwordInputField = By.cssSelector("input[id$='password']");
    private final By verifyPasswordInputField = By.cssSelector("input[id$='verifypassword']");
    private final By groupFinderInputField = By.cssSelector("input[id$='groupfinder-search-text']");
    private final By searchButton = By.cssSelector("button[id*='groupfinder']");
    private final By quotaInputField = By.cssSelector("input[id$='quota']");
    private final By quotaType = By.cssSelector("select[id*='quotatype']");
    private final By disableAccountCheckBox = By.cssSelector("input[id*='disableaccount']");
    private final By createUserButton = By.cssSelector("button[id$='createuser-ok-button-button']");
    private final By createUserAndStartAnotherButton = By.cssSelector("button[id$='createuser-another-button-button']");
    private final By cancelButton = By.cssSelector(".createuser-cancel-button button");
    private final By groupRows = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
    private final String addedGroup = "//div[@class='groupselection-row']//span[text()='%s']";

    public CreateUserPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users#state=panel%3Dcreate";
    }

    public CreateUserPage assertCreateUserPageIsOpened()
    {
        webElementInteraction.waitUntilElementIsVisible(firstNameInputField);
        assertTrue(webElementInteraction.isElementDisplayed(firstNameInputField), "Create user page is opened");
        return this;
    }

    public CreateUserPage assertAllInputsFromInfoSectionAreDisplayed()
    {
        LOG.info("Assert all input fields from Info Section are displayed");
        webElementInteraction.waitUntilElementIsVisible(firstNameInputField);
        assertTrue(webElementInteraction.isElementDisplayed(firstNameInputField), "First name is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(lastNameInputField), "Last name is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(emailInputField), "Email is displayed");

        return this;
    }

    public CreateUserPage assertAllElementsFromAboutUserAreDisplayed()
    {
        LOG.info("Assert all elements from About User are displayed");
        webElementInteraction.waitUntilElementIsVisible(usernameInputField);
        assertTrue(webElementInteraction.isElementDisplayed(usernameInputField), "User name is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(passwordInputField), "Password is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(verifyPasswordInputField), "Verify password is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(groupFinderInputField), "Group finder is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(quotaInputField), "Quota finder is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(quotaType), "Quota type is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(searchButton), "Search type is displayed");
        assertEquals(webElementInteraction.findElement(searchForGroupsMessage).getText(), language.translate("adminTools.user.searchGroupsMessage"));

        return this;
    }

    public CreateUserPage assertAllButtonsAreDisplayed()
    {
        LOG.info("Assert all buttons are displayed");
        webElementInteraction.waitUntilElementIsVisible(createUserButton);
        assertTrue(webElementInteraction.isElementDisplayed(createUserButton), "Create button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(createUserAndStartAnotherButton),
            "Create and Start another button is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is displayed");

        return this;
    }

    public CreateUserPage setFirstName(String firstName)
    {
        webElementInteraction.clearAndType(firstNameInputField, firstName);
        return this;
    }

    public CreateUserPage setLastName(String lastName)
    {
        webElementInteraction.clearAndType(lastNameInputField, lastName);
        return this;
    }

    public CreateUserPage setEmail(String email)
    {
        webElementInteraction.clearAndType(emailInputField, email);
        return this;
    }

    public CreateUserPage setUsername(String userName)
    {
        webElementInteraction.clearAndType(usernameInputField, userName);
        return this;
    }

    public CreateUserPage setPassword(String password)
    {
        webElementInteraction.clearAndType(passwordInputField, password);
        return this;
    }

    public CreateUserPage setVerifyPassword(String verifyPassword)
    {
        webElementInteraction.clearAndType(verifyPasswordInputField, verifyPassword);
        return this;
    }

    public CreateUserPage setQuota(String quotaValue)
    {
        webElementInteraction.clearAndType(quotaInputField, quotaValue);
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
        webElementInteraction.clearAndType(groupFinderInputField, group.getGroupIdentifier());
        webElementInteraction.clickElement(searchButton);
        List<WebElement> searchRows = webElementInteraction.waitUntilElementsAreVisible(groupRows);
        searchRows.stream()
            .filter(searchRow -> searchRow.getText().contains(group.getGroupIdentifier()))
            .forEach(searchRow -> webElementInteraction.waitUntilChildElementIsPresent(searchRow,
                By.cssSelector("span[class$='button'] span button")).click());
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        return this;
    }

    public CreateUserPage removeGroup(GroupModel group)
    {
        LOG.info("Remove group {}", group.getGroupIdentifier());
        WebElement groupToRemove = webElementInteraction.findElement(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        webElementInteraction.clickElement(groupToRemove);
        webElementInteraction.waitUntilElementDisappears(groupToRemove);
        return this;
    }

    public CreateUserPage assertGroupIsAdded(GroupModel group)
    {
        LOG.info("Assert group {} is added", group.getGroupIdentifier());
        assertTrue(webElementInteraction.isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public CreateUserPage assertGroupIsNotAdded(GroupModel group)
    {
        LOG.info("Assert group {} is not added", group.getGroupIdentifier());
        assertFalse(webElementInteraction.isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public UsersPage clickCreate()
    {
        LOG.info("Click Create");
        WebElement createButton = webElementInteraction.waitUntilElementIsVisible(createUserButton);
        webElementInteraction.mouseOver(createButton);
        webElementInteraction.clickElement(createButton);
        waitUntilNotificationMessageDisappears();
        return new UsersPage(webDriver);
    }

    public CreateUserPage clickCreateButtonAndExpectFailure()
    {
        webElementInteraction.clickElement(createUserButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public CreateUserPage clickCreateUserAndStartAnother()
    {
        LOG.info("Click Create User And Start Another");
        webElementInteraction.clickElement(createUserAndStartAnotherButton);
        waitUntilNotificationMessageDisappears();
        return this;
    }

    public UsersPage clickCancel()
    {
        webElementInteraction.clickElement(cancelButton);
        return new UsersPage(webDriver);
    }

    public CreateUserPage assertAllFieldsAreEmpty()
    {
        LOG.info("Assert all input fields are empty");
        assertTrue(webElementInteraction.findElement(firstNameInputField).getAttribute("value").isEmpty(), "First Name field is empty");
        assertTrue(webElementInteraction.findElement(lastNameInputField).getAttribute("value").isEmpty(), "Last Name field is empty");
        assertTrue(webElementInteraction.findElement(emailInputField).getAttribute("value").isEmpty(), "Email field is empty");
        assertTrue(webElementInteraction.findElement(passwordInputField).getAttribute("value").isEmpty(), "Password field is empty");
        assertTrue(webElementInteraction.findElement(verifyPasswordInputField).getAttribute("value").isEmpty(), "Verify password field is empty");
        assertTrue(webElementInteraction.findElement(groupFinderInputField).getAttribute("value").isEmpty(), "Group field is empty");
        assertTrue(webElementInteraction.findElement(quotaInputField).getAttribute("value").isEmpty(), "Quota field is empty");

        return this;
    }

    public CreateUserPage assertPasswordDoesntMatchNotificationIsDisplayed()
    {
        assertEquals(notificationMessageThread.get(), language.translate("adminTools.user.passwordMatchNotification"),
            "Password doesn't match notification is displayed");
        return this;
    }

    public CreateUserPage checkDisableAccount()
    {
        webElementInteraction.clickElement(disableAccountCheckBox);
        return this;
    }

    public CreateUserPage assertSelectedQuotaTypeIs(String expectedQuotaType)
    {
        Select quota = new Select(webElementInteraction.findElement(quotaType));
        assertEquals(quota.getFirstSelectedOption().getText(), expectedQuotaType);
        return this;
    }
}
