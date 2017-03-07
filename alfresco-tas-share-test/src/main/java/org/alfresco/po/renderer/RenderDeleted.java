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
 *  @RenderWebElement(state=ElementState.DELETED_FROM_DOM)
 *  @FindBy(...)
 *  WebElement element;
 * {code}
 * 
 * @author Paul.Brodner
 */
public class RenderDeleted extends RenderElement
{
    @Override
    public void doWork(By locator, WebBrowser browser, long timeOutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(browser, timeOutInSeconds);
        wait.until(ExpectedConditions.stalenessOf(browser.findElement(locator)));
    }
}
