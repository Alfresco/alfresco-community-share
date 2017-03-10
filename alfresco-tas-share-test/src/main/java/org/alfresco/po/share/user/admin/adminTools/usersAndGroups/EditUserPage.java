package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by Mirela Tifui on 12/9/2016.
 */
@PageObject
public class EditUserPage extends SharePage<EditUserPage>
{
    @RenderWebElement
    private By searchGroupButton = By.cssSelector("button[id$='_default-update-groupfinder-group-search-button-button']");

    @RenderWebElement
    private By useDefaultButton = By.cssSelector("button[id$='_default-updateuser-clearphoto-button-button']");

    @RenderWebElement
    private By saveChangesButton = By.cssSelector("button[id$='_default-updateuser-save-button-button']");

    @RenderWebElement
    private By cancelButton = By.cssSelector("button[id$='_default-updateuser-cancel-button-button']");

    @FindBy(css ="span[id$='_default-update-title']")
    private WebElement userNameInEditUserPageTitle;

    @FindAll(@FindBy(css="form[id$='_default-update-form'] div.header-bar"))
    private List<WebElement> userProfileHeaderList;

    @FindBy(css="input[id$='_default-update-firstname']")
    private WebElement firstNameField;

    @FindBy(css ="input[id$='_default-update-lastname']")
    private WebElement lastNameField;

    @FindBy(css ="input[id$='_default-update-email']")
    private WebElement emailField;

    @FindBy(css ="input[id$='_default-update-groupfinder-search-text']")
    private WebElement groupsInputField;

    @FindBy(css="input[id$='_default-update-quota']")
    private WebElement quotaField;

    @FindBy(css = "input[id$='_default-update-password']")
    private WebElement newPasswordField;

    @FindBy(css = "input[id$='_default-update-verifypassword']")
    private WebElement verifyPasswordField;

    @FindBy(css="input[id$='_default-update-disableaccount']")
    private WebElement disableAccount;

    @FindBy(css ="img[id$='_default-update-photoimg']")
    private WebElement photoField;

    @Override
    public String getRelativePath() {
        return null;
    }

    public boolean isSearchButtonDisplayed()
    {
        browser.waitUntilElementClickable(searchGroupButton, 5);
        return browser.isElementDisplayed(searchGroupButton);
    }

    public boolean isUseDefaultButtonDisplayed()
    {
        browser.waitUntilElementClickable(useDefaultButton, 5);
        return browser.isElementDisplayed(useDefaultButton);
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        browser.waitUntilElementClickable(saveChangesButton, 5);
        return browser.isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
        browser.waitUntilElementClickable(cancelButton, 5);
        return browser.isElementDisplayed(cancelButton);
    }

    public String getUserInEditUserPageTitle(String userName)
    {
        browser.waitUntilElementContainsText(userNameInEditUserPageTitle, userName);
        return userNameInEditUserPageTitle.getText();
    }

    public boolean isSectionDisplayed(String sectionName)
    {
        List<WebElement> sectionList = userProfileHeaderList;
        for (WebElement section : sectionList)
        {
            if (section.getText().equals(sectionName))
                return true;
        }
        return false;
    }
    public boolean isFirstNameFieldDisplayed()
    {
        return browser.isElementDisplayed(firstNameField);
    }

    public void editFirstName(String firstName)
    {
        browser.waitUntilElementVisible(firstNameField);
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
    }

    public boolean isLastNameFieldDisplayed()
    {
        return browser.isElementDisplayed(lastNameField);
    }

    public void editLastNameField(String lastName)
    {
        browser.waitUntilElementVisible(lastNameField);
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
    }

    public boolean isEmailFieldDisplayed()
    {
        return browser.isElementDisplayed(emailField);
    }

    public void editEmailField(String email)
    {
        browser.waitUntilElementVisible(emailField);
        emailField.clear();
        emailField.sendKeys(email);
    }

    public boolean isGroupsInputFieldDisplayed()
    {
        browser.waitUntilElementVisible(groupsInputField);
        return browser.isElementDisplayed(groupsInputField);
    }

    public void editGroupsField(String groupName)
    {
        browser.waitUntilElementClickable(groupsInputField,5);
        groupsInputField.clear();
        groupsInputField.sendKeys(groupName);
    }

    public boolean isQuotaFieldDisplayed()
    {
        browser.waitUntilElementVisible(quotaField);
        return browser.isElementDisplayed(quotaField);
    }

    public void editQuota(String quota)
    {
        browser.waitUntilElementClickable(quotaField, 5);
        quotaField.clear();
        quotaField.sendKeys(quota);
    }

    public boolean isNewPasswordFieldDisplayed()
    {
        browser.waitUntilElementVisible(newPasswordField);
        return browser.isElementDisplayed(newPasswordField);
    }

    public void editNewPassword(String newPassword)
    {
        browser.waitUntilElementClickable(newPasswordField, 5);
        newPasswordField.clear();
        newPasswordField.sendKeys(newPassword);
    }

    public boolean isVerifyPasswordFieldDisplayed()
    {
        browser.waitUntilElementVisible(verifyPasswordField);
        return  browser.isElementDisplayed(verifyPasswordField);
    }

    public void editVerifyPasswordFieldDisplayed(String verifyPassword)
    {
        browser.waitUntilElementClickable(verifyPasswordField, 5);
        verifyPasswordField.clear();
        verifyPasswordField.sendKeys(verifyPassword);
    }

    public boolean isDisableAccountFieldDisplayed()
    {
        browser.waitUntilElementVisible(disableAccount);
        return browser.isElementDisplayed(disableAccount);
    }

    public void clickDisabledAccount()
    {
        browser.waitUntilElementClickable(disableAccount, 5L);
        disableAccount.click();
    }

    public boolean isEditPhotoFieldDisplayed()
    {
        browser.waitUntilElementVisible(photoField);
        return browser.isElementDisplayed(photoField);
    }

    public HtmlPage clickSaveChangesButton(HtmlPage page)
    {
        browser.waitUntilElementClickable(saveChangesButton, 5L);
        browser.findElement(saveChangesButton).click();
        return page.renderedPage();
    }
}
