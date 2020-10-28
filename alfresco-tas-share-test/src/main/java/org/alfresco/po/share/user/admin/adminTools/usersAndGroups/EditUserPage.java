package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

/**
 * Created by Mirela Tifui on 12/9/2016.
 */
@PageObject
public class EditUserPage extends SharePage<EditUserPage>
{
    @Autowired
    private UserProfileAdminToolsPage userProfileAdminToolsPage;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-update-groupfinder-group-search-button-button']")
    private WebElement searchGroupButton;

    @FindBy (css = "button[id$='_default-updateuser-clearphoto-button-button']")
    private WebElement useDefaultButton;

    @RenderWebElement
    @FindBy (css = "button[id$='_default-updateuser-save-button-button']")
    private WebElement saveChangesButton;

    @FindBy (css = "button[id$='_default-updateuser-cancel-button-button']")
    private WebElement cancelButton;

    @FindBy (css = "span[id$='_default-update-title']")
    private WebElement userNameInEditUserPageTitle;

    @FindAll (@FindBy (css = "form[id$='_default-update-form'] div.header-bar"))
    private List<WebElement> userProfileHeaderList;

    @FindBy (css = "input[id$='_default-update-firstname']")
    private WebElement firstNameField;

    @FindBy (css = "input[id$='_default-update-lastname']")
    private WebElement lastNameField;

    @FindBy (css = "input[id$='_default-update-email']")
    private WebElement emailField;

    @FindBy (css = "input[id$='_default-update-quota']")
    private WebElement quotaField;

    @FindBy (css = "input[id$='_default-update-password']")
    private WebElement newPasswordField;

    @FindBy (css = "input[id$='_default-update-verifypassword']")
    private WebElement verifyPasswordField;

    @FindBy (css = "input[id$='_default-update-disableaccount']")
    private WebElement disableAccount;

    @FindBy (css = "img[id$='_default-update-photoimg']")
    private WebElement photoField;

    @FindAll (@FindBy (css = "h3[class='itemname']"))
    private List<WebElement> groupSearchResults;

