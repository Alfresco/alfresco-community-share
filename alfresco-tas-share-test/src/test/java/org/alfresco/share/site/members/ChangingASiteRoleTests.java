package org.alfresco.share.site.members;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataGroup;
import org.alfresco.utility.data.DataSite;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ChangingASiteRoleTests extends ContextAwareWebTest
{
    @Autowired
    DataUser dataUser;

    @Autowired
    DataSite dataSite;

    @Autowired
    DataGroup dataGroup;

    @Autowired
    SiteGroupsPage siteGroupsPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteMembersDashlet siteMembersDashlet;

    @Autowired
    SiteUsersPage siteUsersPage;

    UserModel manager, testUser;

    @BeforeClass (alwaysRun = true)
    public void dataPreparation() throws Exception
    {
        manager = dataUser.createRandomTestUser();
        testUser = dataUser.createRandomTestUser();
        setupAuthenticatedSession(manager.getUsername(), password);
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, manager.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + manager.getUsername());

        userService.delete(adminUser, adminPassword, testUser.getUsername());
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + testUser.getUsername());

    }

    @TestRail (id = "C2835")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagerIsAbleToChangeRoles() throws DataPreparationException
    {
        SiteModel testSite = dataSite.usingUser(manager).createPublicRandomSite();
        dataUser.addUserToSite(testUser, testSite, UserRole.SiteCollaborator);

        LOG.info("Step 1: Navigate to site and click 'Site Members' link");
        siteUsersPage.navigate(testSite.getTitle());
        LOG.info("Step 2: Change the role of 'userCollaborator' from 'Collaborator' to 'Contributor'");
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Collaborator ▾", "expected user role: ");
        siteUsersPage.changeRoleForMember("Contributor", testUser.getUsername());
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Contributor ▾", " has new role=");
        siteDashboardPage.navigate(testSite.getTitle());
        assertEquals(siteMembersDashlet.getMemberRole(testUser.getUsername()), "Contributor",
            "Successfully changed role of user to Contributor");

        siteService.delete(adminUser, adminPassword, testSite.getTitle());
    }

    @TestRail (id = "C2836")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAGroup() throws DataPreparationException
    {
        SiteModel testSite = dataSite.usingUser(manager).createPublicRandomSite();
        GroupModel testGroup = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.addListOfUsersToGroup(testGroup, testUser);
        groupService.inviteGroupToSite(adminUser, adminPassword, testSite.getTitle(), testGroup.getDisplayName(), "SiteManager");
        LOG.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(testSite.getTitle());
        assertTrue(siteUsersPage.isSiteMember(testUser));
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Manager ▾", testUser.getUsername() + " has role=");
        LOG.info("Step 2: Click on 'Groups' link");
        siteUsersPage.openSiteGroupsPage();
        siteGroupsPage.typeSearchGroup(testGroup.getDisplayName());
        siteGroupsPage.clickSearch();
        assertTrue(siteGroupsPage.isASiteMember(testGroup.getDisplayName()), "Expected group '" + testGroup.getDisplayName() + "' is not present on the page.");
        LOG.info("Step 3: Change the current role to 'Consumer'");
        siteGroupsPage.changeRoleForMember("Consumer", testGroup.getDisplayName());
        LOG.info("Step 4: Click on 'Users' link and check the role of userTest has changed to 'Consumer'");
        siteGroupsPage.openSiteUsersPage();
        assertTrue(siteUsersPage.isASiteMember(testUser.getFirstName() + " " + testUser.getLastName()));
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Consumer ▾", testUser.getUsername() + " has role in " + testGroup.getDisplayName());

        siteService.delete(adminUser, adminPassword, testSite.getTitle());
    }

    @TestRail (id = "C2837")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAnUser() throws DataPreparationException
    {
        LOG.info("Preconditions: Add the userTest to the created site with 'Manager' role");
        SiteModel testSite = dataSite.usingUser(manager).createPublicRandomSite();
        dataUser.addUserToSite(testUser, testSite, UserRole.SiteManager);
        LOG.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(testSite.getTitle());
        assertTrue(siteUsersPage.isSiteMember(testUser), testUser.getUsername() + " is not a site member of " + testSite.getTitle());
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Manager ▾", testUser.getUsername() + " has role=");
        LOG.info("Step 2: Change the current role to 'Consumer'");
        siteUsersPage.changeRoleForMember("Consumer", testUser.getUsername());
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Consumer ▾", testUser.getUsername() + " has role=");
        siteService.delete(adminUser, adminPassword, testSite.getTitle());
    }
}