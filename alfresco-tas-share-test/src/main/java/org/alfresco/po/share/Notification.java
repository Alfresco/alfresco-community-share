package org.alfresco.po.share;

import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.openqa.selenium.By;

/**
 * Created by Claudia Agache on 7/13/2016.
 */
@PageObject
public class Notification extends HtmlPage
{
    private By notification = By.cssSelector("div.bd span.message");

    public String getDisplayedNotification()
    {
        return browser.waitUntilElementVisible(notification).getText();
    }

    public void waitUntilNotificationDisappears()
    {
        browser.waitUntilElementDisappears(notification, 30);
    }
}
