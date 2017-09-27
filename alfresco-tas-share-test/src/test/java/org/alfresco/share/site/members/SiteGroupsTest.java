package org.alfresco.share.site.members;

import org.alfresco.dataprep.GroupService;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Claudia Agache on 6/30/2016.
 */
public class SiteGroupsTest extends ContextAwareWebTest
{
    @Autowired
    SiteGroupsPage siteGroupsPage;

    @Autowired
    GroupService groupService;

    private String user = String.format("testUser%s", RandomData.getRandomAlphanumeric());
    private String siteName = String.format("siteName%s",RandomData.getRandomAlphanumeric());
    private String group1 = String.format("testGroup1-%s",RandomData.getRandomAlphanumeric());
    private String group2 = String.format("testGroup2-%s",RandomData.getRandomAlphanumeric());
    private String group3 = String.format("testGroup3-%s",RandomData.getRandomAlphanumeric());


    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, user, password, user + domain, "firstName", "lastName");
        siteService.create(user, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);

        groupService.createGroup(adminUser, adminPassword, group1);
        groupService.createGroup(adminUser, adminPassword, group2);
        groupService.createGroup(adminUser, adminPassword, group3);

        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, group1, "SiteCollaborator");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, group2, "SiteContributor");
        groupService.inviteGroupToSite(adminUser, adminPassword, siteName, group3, "SiteConsumer");

        setupAuthenticatedSession(user, password);
    }

    @TestRail(id = "C2819")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void listAllSiteGroups(){
        LOG.info("Navigate to 'Site Groups' page of site '" + siteName + "'.");
        siteGroupsPage.navigate(siteName);

        LOG.info("Expected Result: '"+ group1 +"' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group1), "Group '"+ group1 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Collaborator", group1), "Group '"+ group1 +"' has 'Collaborator' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Group '"+ group1 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group2 +"' with 'Contributor' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group2), "Group '"+ group2 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Contributor", group2), "Group '"+ group2 +"' has 'Contributor' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group2), "Group '"+ group2 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group3 +"' with 'Consumer' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group3), "Group '"+ group3 +"' is not present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Consumer", group3), "Group '"+ group3 +"' has 'Consumer' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group3), "Group '"+ group3 +"' has 'Remove' button.");

        LOG.info("Click on 'Search' button leaving the search box empty.");
        siteGroupsPage.clickSearch();

        LOG.info("Expected Result: '"+ group1 +"' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group1), "Group '"+ group1 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Collaborator", group1), "Group '"+ group1 +"' has 'Collaborator' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Group '"+ group1 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group2 +"' with 'Contributor' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group2), "Group '"+ group2 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Contributor", group2), "Group '"+ group2 +"' has 'Contributor' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group2), "Group '"+ group2 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group3 +"' with 'Consumer' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group3), "Group '"+ group3 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Consumer", group3), "Group '"+ group3 +"' has 'Consumer' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group3), "Group '"+ group3 +"' has 'Remove' button.");
    }

    @TestRail(id = "C2820")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchForGroupsUsingPartialName(){
        LOG.info("Navigate to 'Site Groups' page of site '" + siteName + "'.");
        siteGroupsPage.navigate(siteName);

        LOG.info("Enter 'test' string in the search box and click 'Search' button.");
        siteGroupsPage.typeSearchGroup("test");
        siteGroupsPage.clickSearch();

        LOG.info("Expected Result: '"+ group1 +"' with 'Collaborator' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group1), "Group '"+ group1 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Collaborator", group1), "Group '"+ group1 +"' has 'Collaborator' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group1), "Group '"+ group1 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group2 +"' with 'Contributor' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group2), "Group '"+ group2 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Contributor", group2), "Group '"+ group2 +"' has 'Contributor' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group2), "Group '"+ group2 +"' has 'Remove' button.");

        LOG.info("Expected Result: '"+ group3 +"' with 'Consumer' role and 'Remove' button is displayed");
        assertTrue(siteGroupsPage.isASiteMember(group3), "Group '"+ group3 +"' is present on the page.");
        assertTrue(siteGroupsPage.isRoleSelected("Consumer", group3), "Group '"+ group3 +"' has 'Consumer' role.");
        assertTrue(siteGroupsPage.isRemoveButtonDisplayedForGroup(group3), "Group '"+ group3 +"' has 'Remove' button.");
    }

    @TestRail(id = "C2821")
    @Test(groups = { TestGroup.SANITY, TestGroup.SITES })
    public void searchForGroupsUsingFullName(){
        LOG.info("Navigate to 'Site Groups' page of site '" + siteName + "'.");
        siteGroupsPage.navigate(siteName);

        LOG.info("Enter '"+ group2 +"' string in the search box and click 'Search' button.");
        siteGroupsPage.typeSearchGroup(group2);
        siteGroupsPage.clickSearch();

        LOG.info("Only: '"+ group2 +"' is displayed");
        assertFalse(siteGroupsPage.isASiteMember(group1), "Group '"+ group1 +"' is not expected to be present on the page.");
        assertTrue(siteGroupsPage.isASiteMember(group2), "Group '"+ group2 +"' is present on the page.");
        assertFalse(siteGroupsPage.isASiteMember(group3), "Group '"+ group3 +"' is not expected to be present on the page.");
    }

}