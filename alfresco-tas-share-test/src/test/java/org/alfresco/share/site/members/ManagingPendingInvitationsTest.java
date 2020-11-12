package org.alfresco.share.site.members;

import java.util.Date;

import org.alfresco.common.DataUtil;
import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.PendingInvitesPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ManagingPendingInvitationsTest extends ContextAwareWebTest
{
    //@Autowired
    SiteMembersPage siteMembersPage;

   //@Autowired
    AddSiteUsersPage addSiteUsersPage;

   //@Autowired
    PendingInvitesPage pendingInvitesPage;

    /**
     * Deprecated for Alfresco 5.1.N and above
     */
    @TestRail (id = "C2894")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.SITES})
    public void pendingInvitesAreAvailableOnlyForSiteManagers()
    {
        String userManager = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userCollaborator = String.format("User2%s", RandomData.getRandomAlphanumeric());
        String userContributor = String.format("User3%s", RandomData.getRandomAlphanumeric());
        String userConsumer = String.format("User4%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userManager, password, userManager + domain, userManager, userManager);
        userService.create(adminUser, adminPassword, userCollaborator, password, userCollaborator + domain, userCollaborator, userCollaborator);
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, userContributor, userContributor);
        userService.create(adminUser, adminPassword, userConsumer, password, userConsumer + domain, userConsumer, userConsumer);
        siteService.create(userManager, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        userService.createSiteMember(userManager, password, userCollaborator, siteName, "SiteCollaborator");
        userService.createSiteMember(userManager, password, userContributor, siteName, "SiteContributor");
        userService.createSiteMember(userManager, password, userConsumer, siteName, "SiteConsumer");
        setupAuthenticatedSession(userCollaborator, password);

        LOG.info("STEP 1 - Login to Share as userCollaborator1 and open \"Site Members\" page for \"testSite\"");
        siteMembersPage.navigate(siteName);
        Assert.assertFalse(siteMembersPage.isPendingInvitesDisplayed(), "\"Pending Invites\" isn't displayed");

        LOG.info("STEP 2 - Logout from Share and login as userContributor1. Open \"Site Members\" page for \"testSite\".");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userContributor, password);
        siteMembersPage.navigate(siteName);
        Assert.assertFalse(siteMembersPage.isPendingInvitesDisplayed(), "\"Pending Invites\" isn't displayed");

        LOG.info("STEP 3 - Logout from Share and login as userConsumer1. Open \"Site Members\" page for \"testSite\"");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userConsumer, password);
        siteMembersPage.navigate(siteName);
        Assert.assertFalse(siteMembersPage.isPendingInvitesDisplayed(), "\"Pending Invites\" isn't displayed");

        LOG.info("STEP 4 - Logout from Share and login as userManager1. Open \"Site Members\" page for \"testSite\"");
        cleanupAuthenticatedSession();
        setupAuthenticatedSession(userManager, password);
        siteMembersPage.navigate(siteName);
        Assert.assertTrue(siteMembersPage.isPendingInvitesDisplayed(), "\"Pending Invites\" is displayed");

        userService.delete(adminUser, adminPassword, userManager);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager);

        userService.delete(adminUser, adminPassword, userCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userCollaborator);

        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);

        userService.delete(adminUser, adminPassword, userConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userConsumer);

        siteService.delete(adminUser, adminPassword, siteName);


    }

    /**
     * Deprecated for Alfresco 5.1.N and above
     */
    @TestRail (id = "C2895")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.SITES, "ExternalUsers" })
    public void searchForPendingInvites()
    {
        String userRole = "Contributor";
        String userIdentifier = RandomData.getRandomAlphanumeric();
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        addSiteUsersPage.navigate(siteName);
        addSiteUsersPage.addExternalUser("fname1" + userIdentifier, "lname1" + userIdentifier, "fname1" + userIdentifier + domain);
        addSiteUsersPage.addExternalUser("fname2" + userIdentifier, "lname2" + userIdentifier, "fname2" + userIdentifier + domain);
        addSiteUsersPage.addExternalUser("fname3" + userIdentifier, "lname3" + userIdentifier, "fname3" + userIdentifier + domain);
        addSiteUsersPage.setAllRolesTo(userRole);
        addSiteUsersPage.clickAddUsers();
        Assert.assertEquals(addSiteUsersPage.getAddedUsersTally(), language.translate("addUsersPage.addedUsersTally") + " 3", "Added users tally");

        LOG.info("STEP 1 - Open \"site1\" and click \"Site Members\" link. Click \"Pending Invites\" link");
        pendingInvitesPage.navigate(siteName);

        LOG.info("STEP 2 - Leave the search box empty and click on \"Search\" button");
        pendingInvitesPage.clickSearchButton();
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname1" + userIdentifier), "fname1" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname2" + userIdentifier), "fname2" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname3" + userIdentifier), "fname3" + userIdentifier + " invitation is displayed");

        LOG.info("STEP 3 - Delete text and enter \"fname1\" in the search box. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("fname1");
        pendingInvitesPage.clickSearchButton();
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname1" + userIdentifier), "fname1" + userIdentifier + " invitation is displayed");
        Assert.assertFalse(pendingInvitesPage.isPendingInvitationListed("fname2" + userIdentifier), "fname2" + userIdentifier + " invitation is not displayed");
        Assert.assertFalse(pendingInvitesPage.isPendingInvitationListed("fname3" + userIdentifier), "fname3" + userIdentifier + " invitation is not displayed");

        LOG.info("STEP 4 - Delete text and enter \"lname1\" in the search box. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("lname1");
        pendingInvitesPage.clickSearchButton();
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("lname1" + userIdentifier), "lname1" + userIdentifier + " invitation is displayed");
        Assert.assertFalse(pendingInvitesPage.isPendingInvitationListed("lname2" + userIdentifier), "lname2" + userIdentifier + " invitation is not displayed");
        Assert.assertFalse(pendingInvitesPage.isPendingInvitationListed("lname3" + userIdentifier), "lname3" + userIdentifier + " invitation is not displayed");

        LOG.info("STEP 5 - Delete text and enter \"fname4\" in the search box. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("fname4");
        pendingInvitesPage.clickSearchButton();
        getBrowser().waitInSeconds(3);
        Assert.assertFalse(pendingInvitesPage.isPendingInvitesListDisplayed(), "Pending invites list is not displayed");

        LOG.info("STEP 6 - Delete text and enter \"fname\" in the search box. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("fname");
        pendingInvitesPage.clickSearchButton();
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname1" + userIdentifier), "fname1" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname2" + userIdentifier), "fname2" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fname3" + userIdentifier), "fname3" + userIdentifier + " invitation is displayed");

        LOG.info("STEP 7 - Delete text and enter \"lname\" in the search box. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("lname");
        pendingInvitesPage.clickSearchButton();
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("lname1" + userIdentifier), "lname1" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("lname2" + userIdentifier), "lname2" + userIdentifier + " invitation is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("lname3" + userIdentifier), "lname3" + userIdentifier + " invitation is displayed");

        LOG.info("STEP 8 - Delete text and enter special characters \"!@#$%^&*()_+|}{\":?><\" in search input field. Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput("\"!@#$%^&*()_+|}{\":?><\"");
        pendingInvitesPage.clickSearchButton();
        Assert.assertFalse(pendingInvitesPage.isPendingInvitesListDisplayed(), "Pending invites list is not displayed");

        LOG.info("STEP 9 - Delete text and enter long data in search field (e.g. :. more than 1024 symbols). Click \"Search\" button");
        pendingInvitesPage.typeIntoSearchInput(RandomStringUtils.randomAlphabetic(1025));
        pendingInvitesPage.clickSearchButton();
        Assert.assertFalse(pendingInvitesPage.isPendingInvitesListDisplayed(), "Pending invites list is not displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);

    }

    /**
     * Deprecated for Alfresco 5.1.N and above
     */
    @TestRail (id = "C2898")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.SITES, "ExternalUsers" })
    public void verifyPendingInvitesPage()
    {
        String userRole = "Collaborator";
        String userIdentifier = RandomData.getRandomAlphanumeric();
        String userName = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName, password, userName + domain, userName, userName);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName, password);
        addSiteUsersPage.navigate(siteName);
        addSiteUsersPage.addExternalUser("fName" + userIdentifier, "lName" + userIdentifier, "test" + domain);
        addSiteUsersPage.setAllRolesTo(userRole);
        Date addUsersDate = new Date();
        addSiteUsersPage.clickAddUsers();
        Assert.assertEquals(addSiteUsersPage.getAddedUsersTally(), language.translate("addUsersPage.addedUsersTally") + " 1", "Added users tally");

        LOG.info("STEP 1 - Open \"Pending Invites\" page for site1");
        pendingInvitesPage.navigate(siteName);

        LOG.info("STEP 2 - Verify the items present on \"Pending Invites\" page");
        Assert.assertTrue(pendingInvitesPage.isSearchInputDisplayed(), "Search input is displayed");
        Assert.assertTrue(pendingInvitesPage.isSearchButtonDisplayed(), "Search button is displayed");
        Assert.assertTrue(pendingInvitesPage.isPendingInvitationListed("fName" + userIdentifier), "fName" + userIdentifier + " invitation is listed");

        LOG.info("STEP 3 - Verify the information/actions available for the pending invite");
        String invitationUserNameText = pendingInvitesPage.getInvitationUserName("fName" + userIdentifier);
        Date invitationSentDate = new Date(
            DataUtil.parseDate(pendingInvitesPage.getInvitationSentDate("fName" + userIdentifier).trim(), "EEE dd MMM yyyy HH:mm:ss"));

        Assert.assertTrue(pendingInvitesPage.getInvitationAvatarSource("fName" + userIdentifier).contains("no-user-photo"));
        Assert.assertTrue(invitationUserNameText.contains("fName" + userIdentifier), "Invitation contains fName" + userIdentifier);
        Assert.assertTrue(invitationUserNameText.contains("lName" + userIdentifier), "Invitation contains lName" + userIdentifier);
        Assert.assertTrue(DataUtil.areDatesEqual(addUsersDate, invitationSentDate, 300000), "Invitation date. addUsersDate :" + addUsersDate.toString() + " invitationSentDate: " + invitationSentDate.toString());
        Assert.assertEquals(pendingInvitesPage.getInvitationUserRole("fName" + userIdentifier), userRole, "Invitation user role");
        Assert.assertTrue(pendingInvitesPage.isCancelButtonDisplayed("fName" + userIdentifier), "Cancel button is displayed");

        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);

        siteService.delete(adminUser, adminPassword, siteName);
    }

    /**
     * Deprecated for Alfresco 5.1.N and above
     */
    @TestRail (id = "C2900")
    @Test (enabled = false, groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyPendingInvitesForInternalUsers()
    {
        String userRole = "Consumer";
        String userName1 = String.format("User1%s", RandomData.getRandomAlphanumeric());
        String userName2 = String.format("User2%s", RandomData.getRandomAlphanumeric());
        String siteName = String.format("Site1%s", RandomData.getRandomAlphanumeric());
        userService.create(adminUser, adminPassword, userName1, password, userName1 + domain, userName1, userName1);
        userService.create(adminUser, adminPassword, userName2, password, userName2 + domain, userName2, userName2);
        siteService.create(userName1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userName1, password);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 1 - In the \"Search for users...\" search box, enter " + userName2 + ". Click \"Search\" button");
        addSiteUsersPage.searchForUser(userName2);
        Assert.assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(userName2), userName2 + " is expected to be found at search.");

        LOG.info("STEP 2 - Click on the \"Select\" button for " + userName2 + ", from the \"Search results\"");
        addSiteUsersPage.clickSelectUserButton(userName2);

        LOG.info("STEP 3 - Select any role for the user (e.g.: \"Consumer\"). Click on \"Add Users\" button from \"Add Users to Site\" panel");
        addSiteUsersPage.setUserRole(userName2, userRole);
        addSiteUsersPage.clickAddUsers();
        Assert.assertEquals(addSiteUsersPage.getAddedUsersTally(), language.translate("addUsersPage.addedUsersTally") + " 1", "Added users tally");
        Assert.assertTrue(addSiteUsersPage.isUserAddedToSite(userName2 + " " + userName2), "User is added to site.");
        Assert.assertEquals(addSiteUsersPage.getUserRoleValue(userName2), userRole, userName2 + " user role");

        LOG.info("STEP 4 - Click on \"Pending Invites\" link on the site navigation");
        pendingInvitesPage.navigate(siteName);
        Assert.assertFalse(pendingInvitesPage.isPendingInvitesListDisplayed(), userName2 + " invitation is not displayed");

        userService.delete(adminUser, adminPassword, userName1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName1);
        userService.delete(adminUser, adminPassword, userName2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName2);

        siteService.delete(adminUser, adminPassword, siteName);
    }
}
