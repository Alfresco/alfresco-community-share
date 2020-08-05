package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.CreateUserPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateUsersTests extends ContextAwareWebTest
{
    @Autowired
    private CreateUserPage createUsers;

    @Autowired
    private UsersPage usersPage;

    @BeforeClass(alwaysRun = true)
    public void authenticate()
    {
        setupAuthenticatedSession(getAdminUser());
    }

    @BeforeMethod (alwaysRun = true)
    public void precondition()
    {
        usersPage.navigate();
    }

    @TestRail (id = "C42597")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void verifyCreateUsersPage()
    {
        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser()
            .assertAllInputsFromInfoSectionAreDisplayed()
            .assertAllElementsFromAboutUserAreDisplayed()
            .assertSelectedQuotaTypeIs("GB")
            .assertAllButtonsAreDisplayed();
    }

    @TestRail (id = "C9396")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void browseCreateUserPage()
    {
        usersPage.clickNewUser().assertCreateUserPageIsOpened();
    }

    @TestRail (id = "C9397")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createUser()
    {
        UserModel user = UserModel.getRandomUserModel();

        LOG.info("Step1: Click 'New User' button.");
        usersPage.clickNewUser()
            .typeMandatoryFields(user)
            .clickCreate()
                .assertSuccessfullyCreatedNewUserNotificationIsDisplayed()
                .searchUserWithRetry(user.getUsername())
                    .usingUser(user).assertUserIsFound();
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C9401")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void createAndStartAnother()
    {
        UserModel user = UserModel.getRandomUserModel();

        usersPage.clickNewUser()
            .typeMandatoryFields(user)
            .clickCreateUserAndStartAnother();
        createUsers.assertAllFieldsAreEmpty()
            .clickCancel()
            .searchUserWithRetry(user.getUsername())
                .usingUser(user).assertUserIsFound();
        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C9405")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void managePassword()
    {
        UserModel user = UserModel.getRandomUserModel();

        usersPage.clickNewUser()
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
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addingUserToGroup()
    {
        UserModel user = UserModel.getRandomUserModel();

        usersPage.clickNewUser()
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

        removeUserFromAlfresco(user);
    }

    @TestRail (id = "C9407")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void disableAccount()
    {
        UserModel userToDisable = UserModel.getRandomUserModel();

        usersPage.clickNewUser()
            .typeMandatoryFields(userToDisable)
            .checkDisableAccount()
            .clickCreate()
                .searchUserWithRetry(userToDisable)
                .usingUser(userToDisable)
                    .assertUserIsFound()
                    .assertUserIsDisabled();
        removeUserFromAlfresco(userToDisable);
    }

    @TestRail (id = "C9408")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void addQuotaToUser()
    {
        UserModel user = UserModel.getRandomUserModel();
        usersPage.clickNewUser()
            .typeMandatoryFields(user)
            .setQuota("17")
            .clickCreate()
            .searchUserWithRetry(user)
                .usingUser(user).assertUserIsFound().assertQuotaIs("17 GB");
        removeUserFromAlfresco(user);
    }
}
