package org.alfresco.po.adminconsole.consoles;

import org.alfresco.po.adminconsole.AdminConsolePage;
import org.alfresco.utility.web.annotation.RenderWebElement;
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

    public String getDescription()
    {
        return description.getText();
    }

    public String executeCommand(String command)
    {
        cmd.clear();
        cmd.sendKeys(command);
        execute.click();
        return getResults();
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
}
