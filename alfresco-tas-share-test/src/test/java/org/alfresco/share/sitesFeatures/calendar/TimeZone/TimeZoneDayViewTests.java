package org.alfresco.share.sitesFeatures.calendar.TimeZone;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.calendar.AddEventDialog;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EventInformationDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.report.Bug;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/26/2016.
 */
public class TimeZoneDayViewTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;

    @Autowired
    AddEventDialog addEventDialog;

    @Autowired
    EventInformationDialog eventInformationDialog;

    @Autowired
    Notification notification;

    private String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    private DateTime today = new DateTime();
    private DateTime endDate = today.plusDays(2);
    private DateTime aWeekAgo = today.minusWeeks(1);
    private DateTime nextWeek = today.plusWeeks(1);
    private DateTime aMonthAgo = today.minusMonths(1);
    private DateTime nextMonth = today.plusMonths(1);
    private String clientATimeZone = "tzutil /s \"GTB Standard Time\"";
    private String clientBTimeZone = "tzutil /s \"GMT Standard Time\"";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.CALENDAR, null);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser,adminPassword,siteName );
    }


    private String formatDate(DateTime date, String hour)
    {
        return date.toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    private void changeTimeZone(String command)
    {
        try
        {
            Runtime.getRuntime().exec(command);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Bug(id="SHA-2165", status = Bug.Status.OPENED)
    @TestRail(id = "C5973")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void singleDayEvent()
    {
        String currentEventName = "testEvent-C5973";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1: Create a new single day event on clientA: Start Date today at 2:30 PM, End Date today at 5:30 PM");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.typeInStartTimeInput("2:30 PM");
        addEventDialog.typeInEndTimeInput("5:30 PM");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(today, "12:30 PM"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(today, "3:30 PM"), "Following information is available: Time section with End Date");
    }


    @TestRail(id = "C5974")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void singleDayAllDayEvent()
    {
        String currentEventName = "testEvent-C5974";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1: Create a new single day event on clientA: Start Date today, End Date today, All Day yes");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.checkAllDayCheckBox();
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Created event is an all day event.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), today.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), today.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with End Date");
    }

    @Bug(id="SHA-2165", status = Bug.Status.OPENED)
    @TestRail(id = "C5975")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleDaysEvent()
    {
        String currentEventName = "testEvent-C5975";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date today at 2:30 PM, End Date the day after tomorrow at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.typeInStartTimeInput("2:30 PM");
        addEventDialog.selectEndDateFromCalendarPicker(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear());
        addEventDialog.typeInEndTimeInput("5:25 PM");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(today, "12:30 PM"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, "3:25 PM"), "Following information is available: Time section with End Date");
    }

    @TestRail(id = "C5976")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleDaysAllDayEvent()
    {
        String currentEventName = "testEvent-C5976";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date today, End Date the day after tomorrow, All Day yes");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.checkAllDayCheckBox();
        addEventDialog.selectEndDateFromCalendarPicker(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear());
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), today.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), endDate.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with End Date");
    }

    @Bug(id="SHA-2165", status = Bug.Status.OPENED)
    @TestRail(id = "C5977")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleWeeksEvent()
    {
        String currentEventName = "testEvent-C5977";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a week ago at 2:30 PM, End Date a date from next week at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.selectStartDateFromCalendarPicker(aWeekAgo.getDayOfMonth(), aWeekAgo.getMonthOfYear(), aWeekAgo.getYear());
        addEventDialog.typeInStartTimeInput("2:30 PM");
        addEventDialog.selectEndDateFromCalendarPicker(nextWeek.getDayOfMonth(), nextWeek.getMonthOfYear(), nextWeek.getYear());
        addEventDialog.typeInEndTimeInput("5:25 PM");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertEquals(calendarPage.getEventStartTimeFromCalendar(currentEventName), "2:30pm", "Event starting time is the one filled at creation.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(aWeekAgo, "12:30 PM"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(nextWeek, "3:25 PM"), "Following information is available: Time section with End Date");
    }

    @TestRail(id = "C5978")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleWeeksAllDayEvent()
    {
        String currentEventName = "testEvent-C5978";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a week ago, End Date a date from next week, All day yes");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.checkAllDayCheckBox();
        addEventDialog.selectStartDateFromCalendarPicker(aWeekAgo.getDayOfMonth(), aWeekAgo.getMonthOfYear(), aWeekAgo.getYear());
        addEventDialog.selectEndDateFromCalendarPicker(nextWeek.getDayOfMonth(), nextWeek.getMonthOfYear(), nextWeek.getYear());
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), aWeekAgo.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), nextWeek.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with End Date");
    }

    @Bug(id="SHA-2165", status = Bug.Status.OPENED)
    @TestRail(id = "C5979")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleMonthsEvent()
    {
        String currentEventName = "testEvent-C5979";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a month ago at 2:30 PM, End Date a date from next month at 5:25 PM");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.selectStartDateFromCalendarPicker(aMonthAgo.getDayOfMonth(), aMonthAgo.getMonthOfYear(), aMonthAgo.getYear());
        addEventDialog.typeInStartTimeInput("2:30 PM");
        addEventDialog.selectEndDateFromCalendarPicker(nextMonth.getDayOfMonth(), nextMonth.getMonthOfYear(), nextMonth.getYear());
        addEventDialog.typeInEndTimeInput("5:25 PM");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(aMonthAgo, "12:30 PM"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(nextMonth, "3:25 PM"), "Following information is available: Time section with End Date");
    }

    @TestRail(id = "C5980")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void multipleMonthsAllDayEvent()
    {
        String currentEventName = "testEvent-C5980";
        changeTimeZone(clientATimeZone);
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1:Create a multiple days event on clientA: Start Date a date from a month ago, End Date a date from next month, All day yes");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.checkAllDayCheckBox();
        addEventDialog.selectStartDateFromCalendarPicker(aMonthAgo.getDayOfMonth(), aMonthAgo.getMonthOfYear(), aMonthAgo.getYear());
        addEventDialog.selectEndDateFromCalendarPicker(nextMonth.getDayOfMonth(), nextMonth.getMonthOfYear(), nextMonth.getYear());
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Event created is an all day event.");

        LOG.info("STEP 2: Open 'Calendar' page ('Day' view) on clientB and verify event's details, by clicking on the event.");
        changeTimeZone(clientBTimeZone);
        getBrowser().refresh();
        calendarPage.renderedPage();
        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is displayed on the 'Calendar' page.");
        calendarPage.clickOnEvent(currentEventName);
        assertEquals(eventInformationDialog.getStartDateTime(), aMonthAgo.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), nextMonth.toString("EEEE, d MMMM, yyyy"), "Following information is available: Time section with End Date");
    }
}
