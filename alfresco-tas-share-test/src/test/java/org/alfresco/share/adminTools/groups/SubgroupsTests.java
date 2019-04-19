package org.alfresco.share.adminTools.groups;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.*;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.testng.Assert.*;

/**
 * @author Laura.Capsa
 */
public class SubgroupsTests extends ContextAwareWebTest
{
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

    private final String uniqueIdentifier = RandomData.getRandomAlphanumeric();
    private final String name = "firstName";
    private final String userAdmin = "userAdmin" + uniqueIdentifier;
    private final String userModelAdmin = "userModelAdmin" + uniqueIdentifier;
    private final String userToAdd = "C9481User" + uniqueIdentifier;
    private final String group0 = "0group" + uniqueIdentifier;
    private final String group1 = "1group" + uniqueIdentifier;
    private final String group2 = "2group" + uniqueIdentifier;
    private final String group3 = "3group" + uniqueIdentifier;
    private final String groupNameToAdd = "_C9491Group" + uniqueIdentifier;
    private final String groupNameToDelete = "_C42773Group" + uniqueIdentifier;
    private final String displayName = "C9476name" + uniqueIdentifier;

    @BeforeClass(alwaysRun = true)
    public void setupTest()
    {
        userService.create(adminUser, adminPassword, userAdmin, password, domain, name, userAdmin);
        userService.create(adminUser, adminPassword, userModelAdmin, password, domain, name, userModelAdmin);
        userService.create(adminUser, adminPassword, userToAdd, password, domain, name, userToAdd);
        groupService.createGroup(adminUser, adminPassword, group0);
        groupService.createGroup(adminUser, adminPassword, group1);
        groupService.createGroup(adminUser, adminPassword, group2);
        groupService.createGroup(adminUser, adminPassword, group3);
        groupService.createGroup(adminUser, adminPassword, groupNameToAdd);
        groupService.addSubGroup(adminUser, adminPassword, group3, groupNameToDelete);
        groupService.addUserToGroup(adminUser, adminPassword, "ALFRESCO_ADMINISTRATORS", userAdmin);
        groupService.addUserToGroup(adminUser, adminPassword, group3, userModelAdmin);

        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail(id = "C9476")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createSubgroup()
    {
        String identifier = "C9476id" + uniqueIdentifier;

        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        
        groupsPage.writeInSearchInput(group0);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(group0);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(group0));
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
        expectedList = new ArrayList<>(singletonList(group0));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(displayName + " " + "(" + identifier + ")"));
        assertEquals(groupsPage.getSecondColumnItemsList().toString(), expectedList.toString(), "Groups in " + group0 + "= ");
    }

    @TestRail(id = "C9491")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addGroup()
    {
        groupsPage.navigate();

        groupsPage.writeInSearchInput(group1);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(group1);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(group1));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();
        getBrowser().waitInSeconds(5);
        LOG.info("STEP1: Click 'Add Group' button from second column's header");
        groupsPage.clickAddGroupButton();
        assertEquals(addGroupDialog.getDialogTitle(), language.translate("adminTools.groups.addGroupDialog.title"), "Displayed dialog title=");
        assertTrue(addGroupDialog.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(addGroupDialog.isSearchInputFieldDisplayed(), "Search input field is displayed.");

        LOG.info("STEP2: Fill in 'Search' input field with a group name. Click 'Search' button");
        addGroupDialog.searchGroup(groupNameToAdd);
        expectedList = new ArrayList<>(singletonList(groupNameToAdd));
        assertEquals(addGroupDialog.getSearchResultsName(), expectedList,
                "'Add Group' dialog -> search for " + groupNameToAdd + " returns group name results=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList("ID: " + "GROUP_" + groupNameToAdd));
        assertEquals(addGroupDialog.getSearchResultsId(), expectedList, "'Add Group' dialog -> search for " + groupNameToAdd + " returns group id results=");
        expectedList.clear();

        LOG.info("STEP3: Click 'Add' button for group");
        addGroupDialog.clickAddButtonForGroup(groupNameToAdd);
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        expectedList = new ArrayList<>(singletonList(group1));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(groupNameToAdd));
        assertEquals(groupsPage.getSecondColumnItemsList(), expectedList, "Groups in " + group1 + "= ");
    }

    @TestRail(id = "C9481")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addUser()
    {
        String user = name + " " + userToAdd + " (" + userToAdd + ")";

        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(group2);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(group2);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(group2));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");

        expectedList.clear();

        LOG.info("STEP1: Click 'Add Group' button from second column's header");
        assertTrue(groupsPage.isAddUSerButtonDisplayed(), "'Add Users' button is displayed.");
        groupsPage.clickAddUserButton();
        assertEquals(addUserDialog.getDialogTitle(), language.translate("adminTools.groups.addUserDialog.title"), "Displayed dialog title=");
        assertTrue(addUserDialog.isSearchButtonDisplayed(), "'Search' button is displayed.");
        assertTrue(addUserDialog.isSearchInputFieldDisplayed(), "Search input field is displayed.");

        LOG.info("STEP2: Fill in 'Search' input field with a user name. Click 'Search' button");
        addUserDialog.searchGroup(userToAdd);
        expectedList = new ArrayList<>(singletonList(user));
        assertEquals(addUserDialog.getSearchResultsName().toString(), expectedList.toString(), "'Add User' dialog -> search for " + userToAdd + " returns=");
        expectedList.clear();

        LOG.info("STEP3: Click 'Add' button for user");
        addUserDialog.clickAddButtonForUser(user);
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        expectedList = new ArrayList<>(singletonList(group2));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        expectedList = new ArrayList<>(singletonList(user));
        assertEquals(groupsPage.getSecondColumnItemsList(), expectedList, "Users in " + group2 + "= ");
    }

    @TestRail(id = "C9490")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeUser()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(group3);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(group3);
        String user = name + " " + userModelAdmin + " (" + userModelAdmin + ")";
        ArrayList<String> expectedList = new ArrayList<>(singletonList(group3));
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
        expectedList = new ArrayList<>(singletonList(group3));
        assertEquals(groupsPage.getSelectedItems(), expectedList, "Selected items=");
        expectedList.clear();
        assertFalse(groupsPage.getSecondColumnItemsList().contains(user), user + " is displayed for " + group3);
    }

    @TestRail(id = "C42773")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteSubgroup()
    {
        groupsPage.navigate();
        assertEquals(groupsPage.getSectionTitle(), language.translate("adminTools.groups.title"), "Displayed section=");
        groupsPage.writeInSearchInput(group3);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(group3);
        ArrayList<String> expectedList = new ArrayList<>(singletonList(group3));
        assertEquals(groupsPage.getBreadcrumb(), expectedList, "Groups breadcrumb path=");
        expectedList.clear();

        LOG.info("STEP1: Click 'Delete Group' button for the subgroup from second column's header");
        groupsPage.clickDeleteGroupButtonFromSecondColumn(groupNameToDelete);
        assertEquals(deleteGroupDialog.getDialogHeader(), language.translate("adminTools.groups.deleteGroup.header"), "Displayed dialog=");
        assertEquals(deleteGroupDialog.getMultiparentMessage(),
                String.format(language.translate("adminTools.groups.deleteGroup.multiparentMessage"), groupNameToDelete), "Displayed message=");
        assertEquals(deleteGroupDialog.getParent(), group3, "'Delete Group' dialog: Parent of " + groupNameToDelete + " =");
        assertEquals(deleteGroupDialog.getRemoveRow(), String.format(language.translate("adminTools.groups.deleteGroup.removeRow"), groupNameToDelete, group3),
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
        assertFalse(groupsPage.getSecondColumnItemsList().contains(groupNameToDelete), groupNameToDelete + " is displayed for " + group3);
        groupsPage.writeInSearchInput(groupNameToDelete);
        groupsPage.clickBrowseButton();
        groupsPage.clickItemFromList(groupNameToDelete);
        groupsPage.checkGroupIsInList(groupNameToDelete);

    }

    @AfterClass
    public void cleanupRemoveGroups()
    {
        groupService.removeGroup(adminUser, adminPassword, group0);
        groupService.removeGroup(adminUser, adminPassword, group1);
        groupService.removeGroup(adminUser, adminPassword, group2);
        groupService.removeGroup(adminUser, adminPassword, group3);
        groupService.removeGroup(adminUser, adminPassword, groupNameToAdd);
        groupService.removeGroup(adminUser, adminPassword, displayName);
    }
}