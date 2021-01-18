package org.alfresco.share.site.members;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.members.AddSiteGroupsPage;
import org.alfresco.po.share.site.members.AddSiteGroupsPage.GroupRoles;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AddSiteGroupsTest extends ContextAwareWebTest
{
    private final String user1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
    private final String user2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
    private final String description = String.format("description%s", RandomData.getRandomAlphanumeric());
    private final String group = String.format("aGroup%s", RandomData.getRandomAlphanumeric());
    private final String group2 = String.format("aGroup2%s", RandomData.getRandomAlphanumeric());
    @Autowired
    MySitesDashlet mySitesDashlet;
    //@Autowired
    AddSiteUsersPage addSiteUsers;
    //@Autowired
    AddSiteGroupsPage addSiteGroups;
    //@Autowired
    SiteGroupsPage siteGroups;
   // @Autowired
    SiteUsersPage siteUsers;
    @Autowired
    Notification notification;
    private String user3 = String.format("User3%s", RandomData.getRandomAlphanumeric());
    private String siteName;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1, "lastName");
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2, "lastName");
        userService.create(adminUser, adminPassword, user3, password, user3 + domain, user3, "lastName");

        groupService.createGroup(adminUser, adminPassword, group);
        groupService.createGroup(adminUser, adminPassword, group2);
        groupService.addUserToGroup(adminUser, adminPassword, group, user2);
        groupService.addUserToGroup(adminUser, adminPassword, group, user3);
    }

    @AfterMethod
    public void tearDown()
    {
        cleanupAuthenticatedSession();
    }

    @AfterClass (alwaysRun = true)
    public void cleanup()
    {
        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        userService.delete(adminUser, adminPassword, user3);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user3);

        siteService.delete(adminUser, adminPassword, siteName);

    }


    @TestRail (id = "C2777")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addGroupWithManagerRole()
    {
        //precondition
        siteName = String.format("Site-C2777-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 1 - Navigate to Add Groups page for " + siteName);
        addSiteGroups.navigate(siteName);

        LOG.info("STEP 2 - Search for " + group + " in Site Groups page. Invite group and check that is added on invited list.");
        addSiteGroups.searchForGroup(group);
        assertTrue(addSiteGroups.isGroupReturned(group), "Group is returned at search.");
        addSiteGroups.addGroup(group);
        assertTrue(addSiteGroups.isGroupInvited(group), "Group is on invited list.");
        assertTrue(addSiteGroups.isRoleFilterValid(), "Set role filter values are not correct");

        LOG.info("STEP 3 - Set group role as Manager and add it to this site");
        addSiteGroups.setGroupRole(group, GroupRoles.Manager.toString());
        addSiteGroups.addGroups();
        assertEquals(notification.getDisplayedNotification(), "1 groups added, 0 failures", "Popup text is not correct.");

        LOG.info("STEP 4 - Check site members, verify that group is listed");
        addSiteGroups.goBackToSiteGroupsPage();
        assertTrue(siteGroups.isASiteMember(group), group + " is listed in site members.");
        assertTrue(siteGroups.isRoleSelected(GroupRoles.Manager.toString(), group), "Group has Manager role.");
        assertTrue(siteGroups.isRemoveButtonDisplayedForGroup(group));

        LOG.info("STEP 5 - Check that site is visible to user2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertTrue(mySitesDashlet.isSitePresent(siteName), "Site is visible for user: " + user2);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2778")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addGroupWithCollaboratorRole()
    {
        //precondition
        siteName = String.format("Site-C2778-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 1 - Navigate to Add Groups page for " + siteName);
        addSiteGroups.navigate(siteName);

        LOG.info("STEP 2 - Search for " + group + " in Site Groups page. Invite group and check that is added on invited list.");
        addSiteGroups.searchForGroup(group);
        assertTrue(addSiteGroups.isGroupReturned(group), "Group is returned at search.");
        addSiteGroups.addGroup(group);
        assertTrue(addSiteGroups.isGroupInvited(group), "Group is on invited list.");

        LOG.info("STEP 3 - Set group role as Collaborator and add it to this site");
        addSiteGroups.setGroupRole(group, GroupRoles.Collaborator.toString());
        addSiteGroups.addGroups();
        assertEquals(notification.getDisplayedNotification(), "1 groups added, 0 failures", "Popup text is not correct");

        LOG.info("STEP 4 - Check site members, verify that group is listed");
        addSiteGroups.goBackToSiteGroupsPage();
        assertTrue(siteGroups.isASiteMember(group), group + " is listed in site members.");
        assertTrue(siteGroups.isRoleSelected(GroupRoles.Collaborator.toString(), group), "Group has Collaborator role.");
        assertTrue(siteGroups.isRemoveButtonDisplayedForGroup(group));

        LOG.info("STEP 5 - Check that site is visible to user2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertTrue(mySitesDashlet.isSitePresent(siteName), "Site is visible for user: " + user2);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2779")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addGroupWithContributorRole()
    {
        //precondition
        siteName = String.format("Site-C2779-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 1 - Navigate to Add Groups page for " + siteName);
        addSiteGroups.navigate(siteName);

        LOG.info("STEP 2 - Search for " + group + " in Site Groups page. Invite group and check that is added on invited list.");
        addSiteGroups.searchForGroup(group);
        assertTrue(addSiteGroups.isGroupReturned(group), "Group is returned at search.");
        addSiteGroups.addGroup(group);
        assertTrue(addSiteGroups.isGroupInvited(group), "Group is on invited list.");

        LOG.info("STEP 3 - Set group role as Contributor and add it to this site");
        addSiteGroups.setGroupRole(group, GroupRoles.Contributor.toString());
        addSiteGroups.addGroups();
        assertEquals(notification.getDisplayedNotification(), "1 groups added, 0 failures", "Popup text is not correct");

        LOG.info("STEP 4 - Check site members, verify that group is listed");
        addSiteGroups.goBackToSiteGroupsPage();
        assertTrue(siteGroups.isASiteMember(group), group + " is listed in site members.");
        assertTrue(siteGroups.isRoleSelected(GroupRoles.Contributor.toString(), group), "Group has Contributor role.");
        assertTrue(siteGroups.isRemoveButtonDisplayedForGroup(group));

        LOG.info("STEP 5 - Check that site is visible to user2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertTrue(mySitesDashlet.isSitePresent(siteName), "Site is visible for user: " + user2);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2780")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addGroupWithConsumerRole()
    {
        //precondition
        siteName = String.format("Site-C2780-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 1 - Navigate to Add Groups page for " + siteName);
        addSiteGroups.navigate(siteName);

        LOG.info("STEP 2 - Search for " + group + " in Site Groups page. Invite group and check that is added on invited list.");
        addSiteGroups.searchForGroup(group);
        assertTrue(addSiteGroups.isGroupReturned(group), "Group is returned at search.");
        addSiteGroups.addGroup(group);
        assertTrue(addSiteGroups.isGroupInvited(group), "Group is on invited list.");

        LOG.info("STEP 3 - Set group role as Consumer and add it to this site");
        addSiteGroups.setGroupRole(group, GroupRoles.Consumer.toString());
        addSiteGroups.addGroups();
        assertEquals(notification.getDisplayedNotification(), "1 groups added, 0 failures", "Popup text is not correct");

        LOG.info("STEP 4 - Check site members, verify that group is listed");
        addSiteGroups.goBackToSiteGroupsPage();
        assertTrue(siteGroups.isASiteMember(group), group + " is listed in site members.");
        assertTrue(siteGroups.isRoleSelected(GroupRoles.Consumer.toString(), group), "Group has Consumer role.");
        assertTrue(siteGroups.isRemoveButtonDisplayedForGroup(group));

        LOG.info("STEP 5 - Check that site is visible to user2");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(user2, password);
        assertTrue(mySitesDashlet.isSitePresent(siteName), "Site is visible for user: " + user2);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2784")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyAddGroupsPage()
    {
        LOG.info("STEP 1 - Create valid user and site");
        siteName = String.format("Site-C2784-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 2 - Open Site Groups page, check that all elements are present");
        addSiteGroups.navigate(siteName);
        assertEquals(addSiteGroups.getDefaultSearchText(), "Find Groups to add to this site", "Default group search text is not correct");
        assertEquals(addSiteGroups.getGroupsListTitle(), "Add these Groups", "Group list title is not correct");
        assertTrue(addSiteGroups.isGroupSearchBoxDisplayed(), "Group search box is not displayed");
        assertTrue(addSiteGroups.isGroupSearchButtonDisplayed(), "Group search button is not displayed");
        assertTrue(addSiteGroups.isSetRolesButtonDisplayed(), "Set roles button is not displayed");
        assertFalse(addSiteGroups.isAddGroupsButtonEnabled(), "Add groups button should be disabled by default");
        assertTrue(addSiteGroups.isGoBackToSiteGroupsDisplayed(), "Go back to site groups is not displayed");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2812")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void returnToSiteGroupsPage()
    {
        LOG.info("STEP 1 - Create valid user and site");
        siteName = String.format("Site-C2812-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 2 - Go back to site groups page from add groups page, check proper page is opened");
        addSiteGroups.navigate(siteName);
        addSiteGroups.goBackToSiteGroupsPage();
//        assertTrue(addSiteGroups.getCurrentUrl().contains("site-groups"), "Site groups page should be opened");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2785")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchForGroups()
    {
        LOG.info("STEP 1 - Create valid user and site");
        siteName = String.format("Site-C2785-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 3 - Search for empty groups");
        addSiteGroups.navigate(siteName);
        addSiteGroups.searchForGroup("");
        assertEquals(notification.getDisplayedNotification(), "Enter at least 1 character(s)", "Popup text is not correct");
        notification.waitUntilNotificationDisappears();

        LOG.info("STEP 4 - Search for random strings");
        addSiteGroups.searchForGroup("!~@#$%^&*()_{}:\"");
        getBrowser().waitInSeconds(1);
        assertEquals(addSiteGroups.getSearchText(), "No groups found", "Search results are not correct");

        LOG.info("STEP 5 - Search for long char sequence, larger than 255 chars");
        addSiteGroups.searchForGroup(RandomStringUtils.randomAlphanumeric(260));
        getBrowser().waitInSeconds(1);
        assertEquals(addSiteGroups.getSearchText(), "No groups found", "Search results are not correct");
        assertEquals(addSiteGroups.getSearchBoxContent().length(), 255, "Search box should allow only 255 chars");

        LOG.info("STEP 6 - Search for created groups");
        addSiteGroups.searchForGroup("aGroup");
        assertTrue(addSiteGroups.isGroupReturned(group), "Group was not found");
        assertTrue(addSiteGroups.isGroupReturned(group2), "Group was not found");

        LOG.info("STEP 7 - Search for second group");
        addSiteGroups.searchForGroup(group2);
        assertTrue(addSiteGroups.isGroupReturned(group2), "Group was not found");
        assertFalse(addSiteGroups.isGroupReturned(group), "Group should not be returned");
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2786")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlySiteManagerCanAddGroups()
    {
        String userManager = String.format("UserManager%s", RandomData.getRandomAlphanumeric());
        String userCollaborator = String.format("UserCollaborator%s", RandomData.getRandomAlphanumeric());
        String userContributor = String.format("UserContributor%s", RandomData.getRandomAlphanumeric());
        String userConsumer = String.format("UserConsumer%s", RandomData.getRandomAlphanumeric());
        String groupManager = String.format("GroupManager%s", RandomData.getRandomAlphanumeric());
        String groupCollaborator = String.format("GroupCollaborator%s", RandomData.getRandomAlphanumeric());
        String groupContributor = String.format("GroupContributor%s", RandomData.getRandomAlphanumeric());
        String groupConsumer = String.format("GroupConsumer%s", RandomData.getRandomAlphanumeric());

        LOG.info("STEP 1 - Create 4 users and one site which is added by userManager");
        siteName = String.format("Site-C2786-%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userManager, password, userManager + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, userCollaborator, password, userCollaborator + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, userConsumer, password, userConsumer + domain, "firstName", "lastName");
        siteService.create(userManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);

        LOG.info("STEP 2 - Create 4 groups and add created users to them");
        groupService.createGroup(adminUser, adminPassword, groupManager);
        groupService.addUserToGroup(adminUser, adminPassword, groupManager, userManager);
        groupService.createGroup(adminUser, adminPassword, groupCollaborator);
        groupService.addUserToGroup(adminUser, adminPassword, groupCollaborator, userCollaborator);
        groupService.createGroup(adminUser, adminPassword, groupContributor);
        groupService.addUserToGroup(adminUser, adminPassword, groupContributor, userContributor);
        groupService.createGroup(adminUser, adminPassword, groupConsumer);
        groupService.addUserToGroup(adminUser, adminPassword, groupConsumer, userConsumer);

        LOG.info("STEP 3 - Add groups to the site with custom roles");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, groupManager, "SiteManager");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, groupCollaborator, "SiteCollaborator");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, groupContributor, "SiteContributor");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, groupConsumer, "SiteConsumer");

        LOG.info("STEP 4 - Login as user collaborator and check that user is not able to add groups");
        setupAuthenticatedSession(userCollaborator, password);
        siteGroups.navigate(siteName);
        assertFalse(siteGroups.isAddGroupsButtonDisplayed(), "Add groups button should not be visible for Collaborator user");

        LOG.info("STEP 5 - Login as user contributor and check that user is not able to add groups");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);
        siteGroups.navigate(siteName);
        assertFalse(siteGroups.isAddGroupsButtonDisplayed(), "Add groups button should not be visible for Contributor user");

        LOG.info("STEP 6 - Login as user consumer and check that user is not able to add groups");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userConsumer, password);
        siteGroups.navigate(siteName);
        assertFalse(siteGroups.isAddGroupsButtonDisplayed(), "Add groups button should not be visible for Consumer user");

        LOG.info("STEP 7 - Login as user manager and check that user is able to add groups");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);
        siteGroups.navigate(siteName);
        assertTrue(siteGroups.isAddGroupsButtonDisplayed(), "Add groups button should be visible for Manager user");

        siteService.delete(adminUser, adminPassword, siteName);
        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);

        userService.delete(adminUser, adminPassword, userCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userCollaborator);

        userService.delete(adminUser, adminPassword, userConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userConsumer);

        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);

    }

    @TestRail (id = "C2846")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void groupMembersAreAddedToUsersList()
    {
        LOG.info("STEP 1 - Create 3 users and one site. Login with user1");
        siteName = String.format("Site-C2846-%s", RandomData.getRandomAlphanumeric());
        siteService.create(user1, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(user1, password);

        LOG.info("STEP 2 - Search for group in Site Groups page. Invite group and check that is added on invited list");
        addSiteGroups.navigate(siteName);
        addSiteGroups.addGroupWorkflow(group, GroupRoles.Manager.toString());
        assertEquals(notification.getDisplayedNotification(), "1 groups added, 0 failures", "Popup text is not correct");
        siteUsers.navigate(siteName);

        LOG.info("STEP 3 - Check that group users are added to members list page and remove button is disabled");
        assertTrue(siteUsers.isASiteMember(user2 + " lastName"), "User should be included in site members list");
        assertTrue(siteUsers.isASiteMember(user3 + " lastName"), "User should be included in site members list");
        assertFalse(siteUsers.isRemoveButtonEnabled(user2), "Remove button should be disabled for group user");
        assertFalse(siteUsers.isRemoveButtonEnabled(user3), "Remove button should be disabled for group user");

        LOG.info("STEP 5 - Search for user already added, check that Select button is not enabled");
        siteUsers.goToAddUsersPage();
        addSiteUsers.searchForUser(user2);
        assertFalse(addSiteUsers.isSelectUserButtonEnabled(user2), "Select button should be disabled for already added user");

        siteService.delete(adminUser, adminPassword, siteName);

    }
}