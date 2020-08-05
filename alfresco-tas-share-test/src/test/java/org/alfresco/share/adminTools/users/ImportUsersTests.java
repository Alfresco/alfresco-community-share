package org.alfresco.share.adminTools.users;

import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UploadResults;
import org.alfresco.po.share.user.admin.adminTools.usersAndGroups.UsersPage;
import org.alfresco.share.ContextAwareWebTest;
import org.alfresco.testrail.TestRail;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

public class ImportUsersTests extends ContextAwareWebTest
{
    @Autowired
    private UsersPage usersPage;

    @Autowired
    private UploadResults uploadResults;

    @TestRail (id = "C9438")
    @Test (groups = { TestGroup.SANITY, TestGroup.ADMIN_TOOLS })
    public void importUsers()
    {
        UserModel importedUser = new UserModel("C9438user", password);
        setupAuthenticatedSession(getAdminUser());

        usersPage.navigate();
        usersPage.uploadUsers(testDataFolder + "C9438.csv");
        uploadResults.clickGoBack();
        usersPage.searchUserWithRetry(importedUser)
            .usingUser(importedUser).assertUserIsFound();
        removeUserFromAlfresco(importedUser);
    }
}