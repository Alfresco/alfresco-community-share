package org.alfresco.share.adminTools.users;

import org.alfresco.common.Utils;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ImportUsersTests extends BaseTest
{
    private final String password = "password";

    private UsersPage usersPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        usersPage = new UsersPage(webDriver);
        authenticateUsingLoginPage(getAdminUser());
    }

    @TestRail (id = "C9438")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS, TestGroup.INTEGRATION })
    public void importUsers()
    {
        UserModel importedUser = new UserModel("C9438user", password);

        usersPage.navigate();
        usersPage.uploadUsers(Utils.testDataFolder + "C9438.csv").clickGoBack()
            .searchUserWithRetry(importedUser)
                .usingUser(importedUser).assertUserIsFound();
        deleteUsersIfNotNull(importedUser);
    }
}