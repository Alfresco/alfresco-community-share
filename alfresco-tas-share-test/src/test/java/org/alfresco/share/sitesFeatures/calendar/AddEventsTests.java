package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.AddEventDialogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/15/2016.
 */
@Slf4j
public class AddEventsTests extends BaseTest
{
    //@Autowired
    CalendarPage calendarPage;

    //@Autowired
    SiteCalendarDashlet siteCalendarDashlet;

    @Autowired
    Notification notification;
    DateTime today;


    //@Autowired
    CustomizeSitePage customizeSitePage;
    //@Autowired
    MyCalendarDashlet myCalendarDashlet;
    SiteService siteService;
    AddEventDialogPage addEventDialogPage;

    SiteDashboardPage siteDashboardPage;
    Toolbar toolbar;

    //@Autowired
    SearchPage searchPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String eventTitle = "testEvent";
    private String defaultStartTime = "12:00 PM";
    private String defaultEndTime = "1:00 PM";

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a TestUser2");
        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        addDashlet(user1.get(), siteName.get(), SiteDashlet.SITE_CALENDAR, 1, 2);

        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        siteCalendarDashlet = new SiteCalendarDashlet(webDriver);
        calendarPage = new CalendarPage(webDriver);
        customizeSitePage = new CustomizeSitePage(webDriver);
        myCalendarDashlet = new MyCalendarDashlet(webDriver);
        addEventDialogPage = new AddEventDialogPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteService = new SiteService();
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteName.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C3086")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyAddEventForm()
    {
        today = new DateTime();
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendars();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Verify the items present on 'Add Event' form.");
        assertTrue(addEventDialogPage.checkEventDetailsSectionContainsFields(),
            "'Event Details' section contains: 'What' field (mandatory), 'Where' and 'Description' fields.");
        assertTrue(addEventDialogPage.checkTimeSectionDefaultValues(today),
            "'Time' section contains: 'All Day' check box unchecked, 'Start Date' and 'End Date' fields.");
        assertTrue(addEventDialogPage.isTagsSectionDisplayed(), "Tags section is displayed.");
        assertTrue(addEventDialogPage.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(addEventDialogPage.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(addEventDialogPage.isCloseButtonDisplayed(), "Close button is available on the form.");
    }

    @TestRail (id = "C5451")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventUsingAddEventButton()
    {
        String currentEventName = eventTitle + "C5451";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter the following event details: What: testEvent1, Where: Iasi, Description: First event");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.typeInEventLocationInput("Iasi");
        addEventDialogPage.typeInEventDescriptionInput("First event");
        log.info("STEP 3: Click 'Save' button.");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        log.info("STEP 4: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        calendarPage.navigate(siteName.get().getId());
    }

    @TestRail (id = "C3156")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventByClickingOnTheCalendar()
    {
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendars();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter 'testEvent1C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialogPage.typeInEventTitleInput(eventTitle + "1C3156");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(eventTitle + "1C3156"), "Event is created and displayed on the 'Calendar' page.");
        log.info("STEP 3: Switch to 'Day' view. Click on the Calendar.");
        calendarPage.clickDayButton();
        addEventDialogPage.clickOnTheCalendarDayView();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 4: Enter 'testEvent2C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialogPage.typeInEventTitleInput(eventTitle + "2C3156");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(eventTitle + "2C3156"), "Event is created and displayed on the 'Calendar' page.");
        log.info("STEP 5: Switch to 'Week' view. Click on the Calendar.");
        calendarPage.clickWeekButton();
        addEventDialogPage.clickOnTheCalendarWeekView();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 6: Enter 'testEvent3C3156' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialogPage.typeInEventTitleInput(eventTitle + "3C3156");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(eventTitle + "3C3156"), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5452")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void agendaViewAddAnEventToThisCalendar()
    {
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        String currentEventName = eventTitle + "C5452";
        log.info("STEP 1: Switch to 'Agenda' view and click on 'Add an event to this calendar.' button.");
        calendarPage.clickAgendaButton();
        addEventDialogPage.clickAddEventToThisCalendar();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter 'testEventC5452' in 'What' field. Leave the default values for the other fields and click 'Save' button.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInAgenda(currentEventName), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5464")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void requiredFieldsForAddingAnEvent()
    {
        String currentEventName = eventTitle + "C5464";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Click 'Save' button.");
        addEventDialogPage.clickSave();
        assertEquals(addEventDialogPage.getBalloonMessage(), "The value cannot be empty.", "'The value cannot be empty.' message appears near the 'What' field;");
        assertTrue(addEventDialogPage.isEventTitleInvalid(), "What field is marked in red.");
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is not closed.");
        log.info("STEP 3: Enter 'testEventC5464' in 'What' field and click 'Save' button.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(currentEventName), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C5460")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addAllDayEvent()
    {
        String currentEventName = eventTitle + "C5460";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter '" + currentEventName + "' in 'What' field and check 'All Day' check box.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        assertTrue(addEventDialogPage.isStartTimeDisplayed(), "Start time is hidden");
        assertTrue(addEventDialogPage.isEndTimeDisplayed(), "End time is hidden");
        log.info("STEP 3: Click 'Save' button.");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(currentEventName), "Event is created and displayed on the 'Calendar' page.");
    }

    @TestRail (id = "C3091")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addSeveralDaysDurationEvent() throws InterruptedException {
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        String currentEventName = eventTitle + "C3091";
        today = new DateTime();
        DateTime startDate = today;
        DateTime endDate = startDate.plusDays(1);
        log.info("STEP 1: Click 'Add Event' button and enter any text on the 'What' field");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        log.info("STEP 2: Select any 'Start Date'.");
        addEventDialogPage.selectStartDateFromCalendarPicker(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear());
        assertTrue(addEventDialogPage.hasStartDateValue(startDate), "Start date is selected.");
        log.info("STEP 3: Select any 'End Date', but which will be two days later than the 'Start Date'");
        addEventDialogPage.selectEndDateFromCalendarPicker(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear());
        assertTrue(addEventDialogPage.hasEndDateValue(endDate), "End date is selected.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        log.info("STEP 4: Click 'Save' button.");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        log.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertEquals(siteCalendarDashlet.getEventStartDate(currentEventName), startDate.toString("EEEE, d MMMM, yyyy"),
            "Following information is available for the event: start day, date, month, year (e.g. Monday, 4 July, 2016)");
        assertEquals(siteCalendarDashlet.getEventDetails(currentEventName),
            "12:00 PM " + currentEventName + " (until: " + endDate.toString("EEEE, d MMMM, yyyy") + " 1:00 PM)",
            "The event has expected details displayed on 'Site Calendar' dashlet.");
        log.info("STEP 6: Go to user's dashboard and verify 'My Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertEquals(siteCalendarDashlet.getEventDetails(currentEventName),
            "12:00 PM " + currentEventName + " (until: " + endDate.toString("EEEE, d MMMM, yyyy") + " 1:00 PM)",
            "The event has expected details displayed on 'Site Calendar' dashlet.");
        assertEquals(siteCalendarDashlet.getEventStartDate(currentEventName), startDate.toString("EEEE, d MMMM, yyyy"),
            "Following information is available for the event: start day, date, month, year (e.g. Monday, 4 July, 2016)");
    }

    @TestRail (id = "C5462")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventWithTags() throws InterruptedException {
        String currentEventName = eventTitle + "C5462";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickTodayInCalendars();
        addEventDialogPage.typeInEventTitleInput("testEvent0");
        addEventDialogPage.addTag("tag0");
        addEventDialogPage.addTag("tag1");
        addEventDialogPage.addTag("tag2");
        addEventDialogPage.clickSave();
        log.info("STEP 1: Click on any date from the calendar.");
        calendarPage.clickTodayInCalendars();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter '" + currentEventName + "' in 'What' field.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        log.info("STEP 3: Go to 'Tags:' section and enter any tag in the 'Tags' field. Click 'Add' button.");
        addEventDialogPage.addTag("tag3");
        assertTrue(addEventDialogPage.isTagDisplayedInLists("tag3"), "'tag3' is displayed on 'Tags' section.");
        log.info("STEP 4: Click 'Choose from popular tags in this site' link.");
        addEventDialogPage.choosePopularTagsInSite();
        assertTrue(addEventDialogPage.isTagDisplayedInPopularLists("tag0"), "'tag0' is displayed on popular tags section.");
        assertTrue(addEventDialogPage.isTagDisplayedInPopularLists("tag1"), "'tag1' is displayed on popular tags section.");
        assertTrue(addEventDialogPage.isTagDisplayedInPopularLists("tag2"), "'tag2' is displayed on popular tags section.");
        log.info("STEP 5: Hover over 'tag0'. Click on 'Add' button for 'tag0'.");
        addEventDialogPage.addTag("tag0");
        assertTrue(addEventDialogPage.isTagDisplayedInLists("tag0"), "'tag0' is displayed on 'Tags' section.");
        log.info("STEP 6: Click on 'tag1', 'tag2' tags.");
        addEventDialogPage.addPopularTagByClickingAddButton("tag1");
        assertTrue(addEventDialogPage.isTagDisplayedInLists("tag1"), "'tag1' is displayed on 'Tags' section.");
        addEventDialogPage.addPopularTagByClickingAddButton("tag2");
        assertTrue(addEventDialogPage.isTagDisplayedInLists("tag2"), "'tag2' is displayed on 'Tags' section.");
        log.info("STEP 7: Click 'Save' button.");
        addEventDialogPage.clickSave();
        assertTrue(calendarPage.isEventPresentInCalendars(currentEventName), "Event is created and displayed on the 'Calendar' page.");
        log.info("STEP 8: Verify 'Tags' section.");
        assertEquals(calendarPage.getTagLink("tag0"), "tag0" + " (2)", "Following tags are displayed on 'Tags' section: tag0 (2)");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1" + " (2)", "Following tags are displayed on 'Tags' section: tag1 (2)");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2" + " (2)", "Following tags are displayed on 'Tags' section: tag2 (2)");
    }

    @TestRail (id = "C5478")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addEventWithoutSaving()
    {
        String currentEventName = eventTitle + "C5478";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter '" + currentEventName + "' in 'What' field. Leave the default values for the other fields.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        assertEquals(addEventDialogPage.getEventTitle(), currentEventName, currentEventName + " is added to 'What' field.");
        log.info("STEP 3: Click 'x' button.");
        addEventDialogPage.clickClose();
    }

    @TestRail (id = "C5477")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelAddingEvent()
    {
        String currentEventName = eventTitle + "C5477";
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        log.info("STEP 2: Enter '" + currentEventName + "' in 'What' field. Leave the default values for the other fields.");
        addEventDialogPage.typeInEventTitleInput(currentEventName);
        assertEquals(addEventDialogPage.getEventTitle(), currentEventName, currentEventName + " is added to 'What' field.");
        log.info("STEP 3: Click 'Cancel' button.");
        addEventDialogPage.clickCancelButton();
    }

    @TestRail (id = "C5465")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyDefaultStartEndDatesForEvents()
    {
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        String stuff = today.toString("EEEE, d MMMM, yyyy");
        log.info("STEP 1: Click on any date from the calendar");
        calendarPage.clickTodayInCalendars();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialogPage.hasStartDateValue(today), "Current month and selected day are displayed by default for 'Start Date'.");
        assertTrue(addEventDialogPage.hasEndDateValue(today), "Current month and selected day are displayed by default for 'End Date'.");
        assertTrue(addEventDialogPage.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialogPage.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
        log.info("STEP 2: Click 'x' button.");
        addEventDialogPage.clickClose();
        log.info("STEP 3: Switch to 'Day' view.");
        calendarPage.clickDayButton();
        assertEquals(calendarPage.getCalendarHeader(), today.toString("EE, dd, MMMM yyyy"), "Current date is displayed on the calendar.");
        log.info("STEP 4: Click anywhere on the calendar.");
        addEventDialogPage.clickOnTheCalendarDayView();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialogPage.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialogPage.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialogPage.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialogPage.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
        log.info("STEP 5: Click 'x' button.");
        addEventDialogPage.clickClose();
        log.info("STEP 6: Switch to 'Week' view.");
        calendarPage.clickWeekButton();
        assertEquals(calendarPage.getCalendarHeader(), calendarPage.getMondayFromCurrentWeek(), "Current week is displayed on the calendar.");
        log.info("STEP 7: Click on Thursday on the calendar.");
        addEventDialogPage.clickOnTheCalendarWeekView();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialogPage.hasStartDateValue(calendarPage.getThursdayFromCurrentWeek()), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialogPage.hasEndDateValue(calendarPage.getThursdayFromCurrentWeek()), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialogPage.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialogPage.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
        log.info("STEP 8: Click 'x' button.");
        addEventDialogPage.clickClose();
        log.info("STEP 9: Switch to 'Agenda' view and click on 'Add an event to this calendar.' button.");
        calendarPage.clickAgendaButton();
        addEventDialogPage.clickAddEventToThisCalendar();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialogPage.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialogPage.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialogPage.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialogPage.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
        log.info("STEP 10: Click 'x' button.");
        addEventDialogPage.clickClose();
        log.info("StEP 11: Click 'Add Event' button.");
        calendarPage.clickAddEventButton();
        assertTrue(addEventDialogPage.isDialogDisplayed(), "'Add Event' dialog box is opened.");
        assertTrue(addEventDialogPage.hasStartDateValue(today), "Current date is displayed by default for 'Start Date'.");
        assertTrue(addEventDialogPage.hasEndDateValue(today), "Current date is displayed by default for 'End Date'.");
        assertTrue(addEventDialogPage.hasStartTimeValue(defaultStartTime), "Default time for 'Start Date' is '12:00 PM'");
        assertTrue(addEventDialogPage.hasEndTimeValue(defaultEndTime), "Default time for 'End Date' is '1:00 PM'");
    }
}