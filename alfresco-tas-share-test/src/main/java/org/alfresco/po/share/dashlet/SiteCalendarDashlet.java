package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;

import java.util.List;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by Claudia Agache on 7/13/2016.
 */
@PageObject
public class SiteCalendarDashlet extends Dashlet<SiteCalendarDashlet>
{
    private static final int BEGIN_INDEX = 0;
    private static final int RETRIES_60 = 60;

    //@Autowired
    CalendarPage calendarPage;

    @RenderWebElement
    @FindBy (css = "div.dashlet.calendar")
    private HtmlElement dashletContainer;

    @FindAll (@FindBy (css = "div.dashlet.calendar .detail-list-item span>a"))
    private List<WebElement> siteEventsNameList;

    @FindBy (css = "div.dashlet.calendar .dashlet-padding>h3")
    private WebElement dashletMessage;

    private final By eventStartDate = By.xpath("ancestor::*[@class='details2']//a");
    private String eventTitleLinkLocator = "//div[contains(@class, 'dashlet calendar')]//div[@class='detail-list-item']//a[contains(text(), '%s')]";
    private String eventTimeLocator = "//div[contains(@class, 'dashlet calendar')]//div[@class='detail-list-item']//span[contains(text(), '%s ')]";

    @Override
    public String getDashletTitle()
    {
        return dashletContainer.findElement(dashletTitle).getText();
    }

    private WebElement findEventByTitle(String eventTitle)
    {
        LOG.info("Find event by title: {}", eventTitle);
        return browser.findFirstElementWithValue(siteEventsNameList, eventTitle);
    }

    private WebElement waitUntilEventTitleLinkIsDisplayed(String eventLinkTitle)
    {
        LOG.info("Wait until event title link is displayed: {}", eventLinkTitle);
        return browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(
            eventTitleLinkLocator, eventLinkTitle)), WAIT_1, RETRIES_60);
    }

    private WebElement waitUntilEventTimeIsDisplayed(String eventTime)
    {
        LOG.info("Wait until event time is displayed: {}", eventTime);
        return browser.waitWithRetryAndReturnWebElement(By.xpath(String.format(
            eventTimeLocator, eventTime)), WAIT_1, RETRIES_60);
    }

    public SiteCalendarDashlet assertEventListTitleEquals(String expectedEventTitleLink)
    {
        LOG.info("Assert event list title equals: {}", expectedEventTitleLink);
        assertEquals(waitUntilEventTitleLinkIsDisplayed(expectedEventTitleLink).getText(), expectedEventTitleLink,
            String.format("Event list title not equals %s ", expectedEventTitleLink));

        return this;
    }

    public String getEventDetails(String eventTitle)
    {
        LOG.info("Get event title: {}", eventTitle);
        return findEventByTitle(eventTitle).findElement(By.xpath("..")).getText();
    }

    public SiteCalendarDashlet assertEventTimeEquals(String expectedEventTime, String eventTitle)
    {
        LOG.info("Assert event time equals: {}", expectedEventTime);
        String eventTime = getEventTimeWithoutTitle(expectedEventTime, eventTitle);
        assertEquals(eventTime, expectedEventTime,
            String.format("Event time not equals %s ", expectedEventTime));

        return this;
    }

    private String getEventTimeWithoutTitle(String eventTime, String eventTitle)
    {
        LOG.info("Get event time without title: {}", eventTime);
        String eventTimeAndTitle = waitUntilEventTimeIsDisplayed(eventTime).getText();
        return eventTimeAndTitle.substring(BEGIN_INDEX, eventTimeAndTitle.indexOf(eventTitle)).trim();
    }

    public String getEventStartDate(String eventTitle)
    {
        LOG.info("Get event start date: {}", eventTitle);
        return findEventByTitle(eventTitle).findElement(eventStartDate).getText();
    }

    public SiteCalendarDashlet assertEventStartDateEquals(String eventTitle, String eventStartDate)
    {
        LOG.info("Assert event date equals: {}", eventStartDate);
        assertEquals(getEventStartDate(eventTitle), eventStartDate,
            String.format("Event start date not equals %s", eventStartDate));

        return this;
    }

    public SiteCalendarDashlet assertNoUpcomingEventsMessageEquals(String expectedNoEventsMessage)
    {
        LOG.info("Assert no upcoming events message equals: {}", expectedNoEventsMessage);
        assertEquals(dashletMessage.getText(), expectedNoEventsMessage,
            String.format("No events message not equals %s ", expectedNoEventsMessage));

        return this;
    }

    public void clickEvent(String eventTitle)
    {
        LOG.info("Click event with title: {}", eventTitle);
        browser.findFirstElementWithValue(siteEventsNameList, eventTitle).click();
    }
}
