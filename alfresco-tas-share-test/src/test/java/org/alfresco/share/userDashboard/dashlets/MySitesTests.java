package org.alfresco.share.userDashboard.dashlets;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DashboardCustomization.Page;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.dashlet.MySitesDashlet.SitesFilter;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MySitesTests extends ContextAwareWebTest
{
    @Autowired
    MySitesDashlet mySitesDashlet;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    ContentService content;

    private String userName;
    private String siteName1;
    private String siteName2;
    private String siteName3;

    @BeforeMethod
    public void setup()
    {
        super.setup();
        cleanupAuthenticatedSession();
        userName = "User1" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userName, DataUtil.PASSWORD, userName + "@tests.com", userName, userName);
        setupAuthenticatedSession(userName, DataUtil.PASSWORD);
    }

    @TestRail(id = "C2095")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void noSitesCreated()
    {
        logger.info("STEP 1 - Check that My Sites dashlet is displayed");
        userDashboardPage.navigate(userName);
        Assert.assertEquals(mySitesDashlet.getDashletTitle(), "My Sites", "My Sites dashlet name is not correct");

        logger.info("STEP 2 - Check that help icon is displayed");
        mySitesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_SITES);
        Assert.assertEquals(mySitesDashlet.isBalloonDisplayed(), true, "Help icon is not displayed");

        logger.info("STEP 3 - Check that site filter options are valid");
        Assert.assertTrue(mySitesDashlet.isMySitesFilterDisplayed(), "My Site filter values are not correct");

        logger.info("STEP 4 - Check that Create Site link is displayed");
        Assert.assertTrue(mySitesDashlet.isCreateSiteButtonDisplayed(), "Create site button is not displayed");

        logger.info("STEP 5 - Quickly access your sites message is displayed");
        Assert.assertEquals(mySitesDashlet.getDefaultSiteText(), "Quickly access your sites\n"
                + "A site is a project area where you can share and discuss content with other site members.", "Quick message is not displayed");
        logger.info("STEP 6 - Check that help ballon message is correct");
        mySitesDashlet.clickOnHelpIcon(DashletHelpIcon.MY_SITES);
        Assert.assertTrue(mySitesDashlet.isBalloonDisplayed(), "Help balloon is not displayed");
        //TODO: add message in language properties
        Assert.assertEquals(mySitesDashlet.getHelpBalloonMessage(),
                "Sites are project areas where you collaborate with others, sharing content and working on it together. "
                        + "This dashlet lists the sites you belong to. You can filter this list to show only your favorite sites.\n" + "From here you can:\n"
                        + "Navigate to a site\n" + "Create a new site\n" + "Delete a site if you are the site manager\n"
                        + "Mark a site as a favorite so that it shows in the Sites menu for easy access", "Help balloon message is not correct");

        logger.info("STEP 7 - Check that help ballon can be closed");
        mySitesDashlet.closeHelpBalloon();
        Assert.assertFalse(mySitesDashlet.isBalloonDisplayed(), "Help balloon is displayed");
    }

    @TestRail(id = "C2098")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void deleteSiteThenCancel()
    {
        logger.info("STEP 1 - Create site, check that is available in user dashboard");
        siteName1 = "Site1" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName1, "description", Visibility.PUBLIC);
        userDashboardPage.navigate(userName);
        Assert.assertEquals(mySitesDashlet.getSitesLinks().get(0).getText(), siteName1, "Existing site name is not correct");

        logger.info("STEP 2 - Delete site, then press Cancel on the first prompt");
        mySitesDashlet.clickDeleteSiteIconForSite(siteName1);
        mySitesDashlet.confirmDeleteSite("Cancel");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site is not available");

        logger.info("STEP 3 - Delete site, press Delete on the first prompt then No on the second one");
        mySitesDashlet.clickDeleteSiteIconForSite(siteName1);
        mySitesDashlet.confirmDeleteSite("Delete");
        mySitesDashlet.confirmDeleteSite("No");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site is not available");
    }

    @TestRail(id = "C2100")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER_DASHBOARD})
    public void filterSites()
    {
        logger.info("STEP 1 - Create 3 sites, mark the first one as favourite");
        siteName1 = "Site1" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName1, "description", Visibility.PUBLIC);

        siteName2 = "Site2" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName2, "description", Visibility.PUBLIC);

        siteName3 = "Site3" + DataUtil.getUniqueIdentifier();
        siteService.create(userName, DataUtil.PASSWORD, domain, siteName3, "description", Visibility.PUBLIC);

        userDashboardPage.navigate(userName);

        mySitesDashlet.clickOnFavoriteLink(siteName1);

        mySitesDashlet.accessSite(siteName2);
        List<Page> pagesToAdd = new ArrayList<Page>();
        pagesToAdd.add(Page.WIKI);
        Assert.assertTrue(siteService.addPagesToSite(adminUser, adminPassword, siteName2, pagesToAdd));

        userDashboardPage.navigate(userName);

        logger.info("STEP 2 - All filter, check that all sites are displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.All.toString());
        userDashboardPage.navigate(userName);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " is not available");
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " is not available");

        logger.info("STEP 3 - Recent filter, check that only site1 is displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.Recent.toString());
        userDashboardPage.navigate(userName);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " should not be available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " should not be available");

        logger.info("STEP 4 - My Favorites filter, check that only site1 is displayed");
        mySitesDashlet.selectOptionFromSiteFilters(SitesFilter.MyFavorites.toString());
        userDashboardPage.navigate(userName);
        Assert.assertTrue(mySitesDashlet.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName2), "Site " + siteName2 + " should not be available");
        Assert.assertFalse(mySitesDashlet.isSitePresent(siteName3), "Site " + siteName3 + " should not be available");
    }
}