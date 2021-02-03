package org.alfresco.po.share.dashlet;

import static org.alfresco.common.Wait.WAIT_1;
import static org.alfresco.common.Wait.WAIT_60;
import static org.testng.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class SiteCalendarDashlet extends Dashlet<SiteCalendarDashlet>
{
    private final int BEGIN_INDEX = 0;
    
    private final By dashletContainer = By.cssSelector("div.dashlet.calendar");
    private final By siteEventsNameList = By.cssSelector("div.dashlet.calendar .detail-list-item span>a");
    private final By dashletMessage = By.cssSelector("div.dashlet.calendar .dashlet-padding>h3");
    private final By eventStartDate = By.xpath("ancestor::*[@class='details2']//a");

    private String eventTitleLinkLocator = "//div[contains(@class, 'dashlet calendar')]//div[@class='detail-list-item']//a[contains(text(), '%s')]";
    private String eventTimeLocator = "//div[contains(@class, 'dashlet calendar')]//div[@class='detail-list-item']//span[contains(text(), '%s ')]";

    public SiteCalendarDashlet(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getDashletTitle()
    {
        return webElementInteraction.getElementText(webElementInteraction.waitUntilElementIsVisible(dashletContainer)
            .findElement(dashletTitle));
    }

    private WebElement findEventByTitle(String eventTitle)
    {
        log.info("Find event by title: {}", eventTitle);
        return webElementInteraction.findFirstElementWithValue(siteEventsNameList, eventTitle);
    }

    private WebElement waitUntilEventTitleLinkIsDisplayed(String eventLinkTitle)
    {
        log.info("Wait until event title link is displayed: {}", eventLinkTitle);
        return webElementInteraction.waitWithRetryAndReturnWebElement(By.xpath(String.format(
            eventTitleLinkLocator, eventLinkTitle)), WAIT_1.getValue(), WAIT_60.getValue());
    }

    private WebElement waitUntilEventTimeIsDisplayed(String eventTime)
    {
        log.info("Wait until event time is displayed: {}", eventTime);
        return webElementInteraction.waitWithRetryAndReturnWebElement(By.xpath(String.format(
            eventTimeLocator, eventTime)), WAIT_1.getValue(), WAIT_60.getValue());
    }

    public SiteCalendarDashlet assertEventListTitleEquals(String expectedEventTitleLink)
    {
        log.info("Assert event list title equals: {}", expectedEventTitleLink);
        assertEquals(waitUntilEventTitleLinkIsDisplayed(expectedEventTitleLink).getText(), expectedEventTitleLink,
            String.format("Event list title not equals %s ", expectedEventTitleLink));

        return this;
    }

    public String getEventDetails(String eventTitle)
    {
        log.info("Get event title: {}", eventTitle);
        return findEventByTitle(eventTitle).findElement(By.xpath("..")).getText();
    }

    public SiteCalendarDashlet assertEventTimeEquals(String expectedEventTime, String eventTitle)
    {
        log.info("Assert event time equals: {}", expectedEventTime);
        String eventTime = getEventTimeWithoutTitle(expectedEventTime, eventTitle);
        assertEquals(eventTime, expectedEventTime,
            String.format("Event time not equals %s ", expectedEventTime));

        return this;
    }

    private String getEventTimeWithoutTitle(String eventTime, String eventTitle)
    {
        log.info("Get event time without title: {}", eventTime);
        String eventTimeAndTitle = waitUntilEventTimeIsDisplayed(eventTime).getText();
        return eventTimeAndTitle.substring(BEGIN_INDEX, eventTimeAndTitle.indexOf(eventTitle)).trim();
    }

    public String getEventStartDate(String eventTitle)
    {
        log.info("Get event start date: {}", eventTitle);
        return findEventByTitle(eventTitle).findElement(eventStartDate).getText();
    }

    public SiteCalendarDashlet assertEventStartDateEquals(String eventTitle, String eventStartDate)
    {
        log.info("Assert event date equals: {}", eventStartDate);
        assertEquals(getEventStartDate(eventTitle), eventStartDate,
            String.format("Event start date not equals %s", eventStartDate));

        return this;
    }

    public SiteCalendarDashlet assertNoUpcomingEventsMessageEquals(String expectedNoEventsMessage)
    {
        log.info("Assert no upcoming events message equals: {}", expectedNoEventsMessage);
        assertEquals(webElementInteraction.getElementText(dashletMessage), expectedNoEventsMessage,
            String.format("No events message not equals %s ", expectedNoEventsMessage));

        return this;
    }

    public CalendarPage clickEvent(String eventTitle)
    {
        log.info("Click event with title: {}", eventTitle);
        webElementInteraction.findFirstElementWithValue(siteEventsNameList, eventTitle).click();

        return new CalendarPage(webDriver);
    }
}
