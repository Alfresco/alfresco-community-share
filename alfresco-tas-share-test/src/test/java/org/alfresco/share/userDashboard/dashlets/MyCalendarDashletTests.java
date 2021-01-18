package org.alfresco.share.userDashboard.dashlets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MyCalendarDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MyCalendarDashlet myCalendarDashlet;

    @Autowired
    private CalendarUtility calendarUtility;

    private UserModel user;
    private SiteModel site;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        site = dataSite.usingUser(user).createPublicRandomSite();
        setupAuthenticatedSession(user);
        addDashlet(Dashlets.MY_CALENDAR, 1);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
        dataSite.usingAdmin().deleteSite(site);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD }, priority = 1)
    public void checkMyCalendarDashletWithNoEvents()
    {
        userDashboard.navigate(user);
        myCalendarDashlet.assertDashletTitleEquals(language.translate("myCalendarDashlet.title"))
            .assertNoUpcomingEventsIsDisplayed()
            .clickOnHelpIcon(Dashlet.DashletHelpIcon.MY_CALENDAR)
            .assertBalloonMessageIsDisplayed()
            .assertHelpBalloonMessageEquals(language.translate("myCalendarDashlet.HelpBalloonText"))
            .closeHelpBalloon()
            .assertBalloonMessageIsNotDisplayed();
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD }, priority = 2)
    public void checkEventsAreDisplayed()
    {
        Date startDate = calendarUtility.tomorrow();
        Date endDate = calendarUtility.tomorrow();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
        String eventTitle = "test event";
        String eventLocation = "location";
        String eventDescription = "description";
        sitePagesService.addCalendarEvent(user.getUsername(), user.getPassword(), site.getId(),
            eventTitle, eventLocation, eventDescription, startDate, endDate,
            "", "", false, "tag1");

        userDashboard.navigate(user);
        myCalendarDashlet.assertEventIsDisplayed(eventTitle)
            .assertEventTimeIs(eventTitle, formatter.format(startDate) + " 12:00 PM - 1:00 PM")
            .selectEvent(eventTitle);
//                .assertCalendarPageIsOpened();

        userDashboard.navigate(user);
        myCalendarDashlet.selectSiteFromEvent(eventTitle);
//            .assertSiteDashboardPageIsOpened()
//            .assertSiteHeaderTitleIs(site);
    }
}