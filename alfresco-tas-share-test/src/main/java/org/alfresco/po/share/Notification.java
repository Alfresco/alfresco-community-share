package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.annotation.PageObject;
import org.openqa.selenium.By;

/**
 * Created by Claudia Agache on 7/13/2016.
 */
@PageObject
public class Notification extends HtmlPage
{
    private By notification = By.cssSelector("span[class='message']");

    public String getDisplayedNotification()
    {
        return browser.waitUntilElementVisible(notification).getText();
    }

    public void waitUntilNotificationDisappears()
    {
        browser.waitUntilElementDisappears(notification, 30);
    }
}
