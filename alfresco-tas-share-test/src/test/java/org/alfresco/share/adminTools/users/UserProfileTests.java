package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.UserDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.DialogPages.DeleteUserDialogPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.CreateUsers;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.EditUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UserProfileAdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Created by Mirela Tifui on 12/8/2016.
 */
public class UserProfileTests extends ContextAwareWebTest
{
    @Autowired
    UsersPage usersPage;

    @Autowired
    AdminToolsPage adminTools;

    @Autowired
    CreateUsers createUsers;

    @Autowired
    UserProfileAdminToolsPage userProfileAdminToolsPage;

    @Autowired
    DeleteUserDialogPage deleteUserDialogPage;

    @Autowired
    LoginPage loginPage;

    @Autowired
    UserDashboardPage userDashboardPage;

    @Autowired
    EditUserPage editUserPage;

    private UserModel browseUser, editUser, deleteUser, addUserQuota, enableUser, removeGroupUser, addUserToGroup;
    private String c9423Group = String.format("c9423Group%s", RandomData.getRandomAlphanumeric());

    @BeforeClass (alwaysRun = true)
    public void beforeClass()
    {
        browseUser = dataUser.usingAdmin().createRandomTestUser();
        editUser = dataUser.createRandomTestUser();
        deleteUser = dataUser.createRandomTestUser();
        addUserQuota = dataUser.createRandomTestUser();
        enableUser = dataUser.createRandomTestUser();
        removeGroupUser = dataUser.createRandomTestUser();
        addUserToGroup = dataUser.createRandomTestUser();

        groupService.createGroup(adminUser, adminPassword, c9423Group);
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp()
    {
        removeUserFromAlfresco(browseUser, editUser, addUserQuota, enableUser, removeGroupUser, addUserToGroup);
        groupService.removeGroup(adminUser, adminPassword, c9423Group);
    }

    @TestRail (id = "C9415")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browsingNewUserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        adminTools.navigate();
        LOG.info("Step 1: On the Admin Console page click Users;");
        adminTools.navigateToNodeFromToolsPanel("Users", usersPage);
        Assert.assertEquals(usersPage.getRelativePath(), "share/page/console/admin-console/users", "User is not on Users page");

        LOG.info("Step 2: On the Users page search for test user");
        usersPage.searchUser(browseUser.getUsername());
        assertTrue(usersPage.isUserFound(browseUser.getUsername()), "User " + browseUser.getUsername() + " displayed");

        LOG.info("Step 3: Click on the test user name displayed in search results");
        String fullName = getUserFullName(browseUser);
        usersPage.clickUserLink(fullName);
        Assert.assertEquals(userProfileAdminToolsPage.getUserNameInPageTitle(), fullName, "User is not opened in User Profile");

        LOG.info("Step 4: Verify \"User Profile: \" page");
        Assert.assertTrue(userProfileAdminToolsPage.isEditUserButtonDisplayed(), "Edit User button is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isDeleteUserButtonDisplayed(), "Delete User button is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isGoBackButtonDisplayed(), "Go Back button is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isSectionDisplayed("About"), "About section is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isSectionDisplayed("Contact Information"), "Contact Information section is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isSectionDisplayed("Company Details"), "Company Details section is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isSectionDisplayed("More about this user"), "More about this user section is not displayed");

        LOG.info("Step 5: Verify \"About\" pane");
        Assert.assertTrue(userProfileAdminToolsPage.isUserPhotoDisplayed(), "User Photo is not displayed in the About section");
        Assert.assertEquals(userProfileAdminToolsPage.getTheUserNameInAboutSection(), fullName, "User name displayed in the About section is not correct");

        LOG.info("Step 6: Verify \"Contact Information\" pane");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Email:"), "Email is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Telephone:"), "Telephone is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Mobile:"), "Mobile is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Skype:"), "Skype is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("IM:"), "IM is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Google Username:"), "Google Username is not displayed");

        LOG.info("Step 7: Verify \"Company Details\" pane");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Name:"), "Name is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Address:"), "Address is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Telephone:"), "Telephone is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Fax:"), "Fax is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Email:"), "Email is not displayed");

        LOG.info("Step 8: Verify \"More about this User\" pane");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("User Name:"), "User Name is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Groups:"), "Groups is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Account Status:"), "Account Status is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Quota:"), "Quota is not displayed");
        Assert.assertTrue(userProfileAdminToolsPage.isInfoDisplayedInSection("Usage:"), "Usage is not displayed");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9416")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browsingEditUserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = getUserFullName(browseUser);
        usersPage.navigate();
        usersPage.searchUser(browseUser.getUsername());
        usersPage.clickUserLink(fullName);

        LOG.info("Step 1: Click \"Edit User\" button");
        userProfileAdminToolsPage.clickEditUserButton();
        Assert.assertEquals(editUserPage.getUserInEditUserPageTitle(fullName), fullName, "User is not displayed in Edit User page");

        LOG.info("Step 2: Verify \"Edit User\" page ");
        Assert.assertTrue(editUserPage.isSectionDisplayed("Info"), "Info is not displayed on the Edit User page");
        Assert.assertTrue(editUserPage.isSectionDisplayed("About User"), "About User is not displayed on the Edit User page");
        Assert.assertTrue(editUserPage.isSectionDisplayed("Photo"), "Photo is not displayed on the Edit User page");
        Assert.assertTrue(editUserPage.isSaveChangesButtonDisplayed(), "Save changed button is not displayed");
        Assert.assertTrue(editUserPage.isCancelButtonDisplayed(), "Cancel button is not displayed");

        LOG.info("Step 3: Verify \"Info\" pane");
        Assert.assertTrue(editUserPage.isFirstNameFieldDisplayed(), "First Name field is not displayed");
        Assert.assertTrue(editUserPage.isLastNameFieldDisplayed(), "Last Name field is not displayed");
        Assert.assertTrue(editUserPage.isEmailFieldDisplayed(), "Email field is not displayed");

        LOG.info("Step 4: Verify \"About the User\" pane");
        Assert.assertTrue(editUserPage.isGroupsInputFieldDisplayed(), "Groups input field is not displayed");
        Assert.assertTrue(editUserPage.isQuotaFieldDisplayed(), "Quota field is not displayed");
        Assert.assertTrue(editUserPage.isNewPasswordFieldDisplayed(), "New Password field is not displayed ");
        Assert.assertTrue(editUserPage.isVerifyPasswordFieldDisplayed(), "Verify Password field is not displayed");
        Assert.assertTrue(editUserPage.isDisableAccountFieldDisplayed(), "Disable Account field is not displayed");
        Assert.assertTrue(editUserPage.isSearchButtonDisplayed(), "Search button is not displayed");

        LOG.info("Step 5: Verify \"Photo\" pane");
        Assert.assertTrue(editUserPage.isEditPhotoFieldDisplayed(), "Photo field is not displayed");
        Assert.assertTrue(editUserPage.isUseDefaultButtonDisplayed(), "Use Default button is not displayed");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9417")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editingUser()
    {
        String firstName = "c9417editedFN";
        String lastName = "c9417editedLN";
        String email = "c9417edited@editedEmail.com";
        String expectedUserName = firstName + " " + lastName;

        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = getUserFullName(editUser);

        usersPage.navigate();
        usersPage.searchUser(editUser.getUsername());
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();

        LOG.info("Step 1: Edit first name, last name and email for test user");
        editUserPage.editFirstName(firstName);
        editUserPage.editLastNameField(lastName);
        editUserPage.editEmailField(email);
        editUserPage.clickSaveChangesButton();

        Assert.assertEquals(userProfileAdminToolsPage.getUserName(), expectedUserName, "User name is not correct");
        Assert.assertEquals(userProfileAdminToolsPage.getEmail(), email, "Email is not correct");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9423")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addUserToGroup()
    {
        setupAuthenticatedSession(adminUser, adminPassword);

        String fullName = addUserToGroup.getFirstName() + " " + addUserToGroup.getLastName();

        usersPage.navigate();
        usersPage.searchUser(addUserToGroup.getUsername());
        usersPage.clickUserLink(fullName);
        LOG.info("Open \"Edit user\" page");
        userProfileAdminToolsPage.clickEditUserButton();

        LOG.info("Step 1. Enter into search field on Groups pane the name of the created group \"" + c9423Group + "\" and click the Search button;");
        editUserPage.editGroupsField(c9423Group);
        editUserPage.clickSearchGroupButton();

        LOG.info("Check if group \"" + c9423Group + "\" is displayed in the list of groups with \"Add\" button;");
        Assert.assertTrue(editUserPage.isGroupInSearchResults(c9423Group), "The group \"" + c9423Group + "\" is not in search results");
        Assert.assertTrue(editUserPage.isAddButtonDisplayed(c9423Group), "The \"Add\" button for " + c9423Group + " is not displayed");

        LOG.info("Step 2. Click the Add button for \"" + c9423Group + "\"");
        editUserPage.addGroup(c9423Group);
        Assert.assertTrue(editUserPage.isRemoveGroupDispalyed(c9423Group));

        LOG.info("Step 3. Click \"Remove Group\" icon for added group \"" + c9423Group + "\";");
        editUserPage.removeGroup(c9423Group);
        Assert.assertFalse(editUserPage.isRemoveGroupDispalyed(c9423Group));

        LOG.info("Step 4. Click the Add button for \"" + c9423Group + "\"");
        editUserPage.addGroup(c9423Group);
        Assert.assertTrue(editUserPage.isRemoveGroupDispalyed(c9423Group));

        LOG.info("Step 5. Click \"Save Changes\" button;");
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getUserNameInPageTitle(), fullName);

        LOG.info("Step 6. Verify that user \"" + addUserToGroup.getUsername() + "\" has been added to group \"" + c9423Group + "\".");
        Assert.assertEquals(userProfileAdminToolsPage.getGroupName(), c9423Group);
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9431")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deletingAUser()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = getUserFullName(deleteUser);
        usersPage.navigate();
        usersPage.searchUser(deleteUser.getUsername());
        usersPage.clickUserLink(fullName);

        LOG.info("Step 1:On the User Profile page for user1 click the Delete User button");
        userProfileAdminToolsPage.clickDelete();
        Assert.assertTrue(deleteUserDialogPage.isDeleteUserWindowDisplayed(), "Delete User window is not displayed");
        Assert.assertEquals(deleteUserDialogPage.getDeleteUserWindowText(), language.translate("deleteUser.dialog"));

        LOG.info("Step 2: Click the Delete button on the Delete User pop-up window");
        deleteUserDialogPage.clickButton("Delete", usersPage);
        deleteUserDialogPage.waitUntilMessageDisappears();

        LOG.info("Step 4: Logout and Login using c9431User");
        cleanupAuthenticatedSession();
        loginPage.navigate();
        loginPage.login(deleteUser);
        loginPage.assertAuthenticationErrorIsDisplayed().assertAuthenticationErrorMessageIsCorrect();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9427")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addingQuotaToUser()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = getUserFullName(addUserQuota);
        usersPage.navigate();
        usersPage.searchUser(addUserQuota.getUsername());
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();

        LOG.info("Step 1: Edit quota to 500 GB ");

        editUserPage.editQuota("500");
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getUserQuota("500 GB"), "500 GB", "User quota is not 500 GB");
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9426")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void enablingAccount()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = getUserFullName(enableUser);
        usersPage.navigate();
        usersPage.searchUser(enableUser.getUsername());
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.clickDisabledAccount();
        editUserPage.clickSaveChangesButton();
        getBrowser().waitUntilElementContainsText(userProfileAdminToolsPage.accountStatus, "Disabled");
        Assert.assertEquals(userProfileAdminToolsPage.getAccountStatus(), "Disabled", "Account is not disabled");

        LOG.info("Step 1&2: Switch off \"Disable account\" check-box");
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.clickDisabledAccount();
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getAccountStatus(), "Enabled", "Account is not enabled");

        LOG.info("Step 3: Try to log in as a user");
        cleanupAuthenticatedSession();
        loginPage.navigate();
        loginPage.login(enableUser);
        userDashboardPage.renderedPage();
        userDashboardPage.assertPageIsOpened();
        cleanupAuthenticatedSession();
    }

    @TestRail (id = "C9434")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeGroupFromUserProfile()
    {
        String fullName = getUserFullName(removeGroupUser);
        String groupName = "EMAIL_CONTRIBUTORS";

        LOG.info("Step1: Login as admin and add a user to a group.");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();
        usersPage.searchUser(removeGroupUser.getUsername());
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.editGroupsField(groupName);
        editUserPage.clickSearchGroupButton();
        editUserPage.addGroup(groupName);
        userProfileAdminToolsPage = editUserPage.clickSaveChangesButton();

        Assert.assertEquals(userProfileAdminToolsPage.getGroupName(), groupName, "Group is incorrect.");

        LOG.info("Step 2: Remove the group.");
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.removeGroup(groupName);
        userProfileAdminToolsPage = editUserPage.clickSaveChangesButton();
        Assert.assertFalse(userProfileAdminToolsPage.getGroupName().contains(groupName), "Group was not removed.");

        cleanupAuthenticatedSession();
    }

    private String getUserFullName(UserModel user)
    {
        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }
}