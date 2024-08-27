package org.alfresco.po.adminconsole.consoles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.SharePage2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;

import static org.alfresco.utility.report.log.Step.STEP;
@Slf4j
@ContextConfiguration(classes = ShareTestContext.class)
public class ModelAndMessagesConsolePages<T> extends SharePage2<ModelAndMessagesConsolePages<T>> {
    private By executeButton = By.cssSelector("input.inline");
    private By commandField = By.id("cmd");
    private By results = By.cssSelector("div.column-full > pre");
    private By execute = By.cssSelector("input[value='Execute']");

    public ModelAndMessagesConsolePages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/admin/admin-repoconsole";
    }

    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public ModelAndMessagesConsolePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public ModelAndMessagesConsolePages<T> assertCommandFieldIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(commandField), "Command field is displayed");
        return this;
    }

    public ModelAndMessagesConsolePages<T> assertExecuteButtonIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(executeButton), "Execute button is displayed");
        return this;
    }

    public String getResults()
    {
        return findElement(results).getText();
    }

    public void assertHelpCommandIsCorrect()
    {
        Assert.assertTrue(getResults().contains("Meta commands"), "Meta commands is not present in the result");
        Assert.assertTrue(getResults().contains("Quit / Exit"), "Quit / Exit commands is not present in the result");
        Assert.assertTrue(getResults().contains("General Repo Admin Commands"),
            "General Repo Admin Commands is not present in the result");
        Assert.assertTrue(getResults().contains("Model Admin Commands"), "Model Admin Commands is not present in the result");
        Assert.assertTrue(getResults().contains("Message Admin Commands"), "Message Admin Commands is not present in the result");
    }

    public void assertPageTitleIs(String expectedPageTitle)
    {
        Assert.assertTrue(getPageTitle().contains(expectedPageTitle), "Page title is correct");
    }

    public void executeCommand(String command)
    {
        log.info(String.format("Execute command: %s", command));
        findElement(commandField).clear();
        findElement(commandField).sendKeys(command);
        waitUntilElementClickable(execute).click();
    }

    public void assertResultIs(String expectedResult)
    {
        Assert.assertEquals(getResults(), expectedResult, "Result is correct");
    }
}
