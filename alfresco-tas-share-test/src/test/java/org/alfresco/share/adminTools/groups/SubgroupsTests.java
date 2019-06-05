package org.alfresco.share.adminTools.groups;

import static java.util.Collections.singletonList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AddGroupDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AddUserDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.DeleteGroupDialog;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.RemoveUserFromGroupDialog;
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
 * @author Laura.Capsa
 */
public class SubgroupsTests extends ContextAwareWebTest
{
    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String firstName = "firstName";
    private final String userAdmin = "userAdmin" + uniqueIdentifier;
    private final String userModelAdmin = "userModelAdmin" + uniqueIdentifier;
    private final String userToAdd = "C9481User_" + uniqueIdentifier;
    private final String C9485lastName = String.format("C9485User_%s", uniqueIdentifier);
    private final String C9476group = "C9476group_" + uniqueIdentifier;
    private final String C9491group = "C9491group_" + uniqueIdentifier;
    private final String C9481group = "C9481group_" + uniqueIdentifier;
    private final String C9490group = "C9490group_" + uniqueIdentifier;
    private final String C9478group = "C9478group_" + uniqueIdentifier;
    private final String C9485group = "C9485group_" + uniqueIdentifier;
    private final String groupNameToAdd = "_C9491Group" + uniqueIdentifier;
    private final String groupNameToDelete = "_C42773Group" + uniqueIdentifier;
    private final String displayName = "C9476name" + uniqueIdentifier;
    @Autowired
    private GroupsPage groupsPage;
    @Autowired
    private AddGroupDialog addGroupDialog;
    @Autowired
    private RemoveUserFromGroupDialog removeUserFromGroupDialog;
    @Autowired
    private DeleteGroupDialog deleteGroupDialog;
    @Autowired
    private AddUserDialog addUserDialog;

