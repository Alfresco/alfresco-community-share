package org.alfresco.po.share.user.profile;

import static org.alfresco.common.Wait.WAIT_20;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author bogdan.bocancea
 */
public class ChangePasswordPage extends SharePage2<ChangePasswordPage> implements AccessibleByMenuBar
{
    private String userName;

    private final By oldPasswordInput = By.cssSelector("input[id$='default-oldpassword']");
    private final By newPassword = By.cssSelector("input[id$='default-newpassword1']");
    private final By confirmNewPassword = By.cssSelector("input[id$='default-newpassword2']");
    private final By okButton = By.cssSelector("button[id$='default-button-ok-button']");
    private final By errorPrompt = By.id("prompt");
    private final By errorPromptMessage = By.cssSelector("#prompt .bd");
    private final By errorPromptOKButton = By.cssSelector("#prompt button");

    private final String changePassword = "change-password";

    public ChangePasswordPage(ThreadLocal<WebDriver> webDriver)
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
        return setRelativePathForUserPage("share/page/user/%s/change-password", getUserName());
    }

    public ChangePasswordPage navigate(UserModel user)
    {
        setUserName(user.getUsername());
        return navigate();
    }

    @Override
    public ChangePasswordPage navigateByMenuBar()
    {
        return new Toolbar(webDriver).clickUserMenu().clickChangePassword();
    }

    public ChangePasswordPage typeOldPassword(String oldPasswordText)
    {
        webElementInteraction.clearAndType(oldPasswordInput, oldPasswordText);
        return this;
    }

    public ChangePasswordPage typeNewPassword(String newPasswordText)
    {
        webElementInteraction.clearAndType(newPassword, newPasswordText);
        return this;
    }

    public ChangePasswordPage typeConfirmNewPassword(String confirmNewPasswordText)
    {
        webElementInteraction.clearAndType(confirmNewPassword, confirmNewPasswordText);
        return this;
    }

    public void clickOkButton()
    {
        webElementInteraction.clickElement(okButton);
    }

    public ChangePasswordPage assertChangePasswordPageIsOpened()
    {
        webElementInteraction.waitUrlContains(changePassword, WAIT_20.getValue());
        assertTrue(webElementInteraction.getCurrentUrl().contains(changePassword), "Change password page is opened");
        return this;
    }

    public UserProfilePage changePassword(String oldPassword, String newPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(newPassword);
        clickOkButton();
        return new UserProfilePage(webDriver);
    }

    public ChangePasswordPage changePasswordAndExpectError(String oldPassword, String newPassword, String confirmPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(confirmPassword);
        clickOkButton();
        webElementInteraction.waitUntilElementIsVisible(errorPrompt);
        assertEquals(webElementInteraction.getElementText(errorPromptMessage),
            language.translate("changeUserPassword.errorPrompt.message"),
            "Error prompt message is correct");
        webElementInteraction.clickElement(errorPromptOKButton);
        return this;
    }
}