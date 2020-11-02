package org.alfresco.share;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage2 {

    private ThreadLocal<WebDriver> webDriver;

    public LoginPage2(ThreadLocal<WebDriver> webDriver) {
        this.webDriver = webDriver;
    }

    private By usernameInput = By.cssSelector("[id$='default-username']");
    private By passwordInput = By.cssSelector("[id$='default-password']");
    private By loginButton = By.cssSelector("button[id$='_default-submit-button']");
    private By errorMessage = By.cssSelector(".error");

    public FluentWait<WebDriver> setWaitingTime(int timeOutInSeconds, int pollingTimeInMillis) {
        return new WebDriverWait(webDriver.get(), timeOutInSeconds)
            .pollingEvery(Duration.ofMillis(pollingTimeInMillis))
            .ignoring(ElementClickInterceptedException.class)
            .ignoring(ElementNotInteractableException.class)
            .ignoring(NoSuchElementException.class)
            .ignoring(TimeoutException.class)
            .ignoring(StaleElementReferenceException.class);
    }

    private void enterUsername(String username) {
        try {
            setWaitingTime(60, 2)
                .until(ExpectedConditions.elementToBeClickable(usernameInput)).sendKeys(username);
        }catch (ElementNotInteractableException |StaleElementReferenceException e) {
            System.out.println("Could not type username");
        }
    }

    private void enterPassword(String password) {
        try {
            setWaitingTime(60, 2)
                .until(ExpectedConditions.elementToBeClickable(passwordInput)).sendKeys(password);
        }catch (ElementNotInteractableException | StaleElementReferenceException e) {
            System.out.println("Could not type password");
        }
    }

    private void clickLoginButton() {
        try {
            setWaitingTime(60, 2)
                .until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            System.out.println("Am facut click pe LOGIN button");
        } catch(ElementNotInteractableException | StaleElementReferenceException e) {
            System.out.println("Login button is not clickable");
        }
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getErrorMessage(String expectedMessage) {
        try {
           setWaitingTime(60, 2)
                .until(ExpectedConditions.textToBe(errorMessage,expectedMessage));
           return webDriver.get().findElement(errorMessage).getText();

        }catch (NoSuchElementException e) {
            System.out.println("Catch block " + webDriver.get().findElement(errorMessage).getText());
        }
        return "Nu i acasa";
    }
}
