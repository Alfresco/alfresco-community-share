package org.alfresco.share.site.siteDashboard;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.alfresco.dataprep.DashboardCustomization.SiteDashlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteCalendarDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

public class SiteCalendarDashletTests extends AbstractSiteDashboardDashletsTests
{
    @Autowired
    private SitePagesService sitePagesService;

    private static final String EXPECTED_DASHLET_TITLE = "siteCalendarDashlet.title";
    private static final String EXPECTED_HELP_BALLOON_MESSAGE = "siteCalendarDashlet.helpBalloonMessage";
    private static final String EXPECTED_NO_EVENTS_MESSAGE = "siteCalendarDashlet.noEvents.message";
    private static final String FIRST_EVENT_TITLE = "First event title ".concat(randomAlphabetic(3));
    private static final String FIRST_EVENT_LOCATION = "First event location ".concat(randomAlphabetic(3));
    private static final String FIRST_EVENT_DESCRIPTION = "First event description ".concat(randomAlphabetic(3));
    private static final String SECOND_EVENT_TITLE = "Second event title ".concat(randomAlphabetic(3));
    private static final String SECOND_EVENT_LOCATION = "Second event location ".concat(randomAlphabetic(3));
    private static final String SECOND_EVENT_DESCRIPTION = "Second event description ".concat(randomAlphabetic(3));
    private static final String TAG = "Tag ".concat(randomAlphabetic(3));
    private static final String DASH = " - ";
    private static final String START_TIME_UNDEFINED = "";
    private static final String END_TIME_UNDEFINED = "";
    private static final String UNDEFINED_TAG = "";
    private static final int ONE_ALL_DAY_EVENT = 1;

    private final DateTime today = new DateTime();
    private final DateTime tomorrow = today.plusDays(1);

    private SiteCalendarDashlet siteCalendarDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        siteCalendarDashlet = new SiteCalendarDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        addDashlet(user.get(), site.get(), SiteDashlet.SITE_CALENDAR, 1, 2);

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C5492")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void checkDisplaySpecificMessageWhenNoUpcomingEvents()
    {
        siteDashboardPage.navigate(site.get());
        siteCalendarDashlet
            .assertDashletTitleEquals(language.translate(EXPECTED_DASHLET_TITLE))
            .assertNoUpcomingEventsMessageEquals(language.translate(EXPECTED_NO_EVENTS_MESSAGE))
            .clickOnHelpIcon(DashletHelpIcon.SITE_CALENDAR)
            .assertHelpBalloonMessageEquals(language.translate(EXPECTED_HELP_BALLOON_MESSAGE))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @TestRail (id = "C5499")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayEventWithPredefinedTimeInterval()
    {
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now().plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US);

        String startTimeHour = LocalDateTime.now().format(formatter);
        String endTimeHour = LocalDateTime.now().plusHours(2).format(formatter);

        sitePagesService.addCalendarEvent(
            user.get().getUsername(), user.get().getPassword(),
            site.get().getId(),
            FIRST_EVENT_TITLE, FIRST_EVENT_LOCATION,
            FIRST_EVENT_DESCRIPTION,
            startDate.toDate(), endDate.toDate(),
            startTimeHour.concat(DASH), endTimeHour, false, TAG);

        siteDashboardPage.navigate(site.get());
        siteCalendarDashlet
            .assertEventListTitleEquals(FIRST_EVENT_TITLE)
            .assertEventStartDateEquals(FIRST_EVENT_TITLE, today.toString("EEEE, d MMMM, yyyy"))
            .assertEventTimeEquals(startTimeHour.concat(DASH).concat(endTimeHour), FIRST_EVENT_TITLE)
            .clickEvent(FIRST_EVENT_TITLE)
                .assertCalendarHeaderEquals(today.toString("EE, dd, MMMM yyyy"))
                .assertCalendarEventTitleEquals(FIRST_EVENT_TITLE);
    }

    @TestRail (id = "C588502")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.SITE_DASHBOARD })
    public void shouldDisplayAllDayEventInCalendar()
    {
        sitePagesService
            .addCalendarEvent(user.get().getUsername(),
                    user.get().getPassword(), site.get().getId(),
                SECOND_EVENT_TITLE, SECOND_EVENT_LOCATION,
                SECOND_EVENT_DESCRIPTION,
                tomorrow.toDate(),
                tomorrow.plusHours(1).toDate(),
                START_TIME_UNDEFINED, END_TIME_UNDEFINED,
                true, UNDEFINED_TAG);

        siteDashboardPage.navigate(site.get());
        siteCalendarDashlet
            .assertEventListTitleEquals(SECOND_EVENT_TITLE)
            .assertEventStartDateEquals(SECOND_EVENT_TITLE, tomorrow.toString("EEEE, d MMMM, yyyy"))
            .clickEvent(SECOND_EVENT_TITLE)
                .assertCalendarHeaderEquals(tomorrow.toString("EE, dd, MMMM yyyy"))
                .assertCalendarEventTitleEquals(SECOND_EVENT_TITLE)
                .assertAllDayEventListSizeEquals(ONE_ALL_DAY_EVENT);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
       deleteUsersIfNotNull(user.get());
       deleteSitesIfNotNull(site.get());
    }
}
