package org.alfresco.po.adminconsole.consoles;

import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

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

    public ModelAndMessagesConsolePage assertCommandFieldIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(commandField), "Command field is displayed");
        return this;
    }

    public ModelAndMessagesConsolePage assertExecuteButtonIsDisplayed()
    {
        Assert.assertTrue(browser.isElementDisplayed(executeButton), "Execute button is displayed");
        return this;
    }

    public ModelAndMessagesConsolePage assertHelpCommandIsCorrect()
    {
        Assert.assertTrue(getResults().contains("Meta commands"), "Meta commands is not present in the result");
        Assert.assertTrue(getResults().contains("Quit / Exit"), "Quit / Exit commands is not present in the result");
        Assert.assertTrue(getResults().contains("General Repo Admin Commands"),
            "General Repo Admin Commands is not present in the result");
        Assert.assertTrue(getResults().contains("Model Admin Commands"), "Model Admin Commands is not present in the result");
        Assert.assertTrue(getResults().contains("Message Admin Commands"), "Message Admin Commands is not present in the result");
        return this;
    }
}
