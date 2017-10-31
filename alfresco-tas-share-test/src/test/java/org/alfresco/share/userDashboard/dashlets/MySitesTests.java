package org.alfresco.share.userDashboard.dashlets;

import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet.SitesFilter;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MySitesTests extends ContextAwareWebTest
{
    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    private String userName;
    private String siteName1;
    private String siteName2;
    private String siteName3;

    @BeforeMethod(alwaysRun = true)
    public void createUser()
    {
        cleanupAuthenticatedSession();
        userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        setupAuthenticatedSession(userName, password);
    }

    @TestRail(id = "C2095")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void noSitesCreated()
    {
        LOG.info("STEP 1 - Check that My Sites dashlet is displayed");
        userDashboardPage.navigate(userName);
        Assert.assertEquals(mySitesDashlet.getDashletTitle(), "My Sites", "My Sites dashlet name is not correct");

        LOG.info("STEP 2 - Check that help icon is displayed");
        mySitesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_SITES);
        Assert.assertEquals(mySitesDashlet.isBalloonDisplayed(), true, "Help icon is not displayed");

        LOG.info("STEP 3 - Check that site filter options are valid");
        Assert.assertTrue(mySitesDashlet.isMySitesFilterDisplayed(), "My Site filter values are not correct");

        LOG.info("STEP 4 - Check that Create Site link is displayed");
        Assert.assertTrue(mySitesDashlet.isCreateSiteButtonDisplayed(), "Create site button is not displayed");

        LOG.info("STEP 5 - Quickly access your sites message is displayed");
        Assert.assertEquals(mySitesDashlet.getDefaultSiteText(), "Quickly access your sites\n"
                + "A site is a project area where you can share and discuss content with other site members.", "Quick message is not displayed");
        LOG.info("STEP 6 - Check that help ballon message is correct");
        mySitesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_SITES);
        Assert.assertTrue(mySitesDashlet.isBalloonDisplayed(), "Help balloon is not displayed");
        //TODO: add message in language properties
        Assert.assertEquals(mySitesDashlet.getHelpBalloonMessage(),
                "Sites are project areas where you collaborate with others, sharing content and working on it together. "
                        + "This dashlet lists the sites you belong to. You can filter this list to show only your favorite sites.\n" + "From here you can:\n"
                        + "Navigate to a site\n" + "Create a new site\n" + "Delete a site if you are the site manager\n"
                        + "Mark a site as a favorite so that it shows in the Sites menu for easy access", "Help balloon message is not correct");

        LOG.info("STEP 7 - Check that help ballon can be closed");
        mySitesDashlet.closeHelpBalloon();
        Assert.assertFalse(mySitesDashlet.isBalloonDisplayed(), "Help balloon is displayed");
    }

    @TestRail(id = "C2098")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void deleteSiteThenCancel()
    {
        LOG.info("STEP 1 - Create site, check that is available in user dashboard");
        siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName1, "description", SiteService.Visibility.PUBLIC);
        userDashboardPage.navigate(userName);
        Assert.assertEquals(mySitesDashlet.getSitesLinks().get(0).getText(), siteName1, "Existing site name is not correct");

        LOG.info("STEP 2 - Delete site, then press Cancel on the first prompt");
        mySitesDashlet.clickDeleteSiteIconForSite(siteName1);
        mySitesDashlet.confirmDeleteSite("Cancel");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site is not available");

        LOG.info("STEP 3 - Delete site, press Delete on the first prompt then No on the second one");
        mySitesDashlet.clickDeleteSiteIconForSite(siteName1);
        mySitesDashlet.confirmDeleteSite("Delete");
        mySitesDashlet.confirmDeleteSite("No");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site is not available");
    }

    @TestRail(id = "C2100")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void filterSites()
    {
        LOG.info("STEP 1 - Create 3 sites, mark the first one as favourite");
        siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName1, "description", SiteService.Visibility.PUBLIC);

        siteName2 = String.format("Site2%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName2, "description", SiteService.Visibility.PUBLIC);

        siteName3 = String.format("Site3%s", RandomData.getRandomAlphanumeric());
        siteService.create(userName, password, domain, siteName3, "description", SiteService.Visibility.PUBLIC);

        userDashboardPage.navigate(userName);

        mySitesDashlet.clickOnFavoriteLink(siteName1);
        mySitesDashlet.accessSite(siteName2);

        userDashboardPage.navigate(userName);

        LOG.info("STEP 2 - All filter, check that all sites are displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.All.toString());
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " is not available");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " is not available");

        LOG.info("STEP 3 - Recent filter, check that only site1 is displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.Recent.toString());
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " is not available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " is available");

        LOG.info("STEP 4 - My Favorites filter, check that only site1 is displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.MyFavorites.toString());
        getBrowser().waitInSeconds(2);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " is available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " is available");
    }
}