package org.alfresco.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public abstract class WebElementInteraction
{
    protected static DefaultProperties defaultProperties;
    protected final ThreadLocal<WebDriver> webDriver;
    private String mainWindow;

    private final String STYLE = "style";
    private final String DISPLAY_NONE = "display: none;";

    protected WebElementInteraction(ThreadLocal<WebDriver> webDriver)
    {
        this.webDriver = webDriver;
    }
    protected final DefaultProperties getDefaultProperties()
    {
        return defaultProperties;
    }

    protected WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    protected void mouseOver(By locator)
    {
        mouseOver(waitUntilElementIsVisible(locator));
    }

    protected void mouseOver(WebElement element)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis());
        try
        {
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
        catch (MoveTargetOutOfBoundsException | StaleElementReferenceException | NoSuchElementException ex)
        {
            log.warn("Unable to perform mouse over element {}", element);
            ((JavascriptExecutor) webDriver.get())
                .executeScript("arguments[0].scrollIntoView(true);", element);
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
    }

    protected void mouseOver(WebElement element, long pollingTimeInMillis)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis);
        try
        {
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
        catch (MoveTargetOutOfBoundsException | StaleElementReferenceException | NoSuchElementException ex)
        {
            log.warn("Unable to perform mouse over on element {}", element);
            ((JavascriptExecutor) webDriver.get())
                .executeScript("arguments[0].scrollIntoView(true);", element);
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
    }

    protected void mouseOver(WebElement element, int xOffset, int yOffset)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis());
        try
        {
            new Actions(getWebDriver()).moveToElement(element, xOffset, yOffset).perform();
        }
        catch (StaleElementReferenceException | NoSuchElementException | MoveTargetOutOfBoundsException exception)
        {
            log.warn("Unable to perform mouse over on element {}", element);
            ((JavascriptExecutor) webDriver.get())
                    .executeScript("arguments[0].scrollIntoView(true);", element);
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
    }

    private void executeMouseOverViaJavaScript(WebElement webElement)
    {
        String javaScript = "var event = document.createEvent('MouseEvents');" +
                "event.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                +
                "arguments[0].dispatchEvent(event);";
        ((JavascriptExecutor) getWebDriver()).executeScript(javaScript, webElement);
    }

    protected void mouseOverViaJavascript(WebElement webElement)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(),
                getDefaultProperties().getPollingTimeInMillis());
        try
        {
            executeMouseOverViaJavaScript(webElement);
        }
        catch (StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                executeMouseOverViaJavaScript(webElement);
            }
            catch (TimeoutException e)
            {
                log.warn("Unable to perform mouse over use javascript on element {}", webElement);
            }
        }
    }

    protected void mouseOverViaJavascript(WebElement webElement, long pollingTimeInMillis)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis);
        try
        {
            executeMouseOverViaJavaScript(webElement);
        }
        catch (StaleElementReferenceException | NoSuchElementException exception)
        {
            log.warn("Unable to perform mouse over on element {}, {} ", webElement, exception.getCause());
            try
            {
                executeMouseOverViaJavaScript(webElement);
            }
            catch (TimeoutException e)
            {
                throw new TimeoutException(String.format(
                    "Unable to perform mouse over use javascript on element %s  in the given seconds %d ",
                        webElement, getDefaultProperties().getExplicitWait()));
            }
        }
    }

    protected void refresh()
    {
        getWebDriver().navigate().refresh();
        waitUntilDomReadyStateIsComplete();
    }

    protected WebElement findElement(WebElement webElement)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until((WebDriver driver) -> {
                        isElementDisplayed(webElement);
                        webElement.isEnabled();
                        return webElement;
                    });
        }
        catch (NoSuchElementException | ElementNotInteractableException | StaleElementReferenceException | TimeoutException e)
        {
            log.warn("Unable to find element {}, {} ", webElement, e.getCause());
            try
            {
                return setWaitingTime(getDefaultProperties().getExplicitWait(),
                        getDefaultProperties().getPollingTimeInMillis())
                        .until((WebDriver driver) -> {
                            isElementDisplayed(webElement);
                            webElement.isEnabled();
                            return webElement;
                        });
            }
            catch (TimeoutException timeoutException)
            {
                throw new NoSuchElementException(
                    String.format("Unable to find element %s in the given seconds %d ", webElement,
                        getDefaultProperties().getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    protected WebElement findElement(By locator)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until((WebDriver driver) -> {
                        driver.findElement(locator).isDisplayed();
                        driver.findElement(locator).isEnabled();
                        return driver.findElement(locator);
                    });
        }
        catch (NoSuchElementException | ElementNotInteractableException | StaleElementReferenceException | TimeoutException e)
        {
            log.warn("Unable to find element located by {}", locator);
            try
            {
                return setWaitingTime(getDefaultProperties().getExplicitWait(),
                        getDefaultProperties().getPollingTimeInMillis())
                        .until((WebDriver driver) -> {
                            driver.findElement(locator).isDisplayed();
                            driver.findElement(locator).isEnabled();
                            return driver.findElement(locator);
                        });
            }
            catch (TimeoutException timeoutException)
            {
                throw new NoSuchElementException(
                    String.format("Unable to find element %s in the given seconds %d ", locator,
                        getDefaultProperties().getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    protected void waitUntilElementHasAttribute(WebElement element, String attribute, String value)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.attributeContains(element, attribute, value));
        }
        catch (NoSuchElementException exception)
        {
            try
            {
                log.warn("Element located by {} does not have attribute {} containing value {}", element, attribute, value);
                setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.attributeContains(element, attribute, value));
            }
            catch (TimeoutException timeoutException)
            {
                throw new TimeoutException(String.format(
                    "Element located by %s does not contain attribute %s with value %s in given seconds %d ",
                        element, attribute, value, getDefaultProperties().getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    protected void waitUntilElementHasAttribute(By locator, String attribute, String value)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.attributeContains(locator, attribute, value));
        }
        catch (NoSuchElementException exception)
        {
            try
            {
                log.warn("Element located by {} does not have attribute {} containing value {}", locator, attribute, value);
                setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.attributeContains(locator, attribute, value));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String.format(
                    "Element located by %s does not contain attribute %s with value %s in given seconds %d ",
                        locator, attribute, value, getDefaultProperties().getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    protected WebElement waitUntilElementClickable(WebElement element)
    {
        return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
            .until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitUntilElementClickable(By element)
    {
        return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitUntilElementIsVisible(By locator)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(),
                    getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not visible", locator);
                refresh();
                return setWaitingTime(getDefaultProperties().getExplicitWait(),
                        getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", locator,
                        getDefaultProperties().getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    protected WebElement waitUntilElementIsVisible(By locator, long pollingTimeInMillis)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not visible", locator);
                refresh();
                return setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", locator,
                        getDefaultProperties().getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    protected WebElement waitUntilElementIsVisible(By locator, int timeoutInSeconds)
    {
        try
        {
            return setWaitingTime(timeoutInSeconds, getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not visible", locator);
                return setWaitingTime(timeoutInSeconds, getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", locator,
                            getDefaultProperties().getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    protected WebElement waitUntilElementIsVisible(WebElement element)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOf(element));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not visible", element);
                return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOf(element));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", element,
                        getDefaultProperties().getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    protected WebElement waitUntilElementIsVisible(WebElement element, long pollingTimeInMillis)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                    .until(ExpectedConditions.visibilityOf(element));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not visible", element);
                return setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                        .until(ExpectedConditions.visibilityOf(element));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", element,
                        getDefaultProperties().getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    protected WebElement waitUntilElementIsPresent(By locator)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Element {} is not present", locator);
                return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (ElementNotVisibleException elementNotVisibleException) {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", locator,
                        getDefaultProperties().getExplicitWait()), exception.getCause());
            }
        }
    }

    protected WebElement waitUntilChildElementIsPresent(By parentLocator, By childLocator, long secondsToWait)
    {
        try
        {
            return setWaitingTime(secondsToWait, getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Unable to find parent locator {} with child {}", parentLocator, childLocator);
                return setWaitingTime(secondsToWait, getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element parent %s with child %s was not present in the given seconds  %d ",
                        parentLocator, childLocator, getDefaultProperties().getExplicitWait()), exception.getCause());
            }
        }
    }

    protected WebElement waitUntilChildElementIsPresent(By parentLocator, By childLocator)
    {
        return waitUntilChildElementIsPresent(parentLocator, childLocator, getDefaultProperties().getExplicitWait());
    }

    protected WebElement waitUntilChildElementIsPresent(WebElement parentLocator, By childLocator, long waitInSeconds)
    {
        try
        {
            return setWaitingTime(waitInSeconds, getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Unable to find parent locator {} with child {}", parentLocator, childLocator);
                return setWaitingTime(waitInSeconds, getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(String.format(
                    "Unable to find element with parent %s and with child %s was not present in the given seconds  %d ",
                        parentLocator, childLocator, getDefaultProperties().getExplicitWait()), noSuchElementException.getCause());
            }
        }
    }

    protected WebElement waitUntilChildElementIsPresent(WebElement parentLocator, By childLocator)
    {
        return waitUntilChildElementIsPresent(parentLocator, childLocator, getDefaultProperties().getExplicitWait());
    }

    protected List<WebElement> waitUntilElementsAreVisible(By locator)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                log.warn("Elements located by {} are not visible", locator);
                return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(
                    String.format("Elements %s not visible in the given seconds %d ", locator,
                            getDefaultProperties().getExplicitWait()), noSuchElementException.getCause());
            }
        }
    }

    protected void clickElement(WebElement element)
    {
        clickElement(element, getDefaultProperties().getPollingTimeInMillis());
    }

    protected void clickElement(WebElement element, long pollingTimeInMillis)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                .until(ExpectedConditions.elementToBeClickable(element)).click();
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            log.warn("Element {} is not clickable", element);
            try
            {
                clickJS(element);
            }
            catch (ElementNotInteractableException elementNotInteractableException)
            {
                throw new TimeoutException(String
                        .format("Element %s was not clickable in the given seconds  %d ", element,
                                getDefaultProperties().getExplicitWait()), elementNotInteractableException.getCause());
            }
        }
    }

    protected void clickElement(By locator)
    {
        clickElement(locator, getDefaultProperties().getPollingTimeInMillis());
    }

    protected void clickElement(By locator, long pollingTimeInMillis)
    {
        try
        {
            log.info("Wait until element located by {} is clickable", locator);
            setWaitingTime(getDefaultProperties().getExplicitWait(), pollingTimeInMillis)
                .until(ExpectedConditions.elementToBeClickable(locator)).click();
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            log.warn("Element located by {} is not clickable", locator);
            try
            {
                clickJS(findElement(locator));
            }
            catch (ElementNotInteractableException elementNotInteractableException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not clickable in the given seconds  %d ", locator,
                        getDefaultProperties().getExplicitWait()), elementNotInteractableException.getCause());
            }
        }
    }

    protected List<WebElement> waitUntilElementsAreVisible(List<WebElement> elements)
    {
        try
        {
            log.info("Wait until elements {} are visible", elements);
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfAllElements(elements));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            log.warn("Elements {} are not visible", elements);
            try
            {
                ((JavascriptExecutor) getWebDriver()).executeScript(
                    "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';",
                    elements);

                return setWaitingTime(getDefaultProperties().getExplicitWait(),
                        getDefaultProperties().getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfAllElements(elements));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(String
                    .format("Elements not visible %s in the given seconds %d ", elements,
                        getDefaultProperties().getExplicitWait()), exception.getCause());
            }
        }
    }

    protected void waitUntilElementContainsText(WebElement element, String text)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.textToBePresentInElement(element, text));
        }
        catch (TimeoutException | StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                log.warn("Text {} is not present in element {}", text, element);
                setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.textToBePresentInElement(element, text));
            }
            catch (ElementNotVisibleException elementNotVisibleException) {
                throw new TimeoutException(String
                    .format("Unable to get element %s text %s in the given seconds %d ", element,
                            text, getDefaultProperties().getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    protected void waitUntilElementContainsText(By locator, String text)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        }
        catch (TimeoutException | StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                log.warn("Element {} does not contain text {}", locator, text);
                setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                    .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Unable to get element %s text %s in the given seconds %d ", locator,
                        text, getDefaultProperties().getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    protected void waitUntilElementIsVisibleWithRetry(By locator, int retryCount)
    {
        int counter = 0;
        while (!isElementDisplayed(locator) && counter <= retryCount)
        {
            waitInSeconds(2);
            counter++;
        }
    }

    protected void waitUntilElementIsDisplayedWithRetry(By locator)
    {
        waitUntilElementIsDisplayedWithRetry(locator, 1);
    }

    protected void waitUntilElementIsDisplayedWithRetry(By locator, int secondsToWait)
    {
        waitUntilElementIsDisplayedWithRetry(locator, secondsToWait, 3);
    }

    protected void waitUntilElementIsDisplayedWithRetry(By locator, int secondsToWait, int retryTimes)
    {
        int counter = 1;
        while (counter <= retryTimes && !isElementDisplayed(locator))
        {
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    protected WebElement waitWithRetryAndReturnWebElement(By locator, int secondsToWait, int retryTimes)
    {
        waitUntilElementIsDisplayedWithRetry(locator, secondsToWait, retryTimes);
        return findElement(locator);
    }

    protected void waitUntilElementIsDisplayedWithRetry(WebElement webElement, int secondsToWait)
    {
        int counter = 1;
        int retryRefreshCount = 3;

        while (counter <= retryRefreshCount && !isElementDisplayed(webElement))
        {
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    protected void waitUntilElementDisappearsWithRetry(By locator, int secondsToWait)
    {
        int counter = 1;
        int retryRefreshCount = 3;
        while (counter <= retryRefreshCount && isElementDisplayed(locator))
        {
            log.info("Wait for element seconds after refresh: {}, {}", secondsToWait, counter);
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    protected void waitUntilWebElementIsDisplayedWithRetry(WebElement webElement)
    {
        waitUntilWebElementIsDisplayedWithRetry(webElement, 0);
    }

    protected void waitUntilWebElementIsDisplayedWithRetry(WebElement webElement, int secondsToWait)
    {
        int counter = 1;
        int retryRefreshCount = 3;
        while (counter <= retryRefreshCount && !isElementDisplayed(webElement))
        {
            log.info("Wait for element seconds after refresh: {}, {}", secondsToWait, counter);
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    protected void waitUrlContains(String url, long timeOutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOutInSeconds);
        wait.until(ExpectedConditions.urlContains(url));
    }

    protected void waitUntilElementDeletedFromDom(By locator)
    {
        waitUntilElementDeletedFromDom(locator, getDefaultProperties().getExplicitWait(),
            getDefaultProperties().getPollingTimeInMillis());
    }

    protected void waitUntilElementDeletedFromDom(By locator, long timeOutInSeconds, long pollingTimeInMillis)
    {
        try
        {
            setWaitingTime(timeOutInSeconds, pollingTimeInMillis)
                .until(ExpectedConditions.stalenessOf(getWebDriver().findElement(locator)));
        }
        catch (NoSuchElementException | StaleElementReferenceException e)
        {
            log.warn("Element located by {} is stale", locator);
            try
            {
                setWaitingTime(timeOutInSeconds, pollingTimeInMillis)
                    .until(ExpectedConditions.stalenessOf(getWebDriver().findElement(locator)));
            }
            catch (NoSuchElementException | StaleElementReferenceException exception)
            {
                throw new TimeoutException(String
                    .format("Element %s was still attached to DOM in the given seconds %d ",
                        locator, timeOutInSeconds), exception.getCause());
            }
        }
    }

    protected void waitUntilElementDisappears(By locator)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }
        catch (StaleElementReferenceException | TimeoutException exception )
        {
            log.warn("Element is still visible {}", locator);
        }
    }

    protected void waitUntilElementDisappears(By locator, long timeoutInSeconds)
    {
        try
        {
            setWaitingTime(timeoutInSeconds, getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }
        catch (TimeoutException timeoutException)
        {
            log.info("Element is visible {}", locator);
        }
    }

    protected void waitUntilElementDisappears(WebElement locator)
    {
        try
        {
            setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOf(locator));
        }
        catch (TimeoutException timeoutException)
        {
            log.info("Element is visible {}", locator);
        }
    }

    protected boolean isElementDisplayed(By locator)
    {
        try
        {
            return getWebDriver().findElement(locator).isDisplayed();
        }
        catch (NoSuchElementException | StaleElementReferenceException | TimeoutException exception)
        {
            log.info("Element is not displayed {}", locator);
        }
        return false;
    }

    protected boolean isElementDisplayed(WebElement element)
    {
        try
        {
            return element.isDisplayed();
        }
        catch (NullPointerException | NoSuchElementException | TimeoutException | StaleElementReferenceException nse)
        {
            log.info("Element located by {} is not displayed ", element);
        }
        return false;
    }

    protected boolean isElementDisplayed(WebElement element, By locator)
    {
        try
        {
            return element.findElement(locator).isDisplayed();
        }
        catch (NullPointerException |NoSuchElementException | TimeoutException | StaleElementReferenceException nse)
        {
            log.info("Element located by {} is not displayed", locator);
        }
        return false;
    }

    protected void switchToFrame(String frameId)
    {
        getWebDriver().switchTo().frame(frameId);
    }

    protected void switchToDefaultContent()
    {
        getWebDriver().switchTo().defaultContent();
    }

    protected void switchWindow()
    {
        mainWindow = getWebDriver().getWindowHandle();
        Set<String> windows = getWebDriver().getWindowHandles();
        windows.remove(mainWindow);
        switchToWindow(windows.iterator().next());
    }

    protected void switchWindow(int windowIndex)
    {
        mainWindow = getWebDriver().getWindowHandle();
        Set<String> windows = getWebDriver().getWindowHandles();
        int windowsNumber = windows.size();
        int counter = 1;
        int retryRefreshCount = 5;

        while (counter <= retryRefreshCount)
        {
            if (windowsNumber == windowIndex + 1)
            {
                windows.remove(mainWindow);
                switchToWindow(windows.iterator().next());
                break;
            }
            log.info("Wait for window: {}", counter);
            waitInSeconds(2);
            windowsNumber = getWebDriver().getWindowHandles().size();
            counter++;
        }
    }

    protected void switchWindow(String winHandler)
    {
        mainWindow = getWebDriver().getWindowHandle();
        for (String winHandle : getWebDriver().getWindowHandles())
        {
            getWebDriver().switchTo().window(winHandle);
            if (getWebDriver().getCurrentUrl().contains(winHandler))
            {
                break;
            }
            else
            {
                getWebDriver().switchTo().window(mainWindow);
            }
        }
    }

    protected void closeWindowAndSwitchBack()
    {
        getWebDriver().close();
        switchToWindow(mainWindow);
    }

    protected void switchToWindow(String windowHandle)
    {
        getWebDriver().switchTo().window(windowHandle);
    }

    protected Cookie getCookie(final String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Cookie identifier is required.");
        }
        Set<Cookie> cookies = getWebDriver().manage().getCookies();
        if (cookies != null)
        {
            return cookies.stream().filter(cookie -> name.equalsIgnoreCase(cookie.getName())).findFirst().orElse(null);
        }
        return null;
    }

    protected void deleteCookie(Cookie cookie)
    {
        getWebDriver().manage().deleteCookie(cookie);
    }

    protected void dragAndDrop(WebElement source, WebElement target)
    {
        Actions builder = new Actions(getWebDriver());
        builder.dragAndDrop(source, target).perform();
    }

    protected void dragAndDrop(WebElement source, int x, int y)
    {
        Actions builder = new Actions(getWebDriver());
        Action dragAndDrop = builder.dragAndDropBy(source, x, y).build();
        dragAndDrop.perform();
    }

    protected void doubleClickOnElement(WebElement element)
    {
        Actions builder = new Actions(getWebDriver());
        Action doubleClick = builder.doubleClick(element).build();
        doubleClick.perform();
    }

    protected void rightClickOnElement(WebElement element)
    {
        Actions builder = new Actions(getWebDriver());
        Action rightClick = builder.contextClick(element).build();
        rightClick.perform();
    }

    protected WebElement findFirstDisplayedElement(By locator)
    {
        List<WebElement> elementList = findDisplayedElementsFromLocator(locator);
        if (!elementList.isEmpty())
        {
            return elementList.get(0);
        }
        return null;
    }

    protected WebElement findFirstElementWithValue(By locator, String value)
    {
        List<WebElement> elementList = waitUntilElementsAreVisible(locator);
        return elementList.stream().filter(element -> element.getText().contains(value)).findFirst().orElse(null);
    }

    protected WebElement findFirstElementWithValue(List<WebElement> list, String value)
    {
        for (WebElement element : list)
        {
            if (element.getText().contains(value))
            {
                return element;
            }
        }
        return null;
    }

    protected WebElement findFirstElementWithExactValue(List<WebElement> list, String value)
    {
        return list.stream().filter(element -> element.getText().equals(value)).findFirst().orElse(null);
    }

    protected List<WebElement> findDisplayedElementsFromLocator(By selector)
    {
        List<WebElement> elementList = getWebDriver().findElements(selector);
        List<WebElement> displayedElementList = Collections.synchronizedList(new ArrayList<>());
        for (WebElement elementSelected : elementList)
        {
            if (elementSelected.isDisplayed())
            {
                displayedElementList.add(elementSelected);
            }
        }
        return displayedElementList;
    }

    protected void selectOptionFromFilterOptionsList(String option,
                                                  List<WebElement> filterOptionsList)
    {
        for (WebElement webElement : filterOptionsList)
        {
            if (webElement.getText().contains(option))
            {
                webElement.click();
                break;
            }
        }
    }

    protected void scrollToElement(WebElement element)
    {
        executeJavaScript(String.format("window.scrollTo(0, '%s')", element.getLocation().getY()));
    }

    protected void scrollIntoView(WebElement element)
    {
        executeJavaScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void waitInSeconds(int seconds)
    {
        long time0;
        long time1;
        time0 = System.currentTimeMillis();
        do
        {
            time1 = System.currentTimeMillis();
        }
        while (time1 - time0 < seconds * 1000L);
    }

    protected void executeJavaScript(String command)
    {
        if (getWebDriver() instanceof JavascriptExecutor)
        {
            ((JavascriptExecutor) getWebDriver()).executeScript(command);
        }
    }

    protected void executeJavaScript(String scriptToExecute, WebElement element)
    {
        ((JavascriptExecutor) getWebDriver()).executeScript(scriptToExecute, element);
    }

    protected Object executeJavaScript(String command, Object... args)
    {
        return ((JavascriptExecutor) getWebDriver()).executeScript(command, args);
    }

    protected void clickJS(WebElement elementToClick)
    {
        executeJavaScript("arguments[0].click();", elementToClick);
    }

    protected boolean isAlertPresent()
    {
        try
        {
            getWebDriver().switchTo().alert();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    protected void acceptAlert()
    {
        if (isAlertPresent())
        {
            Alert alert = getWebDriver().switchTo().alert();
            String alertText = alert.getText().trim();
            log.info("Alert data: {}", alertText);
            alert.accept();
        }
    }

    protected void focusOnWebElement(WebElement webElement)
    {
        webElement.sendKeys(Keys.TAB);
    }

    protected void waitUntilElementDoesNotContainText(WebElement element, String text)
    {
        setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
            .until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
    }

    protected List<String> getTextFromLocatorList(By elementsList)
    {
        List<WebElement> elements = waitUntilElementsAreVisible(elementsList);
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    protected List<String> getTextFromElementList(List<WebElement> elementsList)
    {
        return elementsList.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    protected List<WebElement> findElements(By locator)
    {
        try
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(), getDefaultProperties().getPollingTimeInMillis())
                .until((WebDriver driver) -> driver.findElements(locator));
        }
        catch (NoSuchElementException noSuchElementException)
        {
            return setWaitingTime(getDefaultProperties().getExplicitWait(),
                getDefaultProperties().getPollingTimeInMillis())
                .until((WebDriver driver) -> driver.findElements(locator));
        }
    }

    public String getCurrentUrl()
    {
        return getWebDriver().getCurrentUrl();
    }

    protected void getUrl(String url)
    {
        getWebDriver().get(url);
    }

    protected void navigateTo(String url)
    {
        getWebDriver().navigate().to(url);
    }

    protected void navigateBack()
    {
        getWebDriver().navigate().back();
    }

    protected String getTitle()
    {
        return getWebDriver().getTitle();
    }

    protected TargetLocator switchTo()
    {
        return getWebDriver().switchTo();
    }

    protected void clearAndType(WebElement webElement, String value)
    {
        try
        {
            findElement(webElement).clear();
            findElement(webElement).sendKeys(value);
        }
        catch (StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                findElement(webElement).clear();
                findElement(webElement).sendKeys(value);
            }
            catch (StaleElementReferenceException | NoSuchElementException noSuchElement)
            {
                throw new NoSuchElementException(String
                    .format("No such element %s exception when send keys ", noSuchElement.getCause()));
            }
        }
    }

    protected void clearAndType(By locator, String value)
    {
        try
        {
            findElement(locator).clear();
            findElement(locator).sendKeys(value);
        }
        catch (StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                findElement(locator).clear();
                findElement(locator).sendKeys(value);
            }
            catch (StaleElementReferenceException | NoSuchElementException noSuchElement)
            {
                throw new NoSuchElementException(String
                    .format("No such element %s exception when send keys ", noSuchElement.getCause()));
            }
        }
    }

    protected String getElementText(By selector)
    {
        return waitUntilElementIsVisible(selector).getText();
    }

    protected String getElementText(WebElement element)
    {
        return waitUntilElementIsVisible(element).getText();
    }

    protected String getElementText(By selector, int timeoutInSeconds)
    {
        return waitUntilElementIsVisible(selector, timeoutInSeconds).getText();
    }

    protected String getPageSource()
    {
        return getWebDriver().getPageSource();
    }

    protected Set<String> getWindowHandles()
    {
        return getWebDriver().getWindowHandles();
    }

    public String getPageTitle()
    {
        waitInSeconds(5);
        return getWebDriver().getTitle();
    }

    protected void waitUntilDomReadyStateIsComplete()
    {
        new WebDriverWait(webDriver.get(), getDefaultProperties().getExplicitWait())
                .until(driver -> ((JavascriptExecutor) webDriver.get())
                        .executeScript("return document.readyState").equals("complete"));
    }

    protected void sendKeys(Keys key)
    {
        Actions builder = new Actions(webDriver.get());
        builder.sendKeys(key);
    }

    protected void waitUntilLocatorHasDisplayNoneStyle(By locator)
    {
        waitUntilElementHasAttribute(locator, STYLE, DISPLAY_NONE);
    }

    private FluentWait<WebDriver> setWaitingTime(long timeOutInSeconds, long pollingTimeInMillis)
    {
        return new WebDriverWait(getWebDriver(), timeOutInSeconds)
            .pollingEvery(Duration.ofMillis(pollingTimeInMillis))
            .ignoring(ElementClickInterceptedException.class)
            .ignoring(ElementNotInteractableException.class)
            .ignoring(NoSuchElementException.class)
            .ignoring(TimeoutException.class)
            .ignoring(ElementNotVisibleException.class)
            .ignoring(StaleElementReferenceException.class);
    }
}
