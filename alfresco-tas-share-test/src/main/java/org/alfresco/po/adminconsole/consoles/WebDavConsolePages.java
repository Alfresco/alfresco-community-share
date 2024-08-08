package org.alfresco.po.adminconsole.consoles;

import org.alfresco.po.share.SharePage2;
import org.alfresco.utility.web.common.Parameter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.util.List;
import static org.alfresco.utility.report.log.Step.STEP;

public class WebDavConsolePages<T> extends SharePage2<WebDavConsolePages<T>>  {

    private By pageHeader = By.cssSelector("td.textLocation");
    private By listingTable = By.cssSelector("table.listingTable");
    private By sharedLink = By.cssSelector("a[href='/alfresco/webdav/Shared']");
    private By upALevelLink = By.cssSelector("a[href='/alfresco/webdav']");

    public WebDavConsolePages(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath() {
        return "alfresco/webdav";
    }

    public WebDriver getWebDriver()
    {
        return webDriver.get();
    }

    public WebDavConsolePages<T> navigate()
    {
        String baseUrl1 = String.format("%s://%s:%s@%s:%s", defaultProperties.getScheme(),
            defaultProperties.getAdminUser(), defaultProperties.getAdminPassword(),
            defaultProperties.getServer(), defaultProperties.getPort());
        STEP(String.format("Navigate to %s", baseUrl1 + "/" + getRelativePath()));
        getWebDriver().navigate().to(baseUrl1 + "/" + getRelativePath());
        return null;
    }

    public WebDavConsolePages<T> assertPageHeaderRootIsCorrect()
    {
        Assert.assertEquals(findElement(pageHeader).getText(), "Directory listing for /", "WebDav page header is correct");
        return this;
    }

    public WebDavConsolePages<T> assertPageHeaderForDirectoryIsCorrect(String directorPath)
    {
        Assert.assertEquals(findElement(pageHeader).getText(), String.format("Directory listing for /%s", directorPath), "WebDav page header is correct");
        return this;
    }

    public WebDavConsolePages<T> clickSharedLink()
    {
        findElement(sharedLink).click();
        waitUntilElementIsVisible(upALevelLink);
        return this;
    }

    public WebDavConsolePages<T> assertUpALevelLinkIsDisplayed()
    {
        Assert.assertTrue(isElementDisplayed(upALevelLink), "Up a level link is displayed");
        return this;
    }

    public WebElement waitUntilElementClickable(By locator, long timeOutInSeconds) {
        Parameter.checkIsMandotary("Locator", locator);
        WebDriverWait wait = new WebDriverWait((WebDriver) this, timeOutInSeconds);
        return (WebElement)wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebDavConsolePages<T> assertUpALevelLinkIsNotDisplayed()
    {
        Assert.assertFalse(isElementDisplayed(upALevelLink), "Up a level link is displayed");
        return this;
    }

    public void clickUpToLevel()
    {
        waitInSeconds(5);
        findElement(upALevelLink).click();
    }

    public boolean assertDirectoryIsDisplayed(String name)
    {
        List<WebElement> allNameHighlights = findElements(listingTable);
        for (WebElement eachNameHighlight : allNameHighlights)
        {
            if (eachNameHighlight.getText().equals(name))
            {
                return true;
            }
        }
        return false;
    }
}
