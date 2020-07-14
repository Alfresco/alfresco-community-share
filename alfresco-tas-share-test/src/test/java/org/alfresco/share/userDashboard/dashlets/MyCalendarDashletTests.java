package org.alfresco.share.userDashboard.dashlets;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.MyCalendarDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.CalendarUtility;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Mirela Tifui on 11/24/2017.
 */
public class MyCalendarDashletTests extends ContextAwareWebTest
{
    @Autowired
    MyCalendarDashlet myCalendarDashlet;
    @Autowired
    UserDashboardPage userDashboardPage;
    @Autowired
    CalendarUtility calendarUtility;
    @Autowired
    CalendarPage calendarPage;
    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String userName = "MCDUser" + RandomData.getRandomAlphanumeric();
    private String siteName = "MCDSite" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, domain, userName, userName);
        userService.addDashlet(userName, password, DashboardCustomization.UserDashlet.MY_CALENDAR, DashboardCustomization.DashletLayout.THREE_COLUMNS, 1, 3);
        siteService.create(userName, password, domain, siteName, "MCD Site", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        siteService.delete(adminUser, adminPassword, domain, siteName);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD, "Acceptance" })
    public void checkEmptyMyCalendarDashlet()
    {
        LOG.info("Step 1: Navigate to user dahsboard page and check that the dashlet is displayed and is empty.");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isDashletAddedInPosition(Dashlets.MY_CALENDAR, 1, 3), "My Calendar dashlet is not displayed");
        Assert.assertEquals(myCalendarDashlet.getDashletTitle(), "My Calendar", "Dashlet title is not correct");
        Assert.assertEquals(myCalendarDashlet.getEmptyDashletText(), language.translate("myCalendarDashlet.EmptyDashletText"),
            "Dashlet is not empty or text is not correct");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD, "Acceptance" })
    public void checkHelpBalloon()
    {
        LOG.info("Step 1: Navigate to user dashboard and check that the help icon is displayed.");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(myCalendarDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.MY_CALENDAR), "The help icon is not available.");
        LOG.info("Step 2: Click on the Help icon and check that the help balloon is displayed, also check balloon message text.");
        myCalendarDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.MY_CALENDAR);
        Assert.assertTrue(myCalendarDashlet.isBalloonDisplayed(), "Help balloon has not been opened.");
        Assert.assertEquals(myCalendarDashlet.getHelpBalloonMessage(), language.translate("myCalendarDashlet.HelpBalloonText"),
            "Help balloon text is not as expected");
        LOG.info("Step 3: Close the balloon message and confirm that balloon is no longer displayed.");
        myCalendarDashlet.closeHelpBalloon();
        Assert.assertFalse(myCalendarDashlet.isBalloonDisplayed(), "Help balloon has not been closed.");
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD, "Acceptance" })
    public void checkEventsAreDisplayed()
    {
        //Precondition add event
        Date startDate = calendarUtility.tomorrow();
        Date endDate = calendarUtility.tomorrow();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy");
        String eventTitle = "test event";
        String eventLocation = "location";
        String eventDescription = "description";
        sitePagesService.addCalendarEvent(userName, password, siteName, eventTitle, eventLocation, eventDescription, startDate, endDate, "", "", false, "tag1");

        LOG.info("Step 1: Navigate to user dashboard and check that event is present on My Calendar dashlet.");
        userDashboardPage.navigate(siteName);
        Assert.assertTrue(myCalendarDashlet.isEventPresentInList(eventTitle), eventTitle + " is not displayed.");
        Assert.assertEquals(myCalendarDashlet.getEventDetails(eventTitle), formatter.format(startDate).toString() + " 12:00 PM - 1:00 PM");

        LOG.info("Step 2: Click test event link and check that you are redirected to the Calendar page");
        myCalendarDashlet.clickOnEvent(eventTitle, calendarPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Calendar", "User is not redirected to the calendar page");

        LOG.info("Step 3: Click on the siteName link and check that you are redirected to the site dashboard page");
        userDashboardPage.navigate(userName);
        myCalendarDashlet.clickOnSiteName(siteName, siteDashboardPage);
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » Site Dashboard", "User is not redirected to the site dashboard page.");
        Assert.assertEquals(siteDashboardPage.getSiteName(), siteName, "User is not redirected to the correct site");
    }
}