package org.alfresco.share.site;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FavoriteSiteTests extends ContextAwareWebTest
{
    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    DataUser dataUser;

    @Autowired
    DataSite dataSite;

    private String userName = "favoriteSitesUser" + RandomData.getRandomAlphanumeric();

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

    }

    @TestRail (id = "C2216")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addCurrentSiteToFavoritesUsingSitesMenu() throws DataPreparationException
    {
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite = dataSite.createPublicRandomSite();
        dataUser.addUserToSite(testUser, testSite, UserRole.SiteManager);
        setupAuthenticatedSession(testUser.getUsername(), password);
        LOG.info("STEP 1 - Navigate to the created site");
        siteDashboardPage.navigate(testSite.getTitle());
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");

        LOG.info("STEP 2 - Click \"Sites\" menu from Alfresco Toolbar");
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        LOG.info("STEP 3 - Click \"Add current site to Favorites\".");
        toolbar.clickSites().clickAddCurrentSiteToFavorites();
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        LOG.info("STEP 4 - Click again \"Sites\" menu. Click on \"Favorites\" icon.");
        toolbar.clickSites().assertSiteIsFavorite(testSite.getTitle());

        LOG.info("STEP 5 - Click on the site.");
        toolbar.clickSites().clickFavoriteSite(testSite).assertSiteDashboardPageIsOpened();

        LOG.info("STEP 6 - Go to User Dashboard page. Verify \"My Sites\" dashlet.");
        userDashboardPage.navigate(testUser.getUsername());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(testSite.getTitle()), testSite.getTitle() + " is favorite");
        userService.delete(adminUser, adminPassword, testUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser.getUsername());
        siteService.delete(adminUser, adminPassword, testSite.getTitle());

    }

    @TestRail (id = "C2217")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeCurrentSiteFromFavoritesUsingSitesMenu()
    {
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        LOG.info("STEP 1 - Navigate to the created site. Click \"Sites\" menu from Alfresco Toolbar");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        toolbar.clickSites().assertRemoveCurrentSiteFromFavoritesIsDisplayed();

        LOG.info("STEP 2 - Click \"Remove current site from Favorites\". Click again \"Sites\" menu");
        toolbar.clickSites().clickRemoveCurrentSiteFromFavorites();
        toolbar.clickSites().assertAddCurrentSiteToFavoritesDisplayed();

        LOG.info("STEP 3 - Click on \"Favorites\" icon");
        toolbar.clickSites().assertSiteIsNotFavorite(siteName);
        LOG.info("STEP 4 - Go to User Dashboard page. Verify \"My Sites\" dashlet");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(siteName), siteName + " isn't favorite");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2220")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addSiteToFavoritesUsingMySitesDashlet() throws DataPreparationException
    {
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite = dataSite.createPublicRandomSite();
        dataUser.addUserToSite(testUser, testSite, UserRole.SiteManager);
        setupAuthenticatedSession(testUser.getUsername(), password);
        LOG.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(testUser.getUsername());
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        LOG.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(testSite.getTitle()), testSite.getTitle() + " isn't favorite");
        LOG.info("STEP 3 - Click on \"Favorite\" link");
        mySitesDashlet.clickFavorite(testSite.getTitle());
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(testSite.getTitle()), testSite.getTitle() + " is favorite");
        LOG.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        toolbar.clickSites().assertSiteIsFavorite(testSite);
        userService.delete(adminUser, adminPassword, testUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser.getUsername());
        siteService.delete(adminUser, adminPassword, testSite.getTitle());
    }

    @TestRail (id = "C2221")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteFromFavoritesUsingMySitesDashlet()
    {
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        siteService.setFavorite(userName, password, siteName);
        setupAuthenticatedSession(userName, password);
        LOG.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        LOG.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertTrue(mySitesDashlet.isSiteFavorite(siteName), siteName + " is favorite");
        LOG.info("STEP 3 - Click on yellow star (\"Remove site from favorites\" option)");
        mySitesDashlet.clickFavorite(siteName);
        Assert.assertFalse(mySitesDashlet.isSiteFavorite(siteName), siteName + " isn't favorite");
        LOG.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        toolbar.clickSites().assertSiteIsNotFavorite(siteName);
        siteService.delete(adminUser, adminPassword, siteName);
    }
}
