package org.alfresco.share.sitesFeatures.calendar;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EditEventDialog;
import org.alfresco.po.share.site.calendar.EventInformationDialog;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Claudia Agache on 7/20/2016.
 */
public class EditEventsTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;

    @Autowired
    EventInformationDialog eventInformationDialog;

    @Autowired
    EditEventDialog editEventDialog;

    private String user = String.format("user%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName%s", RandomData.getRandomAlphanumeric());
    private DateTime startDate = new DateTime();
    private DateTime endDate = startDate.plusDays(3);
    private DateTime tomorrow = startDate.plusDays(1);
    private DateTime yesterday = startDate.minusDays(1);
    private String eventTitle = "testEvent";
    private String eventLocation = "Iasi";
    private String eventDescription = "Event description";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, user, user);
        siteService.create(user, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addPageToSite(user, password, siteName, Page.CALENDAR, null);
        setupAuthenticatedSession(user, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user);
        siteService.delete(adminUser, adminPassword, siteName);
    }


    private String transformExpectedDate(int day, int month, int year, String hour)
    {
        return new DateTime(year, month, day, 0, 0).toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    @TestRail (id = "C3168")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventMonthView()
    {
        //precondition
        String eventName = eventTitle + "C3168";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName + "Edited"), "The calendar displays the updated event.");

        LOG.info("STEP 6: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName + "Edited");
        assertEquals(eventInformationDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(eventInformationDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5581")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWeekView()
    {
        //precondition
        String eventName = eventTitle + "C5581";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickWeekButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName + "Edited"), "The calendar displays the updated event.");

        LOG.info("STEP 6: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName + "Edited");
        assertEquals(eventInformationDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(eventInformationDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5583")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventDayView()
    {
        //precondition
        String eventName = eventTitle + "C5583";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName + "Edited"), "The calendar displays the updated event.");

        LOG.info("STEP 6: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName + "Edited");
        assertEquals(eventInformationDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(eventInformationDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5580")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventByClickingOnTheEventAgendaView()
    {
        //precondition
        String eventName = eventTitle + "C5580";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName + "Edited"), "The calendar displays the updated event.");

        LOG.info("STEP 6: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName + "Edited");
        assertEquals(eventInformationDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(eventInformationDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }


    @TestRail (id = "C6073")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventByClickingEditIconAgendaView()
    {
        //precondition
        String eventName = eventTitle + "C6073";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("STEP 1: Click 'Edit' icon.");
        calendarPage.clickEditIcon(eventName);
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 2: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 3: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 4: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName + "Edited"), "The calendar displays the updated event.");

        LOG.info("STEP 5: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName + "Edited");
        assertEquals(eventInformationDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(eventInformationDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }


    @TestRail (id = "C3173")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEventMonthView()
    {
        //precondition
        String eventName = eventTitle + "C3173";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5715")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEventWeekView()
    {
        //precondition
        String eventName = eventTitle + "C5715";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickWeekButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5716")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventDayView()
    {
        //precondition
        String eventName = eventTitle + "C5716";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5717")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventByClickingOnEventAgendaView()
    {
        //precondition
        String eventName = eventTitle + "C5715";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C6076")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventByClickingEditIconAgendaView()
    {
        //precondition
        String eventName = eventTitle + "C6076";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("STEP 1: Click 'Edit' icon.");
        calendarPage.clickEditIcon(eventName);
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 2: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 3: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 4: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 5: Click on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5401")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingMonthView()
    {
        //precondition
        String eventName = eventTitle + "C5401";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5718")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingWeekView()
    {
        //precondition
        String eventName = eventTitle + "C5718";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickWeekButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5719")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingDayView()
    {
        //precondition
        String eventName = eventTitle + "C5719";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickDayButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEvent(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendar(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEvent(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5720")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingAgendaView()
    {
        //precondition
        String eventName = eventTitle + "C5720";
        sitePagesService.addCalendarEvent(user, password, siteName, eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName);
        calendarPage.clickAgendaButton();

        LOG.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(eventInformationDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        LOG.info("STEP 2: Click 'Edit' button.");
        eventInformationDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInList("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInList("tag3"), "Tags section contains tag3.");


        LOG.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        LOG.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInList("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInList("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInList("t3"), "t3 tag is added for the event.");

        LOG.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        LOG.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        eventInformationDialog.clickCancelButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The calendar displays the old event name.");

        LOG.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(eventInformationDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(eventInformationDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(eventInformationDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(eventInformationDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(eventInformationDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(eventInformationDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }
}