package org.alfresco.po.adminconsole.consoles;

import org.alfresco.common.Utils;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public abstract class ConsolePage<T> extends AdminConsolePage<T>
{
    @RenderWebElement
    @FindBy (id = "cmd")
    WebElement cmd;

    @RenderWebElement
    @FindBy (className = "description")
    WebElement description;

    @RenderWebElement
    @FindBy (css = "input[value='Execute']")
    WebElement execute;

    @FindBy (css = "div.column-full > pre")
    WebElement results;

    public By resultBy = By.cssSelector("div.column-full > pre");

    public String getDescription()
    {
        return description.getText();
    }

    public T executeCommand(String command)
    {
        LOG.info(String.format("Execute command: %s", command));
        cmd.clear();
        cmd.sendKeys(command);
        browser.waitUntilElementClickable(execute).click();
        return (T) renderedPage();
    }

    public String getResults()
    {
        return results.getText();
    }

    public T assertResultIs(String expectedResult)
    {
        Assert.assertEquals(getResults(), expectedResult, "Result is correct");
        return (T) renderedPage();
    }

    @Override
    public String getInfoPage()
    {
        return "";
    }

    @Override
    public String getIntroPage()
    {
        return "";
    }

    public void waitForResult(String result)
    {
        int retry = 0;
        int retryCount = 60;
        browser.waitUntilElementIsPresent(resultBy);
        WebElement resultElement = null;
        while (retry < retryCount && resultElement == null)
        {
            retry++;
            Utility.waitToLoopTime(1, "Wait until tenant command is executed");
            browser.waitUntilElementIsPresent(resultBy);
            try
            {
                resultElement = browser.findFirstElementWithValue(resultBy, result);
            }
            catch (StaleElementReferenceException e)
            {
                continue;
            }
        }
        LOG.info(String.format("Result is: '%s'", browser.findElement(resultBy).getText()));
    }
}
