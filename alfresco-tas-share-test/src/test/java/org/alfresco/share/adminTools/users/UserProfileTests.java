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
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
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

    private String userName = String.format("UserProfileUser%s", RandomData.getRandomAlphanumeric());
    private String c9416User = String.format("C9416user%s", RandomData.getRandomAlphanumeric());
    private String c9417User = String.format("C9417user%s", RandomData.getRandomAlphanumeric());
    private String c9431User = String.format("c9431user%s", RandomData.getRandomAlphanumeric());
    private String c9427User = String.format("c9427user%s", RandomData.getRandomAlphanumeric());
    private String c9426User = String.format("c9426user%s", RandomData.getRandomAlphanumeric());
    private String c9434User = String.format("c9434user" + RandomData.getRandomAlphanumeric());
    private String authenticationError;

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
    {
        authenticationError = language.translate("login.authError");
        userService.create(adminUser, adminPassword, userName, password, userName + domain, "firstName", "lastName");
        userService.create(adminUser, adminPassword, c9416User, password, c9416User + domain, "c9416firstName", "c9416lastName");
        userService.create(adminUser, adminPassword, c9417User, password, c9417User + domain, "c9417firstName", "c9417lastName");
        userService.create(adminUser, adminPassword, c9431User, password, c9431User + domain, "c9431firstName", "c9431lastName");
        userService.create(adminUser, adminPassword, c9427User, password, c9427User + domain, "c9427firstName", "c9427lastName");
        userService.create(adminUser, adminPassword, c9426User, password, c9426User + domain, "c9426firstName", "c9426lastName");
        userService.create(adminUser, adminPassword, c9434User, password, c9434User + domain, "c9434firstName", "c9434lastName");
        setupAuthenticatedSession(adminUser, adminPassword);
    }

    @TestRail(id = "C9415")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browsingNewUserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "firstName" + " " + "lastName";
        adminTools.navigate();
        LOG.info("Step 1: On the Admin Console page click Users;");
        adminTools.navigateToNodeFromToolsPanel("Users", usersPage);
        Assert.assertEquals(usersPage.getRelativePath(), "share/page/console/admin-console/users", "User is not on Users page");

        LOG.info("Step 2: On the Users page search for test user");
        usersPage.searchUser(userName);
        assertTrue(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");

        LOG.info("Step 3: Click on the test user name displayed in search results");
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

    @TestRail(id = "C9416")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browsingEditUserPage()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "c9416firstName" + " " + "c9416lastName";
        usersPage.navigate();
        usersPage.searchUser(c9416User);
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

    @TestRail(id = "C9417")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void editingUser()
    {
        String firstName = "c9417editedFN";
        String lastName = "c9417editedLN";
        String email = "c9417edited@editedEmail.com";
        String expectedUserName = firstName + " " + lastName;

        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "c9417firstName" + " " + "c9417lastName";

        usersPage.navigate();
        usersPage.searchUser(c9417User);
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

    @TestRail(id = "C9431")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void deletingAUser()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "c9431firstName" + " " + "c9431lastName";
        usersPage.navigate();
        usersPage.searchUser(c9431User);
        usersPage.clickUserLink(fullName);

        LOG.info("Step 1:On the User Profile page for user1 click the Delete User button");
        userProfileAdminToolsPage.clickDelete();
        Assert.assertTrue(deleteUserDialogPage.isDeleteUserWindowDisplayed(), "Delete User window is not displayed");
        Assert.assertEquals(
                deleteUserDialogPage.getDeleteUserWindowText(),
                "Click Delete User to remove this user.\n" +
                        "\n" +
                        "Deleting a user does not remove their permissions from the repository. These permissions will be reused if the user is recreated later. You can also disable the user account.");

        LOG.info("Step 2: Click the Delete button on the Delete User pop-up window");
        getBrowser().waitInSeconds(5);
        deleteUserDialogPage.clickButton("Delete", usersPage);

        LOG.info("Step 3: On the Users Search page search for user1");
        usersPage.searchUser(c9431User);
        Assert.assertFalse(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");

        LOG.info("Step 4: Logout and Login using c9431User");
        cleanupAuthenticatedSession();
        loginPage.navigate();
        loginPage.login(c9431User, password);
        assertEquals(loginPage.getAuthenticationError(), authenticationError, "Authentication error message=");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C9427")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addingQuotaToUser()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "c9427firstName" + " " + "c9427lastName";
        usersPage.navigate();
        usersPage.searchUser(c9427User);
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();

        LOG.info("Step 1: Edit quota to 500 GB ");

        editUserPage.editQuota("500");
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getUserQuota("500 GB"), "500 GB", "User quota is not 500 GB");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C9426")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void enablingAccount()
    {
        setupAuthenticatedSession(adminUser, adminPassword);
        String fullName = "c9426firstName" + " " + "c9426lastName";
        usersPage.navigate();
        usersPage.searchUser(c9426User);
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.clickDisabledAccount();
        getBrowser().waitInSeconds(9);
        editUserPage.clickSaveChangesButton();
        getBrowser().waitUntilElementContainsText(userProfileAdminToolsPage.accountStatus, "Disabled");
        Assert.assertEquals(userProfileAdminToolsPage.getAccountStatus(), "Disabled", "Account is not disabled");

        LOG.info("Step 1&2: Switch off \"Disable account\" check-box");
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.clickDisabledAccount();
        getBrowser().waitInSeconds(5);
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getAccountStatus(), "Enabled", "Account is not enabled");

        LOG.info("Step 3: Try to log in as a user");
        cleanupAuthenticatedSession();
        loginPage.navigate();
        loginPage.login(c9426User, password);
        getBrowser().waitUntilElementIsDisplayedWithRetry(By.cssSelector("div[id$='get-started-panel-container']"));
        assertEquals(userDashboardPage.getPageTitle(), "Alfresco » User Dashboard", "Displayed page=");
        cleanupAuthenticatedSession();
    }

    @TestRail(id = "C9434")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void removeGroupFromUserProfile()
    {
        String fullName = "c9434firstName" + " " + "c9434lastName";
        String groupName = "EMAIL_CONTRIBUTORS";

        LOG.info("Step1: Login as admin and add a user to a group.");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();
        usersPage.searchUser(c9434User);
        usersPage.clickUserLink(fullName);
        userProfileAdminToolsPage.renderedPage();
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.editGroupsField(groupName);
        editUserPage.addGroup(groupName);
        editUserPage.clickSaveChangesButton();
        Assert.assertEquals(userProfileAdminToolsPage.getGroupsNames(), groupName, "Group is incorrect.");

        LOG.info("Step 2: Remove the group.");
        userProfileAdminToolsPage.clickEditUserButton();
        editUserPage.removeGroup(groupName);
        editUserPage.clickSaveChangesButton();
        Assert.assertFalse(userProfileAdminToolsPage.getGroupsNames().contains(groupName), "Group was not removed.");
    }
}