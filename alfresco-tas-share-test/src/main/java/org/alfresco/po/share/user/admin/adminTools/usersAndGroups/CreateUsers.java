package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.common.Utils;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Select;

@PageObject
public class CreateUsers extends SharePage<CreateUsers>
{
    public static final String URL = "share/page/console/admin-console/users#state=panel%3Dcreate";

    @FindAll (@FindBy (css = "span[id*='default_group']"))
    protected List<WebElement> addedGroupsList;
    @RenderWebElement
    @FindBy (xpath = "//div[contains(text(),'New User')]")
    private WebElement newUserLabel;
    @FindBy (xpath = "//div[contains(@class, 'yui-dt-liner') and text()='Enter a search term to find groups']")
    private WebElement searchForGroupsMessage;
    @FindBy (css = "input[id*='firstname']")
    private WebElement firstNameInputField;
    @FindBy (css = "input[id*='lastname']")
    private WebElement lastNameInputField;
    @FindBy (css = "input[id*='email']")
    private WebElement emailInputField;
    @FindBy (css = "input[id*='default-create-username']")
    private WebElement usernameInputField;
    @FindBy (css = "input[id*='password']")
    private WebElement passwordInputField;
    @FindBy (css = "input[id*='verifypassword']")
    private WebElement verifypasswordInputField;
    @FindBy (css = "input[id*='groupfinder']")
    private WebElement groupFinderInputField;
    @FindBy (css = "button[id*='groupfinder']")
    private WebElement searchButton;
    @FindBy (css = "input[id*='quota']")
    private WebElement quotaInputField;
    @FindBy (css = "select[id*='quotatype']")
    private Select quotaType;
    @FindBy (css = "input[id*='disableaccount']")
    private WebElement dissableAccountCheckBox;
    @RenderWebElement
    @FindBy (xpath = "//button[contains(text(),'Create User')]")
    private WebElement createUserButton;
    @FindBy (css = "button[id*='another']")
    private WebElement createUserAndStartAnotherButton;
    @FindBy (css = "button[id='page_x002e_ctool_x002e_admin-console_x0023_default-createuser-cancel-button-button']")
    private WebElement cancelButton;
    @FindBy (xpath = ".//*[@id='message']/div/span")
    private WebElement passwordsDontMatchNotification;
    @FindBy (css = "div[id='prompt']")
    private WebElement duplicateUserPrompt;
    @FindBy (css = "div[id='prompt'] button")
    private WebElement duplicateUserPromptButton;

    @Autowired
    AdminToolsPage adminToolsPage;
    @Autowired
    private Toolbar toolbar;
    @Autowired
    private UsersPage usersPage;

    @Override
    public String getRelativePath()
    {
        return URL;
    }

    public boolean areAllElementsFromInfoSectionDisplayed()
    {
        return browser.isElementDisplayed(firstNameInputField) &&
                browser.isElementDisplayed(lastNameInputField) &&
                browser.isElementDisplayed(emailInputField);
    }

    public boolean areAllElementsFromAboutUserSectionDisplayed()
    {
        return browser.isElementDisplayed(usernameInputField) &&
                browser.isElementDisplayed(passwordInputField) &&
                browser.isElementDisplayed(verifypasswordInputField) &&
                browser.isElementDisplayed(groupFinderInputField) &&
                browser.isElementDisplayed(quotaInputField) &&
                browser.isElementDisplayed(quotaType) &&
                browser.isElementDisplayed(searchButton) &&
                browser.isElementDisplayed(searchForGroupsMessage);
    }

    public boolean areAllButtonsDisplayed()
    {
        return browser.isElementDisplayed(createUserButton) &&
                browser.isElementDisplayed(createUserAndStartAnotherButton) &&
                browser.isElementDisplayed(cancelButton);
    }

    public void setFirstName(String firstName)
    {
        Utils.clearAndType(firstNameInputField, firstName);
    }

    public void setLastName(String lastName)
    {
        Utils.clearAndType(lastNameInputField, lastName);
    }

    public void setEmail(String email)
    {
        Utils.clearAndType(emailInputField, email);
    }

    public void setUsername(String userName)
    {
        Utils.clearAndType(usernameInputField, userName);
    }

    public void setPassword(String password)
    {
        Utils.clearAndType(passwordInputField, password);
    }

    public void setVerifyPassword(String verifyPassword)
    {
        Utils.clearAndType(verifypasswordInputField, verifyPassword);
    }

    public void setQuota(String quotaValue)
    {
        Utils.clearAndType(quotaInputField, quotaValue);
    }

    public CreateUsers addUserToGroup(String group)
    {
        Utils.clearAndType(groupFinderInputField, group);

        searchButton.click();
        By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
        List<WebElement> searchRows = browser.waitUntilElementsVisible(DATA_ROWS);
        searchRows.stream()
                  .filter(searchRow -> searchRow.getText().contains(group))
                  .forEach(searchRow -> browser.waitUntilChildElementIsPresent(searchRow, By.cssSelector("span[class$='button'] span button"))
                                               .click());
        return (CreateUsers) this.renderedPage();
    }

    /**
     * Retrieves the user that matches the text from the search box
     *
     * @param group String
     */
    public CreateUsers removeGroup(final String group)
    {
        browser.findFirstElementWithValue(addedGroupsList, group).click();
        return (CreateUsers) this.renderedPage();
    }

    public boolean isGroupAdded(final String group)
    {
        return browser.findFirstElementWithValue(addedGroupsList, group) != null;
    }

    public UsersPage clickCreateButton()
    {
        browser.waitUntilElementClickable(createUserButton).click();
        waitUntilMessageDisappears();
        return (UsersPage) usersPage.renderedPage();
    }

    public CreateUsers clickCreateButtonAndExpectFailure()
    {
        browser.waitUntilElementClickable(createUserButton).click();
        return (CreateUsers) this.renderedPage();
    }

    public CreateUsers clickCreateUserAndStartAnotherButton()
    {
        browser.waitUntilElementClickable(createUserAndStartAnotherButton).click();
        return (CreateUsers) this.renderedPage();
    }

    public UsersPage clickCancelButton()
    {
        cancelButton.click();
        return (UsersPage) usersPage.renderedPage();
    }

    public boolean areAllFieldsClear()
    {
        String firstName = firstNameInputField.getAttribute("value");
        String lastName = lastNameInputField.getAttribute("value");
        String email = emailInputField.getAttribute("value");
        String password = passwordInputField.getAttribute("value");
        String verifyPassword = verifypasswordInputField.getAttribute("value");
        String groupFinder = groupFinderInputField.getAttribute("value");
        String quota = quotaInputField.getAttribute("value");

        return firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && password.isEmpty() &&
                verifyPassword.isEmpty() && groupFinder.isEmpty() && quota.isEmpty();
    }

    public String getPasswordsDontMatchNotificationText()
    {
        return browser.waitUntilElementVisible(passwordsDontMatchNotification).getText();
    }

    public void checkDisableAccount()
    {
        dissableAccountCheckBox.click();
    }

    public String getValueForQuotaType()
    {
        WebElement defaultQuotaType = quotaType.getFirstSelectedOption();
        return defaultQuotaType.getText();
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
