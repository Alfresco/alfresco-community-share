package org.alfresco.po.share;

import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class SystemErrorPage extends BasePage
{
    private final By errorHeader = By.cssSelector(".alf-error-header");

    public SystemErrorPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    public String getErrorHeader()
    {
        return getElementText(errorHeader);
    }

    public SystemErrorPage assertSomethingIsWrongWithThePageMessageIsDisplayed()
    {
        log.info("Assert Something is wrong with the page message is displayed");
        assertTrue(getErrorHeader().contains(language.translate("systemError.header")));
        return this;
    }
}
