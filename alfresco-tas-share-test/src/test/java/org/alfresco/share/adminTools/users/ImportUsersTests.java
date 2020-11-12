package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UploadUserResultsPage;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.BaseShareWebTests;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ImportUsersTests extends BaseShareWebTests
{
    private UsersPage usersPage;

    @BeforeMethod(alwaysRun = true)
    public void setupTest()
    {
        usersPage = new UsersPage(browser);
        setupAuthenticatedSession(getAdminUser());
    }

    @TestRail (id = "C9438")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importUsers()
    {
        UserModel importedUser = new UserModel("C9438user", password);

        usersPage.navigate();
        usersPage.uploadUsers(testDataFolder + "C9438.csv").clickGoBack()
            .searchUserWithRetry(importedUser)
                .usingUser(importedUser).assertUserIsFound();
        removeUserFromAlfresco(importedUser);
    }
}