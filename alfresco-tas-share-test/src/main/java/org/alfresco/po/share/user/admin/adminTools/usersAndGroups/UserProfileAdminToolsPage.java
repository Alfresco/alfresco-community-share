package org.alfresco.po.share.user.admin.adminTools.usersAndGroups;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by Mirela Tifui on 12/8/2016.
 */
@PageObject
public class UserProfileAdminToolsPage extends SharePage<UserProfileAdminToolsPage>
{
    @RenderWebElement
    private By editUserButton = By.cssSelector("button[id$='_default-edituser-button-button']");

    @RenderWebElement
    private By deleteUserButton = By.cssSelector("button[id$='_default-deleteuser-button-button']");

    @RenderWebElement
    private By goBackButton = By.cssSelector("button[id$='_default-goback-button-button']");

    private By userProfileUserName = By.cssSelector("span[id$='_default-view-title']");

    @FindBy(css="div [id$='_default-view-name']")
    private WebElement userNameInAboutSection;

    @FindAll(@FindBy(css="div.header-bar"))
    private List<WebElement> userProfileHeaderList;

    private By sectionsInfo = By.xpath("//div[@class='field-row']");

    private By userPhoto= By.cssSelector("div.photo img.view-photoimg");

    @FindBy(css ="span[id$='_default-view-quota']")
    private WebElement userQuota;

    @FindBy(css="div.name-label")
    public WebElement userName;

    @FindBy(css ="span[id$='_default-view-email']")
    private WebElement email;

    @FindBy(css="span[id$='_default-view-enabled']")
    public WebElement accountStatus;

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
        browser.waitUntilElementClickable(editUserButton, 5L);
        return browser.isElementDisplayed(editUserButton);
    }

    public boolean isDeleteUserButtonDisplayed()
    {
        browser.waitUntilElementClickable(deleteUserButton, 5L);
        return browser.isElementDisplayed(deleteUserButton);
    }

    public boolean isGoBackButtonDisplayed()
    {
        browser.waitUntilElementClickable(goBackButton, 5L);
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

    public HtmlPage clickEditUserButton(HtmlPage page)
    {
        browser.waitUntilElementClickable(editUserButton, 5L);
        browser.findElement(editUserButton).click();
        return page.renderedPage();
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

    public HtmlPage clickDelete(HtmlPage page)
    {
        browser.waitUntilElementClickable(deleteUserButton, 5L);
        browser.findElement(deleteUserButton).click();
        return page.renderedPage();
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
}
