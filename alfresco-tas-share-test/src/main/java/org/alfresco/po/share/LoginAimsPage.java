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
public class LoginAimsPage extends CommonLoginPage
{
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By submit = By.cssSelector("input.submit");
    private final By alfrescoLogo = By.cssSelector("img.logo");
    private final By errorLogin = By.cssSelector("div.alert-error span.message-text");
    private final By copyright = By.cssSelector(".copyright");

    public LoginAimsPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public CommonLoginPage navigate()
    {
        getUrl(defaultProperties.getShareUrl().toString());
        return new LoginAimsPage(webDriver);
    }

    public void typeUserName(String userName)
    {
        clearAndType(findElement(usernameInput), userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        typeUserName(startCharsUser);
        waitInSeconds(1);
        WebElement userInput = findElement(usernameInput);
        userInput.sendKeys(Keys.ARROW_DOWN);
        userInput.sendKeys(Keys.TAB);
    }

    public void typePassword(String password)
    {
        clearAndType(findElement(passwordInput), password);
    }

    public void clickLogin()
    {
        clickElement(submit);
    }

    public String getAuthenticationError()
    {
        return waitUntilElementIsVisible(errorLogin).getText();
    }

    public boolean isAuthenticationErrorDisplayed()
    {
        return isElementDisplayed(errorLogin);
    }

    public boolean isCopyrightDisplayed()
    {
        return isElementDisplayed(copyright);
    }

    public boolean isLogoDisplayed()
    {
        return isElementDisplayed(alfrescoLogo);
    }

    public String getCopyRightText()
    {
        return findElement(copyright).getText();
    }

    @Override
    public CommonLoginPage assertLoginPageIsOpened()
    {
        log.info("Assert Login Page is displayed");
        assertTrue(isElementDisplayed(usernameInput), "Username input is displayed");
        return this;
    }

    @Override
    public void login(String username, String password)
    {
        log.info(String.format("Login with user: %s and password: %s", username, password));
        typeUserName(username);
        typePassword(password);
        clickLogin();
    }

    @Override
    public CommonLoginPage assertAuthenticationErrorIsDisplayed()
    {
        log.info("Assert authentication error is displayed");
        waitUntilElementIsVisible(errorLogin);
        assertTrue(isAuthenticationErrorDisplayed(), "Authentication error is displayed");
        return this;
    }

    @Override
    public CommonLoginPage assertAuthenticationErrorMessageIsCorrect()
    {
        log.info("Assert authentication error message is correct");
        assertEquals(getAuthenticationError(), language.translate("login.aims.authError"), "Authentication error is correct");
        return this;
    }

    @Override
    public void login(UserModel userModel)
    {
        login(userModel.getUsername(), userModel.getPassword());
    }

    @Override
    public CommonLoginPage assertLoginPageTitleIsCorrect()
    {
        log.info("Assert Login Page Title is correct");
        assertEquals(getTitle(), language.translate("login.aims.pageTitle"), "Login page title is correct");
        return this;
    }
}
