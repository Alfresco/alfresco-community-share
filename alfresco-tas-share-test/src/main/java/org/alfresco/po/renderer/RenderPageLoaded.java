package org.alfresco.po.renderer;

import org.alfresco.browser.WebBrowser;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Render one element using selenium's expectedCondition.
 * Just annotate your {@link PageObject} with
 * {code}
 *  @RenderWebElement(state=ElementState.PAGE_LOADED)
 *  @FindBy(...)
 *  WebElement element;
 * {code}
 * 
 * @author Paul.Brodner
 */
public class RenderPageLoaded extends RenderElement
{

    @Override
    protected void doWork(By locator, WebBrowser browser, long timeOutInSeconds)
    {
        LOG.info("Dom Completed in {} miliseconds", domEventCompleted(browser));
        waitForPageLoad(browser, timeOutInSeconds);
    }

    /**
     * Checks if page event action completed. For further information see:
     * {@link http://w3c.github.io/navigation-timing/}
     * 
     * @return String time took to complete event
     */
    public String domEventCompleted(WebBrowser browser)
    {
        String js = "try{window.performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {};"
                + "return(parseInt(window.performance.timing.domContentLoadedEventEnd)-parseInt(window.performance.timing.navigationStart));}catch(e){}";
        Object val = ((JavascriptExecutor) browser).executeScript(js);
        return val.toString();
    }

    /**
     * Wait document.readyState to return completed.
     * 
     * @param timeOutInSeconds time duration
     */
    public void waitForPageLoad(WebBrowser browser, long timeOutInSeconds)
    {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        try
        {
            new WebDriverWait(browser, timeOutInSeconds).until(expectation);
        }
        catch (TimeoutException exception)
        {
        }
    }
}
