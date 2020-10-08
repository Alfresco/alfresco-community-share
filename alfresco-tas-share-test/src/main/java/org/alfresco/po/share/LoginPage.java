package org.alfresco.po.share;

import org.alfresco.common.Language;
import org.alfresco.common.Utils;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.TextBlock;

/**
 * Simple Page Object class
 *
 * @author Paul.Brodner
 */
@PageObject
public class LoginPage extends CommonLoginPage
{
    @RenderWebElement
    @FindBy(css = "[id$='default-username']")
    private WebElement usernameInput;

    @RenderWebElement
    @FindBy(css = "[id$='default-password']")
    private WebElement passwordInput;

    @RenderWebElement
    @FindBy(css = "button[id$='_default-submit-button']")
    private WebElement submit;

    @FindBy(css = ".theme-company-logo")
    private WebElement alfrescoLogo;

    @FindBy(css = ".login-copy")
    private TextBlock copyright;

    @FindBy(css = ".error")
    private WebElement errorLogin;

    @FindBy(css = ".login-tagline")
    private WebElement newTrademark;

    @FindBy(css = ".sticky-wrapper")
    private WebElement stickyWrapper;

    @FindBy(css = ".sticky-footer")
    private WebElement stickyFooter;

    @FindBy(css = "#Share")
    private WebElement bodyShare;

    @FindBy(css = " .product-tagline")
    private WebElement productTagline;

    @FindBy(css = ".sticky-push")
    private WebElement stickyPush;

    @FindBy(css = ".product-name")
    private WebElement alfrescoShare;

    @FindBy(css = ".theme-trademark")
    private WebElement trademark;

    @FindBy(css = "theme-company-logo.logo-ent")
    private WebElement oldLogo;

    @Autowired
    protected Language language;

    public CommonLoginPage navigate()
    {
        LOG.info("Navigate to Login Page");
        browser.navigate().to(properties.getShareUrl().toString());
        return (LoginPage) renderedPage();
    }

    public CommonLoginPage assertLoginPageIsOpened()
    {
        LOG.info("Assert Login Page is displayed");
        Assert.assertTrue(browser.isElementDisplayed(usernameInput), "Username input is displayed");
        return this;
    }

    /**
     * Type user name
     *
     * @param userName
     */
    public void typeUserName(String userName)
    {
        browser.waitUntilElementVisible(usernameInput);
        usernameInput.clear();
        usernameInput.sendKeys(userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        LOG.info(String.format("Autocomplete user %s", startCharsUser));
        typeUserName(startCharsUser);
        browser.waitInSeconds(1);
        usernameInput.sendKeys(Keys.ARROW_DOWN);
        usernameInput.sendKeys(Keys.TAB);
    }

    /**
     * Type password
     *
     * @param password
     *            to be filled in
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
        browser.waitUntilElementClickable(submit).click();
    }

    /**
     * Login on Share using login form
     *
     * @param username
     *            to be filled in
     * @param password
     *            to be filled in
     */
    public void login(String username, String password)
    {
        LOG.info(String.format("Login with user: %s and password: %s", username, password));
        typeUserName(username);
        typePassword(password);
        clickLogin();
    }

    /**
     * Login on Share using login form
     *
     * @param userModel
     *            to be filled in
     */
    public void login(UserModel userModel)
    {
        login(userModel.getUsername(), userModel.getPassword());
    }

    /**
     * Get the error when the login fails
     *
     * @return String error message
     */
    public String getAuthenticationError()
    {
        return browser.waitUntilElementVisible(errorLogin).getText();
    }

    public CommonLoginPage assertAuthenticationErrorIsDisplayed()
    {
        LOG.info("Assert authentication error is displayed");
        browser.waitUntilElementVisible(errorLogin);
        Assert.assertTrue(isAuthenticationErrorDisplayed(), "Authentication error is displayed");
        return this;
    }

    public CommonLoginPage assertAuthenticationErrorMessageIsCorrect()
    {
        LOG.info("Assert authentication error message is correct");
        Assert.assertEquals(getAuthenticationError(), language.translate("login.authError"), "Authentication error is correct");
        return this;
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

    /**
     * Verify if alfresco logo is displayed
     *
     * @return true if displayed
     */
    public boolean isLogoDisplayed()
    {
        return alfrescoLogo.isDisplayed();
    }

    /**
     * Verify if the old Alfresco logo is displayed
     *
     * @return true if displayed
     */

    public boolean isOldLogoDisplayed()
    {
        return getBrowser().isElementDisplayed(oldLogo);
    }

    /**
     * Verify if alfresco 'make business flow' is displayed
     *
     * @return true if displayed
     */
    public boolean isMakeBusinessFlowDisplayed()
    {
        return newTrademark.isDisplayed();
    }

    /**
     * Verify if 'Simple+Smart' is displayed
     *
     * @return true if displayed
     */

    public boolean isSimpleSmartDisplayed()
    {
        return getBrowser().isElementDisplayed(trademark);
    }

    public String[] getBackgroundColour()
    {
        String colourBodyShare = bodyShare.getCssValue("background-color");
        String colourProductTagline = productTagline.getCssValue("color");
        String[] colours = new String[] { colourBodyShare, colourProductTagline };
        return colours;
    }

    public String getAlfrescoShareColour()
    {
        String colour = alfrescoShare.getCssValue("color");
        return colour;
    }

    /**
     * Get the text from the copyright
     *
     * @return String copyright text
     */
    public String getCopyRightText()
    {
        return copyright.getText();
    }

    public String getSignInButtonColor()
    {
        return submit.getCssValue("color").toString();
    }
    
    @Override
    public CommonLoginPage assertLoginPageTitleIsCorrect()
    {
        LOG.info("Assert Login Page Title is correct");
        Assert.assertEquals(getPageTitle(), language.translate("login.pageTitle"), "Login page title is correct");
        return this;
    }

}