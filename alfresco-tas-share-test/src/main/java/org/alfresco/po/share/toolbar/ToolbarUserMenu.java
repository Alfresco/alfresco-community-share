package org.alfresco.po.share.toolbar;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ToolbarUserMenu extends Toolbar
{
    @FindBy(id = "HEADER_USER_MENU_DASHBOARD_text")
    private WebElement userDashboard;

    @FindBy(id = "HEADER_USER_MENU_PROFILE_text")
    private WebElement myProfileMenu;

    @FindBy(id = "HEADER_USER_MENU_HELP_text")
    private WebElement help;

    @FindBy(id = "HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME")
    private WebElement setCurrentPageAsHome;

    @FindBy(id = "HEADER_USER_MENU_SET_DASHBOARD_AS_HOME")
    private WebElement setDashBoardAsHome;

    @FindBy(id = "HEADER_USER_MENU_CHANGE_PASSWORD")
    private WebElement changePassword;

    @FindBy(id = "HEADER_USER_MENU_LOGOUT_text")
    private WebElement logout;

    public boolean isUserDashboardDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(userDashboard);
    }

    public void clickUserDashboard()
    {
        userMenuLink.click();
        userDashboard.click();
    }

    public boolean isMyProfileDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(myProfileMenu);
    }

    public void clickMyProfile()
    {
        userMenuLink.click();
        myProfileMenu.click();
    }

    public boolean isHelpDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(help);
    }

    public void clickHelp()
    {
        userMenuLink.click();
        help.click();
    }

    public boolean isSetCurrentPageAsHomeDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(setCurrentPageAsHome);
    }

    public void clickSetCurrentPageAsHome()
    {
        userMenuLink.click();
        setCurrentPageAsHome.click();
    }

    public boolean isDashBoardAsHomeDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(setDashBoardAsHome);
    }

    public void clickSetDashBoardAsHome()
    {
        userMenuLink.click();
        setDashBoardAsHome.click();
    }

    public boolean isChangePasswordDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(changePassword);
    }

    public void clickChangePassword()
    {
        browser.waitUntilElementClickable(userMenuLink, 5);
        userMenuLink.click();
        changePassword.click();
    }

    public boolean isLogoutDisplayed()
    {
        userMenuLink.click();
        return browser.isElementDisplayed(logout);
    }

    public void clickLogout()
    {
        userMenuLink.click();
        logout.click();
    }
}
