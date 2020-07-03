package org.alfresco.po.adminconsole.consoles;

import org.alfresco.common.Utils;
import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.Utility;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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

    public void executeCommand(String command)
    {
        Utils.clearAndType(cmd, command);
        execute.click();
    }

    public String getResults()
    {
        return results.getText();
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

    public WebElement waitForResult(String result)
    {
        int retry = 0;
        int retryCount = 40;
        WebElement resultElement = browser.findFirstElementWithValue(resultBy, result);
        while (retry < retryCount && resultElement == null)
        {
            retry++;
            Utility.waitToLoopTime(1, "Wait until tenant command is executed");
            resultElement = browser.findFirstElementWithValue(resultBy, result);
        }
        return resultElement;
    }
}
