package org.alfresco.po.share;

import org.alfresco.common.Language;
import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class SharePageObject extends HtmlPage
{
    public static final int WAIT_1 = 1;
    public static final int WAIT_5 = 5;
    public static final int WAIT_10 = 10;
    public static final int WAIT_15 = 15;
    public static final int WAIT_30 = 30;
    public static final int WAIT_60 = 60;
    public static final int DEFAULT_RETRY = 3;
    public static final int RETRY_TIMES = 20;
    public static String LAST_MODIFICATION_MESSAGE = "";
    public static final By MESSAGE_LOCATOR = By.cssSelector("div.bd span.message");

    @Autowired
    public Language language;

    @Autowired
    public Environment env;

    public String waitUntilNotificationMessageDisappears()
    {
        try
        {
            LAST_MODIFICATION_MESSAGE = browser.waitUntilElementVisible(MESSAGE_LOCATOR, WAIT_5).getText();
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException | StaleElementReferenceException exception)
        {
            LOG.error("Failed to get notification message {}", exception.getMessage());
        }
        return LAST_MODIFICATION_MESSAGE;
    }

    public void clearAndType(WebElement webElement, String value)
    {
        webElement.clear();
        webElement.sendKeys(value);
    }
}
