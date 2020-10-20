package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/21/2016.
 */
public class SiteCalendarDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteCalendarDashlet siteCalendarDashlet;

    @Autowired
    CalendarPage calendarPage;

    private String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    private DateTime today = new DateTime();
    private DateTime tomorrow = today.plusDays(1);

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        List<DashboardCustomization.Page> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(DashboardCustomization.Page.CALENDAR);
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        siteService.addDashlet(user, password, siteName, DashboardCustomization.SiteDashlet.SITE_CALENDAR, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
    }

    @BeforeMethod (alwaysRun = true)
    public void beforeMethod()
    {
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @AfterMethod (alwaysRun = true)
    public void afterMethod()
    {
        cleanupAuthenticatedSession();
    }


    @TestRail (id = "C5492")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void siteCalendarDashletWithNoEvents()
    {
        siteDashboard.navigate(siteName);

        LOG.info("STEP 1: Verify 'Site Calendar' dashlet .");
        assertEquals(siteCalendarDashlet.getDashletMessage(), "No upcoming events", "The text: 'No upcoming events' is displayed.");

        LOG.info("STEP 2: Hover mouse over 'Site Calendar' dashlet");
//        assertTrue(siteCalendarDashlet.assertDashletHelpIconDisplayed(DashletHelpIcon.SITE_CALENDAR), "Help icon is displayed");

        LOG.info("STEP 3: Click on the '?' icon");
        siteCalendarDashlet.clickOnHelpIcon(DashletHelpIcon.SITE_CALENDAR);
        assertTrue(siteCalendarDashlet.isBalloonDisplayed(), "Help balloon is displayed");
        assertEquals(siteCalendarDashlet.getHelpBalloonMessage(), language.translate("siteCalendarDashlet.helpBalloonMessage"), "Help balloon text");
        LOG.info("Step 3: Click 'X' icon on balloon popup");
        siteCalendarDashlet.closeHelpBalloon();
        assertFalse(siteCalendarDashlet.isBalloonDisplayed(), "Help balloon isn't displayed");
    }

    @TestRail (id = "C5499")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES, "tobefixed" })
    public void siteCalendarDashletWithSomeEvents()
    {
        String format = "EEEE, d MMMM, yyyy";

        /**
         * precondition: Some events are created in the calendar with different dates
         *   Event1(all day, date A),
         *   Event2(time interval , date A),
         *   Event3(all day , date B)).
         */
        sitePagesService.addCalendarEvent(user, password, siteName, "Event1", "Event1 location", "Event1 description", today.toDate(), today.toDate(), "", "", true, "tag1");
        sitePagesService.addCalendarEvent(user, password, siteName, "Event2", "Event2 location", "Event2 description", today.toDate(), today.toDate(), "1:00 PM", "3:00 PM", false, "tag2");
        sitePagesService.addCalendarEvent(user, password, siteName, "Event3", "Event3 location", "Event3 description", tomorrow.toDate(), tomorrow.toDate(), "", "", true, "");

        siteDashboard.navigate(siteName);
        LOG.info("STEP 1: Verify the 'Site Calendar' dashlet");
        assertTrue(siteCalendarDashlet.isEventPresentInList("Event1"), "Event 1 is displayed in Site Calendar dashlet");
        assertEquals(siteCalendarDashlet.getEventStartDate("Event1"), today.toString(format), "Event1 is displayed under today.");
        assertEquals(siteCalendarDashlet.getEventDetails("Event1"), "Event1", "The event hasn't start time and end time.");

        assertTrue(siteCalendarDashlet.isEventPresentInList("Event2"), "Event 2 is displayed in Site Calendar dashlet");
        assertEquals(siteCalendarDashlet.getEventStartDate("Event2"), today.toString(format), "Event2 is displayed under today");
        assertEquals(siteCalendarDashlet.getEventDetails("Event2"), "1:00 PM - 3:00 PM Event2", "The event has start time and end time.");

        assertTrue(siteCalendarDashlet.isEventPresentInList("Event3"), "Event 3 is displayed in Site Calendar dashlet");
        assertEquals(siteCalendarDashlet.getEventStartDate("Event3"), tomorrow.toString(format), "Event3 is displayed under tomorrow");
        assertEquals(siteCalendarDashlet.getEventDetails("Event3"), "Event3", "The event hasn't start time and end time.");

        format = "EE, dd, MMMM yyyy";
        LOG.info("STEP 2: Click on 'Event1'");
        siteCalendarDashlet.clickEvent("Event1");
        assertEquals(calendarPage.getCalendarHeader(), today.toString(format));
        assertTrue(calendarPage.isEventPresentInCalendar("Event1"), "'Event1' is displayed on calendar page.");
        assertTrue(calendarPage.isAllDayEvent("Event1"), "'Event1' is an all day event.");
        assertTrue(calendarPage.isEventPresentInCalendar("Event2"), "'Event2' is displayed on calendar page.");
        assertFalse(calendarPage.isEventPresentInCalendar("Event3"), "'Event3' isn't displayed on calendar page.");

        LOG.info("STEP 3: Navigate back to 'Site Dashboard' and click on 'Event3'");
        siteDashboard.navigate(siteName);
        siteCalendarDashlet.clickEvent("Event3");
        assertEquals(calendarPage.getCalendarHeader(), tomorrow.toString(format));
        assertFalse(calendarPage.isEventPresentInCalendar("Event1"), "'Event1' isn't displayed on calendar page.");
        assertFalse(calendarPage.isEventPresentInCalendar("Event2"), "'Event2' isn't displayed on calendar page.");
        assertTrue(calendarPage.isEventPresentInCalendar("Event3"), "'Event3' is displayed on calendar page.");
        assertTrue(calendarPage.isAllDayEvent("Event3"), "'Event3' is an all day event.");

    }
}
