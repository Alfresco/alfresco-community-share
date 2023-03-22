package org.alfresco.po.share;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.utility.model.UserModel;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class LoginPage extends CommonLoginPage
{
    private final By usernameInput = By.cssSelector("[id$='default-username']");
    private final By passwordInput = By.cssSelector("[id$='default-password']");
    private final By submit = By.cssSelector("button[id$='_default-submit-button']");
    private final By alfrescoLogo = By.cssSelector(".theme-company-logo");
    private final By copyright = By.cssSelector(".login-copy");
    private final By errorLogin = By.cssSelector(".error");
    private final By bodyShare = By.id("Share");
    private final By productTagline = By.cssSelector(".product-tagline");
    private final By alfrescoShare = By.cssSelector(".product-name");
    private final By trademark = By.cssSelector(".theme-trademark");

    public LoginPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public CommonLoginPage navigate()
    {
        log.info("Navigate to Login Page");
        webDriver.get().get(defaultProperties.getShareUrl().toString());
        return new LoginPage(webDriver);
    }

    public CommonLoginPage assertLoginPageIsOpened()
    {
        log.info("Assert Login Page is displayed");
        waitUntilElementIsVisible(usernameInput);
        assertTrue(isElementDisplayed(usernameInput), "Username input is displayed");
        return this;
    }

    /**
     * Type user name
     *
     * @param userName
     */
    public void typeUserName(String userName)
    {
        waitUntilElementIsVisible(usernameInput);
        WebElement userInput = findElement(usernameInput);
        clearAndType(userInput, userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        log.info("Autocomplete user {}", startCharsUser);
        typeUserName(startCharsUser);
        waitInSeconds(1);
        WebElement userInput = findElement(usernameInput);
        userInput.sendKeys(Keys.ARROW_DOWN);
        userInput.sendKeys(Keys.TAB);
    }

    public void typePassword(String password)
    {
        WebElement passwordElement = findElement(passwordInput);
        clearAndType(passwordElement, password);
    }

    public void clickLogin()
    {
        clickElement(submit);
    }

    public void login(String username, String password)
    {
        log.info("Login with user: {}", username);
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
        return waitUntilElementIsVisible(errorLogin).getText();
    }

    public CommonLoginPage assertAuthenticationErrorIsDisplayed()
    {
        log.info("Assert authentication error is displayed");
        waitUntilElementIsVisible(errorLogin);
        assertTrue(isAuthenticationErrorDisplayed(), "Authentication error is displayed");
        return this;
    }

    public CommonLoginPage assertAuthenticationErrorMessageIsCorrect()
    {
        log.info("Assert authentication error message is correct");
        assertEquals(getAuthenticationError(), language.translate("login.authError"), "Authentication error is correct");
        return this;
    }

    public boolean isAuthenticationErrorDisplayed()
    {
        waitInSeconds(3);
        return isElementDisplayed(errorLogin);
    }

    public boolean isSimpleSmartDisplayed()
    {
        return isElementDisplayed(trademark);
    }

    public String[] getBackgroundColour()
    {
        String colourBodyShare = findElement(bodyShare).getCssValue("background-color");
        String colourProductTagline = findElement(productTagline).getCssValue("color");
        return new String[] { colourBodyShare, colourProductTagline };
    }

    public String getAlfrescoShareColour()
    {
        return findElement(alfrescoShare).getCssValue("color");
    }

    public String getCopyRightText()
    {
        return getElementText(copyright);
    }

    public String getSignInButtonColor()
    {
        return findElement(submit).getCssValue("color");
    }

    public boolean isLogoDisplayed()
    {
        return isElementDisplayed(alfrescoLogo);
    }
    @Override
    public CommonLoginPage assertLoginPageTitleIsCorrect()
    {
        log.info("Assert Login Page Title is correct");
        assertEquals(getPageTitle(), language.translate("login.pageTitle"), "Login page title is correct");
        return this;
    }
}
