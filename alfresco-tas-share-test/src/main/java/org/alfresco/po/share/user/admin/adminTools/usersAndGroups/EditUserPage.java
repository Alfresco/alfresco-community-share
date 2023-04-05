package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.alfresco.common.Wait.WAIT_2;
import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class EditUserPage extends SharePage2<EditUserPage>
{
    private String userName;

    private final By searchGroupButton = By.cssSelector("button[id$='_default-update-groupfinder-group-search-button-button']");
    private final By useDefaultButton = By.cssSelector("button[id$='_default-updateuser-clearphoto-button-button']");
    private final By saveChangesButton = By.cssSelector("button[id$='_default-updateuser-save-button-button']");
    private final By userNameField = By.xpath("//input[@id=\"page_x002e_ctool_x002e_admin-console_x0023_default-create-username\"]");
    private final By cancelButton = By.cssSelector("button[id$='_default-updateuser-cancel-button-button']");
    private final By userNameInEditUserPageTitle = By.cssSelector("span[id$='_default-update-title']");
    private final By groupNameInput = By.xpath("(//input[@type=\"text\"])[12]");
    private final By groupSearchButton = By.xpath("(//button[text()='Search'])[3]");
    private final By groupAddButton = By.xpath("//button[text()='Add ']");
    private final By userProfileHeaderList = By.cssSelector("form[id$='_default-update-form'] div.header-bar");
    private final By firstNameField = By.cssSelector("input[id$='_default-update-firstname']");
    private final By lastNameField = By.cssSelector("input[id$='_default-update-lastname']");
    private final By emailField = By.cssSelector("input[id$='_default-update-email']");
    private final By quotaField = By.cssSelector("input[id$='_default-update-quota']");
    private final By newPasswordField = By.cssSelector("input[id$='_default-update-password']");
    private final By verifyPasswordField = By.cssSelector("input[id$='_default-update-verifypassword']");
    private final By disableAccount = By.cssSelector("input[id$='_default-update-disableaccount']");
    private final By photoField = By.cssSelector("img[id$='_default-update-photoimg']");
    private final By groupSearchResults = By.cssSelector("h3[class='itemname']");
    private final By groupsInputField = By.cssSelector("input[id$='_default-update-groupfinder-search-text']");

    private final String genericAddToGroupButton = "//h3[@class='itemname' and text()='%s']/../../..//button";
    private final String genericRemoveButton = "//div[contains(@id, 'default-update-groups')]//span[text()= '%s']";

    public EditUserPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
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

    public synchronized EditUserPage navigate(String userName)
    {
        setUserName(userName);
        try
        {
            super.navigate();
            waitUntilElementIsVisible(saveChangesButton);
        }
        catch (TimeoutException e)
        {
            log.error("Retry navigate to Edit User Page");
            UserProfileAdminToolsPage userProfileAdminToolsPage = new UserProfileAdminToolsPage(webDriver);
            userProfileAdminToolsPage.navigate(userName);
            userProfileAdminToolsPage.clickEditUser();
        }
        return this;
    }

    public synchronized EditUserPage navigate(UserModel user)
    {
        return navigate(user.getUsername());
    }

    public EditUserPage assertSearchGroupButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(searchGroupButton), "Search group button is displayed");
        return this;
    }
    public EditUserPage editUserName(String userName)
    {
        WebElement userNameElement = waitUntilElementIsVisible(userNameField);
        clearAndType(userNameElement, userName);
        return this;
    }

    public EditUserPage assertUseDefaultButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(useDefaultButton), "Use default button is displayed");
        return this;
    }

    public EditUserPage assertSaveChangesButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(saveChangesButton), "Save button is displayed");
        return this;
    }

    public EditUserPage assertCancelButtonIsDisplayed()
    {
        assertTrue(isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public EditUserPage assertUserFullNameIsDisplayedInTitle(UserModel user)
    {
        log.info("Assert User full name is displayed in title: {} {}", user.getFirstName(), user.getLastName());
        assertEquals(waitUntilElementIsVisible(userNameInEditUserPageTitle).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }
    public EditUserPage addGroup(String firstName)
    {
        WebElement SearchGroupName = waitUntilElementIsVisible(groupNameInput);
        clearAndType(SearchGroupName, firstName);
        clickElement(groupSearchButton);
        clickElement(groupAddButton);
        return this;
    }



    public EditUserPage assertAllSectionsAreDisplayed()
    {
        List<String> sectionTexts = getTextFromElementList(findElements(userProfileHeaderList));
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
        assertTrue(isElementDisplayed(firstNameField), "First Name field is displayed");
        return this;
    }

    public EditUserPage editFirstName(String firstName)
    {
        WebElement firstNameElement = waitUntilElementIsVisible(firstNameField);
        clearAndType(firstNameElement, firstName);
        return this;
    }

    public EditUserPage assertLastNameFieldIsDisplayed()
    {
        assertTrue(isElementDisplayed(lastNameField), "Last Name field is displayed");
        return this;
    }

    public EditUserPage editLastNameField(String lastName)
    {
        WebElement lastNameElement = waitUntilElementIsVisible(lastNameField);
        clearAndType(lastNameElement, lastName);
        return this;
    }

    public EditUserPage assertEmailFieldIsDisplayed()
    {
        assertTrue(isElementDisplayed(emailField), "Email field is displayed");
        return this;
    }

    public EditUserPage editEmailField(String email)
    {
        WebElement emailElement = waitUntilElementIsVisible(emailField);
        clearAndType(emailElement, email);
        return this;
    }

    public EditUserPage assertGroupSearchFieldIsDisplayed()
    {
        waitUntilElementIsVisible(groupsInputField);
        assertTrue(isElementDisplayed(groupsInputField), "Group search field is displayed");
        return this;
    }

    public EditUserPage searchGroupWithRetry(GroupModel group)
    {
        WebElement inputElement = waitUntilElementIsVisible(groupsInputField);
        clearAndType(inputElement, group.getGroupIdentifier());
        clickElement(searchGroupButton);

        int retryCount = 0;
        boolean found = isGroupInSearchResults(group.getGroupIdentifier());
        while (!found && retryCount <= WAIT_60.getValue())
        {
            log.error("Group {} is not displayed. Retry {}", group.getGroupIdentifier(), retryCount);
            waitToLoopTime(WAIT_2.getValue());
            clickElement(searchGroupButton);
            found = isGroupInSearchResults(group.getGroupIdentifier());
            retryCount++;
        }
        return this;
    }

    public EditUserPage assertQuotaFieldIsDisplayed()
    {
        assertTrue(isElementDisplayed(quotaField), "Quota field is displayed");
        return this;
    }

    public EditUserPage editQuota(String quota)
    {
        waitUntilElementIsVisible(quotaField);
        clearAndType(quotaField, quota);
        return this;
    }

    public EditUserPage assertNewPasswordFieldIsDisplayed()
    {
        assertTrue(isElementDisplayed(newPasswordField), "New password field is displayed");
        return this;
    }

    public EditUserPage assertVerifyPasswordFieldIsDisplayed()
    {
        assertTrue(isElementDisplayed(verifyPasswordField), "Verify password field is displayed");
        return this;
    }

    public EditUserPage assertDisableAccountIsDisplayed()
    {
        waitUntilElementIsVisible(disableAccount);
        assertTrue(isElementDisplayed(disableAccount), "Disable account is displayed");
        return this;
    }

    public EditUserPage selectDisabledAccount()
    {
        WebElement disableCheck = waitUntilElementIsVisible(disableAccount);
        scrollToElement(disableCheck);
        mouseOver(disableCheck);
        clickElement(disableCheck);
        if(!disableCheck.isSelected())
        {
            log.info("Retry select disable account");
            clickElement(disableCheck);
        }
        return this;
    }

    public EditUserPage selectEnableAccount()
    {
        WebElement disableCheck = waitUntilElementIsVisible(disableAccount);
        scrollToElement(disableCheck);
        mouseOver(disableCheck);
        clickElement(disableCheck);
        if(disableCheck.isSelected())
        {
            log.info("Retry select enable account");
            clickElement(disableCheck);
        }
        return this;
    }

    public EditUserPage assertPhotoIsDisplayed()
    {
        assertTrue(isElementDisplayed(photoField), "Photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage clickSaveChanges()
    {
        clickElement(saveChangesButton);
        waitUntilNotificationMessageDisappears();

        return new UserProfileAdminToolsPage(webDriver);
    }

    public EditUserPage addGroup(GroupModel group)
    {
        clickElement(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier())));
        waitUntilElementIsVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    public EditUserPage assertGroupIsAdded(GroupModel group)
    {
        waitUntilElementIsVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        assertTrue(isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertGroupIsNotAdded(GroupModel group)
    {
        assertFalse(
            isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage removeGroup(GroupModel group)
    {
        clickElement(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    private boolean isGroupInSearchResults(String groupName)
    {
        waitUntilElementsAreVisible(groupSearchResults);
        return findElements(groupSearchResults)
            .stream()
            .anyMatch(groupNameItem -> groupNameItem.getText()
            .equalsIgnoreCase(groupName));
    }

    public EditUserPage assertGroupIsFound(GroupModel group)
    {
        log.info("Assert group is found: {}", group.getGroupIdentifier());
        waitUntilElementsAreVisible(groupSearchResults);
        assertTrue(isGroupInSearchResults(group.getGroupIdentifier()),
            String.format("Group %s was found", group.getGroupIdentifier()));

        return this;
    }

    public EditUserPage assertAddButtonIsDisplayedForGroup(GroupModel group)
    {
        log.info("Assert add group button is found for group: {}", group.getGroupIdentifier());
        waitUntilElementIsVisible(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier())));
        assertTrue(isElementDisplayed(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))),
            String.format("Add button is displayed for group: %s", group.getGroupIdentifier()));
        return this;
    }
}
