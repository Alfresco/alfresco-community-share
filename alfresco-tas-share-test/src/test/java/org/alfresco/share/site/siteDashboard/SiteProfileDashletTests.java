package org.alfresco.share.site.siteDashboard;

import org.alfresco.dataprep.DashboardCustomization.DashletLayout;
import org.alfresco.dataprep.DashboardCustomization.SiteDashlet;
import org.alfresco.po.share.dashlet.Dashlet.DashletHelpIcon;
import org.alfresco.po.share.dashlet.SiteProfileDashlet;
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

public class SiteProfileDashletTests extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboard;

    @Autowired
    SiteProfileDashlet siteProfileDashlet;

    @Autowired
    UserProfilePage userProfilePage;

    private String userName1;
    private String userName2;
    private String siteName1;
    private String role;
    private String welcomeMessage;
    private String siteDescription;
    private String siteVisibility;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        siteName1 = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        role = "SiteManager";
        siteVisibility = "Public";
        siteDescription = "testDescription";
        welcomeMessage = "Welcome to " + siteName1;

        userService.create(adminUser, adminPassword, userName1, userName1, userName1 + domain, "fName1", "lName1");
        userService.create(adminUser, adminPassword, userName2, userName2, userName2 + domain, "fName2", "lName2");
        siteService.create(userName1, userName1, domain, siteName1, "testDescription", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userName1, userName1, userName2, siteName1, role);
        siteService.addDashlet(userName1, userName1, siteName1, SiteDashlet.SITE_PROFILE, DashletLayout.TWO_COLUMNS_WIDE_RIGHT, 1, 2);

        setupAuthenticatedSession(userName1, userName1);

    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser,adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);
        siteService.delete(adminUser,adminPassword,siteName1 );

    }

    @TestRail(id = "C2811")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifySiteProfileDashlet()
    {

        LOG.info("STEP 1 - Verify \"Site Profile\" dashlet");
        siteDashboard.navigate(siteName1);

        Assert.assertEquals(siteProfileDashlet.getDashletTitle(), "Site Profile");

        Assert.assertTrue(siteProfileDashlet.isHelpIconDisplayed(DashletHelpIcon.SITE_PROFILE));
        Assert.assertEquals(siteProfileDashlet.getWelcomeMessageText(), welcomeMessage);

        Assert.assertTrue(siteProfileDashlet.isSiteDescriptionPresent(siteDescription));
        Assert.assertTrue(siteProfileDashlet.isSiteManagersLabelDisplayed());

        Assert.assertTrue(siteProfileDashlet.isVisibilityLabelDisplayed());
        Assert.assertTrue(siteProfileDashlet.isSiteVisibilityPresent(siteVisibility), "Site " + siteName1 + "'s visibility is not available");

        Assert.assertTrue(siteProfileDashlet.isMemberPresent("fName1 lName1"), "User " + userName1 + " is not present");
        Assert.assertTrue(siteProfileDashlet.isMemberPresent("fName2 lName2"), "User " + userName2 + " is not present");

        LOG.info("STEP 2 - Click  \"Help\" icon and verify it's contents");
        siteProfileDashlet.clickOnHelpIcon(DashletHelpIcon.SITE_PROFILE);

        Assert.assertTrue(siteProfileDashlet.isBalloonDisplayed());

        Assert.assertEquals(siteProfileDashlet.getHelpBalloonMessage(),
                "This dashlet displays the site details. Only the site manager can change this information.");

        LOG.info("STEP 3 - Close  \"Help\" icon");
        siteProfileDashlet.closeHelpBalloon();
        Assert.assertFalse(siteProfileDashlet.isBalloonDisplayed());

        LOG.info("STEP 4 - Click  \"User\" link and verify \"User Profile Page\" opens");

        siteProfileDashlet.clickSiteManager("fName1 lName1");

        Assert.assertTrue(userProfilePage.isUserProfilePageHeaderDisplayed());

    }
}
