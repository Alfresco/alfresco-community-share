package org.alfresco.po.adminconsole.consoles;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.common.ShareTestContext;
import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.Utility;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.ContextConfiguration;
import static org.alfresco.utility.report.log.Step.STEP;

@Slf4j
@ContextConfiguration(classes = ShareTestContext.class)
public class TenantConsolePages<T> extends SharePage2<TenantConsolePages<T>> {

    private By commandField = By.id("cmd");
    private By execute = By.cssSelector("input[value='Execute']");
    public By resultBy = By.cssSelector("div.column-full > pre");
    private By results = By.cssSelector("div.column-full > pre");

    public TenantConsolePages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/s/admin/admin-tenantconsole";
    }

    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public TenantConsolePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public void executeCommand(String command)
    {
        log.info(String.format("Execute command: %s", command));
        findElement(commandField).clear();
        findElement(commandField).sendKeys(command);
        waitUntilElementClickable(execute).click();
    }

    public void waitForResult(String result)
    {
        int retry = 0;
        int retryCount = 60;
        waitUntilElementIsPresent(resultBy);
        WebElement resultElement = null;
        while (retry < retryCount && resultElement == null)
        {
            retry++;
            Utility.waitToLoopTime(1, "Wait until tenant command is executed");
            waitUntilElementIsPresent(resultBy);
            try
            {
                resultElement = findFirstElementWithValue(resultBy, result);
            }
            catch (StaleElementReferenceException e)
            {
                continue;
            }
        }
        log.info(String.format("Result is: '%s'", findElement(resultBy).getText()));
    }

    public String createTenant(String tenant, String password)
    {
        executeCommand(String.format("create %s %s", tenant, password));
        waitForResult(String.format("created tenant: %s", tenant.toLowerCase()));
        return findElement(results).getText();
    }

    public String deleteTenant(String tenant)
    {
        executeCommand(String.format("delete %s", tenant));
        waitForResult(String.format("Deleted tenant: %s", tenant.toLowerCase()));
        return findElement(results).getText();
    }

    public String disableTenant(String tenant)
    {
        executeCommand(String.format("disable %s", tenant));
        waitForResult(String.format("Disabled tenant: %s", tenant.toLowerCase()));
        return findElement(results).getText();
    }

    public String enableTenant(String tenant)
    {
        executeCommand(String.format("enable %s", tenant));
        waitForResult(String.format("Enabled tenant: %s", tenant.toLowerCase()));
        return findElement(results).getText();
    }

    public String showTenants()
    {
        executeCommand("show tenants");
        Utility.waitToLoopTime(1, "Wait for show tenants command");
        return waitUntilElementIsVisible(findElement(results)).getText();
    }
}
