package org.alfresco.share.sitesFeatures.calendar;

import static org.alfresco.utility.web.AbstractWebTest.getBrowser;
import static org.testng.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.searching.SearchPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.share.BaseTest;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

/// **
// * Created by Gabriela Virna on 7/07/2016.
// */
//
@Slf4j
public class AccessingCalendarTests extends BaseTest
{
    //@Autowired
    CalendarPage calendarPage;
    //@Autowired
    CustomizeSitePage customizeSitePage;
    //@Autowired
    SiteDashboardPage siteDashboardPage;
    //@Autowired
    Toolbar toolbar;

    //@Autowired
    SearchPage searchPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<UserModel> user2 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a TestUser2");
        user2.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        siteName.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        toolbar = new Toolbar(webDriver);
        searchPage = new SearchPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        calendarPage = new CalendarPage(webDriver);
        customizeSitePage = new CustomizeSitePage(webDriver);
    }


    @AfterMethod (alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteUsersIfNotNull(user2.get());
        deleteSitesIfNotNull(siteName.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C5437")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES_FEATURES, "tobefixed" })

    public void accessTheCalendarPage()
    {
        log.info("Step 1 - Open Site1's dashboard and click on 'Calendar' link.");
        authenticateUsingLoginPage(user1.get());
        calendarPage.navigate(siteName.get().getId());
        DateFormat df = new SimpleDateFormat("MMMM yyyy");
        assertEquals(calendarPage.getCalendarHeader(), df.format(new Date()), "'Calendar' page, which defaults to the Month view, is opened.");

        log.info("Step 2 - Open 'Customize Site' page for " + siteName + " and rename 'Calendar' page to 'newCalendar'.");
        customizeSitePage.navigate(siteName.get());
        customizeSitePage.addPageToSite(SitePageType.CALENDER);
        customizeSitePage.renameSitePage(SitePageType.CALENDER, "newCalendar");
        assertEquals(customizeSitePage.getPageDisplayName(SitePageType.CALENDER), "newCalendar", "Calendar display page name is modified.");

        log.info("Step 3 - Click OK to save changes");
        customizeSitePage.saveChanges();
        siteDashboardPage.navigate(siteName.get());
        assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.CALENDER), "newCalendar", "Calendar display page name is modified.");

        log.info("Step 4 - Click on 'newCalendar' link.");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.CALENDER);
        assertEquals(calendarPage.getCalendarHeader(), df.format(new Date()), "'Calendar' page, which defaults to the Month view, is opened.");
    }

}
