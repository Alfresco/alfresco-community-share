package org.alfresco.share.site;

import lombok.extern.slf4j.Slf4j;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.Toolbar;
import org.alfresco.po.share.user.UserDashboardPage;

import org.alfresco.share.BaseTest;

import org.alfresco.testrail.TestRail;

import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;

import org.springframework.beans.factory.annotation.Autowired;

import org.testng.Assert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Slf4j
public class FavoriteSiteTests extends BaseTest
{
    //Autowired
    UserDashboardPage userDashboardPage;

    //@Autowired
    SiteDashboardPage siteDashboardPage;

    //@Autowired
    MySitesDashlet mySitesDashlet;

    Toolbar toolbar;

    @Autowired
    DataUser dataUser;

    @Autowired
    protected SiteService siteService;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteC2216 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteC2217 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteC2220 = new ThreadLocal<>();
    private final ThreadLocal<SiteModel> siteC2221 = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        log.info("Precondition: Any Test user is created");
        user.set(getDataUser().usingAdmin().createRandomTestUser());
        getCmisApi().authenticateUser(getAdminUser());

        siteDashboardPage = new SiteDashboardPage(webDriver);
        toolbar = new Toolbar(webDriver);
        userDashboardPage = new UserDashboardPage(webDriver);
        siteDashboardPage = new SiteDashboardPage(webDriver);
        mySitesDashlet = new MySitesDashlet(webDriver);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup()
    {
        deleteUsersIfNotNull(user.get());
    }

    @TestRail (id = "C2216")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addCurrentSiteToFavoritesUsingSitesMenu() throws DataPreparationException
    {
        log.info("PreCondition: Site siteNameC2216 is created");
        siteC2216.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        dataUser.addUserToSite(user.get(), siteC2216.get(), UserRole.SiteManager);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Navigate to the created site");
        siteDashboardPage.navigate(siteC2216.get().getId());
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");

        log.info("STEP 2 - Click \"Sites\" menu from Alfresco Toolbar");
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        log.info("STEP 3 - Click \"Add current site to Favorites\".");
        toolbar.clickSites().clickAddCurrentSiteToFavorites();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        log.info("STEP 4 - Click again \"Sites\" menu. Click on \"Favorites\" icon.");
        toolbar.clickSites().assertSiteIsFavorite(siteC2216.get().getId());

        log.info("STEP 5 - Click on the site.");
        toolbar.clickSites().clickFavoriteSite(siteC2216.get().getId()).assertSiteDashboardPageIsOpened();

        log.info("STEP 6 - Go to User Dashboard page. Verify \"My Sites\" dashlet.");
        userDashboardPage.navigate(user.get().getUsername());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(siteC2216.get().getId()), siteC2216.get().getId() + " is favorite");

        deleteSitesIfNotNull(siteC2216.get());

    }

    @TestRail (id = "C2217")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeCurrentSiteFromFavoritesUsingSitesMenu()
    {
        log.info("PreCondition: Site siteNameC2217 is created");
        siteC2217.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Navigate to the created site. Click \"Sites\" menu from Alfresco Toolbar");
        siteDashboardPage.navigate(siteC2217.get());
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        log.info("STEP 2 - Click \"Remove current site from Favorites\". Click again \"Sites\" menu");
        toolbar.clickSites().click_RemoveCurrentSiteFromFavorites();
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        log.info("STEP 3 - Click on \"Favorites\" icon");
        toolbar.clickSites().assertVerifySiteIsNotInFavorite(siteC2217.get().getId());

        log.info("STEP 4 - Go to User Dashboard page. Verify \"My Sites\" dashlet");
        userDashboardPage.navigate(user.get());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(siteC2217.get().getId()), siteC2217.get().getId() + " isn't favorite");

        deleteSitesIfNotNull(siteC2217.get());

    }

    @TestRail (id = "C2220")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addSiteToFavoritesUsingMySitesDashlet() throws DataPreparationException
    {
        log.info("PreCondition: Site siteNameC2220 is created");
        siteC2220.set(getDataSite().usingAdmin().createPublicRandomSite());
        getCmisApi().authenticateUser(getAdminUser());

        dataUser.addUserToSite(user.get(), siteC2220.get(), UserRole.SiteManager);

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(user.get().getUsername());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        log.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(siteC2220.get().getId()), siteC2220.get().getId() + " isn't favorite");

        log.info("STEP 3 - Click on \"Favorite\" link");
        mySitesDashlet.clickFavorite(siteC2220.get().getId());
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(siteC2220.get().getId()), siteC2220.get().getId() + " is favorite");

        log.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        toolbar.clickSites().assertSiteIsFavorite(siteC2220.get().getId());

        deleteSitesIfNotNull(siteC2220.get());
    }

    @TestRail (id = "C2221")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteFromFavoritesUsingMySitesDashlet()
    {
        log.info("PreCondition: Site siteNameC2221 is created");
        siteC2221.set(getDataSite().usingUser(user.get()).createPublicRandomSite());
        getCmisApi().authenticateUser(user.get());

        siteService.setFavorite(user.get().getUsername(), user.get().getPassword(), siteC2221.get().getId());

        authenticateUsingLoginPage(user.get());

        log.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(user.get());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        log.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(siteC2221.get().getId()), siteC2221.get().getId() + " is favorite");

        log.info("STEP 3 - Click on yellow star (\"Remove site from favorites\" option)");
        mySitesDashlet.clickFavorite(siteC2221.get().getId());
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(siteC2221.get().getId()), siteC2221.get().getId() + " isn't favorite");

        log.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        toolbar.clickSites().assertVerifySiteIsNotInFavorite(siteC2221.get().getId());

        deleteSitesIfNotNull(siteC2221.get());
    }
}
