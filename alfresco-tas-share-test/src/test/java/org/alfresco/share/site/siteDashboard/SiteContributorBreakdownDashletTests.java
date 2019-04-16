package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.CMISUtil;
import org.alfresco.dataprep.DashboardCustomization;
import org.alfresco.po.share.dashlet.SiteContributorBreakdownDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Mirela Tifui on 3/10/2017.
 */
public class SiteContributorBreakdownDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteContributorBreakdownDashlet siteContributorBreakdownDashlet;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    UserProfilePage userProfilePage;

    private String userName1 = String.format("CUser1%s", RandomData.getRandomAlphanumeric());
    private String userName2 = String.format("CUser2%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("CSiteName%s", RandomData.getRandomAlphanumeric());
    private String fileName1User1 = String.format("File1User1%s", RandomData.getRandomAlphanumeric());
    private String fileName2User1 = String.format("File2User1%s", RandomData.getRandomAlphanumeric());
    private String fileName1User2 = String.format("File1User2%s", RandomData.getRandomAlphanumeric());
    private String fileContent = "File content for site dashboard test";

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, "User1", "User1");
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, "FNUser2", "LNUser2");
        siteService.create(userName1, password, domain, siteName, siteName, SiteService.Visibility.PUBLIC);
        siteService.addDashlet(adminUser, adminPassword, siteName, DashboardCustomization.SiteDashlet.SITE_CONTRIB_BREAKDOWN, DashboardCustomization.DashletLayout.THREE_COLUMNS, 3, 1);
        userService.createSiteMember(userName1, password, userName2, siteName, "SiteManager");
        contentService.createDocument(userName1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User1, fileContent);
        contentService.createDocument(userName1, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName2User1, fileContent);
        contentService.createDocument(userName2, password, siteName, CMISUtil.DocumentType.TEXT_PLAIN, fileName1User2, fileContent);
        setupAuthenticatedSession(userName1, password);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser,adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser,adminPassword,siteName );
    }


    @TestRail(id="C202732")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void checkTheSiteContributorBreakdownDashlet()
    {
        LOG.info("Step 1: Check the Site Contributor Breakdown dashlet default data");
        siteDashboardPage.navigate(siteName);
        Assert.assertEquals(siteContributorBreakdownDashlet.getDashletTitle(), "Site Contributor Breakdown", "The Site Contributor Breakdown dashlet title is not correct");
        Assert.assertTrue(siteContributorBreakdownDashlet.isPeriodFilterDisplayed(), "The period filter is not displayed on the Site Contributor Breakdown dashlet");
        Assert.assertEquals(siteContributorBreakdownDashlet.getSelectedFilterOption(), "Last 30 Days", "Last 30 days option is not selected by default");

        LOG.info("Step 2: Check the Period Filter options");
        ArrayList<String> expectedOptions =  new ArrayList<>(Arrays.asList("Today", "Last 7 Days", "Last 30 Days", "Past Year", "Date Range"));
        Assert.assertEquals(siteContributorBreakdownDashlet.getOptionText(), expectedOptions, expectedOptions +" filter options are not displayed");
    }

    @TestRail(id ="C202304")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void openUsersProfileFromSiteContributorBreakdownDashlet()
    {
        LOG.info ("Step 1: Click on \"user2\" section.");
        siteDashboardPage.navigate(siteName);
        Assert.assertEquals(siteContributorBreakdownDashlet.getNumberOfPieChartSlices(), 2, "Pie chart slices is different!");
        siteContributorBreakdownDashlet.clickOnUserSection(userName2);

        LOG.info("Step 2: Check that user is redirected to User Profile page and that "+ userName2 + " profile details are displayed");
        String expectedName = "FNUser2 "+"LNUser2";
        String expectedEmail = userName2 + "@test.com";
        Assert.assertEquals(getBrowser().getTitle(), "Alfresco » User Profile Page", "User Profile Page is not displayed");
        Assert.assertEquals(userProfilePage.getNameLabel(), expectedName, expectedName + " is not displayed on the User Profile Page" );
        Assert.assertTrue(userProfilePage.getUserInformation().containsValue(expectedEmail),expectedEmail+" is not displayed on Contact information");
    }
}
