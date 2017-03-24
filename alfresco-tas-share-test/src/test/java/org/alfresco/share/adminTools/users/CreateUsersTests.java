package org.alfresco.share.adminTools.users;

import org.alfresco.common.DataUtil;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.user.admin.adminTools.AdminToolsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.AdminToolsUserProfile;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.CreateUsers;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.po.share.user.profile.UserProfilePage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CreateUsersTests extends ContextAwareWebTest
{

    @Autowired
    CreateUsers createUsers;

    @Autowired
    UsersPage usersPage;

    @Autowired
    AdminToolsPage adminTools;

    @Autowired
    PeopleFinderPage peopleFinder;

    @Autowired
    UserProfilePage userProfile;

    @Autowired
    SiteDashboardPage siteDashboardPage;

    @Autowired
    private AdminToolsUserProfile adminToolsUserProfile;

    @Autowired
    private LoginPage loginPage;

    @TestRail(id = "C9396")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browseCreateUserPage()
    {
        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button and verify 'New User' page is displayed");
        usersPage.clickNewUser();
        createUsers.renderedPage();
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("share/page/console/admin-console/users#state=panel%3Dcreate"), "Create users page displayed");
    }

    @TestRail(id = "C9397")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createUser()

    {
        String userName = "user" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Fill in the Info for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setLastName("Last Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsrName(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");
        createUsers.setQuota("1");

        logger.info("Step3: Click 'Create User' button");
        createUsers.clickCreateButton();
        usersPage.renderedPage();

        logger.info("Step4: Search for the created user");
        usersPage.searchUser(userName);
        assertTrue(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");

    }

    @TestRail(id = "C9401")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAndStartAnother()

    {
        String userName = "user" + DataUtil.getUniqueIdentifier();

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Fill in the Info for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setLastName("Last Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsrName(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");
        createUsers.setQuota("1");

        logger.info("Step3: Click 'Create and Start Another' button");
        createUsers.clickCreateUserAndStartAnotherButton();
        createUsers.renderedPage();
        getBrowser().waitInSeconds(2);
        assertTrue(createUsers.areAllFieldsClear(), "All fields from 'Create Users' page cleared");

        logger.info("Step4: Click 'Cancel' button and check 'Admin Tools - Users Page' is opened");
        createUsers.clickCancelButton();
        usersPage.renderedPage();

        logger.info("Step5: Verify the created user is found");
        usersPage.searchUser(userName);
        assertTrue(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");

    }

    @TestRail(id = "C9405")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void managePassword()

    {
        String userName = "user" + DataUtil.getUniqueIdentifier();
        String message = "Password fields don't match.";

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsrName(userName);

        logger.info("Steps4,5: Fill in any string in 'Password' field. Fill in a different string in the 'Verify Password' field");
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password1");

        logger.info("Step5: Click 'Create User' button");
        createUsers.clickCreateButton();
        assertEquals(createUsers.getPasswordsDontMatchNotificationText(), message, "Displayed message:");
        createUsers.renderedPage();
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("share/page/console/admin-console/users#state=panel%3Dcreate"), "Create users page displayed");

        logger.info("Step6: Click 'Create and Start Another' button");
        createUsers.clickCreateUserAndStartAnotherButton();
        assertEquals(createUsers.getPasswordsDontMatchNotificationText(), message, "Displayed message:");
        createUsers.renderedPage();
        Assert.assertTrue(getBrowser().getCurrentUrl().contains("share/page/console/admin-console/users#state=panel%3Dcreate"), "Create users page displayed");

    }

    @TestRail(id = "C9406")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addingUserToGroup()

    {
        String userName = "user" + DataUtil.getUniqueIdentifier();
        String group = "ALFRESCO_ADMINISTRATORS";

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsrName(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");

        logger.info("Steps3,4: Add the user to a group - e.g. to 'ALFRESCO_ADMINISTRATORS'");
        createUsers.addUserToGroup(group);
        assertEquals(createUsers.isGroupAdded(group), true, "Group added");

        logger.info("Step5: Click 'Remove Group' icon for added group");
        createUsers.removeGroup(group);
        assertEquals(createUsers.isGroupAdded(group), false, "Group added");

        logger.info("Step6: Add the user to a group - e.g. to 'ALFRESCO_ADMINISTRATORS' again");
        createUsers.addUserToGroup(group);
        assertEquals(createUsers.isGroupAdded(group), true, "Group added");

        logger.info("Step7: Click 'Create User' button");
        createUsers.clickCreateButton();
        usersPage.renderedPage();

        logger.info("Step8: Verify the the new user is added to group");
        usersPage.searchUser(userName);
        usersPage.clickUserLink("First Name");
        assertEquals(adminToolsUserProfile.isUserAddedToGroup(), "ALFRESCO_ADMINISTRATORS", "User added to group");

    }

    @TestRail(id = "C9407")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void disableAccount()

    {

        String userName = "user" + DataUtil.getUniqueIdentifier();
        String authenticatinError = language.translate("login.authError");

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Fill in the mandatory fields for the new user");
        createUsers.setFirstName("First Name");
        createUsers.setEmail("user@alfresco.com");
        createUsers.setUsrName(userName);
        createUsers.setPassword("password");
        createUsers.setVerifyPassword("password");

        logger.info("Step3: Check 'Disable Account' check-box.");
        createUsers.checkDisableAccount();

        logger.info("Step4: Click 'Create User' button.");
        createUsers.clickCreateButton();
        usersPage.renderedPage();

        logger.info("Step5: Search for recently created user.");
        usersPage.searchUser(userName);
        assertTrue(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");
        assertTrue(usersPage.isUserDisabled(0), "User " + userName + " disabled");

        logger.info("Step7: Log out and try to log in as recently created user.");
        cleanupAuthenticatedSession();
        navigate(properties.getShareUrl().toString());
        assertEquals(loginPage.getPageTitle(), "Alfresco » Login", "Displayed page= ");
        loginPage.login(userName, "password");
        assertEquals(loginPage.getAuthenticationError(), authenticatinError, "Authentication error message=");

    }

    @TestRail(id = "C9408")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addQuotaToUser()
    {

        {

            String userName = "user" + DataUtil.getUniqueIdentifier();
            String quotaValue = "12345";

            logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
            setupAuthenticatedSession(adminUser, adminPassword);
            usersPage.navigate();

            logger.info("Step1: Click 'New User' button.");
            usersPage.clickNewUser();
            createUsers.renderedPage();

            logger.info("Step2: Fill in the mandatory fields for the new user");
            createUsers.setFirstName("First Name");
            createUsers.setEmail("user@alfresco.com");
            createUsers.setUsrName(userName);
            createUsers.setPassword("password");
            createUsers.setVerifyPassword("password");

            logger.info("Step3: Enter into quota field any number.");
            createUsers.setQuota(quotaValue);

            logger.info("Step4: 'Create User' button.");
            createUsers.clickCreateButton();
            usersPage.renderedPage();

            logger.info("Step5: Search for recently created user and verify 'Quota' column.");
            usersPage.searchUser(userName);
            assertTrue(usersPage.verifyUserIsFound(userName), "User " + userName + " displayed");
            assertTrue(usersPage.isSpecificUserDataDisplayed("12345 GB"), "User quota displayed correctly");

        }

    }

    @TestRail(id = "C42597")
    @Test(groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyCreateUsersPage()

    {

        logger.info("Preconditions: Login as admin user and navigate to 'Users' page from 'Admin Console'");
        setupAuthenticatedSession(adminUser, adminPassword);
        usersPage.navigate();

        logger.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser();
        createUsers.renderedPage();

        logger.info("Step2: Verify 'Info' section.");
        assertTrue(createUsers.areAllElementsFromInfoSectionDisplayed(), "All fields displayed on the 'Info' section");

        logger.info("Step3: Verify 'About User' section.");
        assertTrue(createUsers.areAllElementsFromAboutUserSectionDisplayed(), "All fields displayed on the 'About User' section");
        assertEquals(createUsers.getValueForQuotaType(), "GB", "Correct value for quota type=");

        logger.info("Step4: Verify buttons available on the page.");
        assertTrue(createUsers.areAllButtonsDisplayed(), "All buttons displayed");

    }
}
