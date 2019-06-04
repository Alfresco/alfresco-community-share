package org.alfresco.share.adminTools.groups;

import static org.testng.Assert.assertEquals;

import org.alfresco.dataprep.SiteService;
import org.alfresco.po.share.site.members.AddSiteGroupsPage;
import org.alfresco.po.share.site.members.SiteGroupsPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.DeleteGroupDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Razvan.Dorobantu
 */
public class GroupsTests extends ContextAwareWebTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String c9462group = "_C9462group" + uniqueIdentifier;
    private final String c9465group = "_C9465group" + uniqueIdentifier;
    private final String c9469group = "_C9469group" + uniqueIdentifier;
    private final String c9471group = "_C9471group" + uniqueIdentifier;
    private final String c9471group1 = "_C9471groupS" + uniqueIdentifier;
    private final String c9460group = "_C9460group" + uniqueIdentifier;
    private final String c9473ParentGroup1 = "c9473ParentGroup1" + uniqueIdentifier;
    private final String c9473ParentGroup2 = "c9473ParentGroup2" + uniqueIdentifier;
    private final String c9473ParentGroup3 = "c9473ParentGroup3" + uniqueIdentifier;
    private final String c9473SubGroup = "C9473Group" + uniqueIdentifier;
    private final String c9465groupEdited = "_C9465Edited";
    private final String c9465EditedName = c9465groupEdited + " (" + c9465group + ")";
    private final String userName = "user" + uniqueIdentifier;
    private final String siteName = String.format("C9460site%s", uniqueIdentifier);
    @Autowired
    private GroupsPage groupsPage;
    @Autowired
    private UserDashboardPage userDashboardPage;
    @Autowired
    private SiteGroupsPage siteGroupsPage;
    @Autowired
    private AddSiteGroupsPage addSiteGroupsPage;
    @Autowired
    private DeleteGroupDialog deleteGroupDialog;

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        userService.create(adminUser, adminPassword, userName, password, domain, "name", uniqueIdentifier);
        siteService.create(userName, password, domain, siteName, "description", SiteService.Visibility.PUBLIC);
        groupService.createGroup(adminUser, adminPassword, c9462group);
        groupService.createGroup(adminUser, adminPassword, c9465group);
        groupService.createGroup(adminUser, adminPassword, c9471group);
        groupService.createGroup(adminUser, adminPassword, c9471group1);
        groupService.createGroup(adminUser, adminPassword, c9460group);
        groupService.createGroup(adminUser, adminPassword, c9473ParentGroup1);
        groupService.createGroup(adminUser, adminPassword, c9473ParentGroup2);
        groupService.createGroup(adminUser, adminPassword, c9473ParentGroup3);
        groupService.createGroup(adminUser, adminPassword, c9473SubGroup);
        groupService.addSubGroup(adminUser, adminPassword, c9473ParentGroup1, c9473SubGroup);
        groupService.addSubGroup(adminUser, adminPassword, c9473ParentGroup2, c9473SubGroup);
        groupService.addSubGroup(adminUser, adminPassword, c9473ParentGroup3, c9473SubGroup);
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @AfterClass
    public void afterClass()
    {
        cleanupAuthenticatedSession();
        groupService.removeGroup(adminUser, adminPassword, c9465group);
        groupService.removeGroup(adminUser, adminPassword, c9469group);
        groupService.removeGroup(adminUser, adminPassword, c9471group);
        groupService.removeGroup(adminUser, adminPassword, c9460group);
        groupService.removeGroup(adminUser, adminPassword, c9473ParentGroup1);
        groupService.removeGroup(adminUser, adminPassword, c9473ParentGroup2);
        groupService.removeGroup(adminUser, adminPassword, c9473ParentGroup3);
        groupService.removeGroup(adminUser, adminPassword, c9473SubGroup);
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
        siteService.delete(adminUser, adminPassword, siteName);
    }

    @TestRail (id = "C9462")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteGroup()
    {
        groupsPage.navigate();
        groupsPage.clickBrowseButton();

        groupsPage.checkGroupIsInList(c9462group);

        LOG.info("Step 1: Delete the group created in Precondition.");
        groupsPage.deleteGroup(c9462group, true);

        LOG.info("Step 2: Verify the group is no longer present in the groups list.");
        groupsPage.checkGroupIsRemoved(c9462group);
    }

    @TestRail (id = "C9473")
    @Test (groups = { TestGroup.REGRESSION, TestGroup.ADMIN_TOOLS })
    public void deleteGroupWithMultipleParents()
    {

        groupsPage.navigate();
        groupsPage.writeInSearchInput("C9473ParentGroup");
        groupsPage.clickSearchButton();
        groupsPage.clickBrowseButton();

        LOG.info("STEP1: Click on the name of any group on the first column;");
        groupsPage.clickItemFromList(c9473ParentGroup1);
        Assert.assertTrue(groupsPage.getSecondColumnItemsList().contains(c9473SubGroup));

        LOG.info("STEP2: Click  \"Delete Group\" icon for subgroup in second column;");
        groupsPage.clickDeleteGroupButtonFromSecondColumn(c9473SubGroup);

        LOG.info("STEP3: Select  \"Just delete '" + c9473ParentGroup1 + "' from '" + c9473SubGroup + "'.\" option and click \"Delete\" button;");
        Assert.assertEquals(deleteGroupDialog.getMultiparentMessage(), "The group '" + c9473SubGroup + "' has one or more parents:");
        Assert.assertTrue(deleteGroupDialog.getParent().contains(c9473ParentGroup1) && deleteGroupDialog.getParent().contains(c9473ParentGroup2) && deleteGroupDialog.getParent().contains(c9473ParentGroup3));
        Assert.assertTrue(deleteGroupDialog.isRemoveRadioButtonDisplayed());
        Assert.assertTrue(deleteGroupDialog.isDeleteButtonDisplayed());

        deleteGroupDialog.selectRemoveFromGroupRadio();
        deleteGroupDialog.clickDeleteButton();

        Assert.assertEquals(groupsPage.getDeleteMessage(), "Successfully deleted group '" + c9473SubGroup + "'.");

        groupsPage.clickItemFromList(c9473ParentGroup1);
        Assert.assertFalse(groupsPage.getSecondColumnItemsList().contains(c9473SubGroup));


        LOG.info("STEP4: Click on the name of any other created group on the first column;");
        groupsPage.clickItemFromList(c9473ParentGroup2);
        Assert.assertTrue(groupsPage.getSecondColumnItemsList().contains(c9473SubGroup));

        LOG.info("STEP5: Click  \"Delete Group\" icon for subgroup;");
        groupsPage.clickDeleteGroupButtonFromSecondColumn(c9473SubGroup);
        Assert.assertTrue(deleteGroupDialog.getParent().contains(c9473ParentGroup2) && deleteGroupDialog.getParent().contains(c9473ParentGroup3));

        LOG.info("STEP6: Select \"Remove 'subgroup' from all.\" option and click \"Remove\" button;");
        deleteGroupDialog.selectDeleteGroupRadio();
        deleteGroupDialog.clickDeleteButton();
        Assert.assertEquals(groupsPage.getDeleteMessage(), "Successfully deleted group '" + c9473SubGroup + "'.");

        LOG.info("STEP7: Click on the name of the third group on the first column;");
        groupsPage.clickItemFromList(c9473ParentGroup3);
        Assert.assertFalse(groupsPage.getSecondColumnItemsList().contains(c9473SubGroup));


    }

    @TestRail (id = "C9465")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editGroup()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.clickBrowseButton();
        groupsPage.checkGroupIsInList(c9465group);

        LOG.info("Step 1: Edit the group created in Precondition.");
        groupsPage.editGroup(c9465group, c9465groupEdited, true);

        LOG.info("Step 2: Verify the edited group is present in the groups list.");
        groupsPage.checkGroupIsInList(c9465EditedName);
        groupsPage.checkGroupIsRemoved(c9465group);
    }

    @TestRail (id = "C9469")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createGroup()
    {
        groupsPage.navigate();
        groupsPage.clickBrowseButton();

        LOG.info("Step1: Create a group from Admin Tools page.");
        groupsPage.createNewGroup(c9469group, true);

        LOG.info("Step 2: Verify the group is present in the groups list.");
        groupsPage.checkGroupIsInList(c9462group);
    }

    @TestRail (id = "C9471")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchForAGroup()
    {
        groupsPage.navigate();

        LOG.info("Step 2: Search for the group created in Precondition.");
        groupsPage.writeInSearchInput(c9471group);
        groupsPage.clickSearchButton();
        groupsPage.waitUntilSearchElementDisplayed();


        LOG.info("Step 3: Verify the group is present in the search result list.");
        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(c9471group));
        Assert.assertEquals(groupsPage.getSeachBarText(), String.format("Search for '%s' found 1 results.", c9471group));

        groupsPage.writeInSearchInput("*" + uniqueIdentifier);
        groupsPage.clickSearchButton();
        groupsPage.waitUntilSearchElementDisplayed();

        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(c9471group));
        Assert.assertTrue(groupsPage.isGroupPresentInSearchResult(c9471group1));
    }

    @TestRail (id = "C9460")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyAddedToSiteGroupPresence()
    {
        setupAuthenticatedSession(userName, password);
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");

        siteGroupsPage.navigate(siteName);
        assertEquals(siteGroupsPage.getPageTitle(), "Alfresco » Site Groups", "Displayed page=");

        siteGroupsPage.addGroup();
        assertEquals(addSiteGroupsPage.getPageTitle(), "Alfresco » Add Groups", "Displayed page=");

        addSiteGroupsPage.searchForGroup(c9460group);
        addSiteGroupsPage.addGroup(c9460group);
        addSiteGroupsPage.setGroupRole(c9460group, "Contributor");
        addSiteGroupsPage.clickAddGroupsButton();

        setupAuthenticatedSession(adminUser, adminPassword);
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.clickBrowseButton();

        groupsPage.checkGroupIsInList(c9460group);
    }
}
