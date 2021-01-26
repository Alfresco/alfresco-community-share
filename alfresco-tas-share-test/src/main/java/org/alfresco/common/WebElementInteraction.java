package org.alfresco.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebElementInteraction
{
    protected static final Logger LOG = LoggerFactory.getLogger(WebElementInteraction.class);

    private String mainWindow;
    protected DefaultProperties defaultProperties;
    protected ThreadLocal<WebDriver> webDriver;

    public WebElementInteraction(ThreadLocal<WebDriver> webDriver, DefaultProperties defaultProperties)
    {
        this.webDriver = webDriver;
        this.defaultProperties = defaultProperties;
    }

    private WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public void mouseOver(WebElement element)
    {
        setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis());
        try
        {
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
        catch (MoveTargetOutOfBoundsException | StaleElementReferenceException | NoSuchElementException ex)
        {
            LOG.error("Unable to perform mouse over element {}", element);
            ((JavascriptExecutor) webDriver.get())
                .executeScript("arguments[0].scrollIntoView(true);", element);
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
    }

    public void mouseOver(WebElement element, long pollingTimeInMillis)
    {
        setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis);
        try
        {
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
        catch (MoveTargetOutOfBoundsException | StaleElementReferenceException | NoSuchElementException ex)
        {
            LOG.error("Unable to perform mouse over on element {}", element);
            ((JavascriptExecutor) webDriver.get())
                .executeScript("arguments[0].scrollIntoView(true);", element);
            new Actions(getWebDriver()).moveToElement(element).perform();
        }
    }

    public void mouseOver(WebElement element, int xOffset, int yOffset)
    {
        setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis());
        try
        {
            new Actions(getWebDriver()).moveToElement(element, xOffset, yOffset).perform();
        }
        catch (StaleElementReferenceException | NoSuchElementException | MoveTargetOutOfBoundsException exception)
        {
            LOG.error("Unable to perform mouse over on element {}", element);
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

    public void mouseOverViaJavascript(WebElement webElement)
    {
        setWaitingTime(defaultProperties.getExplicitWait(),
                defaultProperties.getPollingTimeInMillis());
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
                LOG.info("Unable to perform mouse over use javascript on element {}", webElement);
            }
        }
    }

    public void mouseOverViaJavascript(WebElement webElement, long pollingTimeInMillis)
    {
        setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis);
        try
        {
            executeMouseOverViaJavaScript(webElement);
        }
        catch (StaleElementReferenceException | NoSuchElementException exception)
        {
            LOG.error("Unable to perform mouse over on element {}, {} ", webElement, exception.getCause());
            try
            {
                executeMouseOverViaJavaScript(webElement);
            }
            catch (TimeoutException e)
            {
                throw new TimeoutException(String.format(
                    "Unable to perform mouse over use javascript on element %s  in the given seconds %d ",
                        webElement, defaultProperties.getExplicitWait()));
            }
        }
    }

    public void refresh()
    {
        getWebDriver().navigate().refresh();
    }

    public WebElement findElement(WebElement webElement)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until((WebDriver driver) -> {
                        isElementDisplayed(webElement);
                        webElement.isEnabled();
                        return webElement;
                    });
        }
        catch (NoSuchElementException | ElementNotInteractableException | StaleElementReferenceException | TimeoutException e)
        {
            LOG.error("Unable to find element {}, {} ", webElement, e.getCause());
            try
            {
                return setWaitingTime(defaultProperties.getExplicitWait(),
                        defaultProperties.getPollingTimeInMillis())
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
                        defaultProperties.getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    public WebElement findElement(By locator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until((WebDriver driver) -> {
                        driver.findElement(locator).isDisplayed();
                        driver.findElement(locator).isEnabled();
                        return driver.findElement(locator);
                    });
        }
        catch (NoSuchElementException | ElementNotInteractableException | StaleElementReferenceException | TimeoutException e)
        {
            LOG.error("Unable to find element located by {}", locator);
            try
            {
                return setWaitingTime(defaultProperties.getExplicitWait(),
                        defaultProperties.getPollingTimeInMillis())
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
                        defaultProperties.getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    public void waitUntilElementHasAttribute(WebElement element, String attribute, String value)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.attributeContains(element, attribute, value));
        }
        catch (NoSuchElementException exception)
        {
            try
            {
                LOG.error("Element located by {} does not have attribute {} containing value {}", element, attribute, value);
                setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.attributeContains(element, attribute, value));
            }
            catch (TimeoutException timeoutException)
            {
                throw new TimeoutException(String.format(
                    "Element located by %s does not contain attribute %s with value %s in given seconds %d ",
                        element, attribute, value, defaultProperties.getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    public void waitUntilElementHasAttribute(By locator, String attribute, String value)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.attributeContains(locator, attribute, value));
        }
        catch (NoSuchElementException exception)
        {
            try
            {
                LOG.error("Element located by {} does not have attribute {} containing value {}", locator, attribute, value);
                setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.attributeContains(locator, attribute, value));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String.format(
                    "Element located by %s does not contain attribute %s with value %s in given seconds %d ",
                        locator, attribute, value, defaultProperties.getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    public WebElement waitUntilElementClickable(WebElement element)
    {
        return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
            .until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitUntilElementClickable(By element)
    {
        return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement waitUntilElementIsVisible(By locator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(),
                    defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not visible", locator);
                return setWaitingTime(defaultProperties.getExplicitWait(),
                        defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", locator,
                        defaultProperties.getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    public WebElement waitUntilElementIsVisible(By locator, long pollingTimeInMillis)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not visible", locator);
                return setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", locator,
                        defaultProperties.getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    public WebElement waitUntilElementIsVisible(By locator, int timeoutInSeconds)
    {
        try
        {
            return setWaitingTime(timeoutInSeconds, defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not visible", locator);
                return setWaitingTime(timeoutInSeconds, defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (TimeoutException | NoSuchElementException noSuchElement)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", locator,
                            defaultProperties.getExplicitWait()), noSuchElement.getCause());
            }
        }
    }

    public WebElement waitUntilElementIsVisible(WebElement element)
    {
        try
        {
            LOG.info("Wait until element {} is visible", element);
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOf(element));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not visible", element);
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOf(element));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", element,
                        defaultProperties.getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    public WebElement waitUntilElementIsVisible(WebElement element, long pollingTimeInMillis)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                    .until(ExpectedConditions.visibilityOf(element));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not visible", element);
                return setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                        .until(ExpectedConditions.visibilityOf(element));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds %d ", element,
                        defaultProperties.getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    public WebElement waitUntilElementIsPresent(By locator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Element {} is not present", locator);
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            catch (ElementNotVisibleException elementNotVisibleException) {
                throw new TimeoutException(String
                    .format("Element %s was not visible in the given seconds  %d ", locator,
                        defaultProperties.getExplicitWait()), exception.getCause());
            }
        }
    }

    public WebElement waitUntilChildElementIsPresent(By parentLocator, By childLocator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Unable to find parent locator {} with child {}", parentLocator, childLocator);
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Element parent %s with child %s was not present in the given seconds  %d ",
                        parentLocator, childLocator, defaultProperties.getExplicitWait()), exception.getCause());
            }
        }
    }

    public WebElement waitUntilChildElementIsPresent(WebElement parentLocator, By childLocator) {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Unable to find parent locator {} with child {}", parentLocator, childLocator);
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentLocator, childLocator));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(String.format(
                    "Unable to find element with parent %s and with child %s was not present in the given seconds  %d ",
                        parentLocator, childLocator, defaultProperties.getExplicitWait()), noSuchElementException.getCause());
            }
        }
    }

    public List<WebElement> waitUntilElementsAreVisible(By locator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            try
            {
                LOG.error("Elements located by {} are not visible", locator);
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(
                    String.format("Elements %s not visible in the given seconds %d ", locator,
                            defaultProperties.getExplicitWait()), noSuchElementException.getCause());
            }
        }
    }

    public void clickElement(WebElement element)
    {
        clickElement(element, defaultProperties.getPollingTimeInMillis());
    }

    public void clickElement(WebElement element, long pollingTimeInMillis)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                .until(ExpectedConditions.elementToBeClickable(element)).click();
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            LOG.error("Element {} is not clickable", element);
            try
            {
                clickJS(element);
            }
            catch (ElementNotInteractableException elementNotInteractableException)
            {
                throw new TimeoutException(String
                        .format("Element %s was not clickable in the given seconds  %d ", element,
                                defaultProperties.getExplicitWait()), elementNotInteractableException.getCause());
            }
        }
    }

    public void clickElement(By locator)
    {
        clickElement(locator, defaultProperties.getPollingTimeInMillis());
    }

    public void clickElement(By locator, long pollingTimeInMillis)
    {
        try
        {
            LOG.info("Wait until element located by {} is clickable", locator);
            setWaitingTime(defaultProperties.getExplicitWait(), pollingTimeInMillis)
                .until(ExpectedConditions.elementToBeClickable(locator)).click();
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            LOG.error("Element located by {} is not clickable", locator);
            try
            {
                clickJS(findElement(locator));
            }
            catch (ElementNotInteractableException elementNotInteractableException)
            {
                throw new TimeoutException(String
                    .format("Element %s was not clickable in the given seconds  %d ", locator,
                        defaultProperties.getExplicitWait()), elementNotInteractableException.getCause());
            }
        }
    }

    public List<WebElement> waitUntilElementsAreVisible(List<WebElement> elements)
    {
        try
        {
            LOG.info("Wait until elements {} are visible", elements);
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.visibilityOfAllElements(elements));
        }
        catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException | TimeoutException exception)
        {
            LOG.error("Elements {} are not visible", elements);
            try
            {
                ((JavascriptExecutor) getWebDriver()).executeScript(
                    "arguments[0].style.height='auto'; arguments[0].style.visibility='visible';",
                    elements);

                return setWaitingTime(defaultProperties.getExplicitWait(),
                        defaultProperties.getPollingTimeInMillis())
                        .until(ExpectedConditions.visibilityOfAllElements(elements));
            }
            catch (NoSuchElementException noSuchElementException)
            {
                throw new TimeoutException(String
                    .format("Elements not visible %s in the given seconds %d ", elements,
                        defaultProperties.getExplicitWait()), exception.getCause());
            }
        }
    }

    public void waitUntilElementContainsText(WebElement element, String text)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.textToBePresentInElement(element, text));
        }
        catch (TimeoutException | StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                LOG.error("Text {} is not present in element {}", text, element);
                setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.textToBePresentInElement(element, text));
            }
            catch (ElementNotVisibleException elementNotVisibleException) {
                throw new TimeoutException(String
                    .format("Unable to get element %s text %s in the given seconds %d ", element,
                            text, defaultProperties.getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    public void waitUntilElementContainsText(By locator, String text)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        }
        catch (TimeoutException | StaleElementReferenceException | NoSuchElementException exception)
        {
            try
            {
                LOG.error("Element {} does not contain text {}", locator, text);
                setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            }
            catch (ElementNotVisibleException elementNotVisibleException)
            {
                throw new TimeoutException(String
                    .format("Unable to get element %s text %s in the given seconds %d ", locator,
                        text, defaultProperties.getExplicitWait()), elementNotVisibleException.getCause());
            }
        }
    }

    public void waitUntilElementIsVisibleWithRetry(By locator, int retryCount)
    {
        int counter = 0;
        while (!isElementDisplayed(locator) && counter <= retryCount)
        {
            waitInSeconds(2);
            counter++;
        }
    }

    public void waitUntilElementIsDisplayedWithRetry(By locator)
    {
        waitUntilElementIsDisplayedWithRetry(locator, 1);
    }

    public void waitUntilElementIsDisplayedWithRetry(By locator, int secondsToWait)
    {
        waitUntilElementIsDisplayedWithRetry(locator, secondsToWait, 3);
    }

    public void waitUntilElementIsDisplayedWithRetry(By locator, int secondsToWait, int retryTimes)
    {
        int counter = 1;
        while (counter <= retryTimes && !isElementDisplayed(locator))
        {
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    public WebElement waitWithRetryAndReturnWebElement(By locator, int secondsToWait, int retryTimes)
    {
        waitUntilElementIsDisplayedWithRetry(locator, secondsToWait, retryTimes);
        return findElement(locator);
    }

    public void waitUntilElementIsDisplayedWithRetry(WebElement webElement, int secondsToWait)
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

    public void waitUntilElementDisappearsWithRetry(By locator, int secondsToWait)
    {
        int counter = 1;
        int retryRefreshCount = 3;
        while (counter <= retryRefreshCount && isElementDisplayed(locator))
        {
            LOG.info("Wait for element seconds after refresh: {}, {}", secondsToWait, counter);
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    public void waitUntilWebElementIsDisplayedWithRetry(WebElement webElement)
    {
        waitUntilWebElementIsDisplayedWithRetry(webElement, 0);
    }

    public void waitUntilWebElementIsDisplayedWithRetry(WebElement webElement, int secondsToWait)
    {
        int counter = 1;
        int retryRefreshCount = 3;
        while (counter <= retryRefreshCount && !isElementDisplayed(webElement))
        {
            LOG.info("Wait for element seconds after refresh: {}, {}", secondsToWait, counter);
            refresh();
            waitInSeconds(secondsToWait);
            counter++;
        }
    }

    public void waitUrlContains(String url, long timeOutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOutInSeconds);
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void waitUntilElementDeletedFromDom(By locator)
    {
        waitUntilElementDeletedFromDom(locator, defaultProperties.getExplicitWait(),
            defaultProperties.getPollingTimeInMillis());
    }

    public void waitUntilElementDeletedFromDom(By locator, long timeOutInSeconds, long pollingTimeInMillis)
    {
        try {
            setWaitingTime(timeOutInSeconds, pollingTimeInMillis)
                .until(ExpectedConditions.stalenessOf(getWebDriver().findElement(locator)));
        }
        catch (NoSuchElementException | StaleElementReferenceException e)
        {
            LOG.error("Element located by {} is stale", locator);
            try
            {
                setWaitingTime(timeOutInSeconds, pollingTimeInMillis)
                    .until(ExpectedConditions.stalenessOf(getWebDriver().findElement(locator)));
            }
            catch (NoSuchElementException | StaleElementReferenceException exception) {
                throw new TimeoutException(String
                    .format("Element %s was still attached to DOM in the given seconds %d ",
                        locator, timeOutInSeconds), exception.getCause());
            }
        }
    }

    public void waitUntilElementDisappears(By locator)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }
        catch (StaleElementReferenceException staleElementReferenceException)
        {
            LOG.error("Element is visible {}", locator);
            try
            {
                setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            }
            catch (TimeoutException timeoutException) {
                throw new TimeoutException(String
                    .format("Element %s was not invisible in the given seconds %d ", locator,
                        defaultProperties.getExplicitWait()), timeoutException.getCause());
            }
        }
    }

    public void waitUntilElementDisappears(By locator, long timeoutInSeconds)
    {
        try
        {
            setWaitingTime(timeoutInSeconds, defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        }
        catch (TimeoutException timeoutException)
        {
            LOG.info("Element is visible {}", locator);
        }
    }

    public void waitUntilElementDisappears(WebElement locator)
    {
        try
        {
            setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until(ExpectedConditions.invisibilityOf(locator));
        }
        catch (TimeoutException timeoutException)
        {
            LOG.info("Element is visible {}", locator);
        }
    }

    public boolean isElementDisplayed(By locator)
    {
        try
        {
            return getWebDriver().findElement(locator).isDisplayed();
        }
        catch (NoSuchElementException | StaleElementReferenceException | TimeoutException exception)
        {
            LOG.info("Element is not displayed {}", locator);
        }
        return false;
    }

    public boolean isElementDisplayed(WebElement element)
    {
        try
        {
            return element.isDisplayed();
        }
        catch (NoSuchElementException | TimeoutException | StaleElementReferenceException nse)
        {
            LOG.info("Element located by {} is not displayed ", element);
        }
        return false;
    }

    public boolean isElementDisplayed(WebElement element, By locator)
    {
        try
        {
            return element.findElement(locator).isDisplayed();
        }
        catch (NoSuchElementException | TimeoutException | StaleElementReferenceException nse)
        {
            LOG.info("Element located by {} is not displayed", locator);
        }
        return false;
    }

    public void switchToFrame(String frameId)
    {
        getWebDriver().switchTo().frame(frameId);
    }

    public void switchToDefaultContent()
    {
        getWebDriver().switchTo().defaultContent();
    }

    public void switchWindow()
    {
        mainWindow = getWebDriver().getWindowHandle();
        Set<String> windows = getWebDriver().getWindowHandles();
        windows.remove(mainWindow);
        switchToWindow(windows.iterator().next());
    }

    public void switchWindow(int windowIndex)
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
            LOG.info("Wait for window: {}", counter);
            waitInSeconds(2);
            windowsNumber = getWebDriver().getWindowHandles().size();
            counter++;
        }
    }

    public void switchWindow(String winHandler)
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

    public void closeWindowAndSwitchBack()
    {
        getWebDriver().close();
        switchToWindow(mainWindow);
    }

    public void switchToWindow(String windowHandle)
    {
        getWebDriver().switchTo().window(windowHandle);
    }

    public Cookie getCookie(final String name)
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

    public void deleteCookie(Cookie cookie)
    {
        getWebDriver().manage().deleteCookie(cookie);
    }

    public void dragAndDrop(WebElement source, WebElement target)
    {
        Actions builder = new Actions(getWebDriver());
        builder.dragAndDrop(source, target).perform();
    }

    public void dragAndDrop(WebElement source, int x, int y)
    {
        Actions builder = new Actions(getWebDriver());
        Action dragAndDrop = builder.dragAndDropBy(source, x, y).build();
        dragAndDrop.perform();
    }

    public void doubleClickOnElement(WebElement element)
    {
        Actions builder = new Actions(getWebDriver());
        Action doubleClick = builder.doubleClick(element).build();
        doubleClick.perform();
    }

    public void rightClickOnElement(WebElement element)
    {
        Actions builder = new Actions(getWebDriver());
        Action rightClick = builder.contextClick(element).build();
        rightClick.perform();
    }

    public WebElement findFirstDisplayedElement(By locator)
    {
        List<WebElement> elementList = findDisplayedElementsFromLocator(locator);
        if (!elementList.isEmpty())
        {
            return elementList.get(0);
        }
        return null;
    }

    public WebElement findFirstElementWithValue(By locator, String value)
    {
        List<WebElement> elementList = waitUntilElementsAreVisible(locator);
        return elementList.stream().filter(element -> element.getText().contains(value)).findFirst().orElse(null);
    }

    public WebElement findFirstElementWithValue(List<WebElement> list, String value)
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

    public WebElement findFirstElementWithExactValue(List<WebElement> list, String value)
    {
        return list.stream().filter(element -> element.getText().equals(value)).findFirst().orElse(null);
    }

    public List<WebElement> findDisplayedElementsFromLocator(By selector)
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

    public void selectOptionFromFilterOptionsList(String option,
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

    public void scrollToElement(WebElement element)
    {
        executeJavaScript(String.format("window.scrollTo(0, '%s')", element.getLocation().getY()));
    }

    public void scrollIntoView(WebElement element)
    {
        executeJavaScript("arguments[0].scrollIntoView(true);", element);
    }

    public void waitInSeconds(int seconds)
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

    public void executeJavaScript(String command)
    {
        if (getWebDriver() instanceof JavascriptExecutor)
        {
            ((JavascriptExecutor) getWebDriver()).executeScript(command);
        }
    }

    public void executeJavaScript(String scriptToExecute, WebElement element)
    {
        ((JavascriptExecutor) getWebDriver()).executeScript(scriptToExecute, element);
    }

    public Object executeJavaScript(String command, Object... args)
    {
        return ((JavascriptExecutor) getWebDriver()).executeScript(command, args);
    }

    public void clickJS(WebElement elementToClick)
    {
        executeJavaScript("arguments[0].click();", elementToClick);
    }

    public boolean isAlertPresent()
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

    public void acceptAlert()
    {
        if (isAlertPresent())
        {
            Alert alert = getWebDriver().switchTo().alert();
            String alertText = alert.getText().trim();
            LOG.info("Alert data: {}", alertText);
            alert.accept();
        }
    }

    public void focusOnWebElement(WebElement webElement)
    {
        webElement.sendKeys(Keys.TAB);
    }

    public void waitUntilElementDoesNotContainText(WebElement element, String text)
    {
        setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
            .until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
    }

    public List<String> getTextFromLocatorList(By elementsList)
    {
        List<WebElement> elements = waitUntilElementsAreVisible(elementsList);
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getTextFromElementList(List<WebElement> elementsList)
    {
        return elementsList.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<WebElement> findElements(By locator)
    {
        try
        {
            return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                .until((WebDriver driver) -> driver.findElements(locator));
        }
        catch (NoSuchElementException noSuchElementException)
        {
            try
            {
                return setWaitingTime(defaultProperties.getExplicitWait(), defaultProperties.getPollingTimeInMillis())
                    .until((WebDriver driver) -> driver.findElements(locator));
            }
            catch (TimeoutException timeoutException)
            {
                throw new TimeoutException(String.format("Unable to find elements located by %s", locator));
            }
        }
    }

    public String getCurrentUrl()
    {
        return getWebDriver().getCurrentUrl();
    }

    public void getUrl(String url)
    {
        getWebDriver().get(url);
    }

    public void navigateTo(String url)
    {
        getWebDriver().navigate().to(url);
    }

    public void navigateBack()
    {
        getWebDriver().navigate().back();
    }

    public String getTitle()
    {
        return getWebDriver().getTitle();
    }

    public TargetLocator switchTo()
    {
        return getWebDriver().switchTo();
    }

    public void clearAndType(WebElement webElement, String value)
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

    public void clearAndType(By locator, String value)
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

    public String getElementText(By selector)
    {
        return waitUntilElementIsVisible(selector).getText();
    }

    public String getElementText(WebElement element)
    {
        return waitUntilElementIsVisible(element).getText();
    }

    public String getElementText(By selector, int timeoutInSeconds)
    {
        return waitUntilElementIsVisible(selector, timeoutInSeconds).getText();
    }

    public String getPageSource()
    {
        return getWebDriver().getPageSource();
    }

    public Set<String> getWindowHandles()
    {
        return getWebDriver().getWindowHandles();
    }

    public String getPageTitle()
    {
        return getWebDriver().getTitle();
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
