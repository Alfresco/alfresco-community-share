package org.alfresco.share.userProfile;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.profile.UserSitesListPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
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

    private String userName1;
    private String userName2;
    private String siteName1;
    private String siteName2;
    private String siteName3;
    private String siteName4;
    private String role;

    @BeforeClass
    public void setup()
    {
        super.setup();
        userName1 = "User1" + DataUtil.getUniqueIdentifier();
        userName2 = "User2" + DataUtil.getUniqueIdentifier();
        siteName1 = "Site1" + DataUtil.getUniqueIdentifier();
        siteName2 = "Site2" + DataUtil.getUniqueIdentifier();
        siteName3 = "Site3" + DataUtil.getUniqueIdentifier();
        siteName4 = "Site4" + DataUtil.getUniqueIdentifier();
        role = "SiteConsumer";

        userService.create(adminUser, adminPassword, userName1, userName1, userName1 + domain, "fName1", "lName1");
        userService.create(adminUser, adminPassword, userName2, userName2, userName2 + domain, "fName2", "lName2");

        siteService.create(userName1, userName1, domain, siteName1, "description", Visibility.PUBLIC);
        siteService.create(userName1, userName1, domain, siteName2, "description", Visibility.PRIVATE);
        siteService.create(userName1, userName1, domain, siteName3, "description", Visibility.PRIVATE);
        siteService.create(userName2, userName2, domain, siteName4, "description", Visibility.PRIVATE);
        userService.createSiteMember(userName1, userName1, userName2, siteName3, role);
        setupAuthenticatedSession(userName2, userName2);
    }

    @TestRail(id = "C2154")
    @Test(groups = { TestGroup.SANITY, TestGroup.USER})
    public void viewSitesWhereUserHasMembership()
    {
        LOG.info("STEP1: Go to 'User Profile' --> 'Sites' page for User1");
        userSites.navigate(userName1);

        LOG.info("STEP2: Verify list of sites displayed for User1");
        Assert.assertTrue(userSites.isSitePresent(siteName1), "Site " + siteName1 + " is not available");
        Assert.assertFalse(userSites.isSitePresent(siteName2), "Site " + siteName2 + " should not be available");
        Assert.assertTrue(userSites.isSitePresent(siteName3), "Site " + siteName3 + " is not be available");
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

    @AfterClass
    public void tearDown()
    {
        userService.delete(adminUser, adminPassword, userName1);
        userService.delete(adminUser, adminPassword, userName2);
    }
}
