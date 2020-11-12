package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UserProfileAdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserProfileTests extends BaseShareWebTests
{
    private UsersPage usersPage;
    private UserProfileAdminToolsPage userProfileAdminToolsPage;
    private EditUserPage editUserPage;

    private UserModel browseUser, editUser, deleteUser, enableUser, removeGroupUser, addUserToGroup;
    private GroupModel c9423Group;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        usersPage = new UsersPage(browser);
        userProfileAdminToolsPage = new UserProfileAdminToolsPage(browser);
        editUserPage = new EditUserPage(browser);

        setupAuthenticatedSession(getAdminUser());
    }

    @BeforeClass (alwaysRun = true)
    public void dataPrep()
    {
        browseUser = dataUser.usingAdmin().createRandomTestUser();
        editUser = dataUser.createRandomTestUser();
        deleteUser = dataUser.createRandomTestUser();
        enableUser = dataUser.createRandomTestUser();
        removeGroupUser = dataUser.createRandomTestUser();
        addUserToGroup = dataUser.createRandomTestUser();

        c9423Group = dataGroup.usingAdmin().createRandomGroup();
    }

    @TestRail (id = "C9415")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browseUserProfilePage()
    {
        userProfileAdminToolsPage.navigate(browseUser)
            .assertUserIsDisplayedInTitle(browseUser)
            .assertEditUserButtonIsDisplayed()
            .assertDeleteUserButtonIsDisplayed()
            .assertGoBackButtonIsDisplayed()
            .assertAllSectionsAreDisplayed()
            .assertUserPhotoIsDisplayed()
            .assertUserFullNameIsDisplayedInAboutSection(browseUser)
            .assertAllInfoAreDisplayedInSections();
    }

    @TestRail (id = "C9416")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browseEditUserAdminPage()
    {
        userProfileAdminToolsPage.navigate(browseUser)
            .clickEditUser()
                .assertUserFullNameIsDisplayedInTitle(browseUser)
                .assertAllSectionsAreDisplayed()
                .assertSaveChangesButtonIsDisplayed()
                .assertCancelButtonIsDisplayed()
                .assertFirstNameFieldIsDisplayed()
                .assertLastNameFieldIsDisplayed()
                .assertEmailFieldIsDisplayed()
                .assertGroupSearchFieldIsDisplayed()
                .assertQuotaFieldIsDisplayed()
                .assertNewPasswordFieldIsDisplayed()
                .assertVerifyPasswordFieldIsDisplayed()
                .assertDisableAccountIsDisplayed()
                .assertSearchGroupButtonIsDisplayed()
                .assertPhotoIsDisplayed()
                .assertUseDefaultButtonIsDisplayed();
    }

    @TestRail (id = "C9417")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editingUser()
    {
        String firstName = "c9417editedFN";
        String lastName = "c9417editedLN";
        String email = "c9417edited@editedEmail.com";

        editUser.setFirstName(firstName);
        editUser.setLastName(lastName);

       editUserPage.navigate(editUser)
           .editFirstName(firstName)
           .editLastNameField(lastName)
           .editEmailField(email)
           .clickSaveChanges()
                .assertUserFullNameIsDisplayedInAboutSection(editUser)
                .assertEmailIs(email);
    }

    @TestRail (id = "C9423")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addUserToGroup()
    {
        editUserPage.navigate(addUserToGroup)
            .searchGroupWithRetry(c9423Group)
                .assertGroupIsFound(c9423Group)
                .assertAddButtonIsDisplayedForGroup(c9423Group)
                .addGroup(c9423Group)
                    .assertGroupIsAdded(c9423Group)
                .removeGroup(c9423Group)
                    .assertGroupIsNotAdded(c9423Group)
                .addGroup(c9423Group)
                .clickSaveChanges()
                    .assertGroupsAreDisplayed(c9423Group.getGroupIdentifier());
    }

    @TestRail (id = "C9431")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteNeverAuthorizedUser()
    {
        UserModel newUser = dataUser.usingAdmin().createRandomTestUser();

        userProfileAdminToolsPage.navigate(newUser)
            .clickDelete()
                .assertDeleteUserDialogIsOpened()
                .assertDeleteUserDialogTextIsCorrect()
                .clickDelete();
        usersPage.waitUntilNotificationMessageDisappears();
        usersPage.searchUser(newUser.getUsername())
            .usingUser(newUser).assertUserIsNotFound();
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deleteAuthorizedUser()
    {
        cmisApi.authenticateUser(deleteUser);

        userProfileAdminToolsPage.navigate(deleteUser)
            .clickDelete()
            .assertDeleteUserDialogIsOpened()
            .assertDeleteUserDialogTextIsCorrect()
            .clickDelete();
        usersPage.assertDeleteUserNotificationIsDisplayed()
            .searchUser(deleteUser.getUsername())
            .usingUser(deleteUser).assertUserDeleteIconIsDisplayed()
                .assertDeletedIsDisplayed();
    }

    @TestRail (id = "C9427")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateUserQuota()
    {
        editUserPage.navigate(browseUser)
            .editQuota("50")
            .clickSaveChanges()
                .assertQuotaIs("50 GB");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9426")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void enablingAccount()
    {
        editUserPage.navigate(editUser)
            .clickDisabledAccount()
            .clickSaveChanges()
                .assertAccountStatusIsDisabled()
            .clickEditUser()
                .clickDisabledAccount()
                .clickSaveChanges()
                    .assertAccountStatusIsEnabled();
    }

    @TestRail (id = "C9434")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeGroupFromUserProfile()
    {
        editUserPage.navigate(removeGroupUser)
            .searchGroupWithRetry(ALFRESCO_ADMIN_GROUP)
            .addGroup(ALFRESCO_ADMIN_GROUP)
            .clickSaveChanges()
                .clickEditUser()
                .removeGroup(ALFRESCO_ADMIN_GROUP)
                .clickSaveChanges()
                    .assertGroupIsNotDisplayed(ALFRESCO_ADMIN_GROUP.getGroupIdentifier());
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(browseUser, editUser, enableUser, removeGroupUser, addUserToGroup);
        dataGroup.deleteGroup(c9423Group);
    }
}