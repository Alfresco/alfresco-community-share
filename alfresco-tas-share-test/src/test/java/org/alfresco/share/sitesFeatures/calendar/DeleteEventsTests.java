package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.DeleteDialog;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EventInformationDialogPage;

import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Claudia Agache on 7/12/2016.
 */
public class DeleteEventsTests extends BaseTest
{
    //@Autowired
    CalendarPage calendarPage;
    EventInformationDialogPage eventInformationDialogPage;
    @Autowired
    private SiteService siteService;
    @Autowired
    protected SitePagesService sitePagesService;
    //@Autowired
    DeleteDialog deleteEventDialog;

    //@Autowired
    SiteDashboardPage siteDashboardPage;

//    @Autowired
    SiteCalendarDashlet siteCalendarDashlet;
    private Date startDate = new Date();
    private String startHour = "2:00 PM";
    private String endHour = "4:00 PM";
    private String eventName;
    private DateFormat df = new SimpleDateFormat("EEEE, d MMMM, yyyy");
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Site siteNameC2216 is created");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        calendarPage = new CalendarPage(webDriver);
        eventInformationDialogPage = new EventInformationDialogPage(webDriver);
        deleteEventDialog = new DeleteDialog(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteCalendarDashlet = new SiteCalendarDashlet(webDriver);

        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.CALENDAR, null);
        siteService.addDashlet(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), SiteDashlet.SITE_CALENDAR, DashletLayout.THREE_COLUMNS, 3, 1);
        authenticateUsingLoginPage(user1.get());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C3174")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteEventMonthView()
    {
        // precondition
        eventName = String.format("testEvent-C3174-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Month' view.");
        calendarPage.navigate(siteName.get());
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmDeletion();
        assertFalse(calendarPage.isEventPresentInCalendars(eventName), "The event is displayed on the 'Calendar' page.");

        log.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertFalse(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5400")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingEventMonthView()
    {
        // precondition
        eventName = String.format("testEvent-C5400-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Month' view.");
        calendarPage.navigate(siteName.get());
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        log.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialogPage.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The event is still displayed on the 'Calendar' page.");

        log.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertTrue(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");

    }

    @TestRail (id = "C5415")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteEventDayView()
    {
        // precondition
        eventName = String.format("testEvent-C5415-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmDeletion();
        assertFalse(calendarPage.isEventPresentInCalendars(eventName), "The event is displayed on the 'Calendar' page.");

        log.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertFalse(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5416")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingEventDayView()
    {
        // precondition
        eventName = String.format("testEvent-C5416-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        log.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialogPage.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The event is still displayed on the 'Calendar' page.");

        log.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertTrue(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5417")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteEventWeekView()
    {
        // precondition
        eventName = String.format("testEvent-C5417-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmDeletion();
        assertFalse(calendarPage.isEventPresentInCalendars(eventName), "The event is displayed on the 'Calendar' page.");

        log.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertFalse(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5418")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingEventWeekView()
    {
        // precondition
        eventName = String.format("testEvent-C5418-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), String.format("%s event is displayed in calendar", eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        log.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialogPage.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The event is still present on the 'Calendar' page.");

        log.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertTrue(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5419")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteEventByClickingOnTheEventAgendaView()
    {
        // precondition
        eventName = String.format("testEvent-C5419-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmDeletion();
        assertFalse(calendarPage.isEventPresentInCalendarAgenda(eventName), "The event is displayed on the 'Calendar' page.");

        log.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertFalse(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C5420")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingEventByClickingOnTheEventAgendaView()
    {
        // precondition
        eventName = String.format("testEvent-C5420-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        log.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.navigate(siteName.get());
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInCalendarAgenda(eventName));

        log.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialogPage.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        log.info("STEP 3: Click 'Delete' button.");
        eventInformationDialogPage.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        log.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialogPage.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendarAgenda(eventName), "The event is still displayed on the 'Calendar' page.");

        log.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertTrue(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C6080")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void deleteEventByClickingDeleteIconAgendaView()
    {
        // precondition
        eventName = String.format("testEvent-C6080-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");
        calendarPage.navigate(siteName.get());
        calendarPage.clickAgendaButton();

        log.info("Step 1: Click on 'Delete' button.");
        calendarPage.clickDeleteIcon(eventName);
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 2: Click 'Delete' button.");
        deleteEventDialog.confirmDeletion();
        assertFalse(calendarPage.isEventPresentInCalendarAgenda(eventName), "The event is displayed on the 'Calendar' page.");

        log.info("STEP 3: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertFalse(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail (id = "C6084")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelDeletingEventByClickingDeleteIconAgendaView()
    {
        // precondition
        eventName = String.format("testEvent-C6084-%s", RandomData.getRandomAlphanumeric());
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), eventName, "", "", startDate, startDate, startHour, endHour, false, "");
        calendarPage.navigate(siteName.get());
        calendarPage.clickAgendaButton();

        log.info("Step 1: Click on 'Delete' button.");
        calendarPage.clickDeleteIcon(eventName);
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
            "'Delete Event' confirmation pop-up is displayed.");

        log.info("STEP 2: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();
        assertTrue(calendarPage.isEventPresentInCalendarAgenda(eventName), "The event is still present on the 'Calendar' page.");

        log.info("STEP 3: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName.get());
        assertTrue(siteCalendarDashlet.isEventPresentInCalendarDashlet(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }
}