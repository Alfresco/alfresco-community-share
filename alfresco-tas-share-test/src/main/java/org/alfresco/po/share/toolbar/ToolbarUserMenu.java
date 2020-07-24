package org.alfresco.po.share.toolbar;

import org.alfresco.po.share.AIMSPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.utility.TasAisProperties;
import org.alfresco.utility.data.auth.DataAIS;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

@PageObject
public class ToolbarUserMenu extends Toolbar
{
    @FindBy (id = "HEADER_USER_MENU_DASHBOARD_text")
    private WebElement userDashboard;

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
    DataAIS dataAIS;

    public boolean isUserDashboardDisplayed()
    {
        getBrowser().findElement(By.cssSelector("div[id='HEADER_USER_MENU_POPUP'] span.alfresco-menus-AlfMenuBarPopup__arrow")).click();
        getBrowser().waitUntilElementVisible(By.cssSelector("div[id='HEADER_USER_MENU']"));
        return browser.isElementDisplayed(userDashboard);
    }

    public void clickUserDashboard()
    {
        userMenuLink.click();
        userDashboard.click();
    }

    public boolean isMyProfileDisplayed()
    {
        browser.mouseOver(userMenuLink);
        userMenuLink.sendKeys(Keys.ENTER);
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(myProfileMenu);
    }

    public void clickMyProfile()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        myProfileMenu.click();
    }

    public boolean isHelpDisplayed()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(help);
    }

    public void clickHelp()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        help.click();
    }

    public boolean isSetCurrentPageAsHomeDisplayed()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(setCurrentPageAsHome);
    }

    public void clickSetCurrentPageAsHome()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        setCurrentPageAsHome.click();
        waitUntilMessageDisappears();
    }

    public boolean isDashBoardAsHomeDisplayed()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(setDashBoardAsHome);
    }

    public void clickSetDashBoardAsHome()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        setDashBoardAsHome.click();
    }

    public boolean isChangePasswordDisplayed()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(changePassword);
    }

    public void clickChangePassword()
    {
        browser.waitUntilElementClickable(userMenuLink, 5);
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        changePassword.click();
    }

    public boolean isLogoutDisplayed()
    {
        userMenuLink.click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        return browser.isElementDisplayed(logout);
    }

    public HtmlPage clickLogout()
    {
        getBrowser().waitUntilElementClickable(userMenuLink).click();
        getBrowser().waitUntilElementVisible(dropdownMenu);
        getBrowser().waitUntilElementClickable(logout).click();
        if (dataAIS.isEnabled())
        {
            return aimsPage.renderedPage();
        }

        return loginPage.renderedPage();

    }
}
