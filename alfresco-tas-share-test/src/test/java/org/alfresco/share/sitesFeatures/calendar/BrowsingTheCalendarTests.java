package org.alfresco.share.sitesFeatures.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.share.site.calendar.AddEventDialogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.po.share.site.calendar.MiniCalendar;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.*;

@Slf4j
public class BrowsingTheCalendarTests extends BaseTest
{
    //@Autowired
     CalendarPage calendarPage;

    //@Autowired
    MiniCalendar miniCalendar;

    @Autowired
    CalendarUtility calendarUtility;

    @Autowired
    SitePagesService sitePagesService;

    AddEventDialogPage addEventDialogPage;

    DateTime today;
    DateTime yesterday;
    DateTime tomorrow;
    Date startDate;
    Calendar refferenceCalendar = Calendar.getInstance();
    Integer dayOfMonth = refferenceCalendar.get(Calendar.DAY_OF_MONTH);
    private String eventTitle = "testEvent";
    private String eventTitle2 = "testEvent2";
    private String eventTitle3 = "testEvent3";
    private String eventLocation = "Iasi C5805";
    private String eventDescription = "Event description C5805";
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
        calendarPage = new CalendarPage(webDriver);
        miniCalendar = new MiniCalendar(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteName.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C5805")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void changeTheMainCalendarView()
    {
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        authenticateUsingLoginPage(user.get());

        log.info("Precondition:  Add calendar event");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
            calendarUtility.firstDayOfCW(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, calendarUtility.firstDayOfCM(),
            calendarUtility.firstDayOfCM(), "", "", false, "tag1");

        log.info("Step 1: Navigate to the Calendar page for SiteService.");
        calendarPage.navigate(siteName.get().getId());
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Month");
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle));
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle3));

        log.info("Step 2: Click on Week tab.");
        calendarPage.clickWeekButton();
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        calendarPage.assertCalendarEventTitleEquals(eventTitle2);

        log.info("Step 3: Click on Day tab.");
        calendarPage.clickDayButton();
        calendarPage.assertCalendarEventTitleEquals(eventTitle);
        System.out.print("View displayed is: " + calendarPage.viewDisplayed());
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=day"));
    }

    @TestRail (id = "C5806")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES }, enabled = false)
    public void moveForwardThroughTheCalendar() {
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";
        String eventTitle5 = "testEvent5";
        String eventTitle6 = "testEvent6";
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        authenticateUsingLoginPage(user2.get());

        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, calendarUtility.tomorrow(),
            calendarUtility.tomorrow(), "", "", false, "tag2");
        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, calendarUtility.dayAfterTomorrow(),
            calendarUtility.dayAfterTomorrow(), "", "", false, "tag3");
        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle4, eventLocation, eventDescription, calendarUtility.dayOfNextWeek(),
            calendarUtility.dayOfNextWeek(), "", "", false, "tag4");
        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle5, eventLocation, eventDescription, calendarUtility.firstDayOfCM(),
            calendarUtility.firstDayOfCM(), "", "", false, "tag5");
        sitePagesService.addCalendarEvent(user2.get().getUsername(), user2.get().getPassword(), siteName.get().getId(), eventTitle6, eventLocation, eventDescription, calendarUtility.firstDayOfNextMonth(),
            calendarUtility.firstDayOfNextMonth(), "", "", false, "tag6");

        log.info("Step 1: Open Calendar page - Day view.");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));
        String dayRefferenceToday = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceToday, "Current day is displayed on the Calendar page.");

        log.info("Step 2: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        calendarPage.assertCalendarEventTitleEquals(eventTitle2);
        String dayRefferenceTomorrow = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(calendarUtility.tomorrow());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceTomorrow);

        log.info("Step 3: Switch to Week view.");
        calendarPage.clickWeekButton();
        if (tomorrow.dayOfWeek().getAsText(Locale.ENGLISH).equals("Sunday"))
        {
            calendarPage.assertCalendarEventTitleEquals(eventTitle);
            calendarPage.assertCalendarEventTitleEquals(eventTitle);
            calendarPage.assertCalendarEventTitleEquals(eventTitle);
        }else
        {
           Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle3));
        }
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        String firstDayOfCurrentWeek = (today.dayOfWeek().getAsText(Locale.ENGLISH).equals("Sunday"))
            ? new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek())
            : new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfCW());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfCurrentWeek);

        log.info("Step 4: Click on Next button (next to Agenda tab).");
        calendarPage.clickTodayButton();
        calendarPage.clickOnNextButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle4));
        String firstDayOfNextWeek = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfNextWeek);

        log.info("Step 5: Switch to Month view.");
        calendarPage.clickMonthButton();
        calendarPage.isEventPresentInCalendars(eventTitle5);

        if (calendarUtility.currentMonth() != calendarUtility.monthOfNextWeek())
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());
            calendarPage.assertCalendarEventTitleEquals(eventTitle1);
        } else
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
            calendarPage.assertCalendarEventTitleEquals(eventTitle5);
            Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar());
        }

        log.info("Step 6: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        if (calendarUtility.currentMonth() != calendarUtility.monthOfNextWeek())
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.monthAfterNextMonth());
        } else
        {
            Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());
            calendarPage.isEventPresentInCalendars(eventTitle4);
        }

        log.info("Step 7: Switch to Agenda view");
        calendarPage.clickAgendaButton();
        Assert.assertEquals(calendarPage.getNextButtonState(), "true");
    }

    @TestRail (id = "C5807")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void moveBackwardsThroughTheCalendar() {
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";
        String eventTitle5 = "testEvent5";
        String eventTitle6 = "testEvent6";

        String firstDayOfLastWeekView = today.minusWeeks(1).dayOfWeek().withMinimumValue().toString("d MMMM yyyy");
        String previousMonth = today.minusMonths(1).toString("MMMM yyyy");
        authenticateUsingLoginPage(user.get());

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, yesterday.toDate(), yesterday.toDate(), "",
            "", false, "tag2");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, tomorrow.toDate(), tomorrow.toDate(), "", "",
            false, "tag3");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle4, eventLocation, eventDescription, calendarUtility.randomDayOfLastWeek(),
            calendarUtility.randomDayOfLastWeek(), "", "", false, "tag4");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle5, eventLocation, eventDescription, calendarUtility.firstDayOfCurrentMonth(),
            calendarUtility.firstDayOfCurrentMonth(), "", "", false, "tag5");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle6, eventLocation, eventDescription, calendarUtility.dayFromPreviousMonth(),
            calendarUtility.dayFromPreviousMonth(), "", "", false, "tag6");

        log.info("Step 1: Open Calendar page - Day view.");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1), "Only testEvent1 is displayed on the Calendar.");
        String dayRefferenceToday = new SimpleDateFormat("EE, dd, MMMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Assert.assertEquals(calendarPage.getCalendarHeader(), dayRefferenceToday, "Current day is displayed on the Calendar page.");

        log.info("Step 2: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertEquals(calendarPage.getCalendarHeader(), yesterday.toString("EE, dd, MMMM yyyy"),
            "The day before current date is displayed on the Calendar page.");

        log.info("Step 3: Click on Today button. Switch to Week view.");
        calendarPage.clickTodayButton();
        calendarPage.clickWeekButton();
        Assert.assertEquals(calendarPage.getSelectedViewName(), "Week", "Current week is displayed on the Calendar page");
        Assert.assertTrue(calendarPage.viewDisplayed().contains("view=week"));
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle3), "testEvent3 is displayed on the Calendar.");
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar(), "Current date is highlighted.");

        log.info("Step 4: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfLastWeekView, "Previous week is displayed on the Calendar page.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle4), "testEvent4 is displayed on the Calendar .");

        log.info("Step 5: Click on Today button. Switch to Month view.");
        calendarPage.clickTodayButton();
        calendarPage.clickMonthButton();
      //  Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay(), "Current month is displayed on the Calendar page");
        Assert.assertTrue(calendarPage.isTodayHighlightedInCalendar(), "Current date is highlighted.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle5), "testEvent5 is displayed on the Calendar.");

        log.info("Step 6: Click on Previous button (near Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), previousMonth, "Previous month is displayed on the Calendar page.");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle6), "testEvent6 is displayed on the Calendar.");

        log.info("Step 7: Switch to Agenda view");
        calendarPage.clickAgendaButton();
        Assert.assertEquals(calendarPage.getNextButtonState(), "true", "Previous button is disabled.");
    }

    @TestRail (id = "C5809")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void displayUpcomingEvents() {
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        authenticateUsingLoginPage(user.get());

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, calendarUtility.tomorrow(),
            calendarUtility.tomorrow(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, calendarUtility.dayAfterTomorrow(),
            calendarUtility.dayAfterTomorrow(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, calendarUtility.dayOfNextWeek(),
            calendarUtility.dayOfNextWeek(), "", "", false, "tag1");
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();

        log.info("Navigate to Calendar page for Site - Agenda view.");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickAgendaButton();
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle1));
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInAgenda(eventTitle3));
    }

    @TestRail (id = "C5905")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void browserEventsByTags() {
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        Calendar dayAfterMidDate = calendarUtility.midDateOfTheMonth();
        dayAfterMidDate.add(Calendar.DAY_OF_MONTH, 1);
        Calendar dayBeforeMidDate = calendarUtility.midDateOfTheMonth();
        dayBeforeMidDate.add(Calendar.DAY_OF_MONTH, -1);
        authenticateUsingLoginPage(user.get());

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, calendarUtility.midDateOfTheMonth().getTime(),
            calendarUtility.midDateOfTheMonth().getTime(), "", "", false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, dayAfterMidDate.getTime(),
            dayAfterMidDate.getTime(), "", "", false, "tag2");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, dayBeforeMidDate.getTime(),
            dayBeforeMidDate.getTime(), "", "", false, "tag3");
        calendarPage.navigate(siteName.get().getId());

        log.info("Step 1: Click on tag1 on the Tags section.");
        calendarPage.clickTagLink("tag1");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle3));

        log.info("Step 2: Click on tag2 on the Tags section.");
        calendarPage.clickTagLink("tag2");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle1));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle3));

        log.info("Step 3: Click on tag3 on the Tags section.");
        calendarPage.clickTagLink("tag3");
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle3));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertFalse(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 4: Click on Show All Items link");
        Assert.assertTrue(calendarPage.isShowAllItemsLinkDisplayed());
        calendarPage.clickShowAllItems();

        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle2));
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle3));
    }

    @TestRail (id = "C5808")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void displayTodayEvents() {
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        String eventTitle1 = "testEvent1";
        authenticateUsingLoginPage(user.get());

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, startDate, startDate, "", "", false, "tag1");
        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickMonthButton();

        log.info("Step 1: Click on Previous button (before the Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferencePreviousMonth());

        log.info("Step 2: Click on Today button.");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 3: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.refferenceNextMonth());

        log.info("Step 4: Click on Today button");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentMonthReference());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 5: Switch the calendar to Week view.");
        calendarPage.clickWeekButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 6: Click on Previous button (before the Day tab)");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfLastWeek());

        log.info("Step 7: Click on Today button");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 8: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        String firstDayOfNextWeek = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(calendarUtility.firstDayOfNextWeek());
        Assert.assertEquals(calendarPage.getCalendarHeader(), firstDayOfNextWeek);

        log.info("Step 9: Click on Today button");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.firstDayOfCurrentWeek());
        calendarPage.assertCalendarEventTitleEquals(eventTitle1);

        log.info("Step 10: Switch to Day view.");
        calendarPage.clickDayButton();
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 11: Click on Previous button (before the Day tab).");
        calendarPage.clickOnPreviousButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), yesterday.toString("E, dd, MMMM yyyy"));

        log.info("Step 12: Click on Today button");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 13: Click on Next button (next to Agenda tab).");
        calendarPage.clickOnNextButton();
        String tomorrow = new SimpleDateFormat("E, dd, MMMM yyyy", Locale.ENGLISH).format(calendarUtility.tomorrow());
        Assert.assertEquals(calendarPage.getCalendarHeader(), tomorrow);

        log.info("Step 14: Click on Today button");
        calendarPage.clickTodayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
        Assert.assertTrue(calendarPage.isEventPresentInCalendars(eventTitle1));

        log.info("Step 15: Switch to Agenda view.");
        calendarPage.clickAgendaButton();
        Assert.assertEquals(calendarPage.getTodayButtonState(), "true");
    }

    @TestRail (id = "C3155")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void verifyTheMiniCalendar()
    {
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        authenticateUsingLoginPage(user.get());
        log.info("Step 1: Verify the presence of the mini-calendar.");

        calendarPage.navigate(siteName.get().getId());
        Integer dayFromCalendar = Integer.valueOf(miniCalendar.getCurrentDayMiniCalendar().trim());
        Assert.assertEquals(dayFromCalendar, dayOfMonth, "Current date is highlighted.");
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendars(), calendarUtility.currentMonthReference(), "Current month is displayed");

        log.info("Step 2: Press '<' button on the calendar.");

        miniCalendar.clickOnPreviousMonthButtonMiniCalendars();
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendars(), calendarUtility.refferencePreviousMonth(),
            "Previous month is displayed on the mini-calendar.");

        log.info("Step 3: Click on any day from the mini-calendar.");
        miniCalendar.clickOnRandomDate();
        Assert.assertEquals(calendarPage.getCalendarHeader(), miniCalendar.getCurrentMonthMiniCalendars());

        log.info("Step 4: Click 'This Month' button.");
        miniCalendar.clickOnThisMonthButtons();
        Assert.assertEquals(dayFromCalendar, dayOfMonth);
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendars(), calendarUtility.currentMonthReference());

        log.info("Step 5: Press '>' button in the calendar.");
        miniCalendar.clickOnNextMonthButtonInMiniCalendars();
        Assert.assertEquals(miniCalendar.getCurrentMonthMiniCalendars(), calendarUtility.refferenceNextMonth());

        log.info("Step 6: Click on any date from the calendar.");
        miniCalendar.clickOnRandomDate();
        Assert.assertEquals(calendarPage.getCalendarHeader(), miniCalendar.getCurrentMonthMiniCalendars());
    }

    @TestRail (id = "C5833")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES })
    public void showAllHours() {
        today = new DateTime();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        startDate = today.toDate();
        String eventTitle1 = "testEvent1";
        String eventTitle2 = "testEvent2";
        String eventTitle3 = "testEvent3";
        String eventTitle4 = "testEvent4";
        authenticateUsingLoginPage(user.get());

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle1, eventLocation, eventDescription, startDate, startDate, "20:00", "23:00",
            false, "tag1");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle2, eventLocation, eventDescription, startDate, startDate, "10:00", "12:00",
            false, "tag2");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle3, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
            calendarUtility.firstDayOfCW(), "20:00", "23:00", false, "tag3");
        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), siteName.get().getId(), eventTitle4, eventLocation, eventDescription, calendarUtility.firstDayOfCW(),
            calendarUtility.firstDayOfCW(), "10:00", "12:00", false, "tag4");

        calendarPage.navigate(siteName.get().getId());
        calendarPage.clickDayButton();
        Assert.assertEquals(calendarPage.getCalendarHeader(), calendarUtility.currentDay());
    }
}
