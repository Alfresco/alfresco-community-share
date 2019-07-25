package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.calendar.AddEventDialog;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 7/11/2016.
 */
public class TagsTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;

    @Autowired
    AddEventDialog addEventDialog;

    @Autowired
    Notification notification;

    private String random = RandomData.getRandomAlphanumeric();
    private String user1 = "user1-" + random;
    private String siteName = "SiteName-" + random;
    private Date currentDate = new Date();
    private String tagName = "tag-" + random.toLowerCase();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        siteService.create(user1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user1, password, siteName, Page.CALENDAR, null);

        sitePagesService.addCalendarEvent(user1, password, siteName, "event1", "", "", currentDate, currentDate, "08:00", "09:00", false, "tag1");
        sitePagesService.addCalendarEvent(user1, password, siteName, "event2", "", "", currentDate, currentDate, "09:00", "10:00", false, "tag2");
        sitePagesService.addCalendarEvent(user1, password, siteName, "event3", "", "", currentDate, currentDate, "10:00", "11:00", false, "tag3");
        setupAuthenticatedSession(user1, password);
        calendarPage.navigate(siteName);
    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C3092")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addTagForEvent()
    {
        String eventName = "event-C3092-" + random;

        LOG.info("STEP 1: Click 'Add Event' button and enter any text on the 'What' field (e.g.: 'testEvent').");
        calendarPage.clickAddEventButton();
        addEventDialog.typeInEventTitleInput(eventName);
        assertEquals(addEventDialog.getEventTitle(), eventName, "Text is entered in the 'What' field.");

        LOG.info("STEP 2: Fill in 'Tags' field with tag '" + tagName + "' and click 'Add' button.");
        addEventDialog.addTag(tagName);
        assertTrue(addEventDialog.isTagDisplayedInList(tagName), "'test' is displayed on 'Tags' section.");

        LOG.info("STEP 3: Click 'Save' button and then verify the added tag.");
        getBrowser().waitInSeconds(1);
        addEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isTagDisplayed(tagName), "Tag is displayed in Tags list.");
        assertEquals(calendarPage.getTagLink(tagName), tagName + " (1)",
            "The following information is displayed for the added tag: tag name and in brackets number of events related to that tag (1)");

        LOG.info("STEP 4: Click on '" + tagName + "'.");
        calendarPage.clickTagLink(tagName);
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "Only 'testEvent' event is displayed on the 'Calendar' page.");
        assertFalse(calendarPage.isEventPresentInCalendar("event1"), "The other events created in preconditions are not displayed.");
        assertFalse(calendarPage.isEventPresentInCalendar("event2"), "The other events created in preconditions are not displayed.");
        assertFalse(calendarPage.isEventPresentInCalendar("event3"), "The other events created in preconditions are not displayed.");
    }

    @TestRail (id = "C3094")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void tagsSectionFromCalendarPage()
    {
        LOG.info("STEP 1: Verify 'Tags' section.");
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        LOG.info("STEP 2: Click on 'tag1' link.");
        calendarPage.clickTagLink("tag1");
        assertTrue(calendarPage.isEventPresentInCalendar("event1"), "Only 'event1' is displayed on 'Calendar' page.");
        assertFalse(calendarPage.isEventPresentInCalendar("event2"), "'event2' is not displayed.");
        assertFalse(calendarPage.isEventPresentInCalendar("event3"), "'event3' is not displayed.");

        LOG.info("STEP 3: Click on 'Show All Items' link.");
        calendarPage.clickTagLink("Show All Items");
        assertTrue(calendarPage.isEventPresentInCalendar("event1"), "All events created in preconditions are displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isEventPresentInCalendar("event2"), "'All events created in preconditions are displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isEventPresentInCalendar("event3"), "'All events created in preconditions are displayed on the 'Calendar' page.");

        LOG.info("STEP 4: Click on 'Day' tab and verify 'Tags' section.");
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        LOG.info("STEP 5: Click on 'Week' tab and verify 'Tags' section.");
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        LOG.info("STEP 6: Click on 'Agenda' tab and verify 'Tags' section.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");
    }
}