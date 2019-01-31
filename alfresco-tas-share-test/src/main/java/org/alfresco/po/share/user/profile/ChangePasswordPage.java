package org.alfresco.po.share.user.profile;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.navigation.AccessibleByMenuBar;
import org.alfresco.po.share.toolbar.ToolbarUserMenu;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * @author bogdan.bocancea
 */
@PageObject
public class ChangePasswordPage extends SharePage<ChangePasswordPage> implements AccessibleByMenuBar
{
    @Autowired
    ToolbarUserMenu toolbarUserMenu;

    @Autowired
    private UserProfilePage userProfilePage;

    @RenderWebElement
    @FindBy(css = "input[id$='default-oldpassword']")
    private WebElement oldPasswordInput;

    @RenderWebElement
    @FindBy(css = "input[id$='default-newpassword1']")
    private TextInput newPassword;

    @RenderWebElement
    @FindBy(css = "input[id$='default-newpassword2']")
    private TextInput confirmNewPassword;

    @RenderWebElement
    @FindBy(css = "button[id*='default-button-ok']")
    private WebElement okButton;

    @Override
    public String getRelativePath()
    {
        return setRelativePathForUserPage("share/page/user/%s/change-password", getUserName());
    }

    public ChangePasswordPage navigate(String userName)
    {
        setUserName(userName);
        return (ChangePasswordPage) navigate();
    }

    /**
     * Open Change Password page from the my profile navigation links
     * 
     * @param myProfileNavigation
     * @return {@link ChangePasswordPage}
     */
    public ChangePasswordPage openFromNavigationLink(MyProfileNavigation myProfileNavigation)
    {
        myProfileNavigation.clickChangePassword();
        return (ChangePasswordPage) this.renderedPage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage navigateByMenuBar()
    {
        toolbarUserMenu.clickChangePassword();
        return (ChangePasswordPage) renderedPage();
    }

    public void typeOldPassword(String oldPasswordText)
    {
        oldPasswordInput.clear();
        oldPasswordInput.sendKeys(oldPasswordText);
    }

    public void typeNewPassword(String newPasswordText)
    {
        newPassword.sendKeys(newPasswordText);
    }

    public void typeConfirmNewPassword(String confirmNewPasswordText)
    {
        confirmNewPassword.sendKeys(confirmNewPasswordText);
    }

    public UserProfilePage clickOkButton()
    {
        getBrowser().waitUntilElementVisible(okButton);
        getBrowser().waitUntilElementClickable(okButton).click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public boolean isOldPasswordInputDisplayed()
    {
        return browser.isElementDisplayed(oldPasswordInput);
    }
}