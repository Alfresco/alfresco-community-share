package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.SiteService;

import org.alfresco.po.share.site.calendar.AddEventDialogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
/**
 * Created by Claudia Agache on 7/11/2016.
 */
public class TagsTests extends BaseTest
{
    //@Autowired
    CalendarPage calendarPage;

    @Autowired
    private SiteService siteService;

    @Autowired
    protected SitePagesService sitePagesService;

    AddEventDialogPage addEventDialogPage;
    private String random = RandomData.getRandomAlphanumeric();
    private Date currentDate = new Date();
    private String tagName = "tag-" + random.toLowerCase();
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
        addEventDialogPage = new AddEventDialogPage(webDriver);


        siteService.addPageToSite(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), Page.CALENDAR, null);

        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "event1", "", "", currentDate, currentDate, "08:00", "09:00", false, "tag1");
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "event2", "", "", currentDate, currentDate, "09:00", "10:00", false, "tag2");
        sitePagesService.addCalendarEvent(user1.get().getUsername(), user1.get().getPassword(), siteName.get().getId(), "event3", "", "", currentDate, currentDate, "10:00", "11:00", false, "tag3");

        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        contentService.deleteTreeByPath(getAdminUser().getUsername(), getAdminUser().getPassword(), "/User Homes/" + user1.get().getUsername());
        deleteSitesIfNotNull(siteName.get());
        deleteUsersIfNotNull(user1.get());
    }

    @TestRail (id = "C3092")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void addTagForEvent() {
        String eventName = "event-C3092-" + random;

        log.info("STEP 1: Click 'Add Event' button and enter any text on the 'What' field (e.g.: 'testEvent').");
        calendarPage.clickAddEventButton();
        addEventDialogPage.typeInEventTitleInput(eventName);
        assertEquals(addEventDialogPage.getEventTitle(), eventName, "Text is entered in the 'What' field.");

        log.info("STEP 2: Fill in 'Tags' field with tag '" + tagName + "' and click 'Add' button.");
        addEventDialogPage.addTag(tagName);
        assertTrue(addEventDialogPage.isTagDisplayedInLists(tagName), "'test' is displayed on 'Tags' section.");

        log.info("STEP 3: Click 'Save' button and then verify the added tag.");
        addEventDialogPage.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "Event is created and displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isTagDisplayed(tagName), "Tag is displayed in Tags list.");
        assertEquals(calendarPage.getTagLink(tagName), tagName + " (1)",
            "The following information is displayed for the added tag: tag name and in brackets number of events related to that tag (1)");

        log.info("STEP 4: Click on '" + tagName + "'.");
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
        log.info("STEP 1: Verify 'Tags' section.");
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        log.info("STEP 2: Click on 'tag1' link.");
        calendarPage.clickTagLink("tag1");
        assertTrue(calendarPage.isEventPresentInCalendar("event1"), "Only 'event1' is displayed on 'Calendar' page.");
        assertFalse(calendarPage.isEventPresentInCalendar("event2"), "'event2' is not displayed.");
        assertFalse(calendarPage.isEventPresentInCalendar("event3"), "'event3' is not displayed.");

        log.info("STEP 3: Click on 'Show All Items' link.");
        calendarPage.clickTagLink("Show All Items");
        assertTrue(calendarPage.isEventPresentInCalendar("event1"), "All events created in preconditions are displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isEventPresentInCalendar("event2"), "'All events created in preconditions are displayed on the 'Calendar' page.");
        assertTrue(calendarPage.isEventPresentInCalendar("event3"), "'All events created in preconditions are displayed on the 'Calendar' page.");

        log.info("STEP 4: Click on 'Day' tab and verify 'Tags' section.");
        calendarPage.clickDayButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        log.info("STEP 5: Click on 'Week' tab and verify 'Tags' section.");
        calendarPage.clickWeekButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");

        log.info("STEP 6: Click on 'Agenda' tab and verify 'Tags' section.");
        calendarPage.clickAgendaButton();
        assertTrue(calendarPage.isShowAllItemsLinkDisplayed(), "'Show All Items' link is displayed.");
        assertEquals(calendarPage.getTagLink("tag1"), "tag1 (1)", "tag1 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag2"), "tag2 (1)", "tag2 (1) link is displayed.");
        assertEquals(calendarPage.getTagLink("tag3"), "tag3 (1)", "tag3 (1) link is displayed.");
    }
}