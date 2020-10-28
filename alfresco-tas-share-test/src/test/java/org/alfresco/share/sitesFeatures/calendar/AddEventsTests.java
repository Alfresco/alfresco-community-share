package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.DashboardCustomization.UserDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.AddEventDialog;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/15/2016.
 */
public class AddEventsTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;

    @Autowired
    AddEventDialog addEventDialog;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SiteCalendarDashlet siteCalendarDashlet;

    @Autowired
    MyCalendarDashlet myCalendarDashlet;

    @Autowired
    Notification notification;
    DateTime today;
    private String user1 = String.format("user1-%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName-%s", RandomData.getRandomAlphanumeric());
    private String siteName2 = String.format("SiteName-C5452%s", RandomData.getRandomAlphanumeric());
    private String siteName3 = String.format("SiteName-C5465%s", RandomData.getRandomAlphanumeric());
    private String eventTitle = "testEvent";
    private String defaultStartTime = "12:00 PM";
    private String defaultEndTime = "1:00 PM";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        userService.addDashlet(user1, password, UserDashlet.MY_CALENDAR, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);
        siteService.create(user1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.CALENDAR, null);
        siteService.create(user1, password, domain, siteName2, siteName2, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.CALENDAR, null);
        siteService.create(user1, password, domain, siteName3, siteName3, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.CALENDAR, null);
        siteService.addDashlet(user1, password, siteName, SiteDashlet.SITE_CALENDAR, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(user1, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, siteName);
        siteService.delete(adminUser, adminPassword, siteName2);
        siteService.delete(adminUser, adminPassword, siteName3);

    }

    @BeforeMethod (alwaysRun = true)
    public void setupMethod()
    {
        today = new DateTime();
    }

    @TestRail (id = "C3086")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyAddEventForm()
    {
        calendarPage.navigate(siteName3);

        LOG.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Verify the items present on 'Add Event' form.");
        assertTrue(addEventDialog.checkEventDetailsSectionContainsFields(),
            "'Event Details' section contains: 'What' field (mandatory), 'Where' and 'Description' fields.");
        assertTrue(addEventDialog.checkTimeSectionDefaultValues(today),
            "'Time' section contains: 'All Day' check box unchecked, 'Start Date' and 'End Date' fields.");
        assertTrue(addEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");
        assertTrue(addEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(addEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(addEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");
    }

    @TestRail (id = "C5451")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed"  })
    public void addEventUsingAddEventButton()
    {
        String currentEventName = eventTitle + "C5451";

        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter the following event details: What: testEvent1, Where: Iasi, Description: First event");
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.typeInEventLocationInput("Iasi");
        addEventDialog.typeInEventDescriptionInput("First event");
        assertEquals(addEventDialog.getEventTitle(), currentEventName, "Text is entered in the 'What' field.");
        assertEquals(addEventDialog.getEventLocation(), "Iasi", "Text is entered in the 'Where' field.");
        assertEquals(addEventDialog.getEventDescription(), "First event", "Text is entered in the 'Description' field.");

        LOG.info("STEP 3: Click 'Save' button.");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 4: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
//        siteCalendarDashlet.assertEventListTitleEquals(currentEventName);
    }

    @TestRail (id = "C3156")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventByClickingOnTheCalendar()
    {
        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter 'testEvent1C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialog.typeInEventTitleInput(eventTitle + "1C3156");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(eventTitle + "1C3156"), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 3: Switch to 'Day' view. Click on the Calendar.");
        calendarPage.clickDayButton();
        calendarPage.clickOnTheCalendarDayView();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 4: Enter 'testEvent2C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialog.typeInEventTitleInput(eventTitle + "2C3156");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(eventTitle + "2C3156"), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Switch to 'Week' view. Click on the Calendar.");
        calendarPage.clickWeekButton();
        calendarPage.clickOnTheCalendarWeekView();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 6: Enter 'testEvent3C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialog.typeInEventTitleInput(eventTitle + "3C3156");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(eventTitle + "3C3156"), "Event is created and displayed on the 'Calendar' page.");

    }

    @TestRail (id = "C5452")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void agendaViewAddAnEventToThisCalendar()
    {
        calendarPage.navigate(siteName2);

        String currentEventName = eventTitle + "C5452";

        LOG.info("STEP 1: Switch to 'Agenda' view and click on 'Add an event to this calendar.' button.");
        calendarPage.clickAgendaButton();
        calendarPage.clickAddEventToThisCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter 'testEventC5452' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
        assertTrue(calendarPage.isEventPresentInAgenda(currentEventName), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5464")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void requiredFieldsForAddingAnEvent()
    {
        String currentEventName = eventTitle + "C5464";

        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Click 'Save' button.");
        addEventDialog.clickSave();
        //assertEquals(addEventDialog.getBalloonMessage(), "The value cannot be empty.", "'The value cannot be empty.' message appears near the 'What' field;");
        assertTrue(addEventDialog.isEventTitleInvalid(), "What field is marked in red.");
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is not closed.");

        LOG.info("STEP 3: Enter 'testEventC5464' in 'What' field and click 'Save' button.");
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.clickSaveButton();
//        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5460")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addAllDayEvent()
    {
        String currentEventName = eventTitle + "C5460";

        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter '" + currentEventName + "' in 'What' field and check 'All Day' check box.");
        addEventDialog.typeInEventTitleInput(currentEventName);
        addEventDialog.checkAllDayCheckBox();
        assertEquals(addEventDialog.getEventTitle(), currentEventName, "What field is filled.");
        assertFalse(addEventDialog.isStartTimeDisplayed(), "Start time is hidden");
        assertFalse(addEventDialog.isEndTimeDisplayed(), "End time is hidden");

        LOG.info("STEP 3: Click 'Save' button.");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");
//        assertTrue(calendarPage.isAllDayEvent(currentEventName), "Only the event's name is displayed, starting time is not displayed.");
    }

    @TestRail (id = "C3091")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed"  })
    public void addSeveralDaysDurationEvent()
    {
        calendarPage.navigate(siteName);

        String currentEventName = eventTitle + "C3091";
        DateTime startDate = today.plusDays(1);
        DateTime endDate = startDate.plusDays(2);

        LOG.info("STEP 1: Click 'Add Event' button and enter any text on the 'What' field");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(currentEventName);

        LOG.info("STEP 2: Select any 'Start Date'.");
        addEventDialog.selectStartDateFromCalendarPicker(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear());
        getBrowser().waitInSeconds(2);
        assertTrue(addEventDialog.hasStartDateValue(startDate), "Start date is selected.");

        LOG.info("STEP 3: Select any 'End Date', but which will be two days later than the 'Start Date'");
        addEventDialog.selectEndDateFromCalendarPicker(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear());
        assertTrue(addEventDialog.hasEndDateValue(endDate), "End date is selected.");

        LOG.info("STEP 4: Click 'Save' button.");
        addEventDialog.clickSaveButton();
//        assertEquals(notification.getDisplayedNotification(), "Event created", "'Event created' pop-up appears.");
//        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
//        assertTrue(siteCalendarDashlet.isEventPresentInList(currentEventName), "The event is displayed on 'Site Calendar' dashlet.");
        assertEquals(siteCalendarDashlet.getEventStartDate(currentEventName), startDate.toString("EEEE, d MMMM, yyyy"),
            "Following information is available for the event: start day, date, month, year (e.g. Monday, 4 July, 2016)");
        assertEquals(siteCalendarDashlet.getEventDetails(currentEventName),
            "12:00 PM " + currentEventName + " (until: " + endDate.toString("EEEE, d MMMM, yyyy") + " 1:00 PM)",
            "The event has expected details displayed on 'Site Calendar' dashlet.");

        LOG.info("STEP 6: Go to user's dashboard and verify 'My Calendar' dashlet.");
        userDashboardPage.navigate(user1);

        myCalendarDashlet.assertEventIsDisplayed(currentEventName)
            .assertEventTimeIs(currentEventName,
                startDate.toString("dd MMMM, yyyy") + " 12:00 PM - "
                    + endDate.toString("dd MMMM, yyyy") + " 1:00 PM");
    }

    @TestRail (id = "C5462")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventWithTags()
    {
        String currentEventName = eventTitle + "C5462";
        String siteNameForEventWithTags = String.format("SiteName-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteNameForEventWithTags, siteNameForEventWithTags, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteNameForEventWithTags, Page.CALENDAR, null);
        calendarPage.navigate(siteNameForEventWithTags);

        // precondition: Any event (e.g. testEvent0) with "tag0", "tag1", "tag2" tags is created.
        // event created at run time because when using sitePagesService.addCalendarEvent tags aren't incremented
        calendarPage.clickTodayInCalendar();
        addEventDialog.typeInEventTitleInput("testEvent0");
        addEventDialog.addTag("tag0");
        addEventDialog.addTag("tag1");
        addEventDialog.addTag("tag2");
        addEventDialog.clickSaveButton();

        LOG.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter '" + currentEventName + "' in 'What' field.");
        addEventDialog.typeInEventTitleInput(currentEventName);

        LOG.info("STEP 3: Go to 'Tags:' section and enter any tag in the 'Tags' field. Click 'Add' button.");
        addEventDialog.addTag("tag3");
        assertTrue(addEventDialog.isTagDisplayedInList("tag3"), "'tag3' is displayed on 'Tags' section.");

        LOG.info("STEP 4: Click 'Choose from popular tags in this site' link.");
        addEventDialog.choosePopularTagsInSite();
        assertTrue(addEventDialog.isTagDisplayedInPopularList("tag0"), "'tag0' is displayed on popular tags section.");
        assertTrue(addEventDialog.isTagDisplayedInPopularList("tag1"), "'tag1' is displayed on popular tags section.");
        assertTrue(addEventDialog.isTagDisplayedInPopularList("tag2"), "'tag2' is displayed on popular tags section.");

        LOG.info("STEP 5: Hover over 'tag0'. Click on 'Add' button for 'tag0'.");
        addEventDialog.addPopularTagByClickingAddButton("tag0");
        assertTrue(addEventDialog.isTagDisplayedInList("tag0"), "'tag0' is displayed on 'Tags' section.");

        LOG.info("STEP 6: Click on 'tag1', 'tag2' tags.");
        addEventDialog.addPopularTagByClickingTag("tag1");
        assertTrue(addEventDialog.isTagDisplayedInList("tag1"), "'tag1' is displayed on 'Tags' section.");
        addEventDialog.addPopularTagByClickingTag("tag2");
        assertTrue(addEventDialog.isTagDisplayedInList("tag2"), "'tag2' is displayed on 'Tags' section.");

        LOG.info("STEP 7: Click 'Save' button.");
        addEventDialog.clickSaveButton();
//        assertTrue(calendarPage.isEventPresentInCalendar(currentEventName), "Event is created and displayed on the 'Calendar' page.");

        LOG.info("STEP 8: Verify 'Tags' section.");
        assertEquals(calendarPage.getTagLink("tag0"), "tag0" + " (2)", "Following tags are displayed on 'Tags' section: tag0 (2)");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1" + " (2)", "Following tags are displayed on 'Tags' section: tag1 (2)");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2" + " (2)", "Following tags are displayed on 'Tags' section: tag2 (2)");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3" + " (1)", "Following tags are displayed on 'Tags' section: tag3 (1)");
    }

    @TestRail (id = "C5478")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventWithoutSaving()
    {
        String currentEventName = eventTitle + "C5478";

        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter '" + currentEventName + "' in 'What' field. Leave the default values for the other fields.");
        addEventDialog.typeInEventTitleInput(currentEventName);
        assertEquals(addEventDialog.getEventTitle(), currentEventName, currentEventName + " is added to 'What' field.");

        LOG.info("STEP 3: Click 'x' button.");
        addEventDialog.clickClose();
//        assertFalse(calendarPage.isEventPresentInCalendar(currentEventName), "Event is not created and not displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5477")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelAddingEvent()
    {
        String currentEventName = eventTitle + "C5477";

        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");

        LOG.info("STEP 2: Enter '" + currentEventName + "' in 'What' field. Leave the default values for the other fields.");
        addEventDialog.typeInEventTitleInput(currentEventName);
        assertEquals(addEventDialog.getEventTitle(), currentEventName, currentEventName + " is added to 'What' field.");

        LOG.info("STEP 3: Click 'Cancel' button.");
        addEventDialog.clickCancelButton();
        getBrowser().waitInSeconds(3);
//        assertFalse(calendarPage.isEventPresentInCalendar(currentEventName), "Event is not created and not displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5465")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyDefaultStartEndDatesForEvents()
    {
        calendarPage.navigate(siteName3);
        String stuff = today.toString("EEEE, d MMMM, yyyy");
        LOG.info("STEP 1: Click on any date from the calendar");
        calendarPage.clickTodayInCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialog.hasStartDateValue(today), "Current month and selected day are displayed by default for 'Start Date'.");
        assertTrue(addEventDialog.hasEndDateValue(today), "Current month and selected day are displayed by default for 'End Date'.");
        assertTrue(addEventDialog.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialog.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");

        LOG.info("STEP 2: Click 'x' button.");
        addEventDialog.clickClose();

        LOG.info("STEP 3: Switch to 'Day' view.");
        calendarPage.clickDayButton();
        assertEquals(calendarPage.getCalendarHeader(), today.toString("EE, dd, MMMM yyyy"), "Current date is displayed on the calendar.");

        LOG.info("STEP 4: Click anywhere on the calendar.");
        calendarPage.clickOnTheCalendarDayView();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialog.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialog.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialog.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialog.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");

        LOG.info("STEP 5: Click 'x' button.");
        addEventDialog.clickClose();

        LOG.info("STEP 6: Switch to 'Week' view.");
        calendarPage.clickWeekButton();
        assertEquals(calendarPage.getCalendarHeader(), calendarPage.getMondayFromCurrentWeek(), "Current week is displayed on the calendar.");

        LOG.info("STEP 7: Click on Thursday on the calendar.");
        calendarPage.clickOnTheCalendarWeekView();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialog.hasStartDateValue(calendarPage.getThursdayFromCurrentWeek()), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialog.hasEndDateValue(calendarPage.getThursdayFromCurrentWeek()), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialog.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialog.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");

        LOG.info("STEP 8: Click 'x' button.");
        addEventDialog.clickClose();

        LOG.info("STEP 9: Switch to 'Agenda' view and click on 'Add an event to this calendar.' button.");
        calendarPage.clickAgendaButton();
        calendarPage.clickAddEventToThisCalendar();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialog.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialog.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialog.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialog.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");

        LOG.info("STEP 10: Click 'x' button.");
        addEventDialog.clickClose();

        LOG.info("StEP 11: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialog.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialog.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialog.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialog.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialog.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
    }
}