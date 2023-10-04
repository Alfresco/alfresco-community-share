package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EditEventDialog;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class ViewEventTests extends BaseTest
{
    @Autowired
    SitePagesService sitePagesService;
    CalendarPage calendarPage;

    EditEventDialog editEventDialog;

    private DateTime startDate = new DateTime();
    private DateTime endDate = startDate.plusDays(4);
    private String startHour = "2:00 PM";
    private String endHour = "4:00 PM";
    private String eventName = "testEvent";
    private String eventLocation = "Iasi";
    private String eventDescription = "Event number 1";
    private String eventTags = "tag1, tag2, tag3";
    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a TestUser2");
        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteName.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        addDashlet(user.get(), siteName.get(), DashboardCustomization.SiteDashlet.SITE_CALENDAR, 1, 2);
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), startHour, endHour, false, eventTags);


        calendarPage = new CalendarPage(webDriver);
        editEventDialog = new EditEventDialog(webDriver);
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteName.get());
        deleteAllCookiesIfNotNull();
    }

    private String formatDate(DateTime date, String hour)
    {
        return date.toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    @TestRail (id = "C3167")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventMonthView()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickMonthButton();
        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(editEventDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(editEventDialog.areButtonsEnabled(), "All buttons should be enabled");
    }

    @TestRail (id = "C5407")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventDayView()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(editEventDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(editEventDialog.areButtonsEnabled(), "All buttons should be enabled");
    }

    @TestRail (id = "5408")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventWeekView()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(editEventDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(editEventDialog.areButtonsEnabled(), "All buttons should be enabled");
    }

    @TestRail (id = "C5409")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventByClickingOnTheEventAgendaView()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(editEventDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(editEventDialog.areButtonsEnabled(), "All buttons should be enabled");
    }

    @TestRail (id = "C6109")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventByClickingViewIconAgendaView()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        log.info("STEP 2: Click 'View' icon.");
        calendarPage.clickViewIcons(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(editEventDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(editEventDialog.areButtonsEnabled(), "All buttons should be enabled");
    }

    @TestRail (id = "C5402")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void closeEventInformationPopup()
    {
        authenticateUsingLoginPage(user.get());
        calendarPage.navigate(siteName.get().getId());
        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 2: Click 'Close' button.");
        editEventDialog.clickOnCancelButton();
        assertFalse(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is closed.");

        log.info("STEP 3: Click again on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 4: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is closed.");
    }
}
