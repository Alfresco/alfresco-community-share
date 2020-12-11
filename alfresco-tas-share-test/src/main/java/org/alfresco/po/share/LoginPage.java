package org.alfresco.po.share;

import static org.alfresco.common.Wait.WAIT_30;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class LoginPage extends CommonLoginPage
{
    @RenderWebElement
    private By usernameInput = By.cssSelector("[id$='default-username']");
    @RenderWebElement
    private By passwordInput = By.cssSelector("[id$='default-password']");
    @RenderWebElement
    private final By submit = By.cssSelector("button[id$='_default-submit-button']");
    private final By alfrescoLogo = By.cssSelector(".theme-company-logo");
    private final By copyright = By.cssSelector(".login-copy");
    private final By errorLogin = By.cssSelector(".error");
    private final By newTrademark = By.cssSelector(".login-tagline");
    private final By bodyShare = By.id("Share");
    private final By productTagline = By.cssSelector(".product-tagline");
    private final By alfrescoShare = By.cssSelector(".product-name");
    private final By trademark = By.cssSelector(".theme-trademark");

    public LoginPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    public CommonLoginPage navigate()
    {
        LOG.info("Navigate to Login Page");
        getBrowser().get(properties.getShareUrl().toString());
        return (LoginPage) renderedPage();
    }

    public CommonLoginPage assertLoginPageIsOpened()
    {
        LOG.info("Assert Login Page is displayed");
        getBrowser().waitUntilElementVisible(usernameInput);
        assertTrue(getBrowser().isElementDisplayed(usernameInput), "Username input is displayed");
        return this;
    }

    /**
     * Type user name
     *
     * @param userName
     */
    public void typeUserName(String userName)
    {
        getBrowser().waitUntilElementVisible(usernameInput);
        WebElement userInput = getBrowser().findElement(usernameInput);
        clearAndType(userInput, userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        LOG.info(String.format("Autocomplete user %s", startCharsUser));
        typeUserName(startCharsUser);
        getBrowser().waitInSeconds(1);
        WebElement userInput = getBrowser().findElement(usernameInput);
        userInput.sendKeys(Keys.ARROW_DOWN);
        userInput.sendKeys(Keys.TAB);
    }

    public void typePassword(String password)
    {
        WebElement passwordElement = getBrowser().findElement(passwordInput);
        clearAndType(passwordElement, password);
    }

    public void clickLogin()
    {
        getBrowser().waitUntilElementClickable(submit).click();
    }

    public void login(String username, String password)
    {
        LOG.info("Login with user: {}", username);
        typeUserName(username);
        typePassword(password);
        clickLogin();
    }

    public void login(UserModel userModel)
    {
        login(userModel.getUsername(), userModel.getPassword());
    }

    public String getAuthenticationError()
    {
        return getBrowser().waitUntilElementVisible(errorLogin).getText();
    }

    public CommonLoginPage assertAuthenticationErrorIsDisplayed()
    {
        LOG.info("Assert authentication error is displayed");
        getBrowser().waitUntilElementVisible(errorLogin);
        assertTrue(isAuthenticationErrorDisplayed(), "Authentication error is displayed");
        return this;
    }

    public CommonLoginPage assertAuthenticationErrorMessageIsCorrect()
    {
        LOG.info("Assert authentication error message is correct");
        assertEquals(getAuthenticationError(), language.translate("login.authError"), "Authentication error is correct");
        return this;
    }

    public boolean isAuthenticationErrorDisplayed()
    {
        return getBrowser().isElementDisplayed(errorLogin);
    }

    public boolean isCopyrightDisplayed()
    {
        return getBrowser().findElement(copyright).isDisplayed();
    }

    public boolean isMakeBusinessFlowDisplayed()
    {
        return getBrowser().findElement(newTrademark).isDisplayed();
    }

    public boolean isSimpleSmartDisplayed()
    {
        return getBrowser().isElementDisplayed(trademark);
    }

    public String[] getBackgroundColour()
    {
        String colourBodyShare = getBrowser().findElement(bodyShare).getCssValue("background-color");
        String colourProductTagline = getBrowser().findElement(productTagline).getCssValue("color");
        return new String[] { colourBodyShare, colourProductTagline };
    }

    public String getAlfrescoShareColour()
    {
        return getBrowser().findElement(alfrescoShare).getCssValue("color");
    }

    public String getCopyRightText()
    {
        return getBrowser().findElement(copyright).getText();
    }

    public String getSignInButtonColor()
    {
        return getBrowser().findElement(submit).getCssValue("color");
    }

    public boolean isLogoDisplayed()
    {
        return getBrowser().isElementDisplayed(alfrescoLogo);
    }
    
    @Override
    public CommonLoginPage assertLoginPageTitleIsCorrect()
    {
        LOG.info("Assert Login Page Title is correct");
        assertEquals(getPageTitle(), language.translate("login.pageTitle"), "Login page title is correct");
        return this;
    }
}