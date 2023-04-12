package org.alfresco.share.site.accessingExistingSites;

import lombok.extern.slf4j.Slf4j;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Slf4j
public class EnteringSiteTests extends BaseTest
{
    SiteFinderPage siteFinderPage;
    MySitesDashlet mySitesDashlet;
    UserSitesListPage userSitesListPage;
    Toolbar toolbar;
    SiteDashboardPage siteDashboardPage;
    UserDashboardPage userDashboardPage;
    private final ThreadLocal<UserModel> user1 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> site = new ThreadLocal<>();
    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("PreCondition: Creating a TestUser1");
        user1.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        log.info("PreCondition: Creating a Random Site");
        site.set(getDataSite().usingUser(user1.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user1.get());

        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
        siteFinderPage = new SiteFinderPage(webDriver);
        toolbar = new Toolbar(webDriver);
        userSitesListPage = new UserSitesListPage(webDriver);

        log.info("Precondition 1: Any user logged in Share.");
        authenticateUsingLoginPage(user1.get());
    }


    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user1.get());
        deleteSitesIfNotNull(site.get());
        deleteAllCookiesIfNotNull();
    }

    @TestRail (id = "C2977")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingMySitesDashlet()
    {
        userDashboardPage.navigate(user1.get());
        log.info("STEP 1: Verify 'My Sites' dashlet.");
        assertTrue(mySitesDashlet.isSitePresent(site.get().getId()), site.get() + " should be displayed in 'My Sites' dashlet.");

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        mySitesDashlet.accessSite(site.get().getId());
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + site.get().getTitle() + "/dashboard"), "User should be redirected to " + site.get().getTitle() + "'s dashboard page.");
    }

    @TestRail (id = "C2978")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSiteFinderPage()
    {
        userDashboardPage.navigate(user1.get());
        siteFinderPage.navigate();

        log.info("STEP 1: Search for '" + site.get().getId() + "'.");
        siteFinderPage.searchSiteNameWithRetry(site.get().getId());
        assertTrue(siteFinderPage.checkSiteWasFound(site.get().getId()), site.get().getId() + " should be found.");

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        siteFinderPage.accessSite(site.get().getId());
        assertTrue(siteFinderPage.getCurrentUrl().endsWith("site/" + site.get().getId() + "/dashboard"), "User should be redirected to " + site.get().getId() + "'s dashboard page.");
    }

    @TestRail (id = "C2979")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuFavorites()
    {
        userDashboardPage.navigate(user1.get());
        log.info("Click on 'Sites' menu -> 'Favorites' link.");
        toolbar.clickSites().assertSiteIsFavorite(site.get());

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        toolbar.clickSites().clickFavoriteSite(site.get()).assertSiteDashboardPageIsOpened();
    }

    @TestRail (id = "C2980")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuRecentSites()
    {
        //precondition: site is recently accessed by current user
        siteDashboardPage.navigate(site.get());
        toolbar.clickHome();

        log.info("Click on 'Sites' and verify 'Recent Sites' section.");
        toolbar.clickSites().assertSiteIsInRecentSites(site.get());

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        toolbar.clickSites().clickRecentSite(site.get());
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + site.get().getTitle() + "/dashboard"), "User should be redirected to " + site.get() + "'s dashboard page.");
    }

    @TestRail (id = "C2981")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingMyProfileSites()
    {
        userSitesListPage.navigate(user1.get())
            .assertSiteIsDisplayed(new SiteModel(site.get().getTitle()));

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        userSitesListPage.clickSite(new SiteModel(site.get().getTitle())).assertSiteDashboardPageIsOpened();
    }

    @TestRail (id = "C2982")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuMySites()
    {
        userDashboardPage.navigate(user1.get());
        toolbar.clickSites().clickMySites()
            .assertSiteIsDisplayed(new SiteModel(site.get().getTitle()));
        log.info("STEP 1: Verify the sites from 'User Sites List' list.");

        log.info("STEP 2: Click on '" + site.get() + "' link.");
        userSitesListPage.clickSite(new SiteModel(site.get().getTitle()));
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + site.get().getTitle() + "/dashboard"), "User should be redirected to " + site.get() + "'s dashboard page.");
    }

    @TestRail (id = "C3006")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSearchBoxOnTheToolbar()
    {
        userDashboardPage.navigate(user1.get());
        log.info("STEP 1: Go to search box on the toolbar and type '" + site.get() + "'.");
        toolbar.searchInToolbar(site.get().getId());
        assertTrue(toolbar.isResultDisplayedLiveSearch(site.get().getId()), site.get() + " should be found.");

        log.info("STEP 2: Click on '" + site.get().getId() + "' link.");
        toolbar.clickResult(site.get().getId());
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + site.get().getId() + "/dashboard"), "User should be redirected to " + site.get() + "'s dashboard page.");
    }
}
