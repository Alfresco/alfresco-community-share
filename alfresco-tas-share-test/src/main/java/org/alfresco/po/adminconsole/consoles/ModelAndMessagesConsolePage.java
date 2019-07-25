package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@PageObject
public class ModelAndMessagesConsolePage extends ConsolePage<ModelAndMessagesConsolePage>
{
    @FindBy (css = "input.inline")
    private WebElement executeButton;
    @FindBy (id = "cmd")
    private WebElement commandField;

    @Override
    protected String relativePathToURL()
    {
        return "alfresco/s/admin/admin-repoconsole";
    }

    public boolean isExecuteButtonPresent()
    {
        return browser.isElementDisplayed(executeButton);
    }

    public boolean isCommandFieldPresent()
    {
        return browser.isElementDisplayed(commandField);
    }

}
