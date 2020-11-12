package org.alfresco.share.sitesFeatures.calendar;

import static org.testng.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/// **
// * Created by Gabriela Virna on 7/07/2016.
// */
//
public class AccessingCalendarTests extends ContextAwareWebTest
{
    //@Autowired
    CalendarPage calendarPage;
    //@Autowired
    CustomizeSitePage customizeSitePage;
    //@Autowired
    SiteDashboardPage siteDashboardPage;

    private String user = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
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

    @TestRail (id = "C5437")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })

    public void accessTheCalendarPage()
    {
        LOG.info("Step 1 - Open Site1's dashboard and click on 'Calendar' link.");
        calendarPage.navigate(siteName);
        DateFormat df = new SimpleDateFormat("MMMM yyyy");
        assertEquals(calendarPage.getCalendarHeader(), df.format(new Date()), "'Calendar' page, which defaults to the Month view, is opened.");

        LOG.info("Step 2 - Open 'Customize Site' page for " + siteName + " and rename 'Calendar' page to 'newCalendar'.");
        customizeSitePage.navigate(siteName);
        customizeSitePage.renamePage(SitePageType.CALENDER, "newCalendar");
        assertEquals(customizeSitePage.getPageDisplayName(SitePageType.CALENDER), "newCalendar", "Calendar display page name is modified.");

        LOG.info("Step 3 - Click OK to save changes");
        customizeSitePage.clickOk();
        siteDashboardPage.navigate(siteName);
        assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.CALENDER), "newCalendar", "Calendar display page name is modified.");

        LOG.info("Step 4 - Click on 'newCalendar' link.");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.CALENDER);
        assertEquals(getBrowser().getTitle(), "Alfresco » newCalendar", "Calendar page is opened.");
    }

}
