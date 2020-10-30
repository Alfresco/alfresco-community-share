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

public class UserDashboardPage {

    private By headerPage = By.cssSelector("#HEADER_CUSTOMIZE_USER_DASHBOARD");
    private By aboutLink = By.cssSelector(".copyright>a>img");
    private By version = By.cssSelector(".about>.header:nth-child(1)");
    private By licenseHolder = By.cssSelector(".about .licenseHolder");

    private WebDriver webDriver;

    public UserDashboardPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public FluentWait<WebDriver> setWaitingTime(int timeOutInSeconds, int pollingTimeInMillis) {
        return new WebDriverWait(webDriver, timeOutInSeconds)
            .pollingEvery(Duration.ofMillis(pollingTimeInMillis))
            .ignoring(ElementClickInterceptedException.class)
            .ignoring(ElementNotInteractableException.class)
            .ignoring(NoSuchElementException.class)
            .ignoring(TimeoutException.class)
            .ignoring(StaleElementReferenceException.class);
    }

    public boolean isLoggedInSuccessfully() {
       return setWaitingTime(30, 1)
            .until(ExpectedConditions.visibilityOfElementLocated(headerPage)).isDisplayed();
    }

    public void openAboutPage() {
        setWaitingTime(30, 1)
            .until(ExpectedConditions.elementToBeClickable(aboutLink)).click();
    }

    public String getVersion() {
        return setWaitingTime(30, 1)
            .until(ExpectedConditions.elementToBeClickable(version)).getText();
    }

    public boolean getLicenseHolder() {
        return !setWaitingTime(30, 1)
            .until(ExpectedConditions.elementToBeClickable(licenseHolder)).getText().isEmpty();
    }
}
