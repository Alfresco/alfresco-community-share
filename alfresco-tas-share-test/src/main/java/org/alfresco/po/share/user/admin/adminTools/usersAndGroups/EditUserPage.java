package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import static org.testng.Assert.*;

public class EditUserPage extends SharePage2<EditUserPage>
{
    private String userName;

    private By searchGroupButton = By.cssSelector("button[id$='_default-update-groupfinder-group-search-button-button']");
    private By useDefaultButton = By.cssSelector("button[id$='_default-updateuser-clearphoto-button-button']");
    @RenderWebElement
    private By saveChangesButton = By.cssSelector("button[id$='_default-updateuser-save-button-button']");
    private By cancelButton = By.cssSelector("button[id$='_default-updateuser-cancel-button-button']");
    private By userNameInEditUserPageTitle = By.cssSelector("span[id$='_default-update-title']");
    private By userProfileHeaderList = By.cssSelector("form[id$='_default-update-form'] div.header-bar");
    @RenderWebElement
    private By firstNameField = By.cssSelector("input[id$='_default-update-firstname']");
    private By lastNameField = By.cssSelector("input[id$='_default-update-lastname']");
    private By emailField = By.cssSelector("input[id$='_default-update-email']");
    private By quotaField = By.cssSelector("input[id$='_default-update-quota']");
    private By newPasswordField = By.cssSelector("input[id$='_default-update-password']");
    private By verifyPasswordField = By.cssSelector("input[id$='_default-update-verifypassword']");
    private By disableAccount = By.cssSelector("input[id$='_default-update-disableaccount']");
    private By photoField = By.cssSelector("img[id$='_default-update-photoimg']");
    private By groupSearchResults = By.cssSelector("h3[class='itemname']");
    private By groupsInputField = By.cssSelector("input[id$='_default-update-groupfinder-search-text']");
    private String genericAddToGroupButton = "//h3[@class='itemname' and text()='%s']/../../..//button";
    private String genericRemoveButton = "//div[contains(@id, 'default-update-groups')]//span[text()= '%s']";

    public EditUserPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String getRelativePath()
    {
        return "share/page/console/admin-console/users#state=panel%3Dupdate%26userid%3D" + getUserName() + "%26search%3D";
    }

    public EditUserPage navigate(String userName)
    {
        setUserName(userName);
        try
        {
            return navigate();
        }
        catch (TimeoutException e)
        {
            LOG.error("Retry navigate to Edit User Page");
            return navigate();
        }
    }

