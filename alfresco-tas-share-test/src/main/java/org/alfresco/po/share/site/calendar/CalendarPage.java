package org.alfresco.po.share.site.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CalendarPage extends SiteCommon<CalendarPage>
{
    @Autowired
    private AddEventDialog addEventDialog;

    @Autowired
    private EventInformationDialog eventInformationDialog;

    @Autowired
    private EditEventDialog editEventDialog;

    private final By addEventButton = By.cssSelector("button[id$='default-addEvent-button-button']");
    private final By calendarHeader = By.xpath("//span[@class='fc-header-title']/h2 | //h2[@id='calTitle']");
    private final By dayButton = By.cssSelector("button[id$='_default-day-button']");
    private final By weekButton = By.cssSelector("button[id$='_default-week-button']");
    private final By monthButton = By.cssSelector("button[id$='_default-month-button']");
    private final By agendaButton = By.cssSelector("button[id$='_default-agenda-button']");
    private final By previousButton = By.cssSelector("button[id$='_default-prev-button-button']");
    private final By nextButton = By.cssSelector("button[id$='_default-next-button-button']");
    private final By todayButton = By.cssSelector("button[id$='_default-today-button-button']");
    private final By showAllItems = By.cssSelector("a[rel='-all-']");
    private final By tags = By.cssSelector(".tag-link");
    private final By today = By.xpath("//div[contains(@class, 'fc-view') and not(contains(@style, 'display: none'))]//td[contains(@class, 'fc-today')]");
    private final By agendaEventsName = By.cssSelector(".yui-dt-data .yui-dt-col-name .yui-dt-liner");
    private final By agendaAddEvent = By.cssSelector("a.addEvent");
    private final By selectedView = By.cssSelector("span.yui-button-checked");
    private final By calendarView = By.id("yui-history-field");
    private final By deleteIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'deleteAction']");
    private final By editIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'editAction']");
    private final By infoIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[contains(@class, 'infoAction')]");
    private final By eventsList = By.xpath(
        "//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']");
    private final By allDayEvents = By.cssSelector("a[class*='fc-event-allday']");

    public CalendarPage(ThreadLocal<WebDriver> webDriver)
    {
        super(webDriver);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/calendar", getCurrentSiteName());
    }

    private WebElement selectTag(String tagName)
    {
        log.info("Select tag: {}", tagName);
        WebElement selectTagElement = webElementInteraction.findFirstElementWithValue(tags, tagName);
        webElementInteraction.waitUntilElementIsVisible(selectTagElement);
        webElementInteraction.clickElement(selectTagElement);

        return selectTagElement;
    }

    public boolean isTagDisplayed(String tagName)
    {
        log.info("Check tag is displayed: {}", tagName);
        return webElementInteraction.isElementDisplayed(selectTag(tagName));
    }

    public String getTagLink(String tagName)
    {
        log.info("Get tag link: {}", tagName);
        return selectTag(tagName).findElement(By.xpath("..")).getText();
    }

    public void clickTagLink(String tagName)
    {
        log.info("Click tag link: {}",tagName);
        selectTag(tagName);
    }

    private WebElement getEventTitleFromCalendar(String eventTitle)
    {
        log.info("Get event title from calendar: {}", eventTitle);
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(eventsList);
        return webElementInteraction.findFirstElementWithValue(webElementInteraction.waitUntilElementsAreVisible(eventsList), eventTitle);
    }

    private boolean isAllDayEventListSizeEquals(int expectedListSize)
    {
        webElementInteraction.waitUntilElementIsDisplayedWithRetry(allDayEvents);
        List<WebElement> titles = webElementInteraction.findElements(allDayEvents);

        return titles.size() == expectedListSize;
    }

    public CalendarPage assertAllDayEventListSizeEquals(int listSize)
    {
        log.info("Assert is not an all day event: {}", listSize);
        assertTrue(isAllDayEventListSizeEquals(listSize),
            String.format("Event with title %s is an all day event", listSize));

        return this;
    }

    public CalendarPage assertCalendarEventTitleEquals(String expectedCalendarEventTitle)
    {
        log.info("Assert calendar event title equals: {}", expectedCalendarEventTitle);
        assertEquals(getEventTitleFromCalendar(expectedCalendarEventTitle).getText(), expectedCalendarEventTitle,
        String.format("Calendar event title not equals %s ", expectedCalendarEventTitle));

        return this;
    }

    public EventInformationDialog clickOnEvent(String eventTitle)
    {
        try
        {
            if (getEventTitleFromCalendar(eventTitle).getText().equals(eventTitle))
            {
                log.info("Click event title: {}", eventTitle);
                webElementInteraction.mouseOver(getEventTitleFromCalendar(eventTitle));
                getEventTitleFromCalendar(eventTitle);
            }
            else
            {
                throw new NoSuchElementException("Unable to locate expected event.");
            }
        } catch (StaleElementReferenceException staleElementReferenceException)
        {
            log.info("Element is no longer appears in DOM: {}", staleElementReferenceException.getMessage());
            clickOnEvent(eventTitle);
        }
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public AddEventDialog clickAddEventButton()
    {
        log.info("Click add event button");
        webElementInteraction.waitUntilElementIsVisible(addEventButton);
        webElementInteraction.clickElement(addEventButton);

        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public boolean isShowAllItemsLinkDisplayed()
    {
        log.info("Check is show all items link displayed");
        return webElementInteraction.isElementDisplayed(showAllItems);
    }

    public CalendarPage clickDayButton()
    {
        log.info("Click day button");
        webElementInteraction.clickElement(dayButton);
        return this;
    }

    public CalendarPage clickWeekButton()
    {
        log.info("Click week button");
        webElementInteraction.clickElement(weekButton);
        return new CalendarPage(webDriver);
    }

    public CalendarPage clickMonthButton()
    {
        webElementInteraction.clickElement(monthButton);
        return this;
    }

    public CalendarPage clickAgendaButton()
    {
        log.info("Click agenda button");
        webElementInteraction.clickElement(agendaButton);
        return this;
    }

    public boolean isEventPresentInAgenda(String eventName)
    {
        log.info("Check is event present in agenda: {}", eventName);
        return webElementInteraction.findFirstElementWithValue(agendaEventsName, eventName) != null;
    }

    public EventInformationDialog clickOnEventInAgenda(String eventName)
    {
        log.info("Click event in agenda: {}", eventName);
        webElementInteraction.findFirstElementWithValue(agendaEventsName, eventName).click();
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public AddEventDialog clickTodayInCalendar()
    {
        log.info("Click today in calendar");
        webElementInteraction.clickElement(today);
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarDayView()
    {
        log.info("Click the calendar day view");
        webElementInteraction.findFirstDisplayedElement(By.cssSelector(".fc-view-agendaDay .fc-agenda-slots .fc-widget-content"));
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarWeekView()
    {
        log.info("click the calendar week view");
        webElementInteraction.findFirstDisplayedElement(By.cssSelector(".fc-view-agendaWeek .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickAddEventToThisCalendar()
    {
        log.info("Click add event to this calendar");
        webElementInteraction.clickElement(agendaAddEvent);
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public String getEventStartTimeFromCalendar(String eventTitle)
    {
        log.info("Get event start time from calendar");
        return getEventTitleFromCalendar(eventTitle).findElement(By.xpath("preceding-sibling::*[1]|../preceding-sibling::*[1]/div")).getText();
    }

    public String getEventDurationFromAgenda(String eventTitle)
    {
        log.info("Get event duration from agenda: {}", eventTitle);
        return webElementInteraction.findFirstElementWithValue(agendaEventsName, eventTitle).findElement(By.xpath("../preceding-sibling::*[1]/div")).getText();
    }

    public String getCalendarHeader()
    {
        log.info("Get calendar header text");
        return webElementInteraction.getElementText(calendarHeader);
    }

    public CalendarPage assertCalendarHeaderEquals(String expectedCalendarHeader)
    {
        log.info("Assert calendar header equals: {}", expectedCalendarHeader);
        assertEquals(getCalendarHeader(), expectedCalendarHeader,
            String.format("Calendar header not equals %s ", expectedCalendarHeader));

        return this;
    }

    public String getMondayFromCurrentWeek()
    {
        log.info("Get Monday from current week");
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().toString("d MMMM yyyy");
    }

    public DateTime getThursdayFromCurrentWeek()
    {
        log.info("Get Thursday from current week");
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().plusDays(3);
    }

    public String getSelectedViewName()
    {
        log.info("Get selected view name");
        return webElementInteraction.findElement(selectedView).getText();
    }

    public String viewDisplayed()
    {
        log.info("Get calendar view attribute");
        return webElementInteraction.findElement(calendarView).getAttribute("value");
    }

    public CalendarPage clickOnNextButton()
    {
        log.info("Get next button state");
        webElementInteraction.clickElement(nextButton);
        return this;
    }

    public CalendarPage clickOnPreviousButton()
    {
        log.info("Click previous button");
        webElementInteraction.clickElement(previousButton);
        return this;
    }

    public String getNextButtonState()
    {
        log.info("Get next button state");
        return webElementInteraction.waitUntilElementIsVisible(nextButton).getAttribute("disabled");
    }

    public String getTodayButtonState()
    {
        log.info("Get today button state");
        return webElementInteraction.waitUntilElementIsVisible(todayButton).getAttribute("disabled");
    }

    public DeleteDialog clickDeleteIcon(String eventName)
    {
        log.info("Click delete icon");
        WebElement eventElement = webElementInteraction.findFirstElementWithValue(agendaEventsName, eventName);
        webElementInteraction.mouseOver(eventElement);
        eventElement.findElement(deleteIcon).click();

        return new DeleteDialog(webDriver);
    }

    public EditEventDialog clickEditIcon(String eventName)
    {
        log.info("Click edit icon");
        WebElement eventElement = webElementInteraction.findFirstElementWithValue(agendaEventsName, eventName);
        webElementInteraction.mouseOver(eventElement);
        eventElement.findElement(editIcon).click();

        return (EditEventDialog) editEventDialog.renderedPage();
    }

    public EventInformationDialog clickViewIcon(String eventName)
    {
        log.info("Click view icon");
        WebElement eventElement = webElementInteraction.findFirstElementWithValue(agendaEventsName, eventName);
        webElementInteraction.mouseOver(eventElement);
        eventElement.findElement(infoIcon).click();

        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public boolean isTodayHighlightedInCalendar()
    {
        log.info("Check is today highlighted in calendar");
        return webElementInteraction.waitUntilElementIsVisible(today)
            .getAttribute("class").contains("fc-state-highlight");
    }

    public CalendarPage clickShowAllItems()
    {
        log.info("Click shows all items");
        webElementInteraction.findElement(By.cssSelector("a[rel='-all-']")).click();
        return new CalendarPage(webDriver);
    }

    public CalendarPage clickTodayButton()
    {
        log.info("Click today button");
        webElementInteraction.clickElement(todayButton);
        return this;
    }
}