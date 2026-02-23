package org.alfresco.po.share;

import org.alfresco.po.share.site.DocumentLibraryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PageNotification extends DocumentLibraryPage {
    public PageNotification(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    private By notification = By.cssSelector("div.bd span.message");

    public String getDisplayedNotification() {

        return findElement(notification).getText();


    }
}
