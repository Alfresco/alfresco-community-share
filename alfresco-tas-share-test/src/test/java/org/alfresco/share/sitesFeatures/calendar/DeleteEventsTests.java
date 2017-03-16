package org.alfresco.share.sitesFeatures.calendar;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.DeleteEventDialog;
import org.alfresco.po.share.site.calendar.EventInformationDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 7/12/2016.
 */
public class DeleteEventsTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;

    @Autowired
    EventInformationDialog eventInformationDialog;

    @Autowired
    DeleteEventDialog deleteEventDialog;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteCalendarDashlet siteCalendarDashlet;

    private String user1 = "user1-" + DataUtil.getUniqueIdentifier();
    private String siteName = "SiteName-" + DataUtil.getUniqueIdentifier();;
    private Date startDate = new Date();
    private String startHour = "2:00 PM";
    private String endHour = "4:00 PM";
    private List<Page> pagesToAdd = new ArrayList<>();
    private String eventName;
    private DateFormat df = new SimpleDateFormat("EEEE, d MMMM, yyyy");

    @BeforeClass
    public void setupTest()
    {
        pagesToAdd.add(Page.CALENDAR);
        userService.create(adminUser, adminPassword, user1, password, user1 + "@tests.com", user1, user1);
        siteService.create(user1, password, domain, siteName, siteName, Site.Visibility.PUBLIC);
        siteService.addPagesToSite(user1, password, siteName, pagesToAdd);
        siteService.addDashlet(user1, password, siteName, SiteDashlet.SITE_CALENDAR, DashletLayout.THREE_COLUMNS, 3, 1);
        setupAuthenticatedSession(user1, password);
    }

    @TestRail(id = "C3174")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteEventMonthView()
    {
        // precondition
        eventName = "testEvent-C3174-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Month' view.");
        calendarPage.navigate(siteName);
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmEventDelete();
        assertFalse(calendarPage.isEventPresentInCalendar(eventName), "The event is displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertFalse(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5400")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingEventMonthView()
    {
        // precondition
        eventName = "testEvent-C5400-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Month' view.");
        calendarPage.navigate(siteName);
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        LOG.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The event is still displayed on the 'Calendar' page.");

        LOG.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5415")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteEventDayView()
    {
        // precondition
        eventName = "testEvent-C5415-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmEventDelete();
        assertFalse(calendarPage.isEventPresentInCalendar(eventName), "The event is displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertFalse(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5416")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingEventDayView()
    {
        // precondition
        eventName = "testEvent-C5416-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        LOG.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The event is still displayed on the 'Calendar' page.");

        LOG.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5417")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteEventWeekView()
    {
        // precondition
        eventName = "testEvent-C5417-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmEventDelete();
        assertFalse(calendarPage.isEventPresentInCalendar(eventName), "The event is displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertFalse(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5418")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingEventWeekView()
    {
        // precondition
        eventName = "testEvent-C5418-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        LOG.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The event is still present on the 'Calendar' page.");

        LOG.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5419")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteEventByClickingOnTheEventAgendaView()
    {
        // precondition
        eventName = "testEvent-C5419-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Delete' button.");
        deleteEventDialog.confirmEventDelete();
        assertFalse(calendarPage.isEventPresentInAgenda(eventName), "The event is displayed on the 'Calendar' page.");

        LOG.info("STEP 5: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertFalse(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C5420")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingEventByClickingOnTheEventAgendaView()
    {
        // precondition
        eventName = "testEvent-C5420-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");

        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 3: Click 'Delete' button.");
        eventInformationDialog.clickDeleteButton();
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 4: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();

        LOG.info("STEP 5: Click 'Close' on 'Event Information' pop-up.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The event is still displayed on the 'Calendar' page.");

        LOG.info("STEP 6: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C6080")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void deleteEventByClickingDeleteIconAgendaView()
    {
        // precondition
        eventName = "testEvent-C6080-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("Step 1: Click on 'Delete' button.");
        calendarPage.clickDeleteIcon(eventName);
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 2: Click 'Delete' button.");
        deleteEventDialog.confirmEventDelete();
        assertFalse(calendarPage.isEventPresentInAgenda(eventName), "The event is displayed on the 'Calendar' page.");

        LOG.info("STEP 3: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertFalse(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }

    @TestRail(id = "C6084")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void cancelDeletingEventByClickingDeleteIconAgendaView()
    {
        // precondition
        eventName = "testEvent-C6084-" + DataUtil.getUniqueIdentifier();
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, "", "", startDate, startDate, startHour, endHour, false, "");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("Step 1: Click on 'Delete' button.");
        calendarPage.clickDeleteIcon(eventName);
        assertEquals(deleteEventDialog.getMessage(), "Are you sure you want to delete '" + eventName + "' on " + df.format(startDate) + "?",
                "'Delete Event' confirmation pop-up is displayed.");

        LOG.info("STEP 2: Click 'Cancel' button.");
        deleteEventDialog.clickCancel();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The event is still present on the 'Calendar' page.");

        LOG.info("STEP 3: Go to site's dashboard and verify 'Site Calendar' dashlet.");
        siteDashboardPage.navigate(siteName);
        assertTrue(siteCalendarDashlet.isEventPresentInList(eventName), "The event is displayed on 'Site Calendar' dashlet.");
    }
}