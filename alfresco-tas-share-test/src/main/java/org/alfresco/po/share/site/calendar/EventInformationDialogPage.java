package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.BaseDialogComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EventInformationDialogPage extends BaseDialogComponent {

    public EventInformationDialogPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }
    private final By startDateTime = By.cssSelector("div[id$='_defaultContainer-startdate']");
    private final By endDateTime = By.cssSelector("div[id$='_defaultContainer-enddate']");

    public String getStartDateTime()
    {
        return findElement(startDateTime).getText();
    }

    public String getEndDateTime()
    {
        return findElement(endDateTime).getText();
    }
}
