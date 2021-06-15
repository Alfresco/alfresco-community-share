package org.alfresco.po.share.toolbar;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginAimsPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class ToolbarUserMenu extends BasePage
{
    private final By userDashboard = By.id("HEADER_USER_MENU_DASHBOARD_text");
    private final By myProfileMenu = By.id("HEADER_USER_MENU_PROFILE_text");
    private final By help = By.id("HEADER_USER_MENU_HELP_text");
    private final By setCurrentPageAsHome = By.id("HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME_text");
    private final By setDashBoardAsHome = By.id("HEADER_USER_MENU_SET_DASHBOARD_AS_HOME_text");
    private final By changePassword = By.id("HEADER_USER_MENU_CHANGE_PASSWORD_text");
    private final By logout = By.id("HEADER_USER_MENU_LOGOUT_text");

    public ToolbarUserMenu(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ToolbarUserMenu assertUserDashboardIsDisplayed()
    {
        log.info("User Dashboard link is displayed");
        waitUntilElementIsVisible(userDashboard);
        assertTrue(isElementDisplayed(userDashboard), "User Dashboard is displayed");
        return this;
    }

    public UserDashboardPage clickUserDashboard()
    {
        log.info("Click User Dashboard");
        clickElement(userDashboard);
        return new UserDashboardPage(webDriver);
    }

    public ToolbarUserMenu assertMyProfileIsDisplayed()
    {
        log.info("My Profile link is displayed");
        assertTrue(isElementDisplayed(myProfileMenu), "My Profile link is displayed");
        return this;
    }

    public UserProfilePage clickMyProfile()
    {
        log.info("Click My Profile");
        clickElement(myProfileMenu);
        return new UserProfilePage(webDriver);
    }

    public ToolbarUserMenu assertHelpIsDisplayed()
    {
        log.info("Help link is displayed");
        assertTrue(isElementDisplayed(help), "Help link is displayed");
        return this;
    }

    public ToolbarUserMenu clickHelp()
    {
        log.info("Click Help");
        clickElement(help);
        return this;
    }

    public ToolbarUserMenu assertHelpWillOpenDocumentationPage()
    {
        log.info("Assert Alfresco Documentation window is opened");
        waitForSecondWindow();
        switchWindow(1);
        waitUrlContains("https://docs.alfresco.com/", WAIT_10.getValue());
        assertTrue(getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        closeWindowAndSwitchBack();

        return this;
    }

    private void waitForSecondWindow()
    {
        int i = 0;
        while(i < WAIT_10.getValue())
        {
            if(getWindowHandles().size() == 2)
            {
                break;
            }
            else
            {
                i++;
                Utility.waitToLoopTime(1, "Wait for second window to open");
            }
        }
    }

    public ToolbarUserMenu assertSetCurrentPageAsHomeIsDisplayed()
    {
        log.info("Set Current Page As Home link is displayed");
        assertTrue(isElementDisplayed(setCurrentPageAsHome), "Set Current Page As Home is displayed");
        return this;
    }

    public void clickSetCurrentPageAsHome()
    {
        log.info("Click Set current page as home");
        waitUntilElementIsVisible(setCurrentPageAsHome);
        clickElement(setCurrentPageAsHome);
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertUseDashboardAsHomeIsDisplayed()
    {
        log.info("Set Current Page As Home link is displayed");
        assertTrue(isElementDisplayed(setCurrentPageAsHome), "Use Dashboard As Home is displayed");
        return this;
    }

    public void clickSetDashBoardAsHome()
    {
        log.info("Click Set Dashboard as Home");
        waitUntilElementIsVisible(setDashBoardAsHome);
        clickElement(setDashBoardAsHome);
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertChangePasswordIsDisplayed()
    {
        log.info("Change Password link is displayed");
        assertTrue(isElementDisplayed(changePassword), "Change Password is not displayed");
        return this;
    }

    public ToolbarUserMenu assertChangePasswordIsNotDisplayed()
    {
        log.info("Change Password link is not displayed");
        assertTrue(isElementDisplayed(changePassword), "Change Password is displayed");
        return this;
    }

    public ChangePasswordPage clickChangePassword()
    {
        log.info("Click change password");
        waitUntilElementIsVisible(changePassword);
        clickElement(changePassword);
        return new ChangePasswordPage(webDriver);
    }

    public ToolbarUserMenu assertLogoutIsDisplayed()
    {
        log.info("Change Password link is displayed");
        assertTrue(isElementDisplayed(logout), "Logout is displayed");
        return this;
    }

    public CommonLoginPage clickLogout()
    {
        log.info("Click Logout");
        clickElement(logout);
        if (defaultProperties.isAimsEnabled())
        {
            return new LoginAimsPage(webDriver);
        }
        return new LoginPage(webDriver);
    }
}
