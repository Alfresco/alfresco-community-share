package org.alfresco.po.share.toolbar;

import static org.alfresco.common.Wait.WAIT_10;
import static org.testng.Assert.assertTrue;

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

public class ToolbarUserMenu extends BasePage
{
    private final By userDashboard = By.id("HEADER_USER_MENU_DASHBOARD_text");
    private final By myProfileMenu = By.id("HEADER_USER_MENU_PROFILE_text");
    private final By help = By.id("HEADER_USER_MENU_HELP_text");
    private final By setCurrentPageAsHome = By.id("HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME_text");
    private final By setDashBoardAsHome = By.id("HEADER_USER_MENU_SET_DASHBOARD_AS_HOME_text");
    private final By changePassword = By.id("HEADER_USER_MENU_CHANGE_PASSWORD_text");
    private final By logout = By.id("HEADER_USER_MENU_LOGOUT_text");
    private final By dropdownMenu = By.id("HEADER_USER_MENU_POPUP_dropdown");

    public ToolbarUserMenu(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public ToolbarUserMenu assertUserDashboardIsDisplayed()
    {
        LOG.info("User Dashboard link is displayed");
        webElementInteraction.waitUntilElementIsVisible(userDashboard);
        assertTrue(webElementInteraction.isElementDisplayed(userDashboard), "User Dashboard is displayed");
        return this;
    }

    public UserDashboardPage clickUserDashboard()
    {
        LOG.info("Click User Dashboard");
//        webElementInteraction.waitUntilElementIsVisible(userDashboard);
        webElementInteraction.clickElement(userDashboard);
        return new UserDashboardPage(webDriver);
    }

    public ToolbarUserMenu assertMyProfileIsDisplayed()
    {
        LOG.info("My Profile link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(myProfileMenu), "My Profile link is displayed");
        return this;
    }

    public UserProfilePage clickMyProfile()
    {
        LOG.info("Click My Profile");
//        webElementInteraction.waitUntilElementIsVisible(myProfileMenu);
        webElementInteraction.clickElement(myProfileMenu);
        return new UserProfilePage(webDriver);
    }

    public ToolbarUserMenu assertHelpIsDisplayed()
    {
        LOG.info("Help link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(help), "Help link is displayed");
        return this;
    }

    public ToolbarUserMenu clickHelp()
    {
        LOG.info("Click Help");
//        webElementInteraction.waitUntilElementIsVisible(help);
        webElementInteraction.clickElement(help);
        return this;
    }

    public ToolbarUserMenu assertHelpWillOpenDocumentationPage()
    {
        LOG.info("Assert Alfresco Documentation window is opened");
        waitForSecondWindow();
        webElementInteraction.switchWindow(1);
        webElementInteraction.waitUrlContains("https://docs.alfresco.com/", WAIT_10.getValue());
        assertTrue(webElementInteraction.getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        webElementInteraction.closeWindowAndSwitchBack();

        return this;
    }

    private void waitForSecondWindow()
    {
        int i = 0;
        while(i < WAIT_10.getValue())
        {
            if(webElementInteraction.getWindowHandles().size() == 2)
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
        LOG.info("Set Current Page As Home link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(setCurrentPageAsHome), "Set Current Page As Home is displayed");
        return this;
    }

    public void clickSetCurrentPageAsHome()
    {
        LOG.info("Click Set current page as home");
        webElementInteraction.waitUntilElementIsVisible(setCurrentPageAsHome);
        webElementInteraction.clickElement(setCurrentPageAsHome);
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertUseDashboardAsHomeIsDisplayed()
    {
        LOG.info("Set Current Page As Home link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(setCurrentPageAsHome), "Use Dashboard As Home is displayed");
        return this;
    }

    public void clickSetDashBoardAsHome()
    {
        LOG.info("Click Set Dashboard as Home");
        webElementInteraction.waitUntilElementIsVisible(setDashBoardAsHome);
        webElementInteraction.clickElement(setDashBoardAsHome);
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertChangePasswordIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(changePassword), "Change Password is not displayed");
        return this;
    }

    public ToolbarUserMenu assertChangePasswordIsNotDisplayed()
    {
        LOG.info("Change Password link is not displayed");
        assertTrue(webElementInteraction.isElementDisplayed(changePassword), "Change Password is displayed");
        return this;
    }

    public ChangePasswordPage clickChangePassword()
    {
        LOG.info("Click change password");
        webElementInteraction.waitUntilElementIsVisible(changePassword);
        webElementInteraction.clickElement(changePassword);
        return new ChangePasswordPage(webDriver);
    }

    public ToolbarUserMenu assertLogoutIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        assertTrue(webElementInteraction.isElementDisplayed(logout), "Logout is displayed");
        return this;
    }

    public CommonLoginPage clickLogout()
    {
        LOG.info("Click Logout");
        webElementInteraction.clickElement(logout);
        if (defaultProperties.get().isAimsEnabled())
        {
            return new LoginAimsPage(webDriver);
        }
        return new LoginPage(webDriver);
    }
}
