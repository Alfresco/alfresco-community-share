package org.alfresco.share.site;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.DashboardCustomizationImpl.Layout;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.site.CustomizeSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author bogdan.bocancea
 */
public class CustomizeSiteDashboardTests extends ContextAwareWebTest
{
    @Autowired
    CustomizeSiteDashboardPage customizeSite;

    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteContentDashlet siteContentDashlet;

    @TestRail (id = "C2198")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeSiteDashboardLayout()
    {
        String userName = "user2198-" + RandomData.getRandomAlphanumeric() ;
        String siteName = String.format("C2198%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2198", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        customizeSite.navigate(siteName);

        LOG.info("Step 1 - Click 'Change Layout' from Customize Dashboard page.");
        customizeSite.clickChangeLayout();
        Assert.assertTrue(customizeSite.isOneColumnLayoutDisplayed(), "One column layout is not displayed");
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide left layout is not displayed");
        Assert.assertTrue(customizeSite.isThreeColumnsLayoutDisplayed(), "Three columns layout is not displayed");
        Assert.assertTrue(customizeSite.isFourColumnsLayoutDisplayed(), "Four columns layout is not displayed");

        LOG.info("Step 2 - Select 'Two columns: wide left, narrow right' layout.");
        customizeSite.selectLayout(Layout.TWO_COLUMNS_WIDE_LEFT);

        LOG.info("Step 3 - Click 'OK' button.");
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.getNumerOfColumns() == 2);
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1));
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_ACTIVITIES, 2, 2));
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 2, 1));

        LOG.info("Step 4 - Go to 'Settings' icon and click 'Customize Dashboard'.");
        customizeSite.navigate(siteName);
        customizeSite.clickChangeLayout();
        Assert.assertTrue(customizeSite.isOneColumnLayoutDisplayed());
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideRightDisplayed());
        Assert.assertTrue(customizeSite.isThreeColumnsLayoutDisplayed());
        Assert.assertTrue(customizeSite.isFourColumnsLayoutDisplayed());

        LOG.info("Step 5 - Select 'One column' layout.");
        customizeSite.selectLayout(Layout.ONE_COLUMN);

        LOG.info("Step 6 - Click 'Ok' button.");
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.getNumerOfColumns() == 1);
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1));
        Assert.assertFalse(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_ACTIVITIES, 2, 2));

        LOG.info("Step 7 - Go to 'Settings' icon and click 'Customize Dashboard'.");
        customizeSite.navigate(siteName);
        customizeSite.clickChangeLayout();
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideLeftDisplayed());
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideRightDisplayed());
        Assert.assertTrue(customizeSite.isThreeColumnsLayoutDisplayed());
        Assert.assertTrue(customizeSite.isFourColumnsLayoutDisplayed());

        LOG.info("Step 8 - Select 'Three columns: wide centre' layout.");
        customizeSite.selectLayout(Layout.THREE_COLUMNS);

        LOG.info("Step 9 - Add any dashlets on the second and on the third column.");
        customizeSite.addDashlet(Dashlets.WEB_VIEW, 2);
        customizeSite.addDashlet(Dashlets.WIKI, 3);
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.getNumerOfColumns() == 3);
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1), Dashlets.SITE_MEMBERS.getDashletName() + " is not on column 1 position 1");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.WEB_VIEW, 2, 1), Dashlets.WEB_VIEW.getDashletName() + " is not on column 2 position 1");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.WIKI, 3, 1), Dashlets.WIKI.getDashletName() + " is not on column 3 position 1");

        LOG.info("Step 10 - Go to 'Settings' icon and click 'Customize Dashboard'");
        customizeSite.navigate(siteName);
        customizeSite.clickChangeLayout();
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideLeftDisplayed());
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideRightDisplayed());
        Assert.assertTrue(customizeSite.isOneColumnLayoutDisplayed());
        Assert.assertTrue(customizeSite.isFourColumnsLayoutDisplayed());

        LOG.info("Step 11 - Select 'Four columns' layout.");
        customizeSite.selectLayout(Layout.FOUR_COLUMS);

        LOG.info("Step 12 - Add any dashlets on the fourth column.");
        customizeSite.addDashlet(Dashlets.SITE_SEARCH, 4);
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.getNumerOfColumns() == 4);
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1));
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.WEB_VIEW, 2, 1));
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.WIKI, 3, 1));
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_SEARCH, 4, 1));

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2200")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addNewDashlet()
    {
        String userName = "user2200-" + RandomData.getRandomAlphanumeric();
        String siteName = String.format("C2200%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2200", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);

        LOG.info("Step 1 - Open 'Site Dashboard' page for the created site. Click 'Settings icon' -> 'Customize Dashboard'.");
        setupAuthenticatedSession(userName, password);
        customizeSite.navigate(siteName);

        LOG.info("Step 2 - Click 'Add dashlets' button. Drag and drop available dashlets into a column.");
        customizeSite.addDashlet(Dashlets.WIKI, 1);
        customizeSite.addDashlet(Dashlets.SITE_PROFILE, 2);
        customizeSite.addDashlet(Dashlets.SITE_LINKS, 2);

        LOG.info("Step 3 - Click 'Ok' button.");
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.WIKI, 1, 2), "Dashlet " + Dashlets.WIKI.getDashletName() + " is missing");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_PROFILE, 2, 3), "Dashlet " + Dashlets.SITE_PROFILE.getDashletName() + " is missing");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_LINKS, 2, 4), "Dashlet " + Dashlets.SITE_LINKS.getDashletName() + " is missing");
        removeUserFromAlfresco(new UserModel(userName, password));
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2202")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyCustomizeSiteDashboardPage()
    {
        String userName = "user2202-" + RandomData.getRandomAlphanumeric();
        String siteName = String.format("C2202%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2202", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 1 / Step 2 - Go to 'Site Dashboard / Click 'Settings' icon -> 'Customize Dashboard.'");
        customizeSite.navigate(siteName);
        Assert.assertTrue(customizeSite.isChangeLayoutSectionDisplayed(), "Change layout section is not displayed");
        Assert.assertTrue(customizeSite.isDashletSectionDisplayed(), "Dashlets section is not displayed");

        LOG.info("Step 3 - Verify 'Change layout' section.");
        Assert.assertTrue(customizeSite.getCurrentLayout().equals(Layout.TWO_COLUMNS_WIDE_RIGHT.description), "Default layout is not " + Layout.TWO_COLUMNS_WIDE_RIGHT.description);
        Assert.assertTrue(customizeSite.isChangeLayoutButtonDisplayed(), "Change layout button is not displayed");

        LOG.info("Step 4 - Click 'Change Layout' button.");
        customizeSite.clickChangeLayout();
        Assert.assertTrue(customizeSite.isOneColumnLayoutDisplayed(), "One column layout is not displayed");
        Assert.assertTrue(customizeSite.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide left layout is not displayed");
        Assert.assertTrue(customizeSite.isThreeColumnsLayoutDisplayed(), "Three columns layout is not displayed");
        Assert.assertTrue(customizeSite.isFourColumnsLayoutDisplayed(), "Four columns layout is not displayed");

        LOG.info("Step 5 - Click 'Cancel' button.");
        customizeSite.clickCancelNewLayout();
        Assert.assertFalse(customizeSite.isOneColumnLayoutDisplayed(), "One column layout is displayed");
        Assert.assertFalse(customizeSite.isTwoColumnsLayoutWideLeftDisplayed(), "Two columns wide left layout is displayed");
        Assert.assertFalse(customizeSite.isThreeColumnsLayoutDisplayed(), "Three columns layout is displayed");
        Assert.assertFalse(customizeSite.isFourColumnsLayoutDisplayed(), "Four columns layout is displayed");

        LOG.info("Step 6 - Verify 'Dashlets' section.");
        Assert.assertTrue(customizeSite.isAddDashletButtonDisplayed(), "Add Dashlets button is not displayed");
        Assert.assertTrue(customizeSite.getDashletsFromColumn(1).get(0).equals(Dashlets.SITE_MEMBERS.getDashletName()));
        Assert.assertTrue(customizeSite.getDashletsFromColumn(2).get(0).equals(Dashlets.SITE_CONTENT.getDashletName()));
        Assert.assertTrue(customizeSite.getDashletsFromColumn(2).get(1).equals(Dashlets.SITE_ACTIVITIES.getDashletName()));

        LOG.info("Step 7 - Click 'Add dashlets' button.");
        customizeSite.clickAddDashlet();
        Assert.assertTrue(customizeSite.getAvailableDashlets().size() > 0, "Available dashlets are not displayed");

        LOG.info("Step 8 - Click 'Close' button above the available dashlets.");
        customizeSite.clickCloseAvailabeDashlets();
        Assert.assertFalse(customizeSite.isAvailableDashletListDisplayed(), "Available dashlets list is not closed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2203")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void moveOrReorderDashlets()
    {
        String userName = "user2203-" + RandomData.getRandomAlphanumeric();
        String siteName = String.format("C2203%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2203", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 1 - Open 'Site Dashboard' page for the created site. Click 'Settings icon' -> 'Customize Dashboard'.");
        customizeSite.navigate(siteName);

        LOG.info("Step 2 - Go to 'Dashlets' section. Drag and drop 'Site Content' dashlet from Column2 to Column1.");
        customizeSite.moveAddedDashletInColumn(Dashlets.SITE_CONTENT, 2, 1);

        LOG.info("Step 3 - Click 'Ok' button.");
        customizeSite.clickOk();
        Assert.assertFalse(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 2, 1), "Site Content dashlet is in column 2");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 1, 2), "Site Content dashlet is not in column 1");

        customizeSite.navigate(siteName);
        customizeSite.reorderDashletsInColumn(Dashlets.SITE_MEMBERS, Dashlets.SITE_CONTENT, 1);
        customizeSite.clickOk();
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 1, 1), "Site Content dashlet is not in column 1 first position");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 2), "Site Members dashlet is not in column 2 first position");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2207")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultLayoutAndDashlets()
    {
        String userName = "user2207-" + RandomData.getRandomAlphanumeric();
        String siteName = String.format("C2207%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName, "C2207", "lname");
        siteService.create(userName, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);

        LOG.info("Step 1 - Open 'Site Dashboard' page for the created site.");
        siteDashboard.navigate(siteName);

        LOG.info("Step 2 - Verify the layout of the dashboard.");
        Assert.assertTrue(siteDashboard.getNumerOfColumns() == 2, "Two column layout is not displayed");

        LOG.info("Step 3 - Verify the dashlets present on each column.");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1), "Site Members dashlet is not in column 1 first position");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_CONTENT, 2, 1), "Site Content dashlet is not in column 2 first position");
        Assert.assertTrue(siteDashboard.isDashletAddedInPosition(Dashlets.SITE_ACTIVITIES, 2, 2), "Site Activities dashlet is not in column 2 second position");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2208")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void resizeDashlet()
    {
        UserModel user = dataUser.createRandomTestUser();
        SiteModel site = dataSite.usingUser(user).createPublicRandomSite();

        setupAuthenticatedSession(user.getUsername(), password);

        LOG.info("Step 1 - Open 'Site Dashboard' page for the created site.");
        siteDashboard.navigate(site.getId());

        LOG.info("Step 2 - Resize any dashlet from the 'Site Dashboard',by dragging the bottom edge of the dashlet.");
        int sizeBefore = siteContentDashlet.getDashletHeight();
        siteContentDashlet.resizeDashlet(200, 0);
        int sizeAfter = siteContentDashlet.getDashletHeight();
        Assert.assertTrue(sizeBefore < sizeAfter, "Height is not changed");

        dataUser.usingAdmin().deleteUser(user);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user.getUsername());

        dataSite.usingAdmin().deleteSite(site);
    }
}
