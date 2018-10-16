package org.alfresco.share.adminTools.groups;

import org.alfresco.po.share.site.members.AddSiteGroupsPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.alfresco.dataprep.SiteService;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Razvan.Dorobantu
 */
public class GroupsTests extends ContextAwareWebTest
{
    @Autowired
    private GroupsPage groupsPage;

    @Autowired
    private UserDashboardPage userDashboardPage;

    @Autowired
    private SiteGroupsPage siteGroupsPage;

    @Autowired
    private AddSiteGroupsPage addSiteGroupsPage;

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String C9462group = "_C9462group" + uniqueIdentifier;
    private final String C9465group = "_C9465group" + uniqueIdentifier;
    private final String C9469group = "_C9469group" + uniqueIdentifier;
    private final String C9471group = "_C9471group" + uniqueIdentifier;
    private final String C9471group1 = "_C9471groupS" + uniqueIdentifier;
    private final String C9460group = "_C9460group" + uniqueIdentifier;
    private final String C9465groupEdited = "_C9465Edited";
    private final String C9465EditedName = C9465groupEdited + " (" + C9465group + ")";
    private final String userName = "user" + uniqueIdentifier;
    private final String siteName = String.format("C9460site%s", uniqueIdentifier);

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
    {
        userService.create(adminUser, adminPassword, userName, password, domain, "name", uniqueIdentifier);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        groupService.createGroup(adminUser, adminPassword, C9462group);
        groupService.createGroup(adminUser, adminPassword, C9465group);
        groupService.createGroup(adminUser, adminPassword, C9471group);
        groupService.createGroup(adminUser, adminPassword, C9471group1);
        groupService.createGroup(adminUser, adminPassword, C9460group);

        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @AfterClass
    public void afterClass()
    {
        groupService.removeGroup(adminUser, adminPassword, C9465group);
        groupService.removeGroup(adminUser, adminPassword, C9469group);
        groupService.removeGroup(adminUser, adminPassword, C9471group);
        groupService.removeGroup(adminUser, adminPassword, C9460group);
    }

    @TestRail(id = "C9462")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteGroup()
    {
        groupsPage.navigate();
        groupsPage.clickBrowseButton();
        
        groupsPage.checkGroupIsInList(C9462group);
 
        LOG.info("Step 1: Delete the group created in Precondition.");
        groupsPage.deleteGroup(C9462group, true);

        LOG.info("Step 2: Verify the group is no longer present in the groups list.");
        groupsPage.checkGroupIsRemoved(C9462group);
    }

    @TestRail(id = "C9465")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editGroup()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.clickBrowseButton();
        groupsPage.checkGroupIsInList(C9465group);

        LOG.info("Step 1: Edit the group created in Precondition.");
        groupsPage.editGroup(C9465group, C9465groupEdited, true);

        LOG.info("Step 2: Verify the edited group is present in the groups list.");
        groupsPage.checkGroupIsInList(C9465EditedName);
        groupsPage.checkGroupIsRemoved(C9465group);
    }

    @TestRail(id = "C9469")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createGroup()
    {
        groupsPage.navigate();
        groupsPage.clickBrowseButton();
        
        LOG.info("Step1: Create a group from Admin Tools page.");
        groupsPage.createGroup(C9469group);

        LOG.info("Step 2: Verify the group is present in the groups list.");
        groupsPage.checkGroupIsInList(C9462group);;
    }

    @TestRail(id = "C9471")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchForAGroup()
    {
        groupsPage.navigate();

        LOG.info("Step 2: Search for the group created in Precondition.");
        groupsPage.writeInSearchInput(C9471group);
        groupsPage.clickSearchButton();
        groupsPage.waitUntilSearchElementDisplayed();


        LOG.info("Step 3: Verify the group is present in the search result list.");
        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(C9471group));
        Assert.assertEquals(groupsPage.getSeachBarText(), String.format("Search for '%s' found 1 results.",C9471group));
    
        groupsPage.writeInSearchInput("*" + uniqueIdentifier);
        groupsPage.clickSearchButton();
        groupsPage.waitUntilSearchElementDisplayed();

        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(C9471group));
        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(C9471group1));
    }

    @TestRail(id = "C9460")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyAddedToSiteGroupPresence()
    {
        setupAuthenticatedSession(userName, password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        siteGroupsPage.navigate(siteName);
        assertEquals(siteGroupsPage.getPageTitle(), "Alfresco » Site Groups", "Displayed page=");

        siteGroupsPage.addGroup();
        assertEquals(addSiteGroupsPage.getPageTitle(), "Alfresco » Add Groups", "Displayed page=");

        addSiteGroupsPage.searchForGroup(C9460group);
        addSiteGroupsPage.addGroup(C9460group);
        addSiteGroupsPage.setGroupRole(C9460group, "Contributor");
        addSiteGroupsPage.clickAddGroupsButton();

        setupAuthenticatedSession(adminUser, adminPassword);
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.clickBrowseButton();
        
        groupsPage.checkGroupIsInList(C9460group);        
    }
}
