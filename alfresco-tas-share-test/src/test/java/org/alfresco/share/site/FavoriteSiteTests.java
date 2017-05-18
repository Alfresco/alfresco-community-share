package org.alfresco.share.site;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.toolbar.ToolbarSitesMenu;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
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
    ToolbarSitesMenu toolbarSitesMenu;

    @TestRail(id = "C2216")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addCurrentSiteToFavoritesUsingSitesMenu()
    {
        String userName = String.format("User1%s", DataUtil.getUniqueIdentifier());
        String siteName = String.format("Site1%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(adminUser, adminPassword, domain, siteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Navigate to the created site");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");

        LOG.info("STEP 2 - Click \"Sites\" menu from Alfresco Toolbar");
        Assert.assertTrue(toolbarSitesMenu.isAddCurrentSiteToFavoritesDisplayed(), "\"Add current site to Favorites\" is displayed");

        LOG.info("STEP 3 - Click \"Add current site to Favorites\".");
        toolbarSitesMenu.clickAddCurrentSiteToFavorites();
        siteDashboardPage.renderedPage();
        Assert.assertTrue(toolbarSitesMenu.isRemoveCurrentSiteFromFavoritesDisplayed(), "\"Remove current site from Favorites\" is displayed");

        LOG.info("STEP 4 - Click again \"Sites\" menu. Click on \"Favorites\" icon.");
        Assert.assertTrue(toolbarSitesMenu.isSiteFavorite(siteName), siteName + " is favorite");

        LOG.info("STEP 5 - Click on the site.");
        toolbarSitesMenu.clickFavoriteSite(siteName);
        siteDashboardPage.renderedPage();
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");

        LOG.info("STEP 6 - Go to User Dashboard page. Verify \"My Sites\" dashlet.");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertTrue(mySitesDashlet.isSiteFavourite(siteName), siteName + " is favorite");
    }

    @TestRail(id = "C2217")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeCurrentSiteFromFavoritesUsingSitesMenu()
    {
        String userName = String.format("User1%s", DataUtil.getUniqueIdentifier());
        String siteName = String.format("Site1%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Navigate to the created site. Click \"Sites\" menu from Alfresco Toolbar");
        siteDashboardPage.navigate(siteName);
        Assert.assertTrue(siteDashboardPage.isAlfrescoLogoDisplayed(), "Alfresco logo is displayed");
        Assert.assertTrue(toolbarSitesMenu.isRemoveCurrentSiteFromFavoritesDisplayed(), "\"Remove current site from Favorites\" is displayed");

        LOG.info("STEP 2 - Click \"Remove current site from Favorites\". Click again \"Sites\" menu");
        toolbarSitesMenu.clickRemoveCurrentSiteFromFavorites();
        siteDashboardPage.renderedPage();
        Assert.assertTrue(toolbarSitesMenu.isAddCurrentSiteToFavoritesDisplayed(), "\"Add current site to Favorites\" is displayed");

        LOG.info("STEP 3 - Click on \"Favorites\" icon");
        Assert.assertFalse(toolbarSitesMenu.isSiteFavorite(siteName), siteName + " isn't favorite");

        LOG.info("STEP 4 - Go to User Dashboard page. Verify \"My Sites\" dashlet");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");
        Assert.assertFalse(mySitesDashlet.isSiteFavourite(siteName), siteName + " isn't favorite");
    }

    @TestRail(id = "C2220")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addSiteToFavoritesUsingMySitesDashlet()
    {
        String userName = String.format("User1%s", DataUtil.getUniqueIdentifier());
        String siteName = String.format("Site1%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertFalse(mySitesDashlet.isSiteFavourite(siteName), siteName + " isn't favorite");

        LOG.info("STEP 3 - Click on \"Favorite\" link");
        mySitesDashlet.clickOnFavoriteLink(siteName);
        Assert.assertTrue(mySitesDashlet.isSiteFavourite(siteName), siteName + " is favorite");

        LOG.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        Assert.assertTrue(toolbarSitesMenu.isSiteFavorite(siteName), siteName + " is favorite");
    }

    @TestRail(id = "C2221")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteFromFavoritesUsingMySitesDashlet()
    {
        String userName = String.format("User1%s", DataUtil.getUniqueIdentifier());
        String siteName = String.format("Site1%s", DataUtil.getUniqueIdentifier());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        siteService.setFavorite(userName, password, siteName);
        setupAuthenticatedSession(userName, password);

        LOG.info("STEP 1 - Go to \"User Dashboard\" page");
        userDashboardPage.navigate(userName);
        Assert.assertTrue(userDashboardPage.isCustomizeUserDashboardDisplayed(), "\"Customize User Dashboard\" is displayed");

        LOG.info("STEP 2 - Verify \"My Sites\" dashlet");
        Assert.assertTrue(mySitesDashlet.isSiteFavourite(siteName), siteName + " is favorite");

        LOG.info("STEP 3 - Click on yellow star (\"Remove site from favorites\" option)");
        mySitesDashlet.clickOnFavoriteLink(siteName);
        Assert.assertFalse(mySitesDashlet.isSiteFavourite(siteName), siteName + " isn't favorite");

        LOG.info("STEP 4 - Go to Alfresco Toolbar -> \"Sites\" menu. Click on \"Favorites\"");
        Assert.assertFalse(toolbarSitesMenu.isSiteFavorite(siteName), siteName + " isn't favorite");
    }
}