    @BeforeClass (alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userAdmin, password, domain, firstName, userAdmin);
        userService.create(adminUser, adminPassword, userModelAdmin, password, domain, firstName, userModelAdmin);
        userService.create(adminUser, adminPassword, userToAdd, password, domain, firstName, userToAdd);
        userService.create(adminUser, adminPassword, C9485lastName, password, domain, firstName, C9485lastName);
        groupService.createGroup(adminUser, adminPassword, C9476group);
        groupService.createGroup(adminUser, adminPassword, C9491group);
        groupService.createGroup(adminUser, adminPassword, C9481group);
        groupService.createGroup(adminUser, adminPassword, C9490group);
        groupService.createGroup(adminUser, adminPassword, C9478group);
        groupService.createGroup(adminUser, adminPassword, C9485group);
        groupService.createGroup(adminUser, adminPassword, groupNameToAdd);
        groupService.addSubGroup(adminUser, adminPassword, C9490group, groupNameToDelete);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, C9490group, userModelAdmin);

        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail (id = "C9476")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createSubgroup()
    {
        String identifier = "C9476id" + uniqueIdentifier;

        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");

        groupsPage.writeInSearchInput(C9476group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9476group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9476group));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();

        LOG.info("STEP1: Click 'New Subgroup' button from second column's header");
        groupsPage.clickNewSubgroupButton();
        assertEquals(groupsPage.getNewGroupTitle(), language.translate("adminTools.groups.newGroupPanelTitle"), "'New Group' title=");
        assertTrue(groupsPage.isIdentifierInputFieldDisplayed(), "Properties: 'Identifier' input field is displayed.");
        assertTrue(groupsPage.isDisplayNameInputFieldDisplayed(), "Properties: 'Display Name' input field is displayed.");
        expectedList = new ArrayList<>(Arrays.asList(language.translate("adminTools.groups.newGroupProperties.identifier"),
            language.translate("adminTools.groups.newGroupProperties.displayName")));
        assertEquals(groupsPage.getNewGroupPropertiesLabels(), expectedList, "New Group -> Properties form labels=");
        expectedList.clear();
        assertTrue(groupsPage.isCreateNewGroupButtonDisplayed(), "New Group: 'Create Group' button is displayed.");
        assertTrue(groupsPage.isCreateAndCreateAnotherGroupButtonDisplayed(), "New Group: 'Create and Create Another' button is displayed.");
        assertTrue(groupsPage.isCancelCreateNewGroupButtonDisplayed(), "New Group: 'Cancel' button is displayed.");

        LOG.info("STEP2: Fill in 'Identifier' and 'Display Name' input fields. Click 'Create Group' button");
        groupsPage.typeGroupIdentifier(identifier);
        groupsPage.typeGroupDisplayName(displayName);
        groupsPage.clickCreateGroupButton();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        expectedList = new ArrayList<>(singletonList(C9476group));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(displayName + " " + "(" + identifier + ")"));
        assertEquals(groupsPage.getSecondColumnItemsList().toString(), expectedList.toString(), "Groups in " + C9476group + "= ");
    }


    @TestRail (id = "C9491")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addGroup()
    {
        groupsPage.navigate();

        groupsPage.writeInSearchInput(C9491group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9491group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9491group));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();

        LOG.info("STEP1: Click 'Add Group' button from second column's header");
        groupsPage.clickAddGroupButton();
        assertEquals(addGroupDialog.getDialogTitle(), language.translate("adminTools.groups.addGroupDialog.title"), "Displayed dialog title=");
        assertTrue(addGroupDialog.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(addGroupDialog.isSearchInputFieldDisplayed(), "Search input field is displayed.");

        LOG.info("STEP2: Fill in 'Search' input field with a group name. Click 'Search' button");
        addGroupDialog.searchGroup(groupNameToAdd);
        expectedList = new ArrayList<>(singletonList(groupNameToAdd));
        assertEquals(addGroupDialog.getSearchResultsName(), expectedList,
            "'Add Group' dialog -> search for " + groupNameToAdd + " returns group firstName results=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList("ID: " + "GROUP_" + groupNameToAdd));
        assertEquals(addGroupDialog.getSearchResultsId(), expectedList, "'Add Group' dialog -> search for " + groupNameToAdd + " returns group id results=");
        expectedList.clear();

        LOG.info("STEP3: Click 'Add' button for group");
        addGroupDialog.clickAddButtonForGroup(groupNameToAdd);
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        expectedList = new ArrayList<>(singletonList(C9491group));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(groupNameToAdd));
        assertEquals(groupsPage.getSecondColumnItemsList(), expectedList, "Groups in " + C9491group + "= ");
    }


    @TestRail (id = "C9481")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addUser()
    {
        String user = firstName + " " + userToAdd + " (" + userToAdd + ")";

        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(C9481group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9481group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9481group));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");

        expectedList.clear();

        LOG.info("STEP1: Click 'Add Group' button from second column's header");
        assertTrue(groupsPage.isAddUserButtonDisplayed(), "'Add Users' button is displayed.");
        groupsPage.clickAddUserButton();
        assertEquals(addUserDialog.getDialogTitle(), language.translate("adminTools.groups.addUserDialog.title"), "Displayed dialog title=");
        assertTrue(addUserDialog.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(addUserDialog.isSearchInputFieldDisplayed(), "Search input field is displayed.");

        LOG.info("STEP2: Fill in 'Search' input field with a user name. Click 'Search' button");
        addUserDialog.searchUser(userToAdd);
        expectedList = new ArrayList<>(singletonList(user));
        assertEquals(addUserDialog.getSearchResultsName().toString(), expectedList.toString(), "'Add User' dialog -> search for " + userToAdd + " returns=");
        expectedList.clear();

        LOG.info("STEP3: Click 'Add' button for user");
        addUserDialog.clickAddButtonForUser(user);
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        expectedList = new ArrayList<>(singletonList(C9481group));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(user));
        assertEquals(groupsPage.getSecondColumnItemsList(), expectedList, "Users in " + C9481group + "= ");
    }


    @TestRail (id = "C9490")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeUser()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(C9490group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9490group);
        String user = firstName + " " + userModelAdmin + " (" + userModelAdmin + ")";
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9490group));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();

        LOG.info("STEP1: Click 'Remove User' button for the user, from second column's header");
        groupsPage.clickRemoveUserIcon(user);
        assertEquals(removeUserFromGroupDialog.getDialogHeader(), language.translate("adminTools.groups.removeUser.title"), "Displayed dialog=");
        assertEquals(removeUserFromGroupDialog.getDialogMessage(), String.format(language.translate("adminTools.groups.removeUser.message"), user),
            "Dialog message=");
        assertTrue(removeUserFromGroupDialog.isYesButtonDisplayed(), "'Remove user from Group' dialog: 'Yes' button is displayed.");
        assertTrue(removeUserFromGroupDialog.isNoButtonDisplayed(), "'Remove user from Group' dialog: 'No' button is displayed.");

        LOG.info("STEP2: Click 'Yes' button");
        removeUserFromGroupDialog.clickYesButton();
        expectedList = new ArrayList<>(singletonList(C9490group));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        assertFalse(groupsPage.getSecondColumnItemsList().contains(user), user + " is displayed for " + C9490group);
    }


    @TestRail (id = "C42773")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteSubgroup()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(C9490group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9490group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9490group));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();

        LOG.info("STEP1: Click 'Delete Group' button for the subgroup from second column's header");
        groupsPage.clickDeleteGroupButtonFromSecondColumn(groupNameToDelete);
        assertEquals(deleteGroupDialog.getDialogHeader(), language.translate("adminTools.groups.deleteGroup.header"), "Displayed dialog=");
        assertEquals(deleteGroupDialog.getMultiparentMessage(),
            String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupNameToDelete), "Displayed message=");
        assertEquals(deleteGroupDialog.getParent(), C9490group, "'Delete Group' dialog: Parent of " + groupNameToDelete + " =");
        assertEquals(deleteGroupDialog.getRemoveRow(), String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupNameToDelete, C9490group),
            "'Delete Group' dialog: remove row=");
        assertEquals(deleteGroupDialog.getDeleteRow(), String.format(language.translate("adminTools.groups.deleteGroup.deleteRow"), groupNameToDelete),
            "'Delete Group' dialog: delete row=");
        assertTrue(deleteGroupDialog.isDeleteButtonDisplayed(), "'Delete Group' dialog: 'Delete' button is displayed.");
        assertTrue(deleteGroupDialog.isCancelButtonDisplayed(), "'Delete Group' dialog: 'Cancel' button is displayed.");
        assertTrue(deleteGroupDialog.isRemoveRadioButtonDisplayed(), "Remove radio button is displayed.");
        assertTrue(deleteGroupDialog.isDeleteRadioButtonDisplayed(), "Delete radio button is displayed.");
        assertTrue(deleteGroupDialog.isRemoveRadioButtonSelected(), "Remove radio button is selected.");
        assertFalse(deleteGroupDialog.isDeleteRadioButtonSelected(), "Delete radio button is selected.");

        LOG.info("STEP2: Click 'Delete' button");
        deleteGroupDialog.clickDeleteButton();
        assertFalse(groupsPage.getSecondColumnItemsList().contains(groupNameToDelete), "'" + groupNameToDelete + "' is displayed for " + "'" + C9490group + "'.");
        groupsPage.writeInSearchInput(groupNameToDelete);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(groupNameToDelete);
        groupsPage.checkGroupIsInList(groupNameToDelete);

    }


    @TestRail (id = "C9478")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchGroup()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");

        LOG.info("STEP1: Search for a group and click on it.");
        groupsPage.writeInSearchInput(C9478group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9478group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9478group));
        Assert.assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path= ");
        System.out.println("EXPECTED RESULT 1: Check if \"New Group\", \"Add Group\" and \"Add User\" buttons are displayed.");
        Assert.assertTrue(groupsPage.isNewGroupButtonDisplayed(), "\"New Group\" button is not displayed.");
        Assert.assertTrue(groupsPage.isAddGroupButtonDisplayed(), "\"Add Group\" button is not displayed.");
        Assert.assertTrue(groupsPage.isAddUserButtonDisplayed(), "\"Add User\" button is not displayed");

        LOG.info("STEP2: Click \"Add Group\" icon.");
        groupsPage.clickAddGroupButton();
        Assert.assertEquals(addGroupDialog.getDialogTitle(), language.translate("adminTools.groups.addGroupDialog.title"), "Displayed dialog title=");
        System.out.println("EXPECTED RESULT 2.1: Check if 'Add Group' dialog is displayed with 'Search' button and input field ");
        Assert.assertTrue(addGroupDialog.isSearchButtonDisplayed(), "'Search' button is not displayed.");
        Assert.assertTrue(addGroupDialog.isSearchInputFieldDisplayed(), "Search input field is not displayed.");
        System.out.println("EXPECTED RESULT 2.2: Check if \"X\" button is displayed in the form.");
        Assert.assertTrue(addGroupDialog.isCloseButtonDisplayed(), "\"X\" button is not displayed");

        LOG.info("STEP3: Enter into search field any valid search request (firstName of the created group).");
        addGroupDialog.searchGroup(C9478group);
        System.out.println("EXPECTED RESULT 3.1: Check if created group is displayed in the table with \"Display Name\".");
        int elementIndex = addGroupDialog.getItemIndexFromSearchResults(C9478group);
        Assert.assertEquals(addGroupDialog.getSearchResultsName().get(elementIndex), C9478group, "'Add Group' dialog -> search for " + C9478group + " returns group firstName results=");
        System.out.println("EXPECTED RESULT 3.2: Check if created group is displayed in the table with \"Name group\".");
        String groupId = "ID: " + "GROUP_" + C9478group;
        Assert.assertEquals(addGroupDialog.getSearchResultsId().get(elementIndex), groupId, "'Add Group' dialog -> search for " + C9478group + " returns group id results=");
        System.out.println("EXPECTED RESULT 3.3: Check if created group is displayed in the table with \"Add\" button displayed.");
        Assert.assertTrue(addGroupDialog.isAddButtonDisplayed(C9478group), "The \"Add\" button for the group " + C9478group + " is not displayed.");
    }


    @TestRail (id = "C9485")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void searchUser()
    {
        String C9485fullName = firstName + " " + C9485lastName + " (" + C9485lastName + ")";

        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");

        LOG.info("STEP1: Search for a group and click on it.");
        groupsPage.writeInSearchInput(C9485group);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(C9485group);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(C9485group));
        Assert.assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path= ");
        System.out.println("EXPECTED RESULT 1: Check if \"New Group\", \"Add Group\" and \"Add User\" buttons are displayed.");
        Assert.assertTrue(groupsPage.isNewGroupButtonDisplayed(), "\"New Group\" button is not displayed.");
        Assert.assertTrue(groupsPage.isAddGroupButtonDisplayed(), "\"Add Group\" button is not displayed.");
        Assert.assertTrue(groupsPage.isAddUserButtonDisplayed(), "\"Add User\" button is not displayed");

        LOG.info("STEP2: Click \"Add User\" icon.");
        groupsPage.clickAddUserButton();
        Assert.assertEquals(addUserDialog.getDialogTitle(), language.translate("adminTools.groups.addUserDialog.title"), "Displayed dialog title=");
        System.out.println("EXPECTED RESULT 2.1: Check if 'Add Group' dialog is displayed with 'Search' button and input field ");
        Assert.assertTrue(addUserDialog.isSearchButtonDisplayed(), "'Search' button is not displayed.");
        Assert.assertTrue(addUserDialog.isSearchInputFieldDisplayed(), "Search input field is not displayed.");
        System.out.println("EXPECTED RESULT 2.2: Check if \"X\" button is displayed in the form.");
        Assert.assertTrue(addUserDialog.isCloseButtonDisplayed(), "\"X\" button is not displayed in the form.");

        LOG.info("STEP3: Enter into search field any valid search request (firstName of the created user);");
        addUserDialog.searchUser(C9485lastName);

        LOG.info("STEP4: Click \"Search\" button.");
        addUserDialog.clickSearchButton();
        System.out.println("EXPECTED RESULT 4.1: Check if the created user is displayed in the table with user full firstName + his username.");
        int elementIndex = addUserDialog.getItemIndexFromSearchResults(C9485fullName);
        Assert.assertEquals(addUserDialog.getSearchResultsName().get(elementIndex), C9485fullName, "The created user is not displayed in the table with user full firstName.");
        System.out.println("EXPECTED RESULT 4.2: Check if the created user is displayed in the table with \"Add\" button displayed.");
        Assert.assertTrue(addUserDialog.isAddButtonDisplayed(C9485fullName), "The \"Add\" button for the created user " + C9485lastName + " is not displayed.");
    }


    @AfterClass
    public void cleanupRemoveGroups()
    {
        groupService.removeGroup(adminUser, adminPassword, C9476group);
        groupService.removeGroup(adminUser, adminPassword, C9491group);
        groupService.removeGroup(adminUser, adminPassword, C9481group);
        groupService.removeGroup(adminUser, adminPassword, C9490group);
        groupService.removeGroup(adminUser, adminPassword, C9478group);
        groupService.removeGroup(adminUser, adminPassword, C9485group);
        groupService.removeGroup(adminUser, adminPassword, groupNameToAdd);
        groupService.removeGroup(adminUser, adminPassword, displayName);
        userService.delete(adminUser, adminPassword, userAdmin);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userAdmin);
        siteService.delete(adminUser, adminPassword, userModelAdmin);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userModelAdmin);
        siteService.delete(adminUser, adminPassword, userToAdd);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userToAdd);

    }
}