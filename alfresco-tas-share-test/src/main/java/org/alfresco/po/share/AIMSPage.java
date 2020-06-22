package org.alfresco.po.share;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import ru.yandex.qatools.htmlelements.element.Image;
import ru.yandex.qatools.htmlelements.element.TextBlock;

@PageObject
public class AIMSPage extends HtmlPage {

	@FindBy(id = "username")
	private WebElement usernameInput;

	@FindBy(id = "password")
	private WebElement passwordInput;

	@RenderWebElement
	@FindBy(css = "input.submit")
	private WebElement submit;

	@RenderWebElement
	@FindBy(css = "img.logo")
	private Image alfrescoLogo;

	@FindBy(css = "div.alert-error span.message-text")
	private WebElement errorLogin;

	@FindBy(css = ".copyright")
	private TextBlock copyright;
	
	@Autowired
	private UserDashboardPage userDashboard;

	public void navigate() {
		browser.navigate().to(properties.getShareUrl().toString());
		renderedPage();
	}

	/**
	 * Type user name
	 *
	 * @param userName
	 */
	public void typeUserName(String userName) {
		usernameInput.clear();
		usernameInput.sendKeys(userName);
	}

	public void autoCompleteUsername(String startCharsUser) {
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
	public void typePassword(String password) {
		passwordInput.clear();
		passwordInput.sendKeys(password);
	}

	/**
	 * Click login button
	 */
	public void clickLogin() {
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
	public UserDashboardPage loginSucced(String username, String password) {
		typeUserName(username);
		typePassword(password);
		clickLogin();
		return (UserDashboardPage) userDashboard.renderedPage();
	}
	
	
	public AIMSPage loginFailed(String username, String password) {
		typeUserName(username);
		typePassword(password);
		clickLogin();
		return (AIMSPage) this.renderedPage();
	}

	/**
	 * Get the error when the login fails
	 *
	 * @return String error message
	 */
	public String getAuthenticationError() {
		browser.waitUntilWebElementIsDisplayedWithRetry(errorLogin);
		return errorLogin.getText();
	}

	/**
	 * Verify if the login error is displayed
	 *
	 * @return true if displayed
	 */
	public boolean isAuthenticationErrorDisplayed() {
		return browser.isElementDisplayed(errorLogin);
	}

	/**
	 * Verify if copyright is displayed
	 *
	 * @return true if displayed
	 */
	public boolean isCopyrightDisplayed() {
		return copyright.isDisplayed();
	}

	/**
	 * Verify if alfresco logo is displayed
	 *
	 * @return true if displayed
	 */
	public boolean isLogoDisplayed() {
		return alfrescoLogo.isDisplayed();
	}

	/**
	 * Get the text from the copyright
	 *
	 * @return String copyright text
	 */
	public String getCopyRightText() {
		return copyright.getText();
	}

}
