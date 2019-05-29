package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.ArrayList;
import java.util.List;

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
    @FindAll (@FindBy (css = "span[id*='default_group']"))
    protected List<WebElement> addedGroupsList;
    @Autowired
    AdminToolsPage adminToolsPage;
    @Autowired
    private Toolbar toolbar;
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

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users#state=panel%3Dcreate";

    }

    public boolean areAllElementsFromInfoSectionDisplayed()
    {

        if (!firstNameInputField.isDisplayed())
            return false;
        if (!lastNameInputField.isDisplayed())
            return false;
        if (!emailInputField.isDisplayed())
            return false;
        return true;

    }

    public boolean areAllElementsFromAboutUserSectionDisplayed()
    {

        if (!usernameInputField.isDisplayed())
            return false;
        if (!passwordInputField.isDisplayed())
            return false;
        if (!verifypasswordInputField.isDisplayed())
            return false;
        if (!groupFinderInputField.isDisplayed())
            return false;
        if (!quotaInputField.isDisplayed())
            return false;
        if (!quotaType.isDisplayed())
            return false;
        if (!searchButton.isDisplayed())
            return false;
        return searchForGroupsMessage.isDisplayed();

    }

    public boolean areAllButtonsDisplayed()
    {

        if (!createUserButton.isDisplayed())
            return false;
        if (!createUserAndStartAnotherButton.isDisplayed())
            return false;
        return cancelButton.isDisplayed();

    }

    public void setFirstName(String firstName)
    {
        firstNameInputField.clear();
        firstNameInputField.sendKeys(firstName);
    }

    public void setLastName(String lastName)
    {
        lastNameInputField.clear();
        lastNameInputField.sendKeys(lastName);
    }

    public void setEmail(String email)
    {
        emailInputField.clear();
        emailInputField.sendKeys(email);
    }

    public void setUsrName(String userName)
    {
        usernameInputField.clear();
        usernameInputField.sendKeys(userName);
    }

    public void setPassword(String password)
    {
        passwordInputField.clear();
        passwordInputField.sendKeys(password);
    }

    public void setVerifyPassword(String verifyPassword)
    {
        verifypasswordInputField.clear();
        verifypasswordInputField.sendKeys(verifyPassword);
    }

    public void setQuota(String quotaValue)
    {

        quotaInputField.clear();
        quotaInputField.sendKeys(quotaValue);
    }

    public void addUserToGroup(String group)
    {

        {

            List<WebElement> searchRows = new ArrayList<>();
            groupFinderInputField.clear();
            groupFinderInputField.sendKeys(group);

            searchButton.click();
            browser.waitInSeconds(3);
            By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
            for (WebElement element : browser.findElements(DATA_ROWS))
            {
                searchRows.add(element);
            }
            for (WebElement searchRow : searchRows)
                if (searchRow.getText().contains(group))
                    searchRow.findElement(By.cssSelector("span[class$='button'] span button")).click();
        }

    }

    /**
     * Retrieves the user that matches the text from the search box
     *
     * @param user String
     */

    public void removeGroup(final String group)

    {

        browser.findFirstElementWithValue(addedGroupsList, group).click();

    }

    public boolean isGroupAdded(final String group)

    {
        return browser.findFirstElementWithValue(addedGroupsList, group) != null;
    }

    public void clickCreateButton()
    {
        browser.waitUntilElementVisible(createUserButton);
        createUserButton.click();

    }

    public void clickCreateUserAndStartAnotherButton()
    {
        createUserAndStartAnotherButton.click();

    }

    public void clickCancelButton()
    {

        cancelButton.click();

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

        return firstName.length() == 0 && lastName.length() == 0 && email.length() == 0 && password.length() == 0 && verifyPassword.length() == 0
            && groupFinder.length() == 0 && quota.length() == 0;

    }

    public String getPasswordsDontMatchNotificationText()

    {
        browser.waitUntilElementVisible(passwordsDontMatchNotification);
        return passwordsDontMatchNotification.getText();
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

}
