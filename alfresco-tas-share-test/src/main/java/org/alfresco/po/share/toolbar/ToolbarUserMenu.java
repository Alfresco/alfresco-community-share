package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.BasePage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;

import static org.testng.Assert.assertTrue;

public class ToolbarUserMenu extends BasePage
{
    @RenderWebElement
    private final By userDashboard = By.id("HEADER_USER_MENU_DASHBOARD_text");
    @RenderWebElement
    private final By myProfileMenu = By.id("HEADER_USER_MENU_PROFILE_text");
    private final By help = By.id("HEADER_USER_MENU_HELP_text");
    private final By setCurrentPageAsHome = By.id("HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME_text");
    private final By setDashBoardAsHome = By.id("HEADER_USER_MENU_SET_DASHBOARD_AS_HOME_text");
    private final By changePassword = By.id("HEADER_USER_MENU_CHANGE_PASSWORD_text");
    private final By logout = By.id("HEADER_USER_MENU_LOGOUT_text");
    private final By dropdownMenu = By.id("HEADER_USER_MENU_POPUP_dropdown");

    public ToolbarUserMenu(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public ToolbarUserMenu assertUserDashboardIsDisplayed()
    {
        LOG.info("User Dashboard link is displayed");
        assertTrue(getBrowser().isElementDisplayed(userDashboard), "User Dashboard is displayed");
        return this;
    }

    public UserDashboardPage clickUserDashboard()
    {
        LOG.info("Click User Dashboard");
        getBrowser().findElement(userDashboard).click();
        return (UserDashboardPage) new UserDashboardPage(browser).renderedPage();
    }

    public ToolbarUserMenu assertMyProfileIsDisplayed()
    {
        LOG.info("My Profile link is displayed");
        assertTrue(getBrowser().isElementDisplayed(myProfileMenu), "My Profile link is displayed");
        return this;
    }

    public UserProfilePage clickMyProfile()
    {
        LOG.info("Click My Profile");
        getBrowser().findElement(myProfileMenu).click();
        return (UserProfilePage) new UserProfilePage(browser).renderedPage();
    }

    public ToolbarUserMenu assertHelpIsDisplayed()
    {
        LOG.info("Help link is displayed");
        assertTrue(getBrowser().isElementDisplayed(help), "Help link is displayed");
        return this;
    }

    public ToolbarUserMenu clickHelp()
    {
        LOG.info("Click Help");
        getBrowser().findElement(help).click();
        return this;
    }

    public ToolbarUserMenu assertHelpWillOpenDocumentationPage()
    {
        LOG.info("Assert Alfresco Documentation window is opened");
        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains("https://docs.alfresco.com/", 10);
        assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        getBrowser().closeWindowAndSwitchBack();

        return this;
    }

    public ToolbarUserMenu assertSetCurrentPageAsHomeIsDisplayed()
    {
        LOG.info("Set Current Page As Home link is displayed");
        assertTrue(getBrowser().isElementDisplayed(setCurrentPageAsHome), "Set Current Page As Home is displayed");
        return this;
    }

    public void clickSetCurrentPageAsHome()
    {
        LOG.info("Click Set current page as home");
        getBrowser().findElement(setCurrentPageAsHome).click();
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertUseDashboardAsHomeIsDisplayed()
    {
        LOG.info("Set Current Page As Home link is displayed");
        assertTrue(getBrowser().isElementDisplayed(setCurrentPageAsHome), "Use Dashboard As Home is displayed");
        return this;
    }

    public void clickSetDashBoardAsHome()
    {
        LOG.info("Click Set Dashboard as Home");
        getBrowser().findElement(setDashBoardAsHome).click();
        waitUntilNotificationMessageDisappears();
    }

    public ToolbarUserMenu assertChangePasswordIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        assertTrue(getBrowser().isElementDisplayed(changePassword), "Change Password is displayed");
        return this;
    }

    public ChangePasswordPage clickChangePassword()
    {
        LOG.info("Click change password");
        getBrowser().findElement(changePassword).click();
        return (ChangePasswordPage) new ChangePasswordPage(browser).renderedPage();
    }

    public ToolbarUserMenu assertLogoutIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        assertTrue(getBrowser().isElementDisplayed(logout), "Logout is displayed");
        return this;
    }

    public CommonLoginPage clickLogout()
    {
        LOG.info("Click Logout");
        getBrowser().waitUntilElementVisible(dropdownMenu);
        getBrowser().waitUntilElementClickable(logout).click();
        if (properties.isAimsEnabled())
        {
            return (CommonLoginPage) new AIMSPage(browser).renderedPage();
        }
        return (CommonLoginPage) new LoginPage(browser).renderedPage();
    }
}
