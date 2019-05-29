package org.alfresco.share.site.members;

import java.util.List;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.site.members.SiteUsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Argint Alex on 7/4/2016.
 */

public class RemoveMembersOrGroupsTest extends ContextAwareWebTest
{
    @Autowired
    SiteUsersPage siteUsers;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    SiteGroupsPage siteGroupsPage;

    @Autowired
    Notification notification;

    private String user1;
    private String user1FirstName;
    private String user1LastName;
    private String user2;
    private String user2FirstName;
    private String user2LastName;
    private String siteName;
    private String uniqueIdentifier;
    private String description;
    private String group1;
    private String user2CompleteName;
    private String user1CompleteName;

    public void setup(String id, SiteService.Visibility visibility) throws DataPreparationException
    {
        super.setup();

        uniqueIdentifier = "-" + id + "-" + RandomData.getRandomAlphanumeric();
        siteName = "siteName" + uniqueIdentifier;
        user1 = "User1" + uniqueIdentifier;
        user2 = "User2" + uniqueIdentifier;
        user2FirstName = "user2";
        user1FirstName = "user1";
        description = "description" + uniqueIdentifier;
        user2LastName = id;
        user1LastName = id;
        user2CompleteName = user2FirstName + " " + user2LastName;
        user1CompleteName = user1FirstName + " " + user1LastName;

        userService.create(adminUser, adminPassword, user1, password, user1 + domain, user1FirstName, user1LastName);
        userService.create(adminUser, adminPassword, user2, password, user2 + domain, user2FirstName, user2LastName);
        siteService.create(user1, password, domain, siteName, description, visibility);
        setupAuthenticatedSession(user1, password);

    }


