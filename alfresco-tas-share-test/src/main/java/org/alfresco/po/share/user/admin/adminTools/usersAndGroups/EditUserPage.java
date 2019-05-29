package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/9/2016.
 */
@PageObject
public class EditUserPage extends SharePage<EditUserPage>
{
    @Autowired
    UserProfileAdminToolsPage userProfileAdminToolsPage;

    @FindBy (css = "button[id$='_default-update-groupfinder-group-search-button-button']")
    private WebElement searchGroupButton;

    @RenderWebElement
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

    @FindBy (css = "input[id$='_default-update-groupfinder-search-text']")
    private WebElement groupsInputField;

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

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public boolean isSearchButtonDisplayed()
    {
        return browser.isElementDisplayed(searchGroupButton);
    }

    public boolean isUseDefaultButtonDisplayed()
    {
        return browser.isElementDisplayed(useDefaultButton);
    }

    public boolean isSaveChangesButtonDisplayed()
    {
        return browser.isElementDisplayed(saveChangesButton);
    }

    public boolean isCancelButtonDisplayed()
    {
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
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
    }

    public boolean isLastNameFieldDisplayed()
    {
        return browser.isElementDisplayed(lastNameField);
    }

    public void editLastNameField(String lastName)
    {
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
    }

    public boolean isEmailFieldDisplayed()
    {
        return browser.isElementDisplayed(emailField);
    }

    public void editEmailField(String email)
    {
        emailField.clear();
        emailField.sendKeys(email);
    }

    public boolean isGroupsInputFieldDisplayed()
    {
        return browser.isElementDisplayed(groupsInputField);
    }

    public void editGroupsField(String groupName)
    {
        groupsInputField.clear();
        groupsInputField.sendKeys(groupName);
    }

    public boolean isQuotaFieldDisplayed()
    {
        return browser.isElementDisplayed(quotaField);
    }

    public void editQuota(String quota)
    {
        quotaField.clear();
        quotaField.sendKeys(quota);
    }

    public boolean isNewPasswordFieldDisplayed()
    {
        return browser.isElementDisplayed(newPasswordField);
    }

    public void editNewPassword(String newPassword)
    {
        newPasswordField.clear();
        newPasswordField.sendKeys(newPassword);
    }

    public boolean isVerifyPasswordFieldDisplayed()
    {
        return browser.isElementDisplayed(verifyPasswordField);
    }

    public void editVerifyPasswordFieldDisplayed(String verifyPassword)
    {
        verifyPasswordField.clear();
        verifyPasswordField.sendKeys(verifyPassword);
    }

    public boolean isDisableAccountFieldDisplayed()
    {
        return browser.isElementDisplayed(disableAccount);
    }

    public void clickDisabledAccount()
    {
        disableAccount.click();
    }

    public boolean isEditPhotoFieldDisplayed()
    {
        return browser.isElementDisplayed(photoField);
    }

    public UserProfileAdminToolsPage clickSaveChangesButton()
    {
        getBrowser().waitUntilElementVisible(saveChangesButton);
        getBrowser().waitUntilElementClickable(saveChangesButton).click();
        return (UserProfileAdminToolsPage) userProfileAdminToolsPage.renderedPage();
    }

    public void addGroup(String groupName)
    {
        getBrowser().waitUntilElementClickable(searchGroupButton).click();
        browser.waitUntilElementVisible(By.xpath(String.format("//td[contains(@class,'col-description')]//h3[@class='itemname' and text()='%s']//ancestor::tr//td[contains(@class,'col-actions')]//button[contains(text(),'Add')]", groupName))).click();
    }

    public void removeGroup(String groupName)
    {
        browser.waitUntilElementVisible(By.xpath(String.format("//span[@title='Remove Group' and text()='%s']", groupName))).click();
    }

}
