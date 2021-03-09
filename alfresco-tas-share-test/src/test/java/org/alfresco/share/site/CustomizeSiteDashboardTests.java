package org.alfresco.share.site;

import static org.alfresco.po.share.dashlet.Dashlets.*;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.enums.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CustomizeSiteDashboardTests extends BaseTest
{
    private CustomizeSiteDashboardPage customizeSiteDashboardPage;
    private SiteDashboardPage siteDashboardPage;
    private SiteContentDashlet siteContentDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        customizeSiteDashboardPage = new CustomizeSiteDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        siteContentDashlet = new SiteContentDashlet(webDriver);

        user.set(getDataUser().usingAdmin().createRandomTestUser());
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2198")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void changeSiteDashboardLayout()
    {
        customizeSiteDashboardPage.navigate(site.get())
            .assertCurrentLayoutIs(Layout.TWO_COLUMNS_WIDE_RIGHT)
            .clickChangeLayout()
                .assertOneColumnLayoutIsDisplayed()
                .assertTwoColumnsLayoutWideLeftIsDisplayed()
                .assertThreeColumnsLayoutIsDisplayed()
                .assertFourColumnsLayoutIsDisplayed()
            .selectLayout(Layout.THREE_COLUMNS)
                .assertChangeLayoutButtonIsDisplayed()
                .clickChangeLayout()
                    .assertOneColumnLayoutIsDisplayed()
                    .assertTwoColumnsLayoutWideLeftIsDisplayed()
                    .assertTwoColumnsLayoutWideRightIsDisplayed()
                    .assertFourColumnsLayoutIsDisplayed()
                .clickCancelNewLayout()
                .assertCurrentLayoutIs(Layout.THREE_COLUMNS)
            .clickOk();

        userDashboardPage.assertNumberOfDashletColumnsIs(3);
    }

    @TestRail (id = "C2200")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void addNewDashletsToSiteDashboard()
    {
        customizeSiteDashboardPage.navigate(site.get())
            .clickAddDashlet()
            .addDashlet(SITE_LINKS, 1)
            .addDashlet(SITE_CALENDAR, 2)
            .clickOk();

        siteDashboardPage
            .assertDashletIsAddedInPosition(SITE_LINKS, 1, 2)
            .assertDashletIsAddedInPosition(SITE_CALENDAR, 2, 3);
    }

    @TestRail (id = "C2203, C2202")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void moveAndReorderDashlets()
    {
        customizeSiteDashboardPage.navigate(site.get())
            .clickAddDashlet()
                .assertAvailableDashletsSectionIsDisplayed()
                .clickCloseAvailabeDashlets()
                .assertAvailableDashletsSectionIsNotDisplayed()
            .clickAddDashlet()
                .addDashlet(Dashlets.SAVED_SEARCH, 1)
                .assertDashletIsAddedInColumn(Dashlets.SAVED_SEARCH, 1)
                .removeDashlet(Dashlets.SAVED_SEARCH, 1)
                .assertDashletIsNotAddedInColumn(Dashlets.SAVED_SEARCH, 1)
            .moveAddedDashletInColumn(Dashlets.SITE_MEMBERS, 1, 2)
                .assertDashletIsAddedInColumn(Dashlets.SITE_MEMBERS, 2)
                .moveDashletDownInColumn(SITE_CONTENT, 2)
            .clickOk();

        siteDashboardPage
            .assertDashletIsAddedInPosition(SITE_ACTIVITIES, 2, 1)
            .assertDashletIsAddedInPosition(SITE_CONTENT, 2, 2)
            .assertDashletIsAddedInPosition(SITE_MEMBERS, 2, 3);
    }

    @TestRail (id = "C2207")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void verifyDefaultLayoutAndDashlets()
    {
        siteDashboardPage.navigate(site.get())
            .assertDashletIsAddedInPosition(SITE_MEMBERS, 1, 1)
            .assertDashletIsAddedInPosition(SITE_CONTENT, 2, 1)
            .assertDashletIsAddedInPosition(SITE_ACTIVITIES, 2, 2);

    }

    @TestRail (id = "C2208")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITE_DASHBOARD })
    public void checkResizeSiteContentDashlet()
    {
        siteDashboardPage.navigate(site.get());

        siteContentDashlet.assertDashletIsExpandable();

        int sizeBefore = siteContentDashlet.getDashletHeight();
        siteContentDashlet.resizeDashlet(20, 1);
        int sizeAfter = siteContentDashlet.getDashletHeight();
        assertTrue(sizeAfter > sizeBefore, "Dashlet size is increased");
        siteContentDashlet.resizeDashlet(-30, 0);
        assertTrue(siteContentDashlet.getDashletHeight() < sizeAfter);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}
