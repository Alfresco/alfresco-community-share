package org.alfresco.share.adminTools.users;

import static org.alfresco.share.TestUtils.ALFRESCO_ADMIN_GROUP;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.CreateUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateUsersTests extends BaseTest
{
    private CreateUserPage createUsers;
    private UsersPage usersPage;

    @BeforeMethod (alwaysRun = true)
    public void beforeTest()
    {
        createUsers = new CreateUserPage(webDriver);
        usersPage = new UsersPage(webDriver);

        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C42597")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void verifyCreateUsersPage()
    {
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .assertAllInputsFromInfoSectionAreDisplayed()
            .assertAllElementsFromAboutUserAreDisplayed()
            .assertSelectedQuotaTypeIs("GB")
            .assertAllButtonsAreDisplayed();
    }

    @TestRail (id = "C9396")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void browseCreateUserPage()
    {
        usersPage.navigate();
        usersPage.clickNewUserButton().assertCreateUserPageIsOpened();
    }

    @TestRail (id = "C9397")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void createUser()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .typeMandatoryFields(user)
            .clickCreate()
                .assertSuccessfullyCreatedNewUserNotificationIsDisplayed()
                .searchUserWithRetry(user.getUsername())
                    .usingUser(user).assertUserIsFound();
    }

    @TestRail (id = "C9401")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void createAndStartAnother()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .typeMandatoryFields(user)
            .clickCreateUserAndStartAnother();
        createUsers.assertAllFieldsAreEmpty()
            .clickCancel()
            .searchUserWithRetry(user.getUsername())
                .usingUser(user).assertUserIsFound();
    }

    @TestRail (id = "C9405")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void managePassword()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .setFirstName(user.getFirstName())
            .setLastName(user.getLastName())
            .setEmail(user.getEmailAddress())
            .setUsername(user.getUsername())
            .setPassword(user.getPassword())
            .setVerifyPassword(user.getPassword() + "-1")
            .clickCreateButtonAndExpectFailure()
                .assertPasswordDoesntMatchNotificationIsDisplayed()
            .clickCreateUserAndStartAnother()
                .assertPasswordDoesntMatchNotificationIsDisplayed();
    }

    @TestRail (id = "C9406")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void addingUserToGroup()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .typeMandatoryFields(user)
            .addUserToGroup(ALFRESCO_ADMIN_GROUP)
                .assertGroupIsAdded(ALFRESCO_ADMIN_GROUP)
            .removeGroup(ALFRESCO_ADMIN_GROUP)
                .assertGroupIsNotAdded(ALFRESCO_ADMIN_GROUP)
            .addUserToGroup(ALFRESCO_ADMIN_GROUP)
            .clickCreate()
            .searchUserWithRetry(user)
            .usingUser(user)
            .selectUserFullName()
                .assertGroupsAreDisplayed(ALFRESCO_ADMIN_GROUP.getGroupIdentifier());
    }

    @TestRail (id = "C9407")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void disableAccount()
    {
        UserModel userToDisable = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .typeMandatoryFields(userToDisable)
            .checkDisableAccount()
            .clickCreate()
                .searchUserWithRetry(userToDisable)
                .usingUser(userToDisable)
                    .assertUserIsFound()
                    .assertUserIsDisabled();
    }

    @TestRail (id = "C9408")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void addQuotaToUser()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.navigate();
        usersPage.clickNewUserButton()
            .typeMandatoryFields(user)
            .setQuota("17")
            .clickCreate()
            .searchUserWithRetry(user)
                .usingUser(user).assertUserIsFound().assertQuotaIs("17 GB");
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupTest()
    {
        deleteUsersIfNotNull();
    }
}
