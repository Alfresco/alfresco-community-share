package org.alfresco.share.site.accessingExistingSites;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.SiteFinderPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 7/6/2016.
 */
public class EnteringSiteTests extends ContextAwareWebTest
{
    @Autowired
    SiteFinderPage siteFinderPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    UserSitesListPage userSitesListPage;

    @Autowired
    Toolbar toolbar;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String user1 = String.format("testUser1%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("description%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, "firstName", "lastName");
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        siteService.setFavorite(user1, password, siteName);
        setupAuthenticatedSession(user1, password);
    }


    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2977")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingMySitesDashlet()
    {
        userDashboardPage.navigate(user1);
        LOG.info("STEP 1: Verify 'My Sites' dashlet.");
        assertTrue(mySitesDashlet.isSitePresent(siteName), siteName + " should be displayed in 'My Sites' dashlet.");

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        mySitesDashlet.accessSite(siteName);
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + siteName + "/dashboard"), "User should be redirected to " + siteName + "'s dashboard page.");
    }

    @TestRail (id = "C2978")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSiteFinderPage()
    {
        userDashboardPage.navigate(user1);
        siteFinderPage.navigate();

        LOG.info("STEP 1: Search for '" + siteName + "'.");
        siteFinderPage.searchSiteWithRetry(siteName);
        assertTrue(siteFinderPage.checkSiteWasFound(siteName), siteName + " should be found.");

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        siteFinderPage.accessSite(siteName);
        assertTrue(siteFinderPage.getCurrentUrl().endsWith("site/" + siteName + "/dashboard"), "User should be redirected to " + siteName + "'s dashboard page.");
    }

    @TestRail (id = "C2979")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuFavorites()
    {
        userDashboardPage.navigate(user1);
        LOG.info("Click on 'Sites' menu -> 'Favorites' link.");
        toolbar.clickSites().assertSiteIsFavorite(siteName);

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        toolbar.clickSites().clickFavoriteSite(siteName).assertSiteDashboardPageIsOpened();
    }

    @TestRail (id = "C2980")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuRecentSites()
    {
        //precondition: site is recently accessed by current user
        siteDashboardPage.navigate(siteName);
        toolbar.clickHome();

        LOG.info("Click on 'Sites' and verify 'Recent Sites' section.");
        toolbar.clickSites().assertSiteIsInRecentSites(siteName);

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        toolbar.clickSites().clickRecentSite(siteName);
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + siteName + "/dashboard"), "User should be redirected to " + siteName + "'s dashboard page.");
    }

    @TestRail (id = "C2981")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingMyProfileSites()
    {
        userSitesListPage.navigate(user1)
            .assertSiteIsDisplayed(new SiteModel(siteName));

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        userSitesListPage.clickSite(new SiteModel(siteName)).assertSiteDashboardPageIsOpened();
    }

    @TestRail (id = "C2982")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSitesMenuMySites()
    {
        userDashboardPage.navigate(user1);
        toolbar.clickSites().clickMySites()
            .assertSiteIsDisplayed(new SiteModel(siteName));
        LOG.info("STEP 1: Verify the sites from 'User Sites List' list.");

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        userSitesListPage.clickSite(new SiteModel(siteName));
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + siteName + "/dashboard"), "User should be redirected to " + siteName + "'s dashboard page.");
    }

    @TestRail (id = "C3006")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void accessSiteUsingSearchBoxOnTheToolbar()
    {
        userDashboardPage.navigate(user1);
        getBrowser().refresh();
        LOG.info("STEP 1: Go to search box on the toolbar and type '" + siteName + "'.");
        toolbar.searchInToolbar(siteName);
        assertTrue(toolbar.isResultDisplayedInLiveSearch(siteName), siteName + " should be found.");

        LOG.info("STEP 2: Click on '" + siteName + "' link.");
        toolbar.clickResult(siteName, siteDashboardPage);
        assertTrue(mySitesDashlet.getCurrentUrl().endsWith("site/" + siteName + "/dashboard"), "User should be redirected to " + siteName + "'s dashboard page.");
    }
}
