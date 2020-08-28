package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.CommonLoginPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.ChangePasswordPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.utility.TasAisProperties;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

@PageObject
public class ToolbarUserMenu extends Toolbar
{
    @RenderWebElement
    @FindBy (id = "HEADER_USER_MENU_DASHBOARD_text")
    private WebElement userDashboard;

    @RenderWebElement
    @FindBy (id = "HEADER_USER_MENU_PROFILE_text")
    private WebElement myProfileMenu;

    @FindBy (id = "HEADER_USER_MENU_HELP_text")
    private WebElement help;

    @FindBy (id = "HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME_text")
    private WebElement setCurrentPageAsHome;

    @FindBy (id = "HEADER_USER_MENU_SET_DASHBOARD_AS_HOME_text")
    private WebElement setDashBoardAsHome;

    @FindBy (id = "HEADER_USER_MENU_CHANGE_PASSWORD_text")
    private WebElement changePassword;

    @FindBy (id = "HEADER_USER_MENU_LOGOUT_text")
    private WebElement logout;

    private By dropdownMenu = By.id("HEADER_USER_MENU_POPUP_dropdown");
    
    @Autowired
    private LoginPage loginPage;
    
    @Autowired
    private AIMSPage aimsPage;
    
    @Autowired
    private DataAIS dataAIS;

    @Autowired
    private UserDashboardPage userDashboardPage;

    @Autowired
    private UserProfilePage userProfilePage;

    @Autowired
    private ChangePasswordPage changePasswordPage;

    public ToolbarUserMenu assertUserDashboardIsDisplayed()
    {
        LOG.info("User Dashboard link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(userDashboard), "User Dashboard is displayed");
        return this;
    }

    public UserDashboardPage clickUserDashboard()
    {
        LOG.info("Click User Dashboard");
        userDashboard.click();
        return (UserDashboardPage) userDashboardPage.renderedPage();
    }

    public ToolbarUserMenu assertMyProfileIsDisplayed()
    {
        LOG.info("My Profile link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(myProfileMenu), "My Profile link is displayed");
        return this;
    }

    public UserProfilePage clickMyProfile()
    {
        LOG.info("Click My Profile");
        myProfileMenu.click();
        return (UserProfilePage) userProfilePage.renderedPage();
    }

    public ToolbarUserMenu assertHelpIsDisplayed()
    {
        LOG.info("Help link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(help), "Help link is displayed");
        return this;
    }

    public ToolbarUserMenu clickHelp()
    {
        LOG.info("Click Help");
        help.click();
        return this;
    }

    public ToolbarUserMenu assertHelpWillOpenDocumentationPage()
    {
        LOG.info("Assert Alfresco Documentation window is opened");
        getBrowser().switchWindow(1);
        getBrowser().waitUrlContains("https://docs.alfresco.com/", 5);
        Assert.assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");
        getBrowser().closeWindowAndSwitchBack();
        return this;
    }

    public ToolbarUserMenu assertSetCurrentPageAsHomeIsDisplayed()
    {
        LOG.info("Set Current Page As Home link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(setCurrentPageAsHome), "Set Current Page As Home is displayed");
        return this;
    }

    public void clickSetCurrentPageAsHome()
    {
        LOG.info("Click Set current page as home");
        setCurrentPageAsHome.click();
        waitUntilMessageDisappears();
    }

    public ToolbarUserMenu assertUseDashboardAsHomeIsDisplayed()
    {
        LOG.info("Set Current Page As Home link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(setCurrentPageAsHome), "Use Dashboard As Home is displayed");
        return this;
    }

    public void clickSetDashBoardAsHome()
    {
        LOG.info("Click Set Dashboard as Home");
        setDashBoardAsHome.click();
        waitUntilMessageDisappears();
    }

    public ToolbarUserMenu assertChangePasswordIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(changePassword), "Change Password is displayed");
        return this;
    }

    public ChangePasswordPage clickChangePassword()
    {
        LOG.info("Click change password");
        changePassword.click();
        return (ChangePasswordPage) changePasswordPage.renderedPage();
    }

    public ToolbarUserMenu assertLogoutIsDisplayed()
    {
        LOG.info("Change Password link is displayed");
        Assert.assertTrue(browser.isElementDisplayed(logout), "Logout is displayed");
        return this;
    }

    public CommonLoginPage clickLogout()
    {
        LOG.info("Click Logout");
        getBrowser().waitUntilElementVisible(dropdownMenu);
        getBrowser().waitUntilElementClickable(logout).click();
        if (dataAIS.isEnabled())
        {
            return (CommonLoginPage) aimsPage.renderedPage();
        }
        return (CommonLoginPage) loginPage.renderedPage();
    }
}
