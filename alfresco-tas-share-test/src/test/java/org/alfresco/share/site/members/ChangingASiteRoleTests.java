package org.alfresco.share.site.members;

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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    private String userNameManager;
    private String testSiteName;
    private String testUserName;
    private String groupName;

    @BeforeClass(alwaysRun = true)
    public void dataPreparation() throws Exception {
        UserModel manager = dataUser.createRandomTestUser();
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite =  dataSite.usingUser(manager).createPublicRandomSite();
        dataUser.addUserToSite(testUser, testSite, UserRole.SiteManager);
        GroupModel testGroup = dataGroup.createRandomGroup();
        dataGroup.addUserToGroup(testGroup);
        userNameManager=manager.getUsername();
        testSiteName = testSite.getTitle();
        testUserName = testUser.getUsername();
        groupName = testGroup.getDisplayName();
    }

    @TestRail(id = "C2835")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagerIsAbleToChangeRoles() throws DataPreparationException {
        UserModel manager = dataUser.createRandomTestUser();
        UserModel testCollaboratorUser = dataUser.createRandomTestUser();
        SiteModel testSite =  dataSite.usingUser(manager).createPublicRandomSite();
        dataUser.addUserToSite(testCollaboratorUser, testSite, UserRole.SiteCollaborator);
        setupAuthenticatedSession(manager.getUsername(), password);
        LOG.info("Step 1: Navigate to site and click 'Site Members' link");
        siteUsersPage.navigate(testSite.getTitle());
        LOG.info("Step 2: Change the role of 'userCollaborator' from 'Collaborator' to 'Contributor'");
        assertEquals(testCollaboratorUser.getUserRole().toString(), "SiteCollaborator", "expected user role: ");
        siteUsersPage.changeRoleForMember("Contributor", testCollaboratorUser.getUsername());
        assertEquals(testCollaboratorUser.getUserRole().toString(), "Contributor ▾",  " has new role=");
        siteDashboardPage.navigate(testSiteName);
        assertEquals(siteMembersDashlet.getMemberRole(testCollaboratorUser.getUsername()), "Contributor",
                "Successfully changed role of user to Contributor");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2836")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAGroup()   {
        setupAuthenticatedSession(testUserName, password);
        LOG.info("Preconditions: Add the userTest in new created group. Add the group to site with 'Manager' role");
        groupService.inviteGroupToSite(adminUser, adminPassword, testSiteName, groupName, "SiteManager");
        LOG.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(testSiteName);
        assertTrue(siteUsersPage.isASiteMember(testUserName + " FirstName LN-" + testUserName), testUserName+" is not a member of " + testSiteName);
        assertEquals(siteUsersPage.getRole(testUserName), "Manager",  testUserName +" has role=");
        LOG.info("Step 2: Click on 'Groups' link");
        siteUsersPage.openSiteGroupsPage();
        siteGroupsPage.typeSearchGroup(groupName);
        siteGroupsPage.clickSearch();
        assertTrue(siteGroupsPage.isASiteMember(groupName), "Expected group '" + groupName + "' is not present on the page.");
        LOG.info("Step 3: Change the current role to 'Consumer'");
        siteGroupsPage.changeRoleForMember("Consumer", groupName);
        LOG.info("Step 4: Click on 'Users' link and check the role of userTest has changed to 'Consumer'");
        siteGroupsPage.openSiteUsersPage();
        assertTrue(siteUsersPage.isASiteMember(testUserName + " FirstName LN-" + testUserName));
        assertEquals(siteUsersPage.getRole(testUserName), "Consumer", testUserName + " has role=");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2837")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAnUser() throws DataPreparationException {
        LOG.info("Preconditions: Add the userTest to the created site with 'Manager' role");
        UserModel manager = dataUser.createRandomTestUser();
        UserModel testUser = dataUser.createRandomTestUser();
        SiteModel testSite =  dataSite.usingUser(manager).createPublicRandomSite();
        dataUser.addUserToSite(testUser,testSite,UserRole.SiteManager);
        setupAuthenticatedSession(manager.getUsername(), password);
        LOG.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(testSite.getTitle());
        assertTrue(siteUsersPage.isASiteMember(testUser.getUsername() + " FirstName LN-" + testUser.getUsername()), testUser.getUsername()+" is not a site member of " + testSite.getTitle());
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Manager ▾", testUser.getUsername() + " has role=");
        LOG.info("Step 2: Change the current role to 'Consumer'");
        siteUsersPage.changeRoleForMember("Consumer", testUser.getUsername());
        assertEquals(siteUsersPage.getRole(testUser.getUsername()), "Consumer ▾", testUser.getUsername() + " has role=");
        cleanupAuthenticatedSession();
    }
}