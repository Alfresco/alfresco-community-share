package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EventInformationDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/11/2016.
 */
public class ViewEventTests extends ContextAwareWebTest
{
    //@Autowired
    CalendarPage calendarPage;

    @Autowired
    EventInformationDialog eventInformationDialog;

    private String user1 = String.format("user1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    private DateTime startDate = new DateTime();
    private DateTime endDate = startDate.plusDays(4);
    private String startHour = "2:00 PM";
    private String endHour = "4:00 PM";
    private String eventName = "testEvent";
    private String eventLocation = "Iasi";
    private String eventDescription = "Event number 1";
    private String eventTags = "tag1, tag2, tag3";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        siteService.create(user1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.CALENDAR, null);
        sitePagesService.addCalendarEvent(user1, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), startHour, endHour, false, eventTags);
    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    private String formatDate(DateTime date, String hour)
    {
        return date.toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    @TestRail (id = "C3167")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventMonthView()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        calendarPage.clickMonthButton();
        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(eventInformationDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(eventInformationDialog.areButtonsEnabled(), "All buttons should be enabled");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C5407")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventDayView()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Day' view.");
        calendarPage.clickDayButton();
//        assertTrue(calendarPage.isEventPresentInCalendar(eventName), String.format("%s event is displayed in calendar", eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(eventInformationDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(eventInformationDialog.areButtonsEnabled(), "All buttons should be enabled");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "5408")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventWeekView()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Week' view.");
        calendarPage.clickWeekButton();
//        assertTrue(calendarPage.isEventPresentInCalendar(eventName), String.format("%s event is displayed in calendar", eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(eventInformationDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(eventInformationDialog.areButtonsEnabled(), "All buttons should be enabled");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C5409")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventByClickingOnTheEventAgendaView()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        LOG.info("STEP 2: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(eventInformationDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(eventInformationDialog.areButtonsEnabled(), "All buttons should be enabled");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C6109")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void viewEventByClickingViewIconAgendaView()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        LOG.info("STEP 1: Open 'Calendar' page for '" + siteName + "' and select 'Agenda' view.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName));

        LOG.info("STEP 2: Click 'View' icon.");
        calendarPage.clickViewIcon(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: testEvent");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: Iasi");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: Event number 1");
        assertEquals(eventInformationDialog.getTagsDetails(), eventTags, "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(), formatDate(startDate, startHour), "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(), formatDate(endDate, endHour), "Following information is available: Time section with End Date");
        assertTrue(eventInformationDialog.areButtonsEnabled(), "All buttons should be enabled");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C5402")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })
    public void closeEventInformationPopup()
    {
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 2: Click 'Close' button.");
        eventInformationDialog.clickCancelButton();
        assertFalse(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is closed.");

        LOG.info("STEP 3: Click again on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is opened.");

        LOG.info("STEP 4: Click 'X' button.");
        eventInformationDialog.clickClose();
        assertFalse(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for 'testEvent' is closed.");
        cleanupAuthenticatedSession();
    }
}