    public EditUserPage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    public EditUserPage assertSearchGroupButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(searchGroupButton), "Search group button is displayed");
        return this;
    }

    public EditUserPage assertUseDefaultButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(useDefaultButton), "Use default button is displayed");
        return this;
    }

    public EditUserPage assertSaveChangesButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(saveChangesButton), "Save button is displayed");
        return this;
    }

    public EditUserPage assertCancelButtonIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public EditUserPage assertUserFullNameIsDisplayedInTitle(UserModel user)
    {
        LOG.info("Assert User full name is displayed in title: {} {}", user.getFirstName(), user.getLastName());
        assertEquals(getBrowser().waitUntilElementVisible(userNameInEditUserPageTitle).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage assertAllSectionsAreDisplayed()
    {
        List<String> sectionTexts = getBrowser().getTextFromElementList(getBrowser().findElements(userProfileHeaderList));
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.info")),
            "Info section is displayed");
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.aboutUser")),
            "About User section is displayed");
        assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.photo")),
            "Photo section is displayed");
        return this;
    }

    public EditUserPage assertFirstNameFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(firstNameField), "First Name field is displayed");
        return this;
    }

    public EditUserPage editFirstName(String firstName)
    {
        WebElement firstNameElement = getBrowser().waitUntilElementVisible(firstNameField);
        clearAndType(firstNameElement, firstName);
        return this;
    }

    public EditUserPage assertLastNameFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(lastNameField), "Last Name field is displayed");
        return this;
    }

    public EditUserPage editLastNameField(String lastName)
    {
        WebElement lastNameElement = getBrowser().waitUntilElementVisible(lastNameField);
        clearAndType(lastNameElement, lastName);
        return this;
    }

    public EditUserPage assertEmailFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(emailField), "Email field is displayed");
        return this;
    }

    public EditUserPage editEmailField(String email)
    {
        WebElement emailElement = getBrowser().waitUntilElementVisible(emailField);
        clearAndType(emailElement, email);
        return this;
    }

    public EditUserPage assertGroupSearchFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(groupsInputField), "Group search field is displayed");
        return this;
    }

    public EditUserPage searchGroupWithRetry(GroupModel group)
    {
        WebElement inputElement = getBrowser().waitUntilElementVisible(groupsInputField);
        clearAndType(inputElement, group.getGroupIdentifier());
        getBrowser().waitUntilElementClickable(searchGroupButton).click();
        int counter = 0;
        boolean found = isGroupInSearchResults(group.getGroupIdentifier());
        while (!found && counter <= 5)
        {
            Utility.waitToLoopTime(1, String.format("Wait for group to be displayed: %s", group.getGroupIdentifier()));
            clickElement(searchGroupButton);
            found = isGroupInSearchResults(group.getGroupIdentifier());
            counter++;
        }
        return this;
    }

    public EditUserPage assertQuotaFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(quotaField), "Quota field is displayed");
        return this;
    }

    public EditUserPage editQuota(String quota)
    {
        clearAndType(quotaField, quota);
        return this;
    }

    public EditUserPage assertNewPasswordFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(newPasswordField), "New password field is displayed");
        return this;
    }

    public void editNewPassword(String newPassword)
    {
        clearAndType(newPasswordField, newPassword);
    }

    public EditUserPage assertVerifyPasswordFieldIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(verifyPasswordField), "Verify password field is displayed");
        return this;
    }

    public void editVerifyPasswordFieldDisplayed(String verifyPassword)
    {
        clearAndType(verifyPasswordField, verifyPassword);
    }

    public EditUserPage assertDisableAccountIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(disableAccount), "Disable account is displayed");
        return this;
    }

    public EditUserPage selectDisabledAccount()
    {
        WebElement disableCheck = getBrowser().waitUntilElementVisible(disableAccount);
        getBrowser().scrollToElement(disableCheck);
        getBrowser().mouseOver(disableCheck);
        disableCheck.click();
        if(!disableCheck.isSelected())
        {
            LOG.info("Retry select disable account");
            disableCheck.click();
        }
        return this;
    }

    public EditUserPage selectEnableAccount()
    {
        WebElement disableCheck = getBrowser().waitUntilElementVisible(disableAccount);
        getBrowser().scrollToElement(disableCheck);
        getBrowser().mouseOver(disableCheck);
        disableCheck.click();
        if(disableCheck.isSelected())
        {
            LOG.info("Retry select enable account");
            disableCheck.click();
        }
        return this;
    }

    public EditUserPage assertPhotoIsDisplayed()
    {
        assertTrue(getBrowser().isElementDisplayed(photoField), "Photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage clickSaveChanges()
    {
        getBrowser().waitUntilElementClickable(saveChangesButton).click();
        waitUntilNotificationMessageDisappears();

        return (UserProfileAdminToolsPage) new UserProfileAdminToolsPage(browser).renderedPage();
    }

    public EditUserPage addGroup(GroupModel group)
    {
        getBrowser().waitUntilElementVisible(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))).click();
        getBrowser().waitUntilElementVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    public EditUserPage assertGroupIsAdded(GroupModel group)
    {
        assertTrue(getBrowser().isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertGroupIsNotAdded(GroupModel group)
    {
        assertFalse(getBrowser().isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage removeGroup(GroupModel group)
    {
        getBrowser().waitUntilElementVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))).click();
        return this;
    }

    private boolean isGroupInSearchResults(String groupName)
    {
        return getBrowser().findElements(groupSearchResults)
            .stream()
            .anyMatch(groupNameItem -> groupNameItem.getText()
            .equalsIgnoreCase(groupName));
    }

    public EditUserPage assertGroupIsFound(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is found", group.getGroupIdentifier()));
        assertTrue(isGroupInSearchResults(group.getGroupIdentifier()), String.format("Group %s was found", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertAddButtonIsDisplayedForGroup(GroupModel group)
    {
        LOG.info(String.format("Assert add group button is found for group %s", group.getGroupIdentifier()));
        assertTrue(getBrowser().isElementDisplayed(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))),
            String.format("Add button is displayed for group: %s", group.getGroupIdentifier()));
        return this;
    }
}
