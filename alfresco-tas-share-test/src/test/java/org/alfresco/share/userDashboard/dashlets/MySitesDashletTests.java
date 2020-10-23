package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet.SitesFilter;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MySitesDashletTests extends AbstractUserDashboardDashletsTests
{
    @Autowired
    private MySitesDashlet mySitesDashlet;

    private UserModel user;

    @BeforeClass(alwaysRun = true)
    public void setupUser()
    {
        user = dataUser.usingAdmin().createRandomTestUser();
        setupAuthenticatedSession(user);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C2095")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void checkNoSitesCreated()
    {
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
        SiteModel deleteSite = dataSite.usingUser(user).createPublicRandomSite();
        userDashboard.navigate(user);
        mySitesDashlet.assertSiteIsDisplayed(deleteSite);

        mySitesDashlet.clickDelete(deleteSite)
            .clickCancel();
        mySitesDashlet.assertSiteIsDisplayed(deleteSite)
            .clickDelete(deleteSite)
                .clickDelete()
                    .clickNo();
        mySitesDashlet.assertSiteIsDisplayed(deleteSite);
        dataSite.usingUser(user).deleteSite(deleteSite);
    }

    @TestRail (id = "C2097")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void deleteSite()
    {
        SiteModel deleteSite = dataSite.usingUser(user).createPublicRandomSite();
        userDashboard.navigate(user);
        mySitesDashlet.assertSiteIsDisplayed(deleteSite)
            .clickDelete(deleteSite)
                .clickDelete().clickYes();
        mySitesDashlet.assertSiteIsNotDisplayed(deleteSite);
    }

    @TestRail (id = "C2100")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD })
    public void filterSites()
    {
        SiteModel site1 = dataSite.usingUser(user).createPublicRandomSite();
        SiteModel site2 = dataSite.usingUser(user).createPublicRandomSite();

        userDashboard.navigate(user);
        mySitesDashlet.assertSiteIsDisplayed(site1).clickFavorite(site1);
        mySitesDashlet.accessSite(site2).assertSiteDashboardPageIsOpened();
        userDashboard.navigate(user);

        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.RECENT);
        mySitesDashlet.assertSiteIsNotDisplayed(site1);
        mySitesDashlet.assertSiteIsDisplayed(site2);

        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.MY_FAVORITES);
        mySitesDashlet.assertSiteIsDisplayed(site2);
        mySitesDashlet.assertSiteIsNotDisplayed(site1);

        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.ALL);
        mySitesDashlet.assertSiteIsDisplayed(site1);
        mySitesDashlet.assertSiteIsDisplayed(site2);

        deleteSites(site1, site2);
    }

    @Test (groups = { TestGroup.SHARE, TestGroup.USER_DASHBOARD })
    public void createSiteFromSiteDashlet()
    {
        SiteModel site = new SiteModel(RandomData.getRandomName("site"));
        userDashboard.navigate(user);
        mySitesDashlet.clickCreateSiteButton()
            .assertCreateSiteDialogIsDisplayed()
                .typeInNameInput(site.getTitle())
                .typeInSiteID(site.getId())
                .typeInDescription(site.getDescription())
                .clickCreateButton()
                    .assertSiteDashboardPageIsOpened().assertSiteHeaderTitleIs(site);
        deleteSites(site);
    }
}