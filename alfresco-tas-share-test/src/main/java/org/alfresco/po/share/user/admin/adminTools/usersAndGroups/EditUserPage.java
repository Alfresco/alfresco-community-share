package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import static org.alfresco.common.Wait.WAIT_60;
import static org.alfresco.utility.Utility.waitToLoopTime;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.exception.PageRenderTimeException;
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
    private final By cancelButton = By.cssSelector("button[id$='_default-updateuser-cancel-button-button']");
    private final By userNameInEditUserPageTitle = By.cssSelector("span[id$='_default-update-title']");
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
            webElementInteraction.waitUntilElementIsVisible(saveChangesButton);
        }
        catch (TimeoutException | PageRenderTimeException e)
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
        assertTrue(webElementInteraction.isElementDisplayed(searchGroupButton), "Search group button is displayed");
        return this;
    }

    public EditUserPage assertUseDefaultButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(useDefaultButton), "Use default button is displayed");
        return this;
    }

    public EditUserPage assertSaveChangesButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(saveChangesButton), "Save button is displayed");
        return this;
    }

    public EditUserPage assertCancelButtonIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public EditUserPage assertUserFullNameIsDisplayedInTitle(UserModel user)
    {
        log.info("Assert User full name is displayed in title: {} {}", user.getFirstName(), user.getLastName());
        assertEquals(webElementInteraction.waitUntilElementIsVisible(userNameInEditUserPageTitle).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage assertAllSectionsAreDisplayed()
    {
        List<String> sectionTexts = webElementInteraction.getTextFromElementList(webElementInteraction.findElements(userProfileHeaderList));
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
        assertTrue(webElementInteraction.isElementDisplayed(firstNameField), "First Name field is displayed");
        return this;
    }

    public EditUserPage editFirstName(String firstName)
    {
        WebElement firstNameElement = webElementInteraction.waitUntilElementIsVisible(firstNameField);
        webElementInteraction.clearAndType(firstNameElement, firstName);
        return this;
    }

    public EditUserPage assertLastNameFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(lastNameField), "Last Name field is displayed");
        return this;
    }

    public EditUserPage editLastNameField(String lastName)
    {
        WebElement lastNameElement = webElementInteraction.waitUntilElementIsVisible(lastNameField);
        webElementInteraction.clearAndType(lastNameElement, lastName);
        return this;
    }

    public EditUserPage assertEmailFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(emailField), "Email field is displayed");
        return this;
    }

    public EditUserPage editEmailField(String email)
    {
        WebElement emailElement = webElementInteraction.waitUntilElementIsVisible(emailField);
        webElementInteraction.clearAndType(emailElement, email);
        return this;
    }

    public EditUserPage assertGroupSearchFieldIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(groupsInputField);
        assertTrue(webElementInteraction.isElementDisplayed(groupsInputField), "Group search field is displayed");
        return this;
    }

    public EditUserPage searchGroupWithRetry(GroupModel group)
    {
        WebElement inputElement = webElementInteraction.waitUntilElementIsVisible(groupsInputField);
        webElementInteraction.clearAndType(inputElement, group.getGroupIdentifier());
        webElementInteraction.clickElement(searchGroupButton);

        int counter = 0;
        boolean found = isGroupInSearchResults(group.getGroupIdentifier());
        while (!found && counter <= WAIT_60.getValue())
        {
            waitToLoopTime(2, String.format("Wait for group to be displayed: %s", group.getGroupIdentifier()));
            webElementInteraction.clickElement(searchGroupButton);
            found = isGroupInSearchResults(group.getGroupIdentifier());
            counter++;
        }
        return this;
    }

    public EditUserPage assertQuotaFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(quotaField), "Quota field is displayed");
        return this;
    }

    public EditUserPage editQuota(String quota)
    {
        webElementInteraction.waitUntilElementIsVisible(quotaField);
        webElementInteraction.clearAndType(quotaField, quota);
        return this;
    }

    public EditUserPage assertNewPasswordFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(newPasswordField), "New password field is displayed");
        return this;
    }

    public EditUserPage assertVerifyPasswordFieldIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(verifyPasswordField), "Verify password field is displayed");
        return this;
    }

    public EditUserPage assertDisableAccountIsDisplayed()
    {
        webElementInteraction.waitUntilElementIsVisible(disableAccount);
        assertTrue(webElementInteraction.isElementDisplayed(disableAccount), "Disable account is displayed");
        return this;
    }

    public EditUserPage selectDisabledAccount()
    {
        WebElement disableCheck = webElementInteraction.waitUntilElementIsVisible(disableAccount);
        webElementInteraction.scrollToElement(disableCheck);
        webElementInteraction.mouseOver(disableCheck);
        webElementInteraction.clickElement(disableCheck);
        if(!disableCheck.isSelected())
        {
            log.info("Retry select disable account");
            webElementInteraction.clickElement(disableCheck);
        }
        return this;
    }

    public EditUserPage selectEnableAccount()
    {
        WebElement disableCheck = webElementInteraction.waitUntilElementIsVisible(disableAccount);
        webElementInteraction.scrollToElement(disableCheck);
        webElementInteraction.mouseOver(disableCheck);
        webElementInteraction.clickElement(disableCheck);
        if(disableCheck.isSelected())
        {
            log.info("Retry select enable account");
            webElementInteraction.clickElement(disableCheck);
        }
        return this;
    }

    public EditUserPage assertPhotoIsDisplayed()
    {
        assertTrue(webElementInteraction.isElementDisplayed(photoField), "Photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage clickSaveChanges()
    {
        webElementInteraction.clickElement(saveChangesButton);
        waitUntilNotificationMessageDisappears();

        return new UserProfileAdminToolsPage(webDriver);
    }

    public EditUserPage addGroup(GroupModel group)
    {
        webElementInteraction.clickElement(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier())));
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    public EditUserPage assertGroupIsAdded(GroupModel group)
    {
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        assertTrue(webElementInteraction.isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertGroupIsNotAdded(GroupModel group)
    {
        assertFalse(
            webElementInteraction.isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage removeGroup(GroupModel group)
    {
        webElementInteraction.clickElement(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    private boolean isGroupInSearchResults(String groupName)
    {
        webElementInteraction.waitUntilElementsAreVisible(groupSearchResults);
        return webElementInteraction.findElements(groupSearchResults)
            .stream()
            .anyMatch(groupNameItem -> groupNameItem.getText()
            .equalsIgnoreCase(groupName));
    }

    public EditUserPage assertGroupIsFound(GroupModel group)
    {
        log.info("Assert group is found: {}", group.getGroupIdentifier());
        webElementInteraction.waitUntilElementsAreVisible(groupSearchResults);
        assertTrue(isGroupInSearchResults(group.getGroupIdentifier()),
            String.format("Group %s was found", group.getGroupIdentifier()));

        return this;
    }

    public EditUserPage assertAddButtonIsDisplayedForGroup(GroupModel group)
    {
        log.info("Assert add group button is found for group: {}", group.getGroupIdentifier());
        webElementInteraction.waitUntilElementIsVisible(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier())));
        assertTrue(webElementInteraction.isElementDisplayed(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))),
            String.format("Add button is displayed for group: %s", group.getGroupIdentifier()));
        return this;
    }
}
