package org.alfresco.share.sitesFeatures.calendar;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;

/// **
// * Created by Gabriela Virna on 7/07/2016.
// */
//
public class AccessingCalendarTests extends ContextAwareWebTest
{
    @Autowired
    CalendarPage calendarPage;
    @Autowired
    CustomizeSitePage customizeSitePage;
    @Autowired
    SiteDashboardPage siteDashboardPage;

    private String user = "User1" + DataUtil.getUniqueIdentifier();
    private String siteName = "Site1" + DataUtil.getUniqueIdentifier();

    @BeforeClass
    public void setupTest()
    {
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.CALENDAR);
        userService.create(adminUser, adminPassword, user, DataUtil.PASSWORD, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.addPagesToSite(user, password, siteName, pagesToAdd);
        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C5437")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

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
        assertEquals(siteDashboardPage.getPageDisplayName(SitePageType.CALENDER), "newCalendar", "Calendar display page name is modified.");

        LOG.info("Step 4 - Click on 'newCalendar' link.");
        siteDashboardPage.clickLinkFromHeaderNavigationMenu(SitePageType.CALENDER);
        assertEquals(getBrowser().getTitle(), "Alfresco » newCalendar", "Calendar page is opened.");
    }

}
