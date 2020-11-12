package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage2;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author bogdan.bocancea
 */
public class ChangePasswordPage extends SharePage2<ChangePasswordPage> implements AccessibleByMenuBar
{
    private String userName;

    @RenderWebElement
    private By oldPasswordInput = By.cssSelector("input[id$='default-oldpassword']");
    @RenderWebElement
    private By newPassword = By.cssSelector("input[id$='default-newpassword1']");
    private By confirmNewPassword = By.cssSelector("input[id$='default-newpassword2']");
    private By okButton = By.cssSelector("button[id$='default-button-ok-button']");
    private By errorPrompt = By.id("prompt");
    private By errorPromptMessage = By.cssSelector("#prompt .bd");
    private By errorPromptOKButton = By.cssSelector("#prompt button");

    public ChangePasswordPage(ThreadLocal<WebBrowser> browser)
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
        return (ChangePasswordPage) new Toolbar(browser).clickUserMenu().clickChangePassword().renderedPage();
    }

    public ChangePasswordPage typeOldPassword(String oldPasswordText)
    {
        clearAndType(getBrowser().findElement(oldPasswordInput), oldPasswordText);
        return this;
    }

    public ChangePasswordPage typeNewPassword(String newPasswordText)
    {
        clearAndType(getBrowser().findElement(newPassword), newPasswordText);
        return this;
    }

    public ChangePasswordPage typeConfirmNewPassword(String confirmNewPasswordText)
    {
        clearAndType(getBrowser().findElement(confirmNewPassword), confirmNewPasswordText);
        return this;
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public boolean isOldPasswordInputDisplayed()
    {
        return getBrowser().isElementDisplayed(oldPasswordInput);
    }

    public ChangePasswordPage assertChangePasswordPageIsOpened()
    {
        assertTrue(getBrowser().getCurrentUrl().contains("change-password"), "Change password page is opened");
        return this;
    }

    public UserProfilePage changePassword(String oldPassword, String newPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(newPassword);
        clickOkButton();
        return (UserProfilePage) new UserProfilePage(browser).renderedPage();
    }

    public ChangePasswordPage changePasswordAndExpectError(String oldPassword, String newPassword, String confirmPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(confirmPassword);
        clickOkButton();
        getBrowser().waitUntilElementVisible(errorPrompt);
        assertEquals(getBrowser().findElement(errorPromptMessage).getText(), language.translate("changeUserPassword.errorPrompt.message"),
            "Error prompt message is correct");
        getBrowser().findElement(errorPromptOKButton).click();
        return this;
    }
}