package org.alfresco.share.adminTools.users;

import static org.alfresco.share.TestUtils.ALFRESCO_ADMIN_GROUP;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UserProfileAdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.GroupModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.*;

public class UserProfileTests extends BaseTest
{
    private UsersPage usersPage;
    private UserProfileAdminToolsPage userProfileAdminToolsPage;
    private EditUserPage editUserPage;

    private final ThreadLocal<UserModel> user = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        user.set(getDataUser().usingAdmin().createRandomTestUser());

        usersPage = new UsersPage(webDriver);
        userProfileAdminToolsPage = new UserProfileAdminToolsPage(webDriver);
        editUserPage = new EditUserPage(webDriver);

        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C9415")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION})
    public void browseUserProfilePage()
    {
        userProfileAdminToolsPage.navigate(user.get())
            .assertUserIsDisplayedInTitle(user.get())
            .assertEditUserButtonIsDisplayed()
            .assertDeleteUserButtonIsDisplayed()
            .assertGoBackButtonIsDisplayed()
            .assertAllSectionsAreDisplayed()
            .assertUserPhotoIsDisplayed()
            .assertUserFullNameIsDisplayedInAboutSection(user.get())
            .assertAllInfoAreDisplayedInSections();
    }

    @TestRail (id = "C9416")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void browseEditUserAdminPage()
    {
        userProfileAdminToolsPage.navigate(user.get())
            .clickEditUser()
                .assertUserFullNameIsDisplayedInTitle(user.get())
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
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void editingUser()
    {
        UserModel editUser = dataUser.createRandomTestUser();
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
        GroupModel c9423Group = dataGroup.usingAdmin().createRandomGroup();
        editUserPage.navigate(user.get())
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
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void deleteNeverAuthorizedUser()
    {
        UserModel newUser = dataUser.usingAdmin().createRandomTestUser();

        userProfileAdminToolsPage.navigate(newUser)
            .clickDelete()
                .assertDeleteUserDialogIsOpened()
                .assertDeleteUserDialogTextIsCorrect()
                .clickDelete();
        usersPage.waitUntilNotificationMessageDisappears();
        usersPage.navigate().searchUser(newUser.getUsername())
            .usingUser(newUser).assertUserIsNotFound();
    }

    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void deleteAuthorizedUser()
    {
        getUserService().login(user.get().getUsername(), user.get().getPassword());
        userProfileAdminToolsPage.navigate(user.get())
            .clickDelete()
            .assertDeleteUserDialogIsOpened()
            .assertDeleteUserDialogTextIsCorrect()
            .clickDelete();
        usersPage.assertDeleteUserNotificationIsDisplayed();
        usersPage.navigate().searchUser(user.get().getUsername())
            .usingUser(user.get()).assertUserDeleteIconIsDisplayed()
                .assertDeletedIsDisplayed();
    }

    @TestRail (id = "C9427")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void updateUserQuota()
    {
        editUserPage.navigate(user.get())
            .editQuota("50")
            .clickSaveChanges()
                .assertQuotaIs("50 GB");
        webDriver.get().manage().deleteAllCookies();
    }

    @TestRail (id = "C9426")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void enablingAccount()
    {
        editUserPage.navigate(user.get())
            .selectDisabledAccount()
            .clickSaveChanges()
                .assertAccountStatusIsDisabled()
            .clickEditUser()
                .selectEnableAccount()
                .clickSaveChanges()
                    .assertAccountStatusIsEnabled();
    }

    @TestRail (id = "C9434")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeGroupFromUserProfile()
    {
        editUserPage.navigate(user.get())
            .searchGroupWithRetry(ALFRESCO_ADMIN_GROUP)
            .addGroup(ALFRESCO_ADMIN_GROUP)
            .clickSaveChanges()
                .clickEditUser()
                .removeGroup(ALFRESCO_ADMIN_GROUP)
                .clickSaveChanges()
                    .assertGroupIsNotDisplayed(ALFRESCO_ADMIN_GROUP.getGroupIdentifier());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp()
    {
        deleteUsersIfNotNull(user.get());
    }
}