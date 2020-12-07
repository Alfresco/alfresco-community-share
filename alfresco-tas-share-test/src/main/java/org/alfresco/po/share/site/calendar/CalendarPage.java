package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.alfresco.utility.web.browser.WebBrowser;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CalendarPage extends SiteCommon<CalendarPage>
{
    @Autowired
    AddEventDialog addEventDialog;

    @Autowired
    EventInformationDialog eventInformationDialog;

    //@Autowired
    DeleteDialog deleteDialog;

    @Autowired
    EditEventDialog editEventDialog;

    @RenderWebElement
    @FindBy (css = "button[id$='default-addEvent-button-button']")
    private Button addEventButton;

    @RenderWebElement
    @FindBy (css = "div[id$='defaultView']")
    private WebElement calendarContainer;

    @RenderWebElement
    @FindBy (xpath = "//span[@class='fc-header-title']/h2 | //h2[@id='calTitle']")
    private WebElement calendarHeader;

    @FindBy (css = "button[id$='_default-day-button']")
    private WebElement dayButton;

    @FindBy (css = "button[id$='_default-week-button']")
    private WebElement weekButton;

    @FindBy (css = "button[id$='_default-month-button']")
    private WebElement monthButton;

    @FindBy (css = "button[id$='_default-agenda-button']")
    private WebElement agendaButton;

    @FindBy (css = "button[id$='_default-prev-button-button']")
    private WebElement previousButton;

    @FindBy (css = "button[id$='_default-next-button-button']")
    private WebElement nextButton;

    @FindBy (css = "button[id$='_default-today-button-button']")
    private WebElement todayButton;

    // Tags
    @RenderWebElement
    @FindBy (css = "div.filter.tags")
    private WebElement tagsFilter;

    @FindBy (css = "a[rel='-all-']")
    private Table showAllItems;

    @FindAll (@FindBy (css = ".tag-link"))
    private List<WebElement> tags;

    @FindBy (xpath = "//div[contains(@class, 'fc-view') and not(contains(@style, 'display: none'))]//td[contains(@class, 'fc-today')]")
    private WebElement today;

    @FindAll (@FindBy (css = ".yui-dt-data .yui-dt-col-name .yui-dt-liner"))
    private List<WebElement> agendaEventsName;

    @FindBy (css = "a.addEvent")
    private Link agendaAddEvent;

    @RenderWebElement
    @FindBy (id = "calendar_t")
    private WebElement miniCalendar;

    private final By selectedView = By.cssSelector("span.yui-button-checked");
    private final By calendarView = By.id("yui-history-field");
    private final By deleteIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'deleteAction']");
    private final By editIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'editAction']");
    private final By infoIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[contains(@class, 'infoAction')]");
    private final By eventsList = By.xpath(
        "//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']");
    private final By allDayEvents = By.cssSelector("a[class*='fc-event-allday']");

    public CalendarPage(ThreadLocal<WebBrowser> browser)
    {
        super(browser);
    }

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/calendar", getCurrentSiteName());
    }

    public CalendarPage assertCalendarPageIsOpened()
    {
        LOG.info("Assert calendar page is opened");
        assertTrue(getBrowser().isElementDisplayed(miniCalendar), "Calendar page is not opened");
        return this;
    }

    private WebElement selectTag(String tagName)
    {
        LOG.info("Select tag: {}", tagName);
        WebElement selectTagElement = getBrowser().findFirstElementWithValue(tags, tagName);
        getBrowser().waitUntilElementVisible(selectTagElement);
        getBrowser().waitUntilElementClickable(selectTagElement);

        return selectTagElement;
    }

    public boolean isTagDisplayed(String tagName)
    {
        LOG.info("Check tag is displayed: {}", tagName);
        return getBrowser().isElementDisplayed(selectTag(tagName));
    }

    public String getTagLink(String tagName)
    {
        LOG.info("Get tag link: {}", tagName);
        return selectTag(tagName).findElement(By.xpath("..")).getText();
    }

    public void clickTagLink(String tagName)
    {
        LOG.info("Click tag link: {}",tagName);
        selectTag(tagName).click();
        this.renderedPage();
    }

    private WebElement getEventTitleFromCalendar(String eventTitle)
    {
        LOG.info("Get event title from calendar: {}", eventTitle);
        getBrowser().waitUntilElementIsDisplayedWithRetry(eventsList);
        return getBrowser().findFirstElementWithValue(getBrowser().waitUntilElementsVisible(eventsList), eventTitle);
    }

    private boolean isAllDayEventListSizeEquals(int expectedListSize)
    {
        getBrowser().waitUntilElementIsDisplayedWithRetry(allDayEvents);
        List<WebElement> titles = getBrowser().findElements(allDayEvents);

        return titles.size() == expectedListSize;
    }

    public CalendarPage assertAllDayEventListSizeEquals(int listSize)
    {
        LOG.info("Assert is not an all day event: {}", listSize);
        assertTrue(isAllDayEventListSizeEquals(listSize),
            String.format("Event with title %s is an all day event", listSize));

        return this;
    }

    public CalendarPage assertCalendarEventTitleEquals(String expectedCalendarEventTitle)
    {
        LOG.info("Assert calendar event title equals: {}", expectedCalendarEventTitle);
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
                LOG.info("Click event title: {}", eventTitle);
                getBrowser().mouseOver(getEventTitleFromCalendar(eventTitle));
                getEventTitleFromCalendar(eventTitle).click();
            } else
            {
                throw new NoSuchElementException("Unable to locate expected event.");
            }
        } catch (StaleElementReferenceException staleElementReferenceException)
        {
            LOG.info("Element is no longer appears in DOM: {}", staleElementReferenceException.getMessage());
            clickOnEvent(eventTitle);
        }
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public AddEventDialog clickAddEventButton()
    {
        LOG.info("Click add event button");
        getBrowser().waitUntilElementVisible(addEventButton);
        getBrowser().waitUntilElementClickable(addEventButton).click();

        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public boolean isShowAllItemsLinkDisplayed()
    {
        LOG.info("Check is show all items link displayed");
        return showAllItems.isDisplayed();
    }

    public CalendarPage clickDayButton()
    {
        LOG.info("Click day button");
        dayButton.click();
        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickWeekButton()
    {
        LOG.info("Click week button");
        getBrowser().waitUntilElementClickable(weekButton).click();
        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickMonthButton()
    {
        monthButton.click();
        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickAgendaButton()
    {
        LOG.info("Click agenda button");
        agendaButton.click();

        return (CalendarPage) this.renderedPage();
    }

    public boolean isEventPresentInAgenda(String eventName)
    {
        LOG.info("Check is event present in agenda: {}", eventName);
        return getBrowser().findFirstElementWithValue(agendaEventsName, eventName) != null;
    }

    public EventInformationDialog clickOnEventInAgenda(String eventName)
    {
        LOG.info("Click event in agenda: {}", eventName);
        getBrowser().findFirstElementWithValue(agendaEventsName, eventName).click();
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public AddEventDialog clickTodayInCalendar()
    {
        LOG.info("Click today in calendar");
        today.click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarDayView()
    {
        LOG.info("Click the calendar day view");
        getBrowser().findFirstDisplayedElement(By.cssSelector(".fc-view-agendaDay .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickOnTheCalendarWeekView()
    {
        LOG.info("click the calendar week view");
        getBrowser().findFirstDisplayedElement(By.cssSelector(".fc-view-agendaWeek .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public AddEventDialog clickAddEventToThisCalendar()
    {
        LOG.info("Click add event to this calendar");
        agendaAddEvent.click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    public String getEventStartTimeFromCalendar(String eventTitle)
    {
        LOG.info("Get event start time from calendar");
        return getEventTitleFromCalendar(eventTitle).findElement(By.xpath("preceding-sibling::*[1]|../preceding-sibling::*[1]/div")).getText();
    }

    public String getEventDurationFromAgenda(String eventTitle)
    {
        LOG.info("Get event duration from agenda: {}", eventTitle);
        return getBrowser().findFirstElementWithValue(agendaEventsName, eventTitle).findElement(By.xpath("../preceding-sibling::*[1]/div")).getText();
    }

    public String getCalendarHeader()
    {
        LOG.info("Get calendar header text");
        return calendarHeader.getText();
    }

    public CalendarPage assertCalendarHeaderEquals(String expectedCalendarHeader)
    {
        LOG.info("Assert calendar header equals: {}", expectedCalendarHeader);
        assertEquals(getCalendarHeader(), expectedCalendarHeader,
            String.format("Calendar header not equals %s ", expectedCalendarHeader));

        return this;
    }

    public String getMondayFromCurrentWeek()
    {
        LOG.info("Get Monday from current week");
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().toString("d MMMM yyyy");
    }

    public DateTime getThursdayFromCurrentWeek()
    {
        LOG.info("Get Thursday from current week");
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().plusDays(3);
    }

    public boolean isMiniCalendarDisplayed()
    {
        LOG.info("Check is mini calendar displayed");
        return getBrowser().isElementDisplayed(miniCalendar);
    }

    public String getSelectedViewName()
    {
        LOG.info("Get selected view name");
        return getBrowser().findElement(selectedView).getText();
    }

    public String viewDisplayed()
    {
        LOG.info("Get calendar view attribute");
        return getBrowser().findElement(calendarView).getAttribute("value");
    }

    public CalendarPage clickOnNextButton()
    {
        LOG.info("Get next button state");
        nextButton.click();

        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickOnPreviousButton()
    {
        LOG.info("Click previous button");
        previousButton.click();

        return (CalendarPage) this.renderedPage();
    }

    public String getNextButtonState()
    {
        LOG.info("Get next button state");
        return getBrowser().findElement(By.cssSelector("button[id$='_default-next-button-button']")).getAttribute("disabled");
    }

    public String getTodayButtonState()
    {
        LOG.info("Get today button state");
        return getBrowser().findElement(By.cssSelector("button[id$='_default-today-button-button']")).getAttribute("disabled");
    }

    public DeleteDialog clickDeleteIcon(String eventName)
    {
        LOG.info("Click delete icon");
        WebElement eventElement = getBrowser().findFirstElementWithValue(agendaEventsName, eventName);
        getBrowser().mouseOver(eventElement);
        eventElement.findElement(deleteIcon).click();

        return (DeleteDialog) deleteDialog.renderedPage();
    }

    public EditEventDialog clickEditIcon(String eventName)
    {
        LOG.info("Click edit icon");
        WebElement eventElement = getBrowser().findFirstElementWithValue(agendaEventsName, eventName);
        getBrowser().mouseOver(eventElement);
        eventElement.findElement(editIcon).click();

        return (EditEventDialog) editEventDialog.renderedPage();
    }

    public EventInformationDialog clickViewIcon(String eventName)
    {
        LOG.info("Click view icon");
        WebElement eventElement = getBrowser().findFirstElementWithValue(agendaEventsName, eventName);
        getBrowser().mouseOver(eventElement);
        eventElement.findElement(infoIcon).click();

        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    public boolean isTodayHighlightedInCalendar()
    {
        LOG.info("Check is today highlighted in calendar");
        return today.getAttribute("class").contains("fc-state-highlight");
    }

    public CalendarPage clickShowAllItems()
    {
        LOG.info("Click shows all items");
        getBrowser().findElement(By.cssSelector("a[rel='-all-']")).click();
        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickTodayButton()
    {
        LOG.info("Click today button");
        todayButton.click();
        return (CalendarPage) this.renderedPage();
    }
}