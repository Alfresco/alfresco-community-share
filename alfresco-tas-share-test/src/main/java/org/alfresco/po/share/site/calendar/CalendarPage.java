package org.alfresco.po.share.site.calendar;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.site.SiteCommon;
import org.alfresco.utility.web.annotation.PageObject;
import org.alfresco.utility.web.annotation.RenderWebElement;
import org.joda.time.DateTime;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.ArrayList;
import java.util.List;

@PageObject
public class CalendarPage extends SiteCommon<CalendarPage>
{
    @Autowired
    AddEventDialog addEventDialog;

    @Autowired
    EventInformationDialog eventInformationDialog;

    @Autowired
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

    private By eventsList = By.xpath("//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']");

    //private By eventsList = By.cssSelector("div.fc-event-bg");
    @FindAll (@FindBy (css = ".yui-dt-data .yui-dt-col-name .yui-dt-liner"))
    private List<WebElement> agendaEventsName;

    private By deleteIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'deleteAction']");
    private By editIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[@class = 'editAction']");
    private By infoIcon = By.xpath("../following-sibling::td[contains(@class, 'yui-dt-col-actions')]//a[contains(@class, 'infoAction')]");

    @FindBy (css = "a.addEvent")
    private Link agendaAddEvent;

    @RenderWebElement
    @FindBy (id = "calendar_t")
    private WebElement miniCalendar;

    private By selectedView = By.cssSelector("span.yui-button-checked");
    private By calendarView = By.id("yui-history-field");
    @FindAll (@FindBy (xpath = "//div[contains(@class, 'fc-view-agendaDay')/th[@calss, 'fc-agenda-axis fc-widget-header']"))
    private List<WebElement> hoursDisplayedInDayView;

    @FindAll (@FindBy (xpath = "//div[contains(@class, 'fc-view') and not(contains(@style,'display: none'))]//a[contains(@class , 'fc-event')]//*[@class='fc-event-title']"))
    private List<WebElement> eventList;

    @Override
    public String getRelativePath()
    {
        return String.format("share/page/site/%s/calendar", getCurrentSiteName());
    }

    /**
     * Get de WebElement with value = tagName
     *
     * @param tagName
     * @return
     */
    private WebElement selectTag(String tagName)
    {
        WebElement selectTagElement = browser.findFirstElementWithValue(tags, tagName);
        browser.waitUntilElementVisible(selectTagElement);
        browser.waitUntilElementClickable(selectTagElement);
        return selectTagElement;
    }

    /**
     * Check if a tag is displayed in the Tags list
     *
     * @param tagName
     * @return
     */
    public boolean isTagDisplayed(String tagName)
    {
        return selectTag(tagName) != null;
    }

    /**
     * Get the link compound by tag name and number of events associated with that tag
     *
     * @param tagName
     * @return String
     */
    public String getTagLink(String tagName)
    {
        browser.waitInSeconds(2);
        return selectTag(tagName).findElement(By.xpath("..")).getText();
    }

    /**
     * Click link from Tags section
     */
    public void clickTagLink(String tagName)
    {
        selectTag(tagName).click();
        browser.waitInSeconds(2);
        this.renderedPage();
    }

    private WebElement selectEvent(String event)
    {
        browser.waitUntilElementIsDisplayedWithRetry(eventsList);
        return browser.findFirstElementWithValue(browser.waitUntilElementsVisible(eventsList), event);
    }

    /**
     * Check if an event is present in calendar
     *
     * @param event
     * @return true if event is present
     */
    public boolean isEventPresentInCalendar(String event)
    {
        boolean eventState = false;
        try
        {
            int retry = 0;

            if (retry < 5)
            {
                if (selectEvent(event) != null)
                {
                    eventState = true;
                } else eventState = false;
            }

        } catch (StaleElementReferenceException se)
        {
            return isEventPresentInCalendar(event);
        }
        return eventState;
    }

    /**
     * Click on event displayed in calendar (Day, Week, Month views))
     *
     * @param event
     * @return
     */
    public EventInformationDialog clickOnEvent(String event)
    {
        try
        {
            if (isEventPresentInCalendar(event))
            {
                browser.mouseOver(selectEvent(event));
                selectEvent(event).click();
            } else
            {
                throw new NoSuchElementException("Unable to locate expected event.");
            }
        } catch (StaleElementReferenceException se)
        {
            clickOnEvent(event);
        }

        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }


    public EventInformationDialog clickEvent(String eventName)
    {
        return null;
    }

    /**
     * Click on Add Event Button
     *
     * @return AddEventDialog
     */
    public AddEventDialog clickAddEventButton()
    {
        getBrowser().waitUntilElementVisible(addEventButton);
        getBrowser().waitUntilElementClickable(addEventButton).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    /**
     * Check if 'Show All Items' link is dispayed in Tags section
     *
     * @return
     */
    public boolean isShowAllItemsLinkDisplayed()
    {
        return showAllItems.isDisplayed();
    }

    /**
     * Click on Day button from calendar bar
     *
     * @return CalendarPage Day view
     */
    public CalendarPage clickDayButton()
    {
        dayButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Click on Week button from calendar bar
     *
     * @return CalendarPage Week view
     */
    public CalendarPage clickWeekButton()
    {
        getBrowser().waitUntilElementClickable(weekButton).click();
        browser.waitInSeconds(4);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Click on Month button from calendar bar
     *
     * @return CalendarPage Month view
     */
    public CalendarPage clickMonthButton()
    {
        monthButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Click on Agenda button from calendar bar
     *
     * @return
     */
    public CalendarPage clickAgendaButton()
    {
        agendaButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Check if an event is present in calendar Agenda view
     *
     * @param eventName
     * @return true if event is present
     */
    public boolean isEventPresentInAgenda(String eventName)
    {
        return browser.findFirstElementWithValue(agendaEventsName, eventName) != null;
    }

    /**
     * Click on event displayed in calendar Agenda view
     *
     * @param eventName
     * @return
     */
    public EventInformationDialog clickOnEventInAgenda(String eventName)
    {
        browser.findFirstElementWithValue(agendaEventsName, eventName).click();
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    /**
     * Click on today date in Calendar, Month View
     *
     * @return AddEventDialog
     */
    public AddEventDialog clickTodayInCalendar()
    {
        today.click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    /**
     * Click on the Calendar, Day View
     *
     * @return AddEventDialog
     */
    public AddEventDialog clickOnTheCalendarDayView()
    {
        browser.findFirstDisplayedElement(By.cssSelector(".fc-view-agendaDay .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    /**
     * Click on the Calendar, Week View
     *
     * @return AddEventDialog
     */
    public AddEventDialog clickOnTheCalendarWeekView()
    {
        browser.findFirstDisplayedElement(By.cssSelector(".fc-view-agendaWeek .fc-agenda-slots .fc-widget-content")).click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    /**
     * Click on the Add an event to this calendar link from Agenda View
     *
     * @return AddEventDialog
     */
    public AddEventDialog clickAddEventToThisCalendar()
    {
        agendaAddEvent.click();
        return (AddEventDialog) addEventDialog.renderedPage();
    }

    /**
     * Check if the specified event is an all day event or not
     *
     * @param event
     * @return true if event title doesn't have any event-time siblings
     */
    public boolean isAllDayEvent(String event)
    {
        return selectEvent(event).findElements(By.xpath("../*")).size() == 1;
    }

    /**
     * Get event start time displayed in calendar
     *
     * @param event
     * @return start time
     */
    public String getEventStartTimeFromCalendar(String event)
    {
        return selectEvent(event).findElement(By.xpath("preceding-sibling::*[1]|../preceding-sibling::*[1]/div")).getText();
    }

    /**
     * Get event duration displayed in agenda
     *
     * @param event
     * @return event duration
     */
    public String getEventDurationFromAgenda(String event)
    {
        return browser.findFirstElementWithValue(agendaEventsName, event).findElement(By.xpath("../preceding-sibling::*[1]/div")).getText();
    }

    /**
     * Get Calendar header
     *
     * @return
     */
    public String getCalendarHeader()
    {
        return calendarHeader.getText();
    }

    /**
     * Get Monday from current week
     *
     * @return
     */
    public String getMondayFromCurrentWeek()
    {
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().toString("d MMMM yyyy");
    }

    /**
     * Get Thursday from current week
     *
     * @return
     */
    public DateTime getThursdayFromCurrentWeek()
    {
        DateTime today = new DateTime();
        return today.weekOfWeekyear().roundFloorCopy().plusDays(3);
    }

    /**
     * Method to check if the MiniCalendar is present on the Calendar Page
     */

    public boolean isMiniCalendarPresent()
    {
        return browser.isElementDisplayed(miniCalendar);
    }

    /**
     * Method to get the selected view name
     *
     * @return
     */
    public String getSelectedViewName()
    {
        return browser.findElement(selectedView).getText();
    }

    /**
     * Method to get the view displayed on the Calendar Page
     *
     * @return
     */
    public String viewDisplayed()
    {
        return browser.findElement(calendarView).getAttribute("value");
    }

    /**
     * Method to click on the Next button next to Agenda
     *
     * @return
     */
    public CalendarPage clickOnNextButton()
    {
        nextButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Method to click on the Previous button next to the Today button
     *
     * @return
     */
    public CalendarPage clickOnPreviousButton()
    {
        previousButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }

    /**
     * Method to get the state of the Next button to check if it is disabled or not.
     *
     * @return
     */
    public String getNextButtonState()
    {
        return browser.findElement(By.cssSelector("button[id$='_default-next-button-button']")).getAttribute("disabled");
    }

    /**
     * Method to get the state of the Today button
     */
    public String getTodayButtonState()
    {
        return browser.findElement(By.cssSelector("button[id$='_default-today-button-button']")).getAttribute("disabled");
    }

    /**
     * Click on specified event Delete icon from Agenda view
     *
     * @param eventName
     * @return DeleteDialog
     */
    public DeleteDialog clickDeleteIcon(String eventName)
    {
        WebElement eventElement = browser.findFirstElementWithValue(agendaEventsName, eventName);
        browser.mouseOver(eventElement);
        eventElement.findElement(deleteIcon).click();
        return (DeleteDialog) deleteDialog.renderedPage();
    }

    /**
     * Click on specified event Edit icon from Agenda view
     *
     * @param eventName
     * @return EditEventDialog
     */
    public EditEventDialog clickEditIcon(String eventName)
    {
        WebElement eventElement = browser.findFirstElementWithValue(agendaEventsName, eventName);
        browser.mouseOver(eventElement);
        eventElement.findElement(editIcon).click();
        return (EditEventDialog) editEventDialog.renderedPage();
    }

    /**
     * Click on specified event View icon from Agenda view
     *
     * @param eventName
     * @return EventInformationDialog
     */
    public EventInformationDialog clickViewIcon(String eventName)
    {
        WebElement eventElement = browser.findFirstElementWithValue(agendaEventsName, eventName);
        browser.mouseOver(eventElement);
        eventElement.findElement(infoIcon).click();
        return (EventInformationDialog) eventInformationDialog.renderedPage();
    }

    /**
     * Check if current date is highlighted
     *
     * @return
     */
    public Boolean isTodayHighlightedInCalendar()
    {
        return today.getAttribute("class").contains("fc-state-highlight");
    }

    public CalendarPage clickShowAllItems()
    {
        browser.findElement(By.cssSelector("a[rel='-all-']")).click();
        return (CalendarPage) this.renderedPage();
    }

    public CalendarPage clickTodayButton()
    {
        todayButton.click();
        browser.waitInSeconds(1);
        return (CalendarPage) this.renderedPage();
    }
}