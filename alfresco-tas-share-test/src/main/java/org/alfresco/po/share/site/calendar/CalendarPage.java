package org.alfresco.po.share.site.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final By EventName = By.className("fc-event-title");
    private final By agendaAddEvent = By.cssSelector("a.addEvent");
    private final By selectedView = By.cssSelector("span.yui-button-checked");
    private final By calendarView = By.id("yui-history-field");
    private final By deleteIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'deleteAction']");
    private final By editIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'editAction']");
    private final By infoIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[contains(@class, 'infoAction')]");
    private final By eventsList = By.xpath(
        "//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']");
    private final By allDayEvents = By.cssSelector("a[class*='fc-event-allday']");
    private final By dates = By.cssSelector("#calendarcontainer[style*='display: block'] a.selector");
    private final By calendarPickerHeader = By.cssSelector("#calendarcontainer[style*='display: block'] .calheader");
    private final By nextMonthArrow = By.cssSelector("#calendarcontainer[style*='display: block'] .calnavright");
    private final By previousMonthArrow = By.cssSelector("#calendarcontainer[style*='display: block'] .calnavleft");
    private ArrayList<String> monthValues = new ArrayList<>(
        Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
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
        WebElement selectTagElement = findFirstElementWithValue(tags, tagName);
        waitUntilElementIsVisible(selectTagElement);
        clickElement(selectTagElement);

        return selectTagElement;
    }

    public boolean isTagDisplayed(String tagName)
    {
        log.info("Check tag is displayed: {}", tagName);
        waitInSeconds(3);
        return isElementDisplayed(selectTag(tagName));
    }

    public String getTagLink(String tagName)
    {
        log.info("Get tag link: {}", tagName);
        waitInSeconds(3);
        return selectTag(tagName).findElement(By.xpath("..")).getText();
    }

    public void clickTagLink(String tagName)
    {
        log.info("Click tag link: {}",tagName);
        waitInSeconds(3);
        selectTag(tagName);
    }

    private WebElement getEventTitleFromCalendar(String eventTitle)
    {
        log.info("Get event title from calendar: {}", eventTitle);
        waitUntilElementIsDisplayedWithRetry(eventsList);
        return findFirstElementWithValue(waitUntilElementsAreVisible(eventsList), eventTitle);
    }

    private boolean isAllDayEventListSizeEquals(int expectedListSize)
    {
        waitUntilElementIsDisplayedWithRetry(allDayEvents);
        List<WebElement> titles = findElements(allDayEvents);

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
        waitInSeconds(2);
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
                mouseOver(getEventTitleFromCalendar(eventTitle));
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

    public void clickEvent(String eventName)
    {
        log.info("Click event: {}", eventName);
        findFirstElementWithValue(EventName, eventName).click();
    }
    public void click_Event(String eventName)
    {
        log.info("Click event: {}", eventName);
        waitInSeconds(2);
        refresh();
        findFirstElementWithValue(EventName, eventName).click();
    }

    public void clickAddEventButton()
    {
        log.info("Click add event button");
        waitUntilElementIsVisible(addEventButton);
        clickElement(addEventButton);
    }

    public boolean isShowAllItemsLinkDisplayed()
    {
        log.info("Check is show all items link displayed");
        return isElementDisplayed(showAllItems);
    }

    public CalendarPage clickDayButton()
    {
        log.info("Click day button");
        clickElement(dayButton);
        return this;
    }

    public CalendarPage clickWeekButton()
    {
        log.info("Click week button");
        clickElement(weekButton);
        return new CalendarPage(webDriver);
    }

    public CalendarPage clickMonthButton()
    {
        clickElement(monthButton);
        return this;
    }

    public CalendarPage clickAgendaButton()
    {
        log.info("Click agenda button");
        clickElement(agendaButton);
        return this;
    }

    public boolean isEventPresentInAgenda(String eventName)
    {
        waitInSeconds(3);
        log.info("Check is event present in agenda: {}", eventName);
        return findFirstElementWithValue(agendaEventsName, eventName) != null;
    }
    public boolean isEventPresentInCalendar(String eventName)
    {
        log.info("Check is event present in agenda: {}", eventName);
        waitInSeconds(3);
        return findFirstElementWithValue(EventName, eventName) != null;
    }

    public void clickOnEventInAgenda(String eventName)
    {
        waitInSeconds(3);
        log.info("Click event in agenda: {}", eventName);
        findFirstElementWithValue(agendaEventsName, eventName).click();
    }

    public AddEventDialog clickTodayInCalendar()
    {
        log.info("Click today in calendar");
        clickElement(today);
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarDayView()
    {
        log.info("Click the calendar day view");
        findFirstDisplayedElement(By.cssSelector(".fc-view-agendaDay .fc-agenda-slots .fc-widget-content"));
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarWeekView()
    {
        log.info("click the calendar week view");
        findFirstDisplayedElement(By.cssSelector(".fc-view-agendaWeek .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickAddEventToThisCalendar()
    {
        log.info("Click add event to this calendar");
        clickElement(agendaAddEvent);
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
        return findFirstElementWithValue(agendaEventsName, eventTitle).findElement(By.xpath("../preceding-sibling::*[1]/div")).getText();
    }

    public String getCalendarHeader()
    {
        log.info("Get calendar header text");
        return getElementText(calendarHeader);
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
        return findElement(selectedView).getText();
    }

    public String viewDisplayed()
    {
        log.info("Get calendar view attribute");
        return findElement(calendarView).getAttribute("value");
    }

    public CalendarPage clickOnNextButton()
    {
        log.info("Get next button state");
        clickElement(nextButton);
        return this;
    }

    public CalendarPage clickOnPreviousButton()
    {
        log.info("Click previous button");
        clickElement(previousButton);
        return this;
    }

    public String getNextButtonState()
    {
        log.info("Get next button state");
        return waitUntilElementIsVisible(nextButton).getAttribute("disabled");
    }

    public String getTodayButtonState()
    {
        log.info("Get today button state");
        return waitUntilElementIsVisible(todayButton).getAttribute("disabled");
    }

    public DeleteDialog clickDeleteIcon(String eventName)
    {
        log.info("Click delete icon");
        WebElement eventElement = findFirstElementWithValue(agendaEventsName, eventName);
        mouseOver(eventElement);
        eventElement.findElement(deleteIcon).click();

        return new DeleteDialog(webDriver);
    }

    public EditEventDialog clickEditIcon(String eventName)
    {
        log.info("Click edit icon");
        WebElement eventElement = findFirstElementWithValue(agendaEventsName, eventName);
        mouseOver(eventElement);
        eventElement.findElement(editIcon).click();
        return null;
    }

    public EventInformationDialog clickViewIcon(String eventName)
    {
        log.info("Click view icon");
        WebElement eventElement = findFirstElementWithValue(agendaEventsName, eventName);
        mouseOver(eventElement);
        eventElement.findElement(infoIcon).click();

        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public boolean isTodayHighlightedInCalendar()
    {
        log.info("Check is today highlighted in calendar");
        return waitUntilElementIsVisible(today)
            .getAttribute("class").contains("fc-state-highlight");
    }

    public CalendarPage clickShowAllItems()
    {
        log.info("Click shows all items");
        findElement(By.cssSelector("a[rel='-all-']")).click();
        return new CalendarPage(webDriver);
    }

    public CalendarPage clickTodayButton()
    {
        log.info("Click today button");
        clickElement(todayButton);
        return this;
    }

    public void selectDate(int day)
    {
        List<WebElement> dates_ = waitUntilElementsAreVisible(dates);
        findFirstElementWithExactValue(dates_, String.valueOf(day)).click();
        waitUntilElementDisappears(By.id("calendarcontainer"), 15);
    }

    public void navigateToYear(int year)
    {
        while (!isCurrentYear(year))
        {
            if (Integer.parseInt(getYearHeader()) < year)
               clickElement(nextMonthArrow);
            else
                clickElement(previousMonthArrow);
        }
    }

    public void navigateToMonth(int month)
    {
        while (!isCurrentMonth(month))
        {
            int count = month - (monthValues.indexOf(getMonthHeader()) + 1);
            if (count > 0)
                clickElement(nextMonthArrow);
            else
                clickElement(previousMonthArrow);
        }

    }

    private boolean isCurrentMonth(int month)
    {
        return monthValues.indexOf(getMonthHeader()) + 1 == month;
    }

    private boolean isCurrentYear(int year)
    {
        return Integer.parseInt(getYearHeader()) == year;
    }

    private String getYearHeader()
    {
        String header = findElement(calendarPickerHeader).getText();
        return header.split("\n")[1].split(" ")[1];
    }

    private String getMonthHeader()
    {
        String header = findElement(calendarPickerHeader).getText();
        return header.split("\n")[1].split(" ")[0];
    }

    public CalendarPage clickTodayInCalendars()
    {
        log.info("Click today in calendar");
        clickElement(today);
        return this;
    }

    public boolean isEventPresentInCalendars(String eventName)
    {
        waitInSeconds(6);
        for (WebElement addTag : findElements(EventName))
        {
            if (addTag.getText().contains(eventName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isEventPresentInCalendarAgenda(String eventName)
    {
        waitInSeconds(2);
        for (WebElement addTag : findElements(agendaEventsName))
        {
            if (addTag.getText().contains(eventName))
            {
                return true;
            }
        }
        return false;
    }

    public void clickViewIcons(String eventName)
    {
        log.info("Click view icon");
        WebElement eventElement = findFirstElementWithValue(agendaEventsName, eventName);
        mouseOver(eventElement);
        eventElement.findElement(infoIcon).click();
    }
}