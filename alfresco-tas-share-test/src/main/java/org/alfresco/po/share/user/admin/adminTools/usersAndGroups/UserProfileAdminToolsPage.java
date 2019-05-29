package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mirela Tifui on 12/8/2016.
 */
@PageObject
public class UserProfileAdminToolsPage extends SharePage<UserProfileAdminToolsPage>
{
    @FindBy (css = "div.name-label")
    public WebElement userName;
    @FindBy (css = "span[id$='_default-view-enabled']")
    public WebElement accountStatus;
    @FindBy (xpath = "//span[text()='Groups:']/../span[@class='field-value']")
    public WebElement groupName;
    @Autowired
    EditUserPage editUserPage;
    @Autowired
    DeleteUserDialogPage deleteUserDialogPage;
    @RenderWebElement
    @FindBy (css = "button[id$='_default-edituser-button-button']")
    private WebElement editUserButton;
    @RenderWebElement
    @FindBy (css = "button[id$='_default-deleteuser-button-button']")
    private WebElement deleteUserButton;
    @RenderWebElement
    @FindBy (css = "button[id$='_default-goback-button-button']")
    private WebElement goBackButton;
    private By userProfileUserName = By.cssSelector("span[id$='_default-view-title']");
    @FindBy (css = "div [id$='_default-view-name']")
    private WebElement userNameInAboutSection;
    @FindAll (@FindBy (css = "div.header-bar"))
    private List<WebElement> userProfileHeaderList;
    private By sectionsInfo = By.xpath("//div[@class='field-row']");
    private By userPhoto = By.cssSelector("div.photo img.view-photoimg");
    @FindBy (css = "span[id$='_default-view-quota']")
    private WebElement userQuota;
    @FindBy (css = "span[id$='_default-view-email']")
    private WebElement email;

    @Override
    public String getRelativePath()
    {
        return null;
    }

    public String getUserNameInPageTitle()
    {
        browser.waitUntilElementVisible(userProfileUserName);
        return browser.findElement(userProfileUserName).getText();
    }

    public boolean isEditUserButtonDisplayed()
    {
        return browser.isElementDisplayed(editUserButton);
    }

    public boolean isDeleteUserButtonDisplayed()
    {
        return browser.isElementDisplayed(deleteUserButton);
    }

    public boolean isGoBackButtonDisplayed()
    {
        return browser.isElementDisplayed(goBackButton);
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

    public boolean isInfoDisplayedInSection(String infoName)
    {
        return browser.isElementDisplayed(By.xpath("//span[text() ='" + infoName + "']"));
    }

    public boolean isUserPhotoDisplayed()
    {
        return browser.isElementDisplayed(userPhoto);
    }

    public String getTheUserNameInAboutSection()
    {
        return userNameInAboutSection.getText();
    }

    public EditUserPage clickEditUserButton()
    {
        editUserButton.click();
        return (EditUserPage) editUserPage.renderedPage();
    }

    public String getUserName()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(userName, 5);
        return userName.getText();
    }

    public String getEmail()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(email, 5);
        return email.getText();
    }

    public DeleteUserDialogPage clickDelete()
    {
        deleteUserButton.click();
        return (DeleteUserDialogPage) deleteUserDialogPage.renderedPage();
    }

    public String getUserQuota(String quota)
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(userQuota, 5);
        browser.waitUntilElementContainsText(userQuota, quota);
        return userQuota.getText();
    }

    public String getAccountStatus()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(accountStatus, 6);
        return accountStatus.getText();
    }

    public String getGroupsNames()
    {
        getBrowser().waitInSeconds(5);
        return groupName.getText();
    }
}
