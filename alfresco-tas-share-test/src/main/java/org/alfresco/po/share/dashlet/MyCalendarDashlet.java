package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MyCalendarDashlet extends Dashlet<MyCalendarDashlet>
{
    private final By dashletContainer = By.cssSelector("div.dashlet.user-calendar");
    private final By userEventsList = By.cssSelector("td.yui-dt-col-event div.detail");
    private final By emptyDashletText = By.cssSelector("div[id$='_default-events'] tbody.yui-dt-message div");
    private final By eventTimeLine = By.cssSelector("div:nth-of-type(1)");
    private final By eventNameLink = By.cssSelector("h4 a");
    private final By eventSiteLink = By.cssSelector("a[class*='theme-link']");

    public MyCalendarDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return waitUntilElementIsVisible(dashletContainer).findElement(dashletTitle).getText();
    }

    private WebElement findEvent(String eventName)
    {
        List<WebElement> events = waitUntilElementsAreVisible(userEventsList);
        for(WebElement event : events)
        {
            if(event.findElement(eventNameLink).getText().equals(eventName))
            {
                return event;
            }
        }
        throw new NoSuchElementException(String.format("Event %s was not found", eventName));
    }

    public boolean isEventPresentInList(String eventName)
    {
        return isElementDisplayed(findEvent(eventName));
    }

    public MyCalendarDashlet assertEventIsDisplayed(String eventName)
    {
        assertTrue(isEventPresentInList(eventName), String.format("Event %s is displayed", eventName));
        return this;
    }

    public MyCalendarDashlet assertEventTimeIs(String eventName, String eventTime)
    {
        assertEquals(findEvent(eventName).findElement(eventTimeLine).getText(), eventTime, "Event time is correct");
        return this;
    }

    public MyCalendarDashlet assertNoUpcomingEventsIsDisplayed()
    {
        assertEquals(getElementText(emptyDashletText),
            language.translate("myCalendarDashlet.EmptyDashletText"), "No upcoming events is displayed");
        return this;
    }

    public CalendarPage selectEvent(String eventName)
    {
        findEvent(eventName).findElement(eventNameLink).click();
        return new CalendarPage(webDriver);
    }

    public SiteDashboardPage selectSiteFromEvent(String eventName)
    {
        findEvent(eventName).findElement(eventSiteLink).click();
        return new SiteDashboardPage(webDriver);
    }
}

