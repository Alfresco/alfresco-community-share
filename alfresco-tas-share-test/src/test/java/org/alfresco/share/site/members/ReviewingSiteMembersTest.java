package org.alfresco.share.site.members;

import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.testng.annotations.Test;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.alfresco.common.DataUtil;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.annotations.BeforeClass;

/// **
// * Created by Gabriela Virna on 7/01/2016.
// */
//
public class ReviewingSiteMembersTest extends ContextAwareWebTest
{
    @Autowired
    SiteUsersPage siteUsersPage;

    private String user1 = "testUser1" + DataUtil.getUniqueIdentifier();
    private String user2 = "testUser2" + DataUtil.getUniqueIdentifier();
    private String user3 = "testUser3" + DataUtil.getUniqueIdentifier();
    private String siteName = "testSite" + DataUtil.getUniqueIdentifier();

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, user1);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, user2);
        userService.create(adminUser, adminPassword, user3, password, user3 + domain, user3, user3);
        siteService.create(user1, password, domain, siteName, "description", Site.Visibility.PUBLIC);
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");
        userService.createSiteMember(user1, password, user3, siteName, "SiteCollaborator");
        setupAuthenticatedSession(user1, password);
    }

    @TestRail(id = "C2816")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })

    public void listAllSiteMembers()
    {
        LOG.info("STEP 1: Go to the created site. Navigate to 'Site Members' page of site '" + siteName + "'.");
        siteUsersPage.navigate(siteName);

        LOG.info("STEP2: Check that all site members are displayed.",
                "Expected Result: '" + user1 + "' with 'Manager' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user1 + " " + user1), "Expected user '" + user1 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Manager", user1), "User '" + user1 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user1), "User '" + user1 + "' is expected to have 'Remove' button.");
        LOG.info("STEP2:Verify listed members.", "Expected Result: '" + user2 + "' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user2 + " " + user2), "Expected user '" + user2 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Collaborator", user2), "User '" + user1 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user2), "User '" + user1 + "' is expected to have 'Remove' button.");

        LOG.info("STEP2:Verify listed members.", "Expected Result: '" + user3 + "' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user3 + " " + user3), "Expected user '" + user3 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Collaborator", user3), "User '" + user3 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user3), "User '" + user3 + "' is expected to have 'Remove' button.");

        LOG.info("STEP3: Click on any user from the list (e.g.: 'testUser2')");
        siteUsersPage.clickUser(user2 + " " + user2);
        assertTrue(getBrowser().getCurrentUrl().endsWith("/user/" + user2 + "/profile"));
    }

    @TestRail(id = "C2817")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchForMembersUsingPartialName()
    {
        LOG.info("Navigate to 'Site Members' page of site '" + siteName + "'.");
        siteUsersPage.navigate(siteName);

        LOG.info("Enter 'test' string in the search box and click 'Search' button.");
        siteUsersPage.searchForSiteMembers("test");
        siteUsersPage.clickSearch();

        LOG.info("STEP 2:Check that all site members are displayed.",
                "Expected Result: '" + user1 + "' with 'Manager' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user1 + " " + user1), "Expected user '" + user1 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Manager", user1), "User '" + user1 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user1), "User '" + user1 + "' is expected to have 'Remove' button.");
        LOG.info("STEP 2:Verify listed members.", "Expected Result: '" + user2 + "' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user2 + " " + user2), "Expected user '" + user2 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Collaborator", user2), "User '" + user1 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user2), "User '" + user1 + "' is expected to have 'Remove' button.");
        LOG.info("STEP 2:Verify listed members.", "Expected Result: '" + user3 + "' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteUsersPage.isASiteMember(user3 + " " + user3), "Expected user '" + user3 + "' is present on the page.");
        assertTrue(siteUsersPage.isRoleSelected("Collaborator", user3), "User '" + user3 + "' is expected to have 'Collaborator' role.");
        assertTrue(siteUsersPage.isRemoveButtonEnabled(user3), "User '" + user3 + "' is expected to have 'Remove' button.");

        LOG.info("STEP 3: Click on any user from the list (e.g.: 'testUser2')");
        siteUsersPage.clickUser(user2 + " " + user2);
        assertTrue(getBrowser().getCurrentUrl().endsWith("/user/" + user2 + "/profile"));
    }

    @TestRail(id = "C2818")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchForMembersUsingFullName()
    {
        LOG.info("STEP 1: Navigate to 'Site Members' page of site '" + siteName + "'.");
        siteUsersPage.navigate(siteName);

        LOG.info("STEP 2: Enter '" + user2 + "' string in the search box and click 'Search' button.");
        siteUsersPage.searchForSiteMembers(user2);
        siteUsersPage.clickSearch();

        LOG.info("Only: '" + user2 + "' is displayed");
        assertFalse(siteUsersPage.isASiteMember(user1 + " " + user1), "Member '" + user1 + "' is not expected to be present on the page.");
        assertTrue(siteUsersPage.isASiteMember(user2 + " " + user2), "Expected member '" + user2 + "' is present on the page.");
        assertFalse(siteUsersPage.isASiteMember(user3 + " " + user3), "Member '" + user3 + "' is not expected to be present on the page.");

        LOG.info("STEP 3: Click on  'testUser2'.");
        siteUsersPage.clickUser(user2 + " " + user2);
        assertTrue(getBrowser().getCurrentUrl().endsWith("/user/" + user2 + "/profile"));
    }
}
