package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class ChangePasswordPage extends SharePage<ChangePasswordPage> implements AccessibleByMenuBar
{
    @Autowired
    private UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy (css = "input[id$='default-oldpassword']")
    private WebElement oldPasswordInput;

    @RenderWebElement
    @FindBy (css = "input[id$='default-newpassword1']")
    private TextInput newPassword;

    @RenderWebElement
    @FindBy (css = "input[id$='default-newpassword2']")
    private TextInput confirmNewPassword;

    @RenderWebElement
    @FindBy (css = "button[id$='default-button-ok-button']")
    private WebElement okButton;

    @FindBy (id = "prompt")
    private WebElement errorPrompt;

    @FindBy (css = "#prompt .bd")
    private WebElement errorPromptMessage;

    @FindBy (css = "#prompt button")
    private WebElement errorPromptOKButton;

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

    @SuppressWarnings ("unchecked")
    @Override
    public ChangePasswordPage navigateByMenuBar()
    {
        toolbar.clickUserMenu().clickChangePassword();
        return (ChangePasswordPage) renderedPage();
    }

    public void typeOldPassword(String oldPasswordText)
    {
        oldPasswordInput.clear();
        oldPasswordInput.sendKeys(oldPasswordText);
    }

    public void typeNewPassword(String newPasswordText)
    {
        newPassword.clear();
        newPassword.sendKeys(newPasswordText);
    }

    public void typeConfirmNewPassword(String confirmNewPasswordText)
    {
        confirmNewPassword.clear();
        confirmNewPassword.sendKeys(confirmNewPasswordText);
    }

    public void clickOkButton()
    {
        getBrowser().waitUntilElementClickable(okButton).click();
    }

    public boolean isOldPasswordInputDisplayed()
    {
        return browser.isElementDisplayed(oldPasswordInput);
    }

    public ChangePasswordPage assertChangePasswordPageIsOpened()
    {
        Assert.assertTrue(browser.getCurrentUrl().contains("change-password"), "Change password page is opened");
        return this;
    }

    public UserProfilePage changePassword(String oldPassword, String newPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(newPassword);
        clickOkButton();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public ChangePasswordPage changePasswordAndExpectError(String oldPassword, String newPassword, String confirmPassword)
    {
        typeOldPassword(oldPassword);
        typeNewPassword(newPassword);
        typeConfirmNewPassword(confirmPassword);
        clickOkButton();
        browser.waitUntilElementVisible(errorPrompt);
        Assert.assertEquals(errorPromptMessage.getText(), language.translate("changeUserPassword.errorPrompt.message"),
            "Error prompt message is correct");
        errorPromptOKButton.click();
        return this;
    }
}