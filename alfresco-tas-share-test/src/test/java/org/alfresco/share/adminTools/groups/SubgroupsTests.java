package org.alfresco.share.adminTools.groups;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.GroupsPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Bogdan Bocancea
 */
public class SubgroupsTests extends BaseShareWebTests
{
    private GroupModel parentGroup;
    private UserModel userToAdd, userToRemove;

    private GroupsPage groupsPage;

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        userToAdd = dataUser.usingAdmin().createRandomTestUser();
        userToRemove = dataUser.usingAdmin().createRandomTestUser();

        parentGroup = dataGroup.usingAdmin().createRandomGroup();
        dataGroup.usingUser(userToRemove).addUserToGroup(parentGroup);
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        groupsPage = new GroupsPage(browser);
        setupAuthenticatedSession(getAdminUser());
        groupsPage.navigate();
    }

    @TestRail (id = "C9476")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createSubgroup()
    {
        GroupModel subGroup = new GroupModel(RandomData.getRandomAlphanumeric());
        groupsPage.writeInSearchInput(parentGroup.getDisplayName())
            .clickBrowse()
            .selectGroup(parentGroup)
            .assertGroupIsInBreadcrumbPath(parentGroup)
            .clickCreateNewGroup(2)
                .assertNewGroupTitleIsDisplayed()
                .assertIdentifierInputFieldIsDisplayed()
                .assertDisplayNameInputFieldIsDisplayed()
                .assertGroupIdentifierFieldLabelIsCorrect()
                .assertDisplayNameFieldLabelIsCorrect()
                .assertCreateNewGroupButtonIsDisplayed()
                .assertCreateAndCreateAnotherGroupButtonDisplayed()
                .assertCancelCreateNewGroupButtonDisplayed()
                .createGroup(subGroup)
                .assertColumnContainsGroup(2, subGroup.getGroupIdentifier());
        dataGroup.usingAdmin().deleteGroup(subGroup);
    }

    @TestRail (id = "C9491, C9478")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addGroup()
    {
        GroupModel addedSubGroup = dataGroup.usingAdmin().createRandomGroup();

        groupsPage.writeInSearchInput(parentGroup.getGroupIdentifier())
            .clickBrowse()
            .selectGroup(parentGroup)
            .assertGroupIsInBreadcrumbPath(parentGroup)
            .clickAddGroup(2)
                .assertAddGroupDialogTitleIsCorrect()
                .assertSearchButtonIsDisplayed()
                .assertSearchInputIsDisplayed()
                .searchGroup(addedSubGroup.getDisplayName())
                .assertGroupDisplayNameIsFound(addedSubGroup)
                .assertGroupIdIsFound(addedSubGroup)
                .addGroup(addedSubGroup);
        groupsPage.waitUntilNotificationMessageDisappears();
        groupsPage.assertColumnContainsGroup(2, addedSubGroup.getDisplayName());
        dataGroup.usingAdmin().deleteGroup(addedSubGroup);
    }

    @TestRail (id = "C9481, C9485")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addUser()
    {
        groupsPage.writeInSearchInput(parentGroup.getDisplayName())
            .clickBrowse()
            .selectGroup(parentGroup)
            .assertAddUserButtonIsDisplayed(2)
            .clickAddUser(2)
                .assertAddUserDialogTitleIsCorrect()
                .assertSearchButtonIsDisplayed()
                .assertSearchInputIsDisplayed()
                .searchUser(userToAdd)
                .assertUserIsFound(userToAdd)
                .assertAddButtonIsDisplayedForUser(userToAdd)
                .addUser(userToAdd);
        groupsPage.assertColumnContainsUser(2, userToAdd);
    }

    @TestRail (id = "C9490")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeUser()
    {
        groupsPage.writeInSearchInput(parentGroup.getDisplayName())
            .clickBrowse()
            .selectGroup(parentGroup)
            .clickRemoveUser(userToRemove)
                .assertDialogTitleIsCorrect()
                .assertRemoveUserDialogMessageIsCorrect(userToRemove)
                .assertYesButtonIsDisplayed()
                .assertNoButtonIsDisplayed()
                .clickYes();
        groupsPage.assertUserIsNotDisplayed(userToRemove);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupRemoveGroups()
    {
        dataGroup.usingAdmin().deleteGroup(parentGroup);
        removeUserFromAlfresco(userToAdd, userToRemove);
    }
}