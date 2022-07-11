package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.enums.DashletHelpIcon;
import org.alfresco.po.enums.SitesFilter;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MySitesDashletTests extends AbstractUserDashboardDashletsTests
{
    private MySitesDashlet mySitesDashlet;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        mySitesDashlet = new MySitesDashlet(webDriver);

        user.set(dataUser.usingAdmin().createRandomTestUser());
        authenticateUsingCookies(user.get());
    }

    @TestRail (id = "C2095")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkNoSitesCreated()
    {
        userDashboardPage.navigate(user.get());
        mySitesDashlet.assertDashletTitleEquals(language.translate("mySitesDashlet.title"))
            .clickOnHelpIcon(DashletHelpIcon.MY_SITES)
                .assertBalloonMessageIsDisplayed()
                .assertHelpBalloonMessageEquals(language.translate("mySitesDashlet.balloonMessage"))
                .closeHelpBalloon()
            .assertSitesFilterButtonIsDisplayed()
            .assertCreateSiteButtonIsDisplayed()
            .assertSelectedFilterIs(SitesFilter.ALL)
            .assertEmptySitesMessageIsDisplayed();
    }

    @TestRail (id = "C2098")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void deleteSiteThenCancel()
    {
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        userDashboardPage.navigate(user.get());
        mySitesDashlet.assertSiteIsDisplayed(site.get());

        mySitesDashlet.clickDelete(site.get()).clickCancel();
        mySitesDashlet.assertSiteIsDisplayed(site.get())
            .clickDelete(site.get())
                .clickDelete()
                    .clickNo();
        mySitesDashlet.assertSiteIsDisplayed(site.get());
    }

    @TestRail (id = "C2097")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void deleteSite()
    {
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());

        userDashboardPage.navigate(user.get());
        mySitesDashlet.assertSiteIsDisplayed(site.get())
            .clickDelete(site.get())
                .clickDelete().clickYes();
        mySitesDashlet.assertSiteIsNotDisplayed(site.get());
    }

    @TestRail (id = "C2100")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void filterSites()
    {
        site.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        SiteModel secondSite = getDataSite().usingUser(user.get()).createPublicRandomSite();

        userDashboardPage.navigate(user.get());
        mySitesDashlet.assertSiteIsDisplayed(site.get()).clickFavorite(site.get());
        mySitesDashlet.accessSite(secondSite).assertSiteDashboardPageIsOpened();

        userDashboardPage.navigate(user.get());
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.RECENT)
            .assertSiteIsNotDisplayed(site.get())
            .assertSiteIsDisplayed(secondSite);

        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.MY_FAVORITES)
            .assertSiteIsDisplayed(secondSite)
            .assertSiteIsNotDisplayed(site.get());

        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.ALL)
            .assertSiteIsDisplayed(site.get())
            .assertSiteIsDisplayed(secondSite);

        deleteSitesIfNotNull(secondSite);
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void createSiteFromSiteDashlet()
    {
        SiteModel site = new SiteModel(RandomData.getRandomName("site"));
        userDashboardPage.navigate(user.get());
        mySitesDashlet.clickCreateSiteButton()
            .assertCreateSiteDialogIsDisplayed()
                .typeInNameInput(site.getTitle())
                .typeInSiteID(site.getId())
                .typeInDescription(site.getDescription())
                .clickCreateButton()
                    .assertSiteDashboardPageIsOpened().assertSiteHeaderTitleIs(site);

        deleteSitesIfNotNull(site);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
        deleteSitesIfNotNull(site.get());
    }
}