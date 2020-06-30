package org.alfresco.po.share.toolbar;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        if (envProperties.isAimsEnabled())
        {
            return aimsPage.renderedPage();
        }

        return loginPage.renderedPage();

    }
}