    private By groupsInputField = By.cssSelector("input[id$='_default-update-groupfinder-search-text']");
    private String genericAddToGroupButton = "//h3[@class='itemname' and text()='%s']/../../..//button";
    private String genericRemoveButton = "//div[contains(@id, 'default-update-groups')]//span[text()= '%s']";

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
        Assert.assertTrue(browser.isElementDisplayed(searchGroupButton), "Search group button is displayed");
        return this;
    }

    public EditUserPage assertUseDefaultButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(useDefaultButton), "Use default button is displayed");
        return this;
    }

    public EditUserPage assertSaveChangesButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(saveChangesButton), "Save button is displayed");
        return this;
    }

    public EditUserPage assertCancelButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(cancelButton), "Cancel button is displayed");
        return this;
    }

    public EditUserPage assertUserFullNameIsDisplayedInTitle(UserModel user)
    {
        LOG.info("Assert User full name is displayed in title: {} {}", user.getFirstName(), user.getLastName());
        Assert.assertEquals(browser.waitUntilElementVisible(userNameInEditUserPageTitle).getText(),
            String.format("%s %s", user.getFirstName(), user.getLastName()));
        return this;
    }

    public EditUserPage assertAllSectionsAreDisplayed()
    {
        List<String> sectionTexts = browser.getTextFromElementList(userProfileHeaderList);
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.info")),
            "Info section is displayed");
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.aboutUser")),
            "About User section is displayed");
        Assert.assertTrue(sectionTexts.contains(language.translate("adminTools.user.edit.photo")),
            "Photo section is displayed");
        return this;
    }

    public EditUserPage assertFirstNameFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(firstNameField), "First Name field is displayed");
        return this;
    }

    public EditUserPage editFirstName(String firstName)
    {
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
        return this;
    }

    public EditUserPage assertLastNameFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(lastNameField), "Last Name field is displayed");
        return this;
    }

    public EditUserPage editLastNameField(String lastName)
    {
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
        return this;
    }

    public EditUserPage assertEmailFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(emailField), "Email field is displayed");
        return this;
    }

    public EditUserPage editEmailField(String email)
    {
        emailField.clear();
        emailField.sendKeys(email);
        return this;
    }

    public EditUserPage assertGroupSearchFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(groupsInputField), "Group search field is displayed");
        return this;
    }

    public EditUserPage searchGroupWithRetry(GroupModel group)
    {
        WebElement inputElement = browser.waitUntilElementVisible(groupsInputField);
        clearAndType(inputElement, group.getGroupIdentifier());
        browser.waitUntilElementClickable(searchGroupButton).click();
        int counter = 0;
        boolean found = isGroupInSearchResults(group.getGroupIdentifier());
        while (!found && counter <= 5)
        {
            Utility.waitToLoopTime(1, String.format("Wait for group to be displayed: %s", group.getGroupIdentifier()));
            searchGroupButton.click();
            found = isGroupInSearchResults(group.getGroupIdentifier());
            counter++;
        }
        return this;
    }

    public EditUserPage assertQuotaFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(quotaField), "Quota field is displayed");
        return this;
    }

    public EditUserPage editQuota(String quota)
    {
        quotaField.clear();
        quotaField.sendKeys(quota);
        return this;
    }

    public EditUserPage assertNewPasswordFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(newPasswordField), "New password field is displayed");
        return this;
    }

    public void editNewPassword(String newPassword)
    {
        newPasswordField.clear();
        newPasswordField.sendKeys(newPassword);
    }

    public EditUserPage assertVerifyPasswordFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(verifyPasswordField), "Verify password field is displayed");
        return this;
    }

    public void editVerifyPasswordFieldDisplayed(String verifyPassword)
    {
        verifyPasswordField.clear();
        verifyPasswordField.sendKeys(verifyPassword);
    }

    public EditUserPage assertDisableAccountIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(disableAccount), "Disable account is displayed");
        return this;
    }

    public EditUserPage clickDisabledAccount()
    {
        browser.waitUntilElementVisible(disableAccount).click();
        return this;
    }

    public EditUserPage assertPhotoIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(photoField), "Photo is displayed");
        return this;
    }

    public UserProfileAdminToolsPage clickSaveChanges()
    {
        getBrowser().waitUntilElementVisible(saveChangesButton);
        getBrowser().waitUntilElementClickable(saveChangesButton).click();
        waitUntilNotificationMessageDisappears();
        return (UserProfileAdminToolsPage) userProfileAdminToolsPage.renderedPage();
    }

    public EditUserPage addGroup(GroupModel group)
    {
        browser.waitUntilElementVisible(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))).click();
        browser.waitUntilElementVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier())));
        return this;
    }

    public EditUserPage assertGroupIsAdded(GroupModel group)
    {
        Assert.assertTrue(browser.isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertGroupIsNotAdded(GroupModel group)
    {
        Assert.assertFalse(browser.isElementDisplayed(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))),
            String.format("Group %s was added", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage removeGroup(GroupModel group)
    {
        browser.waitUntilElementVisible(By.xpath(String.format(genericRemoveButton, group.getGroupIdentifier()))).click();
        return this;
    }

    private boolean isGroupInSearchResults(String groupName)
    {
        return groupSearchResults.stream().anyMatch(groupNameItem -> groupNameItem.getText().equalsIgnoreCase(groupName));
    }

    public EditUserPage assertGroupIsFound(GroupModel group)
    {
        LOG.info(String.format("Assert group %s is found", group.getGroupIdentifier()));
        Assert.assertTrue(isGroupInSearchResults(group.getGroupIdentifier()), String.format("Group %s was found", group.getGroupIdentifier()));
        return this;
    }

    public EditUserPage assertAddButtonIsDisplayedForGroup(GroupModel group)
    {
        LOG.info(String.format("Assert add group button is found for group %s", group.getGroupIdentifier()));
        Assert.assertTrue(browser.isElementDisplayed(By.xpath(String.format(genericAddToGroupButton, group.getGroupIdentifier()))),
            String.format("Add button is displayed for group: %s", group.getGroupIdentifier()));
        return this;
    }
}
