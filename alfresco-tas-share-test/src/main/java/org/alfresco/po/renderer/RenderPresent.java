package org.alfresco.po.renderer;

import org.alfresco.browser.WebBrowser;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Render one element using selenium's expectedCondition.
 * Just annotate your {@link PageObject} with
 * {code}
 *  @RenderWebElement(state=ElementState.PRESENT)
 *  @FindBy(...)
 *  WebElement element;
 * {code}
 * 
 * @author Paul.Brodner
 */
public class RenderPresent extends RenderElement
{
    @Override
    protected void doWork(By locator, WebBrowser browser, long timeOutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(browser, timeOutInSeconds);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
