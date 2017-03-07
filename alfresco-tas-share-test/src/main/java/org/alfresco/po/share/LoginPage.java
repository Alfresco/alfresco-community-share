package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.alfresco.po.annotation.RenderWebElement;
import org.alfresco.po.share.user.UserDashboardPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.TextBlock;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Simple Page Object class
 * 
 * @author Paul.Brodner
 */
@PageObject
public class LoginPage extends HtmlPage
{
    @Autowired
    private UserDashboardPage userDashboardPage;

    @FindBy(css = "[id$='default-username']")
    private WebElement usernameInput;

    @FindBy(css = "[id$='default-password']")
    private WebElement passwordInput;

    @RenderWebElement
    @FindBy(css = "button[id*='default-submit']")
    private WebElement submit;

    @RenderWebElement
    @FindBy(css = ".theme-company-logo")
    private Image alfrescoLogo;

    @FindBy(css = ".copy")
    private TextBlock copyright;

    @FindBy(css = ".error")
    private WebElement errorLogin;

    public String getRelativePath()
    {
        return "/page";
    }

    public void navigate()
    {
        browser.navigate().to(properties.getShareUrl().toString());
    }

    /**
     * Type user name
     * 
     * @param userName
     */
    public void typeUserName(String userName)
    {
        usernameInput.clear();
        usernameInput.sendKeys(userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        typeUserName(startCharsUser);
        browser.waitInSeconds(1);
        usernameInput.sendKeys(Keys.ARROW_DOWN);
        usernameInput.sendKeys(Keys.TAB);
    }

    /**
     * Type password
     * 
     * @param password to be filled in
     */
    public void typePassword(String password)
    {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    /**
     * Click login button
     */
    public void clickLogin()
    {
        submit.click();
    }

    /**
     * Login on Share using login form
     * 
     * @param username to be filled in
     * @param password to be filled in
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public UserDashboardPage login(String username, String password)
    {
        typeUserName(username);
        typePassword(password);
        clickLogin();

        return (UserDashboardPage) userDashboardPage.renderedPage();
    }

    /**
     * Get the error when the login fails
     * 
     * @return String error message
     */
    public String getAuthenticationError()
    {
        browser.waitUntilWebElementIsDisplayedWithRetry(errorLogin);
        return errorLogin.getText();
    }

    /**
     * Verify if the login error is displayed
     * 
     * @return true if displayed
     */
    public boolean isAuthenticationErrorDisplayed()
    {
        return browser.isElementDisplayed(errorLogin);
    }

    /**
     * Verify if copyright is displayed
     * 
     * @return true if displayed
     */
    public boolean isCopyrightDisplayed()
    {
        return copyright.isDisplayed();
    }
}