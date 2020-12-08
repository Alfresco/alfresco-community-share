package org.alfresco.po.share.dashlet;

import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.utility.web.HtmlPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Claudia Agache on 7/19/2016.
 */
@PageObject
public class MyCalendarDashlet extends Dashlet<MyCalendarDashlet>
{
    //@Autowired
    private CalendarPage calendarPage;

    //@Autowired
    private SiteDashboardPage siteDashboardPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.user-calendar")
    private WebElement dashletContainer;

    @FindAll (@FindBy (css = "td.yui-dt-col-event div.detail"))
    private List<WebElement> userEventsList;

    @FindAll (@FindBy (css = "td.yui-dt-col-event div.detail a[class*='theme-link']"))
    private List<WebElement> eventSiteLocationList;

    @FindBy (css = "div[id$='_default-events'] tbody.yui-dt-message div")
    private WebElement emptyDashletText;

    private By eventTimeLine = By.cssSelector("div:nth-of-type(1)");
    private By eventNameLink = By.cssSelector("h4 a");
    private By eventSiteLink = By.cssSelector("a[class*='theme-link']");

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement findEvent(String eventName)
    {
        browser.waitUntilElementsVisible(userEventsList);
        for(WebElement event : userEventsList)
        {
            if(event.findElement(eventNameLink).getText().equals(eventName))
            {
                return event;
            }
        }
        throw new NoSuchElementException(String.format("Event %s was not found", eventName));
    }

    /**
     * Check if the specific event is displayed in My Calendar Dashlet
     *
     * @param eventName
     * @return true if it is displayed in My Calendar Dashlet
     */
    public boolean isEventPresentInList(String eventName)
    {
        return browser.isElementDisplayed(findEvent(eventName));
    }

    public MyCalendarDashlet assertEventIsDisplayed(String eventName)
    {
        Assert.assertTrue(isEventPresentInList(eventName), String.format("Event %s is displayed", eventName));
        return this;
    }

    public MyCalendarDashlet assertEventTimeIs(String eventName, String eventTime)
    {
        Assert.assertEquals(findEvent(eventName).findElement(eventTimeLine).getText(), eventTime, "Event time is correct");
        return this;
    }

    public MyCalendarDashlet assertNoUpcomingEventsIsDisplayed()
    {
        Assert.assertEquals(browser.waitUntilElementVisible(emptyDashletText).getText(),
            language.translate("myCalendarDashlet.EmptyDashletText"), "No upcoming events is displayed");
        return this;
    }

    public CalendarPage selectEvent(String eventName)
    {
        findEvent(eventName).findElement(eventNameLink).click();
        return (CalendarPage) calendarPage.renderedPage();
    }

    public SiteDashboardPage selectSiteFromEvent(String eventName)
    {
        findEvent(eventName).findElement(eventSiteLink).click();
        return (SiteDashboardPage) siteDashboardPage.renderedPage();
    }
}