    @TestRail (id = "C2882")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteMemberFromPublicSiteTest() throws DataPreparationException
    {
        LOG.info("Starting test C2882");
        setup("C2882", SiteService.Visibility.PUBLIC);
        // preconditions
        userService.createSiteMember(user1, password, user2, siteName, "SiteCollaborator");
        LOG.info("Adding " + user2 + "as " + siteName + " collaborator");

        LOG.info("Step 1 logging in as user1 and verifying the users from site members!");
        setupAuthenticatedSession(user1, password);
        siteUsers.navigate(siteName);
        // assert if user 1 is present
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Manager", user1CompleteName), "User1 is not manager for this site");
        // assert if user 2 is present
        Assert.assertTrue(siteUsers.isRoleSelected("Collaborator", user2CompleteName), "User2 is not collaborator for this site");

        LOG.info("Step 2 removing User2 as a collaborator from the site");
        if (siteUsers.isRemoveButtonEnabled(user2CompleteName))
            siteUsers.removeUser(user2CompleteName);
        // verify the notification message
        Assert.assertEquals(notification.getDisplayedNotification(), "Successfully removed user " + user2);
        Assert.assertFalse(siteUsers.isASiteMember(user2), "User 2 is not removed from site members list");

        LOG.info("Step 3 logout as user 1 and login as user 2");
        setupAuthenticatedSession(user2, password);

        LOG.info("Step 4 opening site dashboard page");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Join Site"), "Join site text not present in site configuration");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2883")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteGroupTest() throws DataPreparationException
    {

        LOG.info("Starting test C2883");

        // preconditions
        setup("C2883", SiteService.Visibility.PUBLIC);
        group1 = "testGroup" + uniqueIdentifier;

        LOG.info("Created site " + siteName + " with user " + user1 + "as manager");
        groupService.createGroup(adminUser, adminPassword, group1);

        LOG.info("Created group with name " + group1);
        groupService.addUserToGroup(adminUser, adminPassword, group1, user2);

        LOG.info("Added user " + user2 + " to group " + group1);
        groupService.inviteGroupToSite(user1, password, siteName, group1, "SiteConsumer");

        LOG.info("Added group " + group1 + " to site " + siteName + " with role Consumer");

        setupAuthenticatedSession(user1, password);

        LOG.info("Step 1 log in as user 1 and navigate to site 1 members page");
        siteUsers.navigate(siteName);

        LOG.info("Step 2 navigate to groups page and verify group1 is displayed");
        siteUsers.openSiteGroupsPage();
        Assert.assertTrue(siteGroupsPage.isASiteMember(group1), "Group is not present in site members");
        Assert.assertTrue(siteGroupsPage.isRoleSelected("Consumer", group1), "Group doesn't have the consumer role displayed");
        Assert.assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Group doesn't have the remove button enabled");

        LOG.info("Step 3 remove group q from site");
        siteGroupsPage.removeGroup(group1);
        Assert.assertEquals(notification.getDisplayedNotification(), "Successfully removed group " + group1, "Confirmation notification didn't appear");

        LOG.info("Step 4 check users tab and confirm user2 is not displayed");
        siteUsers.navigate(siteName);
        Assert.assertEquals(null, siteUsers.selectMember(user2), "User2 is displayed as a site member");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2884")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlyManagerCanRemoveUsersTest()
    {
        LOG.info("Starting C2884");

        // preconditions
        List<String> members;
        uniqueIdentifier = String.format("C2884-%s", RandomData.getRandomAlphanumeric());
        String siteManager = "manager" + uniqueIdentifier;
        String siteCollaborator = "collaborator" + uniqueIdentifier;
        String siteConsumer = "consumer" + uniqueIdentifier;
        String siteContributor = "contributor" + uniqueIdentifier;
        siteName = "testSite" + uniqueIdentifier;
        description = "C2884";

        userService.create(adminUser, adminPassword, siteManager, password, siteManager + domain, "Manager", description);
        userService.create(adminUser, adminPassword, siteCollaborator, password, siteCollaborator + domain, "Collaborator", description);
        userService.create(adminUser, adminPassword, siteContributor, password, siteContributor + domain, "Contributor", description);
        userService.create(adminUser, adminPassword, siteConsumer, password, siteConsumer + domain, "Consumer", description);

        siteService.create(siteManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        LOG.info("Created site " + siteName + " with user " + siteManager + "as manager");

        userService.createSiteMember(siteManager, password, siteCollaborator, siteName, "SiteCollaborator");
        LOG.info("Adding " + siteCollaborator + " as " + siteName + " site collaborator");
        userService.createSiteMember(siteManager, password, siteContributor, siteName, "SiteContributor");
        LOG.info("Adding " + siteContributor + " as " + siteName + " site contributor");
        userService.createSiteMember(siteManager, password, siteConsumer, siteName, "SiteConsumer");
        LOG.info("Adding " + siteConsumer + " as " + siteName + " site consumer");

        LOG.info("Step 1 login as site collaborator open site members page and verify if the remove button is disabled for all members");
        setupAuthenticatedSession(siteCollaborator, password);
        siteUsers.navigate(siteName);

        // verify that every member has correct role
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Manager", "Manager " + description), "Site manager doesn't have the correct role");
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Collaborator", "Collaborator " + description), "Site collaborator doesn't have the correct role");
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Consumer", "Consumer " + description), "Site consumer doesn't have the correct role");
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Contributor", "Contributor " + description), "Site contributor doesn't have the correct role");

        members = siteUsers.getSiteMembersList();
        for (int i = 0; i <= members.size() - 1; i++)
        {
            if (!(members.get(i).equals("Collaborator " + description)))
            {
                Assert.assertFalse(siteUsers.isRemoveButtonDisplayedForUser(members.get(i)), "Member has remove button enabled");
            }
        }

        LOG.info("Step 2 login as site contributor open site members page and verify if the remove button is disabled for all members");
        setupAuthenticatedSession(siteContributor, password);
        siteUsers.navigate(siteName);

        members = siteUsers.getSiteMembersList();
        for (int i = 0; i <= members.size() - 1; i++)
        {
            if (!(members.get(i).equals("Contributor " + description)))
            {
                Assert.assertFalse(siteUsers.isRemoveButtonDisplayedForUser(members.get(i)), "Member has remove button enabled");
            }
        }

        LOG.info("Step 3 login as site consumer open site members page and verify if the remove button is disabled for all members");
        setupAuthenticatedSession(siteConsumer, password);
        siteUsers.navigate(siteName);

        members = siteUsers.getSiteMembersList();
        for (int i = 0; i <= members.size() - 1; i++)
        {
            if (!(members.get(i).equals("Consumer " + description)))
            {
                Assert.assertFalse(siteUsers.isRemoveButtonDisplayedForUser(members.get(i)), "Member has remove button enabled");
            }
        }

        LOG.info("Step 4 login as site manager open site members page and verify if the remove button is enabled for all members");
        setupAuthenticatedSession(siteManager, password);
        siteUsers.navigate(siteName);

        members = siteUsers.getSiteMembersList();
        for (int i = 0; i <= members.size() - 1; i++)
        {
            if (!(members.get(i).equals("Manager " + description)))
            {
                Assert.assertTrue(siteUsers.isRemoveButtonDisplayedForUser(members.get(i)), "Member has remove button enabled");
            }
        }

        userService.delete(adminUser, adminPassword, siteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteManager);
        userService.delete(adminUser, adminPassword, siteCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteCollaborator);
        userService.delete(adminUser, adminPassword, siteConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteConsumer);
        userService.delete(adminUser, adminPassword, siteContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteContributor);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2885")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void onlyManagerCanRemoveGroupsTest()
    {

        LOG.info("Starting C2885");

        // preconditions
        uniqueIdentifier = String.format("C2885-%s", RandomData.getRandomAlphanumeric());
        String siteManager = "manager" + uniqueIdentifier;
        String siteCollaborator = "collaborator" + uniqueIdentifier;
        String siteConsumer = "consumer" + uniqueIdentifier;
        String siteContributor = "contributor" + uniqueIdentifier;
        group1 = "testGroup" + uniqueIdentifier;
        siteName = "testSite" + uniqueIdentifier;
        description = "C2885";

        userService.create(adminUser, adminPassword, siteManager, password, siteManager + domain, "Manager", description);
        userService.create(adminUser, adminPassword, siteCollaborator, password, siteCollaborator + domain, "Collaborator", description);
        userService.create(adminUser, adminPassword, siteContributor, password, siteContributor + domain, "Contributor", description);
        userService.create(adminUser, adminPassword, siteConsumer, password, siteConsumer + domain, "Consumer", description);

        siteService.create(siteManager, password, domain, siteName, description, SiteService.Visibility.PUBLIC);
        LOG.info("Created site " + siteName + " with user " + siteManager + "as manager");

        groupService.createGroup(adminUser, adminPassword, group1);
        LOG.info("Created group with name " + group1);

        userService.createSiteMember(siteManager, password, siteCollaborator, siteName, "SiteCollaborator");
        LOG.info("Adding " + siteCollaborator + " as " + siteName + " site collaborator");
        userService.createSiteMember(siteManager, password, siteContributor, siteName, "SiteContributor");
        LOG.info("Adding " + siteContributor + " as " + siteName + " site contributor");
        userService.createSiteMember(siteManager, password, siteConsumer, siteName, "SiteConsumer");
        LOG.info("Adding " + siteConsumer + " as " + siteName + " site consumer");
        groupService.inviteGroupToSite(siteManager, password, siteName, group1, "SiteConsumer");
        LOG.info("Added group " + group1 + " to site " + siteName + " with role Consumer");

        for (int i = 0; i <= 3; i++)
        {
            switch (i)
            {
                case 0:
                    LOG.info("Step 1 login as site collaborator open group members page and verify if the remove button is disabled for consumer group");
                    setupAuthenticatedSession(siteCollaborator, password);
                    break;
                case 1:
                    LOG.info("Step 2 login as site contributor open group members page and verify if the remove button is disabled for consumer group");
                    setupAuthenticatedSession(siteContributor, password);
                    break;
                case 2:
                    LOG.info("Step 3 login as site consumer open group members page and verify if the remove button is disabled for consumer group");
                    setupAuthenticatedSession(siteConsumer, password);
                    break;
                case 3:
                    LOG.info("Step 4 login as site manager open group members page and verify if the remove button is enabled for consumer group");
                    setupAuthenticatedSession(siteManager, password);
                    break;
            }

            siteGroupsPage.navigate(siteName);
            Assert.assertTrue(siteGroupsPage.isASiteMember(group1), "Consumer group is not displayed in site groups");

            if (i != 3)
                Assert.assertFalse(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Remove button for group is displayed for any site member");
            else
            {
                Assert.assertTrue(siteGroupsPage.isRoleSelected("Consumer", group1), "Consumer group doesn't have the correct role displayed");
                Assert.assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Remove button for group is not displayed for manager");
            }
        }
        userService.delete(adminUser, adminPassword, siteManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteManager);
        userService.delete(adminUser, adminPassword, siteCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteCollaborator);
        userService.delete(adminUser, adminPassword, siteConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteConsumer);
        userService.delete(adminUser, adminPassword, siteContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + siteContributor);
        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2890")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteMemberFromPrivateSiteTest() throws DataPreparationException
    {
        LOG.info("Starting test C2890");

        // preconditions
        setup("C2890", SiteService.Visibility.PRIVATE);
        LOG.info("Created site " + siteName + " with user " + user1 + "as manager");

        userService.createSiteMember(user1, password, user2, siteName, "SiteContributor");
        LOG.info("Adding " + user2 + "as " + siteName + " contributor");

        LOG.info("Step 1 logging in as user1 and verifying the users from site members!");
        setupAuthenticatedSession(user1, password);
        siteUsers.navigate(siteName);
        // assert if user 1 is present
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Manager", user1CompleteName), "User1 is not manager for this site");
        // assert if user 2 is present
        Assert.assertTrue(siteUsers.isRoleSelected("Contributor", user2CompleteName), "User2 is not contributor for this site");
        Assert.assertTrue(siteUsers.isRemoveButtonEnabled(user1CompleteName), "Remove button for user 1 is not enabled");
        Assert.assertTrue(siteUsers.isRemoveButtonEnabled(user2CompleteName), "Remove button for user 2 is not enabled");

        LOG.info("Step 2 removing User2 as a collaborator from the site");
        if (siteUsers.isRemoveButtonEnabled(user2CompleteName))
            siteUsers.removeUser(user2CompleteName);
        // verify the notification message
        Assert.assertEquals(notification.getDisplayedNotification(), "Successfully removed user " + user2);
        Assert.assertFalse(siteUsers.isASiteMember(user2), "User 2 is not removed from site members list");

        LOG.info("Step 3 logout as user 1 and login as user 2");
        setupAuthenticatedSession(user2, password);

        LOG.info("Step 4 opening site dashboard page");
        getBrowser().navigate().to(properties.getShareUrl() + "/page/site/" + siteName + "/dashboard");
        Assert.assertTrue(siteDashboardPage.somethingWentWrongMessage(), "The error page is not displayed correctly");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);

    }

    @TestRail (id = "C2892")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeSiteMemberFromModeratedSiteTest() throws DataPreparationException
    {
        LOG.info("Starting test C2892");

        // preconditions
        setup("C2892", SiteService.Visibility.MODERATED);
        LOG.info("Created site " + siteName + " with user " + user1 + "as manager");

        userService.createSiteMember(user1, password, user2, siteName, "SiteContributor");
        LOG.info("Adding " + user2 + "as " + siteName + " contributor");

        LOG.info("Step 1 logging in as user1 and verifying the users from site members!");
        setupAuthenticatedSession(user1, password);
        siteUsers.navigate(siteName);
        // assert if user 1 is present
        Assert.assertTrue(siteUsers.isUserRoleNotChangeable("Manager", user1CompleteName), "User1 is not manager for this site");
        // assert if user 2 is present
        Assert.assertTrue(siteUsers.isRoleSelected("Contributor", user2CompleteName), "User2 is not contributor for this site");
        Assert.assertTrue(siteUsers.isRemoveButtonEnabled(user1CompleteName), "Remove button for user 1 is not enabled");
        Assert.assertTrue(siteUsers.isRemoveButtonEnabled(user2CompleteName), "Remove button for user 2 is not enabled");

        LOG.info("Step 2 removing User2 as a collaborator from the site");
        if (siteUsers.isRemoveButtonEnabled(user2CompleteName))
            siteUsers.removeUser(user2CompleteName);
        // verify the notification message
        Assert.assertEquals(notification.getDisplayedNotification(), "Successfully removed user " + user2);
        Assert.assertFalse(siteUsers.isASiteMember(user2), "User 2 is not removed from site members list");

        LOG.info("Step 3 logout as user 1 and login as user 2");
        setupAuthenticatedSession(user2, password);

        LOG.info("Step 4 open site's dashboard");
        siteDashboardPage.navigate(siteName);

        LOG.info("Step 5 click on site configuration and verify 'Join site' option is available");
        siteDashboardPage.clickSiteConfiguration();
        Assert.assertTrue(siteDashboardPage.isOptionListedInSiteConfigurationDropDown("Request to Join"),
            "Join Site option is not present in site configuration dropdown");

        userService.delete(adminUser, adminPassword, user1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user1);

        userService.delete(adminUser, adminPassword, user2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + user2);

        siteService.delete(adminUser, adminPassword, siteName);

    }
}