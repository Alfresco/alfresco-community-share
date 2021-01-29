package org.alfresco.share.site.members;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.Notification;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.members.AddSiteUsersPage;
import org.alfresco.po.share.site.members.SiteMembersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Claudia Agache on 8/1/2016.
 */
public class AddSiteMembersTests extends ContextAwareWebTest
{
    //@Autowired
    AddSiteUsersPage addSiteUsersPage;

    //@Autowired
    SiteMembersPage siteMembersPage;

   // @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    Notification notification;

    private String siteName = String.format("Site%s", RandomData.getRandomAlphanumeric());
    private String userManager1 = String.format("aManager1%s", RandomData.getRandomAlphanumeric());
    private String userManager2 = String.format("aManager2%s", RandomData.getRandomAlphanumeric());
    private String manager2Name = "Manager2 fName lName";
    private String userCollaborator = String.format("aCollaborator%s", RandomData.getRandomAlphanumeric());
    private String collaboratorName = "Collaborator fName lName";
    private String userContributor = String.format("aContributor%s", RandomData.getRandomAlphanumeric());
    private String contributorName = "Contributor fName lName";
    private String userConsumer = String.format("aConsumer%s", RandomData.getRandomAlphanumeric());
    private String consumerName = "Consumer fName lName";
    private String sameRoleUserA = String.format("sameRoleUserA%s", RandomData.getRandomAlphanumeric());
    private String sameRoleUserB = String.format("sameRoleUserB%s", RandomData.getRandomAlphanumeric());
    private String sameRoleUserC = String.format("sameRoleUserC%s", RandomData.getRandomAlphanumeric());
    private String differentRoles = RandomStringUtils.randomAlphanumeric(7);
    private String differentRoleUserA = String.format("%sA%s", differentRoles, RandomData.getRandomAlphanumeric());
    private String differentRoleUserB = String.format("%sB%s", differentRoles, RandomData.getRandomAlphanumeric());
    private String differentRoleUserC = String.format("%sC%s", differentRoles, RandomData.getRandomAlphanumeric());
    private String differentRoleUserD = String.format("%sD%s", differentRoles, RandomData.getRandomAlphanumeric());
    private String removeUser = String.format("removeUser%s", RandomData.getRandomAlphanumeric());



    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userManager1, password, userManager1 + domain, "Manager1 fName", "lName");

        userService.create(adminUser, adminPassword, userManager2, password, userManager2 + domain, "Manager2 fName", "lName");
        userService.create(adminUser, adminPassword, userCollaborator, password, userCollaborator + domain, "Collaborator fName", "lName");
        userService.create(adminUser, adminPassword, userContributor, password, userContributor + domain, "Contributor fName", "lName");
        userService.create(adminUser, adminPassword, userConsumer, password, userConsumer + domain, "Consumer fName", "lName");

        //precondition for C2413
        userService.create(adminUser, adminPassword, sameRoleUserA, password, sameRoleUserA + domain, sameRoleUserA, sameRoleUserA);
        userService.create(adminUser, adminPassword, sameRoleUserB, password, sameRoleUserB + domain, sameRoleUserB, sameRoleUserB);
        userService.create(adminUser, adminPassword, sameRoleUserC, password, sameRoleUserC + domain, sameRoleUserC, sameRoleUserC);

        //precondition for C2829
        userService.create(adminUser, adminPassword, differentRoleUserA, password, differentRoleUserA + domain, differentRoleUserA, differentRoleUserA);
        userService.create(adminUser, adminPassword, differentRoleUserB, password, differentRoleUserB + domain, differentRoleUserB, differentRoleUserB);
        userService.create(adminUser, adminPassword, differentRoleUserC, password, differentRoleUserC + domain, differentRoleUserC, differentRoleUserC);
        userService.create(adminUser, adminPassword, differentRoleUserD, password, differentRoleUserD + domain, differentRoleUserD, differentRoleUserD);

        userService.create(adminUser, adminPassword, removeUser, password, removeUser + domain, removeUser, removeUser);

        siteService.create(userManager1, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        setupAuthenticatedSession(userManager1, password);
    }

    @AfterClass (alwaysRun = true)
    public void testCleanup()
    {
        userService.delete(adminUser, adminPassword, userManager1);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager1);

        userService.delete(adminUser, adminPassword, userManager2);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userManager2);

        userService.delete(adminUser, adminPassword, userCollaborator);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userCollaborator);

        userService.delete(adminUser, adminPassword, userContributor);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userContributor);

        userService.delete(adminUser, adminPassword, userConsumer);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userConsumer);

        userService.delete(adminUser, adminPassword, sameRoleUserA);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + sameRoleUserA);

        userService.delete(adminUser, adminPassword, sameRoleUserB);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + sameRoleUserB);

        userService.delete(adminUser, adminPassword, sameRoleUserC);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + sameRoleUserC);

        userService.delete(adminUser, adminPassword, differentRoleUserA);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + differentRoleUserA);

        userService.delete(adminUser, adminPassword, differentRoleUserB);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + differentRoleUserB);

        userService.delete(adminUser, adminPassword, differentRoleUserC);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + differentRoleUserC);

        userService.delete(adminUser, adminPassword, differentRoleUserD);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + differentRoleUserD);

        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + removeUser);


        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C2824")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyDefaultItemsFromAddUsersPage()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: Verify the elements displayed in the page.");
        assertTrue(addSiteUsersPage.isSearchForUsersPanelDisplayed(), "'Search for users' panel is displayed.");
        assertTrue(addSiteUsersPage.isSearchBoxInputDisplayed(), "'Search' input box is displayed.");
        assertTrue(addSiteUsersPage.isSearchButtonEnabled(), "'Search' button is enabled.");
        assertEquals(addSiteUsersPage.getSearchForUsersPanelMessage(), language.translate("addUsersPage.searchPanel.defaultMessage"), "Message is displayed in 'Search for users' panel.");

        assertTrue(addSiteUsersPage.isSetUserRolePanelDisplayed(), "'Set User Role' panel is displayed.");
        assertTrue(addSiteUsersPage.isSetAllRolesToButtonDisplayed(), "'Set All Roles To' button is displayed.");
        assertTrue(addSiteUsersPage.isInfoIconDisplayed(), "'Info' icon is displayed.");
        assertEquals(addSiteUsersPage.getSetUserRolePanelMessage(), language.translate("addUsersPage.setUserRolePanel.defaultMessage"),
            "Message is displayed in 'Set User Role' panel.");
        assertTrue(addSiteUsersPage.isAddUsersToSitePanelDisplayed(), "'Add Users to Site' panel is displayed.");
        assertFalse(addSiteUsersPage.isAddUsersButtonEnabled(), "'Add Users' button is disabled.");
        assertEquals(addSiteUsersPage.getAddUsersToSitePanelMessage(), language.translate("addUsersPage.addUsersToSitePanel.defaultMessage"),
            "Message is displayed in 'Add Users to Site' panel.");
    }

    @TestRail (id = "3114")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void verifyInfoIconFromAddUsersPage()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: Click on 'Info' icon from 'Set User Role' panel.");
        addSiteUsersPage.clickInfoIcon();
        assertTrue(addSiteUsersPage.isInfoBalloonDisplayed(), "'Info' pop-up is opened.");
        assertEquals(addSiteUsersPage.getInfoBalloonText(), language.translate("addUsersPage.setUserRolePanel.InfoIcon"), "Info balloon text");

        LOG.info("STEP 3: Click on 'See more' link.");
        addSiteUsersPage.clickSeeMoreLink();
        Assert.assertTrue(getBrowser().getTitle().contains(language.translate("alfrescoDocumentation.pageTitle")) , "Page title");

        LOG.info("STEP 4: Go back to 'Add Users' page and click 'x' button for 'Info' pop-up.");
        addSiteUsersPage.navigate(siteName);
        addSiteUsersPage.clickInfoIcon();
        addSiteUsersPage.closeInfoBalloon();
        assertFalse(addSiteUsersPage.isInfoBalloonDisplayed(), "'Info' pop-up is closed.");
    }

    @TestRail (id = "2409")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addManagerMemberToSite()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + userManager2 + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(userManager2);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(userManager2), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + userManager2 + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(userManager2);

        LOG.info("STEP 4: Click on the 'Select Role' button for the selected user. Select 'Manager' role from the drop-down menu. ");
        addSiteUsersPage.setUserRole(userManager2, "Manager");
        assertTrue(addSiteUsersPage.getUserRole(userManager2).contains("Manager"), userManager2 + " has Manager role selected.");

        LOG.info("STEP 5: Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(1);
        assertTrue(addSiteUsersPage.isUserAddedToSite(manager2Name), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(manager2Name), "Manager");

        LOG.info("STEP 6: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(manager2Name), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Manager", manager2Name), "Added user has 'Manager' role.");
        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(manager2Name), "'Remove' button is available for " + userManager2);
    }

    @TestRail (id = "2410")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addCollaboratorMemberToSite()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + userCollaborator + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(userCollaborator);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(userCollaborator), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + userCollaborator + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(userCollaborator);

        LOG.info("STEP 4: Click on the 'Select Role' button for the selected user. Select 'Collaborator' role from the drop-down menu. ");
        addSiteUsersPage.setUserRole(userCollaborator, "Collaborator");
        assertTrue(addSiteUsersPage.getUserRole(userCollaborator).contains("Collaborator"), userCollaborator + " has Collaborator role selected.");

        LOG.info("STEP 5: Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(1);
        assertTrue(addSiteUsersPage.isUserAddedToSite(collaboratorName), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(collaboratorName), "Collaborator");

        LOG.info("STEP 6: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(collaboratorName), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Collaborator", collaboratorName), "Added user has 'Collaborator' role.");
        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(collaboratorName), "'Remove' button is available for " + userCollaborator);
    }

    @TestRail (id = "2412")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addContributorMemberToSite()
    {
        LOG.info("STEP 1: Navigate to the created site. Click on 'Add Users' icon.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickAddUsersIcon();

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + userContributor + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(userContributor);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(userContributor), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + userContributor + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(userContributor);

        LOG.info("STEP 4: Click on the 'Select Role' button for the selected user. Select 'Contributor' role from the drop-down menu. ");
        addSiteUsersPage.setUserRole(userContributor, "Contributor");
        assertTrue(addSiteUsersPage.getUserRole(userContributor).contains("Contributor"), userContributor + " has Contributor role selected.");

        LOG.info("STEP 5: Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.clickAddUsers();
        assertTrue(addSiteUsersPage.isUserAddedToSite(contributorName), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(contributorName), "Contributor");

        LOG.info("STEP 6: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(contributorName), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Contributor", contributorName), "Added user has 'Contributor' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(contributorName), "'Remove' button is available for " + userContributor);
    }

    @TestRail (id = "2411")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addConsumerMemberToSite()
    {
        LOG.info("STEP 1: Navigate to the created site. Click on 'Add Users' icon.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickAddUsersIcon();

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + userConsumer + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(userConsumer);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(userConsumer), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + userConsumer + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(userConsumer);

        LOG.info("STEP 4: Click on the 'Select Role' button for the selected user. Select 'Contributor' role from the drop-down menu. ");
        addSiteUsersPage.setUserRole(userConsumer, "Consumer");
        assertTrue(addSiteUsersPage.getUserRole(userConsumer).contains("Consumer"), userConsumer + " has Consumer role selected.");

        LOG.info("STEP 5: Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(1);
        assertTrue(addSiteUsersPage.isUserAddedToSite(consumerName), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(consumerName), "Consumer");

        LOG.info("STEP 6: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(consumerName), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Consumer", consumerName), "Added user has 'Consumer' role.");
        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(consumerName), "'Remove' button is available for " + userConsumer);
    }

    @TestRail (id = "2413")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addMultipleUsersWithSameRole()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + sameRoleUserA + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(sameRoleUserA);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserA), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + sameRoleUserA + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(sameRoleUserA);

        LOG.info("STEP 4: In the 'Search for users...' search box, enter " + sameRoleUserB + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(sameRoleUserB);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserB), "The user is displayed in the search results.");

        LOG.info("STEP 5: Click on the 'Select' button for " + sameRoleUserB + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(sameRoleUserB);

        LOG.info("STEP 6: In the 'Search for users...' search box, enter " + sameRoleUserC + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(sameRoleUserC);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserC), "The user is displayed in the search results.");

        LOG.info("STEP 7: Click on the 'Select' button for " + sameRoleUserC + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(sameRoleUserC);

        LOG.info("STEP 8: Click on 'Set all roles to' button from 'Set User Role' panel. Select 'Collaborator' role from the drop-down menu.");
        addSiteUsersPage.setAllRolesTo("Collaborator");
        assertTrue(addSiteUsersPage.getUserRole(sameRoleUserA).contains("Collaborator"), sameRoleUserA + " has Collaborator role selected.");
        assertTrue(addSiteUsersPage.getUserRole(sameRoleUserB).contains("Collaborator"), sameRoleUserB + " has Collaborator role selected.");
        assertTrue(addSiteUsersPage.getUserRole(sameRoleUserC).contains("Collaborator"), sameRoleUserC + " has Collaborator role selected.");

        LOG.info("STEP 9: Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(3);
        assertTrue(addSiteUsersPage.isUserAddedToSite(sameRoleUserA), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(sameRoleUserA), "Collaborator");
        assertTrue(addSiteUsersPage.isUserAddedToSite(sameRoleUserB), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(sameRoleUserB), "Collaborator");
        assertTrue(addSiteUsersPage.isUserAddedToSite(sameRoleUserC), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(sameRoleUserC), "Collaborator");

        LOG.info("STEP 10: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(sameRoleUserA + " " + sameRoleUserA), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Collaborator", sameRoleUserA + " " + sameRoleUserA), "Added user has 'Collaborator' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(sameRoleUserA + " " + sameRoleUserA), "'Remove' button is available for " + sameRoleUserA);
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(sameRoleUserB + " " + sameRoleUserB), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Collaborator", sameRoleUserB + " " + sameRoleUserB), "Added user has 'Collaborator' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(sameRoleUserB + " " + sameRoleUserB), "'Remove' button is available for " + sameRoleUserB);
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(sameRoleUserC + " " + sameRoleUserC), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Collaborator", sameRoleUserC + " " + sameRoleUserC), "Added user has 'Collaborator' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(sameRoleUserC + " " + sameRoleUserC), "'Remove' button is available for " + sameRoleUserC);
    }

    @TestRail (id = "2829")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void addMultipleUsersWithDifferentRoles()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: In the 'Search for users...' search box, enter 'differentRoleUser'. Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(differentRoles);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(differentRoleUserA), differentRoleUserA + " user is displayed in the search results.");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(differentRoleUserB), differentRoleUserB + " user is displayed in the search results.");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(differentRoleUserC), differentRoleUserC + " user is displayed in the search results.");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(differentRoleUserD), differentRoleUserD + " user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + differentRoleUserA + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(differentRoleUserA);

        LOG.info("STEP 4: Click on the 'Select Role' button for the selected user. Select 'Manager' role from the drop-down menu. Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.setUserRole(differentRoleUserA, "Manager");
        assertTrue(addSiteUsersPage.getUserRole(differentRoleUserA).contains("Manager"), differentRoleUserA + " has Manager role selected.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(1);
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserA), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserA), "Manager");

        LOG.info("STEP 5: Click on the 'Select' button for " + differentRoleUserB + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(differentRoleUserB);

        LOG.info("STEP 6: Click on the 'Select Role' button for the selected user. Select 'Collaborator' role from the drop-down menu. Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.setUserRole(differentRoleUserB, "Collaborator");
        assertTrue(addSiteUsersPage.getUserRole(differentRoleUserB).contains("Collaborator"), differentRoleUserB + " has Collaborator role selected.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(2);
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserA), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserA), "Manager");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserB), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserB), "Collaborator");

        LOG.info("STEP 7: Click on the 'Select' button for " + differentRoleUserC + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(differentRoleUserC);

        LOG.info("STEP 8: Click on the 'Select Role' button for the selected user. Select 'Contributor' role from the drop-down menu. Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.setUserRole(differentRoleUserC, "Contributor");
        assertTrue(addSiteUsersPage.getUserRole(differentRoleUserC).contains("Contributor"), differentRoleUserC + " has Contributor role selected.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(3);
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserA), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserA), "Manager");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserB), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserB), "Collaborator");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserC), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserC), "Contributor");

        LOG.info("STEP 9: Click on the 'Select' button for " + differentRoleUserD + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(differentRoleUserD);

        LOG.info("STEP 10: Click on the 'Select Role' button for the selected user. Select 'Consumer' role from the drop-down menu. Click on 'Add Users' button from 'Add Users to Site' panel.");
        addSiteUsersPage.setUserRole(differentRoleUserD, "Consumer");
        assertTrue(addSiteUsersPage.getUserRole(differentRoleUserD).contains("Consumer"), differentRoleUserD + " has Consumer role selected.");
        addSiteUsersPage.clickAddUsers();
        addSiteUsersPage.assertTotalUserAddedIs(4);
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserA), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserA), "Manager");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserB), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserB), "Collaborator");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserC), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserC), "Contributor");
        assertTrue(addSiteUsersPage.isUserAddedToSite(differentRoleUserD), "User is added to site.");
        assertEquals(addSiteUsersPage.getUserRoleValue(differentRoleUserD), "Consumer");

        LOG.info("STEP 11: Click on 'Site Members' link on the site navigation.");
        addSiteUsersPage.clickSiteMembers();
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(differentRoleUserA + " " + differentRoleUserA), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Manager", differentRoleUserA + " " + differentRoleUserA), "Added user has 'Manager' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(differentRoleUserA + " " + differentRoleUserA), "'Remove' button is available for " + differentRoleUserA);
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(differentRoleUserB + " " + differentRoleUserB), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Collaborator", differentRoleUserB + " " + differentRoleUserB), "Added user has 'Collaborator' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(differentRoleUserB + " " + differentRoleUserB), "'Remove' button is available for " + differentRoleUserB);
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(differentRoleUserC + " " + differentRoleUserC), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Contributor", differentRoleUserC + " " + differentRoleUserC), "Added user has 'Contributor' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(differentRoleUserC + " " + differentRoleUserC), "'Remove' button is available for " + differentRoleUserC);
//        assertTrue(siteMembersPage.assertSiteMemberNameEqualsTo(differentRoleUserD + " " + differentRoleUserD), "Added user is displayed in the site members list");
//        assertTrue(siteMembersPage.assertSelectedRoleEqualsTo("Consumer", differentRoleUserD + " " + differentRoleUserD), "Added user has 'Consumer' role.");
//        assertTrue(siteMembersPage.isRemoveButtonEnabledForMember(differentRoleUserD + " " + differentRoleUserD), "'Remove' button is available for " + differentRoleUserD);
    }

    @TestRail (id = "2414")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void removeUserFromSelectRolesPanel()
    {
        LOG.info("STEP 1: Navigate to 'Add Users' page for " + siteName);
        addSiteUsersPage.navigate(siteName);

        LOG.info("STEP 2: In the 'Search for users...' search box, enter " + removeUser + ". Click 'Search' button/ press 'Enter' key.");
        addSiteUsersPage.searchForUser(removeUser);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(removeUser), "The user is displayed in the search results.");

        LOG.info("STEP 3: Click on the 'Select' button for " + removeUser + ", from the 'Search results'.");
        addSiteUsersPage.clickSelectUserButton(removeUser);

        LOG.info("STEP 4: Click on the 'Remove' button for the user, from 'Set User Role' panel");
        addSiteUsersPage.removeUser(removeUser);
        assertEquals(addSiteUsersPage.getSetUserRolePanelMessage(), language.translate("addUsersPage.setUserRolePanel.defaultMessage"), "userB is removed from the select 'Set User Role' panel.");
        assertTrue(addSiteUsersPage.isSelectUserButtonEnabled(removeUser), "'Select' button becomes enabled again in the 'search user' panel.");
    }

    @TestRail (id = "2822")
    @Test (groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchUsersFromAddUsersPage()
    {
        LOG.info("STEP 1: Navigate to the created site. Click on 'Add Users' icon.");
        siteDashboardPage.navigate(siteName);
        siteDashboardPage.clickAddUsersIcon();

        LOG.info("STEP 2: Leave the 'search box' empty and click on 'Search' button.");
        addSiteUsersPage.searchForUser("");
        assertEquals(notification.getDisplayedNotification(), "Enter at least 1 character(s)", "'Enter at least 1 character(s)' pop-up is displayed.");
        notification.waitUntilNotificationDisappears();

        LOG.info("STEP 3: Enter into 'search box' some random wildcards (e.g. !~@#$%^&*()_{}:'). Click 'Search' button.");
        addSiteUsersPage.searchForUser("!~@#$%^&*()_{}:'");
        getBrowser().waitInSeconds(1);
        assertEquals(addSiteUsersPage.getSearchForUsersPanelMessage(), language.translate("addUsersPage.noUsersFound"), "'No users found' message is displayed in 'Search for users' panel.");

        LOG.info("STEP 4: Enter 'sameRole' string in the search box and click 'Search' button.");
        addSiteUsersPage.searchForUser("sameRole");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserA), sameRoleUserA + " user is displayed in the search results.");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserB), sameRoleUserB + " user is displayed in the search results.");
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserC), sameRoleUserC + " user is displayed in the search results.");

        LOG.info("STEP 5: Enter " + sameRoleUserB + " string in the search box and click 'Search' button.");
        addSiteUsersPage.searchForUser(sameRoleUserB);
        assertTrue(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserB), sameRoleUserB + " user is displayed in the search results.");
        assertFalse(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserA), sameRoleUserA + " user is not displayed in the search results.");
        assertFalse(addSiteUsersPage.isUserDisplayedInSearchResults(sameRoleUserC), sameRoleUserC + " user is not displayed in the search results.");

        LOG.info("STEP 6: Enter more than 255 random characters in the 'search box' and click 'Search button'.");
        addSiteUsersPage.searchForUser(RandomStringUtils.randomAlphanumeric(256));
        assertEquals(addSiteUsersPage.getSearchForUsersPanelMessage(), language.translate("addUsersPage.noUsersFound"), "'No users found' message is displayed in 'Search for users' panel.");
    }
}
