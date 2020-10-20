package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.Select;

@PageObject
public class CreateUserPage extends SharePage<CreateUserPage>
{
    public static final String URL = "share/page/console/admin-console/users#state=panel%3Dcreate";

    @FindBy (css = ".create-main .yui-dt-empty div")
    private WebElement searchForGroupsMessage;

    @RenderWebElement
    @FindBy (css = "input[id$='firstname']")
    private WebElement firstNameInputField;

    @FindBy (css = "input[id$='lastname']")
    private WebElement lastNameInputField;

    @FindBy (css = "input[id$='email']")
    private WebElement emailInputField;

    @FindBy (css = "input[id$='default-create-username']")
    private WebElement usernameInputField;

    @FindBy (css = "input[id$='password']")
    private WebElement passwordInputField;

    @FindBy (css = "input[id$='verifypassword']")
    private WebElement verifypasswordInputField;

    @FindBy (css = "input[id$='groupfinder-search-text']")
    private WebElement groupFinderInputField;

    @FindBy (css = "button[id*='groupfinder']")
    private WebElement searchButton;

    @FindBy (css = "input[id$='quota']")
    private WebElement quotaInputField;

    @FindBy (css = "select[id*='quotatype']")
    private Select quotaType;

    @FindBy (css = "input[id*='disableaccount']")
    private WebElement disableAccountCheckBox;

    @RenderWebElement
    @FindBy (css = "button[id$='createuser-ok-button-button']")
    private WebElement createUserButton;

    @FindBy (css = "button[id$='createuser-another-button-button']")
    private WebElement createUserAndStartAnotherButton;

    @FindBy (css = ".createuser-cancel-button button")
    private WebElement cancelButton;

    @FindBy (css = "div[id='prompt']")
    private WebElement duplicateUserPrompt;

    @FindBy (css = "div[id='prompt'] button")
    private WebElement duplicateUserPromptButton;

    private By groupRows = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
    private String addedGroup = "//div[@class='groupselection-row']//span[text()='%s']";

    @Autowired
    AdminToolsPage adminToolsPage;

    @Autowired
    private UsersPage usersPage;

    @Override
    public String getRelativePath()
    {
        return URL;
    }

    public CreateUserPage assertCreateUserPageIsOpened()
    {
        Assert.assertTrue(browser.isElementDisplayed(firstNameInputField), "Create user page is opened");
        return this;
    }

    public CreateUserPage assertAllInputsFromInfoSectionAreDisplayed()
    {
        LOG.info("Assert all input fields from Info Section are displayed");
        Assert.assertTrue(browser.isElementDisplayed(firstNameInputField), "First name is displayed");
        Assert.assertTrue(browser.isElementDisplayed(lastNameInputField), "Last name is displayed");
        Assert.assertTrue(browser.isElementDisplayed(emailInputField), "Email is displayed");
        return this;
    }

    public CreateUserPage assertAllElementsFromAboutUserAreDisplayed()
    {
        LOG.info("Assert all elements from About User are displayed");
        Assert.assertTrue(browser.isElementDisplayed(usernameInputField), "User name is displayed");
        Assert.assertTrue(browser.isElementDisplayed(passwordInputField), "Password is displayed");
        Assert.assertTrue(browser.isElementDisplayed(verifypasswordInputField), "Verify password is displayed");
        Assert.assertTrue(browser.isElementDisplayed(groupFinderInputField), "Group finder is displayed");
        Assert.assertTrue(browser.isElementDisplayed(quotaInputField), "Quota finder is displayed");
        Assert.assertTrue(browser.isElementDisplayed(quotaType), "Quota type is displayed");
        Assert.assertTrue(browser.isElementDisplayed(searchButton), "Search type is displayed");
        Assert.assertEquals(searchForGroupsMessage.getText(), language.translate("adminTools.user.searchGroupsMessage"));
        return this;
    }

