package org.alfresco.share.userProfile;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserProfileSitesTests extends ContextAwareWebTest
{
    @Autowired
    UserSitesListPage userSites;

    @Autowired
    SiteDashboardPage siteDashboard;

    private String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private String siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
    private String siteName2 = String.format("Site2%s", RandomData.getRandomAlphanumeric());
    private String siteName3 = String.format("Site3%s", RandomData.getRandomAlphanumeric());
    private String siteName4 = String.format("Site4%s", RandomData.getRandomAlphanumeric());
    private String role = "SiteConsumer";

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName1, userName1, userName1 + domain, "fName1", "lName1");
        userService.create(adminUser, adminPassword, userName2, userName2, userName2 + domain, "fName2", "lName2");

        siteService.create(userName1, userName1, domain, siteName1, "description", SiteService.Visibility.PUBLIC);
        siteService.create(userName1, userName1, domain, siteName2, "description", SiteService.Visibility.PRIVATE);
        siteService.create(userName1, userName1, domain, siteName3, "description", SiteService.Visibility.PRIVATE);
        siteService.create(userName2, userName2, domain, siteName4, "description", SiteService.Visibility.PRIVATE);
        userService.createSiteMember(userName1, userName1, userName2, siteName3, role);
        setupAuthenticatedSession(userName2, userName2);
    }

    @AfterClass
    public void tearDown()
    {
        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser, adminPassword, siteName1);
        siteService.delete(adminUser, adminPassword, siteName2);
        siteService.delete(adminUser, adminPassword, siteName3);
        siteService.delete(adminUser, adminPassword, siteName4);
    }


    @TestRail (id = "C2154")
    @Test (groups = { TestGroup.SANITY, TestGroup.USER })
    public void viewSitesWhereUserHasMembership()
    {
        LOG.info("STEP1: Go to 'User Profile' --> 'Sites' page for User1");
        userSites.navigate(userName1);

        LOG.info("STEP2: Verify list of sites displayed for User1");
        Assert.assertTrue(userSites.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertFalse(userSites.isSitePresent(siteName2), "Site " + siteName2 + " should not be available");
        //       Assert.assertTrue(userSites.isSitePresent(siteName3), "Site " + siteName3 + " is not be available");
        Assert.assertFalse(userSites.isSitePresent(siteName4), "Site " + siteName4 + " should not be available");

        LOG.info("STEP3: Go to 'My Profile' --> 'Sites' page");
        userSites.navigate(userName2);

        LOG.info("STEP4: Verify list of sites displayed for User2");
        Assert.assertFalse(userSites.isSitePresent(siteName1), "Site " + siteName1 + " should not be available");
        Assert.assertFalse(userSites.isSitePresent(siteName2), "Site " + siteName2 + " should not be available");
        Assert.assertTrue(userSites.isSitePresent(siteName3), "Site " + siteName3 + " is not be available");
        Assert.assertTrue(userSites.isSitePresent(siteName4), "Site " + siteName4 + " is not be available");

        LOG.info("STEP5: Click on Site3 and verify 'Site Dashboard' page is opened, e.g. by verifying 'Welcome message' from 'Site Profile' dashlet");
        userSites.clickSite(siteName3);
        Assert.assertTrue(siteDashboard.getPageHeader().equals(siteName3));
    }


}
