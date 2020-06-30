package org.alfresco.share.adminTools.users;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AdminToolsUserProfile;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.CreateUsers;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.data.RandomData;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateUsersTests extends ContextAwareWebTest
{
    @Autowired
    CreateUsers createUsers;

    @Autowired
    UsersPage usersPage;

    @Autowired
    private AdminToolsUserProfile adminToolsUserProfile;

    @Autowired
    private LoginPage loginPage;

    @BeforeMethod (alwaysRun = true)
    public void precondition()
    {
        LOG.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();
    }

    @TestRail (id = "C9396")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browseCreateUserPage()
    {
        LOG.info("Step1: Click 'New User' button and verify 'New User' page is displayed");
        usersPage.clickNewUser();
        Assert.assertTrue(getBrowser().getCurrentUrl().contains(CreateUsers.URL), "Create users page displayed");
    }

    @TestRail (id = "C9397")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createUser()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the Info for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setLastName("Last Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");
        createUsers.setQuota("1");

        LOG.info("Step3: Click 'Create User' button");
        createUsers.clickCreateButton();

        LOG.info("Step4: Search for the created user");
        usersPage.searchUser(userName);
        assertTrue(usersPage.isUserFound(userName), "User " + userName + " displayed");
        deleteCreatedUser(userName);
    }

    @TestRail (id = "C9401")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAndStartAnother()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the Info for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setLastName("Last Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");
        createUsers.setQuota("1");

        LOG.info("Step3: Click 'Create and Start Another' button");
        createUsers.clickCreateUserAndStartAnotherButton();
        createUsers.waitUntilMessageDisappears();
        assertTrue(createUsers.areAllFieldsClear(), "All fields from 'Create Users' page cleared");

        LOG.info("Step4: Click 'Cancel' button and check 'Admin Tools - Users Page' is opened");
        createUsers.clickCancelButton();

        LOG.info("Step5: Verify the created user is found");
        usersPage.searchUser(userName);
        assertTrue(usersPage.isUserFound(userName), "User " + userName + " displayed");
        deleteCreatedUser(userName);
    }

    @TestRail (id = "C9405")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void managePassword()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        String message = "Password fields don't match.";

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);

        LOG.info("Steps4,5: Fill in any string in 'Password' field. Fill in a different string in the 'Verify Password' field");
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password1");

        LOG.info("Step5: Click 'Create User' button");
        createUsers.clickCreateButtonAndExpectFailure();
        assertEquals(createUsers.getPasswordsDontMatchNotificationText(), message, "Displayed message:");

        LOG.info("Step6: Click 'Create and Start Another' button");
        createUsers.clickCreateUserAndStartAnotherButton();
        assertEquals(createUsers.getPasswordsDontMatchNotificationText(), message, "Displayed message:");
    }

    @TestRail (id = "C9406")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addingUserToGroup()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        String group = "ALFRESCO_ADMINISTRATORS";

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");

        LOG.info("Steps3,4: Add the user to a group - e.g. to 'ALFRESCO_ADMINISTRATORS'");
        createUsers.addUserToGroup(group);
        assertTrue(createUsers.isGroupAdded(group), "Group added");

        LOG.info("Step5: Click 'Remove Group' icon for added group");
        createUsers.removeGroup(group);
        assertFalse(createUsers.isGroupAdded(group), "Group added");

        LOG.info("Step6: Add the user to a group - e.g. to 'ALFRESCO_ADMINISTRATORS' again");
        createUsers.addUserToGroup(group);
        assertTrue(createUsers.isGroupAdded(group), "Group added");

        LOG.info("Step7: Click 'Create User' button");
        createUsers.clickCreateButton();

        LOG.info("Step8: Verify the the new user is added to group");
        usersPage.searchUser(userName);
        usersPage.clickUserLink("First Name");
        assertEquals(adminToolsUserProfile.getUserGroup(), "ALFRESCO_ADMINISTRATORS", "User added to group");
        deleteCreatedUser(userName);
    }

    @TestRail (id = "C9407")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void disableAccount()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        String authenticatinError = language.translate("login.authError");

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");

        LOG.info("Step3: Check 'Disable Account' check-box.");
        createUsers.checkDisableAccount();

        LOG.info("Step4: Click 'Create User' button.");
        createUsers.clickCreateButton();

        LOG.info("Step5: Search for recently created user.");
        usersPage.searchUser(userName);
        assertTrue(usersPage.isUserFound(userName), "User " + userName + " displayed");
        assertTrue(usersPage.isUserDisabled(0), "User " + userName + " disabled");

        LOG.info("Step7: Log out and try to log in as recently created user.");
        cleanupAuthenticatedSession();
        loginPage.navigate();
        loginPage.login(userName, "password");
        assertEquals(loginPage.getAuthenticationError(), authenticatinError, "Authentication error message=");
        deleteCreatedUser(userName);
    }

    @TestRail (id = "C9408")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, "tobefixed" })
    public void addQuotaToUser()
    {
        String userName = String.format("User%s", RandomData.getRandomAlphanumeric());
        String quotaValue = "12345";

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsername(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");

        LOG.info("Step3: Enter into quota field any number.");
        createUsers.setQuota(quotaValue);

        LOG.info("Step4: 'Create User' button.");
        createUsers.clickCreateButton();

        LOG.info("Step5: Search for recently created user and verify 'Quota' column.");
        usersPage.searchUser(userName);
        assertTrue(usersPage.isUserFound(userName), "User " + userName + " displayed");
        assertTrue(usersPage.isSpecificUserDataDisplayed("12345 GB"), "User quota displayed correctly");
        deleteCreatedUser(userName);
    }

    @TestRail (id = "C42597")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyCreateUsersPage()
    {
        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();

        LOG.info("Step2: Verify 'Info' section.");
        assertTrue(createUsers.areAllElementsFromInfoSectionDisplayed(), "All fields displayed on the 'Info' section");

        LOG.info("Step3: Verify 'About User' section.");
        assertTrue(createUsers.areAllElementsFromAboutUserSectionDisplayed(), "All fields displayed on the 'About User' section");
        assertEquals(createUsers.getValueForQuotaType(), "GB", "Correct value for quota type=");

        LOG.info("Step4: Verify buttons available on the page.");
        assertTrue(createUsers.areAllButtonsDisplayed(), "All buttons displayed");
    }

    private void deleteCreatedUser(String userName)
    {
        userService.delete(adminUser, adminPassword, userName);
        contentService.deleteTreeByPath(adminUser, adminPassword, "/User Homes/" + userName);
    }
}
