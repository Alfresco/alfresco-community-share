package org.alfresco.po.share;

import org.alfresco.common.Language;
import org.alfresco.utility.web.HtmlPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class SharePageObject extends HtmlPage
{
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
            LAST_MODIFICATION_MESSAGE = browser.waitUntilElementVisible(MESSAGE_LOCATOR, 5).getText();
            getBrowser().waitUntilElementDisappears(MESSAGE_LOCATOR);
        }
        catch (TimeoutException exception)
        {
            // do nothing and carry on as this might be expected, meaning that the element might be expected to already disappear
        }
        return LAST_MODIFICATION_MESSAGE;
    }

    public void clearAndType(WebElement webElement, String value)
    {
        webElement.clear();
        webElement.sendKeys(value);
    }
}
