package org.alfresco.share.site.siteDashboard;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.Dashlets;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Laura.Capsa
 */
public class SiteMembersTest extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteMembersDashlet siteMembersDashlet;
    String userName1 = "user1" + RandomData.getRandomAlphanumeric();
    String userName2 = "user2" + RandomData.getRandomAlphanumeric();
    String userName3 = "user3" + RandomData.getRandomAlphanumeric();
    String siteNameShare = "siteName" + RandomData.getRandomAlphanumeric();
    private String userName = String.format("profileUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName-C2799-%s", RandomData.getRandomAlphanumeric());
    private String description = String.format("description%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, "First", "User");
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, "Second", "User");
        userService.create(adminUser, adminPassword, userName3, password, userName3 + domain, "Third", "User");
        siteService.create(adminUser, adminPassword, domain, siteNameShare, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(adminUser, adminPassword, userName1, siteNameShare, "SiteCollaborator");
        userService.createSiteMember(adminUser, adminPassword, userName2, siteNameShare, "SiteConsumer");
        userService.createSiteMember(adminUser, adminPassword, userName3, siteNameShare, "SiteContributor");
        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        assertEquals(siteMembersDashlet.getDashletTitle(), "Site Members", "Dashlet title-");
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2799")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void oneUserAdded()
    {
        LOG.info("STEP1: Verify \"Site Members\" dashlet");
        assertEquals(siteMembersDashlet.assertDashletHelpIconIsDisplayed(Dashlet.DashletHelpIcon.SITE_MEMBERS), true, "'Help' icon is displayed.");

        assertEquals(siteMembersDashlet.isAddUsersLinkDisplayed(), true, "'Add users' link is displayed.");
        assertEquals(siteMembersDashlet.getAddUsersLinkText(), language.translate("siteMembers.addUsers"), "'Add Users' link text-");

        assertEquals(siteMembersDashlet.isAllMembersLinkDisplayed(), true, "'All members' link is displayed.");
        assertEquals(siteMembersDashlet.getAllMembersLinkText(), language.translate("siteMembers.allMembers"), "'All Members' link text-");

        assertEquals(siteMembersDashlet.isPaginationDisplayed(), true, "Section containing pagination is displayed.");
        assertEquals(siteMembersDashlet.getPaginationText(), language.translate("dashlet.defaultPagination"), "Pagination text by default-");

        assertEquals(siteMembersDashlet.getEmptyMembersListMessage(), language.translate("siteMembers.emptyMembers"), "Site members list message- ");
        assertEquals(siteMembersDashlet.isUserInfoDisplayed("username"), true, "User's name info is displayed.");
        assertEquals(siteMembersDashlet.isUserInfoDisplayed("role"), true, "User's role info is displayed.");

        LOG.info("STEP2: Click \"?\" icon");
        siteMembersDashlet.clickOnHelpIcon(Dashlet.DashletHelpIcon.SITE_MEMBERS);
        assertEquals(siteMembersDashlet.getHelpBalloonMessage(), language.translate("siteMembers.help"), "Help balloon message.");

        LOG.info("STEP3: Click \"X\" icon");
        siteMembersDashlet.closeHelpBalloon();
        assertEquals(siteMembersDashlet.isHelpBalloonDisplayed(), false, "Help balloon is closed.");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SITE_DASHBOARD })
    public void selectSiteMember()
    {
        String user1 = "First " + "User";
        String user2 = "Second " + "User";
        String user3 = "Third " + "User";
        LOG.info("Step 1: Navigate to Site Dashboard Page and check that Site Members dashlet is present");
        siteDashboardPage.navigate(siteNameShare);
        Assert.assertTrue(siteDashboardPage.isDashletAddedInPosition(Dashlets.SITE_MEMBERS, 1, 1), "Site Members dashlet is not displayed");

        LOG.info("Step 2: Select Site Member");
        siteMembersDashlet.renderedPage();
        Assert.assertTrue(siteMembersDashlet.isMemberNameDisplayed(user1));
        Assert.assertEquals(siteMembersDashlet.getMemberRole(user1), "Collaborator", "User roles is not collaborator");
        siteMembersDashlet.selectSiteMember(user1);
        Assert.assertEquals(getBrowser().getTitle(), language.translate("userProfilePage.pageTitle"), String.format("User is not redirected to user profile page for %s", userName1));

        LOG.info("Step 3: Check members details");
        siteDashboardPage.navigate(siteNameShare);
        Assert.assertTrue(siteMembersDashlet.isMemberNameDisplayed(user2), String.format("User %s is not displayed", user2));
        Assert.assertEquals(siteMembersDashlet.getMemberRole(user2), "Consumer", "User roles is not consumer");
        Assert.assertTrue(siteMembersDashlet.isMemberNameDisplayed(user3), String.format("User %s is not displayed", user3));
        Assert.assertEquals(siteMembersDashlet.getMemberRole(user3), "Contributor", "User roles is not contributor");
    }

    @Test (groups = { TestGroup.SHARE, "Acceptance", TestGroup.SITE_DASHBOARD })
    public void selectAllMembers()
    {
        LOG.info("Step 1: Navigate to site dashboard and click All Members");
        siteDashboardPage.navigate(siteNameShare);
        siteMembersDashlet.renderedPage();
        siteMembersDashlet.clickAllMembersButton("All Members");
        Assert.assertEquals(getBrowser().getTitle(), language.translate("siteMembersPage.pageTitle"), "User has not been redirected to Site Members Page");
    }
}
