package org.alfresco.share.userDashboard.dashlets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.*;

public class MyCalendarDashletTests extends AbstractUserDashboardDashletsTests
{
    private MyCalendarDashlet myCalendarDashlet;

    @Autowired
    private SitePagesService sitePagesService;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        myCalendarDashlet = new MyCalendarDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        addDashlet(user.get(), DashboardCustomization.UserDashlet.MY_CALENDAR, 1, 3);
        authenticateUsingCookies(user.get());
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD })
    public void checkMyCalendarDashletWithNoEvents()
    {
        userDashboardPage.navigate(user.get());
        myCalendarDashlet.assertDashletTitleEquals(language.translate("myCalendarDashlet.title"))
            .assertNoUpcomingEventsIsDisplayed()
            .clickOnHelpIcon(DashletHelpIcon.MY_CALENDAR)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myCalendarDashlet.HelpBalloonText"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @Test (groups = { TestGroup.REGRESSION, TestGroup.USER_DASHBOARD }, priority = 2)
    public void checkEventsAreDisplayed()
    {
        SiteModel site = getDataSite().usingUser(user.get()).createPublicRandomSite();

        Date startDate = CalendarUtility.tomorrow();
        Date endDate = CalendarUtility.tomorrow();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
        String eventTitle = "test event";
        String eventLocation = "location";
        String eventDescription = "description";

        sitePagesService.addCalendarEvent(user.get().getUsername(), user.get().getPassword(), site.getId(),
            eventTitle, eventLocation, eventDescription, startDate, endDate,
            "", "", false, "tag1");

        userDashboardPage.navigate(user.get());
        myCalendarDashlet.assertEventIsDisplayed(eventTitle)
            .assertEventTimeIs(eventTitle, formatter.format(startDate) + " 12:00 PM - 1:00 PM")
            .selectEvent(eventTitle)
                .assertCalendarEventTitleEquals(eventTitle);

        userDashboardPage.navigate(user.get());
        myCalendarDashlet.selectSiteFromEvent(eventTitle)
            .assertSiteDashboardPageIsOpened()
            .assertSiteHeaderTitleIs(site);
    }

    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }
}