package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.EditEventDialog;
import org.alfresco.po.share.site.calendar.EventInformationDialog;
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
public class EditEventsTests extends BaseTest
{
    CalendarPage calendarPage;

    @Autowired
    EventInformationDialog eventInformationDialog;

    EditEventDialog editEventDialog;

    @Autowired
    SitePagesService sitePagesService;
    private DateTime startDate = new DateTime();
    private DateTime endDate = startDate.plusDays(3);
    private DateTime tomorrow = startDate.plusDays(1);
    private DateTime yesterday = startDate.minusDays(1);
    private String eventLocation = "Iasi";
    private String eventDescription = "Event description";

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();
    private String eventTitle = "testEvent";

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


    private String transformExpectedDate(int day, int month, int year, String hour)
    {
        return new DateTime(year, month, day, 0, 0).toString("EEEE, d MMMM, yyyy") + " at " + hour;
    }

    @TestRail (id = "C3168")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventMonthView(){
        String eventName = eventTitle + "C3168";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName + "Edited"), "The calendar displays the updated event.");

        log.info("STEP 6: Click on the created event's name link.");
        calendarPage.click_Event(eventName + "Edited");
        assertEquals(editEventDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(editEventDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5581")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWeekView(){

        String eventName = eventTitle + "C5581";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickWeekButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName + "Edited"), "The calendar displays the updated event.");

        log.info("STEP 6: Click on the created event's name link.");
        calendarPage.click_Event(eventName + "Edited");
        assertEquals(editEventDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(editEventDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5583")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventDayView(){

        String eventName = eventTitle + "C5583";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName + "Edited"), "The calendar displays the updated event.");

        log.info("STEP 6: Click on the created event's name link.");
        calendarPage.click_Event(eventName + "Edited");
        assertEquals(editEventDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(editEventDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5580")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventByClickingOnTheEventAgendaView(){
        String eventName = eventTitle + "C5580";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);

        assertTrue(editEventDialog.isAllDayCheckBoxDisplayedAndUnchecked(), "'All Day' check box is unchecked.");
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");

        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");
        assertTrue(editEventDialog.isTagsSectionDisplayed(), "Tags section is displayed.");

        assertTrue(editEventDialog.isSaveButtonEnabled(), "Save button is available on the form.");
        assertTrue(editEventDialog.isCancelButtonEnabled(), "Cancel button is available on the form.");
        assertTrue(editEventDialog.isCloseButtonDisplayed(), "Close button is available on the form.");

        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName + "Edited"), "The calendar displays the updated event.");

        log.info("STEP 6: Click on the created event's name link.");
        calendarPage.clickAgendaButton();
        calendarPage.clickOnEventInAgenda(eventName + "Edited");
        assertEquals(editEventDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(editEventDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }


    @TestRail (id = "C6073")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventByClickingEditIconAgendaView(){
        String eventName = eventTitle + "C6073";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();

        log.info("STEP 1: Click 'Edit' icon.");
        calendarPage.clickEditIcon(eventName);
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 2: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 3: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 4: Click 'Save' button.");
        editEventDialog.clickSaveButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName + "Edited"), "The calendar displays the updated event.");

        log.info("STEP 5: Click on the created event's name link.");
        calendarPage.clickAgendaButton();
        calendarPage.clickOnEventInAgenda(eventName + "Edited");
        assertEquals(editEventDialog.getWhatDetails(), eventName + "Edited", "Following information is available: What: " + eventName + "Edited");
        assertEquals(editEventDialog.getWhereDetails(), eventLocation + "Edited", "Following information is available: Where: " + eventLocation + "Edited");
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription + "Edited", "Following information is available: Description: " + eventDescription + "Edited");
        assertEquals(editEventDialog.getTagsDetails(), "t1, t2, t3", "Following information is available: Tags: t1, t2, t3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear(), "2:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear(), "4:00 PM"),
            "Following information is available: Time section with End Date");
    }


    @TestRail (id = "C3173")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEventMonthView(){
        String eventName = eventTitle + "C3173";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5715")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEventWeekView(){
        String eventName = eventTitle + "C5715";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickWeekButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5716")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventDayView(){
        String eventName = eventTitle + "C5716";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5717")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventByClickingOnEventAgendaView(){
        String eventName = eventTitle + "C5715";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C6076")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void cancelEditEventByClickingEditIconAgendaView(){
        String eventName = eventTitle + "C6076";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();

        log.info("STEP 1: Click 'Edit' icon.");
        calendarPage.clickEditIcon(eventName);
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 2: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 3: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 4: Click 'Cancel' button.");
        editEventDialog.clickCancelButton();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 5: Click on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5401")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingMonthView(){
        String eventName = eventTitle + "C5401";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5718")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingWeekView(){
        String eventName = eventTitle + "C5718";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickWeekButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5719")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingDayView(){
        String eventName = eventTitle + "C5719";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.click_Event(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInCalendars(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.click_Event(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }

    @TestRail (id = "C5720")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void editEventWithoutSavingAgendaView(){
        String eventName = eventTitle + "C5720";
        authenticateUsingLoginPage(user.get());
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventName, eventLocation, eventDescription, startDate.toDate(), endDate.toDate(), "", "", false, "tag1, tag2, tag3");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();

        log.info("STEP 1: Click on the created event's name link.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertTrue(editEventDialog.isEventInformationPanelDisplayed(), "'Event Information' dialog box for '" + eventName + "' is opened.");

        log.info("STEP 2: Click 'Edit' button.");
        editEventDialog.clickEditButton();
        assertTrue(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is opened.");
        assertEquals(editEventDialog.getEventTitle(), eventName, "What field value is: " + eventName);
        assertEquals(editEventDialog.getEventLocation(), eventLocation, "Where field value is: " + eventLocation);
        assertEquals(editEventDialog.getEventDescription(), eventDescription, "Description field value is: " + eventDescription);
        assertTrue(editEventDialog.hasStartDateValue(startDate), "Start date is: " + startDate.toString());
        assertTrue(editEventDialog.hasEndDateValue(endDate), "End date is: " + endDate.toString());
        assertTrue(editEventDialog.hasStartTimeValue("12:00 PM"), "Start time is: 12:00 PM");
        assertTrue(editEventDialog.hasEndTimeValue("1:00 PM"), "End time is: 1:00 PM");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag1"), "Tags section contains tag1.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag2"), "Tags section contains tag2.");
        assertTrue(editEventDialog.isTagDisplayedInLists("tag3"), "Tags section contains tag3.");


        log.info("STEP 3: Change the details and time for the event.");
        editEventDialog.typeInEventTitleInput(eventName + "Edited");
        editEventDialog.typeInEventLocationInput(eventLocation + "Edited");
        editEventDialog.typeInEventDescriptionInput(eventDescription + "Edited");

        editEventDialog.selectStartDateFromCalendarPicker(yesterday.getDayOfMonth(), yesterday.getMonthOfYear(), yesterday.getYear());
        editEventDialog.typeInStartTimeInput("2:00 PM");
        editEventDialog.selectEndDateFromCalendarPicker(tomorrow.getDayOfMonth(), tomorrow.getMonthOfYear(), tomorrow.getYear());
        editEventDialog.typeInEndTimeInput("4:00 PM");

        log.info("STEP 4: Remove 'tag1', 'tag2', 'tag3' tags and add new ones: 't1', 't2', 't3'.");
        editEventDialog.removeTag("tag1");
        editEventDialog.removeTag("tag2");
        editEventDialog.removeTag("tag3");
        editEventDialog.addTag("t1");
        editEventDialog.addTag("t2");
        editEventDialog.addTag("t3");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag1"), "tag1 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag2"), "tag2 tag is removed for the event.");
        assertFalse(editEventDialog.isTagDisplayedInLists("tag3"), "tag3 tag is removed for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t1"), "t1 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t2"), "t2 tag is added for the event.");
        assertTrue(editEventDialog.isTagDisplayedInLists("t3"), "t3 tag is added for the event.");

        log.info("STEP 5: Click 'X' button.");
        editEventDialog.clickClose();
        assertFalse(editEventDialog.isDialogDisplayed(), "'Edit Event' dialog box is closed.");

        log.info("STEP 6: Click 'Close' button on 'Event Information' dialog box.");
        editEventDialog.clickOnCancelButton();
        assertTrue(calendarPage.isEventPresentInAgenda(eventName), "The calendar displays the old event name.");

        log.info("STEP 7: Click again on the created event's name link. Check event details are not changed.");
        calendarPage.clickOnEventInAgenda(eventName);
        assertEquals(editEventDialog.getWhatDetails(), eventName, "Following information is available: What: " + eventName);
        assertEquals(editEventDialog.getWhereDetails(), eventLocation, "Following information is available: Where: " + eventLocation);
        assertEquals(editEventDialog.getDescriptionDetails(), eventDescription, "Following information is available: Description: " + eventDescription);
        assertEquals(editEventDialog.getTagsDetails(), "tag1, tag2, tag3", "Following information is available: Tags: tag1, tag2, tag3");
        assertEquals(editEventDialog.getStartDateTime(),
            transformExpectedDate(startDate.getDayOfMonth(), startDate.getMonthOfYear(), startDate.getYear(), "12:00 PM"),
            "Following information is available: Time section with Start Date");
        assertEquals(editEventDialog.getEndDateTime(),
            transformExpectedDate(endDate.getDayOfMonth(), endDate.getMonthOfYear(), endDate.getYear(), "1:00 PM"),
            "Following information is available: Time section with End Date");
    }
}