    public CreateUserPage assertAllButtonsAreDisplayed()
    {
        LOG.info("Assert all buttons are displayed");
        Assert.assertTrue(browser.isElementDisplayed(createUserButton), "Create button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(createUserAndStartAnotherButton),
            "Create and Start another button is displayed");
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public CreateUserPage setFirstName(String firstName)
    {
        Utils.clearAndType(firstNameInputField, firstName);
        return this;
    }

    public CreateUserPage setLastName(String lastName)
    {
        Utils.clearAndType(lastNameInputField, lastName);
        return this;
    }

    public CreateUserPage setEmail(String email)
    {
        Utils.clearAndType(emailInputField, email);
        return this;
    }

    public CreateUserPage setUsername(String userName)
    {
        Utils.clearAndType(usernameInputField, userName);
        return this;
    }

    public CreateUserPage setPassword(String password)
    {
        Utils.clearAndType(passwordInputField, password);
        return this;
    }

    public CreateUserPage setVerifyPassword(String verifyPassword)
    {
        Utils.clearAndType(verifypasswordInputField, verifyPassword);
        return this;
    }

    public CreateUserPage setQuota(String quotaValue)
    {
        Utils.clearAndType(quotaInputField, quotaValue);
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
        Utils.clearAndType(groupFinderInputField, group.getGroupIdentifier());

        searchButton.click();
        List<WebElement> searchRows = browser.waitUntilElementsVisible(groupRows);
        searchRows.stream()
            .filter(searchRow -> searchRow.getText().contains(group.getGroupIdentifier()))
            .forEach(searchRow -> browser.waitUntilChildElementIsPresent(searchRow,
                By.cssSelector("span[class$='button'] span button")).click());
        browser.waitUntilElementVisible(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        return this;
    }

    public CreateUserPage removeGroup(GroupModel group)
    {
        LOG.info(String.format("Remove group %s", group.getGroupIdentifier()));
        WebElement groupToRemove = browser.findElement(By.xpath(String.format(addedGroup, group.getGroupIdentifier())));
        groupToRemove.click();
        browser.waitUntilElementDisappears(groupToRemove);
        return this;
    }

    public CreateUserPage assertGroupIsAdded(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is added", group.getGroupIdentifier()));
        Assert.assertTrue(browser.isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public CreateUserPage assertGroupIsNotAdded(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is not added", group.getGroupIdentifier()));
        Assert.assertFalse(browser.isElementDisplayed(By.xpath(String.format(addedGroup, group.getGroupIdentifier()))),
            String.format("Group %s is added", group.getGroupIdentifier()));
        return this;
    }

    public UsersPage clickCreate()
    {
        LOG.info("Click Create");
        browser.waitUntilElementClickable(createUserButton).click();
        waitUntilNotificationMessageDisappears();
        return (UsersPage) usersPage.renderedPage();
    }

    public CreateUserPage clickCreateButtonAndExpectFailure()
    {
        browser.waitUntilElementClickable(createUserButton).click();
        waitUntilNotificationMessageDisappears();
        return (CreateUserPage) this.renderedPage();
    }

    public CreateUserPage clickCreateUserAndStartAnother()
    {
        LOG.info("Click Create User And Start Another");
        browser.waitUntilElementClickable(createUserAndStartAnotherButton).click();
        waitUntilNotificationMessageDisappears();
        return (CreateUserPage) this.renderedPage();
    }

    public UsersPage clickCancel()
    {
        cancelButton.click();
        return (UsersPage) usersPage.renderedPage();
    }

    public CreateUserPage assertAllFieldsAreEmpty()
    {
        Assert.assertTrue(firstNameInputField.getAttribute("value").isEmpty(), "First Name field is empty");
        Assert.assertTrue(lastNameInputField.getAttribute("value").isEmpty(), "Last Name field is empty");
        Assert.assertTrue(emailInputField.getAttribute("value").isEmpty(), "Email field is empty");
        Assert.assertTrue(passwordInputField.getAttribute("value").isEmpty(), "Password field is empty");
        Assert.assertTrue(verifypasswordInputField.getAttribute("value").isEmpty(), "Verify password field is empty");
        Assert.assertTrue(groupFinderInputField.getAttribute("value").isEmpty(), "Group field is empty");
        Assert.assertTrue(quotaInputField.getAttribute("value").isEmpty(), "Quota field is empty");
        return this;
    }

    public CreateUserPage assertPasswordDoesntMatchNotificationIsDisplayed()
    {
        Assert.assertEquals(LAST_MODIFICATION_MESSAGE, language.translate("adminTools.user.passwordMatchNotification"),
            "Password doesn't match notification is displayed");
        return this;
    }

    public CreateUserPage checkDisableAccount()
    {
        disableAccountCheckBox.click();
        return this;
    }

    public CreateUserPage assertSelectedQuotaTypeIs(String expectedQuotaType)
    {
        Assert.assertEquals(quotaType.getFirstSelectedOption().getText(), expectedQuotaType);
        return this;
    }

    public boolean isDuplicateUserPromptDisplayed()
    {
        return browser.isElementDisplayed(duplicateUserPrompt);
    }

    public void clickDuplicateUserPromptButton()
    {
        duplicateUserPromptButton.click();
    }
}
