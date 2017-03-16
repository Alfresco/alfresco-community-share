package org.alfresco.share.site.members;

import org.alfresco.common.DataUtil;
import org.alfresco.common.UserData;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.alfresco.api.entities.Role;
import org.springframework.social.alfresco.api.entities.Site.Visibility;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ChangingASiteRoleTests extends ContextAwareWebTest
{
    private String userManager;
    private String userTest;
    private String siteName;
    private String groupName;

    @Autowired
    DataUtil dataUtil;

    @Autowired
    SiteGroupsPage siteGroupsPage;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteMembersDashlet siteMembersDashlet;

    @Autowired
    SiteUsersPage siteUsersPage;

    @TestRail(id = "C2835")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagerIsAbleToChangeRoles()
    {
        userManager = "Manager-C2835-" + DataUtil.getUniqueIdentifier();
        userTest = "User2-C2835-" + DataUtil.getUniqueIdentifier();
        siteName = "Site-C2835-" + DataUtil.getUniqueIdentifier();
        groupName = "Group-C2835-" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userManager, DataUtil.PASSWORD, userManager + "@tests.com", userManager, userManager);
        userService.create(adminUser, adminPassword, userTest, DataUtil.PASSWORD, userTest + "@tests.com", userTest, userTest);
        siteService.create(userManager, password, domain, siteName, siteName, Visibility.MODERATED);

        logger.info("Preconditions: Create userCollaborator, userContributor and userConsumer");
        List<UserData> usersData = dataUtil.createUsersWithRoles(Arrays.asList("SiteCollaborator", "SiteContributor", "SiteConsumer"), userManager, siteName);

        for (UserData user : usersData)
        {
            logger.info("Login as " + user.getUserRole());
            cleanupAuthenticatedSession();
            setupAuthenticatedSession(user.getUserName(), DataUtil.PASSWORD);

            logger.info("Open 'Site Members' page for the site");
            siteUsersPage.navigate(siteName);

            logger.info("Try to change the role of any user from the list");
            Assert.assertFalse(siteUsersPage.isRoleButtonDisplayed(usersData.get(0).getUserName()), "The role button shouldn't be displayed");
        }

        logger.info("Login as userManager");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, DataUtil.PASSWORD);

        logger.info("Navigate to site and click 'Site Members' link");
        siteUsersPage.navigate(siteName);

        logger.info("Change the role of 'userCollaborator' from 'Collaborator' to 'Contributor'");
        assertEquals(siteUsersPage.getRole(usersData.get(0).getUserName()), "Collaborator ▾", usersData.get(0).getUserName() + " has role=");
        siteUsersPage.changeRoleForMember("Contributor", usersData.get(0).getUserName());
        assertEquals(siteUsersPage.getRole(usersData.get(0).getUserName()), "Contributor ▾", usersData.get(0).getUserName() + " has new role=");
        siteDashboardPage.navigate(siteName);
        assertEquals(siteMembersDashlet.getMemberRole(usersData.get(0).getUserName()), "Contributor",
                "Successfully changed role of user " + usersData.get(0).getUserName() + " to Contributor");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2836")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAGroup()
    {
        userManager = "Manager-C2836-" + DataUtil.getUniqueIdentifier();
        userTest = "User2-C2836-" + DataUtil.getUniqueIdentifier();
        siteName = "site-C2836-" + DataUtil.getUniqueIdentifier();
        groupName = "Group-C2836-" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userManager, DataUtil.PASSWORD, userManager + "@tests.com", userManager, userManager);
        userService.create(adminUser, adminPassword, userTest, DataUtil.PASSWORD, userTest + "@tests.com", userTest, userTest);
        siteService.create(userManager, password, domain, siteName, siteName, Visibility.MODERATED);
        setupAuthenticatedSession(userTest, DataUtil.PASSWORD);

        logger.info("Preconditions: Add the userTest in new created group. Add the group to site with 'Manager' role");
        groupService.createGroup(adminUser, adminPassword, groupName);
        groupService.addUserToGroup(adminUser, adminPassword, groupName, userTest);
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, groupName, "SiteManager");

        logger.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(userTest + " " + userTest));
        assertEquals(siteUsersPage.getRole(userTest), "Manager", userTest + " has role=");

        logger.info("Step 2: Click on 'Groups' link");
        siteUsersPage.openSiteGroupsPage();
        siteGroupsPage.typeSearchGroup(groupName);
        siteGroupsPage.clickSearch();
        assertTrue(siteGroupsPage.isASiteMember(groupName), "Expected group '" + groupName + "' is not present on the page.");

        logger.info("Step 3: Change the current role to 'Consumer'");
        siteGroupsPage.changeRoleForMember("Consumer", groupName);
        // assertEquals(notification.getDisplayedNotification(), "Successfully changed role of group " + groupName + " to Consumer");

        logger.info("Step 3: Click on 'Users' link and check the role of userTest has changed to 'Consumer'");
        siteGroupsPage.openSiteUsersPage();
        assertTrue(siteUsersPage.isASiteMember(userTest + " " + userTest));
        assertEquals(siteUsersPage.getRole(userTest), "Consumer", userTest + " has role=");

        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C2837")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void changeRoleForAnUser()
    {
        userManager = "Manager-C2837-" + DataUtil.getUniqueIdentifier();
        userTest = "User2-C2837-" + DataUtil.getUniqueIdentifier();
        siteName = "site-C2837-" + DataUtil.getUniqueIdentifier();
        groupName = "Group" + DataUtil.getUniqueIdentifier();
        userService.create(adminUser, adminPassword, userManager, DataUtil.PASSWORD, userManager + "@tests.com", userManager, userManager);
        userService.create(adminUser, adminPassword, userTest, DataUtil.PASSWORD, userTest + "@tests.com", userTest, userTest);
        siteService.create(userManager, password, domain, siteName, siteName, Visibility.MODERATED);

        logger.info("Preconditions: Add the userTest to the created site with 'Manager' role");
        userService.inviteUserToSiteAndAccept(userManager, password, userTest, siteName, Role.SiteManager.toString());

        setupAuthenticatedSession(userManager, DataUtil.PASSWORD);

        logger.info("Step 1: Open 'Site Members' page for the site");
        siteUsersPage.navigate(siteName);
        assertTrue(siteUsersPage.isASiteMember(userTest + " " + userTest));
        assertEquals(siteUsersPage.getRole(userTest), "Manager ▾", userTest + " has role=");

        logger.info("Step 2: Change the current role to 'Consumer'");
        siteUsersPage.changeRoleForMember("Consumer", userTest);
        // Assert.assertEquals(notification.getDisplayedNotification(), "Successfully changed role of user " + userTest + " to Consumer");
        assertEquals(siteUsersPage.getRole(userTest), "Consumer ▾", userTest + " has role=");

        cleanupAuthenticatedSession();
    }
}