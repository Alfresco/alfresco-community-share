package org.alfresco.share.site.siteDashboard;

import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Laura.Capsa
 */
public class SiteMembersTest extends ContextAwareWebTest
{
    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteMembersDashlet siteMembersDashlet;

    private String userName = String.format("profileUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("SiteName-C2799-%s",RandomData.getRandomAlphanumeric());
    private String description = String.format("description%s",RandomData.getRandomAlphanumeric());

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        siteService.create(userName, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        setupAuthenticatedSession(userName, password);
        siteDashboardPage.navigate(siteName);
        assertEquals(siteMembersDashlet.getDashletTitle(), "Site Members", "Dashlet title-");
    }

    @AfterClass(alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser,adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser,adminPassword,siteName );

    }

    @TestRail(id = "C2799")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void oneUserAdded()
    {
        LOG.info("STEP1: Verify \"Site Members\" dashlet");
        assertEquals(siteMembersDashlet.isHelpIconDisplayed(Dashlet.DashletHelpIcon.SITE_MEMBERS), true, "'Help' icon is displayed.");

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
        assertEquals(siteMembersDashlet.isBalloonDisplayed(), false, "Help balloon is closed.");
    }
}
