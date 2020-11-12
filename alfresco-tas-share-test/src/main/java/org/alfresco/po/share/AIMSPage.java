package org.alfresco.po.share;

import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AIMSPage extends CommonLoginPage
{
    private By usernameInput = By.id("username");
    private By passwordInput = By.id("password");
    @RenderWebElement
    private By submit = By.cssSelector("input.submit");
    @RenderWebElement
    private By alfrescoLogo = By.cssSelector("img.logo");
    private By errorLogin = By.cssSelector("div.alert-error span.message-text");
    private By copyright = By.cssSelector(".copyright");

    public AIMSPage(ThreadLocal<WebBrowser> browser)
    {
        this.browser = browser;
    }

    public CommonLoginPage navigate()
    {
        getBrowser().navigate().to(properties.getShareUrl().toString());
        return (AIMSPage) renderedPage();
    }

    public void typeUserName(String userName)
    {
        clearAndType(getBrowser().findElement(usernameInput), userName);
    }

    public void autoCompleteUsername(String startCharsUser)
    {
        typeUserName(startCharsUser);
        getBrowser().waitInSeconds(1);
        WebElement userInput = getBrowser().findElement(usernameInput);
        userInput.sendKeys(Keys.ARROW_DOWN);
        userInput.sendKeys(Keys.TAB);
    }

    public void typePassword(String password)
    {
        clearAndType(getBrowser().findElement(passwordInput), password);
    }

    public void clickLogin()
    {
        getBrowser().waitUntilElementClickable(submit).click();
    }

    public AIMSPage loginFailed(String username, String password)
    {
        typeUserName(username);
        typePassword(password);
        clickLogin();
        return (AIMSPage) this.renderedPage();
    }

    public String getAuthenticationError()
    {
        return getBrowser().waitUntilElementVisible(errorLogin).getText();
    }

    public boolean isAuthenticationErrorDisplayed()
    {
        return getBrowser().isElementDisplayed(errorLogin);
    }

    public boolean isCopyrightDisplayed()
    {
        return getBrowser().isElementDisplayed(copyright);
    }

    public boolean isLogoDisplayed()
    {
        return getBrowser().isElementDisplayed(alfrescoLogo);
    }

    public String getCopyRightText()
    {
        return getBrowser().findElement(copyright).getText();
    }

    @Override
    public CommonLoginPage assertLoginPageIsOpened()
    {
        LOG.info("Assert Login Page is displayed");
        assertTrue(getBrowser().isElementDisplayed(usernameInput), "Username input is displayed");
        return this;
    }

    @Override
    public void login(String username, String password)
    {
        LOG.info(String.format("Login with user: %s and password: %s", username, password));
        typeUserName(username);
        typePassword(password);
        clickLogin();
    }

    @Override
    public CommonLoginPage assertAuthenticationErrorIsDisplayed()
    {
        LOG.info("Assert authentication error is displayed");
        getBrowser().waitUntilElementVisible(errorLogin);
        assertTrue(isAuthenticationErrorDisplayed(), "Authentication error is displayed");
        return this;
    }

    @Override
    public CommonLoginPage assertAuthenticationErrorMessageIsCorrect()
    {
        LOG.info("Assert authentication error message is correct");
        assertEquals(getAuthenticationError(), language.translate("login.authError"), "Authentication error is correct");
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
        LOG.info("Assert Login Page Title is correct");
        assertEquals(getPageTitle(), language.translate("login.pageTitle"), "Login page title is correct");
        return this;
    }
}
